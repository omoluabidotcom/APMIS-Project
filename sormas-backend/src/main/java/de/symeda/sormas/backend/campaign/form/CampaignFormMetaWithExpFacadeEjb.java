package de.symeda.sormas.backend.campaign.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Modality;
import de.symeda.sormas.api.campaign.CampaignPhase;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaExpiryDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaExpiryFacade;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CampaignFormMetaExpiryFacade")
public class CampaignFormMetaWithExpFacadeEjb implements CampaignFormMetaExpiryFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private boolean tray;

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



	
	@Override
	public List<CampaignFormMetaExpiryDto> getFormsWithExpiry(){
		
	
			String nQuery = "select * from campaignformmetawithexp ";
			System.out.println(nQuery);
			Query campaignsStatisticsQuery = em.createNativeQuery(nQuery);


		      List<Object[]> resultList = campaignsStatisticsQuery.getResultList();

		        // Create a list to hold the DTOs
		        List<CampaignFormMetaExpiryDto> resultData = new ArrayList<>();

		        // Iterate over the result list and create DTO objects
		        for (Object[] result : resultList) {
		            CampaignFormMetaExpiryDto dto = new CampaignFormMetaExpiryDto();
		            dto.setFormId((String) result[0]);
		            dto.setCampaignId((String) result[1]);
		            dto.setExpiryDay(((Number) result[2]).longValue());
		            dto.setEnddate((Date) result[3]);
		            dto.setUuid((String) result[0]);
		            // Add the DTO object to the list
		            resultData.add(dto);
		        }
		        
		        return resultData;


		}
	
	@Override
	public List<CampaignFormMetaExpiryDto> getFormsWithExpiryByCampaignUuidAndFormUuid(String campaignuuid, String formuuid){
		
	
			String nQuery = "select * from campaignformmetawithexp cexp where cexp.campaignid = '" + campaignuuid + "' and cexp.formid = '" + formuuid + "';";
			System.out.println(nQuery);
			Query campaignsStatisticsQuery = em.createNativeQuery(nQuery);


		      List<Object[]> resultList = campaignsStatisticsQuery.getResultList();

		        // Create a list to hold the DTOs
		        List<CampaignFormMetaExpiryDto> resultData = new ArrayList<>();

		        // Iterate over the result list and create DTO objects
		        for (Object[] result : resultList) {
		            CampaignFormMetaExpiryDto dto = new CampaignFormMetaExpiryDto();
		            dto.setFormId((String) result[0]);
		            dto.setCampaignId((String) result[1]);
		            dto.setExpiryDay(((Number) result[2]).longValue());
		            dto.setEnddate((Date) result[3]);
		            dto.setUuid((String) result[0]);
		            // Add the DTO object to the list
		            resultData.add(dto);
		        }
		        
		        return resultData;


		}



	@LocalBean
	@Stateless
	public static class CampaignFormMetaWithExpFacadeEjbLocal extends CampaignFormMetaWithExpFacadeEjb {
		
		public CampaignFormMetaWithExpFacadeEjbLocal() {
		}
	}



	@Override
	public List<CampaignFormMetaExpiryDto> getAllAfter(Date campaignFormMetaChangeDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date formExpiryDate(CampaignFormDataCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}

}
