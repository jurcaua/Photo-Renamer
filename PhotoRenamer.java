package photo_renamer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PhotoRenamer implements WindowListener {
	
	/**
	 * Creates the simple start screen for the user to see, 
	 *     including a short explanation of what the program does.
	 * 
	 * @return the JFrame of the start screen, ready to open.
	 */
	public static JFrame buildWindow() {
		// creating the JFrame
		JFrame startFrame = new JFrame("PhotoRenamer"); // creating the frame, call it PhotoRenamer
		
		// creating the title
		JPanel titleText = new JPanel();
		JLabel title = new JLabel("PhotoRenamer"); // set the text
		title.setFont(new Font("Serif", Font.PLAIN, 45));  // set the font
		titleText.add(title); // add to the panel
		startFrame.add(titleText, BorderLayout.NORTH); // we want it on top
		
		// creating the description
		JPanel descText = new JPanel();
		JLabel desc = new JLabel( // setting the text and formatting with HTML
				"<html><div style='text-align: center;'>" +
				"Select a file to display it and add tags of your choice to them.<br>" + 
				"Select a directory to view all images and add tags to all or some.<div/><html/>");
		desc.setFont(new Font("Serif", Font.PLAIN, 25)); // set the font
		descText.add(desc); // add the description to the panel
		startFrame.add(descText, BorderLayout.CENTER); // we want it in the center
		
		JButton startButton = new JButton("Click to Open File Chooser"); // create button and set its' text
		startButton.addActionListener(new ActionListener(){ // when the user presses the button..

			@Override
			public void actionPerformed(ActionEvent e) {
				startFrame.dispose(); // destroy this frame and...
				 
				DirectoryViewer.buildWindow(); // start up the directory picker
			}
			
		});
		startFrame.add(startButton, BorderLayout.SOUTH); // we want the button on the bottom
		
		startFrame.pack(); // ready to go, packs all added panels and fits them all in
		
		PhotoRenamer s = new PhotoRenamer(); // set up the window listener to terminate on close
		startFrame.addWindowListener((WindowListener)s);
		
		return startFrame;
	}
	
	public void windowClosing(WindowEvent arg0) {
		System.exit(0); // terminate the process on exit
	}
	
	public void windowActivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}
	
	public static void main(String[] args) {
		PhotoRenamer.buildWindow().setVisible(true);
	}
}
