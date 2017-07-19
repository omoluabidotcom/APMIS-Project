package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardView extends AbstractView {

	public static final String VIEW_NAME = "dashboard";
	
	public static final String I18N_PREFIX = "Dashboard";
	public static final String FROM = "from";
	public static final String TO = "to";
	public static final String HEADING = "heading";
	public static final String SUB_HEADING = "subHeading";
	public static final String QUERY_PERIOD = "queryPeriod";
	public static final String PREVIOUS_PERIOD = "previousPeriod";
	public static final String DATE_FILTER_FOR_MAP = "dateFilterForMap";
	public static final String HCWS = "hcws";
	public static final String TOTAL = "total";
	public static final String UNDER_FOLLOW_UP = "underFollowUp";
	public static final String ON = "on";
	public static final String OUTBREAKS = "outbreaks";
	public static final String RUMORS = "rumors";
	public static final String CASES = "cases";
	public static final String DEATHS = "deaths";
	public static final String CONTACTS = "contacts";
	public static final String ALERTS = "alerts";
	public static final String NEW_CASES = "newCases";
	public static final String ALL = "all";
	public static final String DISEASE = "disease";
	public static final String NOT_YET_CLASSIFIED = "notYetClassified";
	public static final String CONFIRMED = "confirmed";
	public static final String PROBABLE = "probable";
	public static final String SUSPECT = "suspect";
	public static final String EPI_CURVE = "epiCurve";
	public static final String SITUATION_REPORT = "situationReport";
	public static final String CASE_MAP = "caseMap";
	public static final String EXPAND = "expand";
	public static final String COLLAPSE = "collapse";
	
	private VerticalLayout mapLayout;
	private MapComponent mapComponent;
	
	private SituationReportTable situationReportTable;
	private HighChart epiCurveChart;

	private List<CaseDataDto> cases = new ArrayList<>();
	
	private Date fromDate;
	private Date toDate;
	private Disease disease;
	private boolean useDateFilterForMap;

	public DashboardView() {
		setSizeFull();
		addStyleName("crud-view");

        // Initialize case list with the pre-selected data
		cases = FacadeProvider.getCaseFacade().getAllCasesBetween(fromDate, toDate, disease, LoginHelper.getCurrentUser().getUuid());
		
		VerticalLayout dashboardLayout = new VerticalLayout();
        dashboardLayout.setSpacing(false);
        dashboardLayout.setSizeFull();
        dashboardLayout.setStyleName("crud-main-layout");
        dashboardLayout.setMargin(true);
        
        dashboardLayout.addComponent(createTopBar());
        dashboardLayout.addComponent(createFilterBar());
		HorizontalLayout contentLayout = createContents();
        dashboardLayout.addComponent(contentLayout);
        dashboardLayout.setExpandRatio(contentLayout, 1);
        
        addComponent(dashboardLayout);
	}
	
	private HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.addStyleName(CssStyles.VSPACE_NO_FILTERS);
		
		Label header = new Label("Dashboard");
    	header.setSizeUndefined();
    	CssStyles.style(header, CssStyles.H2, CssStyles.NO_MARGIN);
    	topLayout.addComponent(header);
		
		return topLayout;
	}
	
	private HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE3);
        
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		
		DateField dateFromFilter = new DateField();
		dateFromFilter.setDateFormat(DateHelper.getShortDateFormat().toLocalizedPattern());
        dateFromFilter.setWidth(200, Unit.PIXELS);
        dateFromFilter.setValue(DateHelper.subtractDays(c.getTime(), 28));
        dateFromFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, FROM));
        dateFromFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
        dateFromFilter.addValueChangeListener(e -> {
        	fromDate = dateFromFilter.getValue();
        	refreshDashboard();
        });
        filterLayout.addComponent(dateFromFilter);
        fromDate = dateFromFilter.getValue();
        
        DateField dateToFilter = new DateField();
		dateToFilter.setDateFormat(DateHelper.getShortDateFormat().toLocalizedPattern());
        dateToFilter.setWidth(200, Unit.PIXELS);
        dateToFilter.setValue(c.getTime());
        dateToFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, TO));
        dateToFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
        dateToFilter.addValueChangeListener(e -> {
        	toDate = dateToFilter.getValue();
        	refreshDashboard();
        });
        filterLayout.addComponent(dateToFilter);
        toDate = dateToFilter.getValue();
        
		ComboBox diseaseFilter = new ComboBox();
        diseaseFilter.setWidth(200, Unit.PIXELS);
        diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DISEASE));
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.addValueChangeListener(e -> {
        	disease = (Disease) diseaseFilter.getValue();
        	refreshDashboard();
        });
        diseaseFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DISEASE));
        filterLayout.addComponent(diseaseFilter);
        disease = (Disease) diseaseFilter.getValue();
        
        return filterLayout;
	}
	
	private HorizontalLayout createContents() {
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setSpacing(true);
		layout.setMargin(new MarginInfo(false, false, true, false));

    	VerticalLayout leftColumnLayout = new VerticalLayout();
    	leftColumnLayout.setHeightUndefined();
    	leftColumnLayout.setWidth(100, Unit.PERCENTAGE);

    	VerticalLayout rightColumnLayout = new VerticalLayout();
    	rightColumnLayout.setHeightUndefined();
    	rightColumnLayout.setWidth(100, Unit.PERCENTAGE);
    	
    	layout.addComponent(leftColumnLayout);
    	layout.addComponent(rightColumnLayout);
		        
        // Situation report summary
        VerticalLayout situationReportLayout = new VerticalLayout();
        {
	        Label reportTableLabel = new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SITUATION_REPORT));
	    	reportTableLabel.addStyleName(CssStyles.H3);
	    	situationReportLayout.addComponent(reportTableLabel);
	    	situationReportTable = new SituationReportTable();
	    	situationReportTable.clearAndFill(fromDate, toDate, disease, cases);
	    	situationReportLayout.addComponent(situationReportTable);
        }
        leftColumnLayout.addComponent(situationReportLayout);
        
        // Epi curve
        VerticalLayout epiCurveLayout = new VerticalLayout();
        {
            epiCurveLayout.setId("epiCurveLayout");
            epiCurveLayout.setWidth(100, Unit.PERCENTAGE);
            epiCurveLayout.setHeight(360, Unit.PIXELS);
	        
	        Label epiCurveLabel = new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, EPI_CURVE));
	        epiCurveLabel.addStyleName(CssStyles.H3);
	        epiCurveLayout.addComponent(epiCurveLabel);
	        
	        createEpiCurveChart();
	        epiCurveLayout.addComponent(epiCurveChart);
	        epiCurveLayout.setExpandRatio(epiCurveChart, 1);
        }
        rightColumnLayout.addComponent(epiCurveLayout);
        
        mapLayout = createMapLayout(
			new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					layout.removeComponent(leftColumnLayout);
					layout.removeComponent(rightColumnLayout);
					layout.addComponent(mapLayout);
					layout.setHeight(100, Unit.PERCENTAGE);
					mapLayout.setSizeFull();
				}
	        },
	        new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					rightColumnLayout.addComponent(mapLayout);
					mapLayout.setHeight(360, Unit.PIXELS);
					layout.setHeightUndefined();
					layout.addComponent(leftColumnLayout);
					layout.addComponent(rightColumnLayout);
				}
	        });
     
		rightColumnLayout.addComponent(mapLayout);
        
        return layout;
	}

	private VerticalLayout createMapLayout(ClickListener expandListener, ClickListener collapseListener) {
		// Initialize layouts (needs to be done here for the button listener below
    	VerticalLayout mapLayout = new VerticalLayout();
    	mapLayout.setWidth(100, Unit.PERCENTAGE);
    	mapLayout.setHeight(360, Unit.PIXELS);
    	
		// Map header
		HorizontalLayout mapHeaderLayout = new HorizontalLayout();
		{
			mapHeaderLayout.setWidth(100, Unit.PERCENTAGE);
			mapHeaderLayout.setSpacing(true);

			Label caseMapLabel = new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, CASE_MAP));
	        caseMapLabel.setSizeUndefined();
	        CssStyles.style(caseMapLabel, CssStyles.H3);
			mapHeaderLayout.addComponent(caseMapLabel);
			
			CheckBox dateFilterForMap = new CheckBox();
			dateFilterForMap.addStyleName(CssStyles.NO_MARGIN);
	        dateFilterForMap.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DATE_FILTER_FOR_MAP));
	        dateFilterForMap.addValueChangeListener(e -> {
	        	useDateFilterForMap = dateFilterForMap.getValue();
	        	refreshMap();
	        });
	        mapHeaderLayout.addComponent(dateFilterForMap);	        
	        mapHeaderLayout.setComponentAlignment(dateFilterForMap, Alignment.MIDDLE_LEFT);
			
	    	Button expandMap = new Button(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, EXPAND), FontAwesome.EXPAND);
	        expandMap.setStyleName(ValoTheme.BUTTON_LINK);
	        expandMap.addStyleName(CssStyles.NO_MARGIN);   
	        Button collapseMap = new Button(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, COLLAPSE), FontAwesome.COMPRESS);
	        collapseMap.setStyleName(ValoTheme.BUTTON_LINK);
	        collapseMap.addStyleName(CssStyles.NO_MARGIN);
	        
	        expandMap.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					expandListener.buttonClick(event);
					mapHeaderLayout.removeComponent(expandMap);
					mapHeaderLayout.addComponent(collapseMap);
					mapHeaderLayout.setComponentAlignment(collapseMap, Alignment.MIDDLE_RIGHT);
					mapHeaderLayout.setExpandRatio(collapseMap, 1);
				}
	        });
	        collapseMap.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					collapseListener.buttonClick(event);
					mapHeaderLayout.removeComponent(collapseMap);
					mapHeaderLayout.addComponent(expandMap);
					mapHeaderLayout.setComponentAlignment(expandMap, Alignment.MIDDLE_RIGHT);
					mapHeaderLayout.setExpandRatio(expandMap, 1);
				}
	        });
	        
	        mapHeaderLayout.addComponent(expandMap);
	        mapHeaderLayout.setComponentAlignment(expandMap, Alignment.MIDDLE_RIGHT);
	        mapHeaderLayout.setExpandRatio(expandMap, 1);
		}
		mapLayout.addComponent(mapHeaderLayout);
		
		// Map and map key
		mapComponent = new MapComponent();
		mapComponent.setSizeFull();
        mapLayout.addComponent(mapComponent);
        mapLayout.setExpandRatio(mapComponent, 1);
        
        HorizontalLayout legendLayout = new HorizontalLayout();
        legendLayout.addStyleName(CssStyles.VSPACETOP3);
        legendLayout.setSizeUndefined();
        legendLayout.setSpacing(true);
        
        Image iconGrey = new Image(null, new ThemeResource("mapicons/grey-dot-small.png"));
        Image iconYellow = new Image(null, new ThemeResource("mapicons/yellow-dot-small.png"));
        Image iconOrange = new Image(null, new ThemeResource("mapicons/orange-dot-small.png"));
        Image iconRed = new Image(null, new ThemeResource("mapicons/red-dot-small.png"));
        iconGrey.setWidth(16.5f, Unit.PIXELS);
        iconGrey.setHeight(22.5f, Unit.PIXELS);
        iconYellow.setWidth(16.5f, Unit.PIXELS);
        iconYellow.setHeight(22.5f, Unit.PIXELS);
        iconOrange.setWidth(16.5f, Unit.PIXELS);
        iconOrange.setHeight(22.5f, Unit.PIXELS);
        iconRed.setWidth(16.5f, Unit.PIXELS);
        iconRed.setHeight(22.5f, Unit.PIXELS);
        
        legendLayout.addComponent(iconGrey);
        legendLayout.addComponent(new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, NOT_YET_CLASSIFIED)));
        legendLayout.addComponent(iconYellow);
        legendLayout.addComponent(new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SUSPECT)));
        legendLayout.addComponent(iconOrange);
        legendLayout.addComponent(new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, PROBABLE)));
        legendLayout.addComponent(iconRed);
        legendLayout.addComponent(new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, CONFIRMED)));
        mapLayout.addComponent(legendLayout);
        
		return mapLayout;
	}
	
	private void refreshDashboard() {
		// Update the cases list according to the filters
		String userUuid = LoginHelper.getCurrentUser().getUuid();
		cases = FacadeProvider.getCaseFacade().getAllCasesBetween(fromDate, toDate, disease, userUuid);
		
		// Update cases shown on the map
    	refreshMap();
		
    	// Update situation report and epi curve data
    	situationReportTable.clearAndFill(fromDate, toDate, disease, cases);
    	// Epi curve chart has to be created again due to a canvas resizing issue when simply refreshing the component
    	//chartWrapper.removeComponent(epiCurveChart);
    	clearAndFillEpiCurveChart();
    	//chartWrapper.addComponent(epiCurveChart);
	}
	
	private void refreshMap() {
		String userUuid = LoginHelper.getCurrentUser().getUuid();
		
		// If the "use date filter for map" check box is not checked, use a list of all cases irrespective of the dates instead
		if (useDateFilterForMap == true) {
        	mapComponent.showFacilities(cases);
    	} else {
    		List<CaseDataDto> casesForMap = FacadeProvider.getCaseFacade().getAllCasesByDiseaseAfter(null, disease, userUuid);
    		mapComponent.showFacilities(casesForMap);
    	}
	}
	
	/**
	 * Creates the epi curve chart using the Chart.js Vaadin addon
	 */
	private void createEpiCurveChart() {
		epiCurveChart = new HighChart();
		epiCurveChart.setSizeFull();
		clearAndFillEpiCurveChart();
	}
	
	private void clearAndFillEpiCurveChart() {
		StringBuilder hcjs = new StringBuilder();
		hcjs.append("var options = {"
				+ "chart: { type: 'column', backgroundColor: null },"//, events: { addSeries: function(event) {" + chartLoadFunction + "} } },"
				+ "credits: { enabled: false },"
				+ "title: { text: '' },");
		
		// Creates and sets the labels for each day on the x-axis
		List<Date> filteredDates = buildListOfFilteredDates();
		List<String> newLabels = new ArrayList<>();
		for (Date date : filteredDates) {
			String label = DateHelper.formatShortDate(date);
			newLabels.add(label);
		}
				
		hcjs.append("xAxis: { categories: [");
		for (String s : newLabels) {
			if (newLabels.indexOf(s) == newLabels.size() - 1) {
				hcjs.append("'" + s + "']},");
			} else {
				hcjs.append("'" + s + "', ");
			}
		}
				
		hcjs.append("yAxis: { min: 0, title: { text: '' }, allowDecimals: false, softMax: 10, stackLabels: { enabled: true, style: {"
				+ "fontWeight: 'bold', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
				+ "legend: { verticalAlign: 'top', backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',"
				+ "borderColor: '#CCC', borderWidth: 1, shadow: false },"
				+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'},"
				+ "plotOptions: { column: { stacking: 'normal', dataLabels: {"
				+ "enabled: true, formatter: function() { if (this.y > 0) return this.y; },"
				+ "color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white' } } },");
		
		// Adds the number of confirmed, probable and suspect cases for each day as data
		List<CaseDataDto> confirmedCases = cases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED)
				.collect(Collectors.toList());
		List<CaseDataDto> suspectedCases = cases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT)
				.collect(Collectors.toList());
		List<CaseDataDto> probableCases = cases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE)
				.collect(Collectors.toList());
		
		int[] confirmedNumbers = new int[newLabels.size()];
		int[] probableNumbers = new int[newLabels.size()];
		int[] suspectNumbers = new int[newLabels.size()];
		
		for (int i = 0; i < filteredDates.size(); i++) {
			Date date = filteredDates.get(i);
			int confirmedCasesAtDate = (int) confirmedCases.stream()
					.filter(c -> DateHelper.isSameDay(c.getSymptoms().getOnsetDate(), date))
					.count();
			confirmedNumbers[i] = confirmedCasesAtDate;
			int probableCasesAtDate = (int) probableCases.stream()
					.filter(c -> DateHelper.isSameDay(c.getSymptoms().getOnsetDate(), date))
					.count();
			probableNumbers[i] = probableCasesAtDate;
			int suspectCasesAtDate = (int) suspectedCases.stream()
					.filter(c -> DateHelper.isSameDay(c.getSymptoms().getOnsetDate(), date))
					.count();
			suspectNumbers[i] = suspectCasesAtDate;
		}

		hcjs.append("series: [");
		hcjs.append("{ name: 'Confirmed', color: '#B22222', dataLabels: { allowOverlap: false }, data: [");
		for (int i = 0; i < confirmedNumbers.length; i++) {
			if (i == confirmedNumbers.length - 1) {
				hcjs.append(confirmedNumbers[i] + "]},");
			} else {
				hcjs.append(confirmedNumbers[i] + ", ");
			}
		}
		hcjs.append("{ name: 'Probable', color: '#FF4500', dataLabels: { allowOverlap: false },  data: [");
		for (int i = 0; i < probableNumbers.length; i++) {
			if (i == probableNumbers.length - 1) {
				hcjs.append(probableNumbers[i] + "]},");
			} else {
				hcjs.append(probableNumbers[i] + ", ");
			}
		}
		hcjs.append("{ name: 'Suspect', color: '#FFD700', dataLabels: { allowOverlap: false },  data: [");
		for (int i = 0; i < suspectNumbers.length; i++) {
			if (i == suspectNumbers.length - 1) {
				hcjs.append(suspectNumbers[i] + "]}]};");
			} else {
				hcjs.append(suspectNumbers[i] + ", ");
			}
		}
		
		epiCurveChart.setHcjs(hcjs.toString());	
	}
	
	/**
	 * Builds a list that contains an object for each day between the from and to dates
	 * @return
	 */
	private List<Date> buildListOfFilteredDates() {
		List<Date> filteredDates = new ArrayList<>();
		Date currentDate = new Date(fromDate.getTime());
		while (!currentDate.after(toDate)) {
			filteredDates.add(currentDate);
			currentDate = DateHelper.addDays(currentDate, 1);
		}
		
		return filteredDates;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}
	
}