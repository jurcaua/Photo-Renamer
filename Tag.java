package photo_renamer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representation of a Tag
 */
public class Tag implements Serializable  {
	/** */
	private static final long serialVersionUID = 4295657989180285133L;
	/** The name of this Tag. */
	private String name;
	/** The timestamp of this Tag. */
	private LocalDateTime timestamp;

	/** Initialize a new Tag
	 * @param name
	 * 			name of this tag
	 */
	public Tag(String name) {
		this.name = name;
		this.timestamp = LocalDateTime.now(); // the current time according the computer being used
		
	}

	/**
	 * @return name of this Tag
	 */
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.name + " " + this.timestamp;
	}
}
