package org.qesm.app.model.test;

import org.apache.commons.math3.distribution.NormalDistribution;

public class ZTest extends StatisticalTest {

    public ZTest() {
        name = "ZTest";
    }

    @Override
    protected double getCriticalValue() {
        NormalDistribution normalDistribution = new NormalDistribution();
        return normalDistribution.inverseCumulativeProbability(1 - (1 - confidence) / 2);
    }

}
