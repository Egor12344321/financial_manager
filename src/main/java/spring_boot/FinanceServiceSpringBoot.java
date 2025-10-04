package spring_boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class FinanceServiceSpringBoot {
    Scanner scanner = new Scanner(System.in);
    @Autowired
    TransactionRepository transactionRepository;

    public void editTransaction() {
        System.out.println("РЕДАКТИРОВАНИЕ ТРАНЗАКЦИИ");
        showTransaction();
        System.out.println("Выбери транзакцию для редактирования: ");
        Long num_id = scanner.nextLong();
        scanner.nextLine();
        Optional<org.example.model.Transaction> transactionOptional = transactionRepository.findById(num_id);
        if (transactionOptional.isPresent()) {
            org.example.model.Transaction transaction = transactionOptional.get();

            System.out.println("1. Сумма: " + transaction.getAmount());
            System.out.println("2. Тип: " + transaction.getType());
            System.out.println("3. Категория: " + transaction.getCategory());
            System.out.println("4. Описание: " + transaction.getDescription());
            System.out.println("5. Отмена");
            System.out.println("6. Сохранить и выйти");
            System.out.println("Выбери параметр для редактирования");
            int num = scanner.nextInt();
            scanner.nextLine();
            switch (num) {
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
                    transaction.setType(type == 1 ? org.example.model.Transaction.TransactionType.INCOME : org.example.model.Transaction.TransactionType.EXPENSE);
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
                    transactionRepository.save(transaction);
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
    public void showTransaction() {
        List<org.example.model.Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) {
            System.out.println("У вас еще не было транзакций");
        } else {
            System.out.println("Id      |Дата       | Сумма     | Тип     | Категория      | Описание");


            for (org.example.model.Transaction t : transactions) {
                String date = "Нет даты";
                if (t.getDate() != null){
                    try {
                        date = t.getDate().toLocalDate().toString();
                    } catch (Exception e){
                        date = "Ошибка даты";
                    }
                }
                System.out.printf("%-6s  |%s | %-9.2f | %-7s | %-14s | %s\n",
                        t.getId(),
                        date,
                        t.getAmount(),
                        t.getType(),
                        t.getCategory(),
                        t.getDescription() != null ? t.getDescription() : ""
                );
            }
        }
    }
    public void showBalance(){
        System.out.print("Текущий баланс: ");
        System.out.println(transactionRepository.getBalance());
    }
    public void createTransaction(org.example.model.Transaction transaction) {
        transactionRepository.saveAndFlush(transaction);
    }

    public void delTransaction(){
        showTransaction();
        while (true) {
            System.out.println("Введите номер (по полю id) транзакции, которую хотите удалить: ");
            Long id = scanner.nextLong();
            if (transactionRepository.findById(id).isPresent()) {
                transactionRepository.deleteById(id);
                System.out.println("Транзакция удалена");
                return;
            } else {
                System.out.println("Такой транзакции не существует, выберите другую");
            }
        }
    }
    public void categoryStats(){
        System.out.println("Категория     Сумма");
        List<Object[]> stats = transactionRepository.getCategoryStats();
        if (stats.isEmpty()){
            System.out.println("У вас не было трат");
        } else {
            System.out.println("Категория     | Сумма");
            for (Object[] stat : stats) {
                String category = (String) stat[0];
                BigDecimal amount = (BigDecimal) stat[1];
                System.out.printf("%-13s | %-10.2f\n", category, amount);
            }
        }
    }
    public void showTransByDate() throws DateTimeParseException {
        System.out.print("Введите дату начала совершения транзакций (гггг-мм-дд): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());

        System.out.print("Введите дату конца совершения транзакций (гггг-мм-дд): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());
        scanner.nextLine();
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        List<org.example.model.Transaction> transactions = transactionRepository.findByDateRange(startDateTime, endDateTime);
        if (transactions.isEmpty()){
            System.out.println("У вас не было транзакций за указанный период");
        } else {
            System.out.println("Транзакции с " + start + " по " + end + ":");
            BigDecimal summa = BigDecimal.ZERO;
            for (org.example.model.Transaction t : transactions) {
                System.out.println(t.getDate().toLocalDate() + " - " + t.getAmount() + " - " + t.getCategory());
                summa = summa.add(t.getAmount());
            }
            System.out.println(summa);
        }
    }


}




