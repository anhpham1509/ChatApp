/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author minhcao
 */
@XmlRootElement
public class Alert implements Serializable {

    private int _id;

    private Date time;
    private User origin;
    private String message;
    private String targetList;
    private Set<User> confirmList;

    private History h = History.getInstance();
    private List<Alert> alerts = h.getAlerts();

    public Alert() {
        this.time = new Date();
        this._id = alerts.size() + 1;
        this.confirmList = new HashSet<>();
    }

    public Alert(User origin, String targetList, String message) {
        this();
        this.origin = origin;
        this.targetList = targetList;
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    @XmlElement
    public void setTime(Date time) {
        this.time = time;
    }

    public User getOrigin() {
        return origin;
    }

    @XmlElement
    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getTargetList() {
        return targetList;
    }

    @XmlElement
    public void setTargetList(String targetList) {
        this.targetList = targetList;
    }

    public String getMessage() {
        return message;
    }

    @XmlElement
    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return _id;
    }

    @XmlElement
    public void setId(int _id) {
        this._id = _id;
    }

    public Set<User> getConfirmList() {
        return confirmList;
    }

    @XmlTransient
    public void setConfirmList(Set<User> confirmList) {
        this.confirmList = confirmList;
    }
}
