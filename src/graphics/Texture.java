/**
 * Written by Nikolas Gaub, 2016
 */

package graphics;

import java.awt.Color;

/*
 * This class randomizes a given color, for an easy solution to bland colors.
 */
public class Texture {
	
	protected int[] colors;
	protected int[] ranges;
	
	/*
	 * Initializes to blank arrays (default 0)
	 */
	public Texture() {
		colors = new int[4];
		ranges = new int[4];
	}
	
	/*
	 * Initializes colors to given values, leaves ranges blank.
	 */
	public Texture(int red, int green, int blue, int alpha) {
		colors = new int[4];
		colors[0] = red;
		colors[1] = green;
		colors[2] = blue;
		colors[3] = alpha;
		ranges = new int[4];
	}
	
	/*
	 * Initializes ranges to given values, leaves ranges blank.
	 */
	public Texture(int grayValue, int rRange, int gRange, int bRange, int aRange) {
		colors = new int[4];
		for (int i = 0; i < 3; i++) {colors[i] = grayValue;};
		colors[4] = 255;
		ranges = new int[4];
		ranges[0] = rRange;
		ranges[1] = gRange;
		ranges[2] = bRange;
		ranges[3] = aRange;
	}
	
	/*
	 * Initializes both colors and ranges to given values.
	 */
	public Texture(int red, int green, int blue, int alpha, int rRange, int gRange, int bRange, int aRange) {
		colors = new int[4];
		colors[0] = red;
		colors[1] = green;
		colors[2] = blue;
		colors[3] = alpha;
		ranges = new int[4];
		ranges[0] = rRange;
		ranges[1] = gRange;
		ranges[2] = bRange;
		ranges[3] = aRange;
	}
	
	/*
	 * Sets colors to given values
	 */
	protected void setColors(int red, int green, int blue, int alpha) {
		colors[0] = red;
		colors[1] = green;
		colors[2] = blue;
		colors[3] = alpha;
	}
	
	/*
	 * Sets color ranges to given values
	 */
	protected void setColorRanges(int red, int green, int blue, int alpha) {
		ranges = new int[4];
		ranges[0] = red;
		ranges[1] = green;
		ranges[2] = blue;
		ranges[3] = alpha;
	}
	
	/*
	 * returns a random color, at given values, randomized within given range.
	 */
	public Color getColor() {
		int[] tempCol = new int[4];
		for (int i = 0; i < 4; i++) {
			tempCol[i] = colors[i] + (int) (Math.random()*ranges[i]) - (ranges[i]/2);
			if (tempCol[i] > 255) {
				tempCol[i] = 255;
			} else if (tempCol[i] < 0) {
				tempCol[i] = 0;
			}
		}
		Color c = new Color(tempCol[0], tempCol[1], tempCol[2], tempCol[3]);
		return c;
	}
}
