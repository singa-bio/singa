package bio.singa.core.utility;

import java.util.*;

/**
 * @author cl
 */
public class ListHelper {

    private static class Count {
        public int count = 0;
    }

    public static <Type> boolean haveSameElements(final Collection<Type> list1, final Collection<Type> list2) {
        // (list1, list1) is always true
        if (list1 == list2) return true;

        // If either list is null, or the lengths are not equal, they can't possibly match
        if (list1 == null || list2 == null || list1.size() != list2.size())
            return false;

        // (switch the two checks above if (null, null) should return false)

        Map<Type, Count> counts = new HashMap<>();

        // Count the items in list1
        for (Type item : list1) {
            if (!counts.containsKey(item)) counts.put(item, new Count());
            counts.get(item).count += 1;
        }

        // Subtract the count of items in list2
        for (Type item : list2) {
            // If the map doesn't contain the item here, then this item wasn't in list1
            if (!counts.containsKey(item)) return false;
            counts.get(item).count -= 1;
        }

        // If any count is nonzero at this point, then the two lists don't match
        for (Map.Entry<Type, Count> entry : counts.entrySet()) {
            if (entry.getValue().count != 0) return false;
        }

        return true;
    }

    public static <T> List<T> removeDuplicates(List<T> list) {
        List<T> newList = new ArrayList<>();
        // Traverse through the first list
        for (T element : list) {
            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        // return the new list
        return newList;
    }
}
