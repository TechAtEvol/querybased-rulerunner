package se.evol.querybasedRuleRunner;

import java.util.Collection;

public record ControlResultModel(RulesModel ruleExecuted, Boolean isMatched, Collection<String> matches) {
}
