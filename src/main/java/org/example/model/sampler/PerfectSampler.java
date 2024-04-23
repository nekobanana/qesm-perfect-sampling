package org.example.model.sampler;

import org.la4j.Matrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class PerfectSampler extends Sampler {

    public PerfectSampler(Matrix P) {
        super(P);
    }

    public void writeSequenceToFile(String fileName)
            throws IOException {
        String json = mapper.writeValueAsString(this);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(json);
        writer.close();
    }
}
