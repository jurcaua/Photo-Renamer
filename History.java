package photo_renamer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class to keep track of all events, adding and removing tags, in a large log.
 */
/**
 * @author jurcaua
 *
 */
public class History implements Serializable {

	/** */
	private static final long serialVersionUID = 4662379220332918735L;

	/** Used for singleton design pattern, to have only one instance of this class. */
	private static History instance = null;
	/** A log to keep track of every change made to an image. */
	private HashMap <String, ArrayList<Image>> log;
	/** The file path to the .ser file we serialize the log to after termination. */
	private static String filePath = System.getProperty("user.dir") + "//src//photo_renamer//log.ser";
	
	/**
	 * Initializes the History instance by creating the log HashMap, and then
	 * trying to fill it with previous serialized information.
	 */
	public History() {
		log = new HashMap <String, ArrayList<Image>>(); // start empty...
		try {
			readFromSerFile(); // then try to deserialize
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the one instance of the History class.
	 * Creates one if it hasn't been initialized yet.
	 * 
	 * @return the one instance of History.
	 */
	public static History getInstance(){
		if (instance == null){
			instance = new History();
		}
		return instance;
	}
	
	/**
	 * Returns the log of all events.
	 * 
	 * @return the log of all events.
	 */
	public static HashMap <String, ArrayList<Image>> getLog(){
		return History.getInstance().log;
	}
	
	/**
	 * Inserts the corresponding key and vale pair into the log.
	 * 
	 * @param key
	 *           the key being accessed in the HashMap
	 * @param value
	 *           the value being added into the HashMap
	 */
	public static void putInLog(String key, ArrayList<Image> value){
		History.getInstance().log.put(key, value);
	}
	
	 /**
	 * Return the original image given a current image.
	 *
	 * @param img
	 *            the image to return original state
	 * @return the initial version image
	 */
	
	/**
	 * Removes value with the specified key (path), and returns the removed value.
	 * 
	 * @param path
	 *            a filepath that is a key in the HashMap
	 * @return the ArrayList that was removed
	 */
	public static ArrayList<Image> removeFromLog(String path){
		return History.getInstance().log.remove(path);
	}
	
	public Image getInitial(Image img) {
		if (History.getInstance().log.containsKey(img.getFile().getAbsolutePath())){
			return History.getInstance().log.get(img.getFile().getAbsolutePath()).get(0);
		}
		return null;
	}
	
	/**
	 * Return an older version of an image given a current image and a version number i.
	 * 
	 * Reverts the image an older version by renaming it to the old name.
	 *
	 * @param img
	 *            the image to return original state
	 * @param i
	 *            the version number to revert back to
	 * @return the image at given version number.
	 */
	public Image revertBackTo(Image img, int i){
		String path = img.getFile().getAbsolutePath(); // get the path of the current image, current key in log
		
		Image oldImg = this.log.get(img.getFile().getAbsolutePath()).get(i); // get the image the user wants
		String oldPath = oldImg.getFile().getAbsolutePath();  // get the old path, this will be the new key in log
		
		File oldFile = new File(oldPath); // create a new file
		img.getIcon().setDescription(oldPath); // set the icons path to the old path
		
		Image oldImage = new Image(oldFile.getName(), oldFile, img.getIcon()); // create a new image object to revert to
	
		img.getFile().renameTo(oldImage.getFile()); // rename the file
		img.setName(oldImage.getFile().getName()); // set parameters name and..
		img.setFile(oldImage.getFile()); // file
		
		History.putInLog(img.getFile().getAbsolutePath(), History.this.log.remove(path)); // replace the old key with the new
		addEvent(oldImage); // create a new event in the log, since we renamed
		
		return new Image(oldFile.getName(), new File(oldPath), img.getIcon()); // return a whole new image to not alias
	}
	
	/**
	 * Return the history of given image object.
	 * 
	 * @param img
	 *           image to find the history of
	 * @return the whole history of an image
	 */
	public ArrayList<Image> getImageHistory(Image img){
		return History.getInstance().log.get(img.getFile().getAbsolutePath());
	}
	
	/**
	 * Return the history of given path.
	 * 
	 * @param path
	 *            the path of an image to get the history of
	 * @return the whole history of a an image with given path
	 */
	public ArrayList<Image> getImageHistory(String path){
		return History.getInstance().log.get(path);
	}
	
	/**
	 * Adds an event for the given image.
	 * 
	 * @param img
	 *           the image to create an event for
	 */
	public static void addEvent(Image img){
		if (History.getInstance().log.containsKey(img.getFile().getAbsolutePath())){ // if the id already is in the map
			History.getInstance().log.get(img.getFile().getAbsolutePath()).add(img); // just add the event
		}
		else{ // if this is the first time adding an event, we need a new entry
			ArrayList<Image> a = new ArrayList<Image>(); // create the new ArrayList
			a.add(img); // add the initial image
			History.putInLog(img.getFile().getAbsolutePath(), a); // then put it into the log
		}
	}
	
	/**
	 * Clears all the history. Used for testing only.
	 */
	public void clearHistory(){
		History.getInstance().log = new HashMap <String, ArrayList<Image>>(); // clear the log 
		try {
			History.getInstance().saveToFile(); // try to serialize this empty log
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s = ""; // start with empty string
		for(String key : History.getInstance().log.keySet()){ // go through all keys
			s += "Path " + key + ":\n"; // start with adding "Path <filepath>:\n"
			for (Image img : History.getInstance().log.get(key)){ // go through all the entries for this filepath
				s += (History.getInstance().log.get(key).indexOf(img)) + ": " + img.toString() + "\n"; // add each entry with its version number
			}
		}
		return s;
	}
	
	/**
	 * Deserializes the log.ser file to log.
	 * 
	 * @throws FileNotFoundException
	 *                              if the .ser file is not found
	 */
	@SuppressWarnings("unchecked")
	public void readFromSerFile() throws FileNotFoundException {
		try {
			FileInputStream fileIn = new FileInputStream(filePath);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        try{
	        	log = (HashMap<String, ArrayList<Image>>) in.readObject(); // try to deserialize 
	        } catch(ClassNotFoundException e) {
	        	log = new HashMap<String, ArrayList<Image>>(); // if not, just make the log empty
	        	File file = new File(filePath); // make a file with the path we want
				try {
					file.createNewFile(); // and try to create the file since its not there
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
     * Serialize the log to the log.ser file.
     * 
     * @throws IOException
     *                    for if there is a problem outputting the data
     */
    public void saveToFile() throws IOException {

        OutputStream file = new FileOutputStream(filePath);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);

        // serialize the Map
        output.writeObject(History.getInstance().log); // serialize to the log.ser file
        output.close();
    }
}
