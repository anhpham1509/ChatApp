/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import Model.History;
import Model.User;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author beochot
 */
@Path("/user")
public class UserResource {
    private History h=History.getInstance();
    private List<User> users=h.getUsers();
    final static ExecutorService ex = Executors.newSingleThreadExecutor();
    
    @RolesAllowed({"Admin","User"})
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<User> getAll() {
        return users;
    }
    
    @RolesAllowed({"Admin","User"})
    @Path("/promote")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response promote(User promoteUser) {
        for(User u:users){
            if(u.equals(promoteUser)){
                u.setRole("Admin");
                return Response.ok().build();
            }
        }
        
        return Response.notModified().build();
    }
    @RolesAllowed({"Admin","User"})
    @Path("/demote")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response demote(User promoteUser) {
        for(User u:users){
            if(u.equals(promoteUser)){
                u.setRole("User");
                return Response.ok().build();
            }
        }
        
        return Response.notModified().build();
}
}
