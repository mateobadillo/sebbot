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
    private DirectPolicySearch dps = new DirectPolicySearch();

    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        BasicStrategy.dpsGoToBallandShootToGoal(c, fsi, p, dps);
    }
}
