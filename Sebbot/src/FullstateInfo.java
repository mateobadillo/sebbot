import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used o store all the game information given in a fullstate
 * message sent from the server. 
 * 
 * @author Sebastien Lentz
 * 
 */
public class FullstateInfo
{
    /*
     * Constants of the class.
     */
    final static String PLAYMODE_PATTERN  = "\\(pmode ([a-zA-Z_]*)\\)";
    final static String REAL_NB_PATTERN   = "((?:\\-)?[0-9]+(?:\\.[0-9]+(?:e(?:\\-)?[0-9]+)?)?)";
    final static String BALL_PATTERN      = "\\(\\(b\\) " + REAL_NB_PATTERN
                                                  + " " + REAL_NB_PATTERN + " "
                                                  + REAL_NB_PATTERN + " "
                                                  + REAL_NB_PATTERN + "\\)";
    final static String PLAYER_PATTERN    = "\\(\\(p ([lr]) ([1-9]{1,2}) ([g[0-9]+])\\) "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN;
    final static String TIME_STEP_PATTERN = "\\(fullstate ([0-9]+) \\(";

    /*
     * Members of the class.
     */
    private int         timeStep;     // The time step of the game
    private String      playMode;     // The play mode of the game
    private String      fullstateMsg; // The fullstate msg received from server
    private Ball        ball;         // The ball of the game
    private Player[]    leftTeam;     // Players of left and right team,
    private Player[]    rightTeam;    //  indexed by their uniform number - 1

    /**
     * Constructor.
     * 
     * @param fullstateMsg
     */
    public FullstateInfo(String fullstateMsg)
    {
        this.fullstateMsg = fullstateMsg;
        this.ball = new Ball(0, 0, 0, 0);
        leftTeam = new Player[11];
        rightTeam = new Player[11];
        for (int i = 0; i < 11; i++)
        {
            leftTeam[i] = new Player(0, 0, 0, 0, 'l', '0', 0);
            rightTeam[i] = new Player(0, 0, 0, 0, 'r', '0', 0);
        }
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the timeStep
     */
    public int getTimeStep()
    {
        return timeStep;
    }

    /**
     * @param timeStep the timeStep to set
     */
    public void setTimeStep(int timeStep)
    {
        this.timeStep = timeStep;
    }

    /**
     * @return the playMode
     */
    public String getPlayMode()
    {
        return playMode;
    }

    /**
     * @param playMode the playMode to set
     */
    public void setPlayMode(String playMode)
    {
        this.playMode = playMode;
    }

    /**
     * @return the ball
     */
    public Ball getBall()
    {
        return ball;
    }

    /**
     * @return the leftTeam
     */
    public Player[] getLeftTeam()
    {
        return leftTeam;
    }

    /**
     * @return the rightTeam
     */
    public Player[] getRightTeam()
    {
        return rightTeam;
    }

    /**
     * @return the fullstateMsg
     */
    public String getFullstateMsg()
    {
        return fullstateMsg;
    }

    /**
     * @param fullstateMsg
     *            the fullstateMsg to set
     */
    public void setFullstateMsg(String fullstateMsg)
    {
        this.fullstateMsg = fullstateMsg;
    }

    /*
     * =========================================================================
     * 
     *                          Main methods
     * 
     * =========================================================================
     */
    /**
     * This method parses the fullstateMsg string and updates the variables
     * consequently.
     */
    public void parse()
    {
        // Gather playMode information.
        Pattern pattern = Pattern.compile(PLAYMODE_PATTERN);
        Matcher matcher = pattern.matcher(fullstateMsg);
        if (matcher.find())
        {
            this.playMode = matcher.group(1);
        }
        else
        {
            System.err.println("Could not parse play mode info: "
                    + fullstateMsg);
        }

        // Gather ball information.
        pattern = Pattern.compile(BALL_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        if (matcher.find())
        {
            ball.setPosX(Double.valueOf(matcher.group(1)));
            ball.setPosY(Double.valueOf(matcher.group(2)));
            ball.setVelX(Double.valueOf(matcher.group(3)));
            ball.setVelY(Double.valueOf(matcher.group(4)));
        }
        else
        {
            System.err.println("Could not parse ball info: " + fullstateMsg);
        }

        // Get time step.
        pattern = Pattern.compile(TIME_STEP_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        if (matcher.find())
        {
            timeStep = Integer.valueOf(matcher.group(1));
        }
        else
        {
            System.err.println("Could not parse time step: " + fullstateMsg);
        }

        // Gather players information.
        pattern = Pattern.compile(PLAYER_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        Player[] team; // Team of the player currently being parsed.
        int playerNumber; // Number of the player currently being parsed.
        while (matcher.find())
        {
            if (matcher.group(1).compareToIgnoreCase("l") == 0)
            {
                team = this.leftTeam;
            }
            else
            {
                team = this.rightTeam;
            }

            playerNumber = Integer.valueOf(matcher.group(2));

            if (matcher.group(3).compareToIgnoreCase("g") == 0)
            {
                team[playerNumber - 1].setPlayerType(-1);
            }
            else
            {
                team[playerNumber - 1].setPlayerType(Integer.valueOf(matcher
                        .group(3)));
            }

            team[playerNumber - 1].setPosX(Double.valueOf(matcher.group(4)));
            team[playerNumber - 1].setPosY(Double.valueOf(matcher.group(5)));
            team[playerNumber - 1].setVelX(Double.valueOf(matcher.group(6)));
            team[playerNumber - 1].setVelY(Double.valueOf(matcher.group(7)));
            team[playerNumber - 1].setBodyDirection(Double.valueOf(matcher
                    .group(8)));
        }
    }

    // Debugging
    public void printAll()
    {
        System.out.println("------ Fullstate at step: " + timeStep
                + " ---------");
        System.out.println("ball: " + ball.getPosX() + " " + ball.getPosY());
        Player pi;
        for (int i = 0; i < 11; i++)
        {
            pi = leftTeam[i];
            System.out.println("Player nr" + (i + 1) + " " + pi.getPosX() + " "
                    + pi.getPosY() + " " + pi.getBodyDirection());
        }

        System.out.println();

        for (int i = 0; i < 11; i++)
        {
            pi = rightTeam[i];
            System.out.println("Player nr" + (i + 1) + " " + pi.getPosX() + " "
                    + pi.getPosY() + " " + pi.getBodyDirection());
        }

    }

    // Debugging
    public void logFullstate(String path) throws IOException
    {
        String fs = "";
        fs += "------ " + System.currentTimeMillis() + " ---------\n";
        fs += "ball: " + ball.getPosX() + " " + ball.getPosY() + "\n";
        Player pi;
        for (int i = 0; i < 11; i++)
        {
            pi = leftTeam[i];
            fs += "Player nr" + i + " " + pi.getPosX() + " " + pi.getPosY()
                    + " " + pi.getBodyDirection() + "\n";
        }
        for (int i = 0; i < 11; i++)
        {
            pi = rightTeam[i];
            fs += "Player nr" + i + " " + pi.getPosX() + " " + pi.getPosY()
                    + " " + pi.getBodyDirection() + "\n";
        }

        Path logfile = Paths.get(path);
        byte data[] = fs.getBytes();

        OutputStream out = null;
        out = new BufferedOutputStream(logfile.newOutputStream(CREATE, APPEND));
        try
        {
            out.write(data, 0, data.length);
        }
        catch (IOException x)
        {
            System.err.println(x);
        }
        finally
        {
            if (out != null)
            {
                out.flush();
                out.close();
            }
        }

    }

}
