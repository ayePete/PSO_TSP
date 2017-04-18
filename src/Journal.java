/**
 * Created by Peter on 11/21/2015.
 *//*

public class Journal implements Comparable<Journal>{
    private double acceptanceRate;
    private double expectedNumOfCitations;
    private double subToPub;
    private double v;
    private String name;

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

    public double getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(double acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public double getExpectedNumOfCitations() {
        return expectedNumOfCitations;
    }

    public void setExpectedNumOfCitations(double expectedNumOfCitations) {
        this.expectedNumOfCitations = expectedNumOfCitations;
    }

    public double getSubToPub() {
        return subToPub;
    }

    public void setSubToPub(double subToPub) {
        this.subToPub = subToPub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Journal(){
        this(0, 0, 0, null);
    }

    public Journal(double acceptanceRate, double expectedNumofPublications, double subToPub){
        this(acceptanceRate, expectedNumofPublications, subToPub, "Empty");
    }

    public Journal(double acceptanceRate, double expectedNumofPublications, double subToPub, String name){
        this.acceptanceRate = acceptanceRate;
        this.expectedNumOfCitations = expectedNumofPublications;
        this.subToPub = subToPub;
        this.name = name;
        computeV();
    }

    public void computeV(){
        double num = acceptanceRate * expectedNumOfCitations*(1 - subToPub/ Main.TOTAL_TIME);
        double den = 1 - (1 - subToPub/Main.TOTAL_TIME - Main.REVISION_TIME / Main.TOTAL_TIME)
                * (1 - acceptanceRate) * Math.pow(1 - Main.SCOOP_RATE, Main.REVISION_TIME + subToPub);
        v = num/den;
    }

    @Override
    public int compareTo(Journal j) {
        if(v > j.getV())
            return 1;
        else if(v < j.getV())
            return -1;
        else
            return 0;
    }

    public String toString(){
        return name + " (" + acceptanceRate + ", " + subToPub + ", " + expectedNumOfCitations + ")";
    }

    public boolean equals(Object j){
        if(j instanceof Journal) {
            Journal journal = (Journal) j;
            return name.equals(journal.getName());
        }
        else
            return false;
    }
}
*/
