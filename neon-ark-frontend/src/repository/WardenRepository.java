package repository;

//reads CSV
//converts rows to Warden objects
//public simple query methods

import domain.Warden;
import java.util.*;
import java.io.*;

public class WardenRepository {
    private final String filePath;

    //constructor
    public WardenRepository(String filePath) {
        this.filePath = filePath;

    }

    public List<Warden> findAll() {
        List<Warden> wardens = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // skips header

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                //ensure correct number of fields in each row, otherwise skip invalid row
                if (fields.length < 5) {
                    continue;
                }

                int id;

                try {
                    id = Integer.parseInt(fields[0].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid ID row: " + line);
                    continue;
                }
                    String name = fields[1].trim();
                    String role = fields[2].trim();
                    String status = fields[3].trim();
                    String clearance = fields[4].trim();

                    //split name
                    String[] nameParts = name.split(" ", 2);
                    String firstName = nameParts[0].trim();
                    String lastName = "";

                    if (nameParts.length > 1) {
                        lastName = nameParts[1].trim();
                    }
                    Warden w = new Warden(firstName, lastName, role, status, clearance);
                    w.setId(id);
                    wardens.add(w);
            }
        }
        catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        return wardens;
    }

    public Warden findById(int id) {
        for (Warden w : findAll()) {
            if (w.getId() == id) {
                return w;
            }
        }
        return null;
    }

    public boolean nameExists(String f_name, String l_name) {
        for (Warden w : findAll()) {
            boolean sameFirst = w.getFirstName().equalsIgnoreCase(f_name);
            boolean sameLast = w.getLastName().equalsIgnoreCase(l_name);

            if (!l_name.isBlank() && (sameFirst && sameLast)) {
                    return true;
            } else {
                if(sameFirst && w.getLastName().isBlank()) {
                    return true;
                }
            }
        }
        return false;
    }

}
