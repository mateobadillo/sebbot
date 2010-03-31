package sebbot.learning;

import java.util.LinkedList;

import sebbot.MathTools;
import sebbot.SoccerParams;
import sebbot.Vector2D;

/**
 * @author Sebastien Lentz
 *
 */
public class MarkovDecisionProcess
{    
    public static State nextState(State s, Action a)
    {
        State nextState;

        if (s.isTerminal())
        {
            nextState = s;
        }
        else
        {
            nextState = new State();

            /* ------------ First compute the useful vectors ---------------- */
            Vector2D ballVelocity = new Vector2D(s.getBallVelocityNorm(), s
                .getBallVelocityDirection(), true);

            Vector2D oldPlayerSpeed = new Vector2D(s.getPlayerVelocityNorm(), s
                .getPlayerVelocityDirection(), true);

            Vector2D newPlayerSpeed;
            if (a.isTurn())
            {
                newPlayerSpeed = oldPlayerSpeed;
            }
            else
            {
                Vector2D playerAcceleration = (new Vector2D(a.getValue()
                        * SoccerParams.DASH_POWER_RATE, s
                    .getPlayerBodyDirection(), true))
                    .normalize(SoccerParams.PLAYER_ACCEL_MAX);

                newPlayerSpeed = oldPlayerSpeed.add(playerAcceleration)
                    .normalize(SoccerParams.PLAYER_SPEED_MAX);
            }

            Vector2D oldRelPosition = new Vector2D(s.getRelativeDistance(),
                MathTools.normalizeAngle(s.getRelativeDirection()
                        + s.getPlayerBodyDirection()), true);

            Vector2D newRelPosition = oldRelPosition.add(ballVelocity)
                .subtract(newPlayerSpeed);

            /* ----- Now build the next state using the computed vectors ---- */
            nextState.setBallVelocityNorm(s.getBallVelocityNorm()
                    * SoccerParams.BALL_DECAY);

            nextState.setBallVelocityDirection(s.getBallVelocityDirection());

            nextState.setPlayerVelocityNorm((float) (newPlayerSpeed
                .polarRadius() * SoccerParams.PLAYER_DECAY));

            nextState.setPlayerVelocityDirection((float) newPlayerSpeed
                .polarAngle());

            nextState.setPlayerBodyDirection((float) MathTools.normalizeAngle(s
                .getPlayerBodyDirection()
                    + (a.isTurn() ? a.getValue() : 0.0f)));

            nextState.setRelativeDistance((float) newRelPosition.polarRadius());

            nextState.setRelativeDirection((float) MathTools
                .normalizeAngle(newRelPosition.polarAngle()
                        - nextState.getPlayerBodyDirection()));
        }

        return nextState;
    }

    public static float reward(State s, Action a)
    {
        float reward;

        if (s.isTerminal())
        {
            reward = 0.0f;
        }
        else
        {
            State nextState = nextState(s, a);
            float nextStepDistance = nextState.getRelativeDistance();

            if (nextStepDistance < SoccerParams.KICKABLE_MARGIN)
            {
                reward = 10000.0f;
            }
            else
            {
                reward = -1.0f; //0.0f - nextStepDistance;
            }
        }

        return reward;
    }

    public static float infiniteReward(State initialState, Policy p)
    {
        float reward = 0;
        int nbOfIterations = 0;
        LinkedList<State> ls = new LinkedList<State>();
        LinkedList<Action> la = new LinkedList<Action>();

        State s = initialState;
        Action a = null;
        while (!s.isTerminal() && nbOfIterations < 5000)
        {
            a = p.chooseAction(s);

            ls.addLast(s);
            la.addLast(a);

            reward += reward(s, a);
            s = nextState(s, a);
            nbOfIterations++;
        }

        if (reward < 0 && reward != -5000f)
        {
            while (!ls.isEmpty())
            {
                System.out.println(ls.removeFirst() + " : " + la.removeFirst());
            }
        }

        return reward;
    }

}
