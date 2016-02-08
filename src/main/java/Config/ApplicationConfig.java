/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Config;

import AuthConfig.AuthenticationFilter;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author minhcao
 */
@ApplicationPath("/app")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("APIResources");
        register(AuthenticationFilter.class);
    }
}