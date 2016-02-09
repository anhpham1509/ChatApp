/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import Model.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.security.PermitAll;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import Model.History;
import Model.HistoryEntry;
import Model.User;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author minhcao
 */
@Path("/chat")
public class ChatResource {

    @Context
    private UriInfo context;

    private History h = History.getInstance();

    private List<User> users = h.getUsers();
    final static ExecutorService ex = Executors.newSingleThreadExecutor();

    /**
     * Creates a new instance of ChatResource
     */
    public ChatResource() {
    }

    /**
     * Retrieves representation of an instance of Resources.ChatResource
     *
     * @param asyncResp
     */
    @RolesAllowed({"Admin", "User"})
    @GET
    public void hangUp(@Context HttpServletRequest request, @Suspended AsyncResponse asyncResp) {
        int user_idx = (int) request.getAttribute("useridx");
        users.get(user_idx).setAsync(asyncResp);
        //users.add(asyncResp);
    }

    @PermitAll
    @Path("/test")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<HistoryEntry> test() {
        /*  //users.add(asyncResp);
       User u = new User();
       u.setToken("98721yeh982qhfr");
       u.setRole("admin");
       u.setEmail("beochot@gmail.com");
       User u2 = new User();
       u2.setToken("98721yeh982qhfr213123");
       u2.setRole("admin2");
       u2.setEmail("beochot@gmail.com2");
       Group g = new Group();
       g.setName("PowerRanger");
       g.addSubscribers(u);
       g.addSubscribers(u2);
       
       HistoryEntry e1 = new HistoryEntry(u,g,"Daibac");
       HistoryEntry e2 = new HistoryEntry(u,u2,"ga ga ga");
       h.addEntry(e1);
       h.addEntry(e2);
         */
        return h.getEntries();
    }

    @RolesAllowed({"Admin", "User"})
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response broadcast(final HistoryEntry e) {

        h.addEntry(e);
        h.save();
        ex.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (users) {
                    Iterator<User> iterator = users.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().getAsync().resume(e);
                    }
                }
            }
        });
        return Response.accepted().build();
    }

    @RolesAllowed({"Admin", "User"})
    @Path("/@{param}")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response chatToPrivate(final HistoryEntry e, @PathParam("param") String targetPrivate, @Context HttpServletRequest request) {
        int user_idx = (int) request.getAttribute("useridx");
        System.out.println("vao group");
        final String email = targetPrivate;
        users.get(user_idx).getAsync().resume(e);
        ex.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (users) {
                    Iterator<User> iterator = users.iterator();
                    while (iterator.hasNext()) {
                        User user = iterator.next();

                        if (user.getEmail().equals(email)) {
                            user.getAsync().resume(e);
                            e.setTo(user);
                            break;
                        }
                    }

                }
            }
        });
        
        h.addEntry(e);
        h.save();
        return Response.accepted().build();
    }

    @RolesAllowed({"Admin", "User"})
    @Path("/{param}")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response chatToGroup(final HistoryEntry e, @PathParam("param") String param, @Context HttpServletRequest request) {
        System.out.println("in group");
        int user_idx = (int) request.getAttribute("useridx");
        final String group_name = param;
        users.get(user_idx).getAsync().resume(e);
        ex.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (users) {
                    Iterator<User> iterator = users.iterator();
                    while (iterator.hasNext()) {
                        User user = iterator.next();
                        System.out.println(user.getEmail());
                        for (Group g : user.getSubcriptions()) {

                            if (g.getName().equals(group_name)) {
                                System.out.println("Email user " + user.getEmail());
                                user.getAsync().resume(e);
                                e.setTo(g);
                                break;
                            }
                        }
                    }
                }
            }
        });
        
        h.addEntry(e);
        h.save();
        return Response.accepted().build();
    }
}
