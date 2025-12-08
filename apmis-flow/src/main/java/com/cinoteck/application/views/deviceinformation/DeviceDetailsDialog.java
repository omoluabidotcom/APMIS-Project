package com.cinoteck.application.views.deviceinformation;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.cinoteck.application.UserProvider;
import com.cinoteck.application.views.utils.gridexporter.GridExporter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.theme.lumo.LumoUtility;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.deviceerrormanager.DeviceErrorManagerDto;
import de.symeda.sormas.api.devicemanager.DeviceManagerDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;

public class DeviceDetailsDialog extends Dialog {
	
	
	UserProvider userProvider = new UserProvider();
	Grid<DeviceErrorManagerDto> grid = new Grid<>(DeviceErrorManagerDto.class, false);
	Anchor anchor = new Anchor("", I18nProperties.getCaption(Captions.export));
	List<DeviceErrorManagerDto> dataProvider;
	GridListDataView<DeviceErrorManagerDto> dataView;
 
    private Dialog errorLogDialog;
    private Grid<DeviceErrorManagerDto> errorGrid;
 	
	
    
    public DeviceDetailsDialog(DeviceManagerDto deviceManagerDto) {
    	
        setHeight("94%");
        setWidth("80%");
        setModal(true);
        setDraggable(true);
        setResizable(true);

        
        VerticalLayout container = new VerticalLayout();
        container.setSizeFull();
        container.setPadding(false);
        container.setSpacing(false);

        Component header = createHeader();
        Component content = createContent(deviceManagerDto);
        Component footer = createFooter(deviceManagerDto);

        // Content must grow and become scrollable
        container.add(header, content, footer);
        container.setFlexGrow(1, content); // pushes footer down
        content.getElement().getStyle().set("overflow", "auto");
        
        add(container);

//        createHeader();
//        createContent(deviceManagerDto);
//        createFooter(deviceManagerDto);
    }
//    
//    private void createHeader() {
//        H3 title = new H3("Device Details");
//        title.getStyle().set("margin", "0");
//        title.getStyle().set("color", "#2d5a3d");
//        
//        Span subtitle = new Span("This page contains all the information of this mobile device.");
//        subtitle.getStyle().set("color", "#666");
//        subtitle.getStyle().set("font-size", "14px");
//        
//        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
//        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//        closeButton.addClickListener(e -> close());
//        closeButton.getStyle().set("position", "absolute");
//        closeButton.getStyle().set("right", "10px");
//        closeButton.getStyle().set("top", "10px");
//        
//        VerticalLayout headerContent = new VerticalLayout(title, subtitle);
//        headerContent.setSpacing(false);
//        headerContent.setPadding(false);
//        
//        Div header = new Div(headerContent, closeButton);
//        header.getStyle().set("position", "relative");
//        header.getStyle().set("padding", "10px");
////        header.getStyle().set("border-bottom", "1px solid #e0e0e0");
//        
//        add(header);
//    }
    
    private Component createHeader() {
        H3 title = new H3("Device Details");
        title.getStyle().set("margin", "0");
        title.getStyle().set("color", "#2d5a3d");

        Span subtitle = new Span("This page contains all the information of this mobile device.");
        subtitle.getStyle().set("color", "#666");
        subtitle.getStyle().set("font-size", "14px");

        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickListener(e -> close());
        closeButton.getStyle().set("position", "absolute");
        closeButton.getStyle().set("right", "10px");
        closeButton.getStyle().set("top", "10px");

        VerticalLayout headerContent = new VerticalLayout(title, subtitle);
        headerContent.setSpacing(false);
        headerContent.setPadding(false);

        Div header = new Div(headerContent, closeButton);
        header.getStyle().set("position", "relative");
        header.getStyle().set("padding", "10px");

        return header; // <-- return instead of add()
    }

//    
//    private void createContent(DeviceManagerDto deviceManagerDto) {
//        HorizontalLayout mainLayout = new HorizontalLayout();
//        mainLayout.setWidthFull();
//        mainLayout.getStyle().set("height", "84%");
//        mainLayout.setSpacing(true);
//        
//        // Left Column
//        VerticalLayout leftColumn = new VerticalLayout();
//        leftColumn.setWidth("50%");
//        
//        leftColumn.add(createDeviceOverview(deviceManagerDto));
//        leftColumn.add(createStorageInformation(deviceManagerDto));
//        leftColumn.add(createLocationInformation(deviceManagerDto));
//        
//        // Right Column
//        VerticalLayout rightColumn = new VerticalLayout();
//        rightColumn.setWidth("50%");
//        
//        rightColumn.add(createNetworkInformation(deviceManagerDto));
//        rightColumn.add(createBatteryInformation(deviceManagerDto));
//        rightColumn.add(createSystemInformation(deviceManagerDto));
//        
//        mainLayout.add(leftColumn, rightColumn);
//        add(mainLayout);
//    }
//    
    private Component createContent(DeviceManagerDto deviceManagerDto) {
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setSpacing(true);

        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.setWidth("50%");

        leftColumn.add(createDeviceOverview(deviceManagerDto));
        leftColumn.add(createStorageInformation(deviceManagerDto));
        leftColumn.add(createLocationInformation(deviceManagerDto));

        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.setWidth("50%");

        rightColumn.add(createNetworkInformation(deviceManagerDto));
        rightColumn.add(createBatteryInformation(deviceManagerDto));
        rightColumn.add(createSystemInformation(deviceManagerDto));

        mainLayout.add(leftColumn, rightColumn);

        mainLayout.setSizeFull();
        return mainLayout; // <-- return instead of add()
    }

    private VerticalLayout createDeviceOverview(DeviceManagerDto deviceManagerDto) {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "1px solid #e0e0e0");
        section.getStyle().set("border-radius", "8px");
        section.getStyle().set("margin-bottom", "15px");
        section.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        section.getStyle().set("padding", "20px !important");
        section.getStyle().set("height", "20%");
        section.getStyle().set("gap", "0%");



        
        H4 title = new H4("Device Overview");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        title.getStyle().set("font-size", "13px !important");
        
//        Hr horizontalLine = new Hr();

        
        HorizontalLayout deviceInfo = new HorizontalLayout();
        deviceInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        deviceInfo.setWidthFull();
        
        Image androidIcon = new Image("images/android-for-a-device-view.svg", "Android");
        androidIcon.getStyle().set("width", "65px").set("height", "65px");
        
        VerticalLayout deviceDetails = new VerticalLayout();
        deviceDetails.setSpacing(false);
        deviceDetails.setPadding(false);
        
        Span deviceName = new Span(deviceManagerDto.getDeviceBrand());
        deviceName.getStyle().set("font-weight", "bold");
        deviceName.getStyle().set("font-size", "16px");
        
        Span modelInfo = new Span("Model: " +  deviceManagerDto.getDeviceModel());
        modelInfo.getStyle().set("color", "#666");
        modelInfo.getStyle().set("font-size", "14px");
        
        deviceDetails.add(deviceName, modelInfo);
        deviceInfo.add(androidIcon, deviceDetails);
        
        // IMEI and Serial Number
 
        HorizontalLayout userNameLayout = createInfoRow("Username:", deviceManagerDto.getUserName());
        
        
         HorizontalLayout snLayout = createInfoRow("SN:", deviceManagerDto.getDeviceId());
        
        
        VerticalLayout deviceDetail = new VerticalLayout();
 
        deviceDetail.add(userNameLayout, snLayout);
         
        HorizontalLayout deviceInfoLayout = new HorizontalLayout();
        deviceInfoLayout.setWidthFull();
        deviceInfoLayout.add(deviceInfo, deviceDetail);
        
        section.add(title,  deviceInfoLayout);
        return section;
    }
    
    private VerticalLayout createStorageInformation(DeviceManagerDto deviceManagerDto) {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "1px solid #e0e0e0");
        section.getStyle().set("border-radius", "8px");
        section.getStyle().set("margin-bottom", "15px");
        section.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        section.getStyle().set("padding", "20px !important");
        section.getStyle().set("gap", "0%");

        
        H4 title = new H4("Storage Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        title.getStyle().set("font-size", "13px !important");

        

        
        HorizontalLayout storageRow = new HorizontalLayout();
        storageRow.setWidthFull();
        storageRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        
        
        VerticalLayout internalStorageLayout = new VerticalLayout();

        // Internal Storage
        H3 iNtitleSpan = new H3("Internal Storage");
        iNtitleSpan.getStyle().set("font-size", "12px");
        iNtitleSpan.getStyle().set("color", "#666");
        
//        VerticalLayout internalStorage = createStorageCard("Internal Storage",
//        		Double.parseDouble(deviceManagerDto.getInternalStorageTotalGb() - deviceManagerDto.getInternalStorageFreeGb()+""), 
//        		Double.parseDouble(deviceManagerDto.getInternalStorageTotalGb() + ""), 
//        		Integer.parseInt((((deviceManagerDto.getInternalStorageTotalGb() - deviceManagerDto.getInternalStorageFreeGb())/deviceManagerDto.getInternalStorageTotalGb())*100)+""), 
//        		"#9C27B0");
        
        
        BigDecimal totalInternal = deviceManagerDto.getInternalStorageTotalGb();
        BigDecimal freeInternal  = deviceManagerDto.getInternalStorageFreeGb();

        if (totalInternal == null) totalInternal = BigDecimal.ZERO;
        if (freeInternal  == null) freeInternal  = BigDecimal.ZERO;

        BigDecimal used = totalInternal.subtract(freeInternal); // used = total - free

        double usedDouble  = used.doubleValue();
        double totalDouble = totalInternal.doubleValue();

        int percentUsed = totalInternal.signum() == 0
            ? 0
            : used
                .divide(totalInternal, 4, RoundingMode.HALF_UP) // ratio with precision
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)      // round to whole percent
                .intValue();
        
        
        VerticalLayout internalStorage = createStorageCard(
        	    "Internal Storage",
        	    usedDouble,
        	    totalDouble,
        	    percentUsed,
        	    "#9C27B0"
        	);
        internalStorageLayout.add(iNtitleSpan, internalStorage);
        // External Storage
        VerticalLayout externalStorageLayout = new VerticalLayout();

        H3 eStitleSpan = new H3("External Storage");
        eStitleSpan.getStyle().set("font-size", "12px");
        eStitleSpan.getStyle().set("color", "#666");
        
//        VerticalLayout externalStorage = createStorageCard("Internal Storage",
//        		Double.parseDouble( deviceManagerDto.getExternalStorageTotalGb()  - deviceManagerDto.getExternalStorageFreeGb() +""), 
//        		Double.parseDouble(deviceManagerDto.getExternalStorageTotalGb() + ""), 
//        		Integer.parseInt(((((deviceManagerDto.getExternalStorageTotalGb() - deviceManagerDto.getExternalStorageFreeGb())/deviceManagerDto.getExternalStorageTotalGb())*100)) + ""), "#2196F3");
//        
//        
        
        BigDecimal totalExternal = deviceManagerDto.getExternalStorageTotalGb();
        BigDecimal freeExternal  = deviceManagerDto.getExternalStorageFreeGb();

        if (totalExternal == null) totalExternal = BigDecimal.ZERO;
        if (freeExternal  == null) freeExternal  = BigDecimal.ZERO;

        BigDecimal usedEx = totalExternal.subtract(freeExternal); // used = total - free

        double usedExDouble  = usedEx.doubleValue();
        double totalExDouble = totalExternal.doubleValue();

        int percentExUsed = totalExternal.signum() == 0
            ? 0
            : usedEx
                .divide(totalExternal, 4, RoundingMode.HALF_UP) // ratio with precision
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)      // round to whole percent
                .intValue();
        
        
        VerticalLayout externalStorage = createStorageCard(
        	    "Internal Storage",
        	    usedExDouble,
        	    totalExDouble,
        	    percentExUsed,
        	    "#9C27B0"
        	);

        externalStorageLayout.add(eStitleSpan, externalStorage);
        storageRow.add(internalStorageLayout, externalStorageLayout);
        
        // CPU and RAM Usage
        HorizontalLayout performanceRow = new HorizontalLayout();
        performanceRow.setWidthFull();
        performanceRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
 
        
     // CPU Usage 
        VerticalLayout cpuUsageLayout = new VerticalLayout();

        H3 cputitleSpan = new H3("CPU Usage");
        cputitleSpan.getStyle().set("font-size", "12px");
        cputitleSpan.getStyle().set("color", "#666");
        
        VerticalLayout cpuUsage = createUsageCard("CPU Usage", 80, "#2196F3");
        cpuUsageLayout.add(cputitleSpan, cpuUsage);  
//        VerticalLayout ramUsage = createUsageCard("RAM Storage", 91, "#F44336", "7.3 GB / 8 GB Total");
        //RAM Usage 
      
      VerticalLayout ramStorageLayout = new VerticalLayout();

      H3 ramtitleSpan = new H3("RAM Storage");
        ramtitleSpan.getStyle().set("font-size", "12px");
        ramtitleSpan.getStyle().set("color", "#666");
        
        
     // RAM in GB (from DTO)
        BigDecimal ramTotalGb = deviceManagerDto.getRamTotalGb();          // BigDecimal (GB)
        if (ramTotalGb == null) ramTotalGb = BigDecimal.ZERO;

        // If you only have total (no free), assume used == total, or compute used if you have free.
        BigDecimal ramUsedGb = ramTotalGb;

        // If `getRamTotal()` is bytes and you prefer to derive total from bytes:
        Long ramTotalBytes = deviceManagerDto.getRamTotal();               // Long (bytes)
        double totalDoubleRam = ramTotalBytes == null ? ramTotalGb.doubleValue() : gb(ramTotalBytes).doubleValue();

        // Percent (adjust if you can compute real used)
        int percentRam = totalDoubleRam == 0 ? 0 : ramUsedGb
            .divide(BigDecimal.valueOf(totalDoubleRam), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(0, RoundingMode.HALF_UP)
            .intValue();
        
        VerticalLayout ramUsage = createStorageCard(
        	    "RAM Storage",
        	    ramUsedGb.doubleValue(),  // double
        	    totalDoubleRam,              // double
        	    percentRam,                  // int
        	    "#F44336"
        	);
//        VerticalLayout ramUsage = createStorageCard("RAM Storage", deviceManagerDto.getRamTotalGb(), deviceManagerDto.getRamTotal(), 5, "#F44336");
        ramStorageLayout.add(ramtitleSpan, ramUsage);
        performanceRow.add(cpuUsageLayout, ramStorageLayout);
        
        section.add(title,   storageRow, performanceRow);
        return section;
    }
    
    
    private static BigDecimal gb(long bytes) {
        return BigDecimal.valueOf(bytes).divide(BigDecimal.valueOf(1073741824d), 2, RoundingMode.HALF_UP);
    }
    
    private VerticalLayout createLocationInformation(DeviceManagerDto deviceManagerDto) {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "1px solid #e0e0e0");
        section.getStyle().set("border-radius", "8px");
        section.getStyle().set("margin-bottom", "15px");
        section.getStyle().set("padding", "20px !important");        
        section.getStyle().set("height", "15%");
        section.getStyle().set("gap", "0%");


        H4 title = new H4("Location Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        title.getStyle().set("font-size", "13px !important");

        

 
        HorizontalLayout currentLocation = createLocationRow(VaadinIcon.MAP_MARKER, "Assigned Location", deviceManagerDto.getUserLocation());
 //        HorizontalLayout lastLocation = createLocationRow(VaadinIcon.MAP_MARKER, "Last known location", "Badaskan");
        
        section.add(title,  currentLocation);
        return section;
    }
    
    private VerticalLayout createNetworkInformation(DeviceManagerDto deviceManagerDto) {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "1px solid #e0e0e0");
        section.getStyle().set("border-radius", "8px");
        section.getStyle().set("margin-bottom", "15px");
        section.getStyle().set("padding", "20px !important");
        section.getStyle().set("height", "30%");

        
        H4 title = new H4("Network Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        title.getStyle().set("font-size", "13px !important");

        
 
        HorizontalLayout networkStrength = createNetworkRow(VaadinIcon.SIGNAL, "Network Provider", deviceManagerDto.getNetworkProvider() != null ? deviceManagerDto.getNetworkProvider() : "", "#4CAF50");
        HorizontalLayout wifiStatus = new HorizontalLayout();
       
        
        if(deviceManagerDto.getWifiConnected()) {
        wifiStatus  = createNetworkRow(VaadinIcon.SPARK_LINE, "Wi-Fi", "Connected", "#4CAF50");
         }else {
        wifiStatus  = createNetworkRow(VaadinIcon.SIGNAL, "Wi-Fi", "Disonnected", "#F44336");
        }
      
        
        section.add(title, networkStrength, wifiStatus);
        return section;
    }
    
    private VerticalLayout createBatteryInformation(DeviceManagerDto deviceManagerDto) {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "1px solid #e0e0e0");
        section.getStyle().set("border-radius", "8px");
        section.getStyle().set("margin-bottom", "15px");
        section.getStyle().set("padding", "20px !important");
        section.getStyle().set("height", "30%");


        
        H4 title = new H4("Battery Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        title.getStyle().set("font-size", "13px !important");

        
        Hr horizontalLine = new Hr();

        
        HorizontalLayout batteryInfoLayout = new HorizontalLayout();
        batteryInfoLayout.setWidthFull();
        
        // Battery Level
        
        VerticalLayout externalStorage = createBatteryInfoCard("External Storage", deviceManagerDto.getBatteryLevel(), 100, ((deviceManagerDto.getBatteryLevel()/100)*100), "#2196F3");

        // Charging Status
        HorizontalLayout chargingStatus = new HorizontalLayout();
        chargingStatus.setAlignItems(FlexComponent.Alignment.CENTER);
        chargingStatus.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        
        Icon chargingIcon = new Icon(VaadinIcon.BOLT);
        chargingIcon.setColor("#FF9800");
        
 
        Span chargingText = new Span("Not Charging");
         chargingText.getStyle().set("font-weight", "500");
        
        HorizontalLayout chargingStatusTextLayout = new HorizontalLayout();
        chargingStatus.setAlignItems(FlexComponent.Alignment.CENTER);
        chargingStatus.setWidthFull();
        
        Span chargingStatusText = new Span("Charging Status");
        chargingStatusText.getStyle().set("color", "#666");
        
        chargingStatus.add(chargingIcon, chargingText );
        chargingStatusTextLayout.add(chargingStatusText);
        
        VerticalLayout newLayout =  new VerticalLayout();
        newLayout.setAlignItems(Alignment.CENTER);
        newLayout.add(chargingStatus, chargingStatusTextLayout);
        
        batteryInfoLayout.add(externalStorage, newLayout);
        section.add(title, horizontalLine, batteryInfoLayout);
        return section;
    }
       
    private VerticalLayout createDeviceSystemInformationC(DeviceManagerDto deviceManagerDto) {
         VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "1px solid #e0e0e0");
        section.getStyle().set("border-radius", "8px");
        section.getStyle().set("margin-bottom", "15px");
        section.getStyle().set("padding", "20px !important");
        section.getStyle().set("height", "30%");


        
        H4 title = new H4("System Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        title.getStyle().set("font-size", "13px !important");

        
        Hr horizontalLine = new Hr();

        
        HorizontalLayout androidVersion = createInfoRow("Android Version", deviceManagerDto.getAndroidVersion());
        HorizontalLayout apkVersion = createInfoRow("APK Version", deviceManagerDto.getApkVersion());
        
        section.add(title, horizontalLine, androidVersion, apkVersion);
        return section;
    }
     
    private VerticalLayout createSystemInformation(DeviceManagerDto deviceManagerDto) {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "1px solid #e0e0e0");
        section.getStyle().set("border-radius", "8px");
        section.getStyle().set("margin-bottom", "15px");
        section.getStyle().set("padding", "20px !important");
        section.getStyle().set("height", "30%");


        
        H4 title = new H4("System Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        title.getStyle().set("font-size", "13px !important");

        
        Hr horizontalLine = new Hr();
        
        HorizontalLayout itemsLayout = new HorizontalLayout();
        
        
        VerticalLayout itemsLayoutChild1 = new VerticalLayout();

        HorizontalLayout androidVersion = createInfoRow("Android Version", deviceManagerDto.getAndroidVersion());
        HorizontalLayout apkVersion = createInfoRow("APK Version", deviceManagerDto.getApkVersion());
        
        itemsLayoutChild1.add(androidVersion,apkVersion);
        
        VerticalLayout itemsLayoutChild2 = new VerticalLayout();
        HorizontalLayout campaignCount = createInfoRow("Active Synced Campaign", deviceManagerDto.getActiveCampaigns() != null ? deviceManagerDto.getActiveCampaigns().toString() : "Not Synced");
        HorizontalLayout formCount = createInfoRow("Form Count", deviceManagerDto.getActiveFormCount() != null ? deviceManagerDto.getActiveFormCount().toString() : "Not Synced");
        
        itemsLayoutChild2.add(campaignCount,formCount);

        itemsLayout.setWidthFull();
        itemsLayout.add(itemsLayoutChild1 , itemsLayoutChild2);
        
        section.add(title, horizontalLine, itemsLayout);
        return section;
    }

    private Component createFooter(DeviceManagerDto deviceManagerDto) {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        footer.getStyle().set("padding", "0px !important");
//        footer.setPadding(true);

        Button closeBtn = new Button("Close");
        closeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        closeBtn.addClickListener(e -> close());

        HorizontalLayout actionButtons = new HorizontalLayout();
        actionButtons.setSpacing(true);

        Button viewDeviceLogs = new Button("Error Logs", new Icon(VaadinIcon.REFRESH));
        viewDeviceLogs.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        viewDeviceLogs.addClickListener(e -> {
            if (errorLogDialog == null) {
                errorLogDialog = new Dialog();
                errorLogDialog.setHeaderTitle("Error Log");
                errorLogDialog.setWidth("800px");
                errorLogDialog.setHeight("600px");

                errorGrid = configureLogsGrid(deviceManagerDto);
                errorLogDialog.add(errorGrid);

                Button closeButton = new Button("Close", ev -> errorLogDialog.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                errorLogDialog.getFooter().add(closeButton);
            } else {
                errorGrid.getDataProvider().refreshAll();
            }
            errorLogDialog.open();
        });

        actionButtons.add(viewDeviceLogs);
        footer.add(closeBtn, actionButtons);

        return footer; // ✅ return instead of add()
    }

    
	private List<DeviceErrorManagerDto> fetchDevicesErrorData(DeviceManagerDto deviceManagerDto) {
 
		return FacadeProvider.getDeviceErrorManagerFacade().getLatestLogs(deviceManagerDto.getUserName(), deviceManagerDto.getDeviceId(), 19);
 	}
    
    
	private Grid<DeviceErrorManagerDto>configureLogsGrid(DeviceManagerDto deviceManagerDto) {

		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);

		grid.addColumn(DeviceErrorManagerDto::getErrorAction).setHeader(I18nProperties.getCaption("Error Action"))
				.setSortable(true).setResizable(true)
				.setTooltipGenerator(e -> I18nProperties.getCaption(Captions.area));
		grid.addColumn(DeviceErrorManagerDto::getCreationDate).setHeader(I18nProperties.getCaption("Time"))
				.setResizable(true).setSortable(true).setTooltipGenerator(e -> I18nProperties.getCaption("Username"));
		
		grid.addColumn(DeviceErrorManagerDto::getErrorMessage).setHeader(I18nProperties.getCaption("Error Message"))
				.setSortable(true).setResizable(true).setTooltipGenerator(e -> I18nProperties.getCaption("Location"));

		grid.setVisible(true);

		dataProvider = fetchDevicesErrorData(deviceManagerDto);

		grid.setItems(dataProvider);
		dataView = grid.setItems(dataProvider);

			grid.asSingleSelect().addValueChangeListener(event -> {
				if (event.getValue() != null) {
					 Dialog errorLogDialog = new Dialog();
			            errorLogDialog.setHeaderTitle("Error Log Dialog");
			            errorLogDialog.setWidth("800px");
			            errorLogDialog.setHeight("600px");        
			            
			            
			            
//			             Create the error log display area with Eclipse IDE styling
			            Div logContainer = new Div();
			            logContainer.getStyle()
			                .set("background-color", "#2b2b2b")
			                .set("color", "#cccccc")
			                .set("font-family", "Consolas, 'Courier New', monospace")
			                .set("font-size", "12px")
			                .set("padding", "10px")
			                .set("overflow-y", "auto")
			                .set("height", "100%")
			                .set("white-space", "pre-wrap")
			                .set("border", "1px solid #3c3c3c");
			            
			            // Sample error log content (replace with your actual log data)
			            String errorLogContent = buildErrorLogContentByDeviceId(event.getValue());
			            logContainer.getElement().setProperty("innerHTML", errorLogContent);
			            
			            // Create a scrollable content area
			            Div content = new Div(logContainer);
			            content.getStyle()
			                .set("flex", "1")
			                .set("overflow", "hidden");
			            errorLogDialog.add(content);
			            
			            // Footer with close button
			            Button closeButton = new Button("Close", eventx -> errorLogDialog.close());
			            closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			            errorLogDialog.getFooter().add(closeButton);
			            
			            errorLogDialog.open();				}
						grid.deselectAll();
			});
//		}

		    return grid;
	}

    private String buildErrorLogContent() {
        StringBuilder log = new StringBuilder();
        
        // Example error entries with color coding (similar to Eclipse)
        log.append("<span style='color: #cc7832;'>!ENTRY</span> ")
           .append("<span style='color: #a9b7c6;'>com.example.application 1 0 2025-10-02 14:32:15.123</span>\n");
        log.append("<span style='color: #cc7832;'>!MESSAGE</span> ")
           .append("<span style='color: #ff6b68;'>Error occurred while processing request</span>\n");
        log.append("<span style='color: #cc7832;'>!STACK 0</span>\n");
        log.append("<span style='color: #a9b7c6;'>java.lang.NullPointerException: Cannot invoke method on null object\n");
        log.append("    at com.example.service.DataService.processData(DataService.java:145)\n");
        log.append("    at com.example.controller.MainController.handleRequest(MainController.java:89)\n");
        log.append("    at com.vaadin.flow.component.ClickEvent.dispatch(ClickEvent.java:52)</span>\n\n");
        
        log.append("<span style='color: #cc7832;'>!ENTRY</span> ")
           .append("<span style='color: #a9b7c6;'>com.example.application 2 0 2025-10-02 14:30:42.456</span>\n");
        log.append("<span style='color: #cc7832;'>!MESSAGE</span> ")
           .append("<span style='color: #ffc66d;'>Warning: Connection timeout exceeded</span>\n");
        log.append("<span style='color: #a9b7c6;'>Connection to database took longer than expected (5000ms)</span>\n\n");
        
        log.append("<span style='color: #cc7832;'>!ENTRY</span> ")
           .append("<span style='color: #a9b7c6;'>com.example.application 4 0 2025-10-02 14:28:10.789</span>\n");
        log.append("<span style='color: #cc7832;'>!MESSAGE</span> ")
           .append("<span style='color: #6897bb;'>Info: Application started successfully</span>\n");
        
        return log.toString();
    }
    
    private String buildErrorLogContentByDeviceId(DeviceErrorManagerDto deviceManagerDto) {
    	
//    	DeviceErrorManagerDto deviceError =  FacadeProvider.getDeviceErrorManagerFacade().getDeviceErrorByUsernameAndDeviceId(deviceManagerDto.getUserName(), deviceManagerDto.getDeviceSerial());
        
    	
    	System.out.println(deviceManagerDto.getErrorMessage());
    
    	StringBuilder log = new StringBuilder();
        
        // Example error entries with color coding (similar to Eclipse)
    	
    	log.append("<span style='color: #cc7832;'>!USERNAME</span> ");
        log.append(deviceManagerDto.getUserName() +  "\n");
    	
        log.append("<span style='color: #cc7832;'>!ERROR SOURCE</span> ");
        log.append(deviceManagerDto.getErrorAction() +  "\n");
        
        log.append("<span style='color: #cc7832;'>!ERROR MESSAGE</span> ");
        log.append(deviceManagerDto.getErrorMessage() +  "\n");

        log.append("<span style='color: #a9b7c6;'>STACKTRACE</span>\n");
        log.append(deviceManagerDto.getStackTrace() +  "\n");
//        log.append("<span style='color: #cc7832;'>!STACK 0</span>\n");
//        log.append("<span style='color: #a9b7c6;'>java.lang.NullPointerException: Cannot invoke method on null object\n");
//        log.append("    at com.example.service.DataService.processData(DataService.java:145)\n");
//        log.append("    at com.example.controller.MainController.handleRequest(MainController.java:89)\n");
//        log.append("    at com.vaadin.flow.component.ClickEvent.dispatch(ClickEvent.java:52)</span>\n\n");
//        
//        log.append("<span style='color: #cc7832;'>!ENTRY</span> ")
//           .append("<span style='color: #a9b7c6;'>com.example.application 2 0 2025-10-02 14:30:42.456</span>\n");
//        log.append("<span style='color: #cc7832;'>!MESSAGE</span> ")
//           .append("<span style='color: #ffc66d;'>Warning: Connection timeout exceeded</span>\n");
//        log.append("<span style='color: #a9b7c6;'>Connection to database took longer than expected (5000ms)</span>\n\n");
//        
//        log.append("<span style='color: #cc7832;'>!ENTRY</span> ")
//           .append("<span style='color: #a9b7c6;'>com.example.application 4 0 2025-10-02 14:28:10.789</span>\n");
//        log.append("<span style='color: #cc7832;'>!MESSAGE</span> ")
//           .append("<span style='color: #6897bb;'>Info: Application started successfully</span>\n");
        
        return log.toString();
    }
    
    // Helper methods
    private HorizontalLayout createInfoRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("padding-left", "40px");
        
        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-weight", "500");
//        labelSpan.getStyle().set("margin-right", "40%");
        
        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("color", "#666");
        
        row.add(labelSpan, valueSpan);
        return row;
    }
    
    private VerticalLayout createStorageCard(String title, double used, double total, int percentage, String color) {
        
    	
    	VerticalLayout card = new VerticalLayout();
        card.setSpacing(false);
        card.setPadding(false);
        card.setWidthFull();
        card.getStyle().set("border", "1px solid #e0e0e0");
        card.getStyle().set("padding", "10px !important");
        card.getStyle().set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.1)");
        card.getStyle().set("border-radius", "8px");

      
        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "12px");
        titleSpan.getStyle().set("color", "#666");
        titleSpan.getStyle().set("margin-bottom", "5px");
        
        Span storageText = new Span(used + " GB / " + total + " GB Total");
        storageText.getStyle().set("font-size", "12px");
        storageText.getStyle().set("color", "#333");
                
        ProgressBar progressBar = new ProgressBar();
        progressBar.setValue(percentage / 100.0);
        progressBar.getStyle().set("width", "100%");
        progressBar.getStyle().set("--lumo-primary-color", color);        
       
        Span percentageText = new Span(percentage + "%");
        percentageText.getStyle().set("font-size", "12px");
        percentageText.getStyle().set("color", color);
        percentageText.getStyle().set("font-weight", "bold");
        
        
        HorizontalLayout percentageLayout = new HorizontalLayout();        
        percentageLayout.add(progressBar, percentageText);
        percentageLayout.getStyle().set("width", "100%");

//        HorizontalLayout internalStorageLayout = new HorizontalLayout();
        Image storageImage = new Image();
        if(title.equalsIgnoreCase("Internal Storage")) {
        	  storageImage = new Image("images/Shape.svg", "Android");
             storageImage.getStyle().set("width", "40px").set("height", "65px");
             
        }else if(title.equalsIgnoreCase("External Storage")) {

        	  storageImage = new Image("images/Shape1.svg", "Android");
             storageImage.getStyle().set("width", "20px").set("height", "65px");
        }else if(title.equalsIgnoreCase("RAM Storage")) {

      	  storageImage = new Image("images/Shape2.svg", "Android");
           storageImage.getStyle().set("width", "40px").set("height", "65px");
           
      }

       
        VerticalLayout percentageParentLayout = new VerticalLayout(); 
        percentageParentLayout.add(storageText, percentageLayout);
        percentageParentLayout.getStyle().set("width", "100%");

        
        HorizontalLayout cardParentLayout =  new HorizontalLayout();
        cardParentLayout.add(storageImage ,percentageParentLayout);
        cardParentLayout.getStyle().set("width", "100%");

        card.add(cardParentLayout);
        return card;
    }
    
  private VerticalLayout createBatteryInfoCard(String title, double used, double total, int percentage, String color) {
        
    	
    	VerticalLayout card = new VerticalLayout();
        card.setSpacing(false);
        card.setPadding(false);
        card.setWidthFull();
        card.getStyle().set("padding", "10px !important");
 

      
        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "12px");
        titleSpan.getStyle().set("color", "#666");
        titleSpan.getStyle().set("margin-bottom", "5px");
        
        Span storageText = new Span(used + " % / " + total + " % Total");
        storageText.getStyle().set("font-size", "12px");
        storageText.getStyle().set("color", "#333");
                
        ProgressBar progressBar = new ProgressBar();
        progressBar.setValue(percentage / 100.0);
        progressBar.getStyle().set("width", "100%");
        progressBar.getStyle().set("--lumo-primary-color", color);        
       
        Span percentageText = new Span(percentage + "%");
        percentageText.getStyle().set("font-size", "12px");
        percentageText.getStyle().set("color", color);
        percentageText.getStyle().set("font-weight", "bold");
        
        
        HorizontalLayout percentageLayout = new HorizontalLayout();        
        percentageLayout.add(progressBar, percentageText);
        percentageLayout.getStyle().set("width", "100%");

//        HorizontalLayout internalStorageLayout = new HorizontalLayout();
        Image storageImage = new Image("images/Shape3.svg", "Android");
        storageImage.getStyle().set("width", "40px").set("height", "65px");
        
        VerticalLayout percentageParentLayout = new VerticalLayout(); 
        percentageParentLayout.add(storageText, percentageLayout);
        percentageParentLayout.getStyle().set("width", "100%");

        
        HorizontalLayout cardParentLayout =  new HorizontalLayout();
        cardParentLayout.add(storageImage ,percentageParentLayout);
        cardParentLayout.getStyle().set("width", "100%");

        card.add(cardParentLayout);
        return card;
    }
    
    
    private VerticalLayout createUsageCard(String title, int percentage, String color) {
        return createUsageCard(title, percentage, color, null);
    }
    
    private VerticalLayout createUsageCard(String title, int percentage, String color, String additionalText) {
        VerticalLayout card = new VerticalLayout();
        card.setSpacing(false);
        card.setPadding(false);
        card.setWidthFull();
        card.getStyle().set("border", "1px solid #e0e0e0");
        card.getStyle().set("padding", "10px !important");
        card.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        
       
        Image usageImage = new Image("images/Shape1.svg", "Android");
        usageImage.getStyle().set("width", "20px").set("height", "65px");
        
        
        Span percentageText = new Span(percentage + "%");
        percentageText.getStyle().set("font-size", "12px");
        percentageText.getStyle().set("color", color);
        percentageText.getStyle().set("font-weight", "bold");
            
        ProgressBar progressBar = new ProgressBar();
        progressBar.setValue(percentage / 100.0);
        progressBar.getStyle().set("width", "100%");
        progressBar.getStyle().set("--lumo-primary-color", color);
        
        if (additionalText != null) {
            Span additionalSpan = new Span(additionalText);
            additionalSpan.getStyle().set("font-size", "12px");
            additionalSpan.getStyle().set("color", "#333");
            card.add(additionalSpan);
        }
        
      
        VerticalLayout percentageLayout = new VerticalLayout();
        percentageLayout.add(percentageText, progressBar);
        HorizontalLayout cardParentLayout = new HorizontalLayout();
        cardParentLayout.getStyle().set("width", "100%");
        
        cardParentLayout.add(usageImage, percentageLayout);
        card.add(cardParentLayout);
        return card;
    }
    
    private HorizontalLayout createLocationRow(VaadinIcon iconType, String label, String location) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.getStyle().set("padding-left", "20px");        
        row.getStyle().set("padding-right", "40px");

        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSide.setSpacing(true);
        
        Icon icon = new Icon(iconType);
        icon.setColor("#F44336");
        icon.setSize("16px");
        
        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-weight", "500");
        
        leftSide.add(icon, labelSpan);
        
        Span locationSpan = new Span(location);
        locationSpan.getStyle().set("color", "#4CAF50");
        locationSpan.getStyle().set("font-weight", "500");
        
        row.add(leftSide, locationSpan);
        return row;
    }
    
    private HorizontalLayout createNetworkRow(VaadinIcon iconType, String label, String status, String color) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.getStyle().set("padding-left", "20px");        
        row.getStyle().set("padding-right", "40px");
        
        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSide.setSpacing(true);
        
        
        Image icon = new Image();
  
        if(label.equalsIgnoreCase("Network Provider")) {
        	icon = new Image("images/Shape4.svg", "Android");
        	icon.getStyle().set("width", "30px").set("height", "65px");
             
        }else if(label.equalsIgnoreCase("Wi-Fi")) {
        	if(status.equalsIgnoreCase("connected")) {
        		icon = new Image("images/icons8-wi-fi.png", "Wifi On");
            	icon.getStyle().set("width", "30px").set("height", "30px");
        	}else {

            	icon = new Image("images/icons8-wi-fi-off-48.png", "Wifi Off");
            	icon.getStyle().set("width", "30px").set("height", "30px");
        	}

        }

      
        
        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-weight", "500");
        
        leftSide.add(icon, labelSpan);
        
        Span statusSpan = new Span(status);
        statusSpan.getStyle().set("color", color);
        statusSpan.getStyle().set("font-weight", "500");
        
        row.add(leftSide, statusSpan);
        return row;
    }
}