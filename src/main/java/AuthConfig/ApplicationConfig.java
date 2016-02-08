/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AuthConfig;

import APIResources.AuthResource;
import APIResources.ChatResource;
import APIResources.GroupResource;
import APIResources.HistoryResource;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author minhcao
 */
@javax.ws.rs.ApplicationPath("app")
public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig(){
        super(ChatResource.class,AuthResource.class,GroupResource.class,HistoryResource.class);


        register(AuthenticationFilter.class);

    }

    
}
