package sebbot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

/**
 * This class implements the commands of the Robocup Soccer Simulation 2D
 * interface. It contains the client-server communication functions.
 * 
 * @author Sebastien Lentz
 * 
 */
public class Sebbot
{

    private static final int MSG_SIZE = 4096; // Size of the socket buffer

    private DatagramSocket   socket;         // Socket to communicate with the server
    private InetAddress      host;           // Server address
    private int              port;           // Server port
    private String           teamName;       // Team name
    private String           strategy;       // Strategy used by the brain
    private Brain            brain;          // Decision module

    /**
     * @param host
     * @param port
     * @param teamName
     * @throws SocketException
     */
    public Sebbot(InetAddress host, int port, String teamName, String strategy)
            throws SocketException
    {
        this.socket = new DatagramSocket();
        this.host = host;
        this.port = port;
        this.teamName = teamName;
        this.strategy = strategy;
    }

    /**
     * This destructor closes the communication socket.
     */
    public void finalize()
    {
        send("(bye)");
        socket.close();
    }

    /*
     * =========================================================================
     * 
     *                          Socket communication
     * 
     * =========================================================================
     */
    /**
     * Send a message to the server.
     * 
     * @param message
     */
    private void send(String message)
    {
        //System.out.println("Sending: " + message);

        byte[] buffer = message.getBytes(Charset.defaultCharset());

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host,
                port);

        try
        {
            socket.send(packet);
        }
        catch (IOException e)
        {
            System.err.println("socket sending error " + e);
        }
    }

    /**
     * Wait for a new message from the server.
     */
    private String receive()
    {
        byte[] buffer = new byte[MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);
        try
        {
            socket.receive(packet);
        }
        catch (IOException e)
        {
            System.err.println("socket receiving error " + e);
        }
        return new String(buffer, Charset.defaultCharset());
    }

    /*
     * =========================================================================
     * 
     *                  Implementation of client commands
     * 
     * =========================================================================
     */
    /**
     * @param x
     * @param y
     */
    public void move(double x, double y)
    {
        send("(move " + Double.toString(x) + " " + Double.toString(y) + ")");
    }

    /**
     * @param moment
     */
    public void turn(double moment)
    {
        send("(turn " + Double.toString(moment) + ")");
    }

    /**
     * @param power
     */
    public void dash(double power)
    {
        send("(dash " + Double.toString(power) + ")");
    }

    /**
     * @param power
     * @param direction
     */
    public void kick(double power, double direction)
    {
        send("(kick " + Double.toString(power) + " "
                + Double.toString(direction) + ")");
    }

    /*
     * =========================================================================
     * 
     *                      Message parsing methods
     * 
     * =========================================================================
     */
    /**
     * @param message
     */
    private void parseFullstateInformation(String message)
    {
        // First check kind of information
        if (message.charAt(1) == 'f')
        {
            brain.getFullstateInfo().setFullstateMsg(message);
            brain.getFullstateInfo().parse();
        }

        else if (message.charAt(1) == 'e')
            System.out.println(message);

    }

    /**
     * Parse the server initialization message:
     * 
     * (init Side Unum PlayMode)
     * 
     * Side ::= l | r
     * Unum ::= 1 ~ 11
     * PlayMode ::= one of play modes
     * 
     * @param message
     * @throws IOException
     */
    protected void parseInitCommand(String message) throws IOException
    {
        final String initPattern = "\\(init ([lr]) ([1-9]{1,2}) ([a-zA-Z_]+)\\)";

        Pattern pattern = Pattern.compile(initPattern);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find())
        {
            brain = new Brain(this, matcher.group(1).charAt(0) == 'l' ? true
                    : false, Integer.valueOf(matcher.group(2)), strategy);
            brain.getFullstateInfo().setPlayMode(matcher.group(3));
            brain.start();
        }
        else
        {
            throw new IOException(message);
        }
    }

    /**
     * This is the entry point of the application.
     * Launch the soccer client using command line:
     * 
     * Sebbot [-parameter value]
     * 
     * Parameters:
     * 
     * host (default "localhost"):
     * The host name can either be a machine name, such as "java.sun.com"
     * or a string representing its IP address, such as "206.26.48.100."
     *
     * port (default 6000):
     * Port number for the communication with the server
     *
     * team (default Kris):
     * Team name. This name can not contain spaces.
     *
     * 
     * @param args
     * @throws SocketException
     * @throws IOException
     */
    public static void main(String args[]) throws SocketException, IOException
    {
        String hostname = "127.0.0.1";
        int port = 6000;
        String team = "Team1";
        String strategy = "Default";

        try
        {
            // First look for parameters
            for (int i = 0; i < args.length; i += 2)
            {
                if (args[i].compareTo("-host") == 0)
                {
                    hostname = args[i + 1];
                }
                else if (args[i].compareTo("-port") == 0)
                {
                    port = Integer.parseInt(args[i + 1]);
                }
                else if (args[i].compareTo("-team") == 0)
                {
                    team = args[i + 1];
                }
                else if (args[i].compareTo("-strategy") == 0)
                {
                    strategy = args[i + 1];
                }
                else
                {
                    throw new InvalidArgumentException(args[i]);
                }
            }
        }
        catch (InvalidArgumentException e)
        {
            System.err.println("");
            System.err.println("USAGE: Sebbot [-parameter value]");
            System.err.println("");
            System.err.println("    Parameters  value        default");
            System.err.println("   ------------------------------------");
            System.err.println("    host        host_name    localhost");
            System.err.println("    port        port_number  6000");
            System.err.println("    team        team_name    Team1");
            System.err.println("");
            System.err.println("    Example:");
            System.err
                    .println("      Sebbot -host www.host.com -port 6000 -team Team1");
            System.err.println("    or");
            System.err.println("      Sebbot -host 195.142.15.4");
            return;
        }

        Sebbot player = new Sebbot(InetAddress.getByName(hostname), port, team, strategy);

        // Enter main loop
        player.mainLoop();
    }

    // This is main loop for player
    protected void mainLoop() throws IOException
    {
        byte[] buffer = new byte[MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);

        // First we need to initialize the connection to the server
        send("(init " + teamName + " (version 14))");
        socket.receive(packet);
        parseInitCommand(new String(buffer, Charset.defaultCharset()));
        port = packet.getPort();
        
        /* 
         * This loop will just keep waiting for server messages
         * to arrive on the socket then parse them.
         */
        while (true)
        {
            parseFullstateInformation(receive());
        }

    }

}
