package photo_renamer;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DirectoryViewer {
	
	/**
	 * Creates a JFileChooser that allows the user to select an image file or directory of images.
	 * Opens up the ImageViewer after something is selected.
	 */
	public static void buildWindow() {
		
		/* FILE CHOOSER */
		// lets make it so we can see directories in our frame now
		JFileChooser fileChooser = new JFileChooser(); // create the chooser
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // want to be able to select both
		
		// only want to see JPEG files and directories
		FileFilter jpgFilter = new FileNameExtensionFilter("JPG file", "jpg", "jpeg", "JPG", "JPEG");
		fileChooser.setFileFilter(jpgFilter); // set that as the filter for filechooser
		
		// we dont want the user to be able to select the file: All files
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		// instead of "Open", say "Select Photo Directory" as the left button
		fileChooser.setApproveButtonText("Select Photo / Photo Directory");
		
		int button = fileChooser.showOpenDialog(null); // open it in reference to no previous GUI
		
		if (button == JFileChooser.APPROVE_OPTION){ // if they selected the Select button...
			File selected = fileChooser.getSelectedFile(); // get what they selected
			FileNode root = new FileNode(selected.getName(), selected, null, FileType.DIRECTORY); // start with root
			FileNode.buildTree(selected, root); // traverse the directory and make a tree from the root
			FileNode.clearSelectedFiles(); // clear the previously selected files
			FileNode.buildDirectoryContents(root); // build the ArrayList of image files
			ImageViewer.buildWindow().setVisible(true); // open up the ImageViewer!
		}
		else if (button == JFileChooser.CANCEL_OPTION){ // if they chose the close button..
			PhotoRenamer.buildWindow().setVisible(true); // back to the start screen!
		}
	}
	
	/**
	 * Create and show a directory explorer, which displays the contents of a
	 * directory.
	 *
	 * @param args
	 *            the command-line arguments.
	 */
	public static void main(String[] args) {
		DirectoryViewer.buildWindow();
	}
}
