/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chat;

import com.chat.model.Group;
import com.chat.model.History;
import com.chat.model.HistoryEntry;
import com.chat.model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

/**
 * REST Web Service
 *
 * @author minhcao
 */
@Path("/chat")
public class ChatResource {

    @Context
    private UriInfo context;
    private History h=History.getInstance();
    final static List<AsyncResponse> users = Collections.synchronizedList(new ArrayList<AsyncResponse>());
    final static ExecutorService ex = Executors.newSingleThreadExecutor();

    /**
     * Creates a new instance of ChatResource
     */
    public ChatResource() {
    }

    /**
     * Retrieves representation of an instance of Resources.ChatResource
     *
     * @return an instance of java.lang.String
     */
    @GET
   
    public void hangUp(@Suspended AsyncResponse asyncResp) {
        users.add(asyncResp);
    }
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

    /**
     * PUT method for updating or creating an instance of ChatResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public void putText(String content) {
        
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String onMessage(final HistoryEntry e) {
        h.addEntry(e);
        h.save();
        ex.submit(new Runnable() {
            @Override
            public void run() {
                synchronized(users){
                    Iterator<AsyncResponse> iterator = users.iterator();
                    while(iterator.hasNext()){
                        iterator.next().resume(e);
                    }
                }
            }
        });
        return "Success";
    }
}
