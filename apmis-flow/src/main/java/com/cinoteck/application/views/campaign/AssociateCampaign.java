package com.cinoteck.application.views.campaign;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.slf4j.LoggerFactory;

import com.cinoteck.application.UserProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
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
	FormLayout formx;
	TextField creatingUuid = new TextField(I18nProperties.getCaption(Captions.uuid));

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public AssociateCampaign(CampaignDto formData) {
        super();
        I18nProperties.setUserLanguage(userProvider.getUser().getLanguage());
        this.campaignDto = formData;
        isCreateForm = formData == null;
        addClassName("campaign-form");
        if (campaignDto != null) {
            add(configureTreeGrid(false, formData));
        } else {
            Div savecampaignTextAssoccamp = new Div(new Text(I18nProperties.getString(Strings.infoSaveCampaignFirst)));
            add(savecampaignTextAssoccamp);
        }
    }

	public HorizontalLayout configureTreeGrid(boolean isDeletePopulationData,CampaignDto formData) {

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
 
		treeGrid.setWidthFull();

		treeGrid.addHierarchyColumn(CampaignTreeGridDto::getName)
				.setHeader(I18nProperties.getCaption(Captions.Location));
 
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

		assocCampaignLayout.add(treeGrid, configurePopulationPopEdit(formData));
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

	private Component configurePopulationPopEdit(CampaignDto campaignDto_) {
		VerticalLayout formx = populationEditorForm(campaignDto_);
		formx.getStyle().remove("width");
		HorizontalLayout content = new HorizontalLayout(treeGrid, formx);
		content.setFlexGrow(4, treeGrid);
		content.setFlexGrow(0, formx);
		content.addClassNames("content");
//		content.setSizeFull();
		return content;
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
					newPopulationData_423M.setSelected(false);

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
					newPopulationData_0_4.setSelected(false);


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
					newPopulationData_10.setSelected(false);


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

		VerticalLayout dialogLayout = createDialogLayoutByAge(name_, ageGroup, selectedPopData, saveButton, deleteButton,  Uuid,
				campaignDto_, dialog, campaignTreeGridDto, populationByAgeGroup, populationByAgeGroup5_10, populationByAgeGroup4_23M,
				districtModalityByAgeGroup, districtStatusByAgeGroup);

		dialog.add(dialogLayout);

		add(dialog);

		dialog.open();

		isSingleSelectClickItemLock = false;
	}

	private static VerticalLayout createDialogLayoutByAge(String name_, String ageGroup, Long selectedPopData,
			Button saveButton, Button deleteButton, String Uuid, CampaignDto campaignDto_, Dialog dialog,
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
		
		deleteButton.addClickListener(e->{
			
			
			ConfirmDialog confirmationDialog = new ConfirmDialog();

			confirmationDialog.setCancelable(true);
			confirmationDialog.setRejectable(false);
			confirmationDialog.addCancelListener(confirmationDialogx -> confirmationDialog.close());
			confirmationDialog.setConfirmText(I18nProperties.getCaption(Captions.actionYes));
			confirmationDialog.setCancelText(I18nProperties.getCaption("Delete Population Data "));

			confirmationDialog.addConfirmListener(confirmationDialogx -> {
				try {
					
					List<Long> clusterIDs = new ArrayList<>();
						clusterIDs.add(campaignTreeGridDto.getId());
		
						FacadeProvider.getPopulationDataFacade().deletePopulationDataByClusters(clusterIDs, campaignDto_.getUuid());
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
					"Are you sure you want to delete the population data for the Cluster " + name_ + "  ?");
			confirmationDialog.setHeader("Update Population Data");
			confirmationDialog.open();
		});

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

}
