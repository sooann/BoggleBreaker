/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.util.Random;

/**
 *
 * @author irisyil
 */
public class Board {

    private int Width;
    private int Height;
    private BoardCell Cells[][];

    public Board() {
        this.Width = 4;
        this.Height = 4;
        InitBoard();
    }

    public Board(int Width, int Height) {
        this.Width = Width;
        this.Height = Height;
        InitBoard();
    }

    private void InitBoard() {
        Cells = new BoardCell[this.Width][this.Height];
        for (int i = 0; i < this.Width; i++) {
            for (int k = 0; k < this.Height; k++) {
                Cells[i][k] = new BoardCell(getRandomChar());
            }
        }
    }

    public String BoardContent() {
        String outtext = "";
        for (int i = 0; i < this.Width; i++) {
            for (int k = 0; k < this.Height; k++) {
                outtext += Cells[i][k].getValue();
            }
        }
        return outtext;
    }

    private char getRandomChar() {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int N = alphabet.length();

        Random r = new Random();
        char Output = ' ';

        for (int i = 0; i < 5; i++) {
            Output = alphabet.charAt(r.nextInt(N));
        }

        return Output;
    }

    public void printBoard() {
        String textrow;

        for (int i = 0; i < this.Height; i++) {
            textrow = "";
            System.out.println("");
            for (int k = 0; k < this.Width; k++) {
                textrow += " " + Cells[k][i].getValue() + " ";
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
    
    public BoardCell getCell (int x, int y) {
        return Cells[x][y];
    }
}
