package org.qesm.app.model.test;

import org.apache.commons.math3.distribution.TDistribution;

public class StudentTTest extends StatisticalTest {

    public StudentTTest() {
        name = "StudentTTest";
    }

    @Override
    protected double getCriticalValue() {
        TDistribution tDistribution = new TDistribution(currentSamplesSize - 1);
        return tDistribution.inverseCumulativeProbability(1 - (1 - confidence) / 2);
    }
}
