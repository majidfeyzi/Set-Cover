package grid.history;

import java.util.*;

/**
 * Actions that user can do on the grid.
 * This actions are keeping as history to make able users to do undo.
 * @author Majid Feyzi
 * @see Action
 * */
public class History {

    // Use history stack to keep history of actions
    private final Stack<Action> stack = new Stack<>();

    /**
     * Add new action to history
     * @param action Action to keep
     * */
    public void push(Action action) {
        stack.push(action);
    }

    /**
     * Get last action that has been done until now
     * @return Last action that has been done until now
     * */
    public Action pop() {
        return stack.pop();
    }

    /**
     * Clear all items of history
     * */
    public void clear() {
        stack.clear();
    }

    /**
     * Get size of history
     * */
    public int size() {
        return stack.size();
    }

    /**
     * Batch remove items from end to achieve specified action.
     * Action itself will be remain.
     * @param actions Remove history items until achieve this action
     * */
    public void removeUntil(Action... actions) {

        // Convert actions to set to remove duplications
        Set<Action> actionsSet = new HashSet<>(Arrays.asList(actions));

        if (!stack.empty()) {
            Action last = stack.get(stack.size() - 1);
            while (!actionsSet.contains(last) && !stack.empty())
                last = pop();

            // Keep last item if it is inside actionsSet
            if (actionsSet.contains(last))
                stack.push(last);
        }
    }
}
