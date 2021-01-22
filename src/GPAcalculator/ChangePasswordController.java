package GPAcalculator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordController {

    @FXML
    Pane pane;

    @FXML
    PasswordField oldPasswordField;

    @FXML
    PasswordField newPasswordField;

    @FXML
    PasswordField retypePasswordField;

    @FXML
    Text statusText;

    public void resetPassword() throws SQLException {

        Student student = MainMenuController.student;

        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String retypePassword = retypePasswordField.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || retypePassword.isEmpty()) {
            statusText.setText("Error: fields are empty");
        } else {
            if (newPassword.equals(retypePassword)) {
                if (checkMatchPassword(student, oldPassword)) {

                    //update password
                    PreparedStatement updatePasswordStatement = Main.connection.prepareStatement(
                            "UPDATE `student` SET `S_Password` = ? WHERE `SID` = ?"
                    );
                    updatePasswordStatement.setString(1,newPassword);
                    updatePasswordStatement.setString(2,student.getSid());
                    int i = updatePasswordStatement.executeUpdate();

                    if (i > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Change Password Complete!");
                        alert.setHeaderText(null);
                        alert.setContentText("Changed Password for student " + student.getsName());
                        alert.showAndWait();

                        closeWindow();
                    } else {
                        statusText.setText("Error: Updating Password");
                    }


                } else {
                    statusText.setText("Error: Old Password does not match");
                }
            } else {
                statusText.setText("Error: New Passwords do not match");
            }
        }
    }

    private boolean checkMatchPassword(Student student, String oldPassword) throws SQLException {
        PreparedStatement checkStatement = Main.connection.prepareStatement(
                "SELECT `S_Password` FROM `student` WHERE `SID` = ?"
        );

        checkStatement.setString(1, student.getSid());

        ResultSet resultSet = checkStatement.executeQuery();

        while (resultSet.next()) {
            if (oldPassword.equals(resultSet.getString("S_Password"))) {
                return true;
            }
        }

        return false;
    }

    public void closeWindow() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

}
