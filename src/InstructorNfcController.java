import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javax.smartcardio.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javafx.event.Event;
import javafx.scene.control.Label;

/**
 * Controller class for handling NFC-based student attendance for instructors.
 * 
 */
    public class InstructorNfcController {

    @FXML
    private TextArea logTA; // Text area for logging messages

    @FXML
    private Button clearBT; // Button to clear the log
     @FXML
    private Label courseNameLB; 
    @FXML
    private Label lecDateLB; 
    @FXML
    private Label groubNameLB; 

    private final SceneController sceneController = new SceneController(); // Scene navigation controller

    private final Map<String, String> studentMap = new HashMap<>(); // Map for UID-to-student ID

    private final DBcon dbCon = new DBcon(); // Database connection handler

    private String currentLectureId; // Store the current lecture ID

    private Thread nfcReaderThread; // Reference to the NFC reader thread

    /**
     * Initialize the controller.
     */
    public void initialize() {
        // Populate the UID-to-student ID map
        studentMap.put("BE 73 D2 31", "s44000001");
        studentMap.put("1E 04 D4 31", "s44000002");
        studentMap.put("1D D7 25 AD 09 10 80", "s44000003");

        // Set up the clear button to clear the log
        clearBT.setOnAction(event -> logTA.clear());
        lecDateLB.setText(instructorLecturesController.selectedlecture.getDate());
        courseNameLB.setText(InstructorHomeController.selectedCourseGroup.getCourseName());
        groubNameLB.setText(InstructorHomeController.selectedCourseGroup.getGroupName());

        // Start NFC reader in a separate thread
        nfcReaderThread = new Thread(this::startNfcReader);
        nfcReaderThread.start();

        // Set the current lecture ID (this should be dynamic based on the selected lecture)
        currentLectureId = instructorLecturesController.selectedlecture.getID();
    }

    /**
     * Starts the NFC reader to detect student cards.
     */
    private void startNfcReader() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            java.util.List<CardTerminal> terminals = factory.terminals().list();
            if (terminals.isEmpty()) {
                System.out.println("No NFC reader detected.");
                return;
            }
            CardTerminal terminal = terminals.get(0);
            System.out.println("Waiting for NFC cards...");
            System.out.println(currentLectureId);
            while (!Thread.currentThread().isInterrupted()) {
                if (terminal.isCardPresent()) {
                    System.out.println("Card detected!");
                    try {
                        Card card = terminal.connect("*");
                        byte[] atr = card.getATR().getBytes();
                        System.out.println("Card ATR: " + byteArrayToHexString(atr));
                        CommandAPDU command = new CommandAPDU(new byte[]{
                                (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00
                        });
                        ResponseAPDU response = card.getBasicChannel().transmit(command);
                        byte[] uidBytes = response.getData();
                        String uid = byteArrayToHexString(uidBytes);
                        System.out.println("Card UID: " + uid);
                        handleStudentCheck(uid);
                        card.disconnect(false);
                        System.out.println("Card disconnected. Remove card to detect a new one.");
                    } catch (CardException e) {
                        System.out.println("Error communicating with the card: " + e.getMessage());
                    }

                    // Wait for card removal
                    while (terminal.isCardPresent()) {
                        Thread.sleep(500);
                    }
                    System.out.println("Card removed. Waiting for a new card...");
                } else {
                    Thread.sleep(500); // Poll every 500ms for card presence
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the attendance check for a student based on the card UID.
     *
     * @param uid The UID of the NFC card.
     */
    private void handleStudentCheck(String uid) {
        if (studentMap.containsKey(uid)) {
            String studentId = studentMap.get(uid);

            try (Connection connection = dbCon.getConnection()) {
                // Query to fetch the student's full name
                String fullNameQuery = """
                    SELECT s_First_Name, s_middle_Name, s_Last_Name
                    FROM student
                    WHERE s_StudentID = ? 
                """;

                try (PreparedStatement fullNamePreparedStatement = connection.prepareStatement(fullNameQuery)) {
                    fullNamePreparedStatement.setString(1, studentId);
                    ResultSet fullNameResultSet = fullNamePreparedStatement.executeQuery();

                    if (fullNameResultSet.next()) {
                        String firstName = fullNameResultSet.getString("s_First_Name");
                        String middleName = fullNameResultSet.getString("s_middle_Name");
                        String lastName = fullNameResultSet.getString("s_Last_Name");
                        String fullName = firstName + " " + middleName + " " + lastName;

                        System.out.println("Student name: " + fullName);
                        System.out.println("Student ID: " + studentId);

                        // Query to check if the student is in the lecture
                        String inLecQuery = """
                            SELECT 1
                            FROM attendances
                            WHERE s_StudentID = ? AND l_LectureID = ?
                        """;

                        try (PreparedStatement inLecPreparedStatement = connection.prepareStatement(inLecQuery)) {
                            inLecPreparedStatement.setString(1, studentId);
                            inLecPreparedStatement.setString(2, currentLectureId);
                            ResultSet inLecResultSet = inLecPreparedStatement.executeQuery();

                            if (inLecResultSet.next()) {
                                

                                // Query to check attendance status
                                String isStudentPreparedQuery = """
                                    SELECT attend_Status
                                    FROM attendances
                                    WHERE s_StudentID = ? AND l_LectureID = ?
                                """;

                                try (PreparedStatement isStudentPreparedPreparedStatement = connection.prepareStatement(isStudentPreparedQuery)) {
                                    isStudentPreparedPreparedStatement.setString(1, studentId);
                                    isStudentPreparedPreparedStatement.setString(2, currentLectureId);
                                    ResultSet isStudentPreparedResultSet = isStudentPreparedPreparedStatement.executeQuery();

                                    if (isStudentPreparedResultSet.next() && "Attended".equalsIgnoreCase(isStudentPreparedResultSet.getString("attend_Status"))) {
                                        logTA.appendText(fullName + " is already Attended for this lecture.\n");
                                    } else {
                                        // Update attendance status
                                        String updateQuery = """
                                            UPDATE attendances
                                            SET attend_Status = 'Attended'
                                            WHERE s_StudentID = ? AND l_LectureID = ?
                                        """;

                                        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                                            preparedStatement.setString(1, studentId);
                                            preparedStatement.setString(2, currentLectureId);
                                            preparedStatement.executeUpdate();
                                            logTA.appendText(fullName + " is Now Attended for this lecture.\n");
                                        }
                                        
                                            
                                        
                                    }
                                }
                            } else {
                                logTA.appendText(fullName + " is NOT in This Lecture \n");
                            }
                        }
                    } else {
                        logTA.appendText("Student not found for ID: " + studentId + "\n");
                        
                    }
                }
            } catch (Exception e) {
                System.err.println("Error handling student check: " + e.getMessage());
            }
        } else {
            logTA.appendText("Unknown UID detected: " + uid + "\n");
        }
    }

    /**
     * Switches to the instructor attendance page.
     *
     * @param e The event triggering the switch.
     * @throws IOException If an I/O error occurs.
     */
    public void SwitchToInstructorAtt(Event e) throws IOException {
        // Stop the NFC reader thread
        if (nfcReaderThread != null && nfcReaderThread.isAlive()) {
            nfcReaderThread.interrupt();
        }

        // Switch to the instructor attendance page
        sceneController.SwitchTo(4, e);
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes The byte array to convert.
     * @return The hexadecimal string representation.
     */
    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
