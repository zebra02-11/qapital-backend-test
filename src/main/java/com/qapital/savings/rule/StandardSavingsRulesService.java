package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import com.qapital.savings.event.SavingsEventsService;
import com.qapital.savings.rule.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardSavingsRulesService implements SavingsRulesService {

    private final TransactionsService transactionsService;
    private final SavingsEventsService savingsEventsService;

    @Autowired
    public StandardSavingsRulesService(TransactionsService transactionsService, SavingsEventsService savingsEventsService) {
        this.transactionsService = transactionsService;
        this.savingsEventsService = savingsEventsService;
    }

    @Override
    public List<SavingsRule> activeRulesForUser(Long userId) {
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, userId, "Starbucks", 3.00d);
        guiltyPleasureRule.addSavingsGoal(1L);
        guiltyPleasureRule.addSavingsGoal(2L);
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, userId, 2.00d);
        roundupRule.addSavingsGoal(1L);

        return List.of(guiltyPleasureRule, roundupRule);
    }

    @Override
    public List<SavingsEvent> executeRule(SavingsRule savingsRule) {
        if (!Status.ACTIVE.equals(savingsRule.getStatus()))
            throw new RuntimeException("Rule is not ACTIVE. Provided status: " + savingsRule.getStatus());

        return transactionsService.latestTransactionsForUser(savingsRule.getUserId())
                .stream()
                .map(transaction -> executeRuleAndCreateEvent(savingsRule, transaction))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<SavingsEvent> executeRuleAndCreateEvent(SavingsRule savingsRule, Transaction transaction) {
        Double amount = 0.0d;
        switch (savingsRule.getRuleType()) {
            case ROUNDUP:
                amount = executeRoundUpRule(transaction, savingsRule.getAmount());
                break;
            case GUILTY_PLEASURE:
                amount = executeGuiltyPleasureRule(savingsRule, transaction, amount);
                break;
        }
        if (amount > 0.0d) {
            return savingsEventsService.create(savingsRule, transaction, amount);
        }
        return Collections.emptyList();
    }

    private Double executeGuiltyPleasureRule(SavingsRule savingsRule, Transaction transaction, Double amount) {
        if (transaction.getDescription().equalsIgnoreCase(savingsRule.getPlaceDescription())) {
            amount = savingsRule.getAmount();
        }
        return amount;
    }

    private Double executeRoundUpRule(final Transaction transaction, Double configuredAmount) {
        return BigDecimal.valueOf(
                        Math.ceil(Math.abs(transaction.getAmount()) / configuredAmount) * configuredAmount - Math.abs(transaction.getAmount())
                )
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }
}
