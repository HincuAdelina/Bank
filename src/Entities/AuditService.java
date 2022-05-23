package Entities;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuditService {
    private FileWriter writer;

    public AuditService() {
        try {
            this.writer = new FileWriter("data/write/audit.csv");
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void writeActionToAudit(String action) {
        try {
            writer.append(action);
            writer.append(",");
            writer.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            writer.append("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}