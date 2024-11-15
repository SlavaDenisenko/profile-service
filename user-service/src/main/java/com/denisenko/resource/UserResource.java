package com.denisenko.resource;

import com.denisenko.dto.UserDto;
import com.denisenko.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @GET
    @Path("/{userId}")
    public Response getUser(@PathParam("userId") Integer id, @HeaderParam("X-User-ID") String userId) {
        LOG.info("User ID from header=" + userId);
        if (!userId.equals(String.valueOf(id)))
            return Response.status(FORBIDDEN).entity("You can only view your own profile").build();

        UserDto user = userService.getUser(id);
        return user == null ? Response.status(NO_CONTENT).build() : Response.ok(user).build();
    }

    @PUT
    @Path("/{userId}")
    public Response updateUser(@PathParam("userId") Integer id, UserDto userDto, @HeaderParam("X-User-ID") String userId) {
        if (!userId.equals(String.valueOf(id)))
            return Response.status(FORBIDDEN).entity("You can only update your own profile").build();

        return Response.ok(userService.updateUser(id, userDto)).build();
    }
}
