# CargoFlow Frontend Deployment to AWS S3 + CloudFront

## Professional Deployment Guide for Vue 3 Supply Chain Management Platform

This comprehensive guide provides detailed, project-specific instructions for deploying the **CargoFlow** Vue 3 frontend to AWS. The guide is tailored to the exact codebase structure and configuration found in the client dir.

---

## 📋 Prerequisites & Environment Setup

### System Requirements
- **AWS Account** with S3, CloudFront, and IAM permissions
- **AWS CLI v2** installed and configured (`aws configure`)
- **Bun runtime** (v1.0+) - Primary package manager for this project
- **Node.js** (v18+) - Fallback runtime environment

### Verify Local Environment
```bash
# Verify Bun installation (required for this project)
bun --version

# Verify AWS CLI access
aws sts get-caller-identity

# Navigate to project frontend
cd /home/rahulrr/Projects/maersk/client
```

---

## 🏗️ CargoFlow Project Architecture Analysis

### Codebase Structure
```
client/                           # Frontend root directory
├── .env                         # Environment variables (VITE_API_BASE_URL)
├── bun.lock                     # Bun lockfile (2,947 entries)
├── index.html                   # Entry point: "Cargo Management System"
├── package.json                 # Bun-optimized scripts and dependencies
├── vite.config.js              # Vite + Tailwind CSS v4.1.13 config
├── public/
│   └── vite.svg                # Static favicon asset
└── src/
    ├── main.js                 # Vue 3 app initialization
    ├── App.vue                 # Root component with conditional sidebar
    ├── style.css               # Tailwind CSS v4 import
    ├── components/
    │   ├── LandingPage.vue     # Root route ("/") - 560 lines
    │   ├── Dashboard.vue       # Main dashboard ("/dashboard")
    │   ├── CargoManagement.vue # Cargo CRUD operations
    │   ├── ShipmentTracking.vue# Shipment monitoring
    │   ├── RouteManagement.vue # Route optimization
    │   ├── VendorManagement.vue# Vendor relations
    │   ├── Login.vue           # Authentication component
    │   ├── OAuthCallback.vue   # OAuth2 callback handler
    │   ├── Sidebar.vue         # Navigation component
    │   └── shared/             # Reusable components
    │       ├── BaseModal.vue
    │       ├── ConfirmDialog.vue
    │       └── RouteMap.vue    # Leaflet integration
    ├── router/
    │   └── index.js            # Vue Router with auth guards
    └── services/
        ├── api.js              # Centralized API client
        └── auth.js             # Authentication service
```

### Key Technology Stack
- **Vue 3.5.21** with Composition API
- **Vite 7.1.7** build tool with HMR
- **Tailwind CSS 4.1.13** with Vite plugin
- **Vue Router 4** with meta-based navigation
- **Leaflet 1.9.4** for geospatial features
- **Lucide Vue Next 0.544.0** for icons

---

## 🔧 Environment Configuration

### Current Environment Analysis
The project uses environment variables defined in `client/.env`:

```properties
# Current configuration
VITE_API_BASE_URL=http://localhost:8080/api
VITE_ENV=development
```

### Production Environment Setup
Create a production environment file:

```bash
# Create production environment
cp client/.env client/.env.production

# Edit production values
cat > client/.env.production << EOF
# Production API Configuration
VITE_API_BASE_URL=https://api.cargoflow.maersk.com/api
VITE_ENV=production

# Optional: Analytics and monitoring
VITE_APP_VERSION=1.0.0
VITE_BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
EOF
```

### API Service Configuration
The API service (`src/services/api.js`) automatically reads environment variables:

```javascript
// Line 4-5 in src/services/api.js
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
```

**Critical:** Ensure your Spring Boot backend CORS configuration allows your production domain.

---

## 🚀 Build Process

### Bun-Optimized Build (Recommended)
```bash
cd client

# Install dependencies using Bun (faster than npm)
bun install

# Production build using Bun runtime
bun run build
# Equivalent to: bunx --bun vite build
```

### Build Output Analysis
The build process creates:
```
client/dist/
├── index.html              # Minified entry point
├── assets/
│   ├── index-[hash].js     # Vue app bundle (~200-400KB)
│   ├── index-[hash].css    # Tailwind CSS bundle (~50-100KB)
│   └── vendor-[hash].js    # Third-party libraries
└── vite.svg               # Static assets from public/
```

### Build Verification
```bash
# Verify build success
ls -la dist/
du -sh dist/           # Check total size
bun run preview        # Local preview server
```

---

## ☁️ AWS S3 Configuration

### S3 Bucket Creation
```bash
# Create bucket with unique name
export BUCKET_NAME="cargoflow-frontend-$(date +%s)"
aws s3 mb s3://$BUCKET_NAME --region us-east-1

# Enable static website hosting
aws s3 website s3://$BUCKET_NAME \
  --index-document index.html \
  --error-document index.html
```

### SPA Routing Configuration
**Critical for Vue Router:** The error document must be set to `index.html` to handle client-side routing for routes like:
- `/` (LandingPage.vue)
- `/dashboard` (Dashboard.vue)
- `/cargo` (CargoManagement.vue)
- `/shipments` (ShipmentTracking.vue)
- `/routes` (RouteManagement.vue)
- `/vendors` (VendorManagement.vue)

### Bucket Policy for Public Access
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::cargoflow-frontend-*/*"
    }
  ]
}
```

Apply the policy:
```bash
# Create policy file
cat > bucket-policy.json << 'EOF'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::BUCKET_NAME/*"
    }
  ]
}
EOF

# Replace bucket name and apply
sed "s/BUCKET_NAME/$BUCKET_NAME/g" bucket-policy.json > policy.json
aws s3api put-bucket-policy --bucket $BUCKET_NAME --policy file://policy.json
```

---

## 📦 Deployment Process

### Initial Deployment
```bash
# Deploy all files with proper content types
aws s3 sync dist/ s3://$BUCKET_NAME \
  --delete \
  --metadata-directive REPLACE \
  --cache-control "public, max-age=31536000" \
  --exclude "index.html" \
  --exclude "*.json"

# Deploy index.html with no-cache for SPA updates
aws s3 cp dist/index.html s3://$BUCKET_NAME/index.html \
  --metadata-directive REPLACE \
  --cache-control "no-cache, no-store, must-revalidate"

# Get website URL
aws s3api get-bucket-website --bucket $BUCKET_NAME
```

### Automated Deployment Script
Create a deployment script for easy updates:

```bash
cat > deploy.sh << 'EOF'
#!/bin/bash
set -e

BUCKET_NAME="cargoflow-frontend-1697872800"  # Replace with your bucket

echo "🔨 Building CargoFlow frontend..."
cd client
bun run build

echo "📦 Deploying to S3..."
# Upload assets with long cache
aws s3 sync dist/ s3://$BUCKET_NAME \
  --delete \
  --exclude "index.html" \
  --cache-control "public, max-age=31536000"

# Upload index.html with no cache
aws s3 cp dist/index.html s3://$BUCKET_NAME/index.html \
  --cache-control "no-cache, no-store, must-revalidate"

echo "✅ Deployment complete!"
echo "🌐 Website URL: http://$BUCKET_NAME.s3-website-us-east-1.amazonaws.com"
EOF

chmod +x deploy.sh
```

---

## 🌍 CloudFront CDN Setup (Production)

### Distribution Configuration
```bash
# Create CloudFront distribution
cat > cloudfront-config.json << EOF
{
  "CallerReference": "cargoflow-$(date +%s)",
  "Comment": "CargoFlow Vue 3 SPA Distribution",
  "DefaultRootObject": "index.html",
  "Origins": {
    "Quantity": 1,
    "Items": [
      {
        "Id": "S3-$BUCKET_NAME",
        "DomainName": "$BUCKET_NAME.s3.amazonaws.com",
        "S3OriginConfig": {
          "OriginAccessIdentity": ""
        }
      }
    ]
  },
  "DefaultCacheBehavior": {
    "TargetOriginId": "S3-$BUCKET_NAME",
    "ViewerProtocolPolicy": "redirect-to-https",
    "TrustedSigners": {
      "Enabled": false,
      "Quantity": 0
    },
    "ForwardedValues": {
      "QueryString": false,
      "Cookies": {
        "Forward": "none"
      }
    },
    "MinTTL": 0,
    "DefaultTTL": 86400,
    "MaxTTL": 31536000
  },
  "CustomErrorResponses": {
    "Quantity": 2,
    "Items": [
      {
        "ErrorCode": 403,
        "ResponsePagePath": "/index.html",
        "ResponseCode": "200",
        "ErrorCachingMinTTL": 300
      },
      {
        "ErrorCode": 404,
        "ResponsePagePath": "/index.html",
        "ResponseCode": "200",
        "ErrorCachingMinTTL": 300
      }
    ]
  },
  "Enabled": true,
  "PriceClass": "PriceClass_100"
}
EOF

# Create distribution
aws cloudfront create-distribution --distribution-config file://cloudfront-config.json
```

### Cache Invalidation
```bash
# Get distribution ID
DISTRIBUTION_ID=$(aws cloudfront list-distributions --query 'DistributionList.Items[0].Id' --output text)

# Invalidate cache after deployment
aws cloudfront create-invalidation \
  --distribution-id $DISTRIBUTION_ID \
  --paths "/*"
```

---

## 🔍 Deployment Verification

### Functional Testing Checklist
```bash
# Test all Vue Router routes
curl -I "https://yourcloudfront.domain.com/"           # LandingPage
curl -I "https://yourcloudfront.domain.com/dashboard"  # Dashboard
curl -I "https://yourcloudfront.domain.com/cargo"      # CargoManagement
curl -I "https://yourcloudfront.domain.com/routes"     # RouteManagement

# Test static assets
curl -I "https://yourcloudfront.domain.com/vite.svg"   # Public assets
```

### Performance Testing
```bash
# Check bundle sizes
echo "CSS Bundle:" && ls -lh dist/assets/*.css
echo "JS Bundle:" && ls -lh dist/assets/*.js
echo "Total Size:" && du -sh dist/

# Test loading times
curl -w "@curl-format.txt" -o /dev/null -s "https://yourcloudfront.domain.com"
```

### Browser Testing
1. **Landing Page** (`/`) - Professional layout with navigation
2. **Dashboard** (`/dashboard`) - Metrics and analytics
3. **Component Navigation** - Sidebar routing between management modules
4. **Authentication Flow** - Login/OAuth callback functionality
5. **API Integration** - Verify production API connectivity

---

## 🛠️ Troubleshooting

### Common Issues

**Build Errors:**
```bash
# Clear Bun cache
bun install --force

# Verify Tailwind compilation
grep -r "tailwindcss" src/
```

**Routing Issues:**
```bash
# Verify error document configuration
aws s3api get-bucket-website --bucket $BUCKET_NAME
```

**CORS Errors:**
- Ensure Spring Boot backend allows your production domain
- Check `application.properties` CORS configuration

**Environment Variables:**
```bash
# Verify build-time variables
grep -r "VITE_" dist/assets/*.js
```

---

## 📊 Production Monitoring

### CloudWatch Metrics
- S3 bucket requests and data transfer
- CloudFront cache hit ratio and response times
- Error rates for 4xx/5xx responses

### Cost Optimization
- Enable S3 Intelligent Tiering for assets
- Use CloudFront compression
- Monitor data transfer costs

---

## 🔄 CI/CD Integration

### GitHub Actions Workflow
```yaml
name: Deploy CargoFlow Frontend
on:
  push:
    branches: [main]
    paths: ['client/**']

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: oven-sh/setup-bun@v1
      - run: cd client && bun install
      - run: cd client && bun run build
      - uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1
      - run: ./deploy.sh
```

---

## 📚 References & Documentation

- **Project Repository:** Supply-Chain-Cargo-Management-Maersk-Hackathon
- **Vite Build Guide:** [vitejs.dev/guide/build](https://vitejs.dev/guide/build.html)
- **Vue Router Deployment:** [router.vuejs.org/guide/essentials/history-mode](https://router.vuejs.org/guide/essentials/history-mode.html)
- **AWS S3 Static Hosting:** [docs.aws.amazon.com/AmazonS3/latest/userguide/WebsiteHosting](https://docs.aws.amazon.com/AmazonS3/latest/userguide/WebsiteHosting.html)
- **Tailwind CSS Production:** [tailwindcss.com/docs/optimizing-for-production](https://tailwindcss.com/docs/optimizing-for-production)

---

**🚢 Your CargoFlow supply chain management platform is now deployed and ready for global access!**
