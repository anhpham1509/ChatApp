/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author beochot
 */
@XmlRootElement
public class Group extends Messageable{
    private String name;
    private List<User> subscribers;

    public Group() {
        subscribers=new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }
    @XmlElement
    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }
    public void addSubscribers(User u){
        subscribers.add(u);
    }
    
}
