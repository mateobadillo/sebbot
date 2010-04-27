package sebbot.learning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sebbot.MathTools;
import sebbot.SoccerParams;

/**
 * @author Sebastien Lentz
 *
 */
public class Qiteration implements Policy, Serializable, Runnable
{
    private static final long     serialVersionUID = -1245314286263462019L;

    int                           totalNbOfIterations;
    long                          totalComputationTime;

    private float[][][][][][][][] qTable;
    private float[][][][][][][][] oldQtable;

    private int                   nbOfStepsForVelocityNorm;
    private int                   nbOfStepsForVelocityDirection;
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
    public Qiteration(int nbOfStepsForVelocityModulus,
                      int nbOfStepsForVelocityAngle, int nbOfStepsForDistance,
                      int nbOfStepsForRelativeAngle, int nbOfStepsForDash,
                      int nbOfStepsForTurn)
    {
        this.totalNbOfIterations = 0;
        this.totalComputationTime = 0;
        this.nbOfStepsForVelocityNorm = nbOfStepsForVelocityModulus;
        this.nbOfStepsForVelocityDirection = nbOfStepsForVelocityAngle;
        this.nbOfStepsForDistance = nbOfStepsForDistance;
        this.nbOfStepsForRelativeAngle = nbOfStepsForRelativeAngle;
        this.nbOfStepsForDash = nbOfStepsForDash;
        this.nbOfStepsForTurn = nbOfStepsForTurn;

        State.setBallVelocityNormSteps(nbOfStepsForVelocityModulus);
        State.setBallVelocityDirectionSteps(nbOfStepsForVelocityAngle);
        State.setPlayerVelocityNormSteps(nbOfStepsForVelocityModulus);
        State.setPlayerVelocityDirectionSteps(nbOfStepsForVelocityAngle);
        State.setPlayerBodyDirectionSteps(nbOfStepsForRelativeAngle);
        State.setRelativeDistanceSteps(nbOfStepsForDistance);
        State.setRelativeDirectionSteps(nbOfStepsForRelativeAngle);

        Action.setDashSteps(nbOfStepsForDash);
        Action.setTurnSteps(nbOfStepsForTurn);

        this.oldQtable = new float[nbOfStepsForVelocityNorm][nbOfStepsForVelocityDirection][nbOfStepsForVelocityNorm][nbOfStepsForVelocityDirection][nbOfStepsForRelativeAngle][nbOfStepsForDistance][nbOfStepsForRelativeAngle][nbOfStepsForDash
                + nbOfStepsForTurn];

        this.qTable = new float[nbOfStepsForVelocityNorm][nbOfStepsForVelocityDirection][nbOfStepsForVelocityNorm][nbOfStepsForVelocityDirection][nbOfStepsForRelativeAngle][nbOfStepsForDistance][nbOfStepsForRelativeAngle][nbOfStepsForDash
                + nbOfStepsForTurn];

        initQtables();

        //        State s;
        //        float reward;
        //        for (int i = 0; i < 10; i++)
        //        {
        //            s = new State((float) (Math.random() * 3.0D),
        //                (float) (Math.random() * 360D - 180D),
        //                (float) (Math.random() * 1.05D),
        //                (float) (Math.random() * 360D - 180D),
        //                (float) (Math.random() * 360D - 180D),
        //                (float) (Math.random() * 125D),
        //                (float) (Math.random() * 360D - 180D));
        //            
        //            reward = MarkovDecisionProcess.trajectoryReward(s, this, 200);
        //            
        //            System.out.println("init state: " + s);
        //            System.out.println("infinite reward: " + reward);
        //            
        //        }
        //        
        //        printQl();
        //        printQl();
        //        printQl();
        //        printQl();
        //        printQl();
        //        
        //        testQ();
    }

    public void computeQl()
    {
        long startTime;
        long finishTime;

        System.out.println("q iteration starting...");
        System.out.println(this);

        float tmp;

        int nbOfIterations = 0;
        State s = new State();
        Action a = new Action(0, false);

        do
        {
            startTime = new Date().getTime();
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
                                                .setBallVelocityNorm((float) MathTools
                                                    .indexToValue(
                                                        bvn,
                                                        0.0f,
                                                        SoccerParams.BALL_SPEED_MAX,
                                                        nbOfStepsForVelocityNorm,
                                                        0.0f));
                                            s
                                                .setBallVelocityDirection((float) MathTools
                                                    .indexToValue(
                                                        bvd,
                                                        -180.0f,
                                                        180.0f,
                                                        nbOfStepsForVelocityDirection,
                                                        0.0f));
                                            s
                                                .setPlayerVelocityNorm((float) MathTools
                                                    .indexToValue(
                                                        pvn,
                                                        0,
                                                        SoccerParams.PLAYER_SPEED_MAX,
                                                        nbOfStepsForVelocityNorm,
                                                        0.0f));
                                            s
                                                .setPlayerVelocityDirection((float) MathTools
                                                    .indexToValue(
                                                        pvd,
                                                        -180.0f,
                                                        180.0f,
                                                        nbOfStepsForVelocityDirection,
                                                        0.0f));
                                            s
                                                .setPlayerBodyDirection((float) MathTools
                                                    .indexToValue(
                                                        pbd,
                                                        -180.0f,
                                                        180.0f,
                                                        nbOfStepsForRelativeAngle,
                                                        0.0f));
                                            s
                                                .setRelativeDistance((float) MathTools
                                                    .indexToValue(rdist, 0.0f,
                                                        125.0f,
                                                        nbOfStepsForDistance,
                                                        0.0f));
                                            s
                                                .setRelativeDirection((float) MathTools
                                                    .indexToValue(
                                                        rdir,
                                                        -180.0f,
                                                        180.0f,
                                                        nbOfStepsForRelativeAngle,
                                                        0.0f));

                                            if (act >= nbOfStepsForTurn)
                                            {
                                                a.setTurn(false);
                                                a
                                                    .setValue((float) MathTools
                                                        .indexToValue(
                                                            act
                                                                    - nbOfStepsForTurn,
                                                            0.0f, 100.0f,
                                                            nbOfStepsForDash,
                                                            1.0f));
                                            }
                                            else
                                            {
                                                a.setTurn(true);
                                                a
                                                    .setValue((float) MathTools
                                                        .indexToValue(act,
                                                            -180.0f, 180.0f,
                                                            nbOfStepsForTurn,
                                                            0.5f));
                                            }

                                            tmp = qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act];

                                            qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act] = MarkovDecisionProcess
                                                .reward(s, a, true)
                                                    + 0.85f
                                                    * maxUq(
                                                        MarkovDecisionProcess
                                                            .nextState(s, a,
                                                                true),
                                                        oldQtable);

                                            if (qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act] > 2f * 1000000f)
                                            {
                                                System.out
                                                    .println("prob: "
                                                            + qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act]
                                                            + " " + s + " " + a);
                                            }

                                            oldQtable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act] = tmp;

                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

            //printQl();
            totalNbOfIterations++;
            finishTime = new Date().getTime();
            totalComputationTime += (finishTime - startTime);

            System.out.println(nbOfIterations + " iterations done.");
        }
        while (!q0EqualsQ1(oldQtable, qTable) && nbOfIterations < 5000);

        System.out.println("nb of iterations: " + nbOfIterations);
        System.out.println("q iteration table computed.");

        save("backupQl.zip");
    }

    public float qFunction(State s, Action a)
    {
        int i0, i1, i2, i3, i4, i5, i6, i7;
        i0 = MathTools.valueToIndex(s.getBallVelocityNorm(), 0.0f,
            SoccerParams.BALL_SPEED_MAX, nbOfStepsForVelocityNorm);
        i1 = MathTools.valueToIndex(s.getBallVelocityDirection(), -180.0f,
            180.0f, nbOfStepsForVelocityDirection);
        i2 = MathTools.valueToIndex(s.getPlayerVelocityNorm(), 0.0f,
            SoccerParams.PLAYER_SPEED_MAX, nbOfStepsForVelocityNorm);
        i3 = MathTools.valueToIndex(s.getPlayerVelocityDirection(), -180.0f,
            180.0f, nbOfStepsForVelocityDirection);
        i4 = MathTools.valueToIndex(s.getPlayerBodyDirection(), -180.0f,
            180.0f, nbOfStepsForRelativeAngle);
        i5 = MathTools.valueToIndex(s.getRelativeDistance(), 0.0f, 125.0f,
            nbOfStepsForDistance);
        i6 = MathTools.valueToIndex(s.getRelativeDirection(), -180.0f, 180.0f,
            nbOfStepsForRelativeAngle);

        if (a.isTurn())
        {
            i7 = MathTools.valueToIndex(a.getValue(), -180.0f, 180.0f,
                nbOfStepsForTurn);
        }
        else
        {
            i7 = nbOfStepsForTurn
                    + MathTools.valueToIndex(a.getValue(), 0.0f, 100.0f,
                        nbOfStepsForDash);
        }

        return qTable[i0][i1][i2][i3][i4][i5][i6][i7];
    }

    private float maxUq(State s, float[][][][][][][][] q0)
    {
        int s0, s1, s2, s3, s4, s5, s6;
        s0 = MathTools.valueToIndex(s.getBallVelocityNorm(), 0.0f,
            SoccerParams.BALL_SPEED_MAX, nbOfStepsForVelocityNorm);
        s1 = MathTools.valueToIndex(s.getBallVelocityDirection(), -180.0f,
            180.0f, nbOfStepsForVelocityDirection);
        s2 = MathTools.valueToIndex(s.getPlayerVelocityNorm(), 0.0f,
            SoccerParams.PLAYER_SPEED_MAX, nbOfStepsForVelocityNorm);
        s3 = MathTools.valueToIndex(s.getPlayerVelocityDirection(), -180.0f,
            180.0f, nbOfStepsForVelocityDirection);
        s4 = MathTools.valueToIndex(s.getPlayerBodyDirection(), -180.0f,
            180.0f, nbOfStepsForRelativeAngle);
        s5 = MathTools.valueToIndex(s.getRelativeDistance(), 0.0f, 125.0f,
            nbOfStepsForDistance);
        s6 = MathTools.valueToIndex(s.getRelativeDirection(), -180.0f, 180.0f,
            nbOfStepsForRelativeAngle);

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

    public Action chooseAction(State s)
    {
        int s0, s1, s2, s3, s4, s5, s6;
        s0 = MathTools.valueToIndex(s.getBallVelocityNorm(), 0.0f,
            SoccerParams.BALL_SPEED_MAX, nbOfStepsForVelocityNorm);
        s1 = MathTools.valueToIndex(s.getBallVelocityDirection(), -180.0f,
            180.0f, nbOfStepsForVelocityDirection);
        s2 = MathTools.valueToIndex(s.getPlayerVelocityNorm(), 0.0f,
            SoccerParams.PLAYER_SPEED_MAX, nbOfStepsForVelocityNorm);
        s3 = MathTools.valueToIndex(s.getPlayerVelocityDirection(), -180.0f,
            180.0f, nbOfStepsForVelocityDirection);
        s4 = MathTools.valueToIndex(s.getPlayerBodyDirection(), -180.0f,
            180.0f, nbOfStepsForRelativeAngle);
        s5 = MathTools.valueToIndex(s.getRelativeDistance(), 0.0f, 125.0f,
            nbOfStepsForDistance);
        s6 = MathTools.valueToIndex(s.getRelativeDirection(), -180.0f, 180.0f,
            nbOfStepsForRelativeAngle);

        float max = -1000000.0f;
        Action a = null;
        for (int i = 0; i < qTable[s0][s1][s2][s3][s4][s5][s6].length; i++)
        {
            if (qTable[s0][s1][s2][s3][s4][s5][s6][i] > max)
            {
                max = qTable[s0][s1][s2][s3][s4][s5][s6][i];

                if (i < nbOfStepsForTurn)
                {
                    a = new Action((float) MathTools.indexToValue(i, -180.0f,
                        180.0f, nbOfStepsForTurn, 0.5f), true);
                }
                else
                {
                    a = new Action((float) MathTools.indexToValue(i
                            - nbOfStepsForTurn, 0.0f, 100.0f, nbOfStepsForDash,
                        1.0f), false);
                }
            }

        }

        return a;
    }

    private void initQtables()
    {
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

    private void testQ()
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
                                        if (qTable[i][j][k][l][m][n][o][p] > 2f * 1000000f)
                                        {
                                            System.out
                                                .println("prob: "
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

    public void save(String filename)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            ObjectOutputStream out = new ObjectOutputStream(gzos);
            out.writeObject(this);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static synchronized Qiteration loadQl(String filename)
    {
        Qiteration q = null;
        try
        {
            System.out.println("Loading QTable...");
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(gzis);
            q = (Qiteration) in.readObject();
            in.close();
            System.out.println("QTable loaded.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return q;
    }

    public void printQl()
    {
        for (int i = 0; i < 10; i++)
        {
            int i0, i1, i2, i3, i4, i5, i6, i7;
            i0 = (int) (Math.ceil(Math.random()
                    * (nbOfStepsForVelocityNorm - 1)));
            i1 = (int) (Math.ceil(Math.random()
                    * (nbOfStepsForVelocityDirection - 1)));
            i2 = (int) (Math.ceil(Math.random()
                    * (nbOfStepsForVelocityNorm - 1)));
            i3 = (int) Math.ceil(Math.random()
                    * (nbOfStepsForVelocityDirection - 1));
            i4 = (int) (Math.ceil(Math.random()
                    * (nbOfStepsForRelativeAngle - 1)));
            i5 = (int) (Math.ceil(Math.random() * (nbOfStepsForDistance - 1)));
            i6 = (int) Math.ceil(Math.random()
                    * (nbOfStepsForRelativeAngle - 1));
            i7 = (int) (Math.ceil(Math.random()
                    * ((nbOfStepsForDash + nbOfStepsForTurn - 1))));

            System.out.println(i0 + " " + i1 + " " + i2 + " " + i3 + " " + i4
                    + " " + i5 + " " + i6 + " " + i7 + " ");
            System.out.println(qTable[i0][i1][i2][i3][i4][i5][i6][i7]);
        }
    }

    public void run()
    {
        computeQl();
    }

    public String toString()
    {
        String str = "";
        int stateSpaceSize = nbOfStepsForDistance
                * nbOfStepsForVelocityDirection * nbOfStepsForVelocityDirection
                * nbOfStepsForVelocityNorm * nbOfStepsForVelocityNorm
                * nbOfStepsForRelativeAngle * nbOfStepsForRelativeAngle;
        int actionSpaceSize = (nbOfStepsForTurn + nbOfStepsForDash);

        str += "X x U² = "
                + (stateSpaceSize * actionSpaceSize * actionSpaceSize) + "\n";

        str += "Total number of iterations done so far: " + totalNbOfIterations;
        str += "Total computation time so far (min): "
                + ((float) (totalComputationTime) / 1000f / 60f) + "\n";

        return str;
    }
}
