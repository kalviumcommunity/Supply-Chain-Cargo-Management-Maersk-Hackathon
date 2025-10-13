# CargoFlow - Supply Chain Management Client

A modern Vue 3 + Vite application for supply chain cargo management.

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ or Bun
- Backend API running (local or production)

### Installation

```bash
# Install dependencies
npm install
# or
bun install
```

### Environment Setup

**For Local Development:**
```bash
# Copy the local environment file
cp .env.local .env

# Start the development server
npm run dev
# or
bun run dev
```

**For Production Build:**
```bash
# Build using production environment
npm run build
# or
bun run build
```

The app will automatically connect to:
- **Local**: `http://localhost:8080/api` (development)
- **Production**: `http://65.0.178.32:8080/api` (production build)

📖 See [ENV_CONFIG.md](./ENV_CONFIG.md) for detailed environment configuration.

## 🛠️ Tech Stack

- **Vue 3** - Progressive JavaScript Framework
- **Vite** - Next Generation Frontend Tooling
- **TailwindCSS** - Utility-first CSS framework
- **shadcn-vue** - Re-usable components built with Radix Vue and Tailwind CSS
- **Lucide Vue** - Beautiful & consistent icon toolkit
- **Vue Router** - Official router for Vue.js

## 📦 Project Structure

```
client/
├── src/
│   ├── components/        # Vue components
│   │   ├── cargo/        # Cargo management
│   │   ├── routes/       # Route management
│   │   ├── shipments/    # Shipment tracking
│   │   ├── vendors/      # Vendor management
│   │   ├── shared/       # Shared components
│   │   └── ui/           # UI components (shadcn-vue)
│   ├── services/         # API services
│   ├── router/           # Vue Router configuration
│   └── lib/              # Utilities
├── .env.example          # Environment template
├── .env.local            # Local development config
└── .env.production       # Production config
```

## 🔧 Available Scripts

```bash
# Development
npm run dev          # Start dev server
bun run dev

# Production
npm run build        # Build for production
npm run preview      # Preview production build
bun run build
bun run preview

# Linting
npm run lint         # Run ESLint
```

## 🌐 API Integration

The application connects to a Spring Boot backend API. Configuration is managed through environment variables.

See [ENV_CONFIG.md](./ENV_CONFIG.md) for API configuration details.

## 📚 Learn More

- [Vue 3 Documentation](https://vuejs.org/)
- [Vite Documentation](https://vitejs.dev/)
- [Vue Router](https://router.vuejs.org/)
- [TailwindCSS](https://tailwindcss.com/)
- [shadcn-vue](https://www.shadcn-vue.com/)
