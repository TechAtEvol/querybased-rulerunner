package se.evol.querybasedRuleRunner;

import com.mongodb.client.FindIterable;
import io.quarkus.logging.Log;
import org.bson.BsonDocument;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControlResultExecutor {
    private final DocumentRepo documentRepo;
    private final KycService kycService;

    public ControlResultExecutor(DocumentRepo documentRepo, KycService kycService) {
        this.documentRepo = documentRepo;
        this.kycService = kycService;
    }

    public List<ControlResultModel> runControlsByRulesPackageIdAndOrgNr(String rulesPackageId, String orgNr) throws IOException {
        // TODO: Implement the concept of rules packages, so consumers can have different groupings but still reuse rules
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
                    return new ControlResultModel(ruleModel, isNotMatched.equals(false));
                }
        ).into(new ArrayList<>());
    }
}
