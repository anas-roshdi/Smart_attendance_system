import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class instructorAttendancesController {
    @FXML
    private Label ins_nameLB;
    @FXML
    private Label courseLB;
    @FXML
    private Label groupLB;
    @FXML
    private Label dateLB;
    @FXML
    private TableView<StudentModel> tableView; // Table view to show LecturesModel
    @FXML
    private TableColumn<StudentModel, String> NameCol;
    @FXML
    private TableColumn<StudentModel, String> IDCol;
    @FXML
    private TableColumn<StudentModel, String> statusCol; // Column for week

    private DBcon dbCon = new DBcon(); // Database connection handler
    private SceneController SceneController = new SceneController(); // Scene controller for scene switching

    // Initialize the controller
    @FXML
    public void initialize() {

        NameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("studentStatus"));
        IDCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        courseLB.setText(InstructorHomeController.selectedCourseGroup.getCourseName());
        groupLB.setText(InstructorHomeController.selectedCourseGroup.getGroupName());
        dateLB.setText(instructorLecturesController.selectedlecture.getDate());

        // Load instructor's name based on the logged-in ID
        loadInstructorName();

        // Load table data related to the logged-in instructor's LecturesModel
        loadTableData();

    }

    // Load the instructor's name based on the logged-in ID
    private void loadInstructorName() {
        // Get the logged-in instructor's ID (you can replace it dynamically)
        String loggedInInstructorId = LogInController.getLoggedInUserId();

        if (loggedInInstructorId != null && !loggedInInstructorId.isEmpty()) {
            // Query to fetch the instructor's full name
            String query = """
                        SELECT I_FIRST_NAME, I_MIDDLE_NAME, I_LAST_NAME
                        FROM INSTRUCTOR
                        WHERE I_INSTRUCTORID = ?
                    """;

            try (Connection connection = dbCon.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, loggedInInstructorId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Get first, middle, and last names from the result set
                    String firstName = resultSet.getString("I_FIRST_NAME");
                    String middleName = resultSet.getString("I_MIDDLE_NAME");
                    String lastName = resultSet.getString("I_LAST_NAME");

                    // Set the full name in the label
                    ins_nameLB.setText(firstName + " " + middleName + " " + lastName);
                }
            } catch (Exception e) {
                System.err.println("Error loading instructor name: " + e.getMessage());
            }
        } else {
            System.err.println("No logged-in instructor ID found.");
        }
    }

    // Load the table data for LecturesModel assigned to the logged-in instructor
    private void loadTableData() {
        ObservableList<StudentModel> data = FXCollections.observableArrayList();

        // Get the dynamic logged-in instructor ID
        String loggedInInstructorId = LogInController.getLoggedInUserId();

        if (loggedInInstructorId != null && !loggedInInstructorId.isEmpty()) {
            String query = """
                    SELECT
                    s.S_STUDENTID,
                    s.S_FIRST_NAME,
                     s.S_MIDDLE_NAME,
                     s.S_LAST_NAME,
                     a.attend_Status
                    FROM
                    attendances a
                     JOIN
                    student s
                    ON
                    a.S_STUDENTID = s.S_STUDENTID
                     WHERE
                      a.L_LECTUREID = ?
                    """;

            try (Connection connection = dbCon.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, instructorLecturesController.selectedlecture.getID());

                ResultSet resultSet = preparedStatement.executeQuery();

                // Add course and group data to the observable list
                while (resultSet.next()) {
                    String attSID = resultSet.getString("s_StudentID");
                    String firstName = resultSet.getString("S_FIRST_NAME");
                    String middleName = resultSet.getString("S_MIDDLE_NAME");
                    String lastName = resultSet.getString("S_LAST_NAME");
                    String attStatus = resultSet.getString("attend_Status");
                    String fullName = firstName + " " + middleName + " " + lastName;

                    data.add(new StudentModel(fullName, attSID, attStatus));
                }
            } catch (Exception e) {
                System.err.println("Error loading table data: " + e.getMessage());
            }
        } else {
            System.err.println("No logged-in instructor ID found.");
        }

        // Set the items in the table view
        tableView.setItems(data);
    }

    @FXML
    private void ChangeAttendanseStatus(ActionEvent event) {
        // Get the selected student from the table
        StudentModel selectedStudent = tableView.getSelectionModel().getSelectedItem();

        if (selectedStudent != null) {
            // Create a ComboBox for changing attendance status
            ComboBox<String> statusComboBox = new ComboBox<>();
            statusComboBox.getItems().addAll("Attended", "Absent", "With Excuse");
            statusComboBox.setValue(selectedStudent.getStudentStatus()); // Set current status as default

            // Show the ComboBox in a dialog or inline
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Change Attendance Status");
            alert.setHeaderText("Change status for: " + selectedStudent.getStudentName());
            alert.setContentText("Select the new attendance status:");
            alert.getDialogPane().setContent(statusComboBox);

            // Wait for user confirmation
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String newStatus = statusComboBox.getValue();

                    if (newStatus != null && !newStatus.equals(selectedStudent.getStudentStatus())) {
                        // Update the database
                        String query = """
                                    UPDATE attendances
                                    SET attend_Status = ?
                                    WHERE S_STUDENTID = ? AND L_LECTUREID = ?
                                """;

                        try (Connection connection = dbCon.getConnection();
                                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                            preparedStatement.setString(1, newStatus);
                            preparedStatement.setString(2, selectedStudent.getStudentId());
                            preparedStatement.setString(3, instructorLecturesController.selectedlecture.getID());

                            int rowsUpdated = preparedStatement.executeUpdate();

                            if (rowsUpdated > 0) {
                                // Update the local TableView model
                                selectedStudent.setStudentStatus(newStatus);
                                tableView.refresh(); // Refresh the table to reflect the changes
                                System.out.println(
                                        "Attendance status updated successfully for " + selectedStudent.getStudentId());
                            } else {
                                System.err.println(
                                        "Failed to update attendance status for " + selectedStudent.getStudentId());
                            }
                        } catch (Exception e) {
                            System.err.println("Error updating attendance status: " + e.getMessage());
                        }
                    }
                }
            });
        } else {
            System.err.println("No student selected. Please select a student to change attendance status.");
        }
    }

    public void SwitchToLogIn(Event e) throws IOException {
        SceneController.SwitchTo(0, e);
    }

    public void SwitchToInstructorHome(Event e) throws IOException {

        SceneController.SwitchTo(2, e);
    }

    public void SwitchToInstructorLecture(Event e) throws IOException {

        SceneController.SwitchTo(3, e);
    }

    public void SwitchToNFC(Event e) throws IOException {

        SceneController.SwitchTo(5, e);
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
}
