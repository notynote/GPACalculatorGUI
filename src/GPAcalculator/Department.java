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
}
