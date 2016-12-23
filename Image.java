package photo_renamer;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.swing.ImageIcon;

/**
 * Representation of an image.
 */
public class Image implements Serializable {
	/** */
	private static final long serialVersionUID = 1L;
	/** The name of an image. */
	private String name;
	/** The exact time the image was created. */
	private LocalDateTime timestamp;
	/** The File connected to this image. */
	private File file;
	/** The ImageIcon connected to this image. */
	private ImageIcon icon;
	
	/**
	 * Initializes an Image.
	 * 
	 * @param name
	 *           A String of the name of the image file.
	 * @param file
	 *           The File object of this image.
	 * @param icon
	 *           The ImageIcon representing this image.
	 */
	public Image(String name, File file, ImageIcon icon){
		this.setName(name);
		this.timestamp = LocalDateTime.now();
		this.setFile(file);
		this.setIcon(icon);
	}
	
	/**
	 * Returns the images' name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return file.getName();
	}

	/**
	 * Sets the images' name.
	 * 
	 * @param the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the images' file.
	 * 
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the images' file.
	 * 
	 * @param the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Renames the file to the given file parameter.
	 * 
	 * @param file
	 *            the file to rename to
	 */
	public void renameFile(File file){
		this.file.renameTo(file);
	}
	
	/**
	 * Returns the images' ImageIcon.
	 * 
	 * @return the icon
	 */
	public ImageIcon getIcon() {
		return icon;
	}

	/**
	 * Sets the images' ImageIcon.
	 * 
	 * @param the icon to set
	 */
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	
	/**
	 * Sets the description of the ImageIcon to s.
	 * 
	 * @param s
	 *         what String to set the ImageIcon desciption to
	 */
	public void setIconDesc(String s){
		this.icon.setDescription(s);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.getName() + " - (" 
				+ this.timestamp.getHour() + ":" 
				+ this.timestamp.getMinute() + ":" 
				+ this.timestamp.getSecond() + ") - "
				+ this.timestamp.getMonth() + "-"
				+ this.timestamp.getDayOfMonth() + "-"
				+ this.timestamp.getYear();
	}
}
