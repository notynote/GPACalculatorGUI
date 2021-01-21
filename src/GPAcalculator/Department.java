package GPAcalculator;

public class Department {

    int D_ID;
    String D_NAME;
    String LOCATION;

    public Department(int d_ID, String d_NAME, String location) {
        this.D_ID = d_ID;
        this.D_NAME = d_NAME;
        this.LOCATION = location;
    }

    @Override
    public String toString() {
        return  D_ID + " - " + D_NAME + " - " + LOCATION;
    }

    public int getD_ID() {
        return D_ID;
    }

    public void setD_ID(int d_ID) {
        D_ID = d_ID;
    }

    public String getD_NAME() {
        return D_NAME;
    }

    public void setD_NAME(String d_NAME) {
        D_NAME = d_NAME;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String LOCATION) {
        this.LOCATION = LOCATION;
    }
}
