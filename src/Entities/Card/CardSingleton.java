package Entities.Card;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CardSingleton {
    private static CardSingleton instance = null;
    HashMap<String, ArrayList<Card>> cards = new HashMap<>();

    public static CardSingleton getInstance() {
        if (instance == null)
            instance = new CardSingleton();
        return instance;
    }

    public HashMap<String, ArrayList<Card>> getCards() {
        return this.cards;
    }

    public void setCards(HashMap<String, ArrayList<Card>> cards) {
        this.cards = cards;
    }

    public void parseCSVFile(String filePath) {
        List<List<String>> lines = new ArrayList<>();
        ArrayList<Card> currentCards;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                lines.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (List<String> line : lines) {
            Card card = null;
            String clientId = line.get(0);
            String accountId = line.get(1);
            String number = line.get(2);
            String cvv = line.get(3);

            if (cards.containsKey(clientId)) {
                currentCards = cards.get(clientId);
            } else {
                currentCards = new ArrayList<>();
            }

            if (line.size() == 4) {
                // debit card
                card = new DebitCard(accountId, number, Integer.valueOf(cvv));
            } else if (line.size() == 7) {
                // credit card
                String creditLimit = line.get(4);
                String interest = line.get(5);
                String accumulatedDebt = line.get(6);
                card = new CreditCard(accountId, number, Integer.valueOf(cvv), Double.parseDouble(creditLimit), Double.parseDouble(interest), Double.parseDouble(accumulatedDebt));
            }
            currentCards.add(card);
            cards.put(clientId, currentCards);
        }
    }

    public void writeCSVFile(ArrayList<Card> csvCards, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Card csvCard : csvCards) {
                writer.write(csvCard.toCSV());
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
