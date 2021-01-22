package GPAcalculator;

import GPAcalculator.GPA.CurrentGPA;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DetailedGPAController implements Initializable {

    @FXML
    Pane pane;

    @FXML
    Text subjectDoneText;

    @FXML
    Text creditEarnedText;

    @FXML
    Text creditTransferText;

    @FXML
    Text termTakenText;

    @FXML
    Text currentGPAText;

    @FXML
    Text totalCreditText;

    @FXML
    Text possibleDistinctionText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Student student = MainMenuController.student;
        CurrentGPA currentGPA = new CurrentGPA(student);
        double oldGPAXOldCredit = student.getTotalCredit() * currentGPA.getGPA();
        int remainingSubject = (student.getRequireCredit() - student.getTotalCredit())/4;

        creditEarnedText.setText(String.valueOf(student.getCreditEarned()));
        creditTransferText.setText(String.valueOf(student.getCreditTransfer()));
        termTakenText.setText(String.valueOf(student.getTermTaken()));
        totalCreditText.setText(String.valueOf(student.getTotalCredit()));

        try {
            subjectDoneText.setText(String.valueOf(getSubjectDone(student)));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (possibleDistinction(student,remainingSubject,oldGPAXOldCredit)) {
            possibleDistinctionText.setText("QUALIFY FOR DISTINCTION");
        } else {
            possibleDistinctionText.setText("");
        }

        getGPAText(currentGPA, currentGPAText);

    }

    private void getGPAText(CurrentGPA currentGPA, Text currentGPAText) {
        double gpa = currentGPA.getGPA();

        if (gpa < 2.0) {
            currentGPAText.setFill(Color.RED);
        } else if (gpa > 3.25) {
            currentGPAText.setFill(Color.DARKBLUE);
        } else {
            currentGPAText.setFill(Color.BLACK);
        }

        currentGPAText.setText(String.format("%.2f",gpa));

    }

    public boolean possibleDistinction(Student student, int remainingSubject, double oldGPAXOldCredit) {
        for (Subject subject: student.getSubjects()) {
            if (subject.getGrade() == 0.0) {
                return false;
            }
        }
        double remainCreditXGrade = 0.0;

        for (int i = 0; i < remainingSubject; i++) {
            remainCreditXGrade += 4*4;
        }

        double maximumGPA = (remainCreditXGrade + oldGPAXOldCredit) / student.getRequireCredit();

        return maximumGPA > 3.25;
    }

    private int getSubjectDone(Student student) throws SQLException {
        int count = 0;
        PreparedStatement statement = Main.connection.prepareStatement(
                "SELECT * FROM `enrolled` WHERE `SID` = ?"
        );
        statement.setString(1,student.getSid());
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            count++;
        }

        statement.close();
        return count;
    }

    public void closeWindow() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }
}
