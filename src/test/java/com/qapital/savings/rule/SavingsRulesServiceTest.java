package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.StandardTransactionsService;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import com.qapital.savings.event.SavingsEventsService;
import com.qapital.savings.event.StandardSavingsEventsService;
import com.qapital.savings.rule.enums.RuleType;
import com.qapital.savings.rule.enums.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SavingsRulesServiceTest {

    private final TransactionsService transactionsService = new StandardTransactionsService();
    private final SavingsEventsService savingsEventsService = new StandardSavingsEventsService();

    private final SavingsRulesService savingsRulesService = new StandardSavingsRulesService(
            transactionsService,
            savingsEventsService
    );

    @Test
    void executeRule() {
        final List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(savingsRulesService.activeRulesForUser(1L).get(0));

        assertNotNull(savingsEvents);
        assertThat(savingsEvents.size()).isGreaterThan(0);
        assertThat(savingsEvents.size()).isEqualTo(8);
    }

    @ParameterizedTest
    @CsvSource({
            "1006, 2.00, 0.45",
            "1007, 2.00, 1.45",
            "1005, 2.00, 0.45"
    })
    void executeRoundUpRuleWithOnlySavingGoal(Long userId, Double configuredAmount, Double expected) {
        SavingsRule realSavingRule = savingsRulesService.activeRulesForUser(userId).get(0);
        SavingsRule savingsRule = new SavingsRule();
        savingsRule.setRuleType(RuleType.ROUNDUP);
        savingsRule.setStatus(Status.ACTIVE);
        savingsRule.setSavingsGoalIds(List.of(1L));
        savingsRule.setId(1000L);
        savingsRule.setUserId(userId);
        savingsRule.setAmount(configuredAmount);
        savingsRule.setPlaceDescription(realSavingRule.getPlaceDescription());
        final List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(savingsRule);

        assertNotNull(savingsEvents);
        assertThat(savingsEvents.size()).isGreaterThan(0);
        List<SavingsEvent> collect = savingsEvents.stream()
                .filter(savingsEvent -> userId.equals(savingsEvent.getUserId()) && savingsEvent.getDate().getYear() == 2015 && savingsEvent.getDate().getMonth().equals(Month.AUGUST) && savingsEvent.getDate().getDayOfMonth() == 6).collect(Collectors.toList());
        SavingsEvent event = collect.stream().findFirst().orElseThrow();
        assertEquals(expected, event.getAmount());
    }

    @ParameterizedTest
    @CsvSource({
            "1006, 2.00, 0.22",
            "1007, 2.00, 0.72",
            "1005, 2.00, 0.22"
    })
    void executeRoundUpRuleWith2SavingGoals(Long userId, Double configuredAmount, Double expected) {
        SavingsRule realSavingRule = savingsRulesService.activeRulesForUser(userId).get(0);
        SavingsRule savingsRule = new SavingsRule();
        savingsRule.setRuleType(RuleType.ROUNDUP);
        savingsRule.setStatus(Status.ACTIVE);
        savingsRule.setSavingsGoalIds(List.of(1L, 2L));
        savingsRule.setId(1000L);
        savingsRule.setUserId(userId);
        savingsRule.setAmount(configuredAmount);
        savingsRule.setPlaceDescription(realSavingRule.getPlaceDescription());
        final List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(savingsRule);

        assertNotNull(savingsEvents);
        assertThat(savingsEvents.size()).isGreaterThan(0);
        List<SavingsEvent> collect = savingsEvents.stream()
                .filter(savingsEvent -> userId.equals(savingsEvent.getUserId()) && savingsEvent.getDate().getYear() == 2015 && savingsEvent.getDate().getMonth().equals(Month.AUGUST) && savingsEvent.getDate().getDayOfMonth() == 6).collect(Collectors.toList());
        SavingsEvent event = collect.stream().findFirst().orElseThrow();
        assertEquals(expected, event.getAmount());
    }

    @ParameterizedTest
    @CsvSource({
            "2.55, 1.00, 1.00",
            "2.55, 2.00, 2.00",
            "2.55, 3.00, 3.00"
    })
    void executeGuiltyPleasureRuleWithOnlySavingGoal(Double amount, Double configuredAmount, Double expected) {
        SavingsRule savingsRule = new SavingsRule();
        savingsRule.setRuleType(RuleType.GUILTY_PLEASURE);
        savingsRule.setStatus(Status.ACTIVE);
        savingsRule.setSavingsGoalIds(List.of(1L));
        savingsRule.setId(1000L);
        savingsRule.setUserId(1002L);
        savingsRule.setAmount(configuredAmount);
        savingsRule.setPlaceDescription("Dammy transaction3");
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(savingsRule);
        SavingsEvent event = savingsEvents
                .stream()
                .findFirst()
                .orElseThrow();

        assertEquals(expected, event.getAmount());
    }

    @ParameterizedTest
    @CsvSource({
            "2.55, 1.00, 0.50",
            "2.55, 2.00, 1.00",
            "2.55, 3.00, 1.5"
    })
    void executeGuiltyPleasureRuleWith2SavingGoals(Double amount, Double configuredAmount, Double expected) {
        SavingsRule savingsRule = new SavingsRule();
        savingsRule.setRuleType(RuleType.GUILTY_PLEASURE);
        savingsRule.setStatus(Status.ACTIVE);
        savingsRule.setSavingsGoalIds(List.of(1L, 2L));
        savingsRule.setId(1000L);
        savingsRule.setUserId(1002L);
        savingsRule.setAmount(configuredAmount);
        savingsRule.setPlaceDescription("Dammy transaction3");
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(savingsRule);
        SavingsEvent event = savingsEvents
                .stream()
                .findFirst()
                .orElseThrow();

        assertEquals(expected, event.getAmount());
    }
}