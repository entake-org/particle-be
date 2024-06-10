package io.sdsolutions.particle.smartystreets.services;

import com.smartystreets.api.exceptions.SmartyException;
import io.sdsolutions.particle.smartystreets.model.AddressInputDTO;
import io.sdsolutions.particle.smartystreets.model.AddressResultDTO;
import io.sdsolutions.particle.smartystreets.model.CoordinatesDTO;

import java.io.IOException;
import java.util.List;

public interface AddressService {

    AddressResultDTO checkAddress(AddressInputDTO dto) throws IOException, SmartyException, InterruptedException;

    List<AddressResultDTO> checkAddresses(List<AddressInputDTO> dtos) throws IOException, SmartyException, InterruptedException;

    /**
     *  Uses Haversine formula - http://en.wikipedia.org/wiki/Haversine_formula
     *  Note: The Haversine formula does not take into account the non-spheroidal (ellipsoidal) shape of the Earth
     *  neither does it consider the walk path between 2 coordinates.
     */
    double distanceBetween(CoordinatesDTO coordinates1, CoordinatesDTO coordinates2);

}
