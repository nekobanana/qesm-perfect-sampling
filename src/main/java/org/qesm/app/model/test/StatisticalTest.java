package org.qesm.app.model.test;

public interface StatisticalTest {
    void updateStats(int samplesSize, double avg, double stdDev);

    boolean test();
}
