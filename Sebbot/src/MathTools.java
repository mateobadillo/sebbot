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
     * Convert an angle expressed in radians to degrees.
     * 
     * @param a
     *          the angle in radians.
     * @return
     *          the angle converted in degrees.
     */
    public static double radToDeg(double a)
    {
        return a * 180.0D / Math.PI;
    }

    /**
     * Convert an angle expressed in degrees to radians.
     * 
     * @param a
     *          the angle in degrees.
     * @return
     *          the angle converted in radians.
     */
    public static double degToRad(double a)
    {
        return a / 180.0D * Math.PI;
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

}
