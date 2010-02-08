package sebbot;

import java.util.ArrayList;

/**
 * @author Sebastien Lentz
 *
 */
public class Ball extends MobileObject
{

    /**
     * @param posX
     * @param posY
     * @param velX
     * @param velY
     */
    public Ball(float posX, float posY, float velX, float velY)
    {
        super(posX, posY, velX, velY);
    }

    public Vector2D nextPosition(double power, double angle)
    {
        return super.nextPosition(SoccerParams.BALL_SPEED_MAX,
                SoccerParams.BALL_ACCEL_MAX, SoccerParams.KICK_POWER_RATE,
                power, angle);
    }

    public Vector2D nextPosition(Vector2D initialPosition,
            Vector2D initialVelocity, double power, double angle)
    {
        return super.nextPosition(initialPosition, initialVelocity,
                SoccerParams.BALL_SPEED_MAX, SoccerParams.BALL_ACCEL_MAX,
                SoccerParams.KICK_POWER_RATE, power, angle);
    }

    public Vector2D nextVelocity()
    {
        return super.nextVelocity(SoccerParams.BALL_DECAY);
    }
    
    public Vector2D nextVelocity(Vector2D initialVelocity)
    {
        return super.nextVelocity(initialVelocity, SoccerParams.BALL_DECAY);
    }
    
    public Vector2D nextVelocity(double power, double angle)
    {
        return super.nextVelocity(SoccerParams.BALL_DECAY,
                SoccerParams.BALL_SPEED_MAX, SoccerParams.BALL_ACCEL_MAX,
                SoccerParams.KICK_POWER_RATE, power, angle);
    }

    public Vector2D nextVelocity(Vector2D initialVelocity, double power,
            double angle)
    {
        return super.nextVelocity(initialVelocity, SoccerParams.BALL_DECAY,
                SoccerParams.BALL_SPEED_MAX, SoccerParams.BALL_ACCEL_MAX,
                SoccerParams.KICK_POWER_RATE, power, angle);
    }

    public ArrayList<Vector2D> trajectory(Vector2D initialPosition, Vector2D initialVelocity)
    {
        Vector2D lastPosition = initialPosition;
        Vector2D lastVelocity = initialVelocity;
        ArrayList<Vector2D> trajectory = new ArrayList<Vector2D>();
        trajectory.add(lastPosition);
        Vector2D currentPosition = nextPosition(lastPosition, lastVelocity);

        while (currentPosition.distanceTo(lastPosition) > SoccerParams.KICKABLE_MARGIN)
        {
            trajectory.add(currentPosition);
            lastPosition = currentPosition;
            currentPosition = nextPosition(lastPosition, lastVelocity);
            lastVelocity = nextVelocity(lastVelocity);
        }
        
        return trajectory;
    }

    public ArrayList<Vector2D> trajectory()
    {
        return trajectory(this.position, this.velocity);
    }

}
