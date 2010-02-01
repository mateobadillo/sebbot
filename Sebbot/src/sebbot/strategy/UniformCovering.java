/**
 * 
 */
package sebbot.strategy;

import sebbot.Ball;
import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.Sebbot;
import sebbot.Vector2D;
import sebbot.SoccerParams;

/**
 * @author Sebastien Lentz
 *
 */
public class UniformCovering implements Strategy
{
    protected int numberOfPlayers;
    protected Vector2D[] optimalPositions;
    Strategy goToBallAndShoot;
    
    public UniformCovering(int numberOfplayers)
    {
        this.numberOfPlayers = numberOfplayers;
        optimalPositions = new Vector2D[numberOfplayers - 1];
    }

    public void doAction(Sebbot s, FullstateInfo fsi, Player p)
    {
        Ball ball = fsi.getBall();
        double myDistanceToBall = p.distanceTo(ball);
        Player[] team = p.isLeftSide() ? fsi.getLeftTeam() : fsi.getRightTeam();
        
        boolean isClosestToBall = true;
        for (int i = 0; i < numberOfPlayers; i++)
        {
            if ((team[i] != p) && (team[i].distanceTo(ball) < myDistanceToBall))
            {
                isClosestToBall = false;
                break;
            }
        }
        
        if (isClosestToBall)
        {
            //BasicStrategy.goToBallAndShootToGoal(s, fsi, p);
        }
        
        else
        {
            double w = SoccerParams.FIELD_WIDTH;
            double l = SoccerParams.FIELD_LENGTH / 2;
            if (p.isLeftSide())
            {
                l += ball.getPosition().getX();
            }
            else
            {
                l -= ball.getPosition().getX();
            }
            
            double rectangleFormationMaxDist = Math.sqrt(Math.pow(w/4, 2) + Math.pow(l/4, 2));
            double lineFormationMaxDist = Math.sqrt(Math.pow(w/8, 2) + Math.pow(l/2, 2));
            
            if (lineFormationMaxDist < rectangleFormationMaxDist)
            { // We choose a line formation.
                double lineAbscissa = p.isLeftSide() ? l/2 - SoccerParams.FIELD_LENGTH/2 : SoccerParams.FIELD_LENGTH/2 - l/2;
                
                System.out.println(w + " " + (-0.375d * w));
                optimalPositions[0] = new Vector2D(lineAbscissa, -0.375d * w);
                optimalPositions[1] = new Vector2D(lineAbscissa, -0.125d * w);
                optimalPositions[2] = new Vector2D(lineAbscissa,  0.125d * w);
                optimalPositions[3] = new Vector2D(lineAbscissa,  0.375d * w);
            }
            
            else
            { // We choose a rectangle formation.
                double lineAbscissa = p.isLeftSide() ? l * 0.25d - SoccerParams.FIELD_LENGTH/2: SoccerParams.FIELD_LENGTH/2 - l * 0.25d;
                
                optimalPositions[0] = new Vector2D(lineAbscissa, -0.25d * w);
                optimalPositions[1] = new Vector2D(lineAbscissa,  0.25d * w);
                
                
                lineAbscissa = p.isLeftSide() ? l* 0.75d - SoccerParams.FIELD_LENGTH/2: SoccerParams.FIELD_LENGTH/2 - l*0.75d;
                
                optimalPositions[2] = new Vector2D(lineAbscissa, -0.25d * w);
                optimalPositions[3] = new Vector2D(lineAbscissa,  0.25d * w);
            }
            
            Vector2D closestPoint = optimalPositions[0];
            for (int i = 1; i < optimalPositions.length; i++)
            {
                if (p.distanceTo(optimalPositions[i]) < p.distanceTo(closestPoint))
                {
                    closestPoint = optimalPositions[i];
                }
            }
            
            BasicStrategy.goTo(closestPoint, s, fsi, p);
            System.out.println(p);
            System.out.println("Closest point: " + closestPoint);
            for (int i = 0; i < optimalPositions.length; i++)
            {
                System.out.println(optimalPositions[i]);
            }
            
        }

    }

}
