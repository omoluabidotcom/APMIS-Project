package com.cinoteck.application.utils.authentication;

import com.cinoteck.application.views.utils.email.SendGridEmailService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserDto;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Route("resetuserpassword")
@PageTitle("Reset Password")
@Component
public class UpdatePasswordView extends VerticalLayout implements BeforeEnterObserver {

	private final ConfirmDialog passwordConfirmDialog = new ConfirmDialog();
	private final ConfirmDialog tokenEntryDialog = new ConfirmDialog();
	private boolean passwordDialogInitialized = false;
	private boolean tokenDialogInitialized = false;

	private final ConfirmDialog passwordEntryDialog = new ConfirmDialog();
	private boolean newPasswordDialogInitialized = false;

	private TextField resetTokenField;

	TextField customPasswordField = new TextField();
	TextField confirmPasswordFieldx = new TextField();

	    private PasswordField newPasswordField;
	    private PasswordField confirmPasswordField;
	    private String currentToken;
	
    public UpdatePasswordView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f7f9fb");
    }
	
//
//	@Override
//	public void beforeEnter(BeforeEnterEvent event) {
//	    QueryParameters queryParameters = event.getLocation().getQueryParameters();
//	    Map<String, List<String>> parametersMap = queryParameters.getParameters();
//
//	    String token = parametersMap.getOrDefault("token", List.of()).stream().findFirst().orElse(null);
//
//	    // If invalid token, show error and reroute after 5 seconds
//	    if (token == null || !checkTokenValidity(token)) {
//	        showErrorMessage();
//
//	        new Thread(() -> {
//	            try {
//	                Thread.sleep(5000);
//	                UI currentUI = UI.getCurrent();
//	                if (currentUI != null) {
//	                    currentUI.access(() -> currentUI.navigate(LoginView.class));
//	                }
//	            } catch (InterruptedException e) {
//	                e.printStackTrace();
//	            }
//	        }).start();
//
//	        return;
//	    }
//
//	    // ✅ If the token is valid, show the password reset form immediately
//	    removeAll();
//	    buildPasswordResetForm(token);
//	}

    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        String token = parametersMap.getOrDefault("token", List.of())
                                    .stream().findFirst().orElse(null);

        if (token == null || !checkTokenValidity(token)) {
            // ✅ Don't show Notification here — show it after UI renders
            getUI().ifPresent(ui -> ui.access(() -> {
                showErrorMessage();
                ui.getPage().executeJs(
                    "setTimeout(() => window.location.href='/login', 5000)"
                ); // ✅ safer than spawning a raw Thread
            }));
            return;
        }

        removeAll();
        buildPasswordResetForm(token);
    }
    
	
    private void showErrorMessage() {
        removeAll();
        
        VerticalLayout errorCard = new VerticalLayout();
        errorCard.setWidth("50%");
        errorCard.setPadding(true);
        errorCard.setSpacing(true);
        errorCard.setAlignItems(Alignment.CENTER);
        errorCard.getStyle()
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 16px rgba(0,0,0,0.1)")
            .set("background-color", "white")
            .set("padding", "4% !important")
            .set("margin-top", "10%");
        
    	Image imgApmis = new Image("images/apmisheaderbanner.png", "APMIS-LOGO");
//		imgApmis.setClassName("apmis-login-logo");
		Div imageDiv = new Div();
		imageDiv.getStyle().set("display", "flex").set("justify-content", "center").set("width", "240px").set("height", "25%");
		imageDiv.add(imgApmis);
		
		

        H2 errorTitle = new H2("Invalid or Expired Token");
        errorTitle.getStyle().set("color", "#d32f2f");
        errorTitle.getStyle().set("text-align", "center");

        Paragraph errorMessage = new Paragraph(
            "The password reset link is either invalid or has expired. " +
            "You will be redirected to the login page in a few seconds."
        );
        errorMessage.getStyle().set("text-align", "center");
        errorMessage.getStyle().set("color", "#666");
        errorMessage.getStyle().set("font-size", "16px");

        errorCard.add(imageDiv , errorTitle, errorMessage);
        add(errorCard);
    }

	
    private void buildPasswordResetForm(String token) {
        removeAll();
        
        VerticalLayout card = new VerticalLayout();
        card.setWidth("45%");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 16px rgba(0,0,0,0.1)")
            .set("background-color", "white")
            .set("padding", "4% !important")
            .set("height", "60%");
        
    	Image imgApmis = new Image("images/apmisheaderbanner.png", "APMIS-LOGO");
//		imgApmis.setClassName("apmis-login-logo");
		Div imageDiv = new Div();
		imageDiv.getStyle().set("display", "flex").set("justify-content", "center").set("width", "240px").set("height", "25%");
		imageDiv.add(imgApmis);
		

        H2 title = new H2("Reset Your Password");
        title.getStyle().set("color", "#0D6938");

        newPasswordField = new PasswordField("New Password");
        newPasswordField.setWidthFull();
        newPasswordField.setPlaceholder("Enter your new password");

        confirmPasswordField = new PasswordField("Confirm New Password");
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setPlaceholder("Confirm your new password");

        Button updatePasswordButton = new Button("Update Password");
        updatePasswordButton.setWidthFull();
        updatePasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        updatePasswordButton.addClickListener(ex -> {
			if (checkTokenValidity(token)) {
				try {
					UserDto foundUserDetails = FacadeProvider.getUserFacade().getUserByResetToken(token);

					handleResetPassword(foundUserDetails.getUuid(), newPasswordField.getValue(), confirmPasswordField.getValue());
				} catch (Exception exc) {
					Notification.show("Error Resetting Password");
				}
			}
		});
    	
//        updatePasswordButton.addClickListener(e -> handleResetPassword(token));

        card.add(imageDiv, title, newPasswordField, confirmPasswordField, updatePasswordButton);
        add(card);
    }


	public void UpdatePasswordView(String token) {
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		getStyle().set("background-color", "#f7f9fb");

		// --- Create a Card Container ---
		VerticalLayout card = new VerticalLayout();
		card.setWidth("45%");
		card.setPadding(true);
		card.setSpacing(true);
		card.setAlignItems(Alignment.CENTER);
		card.getStyle().set("border-radius", "12px").set("box-shadow", "0 4px 16px rgba(0,0,0,0.1)")
				.set("background-color", "white").set("padding", "4% !important").set("height", "60%");

		// --- Title ---
		H2 title = new H2("Reset Your Password");
		title.getStyle().set("color", "#0D6938");

		// --- Fields ---

		customPasswordField = new TextField("New Password");
		confirmPasswordFieldx = new TextField("Confirm New Password");

		Button updateNewPassword = new Button("Update Password");
		updateNewPassword.setWidthFull();
		updateNewPassword.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		updateNewPassword.addClickListener(ex -> {
			if (checkTokenValidity(token)) {
				try {
					UserDto foundUserDetails = FacadeProvider.getUserFacade().getUserByResetToken(token);

					handleResetPassword(foundUserDetails.getUuid(), customPasswordField.getValue(), confirmPasswordField.getValue());
				} catch (Exception exc) {
					Notification.show("Error Resetting Password");
				}
			}
		});
 
		card.add(title, customPasswordField, confirmPasswordField, updateNewPassword);
		add(card);
	}
	
	private boolean checkTokenValidity(String token) {

		try {
			UserDto foundUserDetails = FacadeProvider.getUserFacade().getUserByResetToken(token);
			LocalDateTime currentDateTime = LocalDateTime.now();

			if (foundUserDetails != null && (foundUserDetails.getUserName() != null
					&& foundUserDetails.getUserEmail() != null)) {

				if (FacadeProvider.getUserFacade().isTokenStillValid(token, currentDateTime)) {
					return true;

				} else {
					Notification.show("Expired token.");
					return false;

				}
			} else {
				Notification.show("Token Does not Exist. Contact System Administrator");
				return false;

			}
		} catch (Exception exception) {
			Notification.show("An error occurred while validating token");
			exception.printStackTrace();
			return false;

		}
	}
	
	private void handleResetPassword(String userUuid, String newPassword, String confirmPassword) {
	    if (isInputValid(userUuid, newPassword, confirmPassword)) {
	        try {
	            FacadeProvider.getUserFacade().setCustomPassword(userUuid, newPassword);
	            Notification.show("Password reset successfully! Redirecting to login...", 
	                3000, Notification.Position.MIDDLE);
	            
	            //Redirect to login after reset
	            getUI().ifPresent(ui -> ui.access(() ->
	                ui.getPage().executeJs(
	                    "setTimeout(() => window.location.href='/login', 3000)"
	                )
	            ));
	        } catch (Exception e) {
	            Notification.show("An error occurred: " + e.getMessage(), 
	                3000, Notification.Position.MIDDLE);
	        }
	    }
	}
	
//	
//	private void handleResetPassword(String userUuid, String customPasswordField, String confirmPasswordField) {
//		// Validate inputs
//		if (isInputValid(userUuid, customPasswordField, confirmPasswordField)) {
//			try {				
//	            FacadeProvider.getUserFacade().setCustomPassword(userUuid, customPasswordField);
//
//				Notification.show("Password Reset Sucessfully.");
//			} catch (Exception e) {
//				Notification.show("An error occurred while resetting the password: " + e.getMessage(), 3000,
//						Notification.Position.MIDDLE);
//			}
//		}
//	}

	private boolean isInputValid(String userUuid, String customPassword, String confirmPassword) {
//		if (userUuid == null || userUuid.isEmpty()) {
//			Notification.show("User UUID cannot be empty", 3000, Notification.Position.MIDDLE);
//			return false;
//		}
		if (customPassword == null || customPassword.isEmpty()) {
			Notification.show("Password cannot be empty", 3000, Notification.Position.MIDDLE);
			return false;
		}
		if (!customPassword.equals(confirmPassword)) {
			Notification.show("Passwords do not match", 3000, Notification.Position.MIDDLE);
			return false;
		}
		if (customPassword.length() < 8) {
			Notification.show("Password must be at least 8 characters long", 3000, Notification.Position.MIDDLE);
			return false;
		}
		return true;
	}
}