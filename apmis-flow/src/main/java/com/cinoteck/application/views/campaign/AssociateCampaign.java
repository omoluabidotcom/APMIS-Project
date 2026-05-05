package com.cinoteck.application.views.campaign;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import com.cinoteck.application.UserProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridMultiSelectionModel.SelectAllCheckboxVisibility;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignTreeGridDto;
import de.symeda.sormas.api.campaign.CampaignTreeGridDtoImpl;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

@PageTitle("APMIS-Edit Campaign")
@Route(value = "/databb")
public class AssociateCampaign extends VerticalLayout {
    private static final long serialVersionUID = 764300181578209719L;
    public static TreeGrid<CampaignTreeGridDto> treeGrid = new TreeGrid<>();
    private UserProvider userProvider = new UserProvider();
    private CampaignDto campaignDto;
    private Set<AreaReferenceDto> areass = new HashSet<>();
    private Set<RegionReferenceDto> region = new HashSet<>();
    private Set<DistrictReferenceDto> districts = new HashSet<>();
    private Set<CommunityReferenceDto> community = new HashSet<>();
    private Set<PopulationDataDto> popopulationDataDtoSet = new HashSet<>();
    public Boolean isCreateForm = null;
    List<CampaignTreeGridDto> deletelist = new ArrayList<>();
    List<String> populationDataUuid = new ArrayList<>();
    Checkbox selectDistrictCheckbox = new Checkbox();
    private boolean isSingleSelectClickItemLock;
    private boolean isMultiSelectItemLock;
    private boolean isSelectItemLock;
    private boolean isInitializing = false;
    FormLayout formx;
    TextField creatingUuid = new TextField(I18nProperties.getCaption(Captions.uuid));
    
    private java.util.Map<String, Checkbox> checkboxMap = new java.util.HashMap<>();

    
    ComboBox<String> districtModality = new ComboBox<String>("Modality");
    ComboBox<String> districtStatus = new ComboBox<String>("Status");
    IntegerField popDataAge4_23M = new IntegerField("Target 4_23M");
    IntegerField popDataAge5_10 = new IntegerField("Target 60-120M");
    IntegerField popData0_4 = new IntegerField("Target 0-59M");
    ComboBox<CommunityReferenceDto> clusterFilter = new ComboBox<>(I18nProperties.getCaption(Captions.community));
    ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>(I18nProperties.getCaption(Captions.district));
    ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>(I18nProperties.getCaption(Captions.region));
    ComboBox<AreaReferenceDto> regionFilter = new ComboBox<AreaReferenceDto>();
    HorizontalLayout buttonAfterLay = new HorizontalLayout();
    List<PopulationDataDto> populationDataList = new ArrayList<PopulationDataDto>();
    
    Set<String> selectedAreaUuids = new HashSet<>();
    Set<String> selectedRegionUuids = new HashSet<>();
    Set<String> selectedDistrictUuids = new HashSet<>();
    Set<String> selectedClusterUuids = new HashSet<>();
    
    private Set<String> pendingSelectedClusters = new HashSet<>();
    private Set<String> pendingDeselectedClusters = new HashSet<>();
    boolean hasPendingChanges = false;

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public AssociateCampaign(CampaignDto formData) {
        super();
        I18nProperties.setUserLanguage(userProvider.getUser().getLanguage());
        this.campaignDto = formData;
        isCreateForm = formData == null;
        addClassName("campaign-form");
        
        addRowStylingCSS();
        
        if (campaignDto != null) {
            add(configureTreeGrid(false, formData));
        } else {
            Div savecampaignTextAssoccamp = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));
            add(savecampaignTextAssoccamp);
        }
    }

    public HorizontalLayout configureTreeGrid(boolean isDeletePopulationData, CampaignDto formData) {
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

        ComponentRenderer<Span, CampaignTreeGridDto> populationGenerate4_23M = new ComponentRenderer<>(input -> {
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

        // Enhanced checkbox renderer with pending change tracking
        ComponentRenderer<Component, CampaignTreeGridDto> selectionCheckboxRenderer = new ComponentRenderer<>(dto -> {
            Checkbox checkbox = new Checkbox();
            checkbox.setId("chk_" + dto.getId());
            
            // Set initial state from DTO
            boolean isSelected = false;
            if (dto.getLevelAssessed().equals("cluster")) {
                isSelected = "true".equalsIgnoreCase(dto.getSavedData()) || dto.getSelected();
            } else {
                isSelected = hasAnySelectedChildCluster(dto);

//                isSelected = hasAnySelectedChild(dto);
            }
            checkbox.setValue(isSelected);
            
            // Store checkbox reference for programmatic updates
            checkboxMap.put(dto.getUuid(), checkbox);
            
            checkbox.addValueChangeListener(event -> {
                if (isInitializing) return;
                
                boolean newValue = event.getValue();
                CampaignTreeGridDto currentDto = dto;
                
                isInitializing = true;
                try {
                    // Track pending changes for clusters
                    Set<String> affectedClusters = new HashSet<>();
                    collectAllClustersUnderItem(currentDto, affectedClusters);
                    
                    if (newValue) {
                        // Add to pending selected
                        pendingSelectedClusters.addAll(affectedClusters);
                        pendingDeselectedClusters.removeAll(affectedClusters);
                        // Select this item and all its descendants
                        selectItemAndAllDescendants(currentDto);
                    } else {
                        // Add to pending deselected
                        pendingDeselectedClusters.addAll(affectedClusters);
                        pendingSelectedClusters.removeAll(affectedClusters);
                        // Deselect this item and all its descendants
                        deselectItemAndAllDescendants(currentDto);
                    }
                    
                    // Mark that there are pending changes
                    hasPendingChanges = true;
                    
                    // Update parent checkboxes to reflect new state
                    updateParentCheckboxes(currentDto);
                    
                    updateParentHierarchySelections();
                    
                    treeGrid.getDataProvider().refreshAll();

                    // Show visual indicator of pending changes
                    showPendingChangesNotification(affectedClusters.size(), newValue);
                    
                } catch (Exception ex) {
                    logger.error("Error updating selection", ex);
                    Notification.show("Error updating selection: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    // Revert checkbox state on error
                    checkbox.setValue(!newValue);
                } finally {
                    isInitializing = false;
                }
            });
            
            return checkbox;
        });

        treeGrid = new TreeGrid<>();
        treeGrid.removeAllColumns();
        treeGrid.setWidthFull();
        
        // Set page size for better performance
        treeGrid.setPageSize(50);
        
        // Disable default selection model - we'll use custom checkboxes only
        treeGrid.setSelectionMode(SelectionMode.NONE);
        
        treeGrid.setItems(generateTreeGridData(), item -> {
            if ("area".equals(item.getLevelAssessed())) {
                return item.getRegionData();
            } else if ("region".equals(item.getLevelAssessed())) {
                return item.getDistrictData();
            } else if ("district".equals(item.getLevelAssessed())) {
                return item.getClusterData();
            } else {
                return Collections.emptyList();
            }
        });

        treeGrid.setWidthFull();
        
        // Add columns
        treeGrid.addColumn(selectionCheckboxRenderer).setHeader("Select").setWidth("70px").setFlexGrow(0);
        treeGrid.addHierarchyColumn(CampaignTreeGridDto::getName).setHeader(I18nProperties.getCaption(Captions.Location)).setAutoWidth(true);
        treeGrid.addColumn(populationGenerate).setHeader("Target (0-59M)");
        treeGrid.addColumn(populationGenerate5_10).setHeader("Target (60-120M)");
        treeGrid.addColumn(populationGenerate4_23M).setHeader("Target (4_23M)");
        treeGrid.addColumn(CampaignTreeGridDto::getDistrictModality).setHeader("Modality");
        treeGrid.addColumn(CampaignTreeGridDto::getDistrictStatus).setHeader("Status");
        
        // Add delete column
        ComponentRenderer<Component, CampaignTreeGridDto> deleteCheckboxRenderer = new ComponentRenderer<>(dto -> {
            if (dto.getLevelAssessed().equals("cluster")) {
                Checkbox deleteCheckbox = new Checkbox();
                deleteCheckbox.addValueChangeListener(event -> {
                    if (event.getValue()) {
                        deletelist.add(dto);
                    } else {
                        deletelist.remove(dto);
                    }
                });
                return deleteCheckbox;
            } else {
                return new Span();
            }
        });
        treeGrid.addColumn(deleteCheckboxRenderer).setHeader("Delete?").setWidth("70px").setFlexGrow(0);

        // Load selected UUIDs from campaign DTO
        selectedAreaUuids = campaignDto.getAreas().stream().map(AreaReferenceDto::getUuid).collect(Collectors.toSet());
        selectedRegionUuids = campaignDto.getRegion().stream().map(RegionReferenceDto::getUuid).collect(Collectors.toSet());
        selectedDistrictUuids = campaignDto.getDistricts().stream().map(DistrictReferenceDto::getUuid).collect(Collectors.toSet());
        selectedClusterUuids = campaignDto.getCommunity().stream().map(CommunityReferenceDto::getUuid).collect(Collectors.toSet());

        // Initialize selections from DB state
        isInitializing = true;
        try {
            initializeSelectionsFromDB();
        } finally {
            isInitializing = false;
        }

        // Item click listener for editing clusters
        treeGrid.addItemClickListener(ee -> {
            if (campaignDto != null && ee.getItem().getLevelAssessed().equals("cluster")) {
                openEditDialog(ee.getItem());
            }
        });

        // Enhanced Refresh button with confirmation and pending changes commit
        Button refreshTreeGridBtn = new Button("Refresh Tree Grid", new Icon(VaadinIcon.REFRESH));
        refreshTreeGridBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refreshTreeGridBtn.addClickListener(e -> {
            refreshWithConfirmation();
        });

        // Add Save Changes button for explicit save
        Button saveChangesBtn = new Button("Save Changes", new Icon(VaadinIcon.CHECK));
//        saveChangesBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveChangesBtn.getElement().getStyle().set("color", "white");
        saveChangesBtn.getElement().getStyle().set("background", "#0D6938");
        saveChangesBtn.addClickListener(e -> {
            commitPendingChanges();
        });
        
        // Add button to show pending changes
        Button showPendingBtn = new Button("Show Pending", new Icon(VaadinIcon.INFO));
//        showPendingBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        showPendingBtn.getElement().getStyle().set("color", "white");
        showPendingBtn.getElement().getStyle().set("background", "#F08F3E");

        showPendingBtn.addClickListener(e -> {
        	
            showPendingChangesDialog();
        });

        HorizontalLayout buttonBar = new HorizontalLayout(refreshTreeGridBtn, saveChangesBtn, showPendingBtn);
        buttonBar.setSpacing(true);
        buttonBar.setJustifyContentMode(JustifyContentMode.END);

        Component popEditForm = configurePopulationPopEdit(formData);

        VerticalLayout gridWithButtonLayout = new VerticalLayout(treeGrid, buttonBar);
        gridWithButtonLayout.setPadding(false);
        gridWithButtonLayout.setSpacing(true);
        gridWithButtonLayout.setWidthFull();

        HorizontalLayout assocCampaignLayout = new HorizontalLayout();
        assocCampaignLayout.setWidthFull();
        assocCampaignLayout.setHeight("80vh");

        assocCampaignLayout.add(gridWithButtonLayout, popEditForm);
        assocCampaignLayout.setFlexGrow(4, gridWithButtonLayout);
        return assocCampaignLayout;
    }
    
    /**
     * Show notification for pending changes
     */
    private void showPendingChangesNotification(int clusterCount, boolean isSelected) {
        if (hasPendingChanges) {
            Notification notification = new Notification();
            notification.setDuration(2000);
            notification.setPosition(Notification.Position.BOTTOM_END);
            
            Span message = new Span(isSelected ? 
                "✓ " + clusterCount + " clusters pending selection" : 
                "✗ " + clusterCount + " clusters pending deselection");
            message.getStyle().set("color", "var(--lumo-primary-color)");
            
            Button saveBtn = new Button("Save Now", click -> {
                notification.close();
                commitPendingChanges();
            });
            saveBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            
            HorizontalLayout layout = new HorizontalLayout(message, saveBtn);
            layout.setAlignItems(Alignment.CENTER);
            layout.setSpacing(true);
            
            notification.add(layout);
            notification.open();
        }
    }
    
    /**
     * Show dialog with pending changes details
     */
    private void showPendingChangesDialog() {
        if (!hasPendingChanges) {
            Notification.show("No pending changes to save").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            return;
        }
        
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Pending Changes");
        dialog.setWidth("500px");
        
        VerticalLayout content = new VerticalLayout();
        
        Span title = new Span("The following changes are pending:");
        title.getStyle().set("font-weight", "bold");
        
        VerticalLayout changesLayout = new VerticalLayout();
        
        if (!pendingSelectedClusters.isEmpty()) {
            Span selectedLabel = new Span("✓ To be SELECTED: " + pendingSelectedClusters.size() + " clusters");
            selectedLabel.getStyle().set("color", "green");
            changesLayout.add(selectedLabel);
        }
        
        if (!pendingDeselectedClusters.isEmpty()) {
            Span deselectedLabel = new Span("✗ To be DESELECTED: " + pendingDeselectedClusters.size() + " clusters");
            deselectedLabel.getStyle().set("color", "red");
            changesLayout.add(deselectedLabel);
        }
        
        content.add(title, changesLayout);
        
        Button saveButton = new Button("Save All Changes", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> {
            commitPendingChanges();
            dialog.close();
        });
        
        Button cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create(), e -> dialog.close());
        
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.add(content);
        dialog.open();
    }

    /**
     * Commit all pending changes to database with confirmation
     */
    private void commitPendingChanges() {
        if (!hasPendingChanges) {
            Notification.show("No pending changes to save").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            return;
        }
        
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Save Changes");
        
        StringBuilder message = new StringBuilder("Are you sure you want to save the following changes?\n\n");
        if (!pendingSelectedClusters.isEmpty()) {
            message.append("✓ Select ").append(pendingSelectedClusters.size()).append(" clusters\n");
        }
        if (!pendingDeselectedClusters.isEmpty()) {
            message.append("✗ Deselect ").append(pendingDeselectedClusters.size()).append(" clusters\n");
        }
        
        confirmDialog.setText(message.toString());
        confirmDialog.setConfirmText("Save Changes");
        confirmDialog.setCancelText("Cancel");
        confirmDialog.setConfirmButtonTheme("primary");
        
        confirmDialog.addConfirmListener(event -> {
            try {
                // Commit selections
                if (!pendingSelectedClusters.isEmpty()) {
                    FacadeProvider.getPopulationDataFacade()
                        .updateClusterSelectionByClusterIds(
                            new ArrayList<>(pendingSelectedClusters), 
                            campaignDto.getUuid(), 
                            true
                        );
                }
                
                // Commit deselections
                if (!pendingDeselectedClusters.isEmpty()) {
                    FacadeProvider.getPopulationDataFacade()
                        .updateClusterSelectionByClusterIds(
                            new ArrayList<>(pendingDeselectedClusters), 
                            campaignDto.getUuid(), 
                            false
                        );
                }
                
                // Refresh campaign DTO
                campaignDto = FacadeProvider.getCampaignFacade().getByUuid(campaignDto.getUuid());
                
                // Update selected UUID sets
                selectedClusterUuids = campaignDto.getCommunity().stream()
                    .map(CommunityReferenceDto::getUuid)
                    .collect(Collectors.toSet());
                
                Notification.show("Changes saved successfully! " + 
                    (pendingSelectedClusters.size() + pendingDeselectedClusters.size()) + " updates applied")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                pendingSelectedClusters.clear();
                pendingDeselectedClusters.clear();
                hasPendingChanges = false;
                // Optionally refresh the grid to show saved state
//                refreshTreeGridSilently();
                updateParentHierarchySelections();
                treeGrid.getDataProvider().refreshAll();

                
            } catch (Exception ex) {
                logger.error("Error saving changes", ex);
                Notification.show("Error saving changes: " + ex.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        
        confirmDialog.open();
    }

    /**
     * Refresh with confirmation, committing pending changes first
     */
    private void refreshWithConfirmation() {
        if (hasPendingChanges) {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Unsaved Changes");
            confirmDialog.setText("You have unsaved changes. Do you want to save them before refreshing?");
            confirmDialog.setConfirmText("Save and Refresh");
            confirmDialog.setCancelText("Refresh Without Saving");
            confirmDialog.setRejectText("Cancel");
            
            confirmDialog.addConfirmListener(event -> {
                // Save and then refresh
                commitPendingChangesAndRefresh();
            });
            
            confirmDialog.addCancelListener(event -> {
                // Refresh without saving (discard changes)
                discardPendingChanges();
                refreshTreeGrid();
            });
            
            confirmDialog.open();
        } else {
            // No pending changes, just refresh
            refreshTreeGrid();
        }
    }

    /**
     * Commit pending changes and then refresh
     */
    private void commitPendingChangesAndRefresh() {
        try {
            // Commit selections
            if (!pendingSelectedClusters.isEmpty()) {
                FacadeProvider.getPopulationDataFacade()
                    .updateClusterSelectionByClusterIds(
                        new ArrayList<>(pendingSelectedClusters), 
                        campaignDto.getUuid(), 
                        true
                    );
            }
            
            // Commit deselections
            if (!pendingDeselectedClusters.isEmpty()) {
                FacadeProvider.getPopulationDataFacade()
                    .updateClusterSelectionByClusterIds(
                        new ArrayList<>(pendingDeselectedClusters), 
                        campaignDto.getUuid(), 
                        false
                    );
            }
            
            // Clear pending changes
            pendingSelectedClusters.clear();
            pendingDeselectedClusters.clear();
            hasPendingChanges = false;
            
            Notification.show("Changes saved, refreshing data...", 2000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            // Refresh the grid
            refreshTreeGrid();
            
        } catch (Exception ex) {
            logger.error("Error saving changes before refresh", ex);
            Notification.show("Error saving changes: " + ex.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Discard all pending changes
     */
    private void discardPendingChanges() {
        isInitializing = true;
        try {
            // Reset checkboxes to their original state from DB
            for (CampaignTreeGridDto root : treeGrid.getTreeData().getRootItems()) {
                resetCheckboxesFromDB(root);
            }
            
            // Clear pending changes
            pendingSelectedClusters.clear();
            pendingDeselectedClusters.clear();
            hasPendingChanges = false;
            
            Notification.show("Pending changes discarded", 2000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            
        } finally {
            isInitializing = false;
        }
    }
    
    /**
     * Reset checkboxes to reflect actual DB state
     */
    private void resetCheckboxesFromDB(CampaignTreeGridDto item) {
        if ("cluster".equals(item.getLevelAssessed())) {
            boolean isSelected = selectedClusterUuids.contains(item.getUuid());
            updateCheckboxState(item.getUuid(), isSelected);
            item.setSavedData(String.valueOf(isSelected));
            item.setSelected(isSelected);
        }
        
        for (CampaignTreeGridDto child : getChildrenForSelection(item)) {
            resetCheckboxesFromDB(child);
        }
    }

    /**
     * Silent refresh without confirmation (used after save)
     */
    private void refreshTreeGridSilently() {
        isInitializing = true;
        try {
            // Refresh campaign DTO from DB
            campaignDto = FacadeProvider.getCampaignFacade().getByUuid(campaignDto.getUuid());
            
            // Update selected UUID sets
            selectedAreaUuids = campaignDto.getAreas().stream().map(AreaReferenceDto::getUuid).collect(Collectors.toSet());
            selectedRegionUuids = campaignDto.getRegion().stream().map(RegionReferenceDto::getUuid).collect(Collectors.toSet());
            selectedDistrictUuids = campaignDto.getDistricts().stream().map(DistrictReferenceDto::getUuid).collect(Collectors.toSet());
            selectedClusterUuids = campaignDto.getCommunity().stream().map(CommunityReferenceDto::getUuid).collect(Collectors.toSet());
            
            // Clear checkbox map and rebuild
            checkboxMap.clear();
            
            // Reload tree data
            treeGrid.setItems(generateTreeGridData(), item -> {
                if ("area".equals(item.getLevelAssessed())) return item.getRegionData();
                else if ("region".equals(item.getLevelAssessed())) return item.getDistrictData();
                else if ("district".equals(item.getLevelAssessed())) return item.getClusterData();
                else return Collections.emptyList();
            });
            
            // Re-initialize selections
            initializeSelectionsFromDB();
            
            treeGrid.getDataProvider().refreshAll();
            
        } catch (Exception ex) {
            logger.error("Error refreshing tree grid silently", ex);
        } finally {
            isInitializing = false;
        }
    }
    
    /**
     * Check if any child of this item is selected
     */
    private boolean hasAnySelectedChild(CampaignTreeGridDto item) {
        List<CampaignTreeGridDto> children = getChildrenForSelection(item);
        if (children.isEmpty()) return false;
        
        for (CampaignTreeGridDto child : children) {
            if ("cluster".equals(child.getLevelAssessed())) {
                if (selectedClusterUuids.contains(child.getUuid())) return true;
            } else {
                if (hasAnySelectedChild(child)) return true;
            }
        }
        return false;
    }

    /**
     * Select item and all its descendants recursively
     */
    private void selectItemAndAllDescendants(CampaignTreeGridDto item) {
        // Select the item in tree grid
        // Note: Selection mode is NONE, so we just update checkbox states
        updateCheckboxState(item.getUuid(), true);
        
        // Update DTO's selection state
        if ("cluster".equals(item.getLevelAssessed())) {
            item.setSavedData("true");
            item.setSelected(true);
        }
        
        // Recursively select all children
        for (CampaignTreeGridDto child : getChildrenForSelection(item)) {
            selectItemAndAllDescendants(child);
        }
    }


    /**
     * Deselect item and all its descendants recursively
     */
    private void deselectItemAndAllDescendants(CampaignTreeGridDto item) {
        // Deselect the item in tree grid
        updateCheckboxState(item.getUuid(), false);
        
        // Update DTO's selection state
        if ("cluster".equals(item.getLevelAssessed())) {
            item.setSavedData("false");
            item.setSelected(false);
        }
        
        // Recursively deselect all children
        for (CampaignTreeGridDto child : getChildrenForSelection(item)) {
            deselectItemAndAllDescendants(child);
        }
    }

    /**
     * Update checkbox state programmatically
     */
    private void updateCheckboxState(String uuid, boolean selected) {
        Checkbox checkbox = checkboxMap.get(uuid);
        if (checkbox != null && checkbox.getValue() != selected) {
            checkbox.setValue(selected);
        }
    }
    
    
    /**
     * Update parent checkboxes based on children selection state
     */
    private void updateParentCheckboxes(CampaignTreeGridDto item) {
        // Find parent and update its checkbox
        CampaignTreeGridDto parent = findParentItem(item);
        if (parent != null) {
            boolean hasSelectedChild = hasAnySelectedChild(parent);
            updateCheckboxState(parent.getUuid(), hasSelectedChild);
            // Recursively update higher level parents
            updateParentCheckboxes(parent);
        }
    }

    /**
     * Find parent of an item in the tree
     */
    private CampaignTreeGridDto findParentItem(CampaignTreeGridDto item) {
        for (CampaignTreeGridDto area : treeGrid.getTreeData().getRootItems()) {
            CampaignTreeGridDto found = findParentRecursive(area, item);
            if (found != null) return found;
        }
        return null;
    }
    
    /**
     * Recursively search for parent
     */
    private CampaignTreeGridDto findParentRecursive(CampaignTreeGridDto current, CampaignTreeGridDto target) {
        for (CampaignTreeGridDto child : getChildrenForSelection(current)) {
            if (child.getUuid().equals(target.getUuid())) {
                return current;
            }
            CampaignTreeGridDto found = findParentRecursive(child, target);
            if (found != null) return found;
        }
        return null;
    }

    /**
     * Collect all cluster UUIDs under an item
     */
    private void collectAllClustersUnderItem(CampaignTreeGridDto item, Set<String> collectedClusters) {
        if ("cluster".equals(item.getLevelAssessed())) {
            if (item.getUuid() != null) {
                collectedClusters.add(item.getUuid());
            }
        } else {
            for (CampaignTreeGridDto child : getChildrenForSelection(item)) {
                collectAllClustersUnderItem(child, collectedClusters);
            }
        }
    }
    
    private void initializeSelectionsFromDB() {
        for (CampaignTreeGridDto areax : treeGrid.getTreeData().getRootItems()) {
            initializeSelectionsRecursive(areax);
        }
    }
    
    private void initializeSelectionsRecursive(CampaignTreeGridDto item) {
        if ("cluster".equals(item.getLevelAssessed())) {
            boolean isSelected = selectedClusterUuids.contains(item.getUuid()) || pendingSelectedClusters.contains(item.getUuid());
            if (isSelected) {
                updateCheckboxState(item.getUuid(), true);
                item.setSavedData("true");
                item.setSelected(true);
            }
        } else {
            // For parent items, check if any child is selected
            boolean hasSelectedChild = hasAnySelectedChildCluster(item);
            if (hasSelectedChild) {
                updateCheckboxState(item.getUuid(), true);
                item.setSavedData("true");
                item.setSelected(true);
            } else {
                // Check if this parent itself is selected
                boolean isSelected = false;
                if ("district".equals(item.getLevelAssessed())) {
                    isSelected = selectedDistrictUuids.contains(item.getUuid());
                } else if ("region".equals(item.getLevelAssessed())) {
                    isSelected = selectedRegionUuids.contains(item.getUuid());
                } else if ("area".equals(item.getLevelAssessed())) {
                    isSelected = selectedAreaUuids.contains(item.getUuid());
                }
                updateCheckboxState(item.getUuid(), isSelected);
                item.setSavedData(String.valueOf(isSelected));
                item.setSelected(isSelected);
            }
            
            // Recursively initialize children
            for (CampaignTreeGridDto child : getChildrenForSelection(item)) {
                initializeSelectionsRecursive(child);
            }
        }
    }
   
//   	private void initializeSelectionsRecursive(CampaignTreeGridDto item) {
//       if ("cluster".equals(item.getLevelAssessed())) {
//           boolean isSelected = selectedClusterUuids.contains(item.getUuid());
//           if (isSelected) {
//               updateCheckboxState(item.getUuid(), true);
//               item.setSavedData("true");
//               item.setSelected(true);
//           }
//       } else {
//           // For parent items, check if any child is selected
//           boolean hasSelectedChild = false;
//           for (CampaignTreeGridDto child : getChildrenForSelection(item)) {
//               initializeSelectionsRecursive(child);
//               if ("cluster".equals(child.getLevelAssessed())) {
//                   if (selectedClusterUuids.contains(child.getUuid())) {
//                       hasSelectedChild = true;
//                   }
//               } else {
//                   // Check if this child (district/region) has any selected children
//                   if (hasAnySelectedChild(child)) {
//                       hasSelectedChild = true;
//                   }
//               }
//           }
//           
//           // Update parent checkbox if it has selected children
//           if (hasSelectedChild) {
//               updateCheckboxState(item.getUuid(), true);
//           }
//       }
//   }
//   
   	private void refreshTreeGrid() {
        // Store expanded nodes
//        Set<String> expandedUuids = new HashSet<>();
//        Queue<CampaignTreeGridDto> queue = new LinkedList<>(treeGrid.getTreeData().getRootItems());
//        
//        while (!queue.isEmpty()) {
//            CampaignTreeGridDto current = queue.poll();
//            if (treeGrid.isExpanded(current)) {
//                expandedUuids.add(current.getUuid());
//            }
//            queue.addAll(treeGrid.getTreeData().getChildren(current));
//        }
//        
        isInitializing = true;
        try {
            // Refresh campaign DTO from DB
            campaignDto = FacadeProvider.getCampaignFacade().getByUuid(campaignDto.getUuid());
            
            // Update selected UUID sets
            selectedAreaUuids = campaignDto.getAreas().stream().map(AreaReferenceDto::getUuid).collect(Collectors.toSet());
            selectedRegionUuids = campaignDto.getRegion().stream().map(RegionReferenceDto::getUuid).collect(Collectors.toSet());
            selectedDistrictUuids = campaignDto.getDistricts().stream().map(DistrictReferenceDto::getUuid).collect(Collectors.toSet());
            selectedClusterUuids = campaignDto.getCommunity().stream().map(CommunityReferenceDto::getUuid).collect(Collectors.toSet());
            
            // Clear checkbox map
            checkboxMap.clear();
            
            // Reload tree data
            treeGrid.setItems(generateTreeGridData(), item -> {
                if ("area".equals(item.getLevelAssessed())) return item.getRegionData();
                else if ("region".equals(item.getLevelAssessed())) return item.getDistrictData();
                else if ("district".equals(item.getLevelAssessed())) return item.getClusterData();
                else return Collections.emptyList();
            });
            
            // Restore expanded state
//            restoreExpandedState(expandedUuids, treeGrid.getTreeData().getRootItems());
            
            // Re-initialize selections
            initializeSelectionsFromDB();
            
            // Update parent hierarchy selections
            updateParentHierarchySelections();
            
            // Apply row styling
            applyRowStyling();
            
            treeGrid.getDataProvider().refreshAll();
            Notification.show("Data refreshed from database").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            logger.error("Error refreshing tree grid", ex);
            Notification.show("Error refreshing data: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } finally {
            isInitializing = false;
        }
    }    

   	private void restoreExpandedState(Set<String> expandedUuids, List<CampaignTreeGridDto> items) {
        for (CampaignTreeGridDto item : items) {
            if (expandedUuids.contains(item.getUuid())) {
                treeGrid.expand(item);
            }
            restoreExpandedState(expandedUuids, getChildrenForSelection(item));
        }
    }
   
    private void collectClustersForItem(CampaignTreeGridDto item, Set<String> collectedClusters) {
        switch (item.getLevelAssessed()) {
            case "cluster":
                if (item.getUuid() != null) {
                    collectedClusters.add(item.getUuid());
                }
                break;
            case "district":
                List<CommunityReferenceDto> clusters = FacadeProvider.getCommunityFacade()
                    .getAllActiveByDistrict(item.getUuid());
                for (CommunityReferenceDto c : clusters) {
                    collectedClusters.add(c.getUuid());
                }
                break;
            case "region":
                List<DistrictReferenceDto> districts = FacadeProvider.getDistrictFacade()
                    .getAllActiveByRegion(item.getUuid());
                for (DistrictReferenceDto d : districts) {
                    List<CommunityReferenceDto> dClusters = FacadeProvider.getCommunityFacade()
                        .getAllActiveByDistrict(d.getUuid());
                    for (CommunityReferenceDto c : dClusters) {
                        collectedClusters.add(c.getUuid());
                    }
                }
                break;
            case "area":
                List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade()
                    .getAllActiveByArea(item.getUuid());
                for (RegionReferenceDto r : regions) {
                    List<DistrictReferenceDto> rDistricts = FacadeProvider.getDistrictFacade()
                        .getAllActiveByRegion(r.getUuid());
                    for (DistrictReferenceDto d : rDistricts) {
                        List<CommunityReferenceDto> dClusters = FacadeProvider.getCommunityFacade()
                            .getAllActiveByDistrict(d.getUuid());
                        for (CommunityReferenceDto c : dClusters) {
                            collectedClusters.add(c.getUuid());
                        }
                    }
                }
                break;
        }
    }

    private void selectItemAndDescendantsWithClusters(CampaignTreeGridDto item, boolean select, List<String> collectedClusters) {
        if (select) {
            treeGrid.select(item);
        } else {
            treeGrid.deselect(item);
        }
        collectClusterUuidsFromDb(item, collectedClusters);
        for (CampaignTreeGridDto child : getChildrenForSelection(item)) {
            updateVisualSelectionOnly(child, select);
        }
    }

    private void updateVisualSelectionOnly(CampaignTreeGridDto item, boolean select) {
        if (select) {
            treeGrid.select(item);
        } else {
            treeGrid.deselect(item);
        }
        for (CampaignTreeGridDto child : getChildrenForSelection(item)) {
            updateVisualSelectionOnly(child, select);
        }
    }

    private void collectClusterUuidsFromDb(CampaignTreeGridDto item, List<String> collectedClusters) {
        switch (item.getLevelAssessed()) {
            case "cluster":
                if (item.getUuid() != null) {
                    collectedClusters.add(item.getUuid());
                }
                break;
            case "district":
                List<CommunityReferenceDto> clusters = FacadeProvider.getCommunityFacade()
                    .getAllActiveByDistrict(item.getUuid());
                for (CommunityReferenceDto c : clusters) {
                    collectedClusters.add(c.getUuid());
                }
                break;
            case "region":
                List<DistrictReferenceDto> districts = FacadeProvider.getDistrictFacade()
                    .getAllActiveByRegion(item.getUuid());
                for (DistrictReferenceDto d : districts) {
                    List<CommunityReferenceDto> dClusters = FacadeProvider.getCommunityFacade()
                        .getAllActiveByDistrict(d.getUuid());
                    for (CommunityReferenceDto c : dClusters) {
                        collectedClusters.add(c.getUuid());
                    }
                }
                break;
            case "area":
                List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade()
                    .getAllActiveByArea(item.getUuid());
                for (RegionReferenceDto r : regions) {
                    List<DistrictReferenceDto> rDistricts = FacadeProvider.getDistrictFacade()
                        .getAllActiveByRegion(r.getUuid());
                    for (DistrictReferenceDto d : rDistricts) {
                        List<CommunityReferenceDto> dClusters = FacadeProvider.getCommunityFacade()
                            .getAllActiveByDistrict(d.getUuid());
                        for (CommunityReferenceDto c : dClusters) {
                            collectedClusters.add(c.getUuid());
                        }
                    }
                }
                break;
        }
    }

    private List<CampaignTreeGridDto> getChildrenForSelection(CampaignTreeGridDto item) {
        if ("area".equals(item.getLevelAssessed())) {
            return item.getRegionData();
        } else if ("region".equals(item.getLevelAssessed())) {
            return item.getDistrictData();
        } else if ("district".equals(item.getLevelAssessed())) {
            return item.getClusterData();
        }
        return Collections.emptyList();
    }

    private void openEditDialog(CampaignTreeGridDto ee) {
        // Implementation of edit dialog (kept from original code)
        // This method should contain the existing dialog logic
    	
    		System.out.println(ee.getUuid() + "uuid");
    		System.out.println(ee.getName() + "nameeeee");

    					isSingleSelectClickItemLock = true;
    					if (campaignDto != null && ee.getLevelAssessed().equals("cluster")) {

    						if (ee.getPopulationData() != null) {

    							System.out.println("Age Group from item click " + ee.getAgeGroup());
    							Integer popDataAge0_4 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictPopulationByUuidAndAgeGroup(ee.getUuid(), campaignDto.getUuid(),
    											"AGE_0_4");

    							Integer popDataAge5_10 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictPopulationByUuidAndAgeGroup(ee.getUuid(), campaignDto.getUuid(),
    											"AGE_5_10");
//    							};
    							
    							Integer popDataAge4_23M = FacadeProvider.getPopulationDataFacade()
    									.getDistrictPopulationByUuidAndAgeGroup(ee.getUuid(), campaignDto.getUuid(),
    											"AGE_4_23M");
//    							};

    							String districtModality_0_4 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictModalityByUuidAndCampaignAndAgeGroup(ee.getUuid(),
    											campaignDto.getUuid(), "AGE_0_4");
    							String districtStatus_0_4 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictStatusByCampaign(ee.getUuid(), campaignDto.getUuid(), "AGE_0_4");
    							
    				
    							createDialogBasics(ee.getUuid(), ee.getPopulationData(), ee.getName(),
    									"AGE_0_4", campaignDto, ee, popDataAge0_4, popDataAge5_10, popDataAge4_23M, districtModality_0_4,
    									districtStatus_0_4);
    						}

    						else if (ee.getPopulationData5_10() != null) {
    							System.out.println("Age Group from item click " + ee.getAgeGroup());

    							Integer popDataAge5_10 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictPopulationByUuidAndAgeGroup(ee.getUuid(), campaignDto.getUuid(),
    											"AGE_5_10");

    							Integer popDataAge0_4 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictPopulationByUuidAndAgeGroup(ee.getUuid(), campaignDto.getUuid(),
    											"AGE_0_4");
    							
    							Integer popDataAge4_23M = FacadeProvider.getPopulationDataFacade()
    									.getDistrictPopulationByUuidAndAgeGroup(ee.getUuid(), campaignDto.getUuid(),
    											"AGE_4_23M");
//    							};

    							String districtModality_5_10 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictModalityByUuidAndCampaignAndAgeGroup(ee.getUuid(),
    											campaignDto.getUuid(), "AGE_5_10");

    							String districtStatus_5_10 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictStatusByCampaign(ee.getUuid(), campaignDto.getUuid(), "AGE_5_10");

//    							if (popDataAge0_4 == null) {
//    								popDataAge0_4 = 0;
//    							}

    							createDialogBasics(ee.getUuid(), ee.getPopulationData(), ee.getName(),
    									"AGE_5_10", campaignDto, ee, popDataAge0_4, popDataAge5_10, popDataAge4_23M, districtModality_5_10,
    									districtStatus_5_10);

//    							createDialogBasics(ee.getItem().getUuid(), ee.getItem().getPopulationData(), ee.getItem().getName(),
//    									ee.getItem().getAgeGroup(), campaignDto, ee.getItem(), popDataAge5_10,
//    									districtModality_5_10, districtStatus_5_10);
    						} 				else if (ee.getPopulationData4_23M() != null) {
    							System.out.println("Age Group from item click " + ee.getAgeGroup());

    							Integer popDataAge5_10 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictPopulationByUuidAndAgeGroup(ee.getUuid(), campaignDto.getUuid(),
    											"AGE_5_10");

    							Integer popDataAge0_4 = FacadeProvider.getPopulationDataFacade()
    									.getDistrictPopulationByUuidAndAgeGroup(ee.getUuid(), campaignDto.getUuid(),
    											"AGE_0_4");
    							
    							Integer popDataAge4_23M = FacadeProvider.getPopulationDataFacade()
    									.getDistrictPopulationByUuidAndAgeGroup(ee.getUuid(), campaignDto.getUuid(),
    											"AGE_4_23M");
//    							};

    							String districtModality_4_23M = FacadeProvider.getPopulationDataFacade()
    									.getDistrictModalityByUuidAndCampaignAndAgeGroup(ee.getUuid(),
    											campaignDto.getUuid(), "AGE_4_23M");

    							String districtStatus_4_23M = FacadeProvider.getPopulationDataFacade()
    									.getDistrictStatusByCampaign(ee.getUuid(), campaignDto.getUuid(), "AGE_4_23M");

    		 
    							createDialogBasics(ee.getUuid(), ee.getPopulationData(), ee.getName(),
    									"AGE_4_23M", campaignDto, ee, popDataAge0_4, popDataAge5_10, popDataAge4_23M, districtModality_4_23M,
    									districtStatus_4_23M);

    		 
    						} else {

    						}

    					}
    				
    }

    private Component configurePopulationPopEdit(CampaignDto campaignDto_) {
        VerticalLayout formx = populationEditorForm(campaignDto_);
        formx.getStyle().remove("width");
        HorizontalLayout content = new HorizontalLayout(treeGrid, formx);
        content.setFlexGrow(4, treeGrid);
        content.setFlexGrow(0, formx);
        content.addClassNames("content");
        return content;
    }

    // FIXED: Optimized generateTreeGridData with selection state from DB
    private List<CampaignTreeGridDto> generateTreeGridData() {
        List<CampaignTreeGridDto> gridData = new ArrayList<>();
        
        // Cache selected clusters from DB for O(1) lookups
        Set<String> selectedClusterUuids = campaignDto.getCommunity().stream()
            .map(CommunityReferenceDto::getUuid)
            .collect(Collectors.toSet());
        
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
                CampaignTreeGridDto regionData = new CampaignTreeGridDto(
                    regions_x.getName(), 
                    regions_x.getRegionId(),
                    regions_x.getAreaUuid_(), 
                    regions_x.getUuid_(), 
                    "region"
                );
                
                List<DistrictDto> district_ = FacadeProvider.getDistrictFacade()
                    .getAllActiveAsReferenceAndPopulation(regions_x.getRegionId(), campaignDto);
                
                for (DistrictDto district_x : district_) {
                    CampaignTreeGridDto districtData = new CampaignTreeGridDto(
                        district_x.getName(),
                        district_x.getRegionId(),
                        district_x.getRegionUuid_(),
                        district_x.getUuid_(),
                        "district",
                        district_x.isSelectedForPopulationData()
                    );
                    
                    List<CommunityDto> clusters_ = FacadeProvider.getCommunityFacade()
                        .getAllActiveClustersAsReferenceAndPopulation(
                            regions_x.getRegionId(),
                            district_x.getUuid_(),
                            campaignDto
                        );
                    try {
                    	
                    }finally {
                    	
                    }
                    if (clusters_.size() > 0) {
                        for (CommunityDto clusterdto : clusters_) {
                            if (clusterdto.getName() != null) {
                                Long totalPopulation =
                                    (clusterdto.getPopulationData() != null ? clusterdto.getPopulationData() : 0L)
                                    + (clusterdto.getPopulationData5_10() != null ? clusterdto.getPopulationData5_10() : 0L);
                                
                                // CRITICAL: Set the selected flag from DB
                                boolean isSelected = selectedClusterUuids.contains(clusterdto.getClusterUuid());
                                
                                CampaignTreeGridDto clusterData = new CampaignTreeGridDtoImpl(
                                    clusterdto.getName(),
                                    clusterdto.getPopulationData(),
                                    clusterdto.getPopulationData5_10(),
                                    clusterdto.getPopulationData4_23M(),
                                    clusterdto.getClusterId(),
                                    clusterdto.getDistrictUuid(),
                                    clusterdto.getClusterUuid(),
                                    "cluster",
                                    clusterdto.isSelectedForPopulationData(), // Set selected flag from DB
                                    clusterdto.getDistrictModality(),
                                    clusterdto.getDistrictStatus(),
                                    clusterdto.provideFloatStatus(),
                                    totalPopulation
                                );
                                
                                districtData.addClusterData(clusterData);
                            }
                        }
                    } else {
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
                                ((district_x.getPopulationData() != null ? district_x.getPopulationData() : 0L) 
                                    + (district_x.getPopulationData5_10() != null ? district_x.getPopulationData5_10() : 0L))
                            );
                        }
                    }
                    
                    regionData.addDistrictData(districtData);
                }
                
                areaData.addRegionData(regionData);
            }
            
            gridData.add(areaData);
        }
        
        return gridData;
    }

	private VerticalLayout populationEditorForm(CampaignDto campaignDto_) {
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
		cancelButton.getElement().getStyle().set("background", "red");
		
		regionFilter = new ComboBox<AreaReferenceDto>();
		regionFilter.setLabel(I18nProperties.getCaption(Captions.area));
		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		regionFilter.isRequired();
		regionFilter.setRequiredIndicatorVisible(true);

		provinceFilter = new ComboBox<>(I18nProperties.getCaption(Captions.region));
		provinceFilter.setPlaceholder(I18nProperties.getCaption(Captions.regionAllRegions));
		provinceFilter.setClearButtonVisible(true);
		provinceFilter.getStyle().set("width", "145px !important");
		provinceFilter.isRequired();
		provinceFilter.setRequiredIndicatorVisible(true);
		
		districtFilter = new ComboBox<>(I18nProperties.getCaption(Captions.district));
		districtFilter.setPlaceholder(I18nProperties.getCaption(Captions.districtAllDistricts));
		districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
		districtFilter.getStyle().set("width", "145px !important");
		districtFilter.isRequired();
		districtFilter.setRequiredIndicatorVisible(true);
		
		clusterFilter = new ComboBox<>(I18nProperties.getCaption(Captions.community));
		clusterFilter.setPlaceholder(I18nProperties.getCaption(Captions.districtAllDistricts));
		clusterFilter.setItems(new ArrayList<>());
//		FacadeProvider.getCommunityFacade().getAllActiveByDistrict()
		clusterFilter.getStyle().set("width", "145px !important");
		clusterFilter.setVisible(false);
		clusterFilter.isRequired();
		clusterFilter.setRequiredIndicatorVisible(true);
		
		
		popData0_4 = new IntegerField("Target 0-59M");
//				I18nProperties.getCaption(Captions.District_population) + " Age 0_4");

		popDataAge5_10 = new IntegerField("Target 60-120M");
//				I18nProperties.getCaption(Captions.District_population) + " Age 5_10");
		popDataAge4_23M = new IntegerField("Target 4_23M");
		popDataAge4_23M.setMin(0);
		popData0_4.setMin(0);
		popDataAge5_10.setMin(0);
		
		
	
		popData0_4.setRequiredIndicatorVisible(true);
		popDataAge5_10.setRequiredIndicatorVisible(true);
		popDataAge4_23M.setRequiredIndicatorVisible(true);
		
		popDataAge4_23M.setErrorMessage("Negative Values not Allowed");
		popData0_4.setErrorMessage("Negative Values not Allowed");
		popDataAge5_10.setErrorMessage("Negative Values not Allowed");

		districtModality = new ComboBox<String>("Modality");
		districtModality.setItems("H2H", "M2M", "S2S", "HF2HF", "Mixed");

		districtStatus = new ComboBox<String>("Status");
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

		buttonAfterLay = new HorizontalLayout(saveButton, cancelButton);
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
			validatePopulationEditForm();
	
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
  
				});

				confirmationDialog.addConfirmListener(event -> {
					try {
						List<Long> clusterIDs = new ArrayList<>();
						for (CampaignTreeGridDto treeData : deletelist) {
							clusterIDs.add(treeData.getId());

						}

						FacadeProvider.getPopulationDataFacade().deletePopulationDataByClusters(clusterIDs,
								campaignDto_ != null ? campaignDto_.getUuid() : "");

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
	
	private void validatePopulationEditForm() {
		if (regionFilter.isEmpty()) {
			regionFilter.isInvalid();
			regionFilter.setInvalid(true);
			regionFilter.setErrorMessage("Please select a Region.");
			Notification.show("Please select an Region.").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (provinceFilter.isEmpty()) {
			provinceFilter.isInvalid();
			provinceFilter.setInvalid(true);
			provinceFilter.setErrorMessage("Please select a Province.");
			Notification.show("Please select a Province.").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (districtFilter.isEmpty()) {
			districtFilter.isInvalid();
			districtFilter.setInvalid(true);
			districtFilter.setErrorMessage("Please select a District.");

			Notification.show("Please select a District.").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (clusterFilter.isEmpty()) {
			clusterFilter.isInvalid();
			clusterFilter.setInvalid(true);
			clusterFilter.setErrorMessage("Please select a Cluster.");
			Notification.show("Please select a Cluster.").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (districtModality.isEmpty()) {
			districtModality.isInvalid();
			districtModality.setInvalid(true);
			districtModality.setErrorMessage("Please select a Modality.");
			Notification.show("Please select a Modality.").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (districtStatus.isEmpty()) {
			districtStatus.isInvalid();
			districtStatus.setInvalid(true);
			districtStatus.setErrorMessage("Please select a Status.");
			Notification.show("Please select a Status.").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (popData0_4.isEmpty() || popDataAge5_10.isEmpty() || popDataAge4_23M.isEmpty()) {
			popData0_4.isInvalid();
			popData0_4.setInvalid(true);
			popData0_4.setErrorMessage("Please provide numeric values for Population Target");
			
			popDataAge5_10.isInvalid();
			popDataAge5_10.setInvalid(true);
			popDataAge5_10.setErrorMessage("Please provide numeric values for Population Target");
			
			popDataAge4_23M.isInvalid();
			popDataAge4_23M.setInvalid(true);
			popDataAge4_23M.setErrorMessage("Please provide numeric values for Population Target");
 
			Notification.show("Please provide numeric values for all Target population fields.").addThemeVariants(NotificationVariant.LUMO_ERROR);
			
			return;
		}
		if (popData0_4.isInvalid() || popDataAge5_10.isInvalid() || popDataAge4_23M.isInvalid() || popData0_4.getValue() < 0 || popDataAge5_10.getValue() < 0 || popDataAge4_23M.getValue() < 0) {
			popData0_4.isInvalid();
			popData0_4.setInvalid(true);
			popData0_4.setErrorMessage("Negative Values are not allowed");
			
			popDataAge5_10.isInvalid();
			popDataAge5_10.setInvalid(true);
			popDataAge5_10.setErrorMessage("Negative Values are not allowed");
			
			popDataAge4_23M.isInvalid();
			popDataAge4_23M.setInvalid(true);
			popDataAge4_23M.setErrorMessage("Negative Values are not allowed");
			
			Notification.show("Negative Values are not allowed for target populations.").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		if (clusterFilter.getValue() != null) {
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
				newPopulationData_423M.setSelected(true);

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
				newPopulationData_0_4.setSelected(true);


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
				newPopulationData_10.setSelected(true);


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
	}

    private void createDialogBasics(String Uuid, Long selectedPopData, String name_, String ageGroup,
            CampaignDto campaignDto_, CampaignTreeGridDto campaignTreeGridDto, Integer populationByAgeGroup,
            Integer populationByAgeGroup5_10, Integer populationByAgeGroup4_23M, 
            String districtModalityByAgeGroup, String districtStatusByAgeGroup) {
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

    /**
     * Check if any child cluster is selected for a given item
     */
    private boolean hasAnySelectedChildCluster(CampaignTreeGridDto item) {
        List<CampaignTreeGridDto> children = getChildrenForSelection(item);
        if (children.isEmpty()) return false;
        
        for (CampaignTreeGridDto child : children) {
            if ("cluster".equals(child.getLevelAssessed())) {
                // Check if this cluster is in selectedClusterUuids or has pending selection
                if (selectedClusterUuids.contains(child.getUuid()) || pendingSelectedClusters.contains(child.getUuid())) {
                    return true;
                }
            } else {
                // Recursively check deeper levels
                if (hasAnySelectedChildCluster(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Update all parent checkboxes recursively based on children selection state
     */
    private void updateAllParentCheckboxes(CampaignTreeGridDto item) {
        CampaignTreeGridDto parent = findParentItem(item);
        if (parent != null) {
            boolean hasSelectedChild = hasAnySelectedChildCluster(parent);
            updateCheckboxState(parent.getUuid(), hasSelectedChild);
            // Also update the parent's visual state
            if (hasSelectedChild) {
                parent.setSavedData("true");
                parent.setSelected(true);
            } else {
                parent.setSavedData("false");
                parent.setSelected(false);
            }
            // Recursively update higher level parents
            updateAllParentCheckboxes(parent);
        }
    }

    /**
     * Update district and region selection states based on cluster selections
     */
    private void updateParentHierarchySelections() {
        // Get all districts from the tree and update their selection state
        for (CampaignTreeGridDto area : treeGrid.getTreeData().getRootItems()) {
            for (CampaignTreeGridDto region : treeGrid.getTreeData().getChildren(area)) {
                boolean regionHasSelectedChild = false;
                for (CampaignTreeGridDto district : treeGrid.getTreeData().getChildren(region)) {
                    boolean districtHasSelectedChild = hasAnySelectedChildCluster(district);
                    if (districtHasSelectedChild) {
                        regionHasSelectedChild = true;
                        updateCheckboxState(district.getUuid(), true);
                        district.setSavedData("true");
                        district.setSelected(true);
                    } else {
                        // Check if district itself is in selected districts
                        boolean isDistrictSelected = selectedDistrictUuids.contains(district.getUuid());
                        updateCheckboxState(district.getUuid(), isDistrictSelected);
                        district.setSavedData(String.valueOf(isDistrictSelected));
                        district.setSelected(isDistrictSelected);
                    }
                }
                
                // Update region checkbox
                if (regionHasSelectedChild) {
                    updateCheckboxState(region.getUuid(), true);
                    region.setSavedData("true");
                    region.setSelected(true);
                } else {
                    boolean isRegionSelected = selectedRegionUuids.contains(region.getUuid());
                    updateCheckboxState(region.getUuid(), isRegionSelected);
                    region.setSavedData(String.valueOf(isRegionSelected));
                    region.setSelected(isRegionSelected);
                }
                
                // Update area checkbox
                boolean areaHasSelectedChild = false;
                for (CampaignTreeGridDto regionCheck : treeGrid.getTreeData().getChildren(area)) {
                    if ("true".equalsIgnoreCase(regionCheck.getSavedData()) || regionCheck.getSelected()) {
                        areaHasSelectedChild = true;
                        break;
                    }
                }
                
                if (areaHasSelectedChild) {
                    updateCheckboxState(area.getUuid(), true);
                    area.setSavedData("true");
                    area.setSelected(true);
                } else {
                    boolean isAreaSelected = selectedAreaUuids.contains(area.getUuid());
                    updateCheckboxState(area.getUuid(), isAreaSelected);
                    area.setSavedData(String.valueOf(isAreaSelected));
                    area.setSelected(isAreaSelected);
                }
            }
        }
    }
    
// Apply row styling using classNameGenerator
private void applyRowStyling() {
    treeGrid.setClassNameGenerator(item -> {
        if ("cluster".equals(item.getLevelAssessed())) {
            boolean isSelected = "true".equalsIgnoreCase(item.getSavedData()) || item.getSelected();
            if (isSelected) {
                return "selected-cluster-row";
            }
        } else if ("district".equals(item.getLevelAssessed())) {
            boolean hasSelectedChild = hasAnySelectedChildCluster(item);
            if (hasSelectedChild) {
                return "parent-with-selection-row";
            }
        } else if ("region".equals(item.getLevelAssessed())) {
            boolean hasSelectedChild = hasAnySelectedChildCluster(item);
            if (hasSelectedChild) {
                return "parent-with-selection-row";
            }
        } else if ("area".equals(item.getLevelAssessed())) {
            boolean hasSelectedChild = hasAnySelectedChildCluster(item);
            if (hasSelectedChild) {
                return "parent-with-selection-row";
            }
        }
        return null;
    });
}
    // Add this CSS to your shared styles or component
private void addRowStylingCSS() {
    String css = 
        "<style>" +
        "  .selected-cluster-row { background-color: #f08f3e80 !important; }" +
        "  .selected-cluster-row td { background-color: #f08f3e80 !important; color: white !important; }" +
        "  .parent-with-selection-row { background-color: #f08f3e80 !important; opacity: 0.85; }" +
        "  .parent-with-selection-row td { background-color: #f08f3e80 !important; color: white !important; opacity: 0.85; }" +
        "  .selected-cluster-row .v-checkbox, .parent-with-selection-row .v-checkbox { --lumo-primary-color: white !important; }" +
        "</style>";
    
    // Add to UI or use getElement().executeJs
    UI.getCurrent().getPage().executeJs(
        "const style = document.createElement('style'); style.textContent = $0; document.head.appendChild(style);",
        css
    );
}

}