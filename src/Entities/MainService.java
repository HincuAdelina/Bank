package Entities;

import Entities.Account.Account;
import Entities.Account.AccountSingleton;
import Entities.Account.AccountStatement;
import Entities.Card.Card;
import Entities.Card.CardSingleton;
import Entities.Card.CreditCard;
import Entities.Card.DebitCard;
import Entities.Client.Client;
import Entities.Client.ClientSingleton;
import Entities.Transaction.*;

import java.sql.SQLException;
import java.util.*;


public class MainService {
    HashMap<String, Client> clients;
    HashMap<String, Transaction> transactions;
    Scanner scanner;
    ClientSingleton clientSingleton;
    AccountSingleton accountSingleton;
    CardSingleton cardSingleton;
    TransactionSingleton transactionSingleton;
    AuditService auditService;
    DatabaseService databaseService;

    public MainService() throws SQLException {
        clients = new HashMap<>();
        transactions = new HashMap<>();
        scanner = new Scanner(System.in);
        clientSingleton = ClientSingleton.getInstance();
        accountSingleton = AccountSingleton.getInstance();
        cardSingleton = CardSingleton.getInstance();
        transactionSingleton = TransactionSingleton.getInstance();
        auditService = new AuditService();
        databaseService = DatabaseService.getInstance();
    }

    // Load CSV files
    public void parseCSVFiles() {
        clientSingleton.parseCSVFile("data/read/clients.csv");
        ArrayList<Client> csvClients = clientSingleton.getClients();
        for (Client csvClient : csvClients) {
            clients.put(csvClient.getClientId(), csvClient);
        }

        accountSingleton.parseCSVFile("data/read/accounts.csv");
        HashMap<String, ArrayList<Account>> csvAccounts = accountSingleton.getAccounts();
        for (String clientId : csvAccounts.keySet()) {
            Client client = clients.get(clientId);
            ArrayList<Account> clientAccounts = csvAccounts.get(clientId);
            for (Account clientAccount : clientAccounts) {
                client.addAccount(clientAccount);
            }
        }

        cardSingleton.parseCSVFile("data/read/cards.csv");
        HashMap<String, ArrayList<Card>> csvCards = cardSingleton.getCards();
        for (String clientId : csvCards.keySet()) {
            Client client = clients.get(clientId);
            ArrayList<Card> clientCards = csvCards.get(clientId);
            for (Card clientCard : clientCards) {
                client.addCard(clientCard);
            }
        }

        transactionSingleton.parseCSVFile("data/read/transactions.csv", clients);
        ArrayList<Transaction> csvTransactions = transactionSingleton.getTransactions();
        for (Transaction csvTransaction : csvTransactions) {
            transactions.put(csvTransaction.getDate(), csvTransaction);
        }

    }

    // Write to CSV files
    public void writeCSVFiles() {
        ArrayList<Client> csvClients = new ArrayList<>();
        ArrayList<Account> csvAccounts = new ArrayList<>();
        ArrayList<Card> csvCards = new ArrayList<>();
        ArrayList<Transaction> csvTransactions = new ArrayList<>(transactions.values());

        for (String clientId : clients.keySet()) {
            Client csvClient = clients.get(clientId);
            csvClients.add(csvClient);
            ArrayList<Account> csvClientAccounts = csvClient.getAccounts();
            csvAccounts.addAll(csvClientAccounts);
            ArrayList<Card> csvClientCards = csvClient.getCards();
            csvCards.addAll(csvClientCards);
        }
        clientSingleton.writeCSVFile(csvClients, "data/write/clients.csv");
        accountSingleton.writeCSVFile(csvAccounts, "data/write/accounts.csv");
        cardSingleton.writeCSVFile(csvCards, "data/write/cards.csv");
        transactionSingleton.writeCSVFile(csvTransactions, "data/write/transactions.csv");

    }

    //
    public void parseDB() throws SQLException {
        ArrayList<Client> dbClients = databaseService.readClients();
        for(Client dbClient: dbClients) {
            clients.put(dbClient.getClientId(), dbClient);
        }
    }

    // option 1: Add a new client

    public void addClient() throws SQLException {
        System.out.println("Adding new client. Please enter following client info: ");
        System.out.println("Name: ");
        String name = scanner.next();
        System.out.println("National id: ");
        String nationalId = scanner.next();
        System.out.println("Phone number: ");
        String phoneNumber = scanner.next();

        Client newClient = new Client(String.valueOf(clients.size() + 1), nationalId, name, phoneNumber);
        String clientId = newClient.getClientId();
        clients.put(clientId, newClient);
        System.out.println("Successfully added client " + name + " with id " + newClient.getClientId() + '\n');
        auditService.writeActionToAudit("add client");

        name = "'" + name + "'";
        nationalId = "'" + nationalId + "'";
        phoneNumber = "'" + phoneNumber + "'";
        String query = "insert into Client (nationalId, name, phoneNumber) values (" + nationalId + ", " + name + ", " + phoneNumber + ")";
        databaseService.executeUpdate(query);
    }

    // option 2: Show clients info

    public void showClients() {
        System.out.println("Showing clients: ");
        for (String clientId : clients.keySet()) {
            System.out.println(clients.get(clientId));
        }
        System.out.println('\n');
        auditService.writeActionToAudit("show clients");
    }

    //option 3: Open account for client

    public void openAccount() throws SQLException {
        System.out.println("Opening account. Please enter client's id: ");
        String clientId = scanner.next();
        Client client = clients.get(clientId);
        String accountId = client.openAccount();
        System.out.println("Successfully opened account with id: " + accountId + '\n');
        auditService.writeActionToAudit("open account");

        Account clientAccount = null;
        for(Account account: client.getAccounts()) {
            if(account.getAccountId().equals(accountId)){
                clientAccount = account;
            }

        }
        assert clientAccount != null;
        String creationDate = "'" + clientAccount.getDateOfCreation() + "'";
        String balance = String.valueOf(clientAccount.getBalance());
        clientId = "'" + clientId + "'";

        String query = "insert into Account (creationDate, balance, clientId) values (" + creationDate + ", " + balance + ", " + clientId + ")";
        databaseService.executeUpdate(query);
    }

    // option 4: Show client's accounts

    public void showClientAccounts() {
        System.out.println("Showing client's accounts. Please enter client's id: ");
        String clientId = scanner.next();
        Client client = clients.get(clientId);
        client.showAccounts();
        System.out.println('\n');
        auditService.writeActionToAudit("show clients accounts");
    }

    //option 5: Create transaction to retrieve money

    public void retrieveMoneyFromAccount() throws SQLException {
        RetrieveFundsTransaction transaction = null;
        System.out.println("Retrieving money from account. Please enter following info: ");
        System.out.println("Client id: ");
        String clientId = scanner.next();
        System.out.println("Account id: ");
        String accountId = scanner.next();
        System.out.println("Amount to retrieve: ");
        double amount = Double.parseDouble(scanner.next());

        Client client = clients.get(clientId);
        for (Account account : client.getAccounts()) {
            if (account.getAccountId().equals(accountId)) {
                if (account.getBalance() > amount) {
                    transaction = new RetrieveFundsTransaction(amount, account);
                    transaction.executeTransaction();
                    transactions.put(transaction.getDate(), transaction);
                    System.out.println("Succesfully executed retrieve funds transaction." + "Client id: " + clientId + "account id: " + accountId + " account balance: " + account.getBalance() + '\n');
                    break;
                } else {
                    System.out.println("Retrieve funds transaction failed . Not enough funds. Client id: " + clientId + " Account id: " + accountId + " Current account balance:" + account.getBalance() + ". Tried to send + " + amount + '\n');
                }
            }
        }
        auditService.writeActionToAudit("retrieve money from account");

        String transactionId = "null";
        String date = "'" + transaction.getDate() + "'";
        String _amount = String.valueOf(amount);
        accountId = "'" + accountId + "'";

        String query = "insert into Transaction (date, amount, accountId) values (" +  date + ", " + amount + ", " + accountId + ")";
        databaseService.executeUpdate(query);
    }

    //option 6: Create transaction to add money

    public void addMoneyToAccount() throws SQLException {
        AddFundsTransaction transaction = null;
        System.out.println("Adding money to account. Please enter following info: ");
        System.out.println("Client id: ");
        String clientId = scanner.next();
        System.out.println("Account id: ");
        String accountId = scanner.next();
        System.out.println("Amount to add: ");
        double amount = Double.parseDouble(scanner.next());

        Client client = clients.get(clientId);
        for (Account account : client.getAccounts()) {
            if (account.getAccountId().equals(accountId)) {
                transaction = new AddFundsTransaction(amount, account);
                transaction.executeTransaction();
                transactions.put(transaction.getDate(), transaction);
                System.out.println("Succesfully executed add funds transaction." + "Client id: " + clientId + "account id: " + accountId + " account balance: " + account.getBalance() + '\n');
                break;
            }
        }
        auditService.writeActionToAudit("add money to account");

        String transactionId = "null";
        String date = "'" + transaction.getDate() + "'";
        String _amount = String.valueOf(amount);
        accountId = "'" + accountId + "'";

        String query = "insert into Transaction (date, amount, accountId) values (" +  date + ", " + amount + ", " + accountId + ")";
        databaseService.executeUpdate(query);
    }

    //option 7: Create transaction to send money from one account to another

    public void exchangeMoneyBetweenAccounts() throws SQLException {
        ExchangeFundsTransaction transaction = null;
        System.out.println("Exchanging money between accounts. Please enter following info: ");
        System.out.println("Sender client id: ");
        String senderClientId = scanner.next();
        System.out.println("Sender account id: ");
        String senderAccountId = scanner.next();
        System.out.println("Receiver client id: ");
        String receiverClientId = scanner.next();
        System.out.println("Sender account id: ");
        String receiverAccountId = scanner.next();
        System.out.println("Amount to exchange: ");
        double amount = Double.parseDouble(scanner.next());

        Client senderClient = clients.get(senderClientId);
        Client receiverClient = clients.get(receiverClientId);

        for (Account senderAccount : senderClient.getAccounts()) {
            if (senderAccount.getAccountId().equals(senderAccountId)) {
                if (senderAccount.getBalance() > amount) {
                    for (Account receiverAccount : receiverClient.getAccounts()) {
                        if (receiverAccount.getAccountId().equals(receiverAccountId)) {
                            transaction = new ExchangeFundsTransaction(amount, senderAccount, receiverAccount);
                            transaction.executeTransaction();
                            transactions.put(transaction.getDate(), transaction);
                            System.out.println("Succesfully executed exchange funds transaction. Sender client id: " + senderClientId + " account id: " + senderAccountId + " account balance: " + senderAccount.getBalance() + ". Receiver client id: " + receiverClientId + " account id: " + receiverAccountId + " account balance: " + receiverAccount.getBalance() + '\n');
                            break;
                        } else {
                            System.out.println("Exchange funds transaction failed. Not enough funds. Sender client id: " + senderClientId + " account id: " + senderAccountId + " account balance: " + senderAccount.getBalance() + ". Tried to send + " + amount + '\n');
                        }
                    }
                }
            }
        }
        auditService.writeActionToAudit("exchange money between accounts");

        String transactionId = "null";
        String date = "'" + transaction.getDate() + "'";
        String _amount = String.valueOf(amount);
        senderAccountId = "'" + senderAccountId + "'";
        receiverAccountId = "'" + receiverAccountId + "'";

        String query = "insert into Transaction (date, amount, senderAccountId, receiverAccountId) values (" +  date + ", " + amount + ", " + senderAccountId + "," + receiverAccountId + ")";
        databaseService.executeUpdate(query);
    }

    //option 9: Get account statement

    public void getAccountStatement() {
        System.out.println("Getting account statement. Please enter following info: ");
        System.out.println("Client id: ");
        String clientId = scanner.next();
        System.out.println("Account id: ");
        String accountId = scanner.next();
        System.out.println("\n--ACCOUNT STATEMENT--\n");

        Client client = clients.get(clientId);
        for (Account account : client.getAccounts()) {
            if (account.getAccountId().equals(accountId)) {
                AccountStatement statement = new AccountStatement(account);
                System.out.println(statement);
            }
        }
        auditService.writeActionToAudit("get account statement");
    }

    //option 10: Get all transactions in cronological order

    public void getAllTransactions() {
        System.out.println("TRANSACTIONS\n");
        SortedSet<String> dates = new TreeSet<>(transactions.keySet());
        for (String date : dates) {
            System.out.println(transactions.get(date));
        }
        auditService.writeActionToAudit("get all transactions");
    }

    //option 11: Open card

    public void openClientCard() throws SQLException {
        System.out.println("Opening card. Please enter following info: ");
        System.out.println("Client id: ");
        String clientId = scanner.next();
        Client client = clients.get(clientId);
        String cardId = client.openCard();
        System.out.println("Successfully opened card with id " + cardId + '\n');
        auditService.writeActionToAudit("open client card");

        Card clientCard = null;
        for(Card card: client.getCards()) {
            if(card.getCardId().equals(cardId)) {
                clientCard = card;
            }
        }
        clientId = "'" + clientId + "'";
        String accountId = "'" +  clientCard.getAcoountId() + "'";
        String number = "'" + clientCard.getNumber() + "'";
        String CVV = "'" + clientCard.getCvv() + "'";
        String expirationDate = "'" + clientCard.getExpirationDate() + "'";
        String creditLimit = "null";
        String interest = "null";
        String accumulatedDebt = "null";
        if (clientCard instanceof CreditCard) {
            creditLimit = String.valueOf(((CreditCard) clientCard).getCreditLimit());
            interest = String.valueOf(((CreditCard) clientCard).getInterest());
            accumulatedDebt = String.valueOf(((CreditCard) clientCard).getaccumulatedDebt());
        }
        String query = "insert into Card (accountId, number, CVV, expirationDate, creditLimit, interest, accumulatedDebt, clientId) values (" + accountId + ", " + number + ", " + CVV + ", " + expirationDate + ", " + creditLimit + ", " + interest + ", " + accumulatedDebt + ", " + clientId +  ")";
        databaseService.executeUpdate(query);
    }

    //option 12: Pay by card

    public void payByCard() {
        System.out.println("Paying by card, please enter info: ");
        System.out.println("Client id: ");
        String clientId = scanner.next();
        System.out.println("Card id: ");
        String cardId = scanner.next();
        System.out.println("Amount: ");
        double amount = Double.parseDouble(scanner.next());
        Client client = clients.get(clientId);
        for (Card card : client.getCards()) {
            if (card.getCardId().equals(cardId)) {
                for (Account account : client.getAccounts()) {
                    if (account.getAccountId().equals(card.getAcoountId())) {
                        if (card instanceof DebitCard && account.getBalance() >= amount) {
                            Transaction transaction = new RetrieveFundsTransaction(amount, account);
                            transaction.executeTransaction();
                            transactions.put(transaction.getDate(), transaction);
                            System.out.println("Succesfully executed transaction. Current account balance: " + account.getBalance() + '\n');
                            break;
                        } else if (card instanceof CreditCard && ((CreditCard) card).getaccumulatedDebt() + amount <= ((CreditCard) card).getCreditLimit()) {
                            double accumulatedDebt = ((CreditCard) card).getaccumulatedDebt();
                            ((CreditCard) card).setaccumulatedDebt(accumulatedDebt + amount);
                            System.out.println("Succesfully executed transaction. Credit card spendings: " + ((CreditCard) card).getaccumulatedDebt() + '\n');
                        } else {
                            System.out.println("Transaction failed. Not enough funds.\n");
                        }

                    }
                }
                break;
            }
        }
        auditService.writeActionToAudit("pay by card");
    }

    // option 13: Read ELements
    public void readFromTable() throws SQLException {
        System.out.println("\nReading resources. Please enter: \n");
        System.out.println("Table: ");
        String table = scanner.next();
        databaseService.readResources(table);
    }

    // option 14: Edit Element
    public void editFromTable() throws SQLException {
        System.out.println("\nEditing resource. Please enter: \n");
        System.out.println("Table: ");
        String table = scanner.next();
        System.out.println("Primary key value: ");
        String primaryKey = scanner.next();
        System.out.println("Fields to edit separated by space. (Example: name Andreea clientId 2");
        scanner.nextLine();
        String line = scanner.nextLine();
        String[] splited = line.split("\\s+");
        HashMap<String, String> fields = new HashMap<>();
        for(int i = 0; i < splited.length; i = i + 2) {
            fields.put(splited[i], splited[i+1]);
        }
        databaseService.editResource(table, fields, primaryKey);
    }

    // option 15: Delete Element
    public void deleteFromTable() throws SQLException {
        System.out.println("\nDeleting resource. Please enter: \n");
        System.out.println("Table: ");
        String table = scanner.next();
        System.out.println("Primary key value: ");
        String primaryKey = scanner.next();
        databaseService.deleteResource(table, primaryKey);
    }

}
