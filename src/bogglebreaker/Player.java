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
public class Player {

    private Board board;
    private IWordMap Dict;
    
    private String CurrentWord = "";
    
    private int WordFoundCount=0; 
    private ArrayList<String> WordList = new ArrayList<String>();
    private ArrayList<String> FirstTwoSyl = new ArrayList<String>();
    private int boardHeightLimit;
    private int boardWidthLimit;
    
    private long iterationCount = 0; 
    private long preCheckTimeTaken = 0;
    private long timetaken;
    
    private int ValidSyllables=0;
    private boolean syllableRejection = false;
    
    public Player(Board board, IWordMap Dict) {
        this.board = board;
        this.Dict = Dict;
        this.boardHeightLimit = board.getHeight();
        this.boardWidthLimit = board.getWidth();
        
        int CurrX=0;
        int CurrY=0;
        long timestamp = new Date().getTime();
        
        //count number of 2 valid syllables
        while (CurrX<boardWidthLimit && CurrY<boardHeightLimit) {
            findLetters (CurrX, CurrY,2);
            CurrentWord="";
            CurrX++;
            if (CurrX==boardWidthLimit) {
                CurrY++;
                CurrX=0;
            }
        }
        
        for (int i=0; i<WordList.size(); i++) {
            if (WordList.get(i).length()>1) {
                if (Dict.StartsWith(WordList.get(i))) {
                    ValidSyllables++;
                }
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
            
            //search for word
            timestamp = new Date().getTime();
            while (CurrX<boardWidthLimit && CurrY<boardHeightLimit) {
                findWord (CurrX, CurrY);
                CurrentWord="";
                CurrX++;
                if (CurrX==boardWidthLimit) {
                    CurrY++;
                    CurrX=0;
                }
            }
            
            //analysis
            timetaken = new Date().getTime()-timestamp;

            //analyse syllable
            for (int i=0; i<WordList.size(); i++) {
                if (!FirstTwoSyl.contains(WordList.get(i).substring(0, 1))) {
                    FirstTwoSyl.add(WordList.get(i).substring(0, 1));
                }
            }
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
                
                if (!WordList.contains(CurrentWord)) {
                    WordList.add(CurrentWord);
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
}














