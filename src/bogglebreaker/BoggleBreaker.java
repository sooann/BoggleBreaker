/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.util.ArrayList;

/**
 *
 * @author irisyil
 */
public class BoggleBreaker {

	/**
	 * @param args the command line arguments
	 */
	
	private static ArrayList<Board> gameboards = new ArrayList<Board>();
	
	public static void main(String[] args) {
		// TODO code application logic here
		
		//load dictionary
		
		while (gameboards.size()!=Integer.MAX_VALUE) {
			if (gameboards.size()>0) {
				
				//generate Boggle Board
				Board tempboard = GenerateUniqueBoard();
				System.out.println("Board No: "+gameboards.size());
				tempboard.printBoard();
				System.out.flush();
				
				//run game
				
				
				gameboards.add(tempboard);
				
				
			} else {
				gameboards.add(new Board());
			}
		}
		
	}
	
	private static Board GenerateUniqueBoard() {
		boolean GenerateBoard=true;
		Board tempboard = new Board();
		
		while (GenerateBoard) {
			//generate temp board
			tempboard = new Board();
			boolean duplicatefound = false;
			//search gameboards for duplicate

			for (int i=0; i<gameboards.size(); i++) {
				if (tempboard.compareTo(gameboards.get(i))) {
					duplicatefound=true;
				}
			}
			GenerateBoard = duplicatefound;
		}
		return tempboard; 
	}
}
