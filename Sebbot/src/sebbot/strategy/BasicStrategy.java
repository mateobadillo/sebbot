
package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.MobileObject;
import sebbot.Player;
import sebbot.RobocupClient;
import sebbot.SoccerParams;
import sebbot.Vector2D;

/**
 * @author Sebastien Lentz
 *
 */
public class BasicStrategy
{
    public static boolean goTo(Vector2D position, RobocupClient s, FullstateInfo fsi, Player p)
    {
        if (p.distanceTo(position) > SoccerParams.KICKABLE_MARGIN)
        { // We are too far away from the position.
            if (Math.abs(p.angleFromBody(position)) < 30.0d)
            { // The player is directed at the position.
//                System.out.println("-------- " + fsi.getTimeStep() + " -------");
//                System.out.println("Current position/speed: " + p.getPosition() + p.getVelocity());
//                System.out.println("Next position/speed: " + p.nextPosition(100.0d) + p.nextVelocity(100.0d));

                s.dash(100.0d);
            }
            else
            { // The player needs to turn in the direction of the position.
                s.turn(p.angleFromBody(position));
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
            
            s.kick(100.0d, p.angleFromBody(goalPosX, 0.0d));
            
            return true; // Order is accomplished.
        }
        
        else
        {
            return false; // Order is not yet accomplished.
        }
    }    
    
    /*************************************************************************/
    
    
    public static boolean goTo2(Vector2D position, RobocupClient s, FullstateInfo fsi, Player p)
    {
        if (p.distanceTo(position) > SoccerParams.KICKABLE_MARGIN)
        { // We are too far away from the position.
            if (Math.abs(p.angleFromBody(position)) < 1.0d)
            { // The player is directed at the position.                
                s.dash(100.0d);
//                System.out.println("-------- " + fsi.getTimeStep() + " -------");
//                System.out.println("Current position/speed: " + p.getPosition() + p.getVelocity());
//                System.out.println("Next position/speed: " + p.nextPosition(100.0d) + p.nextVelocity(100.0d));
            }
            else
            { // The player needs to turn in the direction of the position.
                s.turn(p.angleFromBody(position));
            }
            
            return false; // Order is not yet accomplished.
        }
        else
        {
            return true; // Order is accomplished.
        }
    }
    
    public static boolean goToBallAndShootToGoal2(RobocupClient s, FullstateInfo fsi, Player p)
    {
        Vector2D interceptionPoint = p.interceptionPoint(fsi.getBall().trajectory());
        if (goTo2(interceptionPoint, s, fsi, p))
        { // The ball is in the kickable margin => kick it towards the goal!
            double goalPosX = p.isLeftSide() ? 52.5d : -52.5d;
            
            s.kick(100.0d, p.angleFromBody(goalPosX, 0.0d));
            
            return true; // Order is accomplished.
        }
        
        else
        {
            return false; // Order is not yet accomplished.
        }
    }    


    

}
