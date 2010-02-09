package sebbot;

import java.util.Collection;

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
     *                          Movement methods
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
    public double angleFromBody(double x, double y)
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
    public double angleFromBody(Vector2D p)
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
    public double angleFromBody(MobileObject o)
    {
        return angleFromBody(o.getPosition());
    }
    
    public Vector2D nextPosition(Vector2D initialPosition,
            Vector2D initialVelocity, double power)
    {
        return super.nextPosition(initialPosition, initialVelocity,
                SoccerParams.PLAYER_SPEED_MAX, SoccerParams.PLAYER_ACCEL_MAX,
                SoccerParams.DASH_POWER_RATE, power, bodyDirection);
    }

    public Vector2D nextPosition(double power)
    {
        return this.nextPosition(this.position, this.velocity, power);
    }
    
    public Vector2D nextVelocity(Vector2D initialVelocity, double power)
    {
        return super.nextVelocity(initialVelocity, SoccerParams.PLAYER_DECAY,
                SoccerParams.PLAYER_SPEED_MAX, SoccerParams.PLAYER_ACCEL_MAX,
                SoccerParams.DASH_POWER_RATE, power, bodyDirection);
    }

    public Vector2D nextVelocity(double power)
    {
        return this.nextVelocity(this.velocity, power);
    }

    public int timeToReach(Vector2D position)
    {
        if(position == null)
        {
            System.out.println("null");
        }
        int nbOfSteps = 0;
        double bodyAngle = this.bodyDirection;

        if (Math.abs(angleFromBody(position)) > 1.0d)
        {
            this.bodyDirection = MathTools.normalizeAngle(this.bodyDirection
                    + angleFromBody(position));
            nbOfSteps++;
        }

        Vector2D lastVelocity = this.velocity;
        Vector2D lastPosition = this.position;
        Vector2D currentPosition = this.position;
        while (currentPosition.distanceTo(position) > SoccerParams.KICKABLE_MARGIN)
        {
            lastPosition = currentPosition;
            currentPosition = nextPosition(lastPosition, lastVelocity, 100.0d);
            lastVelocity = nextVelocity(lastVelocity, 100.0d);
            nbOfSteps++;

            if (currentPosition.distanceTo(position) > lastPosition.distanceTo(position))
            {
                System.out.println("----------");
                System.out.println("position: " + this.position);
                System.out.println("position to reach: " + position);
                System.out.println("relative position: " + (position.subtract(this.position)));
                System.out.println("angle from body: " + angleFromBody(position));
                System.out.println("Nb of steps: " + nbOfSteps);
                System.out.println("----------");

                break;
            }
        }

        this.bodyDirection = bodyAngle;

        return nbOfSteps;
    }

    public Vector2D interceptionPoint(Collection<Vector2D> trajectory)
    {
        Vector2D interceptionPoint = null;
        int nbOfTimeSteps = 999;

        for (Vector2D point : trajectory)
        {
            if (timeToReach(point) < nbOfTimeSteps)
            {
                interceptionPoint = point;
                nbOfTimeSteps = timeToReach(point);
            }
        }

        return interceptionPoint;
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
        return "Player " + (isLeftSide() ? "left " : "right ") + uniformNumber
                + ": " + super.toString() + " - BodyDir: " + bodyDirection;
    }

}
