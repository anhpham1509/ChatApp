/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chat.model;

import java.io.Serializable;
import java.util.Objects;
import javax.ws.rs.container.AsyncResponse;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author beochot
 */
@XmlRootElement
public class User extends Messageable implements Serializable{
    private String email;
    private String role;
    private String token;
    private AsyncResponse async;

    public User() {

    }
    
    public String getEmail() {
        return email;
    }
    @XmlElement
    public void setEmail(String email) {
        this.email = email;
    }

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
        return this.email.equals(otherUser.email)&&this.role.equals(otherUser.role);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.email);
        hash = 31 * hash + Objects.hashCode(this.role);
        return hash;
    }
    
}
