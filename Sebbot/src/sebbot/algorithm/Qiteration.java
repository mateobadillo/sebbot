package sebbot.algorithm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sebbot.Ball;
import sebbot.MathTools;
import sebbot.Player;
import sebbot.SoccerParams;

/**
 * @author Sebastien Lentz
 *
 */
public class Qiteration
{
    private static Qiteration       instance;

    private float[][][][][][][][][] ql;

    private int                     nbOfStepsForVelocityModulus;
    private int                     nbOfStepsForVelocityAngle;
    private int                     nbOfStepsForDistance;
    private int                     nbOfStepsForRelativeAngle;
    private int                     nbOfStepsForDash;
    private int                     nbOfStepsForTurn;

    /**
     * @param nbOfStepsForVelocityModulus
     * @param nbOfStepsForVelocityAngle
     * @param nbOfStepsForDistance
     * @param nbOfStepsForRelativeAngle
     * @param nbOfStepsForDash
     * @param nbOfStepsForTurn
     */
    protected Qiteration(int nbOfStepsForVelocityModulus,
            int nbOfStepsForVelocityAngle, int nbOfStepsForDistance,
            int nbOfStepsForRelativeAngle, int nbOfStepsForDash,
            int nbOfStepsForTurn)
    {
        this.nbOfStepsForVelocityModulus = nbOfStepsForVelocityModulus;
        this.nbOfStepsForVelocityAngle = nbOfStepsForVelocityAngle;
        this.nbOfStepsForDistance = nbOfStepsForDistance;
        this.nbOfStepsForRelativeAngle = nbOfStepsForRelativeAngle;
        this.nbOfStepsForDash = nbOfStepsForDash;
        this.nbOfStepsForTurn = nbOfStepsForTurn;

        //computeQl();
        ql = loadQl("backupQl.zip");
    }

    public static synchronized Qiteration instance(
            int nbOfStepsForVelocityModulus, int nbOfStepsForVelocityAngle,
            int nbOfStepsForDistance, int nbOfStepsForRelativeAngle,
            int nbOfStepsForDash, int nbOfStepsForTurn)
    {
        if (instance == null)
        {
            instance = new Qiteration(nbOfStepsForVelocityModulus,
                    nbOfStepsForVelocityAngle, nbOfStepsForDistance,
                    nbOfStepsForRelativeAngle, nbOfStepsForDash,
                    nbOfStepsForTurn);
        }

        return instance;
    }

    private float reward(float stateAction[])
    {
        float ballVelocityModulus = stateAction[0];
        float ballVelocityAngle = stateAction[1];
        float playerVelocityModulus = stateAction[2];
        float playerVelocityAngle = stateAction[3];
        float ballPlayerDistance = stateAction[4];
        float ballPlayerAngle = stateAction[5];
        float dashPower = stateAction[6];
        float turnAngle = stateAction[7];
        float dashOrTurn = stateAction[8];

        Player p = new Player(-ballPlayerDistance, 0, playerVelocityModulus
                * Math.cos(playerVelocityAngle), playerVelocityModulus
                * Math.sin(playerVelocityAngle), false, 0,
                (double) ballPlayerAngle);
        Ball b = new Ball(0.0d, 0.0d, ballVelocityModulus
                * Math.cos(ballVelocityAngle), ballVelocityModulus
                * Math.sin(ballVelocityAngle));

        if (ballPlayerDistance < SoccerParams.KICKABLE_MARGIN)
        { // Terminal state
            return 0.0f;
        }

        double nextStepDistance = p.nextPosition(
                dashPower * (1.0f - dashOrTurn)).distanceTo(b.nextPosition());

        float reward = (float) (1000.0d - nextStepDistance);

        if (nextStepDistance < SoccerParams.KICKABLE_MARGIN)
        {
            reward = 10000000;
        }

        return reward;

    }

    private float[] nextState(float[] stateAction)
    {
        float ballVelocityModulus = stateAction[0];
        float ballVelocityAngle = stateAction[1];
        float playerVelocityModulus = stateAction[2];
        float playerVelocityAngle = stateAction[3];
        float ballPlayerDistance = stateAction[4];
        float ballPlayerAngle = stateAction[5];
        float dashPower = stateAction[6];
        float turnAngle = stateAction[7];
        float dashOrTurn = stateAction[8];

        Player p = new Player(-ballPlayerDistance, 0, playerVelocityModulus
                * Math.cos(playerVelocityAngle), playerVelocityModulus
                * Math.sin(playerVelocityAngle), false, 0,
                (double) ballPlayerAngle);
        Ball b = new Ball(0.0d, 0.0d, ballVelocityModulus
                * Math.cos(ballVelocityAngle), ballVelocityModulus
                * Math.sin(ballVelocityAngle));

        float[] nextState = new float[6];

        nextState[0] = ballVelocityModulus * SoccerParams.BALL_DECAY;
        nextState[1] = ballVelocityAngle;
        nextState[2] = (playerVelocityModulus + dashPower * (1.0f - dashOrTurn))
                * SoccerParams.PLAYER_DECAY;
        nextState[3] = playerVelocityAngle;

        p.setBodyDirection(MathTools.normalizeAngle(p.angleFromBody(b)
                + turnAngle * dashOrTurn));
        p.setPosition(p.nextPosition(dashPower * (1.0f - dashOrTurn)));
        b.setPosition(b.nextPosition());

        nextState[4] = (float) p.distanceTo(b);
        nextState[5] = (float) p.angleFromBody(b);

        return nextState;
    }

    private int valueToIndex(double value, double maxValue,
            int nbOfDiscreteSteps)
    {
        int index = (int) Math.round(value / maxValue * nbOfDiscreteSteps);

        return index >= nbOfDiscreteSteps ? nbOfDiscreteSteps - 1 : index;
    }

    private float indexToValue(int index, double maxValue, int nbOfDiscreteSteps)
    {
        return (float) ((maxValue * index / nbOfDiscreteSteps) + maxValue
                / nbOfDiscreteSteps / 2);
    }

    private int angleValueToIndex(double value)
    {
        return valueToIndex(value + 180.0d, 360.0d, nbOfStepsForVelocityAngle);
    }

    private float angleIndexToValue(int index)
    {
        return indexToValue(index, 360.0d, nbOfStepsForVelocityAngle) - 180.0f;
    }

    private int turnAngleValueToIndex(double value)
    {
        return valueToIndex(value + 180.0d, 360.0d, nbOfStepsForTurn);
    }

    private float turnAngleIndexToValue(int index)
    {
        return indexToValue(index, 360.0d, nbOfStepsForTurn) - 180.0f;
    }

    private int relAngleValueToIndex(double value)
    {
        return valueToIndex(value + 180.0d, 360.0d, nbOfStepsForRelativeAngle);
    }

    private float relAngleIndexToValue(int index)
    {
        return indexToValue(index, 360.0d, nbOfStepsForRelativeAngle) - 180.0f;
    }

    public void computeQl()
    {
        System.out.println("q iteration starting...");

        float[][][][][][][][][] q0 = new float[nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForDistance][nbOfStepsForRelativeAngle][nbOfStepsForDash][nbOfStepsForTurn][2];
        float[][][][][][][][][] q1 = new float[nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForDistance][nbOfStepsForRelativeAngle][nbOfStepsForDash][nbOfStepsForTurn][2];
        float tmp;
        float[] currentStateAction = new float[9];

        for (int i = 0; i < q0.length; i++)
        {
            for (int j = 0; j < q0[i].length; j++)
            {
                for (int k = 0; k < q0[i][j].length; k++)
                {
                    for (int l = 0; l < q0[i][j][k].length; l++)
                    {
                        for (int m = 0; m < q0[i][j][k][l].length; m++)
                        {
                            for (int n = 0; n < q0[i][j][k][l][m].length; n++)
                            {
                                for (int o = 0; o < q0[i][j][k][l][m][n].length; o++)
                                {
                                    for (int p = 0; p < q0[i][j][k][l][m][n][o].length; p++)
                                    {
                                        for (int q = 0; q < q0[i][j][k][l][m][n][o][p].length; q++)
                                        {
                                            q0[i][j][k][l][m][n][o][p][q] = 0.0f;
                                            q1[i][j][k][l][m][n][o][p][q] = 0.0f;
                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

        int nbOfIterations = 0;
        do
        {
            nbOfIterations++;
            for (int i = 0; i < q0.length; i++)
            {
                for (int j = 0; j < q0[i].length; j++)
                {
                    for (int k = 0; k < q0[i][j].length; k++)
                    {
                        for (int l = 0; l < q0[i][j][k].length; l++)
                        {
                            for (int m = 0; m < q0[i][j][k][l].length; m++)
                            {
                                for (int n = 0; n < q0[i][j][k][l][m].length; n++)
                                {
                                    for (int o = 0; o < q0[i][j][k][l][m][n].length; o++)
                                    {
                                        for (int p = 0; p < q0[i][j][k][l][m][n][o].length; p++)
                                        {
                                            for (int q = 0; q < q0[i][j][k][l][m][n][o][p].length; q++)
                                            {
                                                currentStateAction[0] = indexToValue(
                                                        i,
                                                        SoccerParams.BALL_SPEED_MAX,
                                                        nbOfStepsForVelocityModulus);

                                                currentStateAction[1] = angleIndexToValue(j);

                                                currentStateAction[2] = indexToValue(
                                                        k,
                                                        SoccerParams.PLAYER_SPEED_MAX,
                                                        nbOfStepsForVelocityModulus);

                                                currentStateAction[3] = angleIndexToValue(l);

                                                currentStateAction[4] = indexToValue(
                                                        m, 125.0d,
                                                        nbOfStepsForDistance);

                                                currentStateAction[5] = relAngleIndexToValue(n);

                                                currentStateAction[6] = indexToValue(
                                                        o, 100.0d,
                                                        nbOfStepsForDash);

                                                currentStateAction[7] = turnAngleIndexToValue(p);

                                                currentStateAction[8] = (float) q;

                                                tmp = q1[i][j][k][l][m][n][o][p][q];

                                                q1[i][j][k][l][m][n][o][p][q] = reward(currentStateAction)
                                                        + 0.95f
                                                        * maxUq(
                                                                nextState(currentStateAction),
                                                                q0);

                                                q0[i][j][k][l][m][n][o][p][q] = tmp;
                                            }

                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

            System.out.println(nbOfIterations + " iterations done.");
        }
        while (!q0EqualsQ1(q0, q1) && nbOfIterations < 500);

        System.out.println("nb of iterations: " + nbOfIterations);
        System.out.println("q iteration table computed.");

        ql = q1;
        
        saveQl("backupQl.zip", ql);
    }

    private float maxUq(float[] state, float[][][][][][][][][] q0)
    {
        int s0 = valueToIndex(state[0], SoccerParams.BALL_SPEED_MAX,
                nbOfStepsForVelocityModulus);

        int s1 = angleValueToIndex(state[1]);

        int s2 = valueToIndex(state[2], SoccerParams.PLAYER_SPEED_MAX,
                nbOfStepsForVelocityModulus);

        int s3 = angleValueToIndex(state[3]);

        int s4 = valueToIndex(state[4], 125, nbOfStepsForDistance);

        int s5 = relAngleValueToIndex(state[5]);

        //        System.out.println("s0: " + s0);
        //        System.out.println("s1: " + s1);
        //        System.out.println("s2: " + s2);
        //        System.out.println("s3: " + s3);
        //        System.out.println("s4: " + s4);
        //        System.out.println("s5: " + s5);

        float max = -1.0f;
        for (int i = 0; i < q0[s0][s1][s2][s3][s4][s5].length; i++)
        {
            for (int j = 0; j < q0[s0][s1][s2][s3][s4][s5][i].length; j++)
            {
                for (int k = 0; k < q0[s0][s1][s2][s3][s4][s5][i][j].length; k++)
                {
                    if (q0[s0][s1][s2][s3][s4][s5][i][j][k] > max)
                    {
                        max = q0[s0][s1][s2][s3][s4][s5][i][j][k];
                    }
                }
            }

        }

        return max;
    }

    private float[] argMaxUq(float[] state, float[][][][][][][][][] q0)
    {
        int s0 = valueToIndex(state[0], SoccerParams.BALL_SPEED_MAX,
                nbOfStepsForVelocityModulus);

        int s1 = angleValueToIndex(state[1]);

        int s2 = valueToIndex(state[2], SoccerParams.PLAYER_SPEED_MAX,
                nbOfStepsForVelocityModulus);

        int s3 = angleValueToIndex(state[3]);

        int s4 = valueToIndex(state[4], 125, nbOfStepsForDistance);

        int s5 = relAngleValueToIndex(state[5]);

        float max = -1.0f;
        float[] argMaxUq = new float[3];
        argMaxUq[0] = -1.0f;
        argMaxUq[1] = -1.0f;
        argMaxUq[2] = -1.0f;
        for (int i = 0; i < q0[s0][s1][s2][s3][s4][s5].length; i++)
        {
            for (int j = 0; j < q0[s0][s1][s2][s3][s4][s5][i].length; j++)
            {
                for (int k = 0; k < q0[s0][s1][s2][s3][s4][s5][i][j].length; k++)
                {
                    if (q0[s0][s1][s2][s3][s4][s5][i][j][k] > max)
                    {
                        max = q0[s0][s1][s2][s3][s4][s5][i][j][k];
                        argMaxUq[0] = indexToValue(i, 100.0d, nbOfStepsForDash);
                        argMaxUq[1] = turnAngleIndexToValue(j);
                        argMaxUq[2] = k;
                    }
                }
            }

        }

        return argMaxUq;
    }

    public float[] getAction(float[] state)
    {
        return argMaxUq(state, ql);
    }

    private boolean q0EqualsQ1(float[][][][][][][][][] q0,
            float[][][][][][][][][] q1)
    {
        for (int i = 0; i < q0.length; i++)
        {
            for (int j = 0; j < q0[i].length; j++)
            {
                for (int k = 0; k < q0[i][j].length; k++)
                {
                    for (int l = 0; l < q0[i][j][k].length; l++)
                    {
                        for (int m = 0; m < q0[i][j][k][l].length; m++)
                        {
                            for (int n = 0; n < q0[i][j][k][l][m].length; n++)
                            {
                                for (int o = 0; o < q0[i][j][k][l][m][n].length; o++)
                                {
                                    for (int p = 0; p < q0[i][j][k][l][m][n][o].length; p++)
                                    {
                                        for (int q = 0; q < q0[i][j][k][l][m][n][o][p].length; q++)
                                        {
                                            if (q0[i][j][k][l][m][n][o][p][q] != q1[i][j][k][l][m][n][o][p][q])
                                            {
                                                return false;
                                            }
                                        }
                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

        return true;

    }

    public void saveQl(String filename, float[][][][][][][][][] q)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            ObjectOutputStream out = new ObjectOutputStream(gzos);
            out.writeObject(q);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public float[][][][][][][][][] loadQl(String filename)
    {
        float[][][][][][][][][] q = null;
        
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(gzis);
            q = (float[][][][][][][][][]) in.readObject();
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return q;
    }

    public void printQl()
    {
        for (int i = 0; i < ql.length; i++)
        {
            for (int j = 0; j < ql[i].length; j++)
            {
                for (int k = 0; k < ql[i][j].length; k++)
                {
                    for (int l = 0; l < ql[i][j][k].length; l++)
                    {
                        for (int m = 0; m < ql[i][j][k][l].length; m++)
                        {
                            for (int n = 0; n < ql[i][j][k][l][m].length; n++)
                            {
                                for (int o = 0; o < ql[i][j][k][l][m][n].length; o++)
                                {
                                    for (int p = 0; p < ql[i][j][k][l][m][n][o].length; p++)
                                    {
                                        for (int q = 0; q < ql[i][j][k][l][m][n][o][p].length; q++)
                                        {
                                            System.out
                                                    .print(" "
                                                            + ql[i][j][k][l][m][n][o][p][q]);
                                        }
                                    }

                                }

                            }

                        }

                    }

                }

            }

        }
    }

}
