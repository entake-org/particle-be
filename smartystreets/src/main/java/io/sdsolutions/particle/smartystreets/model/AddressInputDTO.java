package io.sdsolutions.particle.smartystreets.model;

import org.springframework.util.StringUtils;

public class AddressInputDTO {

	private String inputId;
	private String street;
	private String street2;
	private String secondary;
	private String city;
	private String state;
	private String zipcode;
	private String lastline;
	private String addressee;
	private String urbanization;

	public AddressInputDTO() {

	}

	public AddressInputDTO(String addressLine1, String addressLine2, String city, String state, String zip) {
		this.street = addressLine1;
		this.secondary = addressLine2;
		this.city = city;
		this.state = state;
		this.zipcode = zip;
	}

	public boolean isValidInput() {
		if (StringUtils.isEmpty(street)) {
			return false;
		}

		if (StringUtils.isEmpty(zipcode)) {
			if (StringUtils.isEmpty(city) || StringUtils.isEmpty(state)) {
				if (StringUtils.isEmpty(lastline)) {
					return false;
				}
			}
		}

		return true;
	}

	public String getInputId() {
		return inputId;
	}

	public void setInputId(String inputId) {
		this.inputId = inputId;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getSecondary() {
		return secondary;
	}

	public void setSecondary(String secondary) {
		this.secondary = secondary;
	}

	public String getCity() {
		if (!StringUtils.isEmpty(lastline)) {
			return null;
		}

		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		if (!StringUtils.isEmpty(lastline)) {
			return null;
		}

		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		if (!StringUtils.isEmpty(lastline)) {
			return null;
		}

		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getLastline() {
		return lastline;
	}

	public void setLastline(String lastline) {
		this.lastline = lastline;
	}

	public String getAddressee() {
		return addressee;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public String getUrbanization() {
		return urbanization;
	}

	public void setUrbanization(String urbanization) {
		this.urbanization = urbanization;
	}

}
