package dataStorage;

/*
 * This class wraps a Treemap, using a point as a key.
 * This remains efficient on a large scale due to treemap's sorting, 
 * all important map functions are wrapped.
 * 
 * Generic value to map, point is always Integers.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class PointMap<V> {
	private TreeMap<Point, V> map;

	/*
	 * Initializes map
	 */
	public PointMap() {
		map = new TreeMap<Point, V>();
	}

	/*
	 * wraps map's put function
	 */
	public void put(int x, int y, V value) {
		map.put(new Point(x, y), value);
	}

	/*
	 * wraps map's get function
	 */
	public V get(int x, int y) {
		return map.get(new Point(x, y));
	}

	/*
	 * wraps map's remove function, ease of access for user
	 */
	public V remove(int x, int y) {
		return map.remove(new Point(x, y));
	}
	
	/*
	 * Returns a list (java.util.List) of all values in this PointMap.
	 */
	public List<V> getAllEntries() {
		List<V> list = new ArrayList<V>();
		Iterator<Entry<Point, V>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Point, V> entry = (Entry<Point, V>) it.next();
			list.add(entry.getValue());
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() returns the map in the form of an array,
	 * in form of[(<x, y>), ()...]
	 */
	public String toString() {
		String array = "[";
		Iterator<Entry<Point, V>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Point, V> entry = (Entry<Point, V>) it.next();
			array += "(" + entry.getValue() + ")";
			if (it.hasNext()) {
				array += ", ";
			}
		}
		array += "]";
		return array;
	}

	/*
	 * Saves and unloads all entries on map.
	 */
	public void saveMap() {
		String location = System.getProperty("user.dir") + "/save";
		new File(location).mkdir();
		Iterator<Entry<Point, V>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Point, V> entry = it.next();
			unload(entry.getKey().x, entry.getKey().y);
			it = map.entrySet().iterator();
		}
	}

	/*
	 * Attempts to load entry from file, if entry does not exists, returns false
	 */
	public boolean load(int x, int y) {
		if (get(x, y) == null) {
			String location = System.getProperty("user.dir") + "/save";
			File f = new File(location);
			if (f.exists()) {
				return loadHelper(x, y, location);
			} else {
				f.mkdir();
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * Method to clean up load() method
	 */
	private boolean loadHelper(int x, int y, String loc) {
		loc += "/" + x;
		File folder = new File(loc);
		if (folder.exists()) {
			try {
				FileInputStream fileIn = new FileInputStream(loc + "/" + y + ".txt");
				ObjectInputStream stream = new ObjectInputStream(fileIn);
				@SuppressWarnings("unchecked")
				V value = (V) stream.readObject();
				put(x, y, value);
				stream.close();
				fileIn.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			System.err.println("File " + x + " was not found, creating file.");
			folder.mkdir();
			return false;
		}
	}

	/*
	 * Saves and removes entry with matching x, y from map. Returns false if
	 * entry does no exist.
	 */
	public boolean unload(int x, int y) {
		String location = System.getProperty("user.dir") + "/save";
		File f = new File(location);
		if (f.exists()) {
			return unloadHelper(x, y, location);
		} else {
			f.mkdir();
			return false;
		}
	}

	/*
	 * Method to clean up unload() method.
	 */
	private boolean unloadHelper(int x, int y, String loc) {
		loc += "/" + x;
		File folder = new File(loc);
		if (folder.exists()) {
			try {
				FileOutputStream fileOut = new FileOutputStream(loc + "/" + y + ".txt");
				ObjectOutputStream stream = new ObjectOutputStream(fileOut);
				stream.writeObject(remove(x, y));
				fileOut.close();
				stream.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			System.err.println("File " + x + " was not found, creating file.");
			folder.mkdir();
			return false;
		}
	}
	
	/*
	 * Saves value at an x y position without unloading from map
	 */
	public boolean save(int x, int y) {
		String location = System.getProperty("user.dir") + "/save";
		File f = new File(location);
		if (f.exists()) {
			return saveHelper(x, y, location);
		} else {
			f.mkdir();
			return false;
		}
	}
	
	/*
	 * method to clean up save() method.
	 */
	private boolean saveHelper(int x, int y, String loc) {
		loc += "/" + x;
		File folder = new File(loc);
		if (folder.exists()) {
			try {
				FileOutputStream fileOut = new FileOutputStream(loc + "/" + y + ".txt");
				ObjectOutputStream stream = new ObjectOutputStream(fileOut);
				stream.writeObject(get(x, y));
				fileOut.close();
				stream.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			System.err.println("File " + x + " was not found, creating file.");
			folder.mkdir();
			return false;
		}
	}
}
