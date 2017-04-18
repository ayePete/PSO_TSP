import java.io.*;
import java.util.*;

public class Main {

    public static final int DP = 4;

    public static double c1 = 1.05;
    public static double c2 = 1.05;
    public static final int SEED = 12;
    public static Random rand = new Random(System.currentTimeMillis());
    public static ArrayList<Particle> swarm;


    public static final int NO_OF_RUNS = 1;
    public static int NO_OF_ITERATIONS = 20;
    public static final int SWARM_SIZE = 30;
    public static Particle globalBest;
    public static double omega = 0.15; // Derivation on Evernote
    public static double decreaseOmega  = omega / NO_OF_ITERATIONS;
    public static int k = 0;
    public static int[][] costs = {    //Cities' distance settings
            {0, 13, 4, 6, 27, 1, 11, 3, 5, 6},
            {13, 0, 2, 2, 5, 6, 3, 4, 1, 1},
            {4, 2, 0, 4, 4, 2, 2, 9, 7, 9},
            {6, 2, 4, 0, 10, 11, 11, 14, 19, 17},
            {27, 5, 4, 10, 0, 22, 28, 18, 19, 33},
            {1, 6, 2, 11, 22, 0, 63, 15, 18, 10},
            {11, 3, 2, 11, 28, 63, 0, 17, 20, 20},
            {3, 4, 9, 14, 18, 15, 17, 0, 11, 10},
            {5, 1, 7, 19, 19, 18, 20, 11, 0, 10},
            {6, 1, 9, 17, 33, 10, 20, 10, 10, 0}
    };
    public static int graphSize;


    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        costs = readFile("resources\\bays29.xml", 29, "xml");
        /*init();
        NO_OF_ITERATIONS = 3;
        search();*/
        psoTSP();
    }

    public static void psoTSP() {
        long startTime = System.currentTimeMillis();
        for (k = 0; k < NO_OF_RUNS; k++) {
            System.out.println("************************Run " + (k + 1) + " ************************");
            init();
            omega = 0.15;
            omega += decreaseOmega;
            search();
            System.out.println("************************ Result ************************");
            for (Particle p : swarm) {
                System.out.println(p.getPBest() + "\nCost: " + p.getPBestFitness() + "\n");
            }

            System.out.println("Global Best: ");
            System.out.println(globalBest);
            //System.out.println();
        }
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        double averageTime = totalTime / NO_OF_RUNS;
        System.out.println("\nTotal time: " + totalTime);
        System.out.println("Average time: " + averageTime);
    }

    /**
     * Perform discretized PSO search, based on personally proposed discretization scheme, which is somewhat summarized in
     * <a href = "https://link.springer.com/article/10.1007/s13198-016-0487-2">this paper</a> .
     */

    public static void search() {
        double r1, r2;
        for (int i = 0; i < NO_OF_ITERATIONS; i++) {
            //System.out.println("******************************* Iteration " + i + " ******************************* ");
            //System.out.println("Current Global Best: ");
            //System.out.println(globalBest);
            int k = -1;
            if (i > (NO_OF_ITERATIONS/5))
                omega -= decreaseOmega;
            for (Particle p : swarm) {
                ArrayList<Integer[]> newVelocity = new ArrayList<>();
                ++k;
                /*if (k == 2) {
                    System.out.println("************************** Particle " + k + " **************************");
                    System.out.println(p);
                    System.out.println("Personal best: ");
                    System.out.println(p.getPBest() + "\nCost: " + p.getPBestFitness());
                }*/


                r1 = round(rand.nextDouble(), DP);
                r2 = round(rand.nextDouble(), DP);

                // Get differences between X and pBest and X and gBest
                ArrayList<Integer[]> gDiff = p.subtractPosition(globalBest.getPosition());
                ArrayList<Integer[]> pDiff = p.subtractPosition(p.getPBest());

               /* if (k == 2) {
                    System.out.print("\ngDiff: ");
                    printVelocity(gDiff);
                    System.out.print("\npDiff: ");
                    printVelocity(pDiff);
                }*/

                // Get magnitude of each difference
                int pDiffMagnitude = (int) round(0.726 * c1 * r1 * pDiff.size(), 0);
                int gDiffMagnitude = (int) round(0.729 * c2 * r2 * gDiff.size(), 0);

                /* gBest */
                boolean[] assigned = new boolean[graphSize];
                for (int j = 0; j < gDiffMagnitude; j++) {
                    int index = rand.nextInt(gDiff.size());
                    Integer[] diffVal = gDiff.get(index);
                    if (!assigned[diffVal[1]]) {
                        newVelocity.add(diffVal);
                        assigned[diffVal[1]] = true;
                    }
                    gDiff.remove(index);
                    if (gDiff.isEmpty())
                        break;
                }

                /* gBest + pBest */
                for (int j = 0; j < pDiffMagnitude; j++) {
                    int index = rand.nextInt(pDiff.size());
                    Integer[] diffVal = pDiff.get(index);
                    if (!assigned[diffVal[1]]) {
                        newVelocity.add(diffVal);
                        assigned[diffVal[1]] = true;
                    }
                    pDiff.remove(index);
                    if (pDiff.isEmpty())
                        break;
                }

                int prevVelocityMagnitude;
                if (i > (NO_OF_ITERATIONS / 5))
                    prevVelocityMagnitude = (int) (omega * p.getVelocity().size());
                else
                    prevVelocityMagnitude = (int) (0.4 * p.getVelocity().size());
                ArrayList<Integer[]> prevVelocity = new ArrayList<>(p.getVelocity());

               /* if (k == 2) {
                    System.out.print("prevVel:");
                    printVelocity(prevVelocity);
                }*/

                 /* gBest + pBest + prevVel */
                for (int j = 0; j < prevVelocityMagnitude; j++) {
                    int index = rand.nextInt(prevVelocity.size());
                    Integer[] diffVal = prevVelocity.get(index);
                    if (!assigned[diffVal[1]]) {
                        newVelocity.add(diffVal);
                        assigned[diffVal[1]] = true;
                    }
                    //prevVelocity.remove(index);
                    if (prevVelocity.isEmpty())
                        break;
                }

                /*if (k == 2) {
                    System.out.println("gDiffMagnitude: " + gDiffMagnitude);
                    System.out.println("pDiffMagnitude: " + pDiffMagnitude);
                    System.out.println("prevVelMagnitude: " + prevVelocityMagnitude);
                }*/

                int newVelocityMagnitude = gDiffMagnitude + pDiffMagnitude + prevVelocityMagnitude;

                if (newVelocityMagnitude > (newVelocity.size() - 1))  // Velocity clamping
                    newVelocityMagnitude = newVelocity.size() - 1;

                if (newVelocityMagnitude > (graphSize - 1))  // Velocity clamping
                    newVelocityMagnitude = graphSize - 1;

                /* Apply new velocity to generate new position */

                // Generate empty new position
                Integer[] newPosition = new Integer[graphSize];
                Arrays.fill(newPosition, -1);
                Arrays.fill(assigned, false);

                for (int j = 0; j < newVelocityMagnitude; j++) {
                    Integer[] diffVal = newVelocity.get(j);
                    int newIndex = diffVal[0];
                    if (newPosition[newIndex] == -1.0 && !assigned[diffVal[1]]) {
                        newPosition[newIndex] = diffVal[1];
                        assigned[diffVal[1]] = true;
                    }
                }

               /* if (k == 2) {
                    System.out.println("gbest +pBest + prevVel");
                    System.out.println("New position");
                    System.out.println(Arrays.toString(newPosition));
                }*/

                //pBest + gBest + previous Velocity with random magnitude, and direction specified by sequentially
                //attempting to fill empty spaces.

                /*int prevVel = rand.nextInt(graphSize) + 1;
                int velCount = 0;

                for (Integer j : emptyIndices) {
                    Integer[] toAdd = p.getVelocity().get(j);
                    if (!arrayContains(newPosition, toAdd[1])) {
                        newPosition[toAdd[0]] = toAdd[1];
                    }
                    velCount++;
                    if (emptyIndices.size() == graphSize && velCount >= prevVel)
                        break;
                }
                */

                /* Find unfilled indices */
                ArrayList<Integer> emptyIndices = new ArrayList<>();
                for (int j = 0; j < newPosition.length; j++) {
                    if (newPosition[j] < 0) {
                        emptyIndices.add(j);
                    }
                }

                /* Fill remaining empty spaces from previous position */
                for (int j = 0; j < newPosition.length; j++) {
                    int toAdd = p.getPosition().get(j);
                    if (newPosition[j] < 0 && (!arrayContains(newPosition, toAdd))) {
                        newPosition[j] = toAdd;
                    }
                }

                /* Randomly fill any empty spaces left */
                emptyIndices.clear();
                for (int j = 0; j < newPosition.length; j++) {
                    if (newPosition[j] < 0) {
                        emptyIndices.add(j);
                    }
                }
                int eInd = 0;
                while (!emptyIndices.isEmpty() && eInd < emptyIndices.size()) {
                    int j = emptyIndices.get(eInd);
                    int toAdd = rand.nextInt(graphSize);
                    if (newPosition[j] < 0 && (!arrayContains(newPosition, toAdd))) {
                        newPosition[j] = toAdd;
                        //emptyIndices.remove(j);
                        eInd++;
                    }
                }

                ArrayList<Integer> newPositionList = new ArrayList<>(Arrays.asList(newPosition));

                /*if (k == 2) {
                    System.out.println("Final new position");
                    System.out.println(newPositionList);
                    System.out.println("Velocity size: " + p.getVelocity().size());
                }*/
                /* Update previous velocity */
                //newVelocity.addAll(prevVelocity);
                p.addVelocity(newVelocity);

                /* Update position */
                p.setPosition(newPositionList);

                /* Update pBest */
                if (p.getPBestFitness() > computeTourLength(newPositionList)) {
                    if (k == 2) {
                        System.out.println("New pbest");
                        System.out.println(p);
                    }
                    p.updatePBest();
                }
            }
            updateGlobalBest();
        }
    }

    public static void printVelocity(ArrayList<Integer[]> velocity) {
        if (velocity.size() == 0) {
            System.out.println("[]");
            return;
        }
        System.out.print("[");
        for (int j = 0; j < velocity.size() - 1; j++) {
            System.out.print(Arrays.toString(velocity.get(j)) + ", ");
        }
        System.out.print(Arrays.toString(velocity.get(velocity.size() - 1)));
        System.out.println("]");
    }

    public static void init() {
        graphSize = costs.length;
        NO_OF_ITERATIONS = graphSize * 4;
        swarm = new ArrayList<>();
        for (int i = 0; i < SWARM_SIZE; i++) {
            Particle p = new Particle(graphSize);
            swarm.add(p);
        }
        System.out.println(swarm);
        double minCost = Double.MAX_VALUE;
        int i = 0;
        int bestIndex = 0;
        for (Particle p : swarm) {
            if (p.getFitness() < minCost) {
                minCost = p.getFitness();
                bestIndex = i;
            }
            ++i;
        }
        globalBest = new Particle(swarm.get(bestIndex));
    }

    public static void updateGlobalBest() {
        int bestIndex = -1;
        double bestCost = globalBest.getFitness();
        for (int i = 0; i < swarm.size(); i++) {
            Particle p = swarm.get(i);
            if (p.getPBestFitness() < bestCost) {
                bestCost = p.getPBestFitness();
                bestIndex = i;
            }
        }
        if (bestIndex != -1) {
            //System.out.println("New Gbest found!");
            //System.out.println("************** Updating gbest ****************");
            //System.out.println("Old gbest: \n" + globalBest);
            globalBest = new Particle(swarm.get(bestIndex));
            //System.out.println("New gbest: \n" + globalBest);
        }

        globalBest.setPosition(twoOptLocalSearch(globalBest.getPosition()));
    }

    public static ArrayList<Integer> twoOptMove(ArrayList<Integer> tour, int j, int k){
        ArrayList<Integer> newTour = new ArrayList<>(tour.subList(0, j));
        ArrayList<Integer> reverseTour = new ArrayList<>(tour.subList(j, k));
        Collections.reverse(reverseTour);
        newTour.addAll(reverseTour);
        ArrayList<Integer> tourEnd = new ArrayList<>(tour.subList(k, tour.size()));
        newTour.addAll(tourEnd);

        return newTour;
    }


    public static ArrayList<Integer> twoOptRandom(ArrayList<Integer> tour){
        int nCities = tour.size();
        int j = rand.nextInt(nCities);
        int k = rand.nextInt(nCities);
        while (j == k) k = rand.nextInt(nCities);

        // Ensure that j is less than k. That is, if j > k, swap j and k
        int tempCut = j > k ? j : k;
        if (tempCut == j) {
            j = k;
            k = tempCut;
        }
        return twoOptMove(tour, j, k);
    }

    public static ArrayList<Integer> twoOptLocalSearch(ArrayList<Integer> tour){
        for (int i = 0; i < tour.size(); i++) {
            for (int j = 0; j < tour.size(); j++) {
                if(i < j){
                    ArrayList<Integer> newTour = twoOptMove(tour, i, j);
                    if (computeTourLength(newTour) < computeTourLength(tour)){
                        return newTour;
                    }
                }
            }
        }
        return tour;
    }


    /**
     * Reads TSP instances from TSPLIB in XML file format
     *
     * @param filename name of XML file to be read
     * @return 2-dimensional symmetric cost array
     */
    public static int[][] readFile(String filename, int n, String type) {
        int graph[][] = new int[n][n];
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));

            // Skip first 14 irrelevant lines
            for (int i = 0; i < 14; i++) {
                br.readLine();
            }

            String s;
            int i = -1;
            while (br.ready()) {
                s = br.readLine();
                if (type.equals("xml")) {
                    int j = -1;
                    if (s.contains("<vertex>")) {
                        ++i;
                        do {
                            ++j;
                            if (i == j) {
                                graph[i][j] = -1;
                                continue;
                            }
                            s = br.readLine();
                            // No time to come up with a single check
                            if (s.contains("</vertex>"))
                                break;

                            String[] stringHolder = s.split("\"");
                            int cost = (int) (Double.valueOf(stringHolder[1]) + 0.5);
                            graph[i][j] = cost;
                        } while (!s.contains("</vertex>"));
                    }
                } else if (type.equals("tsp")) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return graph;
    }

    public static double computeTourLength(ArrayList<Integer> tour) {
        double tourLength = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            tourLength += costs[tour.get(i)][tour.get(i + 1)];
        }
        tourLength += costs[tour.get(tour.size() - 1)][tour.get(0)]; // Close tour;
        return tourLength;
    }


    public static boolean arrayContains(Integer[] array, int n) {
        for (int j = 0; j < array.length; j++) {
            if (array[j] == n)
                return true;
        }
        return false;
    }

    public static double round(double d, int numbersAfterDecimalPoint) {
        double n = Math.pow(10, numbersAfterDecimalPoint);
        double d2 = d * n;
        long lon = (long) d2;
        lon = ((long) (d2 + 0.5) > lon) ? lon + 1 : lon;
        return (lon) / n;
    }
}