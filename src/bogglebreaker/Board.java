/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author irisyil
 */
public class Board {

    private int Width;
    private int Height;
    //private BoardCell Cells[][];
    
    private String Content;
    private ArrayList<String> ContentArray = new ArrayList<String>();
    private ArrayList<Boolean> UseIndicatorArray = new ArrayList<Boolean>();
    
    private int vowels;
    private int consonants;
    private String vowelSequence;

    public Board() {
        this.Width = 4;
        this.Height = 4;
    }

    public String GenerateContent() {
        
        Content = "";
        ContentArray.clear();
        UseIndicatorArray.clear();

        vowels = 0;
        consonants = 0;
        vowelSequence="";
        
        //Cells = new BoardCell[this.Width][this.Height];
        for (int i = 0; i < this.Height; i++) {
            for (int k = 0; k < this.Width; k++) {
                char letter = getRandomChar();
                //Cells[k][i] = new BoardCell(letter);
                Content += letter;
                ContentArray.add("" + letter);
                UseIndicatorArray.add(false);
                if (letter=='A' || letter=='E' || letter=='I' || letter=='O' || letter=='U') {
                    vowels++;
                    vowelSequence+=letter;
                } else {
                    consonants++;
                }
            }
        }
        
        return Content;
    }

    public String BoardContent() {
        return Content;
    }

    private char getRandomChar() {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int N = alphabet.length();

        Random r = new Random();
        
        return alphabet.charAt(r.nextInt(N));
    }

    public void printBoard() {
        String textrow;

        for (int i = 0; i < this.Height; i++) {
            textrow = "";
            System.out.println("");
            for (int k = 0; k < this.Width; k++) {
                //textrow += " " + Cells[k][i].getValue() + " ";
                textrow += " " + ContentArray.get(k+(i*Width)) + " ";
            }
            System.out.println(textrow);
        }
        System.out.println("");
    }

    public boolean compareTo(Board board) {
        return (this.BoardContent().compareTo(board.BoardContent()) == 0);
    }
    
    public int getWidth() {
        return this.Width;
    }
    
    public int getHeight() {
        return this.Height;
    }
    
//    public BoardCell getCell (int x, int y) {
//        return Cells[x][y];
//    }
    
    public String getCellText(int x, int y) {
        return ContentArray.get(x+(y*Width));
    }
    
    public boolean IsCellUsed(int x, int y) {
        return UseIndicatorArray.get(x+(y*Width));
    }
            
    public void setUse(int x, int y) {
        UseIndicatorArray.set(x+(y*Width), true);
    }
    
    public void setUnuse(int x, int y) {
        UseIndicatorArray.set(x+(y*Width), false);
    }
    
    public int getVowelCount () {
        return vowels;
    }
    
    public int getConsonants() {
        return consonants;
    }
    
    public String getVowelSequence() {
        return vowelSequence;
    }
}
