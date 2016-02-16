/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import Model.Group;
import Model.History;
import Model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author beochot
 */
@Path("/group")
public class GroupResource {
    private final History h =History.getInstance();
    private final List<User> users = h.getUsers();
    private final Set<Group> groups = h.getGroups();
    
    
    @RolesAllowed({"Admin","User"})
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Set<Group> getJoinedGroups(@Context HttpServletRequest request){
        int user_idx =(int)request.getAttribute("useridx");
      
        return users.get(user_idx).getSubcriptions();
       
    }
    @RolesAllowed({"Admin","User"})
    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Set<Group> getAllGroups(){
        return groups;
    }
    
    @RolesAllowed({"Admin","User"})
    @Path("{name}")
    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public List<User> getGroupUsers(@PathParam("param") String name){
        List<User> members = new ArrayList<>();
        Group g = new Group();
        g.setName(name);

            for(User u:users){
                if(u.getSubcriptions().contains(g)){
                    members.add(u);
                }
            }
        
        return members;
    }
    
    @RolesAllowed({"Admin","User"})
    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(Group g){
        if(g.getName().isEmpty()||g.getName().trim().isEmpty()){
          return Response.notAcceptable(null).build();
        }
        groups.add(g);
        h.save();
        return Response.ok().build();
    }
    
    @RolesAllowed({"Admin","User"})
    @Path("/join")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response join(Group g,@Context HttpServletRequest request){
        if(g.getName().isEmpty()||g.getName().trim().isEmpty()){
            return Response.notAcceptable(null).build();
        }
        int useridx = (int)request.getAttribute("useridx");
        System.out.println(useridx);
        if(groups.contains(g)){
            users.get(useridx).getSubcriptions().add(g);
            h.save();
            return Response.ok("ok").build();
        }
        return Response.notAcceptable(null).build();
    }
    
    @RolesAllowed({"Admin","User"})
    @Path("/leave")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response leave(Group g,@Context HttpServletRequest request){
        if(g.getName().isEmpty()||g.getName().trim().isEmpty()){
            return Response.notAcceptable(null).build();
        }
        int useridx = (int)request.getAttribute("useridx");
        if(groups.contains(g)){
            users.get(useridx).getSubcriptions().remove(g);
            return Response.ok().build();
        }
        return Response.notAcceptable(null).build();
    }
    
}