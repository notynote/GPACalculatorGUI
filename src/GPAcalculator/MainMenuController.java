package GPAcalculator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    Text welcomeText;

    Student student;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        welcomeText.setText("WELCOME ");
    }
}
