package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;

public class GoToBallAndShoot2 implements Strategy
{
    public void doAction(RobocupClient s, FullstateInfo fsi, Player p)
    {
        BasicStrategy.goToBallAndShootToGoal(s, fsi, p);
    }
}
