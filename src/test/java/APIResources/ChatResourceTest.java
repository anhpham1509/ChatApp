/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import Model.Group;
import Model.History;
import Model.HistoryEntry;
import Model.User;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author beochot
 */
public class ChatResourceTest {
    private History h=History.getInstance();
    private List<User> users = h.getUsers();
    private List<HistoryEntry> privateEntries=h.getPrivateEntries();
    private List<HistoryEntry> groupEntries=h.getGroupEntries();
    @Context
    private HttpServletRequest request = new TestHttpServletRequest();
    public ChatResourceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {

        User u = new User("beochot@gmail.com","1234","user");
        u.setAsync(new TestAsyncResponse());
        User u2 =new User("beochot2@gmail.com","1234","user");
        u2.setAsync(new TestAsyncResponse());
        Group g = new Group();
        g.setName("test2");
        Group g2 = new Group();
        g2.setName("test3");
        h.getGroups().remove(g);
        h.getGroups().remove(g2);
        u.getSubcriptions().add(g);
        u.getSubcriptions().add(g2);
        u2.getSubcriptions().add(g);
        u2.getSubcriptions().add(g2);
        users.remove(u);
        users.remove(u2);
        users.add(u);
        users.add(u2);
        h.getGroups().add(g);
        h.getGroups().add(g2);
        
    }
    
    @After
    public void tearDown() {

    }

    /**
     * Test of hangUp method, of class ChatResource.
     */
    @Test
    public void testHangUp() {
        System.out.println("hangUp");
        request.setAttribute("useridx", users.size()-1);
        AsyncResponse asyncResp = users.get(users.size()-1).getAsync();
        ChatResource instance = new ChatResource();
        instance.hangUp(request, asyncResp);
        
    }

    /**
     * Test of test method, of class ChatResource.
     */


    @Test
    public void testChatToPrivate() {
        System.out.println("chatToPrivate");
        String targetPrivate = "beochot2@gmail.com";
        HistoryEntry e = new HistoryEntry(users.get(users.size()-2),targetPrivate,"Kec kec kec");
        request.setAttribute("useridx", users.size()-2);
        ChatResource instance = new ChatResource();
        Response expResult = Response.ok().build();
        Response result = instance.chatToPrivate(e, targetPrivate, request);

        privateEntries.remove(privateEntries.size()-1);
        assertEquals(expResult.getStatus(), result.getStatus());

    }
    @Test
    public void emptyChatToPrivate() {
        System.out.println("emptychatToPrivate");
        String targetPrivate = " ";
        request.setAttribute("useridx", users.size()-2);
        HistoryEntry e = new HistoryEntry(users.get(users.size()-2),targetPrivate," ");
        ChatResource instance = new ChatResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.chatToPrivate(e, targetPrivate, request);
        assertEquals(expResult.getStatus(), result.getStatus());
       
    }
    @Test
    public void wrongOriginChatToPrivate() {
        System.out.println("chatToPrivate");
        String targetPrivate = "beochot2@gmail.com";
        HistoryEntry e = new HistoryEntry(users.get(users.size()-1),targetPrivate,"Kec kec kec");
        request.setAttribute("useridx", users.size()-2);
        ChatResource instance = new ChatResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.chatToPrivate(e, targetPrivate, request);
        assertEquals(expResult.getStatus(), result.getStatus());
 
    }
    @Test
    public void unexistTargetChatToPrivate() {
        System.out.println("chatToPrivate");
        String targetPrivate = "abubaca albagadi";
        HistoryEntry e = new HistoryEntry(users.get(users.size()-2),targetPrivate,"Kec kec kec");
        request.setAttribute("useridx", users.size()-2);
        ChatResource instance = new ChatResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.chatToPrivate(e, targetPrivate, request);
        assertEquals(expResult.getStatus(), result.getStatus());
 
    }
    /**
     * Test of chatToGroup method, of class ChatResource.
     */
    @Test
    public void testChatToGroup() {
        System.out.println("User num: "+users.size());
        System.out.println("chatToGroup");
        request.setAttribute("useridx", users.size()-2);
        String param = "test2";
        HistoryEntry e = new HistoryEntry(users.get(users.size()-2),param,"Kec kec kec");
        ChatResource instance = new ChatResource();
        Response expResult = Response.ok().build();
        Response result = instance.chatToGroup(e, param, request);
        System.out.println("Entries size before:"+groupEntries.size());
        groupEntries.remove(e);
        groupEntries.remove(groupEntries.size()-1);
        assertEquals(expResult.getStatus(), result.getStatus());
        
       
    }
    @Test
    public void testEmptyChatToGroup() {
        System.out.println("User num: "+users.size());
        System.out.println("emptyChatToGroup");
        request.setAttribute("useridx", users.size()-1);
        String param = " ";
        HistoryEntry e = new HistoryEntry(users.get(users.size()-1),param," ");
        ChatResource instance = new ChatResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.chatToGroup(e, param, request);
        assertEquals(expResult.getStatus(), result.getStatus());
       
    }
    @Test
    public void wrongOriginChatToGroup() {
        System.out.println("User num: "+users.size());
        System.out.println("chatToGroup");
        request.setAttribute("useridx", users.size()-1);
        String param = "test2";
        HistoryEntry e = new HistoryEntry(users.get(users.size()-2),param,"Kec kec kec");
        ChatResource instance = new ChatResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.chatToGroup(e, param, request);
        assertEquals(expResult.getStatus(), result.getStatus());
       
    }
    @Test
    public void unexistGroupChatToGroup() {
        System.out.println("User num: "+users.size());
        System.out.println("chatToGroup");
        request.setAttribute("useridx", users.size()-2);
        String param = "not existed";
        HistoryEntry e = new HistoryEntry(users.get(users.size()-2),param,"Kec kec kec");
        ChatResource instance = new ChatResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.chatToGroup(e, param, request);
        assertEquals(expResult.getStatus(), result.getStatus());
    }
  
}
