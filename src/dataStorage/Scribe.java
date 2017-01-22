/*
 * Written by Nikolas Gaub, 2017
 */
package dataStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * This class adds easy functionality for saving and loading objects
 * save/load based on name, adds new "save" file to directory
 */

public class Scribe {
	//name of folder objects are to be saved to
	private String saveFolder;
	
	//sets whether the save file is made in the directory of the java file
	//it is being run from.
	private boolean inDirectory;
	
	/*
	 * Sets saveFolder to "save" and inDirectory to true by default
	 */
	public Scribe() {
		saveFolder = "save";
		inDirectory = true;
	}
	
	/*
	 * allows initial customization of saveFolder and inDirectory
	 */
	public Scribe(String folderName, boolean inDirectory) {
		saveFolder = folderName;
		this.inDirectory = inDirectory;
	}
	
	/*
	 * Saves an object using objectOutputStream
	 * the name given should be the same one used to load.
	 */
	public void saveObject(Object object, String name) throws IOException {
		if (!(object instanceof Serializable)) throw new NotSerializableException();
		
		String location = getFileLocation() + name;
		
		File f = new File(location);
		f.getParentFile().mkdirs();
		FileOutputStream fileOutput = new FileOutputStream(f);
		ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
		objectOutput.writeObject(object);
		objectOutput.close();
		fileOutput.close();
		System.out.println("Object " + name + " saved.");
	}
	
	/*
	 * Loads an object using objectInputStream
	 * name given should be an already saved object.
	 */
	public Object loadObject(String name) throws ClassNotFoundException, IOException {
		String location = getFileLocation() + name;
		
		FileInputStream fileInput = new FileInputStream(location);
		ObjectInputStream objectInput = new ObjectInputStream(fileInput);
		Object object = objectInput.readObject();
		objectInput.close();
		fileInput.close();
		System.out.println("Object " + name + " loaded.");
		return object;
	}
	
	/*
	 * Returns a string of the exact file location.
	 */
	public String getFileLocation() {
		String location = saveFolder;
		if (inDirectory) {
			location = System.getProperty("user.dir");
			location += "/" + saveFolder +"/";
		}
		return location;
	}
	
	/*
	 * Sets the name of the folder that objects will be saved to.
	 */
	public void setSaveFolderInDirectory(String name) {
		saveFolder = name;
		inDirectory = true;
	}
	
	public void setSaveFolder(String name) {
		saveFolder = name;
		inDirectory = false;
	}
}
