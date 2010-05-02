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
        log();
    }

    private void log()
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
        
        LinkedList<State> ts;
        LinkedList<Action> ta;
        for (State s : initialStates)
        {
            ts = new LinkedList<State>();
            ta = new LinkedList<Action>();

            MarkovDecisionProcess.trajectoryReward(s, policy, 200, ts, ta);

            while (!(ts.isEmpty() || ta.isEmpty()))
            {
                log.println(ts.removeFirst() + " : " + ta.removeFirst());
            }

            log.println("");
            log.println("----------------------------------------------------");
            log.println("");
        }

        log.close();
    }
}
