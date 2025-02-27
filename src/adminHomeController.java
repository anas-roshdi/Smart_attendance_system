import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;

public class adminHomeController {

    // FXML components for UI
    @FXML
    private Button ProfileBt; // Button for navigating to profile
    @FXML
    private Button LogOutBt; // Button for logging out
    @FXML
    private Button addBt; // Button to work on add
    @FXML
    private Button editBt; // Button to work on edit
    @FXML
    private Button deleteBt; // Button to work on delete
    @FXML
    private RadioButton courseRb; // Button to select course
    @FXML
    private RadioButton instructorRb; // Button to select instructor
    @FXML
    private RadioButton groupRd;
    @FXML
    private RadioButton studentRb; // Button to select student
    @FXML
    private Label use_msg;

    private static int i;

    @FXML
    private Label ad_nameLB; // Label to display the name

    private SceneController SceneController = new SceneController(); // Scene controller for scene switching
    private DBcon dbCon = new DBcon(); // Database connection handler

    // Initialize the controller
    @FXML
    public void initialize() {

        // Load user's name based on the logged-in ID
        loadUserName();

    }

    // Load the user's name based on the logged-in ID
    private void loadUserName() {
        // Get the logged-in ID
        String loggedInAdminId = LogInController.getLoggedInUserId();

        if (loggedInAdminId != null && !loggedInAdminId.isEmpty()) {
            // Query to fetch the instructor's full name
            String query = """
                        SELECT a_FIRSTNAME, a_LASTNAME
                        FROM administrators
                        WHERE a_adminID = ?
                    """;

            try (Connection connection = dbCon.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, loggedInAdminId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Get first, middle, and last names from the result set
                    String firstName = resultSet.getString("a_FIRSTNAME");
                    String lastName = resultSet.getString("a_LASTNAME");

                    // Set the full name in the label
                    ad_nameLB.setText(firstName + " " + lastName);
                }
            } catch (Exception e) {
                System.err.println("Error loading Admin name: " + e.getMessage());
            }
        } else {
            System.err.println("No logged-in Admin ID found.");
        }
    }

    public void SetChoice() {
        if (instructorRb.isSelected()) {
            i = 0;
        } else if (courseRb.isSelected()) {
            i = 1;
        } else if (groupRd.isSelected()) {
            i = 2;
        } else {
            i = 3;
        }
    }

    // Handle mouse enter event for buttons (change background color)
    @FXML
    private void MouseEnter(MouseEvent event) {
        Button hoveredButton = (Button) event.getSource();
        if (hoveredButton != null) {
            hoveredButton.setStyle("-fx-background-color: rgb(0, 7, 141);");
        }
    }

    // Handle mouse exit event for buttons (reset background color)
    @FXML
    private void MouseExited(MouseEvent event) {
        Button exitedButton = (Button) event.getSource();
        if (exitedButton != null) {
            exitedButton.setStyle("-fx-background-color: rgb(59, 56, 158);");
        }
    }

    // Switch to the login screen
    public void SwitchToLogIn(Event e) throws IOException {
        SceneController.SwitchTo(0, e);
    }

    public void SwitchToUserProfile(Event e) throws IOException {
        SceneController.SwitchTo(1, e);
    }

    public void SwitchToAddPage(Event e) throws IOException {
        if (!courseRb.isSelected() && !instructorRb.isSelected() && !studentRb.isSelected() && !groupRd.isSelected()) {
            use_msg.setText("You must choese one ");
        } else {
            SceneController.SwitchTo(9, e);
        }
    }

    public void SwitchToEditPage(Event e) throws IOException {
        if (!courseRb.isSelected() && !instructorRb.isSelected() && !studentRb.isSelected() && !groupRd.isSelected()) {
            use_msg.setText("You must choese one ");
        } else {
            SceneController.SwitchTo(10, e);
        }
    }

    public void SwitchToDeletePage(Event e) throws IOException {
        if (!courseRb.isSelected() && !instructorRb.isSelected() && !studentRb.isSelected() && !groupRd.isSelected()) {
            use_msg.setText("You must choese one ");
        } else {
            SceneController.SwitchTo(11, e);
        }
    }

    public static int AdminChoice() {
        return i;
    }
}