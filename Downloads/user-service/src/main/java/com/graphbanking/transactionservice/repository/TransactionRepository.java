package com.graphbanking.transactionservice.repository;

import com.graphbanking.transactionservice.model.Transaction;
import com.graphbanking.transactionservice.model.TransactionStatus;
import com.graphbanking.transactionservice.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionReference(String transactionReference);
    
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findByUserIdAndTransactionStatus(Long userId, TransactionStatus status);
    
    List<Transaction> findByUserIdAndTransactionType(Long userId, TransactionType type);
    
    List<Transaction> findByFromAccountId(Long fromAccountId);
    
    List<Transaction> findByToAccountId(Long toAccountId);
    
    List<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId);
    
    List<Transaction> findByTransactionStatus(TransactionStatus status);
    
    List<Transaction> findByTransactionType(TransactionType type);
    
    boolean existsByTransactionReference(String transactionReference);
    
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                              @Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccountId = :accountId OR t.toAccountId = :accountId) AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId, 
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.transactionStatus = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TransactionStatus status);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.transactionStatus = 'COMPLETED' AND t.transactionType = :type")
    BigDecimal getTotalAmountByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.fromAccountId = :accountId AND t.transactionStatus = 'COMPLETED'")
    BigDecimal getTotalDebitsByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.toAccountId = :accountId AND t.transactionStatus = 'COMPLETED'")
    BigDecimal getTotalCreditsByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT t FROM Transaction t WHERE t.transactionStatus = 'PENDING' AND t.createdAt < :cutoffTime")
    List<Transaction> findStaleTransactions(@Param("cutoffTime") LocalDateTime cutoffTime);
} 