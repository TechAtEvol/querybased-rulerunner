package se.evol.querybasedRuleRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO:  Working with rules is admin rights only
@Path("/rules")
@Produces("application/json")
public class RulesAPI {
    @Inject
    DocumentRepo documentRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    @GET
    @RunOnVirtualThread
    @Path("")
    public Response getAllRules() {
        FindIterable<Document> rulesAsModel = documentRepo.findAllRules();
        List<String> result = rulesAsModel.map(Document::toJson).into(new ArrayList<>());
        return Response.ok(result.toString()).build();
    }

    @GET
    @RunOnVirtualThread
    @Path("/{id}")
    public Response getRuleById(@PathParam("id")String id) {
        Optional<Document> searchResult = documentRepo.findLatestRuleVersionById(id);
        if(searchResult.isEmpty()) {
            return Response.status(404).build();
        } else {
            return Response.ok(searchResult.get().toJson()).build();
        }
    }

    // TODO: Add schema validation OR rules does not change that much so you could go the ORM route
    @POST
    @RunOnVirtualThread
    public Response postRule(String jsonString) {
        String responseText = documentRepo.saveRule(jsonString);
        return Response.ok(responseText).status(201).build();
    }
}
