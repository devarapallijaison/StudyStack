# StudyStack

**Learn. Practice. Level Up.**

StudyStack is a Java web application for programming missions, quizzes, XP progression, and leaderboards. It follows the assignment's required stack: Java 17, Jakarta Servlets/JSP, JDBC, MySQL, and Tomcat 10.1. Spring, JPA/Hibernate, JSTL, and JSP EL are not part of this project.

## Architecture

The request flow is:

```text
Browser → Servlet controller → Service/business rules → DAO (JDBC) → MySQL
Browser ← JSP view ← Servlet request attributes ← DAO/service results
```

Controllers handle HTTP requests and choose a view or redirect. Services enforce application rules, DAOs own parameterized SQL, models carry data, filters enforce session/role access, and utility classes hold shared infrastructure such as database access and password hashing. JSPs render data; they do not query the database. Static CSS and JavaScript live under `src/main/webapp/assets`.

## Project setup (Phase 1)

### Prerequisites

- JDK 17 (set `JAVA_HOME` to the JDK, not a JRE)
- Eclipse IDE for Enterprise Java and Web Developers
- Apache Tomcat 10.1
- MySQL Server 8.0.16+ and a MySQL client
- Maven 3.8+ (or Eclipse's embedded Maven)

### Import into Eclipse

1. In Eclipse, choose **File → Import → Maven → Existing Maven Projects** and select this folder.
2. Confirm the project uses JDK 17: **Project → Properties → Java Compiler**.
3. Add Tomcat 10.1 in **Window → Show View → Servers**, then choose **New → Server**.
4. In **Project → Properties → Targeted Runtimes**, select Tomcat 10.1.
5. Run **Maven → Update Project**, then add the project to the Tomcat server and start it.
6. After completing the MySQL setup below and configuring the database environment, visit `http://localhost:8080/StudyStack/` for the landing page.

The Maven WAR layout is also a standard Eclipse Dynamic Web Project layout. `src/main/java` contains Java sources and `src/main/webapp` is the web root. Maven produces `target/StudyStack.war`, which Tomcat can deploy. The Servlet API is marked `provided` because Tomcat supplies it at runtime.

### MySQL database setup

Open a MySQL client from the project directory and run the schema and sample mission script:

```text
mysql -u root -p
```

At the `mysql>` prompt, run:

```sql
SOURCE database/studystack.sql;
```

Create a separate application account with only the permissions needed by the application (replace the example secret locally):

```sql
CREATE USER 'studystack_app'@'localhost' IDENTIFIED BY 'replace-with-a-local-secret';
GRANT SELECT, INSERT, UPDATE, DELETE ON studystack_db.* TO 'studystack_app'@'localhost';
```

Configure the application process before starting Tomcat. For the current PowerShell terminal:

```powershell
$env:STUDYSTACK_DB_USERNAME = 'studystack_app'
$env:STUDYSTACK_DB_PASSWORD = 'your-local-secret'
# Optional; this is the default URL:
$env:STUDYSTACK_DB_URL = 'jdbc:mysql://localhost:3306/studystack_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC'
```

`DBConnection` reads these environment variables (or same-named JVM system properties); it contains no credentials. The Maven dependency supplies MySQL Connector/J 9.7.0, which supports MySQL Server 8.0+ and Java 8+.

### Provision the initial administrator

After the schema and database environment are configured, build the project and invoke the one-time terminal setup utility. It refuses to run once an admin row exists and reads the password without echoing it:

```powershell
mvn clean package
mvn dependency:build-classpath "-Dmdep.outputFile=target/dependency-classpath.txt"
$appClasspath = "target/classes;" + (Get-Content -Raw target/dependency-classpath.txt).Trim()
java -cp $appClasspath com.studystack.util.AdminSetup
```

Run this from a real terminal so hidden password input is available. No admin password or default login is stored in the project.

### Build and deploy

From a terminal with JDK 17 and Maven configured:

```text
mvn clean package
```

Deploy `target/StudyStack.war` to Tomcat 10.1, or run the project through Eclipse's configured Tomcat server. Tomcat 10.1 implements Jakarta Servlet 6.0, matching the `jakarta.servlet.*` namespace used by this application.

## Current project map

```text
src/main/java/com/studystack/  controller, service, dao, model, filter, util packages
src/main/webapp/               JSP/web root and static assets
src/main/webapp/WEB-INF/       deployment descriptor (not directly web-accessible)
database/                      MySQL schema and sample missions
```

## Database and backend foundation

`database/studystack.sql` creates the users, admins, missions, and mission_submissions tables and inserts nine repeatable sample missions. Each student can submit a mission once, enforced by `UNIQUE(user_id, mission_id)`. Deleting a mission in the application will archive it with `is_active = FALSE`, preserving historical submission references. Editing is allowed only before the first attempt; once a mission has submissions, its question and answer key are frozen. User level is derived consistently as `1 + floor(total XP / 100)`; each level spans 100 XP.

DAO SQL uses `PreparedStatement`. `MissionDAO.findActiveForStudent` deliberately omits the answer key, while the transactional submission method reads the key server-side. Correct attempts, submission records, and XP awards commit together or roll back together.

## Authentication

Student signup, student login, separate admin login, logout, PBKDF2-HMAC-SHA256 password hashing, CSRF tokens, session renewal at login, and student/admin route filters are implemented. Passwords are stored only as salted hashes. The initial administrator is intentionally provisioned separately; no default credentials are committed.

## Progress

- Phase 1: Maven WAR scaffold, deployment descriptor, and initial deployable endpoint.
- Phase 2: MySQL schema, sample missions, models, environment-configured JDBC connection, and initial DAO CRUD/query methods.
- Phase 3: Student/admin authentication, password hashing, CSRF checks, and role filters.
- Phase 4: Student dashboard, filtered missions, one-attempt submissions, XP transactions, profile, and leaderboard.
- Phase 5: Admin statistics and mission creation/editing/archiving; attempted questions remain frozen.
- Phase 6: Responsive landing and application views, shared styling, persistent dark/light theme.
- Remaining: full Tomcat/MySQL integration and workflow verification on a machine with the required services installed.

The servlets use `RequestDispatcher.forward()` to render JSPs and `sendRedirect()` after successful form submissions. `HttpSession` stores only the user/admin ID, username, role, and CSRF token. The container issues the HttpOnly session cookie; logout invalidates the session. `ServletContext.log()` records server-side failures without returning SQL errors or stack traces to users. All dynamic JSP content is escaped by `HtmlUtil`; JSPs do not use EL or JSTL.

For the assignment concept walkthrough: `doGet()` renders dashboard/list/detail views; `doPost()` handles signup, login, answer submission, mission save/archive, and logout. `DashboardServlet.init()` reads its recommendation limit through `ServletConfig`. `web.xml` sets cookie-only session tracking, HttpOnly session cookies, and a 30-minute timeout. DAO classes provide create/read/update/archive and aggregate queries using JDBC.

## Demonstration checklist

1. Register a student, then sign in from the student login page.
2. Open the dashboard and compare XP, level, completed missions, and available missions with the database.
3. Filter missions by category, difficulty, and status.
4. Submit a correct answer and confirm the mission submission and XP are recorded together. Try that mission again and confirm it cannot be attempted twice.
5. Submit an incorrect answer on another mission and confirm it records zero XP.
6. Compare the leaderboard and profile history to the student's submissions.
7. Log out and try reopening a student route; the authentication filter should redirect to login.
8. Sign in as the provisioned administrator, create a mission, edit an unattempted mission, and archive a mission. Confirm an attempted mission is frozen and history remains visible.
9. While signed in as a student, open an admin route; it should redirect to admin sign-in.
10. Toggle dark/light mode, visit another page, and refresh to confirm the theme is restored.

### Verification status in this workspace

The non-servlet Java foundation compiles with `javac --release 17`; the password hash/verify smoke check passes; `pom.xml` and `web.xml` parse as XML; and the JSP source contains no EL expressions. Full Maven/WAR compilation, servlet/JSP container compilation, and MySQL workflow checks still need to be run after installing Maven, JDK 17, Tomcat 10.1, and MySQL 8.0.16+.

The administrator table is intentionally not seeded with a default account or password. `AdminSetup` provides secure one-time provisioning from a terminal.
