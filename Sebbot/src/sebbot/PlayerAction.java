package sebbot;

/**
 * @author Sebastien Lentz
 *
 */
public class PlayerAction
{
    /*
     * Class constants
     */
    public final static int DASH = 0;
    public final static int KICK = 1;
    public final static int TURN = 2;

    /*
     * Private members
     */
    private int             actionType; // "DASH", "KICK" or "TURN".
    private double          power;     // The power arg of a KICK or DASH cmd.
    private double          direction; // The direction arg of a KICK or TURN cmd.
    private RobocupClient   client;    // The client to send actions to the server.

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * Constructor
     * 
     * @param actionType
     * @param power
     * @param direction
     * @param client
     */
    public PlayerAction(int actionType, double power, double direction,
            RobocupClient client)
    {
        this.actionType = actionType;
        this.power = power;
        this.direction = direction;
        this.client = client;
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the actionType
     */
    public int getActionType()
    {
        return actionType;
    }

    /**
     * @param actionType the actionType to set
     */
    public void setActionType(int actionType)
    {
        this.actionType = actionType;
    }

    /**
     * @return the power
     */
    public double getPower()
    {
        return power;
    }

    /**
     * @param power the power to set
     */
    public void setPower(double power)
    {
        this.power = power;
    }

    /**
     * @return the direction
     */
    public double getDirection()
    {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(double direction)
    {
        this.direction = direction;
    }

    /*
     * =========================================================================
     * 
     *                          Other methods
     * 
     * =========================================================================
     */
    /**
     * This method sends this action to the server.
     */
    public void execute()
    {
        switch (actionType)
        {
        case DASH:
            client.dash(power);
            break;
        case KICK:
            client.kick(power, direction);
            break;
        case TURN:
            client.turn(direction);
            break;
        default:
            break;
        }
    }

}
