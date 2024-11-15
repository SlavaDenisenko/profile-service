package com.denisenko.resource;

import com.denisenko.entity.User;
import com.denisenko.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.Optional;

import static jakarta.ws.rs.core.Response.Status.*;

@Path("/api/v1/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    private static final Logger LOG = Logger.getLogger(AuthResource.class);

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response registerUser(User user) {
        Optional<Integer> userId = authService.registerUser(user);
        if (userId.isEmpty())
            return Response.status(BAD_REQUEST).entity("User already exists").build();

        return Response.status(CREATED).entity(Collections.singletonMap("id", userId.get())).build();
    }

    @POST
    @Path("/login")
    public Response login(User user) {
        Optional<String> token = authService.login(user);
        if (token.isEmpty())
            return Response.status(UNAUTHORIZED).build();

        return Response.ok(Collections.singletonMap("token", token.get())).build();
    }

    @GET
    @Path("/verify")
    public Response verifyToken(@HeaderParam("X-Authorization") String token) {
        if (!authService.validateToken(token))
            return Response.status(FORBIDDEN).build();

        String userId = authService.extractUserId(token);
        LOG.info("User with ID=" + userId + " has been successfully verified");
        return Response.ok()
                .header("X-User-ID", userId)
                .build();
    }
}
