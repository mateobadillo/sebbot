package sebbot.learning;

/**
 * @author Sebastien Lentz
 *
 */
public interface Policy
{
    public Action chooseAction(State s);
}
