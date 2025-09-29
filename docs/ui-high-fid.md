# High‑Fidelity UI (5 Pages) — Supply Chain Cargo Management
  
Date: September 29, 2025  
Branch: docs/ui-high-fid-design 
Scope: Day 2 — High‑fid designs derived from Day 1 low‑fid wireframes

## Links
- Figma (Hi‑Fi, Prototype): <https://www.figma.com/design/NDKReJv3eiStFNu02qZxSa/High-fid_Supply_management?node-id=0-1&t=zcgoS5bqS3pMZzSN-1>
- Reference (Low‑Fi): docs/ui-wireframes.md

## What’s in this Handoff
- Visual system finalized (typography, color roles, spacing, elevation)
- Page layouts locked for 5 modules
- Primary actions, empty/loading/error states, and validation patterns defined
- Component boundaries clarified for Vue implementation (Week 2)

## Design System (Applied Across Pages)
- Typography: Inter 400/500/600
- Color roles: Primary/Success/Warning/Danger + neutral grayscale
- Spacing: 8px grid; content max‑width ~1200–1280px
- Components: AppHeader, SideNavigation, PageContainer, DataTable, FormModal, StatusBadge, Toast/Alert, Tabs/Chips (where used)
- States: skeleton loading for tables, empty states with CTA, inline field errors, confirm dialogs for destructive actions

---

## Page 1 — Dashboard (Low‑Fi → Hi‑Fi)
Low‑Fi intent: Sidebar + metric cards + recent activity + quick actions.  
Hi‑Fi refinements:
- Elevated metric cards with icon + label + value for Shipments, Cargo, Routes, Vendors
- Recent Activity as a timestamped timeline (dense list style)
- Quick Actions as primary buttons (New Shipment, Add Cargo, Create Route)
- Header search with debounced input and keyboard focus ring
Flow:
1) Land → scan metrics in <5s  
2) Check activity → click through to detail  
3) Launch quick action (routes to create forms in respective modules)

---

## Page 2 — Shipment Tracking (Low‑Fi → Hi‑Fi)
Low‑Fi intent: lifecycle filters + shipment table + “New Shipment”.  
Hi‑Fi refinements:
- Status chips row (Created, Picked Up, In Transit, Delivered, Delayed) with counts
- Table columns: ID, Route, Status badge, Est. Delivery, Created, Actions (View/Edit)
- Inline status update via quick action or open detail modal
- Sticky toolbar: search, filter by status, sort by date
Flow:
1) Filter via chips → table updates  
2) Search by ID/route → refine  
3) Update status (inline/modal) → toast feedback  
4) Create new shipment → form → return to table

---

## Page 3 — Cargo Management (Low‑Fi → Hi‑Fi)
Low‑Fi intent: searchable table + “Add New Cargo” + CRUD.  
Hi‑Fi refinements:
- Toolbar: search, filter, sort, primary “Add New Cargo”
- Table columns: Cargo ID, Shipment, Type, Value, Weight, Description, Actions
- FormModal: Shipment select, Type select, Value, Weight, Description, Save/Cancel
- Validation: required fields, numeric constraints, inline errors
Flow:
1) Search/filter table  
2) Row action: view/edit/delete (confirm on delete)  
3) Add cargo → modal → submit → table refresh with toast

---

## Page 4 — Route Management (Low‑Fi → Hi‑Fi)
Low‑Fi intent: list routes + create/edit + filters.  
Hi‑Fi refinements:
- Toolbar: search by ID/name/ports, filter by status (Active/Delayed/Closed), sort by duration
- Table columns: Route ID, Name, Origin → Destination, Duration, Distance/Notes, Status, Actions
- Create/Edit Route form with normalized port fields and status selector
Flow:
1) Locate route via search/filter/sort  
2) Edit/close route (status change)  
3) Create route → submit → visible in table

---

## Page 5 — Vendor Coordination (Low‑Fi → Hi‑Fi)
Low‑Fi intent: vendor directory + assign to shipments.  
Hi‑Fi refinements:
- Two layouts supported: cards (default) or compact table (toggle)
- Card content: Name, Service Type, Contact, quick stats, actions (View/Assign/Edit/Remove)
- Toolbar: search by name/ID, filter by service, sort by rating/alphabetical
- Assign flow opens modal with shipment selector and confirmation
Flow:
1) Search/filter vendors  
2) Open vendor → assign to shipment → toast on success  
3) Add/Edit vendor details via modal; confirm on remove

---

## Implementation Notes (Vue)
Routes:
- /dashboard, /cargo, /shipments, /routes, /vendors

Components to scaffold:
- Layout: AppHeader, SideNavigation, PageContainer
- Shared: DataTable (slots for loading/empty), SearchBar, FilterDropdown, StatusBadge, ConfirmDialog, Toast
- Forms: CargoForm, ShipmentForm, RouteForm, VendorForm
- Pages: Dashboard.vue, CargoManagement.vue, ShipmentTracking.vue, RouteManagement.vue, VendorCoordination.vue

Interaction patterns:
- Primary page actions in top‑right toolbar
- Inline row actions with confirmation for destructive ops
- Modal forms to preserve table context
- Preserve table state (query params) on navigation



## Next Steps
- Generate page + shared components and wire up routing
- Connect forms/tables to backend endpoints
- Add unit tests for validation/table utils
