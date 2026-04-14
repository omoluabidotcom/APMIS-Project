package com.cinoteck.application.views.campaign;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import com.cinoteck.application.UserProvider;
import com.cinoteck.application.views.user.UserUiHelper;
import com.cinoteck.application.views.utils.DownloadFlowUtilityView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridMultiSelectionModel.SelectAllCheckboxVisibility;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.treegrid.TreeGrid;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignIndexDto;
import de.symeda.sormas.api.campaign.CampaignLogDto;
import de.symeda.sormas.api.campaign.CampaignTreeGridDto;
import de.symeda.sormas.api.campaign.CampaignTreeGridDtoImpl;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaExpiryDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaWithExpReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
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
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.api.utils.DataHelper;

@PageTitle("APMIS-Edit Campaign")
@Route(value = "/data")
public class CampaignForm extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 764300181578209719L;
	private static final String PRE_CAMPAIGN = "pre-campaign";
	private static final String INTRA_CAMPAIGN = "intra-campaign";
	private static final String POST_CAMPAIGN = "post-campaign";

	Button archiveDearchive = new Button(I18nProperties.getCaption(Captions.actionArchive));

	Button openCloseCampaign;
	Button duplicateCampaign;
	Button deleteCampaign;
	Button publishUnpublishCampaign;
	private Div tooltipHolder;
	Button discardChanges;
	Button saveChanges;

	Button logButton;
//	Button generateDefaultPopulation;

	Binder<CampaignIndexDto> binder = new BeanValidationBinder<>(CampaignIndexDto.class);
	Binder<CampaignDto> binderx = new BeanValidationBinder<>(CampaignDto.class);

	List<CampaignIndexDto> campaignNames, startDates, endDates, descriptions;
//	CampaignRounds rounds;
	private CampaignDto campaignDto;
	private CampaignIndexDto campaignDtox;

	H4 campaignBasics = new H4(I18nProperties.getCaption(Captions.campaignBasics));

	TextField campaignName = new TextField(I18nProperties.getCaption(Captions.Campaign_name));
	ComboBox round = new ComboBox<>(I18nProperties.getCaption(Captions.round));

	DatePicker preCampaignstartDate = new DatePicker("Pre-Campaign Start date");
	DatePicker preCampaignendDate = new DatePicker("Pre-Campaign End Date");
	DatePicker startDate = new DatePicker(I18nProperties.getCaption(Captions.Campaign_startDate));
	DatePicker endDate = new DatePicker(I18nProperties.getCaption(Captions.Campaign_endDate));
	DatePicker postCampaignstartDate = new DatePicker("Post-Campaign Start date");
	DatePicker postCampaignendDate = new DatePicker("Post-Campaign End date");

	TextField creatingUser = new TextField(I18nProperties.getCaption(Captions.Campaign_creatingUser));
	TextField creatingUuid = new TextField(I18nProperties.getCaption(Captions.uuid));
	TextField campaaignYear = new TextField(I18nProperties.getCaption(Captions.campaignYear));

	UUID uuid = UUID.randomUUID();
	TextArea description = new TextArea(I18nProperties.getCaption(Captions.description));

	public static TreeGrid<CampaignTreeGridDto> treeGrid = new TreeGrid<>();

	HorizontalLayout actionButtonsLayout = new HorizontalLayout();

	private Set<AreaReferenceDto> areass = new HashSet<>();;
	private Set<RegionReferenceDto> region = new HashSet<>();
	private Set<DistrictReferenceDto> districts = new HashSet<>();
	private Set<CommunityReferenceDto> community = new HashSet<>();
	private Set<PopulationDataDto> popopulationDataDtoSet = new HashSet<>();

	private final VerticalLayout statusChangeLayout;
	FormLayout formx;

	public Boolean isCreateForm = null;

	CampaignFormMetaReferenceDto xx;
	CampaignDto formDatac;
	boolean isArchived;
	boolean isPublished;
	boolean isOpenClose;
	boolean editMode;

	Set<CampaignFormMetaReferenceDto> selectedFormData = new HashSet<>();
	List<CampaignFormMetaReferenceDto> vvvv;
	CampaignFormGridComponent preCampaignFormGridComponent;

	CampaignFormGridComponent intraCampaignFormGridComponent;

	CampaignFormGridComponent postCampaignFormGridComponent;


	private boolean isSingleSelectClickItemLock;
	private boolean isMultiSelectItemLock;
	private boolean isSelectItemLock;

	private static final String DEFAULT_POPULATION_DATA_IMPORT_TEMPLATE_FILE_NAME = "default_population_data.csv";

	private UserProvider userProvider = new UserProvider();

	private String userLanguage = "";

	Checkbox selectDistrictCheckbox = new Checkbox();

	List<CampaignTreeGridDto> deletelist = new ArrayList<>();

	List<String> populationDataUuid = new ArrayList<>();

	boolean isClickListenerAttached;
	private boolean callbackRunning = false;
	private Timer timer;
	protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	public CampaignForm(CampaignDto formData) {

		super();
		I18nProperties.setUserLanguage(userProvider.getUser().getLanguage());
		this.statusChangeLayout = new VerticalLayout();
		this.formDatac = formData;

		isCreateForm = formData == null;

		statusChangeLayout.setSpacing(false);
		statusChangeLayout.setMargin(false);
		add(statusChangeLayout);

		addClassName("campaign-form");
		configureFields(formData);
		publishUnvisibleForEOC();
	}

	public void publishUnvisibleForEOC() {
		if (userProvider.getUser().getUsertype().equals(UserType.EOC_USER)) {
			publishUnpublishCampaign.setVisible(false);
		}
	}

	public LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getDate()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private boolean validateDatesByPhase(String campaignphase) {
		if (campaignphase.equalsIgnoreCase("pre-campaign")) {

			LocalDate preCampaignsstartDateValue = preCampaignstartDate.getValue();
			LocalDate preCampaignsendDateValue = preCampaignendDate.getValue();

			if (preCampaignsstartDateValue == null || preCampaignsendDateValue == null) {

				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
//			notification.setPosition(Position.MIDDLE_CENTER);
				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				Paragraph text = new Paragraph(
						"Please Check the Input Data : Enter a valid Pre-Campaign Start Date and End Date to continue.");

				HorizontalLayout layout = new HorizontalLayout(text, closeButton);
				layout.setAlignItems(Alignment.CENTER);

				notification.add(layout);
				notification.open();

//			saveChanges.setTooltipText("Please Check the Input Data for Errors");
				saveChanges.setEnabled(false);
//
				return false;
			}

			if (preCampaignsstartDateValue.isAfter(preCampaignsendDateValue)) {
				preCampaignstartDate.setInvalid(true);
				preCampaignendDate.setInvalid(true);

				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
//			notification.setPosition(Position.MIDDLE_CENTER);
				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				Paragraph text = new Paragraph(
						"Please Check the Input Data : Pre-Campaign End Date has to be after or on the same day as Pre-Campaign Start Date.");

				HorizontalLayout layout = new HorizontalLayout(text, closeButton);
				layout.setAlignItems(Alignment.CENTER);

				notification.add(layout);
				notification.open();
				saveChanges.setEnabled(false);
//			saveChanges.setTooltipText("Please Check the Input Data for Errors");
//			startDate.setHelperText("Please Check the Input Data : End Date has to be after or on the same day as Start Date.");
//			endDate.setHelperText("Please Check the Input Data : End Date has to be after or on the same day as Start Date.");

				return false; // Start date is after end date
			}

			preCampaignstartDate.setInvalid(false);
			preCampaignendDate.setInvalid(false);

		} else if (campaignphase.equalsIgnoreCase("intra-campaign")) {

			LocalDate startDateValue = startDate.getValue();
			LocalDate endDateValue = endDate.getValue();

			if (startDateValue == null || endDateValue == null) {

				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
//			notification.setPosition(Position.MIDDLE_CENTER);
				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				Paragraph text = new Paragraph(
						"Please Check the Input Data : Enter a valid Start Date and End Date to continue.");

				HorizontalLayout layout = new HorizontalLayout(text, closeButton);
				layout.setAlignItems(Alignment.CENTER);

				notification.add(layout);
				notification.open();

//			saveChanges.setTooltipText("Please Check the Input Data for Errors");
				saveChanges.setEnabled(false);
//
				return false;
			}

			if (startDateValue.isAfter(endDateValue)) {
				startDate.setInvalid(true);
				endDate.setInvalid(true);

				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
//			notification.setPosition(Position.MIDDLE_CENTER);
				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				Paragraph text = new Paragraph(
						"Please Check the Input Data : End Date has to be after or on the same day as Start Date.");

				HorizontalLayout layout = new HorizontalLayout(text, closeButton);
				layout.setAlignItems(Alignment.CENTER);

				notification.add(layout);
				notification.open();
				saveChanges.setEnabled(false);
//			saveChanges.setTooltipText("Please Check the Input Data for Errors");
//			startDate.setHelperText("Please Check the Input Data : End Date has to be after or on the same day as Start Date.");
//			endDate.setHelperText("Please Check the Input Data : End Date has to be after or on the same day as Start Date.");

				return false; // Start date is after end date
			}

			startDate.setInvalid(false);
			endDate.setInvalid(false);

		} else if (campaignphase.equalsIgnoreCase("intra-campaign")) {

			LocalDate postCampaignsstartDateValue = postCampaignstartDate.getValue();
			LocalDate postCampaignsendDateValue = postCampaignendDate.getValue();

			if (postCampaignsstartDateValue == null || postCampaignsendDateValue == null) {

				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
//			notification.setPosition(Position.MIDDLE_CENTER);
				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				Paragraph text = new Paragraph(
						"Please Check the Input Data : Enter a valid Post-Campaign Start Date and End Date to continue.");

				HorizontalLayout layout = new HorizontalLayout(text, closeButton);
				layout.setAlignItems(Alignment.CENTER);

				notification.add(layout);
				notification.open();

//			saveChanges.setTooltipText("Please Check the Input Data for Errors");
				saveChanges.setEnabled(false);
//
				return false;
			}

			if (postCampaignsstartDateValue.isAfter(postCampaignsendDateValue)) {
				postCampaignstartDate.setInvalid(true);
				postCampaignendDate.setInvalid(true);

				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
//			notification.setPosition(Position.MIDDLE_CENTER);
				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				Paragraph text = new Paragraph(
						"Please Check the Input Data : Pre-Campaign End Date has to be after or on the same day as Pre-Campaign Start Date.");

				HorizontalLayout layout = new HorizontalLayout(text, closeButton);
				layout.setAlignItems(Alignment.CENTER);

				notification.add(layout);
				notification.open();
				saveChanges.setEnabled(false);
//			saveChanges.setTooltipText("Please Check the Input Data for Errors");
//			startDate.setHelperText("Please Check the Input Data : End Date has to be after or on the same day as Start Date.");
//			endDate.setHelperText("Please Check the Input Data : End Date has to be after or on the same day as Start Date.");

				return false; // Start date is after end date
			}

			postCampaignstartDate.setInvalid(false);
			postCampaignendDate.setInvalid(false);

		}

		// Clear invalid state if dates are valid

		saveChanges.setEnabled(true);

		return true; // Dates are valid
	}

	private void configureFields(CampaignDto formData) {

		this.campaignDto = formData;
		CampaignsView view = new CampaignsView();
		description.getStyle().set("height", "10rem");

		creatingUser.setReadOnly(true);
		creatingUuid.setReadOnly(true);
		campaaignYear.setReadOnly(true);

		UserProvider usr = new UserProvider();
		String curentUse = usr.getUserName();

		creatingUser.setWidthFull();
		creatingUuid.setWidthFull();
		campaaignYear.setWidthFull();

		HorizontalLayout hort = new HorizontalLayout();
		hort.add(creatingUuid, creatingUser, campaaignYear);
		hort.setJustifyContentMode(JustifyContentMode.BETWEEN);

		round.setItems("NID", "SNID", "CRC", "SIA", "Mopping-Up", "Training");

		if (creatingUuid.getValue() == "") {

			creatingUuid.setValue(DataHelper.createUuid());
			creatingUser.setValue(curentUse);

		}

//		System.out.println(creatingUuid.getValue() + "craeting uuid ");
		binderx.forField(creatingUuid).bind(CampaignDto.UUID);

		binderx.forField(creatingUser).bind(CampaignDto.CREATING_USER_NAME);
		binderx.forField(campaaignYear).bind(CampaignDto.CAMPAIGN_YEAR);

		binderx.forField(campaignName).asRequired(I18nProperties.getString(Strings.campaignNameRequired))
				.bind(CampaignDto.NAME);
		binderx.forField(round).asRequired(I18nProperties.getString(Strings.campaignRoundrequired))
				.bind(CampaignDto.ROUND);
//		if(binderx.getBean().getRound()!= null && binderx.getBean().getRound() ==  "Case Respond" ) {
//			round.setValue("CRC");	
		System.out.println(round.getValue() + "ROUND VALUE BAWSED OFF BINDER ");
//		}

		binderx.forField(preCampaignstartDate).withConverter(new LocalDateToDateConverter())
				.bind(CampaignDto::getPreCampStartDate, CampaignDto::setPreCampStartDate);

		binderx.forField(preCampaignendDate).withConverter(new LocalDateToDateConverter())
				.bind(CampaignDto::getPreCampEndDate, CampaignDto::setPreCampEndDate);

		binderx.forField(startDate).withConverter(new LocalDateToDateConverter()).bind(CampaignDto::getStartDate,
				CampaignDto::setStartDate);

		binderx.forField(endDate).withConverter(new LocalDateToDateConverter()).bind(CampaignDto::getEndDate,
				CampaignDto::setEndDate);

		binderx.forField(postCampaignstartDate).withConverter(new LocalDateToDateConverter())
				.bind(CampaignDto::getPostCampStartDate, CampaignDto::setPostCampStartDate);

		binderx.forField(postCampaignendDate).withConverter(new LocalDateToDateConverter())
				.bind(CampaignDto::getPostCampEndDate, CampaignDto::setPostCampEndDate);

		if (formData != null) {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d. M. yyyy");

			if (formData.getPreCampStartDate() != null) {
				LocalDate timestamp = formData.getPreCampStartDate().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();
				LocalDate localDatex = formData.getPreCampEndDate().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();
				String formString = timestamp.format(dateTimeFormatter);
				LocalDate localDate = LocalDate.parse(formString, dateTimeFormatter);
				preCampaignstartDate.setValue(localDate);
				preCampaignendDate.setValue(localDatex);
			}

			if (formData.getStartDate() != null) {
				LocalDate timestamp = formData.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate localDatex = formData.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				String formString = timestamp.format(dateTimeFormatter);
				LocalDate localDate = LocalDate.parse(formString, dateTimeFormatter);
				startDate.setValue(localDate);
				endDate.setValue(localDatex);
			}

			if (formData.getPostCampStartDate() != null) {
				LocalDate timestamp = formData.getPostCampStartDate().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();
				LocalDate localDatex = formData.getPostCampEndDate().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();
				String formString = timestamp.format(dateTimeFormatter);
				LocalDate localDate = LocalDate.parse(formString, dateTimeFormatter);
				postCampaignstartDate.setValue(localDate);
				postCampaignendDate.setValue(localDatex);
			}

		} 

		preCampaignstartDate.addValueChangeListener(e -> {
			LocalDate selectedDate = e.getValue();
			int selectedYear = selectedDate.getYear();
			String selectedYearAsString = Integer.toString(selectedYear);

			if (preCampaignendDate.getValue() != null) {
//				validateDates();
				validateDatesByPhase("pre-campaign");
 
			} else if (formData == null || formData != null) {
//				campaaignYear.setValue(selectedYearAsString);

				// System.out.println(selectedYearAsString + "Selected Year: " + selectedYear);
			}
		});

		startDate.addValueChangeListener(e -> {
			LocalDate selectedDate = e.getValue();
			int selectedYear = selectedDate.getYear();
			String selectedYearAsString = Integer.toString(selectedYear);

			if (endDate.getValue() != null) {
//				validateDates();
				validateDatesByPhase("intra-campaign");

				campaaignYear.setValue(selectedYearAsString);

				if (intraCampaignFormGridComponent != null) {
					intraCampaignFormGridComponent.recalculateExpiryForPhase(campaignDto);
				}
				// System.out.println(selectedYearAsString + "Selected Yearaaaaaaaaaaa: " +
				// selectedYear);

			} else if (formData == null || formData != null) {
				campaaignYear.setValue(selectedYearAsString);

				// System.out.println(selectedYearAsString + "Selected Year: " + selectedYear);
			}
		});

		postCampaignstartDate.addValueChangeListener(e -> {
			LocalDate selectedDate = e.getValue();
			int selectedYear = selectedDate.getYear();
			String selectedYearAsString = Integer.toString(selectedYear);

			if (postCampaignendDate.getValue() != null) {
//				validateDates();
				validateDatesByPhase("post-campaign");

//				campaaignYear.setValue(selectedYearAsString);
				// System.out.println(selectedYearAsString + "Selected Yearaaaaaaaaaaa: " +
				// selectedYear);

				if (postCampaignFormGridComponent != null) {
					postCampaignFormGridComponent.recalculateExpiryForPhase(campaignDto);
				}
			} else if (formData == null || formData != null) {
//				campaaignYear.setValue(selectedYearAsString);

				// System.out.println(selectedYearAsString + "Selected Year: " + selectedYear);
			}
		});

		endDate.addValueChangeListener(e -> {
			if (startDate.getValue() != null) {
//				validateDates();
				validateDatesByPhase("intra-campaign");

			}
		});

		preCampaignendDate.addValueChangeListener(e -> {
			if (preCampaignstartDate.getValue() != null) {
//				validateDates();
				validateDatesByPhase("pre-campaign");

			}
		});
		postCampaignendDate.addValueChangeListener(e -> {
			if (postCampaignstartDate.getValue() != null) {
//				validateDates();
				validateDatesByPhase("post-campaign");

			}
		});

		binderx.forField(description).asRequired(I18nProperties.getString(Strings.campaignDescriptionRequired)).bind(
				CampaignDto::getDescription,

				CampaignDto::setDescription);

		final HorizontalLayout layoutParent = new HorizontalLayout();
		layoutParent.setWidthFull();

		TabSheet tabsheetParent = new TabSheet();
		layoutParent.add(tabsheetParent);
		VerticalLayout parentTab1 = new VerticalLayout();
		final HorizontalLayout layout = new HorizontalLayout();
		layout.setWidthFull();

		TabSheet tabsheet = new TabSheet();
		layout.add(tabsheet);

		userLanguage = userProvider.getUser().getLanguage().toString();

//		System.out.println(userLanguage + "User language in campaugn data ");

		VerticalLayout tab1 = new VerticalLayout();

		preCampaignFormGridComponent = new CampaignFormGridComponent(
				this.campaignDto == null ? Collections.emptyList()
						: new ArrayList<>(campaignDto.getCampaignFormMetas(PRE_CAMPAIGN)),
				FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferencesByRoundAndUserLanguage(
						PRE_CAMPAIGN, userLanguage),
				campaignDto, PRE_CAMPAIGN);

		intraCampaignFormGridComponent = new CampaignFormGridComponent(
				this.campaignDto == null ? Collections.emptyList()
						: new ArrayList<>(campaignDto.getCampaignFormMetas(INTRA_CAMPAIGN)),
				FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferencesByRoundAndUserLanguage(
						INTRA_CAMPAIGN, userLanguage),
				campaignDto, INTRA_CAMPAIGN);

		postCampaignFormGridComponent = new CampaignFormGridComponent(
				this.campaignDto == null ? Collections.emptyList()
						: new ArrayList<>(campaignDto.getCampaignFormMetas(POST_CAMPAIGN)),
				FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferencesByRoundAndUserLanguage(
						POST_CAMPAIGN, userLanguage),
				campaignDto, POST_CAMPAIGN);

		VerticalLayout tab2 = new VerticalLayout();

		tabsheet.add(I18nProperties.getCaption(Captions.preCampaignForms), tab1);
		// tabsheet.add(I18nProperties.getCaption(Captions.preCampaignDashboard), tab2);
		tabsheet.setWidthFull();
		parentTab1.add(layout);
		tabsheetParent.add(I18nProperties.getCaption(Captions.preCampaignPhase), parentTab1);

		VerticalLayout parentTab2 = new VerticalLayout();

		VerticalLayout parentTab3 = new VerticalLayout();

		final HorizontalLayout layoutIntra = new HorizontalLayout();
		layoutIntra.setWidthFull();

		TabSheet tabsheetIntra = new TabSheet();
		layoutIntra.add(tabsheetIntra);

		VerticalLayout tab1Intra = new VerticalLayout();

		H1 text = new H1(I18nProperties.getString(Strings.contentGoeshere));

//		this.campaignDto = compp.getModifiedDto();
		tabsheetIntra.add(I18nProperties.getCaption(Captions.intraCampaignForms), tab1Intra);
		tabsheetIntra.setWidthFull();

		VerticalLayout tab2Intra = new VerticalLayout();

//		final List<CampaignDashboardElement> intracampaignDashboardElements = FacadeProvider.getCampaignFacade()
//				.getCampaignDashboardElements(null, INTRA_CAMPAIGN);

		// tabsheetIntra.add(I18nProperties.getCaption(Captions.intraCampaignDashboard),
		// tab2Intra);
		parentTab2.add(layoutIntra);
		// parentTab2.getStyle().set("color", "green");

		tabsheetParent.add(I18nProperties.getCaption(Captions.intraCampaignPhase), parentTab2);

		final HorizontalLayout layoutPost = new HorizontalLayout();
		layoutPost.setWidthFull();

		TabSheet tabsheetPost = new TabSheet();
		layoutPost.add(tabsheetPost);

		VerticalLayout tab1Post = new VerticalLayout();

//		this.campaignDto = comppp.getModifiedDto();
		tabsheetPost.add(I18nProperties.getCaption(Captions.postCampaignForms), tab1Post);

		VerticalLayout tab2Post = new VerticalLayout();

		// tabsheetPost.add(I18nProperties.getCaption(Captions.postCampaignDashboard),
		// tab2Post);
		tabsheetPost.setWidthFull();
		parentTab3.add(layoutPost);
		tabsheetParent.add(I18nProperties.getCaption(Captions.postCampaignPhase), parentTab3);

		VerticalLayout parentTab4 = new VerticalLayout();
		final HorizontalLayout layoutAssocCamp = new HorizontalLayout();
		layoutAssocCamp.setWidthFull();

		if (campaignDto != null) {
			tab1.add(preCampaignFormGridComponent);
			tab1Intra.add(intraCampaignFormGridComponent);
			tab1Post.add(postCampaignFormGridComponent);

//			tab2.add(comp1);
//			tab2Intra.add(compp2);
//			tab2Post.add(comppp2);
			
//			AssociateCampaign associateTab = new AssociateCampaign(formData); 
//			parentTab4.add(associateTab);

		} else {

			Div savecampaignText, savecampaignText1, savecampaignText2, savecampaignTextt, savecampaignText11,
					savecampaignText22, savecampaignTextAssoccamp;

			savecampaignText = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));
			savecampaignText1 = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));
			savecampaignText2 = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));
			savecampaignTextt = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));
			savecampaignText11 = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));
			savecampaignText22 = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));
			savecampaignTextAssoccamp = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));

			tab1.add(savecampaignText);
			tab2.add(savecampaignText1);
			tab1Intra.add(savecampaignText2);
			tab2Intra.add(savecampaignTextt);
			tab1Post.add(savecampaignText11);
			tab2Post.add(savecampaignText22);
			parentTab4.add(savecampaignTextAssoccamp);

		}

//		parentTab4.add(layoutAssocCamp);
		tabsheetParent.add(I18nProperties.getCaption(Captions.associateCampaign), parentTab4);

		VerticalLayout parentTab5 = new VerticalLayout();
		parentTab5.setId("parentTab5");

		VerticalLayout poplayout = new VerticalLayout();
		poplayout.setSpacing(false);
		poplayout.setId("poplayout");

		Label lblIntroduction = new Label(I18nProperties.getString(Strings.infoPopulationDataView));

		poplayout.add(lblIntroduction);

		poplayout.setHorizontalComponentAlignment(Alignment.CENTER, lblIntroduction);// .setHorizontalComponentAlignment(lblIntroduction,
		
		if (campaignDto != null) {
		Button btnGeneratePopulationData = new Button(I18nProperties.getCaption("Generate Population Data | " +  campaignDto.getName()));// , e -> {

		btnGeneratePopulationData.addClickListener(e -> {
			if (campaignDto != null) {
				Dialog genDialog = new Dialog();
				genDialog.setHeaderTitle(I18nProperties.getCaption("Generate Population Data | " +  campaignDto.getName()));
				genDialog.setWidth("40%");
				
				VerticalLayout dialogLayout = new VerticalLayout();
				
//				TextField campaignName = new TextField(I18nProperties.getCaption("Campaign Name"));
//				campaignName.setValue(campaignDto.getName());
//				campaignName.setReadOnly(true);
//				campaignName.setWidthFull();
				
				Paragraph note = new Paragraph("Please select the population target group to generate population targets for this campaign");

				MultiSelectComboBox<AgeGroup> ageGroupsSelection = new MultiSelectComboBox<>("Poulation Target Categories");
				ageGroupsSelection.setItems(AgeGroup.AGE_0_4, AgeGroup.AGE_5_10, AgeGroup.AGE_4_23M);
				ageGroupsSelection.setItemLabelGenerator(item -> {
					if (item == AgeGroup.AGE_0_4) return "Target 0-59M";
					if (item == AgeGroup.AGE_5_10) return "Target 60-120M";
					if (item == AgeGroup.AGE_4_23M) return "Target 4-23M";
					return item.toString();
				});
				ageGroupsSelection.setWidthFull();
				ageGroupsSelection.setClearButtonVisible(true);

				Button selectAllBtn = new Button("Select All", event -> {
					ageGroupsSelection.setValue(Set.of(AgeGroup.AGE_0_4, AgeGroup.AGE_5_10, AgeGroup.AGE_4_23M));
				});
				selectAllBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

//				HorizontalLayout selectionHeader = new HorizontalLayout(new Span(I18nProperties.getCaption("Population Categories")), selectAllBtn);
//				selectionHeader.setWidthFull();
//				selectionHeader.setJustifyContentMode(JustifyContentMode.BETWEEN);
//				selectionHeader.setAlignItems(Alignment.CENTER);

				dialogLayout.add(note, ageGroupsSelection, selectAllBtn);
				genDialog.add(dialogLayout);

				Button confirmBtn = new Button(I18nProperties.getCaption("Generate Data"), event -> {
					if (ageGroupsSelection.getValue().isEmpty()) {
						Notification.show(I18nProperties.getString(Strings.infoSelectAtLeastOneCategory));
						return;
					}

					ConfirmDialog confirmGeneration = new ConfirmDialog();
					confirmGeneration.setHeader(I18nProperties.getCaption("Confirm Generation"));
					Paragraph textNote = new Paragraph("Are you sure you want to generate population data for the selected categories? This will overwrite existing data for this campaign.");
					confirmGeneration.add(textNote);
//					confirmGeneration.setText(I18nProperties.getString("Are you sure you want to generate population data for the selected categories? This will overwrite existing data for this campaign."));
					confirmGeneration.setCancelable(true);
					confirmGeneration.setConfirmText(I18nProperties.getCaption("Yes, Generate"));
					confirmGeneration.addConfirmListener(confirmEvent -> {
						genDialog.close();

						List<AgeGroup> selectedGroups = new ArrayList<>(ageGroupsSelection.getValue());
						if (FacadeProvider.getPopulationDataFacade().generatePopulationDataForCamapign(campaignDto, selectedGroups)) {
							Notification.show("Population Data Generation Complete For Campaign");
							
							CampaignLogDto log = new CampaignLogDto();

							// logging audit
							if (campaignDto.getUuid() != null) {
								log.setCampaign(campaignDto);
								String slectedString=""; 
								for (Iterator iterator = selectedGroups.iterator(); iterator.hasNext();) {
									AgeGroup ageGroup = (AgeGroup) iterator.next();
									String ageGroupString = ageGroup+"";
									
									
									System.out.println(ageGroupString + "ageGroupStringageGroupStringageGroupStringageGroupStringageGroupString");
									if (ageGroupString.equalsIgnoreCase("0--4")) { 
										ageGroupString = "Target 0-59M";
									}else if (ageGroupString.equalsIgnoreCase("5--10")) {
										ageGroupString = "Target 60-120M";
									}else if (ageGroupString.equalsIgnoreCase("AGE_4_23M")) 
										ageGroupString = "Target 4-23M";
	
									slectedString = slectedString + " " + ageGroupString  + " ";
	
								}
								log.setAction("Generated Population Targets Categories: " + slectedString);
							}

							FacadeProvider.getCampaignFacade().saveAuditLog(log);
							
						} else {
							Notification.show("Population Data Generation could not be Complete For Campaign");
						}
					});
					confirmGeneration.open();
				});
				
				Button cancelBtn = new Button(I18nProperties.getCaption(Captions.actionCancel), event -> genDialog.close());
				genDialog.getFooter().add(cancelBtn, confirmBtn);
				genDialog.open();
			} else {
				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

				Div textx = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));

				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				HorizontalLayout layoutx = new HorizontalLayout(textx, closeButton);
				layoutx.setAlignItems(Alignment.CENTER);
				notification.setPosition(Notification.Position.MIDDLE);
				notification.add(layoutx);
				notification.open();
			}
		});

		poplayout.add(btnGeneratePopulationData);
		poplayout.setHorizontalComponentAlignment(Alignment.CENTER, btnGeneratePopulationData);
	}

		Button btnImport = new Button(I18nProperties.getCaption(Captions.actionImport));// , e -> {

		btnImport.addClickListener(e -> {
			if (campaignDto != null) {

				startIntervalCallback();
				UI.getCurrent().addPollListener(event -> {
					if (callbackRunning) {
						UI.getCurrent().access(this::pokeFlow);
					} else {
						stopPullers();
					}
				});

				ImportPopulationDataDialog dialog = new ImportPopulationDataDialog(InfrastructureType.POPULATION_DATA,
						campaignDto);
				dialog.open();
			} else {
				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

				Div textx = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));

				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				HorizontalLayout layoutx = new HorizontalLayout(textx, closeButton);
				layoutx.setAlignItems(Alignment.CENTER);
				notification.setPosition(Notification.Position.MIDDLE);
				notification.add(layoutx);
				notification.open();
			}
		});

		poplayout.add(btnImport);
		poplayout.setHorizontalComponentAlignment(Alignment.CENTER, btnImport);

		if (campaignDto != null) {
			Button btnExport = new Button(I18nProperties.getCaption(Captions.export)); // poplayout.addComponent(btnExport);
			poplayout.add(btnExport);
			poplayout.setHorizontalComponentAlignment(Alignment.CENTER, btnExport);

			StreamResource populationDataStreamResource = DownloadFlowUtilityView
					.createPopulationDataExportResource(campaignDto.getUuid());

			populationDataStreamResource.setContentType("text/csv");
			populationDataStreamResource.setCacheTime(0);

			// Create an anchor to trigger the download
			Anchor downloadAnchor = new Anchor(populationDataStreamResource,
					I18nProperties.getCaption(Captions.downloadCsv));
			downloadAnchor.getElement().setAttribute("download", true);
			downloadAnchor.getStyle().set("display", "none");

			poplayout.add(downloadAnchor);

			// Simulate a click event on the hidden anchor to trigger the download

			btnExport.addClickListener(e -> {
				downloadAnchor.getElement().callJsFunction("click");
				Notification.show("downloading...");
			});
		}
//		StreamResource populationDataExportResource = DownloadUtil.createPopulationDataExportResource();
//		new FileDownloader(populationDataExportResource).extend(btnExportDummy);
		poplayout.addClickListener(e -> {
			if (campaignDto == null) {
				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

				Div textx = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));

				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				HorizontalLayout layoutx = new HorizontalLayout(textx, closeButton);
				layoutx.setAlignItems(Alignment.CENTER);

				notification.add(layoutx);
				notification.setPosition(Notification.Position.MIDDLE);
				notification.open();
			}
		});
		parentTab5.add(poplayout);
//		

		tabsheetParent.add(I18nProperties.getCaption(Captions.View_configuration_populationdata), parentTab5);
		tabsheetParent.setWidthFull();
		tabsheetParent.setId("tabsheetParent");

		openCloseCampaign = new Button();

		openCloseCampaign.setText(I18nProperties.getString(Strings.openCampaign));
		duplicateCampaign = new Button(I18nProperties.getString(Strings.duplicate));
		duplicateCampaign.addClickListener(e -> {
			duplicateCampaign();
//			updatePublishButtonText(isPublished);
		});

		deleteCampaign = new Button();
		deleteCampaign.setText(I18nProperties.getCaption(Captions.actionDelete));
		deleteCampaign.getStyle().set("background", "red");
		deleteCampaign.addClickListener(e -> {
			deleteCampaign();
		});

		logButton = new Button();
		logButton.setText(I18nProperties.getCaption(Captions.log));
		logButton.addClickListener(e -> {
			Notification.show("clicked");
			logEventMethod();
		});

		publishUnpublishCampaign = new Button();

		if (campaignDto != null) {
			isArchived = FacadeProvider.getCampaignFacade().isArchived(campaignDto.getUuid());
			isPublished = FacadeProvider.getCampaignFacade().isPublished(campaignDto.getUuid());
			isOpenClose = FacadeProvider.getCampaignFacade().isClosedd(campaignDto.getUuid());
			updateArchiveButtonText(isArchived);
			updatePublishButtonText(isPublished);
			updateOpenCloseButtonText(isOpenClose);
		} else {
			openCloseCampaign.setEnabled(false);
			archiveDearchive.setEnabled(false);
			duplicateCampaign.setEnabled(false);
			publishUnpublishCampaign.setEnabled(false);
		}

		publishUnpublishCampaign.addClickListener(e -> {
			publishUnpublish();
		});

		openCloseCampaign.addClickListener(e -> {
			openCloseCampaign();

		});

		if (isOpenClose) {
			archiveDearchive.addClickListener(e -> {
				archive();

			});
		} else {
			archiveDearchive.addClickListener(e -> {
				Notification.show("Please close present campaign before attemping to Archive")
						.addThemeVariants(NotificationVariant.LUMO_ERROR);

			});
		}

		discardChanges = new Button();
		discardChanges.setText(I18nProperties.getCaption(Captions.actionDiscard));
		discardChanges.addThemeVariants(ButtonVariant.LUMO_ERROR);

		discardChanges.addClickListener(e -> {
			discard();
		});

		saveChanges = new Button();

		saveChanges.setText(I18nProperties.getCaption(Captions.actionSave));

		saveChanges.addClickListener(e -> {

			areass.clear();
			region.clear();
			districts.clear();
			community.clear();
			popopulationDataDtoSet.clear();
			
			for (CampaignTreeGridDto item : treeGrid.getSelectionModel().getSelectedItems()) {

			    String level = item.getLevelAssessed();

			    if ("area".equals(level)) {
			        areass.add(
			            FacadeProvider.getAreaFacade().getAreaReferenceByUuid(item.getUuid())
			        );
			    } else if ("region".equals(level)) {
			        region.add(
			            FacadeProvider.getRegionFacade().getRegionReferenceByUuid(item.getUuid())
			        );
			    } else if ("district".equals(level)) {
			        districts.add(
			            FacadeProvider.getDistrictFacade().getDistrictReferenceByUuid(item.getUuid())
			        );
			    } else if ("cluster".equals(level)) {
			        // 1) Add to community set
			        CommunityReferenceDto clusterRef =
			            FacadeProvider.getCommunityFacade().getCommunityReferenceByUuid(item.getUuid());
			        community.add(clusterRef);

			        // 2) Create PopulationData entry for this cluster + campaign
			        PopulationDataDto popData = new PopulationDataDto();
			        popData.setCampaign(
			            FacadeProvider.getCampaignFacade().getReferenceByUuid(campaignDto.getUuid())
			        );
			        popData.setCommunity(clusterRef);

			        popopulationDataDtoSet.add(popData);
			    }
			}

			if (campaignDto != null) {
				campaignDto.setAreas((Set<AreaReferenceDto>) areass);
				campaignDto.setRegion((Set<RegionReferenceDto>) region);
				campaignDto.setDistricts((Set<DistrictReferenceDto>) districts);
				campaignDto.setPopulationdata((Set<PopulationDataDto>) popopulationDataDtoSet);
				campaignDto.setCommunity((Set<CommunityReferenceDto>) community);

				updateCampaignDatesFromForm();

				// Recalculate expiry for all phases
				recalculateExpiryForAllPhases();

			}

			validateAndSave(editMode);
		});

		HorizontalLayout leftFloat = new HorizontalLayout();
		HorizontalLayout rightFloat = new HorizontalLayout();
		leftFloat.setJustifyContentMode(JustifyContentMode.START);

		rightFloat.setJustifyContentMode(JustifyContentMode.END);

		if (campaignDto != null) {
			leftFloat.add(archiveDearchive, openCloseCampaign, duplicateCampaign, deleteCampaign, logButton);
		} else {
			publishUnpublishCampaign.setText(I18nProperties.getString(Strings.headingPublishCampaign));
			openCloseCampaign.setText("Open Campaign");
			leftFloat.add(openCloseCampaign, logButton);
		}

		leftFloat.setWidth("50%");

		rightFloat.add(discardChanges, saveChanges);
		rightFloat.setWidth("50%");

		actionButtonsLayout.add(leftFloat, rightFloat);
		actionButtonsLayout.setWidthFull();

		FormLayout formL = new FormLayout();
		HorizontalLayout header = new HorizontalLayout();
		header.add(creatingUser, creatingUuid, campaaignYear);

		formL.add(header, campaignName, round, preCampaignstartDate, preCampaignendDate, startDate, endDate,
				postCampaignstartDate, postCampaignendDate, description);

		formL.setColspan(header, 2);
		formL.setColspan(description, 2);
		formL.setColspan(hort, 2);
		formL.setColspan(leftFloat, 1);
		formL.setColspan(rightFloat, 1);
//		formL.setColspan(actionButtonsLayout, 2);
		round.addValueChangeListener(e -> {
			roundChange();
		});

		campaignBasics.getStyle().set("margin-top", "0px");
		campaignBasics.getStyle().set("margin-bottom", "0px");
		add(campaignBasics, formL, layoutParent, actionButtonsLayout); // ,

	}

	private void updateCampaignDatesFromForm() {
		if (campaignDto != null) {
			if (preCampaignstartDate.getValue() != null) {
				LocalDate localDate = preCampaignstartDate.getValue();
				campaignDto.setPreCampStartDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			}

			if (preCampaignendDate.getValue() != null) {
				LocalDate localDate = preCampaignendDate.getValue();
				campaignDto.setPreCampEndDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			}

			if (startDate.getValue() != null) {
				LocalDate localDate = startDate.getValue();
				campaignDto.setStartDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			}

			if (endDate.getValue() != null) {
				LocalDate localDate = endDate.getValue();
				campaignDto.setEndDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			}

			if (postCampaignstartDate.getValue() != null) {
				LocalDate localDate = postCampaignstartDate.getValue();
				campaignDto.setPostCampStartDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			}

			if (postCampaignendDate.getValue() != null) {
				LocalDate localDate = postCampaignendDate.getValue();
				campaignDto.setPostCampEndDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			}
		}
	}

	private void recalculateExpiryForAllPhases() {
		// Recalculate expiry for all phases based on current date values
		if (campaignDto != null) {
			updateCampaignDatesFromForm();
			Set<CampaignFormMetaWithExpReferenceDto> allExpiryData = new HashSet<>();

			// Update the campaignDto with current date values from the form
			if (preCampaignstartDate.getValue() != null) {
				LocalDate preCampaignLocalDate = preCampaignstartDate.getValue();
				Date preCampaignStartDate = Date
						.from(preCampaignLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
				campaignDto.setPreCampStartDate(preCampaignStartDate);
			}

			if (startDate.getValue() != null) {
				LocalDate intraCampaignLocalDate = startDate.getValue();
				Date intraCampaignStartDate = Date
						.from(intraCampaignLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
				campaignDto.setStartDate(intraCampaignStartDate);
			}

			if (postCampaignstartDate.getValue() != null) {
				LocalDate postCampaignLocalDate = postCampaignstartDate.getValue();
				Date postCampaignStartDate = Date
						.from(postCampaignLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
				campaignDto.setPostCampStartDate(postCampaignStartDate);
			}

			// Recalculate expiry for each phase
			if (preCampaignFormGridComponent != null) {
				preCampaignFormGridComponent.recalculateExpiryForPhase(campaignDto);
				allExpiryData.addAll(campaignDto.getCampaignFormMetaExpiry());

			}

			if (intraCampaignFormGridComponent != null) {
				intraCampaignFormGridComponent.recalculateExpiryForPhase(campaignDto);
				allExpiryData.addAll(campaignDto.getCampaignFormMetaExpiry());

			}

			if (postCampaignFormGridComponent != null) {
				postCampaignFormGridComponent.recalculateExpiryForPhase(campaignDto);
				allExpiryData.addAll(campaignDto.getCampaignFormMetaExpiry());

			}

			campaignDto.setCampaignFormMetaExpiryDto(allExpiryData);

			System.out.println("Total forms with expiry: " + allExpiryData.size());

		}
	}

	public HorizontalLayout configureTreeGrid(boolean isDeletePopulationData) {

		ComponentRenderer<Span, CampaignTreeGridDto> populationGenerate = new ComponentRenderer<>(input -> {

			NumberFormat arabicFormat = NumberFormat.getInstance();
			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
			} else {
				arabicFormat = NumberFormat.getInstance(new Locale("en"));
			}

			String value = String.valueOf(arabicFormat.format(input.getPopulationData()));
			Span label = new Span(value);
			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
			return label;
		});
 
		ComponentRenderer<Span, CampaignTreeGridDto> populationGenerate5_10 = new ComponentRenderer<>(input -> {

			NumberFormat arabicFormat = NumberFormat.getInstance();
			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
			} else {
				arabicFormat = NumberFormat.getInstance(new Locale("en"));
			}

			String value = String.valueOf(arabicFormat.format(input.getPopulationData5_10()));
			Span label = new Span(value);
			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
			return label;
		});
		
//		ComponentRenderer<Span, CampaignTreeGridDto> populationGenerate4_23M = new ComponentRenderer<>(input -> {
//
//			NumberFormat arabicFormat = NumberFormat.getInstance();
//			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
//				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
//			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
//				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
//			} else {
//				arabicFormat = NumberFormat.getInstance(new Locale("en"));
//			}
//
//			String value = String.valueOf(arabicFormat.format(input.getPopulationData4_23M()));
//			Span label = new Span(value);
//			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
//			return label;
//		});
		
		ComponentRenderer<Span, CampaignTreeGridDto> populationGenerate4_23M =
			    new ComponentRenderer<>(input -> {

			        Locale locale;
			        String lang = userProvider.getUser().getLanguage().toString();

			        if ("Pashto".equals(lang)) {
			            locale = new Locale("ps");
			        } else if ("Dari".equals(lang)) {
			            locale = new Locale("fa");
			        } else {
			            locale = Locale.ENGLISH;
			        }

			        NumberFormat format = NumberFormat.getInstance(locale);

			        Number population = input.getPopulationData4_23M();
			        String value = population != null ? format.format(population) : "0";

			        Span label = new Span(value);
			        label.getStyle().set("color", "var(--lumo-body-text-color)");
			        return label;
			});

		treeGrid = new TreeGrid<>();

		treeGrid.removeAllColumns();
		treeGrid.setWidthFull();
		
		
//		treeGrid.setItems(generateTreeGridData(), CampaignTreeGridDto::getDistrictData);

	    treeGrid.setItems(generateTreeGridData(), 
	    		
	    		
	            item -> {
	                // This is the key - you need to check the level and return appropriate children
	                if ("area".equals(item.getLevelAssessed())) {
	                    return item.getRegionData();
	                } else if ("region".equals(item.getLevelAssessed())) {
	                    return item.getDistrictData();
	                } else if ("district".equals(item.getLevelAssessed())) {
	                    return item.getClusterData(); // Districts have clusters in their districtData list
	                }  else {
	                    return Collections.emptyList(); // Clusters have no children
	                }
	            }
	        );
	    
//		treeGrid.setItems(generateTreeGridData(), CampaignTreeGridDto::getRegionData);

//		treeGrid.setItems(generateTreeGridData(), CampaignTreeGridDto::getDistrictData);

		treeGrid.setWidthFull();

		treeGrid.addHierarchyColumn(CampaignTreeGridDto::getName)
				.setHeader(I18nProperties.getCaption(Captions.Location));

//		treeGrid.addColumn(populationGenerateTotal)
//				.setHeader(I18nProperties.getCaption(Captions.View_configuration_populationdata_short) + " Total");

		treeGrid.addColumn(populationGenerate)
				.setHeader("Target (0-59M)");

		treeGrid.addColumn(populationGenerate5_10)
				.setHeader("Target (60-120M)");

		treeGrid.addColumn(populationGenerate4_23M)
		.setHeader("Target (4_23M)");
		
		treeGrid.addColumn(CampaignTreeGridDto::getDistrictModality).setHeader("Modality");

		treeGrid.addColumn(CampaignTreeGridDto::getDistrictStatus).setHeader("Status");
		
//		treeGrid.addColumn(CampaignTreeGridDto::getFloatStatus).setHeader("Float Status");

		// Add a column with checkboxes for selection

		ComponentRenderer<Component, CampaignTreeGridDto> componentRendererx = new ComponentRenderer<>(dto -> {
			if (dto.getLevelAssessed().equals("cluster")) {
				selectDistrictCheckbox = new Checkbox();
				// checkbox.setValue(treeGrid.getSelectionModel().isSelected(dto));
				selectDistrictCheckbox.addValueChangeListener(event -> {
					if (event.getValue()) {
//						treeGrid.select(dto);
//						populationDataUuid.add(dto.getUuid());
						deletelist.add(dto);

					} else {
//						treeGrid.deselect(dto);
						deletelist.remove(dto);

//						populationDataUuid.remove(dto.getUuid());

					}

				});
				return selectDistrictCheckbox;
			} else {
				return new Span();
			}

		});

		treeGrid.addColumn(componentRendererx).setHeader("Delete?");
	

		GridMultiSelectionModel<CampaignTreeGridDto> selectionModel = (GridMultiSelectionModel<CampaignTreeGridDto>) treeGrid
				.setSelectionMode(SelectionMode.MULTI);

		selectionModel.setSelectAllCheckboxVisibility(SelectAllCheckboxVisibility.HIDDEN);

		for (AreaReferenceDto root : campaignDto.getAreas()) {

			for (CampaignTreeGridDto areax : treeGrid.getTreeData().getRootItems()) {

				System.out.println(areax.getUuid() + "areax.getUuid()" + root.getUuid() + "root.getUuid(root.getUuid(");
				if (areax.getUuid().equals(root.getUuid())) {

					if (isDeletePopulationData) {

					} else {
						treeGrid.select(areax);
					}

				}

				for (RegionReferenceDto region_root : campaignDto.getRegion()) {

					for (CampaignTreeGridDto regionx : treeGrid.getTreeData().getChildren(areax)) {

						if (regionx.getUuid().equals(region_root.getUuid())) {

							if (isDeletePopulationData) {

							} else {
								treeGrid.select(regionx);
							}

						}

						for (DistrictReferenceDto district_root : campaignDto.getDistricts()) {

							for (CampaignTreeGridDto districtx : treeGrid.getTreeData().getChildren(regionx)) {

								if (districtx.getUuid().equals(district_root.getUuid())) {

									if (isDeletePopulationData) {

									} else {
										treeGrid.select(districtx);
//								        selectItemAndDescendantsWithClusters(districtx, true);


									}

								}

								for (CommunityReferenceDto cluster_root : campaignDto.getCommunity()) {

									for (CampaignTreeGridDto clustersx : treeGrid.getTreeData()
											.getChildren(districtx)) {
										if (clustersx.getUuid().equals(cluster_root.getUuid())) {

											if (isDeletePopulationData) {

											} else {
												treeGrid.select(clustersx);

											}

										}
									}
								}
							}
						}
					}
				}
			}
		}


		treeGrid.addItemClickListener(ee -> { // .addItemDoubleClickListener(ee -> {
System.out.println(ee.getItem().getUuid() + "uuid");
System.out.println(ee.getItem().getName() + "nameeeee");

			isSingleSelectClickItemLock = true;
			if (campaignDto != null && ee.getItem().getLevelAssessed().equals("cluster")) {

				if (ee.getItem().getPopulationData() != null) {

					System.out.println("Age Group from item click " + ee.getItem().getAgeGroup());
					Integer popDataAge0_4 = FacadeProvider.getPopulationDataFacade()
							.getDistrictPopulationByUuidAndAgeGroup(ee.getItem().getUuid(), campaignDto.getUuid(),
									"AGE_0_4");

					Integer popDataAge5_10 = FacadeProvider.getPopulationDataFacade()
							.getDistrictPopulationByUuidAndAgeGroup(ee.getItem().getUuid(), campaignDto.getUuid(),
									"AGE_5_10");
//					};
					
					Integer popDataAge4_23M = FacadeProvider.getPopulationDataFacade()
							.getDistrictPopulationByUuidAndAgeGroup(ee.getItem().getUuid(), campaignDto.getUuid(),
									"AGE_4_23M");
//					};

					String districtModality_0_4 = FacadeProvider.getPopulationDataFacade()
							.getDistrictModalityByUuidAndCampaignAndAgeGroup(ee.getItem().getUuid(),
									campaignDto.getUuid(), "AGE_0_4");
					String districtStatus_0_4 = FacadeProvider.getPopulationDataFacade()
							.getDistrictStatusByCampaign(ee.getItem().getUuid(), campaignDto.getUuid(), "AGE_0_4");
					
		
					createDialogBasics(ee.getItem().getUuid(), ee.getItem().getPopulationData(), ee.getItem().getName(),
							"AGE_0_4", campaignDto, ee.getItem(), popDataAge0_4, popDataAge5_10, popDataAge4_23M, districtModality_0_4,
							districtStatus_0_4);
				}

				else if (ee.getItem().getPopulationData5_10() != null) {
					System.out.println("Age Group from item click " + ee.getItem().getAgeGroup());

					Integer popDataAge5_10 = FacadeProvider.getPopulationDataFacade()
							.getDistrictPopulationByUuidAndAgeGroup(ee.getItem().getUuid(), campaignDto.getUuid(),
									"AGE_5_10");

					Integer popDataAge0_4 = FacadeProvider.getPopulationDataFacade()
							.getDistrictPopulationByUuidAndAgeGroup(ee.getItem().getUuid(), campaignDto.getUuid(),
									"AGE_0_4");
					
					Integer popDataAge4_23M = FacadeProvider.getPopulationDataFacade()
							.getDistrictPopulationByUuidAndAgeGroup(ee.getItem().getUuid(), campaignDto.getUuid(),
									"AGE_4_23M");
//					};

					String districtModality_5_10 = FacadeProvider.getPopulationDataFacade()
							.getDistrictModalityByUuidAndCampaignAndAgeGroup(ee.getItem().getUuid(),
									campaignDto.getUuid(), "AGE_5_10");

					String districtStatus_5_10 = FacadeProvider.getPopulationDataFacade()
							.getDistrictStatusByCampaign(ee.getItem().getUuid(), campaignDto.getUuid(), "AGE_5_10");

//					if (popDataAge0_4 == null) {
//						popDataAge0_4 = 0;
//					}

					createDialogBasics(ee.getItem().getUuid(), ee.getItem().getPopulationData(), ee.getItem().getName(),
							"AGE_5_10", campaignDto, ee.getItem(), popDataAge0_4, popDataAge5_10, popDataAge4_23M, districtModality_5_10,
							districtStatus_5_10);

//					createDialogBasics(ee.getItem().getUuid(), ee.getItem().getPopulationData(), ee.getItem().getName(),
//							ee.getItem().getAgeGroup(), campaignDto, ee.getItem(), popDataAge5_10,
//							districtModality_5_10, districtStatus_5_10);
				} 				else if (ee.getItem().getPopulationData4_23M() != null) {
					System.out.println("Age Group from item click " + ee.getItem().getAgeGroup());

					Integer popDataAge5_10 = FacadeProvider.getPopulationDataFacade()
							.getDistrictPopulationByUuidAndAgeGroup(ee.getItem().getUuid(), campaignDto.getUuid(),
									"AGE_5_10");

					Integer popDataAge0_4 = FacadeProvider.getPopulationDataFacade()
							.getDistrictPopulationByUuidAndAgeGroup(ee.getItem().getUuid(), campaignDto.getUuid(),
									"AGE_0_4");
					
					Integer popDataAge4_23M = FacadeProvider.getPopulationDataFacade()
							.getDistrictPopulationByUuidAndAgeGroup(ee.getItem().getUuid(), campaignDto.getUuid(),
									"AGE_4_23M");
//					};

					String districtModality_4_23M = FacadeProvider.getPopulationDataFacade()
							.getDistrictModalityByUuidAndCampaignAndAgeGroup(ee.getItem().getUuid(),
									campaignDto.getUuid(), "AGE_4_23M");

					String districtStatus_4_23M = FacadeProvider.getPopulationDataFacade()
							.getDistrictStatusByCampaign(ee.getItem().getUuid(), campaignDto.getUuid(), "AGE_4_23M");

 
					createDialogBasics(ee.getItem().getUuid(), ee.getItem().getPopulationData(), ee.getItem().getName(),
							"AGE_4_23M", campaignDto, ee.getItem(), popDataAge0_4, popDataAge5_10, popDataAge4_23M, districtModality_4_23M,
							districtStatus_4_23M);

 
				} else {

				}

			}
		});
		
		treeGrid.asMultiSelect().addSelectionListener(event -> {
		    // Added (checked) items: select item + all descendants
		    for (CampaignTreeGridDto added : event.getAddedSelection()) {
		        if (!isDeletePopulationData) {
		            selectItemAndDescendantsWithClusters(added, true);
		        }
		    }

		    // Removed (unchecked) items: deselect item + all descendants
		    for (CampaignTreeGridDto removed : event.getRemovedSelection()) {
		        if (!isDeletePopulationData) {
		            selectItemAndDescendantsWithClusters(removed, false);
		        }
		    }
		});
 
		for (CampaignTreeGridDto ftg : treeGrid.getSelectionModel().getSelectedItems()) {
			ftg.setIsClicked(777L);
		}

		HorizontalLayout assocCampaignLayout = new HorizontalLayout();
		assocCampaignLayout.setWidthFull();

		assocCampaignLayout.add(treeGrid, configurePopulationPopEdit());
		return assocCampaignLayout;

	}
	
	private void selectItemAndDescendantsWithClusters(CampaignTreeGridDto item, boolean select) {
	    if (select) {
	        treeGrid.select(item);
	    } else {
	        treeGrid.deselect(item);
	    }
	    
	    // Get children based on level
	    List<CampaignTreeGridDto> children = getChildrenForSelection(item);
	    
	    // Recursively select/deselect all children including clusters
	    for (CampaignTreeGridDto child : children) {
	        selectItemAndDescendantsWithClusters(child, select);
	    }
	}

	private List<CampaignTreeGridDto> getChildrenForSelection(CampaignTreeGridDto item) {
	    if ("area".equals(item.getLevelAssessed())) {
	        return item.getRegionData();
	    } else if ("region".equals(item.getLevelAssessed())) {
	        return item.getDistrictData();
	    } else if ("district".equals(item.getLevelAssessed())) {
	        // Return both districts and clusters from district data
	        return item.getClusterData();
	    }
	    return Collections.emptyList(); // Clusters have no children
	}

	private Component configurePopulationPopEdit() {
		VerticalLayout formx = populationEditorForm();
		formx.getStyle().remove("width");
		HorizontalLayout content = new HorizontalLayout(treeGrid, formx);
		content.setFlexGrow(4, treeGrid);
		content.setFlexGrow(0, formx);
		content.addClassNames("content");
//		content.setSizeFull();
		return content;
	}

	private VerticalLayout populationEditorForm() {
		VerticalLayout vert = new VerticalLayout();

		formx = new FormLayout();

		Button plusButton = new Button(new Icon(VaadinIcon.PLUS));
		plusButton.addThemeVariants(ButtonVariant.LUMO_ICON);
		plusButton.setTooltipText(I18nProperties.getCaption(Captions.addNewForm));

		Button deleteButton = new Button(new Icon(VaadinIcon.DEL_A));
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON);
		deleteButton.getStyle().set("background-color", "red!important");
		deleteButton.setTooltipText(I18nProperties.getCaption("Delete Population Data"));

		Button saveButton = new Button(I18nProperties.getCaption(Captions.actionAdd), new Icon(VaadinIcon.CHECK));

		Button cancelButton = new Button(I18nProperties.getCaption(Captions.actionCancel),
				new Icon(VaadinIcon.REFRESH));

		ComboBox<AreaReferenceDto> regionFilter = new ComboBox<AreaReferenceDto>();
		regionFilter.setLabel(I18nProperties.getCaption(Captions.area));
		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());

		ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>(I18nProperties.getCaption(Captions.region));
		provinceFilter.setPlaceholder(I18nProperties.getCaption(Captions.regionAllRegions));
		provinceFilter.setClearButtonVisible(true);
		provinceFilter.getStyle().set("width", "145px !important");

		ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>(I18nProperties.getCaption(Captions.district));
		districtFilter.setPlaceholder(I18nProperties.getCaption(Captions.districtAllDistricts));
		districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
		districtFilter.getStyle().set("width", "145px !important");
		
		ComboBox<CommunityReferenceDto> clusterFilter = new ComboBox<>(I18nProperties.getCaption(Captions.community));
		clusterFilter.setPlaceholder(I18nProperties.getCaption(Captions.districtAllDistricts));
		clusterFilter.setItems(new ArrayList<>());
//		FacadeProvider.getCommunityFacade().getAllActiveByDistrict()
		clusterFilter.getStyle().set("width", "145px !important");
		clusterFilter.setVisible(false);
		
		
		List<PopulationDataDto> populationDataList = new ArrayList<PopulationDataDto>();

		IntegerField popData0_4 = new IntegerField("Target 0-59M");
//				I18nProperties.getCaption(Captions.District_population) + " Age 0_4");

		IntegerField popDataAge5_10 = new IntegerField("Target 60-120M");
//				I18nProperties.getCaption(Captions.District_population) + " Age 5_10");
		
		IntegerField popDataAge4_23M = new IntegerField("Target 4_23M");
		popDataAge4_23M.setMin(0);
		popData0_4.setMin(0);
		popDataAge5_10.setMin(0);
		
		popDataAge4_23M.setErrorMessage("Negative Values not Allowed");
		popData0_4.setErrorMessage("Negative Values not Allowed");
		popDataAge5_10.setErrorMessage("Negative Values not Allowed");

		ComboBox<String> districtModality = new ComboBox<String>("Modality");
		districtModality.setItems("H2H", "M2M", "S2S", "HF2HF", "Mixed");

		ComboBox<String> districtStatus = new ComboBox<String>("Status");
		districtStatus.setItems("Additional", "Additional & Cold", "Cold", "Full Cluster", "HRMP only", "Partial",
				"Not Targted", "On Hold");

		popData0_4.setVisible(false);
		popDataAge5_10.setVisible(false);
		popDataAge4_23M.setVisible(false);
		districtModality.setVisible(false);
		districtStatus.setVisible(false);

		popData0_4.setValue(null);
		popDataAge5_10.setValue(null);
		popDataAge4_23M.setValue(null);
		districtModality.setValue(null);
		districtStatus.setValue(null);

		regionFilter.addValueChangeListener(e -> {
			if (regionFilter.getValue() != null) {
				AreaReferenceDto area = e.getValue();

				provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid()));

			} else {
//			criteria.area(null);
//			refreshGridData();
			}

		});

		provinceFilter.addValueChangeListener(e -> {
			if (provinceFilter.getValue() != null) {

				if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
					districtFilter.setItems(
							FacadeProvider.getDistrictFacade().getAllActiveByRegionPashto(e.getValue().getUuid()));
				} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
					districtFilter.setItems(
							FacadeProvider.getDistrictFacade().getAllActiveByRegionDari(e.getValue().getUuid()));
				} else {
					districtFilter
							.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid()));
				}
				RegionReferenceDto province = e.getValue();

			} else {

			}

		});

		districtFilter.addValueChangeListener(e -> {
			if (districtFilter.getValue() != null) {

				List<CommunityReferenceDto> allClusters =  FacadeProvider.getCommunityFacade().getAllActiveByDistrict(e.getValue().getUuid());
				if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
					clusterFilter.setItemLabelGenerator(itm -> {
						CommunityReferenceDto dcfv = (CommunityReferenceDto) itm;
						return dcfv.getNumber() + " | " + dcfv.getPs_af();
					});
					allClusters.sort(Comparator.comparing(CommunityReferenceDto::getNumber));
					clusterFilter.setItems(allClusters);
				} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
					clusterFilter.setItemLabelGenerator(itm -> {
						CommunityReferenceDto dcfv = (CommunityReferenceDto) itm;
						return dcfv.getNumber() + " | " + dcfv.getFa_af();
					});
					allClusters.sort(Comparator.comparing(CommunityReferenceDto::getNumber));
					clusterFilter.setItems(allClusters);
				} else {
					
					clusterFilter.setItemLabelGenerator(itm -> {
						CommunityReferenceDto dcfv = (CommunityReferenceDto) itm;
						return dcfv.getNumber() + " | " + dcfv.getCaption();
					});
					allClusters.sort(Comparator.comparing(CommunityReferenceDto::getNumber));
					clusterFilter.setItems(allClusters);
					
//					clusterFilter
//							.setItems(
//									FacadeProvider.getCommunityFacade().getAllActiveByDistrict(e.getValue().getUuid()));

//									FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid()));
				}
				
				clusterFilter.setVisible(true);

				DistrictReferenceDto district = e.getValue();

			} else {
//				criteria.district(null);
//				refreshGridData();
			}
		});
		
		clusterFilter.addValueChangeListener(e -> {
			if (clusterFilter.getValue() != null) {
//				filteredDataProvider.setFilter(criteria);
					CommunityReferenceDto cluster = e.getValue();
					popData0_4.setVisible(true);
					popDataAge5_10.setVisible(true);
					popDataAge4_23M.setVisible(true);
					districtModality.setVisible(true);
					districtStatus.setVisible(true);

					for (PopulationDataDto xx : FacadeProvider.getPopulationDataFacade()
							.getClusterPopulationByTypeUsingUUIDs(clusterFilter.getValue().getUuid(),
									campaignDto.getUuid(), AgeGroup.AGE_0_4)) {
						popData0_4.setValue(xx.getPopulation());
						System.out.println(xx.getPopulation() + "55555555555555555555555555555555555555555555");
					}

					for (PopulationDataDto xx : FacadeProvider.getPopulationDataFacade()
							.getClusterPopulationByTypeUsingUUIDs(clusterFilter.getValue().getUuid(),
									campaignDto.getUuid(), AgeGroup.AGE_5_10)) {
						popDataAge5_10.setValue(xx.getPopulation());
						System.out.println(xx.getPopulation() + "666666666666666666666666666666666666666666666");
					}
					
					for (PopulationDataDto xx : FacadeProvider.getPopulationDataFacade()
							.getClusterPopulationByTypeUsingUUIDs(clusterFilter.getValue().getUuid(),
									campaignDto.getUuid(), AgeGroup.AGE_4_23M)) {
						popDataAge4_23M.setValue(xx.getPopulation());
						System.out.println(xx.getPopulation() + "666666666666666666666666666666666666666666666");
					}

//					criteria.district(district);
//					refreshGridData();

				} else {
//					criteria.district(null);
//					refreshGridData();
				}
			});

		HorizontalLayout buttonLay = new HorizontalLayout(plusButton, deleteButton);

		HorizontalLayout buttonAfterLay = new HorizontalLayout(saveButton, cancelButton);
		buttonAfterLay.getStyle().set("flex-wrap", "wrap");
		buttonAfterLay.setJustifyContentMode(JustifyContentMode.END);
		buttonLay.setSpacing(true);

		cancelButton.addClickListener(ees -> {
			CampaignFormMetaReferenceDto newcampform = new CampaignFormMetaReferenceDto();

			formx.setVisible(false);
			buttonAfterLay.setVisible(false);
			saveButton.setText(I18nProperties.getCaption(Captions.actionSave));

		});

		saveButton.addClickListener(e -> {

			System.out.println(popDataAge5_10.getValue() + "GGGGGGGGGGGG" + popData0_4.getValue());

			if (popData0_4.isInvalid() || popDataAge5_10.isInvalid() || popDataAge4_23M.isInvalid()) {
				Notification.show("Negative Values are not allowed").addThemeVariants(NotificationVariant.LUMO_ERROR);
				return;
			}

			if (clusterFilter.getValue() != null) {

				System.out.println(FacadeProvider.getCampaignFacade().getReferenceByUuid(campaignDto.getUuid())
						+ "<<<Campaignuid " + " district uuid" + FacadeProvider.getDistrictFacade()
								.getDistrictReferenceByUuid(districtFilter.getValue().getUuid())
						+ "modality " + districtModality.getValue());

				Date collectionDate = new Date();

				PopulationDataDto newPopulationData_10 = PopulationDataDto.build(collectionDate);
				PopulationDataDto newPopulationData_0_4 = PopulationDataDto.build(collectionDate);
				PopulationDataDto newPopulationData_423M = PopulationDataDto.build(collectionDate);


				List<PopulationDataDto> itirationList = new ArrayList<PopulationDataDto>();
				itirationList.add(newPopulationData_0_4);
				itirationList.add(newPopulationData_10);
				itirationList.add(newPopulationData_423M);

				System.out.println("Age Group rom ope  is age 0_4");
				if (popDataAge4_23M.getValue() != null) {
					newPopulationData_423M
							.setCampaign(FacadeProvider.getCampaignFacade().getReferenceByUuid(campaignDto.getUuid()));
					newPopulationData_423M.setRegion(FacadeProvider.getRegionFacade()
							.getRegionReferenceByUuid(provinceFilter.getValue().getUuid()));
					newPopulationData_423M.setDistrict(FacadeProvider.getDistrictFacade()
							.getDistrictReferenceByUuid(districtFilter.getValue().getUuid()));
					newPopulationData_423M.setCommunity(FacadeProvider.getCommunityFacade().getCommunityReferenceByUuid(
							clusterFilter.getValue().getUuid()));
					newPopulationData_423M.setModality(districtModality.getValue());
					newPopulationData_423M.setDistrictStatus(districtStatus.getValue());
					newPopulationData_423M.setAgeGroup(AgeGroup.AGE_4_23M);
					newPopulationData_423M.setPopulation(popDataAge4_23M.getValue());

					populationDataList.add(newPopulationData_423M);

				}
				
				if (popData0_4.getValue() != null) {
					newPopulationData_0_4
							.setCampaign(FacadeProvider.getCampaignFacade().getReferenceByUuid(campaignDto.getUuid()));
					newPopulationData_0_4.setRegion(FacadeProvider.getRegionFacade()
							.getRegionReferenceByUuid(provinceFilter.getValue().getUuid()));
					newPopulationData_0_4.setDistrict(FacadeProvider.getDistrictFacade()
							.getDistrictReferenceByUuid(districtFilter.getValue().getUuid()));
					newPopulationData_0_4.setCommunity(FacadeProvider.getCommunityFacade().getCommunityReferenceByUuid(
							clusterFilter.getValue().getUuid()));
					newPopulationData_0_4.setModality(districtModality.getValue());
					newPopulationData_0_4.setDistrictStatus(districtStatus.getValue());
					newPopulationData_0_4.setAgeGroup(AgeGroup.AGE_0_4);
					newPopulationData_0_4.setPopulation(popData0_4.getValue());

					populationDataList.add(newPopulationData_0_4);

				}

				if (popDataAge5_10.getValue() != null) {
					newPopulationData_10
							.setCampaign(FacadeProvider.getCampaignFacade().getReferenceByUuid(campaignDto.getUuid()));
					newPopulationData_10.setRegion(FacadeProvider.getRegionFacade()
							.getRegionReferenceByUuid(provinceFilter.getValue().getUuid()));
					newPopulationData_10.setDistrict(FacadeProvider.getDistrictFacade()
							.getDistrictReferenceByUuid(districtFilter.getValue().getUuid()));
					newPopulationData_10.setCommunity(FacadeProvider.getCommunityFacade().getCommunityReferenceByUuid(
							clusterFilter.getValue().getUuid()));
					newPopulationData_10.setModality(districtModality.getValue());
					newPopulationData_10.setDistrictStatus(districtStatus.getValue());
					newPopulationData_10.setAgeGroup(AgeGroup.AGE_5_10);
					newPopulationData_10.setPopulation(popDataAge5_10.getValue());

					populationDataList.add(newPopulationData_10);

					ConfirmDialog confirmationDialog = new ConfirmDialog();

					confirmationDialog.setCancelable(true);
					confirmationDialog.setRejectable(false);
					confirmationDialog.addCancelListener(confirmationDialogx -> confirmationDialog.close());
					confirmationDialog.setCancelButtonTheme(ValoTheme.NOTIFICATION_ERROR);
					confirmationDialog.setConfirmText(I18nProperties.getCaption(Captions.actionOkay));
					confirmationDialog.setCancelText(I18nProperties.getCaption("Cancel"));

					confirmationDialog.addConfirmListener(confirmationDialogx -> {

						System.out.println(
								popDataAge5_10.getValue() + "GGGGGGGGGGGG IN THE TRY " + popData0_4.getValue());

						try {
							
							if (popDataAge4_23M.getValue() != null) {
								try {
									FacadeProvider.getPopulationDataFacade().savePopulationData(populationDataList);
								} catch (Exception ex) {
									System.out.println(ex + "Exception");
								} finally {
									populationDataList.remove(newPopulationData_423M);

								}

							}
							
							if (popData0_4.getValue() != null) {
								try {
									FacadeProvider.getPopulationDataFacade().savePopulationData(populationDataList);
								} catch (Exception ex) {
									System.out.println(ex + "Exception");
								} finally {
									populationDataList.remove(newPopulationData_0_4);

								}

							}
							if (popDataAge5_10.getValue() != null) {
								try {
									FacadeProvider.getPopulationDataFacade().savePopulationData(populationDataList);
								} catch (Exception ex) {
									System.out.println(ex + "Exception");
								} finally {
									populationDataList.remove(newPopulationData_10);

								}
							}

						} catch (Exception exception) {

							System.out.println("excetion Caught while saving population data");

						} finally {

							confirmationDialog.close();

							treeGrid.getDataProvider().refreshAll();// refreshItem(campaignTreeGridDto);
																	// add , true
																	// to
																	// regresh
																	// children
							Notification.show(I18nProperties.getString(Strings.dataSavedSuccessfully));

							regionFilter.clear();
							provinceFilter.clear();
							districtFilter.clear();
							clusterFilter.clear();
							popData0_4.clear();
							popDataAge5_10.clear();
							popDataAge4_23M.clear();
							districtModality.clear();
							districtStatus.clear();
							formx.setVisible(false);
							buttonAfterLay.setVisible(false);
						}

					});

					confirmationDialog.setText("Are you sure you want to add the population data for the District "
							+ districtFilter.getValue() + "  ?. \n"
							+ " Please note that the added population data would only be available after reloading Campaign Form");
					confirmationDialog.setHeader("Add Population Data");
					confirmationDialog.open();

				} else {
					Notification.show("Please Select a District");
				}
			}

		});

		formx.add(regionFilter, provinceFilter, districtFilter, clusterFilter, popData0_4, popDataAge5_10,
				popDataAge4_23M, districtModality, districtStatus);
		formx.setColspan(regionFilter, 1);
		formx.setColspan(provinceFilter, 1);
		formx.setColspan(districtFilter, 1);
		formx.setColspan(clusterFilter, 1);
		formx.setColspan(popData0_4, 1);
		formx.setColspan(popDataAge5_10, 1);
		formx.setColspan(popDataAge4_23M, 1);
		formx.setColspan(districtModality, 1);
		formx.setColspan(districtStatus, 2);

		formx.setVisible(false);
		buttonAfterLay.setVisible(false);

		plusButton.addClickListener(ce ->

		{
			deleteButton.setVisible(false);

			formx.setVisible(true);

			buttonAfterLay.setVisible(true);
			saveButton.setText(I18nProperties.getCaption(Captions.actionAdd));
		});

		deleteButton.addClickListener(delete -> {
			if (!deletelist.isEmpty()) {

				// Open a confirmation dialog to confirm deletion
				ConfirmDialog confirmationDialog = new ConfirmDialog();
				confirmationDialog.setHeader("Delete Population Data");
				long distinctCount = deletelist.stream().map(CampaignTreeGridDto::getId) // Extract the ID or
																							// any unique
																							// property
						.distinct() // Eliminate duplicates
						.count(); // Count distinct elements
				confirmationDialog.setText("Are you sure you want to delete the population data for " + distinctCount
						+ " selected districts?");
				confirmationDialog.setCancelable(true);
				confirmationDialog.setRejectable(false);
				confirmationDialog.setConfirmText("Delete");
				confirmationDialog.setCancelText("Cancel");
				confirmationDialog.setCancelButtonTheme("error");

				confirmationDialog.addCancelListener(e -> {
					treeGrid.getDataProvider().refreshAll();
					deletelist.clear();
					confirmationDialog.close();

//							selectDistrictCheckbox.clear();
//							deletelist = new ArrayList<>();
//							confirmationDialog.close();	
				});

				confirmationDialog.addConfirmListener(event -> {
					try {
						List<Long> clusterIDs = new ArrayList<>();
						for (CampaignTreeGridDto treeData : deletelist) {
							clusterIDs.add(treeData.getId());

						}

						FacadeProvider.getPopulationDataFacade().deletePopulationDataByClusters(clusterIDs,
								creatingUuid.getValue());

					} catch (Exception e) {
						Notification.show("Error deleting population data: " + e.getMessage(), 10000,
								Notification.Position.MIDDLE);
					} finally {
						Notification.show(
								"Population data deleted successfully. Please re-open the Campaign Basics form to recieve updated Population Data Table.",
								5000, Notification.Position.MIDDLE);
//								Notification.show("Population data deleted successfully.");
						treeGrid.getDataProvider().refreshAll();
						deletelist.clear();
						confirmationDialog.close();
//								ageGroupSelectionDialog.close();
					}
				});

				confirmationDialog.open();

			} else {
				// Handle the case when no selection is made (optional)
				Notification.show("Please select an age group before confirming.", 3000, Notification.Position.MIDDLE);
			}
		});

		vert.add(buttonLay, formx, buttonAfterLay);

		return vert;
	}

	private void createDialogBasics(String Uuid, Long selectedPopData, String name_, String ageGroup,
			CampaignDto campaignDto_, CampaignTreeGridDto campaignTreeGridDto, Integer populationByAgeGroup,
			Integer populationByAgeGroup5_10, Integer populationByAgeGroup4_23M, String districtModalityByAgeGroup, String districtStatusByAgeGroup) {
		Dialog dialog = new Dialog();

		dialog.removeAll();

		dialog.setHeaderTitle(I18nProperties.getCaption(Captions.editing) + " " + name_);

		Button saveButton = createSaveButton();
		Button deleteButton = createDeleteButton();
		Button cancelButton = new Button(I18nProperties.getCaption(Captions.actionCancel), e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.getFooter().add(deleteButton);
		dialog.getFooter().add(saveButton);

		VerticalLayout dialogLayout = createDialogLayoutByAge(name_, ageGroup, selectedPopData, saveButton, Uuid,
				campaignDto_, dialog, campaignTreeGridDto, populationByAgeGroup, populationByAgeGroup5_10, populationByAgeGroup4_23M,
				districtModalityByAgeGroup, districtStatusByAgeGroup);

		dialog.add(dialogLayout);

		add(dialog);

		dialog.open();

		isSingleSelectClickItemLock = false;
	}

	private static VerticalLayout createDialogLayoutByAge(String name_, String ageGroup, Long selectedPopData,
			Button saveButton, String Uuid, CampaignDto campaignDto_, Dialog dialog,
			CampaignTreeGridDto campaignTreeGridDto, Integer populationByAgeGroup, Integer populationByAgeGroup5_10,Integer populationByAgeGroup4_23M,
			String districtModalityByAgeGroup, String districtStatusByAgeGroup) {

		TextField district = new TextField(I18nProperties.getCaption(Captions.community));
		district.setValue(name_);
		district.setReadOnly(true);

		IntegerField popData = new IntegerField(I18nProperties.getCaption(Captions.District_population) + " " + "0_4");

		IntegerField popData5_10 = new IntegerField(
				I18nProperties.getCaption(Captions.District_population) + " " + "5_10");
		
		IntegerField popData4_23M = new IntegerField(
				I18nProperties.getCaption(Captions.District_population) + " " + "4_23M");


		ComboBox<String> districtModalityCombo = new ComboBox<String>("Modality");
		districtModalityCombo.setItems("H2H", "M2M", "S2S", "HF2HF", "Mixed");

		ComboBox<String> districtStatusCombo = new ComboBox<String>("Campaign Status");
		districtStatusCombo.setItems("Additional", "Additional & Cold", "Cold", "Full Cluster", "HRMP only", "Partial",
				"Not Targted", "On Hold");

		districtModalityCombo.addValueChangeListener(e -> {
			if (e.getValue() != null) {
//				districtModalityCombo.setValue(e.getValue());
				districtModalityCombo.setValue(e.getValue());
			}
		});

		districtStatusCombo.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				districtStatusCombo.setValue(e.getValue());
			}
		});

		popData.setWidthFull();
		popData5_10.setWidthFull();
		popData4_23M.setWidthFull();
		
		popData4_23M.setMin(0);
		popData5_10.setMin(0);
		popData.setMin(0);

		popData4_23M.setErrorMessage("Negative Values not Allowed");
		popData5_10.setErrorMessage("Negative Values not Allowed");
		popData.setErrorMessage("Negative Values not Allowed");


		districtStatusCombo.setWidthFull();
		districtModalityCombo.setWidthFull();

		if (populationByAgeGroup != null) {
			popData.setValue(populationByAgeGroup);
		} else {
			popData.setValue(null);
		}

		if (populationByAgeGroup5_10 != null) {
			popData5_10.setValue(populationByAgeGroup5_10);
		} else {
			popData5_10.setValue(null);
		}
		
		if (populationByAgeGroup4_23M != null) {
			popData4_23M.setValue(populationByAgeGroup4_23M);
		} else {
			popData4_23M.setValue(null);
		}

		if (districtModalityByAgeGroup != null) {
			districtModalityCombo.setValue(districtModalityByAgeGroup);
		} else {
			districtModalityCombo.setValue("");
		}

		if (districtStatusByAgeGroup != null) {
			districtStatusCombo.setValue(districtStatusByAgeGroup);
		} else {
			districtStatusCombo.setValue("");
		}

		saveButton.addClickListener(e -> {
			if (popData.isInvalid() || popData5_10.isInvalid() || popData4_23M.isInvalid()) {
				Notification.show("Negative Values are not allowed").addThemeVariants(NotificationVariant.LUMO_ERROR);
				return;
			}
			List<PopulationDataDto> popDataDto;
			List<PopulationDataDto> popDataDto5_10;
			List<PopulationDataDto> popDataDto4_23M;
			List<PopulationDataDto> district_Modality;
			List<PopulationDataDto> district_Status;

			List<PopulationDataDto> popDataDtotoList = new ArrayList<>();

			System.out.println(ageGroup + " age group from save click listener ");
			if (ageGroup != null) {

				popDataDto = FacadeProvider.getPopulationDataFacade().getClusterPopulationByTypeUsingUUIDs(Uuid,
						campaignDto_.getUuid(), AgeGroup.AGE_0_4);

				popDataDto5_10 = FacadeProvider.getPopulationDataFacade().getClusterPopulationByTypeUsingUUIDs(Uuid,
						campaignDto_.getUuid(), AgeGroup.AGE_5_10);
				
				popDataDto4_23M = FacadeProvider.getPopulationDataFacade().getClusterPopulationByTypeUsingUUIDs(Uuid,
						campaignDto_.getUuid(), AgeGroup.AGE_4_23M);

				district_Modality = FacadeProvider.getPopulationDataFacade()
						.getDistrictModalityByclusterUUIDsandCampaignUUIdAndAgeGroup(Uuid, campaignDto_.getUuid(),
								ageGroup.equals("Age_0_4") ? AgeGroup.AGE_0_4 : ageGroup.equals("AGE_5_10") ? AgeGroup.AGE_5_10 : ageGroup.equals("AGE_4_23M") ? AgeGroup.AGE_4_23M : AgeGroup.AGE_0_4);

				district_Status = FacadeProvider.getPopulationDataFacade()
						.getDistrictModalityByclusterUUIDsandCampaignUUIdAndAgeGroup(Uuid, campaignDto_.getUuid(),
								ageGroup.equals("Age_0_4") ? AgeGroup.AGE_0_4 : ageGroup.equals("AGE_5_10") ? AgeGroup.AGE_5_10 : ageGroup.equals("AGE_4_23M") ? AgeGroup.AGE_4_23M : AgeGroup.AGE_0_4);

//								ageGroup.equals("Age_0_4") ? AgeGroup.AGE_0_4
//										: ageGroup.equals("AGE_5_10") ? AgeGroup.AGE_5_10 : AgeGroup.AGE_0_4);

				if (popData.getValue() != null
						&& (districtModalityCombo.getValue() != null || districtModalityCombo.getValue() != "")
						&& (districtStatusCombo.getValue() != null || districtStatusCombo.getValue() != "")) {

					popDataDto.get(0).setPopulation(popData.getValue());

					popDataDto.get(0).setModality(districtModalityCombo.getValue().toString());

					popDataDto.get(0).setDistrictStatus(districtStatusCombo.getValue().toString());

					popDataDtotoList.add(popDataDto.get(0));

//					System.out.println( popData5_10 + "value from popu;ation data for GE 5_10 " +  populationByAgeGroup5_10);
					if (populationByAgeGroup5_10 != null) {

						popDataDto5_10.get(0).setPopulation(popData5_10.getValue());

						popDataDto5_10.get(0).setModality(districtModalityCombo.getValue().toString());

						popDataDto5_10.get(0).setDistrictStatus(districtStatusCombo.getValue().toString());

						popDataDtotoList.add(popDataDto5_10.get(0));

					}
					
					if (populationByAgeGroup4_23M != null) {

						popDataDto4_23M.get(0).setPopulation(popData4_23M.getValue());

						popDataDto4_23M.get(0).setModality(districtModalityCombo.getValue().toString());

						popDataDto4_23M.get(0).setDistrictStatus(districtStatusCombo.getValue().toString());

						popDataDtotoList.add(popDataDto4_23M.get(0));

					}

					ConfirmDialog confirmationDialog = new ConfirmDialog();

					confirmationDialog.setCancelable(true);
					confirmationDialog.setRejectable(false);
					confirmationDialog.addCancelListener(confirmationDialogx -> confirmationDialog.close());
					confirmationDialog.setConfirmText(I18nProperties.getCaption(Captions.actionYes));
					confirmationDialog.setCancelText(I18nProperties.getCaption("Cancel Update"));

					confirmationDialog.addConfirmListener(confirmationDialogx -> {
						try {
							List<PopulationDataDto> popDataDtox = new ArrayList<PopulationDataDto>();
							FacadeProvider.getPopulationDataFacade().savePopulationData(popDataDtotoList);
						} catch (Exception exception) {

							System.out.println("excetion Caught while saving population data edit");
						} finally {
							confirmationDialog.close();
							dialog.close();
							treeGrid.getDataProvider().refreshAll();// refreshItem(campaignTreeGridDto);
							Notification.show(I18nProperties.getString(Strings.dataSavedSuccessfully));
						}
					});

					confirmationDialog.setText(
							"Are you sure you want to update the population data for the District " + name_ + "  ?");
					confirmationDialog.setHeader("Update Population Data");
					confirmationDialog.open();

				} else {
					Notification.show("You Need to fill all the data fields in order to save this population data");
				}

			}

		});

		VerticalLayout dialogLayout = new VerticalLayout(district, popData, popData5_10, popData4_23M, districtModalityCombo,
				districtStatusCombo);
		dialogLayout.setPadding(false);
		dialogLayout.setSpacing(false);
		dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

		return dialogLayout;

	}

	private static Button createSaveButton() {
		Button saveButton = new Button(I18nProperties.getCaption(Captions.actionSave));
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		return saveButton;
	}

	private static Button createDeleteButton() {
		Button deleteButton = new Button(I18nProperties.getCaption(Captions.actionDelete));
		deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		return deleteButton;
	}

	public String DateGetYear(Date dates) {

		SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
		String currentYear = getYearFormat.format(dates);
		return currentYear;
	}

	public void updateArchiveButtonText(boolean isArchived) {
		this.isArchived = isArchived;
		if (isArchived) {
			archiveDearchive.setText(I18nProperties.getCaption(Captions.actionDearchive));

		} else {

			archiveDearchive.setText(I18nProperties.getCaption(Captions.actionArchive));
		}

	}

	public void updatePublishButtonText(boolean isPublished) {
		this.isPublished = isPublished;
		if (isPublished) {
			publishUnpublishCampaign.setText(I18nProperties.getString(Strings.publish));
		} else {

			publishUnpublishCampaign.setText(I18nProperties.getString(Strings.unpublish));
		}

	}

	public void updateOpenCloseButtonText(boolean isOpenClose) {
		this.isOpenClose = isOpenClose;
		if (isOpenClose) {
			openCloseCampaign.setText(I18nProperties.getString(Strings.openCampaign));
		} else {

			openCloseCampaign.setText(I18nProperties.getString(Strings.closeCampaign));
		}

	}

	private List<CampaignTreeGridDto> generateTreeGridDataX() {
		List<CampaignTreeGridDto> gridData = new ArrayList<>();
		if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
			List<AreaDto> areas = FacadeProvider.getAreaFacade()
					.getAllActiveAsReferenceAndPopulationPashto(campaignDto);

			for (AreaDto area_ : areas) {
				CampaignTreeGridDto areaData = new CampaignTreeGridDto(area_.getName(), area_.getAreaid(), "Area",
						area_.getUuid_(), "area");
				List<RegionDto> regions_ = FacadeProvider.getRegionFacade()
						.getAllActiveAsReferenceAndPopulationPashto(area_.getAreaid(), campaignDto.getUuid());
				for (RegionDto regions_x : regions_) {
					CampaignTreeGridDto regionData = new CampaignTreeGridDto(regions_x.getName(),
							regions_x.getRegionId(), regions_x.getAreaUuid_(), regions_x.getUuid_(), "region");
					List<DistrictDto> district_ = FacadeProvider.getDistrictFacade()
							.getAllActiveAsReferenceAndPopulationPashto(regions_x.getRegionId(), campaignDto);
					ArrayList arr = new ArrayList<>();
//					for (DistrictDto district_x : district_) {
//						arr.add(new CampaignTreeGridDtoImpl(district_x.getName(), district_x.getPopulationData(),
//								district_x.getRegionId(), district_x.getRegionUuid_(), district_x.getUuid_(),
//								"district", district_x.getSelectedPopulationData()));
//					}
					for (DistrictDto district_x : district_) {

						if (district_x.getPopulationData() != null) {
							arr.add(new CampaignTreeGridDtoImpl(district_x.getName(), district_x.getPopulationData(),
									district_x.getPopulationData5_10(), district_x.getRegionId(),
									district_x.getRegionUuid_(), district_x.getUuid_(), "district",
									district_x.getSelectedPopulationData(), district_x.getDistrictModality(),
									district_x.getDistrictStatus(),
									((district_x.getPopulationData() != null ? district_x.getPopulationData() : 0L)
											+ (district_x.getPopulationData5_10() != null
													? district_x.getPopulationData5_10()
													: 0L))));
						} else {
//							arr.add(new CampaignTreeGridDtoImpl(district_x.getName(), district_x.getPopulationData(),
//									district_x.getRegionId(), district_x.getRegionUuid_(), district_x.getUuid_(),
//									"district", district_x.getSelectedPopulationData(), district_x.getDistrictModality(),
//									district_x.getDistrictStatus(), district_x.getAgeGroup()));	
						}

					}

					regionData.setRegionData(arr);

					areaData.addRegionData(regionData);
				}

				gridData.add(areaData);
			}
		} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
			List<AreaDto> areas = FacadeProvider.getAreaFacade().getAllActiveAsReferenceAndPopulationsDari(campaignDto);

			for (AreaDto area_ : areas) {
				CampaignTreeGridDto areaData = new CampaignTreeGridDto(area_.getName(), area_.getAreaid(), "Area",
						area_.getUuid_(), "area");
				List<RegionDto> regions_ = FacadeProvider.getRegionFacade()
						.getAllActiveAsReferenceAndPopulationDari(area_.getAreaid(), campaignDto.getUuid());
				for (RegionDto regions_x : regions_) {
					CampaignTreeGridDto regionData = new CampaignTreeGridDto(regions_x.getName(),
							regions_x.getRegionId(), regions_x.getAreaUuid_(), regions_x.getUuid_(), "region");
					List<DistrictDto> district_ = FacadeProvider.getDistrictFacade()
							.getAllActiveAsReferenceAndPopulationDari(regions_x.getRegionId(), campaignDto);
					ArrayList arr = new ArrayList<>();

					for (DistrictDto district_x : district_) {

						if (district_x.getPopulationData() != null) {
							arr.add(new CampaignTreeGridDtoImpl(district_x.getName(), district_x.getPopulationData(),
									district_x.getPopulationData5_10(), district_x.getRegionId(),
									district_x.getRegionUuid_(), district_x.getUuid_(), "district",
									district_x.getSelectedPopulationData(), district_x.getDistrictModality(),
									district_x.getDistrictStatus(),
									((district_x.getPopulationData() != null ? district_x.getPopulationData() : 0L)
											+ (district_x.getPopulationData5_10() != null
													? district_x.getPopulationData5_10()
													: 0L))));
						} else {

						}

					}

					regionData.setRegionData(arr);

					areaData.addRegionData(regionData);
				}

				gridData.add(areaData);
			}
		} else {

			List<AreaDto> areas = FacadeProvider.getAreaFacade().getAllActiveAsReferenceAndPopulation(campaignDto);

			for (AreaDto area_ : areas) {
				CampaignTreeGridDto areaData = new CampaignTreeGridDto(area_.getName(), area_.getAreaid(), "Area",
						area_.getUuid_(), "area");
				List<RegionDto> regions_ = FacadeProvider.getRegionFacade()
						.getAllActiveAsReferenceAndPopulation(area_.getAreaid(), campaignDto.getUuid());
				for (RegionDto regions_x : regions_) {
					CampaignTreeGridDto regionData = new CampaignTreeGridDto(regions_x.getName(),
							regions_x.getRegionId(), regions_x.getAreaUuid_(), regions_x.getUuid_(), "region");
					List<DistrictDto> district_ = FacadeProvider.getDistrictFacade()
							.getAllActiveAsReferenceAndPopulation(regions_x.getRegionId(), campaignDto);
					ArrayList arr = new ArrayList<>();
					for (DistrictDto district_x : district_) {

						if (district_x.getPopulationData() != null) {
							arr.add(new CampaignTreeGridDtoImpl(district_x.getName(), district_x.getPopulationData(),
									district_x.getPopulationData5_10(), district_x.getRegionId(),
									district_x.getRegionUuid_(), district_x.getUuid_(), "district",
									district_x.getSelectedPopulationData(), district_x.getDistrictModality(),
									district_x.getDistrictStatus(),
									((district_x.getPopulationData() != null ? district_x.getPopulationData() : 0L)
											+ (district_x.getPopulationData5_10() != null
													? district_x.getPopulationData5_10()
													: 0L))));
						} else {
//							arr.add(new CampaignTreeGridDtoImpl(district_x.getName(), district_x.getPopulationData(),
//									district_x.getRegionId(), district_x.getRegionUuid_(), district_x.getUuid_(),
//									"district", district_x.getSelectedPopulationData(), district_x.getDistrictModality(),
//									district_x.getDistrictStatus(), district_x.getAgeGroup()));	
						}

					}
					;

					regionData.setRegionData(arr);

					areaData.addRegionData(regionData);
				}

				gridData.add(areaData);
			}
		}
		return gridData;
	}

		private List<CampaignTreeGridDto> generateTreeGridData() {
    List<CampaignTreeGridDto> gridData = new ArrayList<>();

    List<AreaDto> areas = FacadeProvider.getAreaFacade().getAllActiveAsReferenceAndPopulation(campaignDto);
    for (AreaDto area_ : areas) {
        CampaignTreeGridDto areaData = new CampaignTreeGridDto(
            area_.getName(), 
            area_.getAreaid(), 
            "Area",
            area_.getUuid_(), 
            "area"
        );
        
        List<RegionDto> regions_ = FacadeProvider.getRegionFacade()
            .getAllActiveAsReferenceAndPopulation(area_.getAreaid(), campaignDto.getUuid());
        
        for (RegionDto regions_x : regions_) {
        	
            System.out.println("  Region------: " + regions_x.getName());

            
            CampaignTreeGridDto regionData = new CampaignTreeGridDto(
                regions_x.getName(), 
                regions_x.getRegionId(),
                regions_x.getAreaUuid_(), 
                regions_x.getUuid_(), 
                "region"
            );
            
            List<DistrictDto> district_ = FacadeProvider.getDistrictFacade()
            	    .getAllActiveAsReferenceAndPopulation(regions_x.getRegionId(), campaignDto);

            	System.out.println("    Districts in region (raw): " + district_.size());

            	// Track which district UUIDs we've already turned into tree nodes
            	Set<String> processedDistrictUuids = new HashSet<>();

            	for (DistrictDto district_x : district_) {

            	    // Skip duplicate district rows that have the same UUID
            	    if (!processedDistrictUuids.add(district_x.getUuid_())) {
            	        System.out.println("    Skipping duplicate district: " + district_x.getName()
            	            + " (" + district_x.getUuid_() + ")");
            	        continue;
            	    }

            	    System.out.println("    District: " + district_x.getName() + " (" + district_x.getUuid_() + ")");

            	    CampaignTreeGridDto districtData = new CampaignTreeGridDto();
            	    
            	    districtData = new CampaignTreeGridDto( district_x.getName(),
            	        district_x.getRegionId(),
            	        district_x.getRegionUuid_(),
            	        district_x.getUuid_(),
            	        "district"
            	    );

            	    // Get clusters for this district
            	    List<CommunityDto> clusters_ = FacadeProvider.getCommunityFacade()
            	        .getAllActiveClustersAsReferenceAndPopulation(
            	            regions_x.getRegionId(),
            	            district_x.getUuid_(),
            	            campaignDto
            	        );

            	    System.out.println("      Clusters in district: " + clusters_.size());
            	   if(clusters_.size() > 0 ) {
            	    for (CommunityDto clusterdto : clusters_) {
            	        if (clusterdto.getName() != null) {

            	            System.out.println("      Cluster: " + clusterdto.getName());

            	            Long totalPopulation =
            	                (clusterdto.getPopulationData() != null ? clusterdto.getPopulationData() : 0L)
            	                + (clusterdto.getPopulationData5_10() != null ? clusterdto.getPopulationData5_10() : 0L);
            	            
//            	            CampaignTreeGridDto clusterData = new CampaignTreeGridDtoImpl(
//            	                clusterdto.getName(),
//            	                clusterdto.getPopulationData(),
//            	                clusterdto.getPopulationData5_10(),
//            	                clusterdto.getClusterId(),
//            	                clusterdto.getDistrictUuid(), // Parent is district UUID
//            	                clusterdto.getClusterUuid(),
//            	                "cluster",
//            	                clusterdto.getSelectedPopulationData(),
//            	                clusterdto.getDistrictModality(),
//            	                clusterdto.getDistrictStatus(),
//            	                clusterdto.provideFloatStatus(),
//            	                totalPopulation
//            	            );
            	            
            	            CampaignTreeGridDto clusterData = new CampaignTreeGridDtoImpl(
                	                clusterdto.getName(),
                	                clusterdto.getPopulationData(),
                	                clusterdto.getPopulationData5_10(),
                	                clusterdto.getPopulationData4_23M(),
                	                clusterdto.getClusterId(),
                	                clusterdto.getDistrictUuid(), // Parent is district UUID
                	                clusterdto.getClusterUuid(),
                	                "cluster",
                	                clusterdto.getSelectedPopulationData(),
                	                clusterdto.getDistrictModality(),
                	                clusterdto.getDistrictStatus(),
                	                clusterdto.provideFloatStatus(),
                	                totalPopulation
                	            );

            	            // Add cluster to district
            	            districtData.addClusterData(clusterData);
            	        }
            	    }
            	}else {
            		
            		if (district_x.getPopulationData() != null) {
            			districtData = new CampaignTreeGridDtoImpl(
            					district_x.getName(), 
            					district_x.getPopulationData(),
								district_x.getPopulationData5_10(), 
								district_x.getRegionId(),
								district_x.getRegionUuid_(), 
								district_x.getUuid_(), 
								"district",
								district_x.getSelectedPopulationData(), 
								district_x.getDistrictModality(),
								district_x.getDistrictStatus(),
								((district_x.getPopulationData() != null ? district_x.getPopulationData() : 0L) + (district_x.getPopulationData5_10() != null ? district_x.getPopulationData5_10() : 0L))
								);
					} 
            		
            	}

            	    // Add district to region
            	    regionData.addDistrictData(districtData);
            	}

            // Add region to area
            areaData.addRegionData(regionData);
        }
        
        gridData.add(areaData);
    }
    
    return gridData;
}


		private void discardChanges() {
		UI currentUI = UI.getCurrent();
		if (currentUI != null) {
			Dialog dialog = (Dialog) this.getParent().get();
			dialog.close();
		}
	}

	// Events
		public static abstract class CampaignFormEvent extends ComponentEvent<CampaignForm> {
		private CampaignDto campaign;

		protected CampaignFormEvent(CampaignForm source, CampaignDto campaign) {
			super(source, false);
			this.campaign = campaign;
		}

		public CampaignDto getCampaign() {
			if (campaign == null) {
				campaign = new CampaignDto();
				return campaign;
			} else {
				return campaign;

			}
		}
	}

	public void validateAndSave(boolean editMode) {
		this.editMode = editMode;
		if (formDatac == null) {

			UserReferenceDto user = new UserReferenceDto();
			UserProvider usr = new UserProvider();
			user.setUuid(usr.getUuid());
			formDatac = new CampaignDto();
			formDatac.setUuid(creatingUuid.getValue());
			formDatac.setCreatingUser(user);

			LocalDate preCampaignlocalDate = preCampaignstartDate.getValue();
			Date preCampaignstartdate = Date
					.from(preCampaignlocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			LocalDate preCampaignendxDate = preCampaignendDate.getValue();
			Date preCampaignendxDatex = Date.from(preCampaignendxDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			LocalDate localDate = startDate.getValue();
			Date startdate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			LocalDate endxDate = endDate.getValue();
			Date endxDatex = Date.from(endxDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			LocalDate postCampaignlocalDate = postCampaignstartDate.getValue();
			Date postCampaignstartdate = Date
					.from(postCampaignlocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			LocalDate postCampaignendxDate = postCampaignendDate.getValue();
			Date postCampaignendxDatex = Date
					.from(postCampaignendxDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			formDatac.setCampaignYear(campaaignYear.getValue().toString());

			formDatac.setName(campaignName.getValue());
			formDatac.setRound(round.getValue().toString());

			formDatac.setPreCampStartDate(preCampaignstartdate);
			formDatac.setPreCampEndDate(preCampaignendxDatex);
			formDatac.setStartDate(startdate);
			formDatac.setEndDate(endxDatex);
			formDatac.setPostCampStartDate(postCampaignstartdate);
			formDatac.setPostCampEndDate(postCampaignendxDatex);
			formDatac.setDescription(description.getValue());
			formDatac.setCampaignStatus(formDatac.campaignStatus = "Closed");

			List<CampaignDashboardElement> superList = new ArrayList<>();

			List<CampaignFormMetaReferenceDto> preCampaignFormgridData = preCampaignFormGridComponent
					.getSavedElements();

			List<CampaignFormMetaReferenceDto> intraCampaignFormgridData = intraCampaignFormGridComponent
					.getSavedElements();
			List<CampaignFormMetaReferenceDto> postCampaignFormgridData = preCampaignFormGridComponent
					.getSavedElements();

			Set<CampaignFormMetaReferenceDto> superSet = new HashSet<>();
			Set<CampaignFormMetaWithExpReferenceDto> formMetatExpirySet = formDatac.getCampaignFormMetaExpiry();

			for (CampaignFormMetaReferenceDto item : preCampaignFormgridData) {

				if (item != null) {
					superSet.add(item);
				}
			}

			// Add items from intraCampaignDashboardgridData if they are not null
			for (CampaignFormMetaReferenceDto item : intraCampaignFormgridData) {
				if (item != null) {
					superSet.add(item);
				}
			}

			// Add items from postCampaignDashboardgridData if they are not null
			for (CampaignFormMetaReferenceDto item : postCampaignFormgridData) {
				if (item != null) {
					superSet.add(item);
				}
			}

			formDatac.setCampaignDashboardElements(superList);
			formDatac.setCampaignFormMetas(superSet);
			formDatac.setCampaignFormMetaExpiryDto(formMetatExpirySet);

			fireEvent(new SaveEvent(this, formDatac));
			UI.getCurrent().getPage().reload();

			System.out.println("eskelebetiolebetetttttttthvkfvskv");

		} else {
			if (binder.validate().isOk()) {
				
				System.out.println(campaignDto.getPopulationdata() + "campaignDto.getPopulationdata()");

				System.out.println(campaignDto.getPopulationdata().size() + "campaignDto.getPopulationdata()");
for(PopulationDataDto ccc :  campaignDto.getPopulationdata()) {
	System.out.println(ccc.getCampaign_id() + "getCampaign_id()");

	System.out.println(ccc.getCommunity().getCaption() + "getCommunity().getCaption()()");
	
	System.out.println(ccc.getSelected() + "getCommunity().getCaption()()");


}
				FacadeProvider.getPopulationDataFacade().savePopulationList(campaignDto.getPopulationdata());

				Notification.show(
						String.format(I18nProperties.getString(Strings.messageCampaignSaved), campaignDto.getName()));

				fireEvent(new SaveEvent(this, binderx.getBean()));

				UI.getCurrent().getPage().reload();

//				Notification.show(I18nProperties.getString(Strings.headingUploadSuccess) + "!");

			} else {
				Notification.show(I18nProperties.getString(Strings.errorCampaignForm));
			}
		}
	}

	private void archive() {
//		updateArchiveButtonText(isArchived);
//		UI.getCurrent().getPage().reload();
		try {
			fireEvent(new ArchiveEvent(this, binderx.getBean()));
		} finally {
//			isArchived = FacadeProvider.getCampaignFacade().isArchived(campaignDto.getUuid());
//			updateArchiveButtonText(isArchived);
		}

	}

	private void publishUnpublish() {
		fireEvent(new PublishUnpublishEvent(this, binderx.getBean()));
	}

	private void logEventMethod() {
		fireEvent(new LogCampaignEvent(this, binderx.getBean()));
	}

	private void deleteCampaign() {

		fireEvent(new DeleteEvent(this, binderx.getBean()));
//		updatePublishButtonText(isArchived);
//		UI.getCurrent().getPage().reload();

	}

	private void duplicateCampaign() {

		fireEvent(new DuplicateEvent(this, binderx.getBean()));
//		updatePublishButtonText(isArchived);
//		UI.getCurrent().getPage().reload();

	}

	private void openCloseCampaign() {

		fireEvent(new OpenCloseEvent(this, binderx.getBean()));

	}

	private void roundChange() {

		fireEvent(new RoundChangeEvent(this, binderx.getBean()));

	}

	private void discard() {

		fireEvent(new DiscardEvent(this));

	}

	public void setCampaign(CampaignDto campaignIndexDto) {
		// TODO Auto-generated method stub
		binderx.setBean(campaignIndexDto);
	}

	public static class SaveEvent extends CampaignFormEvent {
		SaveEvent(CampaignForm source, CampaignDto campaign) {
			super(source, campaign);
		}
	}

	public static class RoundChangeEvent extends CampaignFormEvent {
		RoundChangeEvent(CampaignForm source, CampaignDto campaign) {
			super(source, campaign);
		}
	}

	public class ArchiveEvent extends CampaignFormEvent {
		ArchiveEvent(CampaignForm source, CampaignDto campaign) {
			super(source, campaign);

		}
	}

	public static class PublishUnpublishEvent extends CampaignFormEvent {
		PublishUnpublishEvent(CampaignForm source, CampaignDto campaign) {
			super(source, campaign);
		}
	}

	public static class LogCampaignEvent extends CampaignFormEvent {
		LogCampaignEvent(CampaignForm source, CampaignDto campaign) {
			super(source, campaign);
		}
	}

	public static class OpenCloseEvent extends CampaignFormEvent {
		OpenCloseEvent(CampaignForm source, CampaignDto campaign) {
			super(source, campaign);
		}
	}

	public static class DeleteEvent extends CampaignFormEvent {
		DeleteEvent(CampaignForm source, CampaignDto contact) {
			super(source, contact);
		}

	}

	public static class DuplicateEvent extends CampaignFormEvent {
		DuplicateEvent(CampaignForm source, CampaignDto contact) {
			super(source, contact);
		}

	}

	public static class CloseEvent extends CampaignFormEvent {
		CloseEvent(CampaignForm source) {
			super(source, null);
		}
	}

	public static class DiscardEvent extends CampaignFormEvent {
		DiscardEvent(CampaignForm source) {
			super(source, null);
		}
	}

	public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
		return addListener(DeleteEvent.class, listener);
	}

	public Registration addDuplicateListener(ComponentEventListener<DuplicateEvent> listener) {
		return addListener(DuplicateEvent.class, listener);
	}

	public Registration addArchiveListener(ComponentEventListener<ArchiveEvent> listener) {
		return addListener(ArchiveEvent.class, listener);
	}

	public Registration addPublishListener(ComponentEventListener<PublishUnpublishEvent> listener) {
		return addListener(PublishUnpublishEvent.class, listener);
	}

	public Registration addLogListener(ComponentEventListener<LogCampaignEvent> listener) {
		return addListener(LogCampaignEvent.class, listener);
	}

	public Registration addOpenCloseListener(ComponentEventListener<OpenCloseEvent> listener) {
		return addListener(OpenCloseEvent.class, listener);
	}

	public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
		return addListener(SaveEvent.class, listener);
	}

	public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
		return addListener(CloseEvent.class, listener);
	}

	public Registration addRoundChangeListener(ComponentEventListener<RoundChangeEvent> listener) {
		return addListener(RoundChangeEvent.class, listener);
	}

	public Registration addDiscardListener(ComponentEventListener<DiscardEvent> listener) {
		return addListener(DiscardEvent.class, listener);
	}

	private List<CampaignDashboardElement> getListDashboardFromType(String phaseTy) {
		final List<CampaignDiagramDefinitionDto> allDiagram = FacadeProvider.getCampaignDiagramDefinitionFacade()
				.getAll();

		List<CampaignDashboardElement> elements = new ArrayList<>();
		final List<CampaignDiagramDefinitionDto> filterList = allDiagram.stream()
				.filter(e -> e.getFormType().equalsIgnoreCase(phaseTy)).collect(Collectors.toList());

		for (CampaignDiagramDefinitionDto lsiter : filterList) {
			CampaignDashboardElement emptyElements = new CampaignDashboardElement();
			emptyElements.setDiagramId(lsiter.getDiagramId());
			emptyElements.setPhase(phaseTy);
			elements.add(emptyElements);

		}
		return elements;

	}

	private void pokeFlow() {
		logger.debug("runingImport...");
	}

	private void startIntervalCallback() {
		UI.getCurrent().setPollInterval(5000);
		if (!callbackRunning) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
//					stopIntervalCallback();
				}
			}, 15000); // 10 minutes

			callbackRunning = true;
		}
	}

	private void stopIntervalCallback() {
		if (callbackRunning) {
			callbackRunning = false;
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}

		}

	}

	private void stopPullers() {
		UI.getCurrent().setPollInterval(-1);
	}

}