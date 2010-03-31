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

    /**
     * This function discretizes the input value to the middle value of an
     * interval.
     * 
     * @param value
     * @param minValue
     * @param maxValue
     * @param nbOfSteps
     * @return
     */
    public static double discretize(double value, double minValue,
                                    double maxValue, int nbOfSteps,
                                    float intervalPosition)
    {
        if (intervalPosition > 1.0f || intervalPosition < 0.0f)
        { // Invalid intervalPosition
            intervalPosition = 0.5f;
        }

        double intervalLength = Math.abs((maxValue - minValue) / nbOfSteps);

        return Math.min(value - (value % intervalLength) + intervalLength
                * intervalPosition, maxValue - intervalLength
                * (1.0f - intervalPosition * intervalLength));
    }

    public static Vector2D toCartesianCoordinates(double radius, double angle)
    {
        return new Vector2D(radius * Math.cos(Math.toRadians(angle)), radius
                * Math.sin(Math.toRadians(angle)));
    }

}
