package de.symeda.sormas.backend.campaign.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.symeda.sormas.api.campaign.form.DialingCodeDto;
import de.symeda.sormas.api.campaign.form.DialingCodeFacade;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "DialingCodeFacade")
public class DialingCodeFacadeEjb implements DialingCodeFacade {
	
	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	
	@Override
	public List<DialingCodeDto> getAllCountriesDto(){
		
		String queryStringBuilder = "select country, code, min_length, max_length from dialingcode;";
		
		Query getFormExpressionsQuery = em.createNativeQuery(queryStringBuilder);
		//
		List<DialingCodeDto> resultData = new ArrayList<>();
		
		List<Object[]> resultList = getFormExpressionsQuery.getResultList();
	
		resultData.addAll(resultList.stream()
			    .map(result -> new DialingCodeDto(
			        result[0] != null ? result[0].toString() : "",
			        result[1] != null ? result[1].toString() : "",
			        		result[2] != null ? Integer.parseInt(result[2].toString()) : 0,
			        result[3] != null ? Integer.parseInt(result[3].toString()) : 0 
			    ))
			    .collect(Collectors.toList()));

		return resultData;	
	}

	@Override
	public DialingCodeDto getCountryByCode(String countryCode) {
	    System.out.println("countryCodecountryCodecountryCode " + countryCode);
	    return em.createQuery(
	        "SELECT NEW de.symeda.sormas.api.campaign.form.DialingCodeDto(d.country, d.code, d.min_length, d.max_length) " +
	        "FROM DialingCode d WHERE d.country = :countryCode", DialingCodeDto.class)
	        .setParameter("countryCode", countryCode)
	        .getSingleResult();
	}
	
}
