package de.symeda.sormas.api.messaging;

import java.sql.Timestamp;

public class MessageUtil {

	public static final String MESSAGE_CONTENT = "messageContent";
	public static final String ARCHIVED = "archived";
	public static final String MESSAGE_CATEGORY = "messageCategory";
	public static final String CHG_DATE = "chgDate";
	public static final String CREATED_BY = "creatingUser";

	private String messageContent;
	private String creatingUser;
	private boolean archived;
	private MessageCategory messageCategory;
	private Timestamp chgDate;	

	public MessageUtil(String messageContent, String creatingUser, boolean archived, MessageCategory messageCategory,
			Timestamp chgDate) {
		super();
		this.messageContent = messageContent;
		this.creatingUser = creatingUser;
		this.archived = archived;
		this.messageCategory = messageCategory;
		this.chgDate = chgDate;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	
	public String getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(String creatingUser) {
		this.creatingUser = creatingUser;
	}

	public MessageCategory getMessageCategory() {
		return messageCategory;
	}

	public void setMessageCategory(MessageCategory messageCategory) {
		this.messageCategory = messageCategory;
	}

	public Timestamp getChgDate() {
		return chgDate;
	}

	public void setChgDate(Timestamp chgDate) {
		this.chgDate = chgDate;
	}

}
