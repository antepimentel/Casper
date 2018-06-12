package LFG;

import java.util.Comparator;

/**
 * Used to compare groups for sorting
 */
public class GroupComparator implements Comparator<Group> {
    @Override
    public int compare(Group o1, Group o2) {
        return Integer.compare(o1.getID(), o2.getID());
    }
}
