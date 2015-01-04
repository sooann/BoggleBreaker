/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    public static void main(String[] args) {

        int RejectedCount = 0;
        int TotalSyllableRejectionCount = 0;

        int MaxUniqueFirstSyll = 0;
        int MinUniqueFirstSyll = 0;
        int TotalUniqueFirstSyll = 0;
        
        HashMap<Integer,Integer[]> SyllableStats = new HashMap<Integer,Integer[]>();
        
        ArrayList<HashMap<Integer,Integer[]>> MileStoneStat = new ArrayList<HashMap<Integer,Integer[]>>();
        MileStoneStat.add(new HashMap<Integer,Integer[]>());
        MileStoneStat.add(new HashMap<Integer,Integer[]>());
        MileStoneStat.add(new HashMap<Integer,Integer[]>());
        MileStoneStat.add(new HashMap<Integer,Integer[]>());

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
        
        //load dictionary
        //dict = new WordMapDB("en_US"); //DB version
        dict = new WordMapMem("en_US"); //DB version

        long timestamp = new Date().getTime();
        long SessionStartTime = timestamp;
        while (gameboards.size() != Integer.MAX_VALUE) {

            //generate Boggle Board
            Board tempboard = GenerateUniqueBoard();

            //run game
            Player play = new Player(tempboard, dict);

            //check if unqiue words >100
            if (play.getUniqueWordCount() > 200) {
                
                long timetakenmsec = (new Date().getTime() - timestamp);
                gameboards.add(tempboard.BoardContent());
                
                if (SyllableStats.containsKey(play.getTwoSyllableCount())) {
                    Integer[] counter = SyllableStats.get(play.getTwoSyllableCount());
                    counter[1]++;
                    SyllableStats.put(play.getTwoSyllableCount(),counter);
                } else {
                    Integer[] counter = new Integer[] {0,1};
                    SyllableStats.put(play.getTwoSyllableCount(),counter);
                }
                
                for (int i=0; i<MileStoneStat.size(); i++) {
                    if (MileStoneStat.get(i).containsKey(play.getQuarterlyMileStones()[i])) {
                        Integer[] counter = MileStoneStat.get(i).get(play.getQuarterlyMileStones()[i]);
                        counter[1]++;
                        MileStoneStat.get(i).put(play.getQuarterlyMileStones()[i], counter);
                    } else {
                        Integer[] counter = new Integer[] {0,1};
                        MileStoneStat.get(i).put(play.getQuarterlyMileStones()[i], counter);
                    }
                }
                
                long timetakensec = timetakenmsec / 1000;
                String Timetaken = (timetakensec / 60) + " min " + (timetakensec % 60) + "." + (timetakenmsec % 1000) + " sec";

                //Display analysis of boards taken more than 2.5sec
                if (timetakenmsec>2500) {
                    System.out.println("Board No: " + gameboards.size() + ", Rejected count: " + RejectedCount + " (Syll: " + TotalSyllableRejectionCount + ", Mile: "+TotalMileStoneRejectionCount+")" + ", Taken: " + Timetaken);
                    tempboard.printBoard();
                    System.out.println("Unique Words: " + play.getUniqueWordCount() + ", Words Found: " + play.getWordCount());
    //                for (int i = 0; i < play.getWordList().size(); i++) {
    //                    System.out.print(play.getWordList().get(i) + ", ");
    //                    if ((i + 1) % 25 == 0) {
    //                        System.out.println("");
    //                    }
    //                }

                    System.out.println("\n" + new String(new char[100]).replace("\0", "="));
                    
                    //Player Iteration Analysis
//                    System.out.println("No of Iterations: " + play.getIteration() + ", Time Taken: " + play.getTimeTaken() + " msec, Iteration Duration: " + (float) play.getTimeTaken() / (float) play.getIteration() * 1000.00 + "ns");

                    //Syllable Analysis
    //                if (MaxUniqueFirstSyll == 0 && MinUniqueFirstSyll == 0) {
    //                    MaxUniqueFirstSyll = play.getTwoSyllableCount();
    //                    MinUniqueFirstSyll = play.getTwoSyllableCount();
    //                } else {
    //                    if (play.getTwoSyllableCount() >= MaxUniqueFirstSyll) {
    //                        MaxUniqueFirstSyll = play.getTwoSyllableCount();
    //                    }
    //
    //                    if (play.getTwoSyllableCount() <= MinUniqueFirstSyll) {
    //                        MinUniqueFirstSyll = play.getTwoSyllableCount();
    //                    }
    //                }
    //
    //                TotalUniqueFirstSyll += play.getTwoSyllableCount();
    //                System.out.println("Unique First Syllable Analysis: Current: " + play.getTwoSyllableCount() + " Mean: " + TotalUniqueFirstSyll / gameboards.size() + " Min: " + MinUniqueFirstSyll + " Max: " + MaxUniqueFirstSyll);

                    //Display syllable distribution
    //                System.out.print("Syllable Distribution: ");
    //                
    //                ArrayList sortedKeys=new ArrayList(SyllableStats.keySet());
    //                Collections.sort(sortedKeys);
    //                
    //                for(int i=0; i<sortedKeys.size(); i++) {
    //                    if ((float)SyllableStats.get(sortedKeys.get(i))[0]==0) {
    //                        System.out.printf(" %02d - %d/%d (%.4f) ", 
    //                            sortedKeys.get(i),
    //                            SyllableStats.get(sortedKeys.get(i))[1],
    //                            SyllableStats.get(sortedKeys.get(i))[0],
    //                            (float)SyllableStats.get(sortedKeys.get(i))[1] / 1.00 * 100.00);
    //                    } else {
    //                        System.out.printf(" %02d - %d/%d (%.4f) ", 
    //                            sortedKeys.get(i),
    //                            SyllableStats.get(sortedKeys.get(i))[1],
    //                            SyllableStats.get(sortedKeys.get(i))[0],
    //                            (float)SyllableStats.get(sortedKeys.get(i))[1] / SyllableStats.get(sortedKeys.get(i))[0] * 100.00);
    //                    }
    //                    
    //                    if (((i+1)%7)==0) {
    //                        System.out.println("");
    //                    }
    //                }
    //                System.out.println("");

                    //Performance Analysis - Sample Size 20
//                    if (gameboards.size() % 20 == 0) {
//                        TotalTimeTaken = 0;
//                        MaxTimeTaken = 0;
//                        MinTimeTaken = 0;
//                    }
//
//                    if (MaxTimeTaken == 0 && MinTimeTaken == 0) {
//                        MaxTimeTaken = timetakenmsec;
//                        MinTimeTaken = timetakenmsec;
//                    } else {
//                        if (timetakenmsec >= MaxTimeTaken) {
//                            MaxTimeTaken = timetakenmsec;
//                        }
//
//                        if (timetakenmsec <= MinTimeTaken) {
//                            MinTimeTaken = timetakenmsec;
//                        }
//                    }
//
//                    TotalTimeTaken += timetakenmsec;
//
//                    System.out.print(
//                            String.format("Selection Time Analysis (20 Samples) - Mean: %.4f Min: %.4f Max: %.4f\t\t",
//                            ((float) TotalTimeTaken / (float) ((gameboards.size() % 20) + 1) / (float) 1000),
//                            ((float) MinTimeTaken / (float) 1000),
//                            ((float) MaxTimeTaken / (float) 1000)));

                    //Performance Analysis - Global
                    if (GlobalMaxTimeTaken == 0 && GlobalMinTimeTaken == 0) {
                        GlobalMaxTimeTaken = timetakenmsec;
                        GlobalMinTimeTaken = timetakenmsec;
                    } else {
                        if (timetakenmsec >= GlobalMaxTimeTaken) {
                            GlobalMaxTimeTaken = timetakenmsec;
                        }

                        if (timetakenmsec <= GlobalMinTimeTaken) {
                            GlobalMinTimeTaken = timetakenmsec;
                        }
                    }
                    GlobalMeanTotalTimeTaken += timetakenmsec;

                    System.out.println(
                            String.format("Selection Time Analysis (Global) - Mean: %.4f Min: %.4f Max: %.4f",
                            ((float) GlobalMeanTotalTimeTaken / (float) (gameboards.size()) / (float) 1000),
                            ((float) GlobalMinTimeTaken / (float) 1000),
                            ((float) GlobalMaxTimeTaken / (float) 1000)));

                    //Performance Analysis - Syllable Rejection
                    if (countSyllableRej > 0) {
                        System.out.println(
                                String.format("Syllable Rejection Analysis: Count: %d, Time Taken: %d msec, Ave.: %.4f msec\t\t",
                                countSyllableRej, SyllableRejTimeTaken,
                                (float) SyllableRejTimeTaken / (float) countSyllableRej));
                    }

                    //reset Syllable Rejection counters
                    countSyllableRej = 0;
                    SyllableRejTimeTaken = 0;
                    
                    //Performance Analysis - MileStone Rejection
                    if (countMileStoneRejection>0) {
                        System.out.println(
                                String.format("MileStone Rejection Analysis: Count: %d, Time Taken: %d msec, Ave.: %.4f msec",
                                countMileStoneRejection, MileStoneRejectionTimeTaken,
                                (float) MileStoneRejectionTimeTaken / (float) countMileStoneRejection));
                    }
                    countMileStoneRejection=0;
                    MileStoneRejectionTimeTaken=0;

                    //Performance Analysis - Search Result Rejection
                    if (countSearchResultRej > 0) {
                        System.out.println(
                                String.format("Search Result Rejection Analysis: Count: %d, Time Taken: %d msec, Ave.: %.4f msec",
                                countSearchResultRej, SearchResultRejTimeTaken,
                                (float) SearchResultRejTimeTaken / (float) countSearchResultRej));
                    }

                    //reset Syllable Rejection counters
                    countSearchResultRej = 0;
                    SearchResultRejTimeTaken = 0;
                    
                    

                    //Duplicate Encountered 
                    System.out.println("Duplicate Encountered: "+ duplicateEncountered);
                    duplicateEncountered=0;

                    //Milestone Analysis
//                    System.out.println("First Milestone Distribution ("+gameboards.size()+"/"+TotalSyllableRejectionCount+"/"+TotalMileStoneRejectionCount+"/"+(RejectedCount-TotalSyllableRejectionCount-TotalMileStoneRejectionCount)+"/"+Timetaken+"): ");
//
//                    ArrayList sortedKeys=new ArrayList(MileStoneStat.get(0).keySet());
//                    Collections.sort(sortedKeys);
//
//                    for(int i=0; i<sortedKeys.size(); i++) {
//                        if ((float)MileStoneStat.get(0).get(sortedKeys.get(i))[0]==0) {
//                            System.out.printf(" %02d - %d/%d (%.4f) ", 
//                                sortedKeys.get(i),
//                                MileStoneStat.get(0).get(sortedKeys.get(i))[1],
//                                MileStoneStat.get(0).get(sortedKeys.get(i))[0],
//                                (float)MileStoneStat.get(0).get(sortedKeys.get(i))[1] / 1.00 * 100.00);
//                        } else {
//                            System.out.printf(" %02d - %d/%d (%.4f) ", 
//                                sortedKeys.get(i),
//                                MileStoneStat.get(0).get(sortedKeys.get(i))[1],
//                                MileStoneStat.get(0).get(sortedKeys.get(i))[0],
//                                (float)MileStoneStat.get(0).get(sortedKeys.get(i))[1] / MileStoneStat.get(0).get(sortedKeys.get(i))[0] * 100.00);
//                        }
//
//                        if (((i+1)%9)==0) {
//                            System.out.println("");
//                        }
//                    }
//                    System.out.println("");
//
//                    System.out.println("Second Milestone Distribution ("+gameboards.size()+"/"+TotalSyllableRejectionCount+"/"+TotalMileStoneRejectionCount+"/"+(RejectedCount-TotalSyllableRejectionCount-TotalMileStoneRejectionCount)+"/"+Timetaken+"): ");
//
//                    sortedKeys=new ArrayList(MileStoneStat.get(1).keySet());
//                    Collections.sort(sortedKeys);
//
//                    for(int i=0; i<sortedKeys.size(); i++) {
//                        if ((float)MileStoneStat.get(1).get(sortedKeys.get(i))[0]==0) {
//                            System.out.printf(" %02d - %d/%d (%.4f) ", 
//                                sortedKeys.get(i),
//                                MileStoneStat.get(1).get(sortedKeys.get(i))[1],
//                                MileStoneStat.get(1).get(sortedKeys.get(i))[0],
//                                (float)MileStoneStat.get(1).get(sortedKeys.get(i))[1] / 1.00 * 100.00);
//                        } else {
//                            System.out.printf(" %02d - %d/%d (%.4f) ", 
//                                sortedKeys.get(i),
//                                MileStoneStat.get(1).get(sortedKeys.get(i))[1],
//                                MileStoneStat.get(1).get(sortedKeys.get(i))[0],
//                                (float)MileStoneStat.get(1).get(sortedKeys.get(i))[1] / MileStoneStat.get(1).get(sortedKeys.get(i))[0] * 100.00);
//                        }
//
//                        if (((i+1)%9)==0) {
//                            System.out.println("");
//                        }
//                    }
//                    System.out.println("");

                    //Total Running time
                    long millis = new Date().getTime() - SessionStartTime;
                    String hms = String.format("%02d Days %02d Hrs %02d Mins %02d Secs",
                            TimeUnit.MILLISECONDS.toDays(millis),
                            TimeUnit.MILLISECONDS.toHours(millis) % TimeUnit.DAYS.toHours(1),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                    System.out.println("Total Time Taken: " + hms);

                    System.out.println("");
                    System.out.flush();
                }
                timestamp = new Date().getTime();

            } else {
                //rejectedgameboards.add(tempboard.BoardContent());
                RejectedCount++;
                if (play.IsSyllableRejected()) {
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
                    
                    if (SyllableStats.containsKey(play.getTwoSyllableCount())) {
                        Integer[] counter = SyllableStats.get(play.getTwoSyllableCount());
                        counter[0]++;
                        SyllableStats.put(play.getTwoSyllableCount(),counter);
                    } else {
                        Integer[] counter = new Integer[] {1,0};
                        SyllableStats.put(play.getTwoSyllableCount(),counter);
                    }
                    
                    for (int i=0; i<MileStoneStat.size(); i++) {
                        if (MileStoneStat.get(i).containsKey(play.getQuarterlyMileStones()[i])) {
                            Integer[] counter = MileStoneStat.get(i).get(play.getQuarterlyMileStones()[i]);
                            counter[0]++;
                            MileStoneStat.get(i).put(play.getQuarterlyMileStones()[i], counter);
                        } else {
                            Integer[] counter = new Integer[] {1,0};
                            MileStoneStat.get(i).put(play.getQuarterlyMileStones()[i], counter);
                        }
                    }
                }
            }
        }
    }

    private static Board GenerateUniqueBoard() {
        boolean GenerateBoard = true;
        Board tempboard = new Board();
        
        while (GenerateBoard) {
            //generate temp board
            tempboard = new Board();
            GenerateBoard = false;

            //search gameboards for duplicate
            if (gameboards.contains(tempboard.BoardContent())) {
                GenerateBoard = true;
            }

            //search in blacklist gameboards for duplicates
//            if (rejectedgameboards.contains(tempboard.BoardContent())) {
//                GenerateBoard = true;
//            }
            
            if (GenerateBoard) {
                duplicateEncountered++;
            }
        }
        return tempboard;
    }
}
