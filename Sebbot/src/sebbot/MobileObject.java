package sebbot;

/**
 * @author Sebastien Lentz
 *
 */
public class MobileObject
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