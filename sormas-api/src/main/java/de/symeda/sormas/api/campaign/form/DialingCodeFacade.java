package de.symeda.sormas.api.campaign.form;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface DialingCodeFacade {

	List<DialingCodeDto> getAllCountriesDto();
	
	DialingCodeDto getCountryByCode(String countryCode);
}
