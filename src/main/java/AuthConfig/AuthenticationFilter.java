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
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        }

        if (method.isAnnotationPresent(DenyAll.class)) {
            requestContext.abortWith(unauthorizedResponse());
            return;
        }

        if (method.isAnnotationPresent(RolesAllowed.class)) {
            MultivaluedMap<String, String> headers = requestContext.getHeaders();
            List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(unauthorizedResponse());
                return;
            }
            String token = authorization.get(0).replace(AUTHENTICATION_SCHEME + " ", "");
            out.println(token);
            if (!isUserAllowed(token)) {
                requestContext.abortWith(unauthorizedResponse());
            }
        }
    }

    private boolean isUserAllowed(String token) {
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
}
