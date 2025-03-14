package de.symeda.sormas.api.messaging;

import java.io.Serializable;
import java.sql.Timestamp;

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
	private Boolean archived;

	public MessageCategory getMessageCategory() {
		return messageCategory;
	}

	public MessageTemplateCriteria setMessageCategory(MessageCategory messageCategory) {
		this.messageCategory = messageCategory;
		return this;
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

	public Boolean isArchived() {
		return archived;
	}

	public MessageTemplateCriteria setArchived(Boolean archived) {
		this.archived = archived;
		return this;
	}		
	
}
