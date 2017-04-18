import java.util.*;


public class Particle {

    private static final int MAX_VELOCITY = Main.graphSize * 5;
    private Random rand = Main.rand;
    private ArrayList<Integer[]> velocity;
    private ArrayList<Integer> position;
    private ArrayList<Integer> pBest;
    private double pBestFitness;
    private double fitness;
    private boolean maxVelocityExceeded = false;


    public Particle() {
        position = new ArrayList<>();
        pBest = new ArrayList<>(position);
        velocity = new ArrayList<>();
        generateVelocity();
    }

    public Particle(int n) {
        position = new ArrayList<>();
        boolean visited[] = new boolean[n];
        for (int i = 0; i < n; i++) {
            int next = rand.nextInt(n);
            while (visited[next]) {
                next = rand.nextInt(n);
            }
            position.add(next);
            visited[next] = true;
        }
        fitness = Main.computeTourLength(position);
        pBest = new ArrayList<>(position);
        pBestFitness = fitness;
        velocity = new ArrayList<>();
        generateVelocity();
    }

    public Particle(ArrayList<Integer> position) {
        while (position.size() < Main.graphSize) {
            int toAdd = rand.nextInt(Main.graphSize);
            if (!position.contains(toAdd))
                position.add(toAdd);
        }
        this.position = position;
        pBest = new ArrayList<>(this.position);
        velocity = new ArrayList<>();
        generateVelocity();
    }

    public Particle(Particle particle) {
        position = new ArrayList<>(particle.getPosition());
        pBest = new ArrayList<>(particle.getPBest());
        velocity = new ArrayList<Integer[]>(particle.getVelocity());
        fitness = particle.getFitness();
        pBestFitness = particle.getPBestFitness();
    }


    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public ArrayList<Integer[]> getVelocity() {
        return velocity;
    }

    /*public void setVelocity(ArrayList<Integer[]> vel) {
        this.velocity = velocity;
    }*/

    public void addVelocity(ArrayList<Integer[]> vel) {
        if (velocity.size() < MAX_VELOCITY) {
            for (int i = 0; i < vel.size(); i++) {
                velocity.add(vel.get(i));
                if (velocity.size() >= MAX_VELOCITY) break;
            }
        } else {
            maxVelocityExceeded = true;
            //int size = velocity.size();
            for (int i = 0; i <= vel.size(); i++) {
                velocity.remove(i);
            }
            for (int i = 0; i < vel.size(); i++) {
                velocity.add(vel.get(i));
            }
        }
    }

    public ArrayList<Integer> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Integer> position) {
        this.position = new ArrayList<>(position);
        fitness = Main.computeTourLength(position);
        if(fitness < pBestFitness){
            pBest = this.position;
            pBestFitness = fitness;
        }
    }

    public ArrayList<Integer> getPBest() {
        return pBest;
    }

    public void setPBest(ArrayList<Integer> pBest) {
        this.pBest = new ArrayList<>(pBest);
    }

    public ArrayList<Integer[]> subtractPosition(ArrayList<Integer> p1) {
        ArrayList<Integer[]> difference = new ArrayList<>();
        for (int i = 0; i < p1.size(); i++) {
            Integer pVal = p1.get(i);
            if (!pVal.equals(position.get(i))) {
                Integer[] value = new Integer[2];
                value[0] = i;
                value[1] = p1.get(i);
                difference.add(value);
            }
        }
        return difference;
    }

    public void generateVelocity() {
        velocity.clear();
        int n = position.size();
        boolean[] assignedIndex = new boolean[n];
        boolean[] assignedValue = new boolean[n];
        int newIndex, newValue;
        while (velocity.size() < n) {
            newIndex = rand.nextInt(n);
            newValue = rand.nextInt(n);
            if (!assignedIndex[newIndex] && !assignedValue[newValue]) {
                Integer[] toAdd = new Integer[2];
                toAdd[0] = newIndex;
                toAdd[1] = newValue;
                velocity.add(toAdd);
                assignedIndex[newIndex] = true;
                assignedValue[newValue] = true;
            }
        }
    }

    public boolean isMaxVelocityExceeded() {
        return maxVelocityExceeded;
    }


    @Override
    public String toString() {
        return position.toString() + "\nCost: " + fitness + "\n";
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

    public void updatePBest() {
        pBest = new ArrayList<>(position);
        pBestFitness = fitness;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Particle))
            return false;
        if (obj == this)
            return true;

        Particle rhs = (Particle) obj;
        return rhs.getPosition().equals(position);
    }
}
