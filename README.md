# ON THE WAY LOGISTICS — Franchise Management Portal

A complete, high-fidelity franchise management portal featuring mandatory selfie-at-login verification, persistent front-facing camera profile selfies, logistics mission dispatching, rider order logs with payout calculation, and 15-day operational performance auditing.

---

## 🚚 Key Features & Capabilities

### 1. Security & Selfie Camera Workflow
- **Mandatory Login Selfie Verification:** Accounts lacking a verified profile selfie are gated before dashboard access. The front-facing camera opens automatically to capture, preview, retake, and confirm the selfie.
- **Dual Synchronized Avatars:** Displays letter **"F"** by default until a selfie is saved. The saved selfie immediately displays in both the **Sidebar Avatar** (left drawer) and the **Header Avatar** (top-right app bar).
- **In-App Selfie Replacement:** Tapping either avatar at any time launches the front-camera modal for seamless selfie updates.
- **Persistent Storage:** Captured selfies are stored locally in application storage (`filesDir/selfies`) and associated with the authenticated franchise identifier (`FR-000001`), persisting across app restarts and logouts.
- **Camera-Only Restrictions:** Direct device camera stream capture only, strictly disallowing gallery selection or file uploads. No artificial image resolution or file size restrictions.

### 2. Main Executive Dashboard
- **Welcome Banner:** Displays active Franchise ID (`FR-000001`), operating hub name, and city/state.
- **KPI Metrics:** Operational tracking of Locations, Active Riders, Total Orders, Active Missions, On-Time Delivery %, and Total Payout in Indian Rupee (₹).
- **Quick Action Bar:** Shortcuts to create missions, log daily orders, add hub locations, or review 15-day performance.
- **15-Day Sparkline:** Visual 15-day volume trend graph directly on the dashboard overview.
- **Live Mission Feed:** Real-time feed of in-transit, pickup, and delivered dispatches with priority tags.

### 3. Mission Management Module
- **Lifecycle Statuses:** Created, Assigned, Accepted, Pickup in Progress, Picked Up, In Transit, Out for Delivery, Delivered, Failed Delivery, Cancelled, Returned.
- **Data Attributes:** Mission ID, Consignment Shipment Reference, Pickup & Delivery addresses, Scheduled and Actual timestamps, Priority (Low, Medium, High, Urgent), Package count, Distance (km), Delivery notes, and Proof of Delivery (POD) reference.
- **Status Audit Trail:** Full timestamped history of status updates and operational transition notes.
- **Filtering & Search:** Real-time search across Mission IDs, rider names, and shipment references, with filtering by status and priority.

### 4. 15-Day Performance Auditing
- **Exact 15 Calendar Days:** Audits precisely 15 continuous dates ending on the selected evaluation date.
- **Zero-Order Handling:** Retains calendar days with 0 volume without omitting rows or altering daily averages.
- **Daily Trend Bar Chart:** Visual breakdown of Primary (₹18), Club (₹6), and Large (₹25) orders for every date.
- **Daily Audit Table:** Breakdown showing Date, Day of Week, Primary count, Club count, Large count, Total Orders, and Total Payout (₹).
- **15-Day Mission Performance (Section 13):** Tracks Missions Created, Assigned, Completed, Failed, Cancelled, Completion Rate %, On-Time Rate %, and Average Delivery Duration, plus rider-level and location-level performance summaries.

### 5. Riders & Order Management
- **Daily Orders & Payouts:** Calculates Primary amount (₹18), Club amount (₹6), Large amount (₹25), and SDD incentives with instant Indian Rupee (₹) formatting.
- **Configurable Rate Card:** Allows franchise administrators to adjust unit rates in Settings.
- **Riders Fleet Directory:** Tracks registered drivers, vehicles (Motorbike, EV Scooter, Three-Wheeler), registration plates, active duty status, and customer ratings.

### 6. Hub Locations & Franchise Profile
- **Hub Management:** Manages sorting hubs, managers, contact numbers, addresses, status (Active/Pending/Inactive), and daily parcel capacities.
- **Franchise Profile:** Maintains protected Franchise ID, company legal title, contact details, GSTIN, district, state, and PIN code.

---

## 🔑 Demonstration Credentials
- **Franchise ID:** `FR-000001`
- **Password:** `franchise123`
*(Custom registered IDs starting with `FR-` and 6+ character passwords are also supported)*

---

## 🛠️ Tech Stack & Architecture
- **Framework:** Android SDK (minSdk 24, targetSdk 36)
- **UI:** 100% Jetpack Compose with Material 3 Design
- **Camera:** Android CameraX (`camera-camera2`, `camera-lifecycle`, `camera-view`) with front-camera preference and simulated fallback for headless test environments
- **Architecture:** Clean MVVM with `LogisticsViewModel` and `LogisticsRepository`
- **State Management:** Reactive Kotlin `StateFlow` and Coroutines
- **Testing:** JUnit 4 automated local JVM unit tests
