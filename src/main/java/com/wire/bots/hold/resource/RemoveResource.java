package com.wire.bots.hold.resource;

import com.wire.bots.hold.Database;
import com.wire.bots.hold.model.InitPayload;
import com.wire.bots.sdk.server.model.ErrorMessage;
import com.wire.bots.sdk.tools.AuthValidator;
import com.wire.bots.sdk.tools.Logger;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api
@Path("/remove")
@Produces(MediaType.APPLICATION_JSON)
public class RemoveResource {
    private final Database database;
    private final AuthValidator validator;

    public RemoveResource(Database database, AuthValidator validator) {
        this.database = database;
        this.validator = validator;
    }

    @POST
    @ApiOperation(value = "Remove legal hold device")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Invalid Authorization"),
            @ApiResponse(code = 500, message = "Something went wrong"),
            @ApiResponse(code = 200, message = "Legal Hold Device was removed")})
    public Response remove(@ApiParam @Valid InitPayload init,
                           @ApiParam @NotNull @HeaderParam("Authorization") String auth) {

        try {
            if (!validator.validate(auth)) {
                Logger.warning("Invalid auth '%s'", auth);
                return Response
                        .status(401)
                        .entity(new ErrorMessage("Invalid Authorization: " + auth))
                        .build();
            }

            boolean removeAccess = database.removeAccess(init.userId);

            Logger.info("RemoveResource: user: %s, removed: %s",
                    init.userId,
                    removeAccess);

            return Response.
                    ok().
                    build();
        } catch (Exception e) {
            Logger.error("RemoveResource.remove: %s", e);
            return Response
                    .ok(e)
                    .status(500)
                    .build();
        }
    }
}