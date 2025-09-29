package com.cinoteck.application.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BackDropSuccessNotification {

    public static void show(String messageText) {
        UI ui = UI.getCurrent();

        ui.access(() -> {
            // Create custom backdrop
            Div backdrop = new Div();
            backdrop.getStyle()
                    .set("position", "fixed")
                    .set("top", "0")
                    .set("left", "0")
                    .set("width", "100%")
                    .set("height", "100%")
                    .set("background-color", "rgba(0,0,0,0.4)") // darker dim background
                    .set("z-index", "1000002");

            // Create dialog
            Dialog dialog = new Dialog();
            dialog.setModal(true);
            dialog.setCloseOnOutsideClick(true); // clicking outside closes dialog
            dialog.setCloseOnEsc(true);
            dialog.getElement().getStyle().set("z-index", "100001"); // above backdrop

            // Success message
            Span message = new Span("Profile updated successfully");
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
