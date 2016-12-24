package dataStorage;

public class Point implements Comparable {
	public int x;
	public int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void move(int deltaX, int deltaY) {
		x += deltaX;
		y += deltaY;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setLocation(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public int compareTo(Object arg0) {
		Point p = (Point) arg0;
		int xComp = x - p.x;
		if (xComp != 0) {
			return xComp;
		} else {
			return y - p.y;
		}
	}
}
