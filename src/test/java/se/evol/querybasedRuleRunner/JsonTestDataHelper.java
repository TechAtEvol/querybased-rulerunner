package se.evol.querybasedRuleRunner;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

public class JsonTestDataHelper {
    public static String getCompanyWithoutTaxRegistrationAsEmployer () throws IOException {
        String companyWithoutTaxRegistrationAsEmployer = "/arbetsgivarregistrerad-org.json";
        return fetchJsonAsString(companyWithoutTaxRegistrationAsEmployer);
    }
    public static String getRuleCompanyWithoutTaxRegistrationAsEmployer () throws IOException {
        String companyWithoutTaxRegistrationAsEmployer = "/arbetsgivarregistrerad-org_rule.json";
        return fetchJsonAsString(companyWithoutTaxRegistrationAsEmployer);
    }

    public static String getEmployerThatIsForeignOwned () throws IOException {
        String employerThatIsForeignOwned = "/utlandsk_filial.json";
        return fetchJsonAsString(employerThatIsForeignOwned);
    }

    public static String getRuleEmployerThatIsForeignOwned () throws IOException {
        String ruleEmployerThatIsForeignOwned = "/utlandsk_filial_rule.json";
        return fetchJsonAsString(ruleEmployerThatIsForeignOwned);
    }

    private static String fetchJsonAsString(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = JsonTestDataHelper.class.getResource(filename);
        return objectMapper.readTree(resource).toString();
    }
}
