package com.company;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.net.URL;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws IOException {

        //create pool of threads size: cpu dependent
        ExecutorService pool = Executors.newWorkStealingPool();
        //initiate helper class
        TextPosition currTextPosition = new TextPosition(0, 0);
        final Aggregator aggregator = new Aggregator();

        //create arraylist of the fifty most common names reading them from names file
        ArrayList<String> fiftyMostCommonNames = getNames();

        try {
            //create link to the website
            URL bigText = new URL("http://norvig.com/big.txt");
            Scanner bigTextScanner = new Scanner(bigText.openStream());

            ArrayList<Future<Map<String, ArrayList<TextPosition>>>> futureMatches = new ArrayList<Future<Map<String, ArrayList<TextPosition>>>>();

            //while something left to read
            while (bigTextScanner.hasNext()) {
                StringBuffer batch = new StringBuffer();
                NameMatcher matcher = new NameMatcher();
                int lines = 0;

                //read in 100 lines at a time
                while (lines < 1000 && bigTextScanner.hasNext()) {
                    batch.append(bigTextScanner.nextLine()).append("/n");
                    lines++;
                }
                //java is pass by reference - extract values
                int startLine =currTextPosition.getLineOffset();
                int startChar = currTextPosition.getCharOffset();

                //call the matcher asynchronously
                Callable<Map<String, ArrayList<TextPosition>>> matcherCallable = ()-> matcher.nameSearcher(batch, fiftyMostCommonNames, startLine, startChar);
                //create list of future maps and fill with maps to be aggregated later
                Future<Map<String, ArrayList<TextPosition>>> future = pool.submit( matcherCallable);
                futureMatches.add(future);

                //increment lineOffset by 1000 to get ready for next chunk
                currTextPosition.setLineOffset(currTextPosition.getLineOffset() + lines);
                //increment charOffset by size of the the string buffer
                currTextPosition.setCharOffset(currTextPosition.getCharOffset() + batch.toString().length());

            }
            //ensure that all of the futures have concrete values
            boolean shouldContinue = true;
            while(shouldContinue){
                shouldContinue = false;
                for(Future<Map<String, ArrayList<TextPosition>>> f: futureMatches){
                    if (!f.isDone()){
                        shouldContinue = true;
                    }
                }
            }
            //aggregate into one master map
            for(Future<Map<String, ArrayList<TextPosition>>> f: futureMatches){
                aggregator.add(f.get());
            }
            //write results into file for easier viewing
                try {
                    System.out.println("tasks completed");
                    String outFile = "/src//com//company/out.txt";
                    FileWriter myWriter = new FileWriter(Paths.get("").toAbsolutePath().toString().concat(outFile));
                    myWriter.write(aggregator.getMap().toString());
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    e.printStackTrace();
                }

        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
    static ArrayList<String> getNames () throws IOException {
        ArrayList<String> names = new ArrayList<String>();
        BufferedReader br = null;
        try {
            String inFile = "/src/com/company/names.txt";
            br = new BufferedReader(new FileReader(Paths.get("").toAbsolutePath().toString().concat(inFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = br != null ? br.readLine() : null;
        while (line != null) {
            String[] values = line.split(",");
            names.addAll(Arrays.asList(values));
            line = br.readLine();
        }
        br.close();
        return names;
    }
}

