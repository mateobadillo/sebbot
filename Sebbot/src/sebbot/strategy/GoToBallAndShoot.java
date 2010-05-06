package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.PlayerAction;
import sebbot.RobocupClient;
import sebbot.learning.Action;
import sebbot.learning.State;

/**
 * @author Sebastien Lentz
 *
 */
public class GoToBallAndShoot implements Strategy
{
    
    private sebbot.learning.Policy ballCaptureAlgorithm;
    

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * @param ballCaptureAlgorithm
     */
    public GoToBallAndShoot(sebbot.learning.Policy ballCaptureAlgorithm)
    {
        this.ballCaptureAlgorithm = ballCaptureAlgorithm;
    }
    
    /**
     * 
     */
    public GoToBallAndShoot()
    {
        this(new sebbot.learning.HandCodedPolicy());
    }
    
    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the ballCaptureAlgorithm
     */
    public sebbot.learning.Policy getBallCaptureAlgorithm()
    {
        return ballCaptureAlgorithm;
    }

    /**
     * @param ballCaptureAlgorithm the ballCaptureAlgorithm to set
     */
    public void setBallCaptureAlgorithm(sebbot.learning.Policy ballCaptureAlgorithm)
    {
        this.ballCaptureAlgorithm = ballCaptureAlgorithm;
    }
    
    /*
     * =========================================================================
     * 
     *                      Main methods
     * 
     * =========================================================================
     */
    /* (non-Javadoc)
     * @see sebbot.strategy.Strategy#doAction(sebbot.RobocupClient, sebbot.FullstateInfo, sebbot.Player)
     */
    @Override
    public void doAction(RobocupClient rcClient, FullstateInfo fsi,
                         Player player)
    {
        if (!CommonStrategies.shootToGoal(rcClient, fsi, player))
        {
            State state = new State(fsi, player);
            Action action = ballCaptureAlgorithm.chooseAction(state);

            rcClient.getBrain().getActionsQueue().addLast(
                new PlayerAction(action, rcClient));
        }
    }
}
