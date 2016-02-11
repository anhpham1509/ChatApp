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
import java.util.List;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
        List<HistoryEntry> history = new ArrayList<>();
        // subjects to compare
        final String fromEmail = users.get(user_idx).getEmail();
        final String toEmail = privateTarget;
        
        // getting entries needed
        for (HistoryEntry e : h.getEntries()) {
            String sender = e.getTo();
            String receiver = e.getFrom().getEmail();
            if ((sender.startsWith("@")) && sender.replaceFirst("@","").equals(fromEmail) && receiver.equals(toEmail)
              ||(sender.startsWith("@")) && sender.replaceFirst("@","").equals(toEmail) && receiver.equals(fromEmail)) {
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
        final String group_name = groupName;
        /*Group g = new Group();
            g.setName(group_name);*/
        for (HistoryEntry e : h.getEntries()) {
            if (!e.getTo().startsWith("@") && (e.getTo()).equals(group_name)) {
                history.add(e);
            }
        }
        return history;
    }
}
