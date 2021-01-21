package GPAcalculator;

import GPAcalculator.GPA.CurrentGPA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    Pane pane;

    @FXML
    Text welcomeText;

    @FXML
    Text studentInformation;

    @FXML
    Text currentGPA;

    String sID;
    public static Student student;
    CurrentGPA currentGPAObject;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.sID = LoginController.loggedInStudentID;
        try {
            student = createLoggedInStudent(sID);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assert student != null;
        currentGPAObject = new CurrentGPA(student);

        int currentTerm = student.getTermTaken()+1;


        welcomeText.setText("WELCOME " + student.getsName());
        studentInformation.setText(
                "Current Term: " + currentTerm + " Total Credit: " + student.getTotalCredit() +
                        " Department: " +student.getDepartment().getD_NAME() + "(" +student.getDepartment().getLOCATION() + ")"
        );

        UpdateGPA();
    }

    public void subjectBtn() throws IOException {

        FXMLLoader ld = new FXMLLoader();
        Pane root = ld.load(getClass().getResource("SubjectPage.fxml").openStream());

        SubjectPageController controller = ld.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Subject List");
        stage.setScene(scene);

        pane.setDisable(true);
        stage.showAndWait();
        UpdateGPA();
        pane.setDisable(false);
    }

    private void UpdateGPA() {
        double currentGPADouble = currentGPAObject.getGPA();

        currentGPA.setText(String.format("%.2f",currentGPADouble));

        if (currentGPADouble < 2.0) {
            currentGPA.setFill(Color.RED);
        } else if (currentGPADouble > 3.25) {
            currentGPA.setFill(Color.DARKBLUE);
        } else {
            currentGPA.setFill(Color.BLACK);
        }
    }

    private Student createLoggedInStudent(String sID) throws SQLException {

        PreparedStatement createStudentStatement = Main.connection.prepareStatement(
                "SELECT * FROM `student` WHERE `SID` = ?"
        );
        createStudentStatement.setString(1, sID);
        ResultSet resultSet = createStudentStatement.executeQuery();

        while (resultSet.next()) {
            return new Student(resultSet.getString("SID"),resultSet.getString("S_NAME"),
                    resultSet.getInt("TERM_TAKEN"),resultSet.getInt("CREDIT_EARNED"),
                    resultSet.getInt("CREDIT_TRANSFER"), resultSet.getInt("D_ID"));
        }

        return null;

    }

    public void closeWindow() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }
}
