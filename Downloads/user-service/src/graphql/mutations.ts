import { gql } from '@apollo/client';

// User Mutations
export const CREATE_USER = gql`
  mutation CreateUser($input: CreateUserInput!) {
    createUser(input: $input) {
      id
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }
`;

export const UPDATE_USER = gql`
  mutation UpdateUser($id: ID!, $input: UpdateUserInput!) {
    updateUser(id: $id, input: $input) {
      id
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }
`;

export const DELETE_USER = gql`
  mutation DeleteUser($id: ID!) {
    deleteUser(id: $id)
  }
`;

// Account Mutations
export const CREATE_ACCOUNT = gql`
  mutation CreateAccount($input: CreateAccountInput!) {
    createAccount(input: $input) {
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

export const UPDATE_ACCOUNT = gql`
  mutation UpdateAccount($id: ID!, $input: UpdateAccountInput!) {
    updateAccount(id: $id, input: $input) {
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

export const DELETE_ACCOUNT = gql`
  mutation DeleteAccount($id: ID!) {
    deleteAccount(id: $id)
  }
`;

export const DEPOSIT = gql`
  mutation Deposit($accountId: ID!, $amount: Float!) {
    deposit(accountId: $accountId, amount: $amount) {
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

export const WITHDRAW = gql`
  mutation Withdraw($accountId: ID!, $amount: Float!) {
    withdraw(accountId: $accountId, amount: $amount) {
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

export const TRANSFER = gql`
  mutation Transfer($fromAccountId: ID!, $toAccountId: ID!, $amount: Float!) {
    transfer(fromAccountId: $fromAccountId, toAccountId: $toAccountId, amount: $amount) {
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

// Transaction Mutations
export const CREATE_TRANSACTION = gql`
  mutation CreateTransaction($input: CreateTransactionInput!) {
    createTransaction(input: $input) {
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

export const UPDATE_TRANSACTION = gql`
  mutation UpdateTransaction($id: ID!, $input: UpdateTransactionInput!) {
    updateTransaction(id: $id, input: $input) {
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

export const DELETE_TRANSACTION = gql`
  mutation DeleteTransaction($id: ID!) {
    deleteTransaction(id: $id)
  }
`;

export const PROCESS_TRANSACTION = gql`
  mutation ProcessTransaction($id: ID!) {
    processTransaction(id: $id) {
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