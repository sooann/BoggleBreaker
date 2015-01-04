/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 *
 * @author irisyil
 */
public class Player {

    private Board board;
    private IWordMap Dict;
    
    private String CurrentWord = "";
    
    private int WordFoundCount=0; 
    private ArrayList<String> WordList = new ArrayList<String>();
    //private ArrayList<String> FirstTwoSyl = new ArrayList<String>();
    
    private HashSet<String> RejectedLetters = new HashSet<String>();
    private HashSet<String> AcceptedLetters = new HashSet<String>();
    
    private int boardHeightLimit;
    private int boardWidthLimit;
    
    private long iterationCount = 0; 
    private long preCheckTimeTaken = 0;
    private long timetaken;
    
    private int ValidSyllables=0;
    private boolean syllableRejection = false;
    
    private int[] QuarterlyMileStones = new int[4];
    private int MileStoneRejectionQtr=0;
    
    public Player(Board board, IWordMap Dict) {
        this.board = board;
        this.Dict = Dict;
        this.boardHeightLimit = board.getHeight();
        this.boardWidthLimit = board.getWidth();
        
        int CurrX=0;
        int CurrY=0;
        long timestamp = new Date().getTime();
        
        //count number of 2 valid syllables
        boolean SkipSyllableChecking = false;
        while (CurrX<boardWidthLimit && CurrY<boardHeightLimit && !SkipSyllableChecking) {
            findLetters (CurrX, CurrY,2);
            if (AcceptedLetters.size()>=48) {
                SkipSyllableChecking=true;
            }
            CurrentWord="";
            CurrX++;
            if (CurrX==boardWidthLimit) {
                CurrY++;
                CurrX=0;
            }
        }
        
        this.preCheckTimeTaken = new Date().getTime() - timestamp;
        
        if (ValidSyllables>=48) {
            
            //clear wordlist and reset
            WordList.clear();
            board.getCell(0, 0).ReleaseCell();
            CurrentWord="";
            CurrX=0; CurrY=0;
            iterationCount=0;
            
            //MileStone Rejection
            boolean MileStoneRejected = false;
            
            //search for word
            timestamp = new Date().getTime();
            int count=1;
            while (CurrX<boardWidthLimit && CurrY<boardHeightLimit && !MileStoneRejected) {
                
                findWord (CurrX, CurrY);
                CurrentWord="";
                
                //MileStone Rule
                float boardProgress = (float)count/(float)(boardHeightLimit*boardWidthLimit)*100.00f;
                if (boardProgress<25.00) {
                    QuarterlyMileStones [0] = WordList.size();
                } else if (boardProgress>=25.00 && boardProgress<50.00) {
                    if (QuarterlyMileStones [0]<25) {
                        MileStoneRejected=true;
                        MileStoneRejectionQtr=1;
                    } else {
                        QuarterlyMileStones [1] = WordList.size();
                    }
                } else if (boardProgress>=50.00 && boardProgress<75.00) {
                    if (QuarterlyMileStones [1]<60) {
                        MileStoneRejected=true;
                        MileStoneRejectionQtr=2;
                    } else {
                        QuarterlyMileStones [2] = WordList.size();
                    }
                } else {
                    QuarterlyMileStones [3] = WordList.size();
                }
                
                if (MileStoneRejected) {
                    WordList.clear();
                    WordFoundCount=0;
                }
                count++;
                
                CurrX++;
                if (CurrX==boardWidthLimit) {
                    CurrY++;
                    CurrX=0;
                }
            }
            
            //analysis
            timetaken = new Date().getTime()-timestamp;

            //analyse syllable
//            for (int i=0; i<WordList.size(); i++) {
//                if (!FirstTwoSyl.contains(WordList.get(i).substring(0, 1))) {
//                    FirstTwoSyl.add(WordList.get(i).substring(0, 1));
//                }
//            }
            
        } else {
            syllableRejection=true;
            WordFoundCount=0;
            WordList.clear();
        }
    }
    
    private void findLetters (int x, int y, int length) {
        if (x>-1 && x<boardWidthLimit && y>-1 && y<boardHeightLimit && (CurrentWord.length())<length) {
            BoardCell cell = board.getCell(x, y);
            if (!cell.IsUsed()) {
                CurrentWord += cell.getValue();
                cell.UseCell();
                
                if (CurrentWord.length() == length) {
                    if (!RejectedLetters.contains(CurrentWord) && !AcceptedLetters.contains(CurrentWord)) {
                        if (Dict.StartsWith(CurrentWord)) {
                            AcceptedLetters.add(CurrentWord);
                            ValidSyllables++;
                        } else {
                            RejectedLetters.add(CurrentWord);
                        }
                    }
                }
                
                //Go N direction
                findLetters(x,y-1,length);

                //GO NE direction
                findLetters(x+1,y-1,length);

                //Go E direction
                findLetters(x+1,y,length);

                //Go SE direction
                findLetters(x+1,y+1,length);

                //GO S direction
                findLetters(x,y+1,length);

                //GO SW direction
                findLetters(x-1,y+1,length);

                //GO W direction
                findLetters(x-1,y,length);

                //GO NW direction
                findLetters(x-1,y-1,length);  
                
                cell.ReleaseCell();
                CurrentWord = CurrentWord.substring(0, CurrentWord.length()-1);
            }
        }
    }
    
    private void findWord (int x, int y) {
        if (x>-1 && x<boardWidthLimit && y>-1 && y<boardHeightLimit) {
            BoardCell cell = board.getCell(x, y);
            if (!cell.IsUsed()) {
                CurrentWord += cell.getValue();
                cell.UseCell();
                
                //System.out.println("Finding Word: "+ CurrentWord);
                
                //check if word found
                if (Dict.FindWord(CurrentWord)) {
                    WordFoundCount++;
                    if (!WordList.contains(CurrentWord)) {
                        WordList.add(CurrentWord);
                    }
                }
                
                iterationCount++;
                
                //check if current word exist in dictionary
                if (Dict.StartsWith(CurrentWord)) {
                    //Go N direction
                    findWord(x,y-1);

                    //GO NE direction
                    findWord(x+1,y-1);

                    //Go E direction
                    findWord(x+1,y);

                    //Go SE direction
                    findWord(x+1,y+1);

                    //GO S direction
                    findWord(x,y+1);

                    //GO SW direction
                    findWord(x-1,y+1);

                    //GO W direction
                    findWord(x-1,y);

                    //GO NW direction
                    findWord(x-1,y-1);  
                }
                
                cell.ReleaseCell();
                CurrentWord = CurrentWord.substring(0, CurrentWord.length()-1);
            }
        }
    }
    
    public int getWordCount() {
        return WordFoundCount;
    }
    
    public int getUniqueWordCount() {
        return WordList.size();
    }
    
    public ArrayList<String> getWordList() {
        return WordList;
    }
    
    public long getIteration() {
        return this.iterationCount;
    }
    
    public long getTimeTaken() {
        return this.timetaken;
    }
    
    public int getTwoSyllableCount() {
        //return FirstTwoSyl.size();
        return this.ValidSyllables;
    }
    
    public boolean IsSyllableRejected() {
        return this.syllableRejection;
    }
    
    public long getPreCheckTime() {
        return this.preCheckTimeTaken;
    }

    public int[] getQuarterlyMileStones() {
        return QuarterlyMileStones;
    }
    
    public int getMileStoneRejectedQtr() {
        return MileStoneRejectionQtr;
    }
}














