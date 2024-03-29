/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author beochot
 */
@XmlRootElement
public class HistoryEntry implements Serializable {

    private Date time;
    private User origin;
    private String target;
    private String messsage;
    private String filePath;
    private String fileType;
    private Set<User> readUser;
    public HistoryEntry() {
        this.time = new Date();
        this.readUser=new HashSet<>();
    }

    public HistoryEntry(User origin, String target, String message) {
        this();
        this.origin = origin;
        this.target = target;
        this.messsage = message;
    }

    public HistoryEntry(User origin, String target, String message, String filePath, String fileType) {
        this(origin,target,message);
        this.filePath = filePath;
        this.fileType = fileType;
    }

    public Date getTime() {
        return time;
    }

    @XmlElement
    public void setTime(Date time) {
        this.time = time;
    }

    public String getMesssage() {
        return messsage;
    }

    @XmlElement
    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }

    public User getOrigin() {
        return origin;
    }

    @XmlElement
    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getFilePath() {
        return filePath;
    }

    @XmlElement
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    @XmlElement
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Set<User> getReadUser() {
        return readUser;
    }
    @XmlTransient
    public void setReadUser(Set<User> readUser) {
        this.readUser = readUser;
    }
    
}
