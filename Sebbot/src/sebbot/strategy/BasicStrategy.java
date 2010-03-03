package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.MobileObject;
import sebbot.Player;
import sebbot.PlayerAction;
import sebbot.PlayerActionType;
import sebbot.RobocupClient;
import sebbot.SoccerParams;
import sebbot.Vector2D;
import sebbot.algorithm.Qiteration;

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
            FullstateInfo fsi, Player p)
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
            System.out.println("Begin seq: " + fsi.getTimeStep()
                    + ", action length: " + nbOfActions);
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
            System.out.println("End seq: " + fsi.getTimeStep());
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
            FullstateInfo fsi, Player p, Qiteration q)
    {
        if (p.distanceTo(fsi.getBall()) < SoccerParams.KICKABLE_MARGIN)
        {
            return true;
        }

        else
        {
            float[] state = new float[6];

            state[0] = (float) fsi.getBall().getVelocity().polarRadius();
            state[1] = (float) fsi.getBall().getVelocity().polarAngle();
            state[2] = (float) p.getVelocity().polarRadius();
            state[3] = (float) p.getVelocity().polarAngle();
            state[4] = (float) p.distanceTo(fsi.getBall());
            state[5] = (float) p.angleFromBody(fsi.getBall());

            //        System.out.println("state:");
            //        for (int i = 0; i < state.length; i++)
            //        {
            //            System.out.println(state[i]);
            //        }

            float[] action = q.getAction(state);

            System.out.println("action:");
            for (int i = 0; i < action.length; i++)
            {
                System.out.println(action[i]);
            }

            if (action[2] == 0.0f)
            {
                PlayerAction pAction = new PlayerAction(PlayerActionType.DASH,
                        action[0] + 10.0d, 0, c);
                c.getBrain().getActionsQueue().addLast(pAction);
            }
            else
            {
                PlayerAction pAction = new PlayerAction(PlayerActionType.TURN,
                        0, action[1], c);
                c.getBrain().getActionsQueue().addLast(pAction);
            }
            
            return false;
        }
    }
    
    public static boolean qIterationGoToBallandShootToGoal(RobocupClient c,
            FullstateInfo fsi, Player p, Qiteration q)
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
