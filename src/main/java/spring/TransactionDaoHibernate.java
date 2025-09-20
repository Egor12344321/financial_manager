package spring;

import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                "FROM Transaction ORDER BY date DESC", Transaction.class);
        return query.getResultList();
    }

    public Transaction findById(int id){
        Session session = sessionFactory.getCurrentSession();
        return session.get(Transaction.class, id);
    }

    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate){
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Transaction> query = session.createQuery(
                "FROM Transaction WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC",
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
        TypedQuery<BigDecimal> query = session.createQuery(
                "SELECT SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END) FROM Transaction",
                BigDecimal.class
        );
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}

