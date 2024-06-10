package io.sdsolutions.particle.core.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface TransformToExcelService {

	/**
	 * Feed in a list of objects, their class, and an output stream and this method will write Excel bytes to the provided
	 * output stream.
	 * 
	 * @param dtos
	 * @param clazz
	 * @param out
	 * @throws IOException
	 */
	<E> void write(List<E> dtos, Class<E> clazz, OutputStream out, String userName) throws IOException;

}
