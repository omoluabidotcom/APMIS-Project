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

public class MessageDto extends EntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8755227013973393504L;
	public static final String TABLE_NAME = "messages";
	public static final String TABLE_NAME_USERROLES = "messages_userroles";
	public static final String TABLE_NAME_USERTYPES = "messages_usertypes";
	public static final String TABLE_NAME_COMMUNITY = "messages_community";
	
	public static final String MESSAGE_CONTENT = "messageContent";
	public static final String USER_ROLES = "userRoles";
	public static final String FORM_ACCESS = "formAccess";
	public static final String AREA = "area";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String COMMUNITY_NOS = "communitynos";
	public static final String CHG_DATE = "chgDate";
	public static final String STATUS = "status";
	public static final String CREATED_BY = "creatingUser";
	
	private String title;
	private String messageContent;
	private Set<UserRole> userRoles;
	private Set<FormAccess> formAccess;
	private Set<AreaReferenceDto> area;
	private Set<RegionReferenceDto> region;
	private Set<DistrictReferenceDto> district;			
	private Set<CommunityReferenceDto> community;
	private Timestamp chgDate;
	private Status status;
	private String creatingUser;
	
	public static MessageDto build() {
		MessageDto messageDto = new MessageDto();
		messageDto.setUuid(DataHelper.createUuid());
		return messageDto;
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
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
	public Set<FormAccess> getFormAccess() {
		return formAccess;
	}

	public void setFormAccess(Set<FormAccess> formAccess) {
		this.formAccess = formAccess;
	}
	public Set<AreaReferenceDto> getArea() {
		return area;
	}
	public void setArea(Set<AreaReferenceDto> area) {
		this.area = area;
	}
	public Set<RegionReferenceDto> getRegion() {
		return region;
	}
	public void setRegion(Set<RegionReferenceDto> region) {
		this.region = region;
	}
	public Set<DistrictReferenceDto> getDistrict() {
		return district;
	}
	public void setDistrict(Set<DistrictReferenceDto> district) {
		this.district = district;
	}
	public Set<CommunityReferenceDto> getCommunity() {
		return community;
	}
	public void setCommunity(Set<CommunityReferenceDto> community) {
		this.community = community;
	}		
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getCreatingUser() {
		return creatingUser;
	}
	public void setCreatingUser(String creatingUser) {
		this.creatingUser = creatingUser;
	}	
	public Timestamp getChgDate() {
		return chgDate;
	}
	public void setChgDate(Timestamp chgDate) {
		this.chgDate = chgDate;
	}
	
}
