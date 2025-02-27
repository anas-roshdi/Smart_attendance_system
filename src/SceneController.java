
import java.io.IOException;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private String Css = this.getClass().getResource("CssFile.css").toExternalForm();

    public void SwitchTo(int i, Event e) throws IOException {
        switch (i) {
            case 0:
                root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
                break;
            case 1:
                root = FXMLLoader.load(getClass().getResource("UserProfile.fxml"));
                break;
            case 2:
                root = FXMLLoader.load(getClass().getResource("InstructorHome.fxml"));
                break;
            case 3:
                root = FXMLLoader.load(getClass().getResource("instructorLectures.fxml"));
                break;
            case 4:
                root = FXMLLoader.load(getClass().getResource("instructorAttendances.fxml"));
                break;
            case 5:
                root = FXMLLoader.load(getClass().getResource("InstructorNFC.fxml"));
                break;
            case 6:
                root = FXMLLoader.load(getClass().getResource("StudentHome.fxml"));
                break;
            case 7:
                root = FXMLLoader.load(getClass().getResource("studentAttendance.fxml"));
                break;
            case 8:
                root = FXMLLoader.load(getClass().getResource("AdminHome.fxml"));
                break;
            case 9:
                root = FXMLLoader.load(getClass().getResource("AdminAdd.fxml"));
                break;
            case 10:
                root = FXMLLoader.load(getClass().getResource("AdminEdit.fxml"));
                break;
            case 11:
                root = FXMLLoader.load(getClass().getResource("AdminDelete.fxml"));
                break;
            default:
                break;
        }

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Css);
        stage.setScene(scene);
        stage.show();
    }

}
