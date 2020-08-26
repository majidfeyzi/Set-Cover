package grid;

import java.awt.Color;

/**
 * One of the elements of set cover problem that user can draw it on grid.
 * Each point can be displayed on grid only with x and y coordinates.
 * Each point has a color too. This color is using to show point in set that belong to it in solving problem.
 * @author Majid Feyzi
 * */
public class Point {

	// Coordinate of point on grid
	private final int x, y;

	// Color of point based on set that is belong to it
	private Color color = null;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isColored() {
		return color != null;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
