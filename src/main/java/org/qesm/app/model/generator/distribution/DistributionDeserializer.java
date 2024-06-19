package org.qesm.app.model.generator.distribution;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class DistributionDeserializer extends JsonDeserializer<Distribution> {
    @Override
    public Distribution deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);

        try {
            return (Distribution) mapper.readValue(root.toString(), Class.forName(root.get("distributionType").asText()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}