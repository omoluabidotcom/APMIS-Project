package com.cinoteck.application.views.reports;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.cinoteck.application.UserProvider;
import com.cinoteck.application.views.utils.gridexporter.GridExporter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.ui.OptionGroup;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.ErrorStatusEnum;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteriaNew;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.report.CommunityUserReportModelDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;

@Route(layout = UserAnalysisView.class)
public class UserAnalysisGridView extends VerticalLayout {

	private static final long serialVersionUID = 2199158503341966128L;

	private ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>();
	private ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>();
	private ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>();
	private Button resetButton;
	private ComboBox<ErrorStatusEnum> errorStatusFilter;

	private List<AreaReferenceDto> regions;
	private List<RegionReferenceDto> provinces;
	private List<DistrictReferenceDto> districts;

	private Grid<CommunityUserReportModelDto> grid = new Grid<>(CommunityUserReportModelDto.class, false);

	private CommunityCriteriaNew criteria;
	private UserProvider currentUser = new UserProvider();
	Anchor anchor = new Anchor("", I18nProperties.getCaption(Captions.export));
	Icon icon = VaadinIcon.UPLOAD_ALT.create();
	Button exportReport = new Button();
	private UserProvider userProvider = new UserProvider();

//    Paragraph countRowItems;
	public UserAnalysisGridView(CommunityCriteriaNew criteria, FormAccess formAccess) {
		this.criteria = new CommunityCriteriaNew();

		this.criteria.area(currentUser.getUser().getArea());
		this.criteria.region(currentUser.getUser().getRegion());
		this.criteria.district(currentUser.getUser().getDistrict());

		setSizeFull();
		addFilter(formAccess);
		userAnalysisGrid(criteria, formAccess);

	}

	private void addFilter(FormAccess formAccess) {
//    	final UserDto user = UserProvider.getCurrent().getUser();
//		criteria.area(user.getArea());// .setArea(user.getArea());
//		criteria.region(user.getRegion());// .setRegion(user.getRegion());
//		criteria.district(user.getDistrict()); 

//        setMargin(true);
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setPadding(false);
		filterLayout.setVisible(true);
		filterLayout.setAlignItems(Alignment.END);

		regionFilter.setLabel(I18nProperties.getCaption(Captions.area));
		regionFilter.setPlaceholder(I18nProperties.getCaption(Captions.areaAllAreas));
		if (currentUser.getUser().getLanguage().toString().equals("Pashto")) {
			regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReferencePashto());
		} else if (currentUser.getUser().getLanguage().toString().equals("Dari")) {
			regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReferenceDari());
		} else {
			regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		}

		regionFilter.addValueChangeListener(e -> {
			AreaReferenceDto selectedArea = e.getValue();
			if (selectedArea != null) {
				if (currentUser.getUser().getLanguage().toString().equals("Pashto")) {
					provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaPashto(e.getValue().getUuid());
					provinceFilter.setItems(provinces);
				} else if (currentUser.getUser().getLanguage().toString().equals("Dari")) {
					provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaDari(e.getValue().getUuid());
					provinceFilter.setItems(provinces);
				} else {
					provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(selectedArea.getUuid());
					provinceFilter.setItems(provinces);
				}

				criteria.area(selectedArea);
				criteria.region(null);
				refreshGridData(formAccess);
			} else {
				criteria.area(null);
				refreshGridData(formAccess);
			}
//            updateText(formAccess);
		});

		provinceFilter.setLabel(I18nProperties.getCaption(Captions.region));
		provinceFilter.setPlaceholder(I18nProperties.getCaption(Captions.regionAllRegions));
		provinceFilter.setClearButtonVisible(true);

		provinceFilter.addValueChangeListener(e -> {
			RegionReferenceDto selectedRegion = e.getValue();
			if (selectedRegion != null) {
				if (currentUser.getUser().getLanguage().toString().equals("Pashto")) {
					districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionPashto(e.getValue().getUuid());
					districtFilter.setItems(districts);
				} else if (currentUser.getUser().getLanguage().toString().equals("Dari")) {
					districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionDari(e.getValue().getUuid());
					districtFilter.setItems(districts);
				} else {
					districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(selectedRegion.getUuid());
					districtFilter.setItems(districts);
				}

				criteria.region(selectedRegion);
				refreshGridData(formAccess);
			} else {
				criteria.region(null);
				refreshGridData(formAccess);
			}
//            updateText(formAccess);
		});

		districtFilter.setLabel(I18nProperties.getCaption(Captions.district));
		districtFilter.setPlaceholder(I18nProperties.getCaption(Captions.districtAllDistricts));
		districtFilter.setClearButtonVisible(true);

		districtFilter.addValueChangeListener(e -> {
			DistrictReferenceDto selectedDistrict = e.getValue();
			if (selectedDistrict != null) {
				criteria.district(selectedDistrict);
				refreshGridData(formAccess);
			} else {
				criteria.district(null);
				refreshGridData(formAccess);
			}
//            updateText(formAccess);
		});
		
		
		configureFiltersByUserRoles(currentUser ,formAccess, criteria);

		errorStatusFilter = new ComboBox<ErrorStatusEnum>();
		errorStatusFilter.setItems(ErrorStatusEnum.values());
		errorStatusFilter.setLabel(I18nProperties.getCaption(Captions.errorStatus));
		errorStatusFilter.setPlaceholder(I18nProperties.getCaption(Captions.errorStatus));
		errorStatusFilter.setId("errorStatusFilter");
		errorStatusFilter.setAllowCustomValue(false);

		errorStatusFilter.setItemLabelGenerator(this::getLabelForEnum);
		errorStatusFilter.setClearButtonVisible(true);
		errorStatusFilter.addValueChangeListener(e -> {
			ErrorStatusEnum selectedErrorStatus = e.getValue();
//        	String stringg = "ErrorStatusEnum."+selectedErrorStatus;
			System.out.println(e.getValue() + " erorvalue chaged to ");

			if (e.getValue() != null) {

				criteria.errorStatusEnum((ErrorStatusEnum) selectedErrorStatus);
				refreshGridData(formAccess);
			} else {
				criteria.errorStatusEnum(ErrorStatusEnum.ALL_REPORT);
				refreshGridData(formAccess);
			}
//            updateText(formAccess);
		});

		resetButton = new Button(I18nProperties.getCaption(Captions.resetFilters));
		resetButton.addClickListener(e -> {

//            errorStatusFilter.clear();
//            criteria.area(null);
//			criteria.area(null);
//
//			criteria.region(null);
//			criteria.district(null);
			regionFilter.clear();
			provinceFilter.clear();
			districtFilter.clear();

//			criteria.errorStatusEnum(null);
			refreshGridData(formAccess);
//            updateText(formAccess);
		});

		exportReport.setIcon(new Icon(VaadinIcon.UPLOAD));
		exportReport.setText(I18nProperties.getCaption(Captions.export));
		exportReport.addClickListener(e -> {
			anchor.getElement().callJsFunction("click");
		});
		anchor.getStyle().set("display", "none");

		Div countAndButtons = new Div();

		Button displayFilters = new Button(I18nProperties.getCaption(Captions.hideFilters),
				new Icon(VaadinIcon.SLIDERS));
		displayFilters.addClickListener(e -> {
			I18nProperties.setUserLanguage(userProvider.getUser().getLanguage());
			filterLayout.setVisible(filterLayout.isVisible());
			displayFilters.setText(filterLayout.isVisible() ? "Show Filters" : "Hide Filters");
		});

		HorizontalLayout layout = new HorizontalLayout();
		layout.setAlignItems(Alignment.END);
		layout.getStyle().set("margin-left", "15px");

		layout.add(displayFilters, filterLayout);

		filterLayout.setClassName("row pl-3");
		filterLayout.add(regionFilter, provinceFilter, districtFilter, errorStatusFilter, exportReport, anchor);
		countAndButtons.add(layout);
		add(countAndButtons);
	}

//    private void updateText( FormAccess formAccess) {
//    	int numberOfRows = FacadeProvider.getCommunityFacade().getAllActiveCommunitytoRerenceCount(criteria, null, null, null, formAccess);
//        String newText = numberOfRows+"";
//        countRowItems.setText(newText);
//        Notification.show("Text updated: " + newText);
//    }

	private String getLabelForEnum(ErrorStatusEnum statusEnum) {
		switch (statusEnum) {
		case ERROR_REPORT:
			return "Error Reports";

		case ALL_REPORT:
			return "Non-Error Reports";

		default:
			return statusEnum.toString();
		}
	}

	private void userAnalysisGrid(CommunityCriteriaNew criteria, FormAccess formAccess) {

//    	countRowItems =  new Paragraph(numberOfRows + "");
//    	add(countRowItems);
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);

		ComponentRenderer<Span, CommunityUserReportModelDto> cCodeRenderer = new ComponentRenderer<>(input -> {
			NumberFormat arabicFormat = NumberFormat.getInstance();
			if (currentUser.getUser().getLanguage().toString().equals("Pashto")) {
				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
			} else if (currentUser.getUser().getLanguage().toString().equals("Dari")) {
				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
			}

			String value = String.valueOf(arabicFormat.format(input.getcCode()));
			Span label = new Span(value);
			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
			return label;
		});

		ComponentRenderer<Span, CommunityUserReportModelDto> clusterNumberRenderer = new ComponentRenderer<>(input -> {
			NumberFormat arabicFormat = NumberFormat.getInstance();
			if (currentUser.getUser().getLanguage().toString().equals("Pashto")) {
				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
			} else if (currentUser.getUser().getLanguage().toString().equals("Dari")) {
				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
			}

			String value = String.valueOf(arabicFormat.format(input.getClusterNumberr()));
			Span label = new Span(value);
			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
			return label;
		});

		grid.addColumn(CommunityUserReportModelDto::getArea).setHeader(I18nProperties.getCaption(Captions.area))
				.setSortProperty("region").setSortable(true).setResizable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.area));
		grid.addColumn(CommunityUserReportModelDto::getRegion).setHeader(I18nProperties.getCaption(Captions.region))
				.setSortProperty("province").setSortable(true).setResizable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.region));
		grid.addColumn(CommunityUserReportModelDto::getDistrict).setHeader(I18nProperties.getCaption(Captions.district))
				.setSortProperty("district").setSortable(true).setResizable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.district));
		grid.addColumn(CommunityUserReportModelDto::getFormAccess)
				.setHeader(I18nProperties.getCaption(Captions.formAccess)).setSortProperty("formAccess")
				.setSortable(true).setResizable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.formAccess));

		if (currentUser.getUser().getLanguage().toString().equals("Pashto")) {
			grid.addColumn(clusterNumberRenderer).setHeader(I18nProperties.getCaption(Captions.clusterNumber))
					.setSortProperty("clusterNumberr").setSortable(true).setResizable(true)
					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.clusterNumber));
			grid.addColumn(cCodeRenderer).setHeader(I18nProperties.getCaption(Captions.Community_externalID))
					.setSortProperty("ccode").setSortable(true).setResizable(true)
					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Community_externalID));
		} else if (currentUser.getUser().getLanguage().toString().equals("Dari")) {
			grid.addColumn(clusterNumberRenderer).setHeader(I18nProperties.getCaption(Captions.clusterNumber))
					.setSortProperty("clusterNumberr").setSortable(true).setResizable(true)
					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.clusterNumber));
			grid.addColumn(cCodeRenderer).setHeader(I18nProperties.getCaption(Captions.Community_externalID))
					.setSortProperty("ccode").setSortable(true).setResizable(true)
					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Community_externalID));
		} else {
			grid.addColumn(CommunityUserReportModelDto::getClusterNumberr)
					.setHeader(I18nProperties.getCaption(Captions.clusterNumber)).setSortProperty("clusterNumberr")
					.setSortable(true).setResizable(true)
					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.clusterNumber));
			grid.addColumn(CommunityUserReportModelDto::getcCode)
					.setHeader(I18nProperties.getCaption(Captions.Community_externalID)).setSortProperty("ccode")
					.setSortable(true).setResizable(true)
					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Community_externalID));
		}

		grid.addColumn(CommunityUserReportModelDto::getUsername)
				.setHeader(I18nProperties.getCaption(Captions.Login_username)).setSortProperty("username")
				.setSortable(true).setResizable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Login_username));
		grid.addColumn(CommunityUserReportModelDto::getMessage).setHeader(I18nProperties.getCaption(Captions.message))
				.setSortProperty("message").setSortable(true).setResizable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.message));

		int numberOfRows = FacadeProvider.getCommunityFacade().getAllActiveCommunitytoRerenceCount(null, null, null,
				null, formAccess);

		DataProvider<CommunityUserReportModelDto, CommunityCriteriaNew> dataProvider = DataProvider
				.fromFilteringCallbacks(
						query -> FacadeProvider.getCommunityFacade()
								.getAllActiveCommunitytoRerenceFlow(criteria, query.getOffset(), query.getLimit(),
										query.getSortOrders().stream()
												.map(sortOrder -> new SortProperty(sortOrder.getSorted(),
														sortOrder.getDirection() == SortDirection.ASCENDING))
												.collect(Collectors.toList()),
										formAccess)
								.stream().filter(e -> e.getFormAccess() != null).collect(Collectors.toList()).stream(),
						query -> numberOfRows
//                        FacadeProvider.getCommunityFacade().getAllActiveCommunitytoRerenceCount(
//                        		criteria , query.getOffset(), query.getLimit(),
//                                query.getSortOrders().stream()
//                                        .map(sortOrder -> new SortProperty(sortOrder.getSorted(),
//                                                sortOrder.getDirection() == SortDirection.ASCENDING))
//                                        .collect(Collectors.toList()),
//                                formAccess)
				);

		grid.setDataProvider(dataProvider);
		grid.setPageSize(250);
		grid.setVisible(true);

		GridExporter<CommunityUserReportModelDto> exporter = GridExporter.createFor(grid);
		exporter.setAutoAttachExportButtons(false);

		exporter.setTitle(I18nProperties.getCaption(Captions.campaignDataInformation));
		exporter.setFileName(
				"Mobile User Report" + new SimpleDateFormat("yyyyddMM").format(Calendar.getInstance().getTime()));

		anchor.setHref(exporter.getCsvStreamResource());
		anchor.getElement().setAttribute("download", true);
		anchor.setClassName("exportJsonGLoss");
		anchor.setId("campDatAnchor");

		anchor.getStyle().set("width", "100px");

		icon.getStyle().set("margin-right", "8px");
		icon.getStyle().set("font-size", "10px");

		anchor.getElement().insertChild(0, icon.getElement());

		add(grid);
	}

	private void refreshGridData(FormAccess formAccess) {
		DataProvider<CommunityUserReportModelDto, CommunityCriteriaNew> dataProvider = DataProvider
				.fromFilteringCallbacks(
						query -> FacadeProvider.getCommunityFacade()
								.getAllActiveCommunitytoRerenceFlow(criteria, query.getOffset(), query.getLimit(),
										query.getSortOrders().stream()
												.map(sortOrder -> new SortProperty(sortOrder.getSorted(),
														sortOrder.getDirection() == SortDirection.ASCENDING))
												.collect(Collectors.toList()),
										formAccess)
								.stream().filter(e -> e.getFormAccess() != null).collect(Collectors.toList()).stream(),
						query -> FacadeProvider.getCommunityFacade().getAllActiveCommunitytoRerenceCount(criteria,
								query.getOffset(), query.getLimit(),
								query.getSortOrders().stream()
										.map(sortOrder -> new SortProperty(sortOrder.getSorted(),
												sortOrder.getDirection() == SortDirection.ASCENDING))
										.collect(Collectors.toList()),
								formAccess));

		grid.setDataProvider(dataProvider);
	}
	
	
	
	public void generateProvinceComboItems(UserProvider user, FormAccess formAccess) {
		provinceFilter.clear();
		AreaReferenceDto selectedArea = regionFilter.getValue();
		if (selectedArea != null) {
			if (currentUser.getUser().getLanguage().toString().equals("Pashto")) {
				provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaPashto(regionFilter.getValue().getUuid());
				provinceFilter.setItems(provinces);
			} else if (currentUser.getUser().getLanguage().toString().equals("Dari")) {
				provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaDari(regionFilter.getValue().getUuid());
				provinceFilter.setItems(provinces);
			} else {
				provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(selectedArea.getUuid());
				provinceFilter.setItems(provinces);
			}

			criteria.area(selectedArea);
			criteria.region(null);
			refreshGridData(formAccess);
		} else {
			criteria.area(null);
			refreshGridData(formAccess);
		}
		
	}


	public void generateDistrictComboItems(UserProvider user, FormAccess formAccess) {
		
		districtFilter.clear();
		RegionReferenceDto selectedRegion = provinceFilter.getValue();
		if (selectedRegion != null) {
			if (currentUser.getUser().getLanguage().toString().equals("Pashto")) {
				districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionPashto(provinceFilter.getValue().getUuid());
				districtFilter.setItems(districts);
			} else if (currentUser.getUser().getLanguage().toString().equals("Dari")) {
				districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionDari(provinceFilter.getValue().getUuid());
				districtFilter.setItems(districts);
			} else {
				districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(selectedRegion.getUuid());
				districtFilter.setItems(districts);
			}

			criteria.region(selectedRegion);
			refreshGridData(formAccess);
		} else {
			criteria.region(null);
			refreshGridData(formAccess);
		}
	
		
		
	}
		
		
		public void configureFiltersByUserRoles(UserProvider userProvider, FormAccess formAccess, CommunityCriteriaNew criteria) {
			if (userProvider.getUser().getArea() != null) {
				regionFilter.setValue(userProvider.getUser().getArea());
				criteria.setArea(userProvider.getUser().getArea());
				regionFilter.setEnabled(false);
				refreshGridData(formAccess);
				generateProvinceComboItems(userProvider, formAccess);
			}

			if (userProvider.getUser().getRegion() != null) {
				provinceFilter.setValue(userProvider.getUser().getRegion());
				criteria.region(userProvider.getUser().getRegion());
				provinceFilter.setEnabled(false);
				generateDistrictComboItems(userProvider, formAccess);
			}

			if (userProvider.getUser().getDistrict() != null) {
				districtFilter.setValue(userProvider.getUser().getDistrict());
				criteria.district(userProvider.getUser().getDistrict());
				refreshGridData(formAccess);
				districtFilter.setEnabled(false);
//			generateDistrictComboItems();
			}
		}
}
