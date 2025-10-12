# Route Management - Testing Checklist

## 🚀 Before Testing

### Prerequisites:
- [ ] Backend server running on `localhost:8080`
  ```bash
  cd server
  mvn spring-boot:run
  ```
- [ ] Frontend dev server running on `localhost:5173`
  ```bash
  cd client
  bun run dev
  ```
- [ ] Database connected and accessible
- [ ] No compilation errors

---

## 📋 Test Cases

### 1. Route List Page (`/routes`)

#### Navigation:
- [ ] Click "Route Management" in sidebar → navigates to `/routes`
- [ ] Page loads without errors
- [ ] URL shows `/routes`

#### Stats Cards:
- [ ] Total Routes displays correct count
- [ ] Average Duration calculated and displayed
- [ ] Average Cost calculated and displayed
- [ ] Total Distance calculated and displayed
- [ ] Icons render correctly (Route, Clock, DollarSign, MapPin)
- [ ] Colors match design (blue, green, yellow, purple)

#### Route Map:
- [ ] Map component renders without errors
- [ ] Routes displayed as lines on map
- [ ] Map is interactive (can zoom/pan)
- [ ] Map shows correct height (400px)

#### Routes Table:
- [ ] All routes from backend displayed
- [ ] Columns: ID, Origin Port, Destination Port, Distance, Duration, Mode, Cost, Status, Actions
- [ ] Data formatted correctly (numbers with commas, currency with $)
- [ ] Transport Mode badges have correct colors:
  - OCEAN/SEA → blue (default)
  - AIR → gray (secondary)
  - ROAD/LAND → outline
  - RAIL → red (destructive)
- [ ] Status badges have correct colors:
  - Active → blue (default)
  - Delayed → red (destructive)
  - Closed → gray (secondary)
- [ ] Action buttons render: Eye (view), Edit (pencil), Trash (delete)

#### Loading State:
- [ ] Shows spinner and "Loading routes..." message
- [ ] Content hidden while loading
- [ ] Loading ends when data arrives

#### Error State:
- [ ] Shows error icon and message if load fails
- [ ] "Try Again" button appears
- [ ] Clicking "Try Again" retries the load
- [ ] No routes table shown during error

#### Empty State:
- [ ] Shows message if no routes exist
- [ ] Message: "No routes found. Create your first route to get started."

---

### 2. Create Route Page (`/routes/create`)

#### Navigation:
- [ ] Click "+ Add Route" button on list page
- [ ] Navigates to `/routes/create`
- [ ] URL changes correctly
- [ ] "Back to Routes" button appears

#### Page Header:
- [ ] Title: "Create New Route"
- [ ] Description: "Set up a new shipping route"
- [ ] Back button navigates to `/routes`

#### Form Fields:
- [ ] Origin Port input visible and functional
- [ ] Destination Port input visible and functional
- [ ] Duration input (number type, min 1)
- [ ] Distance input (number type, min 0)
- [ ] Transport Mode dropdown with options:
  - 🚢 Ocean Transport
  - ✈️ Air Transport
  - 🚛 Road Transport
  - 🚂 Rail Transport
- [ ] Status dropdown with options:
  - Active
  - Delayed
  - Closed
- [ ] Cost input (number type, min 0, step 0.01)
- [ ] All required fields marked with *

#### Form Validation:
- [ ] Cannot submit with empty Origin Port
- [ ] Cannot submit with empty Destination Port
- [ ] Cannot submit with duration < 1
- [ ] Shows error: "Origin Port and Destination Port are required"
- [ ] Shows error: "Duration must be at least 1 day"

#### Form Submission:
- [ ] Click "Create Route" button
- [ ] Loading spinner appears on button
- [ ] Button disabled during submission
- [ ] Success message: "Route created successfully!"
- [ ] Auto-redirects to `/routes` after 1.5 seconds
- [ ] New route appears in list
- [ ] Stats update automatically
- [ ] No TypeScript errors in console

#### Error Handling:
- [ ] If backend error, shows error message with AlertCircle icon
- [ ] Error message displays actual error from backend
- [ ] Modal doesn't close on error
- [ ] Can retry after fixing issue
- [ ] No local fallback route created

#### Cancel Action:
- [ ] Click "Cancel" button
- [ ] Confirmation dialog: "Are you sure you want to cancel?"
- [ ] Clicking OK navigates back to `/routes`
- [ ] No data saved

#### API Call Verification:
- [ ] Open browser DevTools → Network tab
- [ ] Submit form
- [ ] See POST request to `/api/routes`
- [ ] Request payload includes:
  ```json
  {
    "originPort": "string",
    "destinationPort": "string",
    "duration": number,
    "distance": number,
    "status": "string",
    "transportationMode": "string",
    "cost": number
  }
  ```
- [ ] Response includes generated `routeId` and `createdAt`
- [ ] Response status: 200 or 201

---

### 3. Route Details Page (`/routes/:id`)

#### Navigation:
- [ ] Click 👁️ (eye) icon on a route in list
- [ ] Navigates to `/routes/:id` (e.g., `/routes/5`)
- [ ] URL shows correct route ID

#### Page Header:
- [ ] Title: "Route Details"
- [ ] Description: "Detailed information about the selected route"
- [ ] Back button navigates to `/routes`

#### Route Information Card:
- [ ] Title shows: "Origin → Destination"
- [ ] Description shows: "Route ID: #[id]"
- [ ] Status badge displays with correct color
- [ ] Transport mode badge displays with correct color
- [ ] Stats display correctly:
  - Distance (km) with MapPin icon
  - Duration (days) with Clock icon
  - Cost ($) with DollarSign icon
  - Created date with Calendar icon
- [ ] Stats have colored backgrounds:
  - Distance: blue (bg-blue-50)
  - Duration: green (bg-green-50)
  - Cost: yellow (bg-yellow-50)
  - Created: purple (bg-purple-50)

#### Route Visualization:
- [ ] Map renders with single route
- [ ] Route line displayed correctly
- [ ] Map is interactive

#### Assigned Shipments:
- [ ] Section shows "Assigned Shipments"
- [ ] If no shipments: shows empty state with Package icon
- [ ] If shipments exist: displays in table
- [ ] Table columns: Shipment ID, Origin, Destination, Status, Estimated Delivery, Actions
- [ ] Status badges colored correctly
- [ ] Eye icon button navigates to shipment details

#### Route Actions:
- [ ] "Edit Route" button appears
- [ ] "Delete Route" button appears (red text)
- [ ] Edit button navigates to `/routes/:id/edit`
- [ ] Delete button shows confirmation dialog

#### Loading State:
- [ ] Shows spinner while loading route details
- [ ] Message: "Loading route details..."

#### Error State:
- [ ] If route not found, shows error
- [ ] AlertCircle icon displayed
- [ ] "Try Again" button appears

---

### 4. Edit Route Page (`/routes/:id/edit`)

#### Navigation:
- [ ] Click ✏️ (edit) icon on a route in list
- [ ] Navigates to `/routes/:id/edit`
- [ ] URL shows correct route ID
- [ ] Form loads with existing data

#### Page Header:
- [ ] Title: "Edit Route"
- [ ] Description: "Update route information"
- [ ] Back button navigates to `/routes`

#### Form Pre-fill:
- [ ] Origin Port filled with current value
- [ ] Destination Port filled with current value
- [ ] Duration filled with current value
- [ ] Distance filled with current value
- [ ] Transport Mode dropdown shows current value
- [ ] Status dropdown shows current value
- [ ] Cost filled with current value

#### Form Submission:
- [ ] Make changes to fields
- [ ] Click "Update Route" button
- [ ] Loading spinner appears
- [ ] Button disabled during submission
- [ ] Success message: "Route updated successfully!"
- [ ] Auto-redirects to `/routes` after 1.5 seconds
- [ ] Changes reflected in list immediately

#### Error Handling:
- [ ] Same validation as create form
- [ ] Error messages display if validation fails
- [ ] Backend errors shown to user

#### API Call Verification:
- [ ] Open DevTools → Network tab
- [ ] Submit form
- [ ] See PUT request to `/api/routes/:id`
- [ ] Request payload matches create format
- [ ] Response status: 200

---

### 5. Delete Route

#### From List Page:
- [ ] Click 🗑️ (trash) icon on a route
- [ ] Confirmation dialog appears
- [ ] Message: "Are you sure you want to delete route '[Origin] → [Destination]'?"
- [ ] Click Cancel → nothing happens
- [ ] Click OK → route deleted
- [ ] List refreshes automatically
- [ ] Deleted route no longer visible
- [ ] Stats update automatically

#### From Details Page:
- [ ] Navigate to route details
- [ ] Click "Delete Route" button
- [ ] Same confirmation dialog
- [ ] After deletion, redirects to `/routes`

#### API Call Verification:
- [ ] Open DevTools → Network tab
- [ ] Delete a route
- [ ] See DELETE request to `/api/routes/:id`
- [ ] Response status: 200 or 204

---

### 6. Real-time Updates

#### Event System:
- [ ] Open `/routes` in browser
- [ ] Open DevTools → Console
- [ ] Create a new route
- [ ] See "routes-updated" event dispatched
- [ ] List reloads automatically
- [ ] Stats update without manual refresh

#### Multiple Tabs:
- [ ] Open `/routes` in two browser tabs
- [ ] Create route in tab 1
- [ ] Tab 2 doesn't update (would need WebSocket for this)
- [ ] Refresh tab 2 → new route appears

---

### 7. Backend Integration

#### Backend Running:
```bash
curl http://localhost:8080/api/routes
```
- [ ] Returns JSON array of routes
- [ ] Status: 200

#### Create Route:
```bash
curl -X POST http://localhost:8080/api/routes \
  -H "Content-Type: application/json" \
  -d '{
    "originPort": "Test Port A",
    "destinationPort": "Test Port B",
    "duration": 10,
    "distance": 1000,
    "status": "Active",
    "transportationMode": "OCEAN",
    "cost": 15000
  }'
```
- [ ] Returns created route with routeId and createdAt
- [ ] Status: 200 or 201

#### Get Route by ID:
```bash
curl http://localhost:8080/api/routes/1
```
- [ ] Returns route with ID 1
- [ ] Status: 200

#### Update Route:
```bash
curl -X PUT http://localhost:8080/api/routes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "originPort": "Updated Port A",
    "destinationPort": "Updated Port B",
    "duration": 15,
    "distance": 1500,
    "status": "Active",
    "transportationMode": "OCEAN",
    "cost": 20000
  }'
```
- [ ] Returns updated route
- [ ] Status: 200

#### Delete Route:
```bash
curl -X DELETE http://localhost:8080/api/routes/1
```
- [ ] Route deleted
- [ ] Status: 200 or 204

---

### 8. UI/UX Polish

#### Design Consistency:
- [ ] All pages use shadcn-vue components
- [ ] Colors consistent across pages
- [ ] Icons from lucide-vue-next
- [ ] Spacing and padding uniform
- [ ] Typography consistent

#### Responsive Design:
- [ ] Test on desktop (1920x1080)
- [ ] Test on laptop (1366x768)
- [ ] Test on tablet (768x1024)
- [ ] Test on mobile (375x667)
- [ ] All pages responsive
- [ ] No horizontal scroll
- [ ] Forms usable on all sizes

#### Accessibility:
- [ ] All buttons have labels
- [ ] Icons have titles
- [ ] Forms have proper labels
- [ ] Error messages descriptive
- [ ] Success messages clear
- [ ] Loading states announced

#### Performance:
- [ ] Pages load quickly
- [ ] No unnecessary re-renders
- [ ] API calls complete in < 15 seconds
- [ ] No memory leaks
- [ ] Smooth transitions

---

## 🐛 Common Issues to Check

### Console Errors:
- [ ] No Vue warnings
- [ ] No TypeScript errors
- [ ] No network errors (except expected errors)
- [ ] No unhandled promise rejections

### Browser Compatibility:
- [ ] Test in Chrome
- [ ] Test in Firefox
- [ ] Test in Safari (if available)
- [ ] Test in Edge

### Edge Cases:
- [ ] Creating route with minimum values (duration=1, distance=0, cost=0)
- [ ] Creating route with large values (distance=999999, cost=9999999)
- [ ] Editing route to invalid values
- [ ] Navigating while form is submitting
- [ ] Deleting route that has assigned shipments
- [ ] Viewing route that doesn't exist (404)

---

## ✅ Success Criteria

All tests pass when:
1. ✅ No console errors
2. ✅ All pages load correctly
3. ✅ All forms submit successfully
4. ✅ Backend integration works
5. ✅ Real-time updates functioning
6. ✅ UI is professional and clean
7. ✅ Responsive on all devices
8. ✅ Error handling comprehensive
9. ✅ User feedback clear and helpful
10. ✅ Navigation smooth and intuitive

---

## 📝 Testing Notes

**Tester:** _______________
**Date:** _______________
**Browser:** _______________
**Screen Size:** _______________

### Issues Found:
```
Issue #1:
Description:
Steps to Reproduce:
Expected:
Actual:

Issue #2:
...
```

### Overall Assessment:
```
Pass / Fail

Comments:

```
