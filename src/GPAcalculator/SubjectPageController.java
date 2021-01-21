package GPAcalculator;

import GPAcalculator.GPA.CurrentGPA;
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
    Text studentNameText;
    
    @FXML
    Text studentTotalCreditText;
    
    @FXML
    Text currentGPAText;

    @FXML
    ChoiceBox<Grade> subjectChoiceBox;

    Student student;
    CurrentGPA currentGPAObject;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        student = MainMenuController.student;

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("gradeText"));
        btnCol.setCellValueFactory(new PropertyValueFactory<>("button"));

        try {
            tableView.setItems(getSubjectList());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        subjectChoiceBox.setItems(getGradeList());
        subjectChoiceBox.getSelectionModel().selectFirst();

        studentNameText.setText("Name: " + student.getsName());
        studentTotalCreditText.setText("Total Credit: " + student.getTotalCredit());
        currentGPAObject = new CurrentGPA(student);
        double currentGPADouble = currentGPAObject.getGPA();
        currentGPAText.setText("Current GPA: " + String.format("%.2f",currentGPADouble));


    }

    private ObservableList<Grade> getGradeList() {
        ObservableList<Grade> grades = FXCollections.observableArrayList();
        grades.add(new Grade("A",4.0));
        grades.add(new Grade("B+",3.5));
        grades.add(new Grade("B",3.0));
        grades.add(new Grade("C+",2.5));
        grades.add(new Grade("C",2.0));
        grades.add(new Grade("D+",1.5));
        grades.add(new Grade("D",1.0));
        grades.add(new Grade("TR",4.0));
        grades.add(new Grade("F",0.0));

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
                    pane, resultSet.getString("GradeLabel")
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
        double gradeText = subjectChoiceBox.getValue().getValue();
        String gradeLabel = subjectChoiceBox.getValue().getLabel();
        int creditEarn = 0;
        int creditTransfer = 0;

        if (subjectNameText.isEmpty() || creditText.isEmpty()) {
            statusText.setText("Error: Fields are empty");
        } else {
            int creditInt = Integer.parseInt(creditText);
            PreparedStatement addSubjectStatement = Main.connection.prepareStatement(
                    "INSERT INTO `enrolled`(`SID`,`COURSE_ID`,`GRADE`,`GradeLabel`,Credit) VALUES (?,?,?,?,?)"
            );

            addSubjectStatement.setString(1,studentID);
            addSubjectStatement.setString(2,subjectNameText);
            addSubjectStatement.setDouble(3,gradeText);
            addSubjectStatement.setString(4,gradeLabel);
            addSubjectStatement.setInt(5,creditInt);

            int i = addSubjectStatement.executeUpdate();

            if (i > 0) {

                PreparedStatement fetchCreditStatement = Main.connection.prepareStatement(
                    "SELECT * FROM `enrolled` WHERE `GradeLabel` <> ? && `SID` = ?"
                );

                fetchCreditStatement.setString(1, "TR");
                fetchCreditStatement.setString(2, studentID);
                ResultSet resultSet = fetchCreditStatement.executeQuery();

                while (resultSet.next()) {
                    creditEarn += resultSet.getInt("Credit");
                }

                fetchCreditStatement.close();

                PreparedStatement fetchTranCreditStatement = Main.connection.prepareStatement(
                        "SELECT * FROM `enrolled` WHERE `GradeLabel` = ? && `SID` = ?"
                );

                fetchTranCreditStatement.setString(1, "TR");
                fetchTranCreditStatement.setString(2, studentID);
                ResultSet transResultSet = fetchTranCreditStatement.executeQuery();

                while (transResultSet.next()) {
                    creditTransfer += transResultSet.getInt("Credit");
                }

                fetchTranCreditStatement.close();

                PreparedStatement updateCreditStatement = Main.connection.prepareStatement(
                    "UPDATE `student` SET `CREDIT_EARNED` = ?, `CREDIT_TRANSFER` = ? WHERE `student`.`SID` = ?"
                );

                updateCreditStatement.setInt(1, creditEarn);
                updateCreditStatement.setInt(2, creditTransfer);
                updateCreditStatement.setString(3, studentID);

                int k = updateCreditStatement.executeUpdate();

                if (k > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Add Subject Complete!");
                    alert.setHeaderText(null);
                    alert.setContentText("Added " + subjectNameText + " for student " + student.getsName());
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error!");
                    alert.setHeaderText(null);
                    alert.setContentText("Something wrong in k");
                    alert.showAndWait();
                }


            }

            closeWindow();
        }

    }

    public void closeWindow() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

}
