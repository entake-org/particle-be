package io.sdsolutions.particle.core.dozer;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an extension of the DozerBeanMapper that adds the capability to map a List of objects,
 * rather than just a single object. This is currently configured to be brought in as the default
 * mapper.
 *
 * @author ndimola
 */
@Component
public class DozerMapperPlus {

    private final ModelMapper mapper;

	public DozerMapperPlus(ModelMapper mapper) {
	    this.mapper = mapper;
    }

    /**
     * Map a list of objects of one type to a list of objects of another type. Uses an ArrayList as the
     * list implementation under the hood.
     *
     * @param source source
     * @param clazz class
     * @param <S> source class
     * @param <D> destination class
     * @return parameterized list
     */
    public <S, D> List<D> mapList(List<S> source, Class<D> clazz) {
        List<D> destination = new ArrayList<>();

        if(source != null) {
            for (S s : source) {
                destination.add(map(s, clazz));
            }
        }

        return destination;
    }


    public <T> T map(Object source, Class<T> destinationClass, String mapId) {
        if (source == null) {
            return null;
        }

        return mapper.map(source, destinationClass, mapId);
    }

    public <T> T map(Object source, Class<T> destinationClass) {
        if (source == null) {
            return null;
        }

        return mapper.map(source, destinationClass);
    }

}
