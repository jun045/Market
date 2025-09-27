package project.market.ProductVariant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class OptionJsonValidator {
    private final ObjectMapper objectMapper;
    private final int maxBytes;

    public OptionJsonValidator(ObjectMapper objectMapper, int maxBytes) {
        this.objectMapper = objectMapper;
        this.maxBytes = 10*1024; // 10KB 기본
    }

    //가벼운 검증: 크기 제한, JSON 문법, 객체인지 검사
    public void validateLight(String optionsJson) {
        if (optionsJson.getBytes(StandardCharsets.UTF_8).length > maxBytes) {
            throw new IllegalArgumentException("options가 너무 큽니다. 최대 " + maxBytes + " bytes");
        }
        try {
            JsonNode node = objectMapper.readTree(optionsJson); // JSON 파싱 검사
            if (!node.isObject()) {
                throw new IllegalArgumentException("options는 JSON 객체여야 합니다. 예: {\"사이즈\":[\"S\",\"M\"]}");
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("options가 올바른 JSON이 아닙니다.", e);
        }
    }

    //옵션값 중복 검증 : 각 키의 리스트 내 중복값 검사
    public void validateNoDuplicateValues(String optionsJson) {
        try {
            JsonNode node = objectMapper.readTree(optionsJson);
            if (!node.isObject()) {
                throw new IllegalArgumentException("options는 JSON 객체여야 합니다.");
            }

            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode valuesNode = entry.getValue();
                if (!valuesNode.isArray()) {
                    throw new IllegalArgumentException(key + " 옵션 값은 배열이어야 합니다.");
                }
                Set<String> valueSet = new HashSet<>();
                for (JsonNode val : valuesNode) {
                    String valStr = val.asText();
                    if (!valueSet.add(valStr)) {
                        throw new IllegalArgumentException(key + " 옵션에 중복된 값이 있습니다: " + valStr);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("options가 올바른 JSON이 아닙니다.", e);
        }
    }
}
