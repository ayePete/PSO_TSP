import java.util.*;


public class Particle {

    private ArrayList<Integer> position;
    private ArrayList<Integer> pBest;
    Random rand = Main.rand;
    ArrayList<Integer> velocity;
    private double pBestFitness;
    private double sigmaValue;
    private double c;
    private double r;
    private double p;
    public ArrayList<Journal> journalSequence;
    public ArrayList<Double> sigmaVector = new ArrayList<>();
    public static int a;
    public static int b;

    public double getP() {
        return p;
    }

    public double getR() {
        return r;
    }

    public double getC() {
        return c;
    }

    public ArrayList<Journal> getJournalSequence() {
        return journalSequence;
    }

    public Particle (){
        position = new ArrayList<>();
        pBest = new ArrayList<>(position);
        velocity  = new ArrayList<>();
        generateVelocity();
    }

    public Particle(int n){
        position = new ArrayList<>();
                position.add(a);
        position.add(b);
        while (position.size() < n){
            int toAdd = rand.nextInt(Main.N);
            if(!position.contains(toAdd))
                position.add(toAdd);
        }
        pBest = new ArrayList<>(position);
        decodeSequence();
        velocity  = new ArrayList<>();
        generateVelocity();
        sigmaVector = new ArrayList<>();
        updateSigmaVector();

    }

    public Particle(ArrayList<Integer> position){
        while (position.size() < Main.N){
            int toAdd = rand.nextInt(Main.N);
            if(!position.contains(toAdd))
                position.add(toAdd);
        }
        this.position = position;
        pBest = new ArrayList<>(this.position);
        decodeSequence();
        c = computeC(journalSequence);
        r = computeR(journalSequence);
        p = computeP(journalSequence);
        velocity  = new ArrayList<>();
        generateVelocity();
        sigmaVector = new ArrayList<>();
        updateSigmaVector();
    }

    public static ArrayList<Journal> decodeSequence(ArrayList<Integer> position){
        ArrayList<Journal> toReturn = new ArrayList<>();
        for (int i = 0; i < position.size(); i++) {
            toReturn.add(Main.journals.get(position.get(i)));
        }
        return toReturn;
    }
    public void decodeSequence(){
        journalSequence = decodeSequence(position);
    }

    public Particle(Particle particle){
        position = new ArrayList<>(particle.getPosition());
        pBest = new ArrayList<>(particle.getPBest());
        velocity  = new ArrayList<> (particle.getVelocity());
        journalSequence = new ArrayList<> (particle.getJournalSequence());
        c = particle.getC();
        r = particle.getR();
        p = particle.getP();
        sigmaValue = particle.getSigmaValue();
    }

    public ArrayList<Integer> getVelocity() {
        return velocity;
    }

    public void setVelocity(ArrayList<Integer> velocity) {
        this.velocity = velocity;
    }

    public ArrayList<Integer> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Integer> position) {
        this.position = new ArrayList<>(position);
        decodeSequence();
        c = computeC(journalSequence);
        r = computeR(journalSequence);
        p = computeP(journalSequence);
        updateSigmaVector();
    }

    public ArrayList<Integer> getPBest() {
        return pBest;
    }

    public void setPBest(ArrayList<Integer> pBest) {
        this.pBest = new ArrayList<>(pBest);
    }

    public ArrayList<Integer[]> subtractPosition(ArrayList<Integer> p1){
        ArrayList<Integer[]> difference = new ArrayList<>();
        for(int i = 0; i < p1.size(); i++){
            Integer pVal = p1.get(i);
            if(!pVal.equals(position.get(i))){
                Integer[] value = new Integer[2];
                value[0] = i;
                value[1] = p1.get(i);
                difference.add(value);
            }
        }
        return difference;
    }

    public final void generateVelocity() {
        velocity.clear();
        int n = position.size();
        while (velocity.size() < n){
            int toAdd = rand.nextInt(Main.N);
            if(!velocity.contains(toAdd))
                velocity.add(toAdd);
        }
    }

    public static double computeC(ArrayList<Journal> journals){
        int q = 1;//000;
        double result = 1 / q;
        double sum = 0;
        for (int j = 0; j < journals.size(); j++) {
            double remainingTimeSum = 0;
            for (int k = 1; k <= j; k++) {
                remainingTimeSum += journals.get(j).getSubToPub() - (j) * Main.REVISION_TIME;
            }
            remainingTimeSum = remainingTimeSum < 0 ? 0 : 1;
            double resubmissionRiskProduct = 1;

            for (int k = 0; k < j; k++) {
                resubmissionRiskProduct *= (1 - journals.get(k).getAcceptanceRate()) * Math.pow(1 - Main.SCOOP_RATE, journals.get(k).getSubToPub()
                        + Main.REVISION_TIME);
            }
            sum += journals.get(j).getAcceptanceRate() * journals.get(j).getExpectedNumOfCitations()
                    * (Main.TOTAL_TIME - remainingTimeSum) * resubmissionRiskProduct;
        }

        result *= sum;
        return result/365.25;
    }

    public static double computeR(ArrayList<Journal> journals){
        double q = 1;
        double result = 1 / q;
        double sum = 0;

        for (int j = 0; j < journals.size(); j++) {
            double product = 1;
            double hSum = 0;
            for (int i = 0; i < j; i++) {
                hSum += journals.get(j).getSubToPub() - (j * Main.REVISION_TIME);
            }
            int h = Main.TOTAL_TIME - hSum > 0 ? 1 : 0;
            for (int i = 0; i < j; i++) {
                product *= (1 - journals.get(i).getAcceptanceRate()) * Math.pow(1 - Main.SCOOP_RATE, journals.get(i).getSubToPub()
                        + Main.REVISION_TIME)  * h;
            }
            sum +=  (j+1) * journals.get(j).getAcceptanceRate() * product;
        }
        result *= sum;
        return result;
    }

    public static double computeP(ArrayList<Journal> journals){
        int q = 1;
        double result = 1 / q;
        double sum = 0;

        for (int j = 0; j < journals.size(); j++) {
            double product = 1;
            double hSum = 0;
            for (int i = 0; i < j; i++) {
                hSum += journals.get(i).getSubToPub() - (j) * Main.REVISION_TIME;
            }
            int h = Main.TOTAL_TIME - hSum > 0 ? 1 : 0;
            for (int i = 0; i < j; i++) {
                product *= (1 - journals.get(i).getAcceptanceRate()) * Math.pow(1 - Main.SCOOP_RATE, journals.get(i).getSubToPub()
                        + Main.REVISION_TIME)  * h;
            }
            sum +=  (journals.get(j).getSubToPub() + (j) * Main.REVISION_TIME) * journals.get(j).getAcceptanceRate() * product;
        }
        result *= sum;
        return result;
    }

    @Override
    public String toString(){
        return position.toString();
    }


    /**
     * @return the pBestFitness
     */
    public double getPBestFitness() {
        return pBestFitness;
    }

    /**
     * @param pBestFitness the pBestFitness to set
     */
    public void setPBestFitness(double pBestFitness) {
        this.pBestFitness = pBestFitness;
    }

    public void updatePBest(){
        pBest = new ArrayList<> (position);
    }


    public double getSigmaValue() {
        return sigmaValue;
    }


    public void updateSigma(){
        sigmaValue = (Math.pow((Main.minR * c), 2) - Math.pow((Main.maxC * r), 2))
                / (Math.pow((Main.minR * c), 2) + Math.pow((Main.maxC * r), 2));
    }
    public void updateSigmaP(){
        sigmaValue = (Math.pow((Main.minP * c), 2) - Math.pow((Main.maxC * p), 2))
                / (Math.pow((Main.minP * c), 2) + Math.pow((Main.maxC * p), 2));
    }


    public void updateSigmaVector(){
        sigmaValue = (Math.pow(c, 2) - Math.pow(p, 2))
                / (Math.pow(c, 2) + Math.pow(p, 2) + Math.pow(r, 2));
        sigmaVector.add(sigmaValue);

        sigmaValue = (Math.pow(p, 2) - Math.pow(r, 2))
                / (Math.pow(c, 2) + Math.pow(p, 2) + Math.pow(r, 2));
        sigmaVector.add(sigmaValue);

        sigmaValue = (Math.pow(r, 2) - Math.pow(c, 2))
                / (Math.pow(c, 2) + Math.pow(p, 2) + Math.pow(r, 2));
        sigmaVector.add(sigmaValue);

        sigmaValue = 0;

        for(Double d: sigmaVector){
            sigmaValue += d;
        }
    }

    public boolean equals (Object obj){
        if (!(obj instanceof Particle))
            return false;
        if (obj == this)
            return true;

        Particle rhs = (Particle) obj;
        return rhs.getPosition().equals(position);
    }

    /*public int hashCode(){
        return Objects.hashCode(position);
    }*/

    /** @Override
    public Particle clone(){
    Particle p = new Particle(Main.GRAPH_SIZE);
    p.setFitness(this.fitness);

    }
     **/
}