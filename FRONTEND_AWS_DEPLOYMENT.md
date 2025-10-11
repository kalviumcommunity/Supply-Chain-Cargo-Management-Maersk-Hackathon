# Frontend Deployment Guide (AWS S3 & CloudFront)

This guide provides a concise workflow for deploying the Vue.js frontend located in the `client/` directory to AWS using S3 for hosting and CloudFront as a CDN.

### **Project Overview**
- **Framework**: Vue 3 + Vite
- **Package Manager**: Bun
- **Source Directory**: `client/`
- **Build Output**: `client/dist/`
- **API Service**: `client/src/services/api.js`, which uses `import.meta.env.VITE_API_BASE_URL`.

---

### **Step 1: Build Production Assets**

First, create a production environment file to ensure the application connects to the correct backend API.

1.  **Create Environment File**:
    In the `client/` directory, create a file named `.env.production`.

    ```bash
    # Navigate to the client directory
    cd client/

    # Create and populate the production environment file
    echo "VITE_API_BASE_URL=https://your-production-api-url.com/api" > .env.production
    ```
    *Replace `https://your-production-api-url.com/api` with your actual production API endpoint.*

2.  **Install Dependencies and Build**:
    Use Bun to install dependencies and run the Vite build script.

    ```bash
    # From the client/ directory
    bun install
    bun run build
    ```
    This command compiles the application and places the static assets in the `client/dist/` directory.

---

### **Step 2: Configure and Deploy to S3**

Next, create and configure an S3 bucket to host the static files.

1.  **Create S3 Bucket**:
    Bucket names must be globally unique. Use a descriptive name.

    ```bash
    # Replace with your unique bucket name
    export BUCKET_NAME=maersk-hackathon-frontend-app

    aws s3 mb s3://$BUCKET_NAME --region us-east-1
    ```

2.  **Enable Static Website Hosting**:
    Configure the bucket to serve a static website. Set both the index and error documents to `index.html` to enable client-side routing for this Single Page Application (SPA).

    ```bash
    aws s3 website s3://$BUCKET_NAME/ --index-document index.html --error-document index.html
    ```

3.  **Set Bucket Policy for Public Access (Optional)**:
    To make the site accessible via the S3 website endpoint, apply a public-read policy. **Note**: For production, it's recommended to keep the bucket private and use CloudFront with an Origin Access Identity (OAI).

    ```bash
    # Create a policy file
    cat > policy.json <<EOF
    {
      "Version": "2012-10-17",
      "Statement": [{
        "Sid": "PublicReadGetObject",
        "Effect": "Allow",
        "Principal": "*",
        "Action": "s3:GetObject",
        "Resource": "arn:aws:s3:::$BUCKET_NAME/*"
      }]
    }
    EOF

    # Apply the policy
    aws s3api put-bucket-policy --bucket $BUCKET_NAME --policy file://policy.json
    rm policy.json
    ```

4.  **Upload Files to S3**:
    Sync the build output to your S3 bucket. Use different cache settings for hashed assets versus the `index.html` file to ensure users get new versions immediately upon deployment.

    ```bash
    # Sync hashed assets (JS, CSS) with a long cache time (1 year)
    aws s3 sync client/dist/assets s3://$BUCKET_NAME/assets --cache-control "public, max-age=31536000" --delete

    # Upload other files from dist (like index.html, favicon) with no cache
    aws s3 cp client/dist/index.html s3://$BUCKET_NAME/ --cache-control "no-cache, no-store, must-revalidate"
    # Add similar cp commands for other root files like favicon.ico if they exist
    ```

---

### **Step 3: Set Up CloudFront (Recommended for Production)**

Using CloudFront provides benefits like a global CDN, SSL/TLS, and the ability to keep your S3 bucket private.

1.  **Create a Distribution**:
    - **Origin Domain**: Select your S3 bucket. If you plan to keep the bucket private, set up an **Origin Access Identity (OAI)** and grant it read permissions.
    - **Viewer Protocol Policy**: Redirect HTTP to HTTPS.
    - **Default Root Object**: Set to `index.html`.

2.  **Configure Error Pages for SPA Routing**:
    To ensure browser refreshes and direct navigation work correctly, create custom error responses for `403: Forbidden` and `404: Not Found`.
    - **Response Page Path**: `/index.html`
    - **HTTP Response Code**: `200: OK`

3.  **Invalidate Cache After Deployment**:
    After deploying new code, invalidate the CloudFront cache to force it to fetch the latest `index.html`.

    ```bash
    # Replace with your distribution ID
    export DISTRIBUTION_ID=E12345ABCDEF

    aws cloudfront create-invalidation --distribution-id $DISTRIBUTION_ID --paths "/*"
    ```

---

### **Step 4: Verification**

- Access your site via the S3 website endpoint or the CloudFront domain.
- **Check SPA Routing**: Navigate directly to a route like `https://your-domain.com/dashboard`. The page should load correctly.
- **Check Network Tab**: In your browser's developer tools, confirm that `index.html` has `no-cache` headers and that assets in the `/assets` directory have a long `max-age`.
- **API Connectivity**: Ensure the frontend can successfully communicate with your backend API. Check for any CORS errors in the browser console.

---

### **Minimal CI/CD with GitHub Actions**

Automate deployment by adding a workflow file to `.github/workflows/deploy-frontend.yml`.

```yaml
name: Deploy Frontend to S3

on:
  push:
    branches: [ main ] # Or your production branch
    paths: [ 'client/**' ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Setup Bun
        uses: oven-sh/setup-bun@v1

      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1

      - name: Install, Build, and Deploy
        env:
          BUCKET_NAME: your-s3-bucket-name # Replace with your bucket name
          PROD_API_URL: ${{ secrets.VITE_API_BASE_URL }}
        run: |
          cd client
          echo "VITE_API_BASE_URL=$PROD_API_URL" > .env.production
          bun install
          bun run build
          aws s3 sync dist/assets s3://$BUCKET_NAME/assets --cache-control "public, max-age=31536000" --delete
          aws s3 cp dist/index.html s3://$BUCKET_NAME/index.html --cache-control "no-cache, no-store, must-revalidate"

      - name: Invalidate CloudFront Cache
        env:
          DISTRIBUTION_ID: ${{ secrets.CLOUDFRONT_DISTRIBUTION_ID }}
        run: |
          aws cloudfront create-invalidation --distribution-id $DISTRIBUTION_ID --paths "/*"
```
**Secrets to configure in GitHub Repository Settings:**
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `VITE_API_BASE_URL` (Your production API URL)
- `CLOUDFRONT_DISTRIBUTION_ID` (Optional, if using CloudFront)


