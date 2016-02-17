/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author beochot
 */
@XmlRootElement
public class Group extends Messageable implements Serializable{
    private String name;
    //private List<User> subscribers;

    public Group() {
      //  subscribers=new ArrayList<>();
    }

    public Group(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    @XmlElement
    public void setName(String name) {
        this.name = name;
    }
/*
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
*/
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Group))return false;
        Group otherGroup = (Group)other;
        return this.name.equals(otherGroup.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.name);
        //hash = 73 * hash + Objects.hashCode(this.subscribers);
        return hash;
    }

}
