/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import AuthConfig.AuthService;
import Model.History;
import Model.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.OAuth2FlowGoogleBuilder;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author minhcao
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthService.class, OAuth2ClientSupport.class, OAuth2FlowGoogleBuilder.class})
public class AuthResourceTest {
    private History h = History.getInstance();
    private UriInfo mockContext;
    
    public AuthResourceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws URISyntaxException {
        mockContext = mock(UriInfo.class);
        when(mockContext.getBaseUri()).thenReturn(new URI("http://localhost:8080/app/"));
    }
    
    @After
    public void tearDown() {
    }

    @org.junit.Test
    public void testGetUri(){
        AuthResource instance = new AuthResource();
        instance.setContext(mockContext);
        assertEquals(instance.getContext(), mockContext);
    }
    /**
     * Test of register method, of class AuthResource.
     */
    @org.junit.Test
    public void testRegister() {
        
        // Positive test
        System.out.println("register");
        String email = "test3";
        String password = "test3";
        User user = new User();
        user.setEmail(email);
        user.setRole("User");
        AuthResource instance = new AuthResource();
        String expToken = DigestUtils.shaHex(email+"vietnamvodich");
        Response result = instance.register(email, password);
        assertEquals((String) result.getEntity(), expToken);
        
        // Negative test: duplicated user
        Response resultDuplicated = instance.register(email, password);
        assertEquals(resultDuplicated.getStatus(), 406);
        h.getUsers().remove(user);
        h.save();
        
        // Negative test: wrong params
        Response resultNegativeWrongParams = instance.register("  ", "");
        assertEquals(resultNegativeWrongParams.getStatus(), 406);
    }
    
    
    /**
     * Test of login method, of class AuthResource.
     */
    @org.junit.Test
    public void testLogin() {

        // Positive test
        System.out.println("login");
        String email = "test3";
        String password = "test3";
        User user = new User();
        user.setEmail(email);
        user.setRole("User");
        AuthResource instance = new AuthResource();
        //String expToken = DigestUtils.shaHex(email+"vietnamvodich");
        instance.register(email, password);//create dump user
        Response exp = Response.ok().build();
        Response loginResult = instance.login(email, password);
        assertEquals(exp.getStatus(),loginResult.getStatus());
        
        // Negative test: wrong id
        Response resultNegative = instance.login("nouseryet", "nopasswordyet");
        assertEquals(resultNegative.getStatus(), 406);
        h.getUsers().remove(user);
        h.save();
        
        // Negative test: wrong params
        Response resultNegativeWrongParams = instance.login("  ", "");
        assertEquals(resultNegativeWrongParams.getStatus(), 406);
    }


    /**
     * Test of verifyAuth method, of class AuthResource.
     */
    @org.junit.Test
    public void testVerifyAuth() {
        System.out.println("verifyAuth");
        // mock
        String code = "1";
        String state = "2";
        PowerMockito.mockStatic(AuthService.class);
        OAuth2CodeGrantFlow mockFlow = mock(OAuth2CodeGrantFlow.class);
        TokenResult mockToken = mock(TokenResult.class);
        when(mockToken.getAccessToken()).thenReturn("token");
        when(mockFlow.finish(code, state)).thenReturn(mockToken);
        when(AuthService.getFlow()).thenReturn(mockFlow);
        AuthResource instance = new AuthResource();
        instance.setContext(this.mockContext);
        
        
        // run
        String result = instance.verifyAuth(code, state).getLocation().toString();
        assertEquals("http://localhost:8080/?token=token", result);
    }

    /**
     * Test of authenticateUser method, of class AuthResource.
     */
    @org.junit.Test
    public void testAuthenticateUser() {
        System.out.println("authenticateUser");
        // mock
        OAuth2CodeGrantFlow mockFlow = mock(OAuth2CodeGrantFlow.class);
        OAuth2FlowGoogleBuilder mockBuilder = PowerMockito.mock(OAuth2FlowGoogleBuilder.class);
        OAuth2FlowGoogleBuilder mockBuilderNext = PowerMockito.mock(OAuth2FlowGoogleBuilder.class);
        PowerMockito.mockStatic(OAuth2ClientSupport.class);
        doReturn(mockBuilderNext).when(mockBuilder).prompt(OAuth2FlowGoogleBuilder.Prompt.CONSENT);
        doReturn("http://someLink").when(mockFlow).start();
        when(OAuth2ClientSupport.googleFlowBuilder((ClientIdentifier) anyObject(),anyString(),anyString())).thenReturn(mockBuilder);
        doReturn(mockFlow).when(mockBuilderNext).build();

        // run
        System.out.println("run");
        AuthResource instance = new AuthResource();
        instance.setContext(mockContext);
        String result = instance.authenticateUser().getLocation().toString();
        assertEquals("http://someLink", result);
    }
    
}
