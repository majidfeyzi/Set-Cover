package grid;

import java.util.List;

/**
 * Context abstract class to notify grid changes.
 * @author Majid Feyzi
 * */
public abstract class Context {

    /**
     * This method notify every change in exist sets of grid and pass updated list of sets to the context.
     * @param sets list of updated lists
     * */
    public void onSetsChange(List<Set> sets) {}

    /**
     * This method notify result of algorithm after finish.
     * @param result result of algorithm
     * */
    public void onSolveComplete(String result) {}
}
