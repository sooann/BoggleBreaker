/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

/**
 *
 * @author irisyil
 */
public class BoardCell {
	
	private char value;
	private boolean used;
	
	public BoardCell (char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
	
	public void UseCell() {
		this.used=true;
	}
	
	public void ReleaseCell() {
		this.used=false;
	}
	
	public boolean IsUsed() {
		return this.used;
	}
}
