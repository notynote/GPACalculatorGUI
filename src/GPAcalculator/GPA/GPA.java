package GPAcalculator.GPA;

import GPAcalculator.Student;
import GPAcalculator.Subject;

abstract class GPA {

    public abstract Double getGPA();
    public abstract boolean isDistinction();
    public abstract boolean isHighDistinction();

    public double FindPossibleGrade(Student student,int remainingSubject,double requireGrade,double oldGPAXOldCredit) {

        double remainCreditXGrade = 0.0;

        for (double i = 1.0; i <= 4.0; i+=0.5) {
            for (int j = 0; j < remainingSubject; j++) {
                remainCreditXGrade += i*4;
            }
            if ((remainCreditXGrade+oldGPAXOldCredit)/student.getRequireCredit() > requireGrade) {
                return i;
            }

        }

        return 0.0;
    }
}
