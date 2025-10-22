package com.cinoteck.application.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BackDropErrorNotification {

    public static void show(String errorMessage) {
        UI ui = UI.getCurrent();

        // Create backdrop layer
        ui.access(() -> {

        Div backdrop = new Div();
        backdrop.getStyle()
                .set("position", "fixed")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("background-color", "rgba(0,0,0,0.2)") // dim background
                .set("z-index", "9998");

        // Notification
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setCloseOnOutsideClick(true); // clicking outside closes dialog
        dialog.setCloseOnEsc(true);
        dialog.getElement().getStyle().set("z-index", "100001"); // above backdrop

        // Message + Cancel button
        // Success message
        Span message = new Span("Profile update failed. Please contact administrator.");
        message.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "500")
                .set("color", "green");
        VerticalLayout layout = new VerticalLayout(message);
        layout.setSpacing(true);
        layout.setAlignItems(VerticalLayout.Alignment.CENTER);

        dialog.add(layout);

        // Ensure backdrop is always removed when dialog closes
        dialog.addDialogCloseActionListener(e -> {
        	
        	dialog.close();
        ui.remove(backdrop);
        });
        
        dialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                ui.remove(backdrop);
            }
        });

        // Clicking backdrop also closes dialog
        backdrop.addClickListener(e -> {
            ui.remove(backdrop);
            dialog.close();
        });

        // Add backdrop + dialog
        ui.add(backdrop);
        dialog.open();
    });
    }
}

