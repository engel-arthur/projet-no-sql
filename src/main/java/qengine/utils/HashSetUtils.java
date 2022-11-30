package qengine.utils;

import java.util.HashSet;

public final class HashSetUtils {
    public static HashSet<Integer> listIntersection(HashSet<Integer> list1, HashSet<Integer> list2) {

        HashSet<Integer> interList = new HashSet<>();

        for (Integer member : list1) {
            if (list2.contains(member)) {
                interList.add(member);
            }
        }
        return interList;
    }
}