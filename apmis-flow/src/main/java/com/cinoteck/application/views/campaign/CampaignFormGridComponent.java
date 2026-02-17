package com.cinoteck.application.views.campaign;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.cinoteck.application.UserProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaExpiryDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaWithExpReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.ValueProvider;

public class CampaignFormGridComponent extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5040277864446152755L;
	List<CampaignFormMetaReferenceDto> savedCampaignFormMetas;
	List<CampaignFormMetaReferenceDto> allCampaignFormMetas;
	Grid<CampaignFormMetaReferenceDto> grid = new Grid<>(CampaignFormMetaReferenceDto.class, false);
	CampaignDto capaingDto;
	private CampaignFormMetaReferenceDto formBeenEdited;
	private String campaignPhase;
	List<CampaignFormMetaReferenceDto> allElements;
	private UserProvider userProvider = new UserProvider();
	private String userLanguage;
	FormLayout formx = new FormLayout();

	CampaignDto ExpcapaingDto;
	List<CampaignFormMetaReferenceDto> savedCampaignFormMetasExpiry;

	public CampaignFormGridComponent(List<CampaignFormMetaReferenceDto> savedCampaignFormMetas,
			List<CampaignFormMetaReferenceDto> allCampaignFormMetas, CampaignDto capaingDto, String campaignPhase) {
		this.savedCampaignFormMetas = savedCampaignFormMetas;
		this.allCampaignFormMetas = allCampaignFormMetas;
		this.capaingDto = capaingDto;
		this.campaignPhase = campaignPhase;
		this.userLanguage = userLanguage;
		this.ExpcapaingDto = capaingDto;

		userLanguage = userProvider.getUser().getLanguage().toString();

		if (userLanguage.equalsIgnoreCase("pashto")) {
			grid.addColumn(CampaignFormMetaReferenceDto::getFormname_ps_af)
					.setHeader(I18nProperties.getCaption(Captions.formname) + "Pashto");
		} else if (userLanguage.equalsIgnoreCase("Dari")) {
			grid.addColumn(CampaignFormMetaReferenceDto::getFormname_fa_af)
					.setHeader(I18nProperties.getCaption(Captions.formname) + "Dari ");
		} else if (userLanguage.equalsIgnoreCase("english")) {
			grid.addColumn(CampaignFormMetaReferenceDto::getCaption)
					.setHeader(I18nProperties.getCaption(Captions.formname));
		}

//		grid.addColumn(CampaignFormMetaReferenceDto::getDaysExpired)
//				.setHeader(I18nProperties.getCaption(Captions.expiry) + " (default)");

		grid.addColumn(CampaignFormMetaReferenceDto::getFormVersion)
				.setHeader(I18nProperties.getCaption("Form Version (may not update until saved)"));

		grid.addColumn(this::getDaysExpiredEditable)
				.setHeader(I18nProperties.getCaption(Captions.expiry) + " custom days (may not update until saved)");

		grid.setItems(savedCampaignFormMetas);
		addClassName("list-view");
		setSizeFull();
		add(getContent(capaingDto, savedCampaignFormMetas));

	}

	private int getDaysExpiredEditable(CampaignFormMetaReferenceDto item) {

//		return FacadeProvider.getCampaignFacade().getCampaignFormExp(item.getUuid(), capaingDto.getUuid());
		return FacadeProvider.getCampaignFacade().getCampaignFormExp(item.getUuid(), capaingDto.getUuid());

	}

	private Component getContent(CampaignDto capaingDto, List<CampaignFormMetaReferenceDto> savedCampaignFormMetas) {
//		System.out.println(capaingDto + "0------HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
		if (capaingDto != null) {
			VerticalLayout formx = editorForm(capaingDto, savedCampaignFormMetas);

			this.ExpcapaingDto = capaingDto;
			this.savedCampaignFormMetasExpiry = savedCampaignFormMetas;

			formx.getStyle().remove("width");
			HorizontalLayout content = new HorizontalLayout(grid, formx);
			content.setFlexGrow(4, grid);
			content.setFlexGrow(0, formx);
			content.addClassNames("content");
			content.setSizeFull();
			return content;
		} else {
			VerticalLayout saveCampaignFirstLayout = new VerticalLayout();

			return saveCampaignFirstLayout;
		}

	}

	private VerticalLayout editorForm(CampaignDto capaingDto,
			List<CampaignFormMetaReferenceDto> savedCampaignFormMetas) {

		VerticalLayout vert = new VerticalLayout();

		Button plusButton = new Button(new Icon(VaadinIcon.PLUS));
		plusButton.addThemeVariants(ButtonVariant.LUMO_ICON);
		plusButton.setTooltipText(I18nProperties.getCaption(Captions.addNewForm));

		Button deleteButton = new Button(new Icon(VaadinIcon.DEL_A));
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON);
		deleteButton.getStyle().set("background-color", "red!important");
		deleteButton.setTooltipText(I18nProperties.getCaption(Captions.removeThisForm));

		Button saveButton = new Button(I18nProperties.getCaption(Captions.actionAdd), new Icon(VaadinIcon.CHECK));

		Button cacleButton = new Button(I18nProperties.getCaption(Captions.actionCancel), new Icon(VaadinIcon.REFRESH));

		ComboBox<CampaignFormMetaReferenceDto> forms = new ComboBox<CampaignFormMetaReferenceDto>();
		forms.setLabel(I18nProperties.getCaption(Captions.campaignCampaignForm));

		Map<String, List<CampaignFormMetaReferenceDto>> formsByIdentifier = new HashMap<>();
		for (CampaignFormMetaReferenceDto form : allCampaignFormMetas) {
			String formId = form.getFormGroupUuid();
			if (!formsByIdentifier.containsKey(formId)) {
				formsByIdentifier.put(formId, new ArrayList<>());
			}
			formsByIdentifier.get(formId).add(form);
		}

		// Process forms that have multiple versions
		for (List<CampaignFormMetaReferenceDto> formVersions : formsByIdentifier.values()) {
			if (formVersions.size() > 1) {
				for (CampaignFormMetaReferenceDto form : formVersions) {
					String currentCaption = form.getCaption();
					String formVersion = form.getFormVersion() + "";
					form.setCaption(currentCaption + " V" + formVersion);
				}
			}
		}

		forms.setItems(allCampaignFormMetas);

		IntegerField daysExpire = new IntegerField();
		daysExpire.setLabel(I18nProperties.getCaption(Captions.daysTOExpiry));
		String datd = "";

		if (capaingDto == null) {
			forms.addValueChangeListener(e -> {
				daysExpire.clear();
				daysExpire
						.setValue(FacadeProvider.getCampaignFacade().getDefaultCampaignFormExp(e.getValue().getUuid()));

			});
		} else {

			forms.addValueChangeListener(e -> {
				boolean isFormUUidInList = false;
				for (CampaignFormMetaReferenceDto formName : savedCampaignFormMetas) {
					if (formName.getUuid().equalsIgnoreCase(e.getValue().getUuid())) {
						isFormUUidInList = true;
						break;
					}
				}

				if (isFormUUidInList) {
					daysExpire.clear();
					daysExpire.setValue(

//							FacadeProvider.getCampaignFacade().getCampaignFormExp(e.getValue().getUuid(),capaingDto.getUuid() ));
							FacadeProvider.getCampaignFacade().getCampaignFormExp(e.getValue().getUuid(),
									capaingDto.getUuid()));

				} else {

					daysExpire.clear();
					daysExpire.setValue(
							FacadeProvider.getCampaignFacade().getDefaultCampaignFormExp(e.getValue().getUuid()));
				}

			});

		}
		;

		if (capaingDto != null && capaingDto.getStartDate() != null) {
			datd = capaingDto.getStartDate().toLocaleString();
		} else if (capaingDto != null && capaingDto.getStartDate() == null) {

		}
		daysExpire.setHelperText(I18nProperties.getString(Strings.max60DaysFromStartDate) + " (" + datd + ")");
		daysExpire.setMin(1);
		daysExpire.setMax(60);
		daysExpire.setStepButtonsVisible(true);
		// if its a clicked action set the value from the item....TODO

		HorizontalLayout buttonLay = new HorizontalLayout(plusButton, deleteButton);

		// buttonLay.setEnabled(false);

		HorizontalLayout buttonAfterLay = new HorizontalLayout(saveButton, cacleButton);
		buttonAfterLay.getStyle().set("flex-wrap", "wrap");
		buttonAfterLay.setJustifyContentMode(JustifyContentMode.END);
		buttonLay.setSpacing(true);

		deleteButton.addClickListener(dex -> {
			if (formBeenEdited == null) {
				Notification.show(I18nProperties.getString(Strings.pleaseSelectFormFirst));
			} else {

				capaingDto.getCampaignFormMetas().remove(formBeenEdited);
				// FacadeProvider.getCampaignFacade().saveCampaign(capdto);
				Notification.show(formBeenEdited + I18nProperties.getString(Strings.wasRemovedFromCampaign));
				grid.setItems(capaingDto.getCampaignFormMetas());
			}
			grid.setItems(capaingDto.getCampaignFormMetas(campaignPhase));
		});

		plusButton.addClickListener(ce -> {
			CampaignFormMetaReferenceDto newcampform = new CampaignFormMetaReferenceDto();

			formx.setVisible(true);
			buttonAfterLay.setVisible(true);

			try {
				forms.setValue(newcampform);
			} finally {
				saveButton.setText(I18nProperties.getCaption(Captions.actionAdd));
				daysExpire.setValue(5);
			}
			grid.setItems(capaingDto.getCampaignFormMetas(campaignPhase));
			grid.setHeight("auto !important");

		});

		cacleButton.addClickListener(ees -> {
			CampaignFormMetaReferenceDto newcampform = new CampaignFormMetaReferenceDto();
			formx.setVisible(false);
			buttonAfterLay.setVisible(false);
			forms.setValue(newcampform);
			saveButton.setText(I18nProperties.getCaption(Captions.actionSave));
			daysExpire.setValue(0);
			grid.setItems(capaingDto.getCampaignFormMetas(campaignPhase));
			grid.setHeight("");
		});

		saveButton.addClickListener(e -> {

			if (((Button) e.getSource()).getText().equalsIgnoreCase("Add")
					|| ((Button) e.getSource()).getText().equalsIgnoreCase("Add?")) {
				CampaignFormMetaReferenceDto newCampForm = forms.getValue();

				UUID uuid = UUID.randomUUID();
				String fullUUID = uuid.toString(); // Get full UUID with dashes
				String truncatedUUID = fullUUID.substring(0, Math.min(fullUUID.length(), 36));

				String uuidWithHyphens = truncatedUUID.toString();

				Date startDate = null;
				long differenceInMilliseconds;
				CampaignFormMetaWithExpReferenceDto camFormExp = new CampaignFormMetaWithExpReferenceDto();

				for (CampaignFormMetaReferenceDto cc : savedCampaignFormMetas) {

//					System.out.println("Form Ytpeeeeeeeeeeeee1111111111111111111111111111" + cc.getFormType());
					if (cc.getFormType().equalsIgnoreCase("pre-campaign")) {
						startDate = capaingDto.getPreCampStartDate();
						differenceInMilliseconds = startDate.getTime();

					} else if (cc.getFormType().equalsIgnoreCase("intra-campaign")) {
						startDate = capaingDto.getStartDate();
						differenceInMilliseconds = startDate.getTime();
					} else if (cc.getFormType().equalsIgnoreCase("post-campaign")) {
						startDate = capaingDto.getPostCampStartDate();
						differenceInMilliseconds = startDate.getTime();
					} else {
						startDate = capaingDto.getStartDate();
						differenceInMilliseconds = startDate.getTime();
					}
					Date newDate = new Date(startDate.getTime() + differenceInMilliseconds);
//					System.out.println("DATEEEEEEEEEEEEEEEEEEEE1111111111" + startDate);

					camFormExp.setCampaignId(capaingDto.getUuid());
					camFormExp.setFormId(forms.getValue().getUuid());
					camFormExp.setDaysExpired(daysExpire.getValue().longValue());
					camFormExp.setDate(newDate);
					camFormExp.setUuid(uuidWithHyphens.toUpperCase());

					capaingDto.getCampaignFormMetaExpiry().add(camFormExp);

					FacadeProvider.getCampaignFacade().saveCampaignFormExpiryEntry(camFormExp, capaingDto);

				}

				newCampForm.setCaption(forms.getValue().toString());
				newCampForm.setDaysExpired(daysExpire.getValue());

				capaingDto.getCampaignFormMetas().add(newCampForm);

				capaingDto.setCampaignFormMetaExpiryDto(capaingDto.getCampaignFormMetaExpiry());

				allCampaignFormMetas.removeAll(capaingDto.getCampaignFormMetas());

				forms.setItems(allCampaignFormMetas);

				Notification.show(I18nProperties.getString(Strings.newFormAddedSucces));
				grid.setItems(capaingDto.getCampaignFormMetas(campaignPhase));

				CampaignFormMetaReferenceDto newcampform = new CampaignFormMetaReferenceDto();
				formx.setVisible(false);
				buttonAfterLay.setVisible(false);
				forms.setValue(newcampform);
				saveButton.setText(I18nProperties.getCaption(Captions.actionSave));
				daysExpire.setValue(0);

			} else {

//				System.out.println(((Button) e.getSource()).getText()
//						+ " Text buttton when addd ELSEEEEEEEEEEEEEEEEEEE IFFFFFFFFFF");
				if (formBeenEdited != null) {
					CampaignFormMetaReferenceDto newCampForm = forms.getValue();
					capaingDto.getCampaignFormMetas().remove(formBeenEdited);
					capaingDto.getCampaignFormMetas().add(newCampForm);

					List<CampaignFormMetaWithExpReferenceDto> camFormExp_ = new ArrayList<>();
					CampaignFormMetaReferenceDto newCampFormxx = formBeenEdited;
					String getFormCaption = newCampFormxx.getUuid();

					camFormExp_ = capaingDto.getCampaignFormMetaExpiry().stream()
							.filter(ew -> ew.getUuid() == getFormCaption).collect(Collectors.toList());

//					CampaignFormMetaWithExpReferenceDto camFormExp_i = new CampaignFormMetaWithExpReferenceDto(capaingDto.getUuid(), forms.getValue().getUuid(),
//							daysExpire.getValue().longValue());
//									String uuidWithHyphens = uuid.toString();
					UUID uuid = UUID.randomUUID();
					String fullUUID = uuid.toString(); // Get full UUID with dashes
					String truncatedUUID = fullUUID.substring(0, Math.min(fullUUID.length(), 36));

					String uuidWithHyphens = truncatedUUID.toString();

					Date startDate = null;
					long differenceInMilliseconds;
					CampaignFormMetaWithExpReferenceDto camFormExp_i = new CampaignFormMetaWithExpReferenceDto();

					for (CampaignFormMetaReferenceDto cc : savedCampaignFormMetas) {

//						System.out.println("Form Ytpeeeeeeeeeeeee1111111111111111111111111111" + cc.getFormType());
						if (cc.getFormType().equalsIgnoreCase("pre-campaign")) {
							startDate = capaingDto.getPreCampStartDate();
							differenceInMilliseconds = startDate.getTime();

						} else if (cc.getFormType().equalsIgnoreCase("intra-campaign")) {
							startDate = capaingDto.getStartDate();
							differenceInMilliseconds = startDate.getTime();
						} else if (cc.getFormType().equalsIgnoreCase("post-campaign")) {
							startDate = capaingDto.getPostCampStartDate();
							differenceInMilliseconds = startDate.getTime();
						} else {
							startDate = capaingDto.getStartDate();
							differenceInMilliseconds = startDate.getTime();
						}
						Date newDate = new Date(startDate.getTime() + differenceInMilliseconds);
//						System.out.println("DATEEEEEEEEEEEEEEEEEEEE1111111111" + startDate);

						camFormExp_i.setCampaignId(capaingDto.getUuid());
						camFormExp_i.setFormId(forms.getValue().getUuid());
						camFormExp_i.setDaysExpired(daysExpire.getValue().longValue());
						camFormExp_i.setDate(newDate);
						camFormExp_i.setUuid(uuidWithHyphens.toUpperCase());

						capaingDto.getCampaignFormMetaExpiry().add(camFormExp_i);
						FacadeProvider.getCampaignFacade().saveCampaignFormExpiryEntry(camFormExp_i, capaingDto);

					}

					if (camFormExp_.size() > 0)
						capaingDto.getCampaignFormMetaExpiry().remove(camFormExp_.get(0));

					capaingDto.setCampaignFormMetaExpiryDto(capaingDto.getCampaignFormMetaExpiry());

					grid.setItems(capaingDto.getCampaignFormMetas(campaignPhase));
					getSavedElements();

					Notification.show(I18nProperties.getString(Strings.campaignUpdated));
				} else {

//					System.out.println(
//							((Button) e.getSource()).getText() + " Text buttton when addd ELSEEEEEEEEEEEEEEEEEEE");
					CampaignFormMetaReferenceDto newCampForm = forms.getValue();
					UUID uuid = UUID.randomUUID();
					String fullUUID = uuid.toString(); // Get full UUID with dashes
					String truncatedUUID = fullUUID.substring(0, Math.min(fullUUID.length(), 36));

					String uuidWithHyphens = truncatedUUID.toString();
					Date startDate = null;
					long differenceInMilliseconds;
					CampaignFormMetaWithExpReferenceDto camFormExp = new CampaignFormMetaWithExpReferenceDto();

					for (CampaignFormMetaReferenceDto cc : savedCampaignFormMetas) {

//						System.out.println("Form Ytpeeeeeeeeeeeee1111111111111111111111111111" + cc.getFormType());
						if (cc.getFormType().equalsIgnoreCase("pre-campaign")) {
							startDate = capaingDto.getPreCampStartDate();
							differenceInMilliseconds = startDate.getTime();

						} else if (cc.getFormType().equalsIgnoreCase("intra-campaign")) {
							startDate = capaingDto.getStartDate();
							differenceInMilliseconds = startDate.getTime();
						} else if (cc.getFormType().equalsIgnoreCase("post-campaign")) {
							startDate = capaingDto.getPostCampStartDate();
							differenceInMilliseconds = startDate.getTime();
						} else {
							startDate = capaingDto.getStartDate();
							differenceInMilliseconds = startDate.getTime();
						}
						Date newDate = new Date(startDate.getTime() + differenceInMilliseconds);
//						System.out.println("DATEEEEEEEEEEEEEEEEEEEE1111111111" + startDate);

						camFormExp.setCampaignId(capaingDto.getUuid());
						camFormExp.setFormId(forms.getValue().getUuid());
						camFormExp.setDaysExpired(daysExpire.getValue().longValue());
						camFormExp.setDate(newDate);
						camFormExp.setUuid(uuidWithHyphens.toUpperCase());

						capaingDto.getCampaignFormMetaExpiry().add(camFormExp);
						FacadeProvider.getCampaignFacade().saveCampaignFormExpiryEntry(camFormExp, capaingDto);

					}

//					System.out.println("DATEEEEEEEEEEEEEEEEEEEE333333333" + startDate);

					newCampForm.setCaption(forms.getValue().toString());
					newCampForm.setDaysExpired(daysExpire.getValue());

					capaingDto.getCampaignFormMetas().add(newCampForm);
					capaingDto.getCampaignFormMetaExpiry().add(camFormExp);
					capaingDto.setCampaignFormMetaExpiryDto(capaingDto.getCampaignFormMetaExpiry());
					allCampaignFormMetas.removeAll(capaingDto.getCampaignFormMetas());

					forms.setItems(allCampaignFormMetas);

					Notification.show(I18nProperties.getString(Strings.newFormAddedSucces));
					grid.setItems(capaingDto.getCampaignFormMetas(campaignPhase));
//					Notification.show(I18nProperties.getString(Strings.pleaseSelectaFormBeforeUpdate));
				}

				CampaignFormMetaReferenceDto newcampform = new CampaignFormMetaReferenceDto();
				formx.setVisible(false);
				buttonAfterLay.setVisible(false);
				forms.setValue(newcampform);
				saveButton.setText(I18nProperties.getCaption(Captions.actionSave));
				daysExpire.setValue(0);
			}
			grid.setHeight("");
		});

		formx.add(forms, daysExpire);
		formx.setColspan(forms, 1);
		formx.setColspan(daysExpire, 1);
		formx.setVisible(false);
		buttonAfterLay.setVisible(false);

		vert.add(buttonLay, formx, buttonAfterLay);

		grid.addSelectionListener(ee -> {

			int size = ee.getAllSelectedItems().size();
			if (size > 0) {
				CampaignFormMetaReferenceDto selectedCamp = ee.getFirstSelectedItem().get();
				formBeenEdited = selectedCamp;
				boolean isSingleSelection = size == 1;
				buttonLay.setEnabled(isSingleSelection);
				buttonAfterLay.setEnabled(isSingleSelection);
				formx.setVisible(true);
				buttonAfterLay.setVisible(true);

				// delete.setEnabled(size != 0);
				forms.setValue(selectedCamp);
				saveButton.setText(I18nProperties.getCaption(Captions.update));
				int dayz = getDaysExpiredEditable(selectedCamp);

				daysExpire.clear();
				daysExpire.setPlaceholder(String.valueOf(dayz));
				daysExpire.setValue(daysExpire.getValue() == null ? dayz : selectedCamp.getDaysExpired());

			} else {
				formBeenEdited = new CampaignFormMetaReferenceDto();
			}
		});

		return vert;
	}

	public void savedCampaignFormMetasForExpiry() {
		recalculatedFormExpiryForPhase(ExpcapaingDto);
	}

	public Set<CampaignFormMetaWithExpReferenceDto> recalculatedFormExpiryForPhase(CampaignDto campaignDto) {
		if (campaignDto == null) {
			return null;
		}

//		System.out.println("Recalculating expiry for phase: " + campaignPhase + " with new start date: " + campaignDto);

		// Get all forms currently in this phase's grid
		List<CampaignFormMetaReferenceDto> phaseForms = savedCampaignFormMetasExpiry;

		// Filter forms that actually belong to this phase
		List<CampaignFormMetaReferenceDto> filteredForms = phaseForms.stream()
				.filter(form -> belongsToCurrentPhase(form)).collect(Collectors.toList());

//		System.out.println("Found " + filteredForms.size() + " forms in phase " + campaignPhase);

		// Update expiry only for forms in this phase

		Set<CampaignFormMetaWithExpReferenceDto> recalculatedExpiry = new HashSet<>();
		for (CampaignFormMetaReferenceDto form : filteredForms) {
//			if(			updateFormExpiryCalc(form, campaignDto) != null) {
//				recalculatedExpiry.add(updateFormExpiryCalc(form, campaignDto));
//			};
		}
		return recalculatedExpiry;

	}

	private boolean belongsToCurrentPhase(CampaignFormMetaReferenceDto form) {
		if (form.getFormType() != null) {
			boolean matches = form.getFormType().equalsIgnoreCase(campaignPhase);
			return matches;
		}

		if (savedCampaignFormMetas != null) {
			boolean existsInPhase = savedCampaignFormMetas.stream()
					.anyMatch(savedForm -> savedForm.getUuid().equals(form.getUuid()));
			if (existsInPhase) {
				System.out.println("Form " + form.getCaption() + " exists in saved forms for phase " + campaignPhase);
			}
			return existsInPhase;
		}

		if (form.getCaption() != null) {
			String caption = form.getCaption().toLowerCase();
			String phaseLower = campaignPhase.toLowerCase();

			if (caption.contains(phaseLower) || (phaseLower.contains("pre") && caption.contains("pre"))
					|| (phaseLower.contains("post") && caption.contains("post"))
					|| (phaseLower.contains("intra") && caption.contains("intra"))) {

				return true;
			}
		}

		return false;
	}

	public void recalculateExpiryForPhase(CampaignDto newPhaseStartDate) {
		if (newPhaseStartDate == null) {
			return;
		}

//    System.out.println("Recalculating expiry for phase: " + campaignPhase);

		// Get the actual forms from the grid
		List<CampaignFormMetaReferenceDto> gridForms = getSavedElements();

//    System.out.println("Found " + gridForms.size() + " forms in grid for phase " + campaignPhase);

		// Ensure the campaign has an expiry set
		Set<CampaignFormMetaWithExpReferenceDto> expirySet = newPhaseStartDate.getCampaignFormMetaExpiry();
		if (expirySet == null) {
			expirySet = new HashSet<>();
			newPhaseStartDate.setCampaignFormMetaExpiryDto(expirySet);
		}

		// Clear existing entries for this phase to avoid duplicates
		expirySet.removeIf(exp -> {
			// Keep entries that don't belong to this phase
			CampaignFormMetaReferenceDto form = findFormById(exp.getFormId(), gridForms);
			return form != null && belongsToCurrentPhase(form);
		});

		// Update expiry for each form in the grid
		for (CampaignFormMetaReferenceDto form : gridForms) {
			if (form != null && belongsToCurrentPhase(form)) {
				// Get the actual days from the grid
				int daysFromGrid = getDaysExpiredEditable(form);
				;

				// Make sure daysFromGrid is valid
				if (daysFromGrid <= 0) {
					daysFromGrid = FacadeProvider.getCampaignFacade().getDefaultCampaignFormExp(form.getUuid());
					form.setDaysExpired(daysFromGrid);
				}

//            System.out.println("Processing form: " + form.getCaption() + 
//                              ", ID: " + form.getUuid() + 
//                              ", Days from grid: " + daysFromGrid);

				// Create/update expiry entry
				CampaignFormMetaWithExpReferenceDto expiryDto = createOrUpdateExpiry(form, newPhaseStartDate,
						daysFromGrid);

				// Add to the set
				if (expiryDto != null) {
					expirySet.add(expiryDto);
//                System.out.println("Added expiry for form: " + form.getUuid());
				}
			}
		}

		// Update the campaign with the new expiry set
		newPhaseStartDate.setCampaignFormMetaExpiryDto(expirySet);

//    System.out.println("Total expiry entries after recalculation: " + expirySet.size());

		// Refresh the grid
		if (!gridForms.isEmpty()) {
			grid.getDataProvider().refreshAll();
		}
	}

	private void updateFormExpiry(CampaignFormMetaReferenceDto form, CampaignDto campaignDto, int daysFromGrid) {
		if (form == null || campaignDto == null || capaingDto == null || daysFromGrid <= 0) {
			return;
		}

		// Determine which start date to use based on phase
		Date phaseStartDate = getPhaseStartDate(campaignDto, campaignPhase);
		if (phaseStartDate == null) {
			return;
		}

		// Calculate new expiry date
		long expiryMillis = daysFromGrid * 24L * 60L * 60L * 1000L;
		Date newExpiryDate = new Date(phaseStartDate.getTime() + expiryMillis);

//	    System.out.println( phaseStartDate + "phaseStartDate daysFromGrid" +  daysFromGrid + "newExpiryDate" + newExpiryDate);

		// Get or create the expiry set
		Set<CampaignFormMetaWithExpReferenceDto> expirySet = campaignDto.getCampaignFormMetaExpiry();
		if (expirySet == null) {
			expirySet = new HashSet<>();
			campaignDto.setCampaignFormMetaExpiryDto(expirySet);
		}

		// Find existing expiry entry
		CampaignFormMetaWithExpReferenceDto existingExpiry = expirySet.stream()
				.filter(exp -> Objects.equals(exp.getCampaignId(), capaingDto.getUuid())
//	        		&&
//	                       Objects.equals(exp.getFormId(), form.getUuid()
//	        		)
				).findFirst().orElse(null);

		if (existingExpiry != null) {
			// Update existing entry
			existingExpiry.setDaysExpired((long) daysFromGrid);
			existingExpiry.setDate(newExpiryDate);
			expirySet.add(existingExpiry);

		} else {
			// Create new entry
			CampaignFormMetaWithExpReferenceDto newExpiry = new CampaignFormMetaWithExpReferenceDto();
			String uuid = UUID.randomUUID().toString().toUpperCase();
			newExpiry.setUuid(uuid.substring(0, Math.min(uuid.length(), 36)));
			newExpiry.setCampaignId(capaingDto.getUuid());
			newExpiry.setFormId(form.getUuid());
			newExpiry.setDaysExpired((long) daysFromGrid);
			newExpiry.setDate(newExpiryDate);
			expirySet.add(newExpiry);
		}

		// Update the form's expiry date for display
		form.setDateExpired(newExpiryDate.toString());

		// Make sure the grid value is preserved
		form.setDaysExpired(daysFromGrid);
	}

	private CampaignFormMetaWithExpReferenceDto createOrUpdateExpiry(CampaignFormMetaReferenceDto form,
			CampaignDto campaignDto, int daysFromGrid) {
		if (form == null || campaignDto == null || capaingDto == null || daysFromGrid <= 0) {
			return null;
		}

// Determine which start date to use based on phase
		Date phaseStartDate = getPhaseStartDate(campaignDto, campaignPhase);
		if (phaseStartDate == null) {
			return null;
		}

// Calculate new expiry date
		long expiryMillis = daysFromGrid * 24L * 60L * 60L * 1000L;
		Date newExpiryDate = new Date(phaseStartDate.getTime() + expiryMillis);

// Create new expiry entry
		CampaignFormMetaWithExpReferenceDto expiryDto = new CampaignFormMetaWithExpReferenceDto();
		String uuid = UUID.randomUUID().toString().toUpperCase();

		expiryDto.setUuid(uuid.substring(0, Math.min(uuid.length(), 36)));
		expiryDto.setCampaignId(capaingDto.getUuid());
		expiryDto.setFormId(form.getUuid());
		expiryDto.setDaysExpired((long) daysFromGrid);
		expiryDto.setDate(newExpiryDate);

// Also update the form's display date
		form.setDateExpired(newExpiryDate.toString());

		return expiryDto;
	}

	private CampaignFormMetaReferenceDto findFormById(String formId, List<CampaignFormMetaReferenceDto> forms) {
		if (formId == null || forms == null) {
			return null;
		}

		return forms.stream().filter(f -> formId.equals(f.getUuid())).findFirst().orElse(null);
	}

	private Date getPhaseStartDate(CampaignDto campaignDto, String phase) {
		if ("pre-campaign".equalsIgnoreCase(phase)) {
			return campaignDto.getPreCampStartDate();
		} else if ("intra-campaign".equalsIgnoreCase(phase)) {
			return campaignDto.getStartDate();
		} else if ("post-campaign".equalsIgnoreCase(phase)) {
			return campaignDto.getPostCampStartDate();
		} else {
			return campaignDto.getStartDate();
		}
	}

	// Update the getSavedElements method to ensure it returns actual grid values
	public List<CampaignFormMetaReferenceDto> getSavedElements() {
		List<CampaignFormMetaReferenceDto> gridItems = new ArrayList<>();

		// Get items from the grid data provider
		grid.getDataProvider().fetch(new Query<>()).forEach(gridItems::add);

		return gridItems;
	}

//
//	public List<CampaignFormMetaReferenceDto> getSavedElements() {
//		return grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
//	}

}