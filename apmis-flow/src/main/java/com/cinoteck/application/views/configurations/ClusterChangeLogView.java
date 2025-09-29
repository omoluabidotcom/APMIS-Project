package com.cinoteck.application.views.configurations;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cinoteck.application.UserProvider;
import com.cinoteck.application.views.utils.gridexporter.GridExporter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.symeda.sormas.api.ClusterFloatStatus;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.campaign.CampaignIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.ConfigurationChangeLogCriteria;
import de.symeda.sormas.api.infrastructure.ConfigurationChangeLogDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteriaNew;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.community.CommunityHistoryExtractDto;
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;

@PageTitle("APMIS-CLuster Change Log")
@Route(value = "cluster-change-log", layout = ConfigurationsView.class)
public class ClusterChangeLogView extends VerticalLayout {

	private static final long serialVersionUID = 5091856954264511639L;
//	private GridListDataView<CommunityDto> dataView;
	private CommunityCriteriaNew criteria = new CommunityCriteriaNew();

	ClusterDataProvider clusterDataProvider = new ClusterDataProvider();
	CommunityDto communityDto;
	ConfigurableFilterDataProvider<CommunityHistoryExtractDto, Void, CommunityCriteriaNew> filteredDataProvider;

	Grid<CommunityHistoryExtractDto> grid = new Grid<>(CommunityHistoryExtractDto.class, false);
	Anchor anchor = new Anchor("", I18nProperties.getCaption(Captions.export));
	UserProvider currentUser = new UserProvider();
	Paragraph countRowItems;

	UserProvider userProvider = new UserProvider();
	MenuBar dropdownBulkOperations = new MenuBar();
	ConfirmDialog archiveDearchiveConfirmation;
	String uuidsz = "";
	List<CommunityHistoryExtractDto> dataProvider;
	int itemCount;

	TextField searchField = new TextField();
	ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>(I18nProperties.getCaption(Captions.area));
	ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>(I18nProperties.getCaption(Captions.region));
	ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>(I18nProperties.getCaption(Captions.district));
	Button resetFilters = new Button(I18nProperties.getCaption(Captions.resetFilters));
	ComboBox<EntityRelevanceStatus> relevanceStatusFilter = new ComboBox<>(
			I18nProperties.getCaption(Captions.relevanceStatus));
	LocalDate localDate = LocalDate.now();
	Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	Query query = new Query();

	@SuppressWarnings("deprecation")
	
	public ClusterChangeLogView() {
		setSpacing(false);
		setHeightFull();
		setSizeFull();
		addFilters();
		clusterGrid();

	}

	private void clusterGrid() {

		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);

		ComponentRenderer<Span, CommunityDto> areaExternalIdRenderer = new ComponentRenderer<>(input -> {
			NumberFormat arabicFormat = NumberFormat.getInstance();
			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
			}
			String value = String.valueOf(arabicFormat.format(input.getAreaexternalId()));
			Span label = new Span(value);
			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
			return label;
		});

		ComponentRenderer<Span, CommunityDto> regionExternalIdRenderer = new ComponentRenderer<>(input -> {
			NumberFormat arabicFormat = NumberFormat.getInstance();
			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
			}
			String value = String.valueOf(arabicFormat.format(input.getRegionexternalId()));
			Span label = new Span(value);
			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
			return label;
		});

		ComponentRenderer<Span, CommunityDto> districtExternalIdRenderer = new ComponentRenderer<>(input -> {
			NumberFormat arabicFormat = NumberFormat.getInstance();
			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
			}
			String value = String.valueOf(arabicFormat.format(input.getDistrictexternalId()));
			Span label = new Span(value);
			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
			return label;
		});

		ComponentRenderer<Span, CommunityDto> clusterNumberRenderer = new ComponentRenderer<>(input -> {
			NumberFormat arabicFormat = NumberFormat.getInstance();
			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
			}
			String value = String.valueOf(arabicFormat.format(input.getClusterNumber()));
			Span label = new Span(value);
			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
			return label;
		});

		ComponentRenderer<Span, CommunityDto> communityExternalIdRenderer = new ComponentRenderer<>(input -> {
			NumberFormat arabicFormat = NumberFormat.getInstance();
			if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
				arabicFormat = NumberFormat.getInstance(new Locale("ps"));
			} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
				arabicFormat = NumberFormat.getInstance(new Locale("fa"));
			}
			String value = String.valueOf(arabicFormat.format(input.getExternalId()));
			Span label = new Span(value);
			label.getStyle().set("color", "var(--lumo-body-text-color) !important");
			return label;
		});
		
		TextRenderer<CommunityHistoryExtractDto> dateRenderer = new TextRenderer<>(dto -> {
			Date timestamp = dto.getChangedate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			if (timestamp != null) {
				return dateFormat.format(timestamp);

			} else {
				return "";
			}
		});

//		if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
//			grid.addColumn(CommunityHistoryExtractDto::get).setHeader(I18nProperties.getCaption(Captions.area))
//					.setSortable(true).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.area));
//			grid.addColumn(areaExternalIdRenderer).setHeader(I18nProperties.getCaption(Captions.Area_externalId))
//					.setResizable(true).setSortable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Area_externalId));
//			grid.addColumn(CommunityDto::getRegion).setHeader(I18nProperties.getCaption(Captions.region))
//					.setSortable(true).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.region));
//			grid.addColumn(regionExternalIdRenderer).setHeader(I18nProperties.getCaption(Captions.Region_externalID))
//					.setResizable(true).setSortable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Region_externalID));
//			grid.addColumn(CommunityDto::getDistrict).setHeader(I18nProperties.getCaption(Captions.district))
//					.setSortable(true).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.district));
//			grid.addColumn(districtExternalIdRenderer)
//					.setHeader(I18nProperties.getCaption(Captions.District_externalID)).setResizable(true)
//					.setSortable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.District_externalID));
//			grid.addColumn(CommunityDto::getName).setHeader(I18nProperties.getCaption(Captions.community))
//					.setSortable(true).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.community));
//			grid.addColumn(clusterNumberRenderer).setHeader(I18nProperties.getCaption(Captions.clusterNumber))
//					.setSortable(false).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.clusterNumber));
//			grid.addColumn(communityExternalIdRenderer)
//					.setHeader(I18nProperties.getCaption(Captions.Community_externalID)).setResizable(true)
//					.setSortable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Community_externalID));
//		} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
//
//			grid.addColumn(CommunityDto::getAreaname).setHeader(I18nProperties.getCaption(Captions.area))
//					.setSortable(true).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.area));
//			grid.addColumn(areaExternalIdRenderer).setHeader(I18nProperties.getCaption(Captions.Area_externalId))
//					.setResizable(true).setSortable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Area_externalId));
//			grid.addColumn(CommunityDto::getRegion).setHeader(I18nProperties.getCaption(Captions.region))
//					.setSortable(true).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.region));
//			grid.addColumn(regionExternalIdRenderer).setHeader(I18nProperties.getCaption(Captions.Region_externalID))
//					.setResizable(true).setSortable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Region_externalID));
//			grid.addColumn(CommunityDto::getDistrict).setHeader(I18nProperties.getCaption(Captions.district))
//					.setSortable(true).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.district));
//			grid.addColumn(districtExternalIdRenderer)
//					.setHeader(I18nProperties.getCaption(Captions.District_externalID)).setResizable(true)
//					.setSortable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.District_externalID));
//			grid.addColumn(CommunityDto::getName).setHeader(I18nProperties.getCaption(Captions.community))
//					.setSortable(true).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.community));
//			grid.addColumn(clusterNumberRenderer).setHeader(I18nProperties.getCaption(Captions.clusterNumber))
//					.setSortable(false).setResizable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.clusterNumber));
//			grid.addColumn(communityExternalIdRenderer)
//					.setHeader(I18nProperties.getCaption(Captions.Community_externalID)).setResizable(true)
//					.setSortable(true)
//					.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.Community_externalID));
//		} else {
		

			grid.addColumn(dateRenderer)
				.setHeader(I18nProperties.getCaption("Date")).setResizable(true).setSortable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption("Date"))
				.setComparator(Comparator.comparing(CommunityHistoryExtractDto::getChangedate));

		
			grid.addColumn(CommunityHistoryExtractDto::getName)
					.setHeader(I18nProperties.getCaption("Cluster Name")).setResizable(true).setSortable(true)
					.setTooltipGenerator(e -> I18nProperties.getCaption("Cluster Name"));
			
			grid.addColumn(CommunityHistoryExtractDto::getExternalId).setHeader(I18nProperties.getCaption("CCode"))
			.setSortable(true).setResizable(true)
			.setTooltipGenerator(e -> I18nProperties.getCaption("CCode"));
	
			grid.addColumn(CommunityHistoryExtractDto::getClusternumber).setHeader(I18nProperties.getCaption("Cluster Number"))
					.setSortable(true).setResizable(true)
					.setTooltipGenerator(e -> I18nProperties.getCaption("Cluster Number"));
			
			grid.addColumn(CommunityHistoryExtractDto::getDistrictname).setHeader(I18nProperties.getCaption("District"))
			.setSortable(true).setResizable(true)
			.setTooltipGenerator(e -> I18nProperties.getCaption("District"));
			
			grid.addColumn(CommunityHistoryExtractDto::getFloating)
					.setHeader(I18nProperties.getCaption("Float Status")).setResizable(true)
					.setSortable(true).setTooltipGenerator(e -> I18nProperties.getCaption("Float Status"));
			
			grid.addColumn(CommunityHistoryExtractDto::isArchived).setHeader(I18nProperties.getCaption("Active Status"))
					.setSortable(true).setResizable(true)
					.setTooltipGenerator(e -> I18nProperties.getCaption("Active Status"));

			
//		}
		

		grid.setVisible(true);

		criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		refreshGridData();

		
//		dataProvider = DataProvider
//				.fromStream(FacadeProvider.getCommunityFacade().getIndexList(criteria, null, null, null).stream());
		dataProvider = (ArrayList<CommunityHistoryExtractDto>) fetchClusterHistoryData();

		grid.setItems(dataProvider);

		add(grid);

		GridExporter<CommunityHistoryExtractDto> exporter = GridExporter.createFor(grid);
		exporter.setAutoAttachExportButtons(false);

		exporter.setTitle(I18nProperties.getCaption(Captions.mainMenuUsers));
		exporter.setFileName(
				"APMIS_Clusters_" + new SimpleDateFormat("ddMMyyyy").format(Calendar.getInstance().getTime()));

		anchor.setHref(exporter.getCsvStreamResource());
		anchor.getElement().setAttribute("download", true);
		anchor.setClassName("exportJsonGLoss");
		anchor.setId("exportCluster");
		Icon icon = VaadinIcon.UPLOAD_ALT.create();
		icon.getStyle().set("margin-right", "8px");
		icon.getStyle().set("font-size", "10px");

		anchor.getElement().insertChild(0, icon.getElement());

	}

	private List<CommunityHistoryExtractDto> fetchClusterHistoryData() {
//		List<SortProperty> sortProperties = query.getSortOrders().stream()
//				.map(order -> new SortProperty(order.getSorted(), order.getDirection().equals(SortDirection.ASCENDING)))
//				.collect(Collectors.toList());

		return (List<CommunityHistoryExtractDto>) FacadeProvider.getCommunityFacade()
				.getClusterDataChangeHistory();
//				.getIndexList(criteria, query.getOffset(), query.getLimit(), sortProperties).stream();

	}

	// TODO: Hide the filter bar on smaller screens
	public Component addFilters() {

		
		dataProvider = (ArrayList<CommunityHistoryExtractDto>) fetchClusterHistoryData();

		itemCount =  5; //dataProvider.getItems().size();
		countRowItems = new Paragraph(I18nProperties.getCaption(Captions.rows) + itemCount);
//
		countRowItems.setId("rowCount");

		HorizontalLayout layout = new HorizontalLayout();
		layout.setPadding(false);
		layout.setVisible(true);
		layout.setAlignItems(Alignment.END);

		HorizontalLayout relevancelayout = new HorizontalLayout();
		relevancelayout.setPadding(false);
		relevancelayout.setVisible(true);
		relevancelayout.setAlignItems(Alignment.END);
		relevancelayout.setJustifyContentMode(JustifyContentMode.END);
		relevancelayout.setClassName("row");

		HorizontalLayout vlayout = new HorizontalLayout();
		vlayout.setPadding(false);

		vlayout.setAlignItems(Alignment.END);

		Button displayFilters = new Button(I18nProperties.getCaption(Captions.hideFilters),
				new Icon(VaadinIcon.SLIDERS));
		displayFilters.getStyle().set("margin-left", "1em");
		displayFilters.addClickListener(e -> {
			I18nProperties.setUserLanguage(userProvider.getUser().getLanguage());
			if (layout.isVisible() == false) {
				layout.setVisible(true);
				relevancelayout.setVisible(true);

				displayFilters.setText(I18nProperties.getCaption(Captions.hideFilters));
			} else {
				layout.setVisible(false);
				relevancelayout.setVisible(false);

				displayFilters.setText(I18nProperties.getCaption(Captions.showFilters));
			}
		});

		layout.setPadding(false);

		TextField searchField = new TextField();
		ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>(I18nProperties.getCaption(Captions.area));
		ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>(I18nProperties.getCaption(Captions.region));
		ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>(I18nProperties.getCaption(Captions.district));
		Button resetFilters = new Button(I18nProperties.getCaption(Captions.resetFilters));
		ComboBox<EntityRelevanceStatus> relevanceStatusFilter = new ComboBox<>(
				I18nProperties.getCaption(Captions.relevanceStatus));

		ComboBox<ClusterFloatStatus> floatingStatusFilter = new ComboBox<>(
				I18nProperties.getCaption(Captions.floatStatus));

		searchField.addClassName("filterBar");
		searchField.setPlaceholder(I18nProperties.getCaption(Captions.actionSearch));
		Icon searchIcon = new Icon(VaadinIcon.SEARCH);
		searchIcon.getStyle().set("color", "#0D6938");
		searchField.setPrefixComponent(searchIcon);
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.setWidth("10%");
		searchField.setClearButtonVisible(true);
		layout.add(searchField);

		regionFilter.setPlaceholder(I18nProperties.getCaption(Captions.areaAllAreas));
		regionFilter.setClearButtonVisible(true);
		regionFilter.getStyle().set("width", "145px !important");
//		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());

		if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
			regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReferencePashto());
		} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
			regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReferenceDari());
		} else {
			regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		}

		if (currentUser.getUser().getArea() != null) {
			regionFilter.setValue(currentUser.getUser().getArea());
			criteria.area(currentUser.getUser().getArea());
			provinceFilter.setItems(
					FacadeProvider.getRegionFacade().getAllActiveByArea(currentUser.getUser().getArea().getUuid()));
			regionFilter.setEnabled(false);
			refreshGridData();
		}

		layout.add(regionFilter);

		provinceFilter.setPlaceholder(I18nProperties.getCaption(Captions.regionAllRegions));
		provinceFilter.setClearButtonVisible(true);
		provinceFilter.getStyle().set("width", "145px !important");

//		provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		if (currentUser.getUser().getRegion() != null) {
			provinceFilter.setValue(currentUser.getUser().getRegion());
			criteria.region(currentUser.getUser().getRegion());
			districtFilter.setItems(FacadeProvider.getDistrictFacade()
					.getAllActiveByRegion(currentUser.getUser().getRegion().getUuid()));
			provinceFilter.setEnabled(false);
			refreshGridData();
		}
		layout.add(provinceFilter);

		districtFilter.setPlaceholder(I18nProperties.getCaption(Captions.districtAllDistricts));
		districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
		districtFilter.getStyle().set("width", "145px !important");

		if (currentUser.getUser().getDistrict() != null) {
			districtFilter.setValue(currentUser.getUser().getDistrict());
			criteria.district(currentUser.getUser().getDistrict());
			districtFilter.setEnabled(false);
			refreshGridData();
		}
		layout.add(districtFilter);

		relevanceStatusFilter.setItems(EntityRelevanceStatus.values());
		relevanceStatusFilter.getStyle().set("width", "145px !important");
		relevanceStatusFilter.setPlaceholder("Active");

		relevanceStatusFilter.setItemLabelGenerator(status -> {
			if (status == EntityRelevanceStatus.ARCHIVED) {
				return I18nProperties.getCaption(Captions.archived);
			} else if (status == EntityRelevanceStatus.ACTIVE) {
				return I18nProperties.getCaption(Captions.active);
			} else if (status == EntityRelevanceStatus.ALL) {
				return I18nProperties.getCaption(Captions.all);
			}
			// Handle other enum values if needed
			return status.toString();
		});

		floatingStatusFilter.setItems(ClusterFloatStatus.values());
		floatingStatusFilter.getStyle().set("width", "145px !important");
		floatingStatusFilter.setPlaceholder("Normal");

		floatingStatusFilter.setItemLabelGenerator(status -> {
			if (status == ClusterFloatStatus.NORMAL) {
				return I18nProperties.getCaption(Captions.floatingNormal);
			} else if (status == ClusterFloatStatus.FLOATING) {
				return I18nProperties.getCaption(Captions.floatingFloat);
			}
			// Handle other enum values if needed
			return status.toString();
		});

		layout.add(resetFilters);

		searchField.addValueChangeListener(e -> {
			criteria.nameLike(e.getValue());
			refreshGridData();
			resetFilters.setVisible(true);

		});

		regionFilter.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
					provinceFilter.setItems(
							FacadeProvider.getRegionFacade().getAllActiveByAreaPashto(e.getValue().getUuid()));
				} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
					provinceFilter
							.setItems(FacadeProvider.getRegionFacade().getAllActiveByAreaDari(e.getValue().getUuid()));
				} else {
					provinceFilter
							.setItems(FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid()));
				}
				AreaReferenceDto area = e.getValue();
				criteria.area(area);
				refreshGridData();
//				System.out.println(regionFilter.getValue() + "region filtervalue ");
				resetFilters.setVisible(true);
			} else {
				criteria.area(null);
				refreshGridData();
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
//			filteredDataProvider.setFilter(criteria);
				RegionReferenceDto province = e.getValue();
				criteria.region(province);
				refreshGridData();
			} else {
				criteria.region(null);
				refreshGridData();
			}

		});
		districtFilter.setClearButtonVisible(true);
		districtFilter.addValueChangeListener(e -> {
			if (districtFilter.getValue() != null) {
//			filteredDataProvider.setFilter(criteria);
				DistrictReferenceDto district = e.getValue();
				criteria.district(district);
				refreshGridData();

			} else {
				criteria.district(null);
				refreshGridData();
			}
		});

		relevanceStatusFilter.setClearButtonVisible(true);
		relevanceStatusFilter.addValueChangeListener(e -> {
			if (relevanceStatusFilter.getValue() == null) {
				System.out.println(" Nullll  has beeen actiovated ===============");

				EntityRelevanceStatus selectedStatus = e.getValue();
				criteria.relevanceStatus(selectedStatus);
				refreshGridData();
			} else if (relevanceStatusFilter.getValue().equals(EntityRelevanceStatus.ACTIVE)) {
				EntityRelevanceStatus selectedStatus = e.getValue();
				criteria.relevanceStatus(selectedStatus);
				refreshGridData();
			} else if (relevanceStatusFilter.getValue().equals(EntityRelevanceStatus.ARCHIVED)) {
				EntityRelevanceStatus selectedStatus = e.getValue();
				criteria.relevanceStatus(selectedStatus);
				refreshGridData();

			} else if (relevanceStatusFilter.getValue().equals(EntityRelevanceStatus.ALL)) {
				EntityRelevanceStatus selectedStatus = e.getValue();
				criteria.relevanceStatus(selectedStatus);
				refreshGridData();

			} else {
				EntityRelevanceStatus selectedStatus = e.getValue();
				criteria.relevanceStatus(selectedStatus);
				refreshGridData();
			}

		});

		floatingStatusFilter.setClearButtonVisible(true);
		floatingStatusFilter.addValueChangeListener(e -> {
			if (floatingStatusFilter.getValue() == null) {
				ClusterFloatStatus selectedStatus = e.getValue();
				criteria.floatStatus(selectedStatus);
				refreshGridData();
				
			
			} else {
				if (floatingStatusFilter.getValue().equals(ClusterFloatStatus.FLOATING)) {
					ClusterFloatStatus selectedStatus = e.getValue();
					criteria.floatStatus(selectedStatus);
					refreshGridData();
				} else if (floatingStatusFilter.getValue().equals(ClusterFloatStatus.NORMAL)) {
					ClusterFloatStatus selectedStatus = e.getValue();
					criteria.floatStatus(selectedStatus);
					refreshGridData();

				} else {
					ClusterFloatStatus selectedStatus = e.getValue();
					criteria.floatStatus(selectedStatus);
					refreshGridData();
				}
				
			}
			
			System.out.println(criteria.getFloatStatus() + "Floar Status=------------------ ");
		});

		resetFilters.addClassName("resetButton");
//		resetFilters.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		resetFilters.setVisible(false);

		Button exportCluster = new Button(I18nProperties.getCaption(Captions.export));
		exportCluster.setIcon(new Icon(VaadinIcon.UPLOAD));

		exportCluster.addClickListener(e -> {
			anchor.getElement().callJsFunction("click");

		});		
		
		if (userProvider.hasUserRight(UserRight.INFRASTRUCTURE_EXPORT)) {
			layout.add(exportCluster, anchor);
		}
//		layout.addComponentAsFirst(anchor);
		layout.setWidth("75%");
		layout.addClassName("pl-3");
		layout.addClassName("row");

		relevancelayout.add(relevanceStatusFilter, floatingStatusFilter, countRowItems);
		vlayout.setWidth("99%");
		vlayout.add(displayFilters, layout, relevancelayout);
		vlayout.getStyle().set("margin-right", "0.5rem");

		add(vlayout);

		

		resetFilters.addClickListener(e -> {

			if (!searchField.isEmpty()) {
				refreshGridData();
				searchField.clear();
			}
			if (!regionFilter.isEmpty()) {
				refreshGridData();
				regionFilter.clear();
			}
			if (!provinceFilter.isEmpty()) {
				refreshGridData();
				provinceFilter.clear();
			}
			if (!districtFilter.isEmpty()) {
				refreshGridData();
				districtFilter.clear();
			}
			if (!relevanceStatusFilter.isEmpty()) {
				refreshGridData();
				relevanceStatusFilter.clear();
			}
			if (!floatingStatusFilter.isEmpty()) {
				refreshGridData();
				floatingStatusFilter.clear();
			}
			refreshGridData();

		});
		return vlayout;
	}



	
	private void updateRowCount() {
		int numberOfRows = filteredDataProvider.size(new Query<>());
		String newText = I18nProperties.getCaption(Captions.rows) + numberOfRows;

		countRowItems.setText(newText);
		countRowItems.setId("rowCount");
	}

	private void refreshGridData() {
		dataProvider = (ArrayList<CommunityHistoryExtractDto>) fetchClusterHistoryData();

		grid.setItems(dataProvider);
//		dataView = grid.setItems(dataProvider);
		itemCount = 5; //dataProvider.getItems().size();

		String newText = I18nProperties.getCaption(Captions.rows) + itemCount;
		countRowItems.setText(newText);
		countRowItems.setId("rowCount");

//		dataView = grid.setItems(dataProvider);
	}

}