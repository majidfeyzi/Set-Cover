package grid;

import java.util.ArrayList;
import java.util.List;

/**
 * The class of polygons that user draw on grid.
 * Each polygon is made up with edges and vertices that start and end vertex are same.
 * @author Majid Feyzi
 * */
public class Polygon {

    // Edges of polygon
    private List<Line> edges;

    // Vertices of polygon
    private List<Point> vertices = new ArrayList<>();

    public Polygon(List<Line> edges) {
        this.edges = new ArrayList<>(edges);
        for (Line edge : edges)
            this.vertices.add(edge.getEnd());
    }

    public List<Line> getEdges() {
        return edges;
    }

    /**
     * Check that point is inside of polygon or not.
     * @param point point to check
     * @return true if point is inside of polygon and false if point is outside of polygon
     * */
    public boolean isInsideOfPolygon(Point point) {

        boolean isOnVertex = false;

        // left intersection points
        List<Point> lipoints = new ArrayList<>();

        // top intersection points
        List<Point> tipoints = new ArrayList<>();

        // right intersection points
        List<Point> ripoints = new ArrayList<>();

        // bottom intersection points
        List<Point> bipoints = new ArrayList<>();

        // Create horizontal hypothetical line from point to left of point
        Line left = new Line(point, new Point(Integer.MIN_VALUE, point.getY()));

        // Create vertical hypothetical line from point to top of point
        Line top = new Line(point, new Point(point.getX(), Integer.MIN_VALUE));

        // Create horizontal hypothetical line from point to right of point
        Line right = new Line(point, new Point(Integer.MAX_VALUE, point.getY()));

        // Create vertical hypothetical line from point to bottom of point
        Line bottom = new Line(point, new Point(point.getX(), Integer.MAX_VALUE));

        for (Line edge : edges) {

            // If point is one of polygon vertices so it is inside of polygon
            if (edge.getStart().getX() == point.getX() && edge.getStart().getY() == point.getY()) {
                isOnVertex = true;
                break;
            }

            // Find intersections of point with edges in left of the point
            Point lintersect = left.findIntersectionWith(edge);
            if (lintersect.getX() != -1 && lintersect.getY() != -1 && lintersect.getX() <= point.getX()) {

                // Ignore intersection points that are not on the line
                int distance = new Line(edge.getStart(), lintersect).distance() + new Line(lintersect, edge.getEnd()).distance();

                // If intersection is on one of vertices or is on line
                if (edge.getStart().equals(lintersect) || edge.getEnd().equals(lintersect) || distance <= edge.distance()) {
                    lipoints.add(lintersect);
                }
            }

            // Find intersections of point with edges in top of the point
            Point tintersect = top.findIntersectionWith(edge);
            if (tintersect.getX() != -1 && tintersect.getY() != -1 && tintersect.getY() <= point.getY()) {

                // Ignore intersection points that are not on the line
                int distance = new Line(edge.getStart(), tintersect).distance() + new Line(tintersect, edge.getEnd()).distance();

                // If intersection is on one of vertices or is on line
                if (edge.getStart().equals(tintersect) || edge.getEnd().equals(tintersect) || distance <= edge.distance()) {
                    tipoints.add(tintersect);
                }
            }

            // Find intersections of point with edges in right of the point
            Point rintersect = right.findIntersectionWith(edge);
            if (rintersect.getX() != -1 && rintersect.getY() != -1 && rintersect.getX() >= point.getX()) {

                // Ignore intersection points that are not on the line
                int distance = new Line(edge.getStart(), rintersect).distance() + new Line(rintersect, edge.getEnd()).distance();

                // If intersection is on one of vertices or is on line
                if (edge.getStart().equals(rintersect) || edge.getEnd().equals(rintersect) || distance <= edge.distance()) {
                    ripoints.add(rintersect);
                }
            }

            // Find intersections of point with edges in bottom of the point
            Point bintersect = bottom.findIntersectionWith(edge);
            if (bintersect.getX() != -1 && bintersect.getY() != -1 && bintersect.getY() >= point.getY()) {

                // Ignore intersection points that are not on the line
                int distance = new Line(edge.getStart(), bintersect).distance() + new Line(bintersect, edge.getEnd()).distance();

                // If intersection is on one of vertices or is on line
                if (edge.getStart().equals(bintersect) || edge.getEnd().equals(bintersect) || distance <= edge.distance()) {
                    bipoints.add(bintersect);
                }
            }
        }

        // Determine that point is inside of set or not
        return isOnVertex || (lipoints.size() % 2 == 1 && tipoints.size() % 2 == 1 && ripoints.size() % 2 == 1 && bipoints.size() % 2 == 1);
    }
}
