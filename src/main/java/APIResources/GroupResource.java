/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import Model.Group;
import Model.History;
import Model.HistoryEntry;
import Model.User;
import java.util.ArrayList;
import java.util.HashSet;
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

    private final History h = History.getInstance();
    private final List<User> users = h.getUsers();
    private final Set<Group> groups = h.getGroups();

    @RolesAllowed({"Admin", "User"})
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Set<Group> getJoinedGroups(@Context HttpServletRequest request) {
        int user_idx = (int) request.getAttribute("useridx");

        return users.get(user_idx).getSubcriptions();

    }

    @RolesAllowed({"Admin", "User"})
    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Set<Group> getAllGroups() {
        Set<Group> pgroups=new HashSet<>();
        for(Group g:groups){
            if(!g.isPrivate()){
                pgroups.add(g);
            }
        }
        return groups;
    }
    
    @RolesAllowed({"Admin", "User"})
    @Path("{param}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<User> getGroupUsers(@Context HttpServletRequest request, @PathParam("param") String name) {
        int user_idx = (int) request.getAttribute("useridx");

        User currentUser = users.get(user_idx);
        List<User> members = new ArrayList<>();

        for (User u : users) {
            for (Group g : u.getSubcriptions()) {
                if (g.getName().equals(name)) {
                    members.add(u);
                    break;
                }
            }
        }
        if (!members.contains(currentUser)) {
            return null;
        }
        return members;
    }

    @RolesAllowed({"Admin", "User"})
    @Path("/createPublic")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createPublicGroup(@Context HttpServletRequest request, Group g) {
        int user_idx = (int) request.getAttribute("useridx");
        User currentUser = users.get(user_idx);
        if (g.getName().isEmpty() || g.getName().trim().isEmpty()) {
            return Response.notAcceptable(null).build();
        }
        g.setSize(1);
        groups.add(g);
        currentUser.getSubcriptions().add(g);
        h.save();
        return Response.ok().build();

    }

    @RolesAllowed({"Admin", "User"})
    @Path("/createPrivate")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createPrivateGroup(@Context HttpServletRequest request, Group g) {
        int user_idx = (int) request.getAttribute("useridx");
        User currentUser = users.get(user_idx);
        if (g.getName().isEmpty() || g.getName().trim().isEmpty()) {
            return Response.notAcceptable(null).build();
        }
        g.setPrivate(true);
        g.setSize(1);
        if (groups.add(g)) {
            currentUser.getSubcriptions().add(g);
            h.save();
            return Response.ok().build();
        }
        return Response.notAcceptable(null).build();
    }

    @RolesAllowed({"Admin", "User"})
    @Path("/addUser/{param}")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addUsertoGroup(@Context HttpServletRequest request, @PathParam("param") String groupName, List<User> invitedUsers) {
        Group g = new Group(groupName);
        int user_idx = (int) request.getAttribute("useridx");
        User currentUser = users.get(user_idx);

        if (groupName.isEmpty() || groupName.trim().isEmpty() || users.isEmpty() || (!currentUser.getSubcriptions().contains(g))) {
            return Response.notAcceptable(null).build();
        }
        int numUser = 0;  //number of user that actually join
        for (User invitedUser : invitedUsers) {
            if (!users.contains(invitedUser)) {
                return Response.notAcceptable(null).build();
            }
            for (User u : users) {

                if (u.equals(invitedUser) && u.getSubcriptions().add(g)) {
                    numUser++;
                }
            }
        }
        for (Group gr : groups) {
            if (gr.equals(g)) {
                gr.setSize(gr.getSize() + numUser);
                break;
            }
        }
        h.save();
        return Response.ok().build();
    }

    @RolesAllowed({"Admin", "User"})
    @Path("/join")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response join(Group jg, @Context HttpServletRequest request) {
        if (jg.getName().isEmpty() || jg.getName().trim().isEmpty()) {
            return Response.notAcceptable(null).build();
        }
        int useridx = (int) request.getAttribute("useridx");
        User currentUser = users.get(useridx);
        HistoryEntry entry = new HistoryEntry(new User("System"), jg.getName(), currentUser.getEmail() + " has joined the group");
        for (Group g : groups) {
            if (g.equals(jg) && !g.isPrivate()) {
                if (currentUser.getSubcriptions().add(g)) {
                    g.setSize(g.getSize() + 1);
                    for (User u : users) {
                        if (u.getSubcriptions().contains(g)&&(u.getAsync()!= null)) {
                                u.getAsync().resume(entry);                            
                        }
                    }
                h.addGroupEntry(entry);
                h.save();
                return Response.ok("ok").build();
                }
                
            }
        }

        return Response.notAcceptable(null).build();
    }

    @RolesAllowed({"Admin", "User"})
    @Path("/leave")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response leave(Group lg, @Context HttpServletRequest request) {
        if (lg.getName().isEmpty() || lg.getName().trim().isEmpty()) {
            return Response.notAcceptable(null).build();
        }
        int useridx = (int) request.getAttribute("useridx");

        User currentUser = users.get(useridx);
        HistoryEntry entry = new HistoryEntry(new User("System"), lg.getName(), currentUser.getEmail() + " has leaved the group");
        for (Group g : groups) {
            if (g.equals(lg)) {
                if (currentUser.getSubcriptions().remove(g)) {
                    g.setSize(g.getSize() - 1);
                    for (User u : users) {                        
                        if (u.getSubcriptions().contains(g)&&u.getAsync() != null) {                            
                                u.getAsync().resume(entry);                            
                        }
                    }
                h.addGroupEntry(entry);
                h.save();
                return Response.ok("ok").build();
                }
                
            }
        }
        return Response.notAcceptable(null).build();
    }

}
