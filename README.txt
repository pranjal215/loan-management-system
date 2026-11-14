LOAN MANAGEMENT SYSTEM - INTELLIJ IDEA PROJECT

Requirements:
- IntelliJ IDEA
- JDK 17 or newer
- Maven (IntelliJ can import the included pom.xml)
- Internet on first Maven import so SQLite JDBC can be downloaded

OPEN:
1. Extract this project folder if it is in a ZIP.
2. In IntelliJ IDEA choose File > Open.
3. Select the LoanManagementSystem_SQLite folder (the one containing pom.xml).
4. Trust the project and wait for Maven to load dependencies.
5. Set Project SDK to JDK 17 or newer if prompted.
6. Open src/com/loansystem/Main.java and run Main.

LOGIN:
Administrator: admin / admin123
Loan Officer: officer / loan123

The SQLite file loan_management.db is created in the project working directory on first run.
Demo records are inserted only when the relevant tables are empty. Customer and loan changes persist in SQLite.

You can also run from a terminal in the project folder:
  mvn clean compile exec:java

NOTE: This is an academic demonstration application. Passwords are stored as plain text and should
be replaced with secure password hashing before any real-world deployment.
