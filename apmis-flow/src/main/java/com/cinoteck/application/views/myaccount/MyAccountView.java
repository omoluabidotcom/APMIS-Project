package com.cinoteck.application.views.myaccount;

import com.cinoteck.application.LanguageSwitcher;
import com.cinoteck.application.UserProvider;
import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Direction;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;

import com.vaadin.flow.component.html.H1;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import com.vaadin.flow.component.notification.Notification;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;

import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;

import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
//import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.flow.server.VaadinSession;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.form.DialingCodeDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;

import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserDto;
//import de.symeda.sormas.ui.utils.InternalPasswordChangeComponent;
//import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.api.user.UserRole;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@PageTitle("APMIS-My Account")
@Route(value = "useraccount", layout = MainLayout.class)

public class MyAccountView extends VerticalLayout implements RouterLayout {
	private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();

	UserProvider userProvider = new UserProvider();

	private int min = 0;
	private int max = 0;
	DialingCodeDto dialingCodeDto = new DialingCodeDto();
	String userDefaultEmail = userProvider.getUser().getUserEmail();

	public MyAccountView() {

		if (I18nProperties.getUserLanguage() == null) {

			I18nProperties.setUserLanguage(Language.EN);
		} else {

			I18nProperties.setUserLanguage(userProvider.getUser().getLanguage());
			I18nProperties.getUserLanguage();
		}
		FacadeProvider.getI18nFacade().setUserLanguage(userProvider.getUser().getLanguage());
		setSpacing(false);
		setPadding(false);
		Binder<UserDto> binder = new BeanValidationBinder<>(UserDto.class);

		List<AreaReferenceDto> regionss;
		List<RegionReferenceDto> provincess;
		List<DistrictReferenceDto> districtss;
		List<CommunityReferenceDto> communitiess;

		UserDto currentUser = FacadeProvider.getUserFacade().getCurrentUser();

		VerticalLayout userentry = new VerticalLayout();
		userentry.getStyle().set("margin-right", "0.5rem");

		H3 infooo = new H3(I18nProperties.getCaption(Captions.User_userName));
		infooo.getStyle().set("color", "green");
		infooo.getStyle().set("font-size", "20px");
		infooo.getStyle().set("font-weight", "600");
		infooo.getStyle().set("margin-left", "20px");
		infooo.getStyle().set("margin-bottom", "15px");

		Paragraph infoood = new Paragraph(currentUser.getUserName());
		infoood.getStyle().set("margin-left", "20px");
		infoood.getStyle().set("margin-bottom", "0px");

		H3 infoo = new H3(I18nProperties.getCaption(Captions.personalInformation));

		infoo.getStyle().set("color", "green");
		infoo.getStyle().set("font-size", "20px");
		infoo.getStyle().set("font-weight", "600");
		infoo.getStyle().set("margin-left", "20px");
		infoo.getStyle().set("margin-bottom", "0px");

		TextField firstnamee = new TextField("");
		firstnamee.setLabel(I18nProperties.getCaption(Captions.firstName));
		firstnamee.setValue(currentUser.getFirstName());
		firstnamee.setId("my-disabled-textfield");
		firstnamee.getStyle().set("-webkit-text-fill-color", "green");
		firstnamee.setReadOnly(true);

		TextField lastnamee = new TextField("");
		lastnamee.setLabel(I18nProperties.getCaption(Captions.lastName));
		lastnamee.setValue(currentUser.getLastName());
		lastnamee.getStyle().set("-webkit-text-fill-color", "green");
		lastnamee.setReadOnly(true);

		TextField emailAddresss = new TextField("");
		emailAddresss.setLabel(I18nProperties.getCaption(Captions.User_userEmail));
		if (currentUser.getUserEmail() == null) {
			emailAddresss.setPlaceholder(I18nProperties.getCaption(Captions.User_userEmail));
		} else {
			emailAddresss.setValue(currentUser.getUserEmail());
		}
		emailAddresss.setReadOnly(true);
		binder.forField(emailAddresss).asRequired(I18nProperties.getString(Strings.emailAddressRequired))
//				.bind(UserDto::getUserEmail, UserDto::setUserEmail);
				.bind(userx -> userx.getUserEmail(), (userx, userEmail) -> userx.setUserEmail(userEmail));

		HorizontalLayout phoneFieldsLayout = new HorizontalLayout();
		ComboBox<String> countryCodeCombo = new ComboBox<String>();
		countryCodeCombo.setLabel("Country");
		List<String> namesListx = new ArrayList<String>();
		for (DialingCodeDto dialingCodeDto : FacadeProvider.getDialingCodeFacade().getAllCountriesDto()) {
			namesListx.add(dialingCodeDto.getCountry());
		}
		countryCodeCombo.setItems(namesListx);
		countryCodeCombo.setVisible(false);

		TextField phoneNumberr = new TextField();
		phoneNumberr.setLabel(I18nProperties.getCaption(Captions.phoneNumber));
		if (currentUser.getPhone() == null) {
			phoneNumberr.setPlaceholder(I18nProperties.getCaption(Captions.phoneNumber));
		} else {
			phoneNumberr.setValue(currentUser.getPhone());
		}
		phoneNumberr.setReadOnly(true);
		phoneNumberr.setWidthFull();

		TextField positionn = new TextField();
		positionn.setLabel(I18nProperties.getCaption(Captions.User_userPosition));

		if (currentUser.getPhone() == null) {
			positionn.setPlaceholder(I18nProperties.getCaption(Captions.User_userPosition));
		} else {
			positionn.setValue(currentUser.getUserPosition());
		}
		positionn.setReadOnly(true);

		TextField organisation = new TextField();
		organisation.setLabel(I18nProperties.getCaption("Organisation"));
		organisation.setReadOnly(true);
		if (currentUser.getUserOrganisation() == null) {
			organisation.setPlaceholder(I18nProperties.getCaption("Organisation"));
		} else {
			organisation.setValue(currentUser.getUserOrganisation());
		}

		phoneFieldsLayout.add(countryCodeCombo, phoneNumberr);
		FormLayout dataVieww = new FormLayout();
		dataVieww.add(firstnamee, lastnamee, emailAddresss, phoneFieldsLayout, positionn, organisation);
		dataVieww.getStyle().set("margin-left", "20px");
		dataVieww.getStyle().set("margin-right", "20px");

		Button editPersonalInfo = new Button();
		Button cancelUpdatePersonalInfo = new Button();
		Button updatePersonalInfo = new Button();
		ComboBox<Language> languagee = new ComboBox<>(I18nProperties.getCaption(Captions.language));

		editPersonalInfo.setText("Edit Personal Information");
		editPersonalInfo.addClickListener(e -> {
			firstnamee.setReadOnly(false);
			lastnamee.setReadOnly(false);
			emailAddresss.setReadOnly(false);
			phoneNumberr.setReadOnly(false);
			countryCodeCombo.setVisible(true);

			editPersonalInfo.setVisible(false);
			cancelUpdatePersonalInfo.setVisible(true);
			updatePersonalInfo.setVisible(true);

			for (DialingCodeDto dialingCodeDto : FacadeProvider.getDialingCodeFacade().getAllCountriesDto()) {
				if (phoneNumberr.getValue().toString().startsWith(dialingCodeDto.getCode())) {
					countryCodeCombo.setValue(dialingCodeDto.getCountry());
					dialingCodeDto = FacadeProvider.getDialingCodeFacade()
							.getCountryByCode(countryCodeCombo.getValue());
					break;
				}
			}
			
			
			phoneNumberr.addValueChangeListener(ex->{
				if (!validatePhone(phoneNumberr.getValue())) {
					Notification.show("Phone Number is Invalid.");
					return;
				}

			});
		
			countryCodeCombo.addValueChangeListener(listener -> {
				if (phoneNumberr.getValue() != null) {
					phoneNumberr.clear();
				}
				dialingCodeDto = FacadeProvider.getDialingCodeFacade().getCountryByCode(listener.getValue());
				int addition = dialingCodeDto.getCode().length() - 1;
				min = FacadeProvider.getDialingCodeFacade().getCountryByCode(listener.getValue()).getMin_length()
						+ addition;
				max = FacadeProvider.getDialingCodeFacade().getCountryByCode(listener.getValue()).getMax_length()
						+ addition;

				phoneNumberr.setValue(
						FacadeProvider.getDialingCodeFacade().getCountryByCode(listener.getValue()).getCode());
				phoneNumberr.setHelperText("Mobile number for " + dialingCodeDto.getCountry() + " must be between "
						+ min + " and " + max + " digits with the country code");
				phoneNumberr.setPattern("^[+]?[0-9]{" + min + "," + max + "}$");
				phoneNumberr.setErrorMessage("Invalid");
			});

		});

		cancelUpdatePersonalInfo.setText("Cancel");
		cancelUpdatePersonalInfo.setVisible(false);
		cancelUpdatePersonalInfo.addClickListener(e -> {
			firstnamee.clear();
			firstnamee.setValue(currentUser.getFirstName());
			lastnamee.clear();
			lastnamee.setValue(currentUser.getLastName());
			emailAddresss.clear();
			emailAddresss.setValue(currentUser.getUserEmail());
			phoneNumberr.clear();
			phoneNumberr.setValue(currentUser.getPhone());
			countryCodeCombo.setVisible(false);

			editPersonalInfo.setVisible(true);
			cancelUpdatePersonalInfo.setVisible(false);
			updatePersonalInfo.setVisible(false);

		});

		updatePersonalInfo.setText("Update Personal Information");
		updatePersonalInfo.setVisible(false);
		updatePersonalInfo.addClickListener(e -> {

			try {
				String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

				if (userDefaultEmail.equalsIgnoreCase(emailAddresss.getValue())) {
					

				} else {
					if (emailAddresss.getValue().matches(emailRegex)) {
						if (!validateEmail(emailAddresss.getValue())) {
							Notification.show("Email Address Exists.");
							return;
						}
					} else {
						Notification.show("Email Address is not valid.");
						return;
					}

				}

				if (!validatePhone(phoneNumberr.getValue())) {
					Notification.show("Phone Number is Invalid.");
					return;
				}

		

			} catch (Exception exception) {

			}finally {
				
				try {
					UserDto currentUserToSave = FacadeProvider.getUserFacade().getCurrentUser();
					if (languagee.getValue() != null) {
						currentUserToSave.setFirstName(firstnamee.getValue());
						currentUserToSave.setLastName(lastnamee.getValue());
						currentUserToSave.setUserEmail(emailAddresss.getValue());
						currentUserToSave.setPhone(phoneNumberr.getValue());

						FacadeProvider.getUserFacade().saveUser(currentUserToSave);

					} else {

						Notification.show(
								I18nProperties.getString(Strings.choosePreferredLanguage) + languagee.isInvalid());
					}

				} catch (Exception exception) {
					Notification.show(I18nProperties
							.getString("Error Updating Personal Information, Please contact Administrator"));

				} finally {
					
					UserDto currentUserx = FacadeProvider.getUserFacade().getCurrentUser();

					firstnamee.clear();
					firstnamee.setValue(currentUserx.getFirstName());
					lastnamee.clear();
					lastnamee.setValue(currentUserx.getLastName());
					emailAddresss.clear();
					emailAddresss.setValue(currentUserx.getUserEmail());
					phoneNumberr.clear();
					phoneNumberr.setValue(currentUserx.getPhone());
					countryCodeCombo.setVisible(false);

					editPersonalInfo.setVisible(true);
					cancelUpdatePersonalInfo.setVisible(false);
					updatePersonalInfo.setVisible(false);
				}
				
				
			}

		});

		Div fieldInfoo = new Div();

		H3 infodataa = new H3(I18nProperties.getCaption("Geographical Attachments"));
		infodataa.getStyle().set("color", "green");
		infodataa.getStyle().set("font-size", "20px");
		infodataa.getStyle().set("font-weight", "600");
		infodataa.getStyle().set("margin-left", "20px");
		infodataa.getStyle().set("margin-bottom", "0px");

		FormLayout fielddataVieww = new FormLayout();
		fielddataVieww.setResponsiveSteps(
				// Use one column by default
				new ResponsiveStep("0", 1),
				// Use two columns, if the layout's width exceeds 320px
				new ResponsiveStep("320px", 2),
				// Use three columns, if the layout's width exceeds 500px
				new ResponsiveStep("500px", 3));

		ComboBox<String> regionn = new ComboBox<>(I18nProperties.getCaption(Captions.area));
		regionn.setLabel(I18nProperties.getCaption(Captions.area));
		if (userProvider.getUser().getArea() != null) {
			regionn.setItems(userProvider.getUser().getArea().getCaption());
			regionn.setValue(userProvider.getUser().getArea().getCaption());
			fielddataVieww.add(regionn);
		} else {
			regionn.setVisible(false);
			infodataa.setVisible(false);
		}
		regionn.setReadOnly(true);

		ComboBox<String> provincee = new ComboBox<>(I18nProperties.getCaption(Captions.region));
		provincee.setLabel(I18nProperties.getCaption(Captions.region));
		provincee.setReadOnly(true);

		if (userProvider.getUser().getRegion() != null) {
			provincee.setItems(userProvider.getUser().getRegion().getCaption());
			provincee.setValue(userProvider.getUser().getRegion().getCaption());
			fielddataVieww.add(provincee);
		} else {

		}

		MultiSelectComboBox<String> districtt = new MultiSelectComboBox<>(I18nProperties.getCaption(Captions.district));
		districtt.setReadOnly(true);

		if (userProvider.getUser().getDistrict() != null || userProvider.getUser().getDistricts().size() > 0) {
			List<String> districts = new ArrayList<>();
			if (userProvider.getUser().getDistricts().size() > 0) {
				for (DistrictReferenceDto caption : userProvider.getUser().getDistricts()) {
					districts.add(caption.getCaption());
				}
				districtt.setItems(districts);
				districtt.setValue(districts);
			} else {
				districtt.setItems(userProvider.getUser().getDistrict().getCaption());
				districtt.setValue(userProvider.getUser().getDistrict().getCaption());
			}
			fielddataVieww.add(districtt);
		} else {
		}

		MultiSelectComboBox<String> cluster = new MultiSelectComboBox<>(I18nProperties.getCaption(Captions.community));
		cluster.setReadOnly(true);

		if (userProvider.getUser().getCommunity().size() > 0) {
			List<String> clusters = new ArrayList<>();
			for (CommunityReferenceDto caption : userProvider.getUser().getCommunity()) {
				clusters.add(caption.getCaption());
			}
			cluster.setItems(clusters);
			cluster.setValue(clusters);

			fielddataVieww.add(cluster);
		} else {

		}

		fielddataVieww.getStyle().set("margin-left", "20px");
		fielddataVieww.getStyle().set("margin-right", "20px");

		TextArea userFormAccesses = new TextArea();
		userFormAccesses.setLabel("User Form Accesses");
		userFormAccesses.setWidthFull();
		userFormAccesses.setClassName("formAccessTextField");
		userFormAccesses.setId("formAccessTextFieldID");
		userFormAccesses.setId("my-disabled-textfield");
		userFormAccesses.getStyle().set("-webkit-text-fill-color", "green");
		userFormAccesses.setReadOnly(true);

		Set<FormAccess> userFormAccessesx = FacadeProvider.getUserFacade().getCurrentUser().getFormAccess();// .getAreaFacade().getAllActiveAsReference();
		if (userFormAccessesx.size() < 1) {

		} else {
			userFormAccesses.setValue(userFormAccessesx.toString().replace("[", "").replace("]", ""));
			userFormAccesses.setTooltipText(userFormAccesses.getValue());
		}

		TextArea userUsersRoles = new TextArea();

		userUsersRoles.setLabel("User Roles");
		userUsersRoles.setWidthFull();
		userUsersRoles.setReadOnly(true);
		userUsersRoles.setId("userRolesTextFieldID");
		userUsersRoles.setClassName("userRolesTextField");
		userUsersRoles.setMinHeight("6vh !important");
		userUsersRoles.setMaxHeight("12vh !important");
		userUsersRoles.getStyle().set("-webkit-text-fill-color", "green");

//		userFormAccesses.setMaxHeight("5vh !important");

		Set<UserRole> userRoles = FacadeProvider.getUserFacade().getCurrentUser().getUserRoles();// .getAreaFacade().getAllActiveAsReference();
//		userUsersRoles.setItems(userRoles);
		if (userRoles.size() < 1) {

		} else {
			userUsersRoles.setValue(userRoles.toString().replace("[", "").replace("]", ""));
			userUsersRoles.setTooltipText(userUsersRoles.getValue());
		}

		FormLayout userAssignmentVieww = new FormLayout();
		userAssignmentVieww.setResponsiveSteps(
				// Use one column by default
				new ResponsiveStep("0", 1),
				// Use two columns, if the layout's width exceeds 320px
				new ResponsiveStep("320px", 1),
				// Use three columns, if the layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		userAssignmentVieww.add(userFormAccesses, userUsersRoles);
		userAssignmentVieww.getStyle().set("margin-left", "20px");
		userAssignmentVieww.getStyle().set("margin-right", "20px");

		H3 security = new H3(I18nProperties.getString(Strings.passwordAccessibility));

		security.getStyle().set("color", "green");
		security.getStyle().set("font-size", "20px");
		security.getStyle().set("font-weight", "600");
		security.getStyle().set("margin-left", "20px");
		security.getStyle().set("margin-bottom", "15px");
		security.getStyle().set("margin-top", "16px !important");

		Dialog passwordDialog = new Dialog();

		Button openPasswordPopupButton = new Button(I18nProperties.getCaption(Captions.changePassword));

		openPasswordPopupButton.addClickListener(event -> {
			CredentialPassWordChanger sev = new CredentialPassWordChanger(currentUser);
			sev.continuePasswrd();
		});
		add();

		VerticalLayout pwdSecc = new VerticalLayout();
		pwdSecc.setClassName("superDiv");

		languagee.setItemLabelGenerator(Language::toString);
		languagee.setItems(Language.getAssignableLanguages());
		languagee.getStyle().set("margin-bottom", "0px");
		languagee.getStyle().set("margin-top", "-15px !important");

		binder.forField(languagee).asRequired(I18nProperties.getString(Strings.languageRequired))
				.bind(UserDto::getLanguage, UserDto::setLanguage);

		languagee.setRequired(true);

		languagee.setValue(currentUser.getLanguage());

		languagee.getStyle().set("width", "400px");

		languagee.getStyle().set("width", "400px");

		Div anch = new Div();
		anch.setClassName("anchDiv");
		pwdSecc.getStyle().set("margin-left", "20px");

		pwdSecc.add(openPasswordPopupButton, languagee, anch);

		Div actionss = new Div();

		Icon vadIcc = new Icon(VaadinIcon.CHECK_CIRCLE_O);
		vadIcc.getStyle().set("color", "green");

		Button discard = new Button(I18nProperties.getCaption(Captions.actionDiscard));

		Icon vadIc = new Icon(VaadinIcon.CLOSE_CIRCLE_O);
		vadIc.setId("fghf");
		vadIc.getStyle().set("color", "green !important");

		discard.getStyle().set("margin-right", "20px");
		discard.getStyle().set("color", "green !important");
		discard.getStyle().set("background", "white");
		discard.getStyle().set("border", "1px solid green");
		discard.setIcon(vadIc);

		Button savee = new Button(I18nProperties.getCaption(Captions.actionSave), vadIcc);
		savee.addClickListener(e -> {
			UserDto currentUserToSave = FacadeProvider.getUserFacade().getCurrentUser();
			if (languagee.getValue() != null) {

				LanguageSwitcher languageSwitcher = new LanguageSwitcher();
				currentUserToSave.setLanguage(languagee.getValue());
				FacadeProvider.getUserFacade().saveUser(currentUserToSave);
				I18nProperties.setUserLanguage(languagee.getValue());
				I18nProperties.getUserLanguage();

				String userLanguage = userProvider.getUser().getLanguage().toString();

				if (userLanguage.equals("Pashto")) {

					languageSwitcher.switchLanguage(new Locale("ps"));
				} else if (userLanguage.equals("Dari")) {

					languageSwitcher.switchLanguage(new Locale("fa"));
				} else {

					languageSwitcher.switchLanguage(Locale.ENGLISH);
				}

				VaadinSession.getCurrent().close();
			} else {

				Notification.show(I18nProperties.getString(Strings.choosePreferredLanguage) + languagee.isInvalid());
			}

		});
		actionss.getStyle().set("margin", "20px");
		HorizontalLayout personalInfoActionButtonsLayout = new HorizontalLayout();
		personalInfoActionButtonsLayout.getStyle().set("margin", "20px");
		personalInfoActionButtonsLayout.add(editPersonalInfo, updatePersonalInfo, cancelUpdatePersonalInfo);
		actionss.add(discard, savee);
		userentry.add(infooo, infoood, infoo, dataVieww, personalInfoActionButtonsLayout, infodataa, fieldInfoo,
				fielddataVieww, userAssignmentVieww, security, pwdSecc, actionss);

		add(userentry);

		passwordDialog.setCloseOnEsc(false);
		passwordDialog.setCloseOnOutsideClick(false);

		FormLayout formLayout = new FormLayout();

		PasswordField newPasswordField = new PasswordField(I18nProperties.getString(Strings.headingNewPassword));
		newPasswordField.setRevealButtonVisible(true);
		PasswordField confirmPasswordField = new PasswordField(I18nProperties.getString(Strings.confirmPassword));
		confirmPasswordField.setRevealButtonVisible(true);

		Label instructionLabel = new Label(I18nProperties.getString(Strings.choosePassword) + "\r\n <br>"
				+ I18nProperties.getString(Strings.mustBeAt8Char) + "\r\n <br>"
				+ I18nProperties.getString(Strings.mustContain1UppercaseChar) + "\r\n" + "");
		instructionLabel.getElement().setProperty("innerHTML", instructionLabel.getText());

		// setting action buttons for password change
		Button cancelButton = new Button(I18nProperties.getCaption(Captions.actionCancel));
		cancelButton.addClickListener(event -> passwordDialog.close());

		Button saveButton = new Button(I18nProperties.getCaption(Captions.actionSave));
		saveButton.addClickListener(event -> {
			// Perform password validation and saving logic here
			passwordDialog.close();
		});

		// setting css for the actions buttons
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.add(cancelButton, new Div(), saveButton); // Add an empty Div for spacing

		cancelButton.addClickListener(event -> passwordDialog.close());

		buttonLayout.getStyle().set("margin-top", "1em");

		passwordDialog.add(formLayout, buttonLayout);

		// setting layout for textfields
		VerticalLayout layout = new VerticalLayout();
		layout.add(newPasswordField);
		layout.add(confirmPasswordField);
		formLayout.add(newPasswordField, confirmPasswordField, instructionLabel);

	}

	public boolean validateEmail(String email) {

		UserDto anyEmailFromDb = FacadeProvider.getUserFacade().getByEmail(email);
		if (anyEmailFromDb == null) {
			return true;
		}
		return false;
	}

	public boolean validatePhone(String phoneNumber) {
		if (phoneNumber != null && !phoneNumber.isEmpty())
			if (phoneNumber.matches("^[+]?[0-9]{" + min + "," + max + "}$")) {
				return true;
			}
		return false;
	}

//	public void phone() {
//		
//		ComboBox<String> availableCountries = new ComboBox<String>();
//		availableCountries.setLabel("Country");
//		List<String> namesListx = new ArrayList<String>();
//		for (DialingCodeDto dialingCodeDto : FacadeProvider.getDialingCodeFacade().getAllCountriesDto()) {
//			namesListx.add(dialingCodeDto.getCountry());
//		}
//		availableCountries.setItems(namesListx);
//		availableCountries.setEnabled(false);
//
//		
//		TextField phoneNumberr = new TextField();
//		phoneNumberr.setLabel(I18nProperties.getCaption(Captions.phoneNumber));
//		if (currentUser.getPhone() == null) {
//			phoneNumberr.setPlaceholder(I18nProperties.getCaption(Captions.phoneNumber));
//		} else {
//			phoneNumberr.setValue(currentUser.getPhone());
//		}
//		phoneNumberr.setReadOnly(true);
//		
//		
//
//		TextField numberField = new TextField();
//		numberField.setLabel("Phone Number");
//		numberField.setClassName("customTextWrap");
//
//		numberField.setValue(currentUser.getPhone());
//		
//
//	
//
//		if (numberField.getValue() == null || (numberField.getValue().toString().isEmpty()) {
//
//			availableCountries.setValue(namesListx.get(0));
//			dialingCodeDto = FacadeProvider.getDialingCodeFacade()
//					.getCountryByCode(availableCountries.getValue());
//			numberField.setValue(FacadeProvider.getDialingCodeFacade()
//					.getCountryByCode(availableCountries.getValue()).getCode());
//		} else {
//
//			for (DialingCodeDto dialingCodeDto : FacadeProvider.getDialingCodeFacade()
//					.getAllCountriesDto()) {
//				if (value.toString().startsWith(dialingCodeDto.getCode())) {
//					System.out.println("dialingCodeDto.getCode() " + dialingCodeDto.getCode());
//					availableCountries.setValue(dialingCodeDto.getCountry());
//					dialingCodeDto = FacadeProvider.getDialingCodeFacade()
//							.getCountryByCode(availableCountries.getValue());
//					break;
//				}
//			}
//			numberField.setValue(value.toString());
//		}
//
//		min = FacadeProvider.getDialingCodeFacade().getCountryByCode(availableCountries.getValue())
//				.getMin_length() + 2;
//		max = FacadeProvider.getDialingCodeFacade().getCountryByCode(availableCountries.getValue())
//				.getMax_length() + 2;
//
//		numberField.setHelperText("Mobile number for "
//				+ FacadeProvider.getDialingCodeFacade().getCountryByCode(availableCountries.getValue())
//						.getCountry()
//				+ " must be between " + min + " and " + max + " digits with the country code");
//
//		numberField.setPattern("^[+]?[0-9]{" + min + "," + max + "}$");
//		numberField.setErrorMessage("Invalid");
//
//		availableCountries.addValueChangeListener(e -> {
//
//			if (numberField.getValue() != null) {
//				numberField.clear();
//			}
//			dialingCodeDto = FacadeProvider.getDialingCodeFacade().getCountryByCode(e.getValue());
//			int addition = dialingCodeDto.getCode().length() - 1;
//			min = FacadeProvider.getDialingCodeFacade().getCountryByCode(e.getValue()).getMin_length()
//					+ addition;
//			max = FacadeProvider.getDialingCodeFacade().getCountryByCode(e.getValue()).getMax_length()
//					+ addition;
//
//			numberField.setValue(
//					FacadeProvider.getDialingCodeFacade().getCountryByCode(e.getValue()).getCode());
//			numberField.setHelperText("Mobile number for " + dialingCodeDto.getCountry()
//					+ " must be between " + min + " and " + max + " digits with the country code");
//			numberField.setPattern("^[+]?[0-9]{" + min + "," + max + "}$");
//			numberField.setErrorMessage("Invalid");
//		});
//
//	}

}