package sebbot.strategy;
import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;


/**
 * @author Sebastien Lentz
 *
 */
public interface Strategy
{
    public void doAction(RobocupClient s, FullstateInfo fsi, Player p);

}
