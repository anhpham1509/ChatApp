/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import Model.History;
import Model.User;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.codec.digest.DigestUtils;


/**
 *
 * @author beochot
 */
@Path("/auth")
public class AuthResource {
    private static final String SECRET="vietnamvodich";
    private History h =History.getInstance();
    public AuthResource() {
        
    }
    @PermitAll
    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public String register(@FormParam("email") String email,@FormParam("password") String password) {
        User u = new User();
        u.setEmail(email);
        u.setPassword(password);
        u.setRole("User");
        if(h.getUsers().contains(u)){
            return null;  
        }
        u.setToken(DigestUtils.shaHex(u.getEmail()+SECRET));
        h.addUser(u);
        h.save();
        System.out.println(u.getPassword());
        return u.getToken();
    }
    @PermitAll
    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String login(@FormParam("email") String email,@FormParam("password") String password) {
        for(User user:h.getUsers()){
            if(user.getEmail().equals(email)&&user.getPassword().equals(password)){
                return user.getToken();
            }
        }
        return null;
    }
    @PermitAll
    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<User> get() {
        
       return h.getUsers();
    }
}
