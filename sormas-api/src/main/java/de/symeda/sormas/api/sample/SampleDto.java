package de.symeda.sormas.api.sample;

import java.util.Date;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class SampleDto extends SampleReferenceDto {

	private static final long serialVersionUID = -6975445672442728938L;

	public static final String I18N_PREFIX = "Sample";
	
	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String SAMPLE_CODE = "sampleCode";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_MATERIAL_TEXT = "sampleMaterialText";
	public static final String LAB = "lab";
	public static final String OTHER_LAB = "otherLab";
	public static final String SHIPMENT_STATUS = "shipmentStatus";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String SHIPMENT_DETAILS = "shipmentDetails";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	public static final String COMMENT = "comment";
	public static final String SAMPLE_SOURCE = "sampleSource";
	public static final String SUGGESTED_TYPE_OF_TEST = "suggestedTypeOfTest";
	
	private CaseReferenceDto associatedCase;
	private String sampleCode;
	private String labSampleID;
	private Date sampleDateTime;
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private SampleMaterial sampleMaterial;
	private String sampleMaterialText;
	private FacilityReferenceDto lab;
	private FacilityReferenceDto otherLab;
	private ShipmentStatus shipmentStatus;
	private Date shipmentDate;
	private String shipmentDetails;
	private Date receivedDate;
	private SpecimenCondition specimenCondition;
	private String noTestPossibleReason;
	private String comment;
	private SampleSource sampleSource;
	private SampleTestType suggestedTypeOfTest;
	
	public CaseReferenceDto getAssociatedCase() {
		return associatedCase;
	}
	public void setAssociatedCase(CaseReferenceDto associatedCase) {
		this.associatedCase = associatedCase;
	}
	public String getSampleCode() {
		return sampleCode;
	}
	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}
	public String getLabSampleID() {
		return labSampleID;
	}
	public void setLabSampleID(String labSampleID) {
		this.labSampleID = labSampleID;
	}
	public Date getSampleDateTime() {
		return sampleDateTime;
	}
	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}
	public Date getReportDateTime() {
		return reportDateTime;
	}
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}
	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}
	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}
	public String getSampleMaterialText() {
		return sampleMaterialText;
	}
	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}
	public FacilityReferenceDto getLab() {
		return lab;
	}
	public void setLab(FacilityReferenceDto lab) {
		this.lab = lab;
	}
	public FacilityReferenceDto getOtherLab() {
		return otherLab;
	}
	public void setOtherLab(FacilityReferenceDto otherLab) {
		this.otherLab = otherLab;
	}
	public ShipmentStatus getShipmentStatus() {
		return shipmentStatus;
	}
	public void setShipmentStatus(ShipmentStatus shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
	}
	public Date getShipmentDate() {
		return shipmentDate;
	}
	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}
	public String getShipmentDetails() {
		return shipmentDetails;
	}
	public void setShipmentDetails(String shipmentDetails) {
		this.shipmentDetails = shipmentDetails;
	}
	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}
	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}
	public String getNoTestPossibleReason() {
		return noTestPossibleReason;
	}
	public void setNoTestPossibleReason(String noTestPossibleReason) {
		this.noTestPossibleReason = noTestPossibleReason;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public SampleSource getSampleSource() {
		return sampleSource;
	}
	public void setSampleSource(SampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}
	public SampleTestType getSuggestedTypeOfTest() {
		return suggestedTypeOfTest;
	}
	public void setSuggestedTypeOfTest(SampleTestType suggestedTypeOfTest) {
		this.suggestedTypeOfTest = suggestedTypeOfTest;
	}
	
}