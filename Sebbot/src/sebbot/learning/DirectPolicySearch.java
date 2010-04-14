package sebbot.learning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sebbot.MathTools;
import sebbot.SoccerParams;

/**
 * This class implements a direct policy search algorithm for the ball capture
 * problem. Basic functions are optimized using cross-entropy.
 * To understand how the algorithm works, please refer to the article from
 * <a href=http://www.montefiore.ulg.ac.be/~ernst/>Damien Ernst</a>.
 * 
 * @see <a href=http://www.montefiore.ulg.ac.be/~ernst/adprl-ceps.pdf>
 * "Policy search with cross-entropy optimization of basis functions"
 * L. Busoniu, D. Ernst, R. Babuska and B. De Schutter</a>
 * 
 * @author Sebastien Lentz
 *
 */
public class DirectPolicySearch implements Policy, Serializable, Runnable
{
    private static final long             serialVersionUID = 8074266714836534435L;

    int                                   nbOfIterations;
    Random                                random;

    int                                   nbOfDiscreteActions;
    int                                   nbOfBasicFunctions;
    int                                   nbOfSamples;
    float                                 percentageOfGoodSamples;

    float[][]                             centersMeans;
    float[][]                             centersStdDevs;
    float[][]                             radiiMeans;
    float[][]                             radiiStdDevs;
    float[][]                             bernoulliMeans;

    RadialGaussian[]                      basicFunctions;
    ArrayList<LinkedList<RadialGaussian>> actionToBasicFunctions;
    LinkedList<State>                     initialStates;

    public DirectPolicySearch()
    {
        this.nbOfIterations = 0;

        this.percentageOfGoodSamples = 0.05f;
        this.random = new Random();
        this.nbOfDiscreteActions = (Action.getTurnSteps() + Action
            .getDashSteps());
        this.nbOfBasicFunctions = nbOfDiscreteActions + nbOfDiscreteActions / 2;

        int nbOfBits = (int) (Math.ceil(Math.log(nbOfDiscreteActions)
                / Math.log(2.0d)));

        this.nbOfSamples = 1 /* Multiplier */
        * (4 * 7 * nbOfBasicFunctions /* Nb of Epsilon params */
        + nbOfBasicFunctions * nbOfBits); /* Nb of Theta params */

        this.basicFunctions = new RadialGaussian[nbOfBasicFunctions];
        this.centersMeans = new float[nbOfBasicFunctions][7];
        this.centersStdDevs = new float[nbOfBasicFunctions][7];
        this.radiiMeans = new float[nbOfBasicFunctions][7];
        this.radiiStdDevs = new float[nbOfBasicFunctions][7];
        this.bernoulliMeans = new float[nbOfBasicFunctions][nbOfBits];
        this.actionToBasicFunctions = new ArrayList<LinkedList<RadialGaussian>>();

        for (int i = 0; i < nbOfDiscreteActions; i++)
        {
            actionToBasicFunctions.add(new LinkedList<RadialGaussian>());
        }

        for (int i = 0; i < nbOfBasicFunctions; i++)
        {
            basicFunctions[i] = new RadialGaussian(nbOfDiscreteActions - 1
                    - (i % nbOfDiscreteActions));

            centersMeans[i] = basicFunctions[i].getCenters();
            radiiMeans[i] = basicFunctions[i].getRadii();

            centersStdDevs[i][0] = SoccerParams.BALL_SPEED_MAX / 2.0f;
            centersStdDevs[i][1] = 180f / 2.0f;
            centersStdDevs[i][2] = SoccerParams.PLAYER_SPEED_MAX / 2.0f;
            centersStdDevs[i][3] = 180f / 2.0f;
            centersStdDevs[i][4] = 180f / 2.0f;
            centersStdDevs[i][5] = 125f / 2.0f;
            centersStdDevs[i][6] = 180f / 2.0f;
            radiiStdDevs[i][0] = SoccerParams.BALL_SPEED_MAX / 2.0f;
            radiiStdDevs[i][1] = 180f / 2.0f;
            radiiStdDevs[i][2] = SoccerParams.PLAYER_SPEED_MAX / 2.0f;
            radiiStdDevs[i][3] = 180f / 2.0f;
            radiiStdDevs[i][4] = 180f / 2.0f;
            radiiStdDevs[i][5] = 180f / 2.0f;
            radiiStdDevs[i][6] = 180f / 2.0f;

            for (int j = 0; j < nbOfBits; j++)
            {
                bernoulliMeans[i][j] = 0.5f;
            }

            actionToBasicFunctions.get(basicFunctions[i].getDiscreteActionNb())
                .add(basicFunctions[i]);
        }

        initialStates = new LinkedList<State>();
        generateStates();

        //computeOptimalParameters();
        //loadBFs("savedBFs.zip");

    }

    public Action chooseAction(State s)
    {

        int actionNb = 0;
        float bestScore = 0.0f;
        float score;
        Iterator<RadialGaussian> it;

        for (int i = 0; i < actionToBasicFunctions.size(); i++)
        {
            score = 0.0f;
            it = actionToBasicFunctions.get(i).iterator();
            while (it.hasNext())
            {
                score += it.next().f(s);
            }

            if (score > bestScore)
            {
                bestScore = score;
                actionNb = i;
            }
        }

        return new Action(actionNb);
    }

    public void computeOptimalParameters()
    {
        int nbOfBits = (int) (Math.ceil(Math.log(nbOfDiscreteActions)
                / Math.log(2.0d)));
        float[][][][] epsilonSamples = new float[nbOfSamples][nbOfBasicFunctions][2][7];
        boolean[][][] thetaSamples = new boolean[nbOfSamples][nbOfBasicFunctions][nbOfBits];

        System.out.println("Nb of samples: " + nbOfSamples);
        System.out.println("Nb of basic functions: " + nbOfBasicFunctions);
        System.out.println("Nb of discrete actions: " + nbOfDiscreteActions);
        System.out.println("Nb of initial states: " + initialStates.size());
        System.out.println("Total number of iterations completed so far: "
                + nbOfIterations);
        for (int nbOfIt = 0; nbOfIt < 100; nbOfIt++)
        {
            System.out.println((nbOfIt + 1) + "th iteration starting...");

            // Generate samples
            System.out.println("Generating samples...");
            boolean isValidSample = false;
            for (int i = 0; i < nbOfSamples; i++)
            {
                for (int j = 0; j < nbOfBasicFunctions; j++)
                {
                    for (int k = 0; k < 7; k++)
                    {
                        isValidSample = false;
                        while (!isValidSample)
                        {
                            epsilonSamples[i][j][0][k] = MathTools
                                .nextGaussian(centersMeans[j][k],
                                    centersStdDevs[j][k]);

                            isValidSample = isValidMean(
                                epsilonSamples[i][j][0][k], k);

                        }

                        isValidSample = false;
                        while (!isValidSample)
                        {
                            epsilonSamples[i][j][1][k] = MathTools
                                .nextGaussian(radiiMeans[j][k],
                                    radiiStdDevs[j][k]);

                            if (epsilonSamples[i][j][1][k] > 0.0001f)
                            {
                                isValidSample = true;
                            }
                        }

                    }
                    int actionNb = nbOfDiscreteActions;
                    while (actionNb >= nbOfDiscreteActions)
                    {
                        for (int k = 0; k < nbOfBits; k++)
                        {
                            thetaSamples[i][j][k] = MathTools
                                .nextBernoulli(bernoulliMeans[j][k]);
                        }
                        actionNb = MathTools.toDecimal(thetaSamples[i][j]);
                    }
                }
            }

            // Compute score for each sample
            System.out.println("Computing samples scores...");
            TreeMap<Float, ArrayList<Integer>> samplesScore = new TreeMap<Float, ArrayList<Integer>>();
            float score;
            for (int i = 0; i < nbOfSamples; i++)
            {
                for (int j = 0; j < nbOfDiscreteActions; j++)
                {
                    actionToBasicFunctions.get(j).clear();
                }

                int associatedAction;
                for (int j = 0; j < nbOfBasicFunctions; j++)
                {
                    basicFunctions[j].setCenters(epsilonSamples[i][j][0]);
                    basicFunctions[j].setRadii(epsilonSamples[i][j][1]);

                    associatedAction = MathTools.toDecimal(thetaSamples[i][j]);
                    actionToBasicFunctions.get(associatedAction).add(
                        basicFunctions[j]);
                }

                score = 0.0f;
                for (State s : initialStates)
                {
                    score += MarkovDecisionProcess
                        .trajectoryReward(s, this, 30)
                            / initialStates.size();
                }

                ArrayList<Integer> l = samplesScore.get(score);
                if (l == null)
                {
                    l = new ArrayList<Integer>();
                }
                l.add(i);
                samplesScore.put(score, l);

                float percentageDone = 100.0f * (float) i / (float) nbOfSamples;
                if (i % (nbOfSamples / 100 * 10) == 0)
                {
                    System.out.print(Math.round(percentageDone) + "% ");
                }
            }
            System.out.println();

            // Get best samples
            int nbOfGoodSamples = (int) (Math.ceil(percentageOfGoodSamples
                    * nbOfSamples));

            int[] goodSamplesIndexes = new int[nbOfGoodSamples];
            ArrayList<Integer> l = samplesScore.pollLastEntry().getValue();
            for (int i = 0; i < nbOfGoodSamples; i++)
            {
                if (l.isEmpty())
                {
                    l = samplesScore.pollLastEntry().getValue();
                }
                goodSamplesIndexes[i] = l.remove(l.size() - 1);
            }

            // Compute the new means and standard deviations for the parameters
            System.out.println("Updating means and standard deviations...");
            float goodSamplesCenters[] = new float[goodSamplesIndexes.length];
            float goodSamplesRadii[] = new float[goodSamplesIndexes.length];
            boolean goodSamplesBernoulli[] = new boolean[goodSamplesIndexes.length];
            for (int j = 0; j < nbOfBasicFunctions; j++)
            {
                for (int k = 0; k < 7; k++)
                {
                    for (int i = 0; i < goodSamplesIndexes.length; i++)
                    {
                        goodSamplesCenters[i] = epsilonSamples[goodSamplesIndexes[i]][j][0][k];
                        goodSamplesRadii[i] = epsilonSamples[goodSamplesIndexes[i]][j][1][k];
                    }
                    centersMeans[j][k] = MathTools.mean(goodSamplesCenters);
                    radiiMeans[j][k] = MathTools.mean(goodSamplesRadii);
                    centersStdDevs[j][k] = MathTools.stdDev(goodSamplesCenters,
                        centersMeans[j][k]);
                    radiiStdDevs[j][k] = MathTools.stdDev(goodSamplesRadii,
                        radiiMeans[j][k]);
                }

                for (int k = 0; k < nbOfBits; k++)
                {
                    for (int i = 0; i < goodSamplesIndexes.length; i++)
                    {
                        goodSamplesBernoulli[i] = thetaSamples[goodSamplesIndexes[i]][j][k];
                    }
                    bernoulliMeans[j][k] = MathTools.mean(goodSamplesBernoulli);
                }
            }

            // Update basic functions parameters using the best samples
            for (int i = 0; i < nbOfBasicFunctions; i++)
            {
                basicFunctions[i].setCenters(centersMeans[i]);
                basicFunctions[i].setRadii(radiiMeans[i]);
            }

            nbOfIterations++;
            save("savedBFs.zip");
        }
    }

    private boolean isValidMean(float sample, int stateVariableNb)
    {
        boolean isValidSample = false;

        switch (stateVariableNb)
        {
        case 0:
            if (sample >= 0.0f && sample <= SoccerParams.BALL_SPEED_MAX)
            {
                isValidSample = true;
            }
            break;
        case 1:
            if (sample >= -180.0f && sample <= 180.0f)
            {
                isValidSample = true;
            }
            break;
        case 2:
            if (sample >= 0.0f && sample <= SoccerParams.PLAYER_SPEED_MAX)
            {
                isValidSample = true;
            }
            break;
        case 3:
            if (sample >= -180.0f && sample <= 180.0f)
            {
                isValidSample = true;
            }
            break;
        case 4:
            if (sample >= -180.0f && sample <= 180.0f)
            {
                isValidSample = true;
            }
            break;
        case 5:
            if (sample >= 0.0f && sample <= 125.0f)
            {
                isValidSample = true;
            }
            break;
        case 6:
            if (sample >= -180.0f && sample <= 180.0f)
            {
                isValidSample = true;
            }
            break;
        default:
            break;

        }

        return isValidSample;
    }

    private void generateStates()
    {
        State s;
        for (float i = 0; i < 3.0f; i += 3.0f)
        {
            for (float j = -180.0f; j < 180.0f; j += 120.0f)
            {
                for (float k = 0; k < 1.05f; k += 1.05f)
                {
                    for (float l = -180.0f; l < 180.0f; l += 120.0f)
                    {
                        for (float m = -180.0f; m < 180.0f; m += 120.0f)
                        {
                            for (float n = 0; n < 125.0f; n += 25.0f)
                            {
                                for (float o = -180.0f; o < 180.0f; o += 120.0f)
                                {
                                    s = new State(i, j, k, l, m, n, o);
                                    initialStates.add(s);
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

            //            out.writeObject(centersMeans);
            //            out.writeObject(centersStdDevs);
            //            out.writeObject(radiiMeans);
            //            out.writeObject(radiiStdDevs);
            //            out.writeObject(bernoulliMeans);
            //            out.writeObject(basicFunctions);
            //            out.writeObject(actionToBasicFunctions);
            //            out.writeObject(initialStates);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static DirectPolicySearch load(String filename)
    {
        DirectPolicySearch dps = null;
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(gzis);
            dps = (DirectPolicySearch) in.readObject();
            //            centersMeans = (float[][]) in.readObject();
            //            centersStdDevs = (float[][]) in.readObject();
            //            radiiMeans = (float[][]) in.readObject();
            //            radiiStdDevs = (float[][]) in.readObject();
            //            bernoulliMeans = (float[][]) in.readObject();
            //            basicFunctions = (RadialGaussian[]) in.readObject();
            //            actionToBasicFunctions = (ArrayList<LinkedList<RadialGaussian>>) in.readObject();
            //            initialStates = (LinkedList<State>) in.readObject();
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return dps;
    }

    public void run()
    {
        computeOptimalParameters();
    }

}
