package Entities.Card;

public class CreditCard extends Card {
    double creditLimit;
    double interest;
    double accumulatedDebt;

    public CreditCard(String accountId, String number, int cvv, double limit, double creditLimit, double interest) {
        super(accountId, number, cvv);
        this.creditLimit = creditLimit;
        this.interest = interest;
        this.accumulatedDebt = 0.0;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public double getInterest() {
        return interest;
    }

    public double getaccumulatedDebt() {
        return accumulatedDebt;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }


    public void setaccumulatedDebt(double accumulatedDebt) {
        this.accumulatedDebt = accumulatedDebt;
    }

    public @Override
    String toString() {
        return "Card Id: " + cardId + "\nAccount Id: " + accountId + "\nNumber: " + number + "\nCvv: " + cvv + "\nExpiration Date: " + expirationDate
                + "\nCredit limit: " + creditLimit + "\nInterest: " + interest + "\nAccumulated Debt: " + accumulatedDebt;
    }

    public String toCSV() {
        return cardId + "," + accountId + ',' + number + ',' + cvv + ',' + expirationDate
                + ',' + creditLimit + ',' + interest + ',' + accumulatedDebt;
    }
}
