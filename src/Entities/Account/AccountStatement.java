package Entities.Account;

import Entities.Transaction.Transaction;

import java.util.ArrayList;
import java.util.Date;

public class AccountStatement {
    private Account account;
    private Date date;

    public AccountStatement(Account account) {
        this.account = account;
        this.date = new Date();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public @Override
    String toString() {
        String output = "";
        ArrayList<Transaction> transactions = account.getTransactions();
        for (Transaction transaction : transactions) {
            output = output + transaction + "\n\n";
        }

        return "Date: " + date + "\nAccount info:\n " + account + "\nTransactions:\n " + output;
    }
}
