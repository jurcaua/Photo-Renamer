package photo_renamer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class ImageViewer implements WindowListener {
	
	/** The standard image height in the table of selected images. Width is scaled accordingly. */
	private final static int IMG_HEIGHT = 100;

	/**
	 * Builds and returns a JFrame that contains all images in the selected directory / just the image selected.
	 * Allows users to select an image and then add tags to them, and see its' edit history.
	 * Users can also revert back to previous versions of an image.
	 * 
	 * @return the JFrame that contains all of the image elements
	 */
	public static JFrame buildWindow() {
		
		JFrame imageFrame = new JFrame("PhotoRenamer"); // creating the frame, call it PhotoRenamer
		imageFrame.setPreferredSize(new Dimension(750, 500));
		
		/* ADDING MESSAGE BOX */
		JPanel messageArea = new JPanel();
		JTextArea messageBox = new JTextArea("Please select an image.");
		messageBox.setEditable(false);
		messageArea.add(messageBox);
		imageFrame.add(messageArea, BorderLayout.NORTH);
		
		/* DISPLAYING IMAGE(S) */
		DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Name", "Image"}, 0);
		
		/*
		
		Here we used the iterator design pattern.
		We created a fileIterator from the Iterator interface in Java.
		The iterable is our ArrayList<File>, where we use the .iterator() method on,
		    which returns an instance of Iterator<File> for fileIterator.
		
		Classes involved are ImageViewer for the Iterator,
		                 and FileNode    for the Iterable.
		
		*/
		// Adding all selected files into the JTable we created.
		Iterator<File> fileIterator = FileNode.getSelectedFiles().iterator();
		while (fileIterator.hasNext()){ // go file by file
			File file = fileIterator.next();
			BufferedImage img = null; // start with setting to null
			try {
				img = ImageIO.read(file); // try to read the file (should be an image)
			} catch (IOException i) {
				
			}
			// creating an ImageIcon and scaling the image according to IMG_HEIGHT
			ImageIcon icon = new ImageIcon(img.getScaledInstance(-1, IMG_HEIGHT, BufferedImage.SCALE_SMOOTH));
			icon.setDescription(file.getAbsolutePath()); // need to keep the filepath somewhere for later use
			tableModel.addRow(new Object[]{file.getName(), icon}); // add the row to the model
			if (!History.getLog().containsKey(file.getAbsolutePath())){ // if its a newly seen image...
				History.addEvent((new Image(file.getName(), file, icon))); // create a new entry
			}
		}
		
		@SuppressWarnings("serial")
		JTable imageTable = new JTable(tableModel)
				{
					// this makes the table have individual column types, so we can see our image rather than text
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public Class getColumnClass(int column){
						return getValueAt(0, column).getClass();
					}
				};
		imageTable.setRowHeight(IMG_HEIGHT); // set the table row height to the standard image height
		imageTable.setDefaultEditor(Object.class, null); // do not what user to edit the table
		imageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // able to select only one at a time
		// these are ArrayLists rather than single variables so we have room to implement multiple row selection
		ArrayList<ImageIcon> selectedIcons = new ArrayList<ImageIcon>();
		ArrayList<Integer> selectedRows = new ArrayList<Integer>();
		imageTable.addMouseListener(new MouseListener() { // for whenever the user does something to the table
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) { // only care about when the user clicks on the table
				selectedIcons.clear(); // refresh since they clicked something else
				selectedRows.clear();
				JTable target = (JTable)e.getSource(); // get the source of the event
				int[] rows = target.getSelectedRows(); // now get the selected rows (it'll only be one or nothing)
				if (rows.length > 0){ // if they picked something, do something
					messageBox.setText("You have selected an image. "
							+ "You can add or remove tags on the right. "
							+ "You can view its' history via the button below. ");
					
					for (int row : rows){ // go through each row
						ImageIcon temp = (ImageIcon) imageTable.getValueAt(row, 1); // get the cooresponding image
						selectedIcons.add(temp);
						selectedRows.add(new Integer(row));
					}
				}
				else{ // they dont have anything selected / deselected stuff
					messageBox.setText("Please select an image.");
					
					selectedIcons.clear(); 
					selectedRows.clear();
				}
			}
		});
		
		/* ADDING SCROLL BAR TO IMAGES */
		JScrollPane scrollPane = new JScrollPane(imageTable); // we want to scroll through all the images
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // no need for hor
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // only want vert
	
		imageFrame.add(scrollPane, BorderLayout.CENTER); // finally add the scroll pane which contains the imagePanel
		
		
		/* CREATING BUTTONS */
		JPanel buttonPanel = new JPanel(); // will contain all buttons below the images
		
		// back button to choose a new file/dir
		JButton backButton = new JButton("Select New File / Directory"); // creation
		backButton.addActionListener(new ActionListener(){ // we want it to do something on click

			@Override
			public void actionPerformed(ActionEvent arg0) {
				imageFrame.dispose(); // get rid of current view
				
				DirectoryViewer.buildWindow(); // go back to the directory viewer
			}
			
		});
		buttonPanel.add(backButton); // add it to the panel
		
		// history button to view the history of currently selected photo
		JButton historyButton = new JButton("View History"); // creation
		historyButton.addActionListener(new ActionListener(){ // we want it to do something on click

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!selectedIcons.isEmpty()){ // if something is selected...
					ImageIcon selectedIcon = selectedIcons.get(0); // get value of the selected icon
					File selectedFile = new File(selectedIcon.getDescription());
					int selectedRow = selectedRows.get(0); // get the selected row
					ArrayList<Image> historyList = History.getInstance().getImageHistory(selectedIcon.getDescription()); // get the history for the selected icon
					Object[] history = historyList.toArray();
					
					Image version = (Image)JOptionPane.showInputDialog(   // make a dialog box
						imageFrame,                                      // centered at imageFrame
						"View history below, select one to revert to:", // the prompt text
						"History",                                     // the dialog header
						JOptionPane.PLAIN_MESSAGE,               	  // no special stuff on this
						null,                               	     // no icon
						history,                             	    // everything they can choose is here
						"Select..."                        	       // pre-entered text
					);
					if (version != null){
						Image updatedImg = new Image(selectedFile.getName(), selectedFile, selectedIcon);
						updatedImg = History.getInstance().revertBackTo(updatedImg, historyList.indexOf(version));
						
						imageTable.setValueAt(updatedImg.getFile().getName(), selectedRow, 0); // set new name
						imageTable.setValueAt(updatedImg.getIcon(), selectedRow, 1); // set new imageicon
						
						messageBox.setText("Please select an image.");
					}
					imageTable.clearSelection(); // de-select whatever they selected
					selectedIcons.clear(); // same
					selectedRows.clear(); // same
				}
				else{ // they clicked while not having anything selected...
					messageBox.setText("Please Select an Image to See Its' History!");
				}
			}
		});
		buttonPanel.add(historyButton); // add to the button panel
		
		imageFrame.add(buttonPanel, BorderLayout.SOUTH); // we want it on the bottom
		
		
		/* TAG SECTION */
		JPanel tagPanel = new JPanel(); // creating the general tag panel, holds tags and buttons
		JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1)); // for the checkboxes only, vertically
		
		for (Tag tag : TagHandler.getInstance().tags){ // go through all current tags
			JCheckBox tagCheckBox = new JCheckBox(tag.getName()); // create a checkbox for it
			checkBoxPanel.add(tagCheckBox); // add to the panel
		}
		
		JPanel tagButtonPanel = new JPanel(new GridLayout(0, 1)); // vertical panel for buttons as well
		
		JButton createTag = new JButton("Create New Tag"); // the three buttons we want:
		JButton deleteTag = new JButton("Delete Tag");
		JButton applyButton = new JButton("Apply Changes");
		
		createTag.addActionListener(new ActionListener(){ // what to do on click...

			@Override
			public void actionPerformed(ActionEvent e) {
				String tagName = (String)JOptionPane.showInputDialog( // create dialog box on click
						imageFrame,                                  // centered at imageFrame
						"Enter the new tag name to create:",        // prompt text
						"Create Tag",                              // dialog header text
						JOptionPane.PLAIN_MESSAGE,                // nothing special about this dialog
						null,                                    // no icon
						null,                                   // no options, they enter themselves
						"Enter..."                             // pre-entered text
						);
				if(TagHandler.getInstance().createTag(tagName)){ // if we created a new tag...
					JCheckBox newTag = new JCheckBox(tagName); // create a new checkbox
					checkBoxPanel.add(newTag); // add to the checkbox panel
					imageFrame.revalidate(); // refresh the frame cause we added something new!!
				}
			}
		});
		
		deleteTag.addActionListener(new ActionListener(){ // onl click of the delete tag button...

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> optionsA = new ArrayList<String>(); // arraylist of all tags
				for (Tag tag : TagHandler.getInstance().tags){ // for every current tag...
					optionsA.add(tag.getName()); // add to the arraylist, its name
				}
				Object[] options = optionsA.toArray(); // but we need an array of object not arraylist, so convert
				String tagName = (String)JOptionPane.showInputDialog( // make a dialog box
						imageFrame,                                  // centered at imageFrame
						"Select which tag to delete:",              // the prompt text
						"Delete Tag",                              // the dialog header
						JOptionPane.PLAIN_MESSAGE,                // no special stuff on this
						null,                                    // no icon
						options,                                // everything they can choose is here
						"Select..."                            // pre-entered text
						);
				int tagIndex = TagHandler.getInstance().findTag(tagName); // get the index of the selected tag
				if (tagName != null && tagIndex != -1){ // if they actually selected something...
					checkBoxPanel.remove(tagIndex); // remove it from the panel
					TagHandler.getInstance().deleteTag(tagName); // delete it from the list of current tags
					imageFrame.revalidate(); // refresh the frame!
				}
			}
		});
		
		applyButton.addActionListener(new ActionListener(){ // on clicking the apply button...

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!selectedIcons.isEmpty()){ // if something is selected...
					ImageIcon selectedIcon = selectedIcons.get(0); // this is an ImageIcon of selected image
					File selectedFile = new File(selectedIcon.getDescription()); // get the file component
					int selectedRow = selectedRows.get(0).intValue(); // get the row index that they selected
					ArrayList<String> selectedBoxes = new ArrayList<String>(); // list for checked checkboxes
					ArrayList<String> unselectedBoxes = new ArrayList<String>(); // list for unchecked checkboxes
					for (Component checkbox : checkBoxPanel.getComponents()){ // go through everything in the panel
						if (checkbox instanceof JCheckBox && ((JCheckBox) checkbox).isSelected()){ // if we're at a checkbox thats checked...
							selectedBoxes.add(((AbstractButton) checkbox).getText()); // add to list
						}
						else if (checkbox instanceof JCheckBox && !((JCheckBox) checkbox).isSelected()){ // if we're at an unchecked checkbox...
							unselectedBoxes.add(((AbstractButton) checkbox).getText()); // add to list
						}
					}
					Image oldImg = new Image(selectedFile.getName(), selectedFile, selectedIcon); //old image for comparison
					Image updatedImg = new Image(selectedFile.getName(), selectedFile, selectedIcon); // new image
					
					updatedImg = TagHandler.getInstance().addTag(updatedImg, selectedBoxes); // add the tags and update the image object
					updatedImg = TagHandler.getInstance().removeTag(updatedImg, unselectedBoxes); // same but remove tags
					
					if (!oldImg.getName().equals(updatedImg.getName())){ // if something was changed..
						History.addEvent(updatedImg); // add to the history
					}
					
					imageTable.setValueAt(updatedImg.getFile().getName(), selectedRow, 0); // set new name
					imageTable.setValueAt(updatedImg.getIcon(), selectedRow, 1); // set new imageicon
					
					imageTable.clearSelection(); // de-select whatever they selected
					selectedIcons.clear(); // same
					selectedRows.clear(); // same
				}
			}
		});
		
		tagButtonPanel.add(applyButton); // add the buttons to the panel cause we're done now!
		tagButtonPanel.add(createTag);
		tagButtonPanel.add(deleteTag);
		
		JScrollPane scrollTags = new JScrollPane(checkBoxPanel); // add the checkbox panel to a scrollpane
		scrollTags.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // no need for hor
		scrollTags.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // only want vert
		
		tagPanel.add(scrollTags);  // add out two sections to the panel
		tagPanel.add(tagButtonPanel);
		
		imageFrame.add(tagPanel, BorderLayout.EAST); // add the tag panel to the right
		
		imageFrame.pack(); // we're done, pack it all up
		
		/* SETTING UP WINDOWLISTENER */
		ImageViewer i = new ImageViewer();
		imageFrame.addWindowListener((WindowListener)i);
		
		return imageFrame;
	}

	public void windowClosing(WindowEvent arg0) {
		try { // gotta save before we leave though!
			TagHandler.saveToFile(); // try to save tags
		} catch (IOException e) {
		}
		try {
			History.getInstance().saveToFile(); // try to save history
		} catch (IOException e) {
		}
		System.exit(0);
	}
	
	public void windowActivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}
}
