# ReTech — E-Waste Collection and Recycling Tracker

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build Status](https://img.shields.io/badge/Build-Passing%20(14%2F14%20Tests)-success.svg)](docs/testing-guide.md)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20Flyway-blue.svg)](docs/database-schema.md)
[![Architecture](https://img.shields.io/badge/Architecture-Same--Origin%20Monolith-purple.svg)](docs/architecture.md)

**ReTech** is a complete, beginner-friendly, production-ready semester case-study web application built to tackle the electronic waste crisis. It establishes a transparent, auditable digital chain-of-custody that connects **Citizens (Customers)**, **Field Collection Agents**, **Certified Recycling Facility Operators**, and **System Administrators**.

Every item submitted is tracked via an automatically generated unique tracking identifier (e.g. `RET-2026-A1B2C3D4`) with verifiable weight verification, chronological audit logs, and realistic material recovery metrics.

---

## 🌟 Key Features

- **End-to-End Finite State Machine:** Enforces strict sequential transitions:  
  `REQUEST_SUBMITTED` ➔ `REQUEST_APPROVED` ➔ `PICKUP_SCHEDULED` ➔ `COLLECTED` ➔ `RECEIVED_AT_FACILITY` ➔ `UNDER_PROCESSING` ➔ `RECYCLING_COMPLETED` (plus `CANCELLED` and `REQUEST_REJECTED`).
- **Verifiable Recycling Metrics:** Strictly separates unverified citizen weight estimates from certified on-site scale weights and facility material breakdown logs (reused parts, recycled metals/plastics, hazardous disposal).
- **Same-Origin Monolithic Architecture:** Serves responsive modern HTML5/CSS3/Vanilla JS ES6 directly from Spring Boot's static resources. **No Node.js, Webpack, npm, React, or complex CORS setup required.**
- **Enterprise Security:**
  - Password encryption using **BCrypt** (10 rounds).
  - Role-Based Access Control (RBAC) across 4 roles: `ROLE_CUSTOMER`, `ROLE_AGENT`, `ROLE_OPERATOR`, `ROLE_ADMIN`.
  - Cookie-based Cross-Site Request Forgery (CSRF) protection with automated header sync (`X-XSRF-TOKEN`).
  - No public registration for administrative roles; initial admin account is bootstrapped securely.
- **Database Migrations:** Schema managed via **Flyway** (`V1__init_schema.sql` and `V2__seed_demo_data.sql`).
- **Automated CI/CD:** Complete declarative `Jenkinsfile` and 14 automated JUnit 5 tests running against an isolated in-memory test database.

---

## 🧱 Technology Stack

| Layer | Technology | Details |
| :--- | :--- | :--- |
| **Backend** | Java 21 LTS | Eclipse Temurin / Oracle OpenJDK 21 |
| **Framework** | Spring Boot 4.1.2 | Spring Web (WebMVC), Spring Data JPA, Spring Validation, Spring Security |
| **Security** | Spring Security 7.x | BCrypt password hashing, Session management, Cookie CSRF protection |
| **Database** | MySQL 8.0+ | Normalized 3NF relational schema with foreign keys and B-Tree indexes |
| **Migration** | Flyway Core & MySQL | Versioned SQL migration scripts |
| **Testing** | JUnit 5, AssertJ, Mockito, H2 | In-memory MySQL emulation for automated test isolation |
| **Frontend** | HTML5, CSS3, Vanilla JS ES6 | Forest-Green / Mint theme, CSS Grid & Flexbox, Fetch API |
| **Build & CI** | Apache Maven 3.9+, Jenkins | Declarative multi-stage CI/CD pipeline |

---

## 🚀 Quick Start Guide (Windows 10/11)

### 1. Prerequisites
Ensure you have the following installed on your system:
- **Java 21 JDK**: Check by running `java -version`.
- **MySQL Server 8.0+** & MySQL Workbench.
- **Git**: Check by running `git --version`.

---

### 2. Configure the MySQL Database
Open **MySQL Workbench** (or MySQL CLI) and run:

```sql
CREATE DATABASE IF NOT EXISTS retech_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

> **Note:** You do not need to manually create tables! When the application launches, **Flyway** will automatically run `V1__init_schema.sql` (creating all 6 normalized tables) and `V2__seed_demo_data.sql` (seeding sample collection centres and recycling facilities).

---

### 3. Configure Database Credentials (Optional)
The application reads database configuration from environment variables or defaults to standard MySQL local settings:

| Environment Variable | Default Value | Description |
| :--- | :--- | :--- |
| `DB_URL` | `jdbc:mysql://localhost:3306/retech_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` | JDBC connection string |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `ADMIN_BOOTSTRAP_EMAIL` | `admin@retech.org` | Initial administrator email |
| `ADMIN_BOOTSTRAP_PASSWORD` | `Admin@ReTech2026!` | Initial administrator password |

If your MySQL password is not `root`, simply set it in PowerShell before running:
```powershell
$env:DB_PASSWORD="your_mysql_password"
```
Or edit `src/main/resources/application.properties`.

---

### 4. Build and Run the Application

#### Option A: Run using the workspace helper
```batch
.\powershell.cmd -Command "mvn spring-boot:run"
```

#### Option B: Run using standard Maven
```batch
mvn clean compile
mvn spring-boot:run
```

#### Option C: Run the packaged JAR directly
```batch
mvn package -DskipTests=true
java -jar target/retech-0.0.1-SNAPSHOT.jar
```

Once started, the console will display:
```
Tomcat started on port 8085 (http) with context path '/'
Started RetechApplication in 3.5 seconds
```

Open your browser and navigate to:  
👉 **`http://localhost:8085`**

---

## 👥 Demonstration User Accounts

The application automatically seeds demonstration accounts upon first run. For convenience, the [Login Page](http://localhost:8085/login.html) includes **1-Click Demo Buttons** that instantly log you in under any role:

| Role | Username / Email | Password | Intended Use / Features |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@retech.org` | `Admin@ReTech2026!` | Manage users, approve requests, assign field agents, assign facilities, system stats, export CSV report |
| **Customer** | `customer@retech-demo.org` | `DemoCustomer123!` | Submit pickup requests, view personal requests, live tracking, cancel pending requests, personal recycling impact |
| **Collection Agent** | `agent@retech-demo.org` | `DemoAgent123!` | View assigned doorstep pickups, inspect items on-site, record certified scale weight, confirm pickup |
| **Facility Operator** | `operator@retech-demo.org` | `DemoOperator123!` | Receive collected items, record material recovery (reused, recycled, disposed), complete processing |

---

## 🧪 Running Automated Tests

ReTech includes **14 comprehensive unit and integration tests** verifying security, lifecycle state machines, calculations, and repository persistence. All tests run in an isolated in-memory H2 database (MySQL compatibility mode) with zero external dependencies:

```batch
.\powershell.cmd -Command "mvn test"
```

Expected output:
```
[INFO] Results:
[INFO] 
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

Detailed test documentation is available in [docs/testing-guide.md](docs/testing-guide.md).

---

## 🗺️ System Walkthrough & Pages

The application is structured around responsive, accessible web portals served from `src/main/resources/static`:

1. **Public Landing Page (`/index.html`):** Live aggregated community impact metrics, accepted e-waste category guide, 4-step workflow overview, and instant tracking search bar.
2. **Citizen Registration & Login (`/register.html`, `/login.html`):** Form validation, password rules, and 1-click fast demo credentials.
3. **Customer Dashboard (`/dashboard.html`):** Personal recycling impact card, recent request statuses, and "+ New Pickup Request" modal with condition selector.
4. **My Requests (`/my-requests.html`):** Request management tabbed by status (`All`, `Pending`, `Scheduled`, `Completed`, `Cancelled`), view request details, and cancel pending requests.
5. **Public Tracking Portal (`/tracking.html`):** Visual 7-step progress bar, item verification card (displaying certified scale weight), and chronological audit history timeline.
6. **Collection Centres Directory (`/centres.html`):** Searchable community drop-off depot directory with dynamic city and category filters.
7. **Collection Agent Portal (`/agent.html`):** Field logistics interface displaying assigned pickups, customer address coordinates, and scale weight entry modal.
8. **Recycling Facility Portal (`/facility.html`):** Industrial processing interface for confirming physical intake and logging material recovery quantities (reused, recycled, hazardous disposal).
9. **Admin Operations Center (`/admin.html`):** Central command dashboard to manage users, approve/reject requests, assign agents, route items to facilities, view system metrics, and export CSV reports.

---

## 📂 Project Directory Structure

```
d:\deveops_projects\retech
├── .gitignore                          # Git ignore configuration
├── Jenkinsfile                         # Declarative CI/CD pipeline
├── pom.xml                             # Maven project configuration (Java 21, Spring Boot 4)
├── README.md                           # Master project guide
│
├── docs/                               # Semester Documentation Suite
│   ├── architecture.md                 # System architecture & Mermaid sequence diagrams
│   ├── database-schema.md              # 3NF Schema design, data dictionary & ER diagram
│   ├── api-documentation.md            # REST API endpoints & payload specifications
│   ├── testing-guide.md                # QA strategy, test matrix & manual walkthrough
│   └── case-study-report.md            # Academic B.Tech semester case study report
│
├── src/main/java/com/retech/
│   ├── RetechApplication.java         # Spring Boot entry point
│   ├── controller/                     # REST Controllers (/api/**)
│   ├── dto/                            # Strongly-typed Data Transfer Objects
│   ├── entity/                         # JPA Entities (User, Request, Facility, etc.)
│   │   └── enums/                      # Enums (Role, Status, Category, Condition, Stage)
│   ├── exception/                      # Global exception handling & custom exceptions
│   ├── repository/                     # Spring Data JPA repositories
│   ├── security/                       # Spring Security 7, BCrypt, UserDetailsService
│   └── service/                        # Domain business logic & Bootstrap seeding
│
├── src/main/resources/
│   ├── application.properties          # MySQL datasource and application settings
│   ├── db/migration/                   # Flyway Migration Scripts
│   │   ├── V1__init_schema.sql         # 6 Normalized Relational Tables & Indexes
│   │   └── V2__seed_demo_data.sql      # Seeded Centres and Facilities
│   └── static/                         # Frontend Web Presentation Tier
│       ├── index.html                  # Public home & live stats
│       ├── login.html                  # Login with 1-click demo switcher
│       ├── register.html               # Customer registration
│       ├── dashboard.html              # Customer dashboard & request creation
│       ├── my-requests.html            # Customer request list & cancellation
│       ├── tracking.html               # Interactive visual tracking timeline
│       ├── centres.html                # Searchable drop-off depots
│       ├── agent.html                  # Field collection agent portal
│       ├── facility.html               # Recycling facility operator portal
│       ├── admin.html                  # System administrator operations center
│       ├── 403.html / 404.html         # Error pages
│       ├── css/styles.css              # Responsive forest-green/mint UI theme
│       └── js/app.js                   # Client utility library & CSRF handler
│
└── src/test/java/com/retech/
    ├── RetechApplicationTests.java    # Spring Boot Context smoke test
    ├── RequestStatusTransitionTest.java# Finite state machine unit tests
    ├── AuthServiceTest.java            # Registration, BCrypt & uniqueness tests
    ├── CollectionRequestServiceTest.java # Lifecycle, cancellation & agent assignment tests
    └── ProcessingRecordAndStatisticsTest.java # Facility recovery & metric tests
```

---

## ⚙️ CI/CD with Jenkins

A production-ready declarative `Jenkinsfile` is included in the project root. It coordinates:
1. **Source Code Checkout** from Git.
2. **Environment Verification** (JDK 21 and Maven versions on Windows/Linux agents).
3. **Compilation & Syntax Validation** (`mvn clean compile`).
4. **Automated Testing & Quality Gate** (`mvn test` with JUnit XML report publishing).
5. **Packaging** (`mvn package -DskipTests=true` into an executable fat JAR).
6. **Artifact Archiving** (Stores `target/*.jar` with SHA-256 fingerprinting).

To run in Jenkins, create a new **Pipeline** job and select **"Pipeline script from SCM"** pointing to your Git repository.

---

## 🛠️ Troubleshooting Common Issues

### 1. Port 8080 is Already in Use
If another service is using port 8080, run the application on a different port:
```batch
java -jar target/retech-0.0.1-SNAPSHOT.jar --server.port=8081
```
Then navigate to `http://localhost:8081`.

### 2. Access Denied for MySQL User 'root'
Ensure your MySQL Server service is running in Windows Services (`services.msc`), and verify your password:
```powershell
$env:DB_PASSWORD="your_password"
.\powershell.cmd -Command "mvn spring-boot:run"
```

### 3. Java Version Incompatibility
ReTech requires **JDK 21**. Verify your runtime:
```batch
java -version
```
If your system points to an older Java version, ensure `JAVA_HOME` points to your JDK 21 directory.

---

## 📄 License & Academic Integrity
Developed as an academic semester case-study project for B.Tech Computer Science & Engineering. Designed following sustainable software engineering principles, clean code standards, and the UN Sustainable Development Goals (SDG 12: Responsible Consumption and Production).
