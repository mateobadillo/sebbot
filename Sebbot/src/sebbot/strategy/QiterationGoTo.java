package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;
import sebbot.learning.Qiteration;

/**
 * @author Sebastien Lentz
 *
 */
public class QiterationGoTo implements Strategy
{
    private static Qiteration q = Qiteration.instance(1, 1, 175, 9, 1, 9);

    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        BasicStrategy.qIterationGoToBallandShootToGoal(c, fsi, p, q);
    }
}
