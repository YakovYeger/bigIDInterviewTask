package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NameMatcher {

    Map<String, ArrayList<TextPosition>> NameLocation = new ConcurrentHashMap<String, ArrayList<TextPosition>>();

    public Map<String, ArrayList<TextPosition>> nameSearcher(StringBuffer sb, ArrayList<String> matchedTo, int startLine, int startChar ) throws IOException {
        //ensure that we are starting at the appropriate line/char offset
        int line =0;
        Matcher matcher = null;
        //take file split on new line and insert to list
        String[] sbList = sb.toString().split("/n");
        TextPosition currTextPosition = new TextPosition(0,0);
        //for the fifty most common names
        for(String name: matchedTo){
            //reset line and char offset to beginning of buffer
            currTextPosition.setLineOffset(startLine);
            currTextPosition.setCharOffset(startChar);
            //case insensitive check for name
            Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);

            line = 0;
            ArrayList<TextPosition> listOfPositions = new ArrayList<TextPosition>();
            //while there are still lines to check
            while(line < sbList.length){
                currTextPosition.setLineOffset((currTextPosition.getLineOffset()+1));
                matcher = pattern.matcher(sbList[line]);
                //match found
                if(matcher.find()){
                    //create a new text position and add to map
                    currTextPosition.setCharOffset(currTextPosition.getCharOffset()+sbList[line].indexOf(name));
                    listOfPositions.add(new TextPosition(currTextPosition.getLineOffset(), currTextPosition.getCharOffset()));
                    NameLocation.put(name, listOfPositions);
                }
                //move char buffer down the line
                currTextPosition.setCharOffset(currTextPosition.getCharOffset() +sbList[line].length());
                line++;
            }
        }
        return NameLocation;
    }
}
