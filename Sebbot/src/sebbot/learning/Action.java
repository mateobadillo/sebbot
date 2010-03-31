package sebbot.learning;

import sebbot.MathTools;
import sebbot.SoccerParams;

/**
 * @author Sebastien Lentz
 *
 */
public class Action
{
    private float   value; // Value of the action:-180<=turn<=180 or 0<=dash<=100.
    private boolean isTurn; // True if action = turn, false if action = dash.

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * @param value
     * @param isTurn
     */
    public Action(float value, boolean isTurn)
    {
        this.value = value;
        this.isTurn = isTurn;
    }

    /*
     * =========================================================================
     * 
     *                     Getters and setters
     * 
     * =========================================================================
     */
    /**
     * @return the value
     */
    public float getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(float value)
    {
        this.value = value;
    }

    /**
     * @return the isTurn
     */
    public boolean isTurn()
    {
        return isTurn;
    }

    /**
     * @param isTurn the isTurn to set
     */
    public void setTurn(boolean isTurn)
    {
        this.isTurn = isTurn;
    }

    /*
     * =========================================================================
     * 
     *                          Main methods
     * 
     * =========================================================================
     */
    /**
     * @param dashValueSteps
     * @param turnValueSteps
     * @return
     */
    public Action discretize(int dashValueSteps, int turnValueSteps)

    {
        if (isTurn)
        {
            value = (float) MathTools.discretize(value, -180.0f, 180.0f,
                turnValueSteps,0.5f);
        }
        else
        {
            value = (float) MathTools.discretize(value, 0.0f, 100.0f, dashValueSteps,1.0f);
        }

        return this;
    }
    
    public String toString()
    {
        String str = "{" + (isTurn ? "TURN" : "DASH") + ", " + value + "}";
        
        return str;
    }


}
