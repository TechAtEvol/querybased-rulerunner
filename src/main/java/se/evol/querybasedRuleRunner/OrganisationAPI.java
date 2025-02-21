package se.evol.querybasedRuleRunner;

import com.fasterxml.jackson.databind.util.JSONPObject;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.bson.json.JsonObject;

import java.util.Collection;
import java.util.Optional;


// TODO: This is for test purpose only, and it should have locked down access rights in prod (or be removed)
@Path("/organisations")
@Produces("application/json")
public class OrganisationAPI {
    @Inject
    DocumentRepo documentRepo;

    @GET
    @RunOnVirtualThread
    @Path("/{id}")
    public Response getOrganisationById(@PathParam("id")String id) {
        Optional<Document> searchResult = documentRepo.findLatestOrgById(id);
        if(searchResult.isEmpty()) {
            return Response.status(404).build();
        } else {
            return Response.ok(searchResult.get().toJson()).build();
        }
    }

    @GET
    @RunOnVirtualThread
    @Path("/")
    public Response getAll() {
        Collection<String> responseText = documentRepo.getAllOrganisations();
        return Response.ok(new JsonObject("{"+responseText.toString())+"}").status(201).build();
    }

    @POST
    @RunOnVirtualThread
    public Response postOrganisation(String jsonString) {
        String responseText = documentRepo.saveOrganisation(jsonString);
        return Response.ok(responseText).status(201).build();
    }
}
