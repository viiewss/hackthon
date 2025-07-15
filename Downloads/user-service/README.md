# Banking Frontend - React Application

A modern, responsive React frontend for the GraphBanking microservices application.

## Features

- **Modern UI**: Built with React 18, TypeScript, and Tailwind CSS
- **Authentication**: JWT-based authentication with secure token management
- **GraphQL Integration**: Apollo Client for efficient data fetching
- **Responsive Design**: Mobile-first responsive design
- **Protected Routes**: Route-based authentication and authorization
- **Real-time Updates**: Live data synchronization with backend services

## Tech Stack

- **Frontend**: React 18, TypeScript
- **Styling**: Tailwind CSS
- **GraphQL**: Apollo Client
- **Routing**: React Router v6
- **Authentication**: JWT tokens
- **State Management**: React Context API
- **Build Tool**: Create React App

## Getting Started

### Prerequisites

- Node.js 16+
- npm or yarn
- Running backend services (API Gateway, microservices)

### Installation

1. **Clone and install dependencies**
   ```bash
   cd banking-frontend
   npm install
   ```

2. **Start the development server**
   ```bash
   npm start
   ```

3. **Access the application**
   - Frontend: `http://localhost:3000`
   - Login page: `http://localhost:3000/login`

## Project Structure

```
banking-frontend/
├── public/
├── src/
│   ├── apollo/
│   │   └── client.ts          # Apollo Client configuration
│   ├── components/
│   │   ├── Layout.tsx         # Main layout component
│   │   └── LoadingSpinner.tsx # Loading component
│   ├── context/
│   │   └── AuthContext.tsx    # Authentication context
│   ├── graphql/
│   │   ├── queries.ts         # GraphQL queries
│   │   └── mutations.ts       # GraphQL mutations
│   ├── pages/
│   │   ├── Login.tsx          # Login page
│   │   ├── Register.tsx       # Registration page
│   │   ├── Dashboard.tsx      # Main dashboard
│   │   ├── Accounts.tsx       # Account management
│   │   ├── Transactions.tsx   # Transaction history
│   │   └── Profile.tsx        # User profile
│   ├── types/
│   │   └── index.ts           # TypeScript type definitions
│   ├── App.tsx                # Main app component
│   └── index.tsx              # Entry point
├── tailwind.config.js         # Tailwind configuration
└── package.json
```

## API Integration

The frontend integrates with the backend through the API Gateway:

### GraphQL Services

- **User Service**: User authentication and profile management
- **Account Service**: Bank account operations
- **Transaction Service**: Payment processing and history

### Service Routing

The Apollo Client uses the `X-Service` header to route requests:

```typescript
// User service requests
const userClient = new ApolloClient({
  headers: { 'X-Service': 'user' }
});

// Account service requests
const accountClient = new ApolloClient({
  headers: { 'X-Service': 'account' }
});

// Transaction service requests
const transactionClient = new ApolloClient({
  headers: { 'X-Service': 'transaction' }
});
```

## Authentication Flow

1. **Login/Register**: User credentials sent to API Gateway
2. **JWT Token**: Backend returns JWT token on successful authentication
3. **Token Storage**: Token stored in localStorage
4. **Automatic Headers**: Apollo Client automatically adds token to requests
5. **Route Protection**: Protected routes check for valid token

## Available Scripts

- `npm start` - Start development server
- `npm run build` - Build for production
- `npm test` - Run tests
- `npm run eject` - Eject from Create React App

## Environment Variables

Create a `.env` file in the root directory:

```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_GRAPHQL_URL=http://localhost:8080/graphql
```

## Pages and Features

### 🔐 Authentication Pages

- **Login**: Email/password authentication
- **Register**: User registration with validation

### 📊 Dashboard

- Account overview
- Balance summary
- Recent transactions
- Quick actions

### 🏦 Account Management

- View all accounts
- Account details
- Balance information
- Account type management

### 💳 Transactions

- Transaction history
- Filter and search
- Transaction details
- Transfer money

### 👤 Profile

- User information
- Account settings
- Security settings

## Styling

The application uses Tailwind CSS with a custom design system:

### Color Palette

- **Primary**: Blue tones for main actions
- **Secondary**: Gray tones for secondary elements
- **Success**: Green for positive actions
- **Error**: Red for errors and warnings

### Components

- Responsive grid layouts
- Card-based design
- Form components with validation
- Loading states and spinners

## Development

### Adding New Pages

1. Create component in `src/pages/`
2. Add route in `App.tsx`
3. Update navigation in `Layout.tsx`

### Adding GraphQL Operations

1. Define queries/mutations in `src/graphql/`
2. Add TypeScript types in `src/types/`
3. Use Apollo hooks in components

### Styling Guidelines

- Use Tailwind utility classes
- Follow mobile-first responsive design
- Maintain consistent spacing and typography
- Use semantic HTML elements

## Testing

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Generate coverage report
npm run test:coverage
```

## Building for Production

```bash
# Create production build
npm run build

# Serve production build locally
npm install -g serve
serve -s build
```

## Deployment

### Static Hosting

Deploy the `build` folder to any static hosting service:

- Netlify
- Vercel
- AWS S3 + CloudFront
- GitHub Pages

### Docker

```dockerfile
FROM node:16-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Troubleshooting

### Common Issues

1. **CORS Errors**: Ensure API Gateway has proper CORS configuration
2. **Authentication Issues**: Check JWT token validity and expiration
3. **GraphQL Errors**: Verify service headers and endpoint URLs
4. **Build Errors**: Clear node_modules and reinstall dependencies

### Development Tips

- Use React DevTools for debugging
- Check Network tab for API calls
- Use Apollo Client DevTools for GraphQL debugging
- Enable TypeScript strict mode for better type safety

## License

This project is licensed under the MIT License.

---

**Built with ❤️ for modern banking experiences** 