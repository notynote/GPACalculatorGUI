package GPAcalculator;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Subject {

    String name;
    int credit;
    double grade;
    DelButton button;
    Pane pane;
    String gradeText;

    public Subject(String name, int credit, double grade) {
        this.name = name;
        this.credit = credit;
        this.grade = grade;
    }

    public Subject(String name, int credit, double grade, int id, Pane pane, String gradeText) {
        this.name = name;
        this.credit = credit;
        this.grade = grade;
        this.button = new DelButton(id,pane);
        this.pane = pane;
        this.gradeText = gradeText + " - " + grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public DelButton getButton() {
        return button;
    }

    public void setButton(DelButton button) {
        this.button = button;
    }

    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public String getGradeText() {
        return gradeText;
    }

    public void setGradeText(String gradeText) {
        this.gradeText = gradeText;
    }

    public static class DelButton extends Button {

        public DelButton(int id, Pane pane) {
            super("Delete");
            setOnAction(event -> {

                try {
                    PreparedStatement subjectsStatement = Main.connection.prepareStatement(
                      "SELECT * FROM `enrolled` WHERE `e_id` = ?"
                    );
                    subjectsStatement.setInt(1, id);
                    ResultSet resultSet = subjectsStatement.executeQuery();

                    while (resultSet.next()) {
                        DeleteSubject(resultSet);
                    }

                    subjectsStatement.close();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Hey!");
                    alert.setHeaderText(null);
                    alert.setContentText("Cannot find enrolled id " + id);
                    alert.showAndWait();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success!");
                alert.setHeaderText(null);
                alert.setContentText("Delete subject id \"" + id + "\"");
                alert.showAndWait();

                Stage stage = (Stage) pane.getScene().getWindow();
                stage.close();

            });
        }

        private void DeleteSubject(ResultSet resultSet) throws SQLException {

            int id = resultSet.getInt("e_id");

            PreparedStatement delSubjectStatement = Main.connection.prepareStatement(
                    "DELETE FROM `enrolled` WHERE `e_id` = ?"
            );
            delSubjectStatement.setInt(1, id);
            delSubjectStatement.executeUpdate();
            delSubjectStatement.close();

        }

    }
}
