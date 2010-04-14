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
        q = new Qiteration(1, 1, 175, 10, 2, 10);
        //q = Qiteration.loadQl("backupQl.zip");
        new Thread(q).start();
    }

    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        BasicStrategy.qIterationGoToBallandShootToGoal(c, fsi, p, q);
    }
}
