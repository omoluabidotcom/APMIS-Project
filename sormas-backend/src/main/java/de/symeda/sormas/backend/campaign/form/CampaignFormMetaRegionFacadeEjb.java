package de.symeda.sormas.backend.campaign.form;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaExpiryDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaRegionDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaRegionFacade;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CampaignFormMetaRegionFacade")
public class CampaignFormMetaRegionFacadeEjb implements CampaignFormMetaRegionFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

//	private boolean tray;

	@EJB
	private CampaignFormMetaWithExpiryService service;
	@EJB
	private UserService userService;

	public CampaignFormMetaExpDay fromDto(@NotNull CampaignFormMetaExpiryDto source, boolean checkChangeDate) {
		CampaignFormMetaExpDay target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()),
				CampaignFormMetaExpDay::new, checkChangeDate);

		System.out.println(
				"dssssssssssssssefaasdgasdgasdgasdfasdfasdfasfeasfdasdfs " + service.getByUuid(source.getUuid()));

		target.setFormId(source.getFormId());
		target.setCampaignId(source.getCampaignId());
		target.setExpiryDay(source.getExpiryDay().intValue());
		
		

		return target;
	}

	public static CampaignFormMetaExpiryDto toDto(CampaignFormMetaExpDay source) {
		if (source == null) {
			return null;
		}

		CampaignFormMetaExpiryDto target = new CampaignFormMetaExpiryDto();
		DtoHelper.fillDto(target, source);

		target.setFormId(source.getFormId());
		target.setCampaignId(source.getCampaignId());
		
		

		return target;
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllUuids() {
		String rawStr = userService.getCurrentUser().getFormAccess().toString();
		String nQuery = "select uuid from campaignformmeta where formcategory in ('"
				+ rawStr.replace("]", "").replace("[", "").replace(",", "','").replaceAll(" ", "") + "')";
		System.out.println(nQuery);
		Query campaignsStatisticsQuery = em.createNativeQuery(nQuery);
		for (Object str : campaignsStatisticsQuery.getResultList()) {
			System.out.println(str);

		}

		return campaignsStatisticsQuery.getResultList();

		// return service.getAllUuids();
	}
//	@Override
//	public List<CampaignFormMetaRegionDto> getCampaignFormsByUserRegion(List<String> uuids) {
//		  if (uuids == null || uuids.isEmpty()) {
//		        return Collections.emptyList();
//		    }
//
//		    // Base query using IN clause for multiple UUIDs
//		    String executeQuery = "SELECT campaignformmeta_id, area_id " +
//		                          "FROM campaignformmeta_areas  " +
////		                          "JOIN campaignformmeta d ON p.campaignformmeta_id = d.id " +
////		                          "left join areas c ON p.area_id = c.id " +
//		                          "WHERE c.uuid = '" + uuids.get(0) + "'";
//		    
//			System.out.println("zzzzzzDEBUGGER 5678ijhyuio" + SQLExtractor.from(em.createQuery(executeQuery)));
//
//
//		    // Create the query
//		    Query getFormExpressionsQuery = em.createNativeQuery(executeQuery);
////		    getFormExpressionsQuery.setParameter("uuids", uuids);
//
//		    
//			System.out.println("zzzzzzDEBUGGE222R 5678ijhyuio" + SQLExtractor.from(em.createQuery(executeQuery)));
//
//		    // Fetch and map the results
//		    @SuppressWarnings("unchecked")
//			List<CampaignFormMetaRegionDto> resultData = new ArrayList<>();
//
//		    List<Object[]> resultList = getFormExpressionsQuery.getResultList();
//		    
//			resultData.addAll(resultList.stream()
//					.map((result) -> new CampaignFormMetaRegionDto(
//							result[0] != null ? (String) result[0].toString() : "",	
//							result[1] != null ? (String) result[1].toString() : ""
//							)).collect(Collectors.toList()));
//			
//
//		    return resultData;
//	}
	
	
	
	@Override
	public List<CampaignFormMetaRegionDto> getCampaignFormsByUserRegion(List<String> uuids) {
	    if (uuids == null || uuids.isEmpty()) {
	        return Collections.emptyList();
	    }

	    String executeQuery = 
	        "SELECT cm.uuid as campaignformmeta_id, a.uuid as area_id , cma.uuid as uuid, cm.changedate " +
	        "FROM campaignformmeta_areas cma " +
	        "JOIN campaignformmeta cm ON cma.campaignformmeta_id = cm.id " +
	        "JOIN areas a ON cma.area_id = a.id " +
	        "WHERE a.uuid = ?1";

	    Query getFormExpressionsQuery = em.createNativeQuery(executeQuery)
	        .setParameter(1, uuids.get(0));

	    @SuppressWarnings("unchecked")
	    List<Object[]> resultList = getFormExpressionsQuery.getResultList();

	    return resultList.stream()
	        .map(result -> new CampaignFormMetaRegionDto(
	            result[0] != null ? result[0].toString() : "",
	    	    result[1] != null ? result[1].toString() : "",
	            result[2] != null ? result[2].toString() : "",
	            result[3] != null ? (Date) result[3] : null))
	        .collect(Collectors.toList());
	}
	
	
	@LocalBean
	@Stateless
	public static class CampaignFormMetaRegionFacadeEjbLocal extends CampaignFormMetaRegionFacadeEjb {
		
		public CampaignFormMetaRegionFacadeEjbLocal() {
		}
	}

	@Override
	public Date formExpiryDate(CampaignFormDataCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CampaignFormMetaRegionDto> getFormsWithExpiry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CampaignFormMetaRegionDto> getAllAfter(Date campaignFormMetaChangeDate) {
		// TODO Auto-generated method stub
		return null;
	}


//
//	@Override
//	public List<CampaignFormMetaExpiryDto> getAllAfter(Date campaignFormMetaChangeDate) {
//		// TODO Auto-generated method stub
//		return null;
//	}


}
