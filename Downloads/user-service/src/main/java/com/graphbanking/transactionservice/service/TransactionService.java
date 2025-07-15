package com.graphbanking.transactionservice.service;

import com.graphbanking.transactionservice.model.Transaction;
import com.graphbanking.transactionservice.model.TransactionStatus;
import com.graphbanking.transactionservice.model.TransactionType;
import com.graphbanking.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    public Optional<Transaction> getTransactionByReference(String reference) {
        return transactionRepository.findByTransactionReference(reference);
    }
    
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    public List<Transaction> getTransactionsByUserIdAndStatus(Long userId, TransactionStatus status) {
        return transactionRepository.findByUserIdAndTransactionStatus(userId, status);
    }
    
    public List<Transaction> getTransactionsByUserIdAndType(Long userId, TransactionType type) {
        return transactionRepository.findByUserIdAndTransactionType(userId, type);
    }
    
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);
    }
    
    public List<Transaction> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByTransactionStatus(status);
    }
    
    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByTransactionType(type);
    }
    
    public List<Transaction> getTransactionsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }
    
    public List<Transaction> getTransactionsByAccountAndDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByAccountIdAndDateRange(accountId, startDate, endDate);
    }
    
    public Transaction createTransaction(Long userId, TransactionType type, BigDecimal amount, String currency, String description) {
        String reference = generateTransactionReference();
        while (transactionRepository.existsByTransactionReference(reference)) {
            reference = generateTransactionReference();
        }
        
        Transaction transaction = new Transaction(reference, userId, type, amount);
        if (currency != null && !currency.isEmpty()) {
            transaction.setCurrency(currency);
        }
        if (description != null && !description.isEmpty()) {
            transaction.setDescription(description);
        }
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction createTransfer(Long userId, Long fromAccountId, Long toAccountId, BigDecimal amount, String currency, String description) {
        if (fromAccountId.equals(toAccountId)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        
        String reference = generateTransactionReference();
        while (transactionRepository.existsByTransactionReference(reference)) {
            reference = generateTransactionReference();
        }
        
        Transaction transaction = new Transaction(reference, userId, TransactionType.TRANSFER, amount);
        transaction.setFromAccountId(fromAccountId);
        transaction.setToAccountId(toAccountId);
        
        if (currency != null && !currency.isEmpty()) {
            transaction.setCurrency(currency);
        }
        if (description != null && !description.isEmpty()) {
            transaction.setDescription(description);
        }
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction updateTransactionStatus(Long transactionId, TransactionStatus status, String failureReason) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        
        transaction.setTransactionStatus(status);
        if (failureReason != null && !failureReason.isEmpty()) {
            transaction.setFailureReason(failureReason);
        }
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction completeTransaction(Long transactionId) {
        return updateTransactionStatus(transactionId, TransactionStatus.COMPLETED, null);
    }
    
    public Transaction failTransaction(Long transactionId, String failureReason) {
        return updateTransactionStatus(transactionId, TransactionStatus.FAILED, failureReason);
    }
    
    public Transaction cancelTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        
        if (transaction.getTransactionStatus() == TransactionStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed transaction");
        }
        
        return updateTransactionStatus(transactionId, TransactionStatus.CANCELLED, null);
    }
    
    public void deleteTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        
        if (transaction.getTransactionStatus() == TransactionStatus.COMPLETED) {
            throw new RuntimeException("Cannot delete a completed transaction");
        }
        
        transactionRepository.deleteById(transactionId);
    }
    
    public long getTransactionCount(Long userId, TransactionStatus status) {
        return transactionRepository.countByUserIdAndStatus(userId, status);
    }
    
    public BigDecimal getTotalAmountByUserAndType(Long userId, TransactionType type) {
        BigDecimal total = transactionRepository.getTotalAmountByUserIdAndType(userId, type);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalDebitsByAccount(Long accountId) {
        BigDecimal total = transactionRepository.getTotalDebitsByAccountId(accountId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalCreditsByAccount(Long accountId) {
        BigDecimal total = transactionRepository.getTotalCreditsByAccountId(accountId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public List<Transaction> getStaleTransactions(int hoursOld) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hoursOld);
        return transactionRepository.findStaleTransactions(cutoffTime);
    }
    
    // Process pending transactions (would typically be called by a scheduler)
    public void processPendingTransactions() {
        List<Transaction> pendingTransactions = transactionRepository.findByTransactionStatus(TransactionStatus.PENDING);
        
        for (Transaction transaction : pendingTransactions) {
            try {
                // Update status to processing
                transaction.setTransactionStatus(TransactionStatus.PROCESSING);
                transactionRepository.save(transaction);
                
                // Here you would integrate with account service to perform the actual transfer
                // For now, we'll just mark it as completed
                // TODO: Integrate with account service
                
                // Simulate processing time
                Thread.sleep(100);
                
                // Mark as completed
                transaction.setTransactionStatus(TransactionStatus.COMPLETED);
                transactionRepository.save(transaction);
                
            } catch (Exception e) {
                // Mark as failed
                transaction.setTransactionStatus(TransactionStatus.FAILED);
                transaction.setFailureReason("Processing failed: " + e.getMessage());
                transactionRepository.save(transaction);
            }
        }
    }
    
    private String generateTransactionReference() {
        // Generate a unique transaction reference
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 