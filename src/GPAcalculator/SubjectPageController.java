package GPAcalculator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
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

    public void closeWindow() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

}
