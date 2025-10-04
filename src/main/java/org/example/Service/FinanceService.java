package org.example.Service;

import org.example.dao.TransactionDao;
import org.example.model.Transaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class FinanceService {
    private final TransactionDao transactionDao = new TransactionDao();
    Scanner scanner = new Scanner(System.in);
    public void addTransaction(Transaction transaction) throws SQLException {
        transactionDao.save(transaction);
    }
    public void delTransaction() throws SQLException {
        showTransactions();
        while(true){
            System.out.print("Введите номер транзакции, которую хотите удалить: ");
            int del_id = scanner.nextInt();
            scanner.nextLine();
            if (transactionDao.findById(del_id) != null) {
                transactionDao.deleteTransaction(del_id);
                System.out.println("Операция удалена");
                return;
            } else {
                System.out.println("Такой транзакции нет, выберите другую");
            }
        }

    }
    public void editTransaction() throws SQLException {

        System.out.println("Выберите транзакцию для редактирования (по полю Id): ");
        int id_tr = scanner.nextInt();
        scanner.nextLine();
        Transaction transaction = transactionDao.findById(id_tr);
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
                    transactionDao.updateTransaction(transaction);
                    return;
                }
            }
        }
    }

    public void showCategoryStats() throws SQLException {
        System.out.println("Категория      /          Сумма");
        Map<String, BigDecimal> stats = transactionDao.getCategoryStats();
        if (stats.isEmpty()){
            System.out.println("У вас не было трат");
        } else {
            for (Map.Entry<String, BigDecimal> entry : stats.entrySet()) {
                String key = entry.getKey();
                BigDecimal value = entry.getValue();
                System.out.println(key + "         " + value);
            }
        }
    }

    public void showTransByDate() throws SQLException, DateTimeParseException {
        System.out.print("Введите дату начала совершения транзакций (гггг-мм-дд): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());

        System.out.print("Введите дату конца совершения транзакций (гггг-мм-дд): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());

        List<Transaction> transactions = transactionDao.getTransactionsByDate(start, end);
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
    public String getCategoryName(int choice){
        return switch (choice){
            case 1 -> "Продукты";
            case 2 -> "Транспорт";
            case 3 -> "Кафе";
            case 4 -> "Зарплата";
            default -> "Другое";
        };
    }
    public void showBalance() throws SQLException {
        BigDecimal balance = transactionDao.getBalance();
        System.out.printf("Текущий баланс: " + balance + "руб.\n");
    }

    public void showTransactions() throws SQLException {
        List<Transaction> transactions = transactionDao.findAll();
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
    }
}