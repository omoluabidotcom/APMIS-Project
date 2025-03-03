package de.symeda.sormas.api.campaign.form;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;

@Remote
public interface CampaignFormMetaRegionFacade {

	Date formExpiryDate(CampaignFormDataCriteria criteria);

	List<CampaignFormMetaRegionDto> getFormsWithExpiry();
	
	List<String> getAllUuids();
	
	
    List<CampaignFormMetaRegionDto> getAllAfter(Date campaignFormMetaChangeDate);

    
    List<CampaignFormMetaRegionDto> getCampaignFormsByUserRegion(List<String> uuid);


}
