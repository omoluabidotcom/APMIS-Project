package com.cinoteck.application.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class BackDropSuccessNotification {

    public static void show(String errorMessage) {
        UI ui = UI.getCurrent();

        // Create backdrop layer
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
        Notification notification = new Notification();
        notification.setDuration(0); // stays open until closed
        notification.setPosition(Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.getElement().getStyle().set("z-index", "9999");

        // Message + Cancel button
        Span message = new Span(errorMessage);
//        Icon cancelButton = new Icon(VaadinIcon.clo);
//        cancelButton.se
//        cancelButton.addClickListener(e->{
//            ui.remove(backdrop);
//            notification.close();	
//        });

        HorizontalLayout content = new HorizontalLayout(message);
        content.setSpacing(true);
        content.setAlignItems(HorizontalLayout.Alignment.CENTER);
        notification.add(content);

        // Clicking backdrop closes it
        backdrop.addClickListener(e -> {
            ui.remove(backdrop);
            notification.close();
        });

        // Add backdrop to UI
        ui.add(backdrop);

        // Show notification
        notification.open();
    }
}

