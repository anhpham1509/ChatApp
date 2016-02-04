/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chat.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author beochot
 */
@XmlRootElement
public class HistoryEntry {
    private Date time;
    private User from;
    private Messageable to;
    private String messsage;
    
    public HistoryEntry(){
        
    }
/*    public HistoryEntry(User from, Messageable to,String message) {
        this.time = new Date();
        this.from = from;
        this.to = to;
        this.messsage=message;
    }
*/
    public Date getTime() {
        return time;
    }
    @XmlElement
    public void setTime(Date time) {
        this.time = time;
    }

    public User getFrom() {
        return from;
    }
    @XmlElement
    public void setFrom(User from) {
        this.from = from;
    }

    public Messageable getTo() {
        return to;
    }
    @XmlElement
    public void setTo(Messageable to) {
        this.to = to;
    }

    public String getMesssage() {
        return messsage;
    }
    @XmlElement
    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }
    
    
}
