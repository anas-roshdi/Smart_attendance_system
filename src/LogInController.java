
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;





public class LogInController {
    @FXML
    private TextField passField;
    @FXML
    private TextField nameField;
    @FXML
    private Label userMessage;
    @FXML
    private Button Loginbt;

    private static String userID;

    // obj to change the screen using methods
    SceneController SceneController = new SceneController();

    // statling butons
    public void BtEffectIn() {
        Loginbt.setStyle(Loginbt.getStyle() +
                "-fx-background-color:rgb(0, 7, 141);");
    }

    public void BtEffectOut() {
        Loginbt.setStyle(Loginbt.getStyle() +
                "-fx-background-color:rgb(59, 56, 158);");
    }


    public void Login(ActionEvent e) throws IOException{
            // Retrieve user input
            String name = nameField.getText();
            String pass = passField.getText();
            int i;
        
            // Database connection setup
            DBcon dbcon = new DBcon();
            String query;
            if(name.startsWith("a")){
                i=0;
                query = getQuery(i);
            } else if(name.startsWith("i") ){
                i=1;
                query = getQuery(i);
            } else{
                i=2;
                query = getQuery(i);
            }

            try (Connection connection = dbcon.getConnection()) {
                if (connection != null) {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, pass);
    
                    // Execute query
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        getQueryResult(i,resultSet,e);
                    } else {
                        // If credentials do not match, provide feedback
                        userMessage.setText("Password and username are wrong");
                    }
                } else {
                    userMessage.setText("Unable to connect to the database");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                userMessage.setText("An error occurred: " + ex.getMessage());
            }

    }
    
    protected String getQuery(int i){
        if(i==0){
            return "SELECT a_AdminID, a_FirstName FROM administrators WHERE a_AdminID = ? AND a_PASSWORD = ?";
        } else if(i==1){
            return "SELECT I_INSTRUCTORID,  I_FIRST_NAME FROM instructor WHERE I_INSTRUCTORID = ? AND I_PASSWORD = ?";
        } else {
            return "SELECT s_First_Name,s_StudentID FROM student WHERE s_studentID = ? AND s_password = ?";
        }
    } 

    protected void getQueryResult(int i, ResultSet resultSet, ActionEvent e) throws SQLException, IOException {
        if(i==0){
            userID = resultSet.getString("a_AdminID");
            SceneController.SwitchTo(8, e);
        } else if(i==1){
            userID = resultSet.getString("I_INSTRUCTORID");
            SceneController.SwitchTo(2, e);
        } else {
            userID = resultSet.getString("s_studentID");
            SceneController.SwitchTo(6, e);
        }
    }
  
    public static String getLoggedInUserId() {
        return userID;
    }


}