package com.qapital.savings.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/savings/rule")
public class SavingsRulesController {

    @Autowired
    private SavingsRulesService savingsRulesService;

    @RequestMapping(value = "/active/{userId}", method = RequestMethod.GET)
    public List<SavingsRule> activeRulesForUser(@PathVariable Long userId) {
        return savingsRulesService.activeRulesForUser(userId);
    }



}
