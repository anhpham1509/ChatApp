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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author beochot
 */
public class History implements Serializable{

    private static History instance = null;

    private CopyOnWriteArrayList<HistoryEntry> groupEntries;
    private CopyOnWriteArrayList<HistoryEntry> privateEntries;
    private CopyOnWriteArrayList<User> users;
    private Set<Group> groups;
    private History() {

        users =new CopyOnWriteArrayList<>();
        groups=Collections.synchronizedSet(new HashSet<Group>()); 
        groupEntries=new CopyOnWriteArrayList<>();
        privateEntries=new CopyOnWriteArrayList<>();
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


    public void addUser(User u){
        users.add(u);
    }
    public List<User> getUsers() {
        return users;
    }

    public Set<Group> getGroups() {
        return groups;
    }
    public void addGroupEntry(HistoryEntry e) {
        groupEntries.add(e);
    }
    public void addPrivateEntry(HistoryEntry e) {
        privateEntries.add(e);
    }

    public CopyOnWriteArrayList<HistoryEntry> getGroupEntries() {
        return groupEntries;
    }

    public CopyOnWriteArrayList<HistoryEntry> getPrivateEntries() {
        return privateEntries;
    }


  
    private void restore() {
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
        } catch (IOException e) {
            System.out.println("Error writing into file");
        }
    }

}
