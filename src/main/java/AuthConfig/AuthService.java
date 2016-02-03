/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AuthConfig;

import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;

/**
 *
 * @author minhcao
 */
public class AuthService {
    private final static String id = "961793764325-8var4vdpprdujjs60o0nh0kvflvqo9df.apps.googleusercontent.com";
    private final static String secret = "_ydfLRv7jHk5CoBHIQzTcU2N";
    private static String accessToken = null;
    private static OAuth2CodeGrantFlow flow;
    private static ClientIdentifier clientIdentifier = new ClientIdentifier(id, secret);

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        AuthService.accessToken = accessToken;
    }

    public static OAuth2CodeGrantFlow getFlow() {
        return flow;
    }

    public static void setFlow(OAuth2CodeGrantFlow flow) {
        AuthService.flow = flow;
    }

    public static ClientIdentifier getClientIdentifier() {
        return clientIdentifier;
    }

    public static void setClientIdentifier(ClientIdentifier clientIdentifier) {
        AuthService.clientIdentifier = clientIdentifier;
    }
    
}
