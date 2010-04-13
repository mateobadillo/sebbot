package sebbot.learning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sebbot.SoccerParams;

/**
 * @author Sebastien Lentz
 *
 */
public class DirectPolicySearch implements Policy
{
    Random                             random;
    int                                nbOfDiscreteActions;
    int                                nbOfBasicFunctions;
    int                                nbOfSamples;
    int                                nbOfBits;
    float                              percentageOfGoodSamples;

    GaussianRBF[]                      basicFunctions;

    float[][]                          centersMeans;
    float[][]                          centersStdDevs;
    float[][]                          radiiMeans;
    float[][]                          radiiStdDevs;
    float[][]                          bernoulliMeans;

    ArrayList<LinkedList<GaussianRBF>> actionsBFassoc;
    LinkedList<State>                  initialStates;

    /**
     * @param random
     */
    public DirectPolicySearch()
    {
        this.percentageOfGoodSamples = 0.05f;
        this.random = new Random();
        this.nbOfDiscreteActions = (Action.getTurnSteps() + Action
            .getDashSteps());
        this.nbOfBasicFunctions = nbOfDiscreteActions;

        this.nbOfBits = (int) (Math.ceil(Math.log(nbOfDiscreteActions)
                / Math.log(2.0d)));

        this.nbOfSamples = 1 * (4 * 7 * nbOfBasicFunctions + nbOfBasicFunctions
                * nbOfBits); //TODO changer

        this.basicFunctions = new GaussianRBF[nbOfBasicFunctions];
        this.centersMeans = new float[nbOfBasicFunctions][7];
        this.centersStdDevs = new float[nbOfBasicFunctions][7];
        this.radiiMeans = new float[nbOfBasicFunctions][7];
        this.radiiStdDevs = new float[nbOfBasicFunctions][7];
        this.bernoulliMeans = new float[nbOfBasicFunctions][nbOfBits];
        this.actionsBFassoc = new ArrayList<LinkedList<GaussianRBF>>();

        for (int i = 0; i < nbOfDiscreteActions; i++)
        {
            actionsBFassoc.add(new LinkedList<GaussianRBF>());
        }

        for (int i = 0; i < nbOfBasicFunctions; i++)
        {
            basicFunctions[i] = new GaussianRBF(i);

            centersMeans[i] = basicFunctions[i].getCenters();
            radiiMeans[i] = basicFunctions[i].getRadii();

            for (int j = 0; j < 7; j++)
            {
                centersStdDevs[i][j] = (float) (20.0d * Math.random());
                radiiStdDevs[i][j] = (float) (30.0d * Math.random());
            }

            for (int j = 0; j < nbOfBits; j++)
            {
                bernoulliMeans[i][j] = 0.5f;
            }

            actionsBFassoc.get(basicFunctions[i].getDiscreteActionNb()).add(
                basicFunctions[i]);
        }

        initialStates = new LinkedList<State>();
        generateStates();

        computeOptimalParameters();
        //loadBFs("savedBFs.zip");

    }

    public Action chooseAction(State s)
    {

        int actionNb = 0;
        float bestScore = 0.0f;
        float score;
        Iterator<GaussianRBF> it;

        for (int i = 0; i < actionsBFassoc.size(); i++)
        {
            score = 0.0f;
            it = actionsBFassoc.get(i).iterator();
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

    private void computeOptimalParameters()
    {
        float[][][][] epsilonSamples = new float[nbOfSamples][nbOfBasicFunctions][2][7];
        boolean[][][] thetaSamples = new boolean[nbOfSamples][nbOfBasicFunctions][nbOfBits];

        System.out.println("Nb of samples: " + nbOfSamples);
        System.out.println("Nb of basic functions: " + nbOfBasicFunctions);
        System.out.println("Nb of discrete actions: " + nbOfDiscreteActions);
        System.out.println("Nb of initial states: " + initialStates.size());
        for (int nbOfIt = 0; nbOfIt < 100; nbOfIt++)
        {
            System.out.println((nbOfIt + 1) + "th iteration starting...");

            // Generate samples
            System.out.println("Generating samples...");
            boolean validSample = false;
            for (int i = 0; i < nbOfSamples; i++)
            {
                for (int j = 0; j < nbOfBasicFunctions; j++)
                {
                    for (int k = 0; k < 7; k++)
                    {
                        validSample = false;
                        while (!validSample)
                        {
                            epsilonSamples[i][j][0][k] = nextGaussian(
                                centersMeans[j][k], centersStdDevs[j][k]);
                            switch (k)
                            {
                            case 0:
                                if (epsilonSamples[i][j][0][k] >= 0.0f
                                        && epsilonSamples[i][j][0][k] <= SoccerParams.BALL_SPEED_MAX)
                                {
                                    validSample = true;
                                }
                                break;
                            case 1:
                                if (epsilonSamples[i][j][0][k] >= -180.0f
                                        && epsilonSamples[i][j][0][k] <= 180.0f)
                                {
                                    validSample = true;
                                }
                                break;
                            case 2:
                                if (epsilonSamples[i][j][0][k] >= 0.0f
                                        && epsilonSamples[i][j][0][k] <= SoccerParams.PLAYER_SPEED_MAX)
                                {
                                    validSample = true;
                                }
                                break;
                            case 3:
                                if (epsilonSamples[i][j][0][k] >= -180.0f
                                        && epsilonSamples[i][j][0][k] <= 180.0f)
                                {
                                    validSample = true;
                                }
                                break;
                            case 4:
                                if (epsilonSamples[i][j][0][k] >= -180.0f
                                        && epsilonSamples[i][j][0][k] <= 180.0f)
                                {
                                    validSample = true;
                                }
                                break;
                            case 5:
                                if (epsilonSamples[i][j][0][k] >= 0.0f
                                        && epsilonSamples[i][j][0][k] <= 125.0f)
                                {
                                    validSample = true;
                                }
                                break;
                            case 6:
                                if (epsilonSamples[i][j][0][k] >= -180.0f
                                        && epsilonSamples[i][j][0][k] <= 180.0f)
                                {
                                    validSample = true;
                                }
                                break;

                            }
                        }

                        validSample = false;
                        while (!validSample)
                        {
                            epsilonSamples[i][j][1][k] = nextGaussian(
                                radiiMeans[j][k], radiiStdDevs[j][k]);
                            if (epsilonSamples[i][j][1][k] > 0.0f)
                            {
                                validSample = true;
                            }
                        }

                    }
                    int actionNb = nbOfDiscreteActions;
                    while (actionNb >= nbOfDiscreteActions)
                    {
                        for (int k = 0; k < nbOfBits; k++)
                        {
                            thetaSamples[i][j][k] = nextBernoulli(bernoulliMeans[j][k]);
                        }
                        actionNb = binaryToDecimal(thetaSamples[i][j]);
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
                    actionsBFassoc.get(j).clear();
                }

                int chosenAction;
                for (int j = 0; j < nbOfBasicFunctions; j++)
                {
                    basicFunctions[j].setCenters(epsilonSamples[i][j][0]);
                    basicFunctions[j].setRadii(epsilonSamples[i][j][1]);

                    chosenAction = binaryToDecimal(thetaSamples[i][j]);
                    actionsBFassoc.get(chosenAction).add(basicFunctions[j]);
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
            }

            // Compute the new min score and get best samples.
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
            System.out.println("Updating BFs started...");
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
                    centersMeans[j][k] = mean(goodSamplesCenters);
                    radiiMeans[j][k] = mean(goodSamplesRadii);
                    centersStdDevs[j][k] = stdDev(goodSamplesCenters,
                        centersMeans[j][k]);
                    radiiStdDevs[j][k] = stdDev(goodSamplesRadii,
                        radiiMeans[j][k]);
                }

                for (int k = 0; k < nbOfBits; k++)
                {
                    for (int i = 0; i < goodSamplesIndexes.length; i++)
                    {
                        goodSamplesBernoulli[i] = thetaSamples[goodSamplesIndexes[i]][j][k];
                    }
                    bernoulliMeans[j][k] = mean(goodSamplesBernoulli);
                }
            }

            saveBFs("savedBFs.zip");
            System.out.println("Iteration: " + nbOfIt);
        }
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

    private float mean(float[] f)
    {
        float mean = 0.0f;
        for (int i = 0; i < f.length; i++)
        {
            mean += f[i];
        }

        return mean / f.length;
    }

    private float mean(boolean[] b)
    {
        float nbOfTrue = 0.0f;
        for (int i = 0; i < b.length; i++)
        {
            if (b[i])
            {
                nbOfTrue += 1.0f;
            }
        }

        return nbOfTrue / ((float) b.length);
    }

    private float stdDev(float[] f, float mean)
    {
        float stfDev = 0.0f;
        for (int i = 0; i < f.length; i++)
        {
            stfDev += (f[i] - mean) * (f[i] - mean);
        }

        return (float) Math.sqrt(stfDev / f.length);
    }

    private float nextGaussian(float mean, float stdDev)
    {
        return (float) (mean + random.nextGaussian() * stdDev);
    }

    private boolean nextBernoulli(float mean)
    {
        return random.nextDouble() < mean ? true : false;
    }

    private int binaryToDecimal(boolean[] b)
    {
        int d = 0;
        int pow = 1;

        for (int i = 0; i < b.length; i++)
        {
            if (b[i])
            {
                d += pow;
            }
            pow *= 2;
        }

        return d;
    }

    public void saveBFs(String filename)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            ObjectOutputStream out = new ObjectOutputStream(gzos);
            out.writeObject(basicFunctions);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void loadBFs(String filename)
    {
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(gzis);
            basicFunctions = (GaussianRBF[]) in.readObject();
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
