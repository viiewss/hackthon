import { ApolloClient, InMemoryCache, createHttpLink, from } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';
import { onError } from '@apollo/client/link/error';

// Create HTTP link to API Gateway
const httpLink = createHttpLink({
  uri: 'http://localhost:8080/graphql',
});

// Auth link to add JWT token to headers
const authLink = setContext((_, { headers }) => {
  const token = localStorage.getItem('token');
  return {
    headers: {
      ...headers,
      authorization: token ? `Bearer ${token}` : '',
    },
  };
});

// Error handling link
const errorLink = onError(({ graphQLErrors, networkError, operation, forward }) => {
  if (graphQLErrors) {
    graphQLErrors.forEach(({ message, locations, path }) => {
      console.error(`GraphQL error: Message: ${message}, Location: ${locations}, Path: ${path}`);
    });
  }

  if (networkError) {
    console.error(`Network error: ${networkError}`);
    // Handle authentication errors
    if (networkError.statusCode === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
  }
});

// Create Apollo Client
export const apolloClient = new ApolloClient({
  link: from([errorLink, authLink, httpLink]),
  cache: new InMemoryCache(),
  defaultOptions: {
    watchQuery: {
      errorPolicy: 'all',
    },
    query: {
      errorPolicy: 'all',
    },
  },
});

// Helper function to set service header for routing
export const createServiceLink = (service: 'user' | 'account' | 'transaction') => {
  return setContext((_, { headers }) => {
    const token = localStorage.getItem('token');
    return {
      headers: {
        ...headers,
        'X-Service': service,
        authorization: token ? `Bearer ${token}` : '',
      },
    };
  });
};

// Create service-specific clients
export const userClient = new ApolloClient({
  link: from([errorLink, createServiceLink('user'), httpLink]),
  cache: new InMemoryCache(),
});

export const accountClient = new ApolloClient({
  link: from([errorLink, createServiceLink('account'), httpLink]),
  cache: new InMemoryCache(),
});

export const transactionClient = new ApolloClient({
  link: from([errorLink, createServiceLink('transaction'), httpLink]),
  cache: new InMemoryCache(),
}); 