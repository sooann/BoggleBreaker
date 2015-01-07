/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author irisyil
 */
public class BoggleBreaker {

    /**
     * @param args the command line arguments
     */
    private static HashSet<String> gameboards = new HashSet<String>();
    private static HashSet<String> rejectedgameboards = new HashSet<String>();
    private static IWordMap dict;
    private static int duplicateEncountered =0;
    
    private static HashMap<String,Long[]> vowelSequenceStat = new HashMap<String,Long[]>();

    public static void main(String[] args) {

        long RejectedCount = 0;
        long CurrentAcceptedCount = 0;
        long TotalSyllableRejectionCount = 0;

        int MaxUniqueFirstSyll = 0;
        int MinUniqueFirstSyll = 0;
        int TotalUniqueFirstSyll = 0;

        long TotalTimeTaken = 0;
        long MaxTimeTaken = 0;
        long MinTimeTaken = 0;

        long GlobalMaxTimeTaken = 0;
        long GlobalMinTimeTaken = 0;
        long GlobalMeanTotalTimeTaken = 0;

        long countSyllableRej = 0;
        long SyllableRejTimeTaken = 0;
        
        long countSearchResultRej = 0;
        long SearchResultRejTimeTaken = 0;
        
        long TotalMileStoneRejectionCount = 0;
        long countMileStoneRejection = 0;
        long MileStoneRejectionTimeTaken=0;
        
        long TotalSyllMileStoneRejectionCount = 0;
        long countSyllMileStoneRejection = 0;
        long SyllMileStoneRejectionTimeTaken=0;
        
        long TotalVowelError=0;
        long NoVowelFoundError = 0;
        long InvalidVowelCount = 0;
        
        long loadedCount = 0;
        
        //load dictionary
        //dict = new WordMapDB("en_US"); //DB version
        String locale = "en_US";
        //dict = new WordMapMem(locale);  //HashMap version
        dict = new WordMapArray(locale);
        Player play = new Player(dict);
        
        System.out.println("");
        System.out.println("Loading previous Gameboards");
        System.out.println("===========================");
        long timestamp = new Date().getTime();
        loadAcceptedGameboards(locale);
        loadedCount=gameboards.size();
        System.out.println("Time Taken: " + (float) ((new Date().getTime()) - timestamp) / 1000.00 + " Seconds");
        System.out.println("");
        
        Board board = new Board();
        
        timestamp = new Date().getTime();
        long SessionStartTime = timestamp;
        while (gameboards.size() != Integer.MAX_VALUE) {

            //generate Boggle Board
            boolean GenerateBoard = true;
            while (gameboards.contains(board.GenerateContent())) {
                duplicateEncountered++;
            }

            //check if unqiue words >100
            if (play.play(board) > 200) {
                
                CurrentAcceptedCount++;
                
                long timetakenmsec = (new Date().getTime() - timestamp);
                gameboards.add(board.BoardContent());
                
                long timetakensec = timetakenmsec / 1000;
                String Timetaken = (timetakensec / 60) + " min " + (timetakensec % 60) + "." + (timetakenmsec % 1000) + " sec";
                
                //Performance Analysis - Global (Calculation)
                if (GlobalMaxTimeTaken == 0 && GlobalMinTimeTaken == 0) {
                    GlobalMaxTimeTaken = timetakenmsec;
                    GlobalMinTimeTaken = timetakenmsec;
                } else {
                    if (timetakenmsec >= GlobalMaxTimeTaken) {
                        GlobalMaxTimeTaken = timetakenmsec;
                    }
                    
                    if (GlobalMinTimeTaken==0) {
                        GlobalMinTimeTaken = GlobalMeanTotalTimeTaken / gameboards.size();
                    }
                    
                    if (timetakenmsec <= GlobalMinTimeTaken) {
                        GlobalMinTimeTaken = timetakenmsec;
                    }
                }

                GlobalMeanTotalTimeTaken += timetakenmsec;
                
                //Vowel Sequence Stat
                if (vowelSequenceStat.containsKey(board.getVowelSequence())) {
                    Long[] counter = vowelSequenceStat.get(board.getVowelSequence());
                    counter[1]++;
                    vowelSequenceStat.put(board.getVowelSequence(),counter);
                } else {
                    Long[] counter = new Long[] {0L,1L};
                    vowelSequenceStat.put(board.getVowelSequence(),counter);
                }

                //save gamedata
                if((gameboards.size()+1)%40==1) {
                    saveAcceptedGameboards(locale);
                    saveVowelSequence(locale);
                }
                
                //Display analysis of boards taken more than 2sec
                if (timetakenmsec>2000) {
                    
//                    ArrayList sortedKeys=new ArrayList(vowelSequenceStat.keySet());
//                    Collections.sort(sortedKeys);
//
//                    for(int i=0; i<sortedKeys.size(); i++) {
//                        if ((float)vowelSequenceStat.get(sortedKeys.get(i))[0]==0) {
//                            System.out.printf(" %d - %s ", 
//                                vowelSequenceStat.get(sortedKeys.get(i))[1],
//                                sortedKeys.get(i));
//                        } else {
//                            System.out.printf(" %d - %s ", 
//                                vowelSequenceStat.get(sortedKeys.get(i))[1],
//                                sortedKeys.get(i));
//                        }
//
//                        if (((i+1)%17)==0) {
//                            System.out.println("");
//                        }
//                    }
//                    System.out.println("");

                    System.out.println("Board No: " + gameboards.size() + " (Loaded: "+loadedCount+", Curr: "+(gameboards.size() - loadedCount)+"), "
                            + "Rejected count: " + String.format("%,d", RejectedCount) + ", "
                            + "Taken: " + Timetaken);
                    
                    board.printBoard();
                    
                    System.out.println("\n" + new String(new char[100]).replace("\0", "="));
                    
                    //Word Summary
                    System.out.println("No of Accepted Boards: " + gameboards.size() +" Unique Words: " + play.getUniqueWordCount() + ", Words Found: " + play.getWordCount());

                    long TotalAnalysisRejected = 0;
                    
                    //Performance Analysis - Global (Display)
                    System.out.println(
                            String.format("Selection Time Analysis (Global) - Mean: %.4f Min: %.4f Max: %.4f",
                            ((float) GlobalMeanTotalTimeTaken / (float) (CurrentAcceptedCount) / (float) 1000),
                            ((float) GlobalMinTimeTaken / (float) 1000),
                            ((float) GlobalMaxTimeTaken / (float) 1000)));

                    //vowel sequence validation
                    System.out.println ("Vowel Sequqnce Analysis ("+vowelSequenceStat.keySet().size()+"):");
                    
                    //Vowel Validation
                    System.out.printf("Vowel Errors (%,d) - None found: %,d, Invalid Count: %,d \n", TotalVowelError,NoVowelFoundError,InvalidVowelCount);
                    TotalAnalysisRejected += NoVowelFoundError;
                    TotalAnalysisRejected += InvalidVowelCount;
                    
                    //Performance Analysis - Syllable MileStone Rejection
                    if (countSyllMileStoneRejection > 0) {
                        System.out.println(
                                String.format("Syllable Milestone Rejection Analysis: Count: %,d, Time Taken: %d msec, Ave.: %.4f msec, Total: %,d",
                                countSyllMileStoneRejection, SyllMileStoneRejectionTimeTaken,
                                (float) SyllMileStoneRejectionTimeTaken / (float) countSyllMileStoneRejection,
                                TotalSyllMileStoneRejectionCount));
                    }
                    TotalAnalysisRejected += countSyllMileStoneRejection;
                    
                    //Performance Analysis - Syllable Rejection
                    if (countSyllableRej > 0) {
                        System.out.println(
                                String.format("Syllable Rejection Analysis: Count: %,d, Time Taken: %d msec, Ave.: %.4f msec, Total: %,d",
                                countSyllableRej, SyllableRejTimeTaken,
                                (float) SyllableRejTimeTaken / (float) countSyllableRej,
                                TotalSyllableRejectionCount));
                    }
                    TotalAnalysisRejected += countSyllableRej;
                    
                    //Performance Analysis - MileStone Rejection
                    if (countMileStoneRejection>0) {
                        System.out.println(
                                String.format("MileStone Rejection Analysis: Count: %,d, Time Taken: %d msec, Ave.: %.4f msec, Total: %,d",
                                countMileStoneRejection, MileStoneRejectionTimeTaken,
                                (float) MileStoneRejectionTimeTaken / (float) countMileStoneRejection,
                                TotalMileStoneRejectionCount));
                    }
                    TotalAnalysisRejected += countMileStoneRejection;
                    
                    //Performance Analysis - Search Result Rejection
                    if (countSearchResultRej > 0) {
                        System.out.println(
                                String.format("Search Result Rejection Analysis: Count: %,d, Time Taken: %d msec, Ave.: %.4f msec, Total: %,d",
                                countSearchResultRej, SearchResultRejTimeTaken,
                                (float) SearchResultRejTimeTaken / (float) countSearchResultRej,
                                RejectedCount - TotalSyllMileStoneRejectionCount - TotalSyllableRejectionCount - TotalMileStoneRejectionCount - TotalVowelError));
                    }
                    TotalAnalysisRejected += countSearchResultRej;
                    
                    //Duplicate Encountered 
                    System.out.println("Total Rejected Boards: "+ String.format("%,d", TotalAnalysisRejected) +" Rejection Rate: "+String.format("%,.4f",(float)TotalAnalysisRejected/(float)timetakenmsec*1000)+" Duplicate Encountered: "+ duplicateEncountered);
                    //duplicateEncountered=0;

                    //Total Running time
                    long millis = new Date().getTime() - SessionStartTime;
                    String hms = String.format("%02d Days %02d Hrs %02d Mins %02d Secs",
                            TimeUnit.MILLISECONDS.toDays(millis),
                            TimeUnit.MILLISECONDS.toHours(millis) % TimeUnit.DAYS.toHours(1),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                    System.out.println("Total Time Taken: " + hms + " Checking rate (Global): " + String.format("%,.4f",((float)(RejectedCount+CurrentAcceptedCount)/(float)TimeUnit.MILLISECONDS.toSeconds(millis))));

                    System.out.println("");
                    System.out.flush();
                }
                
                //reset Vowel Rejection counters
                NoVowelFoundError=0;
                InvalidVowelCount=0;
                
                //reset Syllable MileStone Rejection Counters
                countSyllMileStoneRejection=0;
                SyllMileStoneRejectionTimeTaken=0;
                
                //reset Syllable Rejection counters
                countSyllableRej = 0;
                SyllableRejTimeTaken = 0;
                
                //reset Search MileStone counter
                countMileStoneRejection=0;
                MileStoneRejectionTimeTaken=0;
                
                //reset Syllable Rejection counters
                countSearchResultRej = 0;
                SearchResultRejTimeTaken = 0;
                
                timestamp = new Date().getTime();

            } else {
//                rejectedgameboards.add(tempboard.BoardContent());
                RejectedCount++;
                
//                if (vowelSequenceStat.containsKey(board.getVowelSequence())) {
//                    Long[] counter = vowelSequenceStat.get(board.getVowelSequence());
//                    counter[0]++;
//                    vowelSequenceStat.put(board.getVowelSequence(),counter);
//                } else {
//                    Long[] counter = new Long[] {1L,0L};
//                    vowelSequenceStat.put(board.getVowelSequence(),counter);
//                }
                
                if (play.IsNoVowelsFound()) {
                    TotalVowelError++;
                    NoVowelFoundError++;
                } else if (play.IsVowelCountInvalid()) {
                    TotalVowelError++;
                    InvalidVowelCount++;
                } else if (play.IsSyllableMileStoneRejected()) {
                    TotalSyllMileStoneRejectionCount++;
                    countSyllMileStoneRejection++;
                    SyllMileStoneRejectionTimeTaken += play.getPreCheckTime();
                } else if (play.IsSyllableRejected()) {
                    TotalSyllableRejectionCount++;
                    countSyllableRej++;
                    SyllableRejTimeTaken += play.getPreCheckTime();
                } else if (play.getMileStoneRejectedQtr()>0) {
                    TotalMileStoneRejectionCount++;
                    countMileStoneRejection++;
                    MileStoneRejectionTimeTaken += play.getTimeTaken();
                } else {
                    countSearchResultRej++;
                    SearchResultRejTimeTaken += play.getTimeTaken();
                }
            }
        }
    }
    
    public static void loadVowelSequence(String locale) {
        File f = new File("dict/"+locale+"_vowel.txt"); 
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader("dict/"+locale+"_vowel.txt"))) {
                String line = br.readLine();
                while (line != null) {
                    // add into database
                    String[] arr = line.split(" - ");
                    vowelSequenceStat.put(arr[1], new Long[] {0l, Long.parseLong(arr[0])});
                    line = br.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void loadAcceptedGameboards(String locale) {
        File f = new File("dict/"+locale+"_gameb.txt"); 
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader("dict/"+locale+"_gameb.txt"))) {
                String line = br.readLine();
                while (line != null) {
                    // add into database
                    String vowelSequence="";
                    for (int i=0; i<line.length(); i++) {
                        if (line.charAt(i)=='A' || line.charAt(i)=='E' || line.charAt(i)=='I' || line.charAt(i)=='O' || line.charAt(i)=='U') {
                            vowelSequence += line.charAt(i);
                        }
                    }
                    
                    if (vowelSequenceStat.containsKey(vowelSequence)) {
                            Long[] counter = vowelSequenceStat.get(vowelSequence);
                            counter[1]++;
                            vowelSequenceStat.put(vowelSequence,counter);
                        } else {
                            Long[] counter = new Long[] {0L,1L};
                            vowelSequenceStat.put(vowelSequence,counter);
                        }
                    gameboards.add(line);
                    line = br.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void saveVowelSequence(String locale) {
        try {
            File file = new File("dict/"+locale+"_vowel.csv");
 
            // if file doesnt exists, then create it
            if (!file.exists()) {
                    file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            PrintWriter pw = new PrintWriter(fw);
             
            ArrayList sortedKeys=new ArrayList(vowelSequenceStat.keySet());
            for (int i=0; i<sortedKeys.size(); i++) {
                pw.println(String.format("%s,%d", 
                    sortedKeys.get(i),vowelSequenceStat.get(sortedKeys.get(i))[1]));
            }
            
            pw.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public static void saveAcceptedGameboards(String locale) {
        try {
            File file = new File("dict/"+locale+"_gameb.txt");
 
            // if file doesnt exists, then create it
            if (!file.exists()) {
                    file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            PrintWriter pw = new PrintWriter(fw);
            
            for (String content : gameboards) {
                pw.println(content);
            }
            
            pw.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
