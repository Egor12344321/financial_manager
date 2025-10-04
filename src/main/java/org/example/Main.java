package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import org.example.Service.FinanceService;
import org.example.model.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FinanceService service = new FinanceService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1 -> service.showBalance();
                    case 2 -> addNewTransaction(service, scanner);
                    case 3 -> service.showTransactions();
                    case 4 -> {
                        service.showTransactions();
                        service.editTransaction();
                    }
                    case 5 -> service.delTransaction();
                    case 6 -> service.showTransByDate();
                    case 7 -> service.showCategoryStats();
                    case 8 -> {
                        System.out.println("Выход");
                        return;
                    }
                    default -> System.out.println("Неверный выбор");
                }
            } catch (SQLException e) {
                System.out.println("Ошибка БД: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("1. Показать текущий баланс");
        System.out.println("2. Добавить операцию");
        System.out.println("3. Показать историю операций");
        System.out.println("4. Редактировать транзакцию");
        System.out.println("5. Удалить транзакцию");
        System.out.println("6. Показать транзакции за определенный период");
        System.out.println("7. Показать сумму по категориям");
        System.out.println("8. Выход");
        System.out.print("Выберите действие: ");
    }

    private static void addNewTransaction(FinanceService service, Scanner scanner) throws SQLException {
        System.out.print("Введите сумму: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();

        System.out.print("Тип (1 - Доход, 2 - Расход): ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Категории: 1-Продукты 2-Транспорт 3-Кафе 4-Зарплата");
        System.out.print("Выберите категорию: ");
        int categoryChoice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Описание (не обязательно): ");
        String description = scanner.nextLine();

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(typeChoice == 1 ? Transaction.TransactionType.INCOME : Transaction.TransactionType.EXPENSE);
        transaction.setCategory(getCategoryName(categoryChoice));
        transaction.setDescription(description);
        transaction.setDate(LocalDateTime.now());
        service.addTransaction(transaction);

        System.out.println("Операция добавлена");
    }

    private static String getCategoryName(int choice) {
        return switch (choice) {
            case 1 -> "Продукты";
            case 2 -> "Транспорт";
            case 3 -> "Кафе";
            case 4 -> "Зарплата";
            default -> "Другое";
        };
    }
}