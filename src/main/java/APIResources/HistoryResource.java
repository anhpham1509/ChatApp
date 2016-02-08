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
public class HistoryResource {
    private final History h =History.getInstance();
    private final List<User> users = h.getUsers();
    private final Set<Group> groups = h.getGroups();
    
    
    @RolesAllowed({"Admin","User"})
    @Path("/to/{param}")
    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public List<HistoryEntry> getHistory(@PathParam("param") String param,@Context HttpServletRequest request){
        int user_idx =(int)request.getAttribute("useridx");
        List<HistoryEntry> history=new ArrayList<>();
        if(param.charAt(0)=='@'){
            final String email=param.substring(1);
            User partner=null;
            for(User u:users){
                if(u.getEmail().equals(email)){
                    partner=u;
                    break;
                }
            }
            for(HistoryEntry e:h.getEntries()){
                if(partner!=null&&(e.getTo().equals(users.get(user_idx))||e.getTo().equals(partner))){
                    history.add(e);
                }
            }
        }else if(param.charAt(0)=='#'){
            final String group_name=param.substring(1);
            Group g = new Group();
            g.setName(group_name);
            for(HistoryEntry e:h.getEntries()){
                if(e.getTo().equals(g)){
                    history.add(e);
                }
            }
        }
        return history;
    }
    
}
