package GPAcalculator.GPA;

import GPAcalculator.Student;
import GPAcalculator.Subject;

public class CurrentGPA extends GPA {

    Student student;

    public CurrentGPA(Student student) {
        this.student = student;
    }

    @Override
    public Double getGPA() {
        double gradeXCredit = 0.0;

        for (Subject subject: student.getSubjects()) {
            double sGrade = subject.getGrade();
            gradeXCredit += (sGrade * subject.getCredit());
            if (subject.getGrade() == 0.0) {
                student.setHasF(true);
            }
        }

        return gradeXCredit/student.getTotalCredit();
    }

    @Override
    public boolean isDistinction() {
        if (student.getCreditTransfer() < 20 || !student.isHasF()) {
            return getGPA() >= 3.25;
        }
        return false;
    }

    @Override
    public boolean isHighDistinction() {
        if (isDistinction()) {
            return getGPA() >= 3.5;
        }
        return false;
    }

}
