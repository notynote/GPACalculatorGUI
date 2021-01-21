package GPAcalculator;

import GPAcalculator.GPA.CurrentGPA;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    Text welcomeText;

    @FXML
    Text studentInformation;

    @FXML
    Text currentGPA;

    String sID;
    Student student;
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
        currentGPA.setText(String.format("%.2f",currentGPAObject.getGPA()));
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
}
