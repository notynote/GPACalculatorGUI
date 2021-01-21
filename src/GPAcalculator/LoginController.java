package GPAcalculator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    Pane p;

    @FXML
    TextField studentID;

    @FXML
    PasswordField passCode;

    @FXML
    Button loginBtn;

    @FXML
    Button registerBtn;

    @FXML
    Text statusText;

    public static String loggedInStudentID;

    //login
    public void login() throws SQLException, IOException {

        String studentIDText = studentID.getText();
        String passCodeText = passCode.getText();

        //check if fields empty
        if (studentIDText.isEmpty() || passCodeText.isEmpty()) {
            statusText.setText("Error: Student ID or Passcode is empty");
        } else {

            if (checkUser(studentIDText,passCodeText)) {
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("FUCK YEAH!");
//                alert.setHeaderText(null);
//                alert.setContentText("YOU FUCKING LOGGED IN WITH STUDENT ID " + studentIDText);
//                alert.showAndWait();

                loggedInStudentID = studentIDText;

                FXMLLoader ld = new FXMLLoader();
                Pane root = ld.load(getClass().getResource("MainMenu.fxml").openStream());

                MainMenuController controller = ld.getController();

                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Main Menu");
                stage.setScene(scene);

                studentID.clear();
                passCode.clear();

                p.setDisable(true);
                stage.showAndWait();
                p.setDisable(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("FUCK NO!");
                alert.setHeaderText(null);
                alert.setContentText("NOPE NOT THAT PASSCODE");
                alert.showAndWait();
            }

        }

    }

    public void register() throws IOException {
        FXMLLoader ld = new FXMLLoader();
        Pane root = ld.load(getClass().getResource("Register.fxml").openStream());

        RegisterController controller = ld.getController();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Register");
        stage.setScene(scene);

        studentID.clear();
        passCode.clear();

        p.setDisable(true);
        stage.showAndWait();
        p.setDisable(false);
    }



    private boolean checkUser(String student, String pass) throws SQLException {
        PreparedStatement statement = Main.connection.prepareStatement(
                "SELECT `SID`, `S_Password` FROM `student` WHERE `SID` = ?"
        );
        statement.setString(1, student);

        ResultSet resultSet = statement.executeQuery();

        PreparedStatement logStatement = Main.connection.prepareStatement(
                "INSERT INTO `login_log`(`SID`,`Login_status`) VALUES (?,?)"
        );

        logStatement.setString(1, student);

        while (resultSet.next()) {
            if (resultSet.getString("S_Password").equals(pass)) {

                logStatement.setString(2, "Success");
                logStatement.executeUpdate();
                logStatement.close();

                return true;
            } else {

                logStatement.setString(2, "Failed");
                logStatement.executeUpdate();
                logStatement.close();

                return false;
            }
        }

        return false;
    }

    public void closeProgram() {
        System.exit(0);
    }

}
