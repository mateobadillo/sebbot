package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;
import sebbot.algorithm.Qiteration;

/**
 * @author Sebastien Lentz
 *
 */
public class QiterationGoTo implements Strategy
{
    private static Qiteration q = Qiteration.instance(1, 1, 40, 20, 5, 20);

    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        BasicStrategy.qIterationGoToBallandShootToGoal(c, fsi, p, q);
    }
}
