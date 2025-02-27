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
import javafx.scene.layout.VBox;


public class InstructorHomeController {

    // FXML components for UI
    @FXML
    private Button CoursesBt; // Button for navigating to courses
    @FXML
    private Button ProfileBt; // Button for navigating to profile
    @FXML
    private Button LogOutBt; // Button for logging out
    @FXML
    private Label ins_nameLB; // Label to display the instructor's name
    @FXML
    private VBox Vbox; // VBox container for UI components
    @FXML
    private TableView<CourseGroupModel> tableView; // Table view to show courses and groups
    @FXML
    private TableColumn<CourseGroupModel, String> courseCol; // Column for course names
    @FXML
    private TableColumn<CourseGroupModel, String> groupCol; // Column for group names
    @FXML
    private TableColumn<CourseGroupModel, Void> vewCol; // Column for view buttons

    private DBcon dbCon = new DBcon(); // Database connection handler
    private SceneController SceneController = new SceneController(); // Scene controller for scene switching

    // Initialize the controller
    @FXML
    public void initialize() {
        // Set cell value factories for course and group columns
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        groupCol.setCellValueFactory(new PropertyValueFactory<>("groupName"));

            // Load instructor's name based on the logged-in ID
        loadInstructorName();

        // Load table data related to the logged-in instructor's courses and groups
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

    
    // Load the table data for courses and groups assigned to the logged-in instructor
    private void loadTableData() {
        ObservableList<CourseGroupModel> data = FXCollections.observableArrayList();

        // Get the dynamic logged-in instructor ID
        String loggedInInstructorId = LogInController.getLoggedInUserId();

        if (loggedInInstructorId != null && !loggedInInstructorId.isEmpty()) {
            // Query to fetch courses and groups assigned to the logged-in instructor
            String query = """
                SELECT course.C_COURSENAME, groub.G_GROUPNAME,groub.G_GROUPID,course.C_COURSEID 
                FROM groub
                JOIN course ON groub.C_COURSEID = course.C_COURSEID
                WHERE groub.i_instructorID = ?
            """;

            try (Connection connection = dbCon.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, loggedInInstructorId);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Add course and group data to the observable list
                while (resultSet.next()) {
                    String courseName = resultSet.getString("C_COURSENAME");
                    String groupName = resultSet.getString("G_GROUPNAME");
                    
                    String groupID = resultSet.getString("g_GroupID");
                    String coursID = resultSet.getString("c_CourseID");


                    data.add(new CourseGroupModel(courseName, groupName,coursID,groupID));
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

    public static CourseGroupModel selectedCourseGroup;
    // Handle the "View" button click in the table
    @FXML
    private void handleViewAction(ActionEvent e)  {
        // Get the selected course group from the table
        selectedCourseGroup = tableView.getSelectionModel().getSelectedItem();

        if (selectedCourseGroup != null) {
            System.out.println("View button clicked for: " + selectedCourseGroup.getCourseId());
            System.out.println("View button clicked for: " + selectedCourseGroup.getCourseName());
            System.out.println("View button clicked for: " + selectedCourseGroup.getGroupId());
            System.out.println("View button clicked for: " + selectedCourseGroup.getGroupName());
            
            try {
                SceneController.SwitchTo(3, e);
                
                
                
                
                // You can add logic here to navigate to another screen or show course details
            } catch (IOException ex) {
                Logger.getLogger(InstructorHomeController.class.getName()).log(Level.SEVERE, null, ex);
            }
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

}
