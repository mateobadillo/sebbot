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

    public DPSGoTo()
    {
        //this.dps = new DirectPolicySearch();
        this.dps = DirectPolicySearch.load("savedBFs.zip");
        new Thread(dps).start();
    }


    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        BasicStrategy.dpsGoToBallandShootToGoal(c, fsi, p, dps);
    }
}
