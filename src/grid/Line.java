package grid;

/**
 * One of the elements of set cover problem that user can draw it on grid.
 * Each line can be displayed on grid only with start point and end point.
 * @author Majid Feyzi
 * */
public class Line {

	// Start point of the line
	private Point start;

	// End point of the line
	private Point end;

	public Line(Point start, Point end) {
		this.start = start;
		this.end = end;
	}
	
	public Point getStart() {
		return start;
	}
	
	public Point getEnd() {
		return end;
	}
	
	/**
	 * Find distance between two points.
	 * @return distance between start point and end point of line.
	 * */
	public int distance() {
		return (int)Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
	}
	
	/**
	 * Get two line intersection coordinate as a point.
	 * If given lines don't have any intersection so point with [-1, -1] coordinate will be return.
	 * This method find intersection point with computing determinant.
	 * It is obvious that two parallel lines doesn't have intersection point.
	 * @param line The line that we want to find intersection of current point with it.
	 * @return intersection point if it exist else point with (-1, -1) coordinate
	 * */
	public Point findIntersectionWith(Line line) {
		
		// First line represented as a1x + b1y = c1 
        double a1 = this.getEnd().getY() - this.getStart().getY(); 
        double b1 = this.getStart().getX() - this.getEnd().getX(); 
        double c1 = a1 * (this.getStart().getX()) + b1 * (this.getStart().getY()); 

		// Second line represented as a1x + b1y = c1 
        double a2 = line.getEnd().getY() - line.getStart().getY(); 
        double b2 = line.getStart().getX() - line.getEnd().getX(); 
        double c2 = a2 * (line.getStart().getX()) + b2 * (line.getStart().getY()); 

        // Compute determinant
        double determinant = a1 * b2 - a2 * b1; 
       
        if (determinant == 0) 
        { 
            // The lines are parallel. This is simplified by returning [-1, -1]
            return new Point(-1, -1); 
        } 
        else
        { 
            double x = (b2 * c1 - b1 * c2) / determinant; 
            double y = (a1 * c2 - a2 * c1) / determinant; 
            return new Point((int)x, (int)y); 
        } 
	}
}
