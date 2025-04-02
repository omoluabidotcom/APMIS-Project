package de.symeda.sormas.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaExpiryDto;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.PopulationDataReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;

@Path("/populationData")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({ "USER", "REST_USER" })
public class PopulationDataResource  extends EntityDtoResource {

	@GET
	@Path("/all{since}")
	public List<PopulationDataDto> getAllPopulationData(@PathParam("since") long since) {
		final Set<DistrictReferenceDto> rdto = FacadeProvider.getUserFacade().getCurrentUser().getDistricts();
		final DistrictReferenceDto rdtox = FacadeProvider.getUserFacade().getCurrentUser().getDistrict();
		List<String> result = new ArrayList<>();
			if(rdto != null && rdto.size() < 1) {
			result.add(rdtox.getUuid());
			
	
		
		}
			return FacadeProvider.getPopulationDataFacade()
					.fetchPopulationDataSelectionByUserDistricts(result);
	}

	@POST
	@Path("/query")
	public List<PopulationDataDto> getByUuids(List<String> uuids) {

		List<PopulationDataDto> result = FacadeProvider.getPopulationDataFacade()
				.fetchPopulationDataSelectionByUserDistricts(uuids);
		return result;
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		final Set<DistrictReferenceDto> rdto = FacadeProvider.getUserFacade().getCurrentUser().getDistricts();
		final DistrictReferenceDto rdtox = FacadeProvider.getUserFacade().getCurrentUser().getDistrict();

		if(rdto != null && rdto.size() > 0) {
			return FacadeProvider.getDistrictFacade().getAllUuids().stream()
					.filter(e -> rdto.stream().anyMatch(ee -> e.equals(ee.getUuid()))).collect(Collectors.toList());
		} else {
			List<String> retListx = new ArrayList<>();
			
			retListx.add(rdtox.getUuid());
			
			return retListx;
		
		}
	}
	
	@GET
	@Path("/selectedDistricts")
	public List<PopulationDataDto> fetchPopulationDataSelectionByUserDistricts() {
		List<String> retListx = getAllUuids();
		
		final Set<DistrictReferenceDto> rdto = FacadeProvider.getUserFacade().getCurrentUser().getDistricts();
		final DistrictReferenceDto rdtox = FacadeProvider.getUserFacade().getCurrentUser().getDistrict();

		System.out.println(FacadeProvider.getUserFacade().getCurrentUser() + "Request Hits Form Meta With Expiry  population data by district  ==========================111111111"+ rdto.size());
		
		List<String> resultx = new ArrayList<>();
		System.out.println("  ==========================111111111cccc"+ rdto.size());

		if(retListx != null && retListx.size() > 1) {
			for(String district : retListx) {
				resultx.add(district);
			}
			List<PopulationDataDto> result = FacadeProvider.getPopulationDataFacade()
					.fetchPopulationDataSelectionByUserDistricts(resultx);
			System.out.println("  ==========================111111111ccccvv" + result);

			return result;
			

		} else if(retListx != null && retListx.size() == 1) {

			List<PopulationDataDto> result = FacadeProvider.getPopulationDataFacade()
					.fetchPopulationDataSelectionByUserDistricts(resultx);
			System.out.println("  ==========================22221111ccccvv" + result);

			return result;
			

		}else {
			
			resultx.add(rdtox.getUuid());
			
			List<PopulationDataDto> result = FacadeProvider.getPopulationDataFacade()
					.fetchPopulationDataSelectionByUserDistricts(resultx);
			System.out.println("  ==========================333331111ccccvv" + result);

			return result;
		}
		
	}
}
