/**
 * @author Sebastien Lentz
 *
 */
class MobileObject
{
    protected double posX, posY; // (x,y) coordinates of this object
    protected double velX, velY; // (x,y) velocities of this object

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
        this.posX = posX;
        this.posY = posY;
        this.velX = velX;
        this.velY = velY;
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the posX
     */
    public double getPosX()
    {
        return posX;
    }

    /**
     * @param posX
     *            the posX to set
     */
    public void setPosX(double posX)
    {
        this.posX = posX;
    }

    /**
     * @return the posY
     */
    public double getPosY()
    {
        return posY;
    }

    /**
     * @param posY
     *            the posY to set
     */
    public void setPosY(double posY)
    {
        this.posY = posY;
    }

    /**
     * @return the velX
     */
    public double getVelX()
    {
        return velX;
    }

    /**
     * @param velX
     *            the velX to set
     */
    public void setVelX(double velX)
    {
        this.velX = velX;
    }

    /**
     * @return the velY
     */
    public double getVelY()
    {
        return velY;
    }

    /**
     * @param velY
     *            the velY to set
     */
    public void setVelY(double velY)
    {
        this.velY = velY;
    }

    /*
     * =========================================================================
     * 
     *                          Other methods
     * 
     * =========================================================================
     */    
    /**
     * This method computes the distance between this object and the point
     * located at the coordinates (x2,y2).
     * 
     * @param x2
     *          the abscissa of the point.
     * @param y2
     *          the ordinate of the point.
     * @return
     *          the distance to the point.
     */
    protected double distanceTo(double x2, double y2)
    {
        return Math.sqrt(Math.pow(x2 - posX, 2) + Math.pow(y2 - posY, 2));
    }

    /**
     * This method computes the distance between this object and the object o.
     * 
     * @param o
     *          the object o.
     * @return
     *          the distance to the o.
     */
    protected double distanceTo(MobileObject o)
    {
        return distanceTo(o.getPosX(), o.getPosY());
    }
}