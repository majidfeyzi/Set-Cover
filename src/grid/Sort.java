package grid;

import java.util.Comparator;

/**
 * A sorter class for sorting sets according to it's number of points.
 * Set with max number of points is placing at beginning.
 * @author Majid Feyzi
 * */
public class Sort implements Comparator<Set> {
	
	@Override
	public int compare(Set first, Set second) {
		Integer a = first.getNonRemovedPoints().size();
		Integer b = second.getNonRemovedPoints().size();
		return a.compareTo(b);
	}

}
