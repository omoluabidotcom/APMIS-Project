package com.cinoteck.application.utils.authentication;

import com.cinoteck.application.views.utils.email.SendGridEmailService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserDto;

@Route("sendEmail")
@PageTitle("APMIS-Reset Password")

public class ForgotPasswordView extends VerticalLayout {
    
    public ForgotPasswordView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        
        H2 title = new H2("Reset Password");
        title.getStyle().set("margin-bottom", "20px");
        
        TextField userEmail = new TextField("Email Address");
        userEmail.setWidth("400px");
        userEmail.setPlaceholder("Enter your email address");
        
        Button searchEmail = new Button("Send Reset Link");
        searchEmail.setWidth("400px");
        
        searchEmail.addClickListener(e -> {
            String email = userEmail.getValue();
            
            if (email == null || email.trim().isEmpty()) {
                Notification.show("Please enter your email address", 3000, 
                    Notification.Position.MIDDLE);
                return;
            }
            
            try {
                UserDto user = FacadeProvider.getUserFacade().getByEmail(email.trim());
                
                if (user != null) {
                	
              
                    // Reset password - this triggers PasswordResetEvent which KeycloakService
                    // handles and sends the password reset email automatically
                    String newPassword = FacadeProvider.getUserFacade().resetPassword(user.getUuid());
                    
                    if (newPassword != null) {
                        Notification.show(
                            "Password reset email has been sent to " + email + 
                            ". Please check your inbox.", 
                            5000, 
                            Notification.Position.MIDDLE
                        );
                        userEmail.clear();
                    } else {
                        Notification.show(
                            "Unable to reset password. Please contact support.", 
                            3000, 
                            Notification.Position.MIDDLE
                        );
                    }
                } else {
                    Notification.show("Email does not exist", 3000, 
                        Notification.Position.MIDDLE);
                }
            } catch (Exception ex) {
                Notification.show(
                    "An error occurred: " + ex.getMessage(), 
                    3000, 
                    Notification.Position.MIDDLE
                );
            }
        });
        
        
//        searchEmail.addClickListener(e -> {
//            String email = userEmail.getValue();
//            
//            if (email == null || email.trim().isEmpty()) {
//                Notification.show("Please enter your email address", 3000, 
//                    Notification.Position.MIDDLE);
//                return;
//            }
//            
//            try {
//                UserDto user = FacadeProvider.getUserFacade().getByEmail(email.trim());
//                
//                if (user != null) {
//                    // Reset password - this triggers PasswordResetEvent which KeycloakService
//                    // handles and sends the password reset email automatically
//                    String newPassword = FacadeProvider.getUserFacade().resetPassword(user.getUuid());
//                    
//                    if (newPassword != null) {
//                        Notification.show(
//                            "Password reset email has been sent to " + email + 
//                            ". Please check your inbox.", 
//                            5000, 
//                            Notification.Position.MIDDLE
//                        );
//                        userEmail.clear();
//                    } else {
//                        Notification.show(
//                            "Unable to reset password. Please contact support.", 
//                            3000, 
//                            Notification.Position.MIDDLE
//                        );
//                    }
//                } else {
//                    Notification.show("Email does not exist", 3000, 
//                        Notification.Position.MIDDLE);
//                }
//            } catch (Exception ex) {
//                Notification.show(
//                    "An error occurred: " + ex.getMessage(), 
//                    3000, 
//                    Notification.Position.MIDDLE
//                );
//            }
//        });
        
        add(title, userEmail, searchEmail);
    }
}