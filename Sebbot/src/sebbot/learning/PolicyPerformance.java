package sebbot.learning;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * @author Sebastien Lentz
 *
 */
public class PolicyPerformance
{
    private static LinkedList<State> initialStates;
    
    static
    {
        initialStates = new LinkedList<State>();
        
        State s;
        for (float i = 0; i < 3.0f; i += 2.5f)
        {
            for (float j = -180.0f; j < 180.0f; j += 54.0f)
            {
                for (float k = 0f; k < 0.4f; k += 0.39f)
                {
                    for (float l = -180.0f; l < 180.0f; l += 120.0f)
                    {
                        for (float m = -162.0f; m < 180.0f; m += 92f)
                        {
                            for (float n = 2f; n < 100.0f; n += 17.0f)
                            {
                                for (float o = -180.0f; o < 180.0f; o += 81.0f)
                                {
                                    s = new State(i, j, k, l, m, n, o);
                                    initialStates.add(s);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void logPerformances()
    {
        PrintWriter log = null;
        try
        {
            log = new PrintWriter(new FileWriter("performance.log", true));
            log.println("");
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        DirectPolicySearch dps = null;
        for (int i = 12; i < 12 + 28; i = i + 2)
        {
            for (int j = 1; j <= 50; j++)
            {
                int nbOfBFs = i;
                int nbOfSamples = 2 * nbOfBFs * (4 * 7 + 4);
                int nbOfIterations = j;

                try
                {
                    dps = DirectPolicySearch.load(nbOfBFs + "_" + nbOfSamples
                            + "_" + nbOfIterations + ".zip");

                    log.println(dps.getNbOfBasicFunctions() + ";"
                            + dps.getTotalNbOfIterations() + ";"
                            + dps.getTotalComputationTime());
                }
                catch (RuntimeException e)
                {
                    e.printStackTrace();
                }
            }
        }
        log.println("");

        log.close();
    }

    public static void logBadTrajectories(Policy policy)
    {
        System.out.println("Number of initial states: " + initialStates.size());
        PrintWriter log = null;
        try
        {
            log = new PrintWriter(new FileWriter(policy.getName()
                    + "_performance.log"));
            log.println("");
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        LinkedList<State> ts = new LinkedList<State>();
        LinkedList<Action> ta = new LinkedList<Action>();
        LinkedList<Float> tr = new LinkedList<Float>();
        float score;
        int nbOfBadTrajectories = 0;
        for (State s : initialStates)
        {
            ts.clear();
            ta.clear();
            tr.clear();
            score = 0f;

            score = MarkovDecisionProcess.trajectoryReward(s, policy, 500, ts,
                ta, tr);

            if (score < 0f)
            {
                log.println("Total score: " + score + ":");
                for (int i = 1; !(ts.isEmpty() || ta.isEmpty()); i++)
                {
                    log.println(i + ": " + ts.removeFirst() + " | "
                            + ta.removeFirst() + " | "
                            + tr.removeFirst());
                }
                
                log.println("");
                log.println("----------------------------------------------------");
                log.println("");
                
                nbOfBadTrajectories++;
            }
        }

        log.println("Total number of bad trajectories: " + nbOfBadTrajectories);
        log.close();
    }
}
