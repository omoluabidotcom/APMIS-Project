package com.cinoteck.application.views.campaigndata;

import static de.symeda.sormas.api.campaign.ExpressionProcessorUtils.refreshEvaluationContext;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Objects;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.jsoup.select.Evaluator.IsEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.vaadin.addons.taefi.component.ToggleButtonGroup;

import com.cinoteck.application.UserProvider;
import com.google.common.collect.Sets;

//import org.hibernate.internal.build.AllowSysOut;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.VaadinService;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.MapperUtil;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.campaign.data.translation.TranslationElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementStyle;
import de.symeda.sormas.api.campaign.form.CampaignFormElementOptions;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaExpiryDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.campaign.form.DialingCodeDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;

import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;

import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserActivitySummaryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;

public class CampaignFormBuilder extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7153373681106891254L;
	private final List<CampaignFormElement> formElements;
	private final Map<String, Object> formValuesMap;
	// private final FormLayout campaignFormLayout;
	private final Locale userLocale;
	private Map<String, String> userTranslations = new HashMap<String, String>();
	private Map<String, String> userOptTranslations = new HashMap<String, String>();
	Map<String, Component> fields;

	private Map<String, String> optionsValues = new HashMap<String, String>();
	private Map<String, String> optionsOrder = new HashMap<String, String>();

	private List<String> constraints;
	private List<CampaignFormTranslations> translationsOpt;
	private CampaignReferenceDto campaignReferenceDto;

	private List<PopulationDataDto> popDto;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private boolean isDistrictEntry;
	private CampaignFormMetaReferenceDto campaignFormMeta;

	List<AreaReferenceDto> regions;
	List<RegionReferenceDto> provinces;
	List<DistrictReferenceDto> districts;
	List<CommunityReferenceDto> communities;
	Binder<CampaignFormDataDto> binder = new BeanValidationBinder<>(CampaignFormDataDto.class);
	private UserProvider currentUser = new UserProvider();

	private ExpressionProcessor expressionProcessor;

	private final ExpressionParser expressionParser = new SpelExpressionParser();

	private boolean invalidForm = false;

	private boolean openedOnce = false;

	ComboBox<Object> cbCampaign = new ComboBox<>(I18nProperties.getCaption(Captions.Campaign));

	ComboBox<AreaReferenceDto> cbArea = new ComboBox<>(I18nProperties.getCaption(Captions.area));
	ComboBox<RegionReferenceDto> cbRegion = new ComboBox<>(I18nProperties.getCaption(Captions.region));
	ComboBox<DistrictReferenceDto> cbDistrict = new ComboBox<>(I18nProperties.getCaption(Captions.district));
	ComboBox<CommunityReferenceDto> cbCommunity = new ComboBox<>(I18nProperties.getCaption(Captions.community));
	Button reassignDataConfigUnit = new Button(I18nProperties.getCaption("Reassign Data"));

	FormLayout vertical = new FormLayout();
	HorizontalLayout reassigmentLayout = new HorizontalLayout();
	Button updateFormDataUnitAssignment = new Button("Update Form Data Unit");
	Button cancelFormDataUnitAssignment = new Button("Cancel");

	TextField formDate = new TextField();
	private boolean openData = false;
	private String uuidForm;
	private boolean checkDistrictEntry = false;
	private String formName;
	private DialingCodeDto dialingCodeDto = new DialingCodeDto();
	private int min = 0;
	private int max = 0;
	private String currentDay = "Day-1";
	private boolean daywiseTracker = false;

	private CampaignDto campaignDto;
	CampaignFormMetaExpiryDto expiryDto;

	DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

	public CampaignFormBuilder(List<CampaignFormElement> formElements, List<CampaignFormDataEntry> formValues,
			CampaignReferenceDto campaignReferenceDto, List<CampaignFormTranslations> translations, String formName,
			CampaignFormMetaReferenceDto campaignFormMetaUUID, boolean openData, String uuidForm,
			boolean isDistrictEntry, CampaignDto campaignDto, CampaignFormMetaExpiryDto expiryDto) {

		logger.debug("+++++++++++CampaignFormBuilder+++++: " + openData);

		this.openData = openData;
		this.uuidForm = uuidForm;
		this.formElements = formElements;
		this.campaignReferenceDto = campaignReferenceDto;
		this.campaignFormMeta = campaignFormMetaUUID;
		this.isDistrictEntry = isDistrictEntry;
		this.campaignDto = campaignDto;
		this.expiryDto = expiryDto;
		this.formName = formName;
		if (formValues != null) {
			this.formValuesMap = new HashMap<>();
			formValues.forEach(formValue -> formValuesMap.put(formValue.getId(), formValue.getValue()));
		} else {
			this.formValuesMap = new HashMap<>();
		}
		// this.campaignFormLayout = new FormLayout();
		this.fields = new HashMap<>();
		this.translationsOpt = translations;

		UserProvider userProvider = new UserProvider();
		I18nProperties.setUserLanguage(userProvider.getUser().getLanguage());
		this.userLocale = I18nProperties.getUserLanguage().getLocale();

//		logger.debug(userProvider.getUser().getLanguage() +" : I18nProperties.getUserLanguage().getLocale(): "+I18nProperties.getUserLanguage().getLocale());

		if (userLocale != null) {
			if (translations != null) {
				translations.stream().filter(t -> t.getLanguageCode().equals(userLocale.toString())).findFirst()
						.ifPresent(filteredTranslations -> userTranslations = filteredTranslations.getTranslations()
								.stream().collect(Collectors.toMap(TranslationElement::getElementId,
										TranslationElement::getCaption)));
			}
		}

		FormLayout vertical_ = new FormLayout();
		VerticalLayout formNameLabelLayout = new VerticalLayout();
		Label formNam = new Label();
		formNam.getElement().setProperty("innerHTML", "<h3>" + formName + "</h3>");
		formNameLabelLayout.add(formNam);
		vertical_.setColspan(formNameLabelLayout, 3);
		vertical_.add(formNameLabelLayout);

		cbCampaign.setItems(FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference());
		cbCampaign.setValue(campaignReferenceDto);
		cbCampaign.setRequired(true);
		cbCampaign.setReadOnly(true);
		cbCampaign.setId("my-disabled-textfield");
		cbCampaign.getStyle().set("-webkit-text-fill-color", "green !important");

		formDate.setLabel(I18nProperties.getCaption(Captions.CampaignFormData_formDate));
		LocalDate today = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String formattedDate = today.format(formatter);

		formDate.setValue(formattedDate);
		formDate.setRequired(true);
//		formDate.setId("my-disabled-textfield");
		formDate.getStyle().set("-webkit-text-fill-color", "green !important");
		
		formDate.setErrorMessage(""); // Initialize error message
		formDate.setInvalid(false); 

		formDate.addValueChangeListener(e -> {
			
			System.out.println(validateTextInputFormDate(e.getValue()) + "validateTextInputFormDate(e.getValue())validateTextInputFormDate(e.getValue())");
			validateTextInputFormDate(e.getValue());
			});

		//

		popDto = FacadeProvider.getPopulationDataFacade().getPopulationDataWithCriteria(campaignReferenceDto.getUuid());

		logger.debug("++++++++++++++++++++++++++++++++" + popDto.size());

		cbArea = new ComboBox<>(I18nProperties.getCaption(Captions.area));
		cbArea.setRequired(true);
		ArrayList<String> areaNamesExtract = new ArrayList<String>();
		List<AreaReferenceDto> areaNamesExtractFinal = new ArrayList<>();
		List<AreaReferenceDto> selectedAreas = FacadeProvider.getAreaFacade().getAllSelectedAreasByFormUuidAndLocale(
				campaignFormMetaUUID.getUuid(), userProvider.getUser().getLanguage().toString());

		System.out.println("222dtodtodtodtodtodtodtodtodtodtodtodtodtodto------------------------"
				+ FacadeProvider.getAreaFacade().getAllSelectedAreasByFormUuidAndLocale(campaignFormMetaUUID.getUuid(),
						userProvider.getUser().getLanguage().toString()).size());

		if (FacadeProvider.getAreaFacade().getAllSelectedAreasByFormUuidAndLocale(campaignFormMetaUUID.getUuid(),
				userProvider.getUser().getLanguage().toString()).size() > 0) {

			if (!selectedAreas.isEmpty()) {
				String lang = userProvider.getUser().getLanguage().toString();

				if (lang.equals("Pashto")) {
					cbArea.setItems(FacadeProvider.getAreaFacade()
							.getAllSelectedAreasByFormUuidAndLocale(campaignFormMetaUUID.getUuid(), "Pashto"));
				} else if (lang.equals("Dari")) {
					cbArea.setItems(FacadeProvider.getAreaFacade()
							.getAllSelectedAreasByFormUuidAndLocale(campaignFormMetaUUID.getUuid(), "Dari"));
				} else {
					// English or default
					for (AreaReferenceDto dto : FacadeProvider.getAreaFacade()
							.getAllSelectedAreasByFormUuidAndLocale(campaignFormMetaUUID.getUuid(), "English")) {
						areaNamesExtract.add(dto.getCaption());
					}

					for (String name : areaNamesExtract) {
						areaNamesExtractFinal.addAll(FacadeProvider.getAreaFacade().getByName(name, false));
					}

					cbArea.setItems(areaNamesExtractFinal);
				}
			}

		} else {
			cbArea.setItems(new ArrayList<AreaReferenceDto>());

		}
		cbArea.setId("my-disabled-textfield");
		cbArea.getStyle().set("-webkit-text-fill-color", "green !important");

		cbRegion = new ComboBox<>(I18nProperties.getCaption(Captions.region));
		cbRegion.setReadOnly(true);
		;
		cbRegion.setRequired(true);
		cbRegion.setId("my-disabled-textfield");
		cbRegion.getStyle().set("-webkit-text-fill-color", "green !important");

		cbDistrict = new ComboBox<>(I18nProperties.getCaption(Captions.district));
		cbDistrict.setReadOnly(true);
		cbDistrict.setRequired(true);
		cbDistrict.setId("my-disabled-textfield");
		cbDistrict.getStyle().set("-webkit-text-fill-color", "green !important");

		cbCommunity = new ComboBox<>(I18nProperties.getCaption(Captions.community));
		cbCommunity.setReadOnly(true);
		cbCommunity.setRequired(true);
		cbCommunity.setId("my-disabled-textfield");
		cbCommunity.getStyle().set("-webkit-text-fill-color", "green !important");
		Label cbLabel = new Label(cbCommunity.getLabel());
		cbLabel.addClassName("my-custom-label-style");

		// listeners logic
		cbArea.addValueChangeListener(e -> {
			if (e.getValue() != null) {

				if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
					provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaPashto(e.getValue().getUuid());
				} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
					provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaDari(e.getValue().getUuid());
				} else {

					List<RegionReferenceDto> regionsList = FacadeProvider.getRegionFacade()
							.getAllActiveByArea(e.getValue().getUuid());
					List<RegionReferenceDto> allRegionList = new ArrayList<>();

					popDto.forEach(popDtoc -> allRegionList.add(popDtoc.getRegion()));

					List<RegionReferenceDto> filteredRegionListwithDup = regionsList.stream()
							.filter(allRegionList::contains).collect(Collectors.toList());

					// Remove duplicates using Set
					Set<RegionReferenceDto> uniqueSet = new HashSet<>(filteredRegionListwithDup);

					// Convert the set back to a list (if needed)
					List<RegionReferenceDto> filteredRegionList = new ArrayList<>(uniqueSet);

					provinces = filteredRegionList;// FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid());

				}

				cbRegion.clear();
				cbRegion.setReadOnly(false);
				cbRegion.setItems(provinces);
				cbDistrict.clear();
				cbDistrict.setReadOnly(true);
				cbCommunity.clear();
				cbCommunity.setReadOnly(true);
			} else {
				cbRegion.clear();
				cbRegion.setReadOnly(true);
				cbDistrict.clear();
				cbDistrict.setReadOnly(true);
				cbCommunity.clear();
				cbCommunity.setReadOnly(true);
			}

		});

		cbRegion.addValueChangeListener(e -> {
			if (e.getValue() != null) {

				if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
					districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionPashto(e.getValue().getUuid());
				} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
					districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionDari(e.getValue().getUuid());
				} else {
					List<DistrictReferenceDto> districtsList = FacadeProvider.getDistrictFacade()
							.getAllActiveByRegion(e.getValue().getUuid());

					System.out.println(districtsList
							+ "districtsListdistrictsListdistrictsListdistrictsList============================");
					List<DistrictReferenceDto> allDistrictList = new ArrayList<>();

					popDto.forEach(popDtoc -> allDistrictList.add(popDtoc.getDistrict()));

					System.out.println(allDistrictList
							+ "allDistrictListallDistrictListallDistrictList=============2222222222222222222222");

					List<DistrictReferenceDto> filteredDistrictListwithDup = districtsList.stream()
							.filter(allDistrictList::contains).collect(Collectors.toList());

					// Remove duplicates using Set
					Set<DistrictReferenceDto> uniqueSet = new HashSet<>(filteredDistrictListwithDup);

					// Convert the set back to a list (if needed)
					List<DistrictReferenceDto> filteredDistrictList = new ArrayList<>(uniqueSet);
					System.out.println(filteredDistrictList
							+ "filteredDistrictListfilteredDistrictListfilteredDistrictList=============3333333333333333");

					districts = filteredDistrictList;
//					districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid());
				}
				cbDistrict.setReadOnly(false);

				cbDistrict.setItems(districts);
				cbCommunity.clear();
				cbCommunity.setReadOnly(true);
			} else {
				cbDistrict.clear();
				cbDistrict.setReadOnly(true);
				cbCommunity.clear();
				cbCommunity.setReadOnly(true);
			}

		});

//		logger.debug(checkDistrictEntry + "checkingggggggggggggggggggggggggggggg" + campaignFormMetaDto);
		if (isDistrictEntry) {
			cbDistrict.addValueChangeListener(e -> {
				if (e.getValue() != null) {
					communities = FacadeProvider.getCommunityFacade().getAllActiveByDistrict(e.getValue().getUuid());
					cbCommunity.clear();
					cbCommunity.setReadOnly(false);
					;
					communities.sort(Comparator.comparingInt(CommunityReferenceDto::getNumber));

					cbCommunity.setItems(communities);
					cbCommunity.setValue(communities.get(0));
					cbCommunity.setItemLabelGenerator(itm -> {
						CommunityReferenceDto dcfv = (CommunityReferenceDto) itm;
						return dcfv.getNumber() + " | " + dcfv.getCaption();
					});

//					CampaignReferenceDto campaignReferenceDto = (CampaignReferenceDto) cbCampaign.getValue();

					logger.debug(e.getValue().getUuid() + "11111111-------- " + campaignReferenceDto.getUuid()
							+ " ----!!!!!!!!!!!!!!!!!!!!!!: " + AgeGroup.AGE_0_4);

					Integer comdto = FacadeProvider.getPopulationDataFacade().getDistrictPopulationByType(
							e.getValue().getUuid(), campaignReferenceDto.getUuid(), AgeGroup.AGE_0_4);

					logger.debug(" ========================== " + campaignReferenceDto.getUuid());

					VaadinService.getCurrentRequest().getWrappedSession().setAttribute("populationdata", comdto);
				} else {
					cbCommunity.clear();
					cbCommunity.setReadOnly(true);
					;
				}
			});
		} else {
			cbDistrict.addValueChangeListener(e -> {
				if (e.getValue() != null) {
					communities = FacadeProvider.getCommunityFacade().getAllActiveByDistrict(e.getValue().getUuid());
					cbCommunity.clear();
					cbCommunity.setReadOnly(false);
					;
					communities.sort(Comparator.comparingInt(CommunityReferenceDto::getNumber));

					cbCommunity.setItems(communities);
					cbCommunity.setItemLabelGenerator(itm -> {
						CommunityReferenceDto dcfv = (CommunityReferenceDto) itm;
						return dcfv.getNumber() + " | " + dcfv.getCaption();
					});

					logger.debug(
							e.getValue().getUuid() + "11111111xxxxxxxxxxxx-------- " + campaignReferenceDto.getUuid()
									+ " ----!!!!!!xxxxxxxxxxxxxxxxx!!!!!!!!!!!!!!!!: " + AgeGroup.AGE_0_4);

					Integer comdto = FacadeProvider.getPopulationDataFacade().getDistrictPopulationByType(
							e.getValue().getUuid(), campaignReferenceDto.getUuid(), AgeGroup.AGE_0_4);

					logger.debug(" ============xxxxxxxxxxxxx============== " + campaignReferenceDto.getUuid());

					VaadinService.getCurrentRequest().getWrappedSession().setAttribute("populationdata", comdto);
				} else {
					cbCommunity.clear();
					cbCommunity.setReadOnly(true);
				}
			});
		}
		cbCommunity.addValueChangeListener(e -> {

			if (!openData) {
				if (cbCommunity.getValue() != null && cbDistrict.getValue() != null && !openedOnce) {
					openedOnce = true;
					CampaignFormMetaDto campaignForm = FacadeProvider.getCampaignFormMetaFacade()
							.getCampaignFormMetaByUuid(campaignFormMeta.getUuid());

					CampaignDto campaign = FacadeProvider.getCampaignFacade().getByUuid(campaignReferenceDto.getUuid());

					CommunityReferenceDto community = (CommunityReferenceDto) cbCommunity.getValue();

					CommunityDto comdto = FacadeProvider.getCommunityFacade().getByUuid(community.getUuid());

					String formuuid = FacadeProvider.getCampaignFormDataFacade().getByClusterDropDown(community,
							campaignForm, campaign);

					VaadinService.getCurrentRequest().getWrappedSession().setAttribute("Clusternumber",
							comdto.getExternalId());
					VaadinService.getCurrentRequest().getWrappedSession().setAttribute("Clusternumber",
							comdto.getExternalId());
//				
					logger.debug(comdto.getExternalId() + "?comdto.getExternalId() going to session |" + formuuid
							+ "| >>>>>>" + comdto.getClusterNumber());
//				
					if (campaignForm.getFormCategory() == FormAccess.ADMIN
							|| campaignForm.getFormCategory() == FormAccess.MODALITY_PRE
							|| campaignForm.getFormCategory() == FormAccess.MODALITY_POST) {
						if (!formuuid.equals("nul")) {

							CampaignFormDataDto formData = FacadeProvider.getCampaignFormDataFacade()
									.getCampaignFormDataByUuid(formuuid);

							if (formData.getFormValues() != null) {

								formData.getFormValues().forEach(
										formValue -> formValuesMap.put(formValue.getId(), formValue.getValue()));
							}

							// setFormValues(formData.getFormValues());
							remove(vertical);
							buildForm(false);
							vertical.setVisible(true);

						} else {
							buildForm(true);
							vertical.setVisible(true);
						}
					} else {
						buildForm(true);
						vertical.setVisible(true);
					}
				}

			}

		});

		Icon cancelIcon = VaadinIcon.EXCLAMATION_CIRCLE_O.create();
		cancelIcon.getStyle().set("color", "red !important");

		cancelFormDataUnitAssignment.setIcon(cancelIcon);
		cancelFormDataUnitAssignment.addThemeVariants(ButtonVariant.LUMO_ERROR);

		updateFormDataUnitAssignment.setIcon(VaadinIcon.CHECK_CIRCLE_O.create());

		updateFormDataUnitAssignment.setVisible(false);
		cancelFormDataUnitAssignment.setVisible(false);

		reassignDataConfigUnit.addClickListener(e -> {
			cbCommunity.setReadOnly(false);
			updateFormDataUnitAssignment.setVisible(true);
			cancelFormDataUnitAssignment.setVisible(true);

		});

		this.openData = openData;
		this.uuidForm = uuidForm;
//		this.formElements = formElements;
		this.campaignReferenceDto = campaignReferenceDto;
		this.campaignFormMeta = campaignFormMetaUUID;
		this.isDistrictEntry = isDistrictEntry;
		this.formName = formName;

		cancelFormDataUnitAssignment.addClickListener(e -> {
			if (updateFormDataUnitAssignment.isVisible() || cbCommunity.isEnabled()) {
				updateFormDataUnitAssignment.setVisible(false);
				cbCommunity.setReadOnly(true);
				cancelFormDataUnitAssignment.setVisible(false);
			}

		});

		updateFormDataUnitAssignment.addClickListener(e -> {

			try {
				FacadeProvider.getCampaignFormDataFacade().updateFormDataUnitAssignment(uuidForm,
						cbCommunity.getValue().getUuid().toString());
			} catch (Exception ex) {

			} finally {

				Notification.show("Form Configuration Unit Updated Succesfully");
				cbCommunity.setReadOnly(true);
				updateFormDataUnitAssignment.setVisible(false);
				cancelFormDataUnitAssignment.setVisible(false);

				UserActivitySummaryDto userActivitySummaryDto = new UserActivitySummaryDto();
				userActivitySummaryDto.setActionModule("Population Data Import");
				userActivitySummaryDto
						.setAction("User Updated Form Data Cluster Assignment " + cbCampaign.getValue().toString());
				UserProvider usr = new UserProvider();

				userActivitySummaryDto.setCreatingUser_string(usr.getUser().getUserName());
				FacadeProvider.getUserFacade().saveUserActivitySummary(userActivitySummaryDto);

			}
		});

		System.out.println(isDistrictEntry + "campaignFormBuildercampaignFormBuildercampaignFormBuilder");

		if (!isDistrictEntry) {
			if (userProvider.hasUserRight(UserRight.REASSIGN_CAMPAIGN_FORM_DATA_CLUSTER)) {
				reassigmentLayout.add(reassignDataConfigUnit, updateFormDataUnitAssignment,
						cancelFormDataUnitAssignment);

			}

		} else {
			cbCommunity.setVisible(false);
		}

		if (uuidForm != null) {

			if (userProvider.hasUserRight(UserRight.REASSIGN_CAMPAIGN_FORM_DATA_CLUSTER)) {
//			if (currentUser.getUserRoles().contains(UserRole.ADMIN)
//					|| currentUser.getUserRoles().contains(UserRole.COMMUNITY_INFORMANT)) {
				System.out.println(isDistrictEntry + "campaignFormBuildercampaignFormBuildercampaignFormBuilder");

//				
//					vertical_.add(cbCampaign, formDate, cbArea, cbRegion, cbDistrict, cbCommunity);
//
//				}else {
				vertical_.add(cbCampaign, formDate, cbArea, cbRegion, cbDistrict, cbCommunity, reassigmentLayout);

//				}
			} else {
				vertical_.add(cbCampaign, formDate, cbArea, cbRegion, cbDistrict, cbCommunity);

			}

		} else {
			vertical_.add(cbCampaign, formDate, cbArea, cbRegion, cbDistrict, cbCommunity);

		}

		vertical_.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("520px", 2),
				new ResponsiveStep("1000px", 3));
		add(vertical_);

		if (userProvider.getUser().getArea() != null) {
			AreaReferenceDto singleArea = userProvider.getUser().getArea();
			AreaDto singleAreaDto = FacadeProvider.getAreaFacade().getByUuid(singleArea.getUuid());

			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				AreaReferenceDto singleAreatw0 = new AreaReferenceDto(singleAreaDto.getUuid(),
						singleAreaDto.getPs_af());
				cbArea.setValue(singleAreatw0);
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				AreaReferenceDto singleAreatw0 = new AreaReferenceDto(singleAreaDto.getUuid(),
						singleAreaDto.getFa_af());
				cbArea.setValue(singleAreatw0);
			} else {
				cbArea.setValue(userProvider.getUser().getArea());
			}

			// rda56kGbCAja
			cbArea.setReadOnly(true);
			;

			List<RegionReferenceDto> provinces = FacadeProvider.getRegionFacade()
					.getAllActiveByArea(userProvider.getUser().getArea().getUuid());
			cbRegion.clear();
			cbRegion.setReadOnly(false);
			;
			cbRegion.setItems(provinces);
		}

		if (userProvider.getUser().getRegion() != null) {
			RegionReferenceDto singleRegion = userProvider.getUser().getRegion();
			RegionDto singleRegionDto = FacadeProvider.getRegionFacade().getByUuid(singleRegion.getUuid());

			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				RegionReferenceDto singleRegiontw0 = new RegionReferenceDto(singleRegionDto.getUuid(),
						singleRegionDto.getPs_af());
				cbRegion.setValue(singleRegiontw0);
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				RegionReferenceDto singleRegiontw0 = new RegionReferenceDto(singleRegionDto.getUuid(),
						singleRegionDto.getFa_af());
				cbRegion.setValue(singleRegiontw0);
			} else {
				cbRegion.setValue(userProvider.getUser().getRegion());
			}

			cbRegion.setReadOnly(true);
			;

			List<DistrictReferenceDto> districts = FacadeProvider.getDistrictFacade()
					.getAllActiveByRegion(userProvider.getUser().getRegion().getUuid());
			cbDistrict.clear();
			cbDistrict.setReadOnly(false);
			;
			cbDistrict.setItems(districts);
		}

		if (userProvider.getUser().getDistrict() != null) {
			DistrictReferenceDto singleDistrict = userProvider.getUser().getDistrict();
			DistrictDto singleDistrictDto = FacadeProvider.getDistrictFacade().getByUuid(singleDistrict.getUuid());

			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				DistrictReferenceDto singleDistricttw0 = new DistrictReferenceDto(singleDistrictDto.getUuid(),
						singleDistrictDto.getPs_af());
				cbDistrict.setValue(singleDistricttw0);
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				DistrictReferenceDto singleDistricttw0 = new DistrictReferenceDto(singleDistrictDto.getUuid(),
						singleDistrictDto.getFa_af());
				cbDistrict.setValue(singleDistricttw0);
			} else {
				cbDistrict.setValue(userProvider.getUser().getDistrict());
			}

			cbDistrict.setReadOnly(true);
			;

			List<CommunityReferenceDto> communities = FacadeProvider.getCommunityFacade()
					.getAllActiveByDistrict(userProvider.getUser().getDistrict().getUuid());

			communities.sort(Comparator.comparingInt(CommunityReferenceDto::getNumber));

			cbCommunity.clear();
			cbCommunity.setReadOnly(false);
			;

			cbCommunity.setItems(communities);

			cbCommunity.setItemLabelGenerator(itm -> {
				CommunityReferenceDto dcfv = (CommunityReferenceDto) itm;
				return dcfv.getNumber() + " | " + dcfv.getCaption();
			});
		}

		if (userProvider.getUser().getCommunity() != null) {

			cbCommunity.clear();

			List<CommunityReferenceDto> items = userProvider.getUser().getCommunity().stream()
					.collect(Collectors.toList());

			for (CommunityReferenceDto item : items) {
				item.setCaption(item.getNumber() != null ? item.getNumber().toString() : item.getCaption());
			}

//			System.out.println(item  +  " Item caption ");

			System.out.println(items + " items from form builder ");
			System.out.println(CommunityReferenceDto.clusternumber + " items from form builder ");

//			Collections.sort(items, CommunityReferenceDto.clusternumber);
			items.sort(Comparator.comparingInt(CommunityReferenceDto::getNumber));

			cbCommunity.setItems(items);
		}

		if (openData) {
			CampaignFormDataDto formData = FacadeProvider.getCampaignFormDataFacade()
					.getCampaignFormDataByUuid(uuidForm);

			System.out.println(formData + "checking if formm data is null fom form builder");

			LocalDate localDate = formData.getFormDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			System.out.println(localDate + "checking if localDate is null fom form builder" + formData.getFormDate());

			DateTimeFormatter formatterx = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDatex = localDate.format(formatterx);

			formDate.setValue(formattedDatex);
			cbArea.clear();
			cbArea.setValue(formData.getArea());
			cbRegion.clear();
			cbRegion.setValue(formData.getRegion());
			cbDistrict.clear();
			cbDistrict.setValue(formData.getDistrict());
			cbCommunity.clear();
			cbCommunity.setValue(formData.getCommunity());

			if (formData.getFormValues() != null) {
				System.out.println("gggggggggggggggggggggggggggggg");
				formData.getFormValues().forEach(formValue -> {
					formValuesMap.put(formValue.getId(), formValue.getValue());
					System.out.println(
							"formValue.getId() " + formValue.getId() + " formValue.getValue() " + formValue.getValue());
				});
			}

			buildForm(false);
			vertical.setVisible(true);

			cbArea.setReadOnly(true);
			;
			cbRegion.setReadOnly(true);
			;
			cbDistrict.setReadOnly(true);
			;
			cbCommunity.setReadOnly(true);
			;
			formDate.setReadOnly(true);
			;

		}

	}

	public void buildForm(boolean isNewForm) {
		int currentCol = -1;
		int sectionCount = 0;

		int ii = 0;

		TabSheet accrd = new TabSheet();
		
		accrd.addSelectedChangeListener(event -> {
			Tab selectedTab = accrd.getSelectedTab();
			if (selectedTab != null) {
				currentDay = selectedTab.getLabel().toLowerCase().replace("-", "");
				System.out.println("SWITCHHHHHHHHHHHHHHHHHHH " + currentDay.toLowerCase() + " ENDDDDDDDDDDDDDDDDDDDDDDDD");
			}
		});
		accrd.setHeight(750, Unit.PIXELS);

		int accrd_count = 0;

		Date formEndDate = FacadeProvider.getCampaignFormMetaWithExpFacade()
				.getFormExpiryByCampaignAndFormUuid(campaignDto.getUuid(), campaignFormMeta.getUuid());

		for (CampaignFormElement formElement : formElements) {

			if (formElement.getType() != null) {
				CampaignFormElementOptions campaignFormElementOptions = new CampaignFormElementOptions();
				CampaignFormElementType type = CampaignFormElementType.fromString(formElement.getType());
				String fieldId = formElement.getId();
				List<CampaignFormElementStyle> styles;
				if (formElement.getStyles() != null) {
					styles = Arrays.stream(formElement.getStyles()).map(CampaignFormElementStyle::fromString)
							.collect(Collectors.toList());
				} else {
					styles = new ArrayList<>();
				}

				if (formElement.getOptions() != null) {
					campaignFormElementOptions = new CampaignFormElementOptions();

					optionsValues = formElement.getOptions().stream()
							.collect(Collectors.toMap(MapperUtil::getKey, MapperUtil::getCaption));

					if (userLocale != null) {
						if (translationsOpt != null) {
							translationsOpt.stream().filter(t -> t.getLanguageCode().equals(userLocale.toString()))
									.findFirst()
									.ifPresent(filteredTranslations -> filteredTranslations.getTranslations().stream()
											.filter(cd -> cd.getElementId().equals(formElement.getId())).findFirst()
											.ifPresent(optionsList -> {
												if (optionsList.getOptions() != null) {
													userOptTranslations = optionsList.getOptions().stream()
															.filter(c -> c != null && c.getCaption() != null)
															.collect(Collectors.toMap(MapperUtil::getKey,
																	MapperUtil::getCaption));
												}
											}));
						}
					}

					if (userOptTranslations.size() == 0) {
						campaignFormElementOptions.setOptionsListValues(optionsValues);
						// get18nOptCaption(formElement.getId(), optionsValues));
					} else {
						campaignFormElementOptions.setOptionsListValues(userOptTranslations);
					}

				} else {
					optionsValues = new LinkedHashMap<String, String>();
				}

				if (formElement.getConstraints() != null) {
					campaignFormElementOptions = new CampaignFormElementOptions();
					constraints = (List<String>) Arrays.stream(formElement.getConstraints())
							.collect(Collectors.toList());
					ListIterator<String> lstItemsx = constraints.listIterator();
					int i = 1;
					while (lstItemsx.hasNext()) {
						String lss = lstItemsx.next().toString();
						if (lss.toLowerCase().contains("max")) {
							campaignFormElementOptions
									.setMax(Integer.parseInt(lss.substring(lss.lastIndexOf("=") + 1)));
						} else if (lss.toLowerCase().contains("min")) {
							campaignFormElementOptions
									.setMin(Integer.parseInt(lss.substring(lss.lastIndexOf("=") + 1)));
						} else if (lss.toLowerCase().contains("expression")) {
							campaignFormElementOptions.setExpression(true);
						}
					}

				}
				// input:checked

				String dependingOnId = formElement.getDependingOn();
				Object[] dependingOnValues = formElement.getDependingOnValues();

				Object value = formValuesMap.get(formElement.getId());

				int occupiedColumns = getOccupiedColumns(type, styles);

				final HashMap<String, String> data = (HashMap<String, String>) campaignFormElementOptions
						.getOptionsListValues();

				if (type == CampaignFormElementType.DAYWISE) {
					accrd_count++;
					if (accrd_count > 1) {

						final FormLayout layout = new FormLayout(vertical);
						// layout.addComponent(label);
						int temp = accrd_count;
						temp = temp - 1;
						layout.setClassName("daywise_background_" + temp); // .addStyleName(dependingOnId); sormas
																			// background: green
						accrd.add(get18nCaption(formElement.getId(), formElement.getCaption()), layout);

						vertical = new FormLayout();
						vertical.setSizeFull();
						vertical.setWidthFull();
						vertical.setHeightFull();

					}
				} else if (type == CampaignFormElementType.SECTION) {
					sectionCount++;

					vertical.setId("formSectionId-" + sectionCount);

				} else if (type == CampaignFormElementType.LABEL) {

					Label labx = new Label();
					labx.getElement().setProperty("innerHTML", get18nCaption(formElement.getId(),
							get18nCaption(formElement.getId(), formElement.getCaption())));
					labx.setId(formElement.getId());

					VerticalLayout labelLayout = new VerticalLayout();

					labelLayout.add(labx);
					vertical.setColspan(labelLayout, 3);
					vertical.add(labelLayout);
					if (dependingOnId != null && dependingOnValues != null) {
						// needed
						setVisibilityDependency(labx, dependingOnId, dependingOnValues, type, false);
					}
				} else if (type == CampaignFormElementType.LINEBREAK) {
					Paragraph newLine = new Paragraph();
					newLine.setId("pageBreak");
//				newLine.getStyle().set("width", "100% !important");
					vertical.setColspan(newLine, 3);
					vertical.add(newLine);

				} else {
					CampaignFormElementOptions constrainsVal = new CampaignFormElementOptions();
					boolean fieldIsRequired = formElement.isImportant();

					if (type == CampaignFormElementType.YES_NO) {

//					HashMap<Boolean, String> map = new HashMap<>();
//					map.put(true, I18nProperties.getCaption(Captions.actionYes));
//					map.put(false, I18nProperties.getCaption(Captions.actionNo));

						ToggleButtonGroup<Boolean> toggle = new ToggleButtonGroup<>(
								get18nCaption(formElement.getId(), formElement.getCaption()), List.of(true, false));
						toggle.setId(formElement.getId());

						toggle.setClassName("customTextWrap");

						HashMap<Boolean, String> map = new HashMap<>();
						map.put(true, "Yes");
						map.put(false, "No");

						HashMap<Boolean, String> mapPashto = new HashMap<>();
						mapPashto.put(true, "هو");
						mapPashto.put(false, "نه");

						HashMap<Boolean, String> mapDari = new HashMap<>();
						mapDari.put(true, "آره");
						mapDari.put(false, "خیر");

						toggle.setItemLabelGenerator(item -> {
							switch (currentUser.getUser().getLanguage().toString()) {
							case "Pashto":
								return mapPashto.get(item);
							case "Dari":
								return mapDari.get(item);
							default:
								return map.get(item);
							}
						});

//					toggle.setItemLabelGenerator(item -> map.get(item));
						toggle.getStyle().set("color", "Green");
						toggle.getStyle().set("background", "white");

						setFieldValue(toggle, type, value, optionsValues, formElement.getDefaultvalue(), false, null);

						vertical.add(toggle);
						fields.put(formElement.getId(), toggle);
//					System.out.println(dependingOnId + "dependingOnId11111111111111111111111 " + dependingOnValues);

						if (dependingOnId != null && dependingOnValues != null) {

//						System.out.println(dependingOnId + "dependingOnId 2222222222222222" + dependingOnValues
//								+ "tttttt" + formElement.isImportant());
							// needed
							setVisibilityDependency(toggle, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							toggle.setRequiredIndicatorVisible(formElement.isImportant());
						}

					} else if (type == CampaignFormElementType.TEXT) {
						TextField textField = new TextField();
						textField.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						textField.setClassName("customTextWrap");

						// textField.setValue("Ruukinkatu 2");
						textField.setClearButtonVisible(true);
						textField.setPrefixComponent(VaadinIcon.PENCIL.create());
						textField.setId(formElement.getId());
						textField.setSizeFull();
						//
						setFieldValue(textField, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);
						vertical.add(textField);
						fields.put(formElement.getId(), textField);

						if (fieldId.equalsIgnoreCase("eTazkiraNo")) {

							System.out.println("Tazkira Number Found -------------------");

							// Add validation for Tazkira Number
							textField.setAllowedCharPattern("[0-9-]"); // Allow only digits and hyphens

							// Check for existing value - use the value passed to the method
							if (value != null && !value.toString().isEmpty()) {
								System.out.println(
										"Tazkira Number Found -----------------c--" + value.toString().length());
								try {

									String existingValue = value.toString().replace("-", "");
									if (existingValue.length() == 13) {
										String formattedDisplay = existingValue.substring(0, 4) + "-"
												+ existingValue.substring(4, 8) + "-" + existingValue.substring(8);

										setFieldValue(textField, type, formattedDisplay, optionsValues,
												formElement.getDefaultvalue(), false, null);

									}
								} catch (Exception ex) {
									logger.error("Error formatting existing Tazkiraxx: " + ex.getMessage());
								}
							}

							// Add listener for new input
							textField.addValueChangeListener(e -> {
								textField.addInputListener(ex -> {
									System.out.println("textField.getValue().toString().length();------"
											+ textField.getValue().toString().length());
//				        		textField.getValue().toString().length();
								});
								try {
									if (e.getValue() != null) {
										String inputValue = e.getValue().toString();
										// Remove any existing formatting
										String cleanInput = inputValue.replace("-", "").replace(".", "");
										System.out.println("Value changed ===");
										if (e.getValue().length() == 13) {

											System.out.println("Value now 13 changed ===");

											// Format properly and store only the numeric value to avoid double
											// formatting
											String formattedExample = cleanInput.substring(0, 4) + "-"
													+ cleanInput.substring(4, 8) + "-" + cleanInput.substring(8);
//				                        textField.setHelperText("/alid E-Tazkira formatc: " + formattedExample);

											setFieldValue(textField, type, formattedExample, optionsValues,
													formElement.getDefaultvalue(), false, null);

										} else if (!inputValue.isEmpty()) {
											// Show warning if not empty and not 13 digits
//				                    	textField.setHelperText("E-Tazkira should be 13 digits in format: 0000-0000-00000");
										}
									}
								} catch (Exception ex) {
									logger.error("Error in Tazkira value change: " + ex.getMessage());
								}
							});
						}

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(textField, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							textField.setRequiredIndicatorVisible(formElement.isImportant());
						}

					} else if (type == CampaignFormElementType.NUMBER) {
						NumberField numberField = new NumberField();
						numberField.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						numberField.setClassName("customTextWrap");
						numberField.setMin(0);
						numberField.setId(formElement.getId());
						numberField.setSizeFull();

						numberField.setAllowedCharPattern("[0-9.]*"); // allow digits and one decimal point

//					setFieldValue(numberField, type, value, optionsValues, formElement.getDefaultvalue(), false, null);
//					vertical.add(numberField);
//					fields.put(formElement.getId(), numberField);

						// Binder<String> binder = new Binder<>(String.class);

						if (fieldId.equalsIgnoreCase("Villagecode")) {
							numberField.setAllowedCharPattern("(?!.*000$).*");
//								 new RegexpValidator("(?!.*000$).*", I18nProperties.getValidationError(
//											errormsg == null ? caption + ": " + Validations.onlyDecimalNumbersAllowed : errormsg, caption) ));

							numberField.addValueChangeListener(e -> {

								String inputValue = e.getValue() != null ? e.getValue().toString() : "";

								if (e.getValue() != null && e.getValue().toString().length() == 3) {
									String result = inputValue.substring(0, 1);
									logger.debug(result + " resultrrrr lento " + e.getValue().toString().length()
											+ "ttt11111111111" + inputValue + " tttttttttttttttttt" + result.length());

									// Checking the finl length of the trimmd val
									int length = result.length();
									if (length == 1 && VaadinService.getCurrentRequest().getWrappedSession()
											.getAttribute("Clusternumber") != null) {

										String cCodeLengthCheck = VaadinService.getCurrentRequest().getWrappedSession()
												.getAttribute("Clusternumber").toString();

										int ccodeCheckLength = cCodeLengthCheck.length();
										if (ccodeCheckLength == 6) {
											result = VaadinService.getCurrentRequest().getWrappedSession()
													.getAttribute("Clusternumber") + "000" + result;
										} else if (ccodeCheckLength == 7) {
											result = VaadinService.getCurrentRequest().getWrappedSession()
													.getAttribute("Clusternumber") + "00" + result;
										}

										// Prefix "00" for single-digit numbers

										logger.debug(result + " resultrrrr lento ttt222222222222tttttttttttttttttt"
												+ result.length());

									}

									numberField.setValue(Double.parseDouble(result));

								}
								if (e.getValue() != null && e.getValue().toString().length() == 4) {
									String result = inputValue.substring(0, e.getValue().toString().length() - 2);

//								logger.debug(result + " result lento ttttttttttttttttttttt" + result.length() );

									int length = result.length();
									if (length == 2 && VaadinService.getCurrentRequest().getWrappedSession()
											.getAttribute("Clusternumber") != null) {
										// Prefix "0" for single-digit numbers
										String cCodeLengthCheck = VaadinService.getCurrentRequest().getWrappedSession()
												.getAttribute("Clusternumber").toString();

										int ccodeCheckLength = cCodeLengthCheck.length();
										if (ccodeCheckLength == 6) {
											result = VaadinService.getCurrentRequest().getWrappedSession()
													.getAttribute("Clusternumber") + "00" + result;
										} else if (ccodeCheckLength == 7) {
											result = VaadinService.getCurrentRequest().getWrappedSession()
													.getAttribute("Clusternumber") + "0" + result;
										}
//					            	result  =VaadinService.getCurrentRequest().getWrappedSession()
//											.getAttribute("Clusternumber") + "0" + result;
//									logger.debug(result + " resultrrrr lento ttttttttttttttttttttt" + result.length() );

									}

									numberField.setValue(Double.parseDouble(result));

								}
								if (e.getValue() != null && e.getValue().toString().length() == 5) {
//								logger.debug(e.getValue() + "lento ttttttttttttttttttttt" + e.getValue().toString().length() );
									String result = inputValue.substring(0, e.getValue().toString().length() - 2);

//								logger.debug(result + " result lento ttttttttttttttttttttt" + result.length() );

//					            String trimmedValue = inputValue.replaceFirst("^0+(?!$)", "");
									// Check the length of the trimmed value
									int length = result.length();
									if (length == 3 && VaadinService.getCurrentRequest().getWrappedSession()
											.getAttribute("Clusternumber") != null) {
										// Prefix "00" for single-digit numbers

										String cCodeLengthCheck = VaadinService.getCurrentRequest().getWrappedSession()
												.getAttribute("Clusternumber").toString();

										int ccodeCheckLength = cCodeLengthCheck.length();
										if (ccodeCheckLength == 6) {
											result = VaadinService.getCurrentRequest().getWrappedSession()
													.getAttribute("Clusternumber") + "0" + result;
										} else if (ccodeCheckLength == 7) {
											result = VaadinService.getCurrentRequest().getWrappedSession()
													.getAttribute("Clusternumber") + result;
										}

										numberField.setValue(Double.parseDouble(result));

									}
								}
							});

						}

						if (fieldId.equalsIgnoreCase("PopulationGroup_0_4")) {

							numberField.addValueChangeListener(e -> {
								if (VaadinService.getCurrentRequest().getWrappedSession()
										.getAttribute("populationdata") != null) {

									final String des = VaadinService.getCurrentRequest().getWrappedSession()
											.getAttribute("populationdata").toString();
									numberField.setValue(Double.parseDouble(des));
									numberField.setReadOnly(true);
								}

							});

						}

						if (fieldId.equalsIgnoreCase("PopulationGroup_5_10")) {

							numberField.addValueChangeListener(e -> {
								if (VaadinService.getCurrentRequest().getWrappedSession()
										.getAttribute("populationdata") != null) {

									final String des = VaadinService.getCurrentRequest().getWrappedSession()
											.getAttribute("populationdata").toString();
									numberField.setValue(Double.parseDouble(des));
									numberField.setReadOnly(true);
								}

							});

						}

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(numberField, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							numberField.setRequiredIndicatorVisible(formElement.isImportant());
						}

						setFieldValue(numberField, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);

						numberField.addValueChangeListener(e -> {
							if (e.getValue() != null && e.getValue() < 0) {
//				        	integerField.setValue(0.0); // Reset to 0 if negative
								numberField.setInvalid(true);
								numberField.setErrorMessage("Negative values are not allowed");
							}
						});

						vertical.add(numberField);
						fields.put(formElement.getId(), numberField);

					} else if (type == CampaignFormElementType.PHONE) {
						ComboBox<String> availableCountries = new ComboBox<String>();
						availableCountries.setLabel("Country");

						TextField numberField = new TextField();
						numberField.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						numberField.setClassName("customTextWrap");
						numberField.setId(formElement.getId());
						numberField.setSizeFull();

						setFieldValue(numberField, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);
						vertical.add(availableCountries, numberField);
						fields.put(formElement.getId(), numberField);
						List<String> namesListx = new ArrayList<String>();

						for (DialingCodeDto dialingCodeDto : FacadeProvider.getDialingCodeFacade()
								.getAllCountriesDto()) {
							namesListx.add(dialingCodeDto.getCountry());
						}

						availableCountries.setItems(namesListx);

						if (value == null || value.toString().isEmpty()) {

							availableCountries.setValue(namesListx.get(0));
							dialingCodeDto = FacadeProvider.getDialingCodeFacade()
									.getCountryByCode(availableCountries.getValue());
							numberField.setValue(FacadeProvider.getDialingCodeFacade()
									.getCountryByCode(availableCountries.getValue()).getCode());
						} else {

							for (DialingCodeDto dialingCodeDto : FacadeProvider.getDialingCodeFacade()
									.getAllCountriesDto()) {
								if (value.toString().startsWith(dialingCodeDto.getCode())) {
									System.out.println("dialingCodeDto.getCode() " + dialingCodeDto.getCode());
									availableCountries.setValue(dialingCodeDto.getCountry());
									dialingCodeDto = FacadeProvider.getDialingCodeFacade()
											.getCountryByCode(availableCountries.getValue());
									break;
								}
							}
							numberField.setValue(value.toString());
						}

						min = FacadeProvider.getDialingCodeFacade().getCountryByCode(availableCountries.getValue())
								.getMin_length() + 2;
						max = FacadeProvider.getDialingCodeFacade().getCountryByCode(availableCountries.getValue())
								.getMax_length() + 2;

						numberField.setHelperText("Mobile number for "
								+ FacadeProvider.getDialingCodeFacade().getCountryByCode(availableCountries.getValue())
										.getCountry()
								+ " must be between " + min + " and " + max + " digits with the country code");

						numberField.setPattern("^[+]?[0-9]{" + min + "," + max + "}$");
						numberField.setErrorMessage("Invalid");

						availableCountries.addValueChangeListener(e -> {

							if (numberField.getValue() != null) {
								numberField.clear();
							}
							dialingCodeDto = FacadeProvider.getDialingCodeFacade().getCountryByCode(e.getValue());
							int addition = dialingCodeDto.getCode().length() - 1;
							min = FacadeProvider.getDialingCodeFacade().getCountryByCode(e.getValue()).getMin_length()
									+ addition;
							max = FacadeProvider.getDialingCodeFacade().getCountryByCode(e.getValue()).getMax_length()
									+ addition;

							numberField.setValue(
									FacadeProvider.getDialingCodeFacade().getCountryByCode(e.getValue()).getCode());
							numberField.setHelperText("Mobile number for " + dialingCodeDto.getCountry()
									+ " must be between " + min + " and " + max + " digits with the country code");
							numberField.setPattern("^[+]?[0-9]{" + min + "," + max + "}$");
							numberField.setErrorMessage("Invalid");
						});

					} else if (type == CampaignFormElementType.RANGE) {
						IntegerField integerField = new IntegerField();
						integerField.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						integerField.setClassName("customTextWrap");

//					integerField.setHelperText("Max 10 items");
						integerField.setId(formElement.getId());
						integerField.setStepButtonsVisible(true);
						integerField.setSizeFull();
						integerField.setMin(0);

						integerField.setAllowedCharPattern("[0-9.]*"); // allow digits and one decimal point

						setFieldValue(integerField, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);

						vertical.add(integerField);
						fields.put(formElement.getId(), integerField);

						String validationMessageTag = "";
						Map<String, Object> validationMessageArgs = new HashMap<>();

						if (constrainsVal.isExpression()) {

							if (!fieldIsRequired) {
								// ApmisNotification notification = new ApmisNotification("Application
								// submitted!");
							}

							constrainsVal.setExpression(false);

						} else {

							if (constrainsVal.getMin() != null || constrainsVal.getMax() != null) {

								integerField.setMin(constrainsVal.getMin());
								integerField.setMax(constrainsVal.getMax());

								if (constrainsVal.getMin() == null) {
									validationMessageTag = Validations.numberTooBig;
									validationMessageArgs.put("value", constrainsVal.getMax());
								} else if (constrainsVal.getMax() == null) {
									validationMessageTag = Validations.numberTooSmall;
									validationMessageArgs.put("value", constrainsVal.getMin());
								} else {
									validationMessageTag = Validations.numberNotInRange;
									validationMessageArgs.put("min", constrainsVal.getMin());
									validationMessageArgs.put("max", constrainsVal.getMax());
								}

							} else {

								// needed
								// This should throw error as range suppose to have min and max if not taken
								// care by expression
							}
						}

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(integerField, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							integerField.setRequiredIndicatorVisible(formElement.isImportant());
						}

						integerField.addValueChangeListener(e -> {
							if (e.getValue() != null && e.getValue() < 0) {
//				        	integerField.setValue(0.0); // Reset to 0 if negative
								integerField.setInvalid(true);
								integerField.setErrorMessage("Negative values are not allowed");
							}
						});

					} else if (type == CampaignFormElementType.DECIMAL) {

						NumberField numberField = new NumberField();
						numberField.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						numberField.setClassName("customTextWrap");
						numberField.setWidth("240px");
						numberField.setId(formElement.getId());
						numberField.setSizeFull();
						numberField.setReadOnly(false);
						numberField.setMin(0);

						setFieldValue(numberField, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);
						vertical.add(numberField);
						fields.put(formElement.getId(), numberField);

						String validationMessageTag = "";
						Map<String, Object> validationMessageArgs = new HashMap<>();

						if (campaignFormElementOptions.isExpression()) {
							if (!fieldIsRequired) {
							}
							campaignFormElementOptions.setExpression(false);
						} else {
							if (campaignFormElementOptions.getMin() != null
									|| campaignFormElementOptions.getMax() != null) {

								if (campaignFormElementOptions.getMin() != null) {
									numberField.setMin(campaignFormElementOptions.getMin().doubleValue());
								}
								if (campaignFormElementOptions.getMax() != null) {
									numberField.setMax(campaignFormElementOptions.getMax().doubleValue());
								}

								if (campaignFormElementOptions.getMin() == null) {
									validationMessageTag = Validations.numberTooBig;
									validationMessageArgs.put("value", campaignFormElementOptions.getMax());
								} else if (campaignFormElementOptions.getMax() == null) {
									validationMessageTag = Validations.numberTooSmall;
									validationMessageArgs.put("value", campaignFormElementOptions.getMin());
								} else {
									validationMessageTag = Validations.numberNotInRange;
									validationMessageArgs.put("min", campaignFormElementOptions.getMin());
									validationMessageArgs.put("max", campaignFormElementOptions.getMax());
								}

								final Double minValue = numberField.getMin();
								final Double maxValue = numberField.getMax();
								final String errorMsg = I18nProperties.getValidationError(validationMessageTag,
										validationMessageArgs);

								numberField.addValueChangeListener(e -> {
									Double val = e.getValue();
									if (val != null) {
										boolean isInvalid = false;

										if (minValue != null && val < minValue) {
											isInvalid = true;
										}
										if (maxValue != null && val > maxValue) {
											isInvalid = true;
										}

										if (isInvalid) {
											numberField.setInvalid(true);
											numberField.setErrorMessage(errorMsg);
										} else {
											numberField.setInvalid(false);
											numberField.setErrorMessage(null);
										}
									} else {
										numberField.setInvalid(false);
									}
								});
							}
						}

						if (constrainsVal.isExpression()) {

							if (!fieldIsRequired) {

							}

							constrainsVal.setExpression(false);

						} else {

							if (constrainsVal.getMin() != null || constrainsVal.getMax() != null) {

								numberField.setMin(constrainsVal.getMin());
								numberField.setMax(constrainsVal.getMax());

								if (constrainsVal.getMin() == null) {
									validationMessageTag = Validations.numberTooBig;
									validationMessageArgs.put("value", constrainsVal.getMax());
								} else if (constrainsVal.getMax() == null) {
									validationMessageTag = Validations.numberTooSmall;
									validationMessageArgs.put("value", constrainsVal.getMin());
								} else {
									validationMessageTag = Validations.numberNotInRange;
									validationMessageArgs.put("min", constrainsVal.getMin());
									validationMessageArgs.put("max", constrainsVal.getMax());
								}

							} else {

							}
						}

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(numberField, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							numberField.setRequiredIndicatorVisible(formElement.isImportant());
						}

						numberField.addValueChangeListener(e -> {
							if (e.getValue() != null && e.getValue() < 0) {
//				        	integerField.setValue(0.0); // Reset to 0 if negative
								numberField.setInvalid(true);
								numberField.setErrorMessage("Negative values are not allowed");
							}
						});

					} else if (type == CampaignFormElementType.TEXTBOX) {
						TextArea textArea = new TextArea();
						textArea.setWidthFull();
						textArea.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						textArea.setClassName("customTextWrap");
						textArea.setId(formElement.getId());
						textArea.setSizeFull();
						setFieldValue(textArea, type, value, optionsValues, formElement.getDefaultvalue(), false, null);
						vertical.add(textArea);
						fields.put(formElement.getId(), textArea);

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(textArea, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							textArea.setRequiredIndicatorVisible(formElement.isImportant());
						}

					} else if (type == CampaignFormElementType.RADIO) {
						RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
						radioGroup.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						radioGroup.setClassName("customTextWrap");

//					data = (HashMap<String, String>) campaignFormElementOptions
//							.getOptionsListValues();
						radioGroup.setItems(data.keySet().stream().collect(Collectors.toList()));

						radioGroup.setItemLabelGenerator(itm -> data.get(itm.toString().trim()));
						radioGroup.setId(formElement.getId());
						radioGroup.setSizeFull();
						setFieldValue(radioGroup, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);
						vertical.add(radioGroup);
						fields.put(formElement.getId(), radioGroup);

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(radioGroup, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							radioGroup.setRequiredIndicatorVisible(formElement.isImportant());
						}

					} else if (type == CampaignFormElementType.RADIOBASIC) {
						RadioButtonGroup<String> radioGroupVert = new RadioButtonGroup<>();
						radioGroupVert.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
						radioGroupVert.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						radioGroupVert.setClassName("customTextWrap");

//					data = (HashMap<String, String>) campaignFormElementOptions
//							.getOptionsListValues();
						radioGroupVert.setItems(data.keySet().stream().collect(Collectors.toList()));
						radioGroupVert.setItemLabelGenerator(itm -> data.get(itm.toString().trim()));

						radioGroupVert.setId(formElement.getId());
						radioGroupVert.setSizeFull();
						setFieldValue(radioGroupVert, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);
						vertical.add(radioGroupVert);
						fields.put(formElement.getId(), radioGroupVert);

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(radioGroupVert, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							radioGroupVert.setRequiredIndicatorVisible(formElement.isImportant());
						}

					} else if (type == CampaignFormElementType.DROPDOWN) {
						// Note: carrying out the option sorting only i the dropdown
						// to avoid getting a null pointer fro other input types with the
						// option method because making all the checks global would require including
						// the order value in other
						// input types that are not dropdown

						// get the order valuie, do a null check incase order wouldnt be specified
						boolean isNotSorted = false;
						try {
							if (formElement.getOptions().stream()
									.collect(Collectors.toMap(MapperUtil::getKey, MapperUtil::getOrder)) != null) {
								optionsOrder.clear();
								// pop the map with the order based off the key
								optionsOrder = formElement.getOptions().stream()
										.collect(Collectors.toMap(MapperUtil::getKey, MapperUtil::getOrder));
							}
							;
						} catch (NullPointerException ex) {
							optionsOrder.clear();
							optionsOrder = formElement.getOptions().stream()
									.collect(Collectors.toMap(MapperUtil::getKey, MapperUtil::getCaption));
							isNotSorted = true;
						}

						if (userOptTranslations.size() == 0) {
							campaignFormElementOptions.setOptionsListValues(optionsValues);

						} else {
							campaignFormElementOptions.setOptionsListValues(userOptTranslations);
						}
						// Trying toGetting order when using translation(not adequately tes)
						if (optionsOrder != null) {
							if (userOptTranslations.size() == 0) {
								campaignFormElementOptions.setOptionsListOrder(optionsOrder);
							} else {
								campaignFormElementOptions.setOptionsListOrder(optionsOrder);
							}

						}

						final HashMap<String, String> dataOrder = (HashMap<String, String>) campaignFormElementOptions
								.getOptionsListOrder();

						ComboBox<String> select = new ComboBox<>(
								get18nCaption(formElement.getId(), formElement.getCaption()));
						select.setClassName("customTextWrap");

						List<String> sortedKeys = new ArrayList<>(data.keySet()); // Create a list of keys
						if (!isNotSorted) {
							if (dataOrder != null) {
								Comparator<String> orderComparator = (key1, key2) -> {
									String order1 = getOrderValue(dataOrder, key1);
									String order2 = getOrderValue(dataOrder, key2);
									return Integer.compare(Integer.parseInt(order1), Integer.parseInt(order2));
								};

								sortedKeys.sort(orderComparator);
							}
						}

						select.setItems(sortedKeys);

						select.setItemLabelGenerator(itm -> data.get(itm.toString().trim()));
						select.setClearButtonVisible(true);

						select.addValueChangeListener(ee -> {
						});

						setFieldValue(select, type, value, optionsValues, formElement.getDefaultvalue(), false, null);

						vertical.add(select);
						fields.put(formElement.getId(), select);

						System.out.println(
								dependingOnId + " dependingOnId 3333333333333333333333333" + dependingOnValues);

						if (dependingOnId != null && dependingOnValues != null) {
							// needed

							System.out.println(dependingOnId + " dependingOnId 44444444444444444444444"
									+ dependingOnValues + "44444444444444444444444" + formElement.isImportant());

							setVisibilityDependency(select, dependingOnId, dependingOnValues, type,
									formElement.isImportant());

						} else {
							select.setRequiredIndicatorVisible(formElement.isImportant());
						}

					} else if (type == CampaignFormElementType.CHECKBOX) {
						CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
						checkboxGroup.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						checkboxGroup.setClassName("customTextWrap");

//					data = (HashMap<String, String>) campaignFormElementOptions
//							.getOptionsListValues();
						checkboxGroup.setItems(data.keySet().stream().collect(Collectors.toList()));
						checkboxGroup.setItemLabelGenerator(itm -> data.get(itm.toString().trim()));

						checkboxGroup.setId(formElement.getId());
						checkboxGroup.setSizeFull();
						setFieldValue(checkboxGroup, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);
						vertical.add(checkboxGroup);
						fields.put(formElement.getId(), checkboxGroup);

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(checkboxGroup, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							checkboxGroup.setRequiredIndicatorVisible(formElement.isImportant());
						}

					} else if (type == CampaignFormElementType.CHECKBOXBASIC) {
						CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
						checkboxGroup.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						checkboxGroup.setClassName("customTextWrap");

//					data = (HashMap<String, String>) campaignFormElementOptions
//							.getOptionsListValues();
						checkboxGroup.setItems(data.keySet().stream().collect(Collectors.toList()));
						checkboxGroup.setItemLabelGenerator(itm -> data.get(itm.toString().trim()));

						checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
						checkboxGroup.setId(formElement.getId());
						checkboxGroup.setSizeFull();
						setFieldValue(checkboxGroup, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);

						checkboxGroup.addValueChangeListener(event -> {
							if (checkboxGroup.isInvalid()) {
								checkboxGroup.setInvalid(false);
							}
							// Clear error background when value is selected
							if (!event.getValue().isEmpty()) {
								checkboxGroup.getElement().getStyle().remove("background");
								checkboxGroup.getElement().setProperty("error-background-set", null);
							}
						});

						vertical.add(checkboxGroup);
						fields.put(formElement.getId(), checkboxGroup);

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(checkboxGroup, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							checkboxGroup.setRequiredIndicatorVisible(formElement.isImportant());
						}

					} else if (type == CampaignFormElementType.DATE) {
						DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();
						singleFormatI18n.setDateFormat("dd-MM-yyyy");

						DatePicker datePicker = new DatePicker(
								get18nCaption(formElement.getId(), formElement.getCaption()));
						datePicker.setClassName("customTextWrap");

						datePicker.setI18n(singleFormatI18n);
						datePicker.setSizeFull();
						datePicker.setPlaceholder("DD-MM-YYYY");
						datePicker.setId(formElement.getId());

						if (campaignFormMeta.getFormType().equalsIgnoreCase("pre-campaign")) {
							datePicker.setMin(campaignDto.getPreCampStartDate().toInstant()
									.atZone(ZoneId.systemDefault()).toLocalDate());
						} else if (campaignFormMeta.getFormType().equalsIgnoreCase("intra-campaign")) {
							datePicker.setMin(campaignDto.getStartDate().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDate());
						} else if (campaignFormMeta.getFormType().equalsIgnoreCase("post-campaign")) {
							datePicker.setMin(campaignDto.getPostCampStartDate().toInstant()
									.atZone(ZoneId.systemDefault()).toLocalDate());
						}

						if (formEndDate != null) {
							if (formEndDate instanceof java.sql.Date) {
								datePicker.setMax(((java.sql.Date) formEndDate).toLocalDate());
							} else {
								datePicker.setMax(formEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
							}
						}

						setFieldValue(datePicker, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);
						vertical.add(datePicker);
						fields.put(formElement.getId(), datePicker);

						if (dependingOnId != null && dependingOnValues != null) {
							// needed
							setVisibilityDependency(datePicker, dependingOnId, dependingOnValues, type,
									formElement.isImportant());
						} else {
							datePicker.setRequiredIndicatorVisible(formElement.isImportant());
						}

					} else if (type == CampaignFormElementType.EMAIL) {

						EmailField validEmailField = new EmailField();
						validEmailField.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						validEmailField.setWidth("240px");
						validEmailField.setId(formElement.getId());

						setFieldValue(validEmailField, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);
						vertical.add(validEmailField);
						fields.put(formElement.getId(), validEmailField);

						validEmailField.getElement().setAttribute("name", "email");
//					validEmailField.setValue("julia.scheider@email.com");
						validEmailField.setErrorMessage("Enter a valid email address");
						validEmailField.setClearButtonVisible(true);

					} else if (type == CampaignFormElementType.TIME) {

						TimePicker timePicker = new TimePicker();
						timePicker.setLabel(get18nCaption(formElement.getId(), formElement.getCaption()));
						timePicker.setStep(Duration.ofMinutes(30));
						timePicker.setLocale(Locale.forLanguageTag("fi"));
//				timePickear.setValue(LocalTime.of(5, 30));
						timePicker.setAutoOpen(true);

						timePicker.addValueChangeListener(e -> {
							System.out.println("Value Changed-------" + e.getValue());

							timePicker.setValue(e.getValue());
						});
//				add(timePicker);

						setFieldValue(timePicker, type, value, optionsValues, formElement.getDefaultvalue(), false,
								null);

						vertical.add(timePicker);
						fields.put(formElement.getId(), timePicker);

					}

				}

				if (accrd_count == 0) {
					vertical.setVisible(false);
					add(vertical);
				} else {

					add(accrd);
				}

				userOptTranslations = new HashMap<String, String>();
			} else {
				System.out.println(formElement + "Check elements for nulll types");

				System.out.println(formElement.getCaption() + "Check elements caption for nulll types");
			}
		}
		checkExpression();
		disableExpressionFieldsForEditing();
		vertical.setSizeFull();
		vertical.setId("vertical_nn");
		vertical.setResponsiveSteps(
				// Use one column by default
				new ResponsiveStep("0", 1), new ResponsiveStep("520px", 2), new ResponsiveStep("1000px", 3));

		setId("campaignFormLayout");
		setSizeFull();
	}

	private String getOrderValue(Map<String, String> data, String key) {
		String orderValue = data.get(key);
		if (orderValue != null) {
			return orderValue;
		}
		return orderValue;
	}

	private static Date parseDateFromString(Object value) throws Exception {
		if (value instanceof Date) {
			return (Date) value;
		} else if (value instanceof LocalDate) {
			return Date.from(((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toInstant());
		} else if (value instanceof String) {
			String stringValue = ((String) value).trim();

			if (stringValue.isEmpty()) {
				return null;
			}

			// Try known formats in order
			List<String> formats = Arrays.asList("dd-MM-yyyy", "yyyy-MM-dd", "MM/dd/yyyy");

			for (String format : formats) {
				try {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
					LocalDate localDate = LocalDate.parse(stringValue, formatter);
					return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
				} catch (DateTimeParseException e) {

				}
			}
			throw new IllegalArgumentException("Unrecognized date format: " + stringValue);
		} else {
			throw new IllegalArgumentException("Unsupported date value type: " + value.getClass());
		}
	}

	public <T extends Component> void setFieldValue(T field, CampaignFormElementType type, Object value,
			Map<String, String> options, String defaultvalue, Boolean isErrored, Object defaultErrorMsgr) {

		Boolean isExpressionValue = false;
		switch (type) {

		case YES_NO:

			if (value != null) {
				if (value instanceof Boolean) {
					((ToggleButtonGroup) field).setValue(value);
				}

				if (value instanceof String) {
					Boolean dvalue = value.toString().equalsIgnoreCase("YES") ? true
							: value.toString().equalsIgnoreCase("NO") ? false
									: value.toString().equalsIgnoreCase("true") ? true
											: value.toString().equalsIgnoreCase("false") ? false : null;
					((ToggleButtonGroup) field).setValue(dvalue);

				}

			} else {

				((ToggleButtonGroup) field).updateStyles();

			}

			break;
		case RANGE:
//			logger.debug("|" + value + "|================|" + defaultErrorMsgr + "|");
			boolean isExxpression = false;
			if (defaultErrorMsgr != null) {
				if (defaultErrorMsgr.toString().endsWith("..")) {
					isExxpression = true;
					defaultErrorMsgr = defaultErrorMsgr.toString().equals("..") ? null
							: defaultErrorMsgr.toString().replace("..", "");
				}
			}

			if (isExxpression && isErrored && value == null) {

				Object tempz = defaultErrorMsgr != null ? defaultErrorMsgr
						: "Data entered not in range or calculated rangexxx!";
				String lb = field.getElement().getProperty("label");
				field.getElement().setProperty("invalid", true);
				field.getElement().setProperty("label", lb == null ? "" : lb);
				field.getElement().setProperty("errorMessage", defaultErrorMsgr != null ? defaultErrorMsgr.toString()
						: "Data entered not in range or calculated range!");
			}

			if (value != null) {
				if (value.toString().equals("") || value.toString().equals("false")) {
					((IntegerField) field).setValue(null);
				} else {
					String cleanValue = value.toString().replace(".0", "");

					String cleancleanvalue = value.toString(); // Assuming getValue() retrieves the value as a String
					if (cleancleanvalue.endsWith(".0")) {
						cleancleanvalue = cleancleanvalue.substring(0, cleancleanvalue.length() - 2); // Remove the ".0"
					}
					((IntegerField) field).setValue(Integer.parseInt(cleancleanvalue));
				}

			} else if (defaultvalue != null) {
				((IntegerField) field).setValue(Integer.parseInt(defaultvalue));
			} else {
				((IntegerField) field).setValue(null);
			}

			break;
		case TEXT:

			if (value != null) {
				((TextField) field).setValue(value.toString());

			} else if (defaultvalue != null) {
				((TextField) field).setValue(defaultvalue);
			}
			break;

		case NUMBER:
			if (field instanceof NumberField) {
				NumberField numberField = (NumberField) field;

				if (value != null) {
					String cvalue = value.toString().replace("null", "").trim();
					if (cvalue.equals("") || cvalue.equals("null") || cvalue.equals("false")) {
						numberField.setValue(null);
					} else {
						try {
							// For extremely large numbers, store as string value
							// This is supposed to help us avoids precision issues with double particularly
							// for tazkirano thats large
							if (cvalue.contains("E") || cvalue.length() > 15) {
								// Set the string representation as the value
								numberField.setValue(Double.parseDouble(cvalue));
								// Store the full string representation in a hidden field or data attribute if
								// needed
								// numberField.getElement().setAttribute("data-full-value", cvalue);
							} else {
								Double doubleValue = Double.parseDouble(cvalue);
								numberField.setValue(doubleValue);
							}
						} catch (NumberFormatException e) {
							// Handle parsing error
							numberField.setValue(null);

						}
					}
				} else if (defaultvalue != null) {
					try {
						Double doubleValue = Double.parseDouble(defaultvalue);
						numberField.setValue(doubleValue);
					} catch (NumberFormatException e) {
						numberField.setValue(null);
					}
				}
			} else if (field instanceof BigDecimalField) {
				// Handle BigDecimalField separately
				BigDecimalField bigDecimalField = (BigDecimalField) field;

				if (value != null) {
					String cvalue = value.toString().replace("null", "").trim();
					if (cvalue.equals("") || cvalue.equals("null")) {
						bigDecimalField.setValue(null);
					} else {
						try {
							BigDecimal bigValue = new BigDecimal(cvalue);
							bigDecimalField.setValue(bigValue);
						} catch (NumberFormatException e) {
							bigDecimalField.setValue(null);
						}
					}
				} else if (defaultvalue != null) {
					try {
						BigDecimal bigValue = new BigDecimal(defaultvalue);
						bigDecimalField.setValue(bigValue);
					} catch (NumberFormatException e) {
						bigDecimalField.setValue(null);
					}
				}
			}
			break;

		case DECIMAL:
			boolean isExpression = false;

			if (defaultErrorMsgr != null && defaultErrorMsgr.toString().endsWith("..")) {
				isExpression = true;
				defaultErrorMsgr = defaultErrorMsgr.toString().equals("..") ? null
						: defaultErrorMsgr.toString().replace("..", "");
			}

			NumberField decimalField = (NumberField) field;

			// Show error but DO NOT continue processing
			if (isExpression && isErrored && value == null) {

				decimalField.setInvalid(true);
				decimalField.setErrorMessage(defaultErrorMsgr != null ? defaultErrorMsgr.toString()
						: "Decimal value is not within the allowed range");
				return;
			}

			if (value != null) {
				String v = value.toString().trim();

				if (v.equals("")) {
					decimalField.setValue(null);
				} else {
					decimalField.setValue(Double.parseDouble(v));
				}

			} else if (defaultvalue != null) {
				// ORIGINAL BUG: you used value instead of defaultvalue
				decimalField.setValue(Double.parseDouble(defaultvalue));
			} else {
				decimalField.setValue(null);
			}
			break;

		case TEXTBOX:

			if (value != null) {

				if (value.equals(true)) {
					((TextArea) field).setEnabled(true);
				} else if (value.equals(false)) {
					((TextArea) field).setEnabled(false);
					// Notification.show("Warning:", Title "Expression resulted in wrong value
					// please check your data 1", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			;
			((TextArea) field).setValue(value != null ? value.toString() : null);
			break;
		case DATE:
			DatePicker datePicker = (DatePicker) field;

			if (value != null) {
				try {
					LocalDate localDate = null;

					// Handle LocalDate instances directly
					if (value instanceof LocalDate) {
						localDate = (LocalDate) value;
					}
					// Handle Date instances
					else if (value instanceof Date) {
						localDate = ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					}
					// Handle String values - multiple formats
					else if (value instanceof String) {
						String dateStr = ((String) value).trim();

						// Normalize Persian/Dari/Pashto digits to ASCII
						dateStr = normalizeDigits(dateStr);

						localDate = parseMultiFormatDate(dateStr);
					}
					// Handle other types (Long timestamps, etc.)
					else {
						logger.warn("Unexpected date type: {}. Attempting toString conversion.",
								value.getClass().getName());
						String dateStr = normalizeDigits(value.toString().trim());
						localDate = parseMultiFormatDate(dateStr);
					}

					if (localDate != null) {
						// Set locale and format pattern to dd-MM-yyyy
						datePicker.setLocale(new Locale("en", "GB")); // UK locale uses dd-MM-yyyy
						datePicker.setValue(localDate);

						// Optionally log the formatted output
						logger.debug("Date set to: {}", localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
					} else {
						datePicker.setValue(null);
						logger.error("Could not parse date value: {}", value);
					}

				} catch (Exception e) {
					logger.error("Error parsing date value: " + value, e);
					datePicker.setValue(null);
					datePicker.setInvalid(true);
					datePicker.setErrorMessage("Invalid date format. Expected dd-MM-yyyy");
				}
			} else {
				datePicker.setValue(null);
			}
			break;
//		case DATE:
//			if (value != null) {
//				try {
//					Date date = parseDateFromString(value);
//					LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//
//					DatePicker datePicker = (DatePicker) field;
//					datePicker.setLocale(Locale.UK);
//					datePicker.setValue(localDate);
//				} catch (Exception e) {
//					logger.error("Error parsing date value: " + value, e);
//					((DatePicker) field).setValue(null);
//				}
//			} else {
//				((DatePicker) field).setValue(null);
//			}
//			break;

		case RADIO:
			((RadioButtonGroup) field).setValue(Sets.newHashSet(value).toString().replace("[", "").replace("]", ""));
			break;
		case RADIOBASIC:
			((RadioButtonGroup) field).setValue(Sets.newHashSet(value).toString().replace("[", "").replace("]", ""));
			break;
		case CHECKBOX:
			if (value != null) {
				String dcs = value.toString().replace("[", "").replace("]", "").replaceAll(", ", ",");
				String strArray[] = dcs.split(",");
				for (int i = 0; i < strArray.length; i++) {
					((CheckboxGroup) field).select(strArray[i]);
				}
			}
			;
			break;
		case CHECKBOXBASIC:

			if (value != null) {
				String dcxs = value.toString().replace("[", "").replace("]", "").replaceAll(", ", ",");
				String strArraxy[] = dcxs.split(",");
				for (int i = 0; i < strArraxy.length; i++) {
					((CheckboxGroup) field).select(strArraxy[i]);

				}
			}
			;
			break;
		case DROPDOWN:

			final HashMap<String, String> data_ = (HashMap<String, String>) options;

			if (defaultvalue != null) {
				// String dxz = options.get(defaultvalue);
				((ComboBox) field).setValue(defaultvalue);
			}
			;

			if (value != null && data_ != null) {
				if (data_.get(value) != null) {

					// String dxz = options.get(value);
					((ComboBox) field).setValue(value);
				}
			}
			;

			break;

		case EMAIL:

			if (value != null) {
				((EmailField) field).setValue(value.toString());

			} else if (defaultvalue != null) {
				((EmailField) field).setValue(defaultvalue);
			}
			break;

		case TIME:
			TimePicker timePicker = (TimePicker) field;

			if (value != null) {
				System.out.println("time value is not null");

				// Handle LocalTime instances directly
				if (value instanceof LocalTime) {
					timePicker.setValue((LocalTime) value);
				}
				// Handle String values with digit normalization
				else if (value instanceof String) {
					String timeStr = ((String) value).trim();

					// Normalize Persian/Dari/Pashto digits to ASCII
					String normalizedTime = normalizeDigits(timeStr);

					if (normalizedTime.isEmpty() || normalizedTime.equalsIgnoreCase("null")) {
						timePicker.setValue(null);
					} else {
						try {
							timePicker.setValue(LocalTime.parse(normalizedTime));
							logger.debug("Successfully parsed time: '{}' (original: '{}')", normalizedTime, timeStr);
						} catch (DateTimeParseException e) {
							timePicker.setValue(null);
							logger.error("Failed to parse time value: '{}' (normalized: '{}'). Error: {}", timeStr,
									normalizedTime, e.getMessage());

							// Set error state
							timePicker.setInvalid(true);
							timePicker.setErrorMessage("Invalid time format. Expected HH:mm or HH:mm:ss");
						}
					}
				}
				// Handle unexpected types
				else {
					timePicker.setValue(null);
					logger.warn("Unexpected type for TIME field: {}. Expected LocalTime or String.",
							value.getClass().getName());
				}
			}
			// Handle default value with normalization
			else if (defaultvalue != null) {
				String defaultTimeStr = defaultvalue.trim();
				String normalizedDefault = normalizeDigits(defaultTimeStr);

				if (!normalizedDefault.isEmpty()) {
					try {
						timePicker.setValue(LocalTime.parse(normalizedDefault));
					} catch (DateTimeParseException e) {
						timePicker.setValue(null);
						logger.error("Failed to parse default time value: '{}' (normalized: '{}')", defaultTimeStr,
								normalizedDefault);
					}
				} else {
					timePicker.setValue(null);
				}
			}
			// No value or default - clear the field
			else {
				timePicker.setValue(null);
			}
			break;
//			if (value != null) {
//				System.out.println(" time value is not null ");
//				if (value instanceof LocalTime) {
//					((TimePicker) field).setValue((LocalTime) value);
//				} else if (value instanceof String) {
//					((TimePicker) field).setValue(LocalTime.parse((String) value));
//				}
//			} else if (defaultvalue != null) {
//				((TimePicker) field).setValue(LocalTime.parse((String) defaultvalue));
//			}
//			break;

		case PHONE:
			if (value != null) {
				((TextField) field).setValue(value.toString());

			} else if (defaultvalue != null) {
				((TextField) field).setValue(defaultvalue);
			}
			break;

		default:
			throw new IllegalArgumentException(type.toString());
		}
	}

	/**
	 * Normalizes Persian/Dari/Pashto/Arabic-Indic digits to ASCII digits Supports
	 * both Eastern Arabic (٠-٩) and Persian (۰-۹) numerals
	 */
	private String normalizeDigits(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		StringBuilder normalized = new StringBuilder();

		for (char c : input.toCharArray()) {
			// Persian/Dari digits (U+06F0 to U+06F9)
			if (c >= '\u06F0' && c <= '\u06F9') {
				normalized.append((char) ('0' + (c - '\u06F0')));
			}
			// Arabic-Indic digits (U+0660 to U+0669)
			else if (c >= '\u0660' && c <= '\u0669') {
				normalized.append((char) ('0' + (c - '\u0660')));
			}
			// Keep everything else (colons, spaces, etc.)
			else {
				normalized.append(c);
			}
		}

		return normalized.toString();
	}

	/**
	 * Parses dates from multiple common formats including: - "Tue Dec 09 00:00:00
	 * GMT+01:00 2025" (Java Date.toString() format) - "10-12-2025" (dd-MM-yyyy) -
	 * "2025-12-10" (ISO format yyyy-MM-dd) - "10/12/2025" (dd/MM/yyyy) - "Dec 09,
	 * 2025" (MMM dd, yyyy)
	 * 
	 * All inputs are normalized and returned as LocalDate which will be displayed
	 * as dd-MM-yyyy
	 * 
	 * @param dateStr The date string to parse
	 * @return LocalDate or null if parsing fails
	 */
	private LocalDate parseMultiFormatDate(String dateStr) {
		if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("null")) {
			return null;
		}

		// List of formatters to try in order
		// Note: All inputs are parsed to LocalDate, which DatePicker will display as
		// dd-MM-yyyy
		DateTimeFormatter[] formatters = {
				// Handle "Tue Dec 09 00:00:00 GMT+01:00 2025" format
				// This is the output of Java's Date.toString()
				DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH),
				DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH),

				// dd-MM-yyyy format (PRIMARY format - try first)
				DateTimeFormatter.ofPattern("dd-MM-yyyy"), // 10-12-2025
				DateTimeFormatter.ofPattern("d-M-yyyy"), // 9-12-2025 (single digit)

				// Other common formats
				DateTimeFormatter.ofPattern("yyyy-MM-dd"), // 2025-12-10 (ISO)
				DateTimeFormatter.ofPattern("dd/MM/yyyy"), // 10/12/2025
				DateTimeFormatter.ofPattern("d/M/yyyy"), // 9/12/2025

				// With time components
				DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
				DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"),

				// Month name formats
				DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH), // Dec 09, 2025
				DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH), // 09 Dec 2025
				DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH), // December 09, 2025

				// ISO formats
				DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.ISO_DATE_TIME,
				DateTimeFormatter.ISO_OFFSET_DATE_TIME };

		// Try each formatter
		for (DateTimeFormatter formatter : formatters) {
			try {
				// For formatters that include time/zone info
				if (dateStr.contains("GMT") || dateStr.contains("Z")
						|| (dateStr.contains(":") && dateStr.length() > 15)) {
					try {
						// Try parsing as ZonedDateTime first
						ZonedDateTime zdt = ZonedDateTime.parse(dateStr, formatter);
						LocalDate result = zdt.toLocalDate();
						logger.debug("Parsed '{}' to {} (will display as dd-MM-yyyy)", dateStr,
								result.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
						return result;
					} catch (DateTimeParseException e1) {
						try {
							// Try parsing with different approaches
							TemporalAccessor temporal = formatter.parse(dateStr);
							LocalDate result = LocalDate.from(temporal);
							logger.debug("Parsed '{}' to {} (will display as dd-MM-yyyy)", dateStr,
									result.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
							return result;
						} catch (DateTimeParseException e2) {
							// Continue to next formatter
						}
					}
				} else {
					// Direct LocalDate parsing for simple date formats
					LocalDate result = LocalDate.parse(dateStr, formatter);
					logger.debug("Parsed '{}' to {} (will display as dd-MM-yyyy)", dateStr,
							result.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
					return result;
				}
			} catch (DateTimeParseException e) {
				// Continue to next formatter
			}
		}

		// If all formatters fail, try using SimpleDateFormat as fallback
		// This handles edge cases that DateTimeFormatter might miss
		try {
			SimpleDateFormat[] legacyFormatters = {
					new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH),
					new SimpleDateFormat("dd-MM-yyyy"), new SimpleDateFormat("yyyy-MM-dd"),
					new SimpleDateFormat("dd/MM/yyyy") };

			for (SimpleDateFormat sdf : legacyFormatters) {
				try {
					Date date = sdf.parse(dateStr);
					LocalDate result = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					logger.debug("Parsed '{}' to {} using legacy formatter (will display as dd-MM-yyyy)", dateStr,
							result.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
					return result;
				} catch (ParseException e) {
					// Continue
				}
			}
		} catch (Exception e) {
			logger.debug("Legacy date parsing also failed for: {}", dateStr);
		}

		logger.error("Could not parse date string with any known format: '{}'", dateStr);
		return null;
	}

	private int getOccupiedColumns(CampaignFormElementType type, List<CampaignFormElementStyle> styles) {
		List<CampaignFormElementStyle> colStyles = styles.stream().filter(s -> s.toString().startsWith("col"))
				.collect(Collectors.toList());

		if (type == CampaignFormElementType.YES_NO && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.RADIO && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.CHECKBOX && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.DROPDOWN && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.CHECKBOXBASIC && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.RADIOBASIC && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.TEXTBOX && !styles.contains(CampaignFormElementStyle.INLINE)
				|| (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.DATE
						|| type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.EMAIL
						|| type == CampaignFormElementType.TIME || type == CampaignFormElementType.PHONE
						|| type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE
						|| type == CampaignFormElementType.LINEBREAK)) {// &&
																		// styles.contains(CampaignFormElementStyle.ROW))

			return 12;
		}

		if (colStyles.isEmpty()) {
			switch (type) {
			case LABEL:
			case SECTION:
				return 12;
			default:
				return 4;
			}
		}

		// Multiple col styles are not supported; use the first one
		String colStyle = colStyles.get(0).toString();
		return Integer.parseInt(colStyle.substring(colStyle.indexOf("-") + 1));
	}

	private float calculateComponentWidth(CampaignFormElementType type, List<CampaignFormElementStyle> styles) {
		List<CampaignFormElementStyle> colStyles = styles.stream().filter(s -> s.toString().startsWith("col"))
				.collect(Collectors.toList());

		if (type == CampaignFormElementType.YES_NO && styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.RADIO && styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.RADIOBASIC && styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.CHECKBOX && styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.CHECKBOXBASIC && styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.DROPDOWN && styles.contains(CampaignFormElementStyle.INLINE)
				|| (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.NUMBER
						|| type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.EMAIL
						|| type == CampaignFormElementType.TIME || type == CampaignFormElementType.PHONE
						|| type == CampaignFormElementType.RANGE || type == CampaignFormElementType.DATE
						|| type == CampaignFormElementType.TEXTBOX)
				// && !styles.contains(CampaignFormElementStyle.ROW)
				|| type == CampaignFormElementType.LABEL || type == CampaignFormElementType.SECTION) {
			return 100f;
		}
		if (1 == 1) {
			return 100f;
		}

		if (colStyles.isEmpty()) {
			// return 33.3f;
		}

		// Multiple col styles are not supported; use the first one
		String colStyle = colStyles.get(0).toString();
		return Integer.parseInt(colStyle.substring(colStyle.indexOf("-") + 1)) / 12f * 100;
	}

	private Date dateFormatterLongAndMobile(Object value) {

		String dateStr = value + "";
		DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
		DateFormat formatter_ = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");
		DateFormat formatter_x = new SimpleDateFormat("MMM d, yyyy HH:mm:ss a");
		DateFormat formattercx = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		DateFormat formatterx = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formatterxn = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		logger.debug("date in question " + value);
		// +++++++++++++++++++++++===========
//date = (Date) formatterxn.parse(dateStr);
		try {
			date = (Date) formatterxn.parse(dateStr);
		} catch (ParseException ne) {
			logger.debug("date wont parse on " + ne.getMessage());
			try {
				date = (Date) formatter.parse(dateStr);
			} catch (ParseException e) {
				logger.debug("date wont parse on " + e.getMessage());
				try {
					date = (Date) formatter_.parse(dateStr);
				} catch (ParseException ex) {
					logger.debug("date wont parse on " + ex.getMessage());
					try {
						date = (Date) formatter_x.parse(dateStr);
					} catch (ParseException edz) {
						logger.debug("date wont parse on " + edz.getMessage());
						try {
							date = (Date) formatterx.parse(dateStr);
						} catch (ParseException ed) {
							logger.debug("date wont parse on " + ed.getMessage());

							try {
								date = (Date) formattercx.parse(dateStr);
							} catch (ParseException edx) {
								logger.debug("date wont parse on " + edx.getMessage());

								date = new Date((Long) value);

							}
						}
					}
				}
			}
		}
		return date;
	}

	private Date dateFormatter(Object value) throws ParseException {
		// TODO Auto-generated method stub

		String dateStr = value + "";
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		Date date;

		date = (Date) formatter.parse(dateStr);

		// logger.debug(date);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.YEAR);
		// logger.debug("formatedDate : " + formatedDate);

		Date res = new Date(formatedDate + "");

		return res;
	}

	private void setVisibilityDependency(Component component, String dependingOnId, Object[] dependingOnValues,
			CampaignFormElementType typex, boolean isRequiredField) {
		Component dependingOnField = fields.get(dependingOnId);
		List<Object> dependingOnValuesList = Arrays.asList(dependingOnValues);

		if (dependingOnField == null) {
			return;
		}

		// fieldValueMatchesDependingOnValuesNOTValuer
		if (dependingOnValuesList.stream().anyMatch(v -> v.toString().contains("!"))) {

			// hide on default
			boolean hideNt = dependingOnValuesList.stream().anyMatch(
					v -> fieldValueMatchesDependingOnValuesNOTValuer(dependingOnField, dependingOnValuesList, typex));

			System.out.println(dependingOnValuesList + "JJJJ" + dependingOnField
					+ "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH" + hideNt);

			if (hideNt) {
				component.setVisible(hideNt);
				// getElement().setProperty("required", requiredIndicatorVisible);
				component.getElement().setProperty("required", isRequiredField);
			} else {
				component.setVisible(hideNt);
				component.getElement().setProperty("required", false);
			}
			// check value and determine if to hide or show
			((AbstractField) dependingOnField).addValueChangeListener(e -> {
				boolean visible = fieldValueMatchesDependingOnValuesNOTValuer(dependingOnField, dependingOnValuesList,
						typex);

				component.setVisible(visible);
				if (!typex.toString().equalsIgnoreCase(CampaignFormElementType.LABEL.toString())) {
					if (!typex.toString().equalsIgnoreCase(CampaignFormElementType.SECTION.toString())) {
						if (!visible) {

							if (typex == CampaignFormElementType.TEXT) {
								((TextField) component).setValue(" ");
								((TextField) component).setValue("");
							} else {

								((AbstractField) component).setValue(null);
							}

							((AbstractField) component).setRequiredIndicatorVisible(false);

							component.setVisible(visible);
						} else {
							component.setVisible(visible);
							((AbstractField) component).setRequiredIndicatorVisible(isRequiredField);
							component.getElement().setProperty("required", isRequiredField);
						}
					}
				}
			});
		} else {

			System.out.println("COntainss Not-------");

			// hide on default
			boolean hide = dependingOnValuesList.stream()
					.anyMatch(v -> fieldValueMatchesDependingOnValues(dependingOnField, dependingOnValuesList, typex));
			component.setVisible(hide);

			System.out.println("hidehidehide---" + hide);

			if (hide) {
				// getElement().setProperty("required", requiredIndicatorVisible);
				component.setVisible(hide);

				component.getElement().setProperty("required", isRequiredField);
			} else {
				component.getElement().setProperty("required", false);
			}

			// check value and determine if to hide or show
			((AbstractField) dependingOnField).addValueChangeListener(e -> {
				boolean visible = fieldValueMatchesDependingOnValues(dependingOnField, dependingOnValuesList, typex);

				if (typex != CampaignFormElementType.LABEL && typex != CampaignFormElementType.SECTION) {
					if (!visible) {

						((AbstractField) component).setRequiredIndicatorVisible(false);
						if (typex == CampaignFormElementType.TEXT) {
							((TextField) component).setValue(" ");
							((TextField) component).setValue("");
						} else {
							((AbstractField) component).setValue(null);
						}
						component.setVisible(visible);
					} else {
						component.setVisible(visible);
						((AbstractField) component).setRequiredIndicatorVisible(isRequiredField);
						component.getElement().setProperty("required", isRequiredField);
					}
				} else if (typex == CampaignFormElementType.LABEL) {
					((Label) component).setVisible(visible);
					System.out.println(visible + "Its a Label that needs to be show " + hide);
				} else if (typex == CampaignFormElementType.SECTION) {
					component.setVisible(visible);
					System.out.println(visible + "Its a Section that needs to be show " + hide);
				}
			});
		}
	}

	private boolean fieldValueMatchesDependingOnValues(Component dependingOnField, List<Object> dependingOnValuesList,
			CampaignFormElementType typex) {
		if (((AbstractField) dependingOnField).getValue() == null) {
			return false;
		}
//		logger.debug(Boolean.TRUE.equals(((ToggleButton) dependingOnField).getValue()) + "======= == =========: "
//				+ Boolean.TRUE.equals(((ToggleButton) dependingOnField).getValue()));
		if (dependingOnField instanceof ToggleButton) {
			// logger.debug("========getOptio");

			String stringValue = Boolean.TRUE.equals(((ToggleButton) dependingOnField).getValue()) ? "Yes" : "No";

			return dependingOnValuesList.stream().anyMatch(v -> v.toString().equalsIgnoreCase(stringValue));

		} else {

			return dependingOnValuesList.stream().anyMatch(
					v -> v.toString().equalsIgnoreCase(((AbstractField) dependingOnField).getValue().toString()));
		}
	}

	private boolean fieldValueMatchesDependingOnValuesNOTValuer(Component dependingOnField,
			List<Object> dependingOnValuesList, CampaignFormElementType typex) {
		if (((AbstractField) dependingOnField).getValue() == null) {
			return false;
		}

		if (dependingOnField instanceof ToggleButton) {
			// String booleanValue = Boolean.TRUE.equals(((ToggleButton)
			// dependingOnField).getValue()) ? "false" : "true";
			String stringValue = Boolean.TRUE.equals(((ToggleButton) dependingOnField).getValue()) ? "no" : "yes";

			return dependingOnValuesList.stream().anyMatch(v ->
//					v.toString().replaceAll("!", "").equalsIgnoreCase(booleanValue)
//							||
			v.toString().replaceAll("!", "").equalsIgnoreCase(stringValue));
		} else {

			return dependingOnValuesList.stream().anyMatch(v -> !v.toString().replaceAll("!", "")
					.equalsIgnoreCase(((AbstractField) dependingOnField).getValue().toString()));
		}
	}

	public String get18nCaption(String elementId, String defaultCaption) {
		if (userTranslations != null && userTranslations.containsKey(elementId)) {
			return userTranslations.get(elementId);
		}

		return defaultCaption;
	}

	public List<CampaignFormDataEntry> getFormValues() {
		return fields.keySet().stream().map(id -> {
			Component field = fields.get(id);

			if (field instanceof DatePicker) {
//				logger.debug(((DatePicker) field).getValue() + "______________________))");
//
//				String valc = ((DatePicker) field).getValue() != null ? ((DatePicker) field).getValue().toString()
//						: null;
//
//				return new CampaignFormDataEntry(id, valc);

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

				String valc = ((DatePicker) field).getValue() != null
						? ((DatePicker) field).getValue().format(formatter)
						: null;

				return new CampaignFormDataEntry(id, valc);
			} else if (field instanceof TimePicker) {
//				logger.debug(((DatePicker) field).getValue() + "______________________))");

				String valc = ((TimePicker) field).getValue() != null ? ((TimePicker) field).getValue().toString()
						: null;

				return new CampaignFormDataEntry(id, valc);
			} else if (field instanceof ToggleButton) {

				String valc = ((ToggleButton) field).getValue() != null ? ((ToggleButton) field).getValue().toString()
						.equalsIgnoreCase("true")
						|| ((ToggleButton) field).getValue().toString().equalsIgnoreCase("YES")
						|| ((ToggleButton) field).getValue().toString().equalsIgnoreCase("[YES]")
						|| ((ToggleButton) field).getValue().toString().equalsIgnoreCase("[true]")
								? "Yes"
								: ((ToggleButton) field).getValue().toString().equalsIgnoreCase("[NO]")
										|| ((ToggleButton) field).getValue().toString().equalsIgnoreCase("NO")
										|| ((ToggleButton) field).getValue().toString().equalsIgnoreCase("false")
										|| ((ToggleButton) field).getValue().toString().equalsIgnoreCase("[false]")
												? "No"
												: null
						: null;

				return new CampaignFormDataEntry(id, valc);

			} else {
				if (id.equals("villagecode")) {
					String doubletoParse = ((AbstractField) field).getValue() != null
							? ((AbstractField) field).getValue().toString()
							: "0";
					double number = Double.parseDouble(doubletoParse);
					DecimalFormat decimalFormat = new DecimalFormat("0");
					decimalFormat.setMaximumFractionDigits(0);
					String formattedNumber = decimalFormat.format(number);
					return new CampaignFormDataEntry(id, formattedNumber);
				} else if (id.equals("LotNo")) {
					String doubletoParse = ((AbstractField) field).getValue() != null
							? ((AbstractField) field).getValue().toString()
							: "0";
					double number = Double.parseDouble(doubletoParse);
					DecimalFormat decimalFormat = new DecimalFormat("0");
					decimalFormat.setMaximumFractionDigits(0);
					String formattedNumber = decimalFormat.format(number);
					return new CampaignFormDataEntry(id, formattedNumber);
				} else {
					return new CampaignFormDataEntry(id, ((AbstractField) field).getValue());
				}
			}
		}).collect(Collectors.toList());
	}

	private void checkForNegativeValuesSimple() {
		for (Map.Entry<String, Component> entry : fields.entrySet()) {
			Component component = entry.getValue();

			if (component instanceof NumberField) {
				NumberField field = (NumberField) component;
				if (field.getValue() != null && field.getValue() < 0) {
					field.setInvalid(true);
					field.setErrorMessage("Negative values are not allowed");
					hasErrorFormValues(8);
				}
			} else if (component instanceof IntegerField) {
				IntegerField field = (IntegerField) component;
				if (field.getValue() != null && field.getValue() < 0) {
					field.setInvalid(true);
					field.setErrorMessage("Negative values are not allowed");
					hasErrorFormValues(8);
				}
			}
			// Add similar checks for other numeric field types if needed
		}
	}

	
	private boolean validateTextInputFormDate(String formDateFieldValue) {
	    LocalDate minDate = null;
	    LocalDate maxDate = null;

	    Date formEndDate = FacadeProvider.getCampaignFormMetaWithExpFacade()
	            .getFormExpiryByCampaignAndFormUuid(campaignDto.getUuid(), campaignFormMeta.getUuid());

	    if (formEndDate != null) {
	        if (formEndDate instanceof java.sql.Date) {
	            maxDate = ((java.sql.Date) formEndDate).toLocalDate();
	        } else {
	            maxDate = formEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        }
	    }

	    if ("pre-campaign".equalsIgnoreCase(campaignFormMeta.getFormType())) {
	        minDate = campaignDto.getPreCampStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    } else if ("intra-campaign".equalsIgnoreCase(campaignFormMeta.getFormType())) {
	        minDate = campaignDto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    } else if ("post-campaign".equalsIgnoreCase(campaignFormMeta.getFormType())) {
	        minDate = campaignDto.getPostCampStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    }

	    String value = formDateFieldValue;

	    // Reset previous error state
	    formDate.setInvalid(false);
	    formDate.setErrorMessage(null);
	    
	    // Remove error styling if it exists
	    formDate.getElement().getStyle().remove("border-color");
	    formDate.getElement().getStyle().remove("color");

	    if (value == null || value.isBlank()) {
	        return false;
	    }

	    try {
	    	
	    	System.out.println("----------------HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
	        LocalDate inputDate = LocalDate.parse(value.trim(), dateformatter);
	        
	        if ((minDate != null && inputDate.isBefore(minDate)) || 
	            (maxDate != null && inputDate.isAfter(maxDate))) {
	            
	            // Force validation indicator even for read-only fields
	        	formDate.setInvalid(true);
	            
	            // Build error message
	            String errorMsg = "Date must be between " + 
	                minDate.format(dateformatter) + " and " + 
	                maxDate.format(dateformatter);
	            
	            formDate.setErrorMessage(errorMsg);
	            
	            return false;
	            
//		    	System.out.println(minDate +"----------------error mesage set HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH" + maxDate);

	            
//	            // For read-only fields, we need to manually show the error
//	            if (formDateField.isReadOnly()) {
//	            	
//			    	System.out.println(minDate +"-------------BBBBVVVVVVVVVVVVVHHHHHHH" + maxDate);
//
//	                // Temporarily enable to show validation state
//	                formDateField.setReadOnly(false);
//	                formDateField.setInvalid(true);
//	                formDateField.setReadOnly(true);
//	                
//	                // Add visual indicators for read-only error state
//	                formDateField.getElement().getStyle().set("border-color", "var(--lumo-error-color)");
//	                formDateField.getElement().getStyle().set("color", "var(--lumo-error-color)");
//			    	System.out.println(minDate +"-------------NNNNVVVVVVVVVVVVVHHHHHHH" + maxDate);
//
//	                // Show notification as additional feedback
//	                Notification.show(errorMsg, 5000, Position.MIDDLE);
//	            }else {
//			    	System.out.println(minDate +"-------------VVVVVVVVVVVVVHHHHHHH" + maxDate);
//			    	
//			        formDateField.setReadOnly(false);
//	                formDateField.setInvalid(true);
//	                formDateField.setReadOnly(true);
//	                
//	             ;
//
//	                // Add visual indicators for read-only error state
//	                formDateField.getElement().getStyle().set("border-color", "var(--lumo-error-color)");
//	                formDateField.getElement().getStyle().set("color", "var(--lumo-error-color)");
//	                
//	                
//					formDate.getElement().setProperty("invalid", true);
//
//	            }
//	            
//	            hasErrorFormValues(12);
	        }
	        return true;
	    } catch (Exception ex) {
	        logger.error("Error validating form date for value: " + value, ex);
	        
	        formDate.setInvalid(true);
	        formDate.setErrorMessage("Invalid date format. Use DD-MM-YYYY");
	        
	        if (formDate.isReadOnly()) {
	        	formDate.setReadOnly(false);
	        	formDate.setInvalid(true);
	        	formDate.setReadOnly(true);
	        	formDate.getElement().getStyle().set("border-color", "var(--lumo-error-color)");
	        	formDate.getElement().getStyle().set("color", "var(--lumo-error-color)");
	            Notification.show("Invalid date format. Use DD-MM-YYYY", 5000, Position.MIDDLE);
	        }
	        
	        hasErrorFormValues(13);
	        
	        return false;
	    }
	    
	}
	
//	private void validateTextInputFormDate(TextField formDateField) {
//
//		LocalDate minDate = null;
//		
//		LocalDate maxDate = null;
//
////    LocalDate minValidDate = null;
//		
//		System.out.println(campaignDto.getUuid() + "getFormExpiryByCampaignAndFormUuid validatin date from formdate input-----" );
//		
//		System.out.println(campaignFormMeta.getUuid() + "getFormExpiryByCampaignAndFormUuid validatin date from formdate input-----" );
//
//		
//		Date formEndDate = FacadeProvider.getCampaignFormMetaWithExpFacade()
//				.getFormExpiryByCampaignAndFormUuid(campaignDto.getUuid(), campaignFormMeta.getUuid());
//
////	LocalDate maxValidDate = null;
//
//		if (formEndDate != null) {
//			if (formEndDate instanceof java.sql.Date) {
//				maxDate = ((java.sql.Date) formEndDate).toLocalDate();
//			} else {
//				maxDate = formEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			}
//		}
//		
//		System.out.println(maxDate + "maxdategetFormExpiryByCampaignAndFormUuid validatin date from formdate input-----max dte " );
//
//
//		if ("pre-campaign".equalsIgnoreCase(campaignFormMeta.getFormType())) {
//			minDate = campaignDto.getPreCampStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//
//		} else if ("intra-campaign".equalsIgnoreCase(campaignFormMeta.getFormType())) {
//			minDate = campaignDto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//
//		} else if ("post-campaign".equalsIgnoreCase(campaignFormMeta.getFormType())) {
//			minDate = campaignDto.getPostCampStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		}
//
//		
//		System.out.println(minDate + "minDateminDateminDate" + maxDate + "maxDatemaxDatemaxDatemaxDate");
//		String value = formDateField.getValue();
//
//// Reset previous error
//    	formDateField.getElement().setProperty("invalid", false);
//
//		formDateField.setInvalid(false);
//		formDateField.setErrorMessage(null);
//
//		if (value == null || value.isBlank()) {
//			return;
//		}
//		
//		System.out.println(value + "valuevaluevaluevaluevaluevalue-------" );
//
//
//		try {
//            System.out.println(value + "YYYxxxvaluevaluevaluevaluevaluevalue-------" );
//
//            LocalDate inputDate = LocalDate.parse(value.trim(), dateformatter);
//            
//            System.out.println("Parsed date successfully: " + inputDate); // Added debug log
//            if ((minDate != null && inputDate.isBefore(minDate)) || (maxDate != null && inputDate.isAfter(maxDate))) {
//            	formDateField.getElement().setProperty("invalid", true);
//
//            	formDateField.setInvalid(true);
//                formDateField.setErrorMessage("Date must be between " + minDate + " and " + maxDate);
//                hasErrorFormValues(12);
//            }
//        } catch (Exception ex) { // Changed to catch Exception
//            logger.error("Error validating form date for value: " + value, ex); // Added logging
//            ex.printStackTrace(); // Print stack trace for immediate visibility in console
//        	formDateField.getElement().setProperty("invalid", true);
//
//            formDateField.setInvalid(true);
//            formDateField.setErrorMessage("Invalid date format. Use DD-MM-YYYY");
//            hasErrorFormValues(13);
//        }
//	}

	private void checkForDateFieldValuesOutsideValidityPeriod() {

		LocalDate minDate = null;
		Date formEndDate = FacadeProvider.getCampaignFormMetaWithExpFacade()
				.getFormExpiryByCampaignAndFormUuid(campaignDto.getUuid(), campaignFormMeta.getUuid());

		LocalDate maxDate = null;

		if (formEndDate != null) {
			if (formEndDate instanceof java.sql.Date) {
				maxDate = ((java.sql.Date) formEndDate).toLocalDate();
			} else {
				maxDate = formEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}
		}

		// Determine validity period based on form type
		if ("pre-campaign".equalsIgnoreCase(campaignFormMeta.getFormType())) {
			minDate = campaignDto.getPreCampStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		} else if ("intra-campaign".equalsIgnoreCase(campaignFormMeta.getFormType())) {
			minDate = campaignDto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		} else if ("post-campaign".equalsIgnoreCase(campaignFormMeta.getFormType())) {
			minDate = campaignDto.getPostCampStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}

		for (Map.Entry<String, Component> entry : fields.entrySet()) {
			Component component = entry.getValue();

			if (component instanceof DatePicker) {
				DatePicker field = (DatePicker) component;
				LocalDate selectedDate = field.getValue();

				// Reset previous error state
				field.setInvalid(false);
				field.setErrorMessage(null);

				if (selectedDate == null) {
					continue;
				}

				boolean beforeMin = minDate != null && selectedDate.isBefore(minDate);
				boolean afterMax = maxDate != null && selectedDate.isAfter(maxDate);

				if (beforeMin || afterMax) {
					field.setInvalid(true);
					field.setErrorMessage("Date must be between " + minDate + " and " + maxDate);
					hasErrorFormValues(11);
				}
			}
		}
	}

	private boolean validateAndSave() {
		hasErrorFormValuesReset();
				
		if (daywiseTracker) {

			fields.forEach((key, value) -> {
				Component formField = fields.get(key);

				if (cbArea.getValue() == null) {
					cbArea.getElement().setProperty("invalid", true);
					hasErrorFormValues(1);
				}
				if (cbRegion.getValue() == null) {
					cbRegion.getElement().setProperty("invalid", true);
					hasErrorFormValues(2);
				}
				if (cbDistrict.getValue() == null) {
					cbDistrict.getElement().setProperty("invalid", true);
					hasErrorFormValues(3);
				}
				if (!isDistrictEntry) {
					System.out.println(currentDay +" Not a district entry form QQQQQQQQQQQQQQQQ " + key);
					if (cbCommunity.getValue() == null) {
						cbCommunity.getElement().setProperty("invalid", true);
						hasErrorFormValues(4);
					}
				} else {
					System.out.println(currentDay + " district entry form QQQQQQQQQQQQQQQQQQQQQQ " + key);

				}
				if (formDate.getValue() == null) {
					formDate.getElement().setProperty("invalid", true);
					hasErrorFormValues(5);
				}

				if (((AbstractField) formField).isRequiredIndicatorVisible() && key.contains(currentDay)) {
					System.out.println(key + "FORMFIELDSSSSSSSS " + value);
					logger.debug(((AbstractField) formField).getValue() + "++++++++++"
							+ ((AbstractField) formField).getId());

					if (((AbstractField) formField).getValue() == null || ((AbstractField) formField).getValue() == ""
							|| (((AbstractField) formField).getValue() instanceof Set
									&& ((Set<?>) ((AbstractField) formField).getValue()).isEmpty())) {

						if ((((AbstractField) formField).getValue() instanceof Set
								&& ((Set<?>) ((AbstractField) formField).getValue()).isEmpty())) {
							formField.getElement().getStyle().set("background", "#ffe5e5");
						}

						hasErrorFormValues(6);
						formField.getElement().setProperty("invalid", true);
					} else {
						// Clear error state and background color when field has value
						if (formField.getElement().getProperty("invalid", false)) {
							formField.getElement().setProperty("invalid", false);
						}
						// Clear background if it was set due to error
						if ("true".equals(formField.getElement().getProperty("error-background-set"))) {
							formField.getElement().getStyle().remove("background");
							formField.getElement().setProperty("error-background-set", null);
						}
					}
				}
				
				if (((AbstractField) formField).isRequiredIndicatorVisible() && key.contains("day1")) {
					System.out.println(key + "DAY111111111111111111111111111111111 " + value);
					logger.debug(((AbstractField) formField).getValue() + "++++++++++"
							+ ((AbstractField) formField).getId());

					if (((AbstractField) formField).getValue() == null || ((AbstractField) formField).getValue() == ""
							|| (((AbstractField) formField).getValue() instanceof Set
									&& ((Set<?>) ((AbstractField) formField).getValue()).isEmpty())) {

						if ((((AbstractField) formField).getValue() instanceof Set
								&& ((Set<?>) ((AbstractField) formField).getValue()).isEmpty())) {
							formField.getElement().getStyle().set("background", "#ffe5e5");
						}

						hasErrorFormValues(6);
						formField.getElement().setProperty("invalid", true);
					} else {
						// Clear error state and background color when field has value
						if (formField.getElement().getProperty("invalid", false)) {
							formField.getElement().setProperty("invalid", false);
						}
						// Clear background if it was set due to error
						if ("true".equals(formField.getElement().getProperty("error-background-set"))) {
							formField.getElement().getStyle().remove("background");
							formField.getElement().setProperty("error-background-set", null);
						}
					}
				}

			});
		} else {
			System.out.println("NOTSUPPOSETORUNNINGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
			fields.forEach((key, value) -> {
				Component formField = fields.get(key);

				if (cbArea.getValue() == null) {
					cbArea.getElement().setProperty("invalid", true);
					hasErrorFormValues(1);
				}
				if (cbRegion.getValue() == null) {
					cbRegion.getElement().setProperty("invalid", true);
					hasErrorFormValues(2);
				}
				if (cbDistrict.getValue() == null) {
					cbDistrict.getElement().setProperty("invalid", true);
					hasErrorFormValues(3);
				}
				if (!isDistrictEntry) {
					System.out.println("Not a district entry form 1111111");
					if (cbCommunity.getValue() == null) {
						cbCommunity.getElement().setProperty("invalid", true);
						hasErrorFormValues(4);
					}
				} else {
					System.out.println(" district entry form 1111111");

				}
				if (formDate.getValue() == null) {
					formDate.getElement().setProperty("invalid", true);
					hasErrorFormValues(5);
				}

				if (((AbstractField) formField).isRequiredIndicatorVisible()) {
					logger.debug(((AbstractField) formField).getValue() + "++++++++++"
							+ ((AbstractField) formField).getId());

					if (((AbstractField) formField).getValue() == null || ((AbstractField) formField).getValue() == ""
							|| (((AbstractField) formField).getValue() instanceof Set
									&& ((Set<?>) ((AbstractField) formField).getValue()).isEmpty())) {

						if ((((AbstractField) formField).getValue() instanceof Set
								&& ((Set<?>) ((AbstractField) formField).getValue()).isEmpty())) {
							formField.getElement().getStyle().set("background", "#ffe5e5");
						}

						hasErrorFormValues(6);
						formField.getElement().setProperty("invalid", true);
					} else {
						// Clear error state and background color when field has value
						if (formField.getElement().getProperty("invalid", false)) {
							formField.getElement().setProperty("invalid", false);
						}
						// Clear background if it was set due to error
						if ("true".equals(formField.getElement().getProperty("error-background-set"))) {
							formField.getElement().getStyle().remove("background");
							formField.getElement().setProperty("error-background-set", null);
						}
					}
				}
//			
//			if (formField instanceof AbstractField) {
//			    AbstractField<?, ?> field = (AbstractField<?, ?>) formField;
//
//			    if (field.isRequiredIndicatorVisible()) {
//			        Object fieldvalue = field.getValue();
//			        boolean invalid = false;
//
//			        if (fieldvalue == null) {
//			            invalid = true;
//			        } else if (fieldvalue instanceof String && ((String) fieldvalue).trim().isEmpty()) {
//			            invalid = true;
//			        } else if (fieldvalue instanceof Set && ((Set<?>) fieldvalue).isEmpty()) {
//			            invalid = true;
//				        formField.getElement().getStyle().set("background", "#ffe5e5");
//
//			        }
//
//			        
//			        if (invalid) {
//			            hasErrorFormValues(6);
//			            formField.getElement().setProperty("invalid", true);
//			        }
//			    }
//			}

			});

		}

		fields.forEach((key, value) -> {
			Component formField = fields.get(key);
			if (formField.getElement().getProperty("invalid", false)) {
				hasErrorFormValues(7);
				Notification.show("Error on field: " + formField.getElement().getProperty("label"));
				return;
			}

		});

		checkForDateFieldValuesOutsideValidityPeriod();

		checkForNegativeValuesSimple();

		return invalidForm;
	}

	public void hasErrorFormValues(int numer) {
//		Notification.show("Error found in: " + numer);
		invalidForm = true;

	}

	public void hasErrorFormValuesReset() {
		invalidForm = false;
	}

	public boolean saveFormValues() {

		System.out.println("Entered save form New Data waiting response -------------");

		validateAndSave();
		if (!invalidForm) {

			if (openData) {
				boolean saveChecker = true;
				UserProvider userProvider = new UserProvider();
				List<CampaignFormDataEntry> entries = getFormValues();
				if (!isDistrictEntry) {
					CampaignFormDataEntry lotNo = new CampaignFormDataEntry();
					CampaignFormDataEntry lotClusterNo = new CampaignFormDataEntry();

					for (CampaignFormDataEntry sdxc : getFormValues()) {
						logger.debug(sdxc.getId() + "____values____ " + sdxc.getValue());
						if (sdxc.getId().equalsIgnoreCase("LotNo")) {
							lotNo = sdxc;
						}
						if (sdxc.getId().equalsIgnoreCase("LotClusterNo")) {
							lotClusterNo = sdxc;
						}
					}

					List<CampaignFormDataIndexDto> lotchecker = FacadeProvider.getCampaignFormDataFacade()
							.getCampaignFormDataByCampaignandFormMeta(campaignReferenceDto.getUuid(),
									campaignFormMeta.getUuid(), cbDistrict.getValue().getCaption(),
									cbCommunity.getValue().getCaption() != null ? cbCommunity.getValue().getCaption()
											: "");

					lotchecker.removeIf(e -> e.getUuid().equals(uuidForm));

					List<String> listLotNo = new ArrayList();
					List<String> listLotClusterNo = new ArrayList();

					if (lotchecker.size() > 0) {
						for (CampaignFormDataIndexDto campaignFormDataIndexDto : lotchecker) {
							List<CampaignFormDataEntry> lotOwnSec = campaignFormDataIndexDto.getFormValues();
							if (lotOwnSec.contains(lotNo)) {
								listLotNo.add(lotOwnSec.get(lotOwnSec.indexOf(lotNo)).getValue().toString());
							}

							if (lotOwnSec.contains(lotClusterNo) && lotOwnSec.contains(lotNo)) {
								listLotClusterNo
										.add(lotOwnSec.get(lotOwnSec.indexOf(lotClusterNo)).getValue().toString());
							}
						}
					}

					System.out.println(isDistrictEntry + " isDistrictEntryvalueeeeeeeeeeeeeeeee");

					for (String string : listLotClusterNo) {
						if (listLotNo.size() > 0) {
							if ((Long.parseLong(string) - Long.parseLong(lotClusterNo.getValue().toString()) == 0)
									&& (Long.parseLong(listLotNo.get(0))
											- Long.parseLong(lotNo.getValue().toString()) == 0)) {
								saveChecker = false;
								break;
							}
						}
					}
				}

				System.out.println("New Data waiting rffffffesponse -------------");

				if (saveChecker) {
					CampaignFormDataDto dataDto = FacadeProvider.getCampaignFormDataFacade()
							.getCampaignFormDataByUuid(uuidForm);

//			        long versionCount = FacadeProvider.getCampaignFormDataFacade().getRecordCountByGroupUuid(dataDto.getRecordgroupuuid());
					long incrementedVersion = dataDto.getRecordversion() + 1L;
					dataDto.setCommunity(cbCommunity.getValue());

					// maybe we want to check the name of the updating user here
					dataDto.setCreatingUser(userProvider.getUserReference());
					// dataDto.setSource(PlatformEnum.WEB);
//					dataDto.setRecordgroupuuid(dataDto.getRecordgroupuuid());
					dataDto.setRecordversion(incrementedVersion);
					dataDto.setFormValues(entries);

					dataDto = FacadeProvider.getCampaignFormDataFacade().saveCampaignFormData(dataDto);

					Notification.show(I18nProperties.getString(Strings.dataSavedSuccessfully));
					return true;
				} else {

					Notification notification = new Notification();
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					notification.setPosition(Position.MIDDLE);
					Button closeButton = new Button(new Icon("lumo", "cross"));
					closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
					closeButton.getElement().setAttribute("aria-label", "Close");
					closeButton.addClickListener(event -> {
						notification.close();
					});

					Paragraph text = new Paragraph("This Cluster Number or Lot Cluster Number exist");

					HorizontalLayout layout = new HorizontalLayout(text, closeButton);
					layout.setAlignItems(Alignment.CENTER);

					notification.add(layout);
					notification.open();

					return false;

				}
			} else {

				System.out.println("New Data waiting response -------------");
				boolean saveChecker = true;
				boolean ccodeChecker = true;
				UserProvider userProvider = new UserProvider();
				List<CampaignFormDataEntry> entries = getFormValues();

				System.out.println(isDistrictEntry + " gdgdgdtdgststsggtegstsgsgsfs");
				if (!isDistrictEntry) {

					CampaignFormDataEntry lotNo = new CampaignFormDataEntry();
					CampaignFormDataEntry lotClusterNo = new CampaignFormDataEntry();

					for (CampaignFormDataEntry sdxc : getFormValues()) {
//					logger.debug(sdxc.getId() + "____values____ " + sdxc.getValue());
						if (sdxc.getId().equalsIgnoreCase("LotNo")) {
							lotNo = sdxc;
						}
						if (sdxc.getId().equalsIgnoreCase("LotClusterNo")) {
							lotClusterNo = sdxc;
						}

					}

					List<CampaignFormDataIndexDto> lotchecker = FacadeProvider.getCampaignFormDataFacade()
							.getCampaignFormDataByCampaignandFormMeta(campaignReferenceDto.getUuid(),
									campaignFormMeta.getUuid(), cbDistrict.getValue().getCaption(),
									cbCommunity.getValue().getCaption() != null ? cbCommunity.getValue().getCaption()
											: "");

					List<String> listLotNo = new ArrayList();
					List<String> listLotClusterNo = new ArrayList();

					if (lotchecker.size() > 0) {
						for (CampaignFormDataIndexDto campaignFormDataIndexDto : lotchecker) {
							List<CampaignFormDataEntry> lotOwnSec = campaignFormDataIndexDto.getFormValues();
							if (lotOwnSec.contains(lotNo)) {
								listLotNo.add(lotOwnSec.get(lotOwnSec.indexOf(lotNo)).getValue().toString());
							}

							if (lotOwnSec.contains(lotClusterNo) && lotOwnSec.contains(lotNo)) {
								listLotClusterNo
										.add(lotOwnSec.get(lotOwnSec.indexOf(lotClusterNo)).getValue().toString());
							}
						}
					}

					for (String string : listLotClusterNo) {
						if (listLotNo.size() > 0) {
							if ((Long.parseLong(string) - Long.parseLong(lotClusterNo.getValue().toString()) == 0)
									&& (Long.parseLong(listLotNo.get(0))
											- Long.parseLong(lotNo.getValue().toString()) == 0)) {
								saveChecker = false;
								break;
							}
						}
					}
				}

				if (saveChecker) {
//					Date dateData = Date.from(formDate.getValue());

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					LocalDate localDate = LocalDate.parse(formDate.getValue(), formatter);

					// Convert LocalDate to java.util.Date
					Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//					CampaignFormDataDto dataDto = CampaignFormDataDto.build(campaignReferenceDto, campaignFormMeta,
//							cbArea.getValue(), cbRegion.getValue(), cbDistrict.getValue(), cbCommunity.getValue());

					if (isDistrictEntry) {
						System.out.println("District Enry form point 2222222222222222222222222");

						CampaignFormDataDto dataDto = CampaignFormDataDto.buildDistrictLevelForm(campaignReferenceDto,
								campaignFormMeta, cbArea.getValue(), cbRegion.getValue(), cbDistrict.getValue());

//						dataDto.setDistrictEntryForm(isDistrictEntry);
						dataDto.setFormDate(date);
						dataDto.setCreatingUser(userProvider.getUserReference());
						dataDto.setFormValues(entries);
						dataDto.setSource("WEB");
//						dataDto.setRecordgroupuuid(dataDto.getUuid());
						dataDto.setRecordversion(1L);
//						if (dataDto.getFormType())
						dataDto = FacadeProvider.getCampaignFormDataFacade().saveCampaignFormData(dataDto);
						Notification.show(I18nProperties.getString(Strings.dataSavedSuccessfully));
						return true;

					} else {

						System.out.println("nOT   District Enry form point 2222222222222222222222222");

						CampaignFormDataDto dataDto = CampaignFormDataDto.build(campaignReferenceDto, campaignFormMeta,
								cbArea.getValue(), cbRegion.getValue(), cbDistrict.getValue(), cbCommunity.getValue());

//						dataDto.setDistrictEntryForm(!isDistrictEntry);
						dataDto.setFormDate(date);
						dataDto.setCreatingUser(userProvider.getUserReference());
						dataDto.setFormValues(entries);
						dataDto.setSource("WEB");
//						dataDto.setRecordgroupuuid(dataDto.getUuid());
						dataDto.setRecordversion(1L);
//						if (dataDto.getFormType())
						dataDto = FacadeProvider.getCampaignFormDataFacade().saveCampaignFormData(dataDto);
						Notification.show(I18nProperties.getString(Strings.dataSavedSuccessfully));
						return true;
					}

				} else {
					Notification notification = new Notification();
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					notification.setPosition(Position.MIDDLE);
					Button closeButton = new Button(new Icon("lumo", "cross"));
					closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
					closeButton.getElement().setAttribute("aria-label", "Close");
					closeButton.addClickListener(event -> {
						notification.close();
					});

					Paragraph text = new Paragraph("This Cluster Number or Lot Cluster Number exist");

					HorizontalLayout layout = new HorizontalLayout(text, closeButton);
					layout.setAlignItems(Alignment.CENTER);

					notification.add(layout);
					notification.open();

					return false;

				}

			}
		}

		return false;
	}

	public void resetFormValues() {

		fields.keySet().forEach(key -> {
			Component field = fields.get(key);
			((AbstractField) field).setValue(formValuesMap.get(key));
		});
	}

	public void setFormValues(List<CampaignFormDataEntry> formValuex) {
		Map<String, Object> formValuesMapSet = new HashMap<>();
		if (formValuex != null) {
			formValuex.forEach(formValue -> formValuesMapSet.put(formValue.getId(), formValue.getValue()));
		}

		fields.keySet().forEach(key -> {
			Component field = fields.get(key);
			if (field instanceof NumberField) {

			}
			((AbstractField) field).setValue(formValuesMapSet.get(key));
		});
	}

	public List<CampaignFormElement> getFormElements() {
		return formElements;
	}

	public Map<String, Component> getFields() {
		return fields;
	}

	// Expression Logics

	private void checkExpression() {
		// logger.debug("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

		EvaluationContext context = refreshEvaluationContext(getFormValues());
		final List<CampaignFormElement> formElements = getFormElements();
		formElements.stream().filter(element -> element.getExpression() != null).forEach(e -> {
			try {
				final Expression expression = expressionParser.parseExpression(e.getExpression());
				// logger.debug("------: "+expression.getExpressionString());
				final Class<?> valueType = expression.getValueType(context);
				final Object value = expression.getValue(context, valueType);
				String valuex = value + "";

				if (!valuex.isBlank() && value != null) {
					if (e.getType().toString().equals("range")) {

						if (value.toString().equals("0")) {
							setFieldValue(getFields().get(e.getId()), CampaignFormElementType.fromString(e.getType()),
									null, null, null, false,
									e.getErrormessage() != null ? e.getCaption() + " : " + e.getErrormessage() + ".."
											: "..");
							// return;
						} else if (value.toString().equals("false")) {
							setFieldValue(getFields().get(e.getId()), CampaignFormElementType.fromString(e.getType()),
									null, null, null, false,
									e.getErrormessage() != null ? e.getCaption() + " : " + e.getErrormessage() + ".."
											: "..");
							// return;
						} else {

							Boolean isErrored = value.toString().endsWith(".0");

							setFieldValue(getFields().get(e.getId()), CampaignFormElementType.fromString(e.getType()),
									value.toString().endsWith(".0") ? value.toString().replace(".0", "") : value, null,
									null, isErrored,
									e.getErrormessage() != null ? e.getCaption() + " : " + e.getErrormessage() + ".."
											: "..");
							// return;
						}

					} else if (e.getType().toString().equals("decimal")) {

						if (value.toString().equals("0")) {
							setFieldValue(getFields().get(e.getId()), CampaignFormElementType.fromString(e.getType()),
									Double.valueOf(String.format("%.1f", Double.valueOf(value.toString()))), null, null,
									false,
									e.getErrormessage() != null ? e.getCaption() + " : " + e.getErrormessage() : null);
						} else {

							Boolean isErrored = value.toString().endsWith(".0");
							setFieldValue(getFields().get(e.getId()), CampaignFormElementType.fromString(e.getType()),
									Double.valueOf(String.format("%.1f", Double.valueOf(value.toString()))), null, null,
									isErrored,
									e.getErrormessage() != null ? e.getCaption() + " : " + e.getErrormessage() : null);
						}

					} else if (valueType.isAssignableFrom(Double.class)) {
						// logger.debug("yes double detected "+Double.isFinite((double) value) +"
						// = "+ value);
						setFieldValue(getFields().get(e.getId()), CampaignFormElementType.fromString(e.getType()),
								!Double.isFinite((double) value) ? 0
										: value.toString().endsWith(".0") ? value.toString().replace(".0", "")
												: Precision.round((double) value, 2),
								null, null, false,
								e.getErrormessage() != null ? e.getCaption() + " : " + e.getErrormessage() : null);
						// return;
					} else if (valueType.isAssignableFrom(Boolean.class)) {
						logger.debug(e.getCaption() + " : " + value + " = = = = ");
						setFieldValue(getFields().get(e.getId()), CampaignFormElementType.fromString(e.getType()),
								value, null, null, false,
								e.getErrormessage() != null ? e.getCaption() + " : " + e.getErrormessage() : null);
						// return;
						//
					} else {
						setFieldValue(getFields().get(e.getId()), CampaignFormElementType.fromString(e.getType()),
								value, null, null, false,
								e.getErrormessage() != null ? e.getCaption() + " : " + e.getErrormessage() : null);
					}
				} else if (e.getType().toString().equals("range") && valuex == null && e.getDefaultvalue() != null) {
				}
			} catch (SpelEvaluationException evaluationException) {
				// LOG.error("Error evaluating expression: {} / {}",
				// evaluationEx0rception.getMessageCode(), evaluationException.getMessage());
			}
		});

	}

	public void disableExpressionFieldsForEditing() {
		final Map<String, Component> fields_ = getFields();
		getFormElements().stream().filter(formElement -> formElement.getExpression() != null)
				.filter(formElement -> fields_.get(formElement.getId()) != null)
				.filter(formElement -> !formElement.getType().equals("range"))
				.filter(formElement -> !formElement.getType().equals("decimal"))
				.filter(formElement -> !formElement.isIgnoredisable())
				.forEach(formElement -> ((AbstractField) fields_.get(formElement.getId())).setEnabled(false));
		addExpressionListener();
	}

	public void addExpressionListener() {
		final Map<String, Component> fields_ = getFields();
		final List<CampaignFormElement> formElements = getFormElements();
		formElements.stream()
				// .filter(formElement -> formElement.getExpression() == null)
				.filter(formElement -> fields_.get(formElement.getId()) != null).forEach(formElement -> {
					((AbstractField) fields_.get(formElement.getId()))
							.addValueChangeListener(valueChangeEvent -> checkExpression());
				});
		configureExpressionFieldsWithTooltip();
	}

	public void configureExpressionFieldsWithTooltip() {
		final Map<String, Component> fields = getFields();
		getFormElements().stream().filter(formElement -> formElement.getExpression() != null)
				.filter(formElement -> fields.get(formElement.getId()) != null)
				.filter(formElement -> fields.get(formElement.getId()) instanceof Component)
				.forEach(this::buildTooltipDescription);
	}

	private void buildTooltipDescription(CampaignFormElement formElement) {
		final Set<String> fieldNamesInExpression = new HashSet<>();
		final String tooltip = formElement.getExpression();
		final Map<String, Component> fields = getFields();
		final Component field = fields.get(formElement.getId());
		getFormElements().forEach(element -> {
			if (tooltip.contains(element.getId())) {
				fieldNamesInExpression.add(get18nCaption(element.getId(), element.getCaption()));
			}
		});
		Tooltip tooltipx = Tooltip.forComponent(field)
				.withText(
						String.format("%s: %s", I18nProperties.getDescription(Descriptions.Campaign_calculatedBasedOn),
								StringUtils.join(fieldNamesInExpression, ", ")))
				.withPosition(Tooltip.TooltipPosition.TOP_START);
	}

}
