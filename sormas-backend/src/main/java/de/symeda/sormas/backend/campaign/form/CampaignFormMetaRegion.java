package de.symeda.sormas.backend.campaign.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.Modality;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaRegionReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.messaging.Message;
import de.symeda.sormas.backend.util.ModelConstants;

@Entity
@Audited
public class CampaignFormMetaRegion extends AbstractDomainObject {

	private static final long serialVersionUID = -5200626281564146919L;

	public static final String TABLE_NAME = "campaignformmeta_area";

	public static final String FORM_ID = "campaignformmeta_id";
	public static final String FORM_TYPE = "area_id";


	private String campaignformmeta_id;
	private String area_id;



	public CampaignFormMetaRegionReferenceDto toReference() {
		return new CampaignFormMetaRegionReferenceDto(campaignformmeta_id, area_id);
	}

//	@Override
//	public String toString() {
//		return formName;
//	}
	

}
