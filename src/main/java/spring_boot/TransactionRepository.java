package spring_boot;


import org.example.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.date >= :start AND t.date <= :end")
    List<Transaction> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT category, SUM(amount) FROM transactions WHERE type = 'EXPENSE' GROUP BY category", nativeQuery = true)
    List<Object[]> getCategoryStats();

    @Query("SELECT COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END), 0) " +
            "FROM Transaction t")
    BigDecimal getBalance();
}
