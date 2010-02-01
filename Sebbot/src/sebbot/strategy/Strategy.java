package sebbot.strategy;
import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.Sebbot;


/**
 * @author Sebastien Lentz
 *
 */
public interface Strategy
{
    public void doAction(Sebbot s, FullstateInfo fsi, Player p);

}
