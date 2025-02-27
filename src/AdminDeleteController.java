import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class AdminDeleteController {

    // FXML components for UI
    @FXML
    private Button ProfileBt; // Button for navigating to profile
    @FXML
    private Button LogOutBt; // Button for logging out
    @FXML
    private Button BackBt;
    @FXML
    private Button DeleteBt;

    @FXML
    private AnchorPane deletePane;
    @FXML
    private Label Lb1;
    @FXML
    private Label Lb2;
    @FXML
    private Label Lb3;
    @FXML
    private Label Lb4;
    @FXML
    private Label Lb5;
    @FXML
    private Label Lb6;
    @FXML
    private Label Lb7;

    @FXML
    private TextField Tf1;
    @FXML
    private TextField Tf2;
    @FXML
    private TextField Tf3;
    @FXML
    private TextField Tf4;
    @FXML
    private TextField Tf5;
    @FXML
    private TextField Tf6;
    @FXML
    private TextField Tf7;

    @FXML
    private ChoiceBox<String> Cbox1;
    @FXML
    private ChoiceBox<String> Cbox2;

    @FXML
    private Label user_msg;
    @FXML
    private Label user_msg2;
    @FXML
    private Label user_msg4;

    @FXML
    private Label ad_nameLB; // Label to display the name

    private int adminchoice;
    private SceneController SceneController = new SceneController(); // Scene controller for scene switching
    private DBcon dbCon = new DBcon(); // Database connection handler

    // Initialize the controller
    @FXML
    public void initialize() {
        adminchoice = adminHomeController.AdminChoice();

        if (adminchoice == 0) {
            user_msg.setText("Information of the instructor");
            user_msg2.setText("Select instructor: ");
            loadInstructorCbox();
        } else if (adminchoice == 1) {
            user_msg.setText("Information of the course");
            user_msg2.setText("Select course: ");

            Tf3.setVisible(false);
            Tf4.setVisible(false);
            Tf5.setVisible(false);
            Tf6.setVisible(false);
            Tf7.setVisible(false);

            Lb1.setText("Course ID");
            Lb2.setText("Course Name");
            Lb3.setVisible(false);
            Lb4.setVisible(false);
            Lb5.setVisible(false);
            Lb6.setVisible(false);
            Lb7.setVisible(false);

            loadCourseCbox();

        } else if (adminchoice == 2) {
            user_msg.setText("Information of the group");
            user_msg2.setText("Select course: ");
            deletePane.setLayoutX(600);

            Tf5.setVisible(false);
            Tf6.setVisible(false);
            Tf7.setVisible(false);

            Lb1.setText("Group ID");
            Lb2.setText("Group Name");
            Lb3.setText("Course ID");
            Lb4.setText("Instructor Name");
            Lb5.setVisible(false);
            Lb6.setVisible(false);
            Lb7.setVisible(false);

            loadCourseCbox();
        } else {
            loadStudentCbox();
        }

        Cbox1.getSelectionModel().selectedItemProperty().addListener(
                (,oldValue, newValue) -> {
                    if (newValue != null && !newValue.equals(oldValue)) {
                        if (adminchoice == 2) {
                            user_msg4.setVisible(true);
                            Cbox2.setVisible(true);
                            loadGroupCbox();
                        } else {
                            cboxController(newValue);
                            deletePane.setVisible(true);
                        }
                    }
                });

        Cbox2.getSelectionModel().selectedItemProperty().addListener(
                (, oldValue, newValue) -> {
                    if (newValue != null && !newValue.equals(oldValue)) {
                        cboxController(newValue);
                        deletePane.setVisible(true);

                    }
                });

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

    public void cboxController(String str) {
        if (adminchoice == 0) {
            fillInstructorData(str);
        } else if (adminchoice == 1) {
            fillCourseData(str);
        } else if (adminchoice == 2) {
            fillGroupData(str);
        } else {
            fillStudentData(str);
        }
    }

    public void deleteBtController() {
        deleteData();

        clearData();
    }

    public void clearData() {

        Tf1.clear();
        Tf2.clear();
        Tf3.clear();
        Tf4.clear();
        Tf5.clear();
        Tf6.clear();
        Tf7.clear();

    }

    public void loadStudentCbox() {
        String query = "SELECT s_First_Name, s_middle_Name, s_Last_Name FROM student";

        // Observable list to hold the database values
        ObservableList<String> choiceBoxData = FXCollections.observableArrayList();
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            ResultSet resultSet = preparedStatement.executeQuery();
            // Fetch data from ResultSet and add to ObservableList
            while (resultSet.next()) {

                String firstName = resultSet.getString("s_FIRST_NAME");
                String middleName = resultSet.getString("s_MIDDLE_NAME");
                String lastName = resultSet.getString("s_LAST_NAME");

                String Fullname = firstName + " " + middleName + " " + lastName;
                choiceBoxData.add(Fullname);
            }

            // Set the items for the ChoiceBox

            Cbox1.setItems(choiceBoxData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadInstructorCbox() {
        String query = "SELECT i_First_Name, i_middle_Name, i_Last_Name FROM instructor";

        // Observable list to hold the database values
        ObservableList<String> choiceBoxData = FXCollections.observableArrayList();
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            // Fetch data from ResultSet and add to ObservableList
            while (resultSet.next()) {

                String firstName = resultSet.getString("i_FIRST_NAME");
                String middleName = resultSet.getString("i_MIDDLE_NAME");
                String lastName = resultSet.getString("i_LAST_NAME");

                String Fullname = firstName + " " + middleName + " " + lastName;
                choiceBoxData.add(Fullname);
            }

            // Set the items for the ChoiceBox

            Cbox1.setItems(choiceBoxData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadCourseCbox() {
        String query = "SELECT c_CourseName FROM course";

        // Observable list to hold the database values
        ObservableList<String> choiceBoxData = FXCollections.observableArrayList();
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            // Fetch data from ResultSet and add to ObservableList
            while (resultSet.next()) {
                String value = resultSet.getString("c_CourseName");
                choiceBoxData.add(value);
            }

            // Set the items for the ChoiceBox
            Cbox1.setItems(choiceBoxData);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void loadGroupCbox() {
        String query = "SELECT g_GroupID, g_GroupName, c_CourseID, i_instructorID FROM groub WHERE c_CourseID = ? ;";

        // Observable list to hold the database values
        ObservableList<String> choiceBoxData = FXCollections.observableArrayList();
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, getCourseId(Cbox1.getValue()));
            ResultSet resultSet = preparedStatement.executeQuery();
            // Fetch data from ResultSet and add to ObservableList
            while (resultSet.next()) {
                String value = resultSet.getString("g_GroupName");
                choiceBoxData.add(value);
            }

            // Set the items for the ChoiceBox
            Cbox2.setItems(choiceBoxData);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void fillStudentData(String StudentName) {
        String query = """
                SELECT s_StudentID, s_password, s_First_Name, s_middle_Name, s_Last_Name, s_Email, s_phone FROM Student
                 where s_First_Name = ? AND s_middle_Name = ? AND s_Last_Name = ?
                         """;

        String[] parts = StudentName.split(" ", 3);
        // Database connection and query execution
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, parts[0]);
            preparedStatement.setString(2, parts[1]);
            preparedStatement.setString(3, parts[2]);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Tf1.setText(resultSet.getString("s_First_Name"));
                Tf2.setText(resultSet.getString("s_Last_Name"));
                Tf3.setText(resultSet.getString("s_middle_Name"));
                Tf4.setText(resultSet.getString("s_phone"));
                Tf5.setText(resultSet.getString("s_StudentID"));
                Tf6.setText(resultSet.getString("s_Email"));
                Tf7.setText(resultSet.getString("s_password"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void fillInstructorData(String instructorName) {
        String query = """
                SELECT i_instructorID, i_password, i_First_Name, i_middle_Name, i_Last_Name, i_Email, i_phone FROM instructor
                 where i_First_Name = ? AND i_middle_Name = ? AND i_Last_Name = ?
                         """;

        String[] parts = instructorName.split(" ", 3);
        // Database connection and query execution
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, parts[0]);
            preparedStatement.setString(2, parts[1]);
            preparedStatement.setString(3, parts[2]);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Tf1.setText(resultSet.getString("i_First_Name"));
                Tf2.setText(resultSet.getString("i_Last_Name"));
                Tf3.setText(resultSet.getString("i_middle_Name"));
                Tf4.setText(resultSet.getString("i_phone"));
                Tf5.setText(resultSet.getString("i_instructorID"));
                Tf6.setText(resultSet.getString("i_Email"));
                Tf7.setText(resultSet.getString("i_password"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void fillCourseData(String CourseName) {
        String query = """
                Select c_CourseID, c_CourseName FROM course
                    WHERE c_CourseName = ? ;
                """;
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, CourseName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Tf1.setText(resultSet.getString("c_CourseID"));
                Tf2.setText(resultSet.getString("c_CourseName"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fillGroupData(String GroupName) {
        String query = """
                SELECT g_GroupID, g_GroupName, c_CourseID, i_instructorID FROM groub
                WHERE g_GroupName = ? AND c_CourseID = ? ;
                """;
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, GroupName);
            preparedStatement.setString(2, getCourseId(Cbox1.getValue()));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Tf1.setText(resultSet.getString("g_GroupID"));
                Tf2.setText(resultSet.getString("g_GroupName"));
                Tf3.setText(resultSet.getString("c_CourseID"));
                Tf4.setText(getInstructorName(resultSet.getString("i_instructorID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private String getQuery(int i) {
        if (i == 0) {
            return """

                    DELETE FROM instructor WHERE i_instructorID = ?;

                     """;
        } else if (i == 1) {
            return """

                    DELETE FROM course WHERE c_CourseID = ?;

                     """;
        } else if (i == 2) {
            return """

                    DELETE FROM groub WHERE g_GroupID = ?;

                    """;
        } else {
            return """

                    DELETE FROM student WHERE s_StudentID = ?;

                    """;
        }
    }

    public String getCourseId(String courseName) {
        String query = "SELECT c_CourseID FROM course where c_CourseName = ? ";

        // Database connection and query execution
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, courseName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String ID = resultSet.getString("c_CourseID");
                return ID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getInstructorName(String ID) {

        String query = "SELECT i_First_Name, i_middle_Name, i_Last_Name FROM instructor WHERE i_instructorID = ? ";

        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Fetch data from ResultSet and add to ObservableList
            while (resultSet.next()) {

                String firstName = resultSet.getString("i_FIRST_NAME");
                String middleName = resultSet.getString("i_MIDDLE_NAME");
                String lastName = resultSet.getString("i_LAST_NAME");

                return firstName + " " + middleName + " " + lastName;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteData() {
        String query = getQuery(adminchoice);
        try (Connection connection = dbCon.getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if(adminchoice == 0 || adminchoice ==3){
                preparedStatement.setString(1, Tf5.getText());
            } else {
                preparedStatement.setString(1, Tf1.getText());
            }
            

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                user_msg.setTextFill(Color.BLUE);
                if (adminchoice == 0) {
                    user_msg.setText("instructor " + Tf5.getText() + " deleted successfully!");
                } else if (adminchoice == 1) {
                    user_msg.setText("Course " + Tf1.getText() + " deleted successfully!");
                } else if (adminchoice == 2) {
                    user_msg.setText("Group " + Tf1.getText() + " deleted successfully!");
                } else {
                    user_msg.setText("Student " + Tf5.getText() + " updated successfully!");
                }

            } else {
                System.out.println("No rows affected.");
            }

        } catch (Exception e) {
            System.err.println("Error in deleting : " + e.getMessage());
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

    @FXML
    public void SwitchToUserProfile(Event e) throws IOException {
        SceneController.SwitchTo(1, e);
    }

    @FXML
    public void SwitchToAdminHome(Event e) throws IOException {
        SceneController.SwitchTo(8, e);
    }

}