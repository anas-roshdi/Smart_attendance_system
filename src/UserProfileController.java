import java.io.IOException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class UserProfileController {

    // FXML components for UI
    @FXML
    private Label idLB; // Label to display user ID
    @FXML
    private Label nameLB; // Label to display user name
    @FXML
    private Label emsilLB; // Label to display user email
    @FXML
    private Label phoneLB; // Label to display user phone number

    @FXML
    private AnchorPane root1; // Root pane for the scene
    @FXML
    private AnchorPane Pane2; // pane to Display email
    @FXML
    private AnchorPane Pane3; // pane to Display email phone

    @FXML
    private Button ProfileBt; // Button for Profile
    @FXML
    private Button LogOutBt; // Button for Log Out

    private int i;
    private DBcon dbCon = new DBcon(); // Database connection handler
    private SceneController SceneController = new SceneController(); // Scene controller for scene switching

    // Initialize the controller
    @FXML
    public void initialize() {
        // Load details based on the logged-in ID
        loadInstructorProfile();
    }

    // Load the instructor's profile details based on the logged-in ID
    private void loadInstructorProfile() {
        String UserID = LogInController.getLoggedInUserId();

        idLB.setText(UserID);
        if (UserID.startsWith("a")) {
            i = 0;
        } else if (UserID.startsWith("i")) {
            i = 1;
        } else {
            i = 2;
        }
        String query = getquerey(i);

        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, UserID);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if the query returns data
            if (resultSet.next()) {
                displayData(i, resultSet);
            }
        } catch (Exception e) {
            System.err.println("Error loading instructor profile: " + e.getMessage());
        }
    }

    public String getquerey(int i) {
        if (i == 0) {
            return """
                    SELECT a_AdminID, a_password, a_FirstName, a_LastName
                    FROM administrators
                    WHERE a_AdminID = ?
                    """;
        } else if (i == 1) {
            return """
                    SELECT I_INSTRUCTORID, I_FIRST_NAME, I_MIDDLE_NAME, I_LAST_NAME, I_EMAIL, I_PHONE
                    FROM INSTRUCTOR
                    WHERE I_INSTRUCTORID = ?
                            """;
        } else {
            return """
                    SELECT s_StudentID, s_FIRST_NAME, s_MIDDLE_NAME, s_LAST_NAME, s_EMAIL, s_PHONE
                    FROM student
                    WHERE s_StudentID = ?
                    """;
        }
    }

    public void displayData(int i, ResultSet result) throws SQLException {
        if (i == 0) {
            // Display admin name
            nameLB.setText(result.getString("a_FirstName") + " " + result.getString("a_LastName"));
            Pane2.setVisible(false);
            Pane3.setVisible(false);
        } else if (i == 1) {
            // Display instructor name
            nameLB.setText(result.getString("I_FIRST_NAME") + " " + result.getString("I_MIDDLE_NAME") + " "
                    + result.getString("I_LAST_NAME"));
            // Display instructor email
            emsilLB.setText(result.getString("I_EMAIL"));
            // Display instructor phone
            phoneLB.setText(result.getString("I_PHONE"));
        } else {
            // Display student name
            nameLB.setText(result.getString("s_FIRST_NAME") + " " + result.getString("s_MIDDLE_NAME") + " "
                    + result.getString("s_LAST_NAME"));
            // Display student email
            emsilLB.setText(result.getString("s_EMAIL"));
            // Display student phone
            phoneLB.setText(result.getString("s_PHONE"));
        }
    }

    // Switch to the login screen when LogOut button is clicked
    @FXML
    public void SwitchToLogIn(Event e) throws IOException {
        SceneController.SwitchTo(0, e);

    }

    @FXML
    public void SwitchToHomePage(Event e) throws IOException {
        if (i == 0) {
            SceneController.SwitchTo(8, e);
        } else if (i == 1) {
            SceneController.SwitchTo(2, e);
        } else {
            SceneController.SwitchTo(6, e);
        }

    }

    // Handle mouse enter for buttons to change background color
    @FXML
    private void MouseEnter(MouseEvent event) {
        Button hoveredButton = (Button) event.getSource();
        if (hoveredButton != null) {
            hoveredButton.setStyle("-fx-background-color: rgb(0, 7, 141);");
        }
    }

    // Handle mouse exit for buttons to reset background color
    @FXML
    private void MouseExited(MouseEvent event) {
        Button exitedButton = (Button) event.getSource();
        if (exitedButton != null) {
            exitedButton.setStyle("-fx-background-color: rgb(59, 56, 158);");
        }
    }

}
