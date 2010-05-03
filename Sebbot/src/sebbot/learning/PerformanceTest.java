package sebbot.learning;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * @author Sebastien Lentz
 *
 */
public class PerformanceTest implements Runnable
{
    private Policy                 policy;
    private LinkedList<State>      initialStates;

    LinkedList<LinkedList<State>>  statesTrajectories;
    LinkedList<LinkedList<Action>> actionsTrajectories;

    /**
     * @param policy
     * @param initialStates
     */
    public PerformanceTest(Policy policy, LinkedList<State> initialStates)
    {
        this.policy = policy;
        this.initialStates = initialStates;

        statesTrajectories = new LinkedList<LinkedList<State>>();
        actionsTrajectories = new LinkedList<LinkedList<Action>>();
    }

    /**
     * @return the policy
     */
    public Policy getPolicy()
    {
        return policy;
    }

    /**
     * @param policy the policy to set
     */
    public void setPolicy(Policy policy)
    {
        this.policy = policy;
    }

    /**
     * @return the initialStates
     */
    public LinkedList<State> getInitialStates()
    {
        return initialStates;
    }

    /**
     * @param initialStates the initialStates to set
     */
    public void setInitialStates(LinkedList<State> initialStates)
    {
        this.initialStates = initialStates;
    }

    @Override
    public void run()
    {
        logTrajectories();
    }

    private void logPerformances()
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

    private void logTrajectories()
    {
        PrintWriter log = null;
        try
        {
            log = new PrintWriter(new FileWriter(policy.getName()
                    + "_performance.log", true));
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
        for (State s : initialStates)
        {
            ts.clear();
            ta.clear();
            tr.clear();
            score = 0f;

            score = MarkovDecisionProcess.trajectoryReward(s, policy, 200, ts,
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
            }
        }

        log.close();
    }
}
