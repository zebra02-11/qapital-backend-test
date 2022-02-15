package com.qapital.savings.event;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.savings.rule.SavingsRule;

import java.util.List;

public interface SavingsEventsService {

    List<SavingsEvent> create(SavingsRule savingsRule, Transaction transaction, Double amount);
}
