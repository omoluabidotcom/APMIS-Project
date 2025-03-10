package de.symeda.sormas.backend.messaging;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;

import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;

@Entity(name = "messagescron")
public class MessageCron extends AbstractDomainObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9087365913720202358L;
	
	public static final String TABLE_NAME = "messagescron";
	public static final String TABLE_NAME_USERROLES = "messagescron_userroles";
	public static final String TABLE_NAME_USERTYPES = "messagescron_usertypes";
	
	public static final String MESSAGE_CONTENT = "messageContent";
	public static final String USER_ROLES = "userRoles";
	public static final String MESSAGE_FORM_ACCESS = "formAccess";
	public static final String AREA = "area";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String CHG_DATE = "chgDate";
	public static final String SCHEDULE_DATE = "scheduleDate";
	public static final String SCHEDULE_TIME = "scheduleTime";
	public static final String CREATED_BY = "creatingUser";
	
	private String messageContent;
	private Set<UserRole> userRoles;
	private Set<FormAccess> formAccess;
	private Set<Area> area;
	private Set<Region> region;
	private Set<District> district;
	private Set<Community> community;
	private User creatingUser;
	private Timestamp chgDate;
	private LocalDate scheduleDate;
	private LocalTime scheduleTime;
	
	@Column(name = "messagecontent", nullable = false)
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}	
	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "messagescron_userroles",
		joinColumns = @JoinColumn(name = "messagescron_id", referencedColumnName = MessageCron.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"messagescron_id",
			"userrole" }))
	@Column(name = "userrole", nullable = false)
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "messagescron_formaccess",
		joinColumns = @JoinColumn(name = "messagescron_id", referencedColumnName = MessageCron.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"messagescron_id",
			"formAccess" }))
	@Column(name = "formAccess", nullable = false)
	public Set<FormAccess> getFormAccess() {
		return formAccess;
	}
	public void setFormAccess(Set<FormAccess> formAccess) {
		this.formAccess = formAccess;
	}
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "messagescron_areas",
		joinColumns = @JoinColumn(name = "messagescron_id", referencedColumnName = MessageCron.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"messagescron_id",
			"area_id" }))
	@ManyToMany(cascade = {})
	public Set<Area> getArea() {
		return area;
	}
	public void setArea(Set<Area> area) {
		this.area = area;
	}
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "messagescron_region",
		joinColumns = @JoinColumn(name = "messagescron_id", referencedColumnName = MessageCron.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"messagescron_id",
			"region_id" }))
	@ManyToMany(cascade = {})
	public Set<Region> getRegion() {
		return region;
	}
	public void setRegion(Set<Region> region) {
		this.region = region;
	}
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "messagescron_district",
		joinColumns = @JoinColumn(name = "messagescron_id", referencedColumnName = MessageCron.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"messagescron_id",
			"district_id" }))
	@ManyToMany(cascade = {})
	public Set<District> getDistrict() {
		return district;
	}
	public void setDistrict(Set<District> district) {
		this.district = district;
	}	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "messagescron_community",
		joinColumns = @JoinColumn(name = "messagescron_id", referencedColumnName = MessageCron.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"messagescron_id",
			"community_id" }))
	@ManyToMany(cascade = {})
	public Set<Community> getCommunity() {
		return community;
	}
	public void setCommunity(Set<Community> community) {
		this.community = community;
	}	
	@ManyToOne
	@JoinColumn(name ="creatinguser_id")
	public User getCreatingUser() {
		return creatingUser;
	}
	public void setCreatingUser(User creatingUser) {
		this.creatingUser = creatingUser;
	}
	public Timestamp getChgDate() {
		return chgDate;
	}
	public void setChgDate(Timestamp chgDate) {
		this.chgDate = chgDate;
	}
	
	public LocalTime getScheduleTime() {
		return scheduleTime;
	}
	
	public void setScheduleTime (LocalTime scheLocalTime) {
		this.scheduleTime = scheLocalTime;
	}
	
	public LocalDate getScheduleDate() {
		return scheduleDate;
	}
	
	public void setScheduleDate (LocalDate scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	
}
