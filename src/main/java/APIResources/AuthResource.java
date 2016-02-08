/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import AuthConfig.AuthService;
import static java.lang.System.out;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
@PermitAll
public class AuthResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AuthResource
     */
    public AuthResource() {
    }
    
    @Path("/test")
    @RolesAllowed("User")
    @GET
    public String testAuth(){
        return "Hello, World!";
    }
    
    @Path("/verify")
    @GET
    public Response verifyAuth(@QueryParam("code") String code, @QueryParam("state") String state){
        final OAuth2CodeGrantFlow flow = AuthService.getFlow();
        final TokenResult tokenResult = flow.finish(code, state);
        
        AuthService.setAccessToken(tokenResult.getAccessToken());
        String baseUri = context.getBaseUri().toString();
        String rootUri = baseUri.replaceFirst("app/", "");
        return Response.seeOther(UriBuilder.fromUri(rootUri).queryParam("token", tokenResult.getAccessToken()).build()).build();
    }
    
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
        final String redirectURI = UriBuilder.fromUri(context.getBaseUri())
                .path("auth/verify").build().toString();
        final OAuth2CodeGrantFlow flow = OAuth2ClientSupport.googleFlowBuilder(
                AuthService.getClientIdentifier(),
                redirectURI,
                "profile email")
                .prompt(OAuth2FlowGoogleBuilder.Prompt.CONSENT).build();

        AuthService.setFlow(flow);

        // start the flow
        final String googleAuthURI = flow.start();

        // redirect user to Google Authorization URI.
        return Response.seeOther(UriBuilder.fromUri(googleAuthURI).build()).build();
    }
    
    
}
