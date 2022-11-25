package qengine.utils;

import java.util.ArrayList;

public class ListUtils {
    public static ArrayList<Integer> listIntersection(ArrayList<Integer> list1, ArrayList<Integer> list2) {

        ArrayList<Integer> interList = new ArrayList<>();

        for (Integer member : list1) {
            if (list2.contains(member)) {
                interList.add(member);
            }
        }
        return interList;
    }
}