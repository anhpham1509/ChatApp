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
import javax.ws.rs.container.AsyncResponse;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author beochot
 */
@XmlRootElement
public class User implements Serializable{
    private String email;
    private String password;
    private String role;
    private String token;
    private Set<Group> subcriptions;
    
    private transient AsyncResponse async;
    public User() {
        subcriptions=new HashSet<>();
    }

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
        subcriptions=new HashSet<>();
    }


    public String getEmail() {
        return email;
    }
    @XmlElement
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    @XmlTransient
    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Group> getSubcriptions() {
        return subcriptions;
    }
    @XmlTransient
    public void setSubcriptions(Set<Group> subcriptions) {
        this.subcriptions = subcriptions;
    }
    
/*
    public boolean isIsOnline() {
        return isOnline;
    }
    @XmlTransient
    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
 */   
    
    public String getRole() {
        return role;
    }
    @XmlTransient
    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }
    @XmlTransient
    public void setToken(String token) {
        this.token = token;
    }

    public AsyncResponse getAsync() {
        return async;
    }
    @XmlTransient
    public void setAsync(AsyncResponse async) {
        this.async = async;
    }
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof User))return false;
        User otherUser = (User)other;
        return this.email.equals(otherUser.email);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.email);
       // hash = 23 * hash + Objects.hashCode(this.role);
        return hash;
    }


    
}
