import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.cell.PropertyValueFactory;
public class studentAttendancesController {

    @FXML
    private Button LogOutBt;

    @FXML
    private Button ProfileBt;

    @FXML
    private TableColumn<StudentAttendanceModel, String> TimeCol;

    @FXML
    private TableColumn<StudentAttendanceModel, String> dateCol;
        @FXML
    private TableColumn<StudentAttendanceModel, String> weekCol;

    @FXML
    private Label ins_nameLB;

    @FXML
    private AnchorPane root1;

    @FXML
    private TableColumn<StudentAttendanceModel, String> statusCol;

    @FXML
    private TableView<StudentAttendanceModel> tableView;

    private DBcon dbCon = new DBcon(); // Database connection handler
    private SceneController SceneController = new SceneController(); // Scene controller for scene switching
    @FXML
            // Initialize the controller
    public void initialize() {
        // Set cell value factories for course and group columns
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TimeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("studentStatus"));
         weekCol.setCellValueFactory(new PropertyValueFactory<>("week"));



            // Load instructor's name based on the logged-in ID
        loadStudentName();

        // Load table data related to the logged-in instructor's courses and groups
        loadTableData();
    }
     // Load the instructor's name based on the logged-in ID
    private void loadStudentName() {
        // Get the logged-in instructor's ID (you can replace it dynamically)
        String loggedInStudentId = LogInController.getLoggedInUserId();

        if (loggedInStudentId != null && !loggedInStudentId.isEmpty()) {
            // Query to fetch the instructor's full name
            String query = """
                SELECT s_First_Name, s_middle_Name, s_Last_Name
                FROM student
                WHERE s_StudentID = ?
            """;

            try (Connection connection = dbCon.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, loggedInStudentId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Get first, middle, and last names from the result set
                    String firstName = resultSet.getString("s_First_Name");
                    String middleName = resultSet.getString("s_middle_Name");
                    String lastName = resultSet.getString("s_Last_Name");

                    // Set the full name in the label
                    ins_nameLB.setText(firstName + " " + middleName + " " + lastName);
                }
            } catch (Exception e) {
                System.err.println("Error loading Student name: " + e.getMessage());
            }
        } else {
            System.err.println("No logged-in Student ID found.");
        }
    }
    // Load the table data for courses and groups assigned to the logged-in instructor
    private void loadTableData() {
        ObservableList<StudentAttendanceModel> data = FXCollections.observableArrayList();

        // Get the dynamic logged-in instructor ID
        String loggedInStudentId = LogInController.getLoggedInUserId();
 

           
        if (loggedInStudentId != null && !loggedInStudentId.isEmpty()) {
            // Query to fetch courses and groups assigned to the logged-in instructor
            String query = """      
                           SELECT l.l_week,l.l_Date, l.l_Time, a.attend_Status
                                               FROM lectures l
                                               JOIN attendances a ON l.l_LectureID = a.l_LectureID
                                               WHERE l.g_GroupID = ? 
                                               AND a.s_StudentID = ?

            """;

             try (Connection connection = dbCon.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, StudentHomeController.selectedCourseGroup.getGroupId());
                preparedStatement.setString(2, loggedInStudentId);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Add data to the observable list
                while (resultSet.next()) {
                    String lecDate = resultSet.getString("l_Date");
                    String lecTime = resultSet.getString("l_Time");
                    String attendStatus = resultSet.getString("attend_Status");
                    String lecWeek = resultSet.getString("l_week");
                    


                    data.add(new StudentAttendanceModel(lecWeek, lecTime,attendStatus,lecDate));
                                   }
            } catch (Exception e) {
                System.err.println("Error loading table data: " + e.getMessage());
            }
        } else {
            System.err.println("No logged-in Student ID found.");
        }

        // Set the items in the table view
        tableView.setItems(data);
    }
    public static StudentAttendanceModel selectedLectuerAttend;
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
   @FXML
    public void SwitchToStudentPage(ActionEvent event) {
        try {
            SceneController.SwitchTo(6, event); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
