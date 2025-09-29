package com.cinoteck.application.views.deviceinformation;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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

import de.symeda.sormas.api.devicemanager.DeviceManagerDto;

public class DeviceDetailsDialog extends Dialog {
    
    public DeviceDetailsDialog(DeviceManagerDto deviceManagerDto) {
        setSizeFull();
        setModal(true);
        setDraggable(true);
        setResizable(true);
        
        createHeader();
        createContent(deviceManagerDto);
        createFooter();
    }
    
    private void createHeader() {
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
        header.getStyle().set("padding", "20px");
        header.getStyle().set("border-bottom", "1px solid #e0e0e0");
        
        add(header);
    }
    
    private void createContent(DeviceManagerDto deviceManagerDto) {
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        
        // Left Column
        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.setWidth("50%");
        
        leftColumn.add(createDeviceOverview(deviceManagerDto));
        leftColumn.add(createStorageInformation(deviceManagerDto));
        leftColumn.add(createLocationInformation(deviceManagerDto));
        
        // Right Column
        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.setWidth("50%");
        
        rightColumn.add(createNetworkInformation(deviceManagerDto));
        rightColumn.add(createBatteryInformation(deviceManagerDto));
        rightColumn.add(createSystemInformation(deviceManagerDto));
        
        mainLayout.add(leftColumn, rightColumn);
        add(mainLayout);
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

        
        H4 title = new H4("Device Overview");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        
        Hr horizontalLine = new Hr();

        
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
        HorizontalLayout snLayout = createInfoRow("SN:", deviceManagerDto.getDeviceId());
        
        
        VerticalLayout deviceDetail = new VerticalLayout();
        deviceDetail.add(snLayout);
        
        HorizontalLayout deviceInfoLayout = new HorizontalLayout();
        deviceInfoLayout.setWidthFull();
        deviceInfoLayout.add(deviceInfo, deviceDetail);
        
        section.add(title, horizontalLine, deviceInfoLayout);
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

        
        H4 title = new H4("Storage Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        
        Hr horizontalLine = new Hr();

        
        HorizontalLayout storageRow = new HorizontalLayout();
        storageRow.setWidthFull();
        storageRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        
        
        VerticalLayout internalStorageLayout = new VerticalLayout();

        // Internal Storage
        H3 iNtitleSpan = new H3("Internal Storage");
        iNtitleSpan.getStyle().set("font-size", "12px");
        iNtitleSpan.getStyle().set("color", "#666");
        
        VerticalLayout internalStorage = createStorageCard("Internal Storage",
        		Double.parseDouble(((deviceManagerDto.getInternalStorageTotal()/1024) - (deviceManagerDto.getInternalStorageFree()/1024))+""), 
        		Double.parseDouble((deviceManagerDto.getInternalStorageTotal()/1024)+""), 
        		Integer.parseInt((((deviceManagerDto.getInternalStorageFree()/deviceManagerDto.getInternalStorageFree())*100)/1024)+""), 
        		"#9C27B0");
        internalStorageLayout.add(iNtitleSpan, internalStorage);
        // External Storage
        VerticalLayout externalStorageLayout = new VerticalLayout();

        H3 eStitleSpan = new H3("External Storage");
        eStitleSpan.getStyle().set("font-size", "12px");
        eStitleSpan.getStyle().set("color", "#666");
        
        VerticalLayout externalStorage = createStorageCard("Internal Storage",
        		Double.parseDouble((deviceManagerDto.getInternalStorageTotal() - deviceManagerDto.getInternalStorageFree())+""), 
        		Double.parseDouble(deviceManagerDto.getInternalStorageTotal().toString()), 
        		Integer.parseInt(((deviceManagerDto.getInternalStorageFree()/deviceManagerDto.getInternalStorageTotal())*100)+ ""), "#2196F3");
        

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
        
        VerticalLayout ramUsage = createStorageCard("RAM Storage", 47.7, deviceManagerDto.getRamTotal(), 5, "#F44336");
        ramStorageLayout.add(ramtitleSpan, ramUsage);
        performanceRow.add(cpuUsageLayout, ramStorageLayout);
        
        section.add(title,  horizontalLine, storageRow, performanceRow);
        return section;
    }
    
    private VerticalLayout createLocationInformation(DeviceManagerDto deviceManagerDto) {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "1px solid #e0e0e0");
        section.getStyle().set("border-radius", "8px");
        section.getStyle().set("margin-bottom", "15px");
        section.getStyle().set("padding", "20px !important");
        
        H4 title = new H4("Location Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        
        Hr horizontalLine = new Hr();

        
        HorizontalLayout currentLocation = createLocationRow(VaadinIcon.MAP_MARKER, "Current Location", deviceManagerDto.getUserLocation());
//        HorizontalLayout lastLocation = createLocationRow(VaadinIcon.MAP_MARKER, "Last known location", "Badaskan");
        
        section.add(title, horizontalLine, currentLocation);
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

        
        H4 title = new H4("Network Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        
        Hr horizontalLine = new Hr();

        HorizontalLayout networkStrength = createNetworkRow(VaadinIcon.SIGNAL, "Network Strength", "Strong", "#4CAF50");
        HorizontalLayout wifiStatus = createNetworkRow(VaadinIcon.SIGNAL, "Wi-Fi", "Disconnected", "#F44336");
        
        section.add(title, horizontalLine, networkStrength, wifiStatus);
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

        
        H4 title = new H4("Battery Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        
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
        
        Span chargingText = new Span("Charging");
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
    
    private VerticalLayout createSystemInformation(DeviceManagerDto deviceManagerDto) {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "1px solid #e0e0e0");
        section.getStyle().set("border-radius", "8px");
        section.getStyle().set("margin-bottom", "15px");
        section.getStyle().set("padding", "20px !important");

        
        H4 title = new H4("System Information");
        title.getStyle().set("color", "#2d5a3d");
        title.getStyle().set("margin-top", "0");
        
        Hr horizontalLine = new Hr();

        
        HorizontalLayout androidVersion = createInfoRow("Android Version", deviceManagerDto.getAndroidVersion());
        HorizontalLayout apkVersion = createInfoRow("APK Version", deviceManagerDto.getApkVersion());
        
        section.add(title, horizontalLine, androidVersion, apkVersion);
        return section;
    }
    
    private void createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        footer.setPadding(true);
        footer.getStyle().set("border-top", "1px solid #e0e0e0");
        footer.getStyle().set("background-color", "#f5f5f5");
        
        Button closeBtn = new Button("Close");
        closeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        closeBtn.addClickListener(e -> close());
        
        HorizontalLayout actionButtons = new HorizontalLayout();
        actionButtons.setSpacing(true);
        
        Button requestDataSync = new Button("Request Data sync", new Icon(VaadinIcon.REFRESH));
        requestDataSync.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button latestDiagnostics = new Button("Latest diagnostics", new Icon(VaadinIcon.STETHOSCOPE));
        latestDiagnostics.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button remoteSupport = new Button("Remote support", new Icon(VaadinIcon.HEADPHONES));
        remoteSupport.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        actionButtons.add(requestDataSync, latestDiagnostics, remoteSupport);
        
        footer.add(closeBtn, actionButtons);
        add(footer);
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
        labelSpan.getStyle().set("margin-right", "40%");
        
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
        if(label.equalsIgnoreCase("Network Strength")) {
        	icon = new Image("images/Shape4.svg", "Android");
        	icon.getStyle().set("width", "30px").set("height", "65px");
             
        }else if(label.equalsIgnoreCase("Wi-Fi")) {

        	icon = new Image("images/shape5.svg", "Android");
        	icon.getStyle().set("width", "30px").set("height", "65px");
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