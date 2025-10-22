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
import com.vaadin.flow.server.StreamResource;

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
    
    // Export button declaration
    Button exportCluster;

    @SuppressWarnings("deprecation")
    public ClusterChangeLogView(CommunityCriteriaNew criteria) {
        setSpacing(false);
        setHeightFull();
        setSizeFull();
        addFilters(criteria);
        clusterGrid(criteria);
//        setupExport();
    }


    private List<CommunityHistoryExtractDto> fetchClusterHistoryData(CommunityCriteriaNew criteria) {
        try {
//            List<CommunityHistoryExtractDto> data = FacadeProvider.getCommunityFacade()
//                    .getClusterDataChangeHistory();
        	
        	 List<CommunityHistoryExtractDto> data = FacadeProvider.getCommunityFacade()
                     .getClusterDataChangeHistory(criteria);
            
            // Validate data is not null
            if (data == null) {
                System.out.println("No data returned from facade");
                return new ArrayList<>();
            }
            
            System.out.println("Fetched " + data.size() + " records for export");
            return data;
        } catch (Exception e) {
            Notification.show("Error fetching data: " + e.getMessage(), 5000, Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // TODO: Hide the filter bar on smaller screens
    public Component addFilters(CommunityCriteriaNew criteria) {

        dataProvider = fetchClusterHistoryData(criteria);

        itemCount = dataProvider.size();
        countRowItems = new Paragraph(I18nProperties.getCaption(Captions.rows) + itemCount);

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
            refreshGridData(criteria);
        }

        layout.add(regionFilter);

        provinceFilter.setPlaceholder(I18nProperties.getCaption(Captions.regionAllRegions));
        provinceFilter.setClearButtonVisible(true);
        provinceFilter.getStyle().set("width", "145px !important");

        if (currentUser.getUser().getRegion() != null) {
            provinceFilter.setValue(currentUser.getUser().getRegion());
            criteria.region(currentUser.getUser().getRegion());
            districtFilter.setItems(FacadeProvider.getDistrictFacade()
                    .getAllActiveByRegion(currentUser.getUser().getRegion().getUuid()));
            provinceFilter.setEnabled(false);
            refreshGridData(criteria);
        }
        layout.add(provinceFilter);

        districtFilter.setPlaceholder(I18nProperties.getCaption(Captions.districtAllDistricts));
        districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
        districtFilter.getStyle().set("width", "145px !important");

        if (currentUser.getUser().getDistrict() != null) {
            districtFilter.setValue(currentUser.getUser().getDistrict());
            criteria.district(currentUser.getUser().getDistrict());
            districtFilter.setEnabled(false);
            refreshGridData(criteria);
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
            return status.toString();
        });

        layout.add(resetFilters);

        searchField.addValueChangeListener(e -> {
            criteria.nameLike(e.getValue());
            refreshGridData(criteria);
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
                refreshGridData(criteria);
                resetFilters.setVisible(true);
            } else {
                criteria.area(null);
                refreshGridData(criteria);
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
                criteria.region(province);
                refreshGridData(criteria);
            } else {
                criteria.region(null);
                refreshGridData(criteria);
            }
        });
        
        districtFilter.setClearButtonVisible(true);
        districtFilter.addValueChangeListener(e -> {
            if (districtFilter.getValue() != null) {
                DistrictReferenceDto district = e.getValue();
                criteria.district(district);
                refreshGridData(criteria);
            } else {
                criteria.district(null);
                refreshGridData(criteria);
            }
        });

        relevanceStatusFilter.setClearButtonVisible(true);
        relevanceStatusFilter.addValueChangeListener(e -> {
            if (relevanceStatusFilter.getValue() == null) {
                EntityRelevanceStatus selectedStatus = e.getValue();
                criteria.relevanceStatus(selectedStatus);
                refreshGridData(criteria);
            } else if (relevanceStatusFilter.getValue().equals(EntityRelevanceStatus.ACTIVE)) {
                EntityRelevanceStatus selectedStatus = e.getValue();
                criteria.relevanceStatus(selectedStatus);
                refreshGridData(criteria);
            } else if (relevanceStatusFilter.getValue().equals(EntityRelevanceStatus.ARCHIVED)) {
                EntityRelevanceStatus selectedStatus = e.getValue();
                criteria.relevanceStatus(selectedStatus);
                refreshGridData(criteria);
            } else if (relevanceStatusFilter.getValue().equals(EntityRelevanceStatus.ALL)) {
                EntityRelevanceStatus selectedStatus = e.getValue();
                criteria.relevanceStatus(selectedStatus);
                refreshGridData(criteria);
            } else {
                EntityRelevanceStatus selectedStatus = e.getValue();
                criteria.relevanceStatus(selectedStatus);
                refreshGridData(criteria);
            }
        });

        floatingStatusFilter.setClearButtonVisible(true);
        floatingStatusFilter.addValueChangeListener(e -> {
            if (floatingStatusFilter.getValue() == null) {
                ClusterFloatStatus selectedStatus = e.getValue();
                criteria.floatStatus(selectedStatus);
                refreshGridData(criteria);
            } else {
                if (floatingStatusFilter.getValue().equals(ClusterFloatStatus.FLOATING)) {
                    ClusterFloatStatus selectedStatus = e.getValue();
                    criteria.floatStatus(selectedStatus);
                    refreshGridData(criteria);
                } else if (floatingStatusFilter.getValue().equals(ClusterFloatStatus.NORMAL)) {
                    ClusterFloatStatus selectedStatus = e.getValue();
                    criteria.floatStatus(selectedStatus);
                    refreshGridData(criteria);
                } else {
                    ClusterFloatStatus selectedStatus = e.getValue();
                    criteria.floatStatus(selectedStatus);
                    refreshGridData(criteria);
                }
            }
        });

        resetFilters.addClassName("resetButton");
        resetFilters.setVisible(false);

        // Fix the export button setup
        exportCluster = new Button(I18nProperties.getCaption(Captions.export));
        exportCluster.setIcon(new Icon(VaadinIcon.UPLOAD));
        exportCluster.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        anchor.getStyle().set("display", "none");
        
        exportCluster.addClickListener(e -> {
            try {
                // Get fresh data
                dataProvider = fetchClusterHistoryData(criteria);
                
                if (dataProvider == null || dataProvider.isEmpty()) {
                    Notification.show("No data available to export", 5000, Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }
                
                // Update grid with fresh data
                ListDataProvider<CommunityHistoryExtractDto> listDataProvider = new ListDataProvider<>(dataProvider);
                grid.setItems(listDataProvider);
                
                // Create GridExporter instance
                GridExporter<CommunityHistoryExtractDto> exporter = GridExporter.createFor(grid);
                exporter.setAutoAttachExportButtons(false);
                exporter.setTitle("Cluster Change Log");
                exporter.setCsvExportEnabled(true);
       
                // Set export value providers for all columns
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                
                // Date column
                exporter.setExportValue(grid.getColumnByKey("changedate"), dto -> {
                    Date timestamp = ((CommunityHistoryExtractDto) dto).getChangedate();
                    return timestamp != null ? dateFormat.format(timestamp) : "";
                });
                
                // Name column
                exporter.setExportValue(grid.getColumnByKey("name"), dto -> {
                    String name = ((CommunityHistoryExtractDto) dto).getName();
                    return name != null ? name : "";
                });
                
                // External ID column
                exporter.setExportValue(grid.getColumnByKey("externalId"), dto -> {
                    Long externalId = ((CommunityHistoryExtractDto) dto).getExternalId();
                    return externalId != null ? String.valueOf(externalId) : "";
                });
                
                // Cluster Number column
                exporter.setExportValue(grid.getColumnByKey("clusternumber"), dto -> {
                    Long clusterNumber = (long) ((CommunityHistoryExtractDto) dto).getClusternumber();
                    return clusterNumber != null ? String.valueOf(clusterNumber) : "";
                });
                
                // District column
                exporter.setExportValue(grid.getColumnByKey("districtname"), dto -> {
                    String district = ((CommunityHistoryExtractDto) dto).getDistrictname();
                    return district != null ? district : "";
                });
                
                // Floating column
                exporter.setExportValue(grid.getColumnByKey("floating"), dto -> {
                    String floating = ((CommunityHistoryExtractDto) dto).getFloating();
                    return floating != null ? floating : "";
                });
                
                // Archived column
                exporter.setExportValue(grid.getColumnByKey("archived"), dto -> {
                    boolean archived = ((CommunityHistoryExtractDto) dto).isArchived();
                    return archived ? "Archived" : "Active";
                });
                
                // Generate filename with timestamp
                String fileName = "APMIS_Clusters_ChangeLog_" + 
                    new SimpleDateFormat("ddMMyyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".csv";
                
                
                exporter.setFileName(fileName);
                
                // Get the CSV stream resource
                StreamResource streamResource = exporter.getCsvStreamResource();
                
                if (streamResource != null) {
                    // Set the resource and trigger download
                    anchor.setHref(streamResource);
                    anchor.setText(fileName);
                    
                    // Use JavaScript to trigger the download
                    anchor.getElement().executeJs(
                        "const link = this;" +
                        "setTimeout(() => link.click(), 100);"
                    );
                    
                    Notification.show("Exporting " + dataProvider.size() + " records...", 
                        3000, Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    Notification.show("Failed to generate export file", 5000, Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                
            } catch (Exception ex) {
                Notification.show("Export failed: " + ex.getMessage(), 5000, Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });

        // Make sure anchor and export button are properly added
        if (userProvider.hasUserRight(UserRight.INFRASTRUCTURE_EXPORT)) {
            layout.add(exportCluster,anchor);
//            add(anchor); // Ensure anchor is added to the main layout
        }
        
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
                refreshGridData(criteria);
                searchField.clear();
            }
            if (!regionFilter.isEmpty()) {
                refreshGridData(criteria);
                regionFilter.clear();
            }
            if (!provinceFilter.isEmpty()) {
                refreshGridData(criteria);
                provinceFilter.clear();
            }
            if (!districtFilter.isEmpty()) {
                refreshGridData(criteria);
                districtFilter.clear();
            }
            if (!relevanceStatusFilter.isEmpty()) {
                refreshGridData(criteria);
                relevanceStatusFilter.clear();
            }
            if (!floatingStatusFilter.isEmpty()) {
                refreshGridData(criteria);
                floatingStatusFilter.clear();
            }
            refreshGridData(criteria);
        });
        return vlayout;
    }
    

 private void clusterGrid(CommunityCriteriaNew criteria) {
     grid.setSelectionMode(SelectionMode.SINGLE);
     grid.setMultiSort(true, MultiSortPriority.APPEND);
     grid.setSizeFull();
     grid.setColumnReorderingAllowed(true);

     // Date column with TextRenderer AND export value provider
     TextRenderer<CommunityHistoryExtractDto> dateRenderer = new TextRenderer<>(dto -> {
         Date timestamp = dto.getChangedate();
         SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
         if (timestamp != null) {
             return dateFormat.format(timestamp);
         } else {
             return "";
         }
     });

     Grid.Column<CommunityHistoryExtractDto> dateColumn = grid.addColumn(dateRenderer)
         .setHeader(I18nProperties.getCaption("Date"))
         .setResizable(true)
         .setSortable(true)
         .setKey("changedate")
         .setTooltipGenerator(e -> I18nProperties.getCaption("Date"))
         .setComparator(Comparator.comparing(CommunityHistoryExtractDto::getChangedate, 
             Comparator.nullsLast(Comparator.naturalOrder())));

     Grid.Column<CommunityHistoryExtractDto> nameColumn = grid.addColumn(CommunityHistoryExtractDto::getName)
         .setHeader(I18nProperties.getCaption("Cluster Name"))
         .setResizable(true)
         .setSortable(true)
         .setKey("name")
         .setTooltipGenerator(e -> I18nProperties.getCaption("Cluster Name"));
     
     Grid.Column<CommunityHistoryExtractDto> externalIdColumn = grid.addColumn(CommunityHistoryExtractDto::getExternalId)
         .setHeader(I18nProperties.getCaption("CCode"))
         .setSortable(true)
         .setResizable(true)
         .setKey("externalId")
         .setTooltipGenerator(e -> I18nProperties.getCaption("CCode"));

     Grid.Column<CommunityHistoryExtractDto> clusterNumberColumn = grid.addColumn(CommunityHistoryExtractDto::getClusternumber)
         .setHeader(I18nProperties.getCaption("Cluster Number"))
         .setSortable(true)
         .setResizable(true)
         .setKey("clusternumber")
         .setTooltipGenerator(e -> I18nProperties.getCaption("Cluster Number"));
     
     Grid.Column<CommunityHistoryExtractDto> districtColumn = grid.addColumn(CommunityHistoryExtractDto::getDistrictname)
         .setHeader(I18nProperties.getCaption("District"))
         .setSortable(true)
         .setResizable(true)
         .setKey("districtname")
         .setTooltipGenerator(e -> I18nProperties.getCaption("District"));
     
     Grid.Column<CommunityHistoryExtractDto> floatingColumn = grid.addColumn(CommunityHistoryExtractDto::getFloating)
         .setHeader(I18nProperties.getCaption("Float Status"))
         .setResizable(true)
         .setSortable(true)
         .setKey("floating")
         .setTooltipGenerator(e -> I18nProperties.getCaption("Float Status"));
     
     Grid.Column<CommunityHistoryExtractDto> archivedColumn = grid.addColumn(dto -> dto.isArchived() ? "Archived" : "Active")
         .setHeader(I18nProperties.getCaption("Active Status"))
         .setSortable(true)
         .setResizable(true)
         .setKey("archived")
         .setTooltipGenerator(e -> I18nProperties.getCaption("Active Status"));

     grid.setVisible(true);

     criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
     refreshGridData(criteria);

     dataProvider = fetchClusterHistoryData(criteria);
     ListDataProvider<CommunityHistoryExtractDto> listDataProvider = new ListDataProvider<>(dataProvider);
     grid.setItems(listDataProvider);

     add(grid);
     
   
     
     anchor.setClassName("exportJsonGLoss");
     anchor.setId("exportCluster");
     anchor.getElement().setAttribute("download", true);
     anchor.getElement().setAttribute("target", "_blank");

     
     
     Icon icon = VaadinIcon.UPLOAD_ALT.create();
     icon.getStyle().set("margin-right", "8px");
     icon.getStyle().set("font-size", "10px");
     anchor.getElement().insertChild(0, icon.getElement());
     


   
     
 }

 // Update setupExport() to configure export value providers
 private void setupExport() {
     try {
    	 
         GridExporter<CommunityHistoryExtractDto> exporter;

         exporter = GridExporter.createFor(grid);
         exporter.setAutoAttachExportButtons(false);
         exporter.setTitle("Cluster Change Log");
         exporter.setCsvExportEnabled(true);
         
         // Set export value providers for all columns
         SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
         
         // Date column
         exporter.setExportValue(grid.getColumnByKey("changedate"), dto -> {
             Date timestamp = ((CommunityHistoryExtractDto) dto).getChangedate();
             return timestamp != null ? dateFormat.format(timestamp) : "";
         });
         
         // Name column
         exporter.setExportValue(grid.getColumnByKey("name"), dto -> {
             String name = ((CommunityHistoryExtractDto) dto).getName();
             return name != null ? name : "";
         });
         
         // External ID column
         exporter.setExportValue(grid.getColumnByKey("externalId"), dto -> {
             Long externalId = ((CommunityHistoryExtractDto) dto).getExternalId();
             return externalId != null ? String.valueOf(externalId) : "";
         });
         
         // Cluster Number column
         exporter.setExportValue(grid.getColumnByKey("clusternumber"), dto -> {
             Long clusterNumber = (long) ((CommunityHistoryExtractDto) dto).getClusternumber();
             return clusterNumber != null ? String.valueOf(clusterNumber) : "";
         });
         
         // District column
         exporter.setExportValue(grid.getColumnByKey("districtname"), dto -> {
             String district = ((CommunityHistoryExtractDto) dto).getDistrictname();
             return district != null ? district : "";
         });
         
         // Floating column
         exporter.setExportValue(grid.getColumnByKey("floating"), dto -> {
             String floating = ((CommunityHistoryExtractDto) dto).getFloating();
             return floating != null ? floating : "";
         });
         
         // Archived column
         exporter.setExportValue(grid.getColumnByKey("archived"), dto -> {
             boolean archived = ((CommunityHistoryExtractDto) dto).isArchived();
             return archived ? "Archived" : "Active";
         });
         
         // Initialize anchor
         anchor.setClassName("exportJsonGLoss");
         anchor.setId("exportCluster");
         anchor.getElement().setAttribute("download", true);
         
         Icon icon = VaadinIcon.UPLOAD_ALT.create();
         icon.getStyle().set("margin-right", "8px");
         icon.getStyle().set("font-size", "10px");
         anchor.getElement().insertChild(0, icon.getElement());
         add(anchor);
         
     } catch (Exception e) {
         Notification.show("Error setting up export: " + e.getMessage(), 5000, Position.TOP_CENTER)
             .addThemeVariants(NotificationVariant.LUMO_ERROR);
         e.printStackTrace();
     }
 }

 // Update the export button click listener
 // Add this in your addFilters() method where you create exportCluster button:
 

 // Update refreshExportData()
 private void refreshExportData(CommunityCriteriaNew criteria) {
     try {
         dataProvider = fetchClusterHistoryData(criteria);
         
         if (dataProvider != null && !dataProvider.isEmpty()) {
             ListDataProvider<CommunityHistoryExtractDto> listDataProvider = 
                 new ListDataProvider<>(dataProvider);
             grid.setItems(listDataProvider);
             
             itemCount = dataProvider.size();
             String newText = I18nProperties.getCaption(Captions.rows) + itemCount;
             countRowItems.setText(newText);
         }
         
     } catch (Exception e) {
         Notification.show("Error refreshing data: " + e.getMessage(), 3000, Position.TOP_CENTER)
             .addThemeVariants(NotificationVariant.LUMO_ERROR);
         e.printStackTrace();
     }
 }
 
 
private void refreshGridData(CommunityCriteriaNew criteria) {
        refreshExportData(criteria); // Use the same method to refresh both grid and export data
    }

    private void updateRowCount() {
        int numberOfRows = dataProvider.size();
        String newText = I18nProperties.getCaption(Captions.rows) + numberOfRows;
        countRowItems.setText(newText);
        countRowItems.setId("rowCount");
    }
}