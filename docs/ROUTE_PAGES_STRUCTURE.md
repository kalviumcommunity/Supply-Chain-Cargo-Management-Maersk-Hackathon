# Route Management - Page Structure

## 🗺️ Navigation Flow

```
┌─────────────────────────────────────────────┐
│         Dashboard / Any Page                │
│                                             │
│  Sidebar → Click "Route Management"        │
└─────────────┬───────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────┐
│         RouteList.vue (/routes)             │
│  ┌───────────────────────────────────────┐ │
│  │ Header: Route Management              │ │
│  │ [+ Add Route Button]                  │ │
│  └───────────────────────────────────────┘ │
│  ┌───────────────────────────────────────┐ │
│  │ Stats Cards:                          │ │
│  │ [Total] [Avg Duration] [Avg Cost]     │ │
│  │ [Total Distance]                      │ │
│  └───────────────────────────────────────┘ │
│  ┌───────────────────────────────────────┐ │
│  │ 🗺️ Route Map Visualization            │ │
│  │ [Interactive Map with Route Lines]    │ │
│  └───────────────────────────────────────┘ │
│  ┌───────────────────────────────────────┐ │
│  │ Routes Table                          │ │
│  │ ID | Origin | Dest | ... | Actions   │ │
│  │ 1  | Mumbai | Chen | ... | 👁️ ✏️ 🗑️  │ │
│  │ 2  | Delhi  | Bang | ... | 👁️ ✏️ 🗑️  │ │
│  └───────────────────────────────────────┘ │
└─────┬─────────┬─────────┬─────────────────┘
      │         │         │
      │ Click   │ Click   │ Click
      │ [+Add]  │ [👁️View] │ [✏️Edit]
      │         │         │
      ▼         ▼         ▼
┌─────────┐ ┌──────────┐ ┌──────────────────┐
│ CREATE  │ │ DETAILS  │ │ EDIT             │
└─────────┘ └──────────┘ └──────────────────┘
```

---

## 📝 Create Route Page

```
/routes/create

┌─────────────────────────────────────────────┐
│  RouteForm.vue                              │
│                                             │
│  [← Back to Routes]                         │
│                                             │
│  Create New Route                           │
│  Set up a new shipping route                │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ Route Information                     │ │
│  │                                       │ │
│  │ Origin Port *      Destination Port *│ │
│  │ [Mumbai       ]    [Chennai        ] │ │
│  │                                       │ │
│  │ Duration (days)*   Distance (km)     │ │
│  │ [10           ]    [1230           ] │ │
│  │                                       │ │
│  │ Transport Mode     Status *          │ │
│  │ [🚢 Ocean ▾    ]   [Active ▾       ] │ │
│  │                                       │ │
│  │ Cost ($)                              │ │
│  │ [25000        ]                       │ │
│  │                                       │ │
│  │         [Cancel]  [Create Route]     │ │
│  └───────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

---

## 👁️ Route Details Page

```
/routes/:id

┌─────────────────────────────────────────────┐
│  RouteDetails.vue                           │
│                                             │
│  [← Back to Routes]                         │
│                                             │
│  Route Details                              │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ Mumbai → Chennai                      │ │
│  │ Route ID: #5                          │ │
│  │                    [Active] [OCEAN]   │ │
│  │ ─────────────────────────────────────│ │
│  │  📍 1230 km  ⏱️ 10 days              │ │
│  │  💰 $25,000  📅 Oct 11, 2025          │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ 🗺️ Route Visualization                │ │
│  │ [Map showing single route line]       │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ Assigned Shipments                    │ │
│  │ ─────────────────────────────────────│ │
│  │ ID | Origin | Dest | Status | ...    │ │
│  │ #1 | Mumbai | Chen | In Transit ...  │ │
│  │ #2 | Mumbai | Chen | Delivered ...   │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ Route Actions                         │ │
│  │ ─────────────────────────────────────│ │
│  │ [✏️ Edit Route]  [🗑️ Delete Route]   │ │
│  └───────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

---

## ✏️ Edit Route Page

```
/routes/:id/edit

┌─────────────────────────────────────────────┐
│  RouteForm.vue (Edit Mode)                  │
│                                             │
│  [← Back to Routes]                         │
│                                             │
│  Edit Route                                 │
│  Update route information                   │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ Route Information                     │ │
│  │                                       │ │
│  │ Origin Port *      Destination Port *│ │
│  │ [Mumbai       ]    [Chennai        ] │ │
│  │         ↑ Pre-filled with data        │ │
│  │                                       │ │
│  │ Duration (days)*   Distance (km)     │ │
│  │ [10           ]    [1230           ] │ │
│  │                                       │ │
│  │ Transport Mode     Status *          │ │
│  │ [🚢 Ocean ▾    ]   [Active ▾       ] │ │
│  │                                       │ │
│  │ Cost ($)                              │ │
│  │ [25000        ]                       │ │
│  │                                       │ │
│  │         [Cancel]  [Update Route]     │ │
│  └───────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

---

## 🎨 Design Features

### Color Scheme:
- **Blue** (#3B82F6): Primary actions, distances
- **Green** (#10B981): Duration, success states
- **Yellow** (#F59E0B): Cost, warnings
- **Purple** (#8B5CF6): Statistics, info
- **Red** (#EF4444): Delete, errors, delayed status

### Component Usage:

#### RouteList:
```vue
<PageHeader title="Route Management" icon="Route" />
<Card> Stats cards with icons </Card>
<Card> RouteMap visualization </Card>
<Table> Routes with actions </Table>
<Badge> Status indicators </Badge>
<Button> Action buttons </Button>
```

#### RouteForm:
```vue
<Card> Form container </Card>
<Input> Text and number inputs </Input>
<select> Dropdown for mode/status </select>
<Button> Submit and cancel </Button>
<AlertCircle> Error messages </AlertCircle>
<CheckCircle> Success messages </CheckCircle>
```

#### RouteDetails:
```vue
<Card> Route info with badges </Card>
<Card> Stats with colored backgrounds </Card>
<Card> RouteMap (single route) </Card>
<Table> Assigned shipments </Table>
<Card> Action buttons </Card>
```

---

## 🔄 State Management

### Loading States:
```vue
<div v-if="isLoading">
  <Loader2 class="animate-spin" />
  Loading...
</div>
```

### Error States:
```vue
<div v-else-if="error">
  <AlertCircle />
  {{ error }}
  <Button @click="retry">Try Again</Button>
</div>
```

### Empty States:
```vue
<div v-else-if="routes.length === 0">
  No routes found.
  Create your first route to get started.
</div>
```

---

## 🔌 API Integration

### Route List:
```javascript
GET /api/routes
→ loads all routes
→ displays in table
→ updates stats
→ renders on map
```

### Route Create:
```javascript
POST /api/routes
← { originPort, destinationPort, duration, ... }
→ { routeId, originPort, ..., createdAt }
→ dispatches 'routes-updated' event
→ redirects to /routes
```

### Route Details:
```javascript
GET /api/routes/:id
→ loads single route
→ displays details

GET /api/shipments
→ filters by routeId
→ shows assigned shipments
```

### Route Edit:
```javascript
GET /api/routes/:id
→ pre-fills form

PUT /api/routes/:id
← { originPort, destinationPort, ... }
→ { routeId, ..., createdAt }
→ dispatches 'routes-updated' event
→ redirects to /routes
```

### Route Delete:
```javascript
DELETE /api/routes/:id
→ confirmation dialog
→ deletes route
→ dispatches 'routes-updated' event
→ reloads list
```

---

## ✅ User Experience Flow

### Creating a Route:
1. User clicks **"+ Add Route"** button
2. Navigates to `/routes/create`
3. Fills out form with route information
4. Clicks **"Create Route"**
5. Loading spinner shows
6. Success message displays: "Route created successfully!"
7. Auto-redirects to `/routes` after 1.5 seconds
8. New route appears in table
9. Stats cards update automatically
10. Map updates with new route

### Viewing Route Details:
1. User clicks **👁️ View** icon on a route
2. Navigates to `/routes/:id`
3. Sees route information with stats
4. Views route on map
5. Sees list of assigned shipments
6. Can click to edit or delete

### Editing a Route:
1. User clicks **✏️ Edit** icon
2. Navigates to `/routes/:id/edit`
3. Form pre-filled with current data
4. Makes changes
5. Clicks **"Update Route"**
6. Success message displays
7. Redirects to `/routes`
8. Changes reflected immediately

### Deleting a Route:
1. User clicks **🗑️ Delete** icon
2. Confirmation dialog: "Are you sure?"
3. User confirms
4. Route deleted from backend
5. List reloads automatically
6. Deleted route removed from view

---

## 🚀 Performance Features

- **Lazy Loading:** Routes loaded on demand
- **Real-time Updates:** Event-driven synchronization
- **Optimistic UI:** Immediate feedback on actions
- **Error Recovery:** Retry buttons for failed requests
- **Caching:** Re-use loaded data when possible

---

## 📱 Responsive Design

### Desktop (≥1024px):
- 4-column stats grid
- Full table with all columns
- Large map view
- Side-by-side form fields

### Tablet (768px - 1023px):
- 2-column stats grid
- Scrollable table
- Medium map view
- Side-by-side form fields

### Mobile (<768px):
- 1-column stats grid
- Simplified table / card view
- Compact map view
- Stacked form fields
