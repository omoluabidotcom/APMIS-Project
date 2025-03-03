package de.symeda.sormas.api.messaging;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

@SuppressWarnings("serial")
public class MessageTemplateCriteria extends BaseCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8454377259026176092L;

	private String freeText;
	private MessageCategory messageCategory;
	private Timestamp startDate;
	private Timestamp endDate;
	private boolean archived;

	public MessageCategory getMessageCategory() {
		return messageCategory;
	}

	public void setMessageCategory(MessageCategory messageCategory) {
		this.messageCategory = messageCategory;
	}

	public MessageTemplateCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	@IgnoreForUrl
	public String getFreeText() {
		return freeText;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public MessageTemplateCriteria setStartDate(Timestamp startDate) {
		this.startDate = startDate;
		return this;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public MessageTemplateCriteria setEndDate(Timestamp endDate) {
		this.endDate = endDate;
		return this;
	}

	public boolean isArchived() {
		return archived;
	}

	public MessageTemplateCriteria setArchived(boolean archived) {
		this.archived = archived;
		return this;
	}		
	
}
