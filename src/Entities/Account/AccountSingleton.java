package Entities.Account;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AccountSingleton {
    private static AccountSingleton instance = null;
    HashMap<String, ArrayList<Account>> accounts = new HashMap<>();

    public static AccountSingleton getInstance() {
        if (instance == null)
            instance = new AccountSingleton();
        return instance;
    }

    public HashMap<String, ArrayList<Account>> getAccounts() {
        return this.accounts;
    }

    public void setAccounts(HashMap<String, ArrayList<Account>> accounts) {
        this.accounts = accounts;
    }

    public void parseCSVFile(String filePath) {
        List<List<String>> lines = new ArrayList<>();
        ArrayList<Account> currentAccounts;
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
            String clientId = line.get(0);
            String accountId;
            if (accounts.containsKey(clientId)) {
                currentAccounts = accounts.get(clientId);
                int nr = currentAccounts.size() + 1;
                accountId = clientId + '.' + nr;
            } else {
                currentAccounts = new ArrayList<>();
                accountId = clientId + ".1";
            }

            Account account = new Account(accountId);
            currentAccounts.add(account);
            accounts.put(clientId, currentAccounts);
        }
    }

    public void writeCSVFile(ArrayList<Account> csvAccounts, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Account csvAccount : csvAccounts) {
                writer.write(csvAccount.toCSV());
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
