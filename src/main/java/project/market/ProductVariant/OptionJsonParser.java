package project.market.ProductVariant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OptionJsonParser {
    private final ObjectMapper objectMapper;

    public OptionJsonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, List<String>> parseOptions(String optionsJson) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(optionsJson);
        Map<String, List<String>> result = new HashMap<>();

        node.fields().forEachRemaining(entry -> {
            List<String> values = new ArrayList<>();
            entry.getValue().forEach(v -> values.add(v.asText()));
            result.put(entry.getKey(), values);
        });

        return result;
    }

    public String toJson(Map<String, List<String>> options) throws JsonProcessingException {
        return objectMapper.writeValueAsString(options);
    }
}
