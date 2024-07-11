package org.qesm.app.model.test;

import org.apache.commons.math3.distribution.NormalDistribution;

public class ModifiedZTest extends ZTest {

    public ModifiedZTest() {
        name = "ModifiedZTest";
    }

    @Override
    public boolean test() {
        return super.test() && currentSamplesSize > getStdDev() * 2;
    }

}
