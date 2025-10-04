package spring;

import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.example.model.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class TransactionDaoHibernate {
    private final SessionFactory sessionFactory;

    @Autowired
    public TransactionDaoHibernate(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public void save(Transaction transaction){
        Session session = sessionFactory.getCurrentSession();
        transaction.setDate(LocalDateTime.now());
        session.persist(transaction);
    }

    public List<Transaction> findAll(){
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Transaction> query = session.createQuery(
                "FROM org.example.model.Transaction ORDER BY date DESC", Transaction.class);
        return query.getResultList();
    }

    public Transaction findById(int id){
        Session session = sessionFactory.getCurrentSession();
        return session.get(Transaction.class, id);
    }

    public Map<String, BigDecimal> getCategoryStats(){
        Session session = sessionFactory.getCurrentSession();
        Query<Object[]> query = session.createQuery(
                "SELECT category, SUM(amount) FROM org.example.model.Transaction WHERE type = :expenseType GROUP BY category",
                Object[].class
        );
        query.setParameter("expenseType", Transaction.TransactionType.EXPENSE);

        List<Object[]> results = query.getResultList();
        System.out.println("DEBUG: Найдено категорий: " + results.size());

        Map<String, BigDecimal> categoryStats = new HashMap<>();

        for (Object[] result : results){
            String category = (String) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            categoryStats.put(category, amount);
        }
        return categoryStats;
    }

    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate){
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Transaction> query = session.createQuery(
                "FROM org.example.model.Transaction WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC",
                Transaction.class
        );
        query.setParameter("startDate", startDate.atStartOfDay());
        query.setParameter("endDate", endDate.atTime(23, 59, 59));
        return query.getResultList();
    }

    public void update(Transaction transaction) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(transaction);
    }

    public void delete(int id) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.get(Transaction.class, id);
        if (transaction != null) {
            session.delete(transaction);
        }
    }

    public BigDecimal getBalance() {
        Session session = sessionFactory.getCurrentSession();

        String sql = "SELECT " +
                "COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0) " +
                "FROM transactions";

        BigDecimal result = (BigDecimal) session.createNativeQuery(sql).getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}