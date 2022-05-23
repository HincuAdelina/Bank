package Entities.Card;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public abstract class Card {
    protected String cardId;
    protected String accountId;
    protected String number;
    protected int cvv;
    protected Date expirationDate;

    public Card(String accountId, String number, int cvv) {
        this.cardId = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.number = number;
        this.cvv = cvv;
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.YEAR, 3);
        this.expirationDate = c.getTime();
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getAcoountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    abstract public @Override
    String toString();

    abstract public String toCSV();

}


