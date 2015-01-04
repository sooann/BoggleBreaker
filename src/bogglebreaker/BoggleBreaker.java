/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author irisyil
 */
public class BoggleBreaker {

    /**
     * @param args the command line arguments
     */
    private static ArrayList<Board> gameboards = new ArrayList<Board>();
    private static ArrayList<Board> rejectedgameboards = new ArrayList<Board>();
    private static WordDict dict;

    public static void main(String[] args) {

        int RejectedCount=0;
        int TotalSyllableRejectionCount = 0;
        
        int MaxUniqueFirstSyll = 0;
        int MinUniqueFirstSyll = 0;
        int TotalUniqueFirstSyll = 0;
        
        long TotalTimeTaken =0;
        long MaxTimeTaken=0;
        long MinTimeTaken=0;
        
        long GlobalMaxTimeTaken=0;
        long GlobalMinTimeTaken=0;
        long GlobalMeanTotalTimeTaken=0;
        
        long countSyllableRej=0;
        long SyllableRejTimeTaken=0;
        long countSearchResultRej=0;
        long SearchResultRejTimeTaken=0;
        
        //load dictionary
        dict = new WordDict("en_US");
        
        long timestamp = new Date().getTime();
        long SessionStartTime = timestamp;
        while (gameboards.size() != Integer.MAX_VALUE) {
            if (gameboards.size() > 0) {

                //generate Boggle Board
                Board tempboard = GenerateUniqueBoard();

                //run game
                Player play = new Player(tempboard, dict);

                //check if unqiue words >100
                if (play.getUniqueWordCount() > 100) {
                    
                    long timetakenmsec = (new Date().getTime() - timestamp);
                    long timetakensec = timetakenmsec/1000;
                    String Timetaken = (timetakensec/60) + " min " + (timetakensec % 60) + "."+(timetakenmsec%1000)+" sec";
                    
                    System.out.println("Board No: " + gameboards.size()+ ", Rejected count: "+RejectedCount + " (Syll: " + TotalSyllableRejectionCount + ")" +", Taken: " + Timetaken);
                    tempboard.printBoard();
                    System.out.println("Unique Words: "+play.getUniqueWordCount() + ", Words Found: "+ play.getWordCount());
                    for (int i=0; i<play.getWordList().size(); i++) {
                        System.out.print (play.getWordList().get(i)+", ");
                        if ((i+1)%25==0) {
                            System.out.println("");
                        }
                    }
                    
                    System.out.println("\n"+new String(new char[100]).replace("\0", "="));
                    System.out.println("No of Iterations: "+play.getIteration()+", Time Taken: "+play.getTimeTaken()+" msec, Iteration Duration: " + (float)play.getTimeTaken()/(float)play.getIteration()*1000.00 + "ns" );
                    
                    //Syllable Analysis
                    if (MaxUniqueFirstSyll==0 && MinUniqueFirstSyll==0) {
                        MaxUniqueFirstSyll=play.getTwoSyllableCount();
                        MinUniqueFirstSyll=play.getTwoSyllableCount();
                    } else {
                        if (play.getTwoSyllableCount()>=MaxUniqueFirstSyll) {
                            MaxUniqueFirstSyll = play.getTwoSyllableCount();
                        }
                        
                        if (play.getTwoSyllableCount()<=MinUniqueFirstSyll) {
                            MinUniqueFirstSyll = play.getTwoSyllableCount();
                        }
                    }
                    
                    TotalUniqueFirstSyll +=play.getTwoSyllableCount();
                    System.out.println("Unique First Syllable Analysis: Current: "+play.getTwoSyllableCount()+" Mean: "+TotalUniqueFirstSyll/gameboards.size()+" Min: "+MinUniqueFirstSyll+" Max: "+MaxUniqueFirstSyll);
                    
                    //Performance Analysis - Sample Size 20
                    if (gameboards.size()%20==0) {
                        TotalTimeTaken=0;
                        MaxTimeTaken=0;
                        MinTimeTaken=0;
                    }
                    
                    if (MaxTimeTaken==0 && MinTimeTaken==0) {
                        MaxTimeTaken=timetakenmsec;
                        MinTimeTaken=timetakenmsec;
                    } else {
                        if (timetakenmsec>=MaxTimeTaken) {
                            MaxTimeTaken = timetakenmsec;
                        }
                        
                        if (timetakenmsec<=MinTimeTaken) {
                            MinTimeTaken = timetakenmsec;
                        }
                    }
                    
                    TotalTimeTaken += timetakenmsec;
                    
                    System.out.print(
                            String.format("Selection Time Analysis (20 Samples) - Mean: %.4f Min: %.4f Max: %.4f\t\t", 
                            ((float)TotalTimeTaken/(float)((gameboards.size()%20)+1)/(float)1000),
                            ((float)MinTimeTaken/(float)1000),
                            ((float)MaxTimeTaken/(float)1000))
                    );
                    
                    //Performance Analysis - Global
                    if (GlobalMaxTimeTaken==0 && GlobalMinTimeTaken==0) {
                        GlobalMaxTimeTaken=timetakenmsec;
                        GlobalMinTimeTaken=timetakenmsec;
                    } else {
                        if (timetakenmsec>=GlobalMaxTimeTaken) {
                            GlobalMaxTimeTaken = timetakenmsec;
                        }
                        
                        if (timetakenmsec<=GlobalMinTimeTaken) {
                            GlobalMinTimeTaken = timetakenmsec;
                        }
                    }
                    GlobalMeanTotalTimeTaken += timetakenmsec;
                    
                    System.out.println(
                            String.format("Selection Time Analysis (Global) - Mean: %.4f Min: %.4f Max: %.4f",
                            ((float)GlobalMeanTotalTimeTaken/(float)(gameboards.size())/(float)1000),
                            ((float)GlobalMinTimeTaken/(float)1000),
                            ((float)GlobalMaxTimeTaken/(float)1000))
                    );
					
					//Performance Analysis - Syllable Rejection
					if (countSyllableRej >0) {
						System.out.print(
								String.format("Syllable Rejection Analysis: Count: %d, Time Taken: %d msec, Ave.: %.4f msec\t\t",
								countSyllableRej, SyllableRejTimeTaken,
								(float)SyllableRejTimeTaken/(float)countSyllableRej)
						);
					}
					
					//reset Syllable Rejection counters
					countSyllableRej=0;
					SyllableRejTimeTaken=0;
					
					//Performance Analysis - Search Result Rejection
					if (countSearchResultRej>0) {
						System.out.println(
								String.format("Search Result Rejection Analysis: Count: %d, Time Taken: %d msec, Ave.: %.4f msec",
								countSearchResultRej, SearchResultRejTimeTaken, 
								(float)SearchResultRejTimeTaken/(float)countSearchResultRej)
						);	
					}
					
					//reset Syllable Rejection counters
					countSearchResultRej=0;
					SearchResultRejTimeTaken=0;
                    
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
                    gameboards.add(tempboard);
                    timestamp = new Date().getTime();
                    
                } else {
                    //blacklistgameboards.add(tempboard);
                    RejectedCount++;
                    if (play.IsSyllableRejected()) {
                        TotalSyllableRejectionCount++;
						countSyllableRej++;
						SyllableRejTimeTaken += play.getPreCheckTime();
                    } else {
						countSearchResultRej++;
						SearchResultRejTimeTaken += play.getTimeTaken();
					}
                }

            } else {
                gameboards.add(new Board());
            }
        }

    }

    private static Board GenerateUniqueBoard() {
        boolean GenerateBoard = true;
        Board tempboard = new Board();

        while (GenerateBoard) {
            //generate temp board
            tempboard = new Board();
            boolean duplicatefound = false;

            //search gameboards for duplicate
            for (int i = 0; i < gameboards.size(); i++) {
                if (tempboard.compareTo(gameboards.get(i))) {
                    duplicatefound = true;
                }
            }

            //search in blacklist gameboards for duplicates
            for (int i = 0; i < rejectedgameboards.size(); i++) {
                if (tempboard.compareTo(rejectedgameboards.get(i))) {
                    duplicatefound = true;
                }
            }

            GenerateBoard = duplicatefound;
        }
        return tempboard;
    }
}
