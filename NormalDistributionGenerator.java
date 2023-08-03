package org.example;

import java.text.DecimalFormat;
import java.util.Random;

public class NormalDistributionGenerator {

    public static void main(String[] args) {
        int numberOfElements = 1000;
        DecimalFormat decimalFormat = new DecimalFormat("#.###############");
        double[] normalDistributionArray = generateNormalDistributionArray(numberOfElements);

        double mean = calculateMean(normalDistributionArray);
        double variance = calculateVariance(normalDistributionArray, mean);
        double standardDeviation = Math.sqrt(variance);

        System.out.println("Mean (Expected Value): " + decimalFormat.format(mean));
        System.out.println("Variance: " + decimalFormat.format(variance));
        System.out.println("Standard Deviation: " + decimalFormat.format(standardDeviation));

        int repeat_times = 10_000_000;

        ScenarioResults scenario1Results = executeScenario1(decimalFormat, normalDistributionArray, 100, 33, repeat_times);

        ScenarioResults scenario2Results = executeScenario2(decimalFormat, normalDistributionArray, repeat_times);

        double meanMean2L = Math.abs(mean - scenario1Results.meanSampledResults());
        System.out.println("Difference between mean and 2-level sampling mean: " + decimalFormat.format(meanMean2L) + " | " + 100 * scenario2Results.meanSampledResults() / mean);
        double varianceVariance2L = Math.abs(variance - scenario1Results.varianceSampledResults());
        System.out.println("Difference between variance and 2-level sampling variance: " + decimalFormat.format(varianceVariance2L));
        double standardDeviationStandardDeviation2L = Math.abs(standardDeviation - scenario1Results.standardDeviationSampledResults());
        System.out.println("Difference between standardDeviation and 2-level sampling standardDeviation: " + decimalFormat.format(standardDeviationStandardDeviation2L));

        double meanMean1L = Math.abs(mean - scenario2Results.meanSampledResults());
        System.out.println("Difference between mean and 1-level sampling mean: " + decimalFormat.format(meanMean1L));
        double varianceVariance1L = Math.abs(variance - scenario2Results.varianceSampledResults());
        System.out.println("Difference between variance and 1-level sampling variance: " + decimalFormat.format(varianceVariance1L));
        double standardDeviationStandardDeviation1L = Math.abs(standardDeviation - scenario2Results.standardDeviationSampledResults());
        System.out.println("Difference between standardDeviation and 1-level sampling standardDeviation: " + decimalFormat.format(standardDeviationStandardDeviation1L));

        System.out.println(meanMean2L > meanMean1L);
        System.out.println(varianceVariance2L > varianceVariance1L);
        System.out.println(standardDeviationStandardDeviation2L > standardDeviationStandardDeviation1L);

    }

    private static ScenarioResults executeScenario2(DecimalFormat decimalFormat, double[] normalDistributionArray, int repeat_times) {
        double meanSampledResults1 = 0;
        double varianceSampledResults1 = 0;
        double standardDeviationSampledResults1 = 0;
        for (int i = 0; i < repeat_times; i++) {
            double[] sampledArray1 = sampleFromArray(normalDistributionArray, 33);

            double meanSampled1 = calculateMean(sampledArray1);
            double varianceSampled1 = calculateVariance(sampledArray1, meanSampled1);
            double standardDeviationSampled1 = Math.sqrt(varianceSampled1);

            meanSampledResults1 = meanSampledResults1 + meanSampled1;
            varianceSampledResults1 = varianceSampledResults1 + varianceSampled1;
            standardDeviationSampledResults1 = standardDeviationSampledResults1 + standardDeviationSampled1;
        }

        meanSampledResults1 = meanSampledResults1 / repeat_times;
        varianceSampledResults1 = varianceSampledResults1 / repeat_times;
        standardDeviationSampledResults1 = standardDeviationSampledResults1 / repeat_times;

        System.out.println("Mean (Expected Value) Sampled1: " + decimalFormat.format(meanSampledResults1));
        System.out.println("Variance Sampled1: " + decimalFormat.format(varianceSampledResults1));
        System.out.println("Standard Deviation Sampled1: " + decimalFormat.format(standardDeviationSampledResults1));
        return new ScenarioResults(meanSampledResults1, varianceSampledResults1, standardDeviationSampledResults1);
    }


    private static ScenarioResults executeScenario1(DecimalFormat decimalFormat, double[] normalDistributionArray,
                                                    int firstSampleSize, int secondSampleSize, int repeatTimes) {
        double meanSampledResults = 0;
        double varianceSampledResults = 0;
        double standardDeviationSampledResults = 0;
        for (int i = 0; i < repeatTimes; i++) {
            double[] sampledArray = sampleFromArray(normalDistributionArray, firstSampleSize);
            double[] sampledSampledArray = sampleFromArray(sampledArray, secondSampleSize);

            double meanSampledSampled = calculateMean(sampledSampledArray);
            double varianceSampledSampled = calculateVariance(sampledSampledArray, meanSampledSampled);
            double standardDeviationSampledSampled = Math.sqrt(varianceSampledSampled);

            meanSampledResults = meanSampledResults + meanSampledSampled;
            varianceSampledResults = varianceSampledResults + varianceSampledSampled;
            standardDeviationSampledResults = standardDeviationSampledResults + standardDeviationSampledSampled;
        }

        meanSampledResults = meanSampledResults / repeatTimes;
        varianceSampledResults = varianceSampledResults / repeatTimes;
        standardDeviationSampledResults = standardDeviationSampledResults / repeatTimes;

        System.out.println("Mean (Expected Value) Sampled Sampled: " + decimalFormat.format(meanSampledResults));
        System.out.println("Variance Sampled Sampled: " + decimalFormat.format(varianceSampledResults));
        System.out.println("Standard Deviation Sampled Sampled: " + decimalFormat.format(standardDeviationSampledResults));
        return new ScenarioResults(meanSampledResults, varianceSampledResults, standardDeviationSampledResults);
    }

    private record ScenarioResults(double meanSampledResults, double varianceSampledResults, double standardDeviationSampledResults) {
    }

    private static double[] sampleFromArray(double[] arr, int sampleSize) {
        if (sampleSize >= arr.length) {
            return arr.clone();
        }

        double[] sampledArray = new double[sampleSize];
        Random random = new Random();

        for (int i = 0; i < sampleSize; i++) {
            int randomIndex = random.nextInt(arr.length);
            sampledArray[i] = arr[randomIndex];
        }

        return sampledArray;
    }

    private static double calculateMean(double[] arr) {
        double sum = 0.0;
        for (double value : arr) {
            sum += value;
        }
        return sum / arr.length;
    }

    private static double calculateVariance(double[] arr, double mean) {
        double sumSquaredDifferences = 0.0;
        for (double value : arr) {
            double difference = value - mean;
            sumSquaredDifferences += difference * difference;
        }
        return sumSquaredDifferences / arr.length;
    }

    private static double[] generateNormalDistributionArray(int numberOfElements) {
        double[] result = new double[numberOfElements];
        Random random = new Random();

        for (int i = 0; i < numberOfElements; i += 2) {
            double u1 = random.nextDouble(); // Uniformly distributed between 0 and 1
            double u2 = random.nextDouble(); // Uniformly distributed between 0 and 1

            double z0 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
            double z1 = Math.sqrt(-2.0 * Math.log(u1)) * Math.sin(2.0 * Math.PI * u2);

            result[i] = z0;
            if (i + 1 < numberOfElements) {
                result[i + 1] = z1;
            }
        }

        return result;
    }

}
