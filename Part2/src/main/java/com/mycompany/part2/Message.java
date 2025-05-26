package com.mycompany.part2;

import javax.swing.*;
import java.util.ArrayList;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private static int totalMessages = 0;
    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final String JSON_FILE_PATH = "messages.json";

    private int messageId;
    private String recipient;
    private String content;
    private String messageHash;
    private String timestamp;

    // Load messages from JSON when class is first used
    static {
        loadMessagesFromJSON();
    }

    public Message(int messageId, String recipient, String content) {
        this.messageId = messageId;
        this.recipient = recipient;
        this.content = content;
        this.messageHash = createMessageHash();
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Constructor for loading from JSON
    public Message(int messageId, String recipient, String content, String messageHash, String timestamp) {
        this.messageId = messageId;
        this.recipient = recipient;
        this.content = content;
        this.messageHash = messageHash;
        this.timestamp = timestamp;
    }

    // 1. Boolean: checkMessageID() - ensure ID is <= 10 characters
    public boolean checkMessageID() {
        return String.valueOf(messageId).length() <= 10;
    }

    // 2. Int: checkRecipientCell() - ensure number is <= 10 digits and starts with '+'
    public int checkRecipientCell() {
        if (recipient != null && recipient.length() <= 13 && recipient.startsWith("+")) {
            return 1; // valid
        }
        return 0; // invalid
    }

    // 3. String: createMessageHash() - First 2 digits of ID:count:first-last word
    public String createMessageHash() {
        String[] words = content.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        return (String.valueOf(messageId).substring(0, 2) + ":" + (totalMessages + 1) + ":" + firstWord + "-" + lastWord).toUpperCase();
    }

    // 4. String: SentMessage() - Ask user if they want to send, store, or disregard
    public String SentMessage() {
        String[] options = {"Send", "Disregard", "Store"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "To: " + recipient + "\nMessage: " + content + "\nHash: " + messageHash + "\nTime: " + timestamp,
                "Message Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // Send
                sentMessages.add(this);
                totalMessages++;
                saveMessagesToJSON(); // Save to JSON after adding
                return "Message sent.";
            case 1: // Disregard
                return "Message disregarded.";
            case 2: // Store (placeholder)
                return "Message stored for later (not implemented).";
            default:
                return "No action taken.";
        }
    }

    // 5. String: printMessage() - List of all sent messages
    public static String printMessage() {
        if (sentMessages.isEmpty()) {
            return "No messages sent.";
        }
        StringBuilder sb = new StringBuilder("Sent Messages:\n\n");
        for (Message msg : sentMessages) {
            sb.append("ID: ").append(msg.messageId)
                    .append("\nTo: ").append(msg.recipient)
                    .append("\nMessage: ").append(msg.content)
                    .append("\nHash: ").append(msg.messageHash)
                    .append("\nTime: ").append(msg.timestamp)
                    .append("\n\n");
        }
        return sb.toString();
    }

    // 6. Int: returnTotalMessages() - total sent
    public static int returnTotalMessages() {
        return totalMessages;
    }

    // NEW: Save messages to JSON file
    private static void saveMessagesToJSON() {
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            writer.write("{\n");
            writer.write("  \"totalMessages\": " + totalMessages + ",\n");
            writer.write("  \"messages\": [\n");
            
            for (int i = 0; i < sentMessages.size(); i++) {
                Message msg = sentMessages.get(i);
                writer.write("    {\n");
                writer.write("      \"messageId\": " + msg.messageId + ",\n");
                writer.write("      \"recipient\": \"" + escapeJSON(msg.recipient) + "\",\n");
                writer.write("      \"content\": \"" + escapeJSON(msg.content) + "\",\n");
                writer.write("      \"messageHash\": \"" + escapeJSON(msg.messageHash) + "\",\n");
                writer.write("      \"timestamp\": \"" + escapeJSON(msg.timestamp) + "\"\n");
                writer.write("    }");
                if (i < sentMessages.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            
            writer.write("  ]\n");
            writer.write("}\n");
            
            System.out.println("Messages saved to JSON successfully.");
        } catch (IOException e) {
            System.err.println("Error saving messages to JSON: " + e.getMessage());
        }
    }

    // NEW: Load messages from JSON file
    private static void loadMessagesFromJSON() {
        File file = new File(JSON_FILE_PATH);
        if (!file.exists()) {
            System.out.println("No existing messages file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(JSON_FILE_PATH))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line).append("\n");
            }
            
            parseJSON(jsonContent.toString());
            System.out.println("Messages loaded from JSON successfully. Total messages: " + totalMessages);
        } catch (IOException e) {
            System.err.println("Error loading messages from JSON: " + e.getMessage());
        }
    }

    // NEW: Simple JSON parser (basic implementation)
    private static void parseJSON(String jsonContent) {
        String[] lines = jsonContent.split("\n");
        boolean inMessages = false;
        Message currentMessage = null;
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.contains("\"totalMessages\":")) {
                totalMessages = Integer.parseInt(line.replaceAll("[^0-9]", ""));
            } else if (line.contains("\"messages\":")) {
                inMessages = true;
                sentMessages.clear();
            } else if (inMessages && line.equals("{")) {
                currentMessage = new Message(0, "", "", "", "");
            } else if (inMessages && currentMessage != null) {
                if (line.contains("\"messageId\":")) {
                    currentMessage.messageId = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                } else if (line.contains("\"recipient\":")) {
                    currentMessage.recipient = extractStringValue(line);
                } else if (line.contains("\"content\":")) {
                    currentMessage.content = extractStringValue(line);
                } else if (line.contains("\"messageHash\":")) {
                    currentMessage.messageHash = extractStringValue(line);
                } else if (line.contains("\"timestamp\":")) {
                    currentMessage.timestamp = extractStringValue(line);
                } else if (line.equals("}") || line.equals("},")) {
                    sentMessages.add(currentMessage);
                    currentMessage = null;
                }
            }
        }
    }

    // NEW: Helper method to extract string values from JSON
    private static String extractStringValue(String line) {
        int firstQuote = line.indexOf("\"", line.indexOf(":"));
        int lastQuote = line.lastIndexOf("\"");
        if (firstQuote != -1 && lastQuote != -1 && firstQuote < lastQuote) {
            return line.substring(firstQuote + 1, lastQuote);
        }
        return "";
    }

    // NEW: Helper method to escape special characters in JSON
    private static String escapeJSON(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    // NEW: Public method to manually save messages (useful for testing)
    public static void saveMessages() {
        saveMessagesToJSON();
    }

    // NEW: Public method to manually load messages (useful for testing)
    public static void loadMessages() {
        loadMessagesFromJSON();
    }

    // NEW: Get all sent messages (for external access)
    public static ArrayList<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    // Getters for external access
    public int getMessageId() { return messageId; }
    public String getRecipient() { return recipient; }
    public String getContent() { return content; }
    public String getMessageHash() { return messageHash; }
    public String getTimestamp() { return timestamp; }
}