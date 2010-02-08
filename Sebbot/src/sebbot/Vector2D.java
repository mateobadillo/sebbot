package sebbot;

/**
 * @author Sebastien Lentz
 *
 */
public class Vector2D implements Cloneable
{
    private double x;
    private double y;

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Object clone()
    {
        Vector2D cloneVector;
        try
        {
            cloneVector = (Vector2D) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            cloneVector = null;
        }

        return cloneVector;
    }

    /**
     * @return the x
     */
    public double getX()
    {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY()
    {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y)
    {
        this.y = y;
    }

    public Vector2D subtract(Vector2D v)
    {
        return new Vector2D(x - v.getX(), y - v.getY());
    }

    public Vector2D add(Vector2D v)
    {
        return new Vector2D(x + v.getX(), y + v.getY());
    }

    public Vector2D multiply(double f)
    {
        return new Vector2D(x * f, y * f);
    }

    public double multiply(Vector2D v)
    {
        return x * v.getX() + y * v.getY();
    }

    public double getPolarRadius()
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public double getPolarAngle()
    {
        double angle;
        if ((y == 0) && (x == 0))
        {
            angle = 0.0D;
        }
        else
        {
            angle = Math.toDegrees(Math.atan2(y, x));
        }

        return angle;

    }
    
    public void normalize(double modulusMax)
    {
        double currentModulus = getPolarRadius();
        if (currentModulus > modulusMax)
        {
            this.x *= (modulusMax / currentModulus);
            this.y *= (modulusMax / currentModulus);
        }
    }


    public double distanceTo(Vector2D v)
    {
        return v.subtract(this).getPolarRadius();
    }
    
    public double distanceTo(double x, double y)
    {
        return (new Vector2D(x, y)).subtract(this).getPolarRadius();
    }

    public double directionOf(Vector2D v)
    {
        return v.subtract(this).getPolarAngle();
    }

    public double directionOf(double x, double y)
    {
        return (new Vector2D(x, y)).subtract(this).getPolarAngle();
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
        return String.format("(%g - %g)", x, y);
    }

    
}
