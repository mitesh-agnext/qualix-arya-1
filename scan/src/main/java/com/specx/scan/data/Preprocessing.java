package com.specx.scan.data;

import android.text.TextUtils;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class Preprocessing {

    public List<Double> applyStandardNormalVariate(List<Double> input) {

        List<Double> output = new ArrayList<>();

        double totalSum = 0.0, standardDeviation = 0.0;

        int length = input.size();

        for (double num : input) {
            totalSum += num;
        }

        double mean = totalSum / length;

        for (double num : input) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        double sigma = Math.sqrt(standardDeviation / length);

        for (double num : input) {
            output.add((num - mean) / sigma);
        }

        return output;
    }

    public static double[] findCanonicalArray(double[] x, double[] y, double[] x0) {
        final PolynomialSplineFunction func = new LinearInterpolator().interpolate(x, y);
        final PolynomialFunction[] splines = func.getPolynomials();
        final PolynomialFunction firstFunc = splines[0];
        final PolynomialFunction lastFunc = splines[splines.length - 1];

        final double[] knots = func.getKnots();
        final double firstKnot = knots[0];
        final double lastKnot = knots[knots.length - 1];

        int i = 0;
        double[] y0 = new double[x0.length];

        for (double wv : x0) {
            if (wv > lastKnot) {
                y0[i++] = lastFunc.value(wv - knots[knots.length - 2]);
            } else if (wv < firstKnot) {
                y0[i++] = firstFunc.value(wv - knots[0]);
            } else {
                y0[i++] = func.value(wv);
            }
        }

        return y0;
    }

    public List<Double> apply(String algorithm, String config, List<Double> avgAbsorbance, List<Double> means) {
        List<Double> absorbance = new ArrayList<>(avgAbsorbance);

        List<Integer> algos = getAlgoList(algorithm);

        for (Integer algo : algos) {
            switch (algo) {
                case 1:
                    absorbance = applyMeanCentering(absorbance, means);
                    break;
                case 2:
                    int nl = Integer.parseInt(config.split("-")[0].trim());
                    int nr = Integer.parseInt(config.split("-")[1].trim());
                    int order = Integer.parseInt(config.split("-")[2].trim());

                    absorbance = applySavitzkyGolay(absorbance, nl, nr, order);
                    break;
                case 3:
                    absorbance = applyMeanScatterCorrection(absorbance, means);
                    break;
            }
        }

        return absorbance;
    }

    private List<Integer> getAlgoList(String algorithm) {
        List<Integer> algos = new ArrayList<>();

        if (!TextUtils.isEmpty(algorithm)) {
            if (algorithm.contains("-")) {
                String[] split = algorithm.split("-");
                for (String algo : split) {
                    algos.add(getAlgoType(algo));
                }
            } else {
                algos.add(getAlgoType(algorithm));
            }
        }

        return algos;
    }

    private int getAlgoType(String algorithm) {
        if (TextUtils.isDigitsOnly(algorithm.trim())) {
            try {
                return Integer.parseInt(algorithm.trim());
            } catch (NumberFormatException e) {
                Timber.e(e);
            }
        }
        return 0;
    }

    private List<Double> applyMeanCentering(List<Double> absorbance, List<Double> means) {
        List<Double> result = new ArrayList<>();
        for (int i = 0, j = absorbance.size(); i < j; i++) {
            double value = absorbance.get(i) - means.get(i);
            result.add(value);
        }
        return result;
    }

    public List<Double> applySavitzkyGolay(List<Double> absorbance, int nl, int nr, int order) {
        List<Double> result = new ArrayList<>();
        double[] coeff = SavitzkyGolayFilter.computeSGCoefficients(nl, nr, order);
        SavitzkyGolayFilter sgFilter = new SavitzkyGolayFilter(nl, nr);

        double[] data = new double[absorbance.size()];
        for (int i = 0, j = absorbance.size(); i < j; i++) {
            data[i] = absorbance.get(i);
        }
        double[] output = sgFilter.smooth(data, coeff);
        for (double value : output) {
            result.add(value);
        }

        for (int i = 0; i < nl; i++) {
            result.set(i, absorbance.get(i));
        }

        for (int i = 0; i < nr; i++) {
            final int index = (output.length - 1) - i;
            result.set(index, absorbance.get(index));
        }

        return result;
    }

    private List<Double> applyMeanScatterCorrection(List<Double> absorbance, List<Double> means) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0, j = means.size(); i < j; i++) {
            if (means.get(i) != 0.0 && !Double.isNaN(absorbance.get(i))) {
                obs.add(means.get(i), absorbance.get(i));
            }
        }
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
        final double[] coeff = fitter.fit(obs.toList());
        List<Double> result = new ArrayList<>();
        if (coeff.length > 1) {
            for (int i = 0, j = means.size(); i < j; i++) {
                result.add((absorbance.get(i) - coeff[0]) / coeff[1]);
            }
        }
        return result;
    }
}