package se.evol.querybasedRuleRunner;

import com.mongodb.client.FindIterable;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.BsonDocument;
import org.bson.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ControlResultExecutor {
    @Inject
    DocumentRepo documentRepo;
    @Inject
    KycService kycService;

    public List<ControlResultModel> runControlsByRulesPackageIdAndOrgNr(String rulesPackageId, String orgNr) throws IOException, URISyntaxException {
        // TODO: Rules should be organised in packages, so consumers can combine. fetch and re-use rules for different processes
        String orgInfo = kycService.getInfoByOrgNr(orgNr);
        String internalId = documentRepo.saveOrganisation(orgInfo);
        FindIterable<Document> allRules = documentRepo.findAllRules();
        // TODO: A good UseCase for Reactive or Virtual Threads?
        return allRules.map(rule -> {
                    BsonDocument rlz = rule.toBsonDocument().getDocument("rule");
                    RulesModel ruleModel = new RulesModel(rlz.get("query").asString().getValue(), rlz.get("name").asString().getValue(), rlz.get("svarsText").asString().getValue());
                    FindIterable<Document> queryResult = documentRepo.runQueryForOrgNr(ruleModel, internalId);
                    List<String> result = queryResult.map(Document::toJson).into(new ArrayList<>());
                    Log.info(result);
                    Boolean isNotMatched = queryResult.into(new ArrayList<>()).isEmpty();
                    return new ControlResultModel(ruleModel, isNotMatched.equals(false), List.of(orgNr));
                }
        ).into(new ArrayList<>());
    }

    public List<ControlResultModel> runControlsByRulesPackageId(String rulesPackageId) throws IOException, URISyntaxException {
        // TODO: Rules should be organised in packages, so consumers can combine. fetch and re-use rules for different processes
        FindIterable<Document> allRules = documentRepo.findAllRules();
        // TODO: A good UseCase for Reactive or Virtual Threads?
        return allRules.map(rule -> {
                    BsonDocument rlz = rule.toBsonDocument().getDocument("rule");
                    RulesModel ruleModel = new RulesModel(rlz.get("query").asString().getValue(), rlz.get("name").asString().getValue(), rlz.get("svarsText").asString().getValue());
                    FindIterable<Document> queryResult = documentRepo.runQueryForAll(ruleModel);
                    List<String> ids = queryResult.map(doc -> ((Document)doc.get("organisation")).getString("orgNr")).into(new ArrayList<>());
                    Boolean isNotMatched = queryResult.into(new ArrayList<>()).isEmpty();
                    return new ControlResultModel(ruleModel, isNotMatched.equals(false), ids);
                }
        ).into(new ArrayList<>());
    }
}
