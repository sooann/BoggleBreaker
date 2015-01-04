/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author irisyil
 */
public class WordMap {

	private HashMap<Integer,HashMap<Boolean,HashSet<String>>> wordmap = new HashMap<Integer,HashMap<Boolean,HashSet<String>>>();
	
	public WordMap(String locale) {
		long timestamp = new Date().getTime();
        System.out.println("Loading Dictionary ("+locale+")");
        System.out.println("===================="+new String(new char[locale.length()]).replace("\0", "=")+"=");
        
		loadWordlist (locale);
		
		System.out.println("Time Taken: " + (float)((new Date().getTime())-timestamp)/1000.00 + " Seconds");
	}
	
	private void loadWordlist (String locale) {
        //find wordlist file
        try (BufferedReader br = new BufferedReader(new FileReader("dict/"+locale+".txt"))) {
            
            String line = br.readLine();

            while (line != null) {
                // add into database
                addWord(line);
                line = br.readLine();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
	
	private void addWord (String word) {
		
		if (!wordmap.containsKey(word.length())) {
			//init
			InitMap(word.length());
		}
		
		if (!wordmap.get(word.length()).get(true).contains(word)) {
			//add word
			wordmap.get(word.length()).get(true).add(word);
		}
		
		for (int i=0; i<word.length()-1; i++) {
			if (!wordmap.get(word.length()).get(false).contains(word.substring(0,i+1))) {
				wordmap.get(word.length()).get(false).add(word.substring(0,i+1));
			}
		}
		
	}
	
	private void InitMap (int length) {
		HashMap<Boolean,HashSet<String>> wordmatch = new HashMap<Boolean,HashSet<String>>();
		wordmatch.put(true, new HashSet<String>());
		wordmatch.put(false, new HashSet<String>());
		wordmap.put(length,wordmatch);
	}
	
	public boolean findWord(String word) {
		return false;
	}
}
