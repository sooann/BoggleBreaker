/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 *
 * @author user
 */
public class WordMapArray implements IWordMap {

    private ArrayList<HashSet<String>> wordmap = new ArrayList<HashSet<String>>();
    private String locale;
    
    public WordMapArray (String locale) {
        
        long timestamp = new Date().getTime();
        System.out.println("Loading Dictionary (" + locale + ")");
        System.out.println("====================" + new String(new char[locale.length()]).replace("\0", "=") + "=");
        this.locale = locale;
        
        int longestword = findLongestWord();
        for (int i=0; i<(longestword*2)+1; i++) {
            wordmap.add(new HashSet<String>());
        }
        loadWordlist();
        
        System.out.println("Time Taken: " + (float) ((new Date().getTime()) - timestamp) / 1000.00 + " Seconds");
        
    }
    
    private void addWord(String word) {

        if (!wordmap.get((word.length()*2)-1).contains(word.toUpperCase())) {
            //add word
            wordmap.get((word.length()*2)-1).add(word.toUpperCase());
        }

        for (int i = 1; i < word.length(); i++) {

            if (!wordmap.get(i*2).contains(word.substring(0, i).toUpperCase())) {
                wordmap.get(i*2).add(word.substring(0, i).toUpperCase());
            }
        }

    }
    
    private int findLongestWord() {
        int longestword = 0;
        //find wordlist file
        try (BufferedReader br = new BufferedReader(new FileReader("dict/" + locale + ".txt"))) {

            String line = br.readLine();

            while (line != null) {
                // add into database
                if (longestword<line.length()) {
                    longestword = line.length();
                }
                 line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return longestword;
    }
    
    private void loadWordlist() {
        //find wordlist file
        try (BufferedReader br = new BufferedReader(new FileReader("dict/" + locale + ".txt"))) {

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
    
    public String getLocale() {
        return locale;
    }

    public Boolean FindWord(String word) {
        return wordmap.get((word.length()*2)-1).contains(word.toUpperCase());
    }

    public Boolean StartsWith(String word) {
        return wordmap.get(word.length()*2).contains(word.toUpperCase());
    }
    
}
