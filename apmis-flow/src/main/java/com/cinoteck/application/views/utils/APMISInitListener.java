package com.cinoteck.application.views.utils;

import com.cinoteck.application.utils.authentication.AccessControl;
import com.cinoteck.application.utils.authentication.AccessControlFactory;
//import com.cinoteck.application.utils.authentication.ForgotPasswordView;
import com.cinoteck.application.utils.authentication.LoginView;
import com.cinoteck.application.utils.authentication.ResetPasswordView;
import com.cinoteck.application.utils.authentication.UpdatePasswordView;
import com.vaadin.flow.router.RouteNotFoundError;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
//import org.vaadin.example.bookstore.authentication.AccessControl;
//import org.vaadin.example.bookstore.authentication.AccessControlFactory;
//import org.vaadin.example.bookstore.ui.login.LoginScreen;

/**
 * This class is used to listen to BeforeEnter event of all UIs in order to
 * check whether a user is signed in or not before allowing entering any page.
 * It is registered in a file named
 * com.vaadin.flow.server.VaadinServiceInitListener in META-INF/services.
 */
public class APMISInitListener implements VaadinServiceInitListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8385570446755288676L;

	@Override
	public void serviceInit(ServiceInitEvent initEvent) {
		final AccessControl accessControl = AccessControlFactory.getInstance().createAccessControl();

		initEvent.getSource().addUIInitListener(uiInitEvent -> {
			uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {

				System.out.println(accessControl.isUserSignedIn() + "+++++++++++++++++++++++++++________________"
						+ enterEvent.getNavigationTarget());

				if (!accessControl.isUserSignedIn()) {
					if (ResetPasswordView.class.equals(enterEvent.getNavigationTarget())) {
						enterEvent.rerouteTo(ResetPasswordView.class);
					}
					else if (UpdatePasswordView.class.equals(enterEvent.getNavigationTarget())) {
						enterEvent.rerouteTo(UpdatePasswordView.class);
					}
					else if (!LoginView.class.equals(enterEvent.getNavigationTarget())) {
						enterEvent.rerouteTo(LoginView.class);
					}

				} else if (accessControl.isUserSignedIn() && LoginView.class.equals(enterEvent.getNavigationTarget())) {

				}
			});
		});
	}
	
//	@Override
//	public void serviceInit(ServiceInitEvent initEvent) {
//		final AccessControl accessControl = AccessControlFactory.getInstance().createAccessControl();
//
//		initEvent.getSource().addUIInitListener(uiInitEvent -> {
//			uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
//
//				System.out.println(accessControl.isUserSignedIn() + "+++++++++++++++++++++++++++________________"
//						+ enterEvent.getNavigationTarget() + " Location: " + enterEvent.getLocation().getPath());
//
//				Class<?> targetView = enterEvent.getNavigationTarget();
//				String location = enterEvent.getLocation().getPath();
//
//				// Allow public access to these views without authentication
//				// Check both by class and by location path
//				if (ResetPasswordView.class.equals(targetView) || 
//				    ForgotPasswordView.class.equals(targetView) || 
//				    LoginView.class.equals(targetView) ||
//				    location.equals("sendEmail") ||
//				    location.startsWith("sendEmail")) {
//					// Allow navigation to proceed - do nothing
//					return;
//				}
//
//				// Handle RouteNotFoundError - if it's for a public route, allow it
//				if (RouteNotFoundError.class.equals(targetView)) {
//					if (location.equals("sendEmail") || location.startsWith("sendEmail")) {
//						// Try to navigate to ForgotPasswordView
//						enterEvent.rerouteTo(ForgotPasswordView.class);
//						return;
//					}
//				}
//
//				// For all other views, require authentication
//				if (!accessControl.isUserSignedIn()) {
//					enterEvent.rerouteTo(LoginView.class);
//				}
//
//				// If user is signed in and trying to access LoginView, handle appropriately
//				if (accessControl.isUserSignedIn() && LoginView.class.equals(targetView)) {
//					// User is already signed in, could redirect to dashboard or allow
//					// Leave empty to allow access to login page even when signed in
//				}
//			});
//		});
//	}
}
