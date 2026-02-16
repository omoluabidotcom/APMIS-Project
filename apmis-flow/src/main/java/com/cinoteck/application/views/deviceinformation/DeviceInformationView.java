package com.cinoteck.application.views.deviceinformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import com.cinoteck.application.UserProvider;
import com.cinoteck.application.views.MainLayout;
import com.cinoteck.application.views.utils.gridexporter.GridExporter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.devicemanager.DeviceManagerDto;
import de.symeda.sormas.api.devicemanager.DeviceMangerCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;

@PageTitle("APMIS-Device Manager")
@Route(value = "deviceManager", layout = MainLayout.class)
public class DeviceInformationView extends VerticalLayout {

	UserProvider userProvider = new UserProvider();
	Grid<DeviceManagerDto> grid = new Grid<>(DeviceManagerDto.class, false);
	Anchor anchor = new Anchor("", I18nProperties.getCaption(Captions.export));
	List<DeviceManagerDto> dataProvider;
	GridListDataView<DeviceManagerDto> dataView;
	private DeviceMangerCriteria criteria = new DeviceMangerCriteria();
	Paragraph countRowItems;
	
	private MultiSelectComboBox<AreaReferenceDto> areaFilter = new MultiSelectComboBox<AreaReferenceDto>();
	private MultiSelectComboBox<RegionReferenceDto> regionFilter = new MultiSelectComboBox<RegionReferenceDto>();
	private MultiSelectComboBox<DistrictReferenceDto> districtFilter = new MultiSelectComboBox<DistrictReferenceDto>();
	
	private List<AreaReferenceDto> regions = FacadeProvider.getAreaFacade().getAllActiveAsReference();
	private List<RegionReferenceDto> provinces = FacadeProvider.getRegionFacade().getAllActiveAsReference();
	private List<DistrictReferenceDto> districts = FacadeProvider.getDistrictFacade().getAllActiveAsReference();

	public DeviceInformationView() {

		if (I18nProperties.getUserLanguage() == null) {

			I18nProperties.setUserLanguage(Language.EN);
		} else {

			I18nProperties.setUserLanguage(userProvider.getUser().getLanguage());
			I18nProperties.getUserLanguage();
		}
		setSpacing(false);
		setHeightFull();
		setSizeFull();
		addFilters();
		configureGrid();

	}

	public Component addFilters() {

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

//		ComboBox<?> geographyUnitTypeFilter = new ComboBox<>(I18nProperties.getCaption("Unit Type"));

		Button resetFilters = new Button(I18nProperties.getCaption(Captions.resetFilters));
		
		areaFilter.setId(CaseDataDto.AREA);
		areaFilter.setWidth("145px");

		areaFilter.setLabel(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.AREA));
		areaFilter.setPlaceholder(I18nProperties.getCaption(Captions.area));
		areaFilter.getStyle().set("margin-left", "0.1rem");
		areaFilter.getStyle().set("padding-top", "0px!important");
		areaFilter.setClearButtonVisible(true);
		regions = FacadeProvider.getAreaFacade().getAllActiveAsReference();

		areaFilter.setItems(regions);			

		areaFilter.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				List<RegionReferenceDto> allProvinces = new ArrayList<>();
				if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
					for (AreaReferenceDto selectedUUID : e.getValue()) {
						String uuid = selectedUUID.getUuid();
						provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaPashto(uuid);
						allProvinces.addAll(provinces);
					}
//						provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaPashto(e.getValue().getUuid());
					regionFilter.setItems(allProvinces);
					areaFilter.setValue(e.getValue());
				} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
					for (AreaReferenceDto selectedUUID : e.getValue()) {
						String uuid = selectedUUID.getUuid();
						provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaDari(uuid);
						allProvinces.addAll(provinces);
					}
//						provinces = FacadeProvider.getRegionFacade().getAllActiveByAreaDari(e.getValue().getUuid());
					regionFilter.setItems(allProvinces);
					areaFilter.setValue(e.getValue());
				} else {
					for (AreaReferenceDto selectedUUID : e.getValue()) {
						String uuid = selectedUUID.getUuid();
						provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(uuid);
						allProvinces.addAll(provinces);
						System.out.println(selectedUUID.getCaption() + " CAPTIONNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
					}
//						provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid());
					regionFilter.setItems(allProvinces);
					areaFilter.setValue(e.getValue());					
				}
				regionFilter.setEnabled(true);
			} else {
//				if (regionFilter.getValue() != null) {
//					regionFilter.clear();
//				}
//				regionFilter.setEnabled(false);
			}
			reload();
			updateRowCount();

	
		});

		regionFilter.setId(CaseDataDto.REGION);
		regionFilter.setWidth(145, Unit.PIXELS);
		regionFilter.setLabel(
				I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, I18nProperties.getCaption(Captions.region)));
		regionFilter.setPlaceholder(I18nProperties.getCaption(Captions.region));
		regionFilter.getStyle().set("margin-left", "0.1rem");
		regionFilter.getStyle().set("padding-top", "0px!important");
		regionFilter.setClearButtonVisible(true);
		
		
//		dataView.setFilter((SerializablePredicate<DeviceManagerDto>) criteria.region(userProvider.getUser().getRegion()));


		regionFilter.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				List<DistrictReferenceDto> allDistricts = new ArrayList<>();
				if (userProvider.getUser().getLanguage().toString().equals("Pashto")) {
					for (RegionReferenceDto selectedUUID : e.getValue()) {
						String uuid = selectedUUID.getUuid();
						districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionPashto(uuid);
						allDistricts.addAll(districts);
					}
//					districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionPashto(e.getValue().getUuid());
					districtFilter.setItems(allDistricts);
					regionFilter.setValue(e.getValue());
				} else if (userProvider.getUser().getLanguage().toString().equals("Dari")) {
					for (RegionReferenceDto selectedUUID : e.getValue()) {
						String uuid = selectedUUID.getUuid();
						districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionDari(uuid);
						allDistricts.addAll(districts);
					}

//					districts = FacadeProvider.getDistrictFacade().getAllActiveByRegionDari(e.getValue().getUuid());
					districtFilter.setItems(allDistricts);
					regionFilter.setValue(e.getValue());
				} else {
					for (RegionReferenceDto selectedUUID : e.getValue()) {
						String uuid = selectedUUID.getUuid();
						districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(uuid);
						allDistricts.addAll(districts);
						System.out.println(selectedUUID.getCaption() + " CAPTIONPROVINCEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
					}
//					districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid());
					districtFilter.setItems(allDistricts);
					regionFilter.setValue(e.getValue());
				}
				districtFilter.setEnabled(true);
			} else {
//				if (districtFilter.getValue() != null) {
//					districtFilter.clear();
//				}
//				districtFilter.setEnabled(false);
			}
			reload();
			updateRowCount();

		});

		districtFilter.setId(CaseDataDto.DISTRICT);
		districtFilter.setWidth(145, Unit.PIXELS);
		districtFilter.setLabel(I18nProperties.getCaption(Captions.district));
		districtFilter.setPlaceholder(I18nProperties.getCaption(Captions.district));
		districtFilter.getStyle().set("margin-left", "0.1rem");
		districtFilter.getStyle().set("padding-top", "0px!important");
		districtFilter.setClearButtonVisible(true);
//		districtFilter.setReadOnly(true);	

		districtFilter.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				List<CommunityReferenceDto> allClusters = new ArrayList<>();

//				for (DistrictReferenceDto selectedUUID : e.getValue()) {
//					String uuid = selectedUUID.getUuid();
//					communities = FacadeProvider.getCommunityFacade().getAllActiveByDistrict(uuid);
//					allClusters.addAll(communities);
//				}

//				communities = FacadeProvider.getCommunityFacade().getAllActiveByDistrict(e.getValue().getUuid());
//				clusterCombo.setItemLabelGenerator(itm -> {
//					CommunityReferenceDto dcfv = (CommunityReferenceDto) itm;
//					return dcfv.getNumber() + " | " + dcfv.getCaption();
//				});
//				allClusters.sort(Comparator.comparing(CommunityReferenceDto::getNumber));
//				clusterCombo.setItems(allClusters);
//
//				clusterCombo.setEnabled(true);

				districtFilter.setValue(e.getValue());
				
				for (DistrictReferenceDto districtReferenceDto : e.getValue()) {
					
				}
				System.out.println(DistrictReferenceDto.CAPTION + " CA[TOONDISTRICTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");

			} else {
//				if (clusterCombo.getValue() != null) {
//					clusterCombo.clear();
//				}
//				clusterCombo.setEnabled(false);
			}

			reload();
			updateRowCount();
			
		});
			
		
		
		
		
		
		Button exportDevicesInfo = new Button(I18nProperties.getCaption(Captions.export));

		searchField.addClassName("filterBar");
		searchField.setPlaceholder(I18nProperties.getCaption(Captions.actionSearch));
		Icon searchIcon = new Icon(VaadinIcon.SEARCH);
		searchIcon.getStyle().set("color", "#0D6938");
		searchField.setPrefixComponent(searchIcon);
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.setWidth("10%");
		searchField.setClearButtonVisible(true);

		countRowItems = new Paragraph();
		countRowItems.setId("rowCount");
		if (dataProvider != null) {
			countRowItems.setText("Rows: " + fetchDevicesInfoData().size());

		}

		searchField.addValueChangeListener(e -> {
			resetFilters.setVisible(true);
			String term = e.getValue() == null ? "" : e.getValue().trim().toLowerCase();
			if (term.isEmpty()) {
				if (dataView != null) {
					dataView.removeFilters();
				}
				resetFilters.setVisible(false);
			} else {
				if (dataView != null) {
					dataView.setFilter(item -> {
						String u = item.getUserName() == null ? "" : item.getUserName().toLowerCase();
						String l = item.getUserLocation() == null ? "" : item.getUserLocation().toLowerCase();

						String modelSearctTerm = item.getDeviceModel() == null ? ""
								: item.getDeviceModel().toLowerCase();

						return u.contains(term) || l.contains(term) || modelSearctTerm.contains(term);
					});
				}
			}

			updateRowCount();
		});

		layout.add(searchField, areaFilter, regionFilter, districtFilter);
//		layout.add(geographyUnitTypeFilter);

		layout.add(resetFilters);
		layout.add(exportDevicesInfo);

		relevancelayout.add(countRowItems);

		searchField.addValueChangeListener(e -> {

			resetFilters.setVisible(true);

		});

		resetFilters.addClassName("resetButton");
//		resetFilters.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		resetFilters.setVisible(false);

		// layout.addComponentAsFirst(anchor);
		layout.setWidth("75%");
		layout.addClassName("pl-3");
		layout.addClassName("row");

		vlayout.setWidth("99%");
		vlayout.add(displayFilters, layout, relevancelayout);
		vlayout.getStyle().set("margin-right", "0.5rem");

		add(vlayout);

		return vlayout;
	}

	private void updateRowCount() {
		if (dataView != null) {
			countRowItems.setText("Rows: " + dataView.getItemCount());
		}
	}

	private void configureGrid() {

		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);

		grid.addColumn(DeviceManagerDto::getDeviceBrand).setHeader(I18nProperties.getCaption("Device Brand"))
		.setSortable(true).setResizable(true);
		grid.addColumn(DeviceManagerDto::getDeviceModel).setHeader(I18nProperties.getCaption("Device Model"))
				.setSortable(true).setResizable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.area));
		grid.addColumn(DeviceManagerDto::getUserName).setHeader(I18nProperties.getCaption("Username"))
				.setResizable(true).setSortable(true).setTooltipGenerator(e -> I18nProperties.getCaption("Username"));

		grid.addColumn(DeviceManagerDto::getUserLocation).setHeader(I18nProperties.getCaption("Location"))
				.setSortable(true).setResizable(true).setTooltipGenerator(e -> I18nProperties.getCaption("Location"));

		grid.addColumn(DeviceManagerDto::getAndroidVersion).setHeader(I18nProperties.getCaption("Android Version"))
				.setSortable(true).setResizable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption("Android Version"));

		grid.addColumn(DeviceManagerDto::getApkVersion).setHeader(I18nProperties.getCaption("APK Version"))
				.setResizable(true).setSortable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption("APK Version"));
		grid.addColumn(DeviceManagerDto::getChangeDate).setHeader(I18nProperties.getCaption("Last Synced"))
				.setResizable(true).setSortable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption("Last Synced"));

		grid.setVisible(true);

		dataProvider = fetchDevicesInfoData();

		grid.setItems(dataProvider);
		dataView = grid.setItems(dataProvider);

		if (userProvider.hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {

			grid.asSingleSelect().addValueChangeListener(event -> {
				if (event.getValue() != null) {
					viewDeviceInformation(event.getValue());
				}
				grid.deselectAll();
			});
		}

		updateRowCount();
		add(grid);

		GridExporter<DeviceManagerDto> exporter = GridExporter.createFor(grid);
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

	private List<DeviceManagerDto> fetchDevicesInfoData() {
//		List<SortProperty> sortProperties = query.getSortOrders().stream()
//				.map(order -> new SortProperty(order.getSorted(), order.getDirection().equals(SortDirection.ASCENDING)))
//				.collect(Collectors.toList());

		return FacadeProvider.getDeviceManagerFacade().getIndexList(criteria, null, null, null);

	}

	public void viewDeviceInformation(DeviceManagerDto deviceManagerDto) {
		DeviceDetailsDialog dialog = new DeviceDetailsDialog(deviceManagerDto);
		dialog.open();

	}
	
	public void reload() {
	    if (criteria == null) {
	        criteria = new DeviceMangerCriteria();
	    }
	    
	    // Set filter values
	    criteria.area(areaFilter.getValue());
	    criteria.region(regionFilter.getValue());
	    criteria.district(districtFilter.getValue());
	    
	    // DEBUG: Print what we're sending
	    System.out.println("DEBUG - Sending to backend:");
	    System.out.println("  Area count: " + (areaFilter.getValue() != null ? areaFilter.getValue().size() : 0));
	    System.out.println("  Region count: " + (regionFilter.getValue() != null ? regionFilter.getValue().size() : 0));
	    System.out.println("  District count: " + (districtFilter.getValue() != null ? districtFilter.getValue().size() : 0));
	    
	    // Fetch new data with updated criteria
	    List<DeviceManagerDto> newData = fetchDevicesInfoData();
	    
	    // Update grid data
	    grid.setItems(newData);
	    dataView = grid.getListDataView();
	    
	    updateRowCount();
	}

}
