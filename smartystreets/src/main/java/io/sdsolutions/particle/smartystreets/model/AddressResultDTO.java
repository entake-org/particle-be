package io.sdsolutions.particle.smartystreets.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class AddressResultDTO {

	private static final int NO_MATCH = 0;
	private static final int PERFECT_MATCH = 1;
	private static final int MATCH_WITH_ISSUES = 2;

	private String inputId;
	private String addressee;
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String zipcode;
	private String zip4;

	private String urbanization;
	private char addressType;
	private String countyFipsCode;
	private String countyName;

	private String postnetBarcode;

	private CoordinatesDTO coordinates;

	private int match;
	private List<String> matchNotes;

	public String getAddressTypeDescription() {
		switch (addressType) {
		case 'F':
			return "Full Match";
		case 'G':
			return "General Delivery - Held for Pickup at the Post Office";
		case 'H':
			return "High-rise";
		case 'P':
			return "PO Box";
		case 'R':
			return "Rural Route or Highway Contract - May have Box Number Ranges";
		case 'S':
			return "Street";
		default:
			return "N/A";
		}
	}

	public String getInputId() {
		return inputId;
	}

	public void setInputId(String inputId) {
		this.inputId = inputId;
	}

	public String getAddressee() {
		return addressee;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getZip4() {
		return zip4;
	}

	public void setZip4(String zip4) {
		this.zip4 = zip4;
	}

	public String getUrbanization() {
		return urbanization;
	}

	public void setUrbanization(String urbanization) {
		this.urbanization = urbanization;
	}

	public char getAddressType() {
		return addressType;
	}

	public void setAddressType(char addressType) {
		this.addressType = addressType;
	}

	public void setAddressType(String addressType) {
		if (!StringUtils.isEmpty(addressType)) {
			this.addressType = addressType.charAt(0);
		}
	}

	public String getCountyFipsCode() {
		return countyFipsCode;
	}

	public void setCountyFipsCode(String countyFipsCode) {
		this.countyFipsCode = countyFipsCode;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getPostnetBarcode() {
		return postnetBarcode;
	}

	public void setPostnetBarcode(String postnetBarcode) {
		this.postnetBarcode = postnetBarcode;
	}

	public CoordinatesDTO getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(CoordinatesDTO coordinates) {
		this.coordinates = coordinates;
	}

	public void setMatchInformation(String matchCode, List<String> matchNotes) {
		this.matchNotes = matchNotes;

		if (StringUtils.equalsIgnoreCase("Y", matchCode)) {
			this.match = PERFECT_MATCH;
		} else if (StringUtils.equalsIgnoreCase("S", matchCode)) {
			this.match = MATCH_WITH_ISSUES;
		} else {
			this.match = NO_MATCH;
		}
	}

	public boolean isPerfectMatch() {
		return match == PERFECT_MATCH;
	}

	public boolean isMatchWithIssues() {
		return match == MATCH_WITH_ISSUES;
	}

	public boolean isNoMatch() {
		return match == NO_MATCH;
	}

	public List<String> getMatchNotes() {
		return matchNotes;
	}
}
