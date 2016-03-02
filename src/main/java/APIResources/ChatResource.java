/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIResources;

import Model.Group;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import Model.History;
import Model.HistoryEntry;
import Model.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

/**
 * REST Web Service
 *
 * @author minhcao
 */
@Path("/chat")
public class ChatResource {

    @Context
    private UriInfo context;

    @Context
    private ServletContext servletContext;

    private History h = History.getInstance();

    private List<User> users = h.getUsers();


    public ChatResource() {
    }

    /**
     * Retrieves representation of an instance of Resources.ChatResource
     *
     * @param request
     * @param asyncResp
     */
    @RolesAllowed({"Admin", "User"})
    @GET
    public void hangUp(@Context HttpServletRequest request, @Suspended AsyncResponse asyncResp) {
        int user_idx = (int) request.getAttribute("useridx");
        asyncResp.setTimeout(1, TimeUnit.MINUTES);
        users.get(user_idx).setAsync(asyncResp);
        //users.add(asyncResp); ch
    }

    /*
    @RolesAllowed({"Admin"})
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response alert(final HistoryEntry e) {

        h.addEntry(e);
        h.save();
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            iterator.next().getAsync().resume(e);
        }

        return Response.accepted().build();
    }
     */
    @RolesAllowed({"Admin", "User"})
    @Path("/@{param}")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response chatToPrivate(final HistoryEntry e, @PathParam("param") String targetPrivate, @Context HttpServletRequest request) {
        int user_idx = (int) request.getAttribute("useridx");
        final User originUser = users.get(user_idx);
        if (targetPrivate.isEmpty() || targetPrivate.trim().isEmpty() || e.getMesssage().isEmpty() || e.getMesssage().trim().isEmpty() || e.getOrigin().getEmail().isEmpty() || !e.getOrigin().getEmail().equals(originUser.getEmail())) {
            return Response.notAcceptable(null).build();
        }

        System.out.println("in private chat");
        String email = targetPrivate;
        User targetUser = null;
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getEmail().equals(email)) {
                targetUser = user;
                break;
            }
        }
        if (targetUser == null) {
            return Response.notAcceptable(null).build();
        }
        User tUser = targetUser;
        // for sender
        HistoryEntry entry = new HistoryEntry(e.getOrigin(), "", e.getMesssage(), "", "");
        
        originUser.getAsync().resume(entry);

        entry.setTarget("@" + email);
        if (tUser.getAsync() != null) {
            tUser.getAsync().resume(entry);
        }
        entry.getReadUser().add(originUser);
        
        h.addPrivateEntry(entry);
        h.save();
        return Response.ok().build();
    }

    @RolesAllowed({"Admin", "User"})
    @Path("/{param}")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response chatToGroup(final HistoryEntry e, @PathParam("param") String param, @Context HttpServletRequest request) {
        int user_idx = (int) request.getAttribute("useridx");
        User originUser = users.get(user_idx);
        if (param.isEmpty() || param.trim().isEmpty() || e.getMesssage().isEmpty() || e.getMesssage().trim().isEmpty() || e.getOrigin().getEmail().isEmpty() || !e.getOrigin().getEmail().equals(originUser.getEmail())) {
            return Response.notAcceptable(null).build();
        }
        
        System.out.println("in group chat");
        final List<User> groupUser = new ArrayList<>();
        final String group_name = param;
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            System.out.println(user.getEmail());
            for (Group g : user.getSubcriptions()) {

                if (g.getName().equals(group_name)) {
                    System.out.println("Email user " + user.getEmail());
                    groupUser.add(user);
                    break;
                }
            }
        }
        if (groupUser.size() < 1) {
            return Response.notAcceptable(null).build();
        }
        // for sender
        final HistoryEntry entry = new HistoryEntry(e.getOrigin(), "", e.getMesssage(), "", "");
        entry.setTarget(group_name);
        for (User u : groupUser) {
            if (u.getAsync() != null) {
                u.getAsync().resume(entry);
            }
        }
        //         }

        //      });
        originUser.getAsync().resume(entry);

// a thu cach
        entry.getReadUser().add(originUser);
        h.addGroupEntry(entry);
        h.save();
        return Response.ok().build();
    }

    // ---------------------------------------Uploading images-------------------------------------------
    @RolesAllowed({"Admin", "User"})
    @Path("/image/@{param}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFilePrivate(FormDataMultiPart form, @PathParam("param") String targetPrivate, @Context HttpServletRequest request) {
        String fileName = saveFile(form);
        if (fileName == null) {
            // exception message
            return Response.status(500).entity(fileName).build();
        }
        System.out.println(fileName);
        int user_idx = (int) request.getAttribute("useridx");
        User originUser = users.get(user_idx);
        User targetUser = null;
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getEmail().equals(targetPrivate)) {
                targetUser = user;
                break;
            }
        }
        if (targetUser == null) {
            return Response.notAcceptable(null).build();
        }
        final User tUser = targetUser;
        // for sender
        HistoryEntry entry = new HistoryEntry(originUser, "", "", fileName, "image");
        originUser.getAsync().resume(entry);

        // for receiver
        entry.setTarget("@"+targetPrivate);
        if (tUser.getAsync() != null) {
            tUser.getAsync().resume(entry);
        }
        entry.getReadUser().add(originUser);
        h.addPrivateEntry(entry);
        h.save();
        return Response.status(200).entity(fileName).build();
    }

    @RolesAllowed({"Admin", "User"})
    @Path("/image/{param}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFileGroup(FormDataMultiPart form, @PathParam("param") String param, @Context HttpServletRequest request) {
        String fileName = saveFile(form);
        if (fileName == null) {
            // exception message
            return Response.status(500).entity(fileName).build();
        }
        int user_idx = (int) request.getAttribute("useridx");
        User originUser = users.get(user_idx);
        if (param.isEmpty() || param.trim().isEmpty()) {
            return Response.notAcceptable(null).build();
        }
        final List<User> groupUser = new ArrayList<>();

        final String group_name = param;
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            for (Group g : user.getSubcriptions()) {

                if (g.getName().equals(group_name)) {
                    groupUser.add(user);
                    break;
                }
            }
        }
        if (groupUser.size() < 1) {
            return Response.notAcceptable(null).build();
        }
        // for sender
        HistoryEntry entry = new HistoryEntry(originUser, "", "", fileName, "image");
        users.get(user_idx).getAsync().resume(entry);

        // for receivers
        entry.setTarget(group_name);
        for (User u : groupUser) {
            if (u.getAsync() != null) {
                u.getAsync().resume(entry);
            }
        }
        entry.getReadUser().add(originUser);
        h.addGroupEntry(entry);
        h.save();
        return Response.status(200).entity(fileName).build();
    }

    private String saveFile(FormDataMultiPart form) {
        FormDataBodyPart filePart = form.getField("file");
        ContentDisposition headerOfFilePart = filePart.getContentDisposition();
        InputStream fileInputStream = filePart.getValueAs(InputStream.class);
        String fileName = headerOfFilePart.getFileName();
        String filePath = servletContext.getRealPath("images/");

        try {
            OutputStream outputStream = new FileOutputStream(new File(filePath + "/" + fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatResource.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
        return fileName;
    }
}
