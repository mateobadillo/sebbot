import java.lang.Math;

/**
 * @author Sebastien Lentz
 *
 */
class Brain extends Thread
{
    private Sebbot        sebbot;       // The agent controlled by this brain
    private FullstateInfo fullstateInfo; // Contains all info about the
                                         //     current state of the game
    private char          teamSide;     // Side of the agent ([l]eft/[r]ight)
    private int           playerNumber; // Uniform number of the agent

    /**
     * Constructor.
     * 
     * @param sebbot
     * @param teamSide
     * @param playerNumber
     */
    public Brain(Sebbot sebbot, char teamSide, int playerNumber)
    {
        this.sebbot = sebbot;
        this.fullstateInfo = new FullstateInfo("");
        this.teamSide = teamSide;
        this.playerNumber = playerNumber;
    }

    /**
     * @return the fullstateInfo
     */
    public FullstateInfo getFullstateInfo()
    {
        return fullstateInfo;
    }

    /**
     * @param fullstateInfo
     *            the fullstateInfo to set
     */
    public void setFullstateInfo(FullstateInfo fullstateInfo)
    {
        this.fullstateInfo = fullstateInfo;
    }

    /**
     * This is the main function of the Brain. The strategy is straight forward:
     * 
     * If the ball is the agent's kickable margin, then kick it in the direction
     * of the opposite goal.     * 
     * If not, turn in the direction of the ball then run towards it.
     * 
     * @see java.lang.Thread#run()
     */
    public void run()
    {
//        int lastTimeStep = 0;
        while (true) // TODO: change according to the play mode.
        {
            // TODO: debug agent skipping some steps.
//            if (fullstateInfo.getStepTime() - lastTimeStep != 1)
//            {
//                System.out.println("Agent info: " + playerNumber + " "
//                        + teamSide);
//                System.out.println("Brain Last time: " + lastTimeStep);
//                System.out.println("Brain Current time: "
//                        + fullstateInfo.getStepTime());
//            }
//            lastTimeStep = fullstateInfo.getStepTime();

            Ball ball = fullstateInfo.getBall();
            
            Player player = (teamSide == 'l') ?
                    fullstateInfo.getLeftTeam()[playerNumber - 1] :
                    fullstateInfo.getRightTeam()[playerNumber - 1];

            if (player.distanceTo(ball) > 0.5d)
            { // The ball is not in the kickable margin.
                if (Math.abs(player.directionOf(ball)) < 30.0)
                { // The player is directed at the ball.
                    sebbot.dash(100);
                }
                else
                { // The player needs to turn in the direction of the ball.
                    sebbot.turn(player.directionOf(ball));
                }
            }
            else
            { // The ball is in the kickable margin => kick it towards the goal!
                double goalPosX = this.teamSide == 'l' ? 52.5d : -52.5d;
                sebbot.kick(100, player.directionOf(goalPosX, 0));
            }

            // Wait for next cycle before sending another command.
            try
            {
                Thread.sleep(SoccerParams.SIMULATOR_STEP);
            }
            catch (Exception e)
            {
                System.err.println(e);
            }
        }

    }
}
