package Entities.Transaction;

import Entities.Account.Account;

public class ExchangeFundsTransaction extends Transaction {

    private Account senderAccount, receiverAccount;

    public ExchangeFundsTransaction(double amount, Account senderAccount, Account receiverAccount) {
        super(amount);
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
    }

    public Account getSenderAccount() {
        return this.senderAccount;
    }

    public void setSenderAccount(Account senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Account getReceiverAccount() {
        return this.receiverAccount;
    }

    public void setReceiverAccount(Account receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public void executeTransaction() {
        senderAccount.retrieve(amount, this);
        receiverAccount.add(amount, this);
    }

    public @Override
    String toString() {
        return "Date: " + date + "\nExchanged amount: " + amount + "\n From account with id: " + senderAccount.getAccountId() + " to account with id: " + receiverAccount.getAccountId();
    }

    public String toCSV() {
        return String.valueOf(date) + ',' + amount + ',' + senderAccount.getAccountId() + ',' + receiverAccount.getAccountId();
    }
}
