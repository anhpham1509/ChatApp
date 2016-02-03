/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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
    @PermitAll
    @GET
    public void hangUp(@Suspended AsyncResponse asyncResp) {
        users.add(asyncResp);
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

    @PermitAll
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String onMessage(final String message) {
        //TODO return proper representation object
        ex.submit(new Runnable() {
            @Override
            public void run() {
                synchronized(users){
                    Iterator<AsyncResponse> iterator = users.iterator();
                    while(iterator.hasNext()){
                        iterator.next().resume("said that: "+message);
                    }
                }
            }
        });
        return message;
    }
}
