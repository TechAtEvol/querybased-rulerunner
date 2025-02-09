package se.evol.querybasedRuleRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class KycService {

    public String getInfoByOrgNr(String orgNr) throws IOException {
        String fileToUse = "";
        // String arbetsgivarRegistrerad = TemporaryTestConfigs.pathToMockJsonForArbetsgivarRegistrerad.getValueOf();
        // String utlandskFilial = TemporaryTestConfigs.pathToMockJsonForUtlandskFilial.getValueOf();
        String arbetsgivarRegistrerad = "/Users/evol/Workspaces/tutorials/java/querybased-rulerunner/src/test/resources/arbetsgivarregistrerad-org.json";
        String utlandskFilial = "/Users/evol/Workspaces/tutorials/java/querybased-rulerunner/src/test/resources/utlandsk_filial.json";
        switch (orgNr) {
            case "5020201140" -> fileToUse = utlandskFilial;
            case "5050101145" -> fileToUse = arbetsgivarRegistrerad;
            default -> throw new FileNotFoundException("No matching org nr");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonFile = objectMapper.readTree(new File(fileToUse));
        return jsonFile.toString();
    }
}
