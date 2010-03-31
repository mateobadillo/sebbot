package sebbot.learning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sebbot.MathTools;
import sebbot.SoccerParams;
import sebbot.Vector2D;

/**
 * @author Sebastien Lentz
 *
 */
public class Qiteration
{
    private static Qiteration     instance;

    private float[][][][][][][][] qTable;

    private int                   nbOfStepsForVelocityModulus;
    private int                   nbOfStepsForVelocityAngle;
    private int                   nbOfStepsForDistance;
    private int                   nbOfStepsForRelativeAngle;
    private int                   nbOfStepsForDash;
    private int                   nbOfStepsForTurn;

    /**
     * @param nbOfStepsForVelocityModulus
     * @param nbOfStepsForVelocityAngle
     * @param nbOfStepsForDistance
     * @param nbOfStepsForRelativeAngle
     * @param nbOfStepsForDash
     * @param nbOfStepsForTurn
     */
    protected Qiteration(int nbOfStepsForVelocityModulus,
                         int nbOfStepsForVelocityAngle,
                         int nbOfStepsForDistance,
                         int nbOfStepsForRelativeAngle, int nbOfStepsForDash,
                         int nbOfStepsForTurn)
    {
        this.nbOfStepsForVelocityModulus = nbOfStepsForVelocityModulus;
        this.nbOfStepsForVelocityAngle = nbOfStepsForVelocityAngle;
        this.nbOfStepsForDistance = nbOfStepsForDistance;
        this.nbOfStepsForRelativeAngle = nbOfStepsForRelativeAngle;
        this.nbOfStepsForDash = nbOfStepsForDash;
        this.nbOfStepsForTurn = nbOfStepsForTurn;

        computeQl();
        //qTable = loadQl("backupQl.zip");
    }

    public static synchronized Qiteration instance(
                                                   int nbOfStepsForVelocityModulus,
                                                   int nbOfStepsForVelocityAngle,
                                                   int nbOfStepsForDistance,
                                                   int nbOfStepsForRelativeAngle,
                                                   int nbOfStepsForDash,
                                                   int nbOfStepsForTurn)
    {
        if (instance == null)
        {
            instance = new Qiteration(nbOfStepsForVelocityModulus,
                nbOfStepsForVelocityAngle, nbOfStepsForDistance,
                nbOfStepsForRelativeAngle, nbOfStepsForDash, nbOfStepsForTurn);
        }

        return instance;
    }

    private float reward(State s, Action a)
    {
        float reward;
        State nextState = s.nextState(a).discretize(
            nbOfStepsForVelocityModulus, nbOfStepsForVelocityAngle,
            nbOfStepsForVelocityModulus, nbOfStepsForVelocityAngle,
            nbOfStepsForRelativeAngle, nbOfStepsForDistance,
            nbOfStepsForRelativeAngle);

        if (nextState.isTerminal())
        {
            reward = 0.0f;
        }
        else
        {
            float nextStepDistance = nextState.getRelativeDistance();

            if (nextStepDistance < SoccerParams.KICKABLE_MARGIN)
            {
                reward = 10000.0f;
            }
            else
            {
                reward = 0.0f - nextStepDistance;
            }
        }

        return reward;
    }

    private float turnValue(float action)
    {
        float tv = 0.0f;

        if (action < nbOfStepsForTurn)
        {
            tv = ((action + 1.0f) / nbOfStepsForTurn * 360.0f) - 180.0f;
        }

        return tv;
    }

    private float dashValue(float action)
    {
        float dv = 0.0f;

        if (action >= nbOfStepsForTurn)
        {
            dv = (action - nbOfStepsForTurn + 1.0f) / nbOfStepsForDash * 100.0f;
        }

        return dv;
    }

    private int valueToIndex(double value, double minValue, double maxValue,
                             int nbOfDiscreteSteps)
    {
        int index = (int) Math.rint((value - minValue) / (maxValue - minValue)
                * nbOfDiscreteSteps);

        return index >= nbOfDiscreteSteps ? nbOfDiscreteSteps - 1 : index;
    }

    public void computeQl()
    {
        System.out.println("q iteration starting...");
        int stateSpaceSize = nbOfStepsForDistance * nbOfStepsForVelocityAngle
                * nbOfStepsForVelocityAngle * nbOfStepsForVelocityModulus
                * nbOfStepsForVelocityModulus * nbOfStepsForRelativeAngle
                * nbOfStepsForRelativeAngle;
        int actionSpaceSize = (nbOfStepsForTurn + nbOfStepsForDash);

        System.out.println("X x U² = "
                + (stateSpaceSize * actionSpaceSize * actionSpaceSize));

        float[][][][][][][][] oldQtable = new float[nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForRelativeAngle][nbOfStepsForDistance][nbOfStepsForRelativeAngle][nbOfStepsForDash
                + nbOfStepsForTurn];

        qTable = new float[nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForVelocityModulus][nbOfStepsForVelocityAngle][nbOfStepsForRelativeAngle][nbOfStepsForDistance][nbOfStepsForRelativeAngle][nbOfStepsForDash
                + nbOfStepsForTurn];

        float tmp;

        for (int i = 0; i < oldQtable.length; i++)
        {
            for (int j = 0; j < oldQtable[i].length; j++)
            {
                for (int k = 0; k < oldQtable[i][j].length; k++)
                {
                    for (int l = 0; l < oldQtable[i][j][k].length; l++)
                    {
                        for (int m = 0; m < oldQtable[i][j][k][l].length; m++)
                        {
                            for (int n = 0; n < oldQtable[i][j][k][l][m].length; n++)
                            {
                                for (int o = 0; o < oldQtable[i][j][k][l][m][n].length; o++)
                                {
                                    for (int p = 0; p < oldQtable[i][j][k][l][m][n][o].length; p++)
                                    {
                                        oldQtable[i][j][k][l][m][n][o][p] = 0.0f;
                                        qTable[i][j][k][l][m][n][o][p] = 0.0f;
                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

        int nbOfIterations = 0;
        State s = new State(false);
        Action a = new Action(0, false);
        do
        {
            nbOfIterations++;
            for (int bvn = 0; bvn < oldQtable.length; bvn++)
            {
                for (int bvd = 0; bvd < oldQtable[bvn].length; bvd++)
                {
                    for (int pvn = 0; pvn < oldQtable[bvn][bvd].length; pvn++)
                    {
                        for (int pvd = 0; pvd < oldQtable[bvn][bvd][pvn].length; pvd++)
                        {
                            for (int pbd = 0; pbd < oldQtable[bvn][bvd][pvn][pvd].length; pbd++)
                            {
                                for (int rdist = 0; rdist < oldQtable[bvn][bvd][pvn][pvd][pbd].length; rdist++)
                                {
                                    for (int rdir = 0; rdir < oldQtable[bvn][bvd][pvn][pvd][pbd][rdist].length; rdir++)
                                    {
                                        for (int act = 0; act < oldQtable[bvn][bvd][pvn][pvd][pbd][rdist][rdir].length; act++)
                                        {
                                            s
                                                .setBallVelocityNorm((float) (SoccerParams.BALL_SPEED_MAX
                                                        / nbOfStepsForVelocityModulus * (bvn)));
                                            s
                                                .setBallVelocityDirection((float) (360.0f
                                                        / nbOfStepsForVelocityAngle
                                                        * (bvd) - 180f));
                                            s
                                                .setPlayerVelocityNorm((float) (SoccerParams.PLAYER_SPEED_MAX
                                                        / nbOfStepsForVelocityModulus * (pvn)));
                                            s
                                                .setPlayerVelocityDirection((float) (360.0f
                                                        / nbOfStepsForVelocityAngle
                                                        * (pvd) - 180.0f));
                                            s
                                                .setPlayerBodyDirection((float) (360.0f / nbOfStepsForRelativeAngle * (pbd)) - 180.0f);
                                            s
                                                .setRelativeDistance((float) (125.0f / nbOfStepsForDistance * (rdist)));
                                            s
                                                .setRelativeDirection((float) (360.0f
                                                        / nbOfStepsForRelativeAngle
                                                        * (rdir) - 180.0f));

                                            if (act >= nbOfStepsForTurn)
                                            {
                                                a.setTurn(false);
                                                a
                                                    .setValue(100.0f
                                                            / nbOfStepsForDash
                                                            * (1.0f + (act - nbOfStepsForTurn)));
                                            }
                                            else
                                            {
                                                a.setTurn(true);
                                                a
                                                    .setValue(360.0f
                                                            / nbOfStepsForTurn
                                                            * (0.5f + act)
                                                            - 180.0f);
                                            }

                                            tmp = qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act];

                                            qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act] = reward(
                                                s, a)
                                                    + 0.85f
                                                    * maxUq(s.nextState(a),
                                                        oldQtable);

//                                            if (qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act] > 10000.0f)
//                                            {
//                                                System.out
//                                                    .println("prob: "
//                                                            + qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act]
//                                                            + " " + s + " " + a);
//                                            }

                                            oldQtable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act] = tmp;

                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

            //            for (int i = 0; i < 10; i++)
            //            {
            //                int i0, i1, i2, i3, i4, i5, i6, i7;
            //                i0 = (int) (Math.ceil(Math.random()
            //                        * (nbOfStepsForVelocityModulus - 1)));
            //                i1 = (int) (Math.ceil(Math.random()
            //                        * (nbOfStepsForVelocityAngle - 1)));
            //                i2 = (int) (Math.ceil(Math.random()
            //                        * (nbOfStepsForVelocityModulus - 1)));
            //                i3 = (int) Math.ceil(Math.random()
            //                        * (nbOfStepsForVelocityAngle - 1));
            //                i4 = (int) (Math.ceil(Math.random()
            //                        * (nbOfStepsForRelativeAngle - 1)));
            //                i5 = (int) (Math.ceil(Math.random()
            //                        * (nbOfStepsForDistance - 1)));
            //                i6 = (int) Math.ceil(Math.random()
            //                        * (nbOfStepsForRelativeAngle - 1));
            //                i7 = (int) (Math.ceil(Math.random()
            //                        * ((nbOfStepsForDash + nbOfStepsForTurn - 1))));
            //
            //                System.out.println(i0 + " " + i1 + " " + i2 + " " + i3 + " "
            //                        + i4 + " " + i5 + " " + i6 + " " + i7 + " ");
            //                System.out.println(qTable[i0][i1][i2][i3][i4][i5][i6][i7]);
            //            }

            System.out.println(nbOfIterations + " iterations done.");
        }
        while (!q0EqualsQ1(oldQtable, qTable) && nbOfIterations < 300);

        System.out.println("nb of iterations: " + nbOfIterations);
        System.out.println("q iteration table computed.");

        saveQl("backupQl.zip", qTable);
    }

    public float qFunction(State s, Action a)
    {
        int i0, i1, i2, i3, i4, i5, i6, i7;
        i0 = valueToIndex(s.getBallVelocityNorm(), 0.0f,
            SoccerParams.BALL_SPEED_MAX, nbOfStepsForVelocityModulus);
        i1 = valueToIndex(s.getBallVelocityDirection(), -180.0f, 180.0f,
            nbOfStepsForVelocityAngle);
        i2 = valueToIndex(s.getPlayerVelocityNorm(), 0.0f,
            SoccerParams.PLAYER_SPEED_MAX, nbOfStepsForVelocityModulus);
        i3 = valueToIndex(s.getPlayerVelocityDirection(), -180.0f, 180.0f,
            nbOfStepsForVelocityAngle);
        i4 = valueToIndex(s.getPlayerBodyDirection(), -180.0f, 180.0f,
            nbOfStepsForRelativeAngle);
        i5 = valueToIndex(s.getRelativeDistance(), 0.0f, 125.0f,
            nbOfStepsForDistance);
        i6 = valueToIndex(s.getRelativeDirection(), -180.0f, 180.0f,
            nbOfStepsForRelativeAngle);

        if (a.isTurn())
        {
            i7 = valueToIndex(a.getValue(), -180.0f, 180.0f, nbOfStepsForTurn);
        }
        else
        {
            i7 = nbOfStepsForTurn
                    + valueToIndex(a.getValue(), 0.0f, 100.0f, nbOfStepsForDash);
        }

        return qTable[i0][i1][i2][i3][i4][i5][i6][i7];
    }

    private float maxUq(State s, float[][][][][][][][] q0)
    {
        int s0, s1, s2, s3, s4, s5, s6;
        s0 = valueToIndex(s.getBallVelocityNorm(), 0.0f,
            SoccerParams.BALL_SPEED_MAX, nbOfStepsForVelocityModulus);
        s1 = valueToIndex(s.getBallVelocityDirection(), -180.0f, 180.0f,
            nbOfStepsForVelocityAngle);
        s2 = valueToIndex(s.getPlayerVelocityNorm(), 0.0f,
            SoccerParams.PLAYER_SPEED_MAX, nbOfStepsForVelocityModulus);
        s3 = valueToIndex(s.getPlayerVelocityDirection(), -180.0f, 180.0f,
            nbOfStepsForVelocityAngle);
        s4 = valueToIndex(s.getPlayerBodyDirection(), -180.0f, 180.0f,
            nbOfStepsForRelativeAngle);
        s5 = valueToIndex(s.getRelativeDistance(), 0.0f, 125.0f,
            nbOfStepsForDistance);
        s6 = valueToIndex(s.getRelativeDirection(), -180.0f, 180.0f,
            nbOfStepsForRelativeAngle);

        //                System.out.println("s0: " + s0);
        //                System.out.println("s1: " + s1);
        //                System.out.println("s2: " + s2);
        //                System.out.println("s3: " + s3);
        //                System.out.println("s4: " + s4);
        //                System.out.println("s5: " + s5);
        //                System.out.println("s6: " + s6);

        float max = -1000000.0f;
        for (int i = 0; i < q0[s0][s1][s2][s3][s4][s5][s6].length; i++)
        {
            if (q0[s0][s1][s2][s3][s4][s5][s6][i] > max)
            {
                max = q0[s0][s1][s2][s3][s4][s5][s6][i];
            }

        }

        return max;
    }

    private float[] argMaxUq(State s, float[][][][][][][][] q0)
    {
        int s0, s1, s2, s3, s4, s5, s6;
        s0 = valueToIndex(s.getBallVelocityNorm(), 0.0f,
            SoccerParams.BALL_SPEED_MAX, nbOfStepsForVelocityModulus);
        s1 = valueToIndex(s.getBallVelocityDirection(), -180.0f, 180.0f,
            nbOfStepsForVelocityAngle);
        s2 = valueToIndex(s.getPlayerVelocityNorm(), 0.0f,
            SoccerParams.PLAYER_SPEED_MAX, nbOfStepsForVelocityModulus);
        s3 = valueToIndex(s.getPlayerVelocityDirection(), -180.0f, 180.0f,
            nbOfStepsForVelocityAngle);
        s4 = valueToIndex(s.getPlayerBodyDirection(), -180.0f, 180.0f,
            nbOfStepsForRelativeAngle);
        s5 = valueToIndex(s.getRelativeDistance(), 0.0f, 125.0f,
            nbOfStepsForDistance);
        s6 = valueToIndex(s.getRelativeDirection(), -180.0f, 180.0f,
            nbOfStepsForRelativeAngle);

        //        System.out.println("s0: " + s0);
        //        System.out.println("s1: " + s1);
        //        System.out.println("s2: " + s2);
        //        System.out.println("s3: " + s3);
        //        System.out.println("s4: " + s4);
        //        System.out.println("s5: " + s5);
        //        System.out.println("s6: " + s6);

        float max = -1000000.0f;
        float[] argMaxUq = new float[3];
        argMaxUq[0] = -1.0f;
        argMaxUq[1] = -1.0f;
        argMaxUq[2] = -1.0f;
        for (int i = 0; i < q0[s0][s1][s2][s3][s4][s5][s6].length; i++)
        {
            if (q0[s0][s1][s2][s3][s4][s5][s6][i] > max)
            {
                max = q0[s0][s1][s2][s3][s4][s5][s6][i];
                argMaxUq[0] = i >= nbOfStepsForTurn ? dashValue(i)
                        : turnValue(i);
                argMaxUq[1] = i >= nbOfStepsForTurn ? dashValue(i)
                        : turnValue(i);
                argMaxUq[2] = i >= nbOfStepsForTurn ? 0.0f : 1.0f;
            }

        }

        return argMaxUq;
    }

    public float[] getAction(State s)
    {
        return argMaxUq(s, qTable);
    }

    private boolean q0EqualsQ1(float[][][][][][][][] q0,
                               float[][][][][][][][] q1)
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
                                        if (Math.abs(q0[i][j][k][l][m][n][o][p]
                                                - q1[i][j][k][l][m][n][o][p]) > 0.001f)
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

        return true;

    }

    public void saveQl(String filename, float[][][][][][][][] q)
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

    public float[][][][][][][][] loadQl(String filename)
    {
        float[][][][][][][][] q = null;

        try
        {
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(gzis);
            q = (float[][][][][][][][]) in.readObject();
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
        for (int i = 0; i < qTable.length; i++)
        {
            for (int j = 0; j < qTable[i].length; j++)
            {
                for (int k = 0; k < qTable[i][j].length; k++)
                {
                    for (int l = 0; l < qTable[i][j][k].length; l++)
                    {
                        for (int m = 0; m < qTable[i][j][k][l].length; m++)
                        {
                            for (int n = 0; n < qTable[i][j][k][l][m].length; n++)
                            {
                                for (int o = 0; o < qTable[i][j][k][l][m][n].length; o++)
                                {
                                    for (int p = 0; p < qTable[i][j][k][l][m][n][o].length; p++)
                                    {
                                        System.out
                                            .print(" "
                                                    + qTable[i][j][k][l][m][n][o][p]);
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
