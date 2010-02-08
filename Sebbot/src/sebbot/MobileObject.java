package sebbot;

/**
 * @author Sebastien Lentz
 *
 */
public abstract class MobileObject
{
    protected Vector2D position;
    protected Vector2D velocity;

    /**
     * Constructor.
     * 
     * @param posX
     * @param posY
     * @param velX
     * @param velY
     */
    public MobileObject(double posX, double posY, double velX, double velY)
    {
        position = new Vector2D(posX, posY);
        velocity = new Vector2D(velX, velY);
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the position
     */
    public Vector2D getPosition()
    {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Vector2D position)
    {
        this.position = position;
    }

    /**
     * @return the velocity
     */
    public Vector2D getVelocity()
    {
        return velocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(Vector2D velocity)
    {
        this.velocity = velocity;
    }

    /*
     * =========================================================================
     * 
     *                          Misc methods
     * 
     * =========================================================================
     */
    public double distanceTo(Vector2D v)
    {
        return position.distanceTo(v);
    }

    public double distanceTo(MobileObject o)
    {
        return distanceTo(o.getPosition());
    }

    protected Vector2D velocity(Vector2D initialVelocity, double maxVelocity,
            double maxAccel, double powerRate, double power, double angle)
    {
        Vector2D v = MathTools.toCartesianCoordinates(power * powerRate, angle);
        v.normalize(maxAccel);
        v = v.add(initialVelocity);
        v.normalize(maxVelocity);

        return v;
    }

    protected Vector2D velocity(double maxVelocity, double maxAccel,
            double powerRate, double power, double angle)
    {
        return velocity(this.velocity, maxVelocity, maxAccel, powerRate, power,
                angle);
    }

    public Vector2D nextPosition(Vector2D initialPosition,
            Vector2D initialVelocity)
    {
        return initialPosition.add(initialVelocity);
    }

    public Vector2D nextPosition()
    {
        return nextPosition(position, velocity);
    }

    protected Vector2D nextPosition(Vector2D initialPosition,
            Vector2D initialVelocity, double maxVelocity, double maxAccel,
            double powerRate, double power, double angle)
    {
        return velocity(initialVelocity, maxVelocity, maxAccel, powerRate,
                power, angle).add(initialPosition);
    }

    protected Vector2D nextPosition(double maxVelocity, double maxAccel,
            double powerRate, double power, double angle)
    {
        return nextPosition(this.position, this.velocity, maxVelocity,
                maxAccel, powerRate, power, angle);
    }

    public Vector2D nextVelocity(Vector2D initialVelocity, double decay)
    {
        return initialVelocity.multiply(decay);
    }

    public Vector2D nextVelocity(double decay)
    {
        return nextVelocity(this.velocity, decay);
    }

    protected Vector2D nextVelocity(Vector2D initialVelocity, double decay,
            double maxVelocity, double maxAccel, double powerRate,
            double power, double angle)
    {
        return velocity(initialVelocity, maxVelocity, maxAccel, powerRate,
                power, angle).multiply(decay);
    }

    protected Vector2D nextVelocity(double decay, double maxVelocity,
            double maxAccel, double powerRate, double power, double angle)
    {
        return nextVelocity(this.velocity, decay, maxVelocity, maxAccel,
                powerRate, power, angle);
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
        return "Pos: " + position + " - Vel: " + velocity;
    }

}