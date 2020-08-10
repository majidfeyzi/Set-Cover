package grid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import grid.history.Action;
import grid.history.History;

/**
 * Grid is a component that user can draw objects such as line, point and etc on it.
 * It also support Ctrl+z keyboard shortcut to undo actions.
 * Every class that inherit from Context class of grid can aware from changes.
 * @author Majid Feyzi
 * */
public class Grid extends JComponent {

	// Use history to keep history of actions
	private History history = new History();

	// Context or class that use this class
	private Context context;

	// User specified sites or point in plane
	private List<Point> points = new ArrayList<>();

	// Start or end vertices of lines
	private List<Point> vertices = new ArrayList<>();

	// Lines or edges that need to have a polygon
	private List<Line> lines = new ArrayList<>();

	// Sets that user created using points and polygons
	private List<Set> sets = new ArrayList<>();
	
	// Temporary sets that is using to show step by step solution
	private List<Set> tsets = new ArrayList<>();
	
	// Result string that hold result sets names to show to user
	private String result = "";
	
	// Timer to solve problem automatically and step by step
	private Timer timer;

	// Current color for drawing on plane
	private Color color;

	// Grid plane to draw
	private Graphics2D graphics;
	private Image image;

	// Mode of drawing (Line or Point)
	private Mode mode;

	public void setColor(String hex) {
		
		hex = hex.replace("#", "");
		if (hex.isEmpty() || hex.equals("#"))
			hex = "000000";
		
		this.color = Color.decode("#" + hex);
	}

	public void setType(Mode type) {
		this.mode = type;
	}

	public Grid(Context context) {

		this.context = context;

		// Implement mouse click and double click event
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (getCursor().getType() == Cursor.CROSSHAIR_CURSOR) {
					if (mode == Mode.Line && vertices.size() > 0) {
						Point start = vertices.get(vertices.size() - 1);
						Point end = new Point(e.getX(), e.getY());
						
						Line line = new Line(start, end);
						drawLine(line);	
					} else if (mode == Mode.Line) {
						addVertex(new Point(e.getX(), e.getY()));
					} else {
						Point point = new Point(e.getX(), e.getY());
						addPoint(point);
						if (sets.size() > 0)
							addPointToSets(point);
					}
				} else {
					if (mode == Mode.Line && vertices.size() > 0) {
						Point start = vertices.get(vertices.size() - 1);
						Point end = vertices.get(0);
						
						Line line = new Line(start, end);
						drawLine(line);	

						createSet();
						refreshColor();
					}
				}
				
				// Handle double click event to connect start point of set to end point of set and create new set
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					e.consume();
					
					if (mode == Mode.Line && vertices.size() > 0) {
						Point start = vertices.get(vertices.size() - 1);
						Point end = vertices.get(0);
						
						Line line = new Line(start, end);
						drawLine(line);	

						createSet();
						refreshColor();
					}
				}
			}
		});

		// Implement mouse movement event
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if (vertices.size() > 0) {
					Point point = vertices.get(0);
					if ((point.getX() - (2 * Config.POINT_RADIUS)) <= e.getX() && (point.getX() + (2 * Config.POINT_RADIUS)) >= e.getX() &&
							(point.getY() - (2 * Config.POINT_RADIUS)) <= e.getY() && (point.getY() + (2 * Config.POINT_RADIUS)) >= e.getY()) {
						setCursor(new Cursor(Cursor.HAND_CURSOR));
					} else {
						setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
					}
				}
			}
		});

		// Implement key press event
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "ctrlz");
		getActionMap().put("ctrlz", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				undo();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(getSize().width, getSize().height);
			graphics = (Graphics2D) image.getGraphics();
			clear();
		}
		g.drawImage(image, 0, 0, null);
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	/**
	 * Clear all data of grid
	 * */
	public void clear() {
		history.clear();
		vertices.clear();
		points.clear();
		lines.clear();
		tsets.clear();
		result = "";
		
		if (timer != null)
			timer.cancel();

		// Remove all sets and notify to context
		sets.clear();
		context.onSetsChange(sets);
		
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, getSize().width, getSize().height);
		graphics.setPaint(color);
		repaint();
	}
	
	/**
	 * Add points to canvas with specified coordinates and color on click.
	 * @param point point to show on plane
	 * */
	public void addPoint(Point point) {
		graphics.setPaint(Color.black);
		history.push(Action.AddPoint);
		points.add(point);
		graphics.fillOval(point.getX() - (Config.POINT_RADIUS / 2), point.getY() - (Config.POINT_RADIUS / 2), Config.POINT_RADIUS, Config.POINT_RADIUS);
		repaint();
	}

	/**
	 * Add points to canvas with specified coordinates and color on click.
	 * Vertices are smaller than site points.
	 * @param vertex vertex of polygon to show on plane
	 * */
	public void addVertex(Point vertex) {
		history.push(Action.AddVertex);
		vertices.add(vertex);
		graphics.setPaint(color);
		graphics.fillOval(vertex.getX() - (Config.VERTEX_RADIUS / 2), vertex.getY() - (Config.VERTEX_RADIUS / 2), Config.VERTEX_RADIUS, Config.VERTEX_RADIUS);
		repaint();
	}
	
	/**
	 * Draw polygon edge using start and end point.
	 * @param line edge of polygon to show on plane
	 * */
	public void drawLine(Line line) {
		history.push(Action.DrawLine);
		addVertex(line.getEnd());
		lines.add(line);
		graphics.setPaint(color);
		graphics.setStroke(new BasicStroke(Config.LINE_THICKNESS));
		graphics.drawLine(line.getStart().getX(), line.getStart().getY(), line.getEnd().getX(), line.getEnd().getY());
		repaint();
	}

	/**
	 * Create new set using points and lines that user drew on plane.
	 * Then create new polygon using lines and its vertices.
	 * Then add new set to exist sets and clear lines and vertices list.
	 * Finally update sets panel that user can see.
	 * */
	public void createSet() {

		// After create set, all vertex and line actions must be remove from history
		history.removeUntil(Action.CreateSet, Action.AddPoint);
		history.push(Action.CreateSet);

		// Add new set to list of exist sets and assign a name to it
		String name = "Set " + (sets.size() + 1);
		Polygon polygon = new Polygon(lines);
		Set set = new Set(name, this.color, polygon, points);
		sets.add(set);

		// Notifying sets changes to the context
		context.onSetsChange(sets);

		// Clear set lines and points to make able the user for generate other sets
		vertices.clear();
		lines.clear();
		
		// Clear temporary sets 
		tsets.clear();
	}
	
	/**
	 * Add new point to set.
	 * To detect that a point is inside of a set or not, i use the count of sides intersection.
	 * Point intersection with all lines in all sets must be checked.
	 * @param point point to add
	 * */
	public void addPointToSets(Point point) {

		// All polygons in all sets must be checked
		for (Set set : sets) {

			// Determine that point is inside of set's polygon or not and then add it to set points
			if (set.getPolygon().isInsideOfPolygon(point)) {
				List<Point> spoints = set.getPoints();
				spoints.add(point);
				set.setPoints(spoints);
				set.setName(set.getName());
			}
			
			// Clear temporary sets 
			tsets.clear();
		}

		// Notifying sets changes to the context
		context.onSetsChange(sets);
	}

	/**
	 * Remove point from set/sets that is inside of them.
	 * @param point point to remove
	 * */
	public void removePointFromSets(Point point) {

		// All sets must be check
		for (Set set : sets) {
			List<Point> spoints = set.getPoints();
			spoints.remove(point);
		}
	}
	
	/**
	 * Generate random color for every set.
	 * This method change painting color.
	 * */
	public void refreshColor() {
		Random random = new Random();
		
		int r = 150 + random.nextInt(100);
		int g = 150 + random.nextInt(100);
		int b = 150 + random.nextInt(100);
		
		color = new Color(r, g, b);
	}

	/**
	 * Redraw all objects of grid
	 * */
	private void redrawAllGraphics() {
		
		// First, clear grid graphics 
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, getSize().width, getSize().height);
		
		// Redraw points with black colors
		graphics.setPaint(Color.black);
		for (Point point : points)
			graphics.fillOval(point.getX() - (Config.POINT_RADIUS / 2), point.getY() - (Config.POINT_RADIUS / 2), Config.POINT_RADIUS, Config.POINT_RADIUS);

		// Redraw vertices
		graphics.setPaint(color);
		for (Point vertex : vertices)
			graphics.fillOval(vertex.getX() - (Config.VERTEX_RADIUS / 2), vertex.getY() - (Config.VERTEX_RADIUS / 2), Config.VERTEX_RADIUS, Config.VERTEX_RADIUS);

		// Redraw lines
		for (Line line : lines)
			graphics.drawLine(line.getStart().getX(), line.getStart().getY(), line.getEnd().getX(), line.getEnd().getY());
		
		// Redraw sets
		for (Set set : sets) {

			// Redraw set removed points with its colors
			for (Point point : set.getPoints()) {
				if (point.isColored()) {
					graphics.setPaint(point.getColor());
					graphics.fillOval(point.getX() - (Config.POINT_RADIUS / 2), point.getY() - (Config.POINT_RADIUS / 2), Config.POINT_RADIUS, Config.POINT_RADIUS);
				}
			}
			
			// Redraw set sides with set color
			graphics.setPaint(set.getColor());
			for (Line edge : set.getPolygon().getEdges()) {
				
				// Get line start point and redraw line point with set color
				graphics.fillOval(edge.getStart().getX() - (Config.VERTEX_RADIUS / 2), edge.getStart().getY() - (Config.VERTEX_RADIUS / 2), Config.VERTEX_RADIUS, Config.VERTEX_RADIUS);
				graphics.drawLine(edge.getStart().getX(), edge.getStart().getY(), edge.getEnd().getX(), edge.getEnd().getY());
			}
		}
		
		// Restore color to default
		graphics.setPaint(color);
		repaint();

		// Notifying sets changes to the context
		context.onSetsChange(sets);
		
		// Change focus to grid
		requestFocus();
	}

	/**
	 * Highlight set by changing it border size and points color and redraw all graphics.
	 * @param set set to be highlight
	 * */
	public void highlightSet(Set set) {

		// First clear grid graphics 
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, getSize().width, getSize().height);
		
		// Change main sets points state to show removed points in set color
		for (Set s : sets)
			if (s.getName().equals(set.getName()))
				s.setPoints(set.getPoints());
		
		redrawAllGraphics();
		
		// Redraw set with new border size to highlight it
		graphics.setPaint(set.getColor());
		graphics.setStroke(new BasicStroke(Config.HIGHLIGHTED_LINE_THICKNESS));
		for (Line edge : set.getPolygon().getEdges()) {
			
			// Get line start point and redraw line point with set color
			graphics.fillOval(edge.getStart().getX() - (Config.VERTEX_RADIUS / 2), edge.getStart().getY() - (Config.VERTEX_RADIUS / 2), Config.VERTEX_RADIUS, Config.VERTEX_RADIUS);
			graphics.drawLine(edge.getStart().getX(), edge.getStart().getY(), edge.getEnd().getX(), edge.getEnd().getY());
		}
		graphics.setStroke(new BasicStroke(Config.LINE_THICKNESS));
		repaint();
	}
	
	/**
	 * Solve the problem and show result step by step.
	 * */
	public void next() {

		// Copy sets to new temporary list to do operations on it
		if (tsets.size() == 0) {
			tsets = new ArrayList<>();
			for (Set set : sets) {
				List<Point> newPoints = new ArrayList<>();
				for (Point point : set.getPoints()) {
					Point newPoint = new Point(point.getX(), point.getY());
					newPoint.setColor(point.getColor());
					newPoints.add(newPoint);
				}
				Set newSet = new Set(set.getName(), set.getColor(), set.getPolygon(), newPoints);
				tsets.add(newSet);
			}	
		}

		// Get that all sets has been removed or not
		boolean isAllRemoved = true;
		for (Set set : tsets) {
			if (set.getNonRemovedPoints().size() > 0 && !set.isRemoved()) {
				isAllRemoved = false;
				break;
			}
		}
		
		if (!isAllRemoved) {

			// Sort the sets order by its points count
			tsets.sort(new Sort().reversed());
			for (Set set : tsets) {

				// Logically remove set, if it doesn't have any point
				if (set.getNonRemovedPoints().size() == 0)
					set.setRemoved(true);
					
				// Don't use removed sets in solution
				if (set.isRemoved()) 
					continue;
				
				// Highlight set and remove it logically
				set.setRemoved(true);
				highlightSet(set);
				
				// Add set name to results
				result += set.getName() + "\n";
				
				// Remove this selected set shared points from other sets
				for (Set s : tsets) {
					if (!s.isRemoved() && s.getNonRemovedPoints().size() > 0) {
						
						// All points of currently selected set must be check with other sets non removed points only
						List<Point> points1 = set.getPoints();
						List<Point> points2 = s.getNonRemovedPoints();
						for (Point p1 : points1) {
							for (Point p2 : points2) {
								if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
									
									// Both points must be colored if don't have any color
									if (!p1.isColored()) p1.setColor(set.getColor());
									if (!p2.isColored()) p2.setColor(set.getColor());
								}
							}
						}
					}
				}
				
				break;
			}
		} else {
			
			// Reset sets remove property to show step by step solution in next button click
			for (Set set : tsets) 
				set.setRemoved(false);
			
			// Points of main sets that has been removed must return to non removed state and it's color must be null
			for (Set set : sets)
				for (Point point : set.getPoints())
					point.setColor(null);
			
			// Stop timer to auto solve problem after finish solving
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			
			redrawAllGraphics();

			// Send sets names as the final result to context
			if (!result.isEmpty()) {
				context.onSolveComplete(result);
				result = "";
			}
		}
	}
	
	/**
	 * Undo actions that are performed.
	 * */
	private void undo() {
		if (history.size() > 0) {

			Action action = history.pop();
			switch (action) {
			case AddPoint:
				if (points.size() > 0) {
					
					// First remove point from set/sets
					Point point = points.get(points.size() - 1);
					removePointFromSets(point);
					
					points.remove(points.size() - 1);
				}
				break;
			case AddVertex:
				if (vertices.size() > 0) {
					vertices.remove(vertices.size() - 1);
					
					// Direct before add line point action, a draw line action has been done
					if (history.size() > 0) {
						action = history.pop();
						if (action == Action.DrawLine && lines.size() > 0)
							lines.remove(lines.size() - 1);
						else
							history.push(action);
					}
				}
				break;
			case CreateSet:
				if (sets.size() > 0) {
					sets.remove(sets.size() - 1);
				}
				break;
			default:
				break;
			}
			
			// Redraw graphics after remove last action 
			redrawAllGraphics();
		} else { 
			clear();
		}
		
		// Clear temporary sets 
		tsets.clear();
	}
	
	/**
	 * Solve set cover problem step by step and automatically.
	 * */
	public void startAutoSolve() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					next();
				}
			}, 0, 1000);
		}
	}
	
	/**
	 * Generate random points and sets.
	 * x and y and width and height specifies points scale on grid.
	 * @param count count of points that must be generated
	 * @param x distance from left
	 * @param y distance from top
	 * @param width width of region that points will be located inside it
	 * @param height height of region that points will be located inside it
	 * */
	public void generateRandomSets(int count, int x, int y, int width, int height) {
		
		// First clear all data
		clear();
		
		// Second we need create points
		int counter = 1;
		Random random = new Random();
		while (counter <= count) {
			int rx = x + random.nextInt(width - x);
			int ry = y + random.nextInt(height - y);
			Point point = new Point(rx, ry);
			if (!points.contains(point)) {
				addPoint(point);
				counter++;
			}
		}
		
		// Copy points to temporary list
		List<Point> ps = new ArrayList<>(points);
		
		// Third create sets until cover all points
		int scount = (count / 4) + random.nextInt(count > 1 ? count / 2 : 1);
		while (ps.size() > 0) {
			
			// Randomly generate line points (vertices) of sets
			int rx = x + random.nextInt(width - x);
			int ry = y + random.nextInt(height - y);
			
			// Compute rectangle line points
			Point p1 = new Point(rx - (100 + random.nextInt((rx - 100) > 0 ? (rx - 100) : 1)), ry - (100 + random.nextInt((ry - 100) > 0 ? (ry - 100) : 1)));
			Point p2 = new Point(rx + (80 + random.nextInt(50)), ry - (80 + random.nextInt(50)));
			Point p3 = new Point(rx + (50 + random.nextInt(50)), ry + (50 + random.nextInt(50)));
			Point p4 = new Point(rx - (100 + random.nextInt((rx - 100) > 0 ? (rx - 100) : 1)), ry + (50 + random.nextInt((getHeight() - ry - 100) > 0 ? (getHeight() - ry - 100) : 1)));
			
			// Create sets lines (sides)
			Line l1 = new Line (p1, p2);
			Line l2 = new Line (p2, p3);
			Line l3 = new Line (p3, p4);
			Line l4 = new Line (p4, p1);

			// Add line points and lines on grid (Note: the order of add is important because of undo implementation)
			addVertex(p1);
			drawLine(l1);
			drawLine(l2);
			drawLine(l3);
			drawLine(l4);
			
			// Create set and change color for next set
			createSet();
			refreshColor();
			
			// Remove points that has been covered at least by one set
			Set set = sets.get(sets.size() - 1);
			if (set.getPoints().size() == 0)

				// Remove empty set and undo create set operation
				undo();
			else
				ps.removeAll(set.getPoints());

			// If sets size is equal to random generated size an there is point without set, so operation must be continued
			if (sets.size() > scount && ps.size() > 0) {
				
				sets.clear();
				history.removeUntil(Action.CreateSet);
					
				ps = new ArrayList<>(points);
			}
		}
		
		redrawAllGraphics();
	}
	
	/**
	 * To resize grid and re create image in frame resize.
	 * */
	public void resize() {
		image = createImage(getSize().width, getSize().height);
		graphics = (Graphics2D) image.getGraphics();
		graphics.drawImage(image, 0, 0, null);	
		graphics.setStroke(new BasicStroke(Config.LINE_THICKNESS));
		redrawAllGraphics();
	}
}
