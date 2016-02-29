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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author beochot
 */
@Path("/history")
public class HistoryResource {

    private final History h = History.getInstance();
    private final List<User> users = h.getUsers();

    @RolesAllowed({"Admin", "User"})
    @Path("/@{param}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<HistoryEntry> getPrivateHistory(@PathParam("param") String privateTarget, @Context HttpServletRequest request) {
        System.out.println("in private");
        int user_idx = (int) request.getAttribute("useridx");
        User currentUser = users.get(user_idx);
        List<HistoryEntry> history = new ArrayList<>();
        // subjects to compare
        final String fromEmail = users.get(user_idx).getEmail();
        final String toEmail = privateTarget;
        
        // getting entries needed
        for (HistoryEntry e : h.getPrivateEntries()) {
            String sender = e.getTarget();
            String receiver = e.getOrigin().getEmail();
            if(sender==null){
                System.out.print("Receiver: "+receiver);
                System.out.print("Message"+e.getMesssage());
                System.out.println("Fuck sender");
            }

            if ((sender.equals("@"+fromEmail) && receiver.equals(toEmail))||(sender.equals("@"+toEmail) && receiver.equals(fromEmail))) {
                e.getReadUser().add(currentUser);
                history.add(e);
            }
        }
        return history;
    }

    
    @RolesAllowed({"Admin", "User"})
    @Path("/{param}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<HistoryEntry> getGroupHistory(@PathParam("param") String groupName, @Context HttpServletRequest request) {
        System.out.println("in group");
        List<HistoryEntry> history = new ArrayList<>();
        int user_idx = (int) request.getAttribute("useridx");
        User currentUser = users.get(user_idx);
        if(groupName.isEmpty()||groupName.trim().isEmpty()||!users.get(user_idx).getSubcriptions().contains(new Group(groupName))){
             return history;
        }
        final String group_name = groupName;

        for (HistoryEntry e : h.getGroupEntries()) {
            if ( e.getTarget().equals(group_name)) {
                e.getReadUser().add(currentUser);
                history.add(e);
               
            }
        }
        return history;
    }

    @RolesAllowed({"Admin", "User"})
    @Path("/unread")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Map<String,Integer> getUnreadStatus(@Context HttpServletRequest request) {
        int user_idx = (int) request.getAttribute("useridx");
        Map<String,Integer> unread=new HashMap<>();
        User currentUser = users.get(user_idx);
        Set<Group> currentUserGroup =currentUser.getSubcriptions();
        int count;
        for(Group g:currentUserGroup){
            count=0;
            for(HistoryEntry e:h.getGroupEntries()){
                if(e.getTarget().equals(g.getName())){
                    unread.put(g.getName(),++count);
                }
            }
        }
        count=0;
        for(HistoryEntry e:h.getPrivateEntries()){
            if(e.getTarget().equals("@"+currentUser.getEmail())){
                unread.put(e.getOrigin().getEmail(), ++count);
            }
        }
        
        return unread;
    }

}
