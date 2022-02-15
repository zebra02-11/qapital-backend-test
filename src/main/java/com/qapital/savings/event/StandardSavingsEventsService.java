package com.qapital.savings.event;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.savings.rule.SavingsRule;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardSavingsEventsService implements SavingsEventsService {

    @Override
    public List<SavingsEvent> create(SavingsRule savingsRule, Transaction transaction, Double amount) {

        List<Long> savingsGoalIds = savingsRule.getSavingsGoalIds();
        final double goalAmount = BigDecimal.valueOf(amount / savingsGoalIds.size()).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        return savingsGoalIds.stream()
                .map(goalId ->
                        new SavingsEvent(
                                transaction.getUserId(),
                                goalId,
                                savingsRule.getId(),
                                SavingsEvent.EventName.rule_application,
                                transaction.getDate(),
                                goalAmount,
                                savingsRule.getId(),
                                savingsRule.getRuleType())
                )
                .collect(Collectors.toList());
    }
}
