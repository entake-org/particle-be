package io.sdsolutions.particle.core.dozer;

import com.github.dozermapper.core.DozerConverter;
import org.springframework.stereotype.Component;

@Component("booleanStringConverter")
public class BooleanStringConverter extends DozerConverter<String, Boolean> {

	public BooleanStringConverter() {
		super(String.class, Boolean.class);
	}

	public BooleanStringConverter(Class<String> prototypeA, Class<Boolean> prototypeB) {
		super(prototypeA, prototypeB);
	}

	@Override
	public Boolean convertTo(String source, Boolean destination) {
		if (source != null) {
			if (source.toLowerCase().startsWith("y")) {
				return Boolean.TRUE;
			} else if (source.toLowerCase().startsWith("n")) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	@Override
	public String convertFrom(Boolean source, String destination) {
		return (source != null && (source)) ? "Yes" : "No";
	}
}
