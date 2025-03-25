package de.symeda.sormas.backend.messaging;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.messaging.MessageCategory;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;

@Entity(name = "messagestemplate")
public class MessagesTemplate extends AbstractDomainObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9087365913720345358L;
	
	public static final String TABLE_NAME_MESSAGECATEGORY = "messagestemplate_messagecategory";
	
	public static final String TABLE_NAME = "messagestemplate";	
	public static final String MESSAGE_CONTENT = "messageContent";
	public static final String MESSAGE_CATEGORY = "messageCategory";
	public static final String ARCHIVED = "archived";
	public static final String CHG_DATE = "chgDate";
	public static final String CREATED_BY = "creatingUser";
	
	private String messageContent;
	private User creatingUser;
	private boolean archived;
	private MessageCategory messageCategory;
	private Timestamp chgDate;
	
	public String getMessageContent() {
		return messageContent;
	}
	
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
		
	@ManyToOne
	@JoinColumn(name ="creatinguser_id")
	public User getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(User creatingUser) {
		this.creatingUser = creatingUser;
	}		
	
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Enumerated(EnumType.STRING)
	@CollectionTable(name = TABLE_NAME_MESSAGECATEGORY,
	joinColumns = @JoinColumn(name = "messagestemplate_id", referencedColumnName = MessagesTemplate.ID, nullable = false),
	uniqueConstraints = @UniqueConstraint(columnNames = {
		"messagestemplate_id",
		"messagecategory" }))
	@Column(name = "messagecategory", nullable = true)
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
