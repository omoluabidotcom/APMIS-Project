package com.cinoteck.application.views.campaign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;

import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;

import com.vaadin.flow.component.textfield.IntegerField;

public class CampaignDashboardGridElementComponent extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5040277864446152755L;
	List<CampaignDashboardElement> savedElements;
	List<CampaignDashboardElement> allElements;
	Grid<CampaignDashboardElement> grid = new Grid<>(CampaignDashboardElement.class, false);
	CampaignDto campaignDto;
	private CampaignDashboardElement formBeenEdited;
	private String campaignPhase;
	CampaignDashboardElement newCampForm;
	List<CampaignDashboardElement> formSet = new ArrayList<>();

	FormLayout formx = new FormLayout();
	ComboBox<CampaignDashboardElement> charts = new ComboBox<CampaignDashboardElement>();
	List<String> tempListTabId = new ArrayList<String>();
	List<String> tempListSubTabId = new ArrayList<String>();
	ComboBox<String> tabID = new ComboBox<String>();
	ComboBox<String> subTabID = new ComboBox<String>();
	IntegerField tabWidth = new IntegerField();
	IntegerField tabHeight = new IntegerField();
	IntegerField tabOrder = new IntegerField();
	VerticalLayout vert = new VerticalLayout();
	Button saveButton = new Button(I18nProperties.getCaption(Captions.actionSave), new Icon(VaadinIcon.CHECK));

	Button cacleButton = new Button(I18nProperties.getCaption(Captions.actionCancel), new Icon(VaadinIcon.REFRESH));

	Binder<CampaignDashboardElement> dashboardElementBinder = new BeanValidationBinder<>(
			CampaignDashboardElement.class);
	
	protected List<CampaignDiagramDefinitionDto> campaignDiagramDefinitionDtos_;
	protected Map<String, String> diagramIdCaptionMap_;
	

	public CampaignDashboardGridElementComponent(List<CampaignDashboardElement> savedElements,
			List<CampaignDashboardElement> allElements, CampaignDto campaignDto, String campaignPhase) {
		this.savedElements = savedElements;
		this.allElements = allElements;
		this.campaignDto = campaignDto;
		this.campaignPhase = campaignPhase;

		
		
		campaignDiagramDefinitionDtos_ = FacadeProvider
				.getCampaignDiagramDefinitionFacade().getAll().stream()
				.filter(e -> e.getFormType().equalsIgnoreCase(campaignPhase)).collect(Collectors.toList());
		diagramIdCaptionMap_ = campaignDiagramDefinitionDtos_.stream().collect(Collectors
				.toMap(CampaignDiagramDefinitionDto::getDiagramId, CampaignDiagramDefinitionDto::getDiagramCaption));
		
		
		if (campaignDto == null) {
			campaignDto = new CampaignDto();
		}

		grid.addColumn(this::getDiagramCaption).setHeader(I18nProperties.getCaption(Captions.chart)).setAutoWidth(true)
				.setResizable(true);

		grid.addColumn(CampaignDashboardElement::getTabId)
				.setHeader(I18nProperties.getCaption(Captions.campaignDashboardTabName)).setAutoWidth(true)
				.setResizable(true).setSortable(true);
		grid.addColumn(CampaignDashboardElement::getSubTabId)
				.setHeader(I18nProperties.getCaption(Captions.campaignDashboardSubTabName)).setAutoWidth(true)
				.setResizable(true).setSortable(true);
		grid.addColumn(CampaignDashboardElement::getWidth)
				.setHeader(I18nProperties.getCaption(Captions.campaignDashboardChartWidth));
		grid.addColumn(CampaignDashboardElement::getHeight)
				.setHeader(I18nProperties.getCaption(Captions.campaignDashboardChartHeight));
		grid.addColumn(CampaignDashboardElement::getOrder)

				.setHeader(I18nProperties.getCaption(Captions.campaignDashboardOrder)).setSortable(true);
		
		
		
		
//		for (CommunityReferenceDto item : items) {
//			item.setCaption(item.getNumber() != null ? item.getNumber().toString() : item.getCaption());
//		}
		
		
		Collections.sort(savedElements, 
				CampaignDashboardElement.sortOrderByAge); 
		
		grid.setItems(savedElements);

//		grid.sort(CampaignDashboardElement::getOrder, SortDirection.ASCENDING);
		// setSortOrder(List<GridSortOrder<CampaignDashboardElement>>, boolean)
//		grid.setSortOrder(new SortOrder<>(grid.getColumnByKey("order"), SortDirection.ASCENDING));

		
		
		addClassName("list-view");
		setSizeFull();
		add(getContent());
	}
	
	

	private String getDiagramCaption(CampaignDashboardElement item) {
		return getItemCaption(item, diagramIdCaptionMap_);
	}

	private Component getContent() {
		VerticalLayout formx = editorForm();
//		formx.setId("formControls1");
		formx.getStyle().remove("width");
		HorizontalLayout content = new HorizontalLayout(grid, formx);
//		content.setId("formControls1");
		content.setFlexGrow(4, grid);
		content.setFlexGrow(0, formx);
		content.addClassNames("content");
		content.setSizeFull();
		return content;
	}

	private VerticalLayout editorForm() {

//		setId("formControls2");

		Button plusButton = new Button(new Icon(VaadinIcon.PLUS));
		plusButton.addThemeVariants(ButtonVariant.LUMO_ICON);

		plusButton.setTooltipText(I18nProperties.getString(Strings.addNewForm));

		Button deleteButton = new Button(new Icon(VaadinIcon.DEL_A));
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON);
		deleteButton.getStyle().set("background-color", "red!important");
		deleteButton.setTooltipText(I18nProperties.getString(Strings.removeThisForm));

		final List<CampaignDiagramDefinitionDto> campaignDiagramDefinitionDtos = FacadeProvider
				.getCampaignDiagramDefinitionFacade().getAll().stream()
				.filter(e -> e.getFormType().equalsIgnoreCase(campaignPhase)).collect(Collectors.toList());

		final Map<String, String> diagramIdCaptionMap = campaignDiagramDefinitionDtos.stream().collect(Collectors
				.toMap(CampaignDiagramDefinitionDto::getDiagramId, CampaignDiagramDefinitionDto::getDiagramCaption));

		charts.setLabel(I18nProperties.getCaption(Captions.chart));
		charts.setItems(allElements);
		charts.setItemLabelGenerator(item -> getItemCaption(item, diagramIdCaptionMap));
		// if its a clicked action set the value from the item....TODO

		if (campaignDto != null && campaignDto.getCampaignDashboardElements() != null) {
			for (CampaignDashboardElement elex : campaignDto.getCampaignDashboardElements(campaignPhase))
				tempListTabId.add(elex.getTabId());
		} else if (campaignDto != null && campaignDto.getCampaignDashboardElements() != null) {
			tempListTabId.add(charts.getValue().getTabId());
		} else {
			if (charts.getValue() != null) {
				tempListTabId.add(charts.getValue().getTabId());

			}

		}

		if (campaignDto != null && campaignDto.getCampaignDashboardElements() != null) {
			for (CampaignDashboardElement elex : campaignDto.getCampaignDashboardElements(campaignPhase))
				tempListSubTabId.add(elex.getSubTabId());
		} else if (campaignDto != null && campaignDto.getCampaignDashboardElements() != null) {
			tempListSubTabId.add(charts.getValue().getSubTabId());
		} else {

		}

		tabID.setLabel(I18nProperties.getCaption(Captions.campaignDashboardTabName));
		tabID.setItems(tempListTabId);
		tabID.setAllowCustomValue(true);
		tabID.addCustomValueSetListener(e -> {
			String customValue = e.getDetail();
			tempListTabId.add(customValue);
			tabID.setItems(tempListTabId);
			tabID.setValue(customValue);
		});

		subTabID.setLabel(I18nProperties.getCaption(Captions.campaignDashboardSubTabName));
		subTabID.setItems(tempListSubTabId);
		subTabID.setAllowCustomValue(true);
		subTabID.addCustomValueSetListener(e -> {
			String customValue = e.getDetail();
			tempListSubTabId.add(customValue);
			subTabID.setItems(tempListSubTabId);
			subTabID.setValue(customValue);
		});

		tabWidth.setLabel(I18nProperties.getCaption(Captions.campaignDashboardChartWidth));
		tabWidth.setMin(10);
		tabWidth.setMax(100);
		tabWidth.setStep(5);
		tabWidth.setStepButtonsVisible(true);

		tabHeight.setLabel(I18nProperties.getCaption(Captions.campaignDashboardChartHeight));
		tabHeight.setMin(10);
		tabHeight.setMax(100);
		tabHeight.setStep(5);
		tabHeight.setStepButtonsVisible(true);

		tabOrder.setLabel(I18nProperties.getCaption(Captions.campaignDashboardOrder));
		tabOrder.setMin(0);
		tabOrder.setMax(100);
		tabOrder.setStepButtonsVisible(true);

		HorizontalLayout buttonLay = new HorizontalLayout(plusButton, deleteButton);

		// buttonLay.setEnabled(false);

		HorizontalLayout buttonAfterLay = new HorizontalLayout(saveButton, cacleButton);
		buttonAfterLay.getStyle().set("flex-wrap", "wrap");
		buttonAfterLay.setJustifyContentMode(JustifyContentMode.END);
		buttonLay.setSpacing(true);

//		dashboardElementBinder.forField(charts)
//	    .bind(CampaignDashboardElement::getDiagramId, CampaignDashboardElement::setDiagramId);

		grid.addSelectionListener(ee -> {

			int size = ee.getAllSelectedItems().size();
			if (size > 0) {
				CampaignDashboardElement selectedCamp = ee.getFirstSelectedItem().get();
				formBeenEdited = selectedCamp;
				boolean isSingleSelection = size == 1;
				buttonLay.setEnabled(isSingleSelection);
				buttonAfterLay.setEnabled(isSingleSelection);

				formx.setVisible(true);
				buttonAfterLay.setVisible(true);

				charts.setValue(selectedCamp);
				tabID.setValue(selectedCamp.getTabId());
				subTabID.setValue(selectedCamp.getSubTabId());
				tabWidth.setValue(selectedCamp.getWidth());
				tabHeight.setValue(selectedCamp.getHeight());
				tabOrder.setValue(selectedCamp.getOrder());

				saveButton.setText(I18nProperties.getCaption(Captions.actionSave));
			} else {
				formBeenEdited = new CampaignDashboardElement();
			}
		});

		deleteButton.addClickListener(dex -> {
			if (formBeenEdited == null) {
				Notification.show(I18nProperties.getString(Strings.pleaseSelectFormFirst));
			} else {

				campaignDto.getCampaignDashboardElements().remove(formBeenEdited);
				// FacadeProvider.getCampaignFacade().saveCampaign(capdto);
				Notification.show(formBeenEdited + I18nProperties.getString(Strings.wasRemovedFromCampaign));
				grid.setItems(campaignDto.getCampaignDashboardElements(campaignPhase));
			}
			grid.setItems(campaignDto.getCampaignDashboardElements(campaignPhase));
		});

		plusButton.addClickListener(ce -> {
			CampaignDashboardElement newcampform = new CampaignDashboardElement();

			formx.setVisible(true);
			buttonAfterLay.setVisible(true);

			try {
				charts.setValue(newcampform);
			} finally {
//				 tabID.setItems(newcampform.getTabId());
//					subTabID.setItems(newcampform.getSubTabId());

				tabID.setValue("");
				subTabID.setValue("");
				tabWidth.setValue(0);
				tabHeight.setValue(0);
				tabOrder.setValue(0);
			}
			if (campaignDto == null) {

			} else {
				grid.setItems(campaignDto.getCampaignDashboardElements(campaignPhase));
			}
			grid.setHeight("auto !important");
		});

		cacleButton.addClickListener(ees -> {
			CampaignDashboardElement newcampform = new CampaignDashboardElement();

			formx.setVisible(false);
			buttonAfterLay.setVisible(false);

			try {
				charts.setValue(newcampform);
			} finally {

				tabID.setValue("");
				subTabID.setValue("");
				tabWidth.setValue(0);
				tabHeight.setValue(0);
				tabOrder.setValue(0);
			}
			saveButton.setText(I18nProperties.getCaption(Captions.actionSave));

			grid.setItems(campaignDto.getCampaignDashboardElements(campaignPhase));
			grid.setHeight("");
		});

		saveButton.addClickListener(e -> {

			if (((Button) e.getSource()).getText().equals("Save")) {
				// TODO we need validator on the items before we accept them to database because
				// we are not using binder...
				newCampForm = charts.getValue();
				newCampForm.setTabId(tabID.getValue());
				newCampForm.setSubTabId(subTabID.getValue());
				newCampForm.setWidth(tabWidth.getValue());
				newCampForm.setHeight(tabHeight.getValue());
				newCampForm.setOrder(tabOrder.getValue());

				if (campaignDto == null) {
					campaignDto = new CampaignDto();
					System.out.println(campaignDto.getCampaignFormMetas() + "dtooooooooooooooooooooooooooooo");
					formSet.add(newCampForm);
					campaignDto.setCampaignDashboardElements(formSet);
					System.out.println(campaignDto.getCampaignFormMetas() + "dtooooooooooooooooooooooooooooo");

				}
				campaignDto.getCampaignDashboardElements().add(newCampForm);

				// FacadeProvider.getCampaignFacade().saveCampaign(capdto);

				allElements.removeAll(campaignDto.getCampaignDashboardElements());
				charts.setItems(allElements);

				Notification.show(I18nProperties.getString(Strings.newDashboardChartSuccess));
				grid.setItems(campaignDto.getCampaignDashboardElements(campaignPhase));
			} else {
				// formBeenEdited
				if (formBeenEdited != null) {
					CampaignDashboardElement newCampForm = charts.getValue();
					newCampForm.setTabId(tabID.getValue());
					newCampForm.setSubTabId(subTabID.getValue());
					newCampForm.setWidth(tabWidth.getValue());
					newCampForm.setHeight(tabHeight.getValue());
					newCampForm.setOrder(tabOrder.getValue());

					campaignDto.getCampaignDashboardElements().remove(formBeenEdited);
					campaignDto.getCampaignDashboardElements().add(newCampForm);
					// FacadeProvider.getCampaignFacade().saveCampaign(capdto);
					grid.setItems(campaignDto.getCampaignDashboardElements(campaignPhase));

					allElements.removeAll(campaignDto.getCampaignDashboardElements());
					charts.setItems(allElements);

					Notification.show(I18nProperties.getString(Strings.headingCampaignDashboard));
				} else {
					Notification.show(I18nProperties.getString(Strings.pleaseSelectFormUpdate));
				}
			}
			grid.setHeight("");
		});
		HorizontalLayout newLayout = new HorizontalLayout(tabWidth, tabHeight, tabOrder);

		formx.add(charts, tabID, subTabID, newLayout);

		formx.setColspan(charts, 2);
		formx.setColspan(tabID, 2);
		formx.setColspan(subTabID, 2);
		formx.setColspan(newLayout, 2);
//		formx.setColspan(tabWidth, 1);
//		formx.setColspan(tabHeight, 1);
//		formx.setColspan(tabOrder, 1);

		formx.setVisible(false);
		buttonAfterLay.setVisible(false);

		vert.add(buttonLay, formx, buttonAfterLay);

		return vert;
	}

	private String getItemCaption(CampaignDashboardElement item, Map<String, String> diagramIdCap) {
		String finalLabel = "";

		String lab = diagramIdCap.get(item.getDiagramId());

		if (lab != null) {
			finalLabel = lab;
		}

		return finalLabel;
	}

	public CampaignDto getModifiedDto() {

		return campaignDto;
	}

	 public List<CampaignDashboardElement> getGridData() {
	        return grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
	    }
}