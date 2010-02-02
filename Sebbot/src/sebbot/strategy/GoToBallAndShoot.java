package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.Sebbot;

public class GoToBallAndShoot implements Strategy
{
    public void doAction(Sebbot s, FullstateInfo fsi, Player p)
    {
        BasicStrategy.goToBallAndShootToGoal(s, fsi, p);
    }
}
