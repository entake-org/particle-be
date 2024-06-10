package io.sdsolutions.particle.smartystreets.services.impl;

import com.smartystreets.api.exceptions.SmartyException;
import com.smartystreets.api.us_street.*;
import io.sdsolutions.particle.smartystreets.model.AddressInputDTO;
import io.sdsolutions.particle.smartystreets.model.AddressResultDTO;
import io.sdsolutions.particle.smartystreets.model.CoordinatesDTO;
import io.sdsolutions.particle.smartystreets.services.AddressService;
import io.sdsolutions.particle.smartystreets.services.SmartyStreetsCodeMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    private static final int MEAN_RADIUS_OF_EARTH_IN_MILES = 3959;
    private static final int HAVERSINE_COEFFICIENT = 2;

    private final Client smartyStreetsClient;

    public AddressServiceImpl(Client smartyStreetsClient) {
        this.smartyStreetsClient = smartyStreetsClient;
    }

    @Override
    public AddressResultDTO checkAddress(AddressInputDTO dto) throws IOException, SmartyException, InterruptedException {
        Lookup lookup = getLookupForInput(dto);
        smartyStreetsClient.send(lookup);

        return getResultFromLookup(lookup);
    }

    @Override
    public List<AddressResultDTO> checkAddresses(List<AddressInputDTO> dtos) throws IOException, SmartyException, InterruptedException {
        Batch batch = new Batch();
        for(AddressInputDTO dto : dtos) {
            batch.add(getLookupForInput(dto));
        }

        smartyStreetsClient.send(batch);

        List<AddressResultDTO> results = new ArrayList<>();
        for(Lookup lookup : batch.getAllLookups()) {
            AddressResultDTO result = getResultFromLookup(lookup);
            results.add(result);
        }

        return results;
    }

    @Override
    public double distanceBetween(CoordinatesDTO coordinates1, CoordinatesDTO coordinates2) {
        /* Uses Haversine formula - http://en.wikipedia.org/wiki/Haversine_formula
		   Note: The Haversine formula does not take into account the non-spheroidal (ellipsoidal) shape of the Earth
		   neither does it consider the walk path between 2 coordinates.
		 */

        //for computation purposes we need to convert Degrees to Radians, (multiply with PI/180 OR use Math.toRadians)
        double diffLat = Math.toRadians(coordinates1.getLatitude() - coordinates2.getLatitude());
        double diffLon = Math.toRadians(coordinates1.getLongitude() - coordinates2.getLongitude());

        double intermediateResult = Math.sin(diffLat / HAVERSINE_COEFFICIENT) * Math.sin(diffLat / HAVERSINE_COEFFICIENT) +
                Math.cos(Math.toRadians(coordinates1.getLatitude())) *
                        Math.cos(Math.toRadians(coordinates2.getLatitude())) *
                        Math.sin(diffLon / HAVERSINE_COEFFICIENT) * Math.sin(diffLon / HAVERSINE_COEFFICIENT);

        return HAVERSINE_COEFFICIENT * MEAN_RADIUS_OF_EARTH_IN_MILES * Math.asin(Math.sqrt(intermediateResult));
    }

    private Lookup getLookupForInput(AddressInputDTO dto) {
        Lookup lookup = new Lookup();
        lookup.setInputId(dto.getInputId());
        lookup.setStreet(dto.getStreet());
        lookup.setStreet2(dto.getStreet2());
        lookup.setSecondary(dto.getSecondary());
        lookup.setCity(dto.getCity());
        lookup.setState(dto.getState());
        lookup.setZipCode(dto.getZipcode());
        lookup.setLastline(dto.getLastline());
        lookup.setAddressee(dto.getAddressee());
        lookup.setMaxCandidates(1);
        lookup.setMatch(MatchType.INVALID);

        return lookup;
    }

    private AddressResultDTO getResultFromLookup(Lookup lookup) {
        if(lookup.getResult().isEmpty()) {
            return null;
        }

        Candidate response = lookup.getResult(0);

        AddressResultDTO result = new AddressResultDTO();
        result.setInputId(lookup.getInputId());
        result.setAddressee(response.getAddressee());
        result.setLine1(response.getDeliveryLine1());
        result.setLine2(response.getDeliveryLine2());
        result.setPostnetBarcode(response.getDeliveryPointBarcode());

        if (response.getComponents() != null) {
            result.setCity(response.getComponents().getCityName());
            result.setState(response.getComponents().getState());
            result.setZipcode(response.getComponents().getZipCode());
            result.setZip4(response.getComponents().getPlus4Code());
            result.setUrbanization(response.getComponents().getUrbanization());
        }

        if (response.getMetadata() != null) {
            result.setCountyName(response.getMetadata().getCountyName());
            result.setCountyFipsCode(response.getMetadata().getCountyFips());
            result.setCoordinates(new CoordinatesDTO(response.getMetadata().getLatitude(), response.getMetadata().getLongitude()));
            result.setAddressType(response.getMetadata().getRecordType());
        }

        if (response.getAnalysis() != null) {
            List<String> footnotes = new ArrayList<>();
            footnotes.add(getDescriptionForCode(response.getAnalysis().getFootnotes()));

            String dpvFootnotes = response.getAnalysis().getDpvFootnotes();
            if(StringUtils.isNotBlank(dpvFootnotes)) {
                // Split the string on every 2 characters - Regex suck
                // https://stackoverflow.com/questions/2297347/splitting-a-string-at-every-n-th-character
                for (String code : dpvFootnotes.split("(?<=\\G..)")) {
                    footnotes.add(getDescriptionForCode(code));
                }
            }

            result.setMatchInformation(response.getAnalysis().getDpvMatchCode(), footnotes);
        }

        return result;
    }

    private String getDescriptionForCode(String code) {
        return SmartyStreetsCodeMap.getCodes().get(code);
    }

}
