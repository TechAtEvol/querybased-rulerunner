package se.evol.querybasedRuleRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

@ApplicationScoped
public class KycService {

    // TODO: A forgiving JSON schema instead of ORM would allow the external system to add new keys without syncing releases
    public String getInfoByOrgNr(String orgNr) throws IOException {
        // TODO: Some kind of mock server and a proper REST-client that calls a KYC-mock?
        String fileToUse = "";
        String arbetsgivarRegistrerad = "/arbetsgivarregistrerad-org.json";
        String utlandskFilial = "/utlandsk_filial.json";
        switch (orgNr) {
            case "5020201140" -> fileToUse = utlandskFilial;
            case "5050101145" -> fileToUse = arbetsgivarRegistrerad;
            default -> throw new FileNotFoundException("No matching org nr");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = this.getClass().getResource(fileToUse);
        JsonNode jsonFile = objectMapper.readTree(resource);
        return jsonFile.toString();
    }
}
