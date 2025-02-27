import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {  
        String Css = this.getClass().getResource("CssFile.css").toExternalForm();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);      

        scene.getStylesheets().add(Css);
        primaryStage.setTitle("SAS");  
        primaryStage.setScene(scene);  
        primaryStage.setResizable(false); // Make the window non-resizable
        primaryStage.show();  
    }

    public static void main(String[] args) {
        launch();
    }
}
