package de.symeda.sormas.api.campaign.form;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;

@Remote
public interface CampaignFormMetaExpiryFacade {

	Date formExpiryDate(CampaignFormDataCriteria criteria);

	List<CampaignFormMetaExpiryDto> getFormsWithExpiry();
	
<<<<<<< HEAD
	List<String> getAllUuids();
	
=======
	List<CampaignFormMetaExpiryDto> getFormsWithExpiryByCampaignUuidAndFormUuid(String campaignUuid, String formUuid);
	
	List<String> getAllUuids();	
>>>>>>> team_collaboration
	
    List<CampaignFormMetaExpiryDto> getAllAfter(Date campaignFormMetaChangeDate);



}
