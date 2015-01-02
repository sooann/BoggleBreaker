/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.util.ArrayList;
import java.util.Date;

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
        int SyllableRejectionCount = 0;
        
        int MaxUniqueFirstSyll = 0;
        int MinUniqueFirstSyll = 0;
        int TotalUniqueFirstSyll = 0;
        
        long TotalTimeTaken =0;
        long MaxTimeTaken=0;
        long MinTimeTaken=0;
        
        //load dictionary
        dict = new WordDict("en_US");
        
        long timestamp = new Date().getTime();
        while (gameboards.size() != Integer.MAX_VALUE) {
            if (gameboards.size() > 0) {

                //generate Boggle Board
                Board tempboard = GenerateUniqueBoard();

                //run game
                Player play = new Player(tempboard, dict);

                //check if unqiue words >100
                if (play.getUniqueWordCount() > 100) {
                    
                    long timetakensec = (new Date().getTime() - timestamp)/1000;
                    String Timetaken = (timetakensec/60) + " min " + (timetakensec % 60) + " sec";
                    
                    System.out.println("Board No: " + gameboards.size()+ ", Rejected count: "+RejectedCount + " (Syll: " + SyllableRejectionCount + ")" +", Taken: " + Timetaken);
                    tempboard.printBoard();
                    System.out.println("Unique Words: "+play.getUniqueWordCount() + ", Words Found: "+ play.getWordCount());
                    for (int i=0; i<play.getWordList().size(); i++) {
                        System.out.print (play.getWordList().get(i)+", ");
                        if ((i+1)%20==0) {
                            System.out.println("");
                        }
                    }
                    System.out.println("\nNo of Unique First Syllable: "+play.getTwoSyllableCount()+", No of Iterations: "+play.getIteration()+", Time Taken: "+play.getTimeTaken()+" msec");
                    
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
                    System.out.println("Unique First Syllable Analysis: Mean: "+TotalUniqueFirstSyll/gameboards.size()+" Min: "+MinUniqueFirstSyll+" Max: "+MaxUniqueFirstSyll);
                    
                    //Performance Analysis
                    if (MaxTimeTaken==0 && MinTimeTaken==0) {
                        MaxTimeTaken=timetakensec;
                        MinTimeTaken=timetakensec;
                    } else {
                        if (timetakensec>=MaxTimeTaken) {
                            MaxTimeTaken = timetakensec;
                        }
                        
                        if (MinTimeTaken<1) {
                            MinTimeTaken = TotalTimeTaken/gameboards.size();
                        }
                        
                        if (timetakensec<=MinTimeTaken) {
                            MinTimeTaken = timetakensec;
                        }
                    }
                    TotalTimeTaken += timetakensec;
                    System.out.println("Board Selection Analysis: Mean: "+TotalTimeTaken/gameboards.size()+" Min: "+MinTimeTaken+" Max: "+MaxTimeTaken);
                    System.out.println("Total Time Taken: " + TotalTimeTaken/60/60/24 + " Days "+ TotalTimeTaken/60/60 + " Hours " + TotalTimeTaken/60 + " Mins " + TotalTimeTaken%60 + " Secs");
                    
                    System.out.println("");
                    
                    System.out.flush();
                    gameboards.add(tempboard);
                    timestamp = new Date().getTime();
                } else {
                    //blacklistgameboards.add(tempboard);
                    RejectedCount++;
                    if (play.IsSyllableRejected()) {
                        SyllableRejectionCount++;
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
