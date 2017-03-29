import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Peter on 11/18/2015.
 */
public class Test {
    public static Random rand = new Random();


    public static void main(String[] args){
        Main.readData();
        ArrayList<Journal> journals = new ArrayList<>(Main.journals);
//        randomTests(journals);
        //testGrowthTrend();
        printVRankings(journals);
    }

    public static void testGrowthTrend(){
        double sum = 0;
        ArrayList<Journal> journals = new ArrayList<>();
        for (int i = 0; i < Main.N; i++) {
            journals.add(Main.journals.get(i));
            sum = Particle.computeC(journals);
            System.out.println(sum);
        }
    }

    public static void printVRankings(ArrayList<Journal> journals){
        Collections.sort(journals, new Comparator<Journal>() {
            @Override
            public int compare(Journal o1, Journal o2) {
                if (o1.getV() > o2.getV())
                    return 1;
                else if (o1.getV() < o2.getV())
                    return -1;
                else
                    return 0;
            }
        });
        Collections.reverse(journals);
        System.out.println(journals);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("rankings.txt"));
            pw.println(journals);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pw.close();
    }

    public static void randomTests(ArrayList<Journal> journals){
        System.out.println(journals);
        System.out.println(Particle.computeR(journals));
        //Collections.sort(journals);
        Journal j1 = journals.get(0);
        Journal j2 = journals.get(1);
        Journal j3 = journals.get(2);
        Journal j4 = journals.get(3);

        journals.set(0, journals.get(59));
        journals.set(1, journals.get(46));
        journals.set(2, journals.get(48));
        journals.set(3, journals.get(49));
        journals.set(59, j1);
        journals.set(46, j2);
        journals.set(48, j3);
        journals.set(49, j4);
//        journals.set(14, j1);
//        journals.set(57, j2);
//        System.out.println(journals);
//        for (int i = 0; i < 59; i++) {
//            int toMutate2 = 3 + rand.nextInt(58);
//
//            /** Randomly swap any two positions **/
//            int toMutate1 = 3 + rand.nextInt(58);
//            Journal temp = journals.get(toMutate1);
//            journals.set(toMutate1, journals.get(toMutate2));
//            journals.set(toMutate2, temp);
//        }
//        System.out.println(journals);
//        System.out.println(Particle.computeR(journals));

        Collections.sort(journals);
        /*, new Comparator<Journal>() {
            @Override
            public int compare(Journal o1, Journal o2) {
                if(o1.getExpectedNumOfCitations() > o1.getExpectedNumOfCitations())
                    return 1;
                else if(o1.getExpectedNumOfCitations() < o2.getExpectedNumOfCitations())
                    return -1;
                else
                    return 0;
            }
        });*/
        Collections.reverse(journals);
        System.out.println(journals);
        //System.out.println(Particle.computeC(journals));
        System.out.println(Particle.computeR(journals));

       /* j1 = journals.get(0);

        //Journal j2 = journals.get(2);

        journals.set(0, journals.get(1));
        journals.set(1, j1);

        System.out.println(journals);
        System.out.println(Particle.computeR(journals));

        for (int i = 0; i < 59; i++) {
            int toMutate2 = 3 + rand.nextInt(58);

            *//** Randomly swap any two positions **//*
            int toMutate1 = 2 + rand.nextInt(58);
            Journal temp = journals.get(toMutate1);
            journals.set(toMutate1, journals.get(toMutate2));
            journals.set(toMutate2, temp);
        }
        System.out.println(journals);
        System.out.println(Particle.computeR(journals));*/

        //System.out.println(journals.get(14));
        /*for (int i = 3; i < 61; i++) {
            System.out.println(Particle.computeC(journals, i) - Particle.computeC(journals, i-1));
        }*/
        //Journal j = journals.get(3);

        /** Testing swap and unswap **/
        /*int ample[] = {12, 4, 2, 5, 1};
        System.out.println(Arrays.toString(ample));
        Random rand = new Random();
        int rIndex1 = rand.nextInt(5);
        int rIndex2 = rand.nextInt(5);
        int temp = ample[rIndex1];
        int j1 = ample[rIndex2];
        ample[rIndex1] = j1;
        ample[rIndex2] = temp;
        System.out.println(Arrays.toString(ample));
        temp = ample[rIndex1];
        j1 = ample[rIndex2];
        ample[rIndex1] = j1;
        ample[rIndex2] = temp;
        System.out.println(Arrays.toString(ample));*/
    }
}
