package GPAcalculator;

import GPAcalculator.GPA.CurrentGPA;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GradeEstimationController implements Initializable {

    @FXML
    Pane pane;

    @FXML
    Text gradutatGradeText;

    @FXML
    Text distinctionGradeText;

    @FXML
    Text highDistinctionGradeText;

    @FXML
    Text currentGradeText;

    @FXML
    Text creditLeftText;

    @FXML
    Text creditTransferText;

    @FXML
    Text termTakenText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Student student = MainMenuController.student;
        CurrentGPA currentGPA = new CurrentGPA(student);
        int creditLeft = student.getRequireCredit() - student.getTotalCredit();

        //current information
        currentGradeText.setText(String.format("%.2f",currentGPA.getGPA()));
        creditLeftText.setText(String.valueOf(creditLeft));
        creditTransferText.setText(String.valueOf(student.getCreditTransfer()));
        termTakenText.setText(String.valueOf(student.getTermTaken()));

        //graduate grade
        getGraduateGrade(student, currentGPA, gradutatGradeText);
        getDistinctionGrade(student, currentGPA, distinctionGradeText, 3.25);
        getDistinctionGrade(student, currentGPA, highDistinctionGradeText, 3.5);

    }

    private void getGraduateGrade(Student student, CurrentGPA currentGPA, Text gradutatGradeText) {

        int remainingSubject = (student.getRequireCredit() - student.getTotalCredit())/4;
        double oldGPAXOldCredit = student.getTotalCredit() * currentGPA.getGPA();

        if (qualifyForGraduate(student,currentGPA)) {
            double gradeToGraduate = currentGPA.FindPossibleGrade(student,remainingSubject,2.0,oldGPAXOldCredit);
            Grade grade = new Grade();

            gradutatGradeText.setFill(Color.DARKBLUE);
            gradutatGradeText.setText(grade.getGradeText(gradeToGraduate));

        } else {
            gradutatGradeText.setText("Not Qualify");
            gradutatGradeText.setFill(Color.RED);
        }

    }

    private void getDistinctionGrade(Student student, CurrentGPA currentGPA, Text GradeText, Double requireGrade) {

        int remainingSubject = (student.getRequireCredit() - student.getTotalCredit())/4;
        double oldGPAXOldCredit = student.getTotalCredit() * currentGPA.getGPA();

        if (possibleDistinction(student,remainingSubject,oldGPAXOldCredit)) {
            double gradeToGraduate = currentGPA.FindPossibleGrade(student,remainingSubject,requireGrade,oldGPAXOldCredit);
            Grade grade = new Grade();

            GradeText.setFill(Color.DARKBLUE);
            GradeText.setText(grade.getGradeText(gradeToGraduate));

        } else {
            GradeText.setText("Not Qualify");
            GradeText.setFill(Color.RED);
        }

    }

    public boolean qualifyForGraduate(Student student, CurrentGPA currentGPA) {
        if (student.getTermTaken() < 24) {
            return currentGPA.getGPA() > 2.0;
        }
        return false;
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

    public void closeWindow() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

}
