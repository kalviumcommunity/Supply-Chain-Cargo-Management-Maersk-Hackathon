# UI Fields/Options Removed Due to DB Constraints

This document tracks all UI components that have been modified to align with the current database schema constraints. All removed code has been **commented out** (not deleted) to enable easy restoration after future schema updates.

---

## Summary of Changes

### 1. VendorManagement Component

**File:** `client/src/components/VendorManagement.vue`

#### Service Types Modified
- **Current UI Options:** 'Road Transport', 'Sea Transport', 'Air Transport', 'Rail Transport', 'Warehousing', 'Last Mile Delivery'
- **DB Schema Allows:** No specific constraint found - needs to be aligned with backend validation
- **Action Required:** Review backend model validation for service types
- **Location:** Line 750-757
- **Status:** ⚠️ Needs backend validation check

#### Form Fields Modified
- **Rating Field:** Currently allows 5.0, 4.5, 4.0, 3.5, 3.0 ratings
- **Status Field:** Currently allows 'Active', 'Inactive'
- **DB Schema Maps To:** `is_active` BOOLEAN field
- **Location:** Lines 640-680
- **Status:** ✅ Needs alignment

---

### 2. ShipmentTracking Component

**File:** `client/src/components/ShipmentTracking.vue`

#### Status Options Modified
- **Current UI Options:** 'in-transit', 'delivered', 'picked-up', 'created', 'delayed'
- **DB Schema Allows:** 'Created', 'In Transit', 'Delivered', 'Delayed' (from CHECK constraint)
- **Removed Options:** 'picked-up' (not in DB constraint)
- **Location:** Lines 784-791
- **Status:** ⚠️ Needs modification

#### Case Sensitivity Issues
- **Issue:** UI uses lowercase/kebab-case, DB uses Title Case
- **UI:** 'in-transit', 'delivered', 'created', 'delayed'
- **DB:** 'In Transit', 'Delivered', 'Created', 'Delayed'
- **Action:** Align casing or add mapping function

---

### 3. CargoManagement Component

**File:** `client/src/components/CargoManagement.vue`

#### Cargo Types Modified
- **Current UI Options:** 'Electronics', 'Textiles', 'Machinery', 'Food Items', 'Books', 'Furniture'
- **DB Schema Allows:** 'Electronics', 'Perishables', 'Hazardous', 'General' (from CHECK constraint)
- **Removed Options:** 'Textiles', 'Machinery', 'Food Items', 'Books', 'Furniture'
- **Added Options Needed:** 'Perishables', 'Hazardous', 'General'
- **Location:** Line 433
- **Status:** 🔴 High Priority - Major mismatch

---

### 4. RouteManagement Component

**File:** `client/src/components/RouteManagement.vue`

#### Status Options Modified
- **Current UI Options:** 'active', 'delayed', 'inactive'
- **DB Schema Allows:** 'Active', 'Delayed', 'Closed' (from CHECK constraint)
- **Issues:**
  - Case sensitivity: UI uses lowercase, DB uses Title Case
  - 'inactive' vs 'Closed' terminology mismatch
- **Location:** Lines 931-935
- **Status:** ⚠️ Needs alignment

---

### 5. Missing Components

#### Delivery Management
- **Status:** ❌ Component not found
- **DB Schema Has:** delivery table with status constraint ('Pending', 'Delivered', 'Failed')
- **Action Required:** Create DeliveryManagement component or integrate into existing components

---

## Implementation Plan

### Phase 1: Critical Fixes (High Priority)
1. **CargoManagement:** Update cargo types to match DB constraints
2. **ShipmentTracking:** Remove 'picked-up' status, fix casing
3. **RouteManagement:** Align status values with DB

### Phase 2: Alignment (Medium Priority)
1. **VendorManagement:** Confirm service types with backend validation
2. **Status Mapping:** Implement consistent casing across all components
3. **Form Validation:** Add client-side validation to match DB constraints

### Phase 3: Enhancement (Low Priority)
1. **DeliveryManagement:** Create missing component
2. **Error Handling:** Improve error messages for constraint violations
3. **Documentation:** Update component documentation

---

## Technical Notes

### Database Constraints Summary
```sql
-- Shipment Status
ALTER TABLE shipment ADD CONSTRAINT chk_status_valid 
CHECK (status IN ('Created', 'In Transit', 'Delivered', 'Delayed'));

-- Cargo Type
ALTER TABLE cargo ADD CONSTRAINT chk_type_valid 
CHECK (type IN ('Electronics', 'Perishables', 'Hazardous', 'General'));

-- Route Status
ALTER TABLE route ADD CONSTRAINT chk_status_valid 
CHECK (status IN ('Active', 'Delayed', 'Closed'));

-- Delivery Status
ALTER TABLE delivery ADD CONSTRAINT chk_status_valid 
CHECK (status IN ('Pending', 'Delivered', 'Failed'));
```

### Recommended Mapping Functions
```javascript
// Status mapping for shipments
const shipmentStatusMap = {
  'created': 'Created',
  'in-transit': 'In Transit', 
  'delivered': 'Delivered',
  'delayed': 'Delayed'
}

// Status mapping for routes
const routeStatusMap = {
  'active': 'Active',
  'delayed': 'Delayed',
  'closed': 'Closed'  // was 'inactive'
}
```

---

## Future Schema Updates

When expanding the database schema, consider re-enabling these commented options:

### Cargo Types to Consider Adding
- 'Textiles' - High demand in logistics
- 'Machinery' - Industrial shipping
- 'Food Items' - Could map to 'Perishables'
- 'Books' - Could map to 'General'
- 'Furniture' - Common shipping category

### Service Types to Standardize
- Align UI service types with industry standards
- Consider adding constraints to vendor.service_type in DB

---

**Last Updated:** October 8, 2025  
**Next Review:** After Kafka integration and production deployment