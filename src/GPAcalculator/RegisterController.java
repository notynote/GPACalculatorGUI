package GPAcalculator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

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

    @FXML
    ChoiceBox<Department> departmentChoiceBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            departmentChoiceBox.setItems(getDepartmentList());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        departmentChoiceBox.getSelectionModel().selectFirst();
    }

    private ObservableList<Department> getDepartmentList() throws SQLException {
        ObservableList<Department> departments = FXCollections.observableArrayList();
        PreparedStatement departmentStatement = Main.connection.prepareStatement(
                "SELECT * FROM `department`"
        );

        ResultSet resultSet = departmentStatement.executeQuery();

        while (resultSet.next()) {
            Department department = new Department(resultSet.getInt("D_ID"),resultSet.getString("D_NAME"),resultSet.getString("LOCATION"));
            departments.add(department);
        }

        departmentStatement.close();

        return departments;
    }

    public void register() throws SQLException {

        String studentIDText = studentID.getText();
        String passCodeText = passCode.getText();
        String studentNameText = studentName.getText();
        int departmentText = departmentChoiceBox.getValue().D_ID;

        //check if field empty
        if (studentIDText.isEmpty() || passCodeText.isEmpty() || studentNameText.isEmpty()) {
            statusText.setText("Error: Fields are empty");
        } else {
            if (departmentText == 0) {
                statusText.setText("Error: Please Select Department");
            } else {
                if (studentIDText.length() > 10) {
                    statusText.setText("Error: Student ID cannot be more than 10 digits");
                } else {
                    if (checkExistUser(studentIDText)) {
                        performRegister(studentIDText,passCodeText, studentNameText, departmentText);
                        closeWindow();
                    } else {
                        statusText.setText("Error: Student ID already exist");
                    }
                }
            }
        }

    }

    private boolean performRegister(String studentIDText, String passCodeText, String studentNameTxt, int departmentText) throws SQLException {

        PreparedStatement statement = Main.connection.prepareStatement(
                "INSERT INTO `student`(`SID`,`S_NAME`,`D_ID`,`S_PASSWORD`) VALUES (?,?,?,?)"
        );
        statement.setString(1, studentIDText);
        statement.setString(2, studentNameTxt);
        statement.setInt(3, departmentText);
        statement.setString(4, passCodeText);

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
