package com.mycompany.part2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class WelcomeToQuickChat {
    private static int messageCounter = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WelcomeToQuickChat::showMainMenu);
    }

    private static void showMainMenu() {
        JFrame frame = new JFrame("QuickChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton sendButton = new JButton("1. Send Message");
        JButton viewButton = new JButton("2. Sent Messages");
        JButton quitButton = new JButton("3. Quit");

        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        viewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        sendButton.addActionListener((ActionEvent e) -> sendMessages(frame));
        viewButton.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(frame, "Coming Soon");
        });
       
       
       
        quitButton.addActionListener((ActionEvent e) -> {
            // Auto-save before quitting
            Message.saveMessages();
            JOptionPane.showMessageDialog(frame, "Messages saved! Thank you for using QuickChat. Goodbye!");
            frame.dispose();
        });

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(sendButton);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(viewButton);
        
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(quitButton);

        frame.getContentPane().add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void sendMessages(JFrame frame) {
        int numMessages = 0;
        boolean validInput = false;

        while (!validInput) {
            String input = JOptionPane.showInputDialog(frame, "How many messages would you like to send?");
            if (input == null) return; // Cancel pressed
            try {
                numMessages = Integer.parseInt(input);
                if (numMessages > 0) {
                    validInput = true;
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter a positive number.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number. Please try again.");
            }
        }

        int messagesSentThisSession = 0;
        
        for (int i = 0; i < numMessages; i++) {
            messageCounter++;
            int messageId = 1000 + messageCounter;

            String recipient = null;
            while (recipient == null || !isValidPhoneNumber(recipient)) {
                recipient = JOptionPane.showInputDialog(frame, "Enter recipient phone number (e.g., +27123456789):");
                if (recipient == null) return;
                if (!isValidPhoneNumber(recipient)) {
                    JOptionPane.showMessageDialog(frame, "Invalid phone number. Must start with + and have up to 13 characters total.");
                }
            }

            String content = null;
            while (content == null || content.length() > 250 || content.trim().isEmpty()) {
                content = JOptionPane.showInputDialog(frame, "Enter your message (max 250 characters):");
                if (content == null) return;
                if (content.length() > 250) {
                    JOptionPane.showMessageDialog(frame, "Please enter a message of less than 250 characters.");
                } else if (content.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Message cannot be empty.");
                }
            }

            // Create new Message object and use its SentMessage method
            Message newMessage = new Message(messageId, recipient, content);
            String result = newMessage.SentMessage();
            
            // Count messages that were actually sent (not disregarded)
            if (result.equals("Message sent.")) {
                messagesSentThisSession++;
            }
            
            // Show result to user
            JOptionPane.showMessageDialog(frame, result);
        }
        
        // Display total messages summary after all messages are processed
        int totalMessages = Message.returnTotalMessages();
        String summaryMessage = String.format(
            "Session Summary:\n" +
            "Messages sent this session: %d\n" +
            "Total messages sent overall: %d",
            messagesSentThisSession, totalMessages
        );
        
        JOptionPane.showMessageDialog(frame, summaryMessage, "Message Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        
        // Check if starts with + and has reasonable length
        return phoneNumber.startsWith("+") && 
               phoneNumber.length() >= 10 && 
               phoneNumber.length() <= 13 &&
               phoneNumber.substring(1).matches("\\d+"); // Rest should be digits
    }
}