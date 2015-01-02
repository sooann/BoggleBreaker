/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bogglebreaker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import org.h2.jdbcx.JdbcDataSource;

/**
 *
 * @author user
 */
public class WordDict {
    
    private Connection conn;
    private ArrayList<String> twoLetterList = new ArrayList<String>();
    
    public WordDict(String locale) {
        
        long timestamp = new Date().getTime();
        System.out.println("Loading Dictionary ("+locale+")");
        System.out.println("===================="+new String(new char[locale.length()]).replace("\0", "=")+"=");
        
        //Connecting to database
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        try { 
            conn = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        //creating Memory Table Schema
        try (Statement stmt = conn.createStatement()) {
            //create wordlist table
            String SQL = "Create Table Wordlist(word VARCHAR(255)); Create Index IDX_Wordlist_Word on Wordlist(word); ";
            SQL += "Create Table TwoLetterList(word VARCHAR(255)); Create Index IDX_TwoLetterList_Word on TwoLetterList(word); ";
            stmt.execute(SQL);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        //load words from file
        loadWordlist(locale);
        loadTwoLetterList();
        
        try (Statement stmt = conn.createStatement()) {
            
            String SQL = "select count(*) from Wordlist";
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                System.out.println("Loaded " + rs.getString(1) + " Words.");
            } else { 
                System.out.println("Dictionary not Loaded.");
            }
            
            SQL = "select count(*) from TwoLetterList";
            rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                System.out.println("Loaded " + rs.getString(1) + " TwoLetters.");
            } else { 
                System.out.println("Dictionary not Loaded.");
            }
            
            System.out.println("Time Taken: " + ((new Date().getTime())-timestamp)/1000 + " Seconds");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Boolean FindTwoLetters (String word) {
        
        String SQL = "select count(*) from TwoLetterList where word like '"+word+"'";
        boolean result=false;
        
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                if (rs.getInt(1)>0) {
                    result=true;
                } else {
                    result=false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public Boolean FindWord (String word) {
        
        String SQL = "select count(*) from wordlist where word like '"+word+"'";
        boolean result=false;
        
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                if (rs.getInt(1)==1) {
                    result=true;
                } else {
                    result=false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    } 
    
    public Boolean StartsWith (String word) {
        
        boolean result = false;
        String SQL = "select count(*) from wordlist where word like '"+word.toUpperCase()+"%'";
        
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                if (rs.getInt(1)>0) {
                    result=true;
                } else {
                    result=false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    private void loadWordlist (String locale) {
        //find wordlist file
        try (BufferedReader br = new BufferedReader(new FileReader("dict/"+locale+".txt"))) {
            
            String line = br.readLine();

            while (line != null) {
                // add into database
                String SQL = "insert into Wordlist(word) values ('"+line.toUpperCase()+"')";
                conn.createStatement().execute(SQL);
                
                //add into twoletterlist
                if (!twoLetterList.contains(line.substring(0, 2).toUpperCase())) {
                    twoLetterList.add(line.substring(0, 2).toUpperCase());
                }
                
                line = br.readLine();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadTwoLetterList () {
        try (Statement stmt = conn.createStatement()) {
            for (int i=0; i<twoLetterList.size(); i++) {
                String SQL = "insert into TwoLetterList(word) values ('"+twoLetterList.get(i).toUpperCase()+"')";
                stmt.execute(SQL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
