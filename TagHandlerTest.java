package photo_renamer;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TagHandlerTest {

	/**
	 * NOTE: Since Tag is an object with a string and timestamp, the only way we can make sure
	 * two tags are equal is to check if their names are the same since timestamp will have a very
	 * small difference. In the tests, we have implemented a for loop that checks this and comments
	 * have been added where this happens.
	 */
	
	/** A random file in the system to avoid duplicate code */
	private File file = new File("a.jpg");
	
	/** an image icon since image needs an icon */
	private ImageIcon icon = new ImageIcon();
	
	/**  instance of image to be used late. file and icon have been instanciated before. */
	private Image img = new Image(file.getName(), file, icon);
	
	/**  An empty ArrayList of tags. To be used in testAddTag. */
	private ArrayList<String> tagsToAdd = new ArrayList<String>();
	
	/**  An empty ArrayList of tags. To be used in testAddTag. */
	private ArrayList<String> tagsToRemove = new ArrayList<String>();

	
	@Before
	public void setUp() throws Exception {
		TagHandler.clearTags(); //have an empty list of Tags
	}

	@After
	public void tearDown() throws Exception {
		TagHandler.clearTags();
		file.delete(); //delete the file that was created
	
	}

	/**
	 * Tests to see if getInstance correctly makes an instance of TagHandler
	 */
	@Test
	public void testGetInstance() {
		TagHandler t1 = new TagHandler(); //make two instances of TagHander
		TagHandler t2 = new TagHandler();
		//They should be the same since TagHandler should only have one instance
		assertEquals(t1.getInstance(), t2.getInstance());
		
	}
	
	/**
	 * Test to see if a new instance of TagHandler has an empty list of tags
	 */
	@Test
	public void testGetTagsEmpty() {
		
		ArrayList<Tag> expected = new ArrayList<Tag>();
		ArrayList<Tag> actual = TagHandler.getTags();
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Test to see when tags are created, getTags works properly
	 */
	@Test
	public void testGetTagsNonEmpty() {
		
		ArrayList<String> expected = new ArrayList<String>();
		//have to do this way since
		//timestamps are different so only look tagName
		ArrayList<String> actual = new ArrayList<String>();

		expected.add("amir");
		expected.add("alex");
		
		TagHandler.getInstance().tags.add(new Tag("amir"));
		TagHandler.getInstance().tags.add(new Tag("alex"));
		for (Tag element : TagHandler.getInstance().tags){
			actual.add(element.getName());
		}

		assertEquals(expected, actual);
	}
	
	/**
	 * Tests to see if findTag works properly in cases of empty, non-existant and existing tags
	 */
	@Test
	public void testFindTag() {
		int actual = -1; //TESTING ON AN EMPTY TAGS LIST
		int expected = TagHandler.getInstance().findTag("NOTHING");
		assertEquals(expected, actual);
		
		TagHandler.getInstance().tags.add(new Tag("amir"));
		TagHandler.getInstance().tags.add(new Tag("alex"));
		expected = TagHandler.getInstance().findTag("SOMETHING THAT DOESN'T EXIST IN NON-EMPTY TAGS");
		assertEquals(expected, actual);
		
		actual = 1; //testing a tag that actually exists in tags
		expected = TagHandler.getInstance().findTag("alex");
		assertEquals(expected, actual);

	}
	/**
	 * Test to see createTag works, i.e. can make new tags or use the old one if tag already exists
	 */
	@Test
	public void testCreateTag() {
		
		boolean expected = true; //true since tags is empty, so no tags exists
		boolean actual = TagHandler.getInstance().createTag("amir");
		assertEquals(expected, actual);
		
		expected = false; //try to see to add amir, but it already exists
		actual = TagHandler.getInstance().createTag("amir");
		assertEquals(expected, actual);

	}
	
	/**
	 * Test to see if deleteTag works for an empty list of Tags, deleting a non-existant tag and
	 * deleting an actual tag
	 */
	@Test
	public void testDeleteTag() {
		ArrayList<Tag> expected = new ArrayList<Tag>(); //try to delete a tag from an empty tags list
		TagHandler.getInstance().deleteTag("non-existant");
		ArrayList<Tag> actual = TagHandler.getInstance().tags;
		assertEquals(expected, actual);
		
		ArrayList<String> newExpected = new ArrayList<String>(); //have to do this way since
		//timestamps are different so only look tagName
		ArrayList<String> newActual = new ArrayList<String>();
		newExpected.add("amir"); //populate the expeceted
		newExpected.add("alex");
		TagHandler.getInstance().tags.add(new Tag("amir"));
		TagHandler.getInstance().tags.add(new Tag("alex"));
		for (Tag element : TagHandler.getInstance().tags){
			newActual.add(element.getName());
		}
		
		TagHandler.getInstance().deleteTag("notAmirorAlex"); 
		//try to delete a tag that doen't exist
		//from a non-empty tags list
		actual = TagHandler.getInstance().tags;
		assertEquals(newExpected, newActual);		
		
		ArrayList<String> newActual2 = new ArrayList<String>();

		newExpected.remove(0); //try to delete "amir" from tags list and see if result is "alex" only
		TagHandler.getInstance().deleteTag("amir"); 
		for (Tag element : TagHandler.getInstance().tags){
			newActual2.add(element.getName());
		}
		assertEquals(newExpected, newActual2);
	
	}
	
	/**
	 * Test to see addTag works, check name in the system and the name of the file
	 */
	@Test
	public void testAddTag() {
		
		
		//try to add no tags to the image
		Image actual = TagHandler.getInstance().addTag(img, tagsToAdd);
		assertEquals(img, actual);
		assertEquals(img.getName(), actual.getName()); //there should be no change to image name
		
		tagsToAdd.add("amir"); //try to add the tag amir to the image
		TagHandler.getInstance().addTag(img, tagsToAdd); 
		assertEquals("a @amir.jpg", img.getName()); // test to see if the names match for image
		//assertEquals("a @amir.jpg", img.getFile().getName());
		// test to see if the name changed in system

		
	}

	/**
	 * Test to see removeTag works, check name in the system and the name of the file
	 */
	@Test
	public void testRemoveTag() {
		
		//tagsToRemove empty at this point
		//try to remove no tags from the image
		Image actual = TagHandler.getInstance().removeTag(img, tagsToRemove);
		assertEquals(img, actual);
		assertEquals(img.getName(), actual.getName());
		
		tagsToRemove.add("amir"); //try to remove the tag amir, but image has no tags yet
		actual = TagHandler.getInstance().removeTag(img, tagsToRemove); 
		assertEquals("a.jpg", actual.getName()); // test to see if the names match for image
		assertEquals("a.jpg", file.getName()); // test to see if the name changed in system
		
		//add tags "amir" and "alex" to the image
		tagsToAdd.add("amir");
		tagsToAdd.add("alex");
		actual = TagHandler.getInstance().addTag(img, tagsToAdd);
		//image name should have "@amir" and "@alex" now, let's check
		assertEquals("a @amir @alex.jpg", img.getName());
		
		//now let's delete the tag "amir" from the image
		actual = TagHandler.getInstance().removeTag(img, tagsToRemove);
		assertEquals("a @alex.jpg", img.getName());
		assertEquals("a @alex.jpg", img.getFile().getName());
		// test to see if the name changed in system

	}
}

