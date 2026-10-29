# ReTech — REST API Documentation

## 1. Overview & General Conventions

The ReTech REST API is designed according to RESTful architectural principles. It is served under the `/api` root namespace.

- **Content-Type:** `application/json;charset=UTF-8`
- **Authentication:** Standard HTTP Session Cookie (`JSESSIONID`).
- **CSRF Protection:** State-modifying requests (`POST`, `PUT`, `DELETE`) require the `X-XSRF-TOKEN` request header containing the token retrieved from the `XSRF-TOKEN` cookie.
- **Envelope Standard:** All responses follow a standardized JSON envelope:
  - **Success Response:** `ApiResponse<T>` with `{ "success": true, "message": "...", "data": { ... } }`
  - **Error Response:** `ErrorResponse` with `{ "status": 400, "error": "...", "message": "...", "path": "...", "timestamp": "...", "fieldErrors": { ... } }`

---

## 2. Authentication API (`/api/auth`)

### 2.1 Register New Customer
- **Endpoint:** `POST /api/auth/register`
- **Access:** Public
- **Request Body:**
```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "password": "Password123!",
  "fullName": "John Doe",
  "phone": "+91-9876543210",
  "address": "42 Tech Boulevard, Green Park",
  "city": "Metro City",
  "postalCode": "560001"
}
```
- **Success (201 Created):**
```json
{
  "success": true,
  "message": "Registration successful. You can now log in.",
  "data": {
    "id": 5,
    "username": "johndoe",
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "phone": "+91-9876543210",
    "address": "42 Tech Boulevard, Green Park",
    "city": "Metro City",
    "postalCode": "560001",
    "role": "ROLE_CUSTOMER"
  }
}
```

### 2.2 User Login
- **Endpoint:** `POST /api/auth/login`
- **Access:** Public
- **Request Body:**
```json
{
  "username": "johndoe",
  "password": "Password123!"
}
```
- **Success (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": 5,
    "username": "johndoe",
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "role": "ROLE_CUSTOMER"
  }
}
```

### 2.3 User Logout
- **Endpoint:** `POST /api/auth/logout`
- **Access:** Authenticated
- **Success (200 OK):**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

## 3. User & Profile API (`/api/users`)

### 3.1 Get Current User Profile
- **Endpoint:** `GET /api/users/me/profile`
- **Access:** Authenticated (`ROLE_CUSTOMER`, `ROLE_AGENT`, `ROLE_OPERATOR`, `ROLE_ADMIN`)
- **Success (200 OK):** Returns current user's profile information.

### 3.2 Update Current User Profile
- **Endpoint:** `PUT /api/users/me/profile`
- **Access:** Authenticated
- **Request Body:**
```json
{
  "fullName": "John Jonathan Doe",
  "phone": "+91-9876543219",
  "address": "New Flat 10B, Silver Oak Heights",
  "city": "Metro City",
  "postalCode": "560002"
}
```
- **Success (200 OK):** Returns updated `UserProfileDto`.

---

## 4. Collection Requests API (`/api/requests`)

### 4.1 Create Collection Request
- **Endpoint:** `POST /api/requests`
- **Access:** `ROLE_CUSTOMER`, `ROLE_ADMIN`
- **Request Body:**
```json
{
  "itemName": "Old Lenovo ThinkPad T480",
  "category": "LAPTOPS_COMPUTERS",
  "quantity": 1,
  "estimatedWeightKg": 1.75,
  "itemCondition": "PARTIALLY_WORKING",
  "pickupAddress": "New Flat 10B, Silver Oak Heights",
  "pickupCity": "Metro City",
  "pickupPostalCode": "560002",
  "preferredPickupDate": "2026-11-05",
  "notes": "Original charger included, battery functional",
  "termsAccepted": true
}
```
- **Success (201 Created):** Returns created request with unique tracking code (e.g. `RET-2026-B8C4F1A2`).

### 4.2 List Customer's Own Requests
- **Endpoint:** `GET /api/requests/my`
- **Access:** `ROLE_CUSTOMER`, `ROLE_ADMIN`
- **Success (200 OK):** Returns array of `CollectionRequestResponseDto`.

### 4.3 Public Tracking by Tracking Code
- **Endpoint:** `GET /api/requests/track/{trackingCode}`
- **Access:** Public (no authentication required)
- **Success (200 OK):**
```json
{
  "success": true,
  "message": "Tracking details retrieved",
  "data": {
    "id": 1,
    "trackingCode": "RET-2026-DEMO0001",
    "itemName": "Dell Inspiron 15 (2018)",
    "category": "LAPTOPS_COMPUTERS",
    "quantity": 1,
    "estimatedWeightKg": 2.40,
    "actualWeightKg": 2.35,
    "status": "RECYCLING_COMPLETED",
    "pickupCity": "Metro City",
    "assignedAgentName": "Vikram Singh",
    "assignedFacilityName": "EcoCycle Advanced E-Waste Recovery Centre",
    "actualPickupDate": "2026-10-17",
    "statusHistory": [
      {
        "previousStatus": null,
        "newStatus": "REQUEST_SUBMITTED",
        "actorName": "Ananya Sharma",
        "remarks": "Request submitted by user",
        "createdAt": "2026-10-15T10:00:00"
      },
      {
        "previousStatus": "REQUEST_SUBMITTED",
        "newStatus": "REQUEST_APPROVED",
        "actorName": "System Admin",
        "remarks": "Request verified and approved",
        "createdAt": "2026-10-15T14:30:00"
      },
      {
        "previousStatus": "PICKUP_SCHEDULED",
        "newStatus": "COLLECTED",
        "actorName": "Vikram Singh",
        "remarks": "Item collected safely, weighed 2.35 kg",
        "createdAt": "2026-10-17T11:20:00"
      },
      {
        "previousStatus": "UNDER_PROCESSING",
        "newStatus": "RECYCLING_COMPLETED",
        "actorName": "Dr. Rajesh Verma",
        "remarks": "Recycling completed with copper and ABS recovery",
        "createdAt": "2026-10-22T16:00:00"
      }
    ],
    "processingRecords": [
      {
        "id": 1,
        "facilityName": "EcoCycle Advanced E-Waste Recovery Centre",
        "recordedByOperatorName": "Dr. Rajesh Verma",
        "stage": "COMPLETED",
        "quantityProcessed": 1,
        "inputWeightKg": 2.35,
        "weightReusedKg": 0.30,
        "weightRecycledKg": 1.80,
        "weightDisposedKg": 0.25,
        "outcome": "MATERIAL_RECOVERY",
        "notes": "Disassembled under certified ESD standard. Lead-free solder extracted."
      }
    ]
  }
}
```

### 4.4 Cancel Eligible Request
- **Endpoint:** `POST /api/requests/{id}/cancel`
- **Access:** `ROLE_CUSTOMER` (own requests), `ROLE_ADMIN`
- **Request Body (optional):**
```json
{
  "remarks": "Moving to another location, will re-submit later"
}
```
- **Rules:** Cannot cancel if item is already `COLLECTED`, `RECEIVED_AT_FACILITY`, `UNDER_PROCESSING`, or `RECYCLING_COMPLETED`.

---

## 5. Collection Agent API (`/api/agent`)

### 5.1 View Assigned Pickup Requests
- **Endpoint:** `GET /api/agent/assignments`
- **Access:** `ROLE_AGENT`, `ROLE_ADMIN`
- **Success (200 OK):** Returns all pickup requests assigned to current logged-in agent.

### 5.2 Confirm Doorstep Collection
- **Endpoint:** `POST /api/agent/requests/{id}/collect`
- **Access:** `ROLE_AGENT`, `ROLE_ADMIN`
- **Request Body:**
```json
{
  "actualWeightKg": 2.45,
  "pickupDate": "2026-10-29",
  "remarks": "Inspected item on-site. Weighed with certified digital scale. Customer signed confirmation slip."
}
```
- **Success (200 OK):** Updates status to `COLLECTED` and appends audit history entry.

---

## 6. Recycling Facility Operator API (`/api/facility`)

### 6.1 View Facility Intake Requests
- **Endpoint:** `GET /api/facility/requests`
- **Query Params:** `facilityId` (optional, default to operator's facility)
- **Access:** `ROLE_OPERATOR`, `ROLE_ADMIN`

### 6.2 Confirm Receipt at Facility
- **Endpoint:** `POST /api/facility/requests/{id}/receive`
- **Access:** `ROLE_OPERATOR`, `ROLE_ADMIN`
- **Request Body:**
```json
{
  "facilityId": 1,
  "remarks": "Item received in sound packaging. Security barcode scanned and verified against manifests."
}
```
- **Success (200 OK):** Updates status to `RECEIVED_AT_FACILITY`.

### 6.3 Record Processing & Recycling Outcome
- **Endpoint:** `POST /api/facility/requests/{id}/processing-records`
- **Access:** `ROLE_OPERATOR`, `ROLE_ADMIN`
- **Request Body:**
```json
{
  "facilityId": 1,
  "stage": "COMPLETED",
  "quantityProcessed": 1,
  "inputWeightKg": 2.45,
  "weightReusedKg": 0.40,
  "weightRecycledKg": 1.85,
  "weightDisposedKg": 0.20,
  "outcome": "MATERIAL_RECOVERY",
  "notes": "Lithium cobalt battery isolated for specialized hydrometallurgy. PCB shredded and pyrolyzed.",
  "markAsCompleted": true
}
```
- **Success (201 Created):** Saves processing record and updates status to `RECYCLING_COMPLETED`.

---

## 7. Community Collection Centres API (`/api/centres`)

### 7.1 Search Collection Centres
- **Endpoint:** `GET /api/centres/search`
- **Access:** Public
- **Query Params:**
  - `city`: e.g., `Metro City`
  - `category`: e.g., `BATTERIES`
- **Success (200 OK):** Returns matching active collection centres.

---

## 8. Statistics API (`/api/statistics`)

### 8.1 Public Dashboard Metrics
- **Endpoint:** `GET /api/statistics/public`
- **Access:** Public
- **Success (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalRequests": 4,
    "totalItemsCollected": 4,
    "totalWeightCollectedKg": 3.25,
    "totalWeightRecycledKg": 1.80,
    "totalWeightReusedKg": 0.30,
    "activeCentresCount": 4,
    "certifiedFacilitiesCount": 3
  }
}
```

### 8.2 Customer Personal Recycling Impact
- **Endpoint:** `GET /api/statistics/customer`
- **Access:** `ROLE_CUSTOMER`, `ROLE_ADMIN`
- **Success (200 OK):** Returns customer's individual lifetime requests, collected count, weight recycled, and status breakdown.

---

## 9. Administrator API (`/api/admin`)

- `GET /api/admin/users`: List all system users with roles and contact information.
- `PUT /api/admin/users/{id}/role`: Elevate or modify user role (`ROLE_AGENT`, `ROLE_OPERATOR`, etc.).
- `GET /api/admin/requests`: List all requests with comprehensive filters.
- `PUT /api/admin/requests/{id}/status`: Auditable status override by Administrator.
- `POST /api/admin/requests/{id}/assign-agent`: Assign a collection agent and preferred collection schedule.
- `POST /api/admin/requests/{id}/assign-facility`: Route request to a certified recycling facility.
- `GET /api/admin/statistics`: System-wide operational dashboard metrics.
- `GET /api/admin/reports/export-csv`: Streams a complete UTF-8 CSV report containing all collection requests, actual weights, agents, and processing stages.
