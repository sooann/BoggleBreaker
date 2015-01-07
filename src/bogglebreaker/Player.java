/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author irisyil
 */
public class Player {

    private Board board;
    private IWordMap Dict;
    
    private String CurrentWord;
    
    private int uniquewordcounter;
    private int WordFoundCount;
    
    private ArrayList<String> WordList = new ArrayList<String>();
    //private ArrayList<String> FirstTwoSyl = new ArrayList<String>();
    
    private HashSet<String> RejectedLetters = new HashSet<String>();
    private HashSet<String> AcceptedLetters = new HashSet<String>();
    
    private int boardHeightLimit;
    private int boardWidthLimit;
    
    private long iterationCount; 
    private long preCheckTimeTaken;
    private long timetaken;
    
    private int ValidSyllables;
    private boolean syllableRejection;
    
    private int[] QuarterlyMileStones;
    private int MileStoneRejectionQtr;
    
    private int[] SyllQuarterlyMileStones;
    private int SyllMileStoneRejectionQtr;
    private boolean RejectBoardSyllMileStone;
    
    private boolean VowelValidation;
    private boolean NoVowelsFound;
    private boolean VowelCountInvalid;
    
    private boolean hasReset = false;
    
    public Player (IWordMap dict) {
        this.Dict = dict;
    }
    
    public void reset() {
        hasReset=true;
        
        CurrentWord = "";
        
        uniquewordcounter=0;
        WordFoundCount=0; 
        WordList.clear();
        
        RejectedLetters.clear();
        AcceptedLetters.clear();
        
        iterationCount = 0; 
        preCheckTimeTaken = 0;
        timetaken=0;

        ValidSyllables=0;
        syllableRejection = false;

        QuarterlyMileStones = new int[4];
        MileStoneRejectionQtr=0;

        SyllQuarterlyMileStones = new int[4];
        SyllMileStoneRejectionQtr=0;
        RejectBoardSyllMileStone = false;

        VowelValidation = true;
        NoVowelsFound =false;
        VowelCountInvalid = false;
    }
    
    public int play(Board board) {
        
        if (!hasReset) {
            reset();
        }

        this.board = board;
        this.boardHeightLimit = board.getHeight();
        this.boardWidthLimit = board.getWidth();
    
        int CurrX=0;
        int CurrY=0;
        long timestamp = new Date().getTime();
        
        //check vowels 
        if (board.getVowelCount()>0) {
            if (board.getVowelCount()<4||board.getVowelCount()>7) {
                VowelValidation=false;
                VowelCountInvalid=true;
            }
        } else {
            VowelValidation=false;
            NoVowelsFound=true;
        }
        
        //count number of 2 valid syllables
        boolean SkipSyllableChecking = false;
        int MileStoneCounter=1;
        while (CurrX<boardWidthLimit && CurrY<boardHeightLimit && !SkipSyllableChecking && !RejectBoardSyllMileStone && VowelValidation) {
            
            findLetters (CurrX, CurrY,2);
            
            //MileStone Rule
            float boardProgress = (float)MileStoneCounter/(float)(boardHeightLimit*boardWidthLimit)*100.00f;
            if (boardProgress<25.00) {
                //SyllQuarterlyMileStones [0] = AcceptedLetters.size();
                SyllQuarterlyMileStones [0] = ValidSyllables;
            } else if (boardProgress>=25.00 && boardProgress<50.00) {
                if (SyllQuarterlyMileStones [0]<6) {
                    RejectBoardSyllMileStone=true;
                    SyllMileStoneRejectionQtr=1;
                } else {
                    //SyllQuarterlyMileStones [1] = AcceptedLetters.size();
                    SyllQuarterlyMileStones [1] = ValidSyllables;
                }
            } else if (boardProgress>=50.00 && boardProgress<75.00) {
                if (SyllQuarterlyMileStones [1]<21) {
                    RejectBoardSyllMileStone=true;
                    SyllMileStoneRejectionQtr=2;
                } else {
                    //SyllQuarterlyMileStones [2] = AcceptedLetters.size();
                    SyllQuarterlyMileStones [2] = ValidSyllables;
                }
            } else {
                if (SyllQuarterlyMileStones [2]<37) {
                    RejectBoardSyllMileStone=true;
                    SyllMileStoneRejectionQtr=3;
                } else {
                    //SyllQuarterlyMileStones [3] = AcceptedLetters.size();
                    SyllQuarterlyMileStones [3] = ValidSyllables;
                }
            }
            MileStoneCounter++;
                    
            if (ValidSyllables>=48) {
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
        
        if (ValidSyllables>=48 && !RejectBoardSyllMileStone) {
            
            //clear wordlist and reset
            WordList.clear();
            //board.getCell(0, 0).ReleaseCell();
            board.setUnuse(0, 0);
            CurrentWord="";
            CurrX=0; CurrY=0; MileStoneCounter=1;
            iterationCount=0;
            
            //MileStone Rejection
            boolean MileStoneRejected = false;
            
            //search for word
            timestamp = new Date().getTime();
            
            while (CurrX<boardWidthLimit && CurrY<boardHeightLimit && !MileStoneRejected) {
                
                findWord (CurrX, CurrY);
                CurrentWord="";
                
                //MileStone Rule
                float boardProgress = (float)MileStoneCounter/(float)(boardHeightLimit*boardWidthLimit)*100.00f;
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
                    uniquewordcounter=0;
                }
                MileStoneCounter++;
                
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
            uniquewordcounter=0;
            WordFoundCount=0;
            WordList.clear();
        }
        
        hasReset=false;
        //return WordList.size();
        return uniquewordcounter;
    }
    
    private void findLetters (int x, int y, int length) {
        if (x>-1 && x<boardWidthLimit && y>-1 && y<boardHeightLimit && (CurrentWord.length())<length) {
            if (!board.IsCellUsed(x, y)) {
                CurrentWord += board.getCellText(x, y);
                board.setUse(x, y);
                
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
                
                board.setUnuse(x, y);
                CurrentWord = CurrentWord.substring(0, CurrentWord.length()-1);
            }
        }
    }
    
    private void findWord (int x, int y) {
        if (x>-1 && x<boardWidthLimit && y>-1 && y<boardHeightLimit) {
//            BoardCell cell = board.getCell(x, y);
//            if (!cell.IsUsed()) {
//                CurrentWord += cell.getValue();
//                cell.UseCell();
//          
            if (!board.IsCellUsed(x, y)) {
                CurrentWord += board.getCellText(x, y);
                board.setUse(x, y);
            
                //System.out.println("Finding Word: "+ CurrentWord);
                
                //check if word found
                if (Dict.FindWord(CurrentWord)) {
                    WordFoundCount++;
                    if (!WordList.contains(CurrentWord)) {
                        WordList.add(CurrentWord);
                        uniquewordcounter++;
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
                
//                cell.ReleaseCell();
                board.setUnuse(x, y);
                CurrentWord = CurrentWord.substring(0, CurrentWord.length()-1);
            }
        }
    }
    
    public int getWordCount() {
        return WordFoundCount;
    }
    
    public int getUniqueWordCount() {
        //return WordList.size();
        return uniquewordcounter;
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
    
    public boolean IsSyllableMileStoneRejected() {
        return this.RejectBoardSyllMileStone;
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
    
    public int[] getSyllQuarterlyMileStones() {
        return SyllQuarterlyMileStones;
    }
    
    public boolean IsNoVowelsFound() {
        return NoVowelsFound;
    }
    
    public boolean IsVowelCountInvalid() {
        return VowelCountInvalid;
    }
}














