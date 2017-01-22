/* 
 * Written by Nikolas Gaub, 2016
 */
package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/*
 * This is a utility class containing methods which load and/or manipulate images.
 * This should really be built into java already. Or at least standardized.
 */

public class ImageUtil {
	
	/*
	 * Loads an image contained inside of a project. Returns a blank 100x100 image if 
	 * IO exception is caught. 
	 * For classInProject, pass in the .class of a class from your project (i.e. FooBar.class)
	 * The reason for classInProject is to obtain a starting point for the string path.
	 * The image loaded is contained inside the JAR of the project, and therefore the easiest way to find
	 * said jar is to take the class's URL, using getResource.
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
	 * The reason for classInProject is to obtain a starting point for the string path.
	 * The image loaded is contained inside the JAR of the project, and therefore the easiest way to find
	 * said jar is to take the class's URL, using getResource.
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
	 * Loads an image outside of the JAR file. Returns a blank 100x100 BufferedImage if image is not found.
	 * Rules for the path required are the same as defined by file system navigating.
	 */
	public static BufferedImage loadImageFromPath(String fullPath) {
		BufferedImage image;
		try {
			image = ImageIO.read(new File(fullPath));
		} catch (IOException e) {
			e.printStackTrace();
			return new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		}
		return image;
	}
	
	/*
	 * Loads an image outside of the JAR file. Returns a blank BufferedImage of size width x height
	 * if image is not found.
	 * Rules for the path required are the same as defined by file system navigating.
	 */
	public static BufferedImage loadImageFromPath(String fullPath, double width, double height) {
		BufferedImage image;
		try {
			image = ImageIO.read(new File(fullPath));
		} catch (IOException e) {
			e.printStackTrace();
			return new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
		}
		return image;
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
	 * Returns a 2d array of all pixels with an alpha greater than 0.
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
	
	/*
	 * This method takes a array of Images, and combines them together
	 * This is used for compiling multiple images with transparency into one image.
	 * The top image should be at the final index of the array, and the bottom image should be element 0
	 */
	public static BufferedImage compileImages(Image[] images, int size) {
		BufferedImage compiledImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = compiledImage.createGraphics();
		for (int i = 0; i < images.length; i++) {
			g.drawImage(images[i], 0, 0, null);
		}
		
		return compiledImage;
	}
	
	/*
	 * Replaces all white pixels in an image with a given texture.
	 */
	public static BufferedImage fillWhiteWithTexture(BufferedImage image, Texture filler) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = image.getRGB(x, y);
				if (pixel == -1) {
					image.setRGB(x, y, filler.getColor().getRGB());
				}
			}
		}
		return image;
	}
	
	/*
	 * Replaces all white pixels in an image with a given texture.
	 */
	public static BufferedImage fillWhiteWithColor(BufferedImage image, Color color) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = image.getRGB(x, y);
				if (pixel == -1) {
					image.setRGB(x, y, color.getRGB());
				}
			}
		}
		return image;
	}
	
	/*
	 * Replaces a given color with a texture in an image. Useful for templates with
	 * multiple colors that need to be replaced (i.e. both white and black should be changed)
	 */
	public static BufferedImage fillColorWithTexture(BufferedImage image, Color target, Texture filler) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = image.getRGB(x, y);
				if (pixel == target.getRGB()) {
					image.setRGB(x, y, filler.getColor().getRGB());
				}
			}
		}
		return image;
	}
}
