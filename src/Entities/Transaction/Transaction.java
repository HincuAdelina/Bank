package Entities.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Transaction {
    protected String date;
    protected double amount;

    public Transaction(double amount) {
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate() {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public abstract void executeTransaction();

    abstract public @Override
    String toString();

    abstract public String toCSV();
}
