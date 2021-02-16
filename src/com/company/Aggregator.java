package com.company;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Aggregator {
    //static map so it references only one object
    public static Map<String, ArrayList<TextPosition>> master = new ConcurrentHashMap<String, ArrayList<TextPosition>>();

    //aggregate input map to the master map
    public void add(Map<String, ArrayList<TextPosition>> map) {
        for (String key : map.keySet()) {
            if (master.containsKey(key)) {
                ArrayList<TextPosition> combinedList = master.get(key);
                combinedList.addAll(map.get(key));
                master.put(key, combinedList);
            } else {
                master.put(key, map.get(key));
            }
        }
    }

    public Map<String, ArrayList<TextPosition>> getMap() {
        return master;
    }
}