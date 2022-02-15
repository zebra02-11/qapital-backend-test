package com.qapital.savings.rule;

import com.qapital.savings.event.SavingsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings")
public class SavingsRulesController {

    private final SavingsRulesService savingsRulesService;

    @Autowired
    public SavingsRulesController(SavingsRulesService savingsRulesService) {
        this.savingsRulesService = savingsRulesService;
    }

    @GetMapping("/rule/active/{userId}")
    public List<SavingsRule> activeRulesForUser(@PathVariable Long userId) {
        return savingsRulesService.activeRulesForUser(userId);
    }

    @PostMapping("/rule")
    public List<SavingsEvent> executeRulesForUser(@RequestBody SavingsRule savingsRule) {
        return savingsRulesService.executeRule(savingsRule);
    }
}
