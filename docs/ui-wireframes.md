# UI Wireframes and Prototype — Supply Chain Cargo Management

Date: September 27, 2025  
Branch: docs/ui-wireframes-cargo  
Task: Day 1 — Low‑fidelity wireframe + prototype reference

## Overview
This document outlines the low‑fidelity Figma wireframes (5 pages) and the aligned interactive prototype built in Lovable.dev. The goal is to validate navigation, page layout, and CRUD entry points that will be implemented in Week 2.

## Artifacts
- Figma wireframe (5 pages): <https://www.figma.com/design/m0Shws5DyYek2bnVTek0um/Supply_chain_management?node-id=0-1&t=IMFVRfnimsdZeTdK-1>
- Lovable interactive prototype: <https://flow-cargo-dash.lovable.app/>

---

## Global Layout and Navigation
- Persistent header: app title, search, user/profile actions
- Left sidebar: Dashboard, Cargo, Shipments, Routes, Vendors
- Main content: page‑specific tables, cards, and forms
- Primary actions: top‑right buttons (e.g., “New Shipment”, “Add Cargo”, “Create Route”)

Navigation model:
Dashboard → Cargo Management → Shipment Tracking → Route Management → Vendor Coordination

Rationale:
- Matches operator workflow from overview to specific actions
- Minimizes clicks to reach CRUD operations
- Keeps page patterns consistent for faster user adoption

---

## Page 1 — Dashboard

Purpose:
- Give operators a quick system overview and one‑click entry to core tasks.

Layout:
- Top stats row: Total Shipments, Active Cargo, Vendors, Routes
- Main panel: Recent Activity / Logs (reverse chronological)
- Right panel (or compact area): Quick Actions (New Shipment, Add Cargo, Create Route)

Flow of actions:
1) Land on Dashboard → glance at metrics  
2) Scan Recent Activity for exceptions → click through to detail (shipment/cargo)  
3) Use Quick Actions to create common records without leaving the page

Success criteria:
- Overview in under 5 seconds
- One click to start a common task
- Recent events visible without search

---

## Page 2 — Cargo Management

Purpose:
- Primary CRUD surface for cargo inventory.

Layout:
- Toolbar: search, filter, sort, “Add New Cargo”
- Inventory table: ID, linked Shipment, Type, Value, Weight, Description, Actions
- Row actions: View, Edit, Delete
- Creation flow: opens modal form

Flow of actions:
1) Search or filter cargo → table updates  
2) Click a row to view/edit details → save or cancel  
3) Click “Add New Cargo” → modal form → select shipment → submit → table refresh

Validation points:
- Required fields validated in the form
- Prevent destructive actions with a confirm dialog
- Maintain table state (search/filter) after create/update/delete

---

## Page 3 — Shipment Tracking

Purpose:
- Track shipment lifecycle and manage status transitions.

Layout:
- Status filter cards: Created, Picked Up, In Transit, Delivered, Delayed
- Toolbar: search by ID/route, filter by status, sort by date
- Shipments table: ID, Route, Status badge, Est. Delivery, Created, Actions
- Primary action: “New Shipment” (top‑right)

Flow of actions:
1) Filter by lifecycle using cards → table narrows  
2) Search by route or ID → refine results  
3) Select shipment → update status (inline or modal) → reflect immediately  
4) Create new shipment → form → submit → appears in table

Future (Week 3):
- Hook status updates to Kafka events to reflect real‑time state changes

---

## Page 4 — Route Management

Purpose:
- Define and manage transport routes used by shipments.

Layout:
- Toolbar: search by ID/name/ports, filter by status, sort by duration
- Routes table: Route ID, Route Name, Origin → Destination, Duration, Distance/Notes, Status, Actions
- Primary action: “Create Route”

Flow of actions:
1) Search/filter to locate a route  
2) Create route → form (origin port, destination port, duration, status) → submit  
3) Edit or retire route → update status (Active/Delayed/Closed)

Guidelines:
- Keep origin/destination naming consistent with the database
- Keep status options limited and clear to avoid ambiguity

---

## Page 5 — Vendor Coordination

Purpose:
- Manage logistics vendors and assignments to shipments.

Layout:
- Toolbar: search by name/ID, filter by service type, sort by rating/alphabetical
- Directory: vendor cards/table listing name, service type, contact, quick stats
- Actions: View details, Assign to Shipment, Edit, Remove
- Primary action: “Add Vendor”

Flow of actions:
1) Search/filter vendors by service type  
2) Open vendor → assign to shipment → confirm  
3) Add or update vendor details via modal form

Consistency:
- Use the same action patterns (top‑right primary button, row/card actions) as other modules

---

## Prototype Alignment (Lovable)
The interactive prototype mirrors the five modules, navigation, and CRUD entry points above. It serves as a living reference for:
- Route structure (one route per module page)
- Component composition (layout shell + feature components)
- Form/table patterns and placement of primary actions

---

## Frontend Implementation Notes

Framework and routing:
- Vue 3 (Composition API), Vue Router (one route per module)

Planned components:
- Layout: AppHeader, SideNavigation, PageContainer
- Dashboard: StatisticsCards, RecentActivity, QuickActions
- Shared: DataTable, SearchBar, FilterDropdown, StatusBadge, ConfirmDialog
- Feature pages: Dashboard.vue, CargoManagement.vue, ShipmentTracking.vue, RouteManagement.vue, VendorCoordination.vue
- Feature forms: CargoForm.vue, ShipmentForm.vue, RouteForm.vue, VendorForm.vue

Interaction patterns:
- Primary actions in the top‑right of each page
- Row actions inline with confirmation for destructive ops
- Modal forms for create/edit to preserve table context

---


