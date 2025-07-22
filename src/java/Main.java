import model.*;
import services.jdbc.AccountService;
import services.jdbc.CustomerService;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        CustomerService customerService = new CustomerService();
        AccountService accountService = new AccountService();

        Customer customer1 = new Customer("John", "john@example.com", "123456789");
        Customer customer2 = new Customer("Bob", "bob@example.com", "987654321");

        customer1 = customerService.createCustomer(customer1);
        customer2 = customerService.createCustomer(customer2);

        Account account1 = new Account(customer1.getId(), "ACC-1001", 0.0);
        Account account2 = new Account(customer2.getId(), "ACC-2002", 0.0);

        account1 = accountService.createAccount(account1);
        account2 = accountService.createAccount(account2);

        accountService.deposit(List.of(
                new DepositParams(account1, 1000.0, "First batch deposit test"),
                new DepositParams(account2, 500.0, "Second batch deposit test")
        ));

        accountService.withdraw(new WithdrawParams(account1, 200.0, "Bill payment"), false);
        accountService.transfer(new TransferParams(account1, account2, 150.0));

        List<TransferParams> batchTransfers = List.of(
                new TransferParams(account2, account1, 50.55),
                new TransferParams(account1, account2, 30.0)
        );
        accountService.transfer(batchTransfers);

        System.out.println("Final balance - Account 1 (" + account1.getAccountNumber() + "): " + account1.getBalance());
        System.out.println("Final balance - Account 2 (" + account2.getAccountNumber() + "): " + account2.getBalance());
    }
}
