package GPAcalculator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    Pane p;

    @FXML
    TextField studentID;

    @FXML
    TextField passCode;

    @FXML
    TextField studentName;

    @FXML
    Text statusText;

    public void register() throws SQLException {

        String studentIDText = studentID.getText();
        String passCodeText = passCode.getText();
        String studentNameText = studentName.getText();

        //check if field empty
        if (studentIDText.isEmpty() || passCodeText.isEmpty() || studentNameText.isEmpty()) {
            statusText.setText("Error: Fields are empty");
        } else {
            if (passCodeText.equals(studentNameText)) {
                statusText.setText("Error: Passcode cannot be the same as Name");
            } else {
                if (studentIDText.length() > 10) {
                    statusText.setText("Error: Student ID cannot be more than 10 digits");
                } else {
                    if (checkExistUser(studentIDText)) {
                        performRegister(studentIDText,passCodeText, studentNameText);
                        closeWindow();
                    } else {
                        statusText.setText("Error: Student ID already exist");
                    }
                }
            }
        }

    }

    private boolean performRegister(String studentIDText, String passCodeText, String studentNameTxt) throws SQLException {

        PreparedStatement statement = Main.connection.prepareStatement(
                "INSERT INTO `student`(`SID`,`S_NAME`,`S_PASSWORD`) VALUES (?,?,?)"
        );
        statement.setString(1, studentIDText);
        statement.setString(2, studentNameTxt);
        statement.setString(3, passCodeText);

        int i = statement.executeUpdate();

        if (i > 0) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Complete!");
            alert.setHeaderText(null);
            alert.setContentText("Create new record for id " + studentIDText + " please login");
            alert.showAndWait();

            return true;
        }

        return false;

    }

    private boolean checkExistUser(String studentIDText) throws SQLException {

        PreparedStatement statement = Main.connection.prepareStatement(
                "SELECT `SID` FROM `student` WHERE `SID` = ?"
        );
        statement.setString(1, studentIDText);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return false;
        }

        return true;

    }

    public void closeWindow() {
        Stage stage = (Stage) p.getScene().getWindow();
        stage.close();
    }

}
