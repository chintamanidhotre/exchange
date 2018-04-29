Description:

The project contains the test solution for implementing Exchange functioning.

Prerequisites:

Java 8 installed and set into classpath.
Maven 3.0+ set into classpath

Installing:
Import the pom.xml into ide and run "test" to get the project compiled.

Assumption:
1. Direction has been assumed to be Side of the Order (BUY/SELL).
   The solution uses Side as against Direction for better reading.
2. The test asked the order to be removed when it has been matched.
   In the solution the order is never removed physically from the order list.
   Only the status is marked as Executed for it to be prevented from further matching.


# exchange
