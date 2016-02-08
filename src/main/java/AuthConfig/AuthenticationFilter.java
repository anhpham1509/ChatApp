/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AuthConfig;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.out;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.NotAuthorizedException;
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
            out.println("in2");
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }

        if (method.isAnnotationPresent(RolesAllowed.class)) {
            out.println("in role");
            MultivaluedMap<String, String> headers = requestContext.getHeaders();
            List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            if (authorization == null || authorization.isEmpty()) {
                            out.println("in3");
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }
            String token = authorization.get(0).replace(AUTHENTICATION_SCHEME + " ", "");
            out.println(token);
            if (!isUserAllowed(token)) {
                requestContext.abortWith(ACCESS_DENIED);
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
            out.println("code "+code);
            if(code==200){
                out.println("in success");
                return false;    
            } else {
                                out.println("in fale");

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
}
