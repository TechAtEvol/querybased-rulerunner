package se.evol.querybasedRuleRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: Ideally there should be restrictions on which user who can execute which rules package
@Path("/control-results")
@Produces("application/json")
public class ControlResultsAPI {

    @Inject
    ControlResultExecutor controlResultExecutor;
    private final ObjectMapper mapper = new ObjectMapper();

    @GET
    @RunOnVirtualThread
    @Path("/{id}")
    public Response getOrganisationById(@PathParam("id") String orgNr , @QueryParam("rules-package") String rulesPackage) {
        try {
            long start = System.nanoTime();
            List<ControlResultModel> result = controlResultExecutor.runControlsByRulesPackageIdAndOrgNr(rulesPackage, orgNr);
            long durationInMilliseconds = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            Log.info("Rule engine duration in milli-seconds: " + durationInMilliseconds);
            return Response.ok(mapper.writeValueAsString(result)).header("RuleEngineDurationInMilliseconds", durationInMilliseconds).build();
        } catch (IOException | URISyntaxException e) {
            return Response.ok("Do something about the IOException handling: "+e.getMessage()).status(500).build();
        }
    }

    @GET
    @RunOnVirtualThread
    @Path("/")
    public Response getOrganisations(@QueryParam("rules-package") String rulesPackage) {
        try {
            long start = System.nanoTime();
            List<ControlResultModel> result = controlResultExecutor.runControlsByRulesPackageId(rulesPackage);
            long durationInMilliseconds = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            Log.info("Rule engine duration in milli-seconds: " + durationInMilliseconds);
            return Response.ok(mapper.writeValueAsString(result)).header("RuleEngineDurationInMilliseconds", durationInMilliseconds).build();
        } catch (IOException | URISyntaxException e) {
            return Response.ok("Do something about the IOException handling: "+e.getMessage()).status(500).build();
        }
    }
}
