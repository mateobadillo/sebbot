
package sebbot.strategy;

import sebbot.Ball;
import sebbot.FullstateInfo;
import sebbot.MobileObject;
import sebbot.Player;
import sebbot.RobocupClient;
import sebbot.Vector2D;

/**
 * @author Sebastien Lentz
 *
 */
public class BasicStrategy
{
    public static boolean goTo(Vector2D position, RobocupClient s, FullstateInfo fsi, Player p)
    {
        if (p.distanceTo(position) > 0.5d)
        { // We are too far away from the position.
            if (Math.abs(p.getAngleFromBody(position)) < 30.0)
            { // The player is directed at the position.
                s.dash(100);
            }
            else
            { // The player needs to turn in the direction of the position.
                s.turn(p.getAngleFromBody(position));
            }
            
            return false; // Order is not yet accomplished.
        }
        else
        {
            return true; // Order is accomplished.
        }
    }
    
    public static boolean goTo (MobileObject o, RobocupClient s, FullstateInfo fsi, Player p)
    {
        return goTo(o.getPosition(), s, fsi, p);
    }
    
    public static boolean goToBallAndShootToGoal(RobocupClient s, FullstateInfo fsi, Player p)
    {
        if (goTo(fsi.getBall(), s, fsi, p))
        { // The ball is in the kickable margin => kick it towards the goal!
            double goalPosX = p.isLeftSide() ? 52.5d : -52.5d;
            s.kick(100, p.getAngleFromBody(goalPosX, 0));
            
            return true; // Order is accomplished.
        }
        
        else
        {
            return false; // Order is not yet accomplished.
        }
    }
}
