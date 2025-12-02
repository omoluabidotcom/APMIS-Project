package com.cinoteck.application.utils.authentication;

 
import com.cinoteck.application.views.utils.email.SendGridEmailService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

 import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Route("passwordresetview")
@PageTitle("Reset Password")
@Component

public class ResetPasswordView extends VerticalLayout implements BeforeEnterObserver {

	@Autowired
	private SendGridEmailService mailer;
	
	private final ConfirmDialog passwordConfirmDialog = new ConfirmDialog();
	private final ConfirmDialog tokenEntryDialog = new ConfirmDialog();
	private boolean passwordDialogInitialized = false;
	private boolean tokenDialogInitialized = false;

	private final ConfirmDialog passwordEntryDialog = new ConfirmDialog();
	private boolean newPasswordDialogInitialized = false;

	private TextField resetTokenField;

	TextField customPasswordField = new TextField();
	TextField confirmPasswordField = new TextField();

	public ResetPasswordView() {
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
		Image imgApmis = new Image("images/apmisheaderbanner.png", "APMIS-LOGO");
//		imgApmis.setClassName("apmis-login-logo");
		Div imageDiv = new Div();
		imageDiv.getStyle().set("display", "flex").set("justify-content", "center").set("width", "240px").set("height", "25%");
		imageDiv.add(imgApmis);
		
//		imageDiv.add(imgApmis);		
		H2 title = new H2("Reset Your Password");
		title.getStyle().set("color", "#0D6938");

		// --- Fields ---
		TextField userEmail = new TextField("Email Address");
		userEmail.setWidthFull();
		userEmail.setPlaceholder("Enter your registered email");

		TextField userName = new TextField("Username");
		userName.setWidthFull();
		userName.setPlaceholder("Enter your username");

		// --- Button ---
		Button generateTokenAndSendMail = new Button("Generate Token");
		generateTokenAndSendMail.setWidthFull();
		generateTokenAndSendMail.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

//        Button useToken = new Button("Reset Password Using Token");
//        useToken.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
//        useToken.getStyle().set("color", "#0D6938"); // Optional styling
//        useToken.addClickListener(e -> EnterTokenDialog(null));

		// --- Action ---
		generateTokenAndSendMail.addClickListener(e -> {
			if (userEmail.isEmpty() || userName.isEmpty()) {
				Notification.show("Email and Username cannot be empty.", 3000, Notification.Position.MIDDLE);
				return;
			}

			boolean emailExists = FacadeProvider.getUserFacade().getUserByEmail(userEmail.getValue());
			boolean userExists = FacadeProvider.getUserFacade().getUserByUserNameAndEmail(userName.getValue(),
					userEmail.getValue());

			if (!emailExists) {
				Notification.show("Email does not exist", 3000, Notification.Position.MIDDLE);
				return;
			}

			if (!userExists) {
				Notification.show("Username does not exist", 3000, Notification.Position.MIDDLE);
				return;
			}

			UserDto userDetails = FacadeProvider.getUserFacade().getByUserName(userName.getValue());
			updatePasswordDialog(userDetails);
		});

		// --- Assemble the Card ---
		card.add(imageDiv , title, userEmail, userName, generateTokenAndSendMail);
		add(card);
	}
	
    private SendGridEmailService getMailer() {
        // Get values from application.properties or use defaults
        // These values should match your application.properties
        String apiKey = System.getProperty("sendgrid.api.key", 
            "REDACTED");
        String fromEmail = System.getProperty("mail.from.email", 
            "salamioluwasegun.a@gmail.com");
        String fromName = System.getProperty("mail.from.name", 
            "APMIS Support");
        String appBaseUrl = System.getProperty("app.base.url", 
            "https://afghanistan-apmis.com");
        
        return new SendGridEmailService(apiKey, fromEmail, fromName, appBaseUrl);
    }

	public void updatePasswordDialog(UserDto userDetails) {
		if (!passwordDialogInitialized) {
			passwordConfirmDialog.setHeader("Update Password");
			passwordConfirmDialog.setText("Are you sure you want to update your password?");
			passwordConfirmDialog.setCloseOnEsc(false);
			passwordConfirmDialog.setCancelable(true);
			passwordConfirmDialog.setRejectable(true);
			passwordConfirmDialog.setRejectText("Cancel");

			passwordConfirmDialog.addRejectListener(e -> passwordConfirmDialog.close());

			passwordConfirmDialog.setConfirmText("Update Password");
			passwordConfirmDialog.addConfirmListener(e -> {
				try {
					String token = java.util.UUID.randomUUID().toString();
					LocalDateTime expirationDate = LocalDateTime.now().plusHours(1).plusMinutes(20);// .plusHours(24);

					FacadeProvider.getUserFacade().registerGeneratedToken(expirationDate, token, userDetails);

				
				     SendGridEmailService mailer = getMailer();
	                    mailer.sendPasswordResetToken(userDetails.getUserEmail(), token);

//					mailer.sendPasswordResetToken(userDetails.getUserEmail(), token);

					Notification.show("Token has been generated. Check your email and follow steps to reset password.");
					passwordConfirmDialog.close();
//                    EnterTokenDialog(null);
				} catch (Exception exception) {
					Notification.show("Token was not generated. Contact System Administrators.");
					exception.printStackTrace();
				}
			});

			passwordDialogInitialized = true;
		}

		passwordConfirmDialog.open();
	}

	public void beforeEnter(BeforeEnterEvent event) {
		// Check if token parameter exists in URL
		QueryParameters queryParameters = event.getLocation().getQueryParameters();
		Map<String, List<String>> parametersMap = queryParameters.getParameters();

		if (parametersMap.containsKey("token")) {
			List<String> tokenValues = parametersMap.get("token");
			if (tokenValues != null && !tokenValues.isEmpty()) {
				String token = tokenValues.get(0);
				// Open the token dialog with the token from URL
				// Use UI.getCurrent().access() to ensure we're on the UI thread
				getUI().ifPresent(ui -> ui.access(() -> {
					try {
						if (checkTokenValidity(token)) {
							EnterNewPasswordDialog(token);
						}

					} catch (Exception e) {
						Notification.show("Token could not be validate please contact system administration.");
					}
//                    EnterTokenDialog(token);
				}));
			}
		}
	}

    public void EnterTokenDialog(String prefillToken) {
        if (!tokenDialogInitialized) {
        	
        	tokenEntryDialog.setWidth("25%");
            tokenEntryDialog.setHeader("Enter Reset Token");
            tokenEntryDialog.setCloseOnEsc(false);
            tokenEntryDialog.setCancelable(true);
            tokenEntryDialog.setRejectable(true);
            tokenEntryDialog.setRejectText("Cancel");
            tokenEntryDialog.addRejectListener(e -> tokenEntryDialog.close());

            
            customPasswordField = new TextField("New Password");
            confirmPasswordField = new TextField("");
            
            // Pre-fill token if provided
            if (prefillToken != null && !prefillToken.isEmpty()) {
                resetTokenField.setValue(prefillToken);
                // Optionally auto-validate when token is pre-filled from URL
                // You can remove this if you want user to click the button
                validateAndProcessToken(prefillToken);
            }

            Button updatePassword = new Button("Update Password");
            updatePassword.setWidth("400px");

            updatePassword.addClickListener(e -> {
                String tokenValue = resetTokenField.getValue();
                if (tokenValue == null || tokenValue.isEmpty()) {
                    Notification.show("Please enter a valid token");
                    return;
                }
                validateAndProcessToken(tokenValue);
            });

            VerticalLayout layout = new VerticalLayout(customPasswordField, confirmPasswordField, updatePassword);
            tokenEntryDialog.add(layout);

            tokenDialogInitialized = true;
        } else {
            // If dialog already initialized, update the token field if token is provided
            if (prefillToken != null && !prefillToken.isEmpty() && resetTokenField != null) {
                resetTokenField.setValue(prefillToken);
                validateAndProcessToken(prefillToken);
            }
        }

        tokenEntryDialog.open();
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

	private boolean validateAndProcessToken(String token) {

		try {
			UserDto foundUserDetails = FacadeProvider.getUserFacade().getUserByResetToken(token);
			if (foundUserDetails != null && foundUserDetails.getUserName() != null
					&& foundUserDetails.getUserEmail() != null) {
				makeNewPassword(foundUserDetails.getUuid(), foundUserDetails.getUserEmail(),
						foundUserDetails.getUserName());
				tokenEntryDialog.close();
				return true;
			} else {
				Notification.show("Invalid or expired token");
				return false;

			}
		} catch (Exception exception) {

			Notification.show("An error occurred while validating token");
			exception.printStackTrace();
			return false;

		}
	}

	public void makeNewPassword(String userUuid, String userEmail, String userName) {
		String newPassword = FacadeProvider.getUserFacade().resetPassword(userUuid);

		Dialog newUserPop = new Dialog();
		newUserPop.setClassName("passwordsDialog");
		VerticalLayout infoLayout = new VerticalLayout();

		newUserPop.setHeaderTitle("Password Updated");
		Paragraph infoText = new Paragraph("Please copy this password, it is shown only once.");

		H3 username = new H3("Username: " + userName);
		username.getStyle().set("color", "#0D6938");

		H3 password = new H3("Password: " + newPassword);
		password.getStyle().set("color", "#0D6938");

		infoLayout.add(infoText, username, password);
		newUserPop.add(infoLayout);
		newUserPop.open();
	}

	public void EnterNewPasswordDialog(String prefillToken) {
		if(prefillToken != null ) {
			
		}
		if (!newPasswordDialogInitialized) {

			passwordEntryDialog.setWidth("25%");
			passwordEntryDialog.setHeader("Reset Password");
			passwordEntryDialog.setCloseOnEsc(false);
			passwordEntryDialog.setCancelable(true);
			passwordEntryDialog.setRejectable(true);
			passwordEntryDialog.setRejectText("Cancel");
			passwordEntryDialog.addRejectListener(e -> tokenEntryDialog.close());

			customPasswordField = new TextField("New Password");
			confirmPasswordField = new TextField("Confirm New Password");

			Button updateNewPassword = new Button("Update Password");
			updateNewPassword.setWidth("400px");

			updateNewPassword.addClickListener(ex -> {
				if (checkTokenValidity(prefillToken)) {
					try {
						UserDto foundUserDetails = FacadeProvider.getUserFacade().getUserByResetToken(prefillToken);

						handleResetPassword(foundUserDetails.getUuid(), customPasswordField.getValue(), confirmPasswordField.getValue());
					} catch (Exception exc) {
						Notification.show("Error Resetting Password");
					}
				}
			});

		} else {
			// If dialog already initialized, update the token field if token is provided
//      if (prefillToken != null && !prefillToken.isEmpty() && resetTokenField != null) {
//          resetTokenField.setValue(prefillToken);
//          validateAndProcessToken(prefillToken);
//      }
		}

		passwordEntryDialog.open();

	}

	private void handleResetPassword(String userUuid, String customPasswordField, String confirmPasswordField) {
		// Validate inputs
		if (isInputValid(userUuid, customPasswordField, confirmPasswordField)) {
			try {				
	            FacadeProvider.getUserFacade().setCustomPassword(userUuid, customPasswordField);

				Notification.show("Password Reset Sucessfully);");
			} catch (Exception e) {
				Notification.show("An error occurred while resetting the password: " + e.getMessage(), 3000,
						Notification.Position.MIDDLE);
			}
		}
	}

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