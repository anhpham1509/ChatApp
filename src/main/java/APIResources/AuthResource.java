/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import AuthConfig.AuthService;
import static java.lang.System.out;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.OAuth2FlowGoogleBuilder;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.apache.commons.codec.digest.DigestUtils;
import Model.History;
import Model.User;
import javax.ws.rs.core.Response.Status;

/**
 * REST Web Service
 *
 * @author minhcao
 */
@Path("/auth")
@PermitAll
public class AuthResource {

    private static final String SECRET = "vietnamvodich";
    private History h = History.getInstance();

    @Context
    private UriInfo context;

    public UriInfo getContext() {
        return context;
    }

    public void setContext(UriInfo context) {
        this.context = context;
    }

    /**
     * Creates a new instance of AuthResource
     */
    public AuthResource() {
    }

    @PermitAll
    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public Response register(@FormParam("email") String email, @FormParam("password") String password) {
        User u = new User();
        u.setEmail(email);
        u.setPassword(password);
        u.setRole("User");
        if (h.getUsers().contains(u)) {
            return Response.status(Status.NOT_ACCEPTABLE).build();
        }
        u.setToken(DigestUtils.shaHex(u.getEmail() + SECRET));
        h.addUser(u);
        h.save();
        System.out.println(u.getPassword());
        return Response.ok(u.getToken()).build();
    }

    @PermitAll
    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(@FormParam("email") String email, @FormParam("password") String password) {
        for (User user : h.getUsers()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return Response.ok(user.getToken()).build();
            }
        }
        return Response.status(Status.NOT_ACCEPTABLE).build();
    }

    @PermitAll
    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<User> get() {

        return h.getUsers();
    }

    @Path("/verify")
    @GET
    public Response verifyAuth(@QueryParam("code") String code, @QueryParam("state") String state) {
        final OAuth2CodeGrantFlow flow = AuthService.getFlow();
        final TokenResult tokenResult = flow.finish(code, state);

        AuthService.setAccessToken(tokenResult.getAccessToken());
        String baseUri = context.getBaseUri().toString();
        String rootUri = baseUri.replaceFirst("app/", "");
        return Response.seeOther(UriBuilder.fromUri(rootUri).queryParam("token", tokenResult.getAccessToken()).build()).build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response authenticateUser() {
        return authenticate();

    }

    private Response authenticate() {
        final String redirectURI = UriBuilder.fromUri(context.getBaseUri())
                .path("auth/verify").build().toString();
        OAuth2FlowGoogleBuilder builder = OAuth2ClientSupport.googleFlowBuilder(
                AuthService.getClientIdentifier(),
                redirectURI,
                "profile email");
        OAuth2FlowGoogleBuilder builder2 = builder.prompt(OAuth2FlowGoogleBuilder.Prompt.CONSENT);
        OAuth2CodeGrantFlow flow = builder2.build();

        AuthService.setFlow(flow);

        // start the flow
        final String googleAuthURI = flow.start();
        System.out.println(googleAuthURI);
        // redirect user to Google Authorization URI.
        return Response.seeOther(UriBuilder.fromUri(googleAuthURI).build()).build();
    }

}
