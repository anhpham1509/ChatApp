/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import AuthConfig.AuthService;
import static java.lang.System.out;
import java.net.URI;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.OAuth2FlowGoogleBuilder;
import org.glassfish.jersey.client.oauth2.TokenResult;

/**
 * REST Web Service
 *
 * @author minhcao
 */
@Path("/auth")
public class AuthResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AuthResource
     */
    public AuthResource() {
    }
    
    @Path("/test")
    @GET
    public String testAuth(){
        return "<html><body><h1>Hello, World!</body></h1></html>";
    }
    
    @Path("/verify")
    @GET
    public Response verifyAuth(@QueryParam("code") String code, @QueryParam("state") String state){
        final OAuth2CodeGrantFlow flow = AuthService.getFlow();

        final TokenResult tokenResult = flow.finish(code, state);

        out.println("token "+tokenResult.getAccessToken()+" "+tokenResult.getTokenType()+" "+tokenResult.toString());
        AuthService.setAccessToken(tokenResult.getAccessToken());
        return Response.seeOther(UriBuilder.fromUri(context.getBaseUri()).path("auth/test").build()).build();
    }
    
//    @POST
//    @Produces(MediaType.TEXT_PLAIN)
//    @Consumes("application/x-www-form-urlencoded")
//    public Response authenticateUser(@FormParam("username") String username, 
//                                     @FormParam("password") String password){
//        try {
//            return authenticate();
//        } catch (Exception e){
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
//        
//    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response authenticateUser(){
        try {
            return authenticate();
        } catch (Exception e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
    }
    
    
    private Response authenticate() throws Exception {
        out.println("in login"+context.getBaseUri());
        final String redirectURI = UriBuilder.fromUri(context.getBaseUri())
                .path("auth/verify").build().toString();
        out.println(redirectURI);
        final OAuth2CodeGrantFlow flow = OAuth2ClientSupport.googleFlowBuilder(
                AuthService.getClientIdentifier(),
                redirectURI,
                "https://www.googleapis.com/auth/userinfo.profile")
                .prompt(OAuth2FlowGoogleBuilder.Prompt.CONSENT).build();

        AuthService.setFlow(flow);

        // start the flow
        final String googleAuthURI = flow.start();

        // redirect user to Google Authorization URI.
        return Response.seeOther(UriBuilder.fromUri(googleAuthURI).build()).build();
    }
    
    
}
