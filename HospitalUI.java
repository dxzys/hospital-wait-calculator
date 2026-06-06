import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HospitalUI {

    public static void main(String[] args) {
        
        // CREAT A MAIN FRAME
        JFrame frame = new JFrame("Wait Time & Distance Calculator");
        frame.setSize(850, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));

        //CREAT UI COMPONENTS
        JLabel titleLabel = new JLabel("Nearest Hospital Finder");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField postalCodeInput = new JTextField(20);
        postalCodeInput.setToolTipText("Enter your postal code(format:A1A1A1)");

        JButton searchButton = new JButton("Find Nearest Hospital");
        searchButton.setBackground(new Color(0, 120, 212));
        searchButton.setForeground(Color.WHITE);
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);

        // PRINT
        JTextArea resultArea = new JTextArea(18, 105);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Menlo", Font.PLAIN, 11));
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        resultArea.setText("Enter postal code and click search.");
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // BUTTON
        searchButton.addActionListener(e -> {
            String inputPostal = postalCodeInput.getText().trim().replace(" ", "").toUpperCase();
            if (inputPostal.isEmpty()) {
                resultArea.setText("Error: empty!\n input your post code");
                return;
            }
            searchButton.setEnabled(false);
            resultArea.setText("Please wait......");

            //NEW THREAD
            new Thread(() -> {
                try {
                    String pythonPath = System.getProperty("user.dir") + "/.venv/bin/python3";
                    ProcessBuilder pb = new ProcessBuilder(pythonPath, "main.py", inputPostal);

                    pb.redirectErrorStream(true); 
                    
                    Process process = pb.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuilder pythonOutput = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        pythonOutput.append(line).append("\n");
                    }

                    process.waitFor(); 

                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText(pythonOutput.toString());
                        searchButton.setEnabled(true);
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Execution Error: Failed to connect to Python.\n" + ex.getMessage());
                        searchButton.setEnabled(true);
                    });
                }
            }).start();
        });

        frame.add(titleLabel);
        frame.add(postalCodeInput);
        frame.add(searchButton);
        frame.add(scrollPane);
        
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(searchButton);
        frame.setVisible(true);
    }
}
