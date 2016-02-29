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
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
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
public class HistoryResourceTest {
    private History h=History.getInstance();
    private List<User> users = h.getUsers();
    private List<HistoryEntry> privateEntries=h.getPrivateEntries();
    private List<HistoryEntry> groupEntries=h.getGroupEntries();
    @Context
    private HttpServletRequest request = new TestHttpServletRequest();
    public HistoryResourceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {

        User u = new User("beochot","1234","user");
        u.setAsync(new TestAsyncResponse());
        User u2 =new User("beochot2","1234","user");
        u2.setAsync(new TestAsyncResponse());
        Group g = new Group("test2");
        Group g2 = new Group("test3");
        h.getGroups().remove(g);
        h.getGroups().remove(g2);
        u.getSubcriptions().add(g);
        u2.getSubcriptions().add(g);
        privateEntries.add(new HistoryEntry(u,"@"+u2.getEmail(),"haha1"));
        privateEntries.add(new HistoryEntry(u2,"@"+u.getEmail(),"haha2"));
        groupEntries.add(new HistoryEntry(u,g.getName(),"hoho1"));
        groupEntries.add(new HistoryEntry(u2,g.getName(),"hoho1"));
        users.remove(u);
        users.remove(u2);
        users.add(u);
        users.add(u2);
        h.getGroups().add(g);
        h.getGroups().add(g2);
    }
    
    @After
    public void tearDown() {
        privateEntries.remove(privateEntries.size()-1);
        privateEntries.remove(privateEntries.size()-1);
        groupEntries.remove(groupEntries.size()-1);
        groupEntries.remove(groupEntries.size()-1);

    }

    /**
     * Test of getPrivateHistory method, of class HistoryResource.
     */
    @Test
    public void testGetPrivateHistory() {
        System.out.println("testGetPrivateHistory");

        String privateTarget = "beochot2";
        request.setAttribute("useridx", users.size()-2);
        HistoryResource instance = new HistoryResource();
        int expResult = 2;
        List<HistoryEntry> result = instance.getPrivateHistory(privateTarget, request);
        assertEquals(expResult, result.size());
        
    }
    @Test
    public void EmptyInputPrivateHistory() {
        System.out.println("getPrivateHistory");

        String privateTarget = " ";
        request.setAttribute("useridx", users.size()-2);
        HistoryResource instance = new HistoryResource();
        int expResult = 0;
        List<HistoryEntry> result = instance.getPrivateHistory(privateTarget, request);
        assertEquals(expResult, result.size());
        
    }
    @Test
    public void NotExistUserPrivateHistory() {
        System.out.println("NotExistUserPrivateHistory");

        String privateTarget = "abubacaalbagadi";
        request.setAttribute("useridx", users.size()-2);
        HistoryResource instance = new HistoryResource();
        int expResult = 0;
        List<HistoryEntry> result = instance.getPrivateHistory(privateTarget, request);
        assertEquals(expResult, result.size());
        
    }
    /**
     * Test of getGroupHistory method, of class HistoryResource.
     */
    @Test
    public void testGetGroupHistory() {
        System.out.println("getGroupHistory");
        String groupName = "test2";
        request.setAttribute("useridx", users.size()-2);
        HistoryResource instance = new HistoryResource();
        int expResult = 2;
        List<HistoryEntry> result = instance.getGroupHistory(groupName, request);
        for(HistoryEntry e :result){
            System.out.println(e.getOrigin()+":"+e.getMesssage());
        }
        assertEquals(expResult, result.size());
        
    }
    @Test
    public void EmptyInputGetGroupHistory() {
        System.out.println("EmptyInputGetGroupHistory");
        String groupName = " ";
        request.setAttribute("useridx", users.size()-2);
        HistoryResource instance = new HistoryResource();
        int expResult = 0;
        List<HistoryEntry> result = instance.getGroupHistory(groupName, request);

        assertEquals(expResult, result.size());
        
    }
    @Test
    public void NotExistedGroupHistory() {
        System.out.println("NotExistedGroupHistory");
        String groupName = "..asd";
        request.setAttribute("useridx", users.size()-2);
        HistoryResource instance = new HistoryResource();
        int expResult = 0;
        List<HistoryEntry> result = instance.getGroupHistory(groupName, request);

        assertEquals(expResult, result.size());
        
    }
}
