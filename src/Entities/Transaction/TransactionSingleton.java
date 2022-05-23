package Entities.Transaction;

import Entities.Account.Account;
import Entities.Client.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TransactionSingleton {
    private static TransactionSingleton instance = null;
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public static TransactionSingleton getInstance() {
        if (instance == null)
            instance = new TransactionSingleton();
        return instance;
    }

    public ArrayList<Transaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void parseCSVFile(String filePath, HashMap<String, Client> clients) {
        List<List<String>> lines = new ArrayList<>();
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
            String type = line.get(0);
            String clientId = line.get(1);
            String accountId = line.get(2);
            String amount = line.get(3);
            Client client = clients.get(clientId);
            switch (type) {
                case "a":
                    if (client != null) {
                        ArrayList<Account> clientAccounts = client.getAccounts();
                        for (Account account : clientAccounts) {
                            if (account.getAccountId().equals(accountId)) {
                                Transaction transaction = new AddFundsTransaction(Double.parseDouble(amount), account);
                                transaction.executeTransaction();
                                transactions.add(transaction);
                                System.out.println("Succesfully executed add funds transaction." + "Client id: " + clientId + " account id: " + accountId + " account balance: " + account.getBalance() + '\n');
                                try {
                                    TimeUnit.MILLISECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                    break;
                case "r":
                    if (client != null) {
                        ArrayList<Account> clientAccounts = client.getAccounts();
                        for (Account account : clientAccounts) {
                            if (account.getAccountId().equals(accountId)) {
                                if (account.getBalance() >= Double.parseDouble(amount)) {
                                    Transaction transaction = new RetrieveFundsTransaction(Double.parseDouble(amount), account);
                                    transaction.executeTransaction();
                                    transactions.add(transaction);
                                    System.out.println("Succesfully executed retrieve funds transaction." + "Client id: " + clientId + " account id: " + accountId + " account balance: " + account.getBalance() + '\n');
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    break;
                                } else {
                                    System.out.println("Retrieve funds transaction failed. Not enough funds. Client id: " + clientId + " account id: " + accountId + " current account balance: " + account.getBalance() + ". Tried to send + " + amount + '\n');
                                    break;
                                }

                            }
                        }
                    }
                    break;

                case "e":
                    String receiverClientId = line.get(1);
                    String receiverAccountId = line.get(2);
                    Client receiverClient = clients.get(receiverClientId);
                    if (client != null && receiverClient != null) {
                        ArrayList<Account> clientAccounts = client.getAccounts();
                        for (Account account : clientAccounts) {
                            if (account.getAccountId().equals(accountId)) {
                                if (account.getBalance() >= Double.parseDouble(amount)) {
                                    ArrayList<Account> receiverClientAccounts = receiverClient.getAccounts();
                                    for (Account receiverAccount : receiverClientAccounts) {
                                        if (receiverAccount.getAccountId().equals(receiverAccountId)) {
                                            Transaction transaction = new ExchangeFundsTransaction(Double.parseDouble(amount), account, receiverAccount);
                                            transaction.executeTransaction();
                                            transactions.add(transaction);
                                            System.out.println("Succesfully executed exchange funds transaction. Sender client id: " + clientId + "account id: " + accountId + " account balance: " + account.getBalance() + "Receiver client id: " + receiverClientId + "account id: " + receiverAccountId + " account balance: " + receiverAccount.getBalance() + '\n');
                                            try {
                                                TimeUnit.MILLISECONDS.sleep(1);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                } else {
                                    System.out.println("Exhange funds transaction failed. Not enough funds. Sender client id: " + clientId + " account id: " + accountId + " account balance:" + account.getBalance() + ". Tried to send + " + amount + '\n');
                                    break;
                                }

                            }
                        }

                        break;
                    }
            }
        }
    }

    public void writeCSVFile(ArrayList<Transaction> csvTransactions, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Transaction csvTransaction : csvTransactions) {
                writer.write(csvTransaction.toCSV());
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
