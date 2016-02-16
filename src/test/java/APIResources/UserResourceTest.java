/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import Model.History;
import Model.User;
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
public class UserResourceTest {
    
    @Context
    private HttpServletRequest request = new TestHttpServletRequest();
    public UserResourceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getAll method, of class UserResource.
     */
    @Test
    public void testGetAll() {
        System.out.println("getAll");
        List<User> users = History.getInstance().getUsers();
        UserResource instance = new UserResource();
        List<User> expResult = null;
        List<User> result = instance.getAll();
        assertEquals(users, result);
        
    }
    
}
