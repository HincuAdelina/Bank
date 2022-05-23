package Entities.Card;

public class DebitCard extends Card {

    public DebitCard(String accountId, String number, int cvv) {
        super(accountId, number, cvv);
    }

    public @Override
    String toString() {
        return "Card Id: " + cardId + "\nAccount Id: " + accountId + "\nNumber: " + number + "\nCvv: " + cvv + "\nExpiration Date: " + expirationDate;
    }

    public @Override
    String toCSV() {
        return cardId + ',' + accountId + number + ',' + cvv + ',' + expirationDate;
    }
}
