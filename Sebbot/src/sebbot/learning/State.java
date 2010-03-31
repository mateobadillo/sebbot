package sebbot.learning;

import sebbot.MathTools;
import sebbot.SoccerParams;
import sebbot.Vector2D;

public class State
{
    private static int ballVelocityNormSteps;
    private static int ballVelocityDirectionSteps;
    private static int playerVelocityNormSteps;
    private static int playerVelocityDirectionSteps;
    private static int playerBodyDirectionSteps;
    private static int relativeDistanceSteps;
    private static int relativeDirectionSteps;

    private float      ballVelocityNorm;
    private float      ballVelocityDirection;
    private float      playerVelocityNorm;
    private float      playerVelocityDirection;
    private float      playerBodyDirection;
    private float      relativeDistance;
    private float      relativeDirection;
    private boolean    isTerminal;

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * @param ballVelocityNorm
     * @param ballVelocityDirection
     * @param playerVelocityNorm
     * @param playerVelocityDirection
     * @param playerBodyDirection
     * @param relativeDistance
     * @param relativeDirection
     * @param isTerminal
     */
    public State(float ballVelocityNorm, float ballVelocityDirection,
                 float playerVelocityNorm, float playerVelocityDirection,
                 float playerBodyDirection, float relativeDistance,
                 float relativeDirection, boolean isTerminal)
    {
        this.ballVelocityNorm = ballVelocityNorm;
        this.ballVelocityDirection = ballVelocityDirection;
        this.playerVelocityNorm = playerVelocityNorm;
        this.playerVelocityDirection = playerVelocityDirection;
        this.playerBodyDirection = playerBodyDirection;
        this.relativeDistance = relativeDistance;
        this.relativeDirection = relativeDirection;
        this.isTerminal = isTerminal;
    }

    /**
     * @param ballVelocityNorm
     * @param ballVelocityDirection
     * @param playerVelocityNorm
     * @param playerVelocityDirection
     * @param playerBodyDirection
     * @param relativeDistance
     * @param relativeDirection
     */
    public State(float ballVelocityNorm, float ballVelocityDirection,
                 float playerVelocityNorm, float playerVelocityDirection,
                 float playerBodyDirection, float relativeDistance,
                 float relativeDirection)
    {
        this(ballVelocityNorm, ballVelocityDirection, playerVelocityNorm,
            playerVelocityDirection, playerBodyDirection, relativeDistance,
            relativeDirection, false);
    }

    /**
     * @param isTerminal
     */
    public State(boolean isTerminal)
    {
        this(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 99.0f, 0.0f, isTerminal);
    }

    /*
     * =========================================================================
     * 
     *                     Getters and setters
     * 
     * =========================================================================
     */
    /**
     * @return the ballVelocityNormSteps
     */
    public static int getBallVelocityNormSteps()
    {
        return ballVelocityNormSteps;
    }

    /**
     * @param ballVelocityNormSteps the ballVelocityNormSteps to set
     */
    public static void setBallVelocityNormSteps(int ballVelocityNormSteps)
    {
        State.ballVelocityNormSteps = ballVelocityNormSteps;
    }

    /**
     * @return the ballVelocityDirectionSteps
     */
    public static int getBallVelocityDirectionSteps()
    {
        return ballVelocityDirectionSteps;
    }

    /**
     * @param ballVelocityDirectionSteps the ballVelocityDirectionSteps to set
     */
    public static void setBallVelocityDirectionSteps(
                                                     int ballVelocityDirectionSteps)
    {
        State.ballVelocityDirectionSteps = ballVelocityDirectionSteps;
    }

    /**
     * @return the playerVelocityNormSteps
     */
    public static int getPlayerVelocityNormSteps()
    {
        return playerVelocityNormSteps;
    }

    /**
     * @param playerVelocityNormSteps the playerVelocityNormSteps to set
     */
    public static void setPlayerVelocityNormSteps(int playerVelocityNormSteps)
    {
        State.playerVelocityNormSteps = playerVelocityNormSteps;
    }

    /**
     * @return the playerVelocityDirectionSteps
     */
    public static int getPlayerVelocityDirectionSteps()
    {
        return playerVelocityDirectionSteps;
    }

    /**
     * @param playerVelocityDirectionSteps the playerVelocityDirectionSteps to set
     */
    public static void setPlayerVelocityDirectionSteps(
                                                       int playerVelocityDirectionSteps)
    {
        State.playerVelocityDirectionSteps = playerVelocityDirectionSteps;
    }

    /**
     * @return the playerBodyDirectionSteps
     */
    public static int getPlayerBodyDirectionSteps()
    {
        return playerBodyDirectionSteps;
    }

    /**
     * @param playerBodyDirectionSteps the playerBodyDirectionSteps to set
     */
    public static void setPlayerBodyDirectionSteps(int playerBodyDirectionSteps)
    {
        State.playerBodyDirectionSteps = playerBodyDirectionSteps;
    }

    /**
     * @return the relativeDistanceSteps
     */
    public static int getRelativeDistanceSteps()
    {
        return relativeDistanceSteps;
    }

    /**
     * @param relativeDistanceSteps the relativeDistanceSteps to set
     */
    public static void setRelativeDistanceSteps(int relativeDistanceSteps)
    {
        State.relativeDistanceSteps = relativeDistanceSteps;
    }

    /**
     * @return the relativeDirectionSteps
     */
    public static int getRelativeDirectionSteps()
    {
        return relativeDirectionSteps;
    }

    /**
     * @param relativeDirectionSteps the relativeDirectionSteps to set
     */
    public static void setRelativeDirectionSteps(int relativeDirectionSteps)
    {
        State.relativeDirectionSteps = relativeDirectionSteps;
    }

    /**
     * @return the ballVelocityNorm
     */
    public float getBallVelocityNorm()
    {
        return ballVelocityNorm;
    }

    /**
     * @param ballVelocityNorm the ballVelocityNorm to set
     */
    public void setBallVelocityNorm(float ballVelocityNorm)
    {
        this.ballVelocityNorm = ballVelocityNorm;
    }

    /**
     * @return the ballVelocityDirection
     */
    public float getBallVelocityDirection()
    {
        return ballVelocityDirection;
    }

    /**
     * @param ballVelocityDirection the ballVelocityDirection to set
     */
    public void setBallVelocityDirection(float ballVelocityDirection)
    {
        this.ballVelocityDirection = ballVelocityDirection;
    }

    /**
     * @return the playerVelocityNorm
     */
    public float getPlayerVelocityNorm()
    {
        return playerVelocityNorm;
    }

    /**
     * @param playerVelocityNorm the playerVelocityNorm to set
     */
    public void setPlayerVelocityNorm(float playerVelocityNorm)
    {
        this.playerVelocityNorm = playerVelocityNorm;
    }

    /**
     * @return the playerVelocityDirection
     */
    public float getPlayerVelocityDirection()
    {
        return playerVelocityDirection;
    }

    /**
     * @param playerVelocityDirection the playerVelocityDirection to set
     */
    public void setPlayerVelocityDirection(float playerVelocityDirection)
    {
        this.playerVelocityDirection = playerVelocityDirection;
    }

    /**
     * @return the playerBodyDirection
     */
    public float getPlayerBodyDirection()
    {
        return playerBodyDirection;
    }

    /**
     * @param playerBodyDirection the playerBodyDirection to set
     */
    public void setPlayerBodyDirection(float playerBodyDirection)
    {
        this.playerBodyDirection = playerBodyDirection;
    }

    /**
     * @return the relativeDistance
     */
    public float getRelativeDistance()
    {
        return relativeDistance;
    }

    /**
     * @param relativeDistance the relativeDistance to set
     */
    public void setRelativeDistance(float relativeDistance)
    {
        this.relativeDistance = relativeDistance;
    }

    /**
     * @return the relativeDirection
     */
    public float getRelativeDirection()
    {
        return relativeDirection;
    }

    /**
     * @param relativeDirection the relativeDirection to set
     */
    public void setRelativeDirection(float relativeDirection)
    {
        this.relativeDirection = relativeDirection;
    }

    /**
     * @return the isTerminal
     */
    public boolean isTerminal()
    {
        return isTerminal;
    }

    /**
     * @param isTerminal the isTerminal to set
     */
    public void setTerminal(boolean isTerminal)
    {
        this.isTerminal = isTerminal;
    }

    /*
     * =========================================================================
     * 
     *                          Main methods
     * 
     * =========================================================================
     */
    public State nextState(Action a)
    {
        State nextState;

        if (isTerminal)
        {
            nextState = this;
        }
        else if (relativeDistance < SoccerParams.KICKABLE_MARGIN)
        {
            nextState = new State(true); // Terminal state
        }
        else
        {
            nextState = new State(false); // Non terminal state

            /* ------------ First compute the useful vectors ---------------- */
            Vector2D ballVelocity = new Vector2D(ballVelocityNorm,
                ballVelocityDirection, true);

            Vector2D oldPlayerSpeed = new Vector2D(playerVelocityNorm,
                playerVelocityDirection, true);

            Vector2D newPlayerSpeed;
            if (a.isTurn())
            {
                newPlayerSpeed = oldPlayerSpeed;
            }
            else
            {
                Vector2D playerAcceleration = (new Vector2D(a.getValue()
                        * SoccerParams.DASH_POWER_RATE, playerBodyDirection,
                    true)).normalize(SoccerParams.PLAYER_ACCEL_MAX);

                newPlayerSpeed = oldPlayerSpeed.add(playerAcceleration)
                    .normalize(SoccerParams.PLAYER_SPEED_MAX);
            }

            Vector2D oldRelPosition = new Vector2D(relativeDistance, MathTools
                .normalizeAngle(relativeDirection + playerBodyDirection), true);

            Vector2D newRelPosition = oldRelPosition.add(ballVelocity)
                .subtract(newPlayerSpeed);

            /* ----- Now build the next state using the computed vectors ---- */
            nextState.setBallVelocityNorm(this.ballVelocityNorm
                    * SoccerParams.BALL_DECAY);

            nextState.setBallVelocityDirection(this.ballVelocityDirection);

            nextState.setPlayerVelocityNorm((float) (newPlayerSpeed
                .polarRadius() * SoccerParams.PLAYER_DECAY));

            nextState.setPlayerVelocityDirection((float) newPlayerSpeed
                .polarAngle());

            nextState.setPlayerBodyDirection((float) MathTools
                .normalizeAngle(playerBodyDirection
                        + (a.isTurn() ? a.getValue() : 0.0f)));

            nextState.setRelativeDistance((float) newRelPosition.polarRadius());

            nextState.setRelativeDirection((float) MathTools
                .normalizeAngle(newRelPosition.polarAngle()
                        - nextState.getPlayerBodyDirection()));
        }

        return nextState;
    }

    public State discretize(int ballVelocityNormSteps,
                            int ballVelocityDirectionSteps,
                            int playerVelocityNormSteps,
                            int playerVelocityDirectionSteps,
                            int playerBodyDirectionSteps,
                            int relativeDistanceSteps,
                            int relativeDirectionSteps)

    {
        ballVelocityNorm = (float) MathTools.discretize(ballVelocityNorm, 0.0f,
            SoccerParams.BALL_SPEED_MAX, ballVelocityNormSteps, 0.0f);

        ballVelocityDirection = (float) MathTools.discretize(
            ballVelocityDirection, -180.0f, 180.0f, ballVelocityDirectionSteps,
            0.0f);

        playerVelocityNorm = (float) MathTools.discretize(playerVelocityNorm,
            0.0f, SoccerParams.PLAYER_SPEED_MAX, playerVelocityNormSteps, 0.0f);

        playerVelocityDirection = (float) MathTools.discretize(
            playerVelocityDirection, -180.0f, 180.0f,
            playerVelocityDirectionSteps, 0.0f);

        playerBodyDirection = (float) MathTools.discretize(playerBodyDirection,
            -180.0f, 180.0f, playerBodyDirectionSteps, 0.0f);

        relativeDistance = (float) MathTools.discretize(relativeDistance, 0.0f,
            125.0f, relativeDistanceSteps, 0.0f);

        relativeDirection = (float) MathTools.discretize(relativeDirection,
            -180.0f, 180.0f, relativeDirectionSteps, 0.0f);

        return this;
    }

    public State discretize()
    {
        discretize(ballVelocityNormSteps, ballVelocityDirectionSteps,
            playerVelocityNormSteps, playerVelocityDirectionSteps,
            playerBodyDirectionSteps, relativeDistanceSteps,
            relativeDirectionSteps);
        
        return this;
    }

    public String toString()
    {
        String str = "{";

        str += ballVelocityNorm + ", ";
        str += ballVelocityDirection + ", ";
        str += playerVelocityNorm + ", ";
        str += playerVelocityDirection + ", ";
        str += playerBodyDirection + ", ";
        str += relativeDistance + ", ";
        str += relativeDirection + "}";

        return str;
    }
}
