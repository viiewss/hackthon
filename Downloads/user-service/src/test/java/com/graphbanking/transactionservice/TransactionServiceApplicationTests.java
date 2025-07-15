package com.graphbanking.transactionservice;

import com.graphbanking.transactionservice.model.Transaction;
import com.graphbanking.transactionservice.model.TransactionStatus;
import com.graphbanking.transactionservice.model.TransactionType;
import com.graphbanking.transactionservice.repository.TransactionRepository;
import com.graphbanking.transactionservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionServiceApplicationTests {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void contextLoads() {
        assertNotNull(transactionService);
        assertNotNull(transactionRepository);
    }

    @Test
    void testCreateTransaction() {
        // Test transaction creation
        Transaction transaction = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Test deposit"
        );
        
        assertNotNull(transaction);
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getTransactionReference());
        assertTrue(transaction.getTransactionReference().startsWith("TXN-"));
        assertEquals(Long.valueOf(1), transaction.getUserId());
        assertEquals(TransactionType.DEPOSIT, transaction.getTransactionType());
        assertEquals(TransactionStatus.PENDING, transaction.getTransactionStatus());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals("USD", transaction.getCurrency());
        assertEquals("Test deposit", transaction.getDescription());
        assertNotNull(transaction.getCreatedAt());
        assertNotNull(transaction.getUpdatedAt());
    }

    @Test
    void testCreateTransfer() {
        // Test transfer creation
        Transaction transfer = transactionService.createTransfer(
            1L, 1L, 2L, new BigDecimal("50.00"), "USD", "Transfer to account 2"
        );
        
        assertNotNull(transfer);
        assertEquals(TransactionType.TRANSFER, transfer.getTransactionType());
        assertEquals(Long.valueOf(1), transfer.getFromAccountId());
        assertEquals(Long.valueOf(2), transfer.getToAccountId());
        assertEquals(new BigDecimal("50.00"), transfer.getAmount());
        assertEquals("Transfer to account 2", transfer.getDescription());
    }

    @Test
    void testCreateTransferSameAccount() {
        // Test transfer to same account should fail
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransfer(1L, 1L, 1L, new BigDecimal("50.00"), "USD", "Invalid transfer");
        });
    }

    @Test
    void testGetTransactionsByUserId() {
        // Create multiple transactions for the same user
        Transaction deposit = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Deposit 1"
        );
        Transaction withdrawal = transactionService.createTransaction(
            1L, TransactionType.WITHDRAWAL, new BigDecimal("25.00"), "USD", "Withdrawal 1"
        );
        
        List<Transaction> transactions = transactionService.getTransactionsByUserId(1L);
        
        assertEquals(2, transactions.size());
        assertTrue(transactions.contains(deposit));
        assertTrue(transactions.contains(withdrawal));
    }

    @Test
    void testGetTransactionsByUserIdAndStatus() {
        // Create transactions with different statuses
        Transaction pending = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Pending deposit"
        );
        Transaction completed = transactionService.createTransaction(
            1L, TransactionType.WITHDRAWAL, new BigDecimal("25.00"), "USD", "Completed withdrawal"
        );
        transactionService.completeTransaction(completed.getId());
        
        List<Transaction> pendingTransactions = transactionService.getTransactionsByUserIdAndStatus(1L, TransactionStatus.PENDING);
        List<Transaction> completedTransactions = transactionService.getTransactionsByUserIdAndStatus(1L, TransactionStatus.COMPLETED);
        
        assertEquals(1, pendingTransactions.size());
        assertEquals(1, completedTransactions.size());
        assertTrue(pendingTransactions.contains(pending));
        assertFalse(pendingTransactions.contains(completed));
    }

    @Test
    void testGetTransactionsByUserIdAndType() {
        // Create transactions with different types
        Transaction deposit = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Deposit"
        );
        Transaction withdrawal = transactionService.createTransaction(
            1L, TransactionType.WITHDRAWAL, new BigDecimal("25.00"), "USD", "Withdrawal"
        );
        
        List<Transaction> deposits = transactionService.getTransactionsByUserIdAndType(1L, TransactionType.DEPOSIT);
        List<Transaction> withdrawals = transactionService.getTransactionsByUserIdAndType(1L, TransactionType.WITHDRAWAL);
        
        assertEquals(1, deposits.size());
        assertEquals(1, withdrawals.size());
        assertTrue(deposits.contains(deposit));
        assertTrue(withdrawals.contains(withdrawal));
    }

    @Test
    void testUpdateTransactionStatus() {
        Transaction transaction = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Test deposit"
        );
        assertEquals(TransactionStatus.PENDING, transaction.getTransactionStatus());
        
        Transaction updated = transactionService.updateTransactionStatus(
            transaction.getId(), TransactionStatus.COMPLETED, null
        );
        
        assertEquals(TransactionStatus.COMPLETED, updated.getTransactionStatus());
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
    }

    @Test
    void testCompleteTransaction() {
        Transaction transaction = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Test deposit"
        );
        
        Transaction completed = transactionService.completeTransaction(transaction.getId());
        
        assertEquals(TransactionStatus.COMPLETED, completed.getTransactionStatus());
        assertNotNull(completed.getProcessedAt());
    }

    @Test
    void testFailTransaction() {
        Transaction transaction = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Test deposit"
        );
        
        String failureReason = "Insufficient funds";
        Transaction failed = transactionService.failTransaction(transaction.getId(), failureReason);
        
        assertEquals(TransactionStatus.FAILED, failed.getTransactionStatus());
        assertEquals(failureReason, failed.getFailureReason());
        assertNotNull(failed.getProcessedAt());
    }

    @Test
    void testCancelTransaction() {
        Transaction transaction = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Test deposit"
        );
        
        Transaction cancelled = transactionService.cancelTransaction(transaction.getId());
        
        assertEquals(TransactionStatus.CANCELLED, cancelled.getTransactionStatus());
    }

    @Test
    void testCancelCompletedTransaction() {
        Transaction transaction = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Test deposit"
        );
        transactionService.completeTransaction(transaction.getId());
        
        // Should not be able to cancel a completed transaction
        assertThrows(RuntimeException.class, () -> {
            transactionService.cancelTransaction(transaction.getId());
        });
    }

    @Test
    void testDeleteTransaction() {
        Transaction transaction = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Test deposit"
        );
        Long transactionId = transaction.getId();
        
        assertTrue(transactionService.getTransactionById(transactionId).isPresent());
        
        transactionService.deleteTransaction(transactionId);
        
        assertFalse(transactionService.getTransactionById(transactionId).isPresent());
    }

    @Test
    void testDeleteCompletedTransaction() {
        Transaction transaction = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Test deposit"
        );
        transactionService.completeTransaction(transaction.getId());
        
        // Should not be able to delete a completed transaction
        assertThrows(RuntimeException.class, () -> {
            transactionService.deleteTransaction(transaction.getId());
        });
    }

    @Test
    void testGetTransactionCount() {
        // Create transactions with different statuses
        Transaction pending = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Pending"
        );
        Transaction completed = transactionService.createTransaction(
            1L, TransactionType.WITHDRAWAL, new BigDecimal("25.00"), "USD", "Completed"
        );
        transactionService.completeTransaction(completed.getId());
        
        assertEquals(1, transactionService.getTransactionCount(1L, TransactionStatus.PENDING));
        assertEquals(1, transactionService.getTransactionCount(1L, TransactionStatus.COMPLETED));
        assertEquals(0, transactionService.getTransactionCount(1L, TransactionStatus.FAILED));
    }

    @Test
    void testGetTotalAmountByUserAndType() {
        // Create multiple transactions of the same type
        transactionService.createTransaction(1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Deposit 1");
        transactionService.createTransaction(1L, TransactionType.DEPOSIT, new BigDecimal("50.00"), "USD", "Deposit 2");
        transactionService.createTransaction(1L, TransactionType.WITHDRAWAL, new BigDecimal("25.00"), "USD", "Withdrawal 1");
        
        // Complete all transactions
        List<Transaction> userTransactions = transactionService.getTransactionsByUserId(1L);
        for (Transaction t : userTransactions) {
            transactionService.completeTransaction(t.getId());
        }
        
        BigDecimal totalDeposits = transactionService.getTotalAmountByUserAndType(1L, TransactionType.DEPOSIT);
        BigDecimal totalWithdrawals = transactionService.getTotalAmountByUserAndType(1L, TransactionType.WITHDRAWAL);
        
        assertEquals(new BigDecimal("150.00"), totalDeposits);
        assertEquals(new BigDecimal("25.00"), totalWithdrawals);
    }

    @Test
    void testUniqueTransactionReferences() {
        Transaction transaction1 = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Transaction 1"
        );
        Transaction transaction2 = transactionService.createTransaction(
            2L, TransactionType.WITHDRAWAL, new BigDecimal("50.00"), "USD", "Transaction 2"
        );
        
        assertNotEquals(transaction1.getTransactionReference(), transaction2.getTransactionReference());
    }

    @Test
    void testGetTransactionsByDateRange() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);
        
        Transaction transaction = transactionService.createTransaction(
            1L, TransactionType.DEPOSIT, new BigDecimal("100.00"), "USD", "Test transaction"
        );
        
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(1L, yesterday, tomorrow);
        
        assertEquals(1, transactions.size());
        assertTrue(transactions.contains(transaction));
    }
} 