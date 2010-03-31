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
     * This function returns the number of the interval that contains the input
     * value according to the number of the steps, the min and max values of
     * the continuous range that is being considered.
     * 
     * @param value the input value to index.
     * @param minValue the min value of the continuous range.
     * @param maxValue the max value of the continuous range.
     * @param nbOfSteps the number of intervals in the continuous range.
     * @return
     */
    public static int valueToIndex(double value, double minValue,
                                   double maxValue, int nbOfSteps)
    {
        int index = (int) Math.rint((value - minValue) / (maxValue - minValue)
                * nbOfSteps);

        return index >= nbOfSteps ? nbOfSteps - 1 : index;
    }

    /**
     * This function returns a representative value of the the interval indexed
     * by the input integer.
     * 
     * @param index the index of the interval in the continuous range.
     * @param minValue the min value of the continuous range.
     * @param maxValue the max value of the continuous range.
     * @param nbOfSteps the number of intervals in the continuous range.
     * @param intervalPosition the position of the representative value inside
     *                         the interval. 0<=intervalPosition<=1.
     * @return
     */
    public static double indexToValue(int index, double minValue,
                                      double maxValue, int nbOfSteps,
                                      float intervalPosition)
    {
        if (intervalPosition > 1.0f || intervalPosition < 0.0f)
        { // Invalid intervalPosition
            intervalPosition = 0.5f;
        }

        double intervalLength = Math.abs((maxValue - minValue) / nbOfSteps);

        return minValue + intervalLength * (intervalPosition + index);
    }

    /**
     * This function discretizes the input value to the middle value of an
     * interval.
     * 
     * @param value the input value to discretize.
     * @param minValue the min value of the continuous range.
     * @param maxValue the max value of the continuous range.
     * @param nbOfSteps the number of intervals in the continuous range.
     * @param intervalPosition the position of the representative value inside
     *                         the interval. 0<=intervalPosition<=1.
     * @return
     */
    public static double discretize(double value, double minValue,
                                    double maxValue, int nbOfSteps,
                                    float intervalPosition)
    {
        return indexToValue(valueToIndex(value, minValue, maxValue, nbOfSteps),
            minValue, maxValue, nbOfSteps, intervalPosition);

        //        if (intervalPosition > 1.0f || intervalPosition < 0.0f)
        //        { // Invalid intervalPosition
        //            intervalPosition = 0.5f;
        //        }
        //
        //        double intervalLength = Math.abs((maxValue - minValue) / nbOfSteps);
        //
        //        return Math.min(value - (value % intervalLength) + intervalLength
        //                * intervalPosition, maxValue - intervalLength
        //                * (1.0f - intervalPosition * intervalLength));
    }

    public static Vector2D toCartesianCoordinates(double radius, double angle)
    {
        return new Vector2D(radius * Math.cos(Math.toRadians(angle)), radius
                * Math.sin(Math.toRadians(angle)));
    }

}
