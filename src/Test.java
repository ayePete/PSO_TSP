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
       /* Main.costs = Main.readFile("resources\\bays29.xml", 29, "xml");
        Main.init();
        System.out.println(Main.swarm);
        System.out.println("\nBest:");
        System.out.println(Main.globalBest);*/
       Main.printVelocity(generateVelocity());
    }


    public static ArrayList<Integer[]> generateVelocity() {
        ArrayList<Integer[]> velocity = new ArrayList<>();
        velocity.clear();
        int n = 29;
        boolean[] assignedIndex = new boolean[n];
        boolean[] assignedValue = new boolean[n];

        int newIndex;
        int newValue;
        while (velocity.size() < n) {
            newIndex = rand.nextInt(n);
            newValue = rand.nextInt(n);

            if (!assignedIndex[newIndex] && !assignedValue[newValue]) {
                System.out.println(newIndex + " " + newValue);
                Integer[] toAdd = new Integer[2];
                toAdd[0] = newIndex;
                toAdd[1] = newValue;
                velocity.add(toAdd);
                assignedIndex[newIndex] = true;
                assignedValue[newValue] = true;
            }
        }
        return velocity;
    }

}
