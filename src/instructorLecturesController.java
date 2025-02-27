import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class instructorLecturesController {

    static LecturesModel selectedlecture;
    @FXML
    private Label ins_nameLB;
    @FXML
    private TableView<LecturesModel> tableView; // Table view to show LecturesModel
    @FXML
    private TableColumn<LecturesModel, String> dateCol; // Column for LecturesModel date
    @FXML
    private TableColumn<LecturesModel, String> timeCol; // Column for week
    @FXML
    private TableColumn<LecturesModel, String> topicCol;
     @FXML
    private TableColumn<LecturesModel, String> weekCol;
    

    private DBcon dbCon = new DBcon(); // Database connection handler
    private SceneController SceneController = new SceneController(); // Scene controller for scene switching

    // Initialize the controller
    @FXML
    public void initialize() {
        // Set cell value factories for course and group columns
        dateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("Time"));
        topicCol.setCellValueFactory(new PropertyValueFactory<>("topic"));
        weekCol.setCellValueFactory(new PropertyValueFactory<>("week"));


        
    
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
        ObservableList<LecturesModel> data = FXCollections.observableArrayList();

        // Get the dynamic logged-in instructor ID
        String loggedInInstructorId = LogInController.getLoggedInUserId();

        if (loggedInInstructorId != null && !loggedInInstructorId.isEmpty()) {
            // Query to fetch LecturesModel assigned to the logged-in instructor
            String query = """
            SELECT l.l_Date, l.l_Time , l.l_topic,l.l_LectureID,l.l_week
            FROM lectures l 
            WHERE l.g_GroupID = ? AND l.c_CourseID = ?   

            """;


            try (Connection connection = dbCon.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1,InstructorHomeController.selectedCourseGroup.getGroupId());
                preparedStatement.setString(2,InstructorHomeController.selectedCourseGroup.getCourseId());
                
                ResultSet resultSet = preparedStatement.executeQuery();

                // Add course and group data to the observable list
                while (resultSet.next()) {
                    String lecDate = resultSet.getString("l_Date");
                    String lecTime = resultSet.getString("l_Time");
                     String lecTopic = resultSet.getString("l_topic");
                     String lecID = resultSet.getString("l_LectureID");
                      String lecWeek = resultSet.getString("l_week");

                    data.add(new LecturesModel(lecDate, lecTime,lecTopic,lecID,lecWeek));
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

    // Handle the "View" button click in the table
    @FXML
    private void handleViewAction(ActionEvent event) {
        
        // Get the selected course group from the table
         selectedlecture = tableView.getSelectionModel().getSelectedItem();

        if (selectedlecture != null) {
            System.out.println("attendance button clicked for: " + selectedlecture.getDate());
             try {
                 // You can add logic here to navigate to another screen
                 SceneController.SwitchTo(4, event);
             } catch (IOException ex) {
                 Logger.getLogger(instructorLecturesController.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
    }

    public void SwitchToLogIn(Event e) throws IOException {
        SceneController.SwitchTo(0, e);
    }
    public void SwitchToInstructorHome(Event e) throws IOException {
        SceneController.SwitchTo(2, e);
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
