/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AuthConfig;

import Model.History;
import Model.User;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author beochot
 */
@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;
    @Context 
    private HttpServletRequest request;
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
            .entity("You cannot access this resource").build();
    private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
            .entity("Access blocked for all users !!").build();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();
        //Access allowed for all

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        }
        //Access denied for all
        if (method.isAnnotationPresent(DenyAll.class)) {
            requestContext.abortWith(ACCESS_FORBIDDEN);
            return;
        }
        
        //Get request headers
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        
        //Fetch authorization header
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        //If no authorization information present; block access
        if (authorization == null || authorization.isEmpty()) {
            requestContext.abortWith(unauthorizedResponse());
            return;
        }

        //Get encoded username and password
        final String token = authorization.get(0);

        System.out.println(token);
        //Verify user access
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

            //Is user valid?
            if (!isUserAllowed(token, rolesSet)) {
                requestContext.abortWith(unauthorizedResponse());
            }
        }

    }
    private Response unauthorizedResponse(){
        return Response.status(Response.Status.UNAUTHORIZED).entity("You cannot access this resource").build();
    }

    private boolean isUserAllowed(final String token, final Set<String> rolesSet) {
        boolean isAllowed = false;

        //Step 1. Fetch password from database and match with password in argument
        //If both match then get the defined role for user from database and continue; else return isAllowed [false]
        //Access the database and do this part yourself
        //String userRole = userMgr.getUserRole(username);
        List<User> users=History.getInstance().getUsers();
        for (int i=0;i<users.size();i++) {
            if (users.get(i).getToken().equals(token)&&rolesSet.contains(users.get(i).getRole())) {
                isAllowed=true;
                request.setAttribute("useridx", i);
                break;
            }
        }
        return isAllowed;
    }

}
