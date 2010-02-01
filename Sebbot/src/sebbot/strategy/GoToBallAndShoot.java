package sebbot.strategy;

import sebbot.Ball;
import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.Sebbot;

public class GoToBallAndShoot implements Strategy
{

    public void doAction(Sebbot s, FullstateInfo fsi, Player p)
    {
        Ball ball = fsi.getBall();

        if (p.distanceTo(ball) > 0.5d)
        { // The ball is not in the kickable margin.
            if (Math.abs(p.getAngleFromBody(ball)) < 30.0)
            { // The player is directed at the ball.
                s.dash(100);
            }
            else
            { // The player needs to turn in the direction of the ball.
                s.turn(p.getAngleFromBody(ball));
            }
        }
        else
        { // The ball is in the kickable margin => kick it towards the goal!
            double goalPosX = p.isLeftSide() ? 52.5d : -52.5d;
            s.kick(100, p.getAngleFromBody(goalPosX, 0));
        }
        
    }
    
    

}
