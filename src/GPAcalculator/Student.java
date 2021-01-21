package GPAcalculator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Student {

    int sid;
    String sName;
    int termTaken;
    int creditEarned, creditTransfer, totalCredit, requireCredit;
    Department department;
    ArrayList<GPAcalculator.Subject> subjects;
    boolean hasF;

    public Student(int sid, String sName, int termTaken, int creditEarned, int creditTransfer, int did) throws SQLException {
        this.sid = sid;
        this.sName = sName;
        this.termTaken = termTaken;
        this.creditEarned = creditEarned;
        this.creditTransfer = creditTransfer;
        this.totalCredit = creditEarned + creditTransfer;
        this.requireCredit = 160;
        this.department = getDepartment(did);
        this.subjects = getSubject(sid);
        this.hasF = checkHasF(subjects);

    }

    private boolean checkHasF(ArrayList<Subject> subjects) {
        for (Subject s: subjects) {
            if (s.grade == 0.0) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<GPAcalculator.Subject> getSubject(int sid) throws SQLException {

        ArrayList<GPAcalculator.Subject> getSubjects = new ArrayList<>();

        PreparedStatement getSubjectStatement = Main.connection.prepareStatement(
                "SELECT * FROM `enrolled` WHERE `SID` = ?"
        );
        getSubjectStatement.setInt(1,sid);
        ResultSet resultSet = getSubjectStatement.executeQuery();

        while (resultSet.next()) {
            getSubjects.add(new GPAcalculator.Subject(resultSet.getString("COURSE_ID"), resultSet.getInt("Credit"), resultSet.getDouble("GRADE")));
        }

        getSubjectStatement.close();
        return getSubjects;

    }

    private Department getDepartment(int did) throws SQLException {

        PreparedStatement getDepartmentStmt = Main.connection.prepareStatement(
                "SELECT * FROM `department` WHERE `D_ID` = ?"
        );
        getDepartmentStmt.setInt(1, did);
        ResultSet resultSet = getDepartmentStmt.executeQuery();

        while (resultSet.next()) {
            Department department = new Department(resultSet.getInt("D_ID"), resultSet.getString("D_NAME"), resultSet.getString("LOCATION"));
            return department;
        }
        
        return null;

    }

}
