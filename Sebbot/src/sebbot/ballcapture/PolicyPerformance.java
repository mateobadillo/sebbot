package sebbot.ballcapture;

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

    public static void testAllDps()
    {
        DirectPolicySearch dps = null;

        for (int nbOfBFs = 12; nbOfBFs < 12+9*2; nbOfBFs = nbOfBFs + 2)
        {
            for (int nbOfIterations = 1; nbOfIterations <= 50; nbOfIterations++)
            {
                try
                {
                    dps = DirectPolicySearch.load("DPS_" + nbOfBFs + "_"
                            + (3 * nbOfBFs * (4 * 7 + 4)) + "_" + "100" + "_"
                            + nbOfIterations + ".zip");
                }
                catch (RuntimeException e)
                {
                    continue;
                }

                logPerformances(dps, false);
            }
        }
    }

    public static void logPerformances(Policy policy, boolean logBadTrajectories)
    {
        PrintWriter badTrajectories = null;
        PrintWriter performanceLog = null;
        try
        {
            performanceLog = new PrintWriter(new FileWriter("performance.log",
                true));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (logBadTrajectories)
        {
            try
            {
                badTrajectories = new PrintWriter(new FileWriter(policy
                    .getName()
                        + "_bad_trajectories.log"));
                badTrajectories.println("");
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }

        LinkedList<State> ts = new LinkedList<State>();
        LinkedList<Action> ta = new LinkedList<Action>();
        LinkedList<Float> tr = new LinkedList<Float>();
        float score;
        int iterationsDone = 0;
        int nbOfBadTrajectories = 0;
        float averageScore = 0f;
        float totalScore = 0f;
        for (State s : initialStates)
        {
            ts.clear();
            ta.clear();
            tr.clear();

            score = MarkovDecisionProcess.trajectoryReward(s, policy, 500, ts,
                ta, tr);

            if (score < 0f)
            {
                nbOfBadTrajectories++;

                if (logBadTrajectories)
                {
                    badTrajectories.println("Total score: " + score + ":");
                    for (int i = 1; !(ts.isEmpty() || ta.isEmpty()); i++)
                    {
                        badTrajectories.println(i + ": " + ts.removeFirst()
                                + " | " + ta.removeFirst() + " | "
                                + tr.removeFirst());
                    }

                    badTrajectories.println("");
                    badTrajectories
                        .println("----------------------------------------------------");
                    badTrajectories.println("");
                }
            }
            else
            {
                totalScore += score;
            }

            iterationsDone++;

            if (iterationsDone == 10 && nbOfBadTrajectories == 10)
            {
                totalScore = 0f;
                nbOfBadTrajectories = initialStates.size();
                break;
            }
        }
        if (logBadTrajectories)
        {
            badTrajectories.println("Total number of bad trajectories: "
                    + nbOfBadTrajectories);
            badTrajectories.close();
        }

        if (initialStates.size() != nbOfBadTrajectories)
        {
            averageScore = totalScore
                    / (float) (initialStates.size() - nbOfBadTrajectories);
        }
        else
        {
            averageScore = 0f;
        }

        String line;
        if (policy.getClass() == DirectPolicySearch.class)
        {
            DirectPolicySearch dps = (DirectPolicySearch) policy;
            float totalComputationTime = (float) dps.getTotalComputationTime() / 1000f / 60f;

            line = dps.getName() + ";" + dps.getTotalNbOfIterations() + ";"
                    + totalComputationTime + ";" + averageScore + ";"
                    + nbOfBadTrajectories;
        }
        else
        {
            line = policy.getName() + ";" + averageScore + ";"
                    + nbOfBadTrajectories;
        }
        performanceLog.println(line);

        performanceLog.close();
    }
}
