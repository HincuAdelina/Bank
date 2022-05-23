package Entities.Transaction;

import Entities.Account.Account;

public class AddFundsTransaction extends Transaction {
    private Account account;

    public AddFundsTransaction(double amount, Account account) {
        super(amount);
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void executeTransaction() {
        account.add(amount, this);
    }

    public @Override
    String toString() {
        return "Date: " + date + "\nAdded amount: " + amount + "\nTo account with id: " + account.getAccountId();
    }

    public String toCSV() {
        return String.valueOf(date) + ',' + amount + ',' + account.getAccountId();
    }
}
