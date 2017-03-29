import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class Main {

    public static final int N = 61;
    public static ArrayList<Journal> journals;
    public static final double REVISION_TIME = 30;
    public static final double SCOOP_RATE = 0.001;
    public static final int TOTAL_TIME = 1826;

    public static final int DP = 4;

    public static double c1 = 2.05;
    public static double c2 = 0.5;
    public static final int SEED = 12;
    public static Random rand = new Random();
    public static ArrayList<Particle> swarm;
    public static ArrayList<Particle> externalArchive;
    public static ArrayList<Particle> combinedExternalArchive;
    public static int particleSize = 10;


    public static final int NO_OF_RUNS = 3;
    public static final int NO_OF_ITERATIONS = 100;
    public static final int SWARM_SIZE = 50;
    public static final double EPSILON = 0;
    public static final double epsPlus = EPSILON + 1;
    public static double maxC;
    public static double minP;
    public static double minR;
    public static int k = 0;
    public static int i = 0;
    public static double MUTATION_RATE = 0.6;
    public static ArrayList<String> improvedMinC = new ArrayList<>();
    public static ArrayList<String> improvedMinR = new ArrayList<>();



    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        readData();
        psoSDP();
        //metropolis();

    }

    public static void metropolis(){

        PrintWriter pw = null;
        try {
            long startTime = System.currentTimeMillis();

            ArrayList<Pair<Double, Double>> results = new ArrayList<>();
            ArrayList<Journal> mutableJournalList;
            Map<String, Pair<Double, Double>> rankResults = new TreeMap<>();
            pw = new PrintWriter(new File("summary_sequences.txt"), "UTF-8");
            pw.println("Journal|C|R");


            int count = 0;
            outer:
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i == j) continue;
                    mutableJournalList = (ArrayList<Journal>) journals.clone();

                    /** Swapping the journal positions based on their original positions **/

                    /** Get the new journals to be placed at the top**/
                    Journal new1 = journals.get(i);
                    Journal new2 = journals.get(j);

                    /** Get their indices in the mutableJournalList**/
                    int newIndex1 = mutableJournalList.indexOf(new1);
                    int newIndex2 = mutableJournalList.indexOf(new2);

                    /** Get the journals to be overwritten at the head of the submission sequence **/
                    Journal old1 = mutableJournalList.get(1);
                    Journal old2 = mutableJournalList.get(2);

                    /** Place the new journals at the top, overwriting the old ones **/
                    mutableJournalList.set(1, new1);
                    mutableJournalList.set(2, new2);

                    /** Place the overwritten journals in the indices of the ones placed at the top **/
                    mutableJournalList.set(newIndex1, old1);
                    mutableJournalList.set(newIndex2, old2);

                    double p = Particle.computeP(mutableJournalList);
                    double c = Particle.computeC(mutableJournalList);
                    ArrayList<ArrayList<Journal>> allSequences = new ArrayList<>();
                    //allSequences.add(mutableJournalList);
                    //pw.println(mutableJournalList.get(1).getName() + "," + c + "," + r);
                    //results.add(new Pair<>(c, p));

                    for (int k = 0; ;) {
                       /** Swap two journals in random positions **/
                        int rIndex1 = 2 + rand.nextInt(59);
                        int rIndex2 = 2 + rand.nextInt(59);
                        Journal temp = mutableJournalList.get(rIndex1);
                        Journal j1 = mutableJournalList.get(rIndex2);
                        mutableJournalList.set(rIndex1, j1);
                        mutableJournalList.set(rIndex2, temp);

                        double newP = Particle.computeP(mutableJournalList);
                        double newC = Particle.computeC(mutableJournalList);

                        double acceptProbC = (newC / c);
                        double acceptProbP = (p / newP);
                        double uProb = rand.nextDouble();

                        //results.add(new Pair<>(newC, newR));
                        //allSequences.add(mutableJournalList);
                        //pw.println(mutableJournalList.get(1).getName() + "|" + newC + "|" + newR);
                        //System.out.println(mutableJournalList.get(1).getName() + "," + newC + "," + newR);
                        if (uProb < acceptProbC && uProb < acceptProbP) {
                            pw.println(mutableJournalList.get(1).getName() + "|" + newC + "|" + newP);
                            System.out.println(mutableJournalList.get(1).getName() + "," + newC + "," + newP);
                            p = newP;
                            c = newC;
                            k++;
                            if (k > 850)
                                break;
                        } else { // Unswap
                            temp = mutableJournalList.get(rIndex1);
                            j1 = mutableJournalList.get(rIndex2);
                            mutableJournalList.set(rIndex1, j1);
                            mutableJournalList.set(rIndex2, temp);
                        }
                    }

                    //pw.println(mutableJournalList.get(1).getName() + "\t" + c + "\t" + r);

                    //rankResults.put(mutableJournalList.get(1).getName(), new Pair<>(c, r));
                }

            }
            long endTime = System.currentTimeMillis();
            System.out.println();
            System.out.println("****************** Final rank *********************");
            //pw.println("****************** Final rank *********************");
            System.out.println(rankResults);
            //pw.println(rankResults);
            System.out.println("Total time: " + (endTime - startTime)/1000);
            //pw.println("Total time: " + (endTime - startTime)/1000);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        pw.close();

    }

    public static void psoSDP() {
        combinedExternalArchive = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        try {
            PrintWriter pw = new PrintWriter(new File("result.txt"));

            for (k = 0; k < NO_OF_RUNS; k++) {

                MUTATION_RATE = 0.5;
                particleSize = 10;
                //improvedMinC.add("New run");
                //improvedMinR.add("New run");
                System.out.println("Run " + (k + 1) + ":");
                pw.println("Run " + (k + 1) + ":");
                init();
                search();
                combinedExternalArchive.addAll(externalArchive);
                System.out.println("Result: ");
                //pw.println("Result: ");
                for (Particle p : externalArchive) {
                    System.out.println(p.getPosition() + ": C = " + p.getC() + " P = " + p.getP() + " " + p.getJournalSequence().get(0).getName());
                    /*pw.print(p.getPosition() + ":\t" + p.getC() + "\t" + p.getP() + "\t");
                    for (int l = 0; l < 5; l++) {
                        pw.print(p.getJournalSequence().get(l).getName() + ", ");
                    }
                    pw.println(p.getJournalSequence().get(5).getName());
*/
                }
                System.out.println();
//                pw.println();



                /** Start subswarm search *
                 MUTATION_RATE = 0.5;
                 int j = 0;
                 swarm.clear();
                 particleSize = N;
                 for (int i = 0; i < SWARM_SIZE; i++) {
                 Particle p = new Particle(externalArchive.get(j).getPosition());
                 swarm.add(p);
                 j++;
                 if (j >= externalArchive.size())
                 j = 0;
                 }
                 System.out.println(swarm);
                 computeNonDominated();
                 for (Particle p : swarm)
                 p.updateSigma();
                 for (Particle p : externalArchive)
                 p.updateSigma();
                 search();

                 System.out.println("Result: ");
                 pw.println("Result: ");
                 for (Particle p : externalArchive) {
                 System.out.println(p.getPosition() + ": C = " + p.getC() + " R = " + p.getR() + " " + p.getJournalSequence().get(0).getName());
                 pw.print(p.getPosition() + ":\t" + p.getC() + "\t" + p.getR() + "\t");
                 for (int l = 0; l < 5; l++) {
                 pw.print(p.getJournalSequence().get(l).getName() + ", ");
                 }
                 pw.println(p.getJournalSequence().get(5).getName());

                 }
                 System.out.println();
                 pw.println();*/
            }

            updateExternalArchive(combinedExternalArchive);
            System.out.println("Combined Result: ");
            pw.println("Sequence\tC\tP\tR\tFirst\tOthers");
            for (Particle p : combinedExternalArchive) {
                System.out.println(p.getPosition() + ": C = " + p.getC() + " P = " + p.getP() + " " + " R = " + p.getR() + " " + p.getJournalSequence().get(0).getName());
                pw.print(p.getPosition() + "\t" + p.getC() + "\t" + p.getP() + "\t" + p.getR() + "\t");
                pw.print(p.getJournalSequence().get(0).getName() + "\t");
                for (int l = 1; l < 5; l++) {
                    pw.print(p.getJournalSequence().get(l).getName() + ", ");
                }
                pw.println(p.getJournalSequence().get(5).getName());

            }

            long endTime = System.currentTimeMillis();
            double totalTime = (endTime - startTime) / 1000.0;
            double averageTime = totalTime / NO_OF_RUNS;
            System.out.println("\nTotal time: " + totalTime);
            pw.println("\nTotal time: " + totalTime);
            System.out.println("Average time: " + averageTime);
            pw.println("Average time: " + averageTime);

            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void search(){
        double r1, r2;
        for (i = 0; i < NO_OF_ITERATIONS; i++) {
            for (Particle p : swarm) {
                r1 = round(rand.nextDouble(), DP);
                r2 = round(rand.nextDouble(), DP);

                /** Selection of Leader **/
                Particle leader = selectLeader(p);

                // Get differences between X and pBest and X and gBest
                ArrayList<Integer[]> gDiff = p.subtractPosition(leader.getPosition());
                ArrayList<Integer[]> pDiff = p.subtractPosition(p.getPBest());

                // Get magnitude of each difference
                int pDiffMagnitude = (int) round(c1 * r1 * pDiff.size(), 0);
                int gDiffMagnitude = (int) round(c2 * r2 * gDiff.size(), 0);

                // Generate new velocity
                Integer[] newPosition = new Integer[particleSize];
                Arrays.fill(newPosition, -1);
                for (int j = 0; j < gDiffMagnitude; j++) {
                    int index = rand.nextInt(gDiff.size());
                    Integer[] diffVal = gDiff.get(index);
                    if(!arrayContains(newPosition, diffVal[1])) {
                        newPosition[diffVal[0]] = diffVal[1];
                    }
                    gDiff.remove(index);
                    if(gDiff.isEmpty())
                        break;
                }
                for (int j = 0; j < pDiffMagnitude; j++) {
                    int index = rand.nextInt(pDiff.size());
                    Integer[] diffVal = pDiff.get(index);
                    int pDiffIndex = diffVal[0];
                    if (newPosition[pDiffIndex] == -1.0 && (!arrayContains(newPosition, diffVal[1]))) {
                        newPosition[pDiffIndex] = diffVal[1];
                    }
                    pDiff.remove(index);
                    if(pDiff.isEmpty())
                        break;
                }
                ArrayList<Integer> emptyIndices = new ArrayList<>();
                for (int j = 0; j < newPosition.length; j++) {
                    if (newPosition[j] < 0) {
                        emptyIndices.add(j);
                    }
                }

                int prevVel = rand.nextInt(particleSize) + 1;
                int velCount = 0;

                for (Integer j : emptyIndices) {
                    int toAdd =  p.getVelocity().get(j);
                    if(!arrayContains(newPosition, toAdd)) {
                        newPosition[j] = toAdd;
                    }
                    velCount++;
                    if(emptyIndices.size() == particleSize&& velCount >= prevVel)
                        break;
                }
                for (int j = 0; j < newPosition.length; j++){
                    int toAdd =  p.getPosition().get(j);
                    if(newPosition[j] < 0 && (!arrayContains(newPosition, toAdd)) ){
                        newPosition[j] = toAdd;
                    }
                }

                emptyIndices.clear();
                for (int j = 0; j < newPosition.length; j++) {
                    if (newPosition[j] < 0) {
                        emptyIndices.add(j);
                    }
                }
                int eInd = 0;
                while (!emptyIndices.isEmpty() && eInd < emptyIndices.size()){
                    int j =  emptyIndices.get(eInd);
                    int toAdd = rand.nextInt(N);
                    if(newPosition[j] < 0 && (!arrayContains(newPosition, toAdd)) ){
                        newPosition[j] = toAdd;
                        //emptyIndices.remove(j);
                        eInd++;
                    }
                }

                ArrayList<Integer> newPositionList = new ArrayList<>(Arrays.asList(newPosition));


                /** Update of pBest **/
                double prevC = Particle.computeC(Particle.decodeSequence(p.getPBest()));
                double prevP = Particle.computeP(Particle.decodeSequence(p.getPBest()));
                //System.out.println(newPositionList);
                p.setPosition(newPositionList);
                p.generateVelocity();

                if (p.getC()/epsPlus >= prevC || p.getP()/epsPlus <= prevP) {
                    p.updatePBest();
                    if(!isInExternalArchive(p))
                        externalArchive.add(new Particle(p));
                }
                /** Mutation **/
                if (rand.nextDouble() < MUTATION_RATE) {
                    int toMutate1 = rand.nextInt(particleSize);
                    int toMutate2 = rand.nextInt(particleSize);

                    /* Randomly swap a position with the leading position*/
                    int temp = p.getPosition().get(0);
                    p.getPosition().set(0, newPositionList.get(toMutate1));
                    p.getPosition().set(toMutate1, temp);


                    /* Randomly swap any two positions */
                    toMutate1 = rand.nextInt(particleSize);
                    temp = p.getPosition().get(toMutate1);
                    p.getPosition().set(toMutate1, newPositionList.get(toMutate2));
                    p.getPosition().set(toMutate2, temp);

                }

                if(i % 10 == 0){
                    /* Randomly swap positions */
                    for (int j = 0; j < 5; j++) {
                        int toMutate1 = rand.nextInt(particleSize);
                        int temp = p.getPosition().get(j);
                        p.getPosition().set(j, newPositionList.get(toMutate1));
                        p.getPosition().set(toMutate1, temp);
                    }
                }

            }
            updateExternalArchive(externalArchive);
        }
    }

    private static void computeNonDominated() {
        externalArchive = new ArrayList<>();
        outerLoop:
        for (int i = 0; i < swarm.size(); i++) {
            Particle p1 = swarm.get(i);

            /** Update min objective values for sigma value calculation **/
            if (p1.getC() > maxC) {
                maxC = p1.getC();
                //improvedMinC.add("iExpected Cost: " + p1.getC() + " Variance: " + p1.getR());
            }
            if (p1.getP() < minP) {
                minP = p1.getP();
                //improvedMinR.add("iExpected Cost: " + p1.getC() + " Variance: " + p1.getP());
            }

            if (p1.getR() < minR) {
                minR = p1.getR();
                //improvedMinR.add("iExpected Cost: " + p1.getC() + " Variance: " + p1.getP());
            }


            for (int j = 0; j < swarm.size(); j++) {
                Particle p2 = swarm.get(j);
                if (p1.equals(p2))
                    continue;
                /*System.out.println("p1: " + p1);
                System.out.println(p1.getC() + ", " + p1.getR());
                System.out.println("p2: " + p2);
                System.out.println(p2.getC() + ", " + p2.getR());*/
                /** Checking for epsilon-non-dominance **/
                if (p2.getC() > p1.getC() && p2.getP() < p1.getP() && p2.getR() < p1.getR()) {
                    continue outerLoop;
                }
            }
            /** Add epsilon-non-dominated candidate to external archive **/
            if (!isInExternalArchive(p1) || externalArchive.isEmpty()) {
                externalArchive.add(new Particle(p1));
            }
        }
    }

    private static void updateExternalArchive(ArrayList<Particle> externalArchive){
        outerLoop: for (int i = 0; i < externalArchive.size(); i++) {
            Particle p1 = externalArchive.get(i);

            /** Update min objective values for sigma value calculation **/
            if (p1.getC() > maxC) {
                maxC = p1.getC();
                //improvedMinC.add("vExpected Cost: " + p1.getC() + " Variance: " + p1.getR());
            }
            if (p1.getP() < minP) {
                minP = p1.getP();
                //improvedMinR.add("vExpected Cost: " + p1.getC() + " Variance: " + p1.getR());
            }

            if (p1.getR() < minR) {
                minR = p1.getR();
                //improvedMinR.add("vExpected Cost: " + p1.getC() + " Variance: " + p1.getR());
            }


            for (int j = 0; j < externalArchive.size(); j++) {
                Particle p2 = externalArchive.get(j);
                if(p1.equals(p2))
                    continue;
                /** Checking for and removing epsilon-dominated candidates **/
                if(p2.getC()/epsPlus > p1.getC() && p2.getP()/epsPlus < p1.getP() && p2.getR()/epsPlus < p1.getR()){
//                    System.out.println("Removing: " + p1 + " " + p1.getC() + " " + p1.getR());
//                    System.out.println("Dominated by: " + p2 + " " + p2.getC() + " " + p2.getR());
                    externalArchive.remove(p1);
                    i = 0;
                    continue outerLoop;
                } else if(p1.getC()/epsPlus > p2.getC() && p1.getP()/epsPlus < p2.getP() && p2.getR()/epsPlus < p1.getR()){
                    externalArchive.remove(p2);
                }
            }
        }
        Main.externalArchive = new ArrayList<>(externalArchive);
    }

    private static void init() {
        maxC = Double.MIN_VALUE;
        minR = Double.MAX_VALUE;
        minP = Double.MAX_VALUE;
        swarm = new ArrayList<>();
        for (int i = 0; i < SWARM_SIZE; i++) {
            Particle p = new Particle(particleSize);
            swarm.add(p);
        }
        System.out.println(swarm);
        computeNonDominated();
        for (Particle p: swarm)
            p.updateSigmaVector();
        for (Particle p: externalArchive)
            p.updateSigmaVector();
    }


    private static boolean isInExternalArchive(Particle p) {
        for (Particle p1: externalArchive){
            if (p.getC() == p1.getC() && p.getP() == p1.getP())
                return true;
        }
        return false;
    }

    private static Particle selectLeader(Particle p) {

        double minDiff = Double.MAX_VALUE;
        int leaderIndex = 0;
        for (int i = 0; i < externalArchive.size(); i++) {
            Particle l = externalArchive.get(i);
            double lDiff = Math.abs(p.getSigmaValue() - l.getSigmaValue());
            if (lDiff < minDiff){
                minDiff = lDiff;
                leaderIndex = i;
            }
        }
        return externalArchive.get(leaderIndex);
    }


    public static void readData(){
        journals = new ArrayList<>();
        //journals.add(new Journal());
        double acceptanceRate;
        double subToPub;
        double expectedNumOfCitations;

        FileReader fr;
        try {
            File f = new File("journal_data.txt");
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String s;
            while(br.ready()){
                s = br.readLine();
                String[] sHolder = s.split("\\s+");
                acceptanceRate = Double.parseDouble(sHolder[0]);
                subToPub = Double.parseDouble(sHolder[1]);
                expectedNumOfCitations = Double.parseDouble(sHolder[2]);
                Journal j = new Journal(acceptanceRate, expectedNumOfCitations, subToPub);
                journals.add(j);
            }

            f = new File("journal_names.txt");
            br = new BufferedReader(new FileReader(f));
            int i = 0;
            while(br.ready()){
                s = br.readLine();
                journals.get(i).setName(s);
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static double coEvaluateJournals(int j, int k){
        double jRank = journals.get(j).getAcceptanceRate() * journals.get(j).getExpectedNumOfCitations() * (TOTAL_TIME - journals.get(j).getSubToPub()
                - (1 - journals.get(k).getAcceptanceRate()) * Math.pow(1 - SCOOP_RATE, REVISION_TIME + journals.get(k).getSubToPub())
                * journals.get(k).getAcceptanceRate() * journals.get(k).getExpectedNumOfCitations() * (TOTAL_TIME - journals.get(k).getSubToPub()))
                + ((1 - journals.get(k).getAcceptanceRate()) * Math.pow(1 - SCOOP_RATE, REVISION_TIME + journals.get(k).getSubToPub())
                * journals.get(j).getAcceptanceRate() * journals.get(j).getExpectedNumOfCitations() * (TOTAL_TIME - journals.get(k).getSubToPub() - REVISION_TIME - journals.get(j).getSubToPub()));
        double kRank = journals.get(k).getAcceptanceRate() * journals.get(k).getExpectedNumOfCitations() * (TOTAL_TIME - journals.get(k).getSubToPub()
                - (1 - journals.get(j).getAcceptanceRate()) * Math.pow(1 - SCOOP_RATE, REVISION_TIME + journals.get(j).getSubToPub())
                * journals.get(j).getAcceptanceRate() * journals.get(j).getExpectedNumOfCitations() * (TOTAL_TIME - journals.get(j).getSubToPub()))
                + ((1 - journals.get(j).getAcceptanceRate()) * Math.pow(1 - SCOOP_RATE, REVISION_TIME + journals.get(j).getSubToPub())
                * journals.get(j).getAcceptanceRate() * journals.get(k).getExpectedNumOfCitations() * (TOTAL_TIME - journals.get(j).getSubToPub() - REVISION_TIME - journals.get(k).getSubToPub()));

        return jRank >= kRank ? j : k;
    }

    public static boolean arrayContains(Integer[] array, int n){
        for (int j = 0; j < array.length ; j++) {
            if(array[j] == n)
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