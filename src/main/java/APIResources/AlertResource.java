/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import Model.Alert;
import Model.Group;
import Model.History;
import Model.User;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author minhcao
 */
@Path("alert")
public class AlertResource {

    @Context
    private UriInfo context;
    private History h = History.getInstance();
    private List<User> users = h.getUsers();
    private List<Alert> alerts = h.getAlerts();

    /**
     * Creates a new instance of AlertResource
     */
    public AlertResource() {
    }

    @RolesAllowed({"Admin"})
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response sendAlert(Alert alert, @Context HttpServletRequest request) {
        System.out.println(alert.getOrigin().getEmail() + "--" + alert.getMessage() + "---" + alert.getTargetList());
        int user_idx = (int) request.getAttribute("useridx");
        final User originUser = users.get(user_idx);
        String[] userList = alert.getTargetList().replaceAll(" ", "").split(",");

        // compose users to setAsync
        final List<User> composedList = new ArrayList<>();
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            for (String toBeAlert : userList) {
                System.out.println("user in list " + toBeAlert + "----" + toBeAlert);
                if (user.getEmail().equals(toBeAlert)) {
                    composedList.add(user);
                    break;
                }
            }
        }

        // send alerts
        System.out.println(composedList);
        try {
            for (User anUser : composedList) {
                if (anUser.getAsync() != null) {
                    System.out.println("in" + anUser.getEmail());
                    anUser.getAsync().resume(alert);
                }
            }
            h.addAlert(alert);
            h.save();
            return Response.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.notAcceptable(null).build();
        }
    }

//    @PermitAll
//    @GET
//    @Path("{alertId}")
//    @Produces(MediaType.APPLICATION_XML)
//    public Alert getAlert(@Context HttpServletRequest request, @PathParam("alertId") int alertId) {
//        return h.getAlerts().get(alertId - 1);
//    }
    @RolesAllowed({"Admin","User"})
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<Alert> getAlert(@Context HttpServletRequest request) {
        int user_idx = (int) request.getAttribute("useridx");
        final User currentUser = users.get(user_idx);

        List<Alert> composedList = new ArrayList<>();
        for (Alert alert : alerts) {
            String[] userList = alert.getTargetList().replaceAll(" ", "").split(",");
            for (String username : userList) {
                if (currentUser.getEmail().equals(username)) {
                    composedList.add(alert);
                }
            }
        }

        return composedList;
    }
}
