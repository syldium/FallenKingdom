package fr.devsylone.fallenkingdom.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.rules.RuleValue;

public class RulesFormatter
{
	private static <T> String format(Rule<?> rule, T ruleValue)
	{
		String format = "§6" + rule.getName() + " » ";

		if (ruleValue instanceof RuleValue)
			return format + ((RuleValue) ruleValue).format();
		else if(ruleValue instanceof Boolean)
			return format + ((boolean) ruleValue ? "§2✔" : "§4✘");

		return format + "§e" + ruleValue;
	}

	public static List<String> formatRules(Rule<?>... withouts)
	{
		List<Rule<?>> withoutsList = Arrays.asList(withouts);

		return FkPI.getInstance().getRulesManager().getRulesList().entrySet().stream()
				.filter(e -> !withoutsList.contains(e.getKey()))
				.map(e -> format(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

	public static List<String> formatRules()
	{
		return formatRules(new Rule[0]);
	}
}
