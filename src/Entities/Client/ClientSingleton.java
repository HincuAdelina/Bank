package Entities.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientSingleton {
    private static ClientSingleton instance = null;
    private ArrayList<Client> clients = new ArrayList<>();

    public static ClientSingleton getInstance() {
        if (instance == null)
            instance = new ClientSingleton();
        return instance;
    }

    public ArrayList<Client> getClients() {
        return this.clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public void parseCSVFile(String filePath) {
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
            Client client = new Client(line.get(0), line.get(1), line.get(2), line.get(3));
            clients.add(client);
        }
    }

    public void writeCSVFile(ArrayList<Client> csvClients, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Client csvClient : csvClients) {
                writer.write(csvClient.toCSV());
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
