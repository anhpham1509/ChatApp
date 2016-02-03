/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AuthConfig;

import Config.*;
import java.io.IOException;
import static java.lang.System.out;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.internal.util.Base64;

/**
 *
 * @author minhcao
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
            .entity("You cannot access this resource").build();
    private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
            .entity("Access blocked for all users !!").build();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();

        out.println("ini asdasdasd");
        if (method.isAnnotationPresent(PermitAll.class)) {
            out.println("permit");
            return;
        }

        if (method.isAnnotationPresent(DenyAll.class)) {
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }

        if (method.isAnnotationPresent(RolesAllowed.class)) {
            out.println("in role");
            MultivaluedMap<String, String> headers = requestContext.getHeaders();
            List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }
            String encodedUserPassword = authorization.get(0).replace(AUTHENTICATION_SCHEME + " ", "");
            out.println(encodedUserPassword);
            String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));
            out.println(usernameAndPassword);
            String[] info = usernameAndPassword.split(":");
            if (!isUserAllowed(info[0], info[1])) {
                requestContext.abortWith(ACCESS_DENIED);
            }
            
        }
    }

    private boolean isUserAllowed(String username, String password) {
        out.println(username+":"+password);
        if(username.equals("user") && password.equals("user")) {
            return true;
        }
        return false;
    }
}
