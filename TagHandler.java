package photo_renamer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;


/**
 * Handles all current tags in the system.
 */
public class TagHandler implements Serializable {

	/** */
	private static final long serialVersionUID = 6188043389727672902L;
	/** Used for singleton design pattern, to have only one instance of this class. */
	private static TagHandler instance = null;
	/** ArrayList of all tags. */
	public ArrayList<Tag> tags;
	/** The file path to the .ser file we serialize the tags to after termination. */
	public static String filePath = System.getProperty("user.dir") + "//src//photo_renamer//tags.ser";

	/** Initializes TagHandler. */
	public TagHandler(){
		tags = new ArrayList<Tag>();
		try {
			readFromSerFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the one instance of TagHandler.
	 * 
	 * @return the one instance of TagHandler
	 */
	public static TagHandler getInstance(){
		if (instance == null){ // if there isnt already an instance created...
			instance = new TagHandler(); // create one
		}
		return instance;
	}
	
	/** Returns an ArrayList of all tags.
	 * 
	 * @return ArrayList of Tags which is the list of all current tags.
	 */
	public static ArrayList<Tag> getTags(){
		return TagHandler.getInstance().tags; 
	}
	
	/**
	 * Returns the index of the given tag name String.
	 * 
	 * @param tagName
	 *               the tag name we're looking for
	 * @return the index of the tag we're looking for 
	 */
	public int findTag (String tagName){
		for (int i = 0; i < TagHandler.getInstance().tags.size(); i++){ // go through all tags via index
			if (TagHandler.getInstance().tags.get(i).getName().equals(tagName)){ // when we find the right one...
				return i; // return its' index
			}
		}
		return -1; // if nothing found, return -1
	}
	
	/** Creates a new tag according to given tagName, if not already existing.
	 * 
	 * @param tagName
	 *			what the user wants the tag to be
	 * @return return False if tag exists, and True if a new tag was created
	 */
	public boolean createTag(String tagName) {
		if (tagName != null){ // if we have a name to work with...
			for (Tag element: TagHandler.getInstance().tags) { // search to see if tag already exists
				if (element.getName().equals(tagName)){
					return false; // return false and do nothing if it does
				}
			}
			Tag tag = new Tag(tagName); // we didnt find it, so create a new one...
			TagHandler.getInstance().tags.add(tag); // and add to the list
			return true; // found it!
		}
		return false;
	}
	
	/** Deletes corresponding tag to given tagName, if found.
	 * 
	 * @param tagName
	 *			what the user wants the tag to be
	 */
	public void deleteTag(String tagName) {
		for (Tag element: TagHandler.getInstance().tags) { // search to see if tag already exists
			if (element.getName().equals(tagName)){ // if we find the tag...
				TagHandler.getInstance().tags.remove(element); // remove from the list
				break; // no need to keep going through the rest of the tags
			}
		}
	}
	
	 /** Add the given list of tag(s) to the corresponding image.
	  * Assume all elements of tagNames exist in tags
	 * @param img
	 *			image for the tag to be added to 
	 *@param  tagNames
	 *			list of tags to be added
	 *@return the updated version of img with added tags
	 */
	public Image addTag(Image img, ArrayList<String> tagNames){
		
		if (tagNames.isEmpty()){
			return img;
		}
		
		else{
			String absPath = img.getFile().getAbsolutePath(); // ref to the path
			String pathNameBeforeExt = absPath.substring(0, absPath.lastIndexOf("\\") + 1).trim(); // part before name
			String imgName = absPath.substring(absPath.lastIndexOf("\\") + 1, absPath.lastIndexOf(".")).trim(); // just name
			String ext = absPath.substring(absPath.lastIndexOf("."), absPath.length()); // just extension
			
			for (String tagName : tagNames){ // go through all tags to add and add to the end of the name
				if (!img.getName().contains("@" + tagName)){
					imgName += " @" + tagName;
				}
			}
			imgName = imgName.trim(); // no extra white space
			
			String fileName = pathNameBeforeExt + imgName + ext; // put it back together
			File newFile = new File(fileName); // create the new file with the new file path
			img.setIconDesc(newFile.getAbsolutePath()); // set the new path to the icon
			
			Image newImage = new Image(newFile.getName(), newFile, img.getIcon()); // create the uodated image object
		
			img.renameFile(newImage.getFile()); // rename the file
			img.setName(newImage.getFile().getName()); // and update the values...
			img.setFile(newImage.getFile());
			
			// update the key on the log to the new one
			History.putInLog(img.getFile().getAbsolutePath(), History.removeFromLog(absPath));
			
			return img;
		}
	}
	
	 /** remove the tag(s) from the corresponding image.
	 * @param img
	 *			image for the tag to be deleted from 
	 *@param  tagNames
	 *			list of tags to be deleted
	 *@return updated img with removed tags
	 */
	public Image removeTag(Image img, ArrayList<String> tagNames){
		
		if (tagNames.size() == 0) { //if the tagNames is empty, don't do anything
			return img;
		}
		
		else {
		// very similar to addTag
		String absPath = img.getFile().getAbsolutePath(); // the path
		String pathNameBeforeExt = absPath.substring(0, absPath.lastIndexOf("\\") + 1); // before name
		String imgName = absPath.substring(absPath.lastIndexOf("\\") + 1, absPath.lastIndexOf(".")); // name
		String ext = absPath.substring(absPath.lastIndexOf("."), absPath.length()); // extension
		
		for (String tagName : tagNames){ // go through all given tag names to remove
			if (img.getName().contains("@" + tagName)){ // if its in the name...
				imgName = imgName.substring(0, imgName.indexOf("@" + tagName)).trim() // part before the tag
						+ imgName.substring(imgName.lastIndexOf("@" + tagName) + // part after tag
								tagName.length() + 1, imgName.length());

			}
		}

		imgName = imgName.trim(); // no extra white space
		
		String fileName = pathNameBeforeExt + imgName + ext; // put the name back together
		File newFile = new File(fileName); // create the new file
		img.setIconDesc(newFile.getAbsolutePath()); // set the imageicons new path
		
		Image newImage = new Image(newFile.getName(), newFile, img.getIcon()); // create new image object
		
		img.renameFile(newImage.getFile()); //rename the file
		img.setName(newImage.getFile().getName()); // update values...
		img.setFile(newImage.getFile());
		
		History.putInLog(img.getFile().getAbsolutePath(), History.removeFromLog(absPath));
		
		return new Image(newFile.getName(), new File(fileName), img.getIcon()); // return a whole new Image for no aliasing
		}
	}
		
	
	/**
	 * Used mainly for testing. Removes all current tags from the system.
	 */
	public static void clearTags(){
		TagHandler.getInstance().tags = new ArrayList <Tag>(); // make tags an empty list
		try {
			TagHandler.saveToFile(); // try to serialize
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Populates the records map from the file at path filePath.
     * 
     * @throws FileNotFoundException if filePath is not a valid path
     */
	@SuppressWarnings("unchecked")
	public void readFromSerFile() throws FileNotFoundException {
        
		try {
			FileInputStream fileIn = new FileInputStream(filePath);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        try{
	        	tags = (ArrayList<Tag>) in.readObject(); // try to deserialize
	        } catch(ClassNotFoundException e) {
	        	tags = new ArrayList<Tag>();
	        	File file = new File(filePath); // if not create a new file
				try {
					file.createNewFile(); // and try to create it in the system
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        }
	        
	        in.close();
	        fileIn.close();
		}
		catch (IOException i){
			i.printStackTrace();
		}
		
	}
	
	/**
     * Writes the students to file at filePath.
     * 
     * @throws IOException 
     *                    if outputting does not succeed
     */
	public static void saveToFile() throws IOException {
		OutputStream file = new FileOutputStream(filePath);
		OutputStream buffer = new BufferedOutputStream(file);
		ObjectOutput output = new ObjectOutputStream(buffer);
		
		// serialize the Map
		output.writeObject(TagHandler.getInstance().tags);
		output.close();
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s = ""; // start with empty string
		for (Tag tag : TagHandler.getInstance().tags){ // just add every tag to the string, with a comma after
			s += tag + ", ";
		}
		if (s.length() > 2){
			return s.substring(0, s.length()-2); // if we have at least one tag, get rid of the last comma
		}
		return s; // otherwise just print what we have, which should be nothing anyways
	}
}
