package Entities.Account;

import Entities.Transaction.Transaction;

import java.util.ArrayList;
import java.util.Date;

public class Account {
    private String accountId;
    private Date dateOfCreation;
    private Double balance;
    private ArrayList<Transaction> transactions;

    public Account(String accountId) {
        this.accountId = accountId;
        this.dateOfCreation = new Date();
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public ArrayList<Transaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void retrieve(double amount, Transaction transaction) {
        this.balance -= amount;
        transactions.add(transaction);
    }

    public void add(double amount, Transaction transaction) {
        this.balance += amount;
        transactions.add(transaction);
    }

    public @Override
    String toString() {
        return "Account Id: " + accountId + "\nDate of creation: " + dateOfCreation + "\nBalance: " + balance;
    }

    public String toCSV() {
        return accountId + ',' + dateOfCreation + ',' + balance;
    }
}
