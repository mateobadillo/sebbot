package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;
import sebbot.ballcapture.Qiteration;

/**
 * @author Sebastien Lentz
 *
 */
public class QiterationGoTo implements Strategy
{
    Qiteration q;

    public QiterationGoTo(Qiteration q)
    {
        this.q = q;
        //new Thread(q).start();
    }
    
    public QiterationGoTo(String filename)
    {
        this.q = Qiteration.loadQl(filename);
        //new Thread(q).start();
    }

    public QiterationGoTo()
    {
        q = new Qiteration(1, 1, 200, 20, 2, 10);
        new Thread(q).start();
    }


    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        BasicStrategy.qIterationGoToBallandShootToGoal(c, fsi, p, q);
    }
}
