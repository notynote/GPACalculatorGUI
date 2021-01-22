package GPAcalculator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditInformationController implements Initializable {

    @FXML
    Pane pane;

    @FXML
    Text currentInformationText;

    @FXML
    Text statusText;

    @FXML
    TextField currentTermField;

    @FXML
    TextField studentNameField;

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

        try {
            currentInformationText.setText(getCurrentInformation(MainMenuController.student));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private String getCurrentInformation(Student student) throws SQLException {

        int currentTerm = student.getTermTaken() +1;
        String departmentName = student.getDepartment().getD_NAME();


        return "Student ID: " + student.getSid() + " - Student Name: " + student.getsName() +
                " - Current Term: " + currentTerm + " - Credit Earn: " + student.getCreditEarned() +
                "- Credit Transfer: " + student.getCreditTransfer() + " - Department: " + departmentName;

    }

    private ObservableList<Department> getDepartmentList() throws SQLException {
        ObservableList<Department> departments = FXCollections.observableArrayList();
        PreparedStatement departmentStatement = Main.connection.prepareStatement(
                "SELECT * FROM `department`"
        );

        ResultSet resultSet = departmentStatement.executeQuery();

        while (resultSet.next()) {
            Department department = new Department(resultSet.getInt("D_ID"),
                    resultSet.getString("D_NAME"),resultSet.getString("LOCATION"));
            departments.add(department);
        }

        departmentStatement.close();;

        return departments;

    }

    public void editInformation() throws SQLException {

        String currentTerm = currentTermField.getText();
        String studentName = studentNameField.getText();
        Department departmentText = departmentChoiceBox.getValue();
        String studentID = MainMenuController.student.getSid();
        int currentTermInt = 0;


        if (currentTerm.isEmpty()) {
            currentTermInt = MainMenuController.student.getTermTaken() + 1;
        }

        if (studentName.isEmpty()) {
            studentName = MainMenuController.student.getsName();
        }

        if (Helper.isNumeric(currentTerm) || currentTermInt > 0) {

            if (!currentTerm.isEmpty()) {
                currentTermInt = Integer.parseInt(currentTerm);
            }

            int termTaken = currentTermInt - 1;

            PreparedStatement statement = Main.connection.prepareStatement(
                    "UPDATE `student` SET `S_NAME` = ?, `TERM_TAKEN` = ?, `D_ID` = ? WHERE `SID` = ?"
            );

            statement.setString(1, studentName);
            statement.setInt(2, termTaken);
            statement.setInt(3, departmentText.getD_ID());
            statement.setString(4,studentID);

            int i = statement.executeUpdate();

            if (i > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Update Information Complete!");
                alert.setHeaderText(null);
                alert.setContentText("Updated Information for student " + MainMenuController.student.getsName());
                alert.showAndWait();

                closeWindow();
            } else {
                statusText.setText("Error: Updating Information Failed");
            }

        } else {
            statusText.setText("Error: Current Term is not Number");
        }


    }

    public void closeWindow() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }
}
