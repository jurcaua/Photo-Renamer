package photo_renamer;
import java.util.Map;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;


/**
 * The root of a tree representing a directory structure.
 */
public class FileNode {

	/** The name of the file or directory this node represents. */
	private String name;
	/** The file this node represents. */
	private File file;
	/** Whether this node represents a file or a directory. */
	private FileType type;
	/** This node's parent. */
	private FileNode parent;
	/** All accepted file extensions */
	private final static ArrayList<String> IMG_EXT = new ArrayList<>(
			Arrays.asList(".JPG", ".JPEG", ".jpg", "jpeg"));
	/** All selected files by the user, starts off empty. */
	private static ArrayList<File> selectedFiles = new ArrayList<File>();
	
	/**
	 * This node's children, mapped from the file names to the nodes. If type is
	 * FileType.FILE, this is null.
	 */
	private Map<File, FileNode> children;

	/**
	 * A node in this tree.
	 *
	 * @param name
	 *            the name of the file
	 * @param file
	 * 		      the file        
	 * @param parent
	 *            the parent node.
	 * @param type
	 *            file or directory
	 */
	public FileNode(String name, File file, FileNode parent, FileType type) { // initialize all the instance vars
		this.name = name;
		this.file = file;
		this.parent = parent;
		this.type = type;
		this.children = new HashMap<File, FileNode>(); // create empty HashMap for the children
	}
	
	/**
	 * Returns the ArrayList of the currently selected files.
	 * 
	 * @return the ArrayList of the currently selected files.
	 */
	public static ArrayList<File> getSelectedFiles() {
		return selectedFiles;
	}
	
	public static void clearSelectedFiles(){
		selectedFiles.clear();
	}
	
	/**
	 * Find and return a child node named name in this directory tree, or null
	 * if there is no such child node.
	 *
	 * @param file
	 *            the file to search for
	 * @return the node named after file
	 */
	public FileNode findChild(File file) {
		FileNode result = null; // start will result being null
		if (this.children.containsKey(file)){ // if the key is one of the children,
			result = this.children.get(file); // result is the child we are looking for
		}
		else{ // we need to check even deeper now
			for (FileNode child : this.getChildren()){ // check each of the children
				if (result == null){ // we want to stop checking once we find the child (!= null)
					result = child.findChild(file); // look for the child somewhere deep in this node
				}
			}
		}
		return result;
	}

	/**
	 * Build the tree of nodes rooted at file in the file system; note curr is
	 * the FileNode corresponding to file, so this only adds nodes for children
	 * of file to the tree. Precondition: file represents a directory.
	 * 
	 * @param file
	 *            the file or directory we are building
	 * @param curr
	 *            the node representing file
	 */
	public static void buildTree(File file, FileNode curr) {
		if (file.isDirectory()){
			for (File element : file.listFiles()){ // go through all sub-files/directories of the root
				String name = element.getName(); // to not have to repeat element.getName() a lot
				if (element.isDirectory()){ // recursively call again if its a directory and add as a child
					curr.addChild(element, new FileNode(name, element, curr, FileType.DIRECTORY));
					FileNode.buildTree(element, curr.findChild(element));
				}
				else{ // this is a file then
					if (name.contains(".") && IMG_EXT.contains(name.substring(name.lastIndexOf(".")))){
						curr.addChild(element, new FileNode(name, element, curr, FileType.FILE));
					}
				}
			}
		}
		else{
			curr.addChild(file, new FileNode(file.getName(), file, curr, FileType.FILE));
		}
	}
	
	/**
	 * Adds all the image files from the built tree structure to an ArrayList.
	 * 
	 * @param fileNode
	 *                the root FileNode to start with
	 */
	public static void buildDirectoryContents(FileNode fileNode) {
		if (fileNode.type == FileType.FILE){
			selectedFiles.add(fileNode.getFile()); // append the current name to the ArrayList
		}
		for (FileNode child : fileNode.getChildren()){
			FileNode.buildDirectoryContents(child); // add another prefix the deeper we go
		}
	}
	
	/**
	 * Return the name of the file or directory represented by this node.
	 *
	 * @return name of this Node
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of the current node.
	 *
	 * @param name
	 *            of the file/directory
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the file of the current node.
	 * 
	 * @return file
	 *             a file of a node
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Return the child nodes of this node.
	 *
	 * @return the child nodes directly underneath this node.
	 */
	public Collection<FileNode> getChildren() {
		return this.children.values();
	}

	/**
	 * Return this node's parent.
	 * 
	 * @return the parent
	 */
	public FileNode getParent() {
		return parent;
	}

	/**
	 * Set this node's parent to p.
	 * 
	 * @param p
	 *            the parent to set
	 */
	public void setParent(FileNode p) {
		this.parent = p;
	}

	/**
	 * Add childNode, representing a file or directory named name, as a child of
	 * this node.
	 * 
	 * @param file
	 *            the name of the file or directory
	 * @param childNode
	 *            the node to add as a child
	 */
	public void addChild(File file, FileNode childNode) {
		this.children.put(file, childNode);
	}

	/**
	 * Return whether this node represents a directory.
	 * 
	 * @return whether this node represents a directory.
	 */
	public boolean isDirectory() {
		return this.type == FileType.DIRECTORY;
	}
}
