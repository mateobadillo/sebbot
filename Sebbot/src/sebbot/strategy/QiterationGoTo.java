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
    Qiteration q;
    
    
    /**
     * @param q
     */
    public QiterationGoTo()
    {
        System.out.println("q iteration starting...");
        this.q = new Qiteration(1, 1, 20, 5, 1, 5);
        q.computeQl();
        System.out.println("q iteration table computed.");
        
    }


    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        BasicStrategy.qIterationGoToBall(c, fsi, p, q);
    }
}
