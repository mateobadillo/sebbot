package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;
import sebbot.learning.DirectPolicySearch;

/**
 * @author Sebastien Lentz
 *
 */
public class DPSGoTo implements Strategy
{
    private DirectPolicySearch dps;

    public DPSGoTo(DirectPolicySearch dps)
    {
        this.dps = dps;
        //new Thread(dps).start();
    }
    
    public DPSGoTo(String filename)
    {
        this.dps = DirectPolicySearch.load(filename);
        //new Thread(dps).start();
    }
    
    public DPSGoTo()
    {
        this.dps = new DirectPolicySearch(12,2,20);
        new Thread(dps).start();
    }



    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        BasicStrategy.dpsGoToBallandShootToGoal(c, fsi, p, dps);
    }
}
