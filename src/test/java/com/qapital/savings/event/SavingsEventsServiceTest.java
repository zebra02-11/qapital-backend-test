package com.qapital.savings.event;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.savings.rule.SavingsRule;
import com.qapital.savings.rule.enums.RuleType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SavingsEventsServiceTest {

    private final SavingsEventsService service = new StandardSavingsEventsService();

    @Test
    void create() {
        Transaction transaction = new Transaction(1L, 1L, 5.00, "", LocalDate.now());
        SavingsRule savingsRule = new SavingsRule();
        savingsRule.setId(1L);
        savingsRule.setUserId(1L);
        savingsRule.setSavingsGoalIds(List.of(1L, 2L, 3L));
        savingsRule.setRuleType(RuleType.ROUNDUP);

        final List<SavingsEvent> savingsEvents = service.create(savingsRule, transaction, 10.00);

        assertNotNull(savingsEvents);
        assertEquals(savingsEvents.size(), 3);
        assertEquals(savingsEvents.get(0).getAmount(), 3.33);
    }
}
