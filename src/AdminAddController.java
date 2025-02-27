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
import javafx.scene.paint.Color;

public class AdminAddController {
    // FXML components for UI
    @FXML
    private Button ProfileBt; // Button for navigating to profile
    @FXML
    private Button LogOutBt; // Button for logging out
    @FXML
    private Button BackBt; // Button to work on add
    @FXML
    private Button SaveBt; // Button to work on edit
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
    private Label ad_nameLB; // Label to display the name

    private int adminchoice;
    private SceneController SceneController = new SceneController(); // Scene controller for scene switching
    private DBcon dbCon = new DBcon(); // Database connection handler

    // Initialize the controller
    @FXML
    public void initialize() {
        adminchoice = adminHomeController.AdminChoice();

        if (adminchoice == 0) {
            user_msg.setText("Enter the information of the instructor");
        } else if (adminchoice == 1) {
            user_msg.setText("Enter the information of the course");
            Tf3.setVisible(false);
            Tf4.setVisible(false);
            Tf5.setVisible(false);
            Tf6.setVisible(false);
            Tf7.setVisible(false);

            Lb3.setVisible(false);
            Lb4.setVisible(false);
            Lb5.setVisible(false);
            Lb6.setVisible(false);
            Lb7.setVisible(false);

            Lb1.setText("Course ID: ");
            Lb2.setText("Course Name: ");

        } else if (adminchoice == 2) {
            user_msg.setText("Enter the information of the group");
            Cbox1.setVisible(true);
            Cbox1.setLayoutY(238);
            Cbox2.setVisible(true);
            Cbox2.setLayoutY(238);

            Tf1.setVisible(false);
            Tf2.setVisible(false);
            Tf5.setVisible(false);
            Tf6.setVisible(false);
            Tf7.setVisible(false);

            Lb5.setVisible(false);
            Lb6.setVisible(false);
            Lb7.setVisible(false);

            Lb1.setText("Select course");
            Lb2.setText("Select instructor");
            Lb3.setText("Group ID");
            Lb4.setText("Group Name");

            loadCboxes();
        }

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

    public void SaveBtController() {
        if (isDataFull()) {
            user_msg.setTextFill(Color.BLACK);
            if (adminchoice == 0 || adminchoice == 3) {
                AddNewStudentOrInstructor();
            } else if (adminchoice == 1) {
                AddNewCourse();
            } else {
                AddNewGroup();
            }
        } else {
            user_msg.setText("please fill all the data");
            user_msg.setTextFill(Color.RED);
        }
    }

    public void AddNewStudentOrInstructor() {
        String query = getQuery(adminchoice);
        try (Connection connection = dbCon.getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, Tf5.getText());
            preparedStatement.setString(2, Tf7.getText());
            preparedStatement.setString(3, Tf1.getText());
            preparedStatement.setString(4, Tf3.getText());
            preparedStatement.setString(5, Tf2.getText());
            preparedStatement.setString(6, Tf6.getText());
            preparedStatement.setString(7, Tf4.getText());

            // Execute the update (insert data)
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                user_msg.setTextFill(Color.BLUE);
                if(adminchoice == 3){
                    user_msg.setText("Student " + Tf5.getText() + " inserted successfully!");
                } else {
                    user_msg.setText("instructor " + Tf5.getText() + " inserted successfully!");
                }
                
                clearData();
            } else {
                System.out.println("No rows affected.");
            }

        } catch (Exception e) {
            System.err.println("Error in adding new : " + e.getMessage());
        }

    }

    public void AddNewCourse() {

        String query = getQuery(adminchoice);
        try (Connection connection = dbCon.getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, Tf1.getText());
            preparedStatement.setString(2, Tf2.getText());

            // Execute the update (insert data)
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                user_msg.setTextFill(Color.BLUE);
                user_msg.setText("Course " + Tf1.getText() + " inserted successfully!");
                clearData();
            } else {
                System.out.println("No rows affected.");
            }

        } catch (Exception e) {
            System.err.println("Error in adding new : " + e.getMessage());
        }

    }

    public void AddNewGroup() {
        String query = getQuery(adminchoice);
        try (Connection connection = dbCon.getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, Tf3.getText());
            preparedStatement.setString(2, Tf4.getText());
            preparedStatement.setString(3, getCourseId(Cbox1.getValue()));
            preparedStatement.setString(4, getInstructorID(Cbox2.getValue()));

            // Execute the update (insert data)
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                user_msg.setTextFill(Color.BLUE);
                user_msg.setText("Group  " + Tf3.getText() + " inserted successfully!");
                clearData();
            } else {
                System.out.println("No rows affected.");
            }

        } catch (Exception e) {
            System.err.println("Error in adding new : " + e.getMessage());
        }
    }

    public void loadCboxes() {
        String query = "SELECT c_CourseName FROM course";

        // Observable list to hold the database values
        ObservableList<String> choiceBoxData = FXCollections.observableArrayList();

        // Database connection and query execution
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

        // Cbox2
        String query2 = "SELECT i_First_Name, i_middle_Name, i_Last_Name FROM instructor";

        // Observable list to hold the database values
        ObservableList<String> choiceBoxData2 = FXCollections.observableArrayList();
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query2);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            // Fetch data from ResultSet and add to ObservableList
            while (resultSet.next()) {

                String firstName = resultSet.getString("I_FIRST_NAME");
                String middleName = resultSet.getString("I_MIDDLE_NAME");
                String lastName = resultSet.getString("I_LAST_NAME");

                String Fullname = firstName + " " + middleName + " " + lastName;
                choiceBoxData2.add(Fullname);
            }

            // Set the items for the ChoiceBox
            Cbox2.setItems(choiceBoxData2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private String getQuery(int i) {
        if (i == 0) {
            return """

                    INSERT INTO instructor (i_instructorID, i_password, i_First_Name, i_middle_Name, i_Last_Name, i_Email, i_phone)
                    VALUES (?, ?, ?, ?, ?, ?, ?);

                    """;
        } else if (i == 1) {
            return """

                    INSERT INTO course (c_CourseID, c_CourseName)
                    VALUES (?, ?);

                    """;
        } else if (i == 2) {
            return """

                    INSERT INTO groub (g_GroupID, g_GroupName, c_CourseID, i_instructorID)
                    VALUES (?, ?, ?, ?);

                    """;
        } else {
            return """

                    INSERT INTO student (s_StudentID, s_password, s_First_Name, s_middle_Name, s_Last_Name, s_Email, s_phone)
                    VALUES (?, ?, ?, ?, ?, ?, ?);

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

    public String getInstructorID(String Name) {
        String query = "SELECT i_instructorID FROM instructor where i_First_Name = ? AND i_middle_Name = ? AND i_Last_Name = ? ";
        String[] parts = Name.split(" ", 3);
        // Database connection and query execution
        try (Connection connection = dbCon.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, parts[0]);
            preparedStatement.setString(2, parts[1]);
            preparedStatement.setString(3, parts[2]);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String ID = resultSet.getString("i_instructorID");
                return ID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isDataFull() {
        if (adminchoice == 0 || adminchoice == 3) {
            return (!Tf1.getText().isBlank() && !Tf2.getText().isBlank() && !Tf3.getText().isBlank()
                    && !Tf4.getText().isBlank() && !Tf5.getText().isBlank() && !Tf6.getText().isBlank()
                    && !Tf7.getText().isBlank());
        } else if (adminchoice == 1) {
            return (!Tf1.getText().isBlank() && !Tf2.getText().isBlank());
        } else if (adminchoice == 2) {
            return (!Tf3.getText().isBlank() && !Tf4.getText().isBlank()
                    && !Cbox1.getValue().isBlank() && !Cbox2.getValue().isBlank());
        }
        return false;
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
