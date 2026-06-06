package main;

import javax.swing.*;
import java.awt.*;

public class HospitalUI {

    public static void main(String[] args) {
        
        //CREAT WINDOW
        JFrame frame = new JFrame("Wait Time & Distance Calculator");
        frame.setSize(450, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));

        //CREAT UI
        JLabel titleLabel = new JLabel("Nearest Hospital Finder");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField postalCodeInput = new JTextField(20);
        postalCodeInput.setToolTipText("Enter your postal code (format: A1A1A1)");

        JButton searchButton = new JButton("Find Nearest Hospital");
        searchButton.setBackground(new Color(0, 120, 212));
        searchButton.setForeground(Color.WHITE);
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);

        JTextArea resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setText("");
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // KEY EVENT
        searchButton.addActionListener(e -> {
            String inputPostal = postalCodeInput.getText().trim().toUpperCase();
            if (inputPostal.isEmpty()) {
                resultArea.setText("Error: Postal code cannot be empty!");
                return;
            }

            try {
                resultArea.setText("Locating and calculating, please wait...");

                // GET USER LOCATION
                double[] userLocation = functions.PostalConverter.getLocation(inputPostal);
                if (userLocation == null) {
                    resultArea.setText("INVALID postal code, Try again(format: A1A1A1)");
                    return;
                }

                // GET HOSPITAL DATA
                String rawJson = functions.HospitalData.loadHospitalData();
                com.google.gson.JsonObject dataObj = com.google.gson.JsonParser.parseString(rawJson).getAsJsonObject();
                com.google.gson.JsonArray features = dataObj.getAsJsonArray("features");

                String closestHospitalName = "";
                double minDistance = Double.MAX_VALUE;
                String waitTime = "N/A";

                double closedLat = 0, closedLon = 0;

                for (com.google.gson.JsonElement element : features) {
                    com.google.gson.JsonObject attributes = element.getAsJsonObject().get("attributes").getAsJsonObject();
                    com.google.gson.JsonObject geometry = element.getAsJsonObject().get("geometry").getAsJsonObject();
                    
                    double hLat = geometry.get("y").getAsDouble();
                    double hLon = geometry.get("x").getAsDouble();
                    
                    double dist = functions.Commute.getDistance(userLocation[0], userLocation[1], hLat, hLon);
                    
                    if (dist < minDistance) {
                        minDistance = dist;
                        closestHospitalName = attributes.get("hospital_name").getAsString();
                        closedLat = hLat;
                        closedLon = hLon;
                        if (attributes.has("ed_wait_range") && !attributes.get("ed_wait_range").isJsonNull()) {
                            waitTime = attributes.get("ed_wait_range").getAsString();
                        } else {
                            waitTime = "No wait / No data";
                        }
                    }
                }

                // PRINT INFORMATION
                String navLink = String.format("https://www.google.com/maps/dir/%.6f,%.6f/%.6f,%.6f", userLocation[0], userLocation[1], closedLat, closedLon);
                resultArea.setText(String.format(
                    
                    "Nearest Hospital:\n" +
                    "Name: %s\n" +
                    "Distance: %.2f km\n" +
                    "Wait: %s" +
                    "\n\n[Google Maps Navigation Link]\n%s",
                    closestHospitalName, minDistance, waitTime, navLink
                ));

            } catch (Exception ex) {
                resultArea.setText("Execution Error: " + ex.getMessage());
            }
        });

        // ADD COMPONENTS
        frame.add(titleLabel);
        frame.add(postalCodeInput);
        frame.add(searchButton);
        frame.add(scrollPane);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
