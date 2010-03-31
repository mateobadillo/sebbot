package sebbot.learning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sebbot.Ball;
import sebbot.MathTools;
import sebbot.Player;
import sebbot.SoccerParams;
import sebbot.Vector2D;

/**
 * @author Sebastien Lentz
 *
 */
public class CopyOfQiteration
{
    private static CopyOfQiteration         instance;

    private float[][][][][][][][][][] ql;

    private int                       nbOfStepsForVelocityModulus;
    private int                       nbOfStepsForVelocityAngle;
    private int                       nbOfStepsForDistance;
    private int                       nbOfStepsForRelativeAngle;
    private int                       nbOfStepsForDash;
    private int                       nbOfStepsForTurn;

    /**
     * @param nbOfStepsForVelocityModulus
     * @param nbOfStepsForVelocityAngle
     * @param nbOfStepsForDistance
     * @param nbOfStepsForRelativeAngle
     * @param nbOfStepsForDash
     * @param nbOfStepsForTurn
     */
    protected CopyOfQiteration(int nbOfStepsForVelocityModulus,
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

    public static synchronized CopyOfQiteration instance(
            int nbOfStepsForVelocityModulus, int nbOfStepsForVelocityAngle,
            int nbOfStepsForDistance, int nbOfStepsForRelativeAngle,
            int nbOfStepsForDash, int nbOfStepsForTurn)
    {
        if (instance == null)
        {
            instance = new CopyOfQiteration(nbOfStepsForVelocityModulus,
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
        float bodyOrientation = stateAction[6];
        float dashPower = stateAction[7];
        float turnAngle = stateAction[8];
        float dashOrTurn = stateAction[9];

        boolean isTerminalState = true;
        for (int i = 0; i < 6; i++)
        {
            if (stateAction[i] != -1.0f)
            {
                isTerminalState = false;
            }
        }

        if (isTerminalState)
        { // Terminal state
            return 0.0f;
        }

        float nextStepDistance = nextState(stateAction)[4];

        float reward = 1000.0f - nextStepDistance;

        if (nextStepDistance < SoccerParams.KICKABLE_MARGIN)
        {
            reward = 100000000;
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
        float bodyOrientation = stateAction[6];
        float dashPower = stateAction[7];
        float turnAngle = stateAction[8];
        float dashOrTurn = stateAction[9];

        float[] nextState = new float[7];

        boolean isTerminalState = true;
        for (int i = 0; i < 6; i++)
        {
            if (stateAction[i] != -1.0f)
            {
                isTerminalState = false;
            }
        }

        if (ballPlayerDistance < SoccerParams.KICKABLE_MARGIN
                || isTerminalState)
        {
            for (int i = 0; i < nextState.length; i++)
            {
                nextState[i] = -1.0f;
            }
        }
        else
        {
            Vector2D ballVelocity = new Vector2D(stateAction[0], stateAction[1], true);
            
            nextState[0] = ballVelocityModulus * SoccerParams.BALL_DECAY;
            nextState[1] = ballVelocityAngle;

            Vector2D oldPlayerSpeed = new Vector2D(playerVelocityModulus,
                    playerVelocityAngle, true);

            Vector2D playerAcceleration = (new Vector2D(dashPower
                    * SoccerParams.DASH_POWER_RATE * (1.0f - dashOrTurn),
                    bodyOrientation, true))
                    .normalize(SoccerParams.PLAYER_ACCEL_MAX);

            Vector2D newPlayerSpeed = oldPlayerSpeed.add(playerAcceleration)
                    .normalize(SoccerParams.PLAYER_SPEED_MAX);

            nextState[2] = (float) newPlayerSpeed.polarRadius()
                    * SoccerParams.PLAYER_DECAY;
            nextState[3] = (float) newPlayerSpeed.polarAngle();

            Vector2D oldRelPosition = new Vector2D(ballPlayerDistance,
                    MathTools.normalizeAngle(ballPlayerAngle+bodyOrientation), true);
            Vector2D newRelPosition = oldRelPosition.add(ballVelocity).subtract(newPlayerSpeed);

            nextState[6] = (float) MathTools.normalizeAngle(bodyOrientation
                    + turnAngle * dashOrTurn);
            
            nextState[4] = (float) newRelPosition.polarRadius();
            nextState[5] = (float) MathTools.normalizeAngle(newRelPosition.polarAngle() - nextState[6]);
        }

        //        else
        //        {
        //            nextState[0] = ballVelocityModulus * SoccerParams.BALL_DECAY;
        //            nextState[1] = ballVelocityAngle;
        //            nextState[2] = (playerVelocityModulus + dashPower
        //                    * (1.0f - dashOrTurn))
        //                    * SoccerParams.PLAYER_DECAY;
        //            nextState[3] = playerVelocityAngle;
        //
        //            p.setBodyDirection(MathTools.normalizeAngle(p.angleFromBody(b)
        //                    + turnAngle * dashOrTurn));
        //            p.setPosition(p.nextPosition(dashPower * (1.0f - dashOrTurn)));
        //            b.setPosition(b.nextPosition());
        //
        //            nextState[4] = (float) p.distanceTo(b);
        //            nextState[5] = (float) p.angleFromBody(b);
        //        }

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
        int stateSpaceSize = nbOfStepsForDistance * nbOfStepsForVelocityAngle
                * nbOfStepsForVelocityAngle * nbOfStepsForVelocityModulus
                * nbOfStepsForVelocityModulus * nbOfStepsForRelativeAngle
                * nbOfStepsForRelativeAngle;
        int actionSpaceSize = nbOfStepsForTurn * nbOfStepsForDash * 2;
        
        System.out.println("X x U² = " + (stateSpaceSize * actionSpaceSize * actionSpaceSize));

        float[][][][][][][][][][] q0 = new float[nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForDistance][nbOfStepsForRelativeAngle][nbOfStepsForRelativeAngle][nbOfStepsForDash][nbOfStepsForTurn][2];
        float[][][][][][][][][][] q1 = new float[nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForDistance][nbOfStepsForRelativeAngle][nbOfStepsForRelativeAngle][nbOfStepsForDash][nbOfStepsForTurn][2];
        float tmp;
        float[] currentStateAction = new float[10];

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
                                            for (int r = 0; r < q0[i][j][k][l][m][n][o][p][q].length; r++)
                                            {
                                                q0[i][j][k][l][m][n][o][p][q][r] = 0.0f;
                                                q1[i][j][k][l][m][n][o][p][q][r] = 0.0f;
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
                                                for (int r = 0; r < q0[i][j][k][l][m][n][o][p][q].length; r++)
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

                                                    currentStateAction[6] = relAngleIndexToValue(o);

                                                    currentStateAction[7] = indexToValue(
                                                            o, 100.0d,
                                                            nbOfStepsForDash);

                                                    currentStateAction[8] = turnAngleIndexToValue(q);

                                                    currentStateAction[9] = (float) r;

                                                    tmp = q1[i][j][k][l][m][n][o][p][q][r];

                                                    q1[i][j][k][l][m][n][o][p][q][r] = reward(currentStateAction)
                                                            + 0.85f
                                                            * maxUq(
                                                                    nextState(currentStateAction),
                                                                    q0);

                                                    q0[i][j][k][l][m][n][o][p][q][r] = tmp;
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

            System.out.println(nbOfIterations + " iterations done.");
        }
        while (!q0EqualsQ1(q0, q1) && nbOfIterations < 500);

        System.out.println("nb of iterations: " + nbOfIterations);
        System.out.println("q iteration table computed.");

        ql = q1;

        saveQl("backupQl.zip", ql);
    }

    private float maxUq(float[] state, float[][][][][][][][][][] q0)
    {
        int s0 = valueToIndex(state[0], SoccerParams.BALL_SPEED_MAX,
                nbOfStepsForVelocityModulus);

        int s1 = angleValueToIndex(state[1]);

        int s2 = valueToIndex(state[2], SoccerParams.PLAYER_SPEED_MAX,
                nbOfStepsForVelocityModulus);

        int s3 = angleValueToIndex(state[3]);

        int s4 = valueToIndex(state[4], 125, nbOfStepsForDistance);

        int s5 = relAngleValueToIndex(state[5]);

        int s6 = relAngleValueToIndex(state[6]);

        //        System.out.println("s0: " + s0);
        //        System.out.println("s1: " + s1);
        //        System.out.println("s2: " + s2);
        //        System.out.println("s3: " + s3);
        //        System.out.println("s4: " + s4);
        //        System.out.println("s5: " + s5);

        float max = -1.0f;
        for (int i = 0; i < q0[s0][s1][s2][s3][s4][s5][s6].length; i++)
        {
            for (int j = 0; j < q0[s0][s1][s2][s3][s4][s5][s6][i].length; j++)
            {
                for (int k = 0; k < q0[s0][s1][s2][s3][s4][s5][s6][i][j].length; k++)
                {
                    if (q0[s0][s1][s2][s3][s4][s5][s6][i][j][k] > max)
                    {
                        max = q0[s0][s1][s2][s3][s4][s5][s6][i][j][k];
                    }
                }
            }

        }

        return max;
    }

    private float[] argMaxUq(float[] state, float[][][][][][][][][][] q0)
    {
        int s0 = valueToIndex(state[0], SoccerParams.BALL_SPEED_MAX,
                nbOfStepsForVelocityModulus);

        int s1 = angleValueToIndex(state[1]);

        int s2 = valueToIndex(state[2], SoccerParams.PLAYER_SPEED_MAX,
                nbOfStepsForVelocityModulus);

        int s3 = angleValueToIndex(state[3]);

        int s4 = valueToIndex(state[4], 125, nbOfStepsForDistance);

        int s5 = relAngleValueToIndex(state[5]);

        int s6 = relAngleValueToIndex(state[6]);

        float max = -1.0f;
        float[] argMaxUq = new float[3];
        argMaxUq[0] = -1.0f;
        argMaxUq[1] = -1.0f;
        argMaxUq[2] = -1.0f;
        for (int i = 0; i < q0[s0][s1][s2][s3][s4][s5][s6].length; i++)
        {
            for (int j = 0; j < q0[s0][s1][s2][s3][s4][s5][s6][i].length; j++)
            {
                for (int k = 0; k < q0[s0][s1][s2][s3][s4][s5][s6][i][j].length; k++)
                {
                    if (q0[s0][s1][s2][s3][s4][s5][s6][i][j][k] > max)
                    {
                        max = q0[s0][s1][s2][s3][s4][s5][s6][i][j][k];
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

    private boolean q0EqualsQ1(float[][][][][][][][][][] q0,
            float[][][][][][][][][][] q1)
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
                                            for (int r = 0; r < q0[i][j][k][l][m][n][o][p][q].length; r++)
                                            {
                                                if (Math
                                                        .abs(q0[i][j][k][l][m][n][o][p][q][r]
                                                                - q1[i][j][k][l][m][n][o][p][q][r]) > 0.001)
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

        }

        return true;

    }

    public void saveQl(String filename, float[][][][][][][][][][] q)
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

    public float[][][][][][][][][][] loadQl(String filename)
    {
        float[][][][][][][][][][] q = null;

        try
        {
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(gzis);
            q = (float[][][][][][][][][][]) in.readObject();
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
                                            for (int r = 0; r < ql[i][j][k][l][m][n][o][p][q].length; r++)
                                            {
                                                System.out
                                                        .print(" "
                                                                + ql[i][j][k][l][m][n][o][p][q][r]);
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

}
