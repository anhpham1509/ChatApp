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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
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
public class GroupResourceTest {
    private History h=History.getInstance();
    private List<User> users = h.getUsers();

    @Context
    private HttpServletRequest request = new TestHttpServletRequest();
    public GroupResourceTest() {
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
     * Test of getGroups method, of class GroupResource.
     */
    @Test
    public void testGetJoinedGroups() {
        System.out.println("GetJoinedGroups");
        GroupResource instance = new GroupResource();
        request.setAttribute("useridx",users.size()-2);
        Set<Group> expResult = users.get(users.size()-2).getSubcriptions();
        Set<Group> result = instance.getJoinedGroups(request);
        
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getAllGroups method, of class GroupResource.
     */
    @Test
    public void testGetAllGroups() {
        System.out.println("getAllGroups");
        GroupResource instance = new GroupResource();
        Set<Group> expResult = h.getGroups();
        Set<Group> result = instance.getAllGroups();
        assertEquals(expResult, result);

    }

    /**
     * Test of getGroup method, of class GroupResource.
     */
    @Test
    public void testGetGroupUsers() {
        System.out.println("GetGroupUsers");
        request.setAttribute("useridx",users.size()-2);
        List<User> groupUsers= new ArrayList<>();
        groupUsers.add(users.get(users.size()-2));
        groupUsers.add(users.get(users.size()-1));
        String name = "test2";
        GroupResource instance = new GroupResource();
        List<User> expResult = groupUsers;
        List<User> result = instance.getGroupUsers(request,name);
        assertEquals(expResult, result);
 
    }

    /**
     * Test of create method, of class GroupResource.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        Group g = new Group();
        g.setName("test");
        request.setAttribute("useridx",users.size()-2);
        GroupResource instance = new GroupResource();
        Response expResult = Response.ok().build();
        Response result = instance.createPublicGroup(request,g);
        assertEquals(expResult.getStatus(), result.getStatus());
        
    }
    @Test
        public void testCreateInvalidGroup() {
            System.out.println("CreateInvalidGroup");
            Group g = new Group(" ");
            request.setAttribute("useridx",users.size()-2);
            System.out.println("Group name:"+g.getName());
            GroupResource instance = new GroupResource();
            Response expResult = Response.notAcceptable(null).build();
            Response result = instance.createPublicGroup(request,g);
            assertEquals(expResult.getStatus(), result.getStatus());

        }
    /**
     * Test of join method, of class GroupResource.
     */
    @Test
    public void testJoin() {
        
        System.out.println("join");
        Group g = new Group("test2");
        users.get(users.size()-2).getSubcriptions().remove(g);
        request.setAttribute("useridx",users.size()-2);
        GroupResource instance = new GroupResource();
        Response expResult = Response.ok("ok").build();
        Response result = instance.join(g, request);
        h.getGroupEntries().remove(h.getGroupEntries().size()-1);
        assertEquals(expResult.getStatus(), result.getStatus());
    }
    @Test
    public void testJoinInvalidGroup() {
        System.out.println("JoinInvalidGroup");
        Group g = new Group(" ");
        request.setAttribute("useridx",users.size()-2);
        GroupResource instance = new GroupResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.join(g, request);
        assertEquals(expResult.getStatus(), result.getStatus());
    }
    @Test
    public void testJoinUnexistGroup() {
        System.out.println("JoinUnexistGroup");
        Group g = new Group("214wefaetg2");
        request.setAttribute("useridx",users.size()-2);
        GroupResource instance = new GroupResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.join(g, request);
        assertEquals(expResult.getStatus(), result.getStatus());
    }
    /**
     * Test of leave method, of class GroupResource.
     */
    @Test
    public void testLeave() {
        System.out.println("leave");
        Group g = new Group("test2");
        request.setAttribute("useridx", users.size()-2);
        GroupResource instance = new GroupResource();
        Response expResult = Response.ok("ok").build();
        Response result = instance.leave(g, request);
        h.getGroupEntries().remove(h.getGroupEntries().size()-1);
        assertEquals(expResult.getStatus(), result.getStatus());
    }
    @Test
    public void testLeaveInvalidGroup() {
        System.out.println("leave");
        Group g = new Group(" ");
        request.setAttribute("useridx", users.size()-2);
        GroupResource instance = new GroupResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.leave(g, request);
        assertEquals(expResult.getStatus(), result.getStatus());
    }
    @Test
    public void testLeaveUnexistGroup() {
        System.out.println("leave");
        Group g = new Group("fqwetg234tgwser");
        request.setAttribute("useridx", users.size()-2);
        GroupResource instance = new GroupResource();
        Response expResult = Response.notAcceptable(null).build();
        Response result = instance.leave(g, request);
        assertEquals(expResult.getStatus(), result.getStatus());
    }
}
