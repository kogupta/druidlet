/*
 * Copyright (c) 2016 Inferlytics.
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.inferlytics.druidlet.resource;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.inferlytics.druidlet.service.DruidService;
import com.inferlytics.druidlet.util.Utils;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Endpoint for queries
 *
 * @author Sriram
 * @since 4/14/2016
 */
@Path("/v2")
@Api("Druid API")
public class DruidResource {
    private static final Logger LOG = Logger.getLogger(DruidResource.class);

    /**
     * Used as a response in case of failure
     */
    private class FailureResponse {
        private String message;

        public FailureResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(@Context HttpServletRequest req, String queryJson) {
        try {
            String indexKey = String.valueOf(req.getServerPort());
            return Response.ok(Utils.JSON_MAPPER.writeValueAsString(DruidService.handleQuery(indexKey, queryJson))).build();
        } catch (JsonMappingException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new FailureResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOG.error("Exception while handling query", e);
            return Response.serverError().entity(new FailureResponse("Internal Server Error")).build();
        }
    }
}
