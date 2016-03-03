/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AuthConfig;

import java.io.IOException;
import static java.lang.System.out;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
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
 * @author minhcao
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpServletRequest request;
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        }
        //Access denied for all
        if (method.isAnnotationPresent(DenyAll.class)) {
            requestContext.abortWith(unauthorizedResponse());
            return;
        }

        if (method.isAnnotationPresent(RolesAllowed.class)) {
            MultivaluedMap<String, String> headers = requestContext.getHeaders();
            List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(unauthorizedResponse());
                return;
            }
            String token = authorization.get(0).replace(AUTHENTICATION_SCHEME, "");
            if (!validateGoogle(token) && !validateOwnAuth(token, rolesSet)) {
                requestContext.abortWith(unauthorizedResponse());
            }
        }
    }

    private boolean validateGoogle(String token) {
        try {
            URL url = new URL("https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + token);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();
            if (code == 200) {
                return true;
            } else {
                return false;
            }
//        NetHttpTransport transport = new NetHttpTransport();
//        JsonFactory factory = new GsonFactory();
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier(transport, factory);
//
//        out.println("token: " + token);
//        GoogleIdToken idToken;
//        GoogleIdToken.Payload payload = null;
//        try {
//            idToken = GoogleIdToken.parse(factory, token);
//            if (verifier.verify(idToken)) {
//                GoogleIdToken.Payload tempPayload = idToken.getPayload();
//                out.println(tempPayload);
//                
//            }
//        } catch (GeneralSecurityException | IOException ex) {
//            Logger.getLogger(AuthenticationFilter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return false;
        } catch (MalformedURLException ex) {
            Logger.getLogger(AuthenticationFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AuthenticationFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private Response unauthorizedResponse() {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("You cannot access this resource")
                .build();
    }

    private boolean validateOwnAuth(final String token, final Set<String> rolesSet) {
        boolean isAllowed = false;

        //Step 1. Fetch password from database and match with password in argument
        //If both match then get the defined role for user from database and continue; else return isAllowed [false]
        //Access the database and do this part yourself
        //String userRole = userMgr.getUserRole(username);
        List<User> users = History.getInstance().getUsers();
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getToken()==null){
                continue;
            }
            if (users.get(i).getToken().equals(token) && rolesSet.contains(users.get(i).getRole())) {
                isAllowed = true;
                request.setAttribute("useridx", i);
                break;
            }
        }
        return isAllowed;
    }

}
