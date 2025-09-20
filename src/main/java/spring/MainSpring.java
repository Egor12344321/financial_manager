package spring;

import org.example.jdbc.dao.Service.FinanceService;
import model.Transaction;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainSpring {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        Scanner scanner = new Scanner(System.in);
        FinanceServiceSpring financeServiceSpring = context.getBean(FinanceServiceSpring.class);
        while (true) {
            try{
                printMenu();

                int choice = scanner.nextInt();
                scanner.nextLine();

                try {
                    switch (choice) {
                        case 1 -> financeServiceSpring.showBalance();
                        case 2 -> addNewTransaction(financeServiceSpring, scanner);
                        case 3 -> financeServiceSpring.showTransactions();
                        case 4 -> {
                            financeServiceSpring.showTransactions();
                            financeServiceSpring.editTransaction();
                        }
                        case 5 -> financeServiceSpring.delTransaction();
                        case 6 -> financeServiceSpring.showTransByDate();
                        case 7 -> {
                            System.out.println("Выход");
                            return;
                        }
                        default -> System.out.println("Неверный выбор");
                    }
                } catch (SQLException e) {
                    System.out.println("Ошибка БД: " + e.getMessage());
                }
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите число от 1 до 7!");
                scanner.nextLine(); // очищаем некорректный ввод
            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
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
        System.out.println("7. Выход");
        System.out.print("Выберите действие: ");
    }

    private static void addNewTransaction(FinanceServiceSpring financeServiceSpring, Scanner scanner) throws SQLException {
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

        financeServiceSpring.addTransaction(transaction);
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

