package io.entake.particle.security.converter;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationPropertiesBinding
public class ListPropertyConverter implements Converter<String, List<String>> {

    @Override
    public List<String> convert(String from) {
        String[] items = from.split(",");
        List<String> list = new ArrayList<>();
        for (String item : items) {
            list.add(item.trim());
        }
        return list;
    }

}
