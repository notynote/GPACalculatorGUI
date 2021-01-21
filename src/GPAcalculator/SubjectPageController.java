package GPAcalculator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SubjectPageController implements Initializable {

    @FXML
    Pane pane;

    @FXML
    TableView<Subject> tableView;

    @FXML
    TableColumn<Subject, String> nameCol;

    @FXML
    TableColumn<Subject, Integer> creditCol;

    @FXML
    TableColumn<Subject, Double> gradeCol;

    @FXML
    TableColumn<Subject, Subject.DelButton> btnCol;

    @FXML
    TextField subjectNameField;

    @FXML
    TextField creditField;

    @FXML
    Text creditText;

    @FXML
    Text statusText;

    @FXML
    ChoiceBox<Double> subjectChoiceBox;

    Student student;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        student = MainMenuController.student;

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        btnCol.setCellValueFactory(new PropertyValueFactory<>("button"));

        try {
            tableView.setItems(getSubjectList());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        subjectChoiceBox.setItems(getGradeList());
        subjectChoiceBox.getSelectionModel().selectFirst();

    }

    private ObservableList<Double> getGradeList() {
        ObservableList<Double> grades = FXCollections.observableArrayList();
        grades.add(4.0);
        grades.add(3.5);
        grades.add(3.0);
        grades.add(2.5);
        grades.add(2.0);
        grades.add(1.5);
        grades.add(1.0);
        grades.add(0.0);

        return grades;
    }

    private ObservableList<Subject> getSubjectList() throws SQLException {
        ObservableList<Subject> subjects = FXCollections.observableArrayList();
        PreparedStatement subjectStatement = Main.connection.prepareStatement(
                "SELECT * FROM `enrolled` WHERE `SID` = ?"
        );

        subjectStatement.setString(1, student.getSid());
        ResultSet resultSet = subjectStatement.executeQuery();

        while (resultSet.next()) {
            Subject subject = new Subject(
                    resultSet.getString("COURSE_ID"), resultSet.getInt("Credit"),
                    resultSet.getDouble("GRADE"), resultSet.getInt("e_id"),
                    pane
            );
            subjects.add(subject);
        }

        subjectStatement.close();

        return subjects;

    }

    public void AddSubject() throws SQLException {
        String subjectNameText = subjectNameField.getText();
        String creditText = creditField.getText();
        String studentID = student.getSid();
        double gradeText = subjectChoiceBox.getValue();

        if (subjectNameText.isEmpty() || creditText.isEmpty()) {
            statusText.setText("Error: Fields are empty");
        } else {
            int creditInt = Integer.parseInt(creditText);
            PreparedStatement addSubjectStatement = Main.connection.prepareStatement(
                    "INSERT INTO `enrolled`(`SID`,`COURSE_ID`,`GRADE`,Credit) VALUES (?,?,?,?)"
            );

            addSubjectStatement.setString(1,studentID);
            addSubjectStatement.setString(2,subjectNameText);
            addSubjectStatement.setDouble(3,gradeText);
            addSubjectStatement.setInt(4,creditInt);

            int i = addSubjectStatement.executeUpdate();

            if (i > 0) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Add Subject Complete!");
                alert.setHeaderText(null);
                alert.setContentText("Added " + subjectNameText + " for student " + student.getsName());
                alert.showAndWait();

            }

            closeWindow();
        }

    }

    public void closeWindow() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

}
