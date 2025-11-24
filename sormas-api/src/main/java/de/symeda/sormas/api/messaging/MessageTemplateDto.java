package de.symeda.sormas.api.messaging;

import java.sql.Timestamp;
import java.util.Set;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.api.utils.DataHelper;

public class MessageTemplateDto extends EntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8755227013973393504L;
	public static final String TABLE_NAME = "messagestemplate";	
	public static final String MESSAGE_CONTENT = "messageContent";
	public static final String ARCHIVED = "archived";
	public static final String MESSAGE_CATEGORY = "messageCategory";
	public static final String CHG_DATE = "chgDate";
	public static final String CREATED_BY = "creatingUser";

	private String title;
	private String messageContent;
	private String creatingUser;
	private boolean archived;
	private MessageCategory messageCategory;
	private Timestamp chgDate;

	public static MessageTemplateDto build() {
		MessageTemplateDto messageTemplateDto = new MessageTemplateDto();
		messageTemplateDto.setUuid(DataHelper.createUuid());
		return messageTemplateDto;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
