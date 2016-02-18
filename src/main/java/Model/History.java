/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author beochot
 */
public class History implements Serializable{
//volatile
    private static History instance = null;
    private List<HistoryEntry> entries;
    private List<User> users;
    private Set<Group> groups;
    private History() {
        entries = Collections.synchronizedList(new ArrayList<HistoryEntry>());
        users = Collections.synchronizedList(new ArrayList<User>());
        groups= Collections.synchronizedSet(new HashSet<Group>()); 
    }

    public static History getInstance() {
        if (instance == null) {
            synchronized (History.class) {
                if (instance == null) {
                    instance = new History();
                    History.getInstance().restore();
                }
            }

        }
        return instance;

    }

    public void addEntry(HistoryEntry e) {
        entries.add(e);
    }

    public List<HistoryEntry> getEntries() {
        return entries;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User u) {
        this.users.add(u);
    }

    public Set<Group> getGroups() {
        return groups;
    }
 /*   public void addGroup(Group g) {
        this.groups.add(g);
    }
*/
  
    private synchronized void restore() {
        try {
            FileInputStream in = new FileInputStream("history.ser");
            ObjectInputStream obin = new ObjectInputStream(in);
            History.instance = (History) obin.readObject();
            System.out.println("Load");
            obin.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open game.ser");
        } catch (IOException e) {
            System.out.println("Error reading file");
        } catch (ClassNotFoundException e) {
            System.out.println("Error reading object");
        }
     /*if(entries==null)
        entries = new ArrayList<>();*/
    }

    public synchronized void save() {
        try {
            FileOutputStream out = new FileOutputStream("history.ser");
            ObjectOutputStream obout = new ObjectOutputStream(out);
            obout.writeObject(History.instance);
            System.out.println("Save");
            obout.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open game.ser");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error writing into file");
            e.printStackTrace();
        }

    }

}
