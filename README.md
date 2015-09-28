# qapital-backend-test
Test task for backend developers

## Introduction
This is a code skeleton for a test task for backend developer candidates seeking to join Qapital.

The code skeleton consists of a functional Spring Boot application that contains logic for applying Savings Rules for a user, given some hardcoded bank transaction data and settings. The task is to implement certain missing pieces of the application, and by doing so demonstrate an understanding of the technologies involved and how to structure code in a well-thought-out way.

To complete the task, clone this repository, implement your solution and submit a link or zipped repository for review.

## Task Specification

1. Implement the method **executeRule()** in [StandardSavingsRulesService.java](src/main/java/com/qapital/savings/rule/StandardSavingsRulesService.java). The method should load the latest transactions for the user using the TransactionsService and apply the given SavingsRule to those transactions. The result is a list of SavingsEvents. If a rule is configured for more than one SavingsGoal, the saved amount should be split equally amongst the goals.
   Note that there are two different RuleTypes: *The Roundup Rule* and the *Guilty Pleasure Rule*. The implementation should handle both types. 

   1.1. The Roundup Rule: When the Roundup Rule is applied to a transaction, it rounds the amount on the transaction to the nearest multiple of the configured roundup amount and generates a SavingsEvent with the difference as the saved amount. I.e., for a transaction of $3.55 with a Roundup Rule configured to round up to the nearest $2.00, the saved amount is $0.45. For a transaction of $2.55, with an identically configured amount, the roundup would be $1.45.

   1.2. The Guilty Pleasure Rule: When the Guilty Pleasure Rule is applied to a transaction, it should check that the configured description matches the transaction's description. If so, the configured amount is saved.
   
2. Create a method in [SavingsRulesController.java](src/main/java/com/qapital/savings/rule/SavingsRulesController.java) that takes a SavingsRule object as a JSON body, calles the executeRule() method implemented above and returns the list of SavingsEvents as a JSON body.

You are free to add any support classes you feel are necessary or would make the solution better, as well as to refactor existing supporting code as long as the above requirements are met.



