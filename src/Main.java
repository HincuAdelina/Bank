import Entities.MainService;

import java.io.IOException;
import java.sql.*;


public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        MainService service = new MainService();
        service.parseDB();

        service.addClient();
        service.openAccount();
        service.addMoneyToAccount();
        service.retrieveMoneyFromAccount();

        service.addClient();
        service.openAccount();
        service.exchangeMoneyBetweenAccounts();

        service.openClientCard();

        service.readFromTable();
        service.readFromTable();
        service.readFromTable();
        service.readFromTable();

        service.editFromTable();
        service.readFromTable();

        service.deleteFromTable();
        service.readFromTable();
    }
}
