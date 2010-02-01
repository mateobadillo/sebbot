package sebbot;

/**
 * @author Sebastien Lentz
 *
 */
public class Player extends MobileObject
{
    private boolean leftSide;
    private int     playerType;
    private int     uniformNumber;
    private double  bodyDirection;

    /**
     * Constructor.
     * 
     * @param posX
     * @param posY
     * @param velX
     * @param velY
     * @param team
     * @param playerType
     * @param bodyDirection
     */
    public Player(double posX, double posY, double velX, double velY,
            boolean team, char playerType, double bodyDirection)
    {
        super(posX, posY, velX, velY);
        this.leftSide = team;
        this.playerType = playerType;
        this.bodyDirection = bodyDirection;
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the bodyDirection
     */
    public double getBodyDirection()
    {
        return bodyDirection;
    }

    /**
     * @param bodyDirection the bodyDirection to set
     */
    public void setBodyDirection(double bodyDirection)
    {
        this.bodyDirection = bodyDirection;
    }

    /**
     * @return the leftSideTeam
     */
    public boolean isLeftSide()
    {
        return leftSide;
    }

    /**
     * @param leftSideTeam the leftSideTeam to set
     */
    public void setLeftSide(boolean leftSide)
    {
        this.leftSide = leftSide;
    }

    /**
     * @return the playerType
     */
    public int getPlayerType()
    {
        return playerType;
    }

    /**
     * @param playerType the playerType to set
     */
    public void setPlayerType(int playerType)
    {
        this.playerType = playerType;
    }

    /**
     * @return the uniformNumber
     */
    public int getUniformNumber()
    {
        return uniformNumber;
    }

    /**
     * @param uniformNumber the uniformNumber to set
     */
    public void setUniformNumber(int uniformNumber)
    {
        this.uniformNumber = uniformNumber;
    }

    /*
     * =========================================================================
     * 
     *                          Math methods
     * 
     * =========================================================================
     */
    /**
     * This methods computes the angle this player needs to turn in order
     * to face the point (x, y).
     * 
     * @param x
     *          the abscissa of the point.
     * @param y
     *          the ordinate of the point.
     * @return
     *          the angle to turn.
     */
    public double getAngleFromBody(double x, double y)
    {
        return MathTools.normalizeAngle(position.directionOf(x, y)
                - bodyDirection);
    }

    /**
     * This methods computes the angle this player needs to turn in order
     * to face the point p.
     * 
     * @param p
     *          the point.
     * @return
     *          the angle to turn.
     */
    public double getAngleFromBody(Vector2D p)
    {
        return MathTools
                .normalizeAngle(position.directionOf(p) - bodyDirection);
    }

    /**
     * This methods computes the angle this player needs to turn in order
     * to face the object o.
     * 
     * @param o
     *          the object.
     * @return
     *          the angle to turn.
     */
    public double getAngleFromBody(MobileObject o)
    {
        return getAngleFromBody(o.getPosition());
    }
    
    /*
     * =========================================================================
     * 
     *                          Other methods
     * 
     * =========================================================================
     */
    public String toString()
    {
        return "Player " + (isLeftSide() ? "left " : "right ") + uniformNumber + ": " + super.toString() + " - BodyDir: " + bodyDirection;
    }


}
