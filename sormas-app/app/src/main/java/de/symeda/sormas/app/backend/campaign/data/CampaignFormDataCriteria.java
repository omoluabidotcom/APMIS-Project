package de.symeda.sormas.app.backend.campaign.data;

import java.io.Serializable;

import de.symeda.sormas.app.backend.campaign.Campaign;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.region.Community;

public class CampaignFormDataCriteria implements Serializable {

    private Campaign campaign;
    private CampaignFormMeta campaignFormMeta;
    private Community community;
    private boolean isFormMetaDistrictLevel;

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public CampaignFormMeta getCampaignFormMeta() {
        return campaignFormMeta;
    }

    public void setCampaignFormMeta(CampaignFormMeta campaignFormMeta) {
        this.campaignFormMeta = campaignFormMeta;
    }

    public Community getCommunity() {return community;}

    public void setCommunity(Community community) {this.community = community;}

    public boolean isFormMetaDistrictLevel() {
        return isFormMetaDistrictLevel;
    }

    public void setFormMetaDistrictLevel(boolean formMetaDistrictLevel) {
        isFormMetaDistrictLevel = formMetaDistrictLevel;
    }
}
