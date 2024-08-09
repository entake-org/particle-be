package io.entake.particle.core.services.impl;

import io.entake.particle.core.services.ReferenceVariablesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("referenceService")
public class ReferenceVariablesServiceImpl implements ReferenceVariablesService {

    private final String pairDelimiter;
    private final String keyValueDelimiter;
    private final Environment environment;

    public ReferenceVariablesServiceImpl(Environment environment) {
        this.environment = environment;
        this.pairDelimiter = this.environment.getProperty("delimiter.pair");
        this.keyValueDelimiter = this.environment.getProperty("delimiter.keyValue");
    }

    @Override
    public Map<String, String> getReferences(String refType) {
        return parseProperty(environment.getProperty(refType), false);
    }

    @Override
    public Map<String, String> getOrderedReferences(String refType) {
        return parseProperty(environment.getProperty(refType), true);
    }

    @Override
    public String getValue(String key, String refType) {
        String description = getReferences(refType).get(key);
        if(StringUtils.isBlank(description)) {
            description = "Unknown";
        }

        return description;
    }

    private Map<String, String> parseProperty(String referenceList, boolean ordered) {
        Map<String, String> references = initMap(ordered);

        if(StringUtils.isNotBlank(referenceList)) {
            if (referenceList.contains(pairDelimiter)) {
                StringTokenizer tokenizer = new StringTokenizer(referenceList, pairDelimiter, false);
                while(tokenizer.hasMoreTokens()) {
                    addReferencePair(tokenizer.nextToken(), references);
                }
            } else {
                addReferencePair(referenceList, references);
            }
        }

        return references;
    }

    private Map<String, String> initMap(boolean ordered) {
        if(ordered) {
            return new TreeMap<>();
        }

        return new LinkedHashMap<>();
    }

    private void addReferencePair(String referencePair, Map<String, String> references) {
        if(referencePair.contains(keyValueDelimiter)) {
            StringTokenizer tokenizer = new StringTokenizer(referencePair, keyValueDelimiter, false);
            references.put(tokenizer.nextToken(), tokenizer.nextToken());
        }
    }
}
