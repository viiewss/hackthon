package com.graphbanking.transactionservice.resolver;

import com.graphbanking.transactionservice.model.Transaction;
import com.graphbanking.transactionservice.model.TransactionStatus;
import com.graphbanking.transactionservice.model.TransactionType;
import com.graphbanking.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
public class TransactionResolver {
    
    @Autowired
    private TransactionService transactionService;
    
    // Query Mappings
    @QueryMapping
    public List<Transaction> transactions() {
        return transactionService.getAllTransactions();
    }
    
    @QueryMapping
    public Transaction transaction(@Argument Long id) {
        return transactionService.getTransactionById(id).orElse(null);
    }
    
    @QueryMapping
    public Transaction transactionByReference(@Argument String reference) {
        return transactionService.getTransactionByReference(reference).orElse(null);
    }
    
    @QueryMapping
    public List<Transaction> transactionsByUserId(@Argument Long userId) {
        return transactionService.getTransactionsByUserId(userId);
    }
    
    @QueryMapping
    public List<Transaction> transactionsByUserIdAndStatus(@Argument Long userId, @Argument TransactionStatus status) {
        return transactionService.getTransactionsByUserIdAndStatus(userId, status);
    }
    
    @QueryMapping
    public List<Transaction> transactionsByUserIdAndType(@Argument Long userId, @Argument TransactionType type) {
        return transactionService.getTransactionsByUserIdAndType(userId, type);
    }
    
    @QueryMapping
    public List<Transaction> transactionsByAccountId(@Argument Long accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }
    
    @QueryMapping
    public List<Transaction> transactionsByStatus(@Argument TransactionStatus status) {
        return transactionService.getTransactionsByStatus(status);
    }
    
    @QueryMapping
    public List<Transaction> transactionsByType(@Argument TransactionType type) {
        return transactionService.getTransactionsByType(type);
    }
    
    @QueryMapping
    public List<Transaction> transactionsByDateRange(@Argument Long userId, @Argument String startDate, @Argument String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);
        return transactionService.getTransactionsByDateRange(userId, start, end);
    }
    
    @QueryMapping
    public List<Transaction> transactionsByAccountAndDateRange(@Argument Long accountId, @Argument String startDate, @Argument String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);
        return transactionService.getTransactionsByAccountAndDateRange(accountId, start, end);
    }
    
    @QueryMapping
    public UserTransactionSummary userTransactionSummary(@Argument Long userId) {
        List<Transaction> allTransactions = transactionService.getTransactionsByUserId(userId);
        long completedCount = transactionService.getTransactionCount(userId, TransactionStatus.COMPLETED);
        long pendingCount = transactionService.getTransactionCount(userId, TransactionStatus.PENDING);
        long failedCount = transactionService.getTransactionCount(userId, TransactionStatus.FAILED);
        
        BigDecimal totalDeposits = transactionService.getTotalAmountByUserAndType(userId, TransactionType.DEPOSIT);
        BigDecimal totalWithdrawals = transactionService.getTotalAmountByUserAndType(userId, TransactionType.WITHDRAWAL);
        BigDecimal totalTransfers = transactionService.getTotalAmountByUserAndType(userId, TransactionType.TRANSFER);
        
        return new UserTransactionSummary(userId, completedCount, pendingCount, failedCount, 
                                        totalDeposits, totalWithdrawals, totalTransfers, allTransactions);
    }
    
    @QueryMapping
    public List<Transaction> staleTransactions(@Argument int hoursOld) {
        return transactionService.getStaleTransactions(hoursOld);
    }
    
    // Mutation Mappings
    @MutationMapping
    public Transaction createTransaction(@Argument Map<String, Object> input) {
        Long userId = Long.valueOf(input.get("userId").toString());
        TransactionType type = TransactionType.valueOf(input.get("type").toString());
        BigDecimal amount = new BigDecimal(input.get("amount").toString());
        String currency = (String) input.get("currency");
        String description = (String) input.get("description");
        
        return transactionService.createTransaction(userId, type, amount, currency, description);
    }
    
    @MutationMapping
    public Transaction createTransfer(@Argument Map<String, Object> input) {
        Long userId = Long.valueOf(input.get("userId").toString());
        Long fromAccountId = Long.valueOf(input.get("fromAccountId").toString());
        Long toAccountId = Long.valueOf(input.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(input.get("amount").toString());
        String currency = (String) input.get("currency");
        String description = (String) input.get("description");
        
        return transactionService.createTransfer(userId, fromAccountId, toAccountId, amount, currency, description);
    }
    
    @MutationMapping
    public Transaction updateTransactionStatus(@Argument Map<String, Object> input) {
        Long transactionId = Long.valueOf(input.get("transactionId").toString());
        TransactionStatus status = TransactionStatus.valueOf(input.get("status").toString());
        String failureReason = (String) input.get("failureReason");
        
        return transactionService.updateTransactionStatus(transactionId, status, failureReason);
    }
    
    @MutationMapping
    public Transaction completeTransaction(@Argument Long transactionId) {
        return transactionService.completeTransaction(transactionId);
    }
    
    @MutationMapping
    public Transaction failTransaction(@Argument Long transactionId, @Argument String failureReason) {
        return transactionService.failTransaction(transactionId, failureReason);
    }
    
    @MutationMapping
    public Transaction cancelTransaction(@Argument Long transactionId) {
        return transactionService.cancelTransaction(transactionId);
    }
    
    @MutationMapping
    public Boolean deleteTransaction(@Argument Long transactionId) {
        try {
            transactionService.deleteTransaction(transactionId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @MutationMapping
    public Boolean processPendingTransactions() {
        try {
            transactionService.processPendingTransactions();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Helper class for UserTransactionSummary
    public static class UserTransactionSummary {
        private Long userId;
        private long completedCount;
        private long pendingCount;
        private long failedCount;
        private BigDecimal totalDeposits;
        private BigDecimal totalWithdrawals;
        private BigDecimal totalTransfers;
        private List<Transaction> transactions;
        
        public UserTransactionSummary(Long userId, long completedCount, long pendingCount, long failedCount,
                                    BigDecimal totalDeposits, BigDecimal totalWithdrawals, BigDecimal totalTransfers,
                                    List<Transaction> transactions) {
            this.userId = userId;
            this.completedCount = completedCount;
            this.pendingCount = pendingCount;
            this.failedCount = failedCount;
            this.totalDeposits = totalDeposits;
            this.totalWithdrawals = totalWithdrawals;
            this.totalTransfers = totalTransfers;
            this.transactions = transactions;
        }
        
        // Getters
        public Long getUserId() { return userId; }
        public long getCompletedCount() { return completedCount; }
        public long getPendingCount() { return pendingCount; }
        public long getFailedCount() { return failedCount; }
        public BigDecimal getTotalDeposits() { return totalDeposits; }
        public BigDecimal getTotalWithdrawals() { return totalWithdrawals; }
        public BigDecimal getTotalTransfers() { return totalTransfers; }
        public List<Transaction> getTransactions() { return transactions; }
    }
} 