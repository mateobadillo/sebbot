/**
 * @author Sebastien Lentz
 *
 */
class Player extends MobileObject
{
    private char   team;
    private int    playerType;
    private double bodyDirection;

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
            char team, char playerType, double bodyDirection)
    {
        super(posX, posY, velX, velY);
        this.team = team;
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
     * @param bodyDirection
     *            the bodyDirection to set
     */
    public void setBodyDirection(double bodyDirection)
    {
        this.bodyDirection = bodyDirection;
    }

    /**
     * @return the team
     */
    public char getTeam()
    {
        return team;
    }

    /**
     * @param team
     *            the team to set
     */
    public void setTeam(char team)
    {
        this.team = team;
    }

    /**
     * @return the playerType
     */
    public int getPlayerType()
    {
        return playerType;
    }

    /**
     * @param playerType
     *            the playerType to set
     */
    public void setPlayerType(int playerType)
    {
        this.playerType = playerType;
    }

    /*
     * =========================================================================
     * 
     *                          Other methods
     * 
     * =========================================================================
     */
    /**
     * This methods computes the angle this player needs to turn in order
     * to face the point (x2, y2).
     * 
     * @param x2
     *          the abscissa of the point.
     * @param y2
     *          the ordinate of the point.
     * @return
     *          the angle to turn.
     */
    public double directionOf(double x2, double y2)
    {
        double angle;
        if ((y2 - posY == 0) && (x2 - posX == 0))
        {
            angle = 0.0D;
        }
        else
        {
            angle = MathTools.radToDeg(Math.atan2(y2 - posY, x2 - posX));
        }

        return MathTools.normalizeAngle(angle - bodyDirection);
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
    public double directionOf(MobileObject o)
    {
        return directionOf(o.getPosX(), o.getPosY());
    }

}
