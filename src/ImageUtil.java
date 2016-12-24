/* Nikolas Gaub
 * 
 * This is a utility class containing methods which load and/or manipulate images.
 * this should really be built into java already.
 */


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class ImageUtil {
	
	/*
	 * Loads an image contained inside of a project. Returns a blank 100x100 image if 
	 * IO exception is caught. 
	 * For classInProject, pass in the .class of a class from your project (i.e. FooBar.class)
	 */
	public static BufferedImage loadImage(String pathFromSrc, Class classInProject) {
		BufferedImage image;
		try {
			URL url = classInProject.getResource(pathFromSrc);
			image = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
			return new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		}
		return image;
	}
	
	/*
	 * Loads an image contained inside of a project, and scales to the input width/height. Returns blank image of 
	 * width and height passed in if image is not found.
	 * For classInProject, pass in the .class of a class from your project (i.e. FooBar.class)
	 */
	public static BufferedImage loadImage(String pathFromSrc, Class classInProject, double width, double height) {
		Image image;
		try {
			URL url = classInProject.getResource(pathFromSrc);
			image = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
			return new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
		}
		image = image.getScaledInstance((int) width, (int) height, Image.SCALE_SMOOTH);
		BufferedImage newImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImage.getGraphics();
		g.drawImage(image, 0, 0, null);
		return newImage;
	}
	
	/*
	 * This method draws an image on graphics g, rotated based on input, at point x, y
	 * The main point is to allow proper rotation (give the image excess room to not cut off corners)
	 * without off-setting the x, y location of the drawing.
	 * 
	 * returns true if image is drawn properly.
	 */
	public static boolean drawImage(Graphics g, BufferedImage image, int rotationDegrees, int x, int y) {
		int a = (int) Math.pow(Math.max(image.getWidth(), image.getHeight()), 2);
		int size = (int) (Math.sqrt(2 * a));
		int difference = (int) ((size - image.getWidth()) / 2);
		image = rotate(image, rotationDegrees, image.getWidth(), image.getHeight());
		boolean completed = g.drawImage(image, x - difference, y - difference, null);
		return completed;
	}
	
	/*
	 * Rotates an image a specified number of degrees. accounts for rotational increase in size.
	 * Because of this, this method is best used in tandem with the drawImage method, which accounts
	 * for this offset.
	 */
	public static BufferedImage rotate(Image image, int degrees, double width, double height) {
		int a = (int) Math.pow(Math.max(width, height), 2);
		int size = (int) (Math.sqrt(2 * a));
		
		ImageIcon icon = new ImageIcon(image);
		int difference = (int) ((size - icon.getIconWidth()) / 2);
		BufferedImage newImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) newImage.getGraphics();
		g2.rotate(Math.toRadians(degrees), size / 2, size / 2);
		g2.drawImage(image, difference, difference, null);
		return newImage;
	}
	
	/*
	 * Returns an array of all pixels with an alpha greater than 0.
	 * This is mainly meant to be used for collision detection.
	 */
	public static boolean[][] getVisiblePixels(BufferedImage image) {
		boolean[][] arr = new boolean[image.getWidth()][image.getHeight()];
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = image.getRGB(x, y);
				int alpha = (pixel >> 24) & 0xff;
				arr[x][y] = (alpha != 0);
			}
		}
		//Arrays.deepToString(arr);
		return arr;
	}
}
