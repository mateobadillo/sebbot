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
    Qiteration q;

    public QiterationGoTo()
    {
        //q = Qiteration.instance(1, 1, 200, 20, 2, 10);
        q = Qiteration.loadQl("backupQl.zip");
        //new Thread(q).start();
    }

    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        BasicStrategy.qIterationGoToBallandShootToGoal(c, fsi, p, q);
    }
}
