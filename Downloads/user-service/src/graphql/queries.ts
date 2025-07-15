import { gql } from '@apollo/client';

// User Queries
export const GET_USERS = gql`
  query GetUsers {
    users {
      id
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }
`;

export const GET_USER = gql`
  query GetUser($id: ID!) {
    user(id: $id) {
      id
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }
`;

export const GET_USER_BY_EMAIL = gql`
  query GetUserByEmail($email: String!) {
    userByEmail(email: $email) {
      id
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }
`;

// Account Queries
export const GET_ACCOUNTS = gql`
  query GetAccounts {
    accounts {
      id
      userId
      accountNumber
      accountType
      balance
      currency
      isActive
      createdAt
      updatedAt
    }
  }
`;

export const GET_ACCOUNT = gql`
  query GetAccount($id: ID!) {
    account(id: $id) {
      id
      userId
      accountNumber
      accountType
      balance
      currency
      isActive
      createdAt
      updatedAt
    }
  }
`;

export const GET_ACCOUNTS_BY_USER = gql`
  query GetAccountsByUser($userId: ID!) {
    accountsByUser(userId: $userId) {
      id
      userId
      accountNumber
      accountType
      balance
      currency
      isActive
      createdAt
      updatedAt
    }
  }
`;

// Transaction Queries
export const GET_TRANSACTIONS = gql`
  query GetTransactions {
    transactions {
      id
      fromAccountId
      toAccountId
      amount
      transactionType
      status
      description
      createdAt
      updatedAt
    }
  }
`;

export const GET_TRANSACTION = gql`
  query GetTransaction($id: ID!) {
    transaction(id: $id) {
      id
      fromAccountId
      toAccountId
      amount
      transactionType
      status
      description
      createdAt
      updatedAt
    }
  }
`;

export const GET_TRANSACTIONS_BY_ACCOUNT = gql`
  query GetTransactionsByAccount($accountId: ID!) {
    transactionsByAccount(accountId: $accountId) {
      id
      fromAccountId
      toAccountId
      amount
      transactionType
      status
      description
      createdAt
      updatedAt
    }
  }
`; 