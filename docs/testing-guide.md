# ReTech — Testing & Quality Assurance Guide

## 1. Testing Philosophy & Architecture

ReTech utilizes a multi-layered testing strategy combining isolated unit tests, integration tests with an in-memory MySQL-compatible database, and role-based end-to-end verification.

```
       / \
      / E2E \       Manual Browser Testing & Role Playthroughs
     /-------\
    /  Integ  \     Spring Boot Tests with H2 (AuthService, CollectionRequestService)
   /-----------\
  /    Unit     \   Pure JUnit 5 Unit Tests (State Machine, Entities)
 /---------------\
```

### 1.1 In-Memory Test Isolation
- **Framework:** JUnit 5 (Jupiter), AssertJ, Mockito.
- **Test Database:** H2 in MySQL emulation mode (`jdbc:h2:mem:retech_test;DB_CLOSE_DELAY=-1;MODE=MySQL`).
- **Configuration:** `src/test/resources/application.properties` sets `spring.jpa.hibernate.ddl-auto=create-drop` and `spring.flyway.enabled=false`, allowing tests to execute rapidly without spinning up an external MySQL daemon or polluting development data.

---

## 2. Automated Test Suite Matrix

The project features **14 automated tests** across 5 test classes, achieving 100% pass rates:

| Test Class | Test Method | Test Category | Objective Verified |
| :--- | :--- | :---: | :--- |
| `RequestStatusTransitionTest` | `testPermittedTransitions()` | Unit Test | Validates permitted sequential lifecycle hops (`SUBMITTED` -> `APPROVED` -> `SCHEDULED` -> `COLLECTED` -> `RECEIVED` -> `PROCESSING` -> `COMPLETED`). |
| `RequestStatusTransitionTest` | `testCancellationRules()` | Unit Test | Verifies that cancellations are permitted early in the lifecycle and strictly rejected once an item has been physically collected or processed. |
| `AuthServiceTest` | `testRegisterCustomerSuccess()` | Integration | Verifies that customer registration hashes passwords with BCrypt, saves customer details, and assigns `ROLE_CUSTOMER`. |
| `AuthServiceTest` | `testDuplicateUsernameRejected()` | Integration | Asserts that duplicate usernames throw `ValidationException`. |
| `AuthServiceTest` | `testDuplicateEmailRejected()` | Integration | Asserts that duplicate emails throw `ValidationException`. |
| `CollectionRequestServiceTest`| `testCreateRequestGeneratesValidTrackingCode()` | Integration | Verifies that creating an e-waste request automatically generates a unique tracking code matching `RET-YYYY-XXXXXXXX` and starts at `REQUEST_SUBMITTED`. |
| `CollectionRequestServiceTest`| `testCustomerCanCancelPendingRequest()` | Integration | Ensures customer can cancel a `REQUEST_SUBMITTED` request, changing status to `CANCELLED` and adding an audit history entry. |
| `CollectionRequestServiceTest`| `testCustomerCannotCancelCollectedRequest()` | Integration | Verifies that attempting to cancel a `COLLECTED` item throws `InvalidStatusTransitionException`. |
| `CollectionRequestServiceTest`| `testAssignAgentChangesStatusToScheduled()` | Integration | Verifies administrator can assign a field agent, automatically transitioning the request to `PICKUP_SCHEDULED`. |
| `CollectionRequestServiceTest`| `testAgentCollectUpdatesActualWeight()` | Integration | Verifies that agent collection updates status to `COLLECTED` and records physical scale weight in kg. |
| `ProcessingRecordAndStatisticsTest`| `testCreateProcessingRecordSuccess()` | Integration | Verifies facility operator can record material recovery weights (reused, recycled, disposed) and advance status to `RECYCLING_COMPLETED`. |
| `ProcessingRecordAndStatisticsTest`| `testSystemStatisticsCalculations()` | Integration | Verifies mathematical integrity of system-wide counts and weight summations across all requests. |
| `ProcessingRecordAndStatisticsTest`| `testCustomerImpactCalculations()` | Integration | Verifies individual customer recycling impact calculations and status aggregations. |
| `RetechApplicationTests` | `contextLoads()` | Smoke Test | Validates Spring ApplicationContext loads without missing dependencies or circular bean definitions. |

---

## 3. Running the Automated Tests

### Windows (Command Prompt or PowerShell)
```batch
.\powershell.cmd -Command "mvn test"
```
Or with standard Maven:
```batch
mvn test
```

### Running a Specific Test Class
```batch
mvn test -Dtest=RequestStatusTransitionTest
```

### Expected Output
```
[INFO] Results:
[INFO] 
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 4. Manual End-to-End Testing & Role Playthrough

ReTech includes pre-seeded demo accounts allowing immediate verification of the full collection lifecycle.

### 4.1 Demo Accounts Reference
| Role | Email | Password | Display Name |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@retech.org` | `Admin@ReTech2026!` | System Administrator |
| **Customer** | `customer@retech-demo.org` | `DemoCustomer123!` | Ananya Sharma |
| **Collection Agent** | `agent@retech-demo.org` | `DemoAgent123!` | Vikram Singh |
| **Facility Operator** | `operator@retech-demo.org` | `DemoOperator123!` | Dr. Rajesh Verma |

> **Pro-Tip:** The [login.html](file:///d:/deveops_projects/retech/src/main/resources/static/login.html) page provides 1-Click Fast Demo Buttons that prefill each credential pair instantly.

---

### 4.2 Lifecycle Verification Walkthrough

#### Step 1: Citizen Registration & Pickup Submission
1. Open `http://localhost:8080/login.html` and click **"Customer Demo"** (or register a new user on `/register.html`).
2. Log in and navigate to the Customer Dashboard (`/dashboard.html`).
3. Click **"+ New Pickup Request"**.
4. Fill out the request form:
   - Item Name: `MacBook Pro 15-inch (Mid 2015)`
   - Category: `Laptops & Computers`
   - Quantity: `1`
   - Estimated Weight: `2.0 kg`
   - Condition: `Non-Working`
   - Street Address: `Flat 102, Green Glen Layout, Bellandur`
   - Preferred Date: Select any future date
   - Check the accuracy agreement checkbox.
5. Click **"Submit Collection Request"**.
6. Note the generated tracking code (e.g. `RET-2026-XXXXXXXX`).
7. Open `http://localhost:8080/tracking.html`, paste the tracking code, and observe the live visual stepper at `REQUEST_SUBMITTED`.

#### Step 2: Administrator Approval & Agent Assignment
1. Log out and log in as **System Administrator** (`admin@retech.org`).
2. Navigate to Admin Operations (`/admin.html`).
3. Under the **"Collection Requests"** tab, find the newly submitted request.
4. Click **"Approve"**. Status transitions to `REQUEST_APPROVED`.
5. Click **"Assign Agent"**, select `Vikram Singh (demo_agent)`, and pick tomorrow's date.
6. Status transitions to `PICKUP_SCHEDULED`.

#### Step 3: Collection Agent Doorstep Pickup
1. Log out and log in as **Collection Agent** (`agent@retech-demo.org`).
2. Navigate to Agent Portal (`/agent.html`).
3. Under **"Assigned Pickups"**, locate the scheduled pickup.
4. Click **"Collect & Weigh"**.
5. Enter verified scale weight: `1.95 kg`.
6. Add operational remark: `Item verified against serial number. Customer confirmed battery intact.`
7. Click **"Confirm Doorstep Collection"**.
8. Status transitions to `COLLECTED`.

#### Step 4: Recycling Facility Intake & Recovery Logging
1. Log out and log in as **Recycling Facility Operator** (`operator@retech-demo.org`).
2. Navigate to Facility Portal (`/facility.html`).
3. Under **"Incoming Collected Items"**, locate the item.
4. Click **"Confirm Intake"**. Status transitions to `RECEIVED_AT_FACILITY`.
5. Under **"Active Processing Line"**, click **"Log Processing"**.
6. Fill out the material recovery breakdown:
   - Processing Stage: `COMPLETED`
   - Input Weight: `1.95 kg`
   - Reused Weight: `0.30 kg` (salvaged screen assembly)
   - Recycled Weight: `1.50 kg` (aluminum casing, copper heat pipes)
   - Safely Disposed: `0.15 kg` (hazardous epoxy dust)
   - Outcome: `Material Recovery`
   - Check **"Mark as Recycling Completed"**.
7. Click **"Save Processing Record"**. Status transitions to `RECYCLING_COMPLETED`.

#### Step 5: Public Audit Trail Verification
1. Navigate to `http://localhost:8080/tracking.html` without logging in.
2. Enter the tracking code.
3. Observe all 7 green completed milestones in the tracking stepper, the item verification card displaying `1.95 kg verified weight`, and the chronological audit history detailing every actor from Ananya Sharma to Dr. Rajesh Verma.
