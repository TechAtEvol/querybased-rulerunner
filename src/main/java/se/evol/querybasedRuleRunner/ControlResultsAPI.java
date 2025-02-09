package se.evol.querybasedRuleRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("/control-results")
@Produces("application/json")
public class ControlResultsAPI {
    DocumentRepo documentRepo= new DocumentRepo();
    KycService kycService = new KycService();
    private final ControlResultExecutor controlResultExecutor = new ControlResultExecutor(documentRepo, kycService);

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
        } catch (IOException e) {
            return Response.ok("Do something about the IOException handling: "+e.getMessage()).status(500).build();
        }

    }
}
