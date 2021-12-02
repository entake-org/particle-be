package io.sdsolutions.particle.core.services;

import java.util.Map;

public interface ReferenceVariablesService {

    Map<String, String> getReferences(String refType);

    Map<String, String> getOrderedReferences(String refType);

    String getValue(String key, String refType);
}
