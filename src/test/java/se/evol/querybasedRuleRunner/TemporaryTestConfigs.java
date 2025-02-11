package se.evol.querybasedRuleRunner;

public enum TemporaryTestConfigs {
    databaseCollectionRulesName("rules"),
    databaseCollectionOrganisationsName("organisations"),
    pathToMockJsonForArbetsgivarRegistrerad("src/test/resources/arbetsgivarregistrerad-org.json"),
    pathToMockJsonForUtlandskFilial("src/test/resources/utlandsk_filial.json"),
    pathToRuleForUtlandskFilial("src/test/resources/utlandsk_filial_rule.json");

    private String valueMapper;

    public String getValueOf() {
        return this.valueMapper;
    }
    TemporaryTestConfigs(String label)
    {
        this.valueMapper = label;
    }

}
