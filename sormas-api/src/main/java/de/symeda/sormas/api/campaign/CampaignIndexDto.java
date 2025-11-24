package de.symeda.sormas.api.campaign;

import java.io.Serializable;
import java.util.Date;

public class CampaignIndexDto implements Serializable {

	private static final long serialVersionUID = 2448753530580084851L; //.save

	public static final String I18N_PREFIX = "Campaign";

	public static final String UUID = "uuid";
	public static final String ROUND = "round";
	public static final String CAMPAIGN_YEAR = "campaignYear";
	public static final String NAME = "name";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
<<<<<<< HEAD
=======
	public static final String PRE_CAMP_START_DATE = "preCampStartDate";
	public static final String PRE_CAMP_END_DATE = "preCampEndDate";
	public static final String POST_CAMP_START_DATE = "postCampStartDate";
	public static final String POST_CAMP_END_DATE = "postCampEndDate";
>>>>>>> team_collaboration
	public static final String CAMPAIGN_STATUS = "campaignStatus";
	public static final String ARCHIVE = "archived";


	private String uuid;
	private String name;
	private String round;
	private String campaignYear;
	private Date startDate;
	private Date endDate;
<<<<<<< HEAD
=======
	private Date preCampStartDate;
	private Date preCampEndDate;
	private Date postCampStartDate;
	private Date postCampEndDate;
>>>>>>> team_collaboration
	private String campaignStatus;
	private String archived;

	

//	public CampaignIndexDto(String uuid, String name, boolean campaignStatus, String cluster,  String campaignYear, Date startDate, Date endDate) {
//		this.uuid = uuid;
//		this.name = name;
//		//this.round = round;
//		this.campaignYear = campaignYear;
//		this.startDate = startDate;
//		this.endDate = endDate;
//		this.campaignStatus = campaignStatus == true ? "Open" : "Closed" ;
//	}
<<<<<<< HEAD
=======
	public CampaignIndexDto(String uuid, String name, boolean campaignStatus, String cluster,  String campaignYear, Date startDate, Date endDate, Date preCampStartDate, Date preCampEndDate,  Date postCampStartDate, Date postCampEndDate, boolean archived) {
		this.uuid = uuid;
		this.name = name;
		//this.round = round;
		this.campaignYear = campaignYear;
		this.startDate = startDate;
		this.endDate = endDate;
		this.preCampStartDate = preCampStartDate;
		this.preCampEndDate = preCampEndDate;
		this.postCampStartDate = postCampStartDate;
		this.postCampEndDate = postCampEndDate;
		this.campaignStatus = campaignStatus == true ? "Open" : "Closed" ;
		this.archived = archived == true ? "Archived" : "Active" ;

	}
	
	public CampaignIndexDto(String uuid, String name, boolean campaignStatus, String cluster,  String campaignYear, Date startDate, Date endDate, Date preCampStartDate, Date preCampEndDate, boolean archived) {
		this.uuid = uuid;
		this.name = name;
		//this.round = round;
		this.campaignYear = campaignYear;
		this.startDate = startDate;
		this.endDate = endDate;
		this.preCampStartDate = preCampStartDate;
		this.preCampEndDate = preCampEndDate;
		this.campaignStatus = campaignStatus == true ? "Open" : "Closed" ;
		this.archived = archived == true ? "Archived" : "Active" ;

	}
>>>>>>> team_collaboration
	
	public CampaignIndexDto(String uuid, String name, boolean campaignStatus, String cluster,  String campaignYear, Date startDate, Date endDate, boolean archived) {
		this.uuid = uuid;
		this.name = name;
		//this.round = round;
		this.campaignYear = campaignYear;
		this.startDate = startDate;
		this.endDate = endDate;
		this.campaignStatus = campaignStatus == true ? "Open" : "Closed" ;
		this.archived = archived == true ? "Archived" : "Active" ;

	}

	public String getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(String campaignStatus) {
		this.campaignStatus = campaignStatus;
	}
	
	public String getArchived() {
		return archived;
	}

	public void setArchived(String archived) {
		this.archived = archived;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public String getCampaignYear() {
		return campaignYear;
	}

	public void setCampaignYear(String campaignYear) {
		this.campaignYear = campaignYear;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
<<<<<<< HEAD
=======

	public Date getPreCampStartDate() {
		return preCampStartDate;
	}

	public void setPreCampStartDate(Date preCampStartDate) {
		this.preCampStartDate = preCampStartDate;
	}

	public Date getPreCampEndDate() {
		return preCampEndDate;
	}

	public void setPreCampEndDate(Date preCampEndDate) {
		this.preCampEndDate = preCampEndDate;
	}

	public Date getPostCampStartDate() {
		return postCampStartDate;
	}

	public void setPostCampStartDate(Date postCampStartDate) {
		this.postCampStartDate = postCampStartDate;
	}

	public Date getPostCampEndDate() {
		return postCampEndDate;
	}

	public void setPostCampEndDate(Date postCampEndDate) {
		this.postCampEndDate = postCampEndDate;
	}
	
	
	
>>>>>>> team_collaboration
}
