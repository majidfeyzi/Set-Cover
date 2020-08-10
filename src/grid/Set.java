package grid;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * This class specify sets of problems that contains a polygon and points inside it.
 * New set is creating with a polygon and detect and keep the points inside it.
 * To detect point is inside of a set or not, i use the count of right sides intersection.
 * Each set has specific color, name and polygon.
 * @author Majid Feyzi
 * */
public class Set {

	// The polygon that contains points
	private Polygon polygon;

	// Points that is inside of polygin
	private List<Point> points;

	// Color of sett
	private Color color;

	// Name of set
	private String name;

	// Flag that is use step by step solving
	private boolean isRemoved = false;
	
	public boolean isRemoved() {
		return isRemoved;
	}
	public void setRemoved(boolean isRemoved) {
		this.isRemoved = isRemoved;
		
		// Logically remove set points to show removed points in color of set
		if (isRemoved) {
			for(Point point : getPoints()) {
				if (!point.isColored())
					point.setColor(getColor());
			}
		} else {
			for(Point point : getPoints()) 
				point.setColor(null);
		}
	}
	public String getName() {
		return name.split("\\(")[0].trim() + " (" + points.size() + " Point)";
	}
	public void setName(String name) {
		this.name = name;
	}
	public Color getColor() {
		return color;
	}
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
	public Polygon getPolygon() {
		return polygon;
	}

	public Set(String name, Color color, Polygon polygon, List<Point> points) {
		this.color = color;
		this.polygon = polygon;

		// Find points of set from all available points
		this.points = findSetPoints(points);

		// Show number of polygon inside points at the end of name
		this.name = name + " (" + points.size() + " Point)";
	}

	/**
	 * Find points that located inside of polygon.
	 * @param points all available points
	 * @return list of points that located inside of set polygon
	 * */
	private List<Point> findSetPoints(List<Point> points) {

		List<Point> spoints = new ArrayList<>();

		// Find new set sides intersections with all points on canvas 
		for (Point point : points)
			// Determine that point is inside of set or not
			if (polygon.isInsideOfPolygon(point))
				spoints.add(point);

		return spoints;
	}

	/**
	 * Get set non removed points of set.
	 * @return list of non removed points
	 * */
	public List<Point> getNonRemovedPoints() {
		List<Point> ps = new ArrayList<>();
		for(Point p : getPoints())
			if (!p.isColored())
				ps.add(p);
		return ps;
	}
}
