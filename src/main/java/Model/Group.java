/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author beochot
 */
@XmlRootElement
public class Group implements Serializable{
    private String name;
    private boolean isPrivate=false;
    private int size;

    public Group() {

    }

    public Group(String name) {
        this();
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
    @XmlTransient
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

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
