package sebbot;

/**
 * @author Sebastien Lentz
 *
 */
public class MathTools
{
    /**
     * Normalize the input angle so that it belongs to the interval [-180, 180].
     * 
     * @param a
     *          the input angle to normalize.
     * @return
     *          the normalized angle.
     */
    public static double normalizeAngle(double a)
    {
        if (Math.abs(a) > 360.0D)
        {
            a %= (360.0D);
        }
        if (a > 180.0D)
        {
            a -= 360.0D;
        }
        if (a < -180.0D)
        {
            a += 360.0D;
        }

        return a;
    }

    /**
     * Quantize the input number according to the input step.
     * 
     * @param nb
     *          the number to quantize.
     * @param step
     *          the quantization step.
     * @return
     *          the quantized number.
     */
    public static double quantize(double nb, double step)
    {
        return Math.rint(nb / step) * step;
    }

    public static Vector2D toCartesianCoordinates(double radius, double angle)
    {
        return new Vector2D(radius * Math.cos(Math.toRadians(angle)), radius
                * Math.sin(Math.toRadians(angle)));
    }

}
