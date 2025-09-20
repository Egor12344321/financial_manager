package spring;

import model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service
public class FinanceServiceSpring {
    private final TransactionDaoHibernate transactionDaoHibernate;
    Scanner scanner = new Scanner(System.in);
    @Autowired
    public FinanceServiceSpring(TransactionDaoHibernate transactionDaoHibernate) {
        this.transactionDaoHibernate = transactionDaoHibernate;
    }
    public void showBalance(){
        System.out.print("Текущий баланс: ");
        System.out.println(transactionDaoHibernate.getBalance());
    }
    public void addTransaction(Transaction transaction) {
        transactionDaoHibernate.save(transaction);
    }


    public void delTransaction(){
        System.out.println("Введите номер (по полю id) транзакции, которую хотите удалить: ");
        int id = scanner.nextInt();
        transactionDaoHibernate.delete(id);
        System.out.println("Транзакция удалена");
    }
    public void showTransByDate() throws DateTimeParseException {
        System.out.print("Введите дату начала совершения транзакций (гггг-мм-дд): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());

        System.out.print("Введите дату конца совершения транзакций (гггг-мм-дд): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());

        List<Transaction> transactions = transactionDaoHibernate.findByDateRange(start, end);
        if (transactions.isEmpty()){
            System.out.println("У вас не было транзакций за указанный период");
        } else {
            System.out.println("Транзакции с " + start + " по " + end + ":");
            BigDecimal summa = BigDecimal.ZERO;
            for (Transaction t : transactions) {
                System.out.println(t.getDate().toLocalDate() + " - " + t.getAmount() + " - " + t.getCategory());
                summa = summa.add(t.getAmount());
            }
            System.out.println(summa);
        }
    }
    public void editTransaction() {

        System.out.println("Выберите транзакцию для редактирования (по полю Id): ");
        int id_tr = scanner.nextInt();
        scanner.nextLine();
        Transaction transaction = transactionDaoHibernate.findById(id_tr);
        if (transaction == null){
            System.out.println("Такой транзакции не существует");
            return;
        }
        while (true) {
            System.out.println("Редактирование транзакции");
            System.out.println("1. Сумма: " + transaction.getAmount());
            System.out.println("2. Тип: " + transaction.getType());
            System.out.println("3. Категория: " + transaction.getCategory());
            System.out.println("4. Описание: " + transaction.getDescription());
            System.out.println("5. Отмена");
            System.out.println("6. Сохранить и выйти");
            System.out.println("Выбери параметр для редактирования");
            int num = scanner.nextInt();
            scanner.nextLine();
            switch (num){
                case 1 -> {
                    System.out.println("Введите сумму: ");
                    BigDecimal amount = scanner.nextBigDecimal();
                    scanner.nextLine();
                    transaction.setAmount(amount);
                }
                case 2 -> {
                    System.out.println("Введите тип транзакции: (1 - доход, 2 - расход)");
                    int type = scanner.nextInt();
                    scanner.nextLine();
                    transaction.setType(type == 1 ? Transaction.TransactionType.INCOME : Transaction.TransactionType.EXPENSE);
                }
                case 3 -> {
                    System.out.println("Введите категорию: 1-Продукты 2-Транспорт 3-Кафе 4-Зарплата");
                    System.out.println("Новая категория: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    transaction.setCategory(getCategoryName(choice));
                }
                case 4 -> {
                    System.out.println("Введите описание: ");
                    String desc = scanner.nextLine();
                    transaction.setDescription(desc);
                }
                case 5 -> {
                    System.out.println("Отмена редактирования");
                    return;
                }
                case 6 -> {
                    System.out.println("Сохранить изменения и выйти");
                    transactionDaoHibernate.update(transaction);
                    return;
                }
            }
        }
    }

    public String getCategoryName(int choice){
        return switch (choice){
            case 1 -> "Продукты";
            case 2 -> "Транспорт";
            case 3 -> "Кафе";
            case 4 -> "Зарплата";
            default -> "Другое";
        };
    }
    public void showTransactions() throws SQLException {
        List<Transaction> transactions = transactionDaoHibernate.findAll();
        if (transactions.isEmpty()){
            System.out.println("У вас еще не было транзакций");
        } else {
            System.out.println("Id      |Дата       | Сумма     | Тип     | Категория      | Описание");


            for (Transaction t : transactions) {
                System.out.printf("%-6s  |%s | %-9.2f | %-7s | %-14s | %s\n",
                        t.getId(),
                        t.getDate().toLocalDate(),
                        t.getAmount(),
                        t.getType(),
                        t.getCategory(),
                        t.getDescription() != null ? t.getDescription() : ""
                );
            }
        }

}}
