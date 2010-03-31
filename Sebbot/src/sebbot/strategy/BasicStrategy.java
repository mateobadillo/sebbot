package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.MobileObject;
import sebbot.Player;
import sebbot.PlayerAction;
import sebbot.PlayerActionType;
import sebbot.RobocupClient;
import sebbot.SoccerParams;
import sebbot.Vector2D;
import sebbot.learning.Action;
import sebbot.learning.Qiteration;
import sebbot.learning.State;

/**
 * @author Sebastien Lentz
 *
 */
public class BasicStrategy
{
    public static boolean simpleGoTo(Vector2D position, RobocupClient c,
                                     FullstateInfo fsi, Player p)
    {
        if (p.distanceTo(position) > SoccerParams.KICKABLE_MARGIN)
        { // We are too far away from the position.           
            
            if (Math.abs(p.angleFromBody(position)) < 30.0d)
            { // The player is directed at the position.
                PlayerAction action = new PlayerAction(PlayerActionType.DASH,
                    100.0d, 0.0d, c);
                c.getBrain().getActionsQueue().addLast(action);
            }
            
            else
            { // The player needs to turn in the direction of the position.
                PlayerAction action = new PlayerAction(PlayerActionType.TURN,
                    0.0d, p.angleFromBody(position), c);
                c.getBrain().getActionsQueue().addLast(action);
            }

            return false; // Order is not yet accomplished.
        }
        else
        {
            return true; // Order is accomplished.
        }
    }

    public static boolean simpleGoTo(MobileObject o, RobocupClient s,
                                     FullstateInfo fsi, Player p)
    {
        return simpleGoTo(o.getPosition(), s, fsi, p);
    }

    public static boolean simpleGoToBallAndShootToGoal(RobocupClient c,
                                                       FullstateInfo fsi,
                                                       Player p)
    {
        if (simpleGoTo(fsi.getBall(), c, fsi, p))
        { // The ball is in the kickable margin => kick it towards the goal!
            double goalPosX = p.isLeftSide() ? 52.5d : -52.5d;

            PlayerAction action = new PlayerAction(PlayerActionType.KICK,
                100.0d, p.angleFromBody(goalPosX, 0.0d), c);
            c.getBrain().getActionsQueue().addLast(action);

            return true; // Order is accomplished.
        }
        else
        {
            return false; // Order is not yet accomplished.
        }
    }

    /*************************************************************************/

    public static boolean goTo(Vector2D position, RobocupClient c,
                               FullstateInfo fsi, Player p)
    {
        if (p.distanceTo(position) > SoccerParams.KICKABLE_MARGIN)
        { // We are too far away from the position.
            if (Math.abs(p.angleFromBody(position)) < 10.0d)
            { // The player is directed at the position.  
                PlayerAction action = new PlayerAction(PlayerActionType.DASH,
                    100.0d, 0.0d, c);
                c.getBrain().getActionsQueue().addLast(action);
            }
            else
            { // The player needs to turn in the direction of the position.
                PlayerAction action = new PlayerAction(PlayerActionType.TURN,
                    0.0d, p.angleFromBody(position), c);
                c.getBrain().getActionsQueue().addLast(action);
            }

            return false; // Order is not yet accomplished.
        }
        else
        {
            return true; // Order is accomplished.
        }
    }

    public static boolean goTo2(Vector2D position, RobocupClient c,
                                FullstateInfo fsi, Player p)
    {
        if (p.distanceTo(position) > SoccerParams.KICKABLE_MARGIN)
        { // We are too far away from the position.
            int nbOfActions = p.timeToReach(position);
            if (Math.abs(p.angleFromBody(position)) < 10.0d)
            { // The player is directed at the position.  
                PlayerAction action = new PlayerAction(PlayerActionType.DASH,
                    100.0d, 0.0d, c);
                c.getBrain().getActionsQueue().addLast(action);

                for (int i = 0; i < nbOfActions - 1; i++)
                {
                    action = new PlayerAction(PlayerActionType.DASH, 100.0d,
                        0.0d, c);
                    c.getBrain().getActionsQueue().addLast(action);
                }
            }
            else
            { // The player needs to turn in the direction of the position.
                PlayerAction action = new PlayerAction(PlayerActionType.TURN,
                    0.0d, p.angleFromBody(position), c);
                c.getBrain().getActionsQueue().addLast(action);

                for (int i = 0; i < nbOfActions - 1; i++)
                {
                    action = new PlayerAction(PlayerActionType.DASH, 100.0d,
                        0.0d, c);
                    c.getBrain().getActionsQueue().addLast(action);
                }

            }

            return false; // Order is not yet accomplished.
        }
        else
        {
            return true; // Order is accomplished.
        }
    }

    public static boolean goToBallAndShootToGoal(RobocupClient c,
                                                 FullstateInfo fsi, Player p)
    {
        Vector2D interceptionPoint = p.interceptionPoint(fsi.getBall()
            .trajectory());
        if (goTo2(interceptionPoint, c, fsi, p))
        { // The ball is in the kickable margin => kick it towards the goal!
            double goalPosX = p.isLeftSide() ? 52.5d : -52.5d;

            PlayerAction action = new PlayerAction(PlayerActionType.KICK,
                100.0d, p.angleFromBody(goalPosX, 0.0d), c);
            c.getBrain().getActionsQueue().addLast(action);

            return true; // Order is accomplished.
        }
        else
        {
            return false; // Order is not yet accomplished.
        }
    }

    /**************************************************************************/

    public static boolean qIterationGoToBall(RobocupClient c,
                                             FullstateInfo fsi, Player p,
                                             Qiteration q)
    {
        if (p.distanceTo(fsi.getBall()) < SoccerParams.KICKABLE_MARGIN)
        {
            return true;
        }

        else
        {
            State state = new State(false);

            state.setBallVelocityNorm((float) fsi.getBall().getVelocity()
                .polarRadius());
            state.setBallVelocityDirection((float) fsi.getBall().getVelocity()
                .polarAngle());
            state.setPlayerVelocityNorm((float) p.getVelocity().polarRadius());
            state.setPlayerVelocityDirection((float) p.getVelocity()
                .polarAngle());
            state.setPlayerBodyDirection((float) p.getBodyDirection());
            state.setRelativeDistance((float) p.distanceTo(fsi.getBall()));
            state.setRelativeDirection((float) p.angleFromBody(fsi.getBall()));

            float[] action = q.getAction(state);

            System.out.println("action:");
            for (int i = 0; i < action.length; i++)
            {
                System.out.println(action[i]);
            }

            //System.out.println("Current state: " + state);
            if (action[0] == 0.0f)
            {
                PlayerAction pAction = new PlayerAction(PlayerActionType.DASH,
                    action[1], 0, c);
                c.getBrain().getActionsQueue().addLast(pAction);
            //    System.out.println("Next state: " + state.nextState(new Action(action[0], false)));
            }
            else
            {
                PlayerAction pAction = new PlayerAction(PlayerActionType.TURN,
                    0, action[1], c);
                c.getBrain().getActionsQueue().addLast(pAction);
            //    System.out.println("Next state: " + state.nextState(new Action(action[1], true)));
            }

            return false;
        }
    }

    public static boolean qIterationGoToBallandShootToGoal(RobocupClient c,
                                                           FullstateInfo fsi,
                                                           Player p,
                                                           Qiteration q)
    {
        if (qIterationGoToBall(c, fsi, p, q))
        { // The ball is in the kickable margin => kick it towards the goal!
            double goalPosX = p.isLeftSide() ? 52.5d : -52.5d;

            PlayerAction action = new PlayerAction(PlayerActionType.KICK,
                100.0d, p.angleFromBody(goalPosX, 0.0d), c);
            c.getBrain().getActionsQueue().addLast(action);

            return true; // Order is accomplished.
        }
        else
        {
            return false; // Order is not yet accomplished.
        }
    }

}
