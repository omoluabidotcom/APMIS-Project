package com.cinoteck.application.messaging;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.cinoteck.application.UserProvider;
import com.cinoteck.application.views.utils.gridexporter.GridExporter;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.messaging.MessageCategory;
import de.symeda.sormas.api.messaging.MessageCriteria;
import de.symeda.sormas.api.messaging.MessageDto;
import de.symeda.sormas.api.messaging.MessageTemplateCriteria;
import de.symeda.sormas.api.messaging.MessageTemplateDto;
import de.symeda.sormas.api.user.UserActivitySummaryDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserType;

@Route(layout = MessagingView.class)
public class MessageTemplateManager extends VerticalLayout implements RouterLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7709701585682941692L;

	private MessageTemplateDto messageTemplateDto;

	private Button newPreWrittenMessage;
	private Button enterBulkEditMode;
	private Button leaveBulkEditMode;

	private Button archive;
	private Button dearchive;

	private TextField search;
	private ComboBox<MessageCategory> messageCategory;
	private DatePicker startDatePicker;
	private DatePicker endDatePicker;
	private ComboBox<Boolean> archiveFilter;

	private UserProvider userProvider = new UserProvider();
	private HorizontalLayout filterLayout = new HorizontalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout filters = new HorizontalLayout();

	private Grid<MessageTemplateDto> grid = new Grid<>(MessageTemplateDto.class, false);
	private GridListDataView<MessageTemplateDto> dataView;

	private MessageTemplateDataProvider messageTemplateDataProvider = new MessageTemplateDataProvider();
	private ConfigurableFilterDataProvider<MessageTemplateDto, Void, MessageTemplateCriteria> filterDataProvider;

	private MessageTemplateCriteria messageTemplateCriteria = new MessageTemplateCriteria();

	private ConfirmDialog confirmDialog = new ConfirmDialog();

	public MessageTemplateManager() {

		this.setSizeFull();
		this.setHeightFull();
		this.setWidthFull();
		this.addClassName("notificationview");

		filterDataProvider = messageTemplateDataProvider.withConfigurableFilter();

		configureGrid();

		newPreWrittenMessage = new Button("New Message Template");
		enterBulkEditMode = new Button("Enter Bulk Edit Mode");
		enterBulkEditMode.setText("Enter Bulk Edit Mode");

		leaveBulkEditMode = new Button("Leave Bulk Edit Mode");
		leaveBulkEditMode.setText("Leave Bulk Edit Mode");
		leaveBulkEditMode.setVisible(false);

		archive = new Button("Archive");
		archive.setVisible(false);
		dearchive = new Button("Dearchive");
		dearchive.setVisible(false);

		search = new TextField("Search");
		search.setClearButtonVisible(true);

		search.setValueChangeMode(ValueChangeMode.EAGER);

		messageCategory = new ComboBox<>("Message category");
		messageCategory.setItems(MessageCategory.values());
		messageCategory.setClearButtonVisible(true);

		startDatePicker = new DatePicker();
		startDatePicker.setLabel("From Date");
		startDatePicker.setPlaceholder("Start Date");
		startDatePicker.setClearButtonVisible(true);

		endDatePicker = new DatePicker();
		endDatePicker.setLabel("To Date");
		endDatePicker.setPlaceholder("End Date");
		endDatePicker.setClearButtonVisible(true);
		
		archiveFilter = new ComboBox<>("Archive Status");
		archiveFilter.setClearButtonVisible(true);
		archiveFilter.setItems(true, false);

		buttonLayout.getStyle().set("margin-left", "10px");
		buttonLayout.setAlignItems(Alignment.END);
		buttonLayout.add(newPreWrittenMessage, enterBulkEditMode, leaveBulkEditMode, archive, dearchive);

		filters.getStyle().set("margin-left", "10px");
		filters.setAlignItems(Alignment.END);
		filterLayout.add(search, messageCategory, startDatePicker, endDatePicker, archiveFilter);
		filterLayout.setAlignItems(Alignment.START);
		filters.add(filterLayout);

		search.addValueChangeListener(e -> {

			if (e.getValue().toString() != null) {
				messageTemplateCriteria.freeText(e.getValue().toString());
				filterDataProvider.setFilter(messageTemplateCriteria);

				filterDataProvider.refreshAll();
			} else {
				messageTemplateCriteria.freeText(null);
				filterDataProvider.setFilter(messageTemplateCriteria);

				filterDataProvider.refreshAll();
			}
		});

		messageCategory.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				messageTemplateCriteria.setMessageCategory(e.getValue());
				filterDataProvider.setFilter(messageTemplateCriteria);

				filterDataProvider.refreshAll();
			} else {
				messageTemplateCriteria.setMessageCategory(null);
				filterDataProvider.setFilter(messageTemplateCriteria);

				filterDataProvider.refreshAll();
			}
		});

		enterBulkEditMode.addClickListener(e -> {

			grid.setSelectionMode(Grid.SelectionMode.MULTI);
			leaveBulkEditMode.setVisible(true);
			archive.setVisible(true);
			dearchive.setVisible(true);
			enterBulkEditMode.setVisible(false);
		});

		leaveBulkEditMode.addClickListener(e -> {

			grid.setSelectionMode(Grid.SelectionMode.SINGLE);
			leaveBulkEditMode.setVisible(false);
			archive.setVisible(false);
			dearchive.setVisible(false);
			enterBulkEditMode.setVisible(true);
		});

		archive.addClickListener(e -> {
			archiveMessageTemplate();
		});
		
		dearchive.addClickListener(e -> {
			dearchiveMessageTemplate();
		});

		startDatePicker.addValueChangeListener(e -> {
			LocalDate startDate = e.getValue();
			LocalDate endDate = endDatePicker.getValue();
			applyDateFilter(startDate, endDate);
		});

		endDatePicker.addValueChangeListener(e -> {
			LocalDate endDate = e.getValue();
			LocalDate startDate = startDatePicker.getValue();
			applyDateFilter(startDate, endDate);
		});

		archiveFilter.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				messageTemplateCriteria.setArchived(e.getValue());
				filterDataProvider.setFilter(messageTemplateCriteria);

				filterDataProvider.refreshAll();
			} else {
				messageTemplateCriteria.setMessageCategory(null);
				filterDataProvider.setFilter(messageTemplateCriteria);

				filterDataProvider.refreshAll();
			}
		});
		
		newPreWrittenMessage.addClickListener(e -> {

			messageTemplateDto = new MessageTemplateDto();
			newMessageTemplateLayout(messageTemplateDto);
		});

		add(buttonLayout, filters, grid);
	}

	private void applyDateFilter(LocalDate startDate, LocalDate endDate) {

		Timestamp startTimestamp = (startDate != null) ? Timestamp.valueOf(startDate.atStartOfDay()) : null;
		System.out.println("startDate.atStartOfDay() " + startDate.atStartOfDay() + " startTimestamp " + startTimestamp);
//		Timestamp endTimestamp = (endDate != null) ? Timestamp.valueOf(endDate.atTime(LocalTime.MAX)) : null;
		Timestamp endTimestamp = (endDate != null) 
			    ? Timestamp.valueOf(ZonedDateTime.of(endDate, LocalTime.MAX, ZoneId.systemDefault()).toLocalDateTime())
			    : null;
		System.out.println("endDate.atTime(LocalTime.MAX) " + startDate.atStartOfDay() + " endTimestamp " + endTimestamp);

		MessageTemplateCriteria criteria = new MessageTemplateCriteria();
		criteria.setStartDate(startTimestamp);
		criteria.setEndDate(endTimestamp);

		filterDataProvider.setFilter(criteria);

		filterDataProvider.refreshAll();
	}

	void archiveMessageTemplate() {

		if (grid.getSelectedItems().size() == 0) {

			Notification notification = Notification.show("Please select a Message Template");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			notification.setPosition(Notification.Position.MIDDLE);
			notification.open();
		} else {
			confirmDialog.setHeader("Archive Message Template");

			confirmDialog.setText("You are about to Archive " + grid.getSelectedItems().size() + " Message Template");
			confirmDialog.setCloseOnEsc(false);
			confirmDialog.setCancelable(true);
			confirmDialog.addCancelListener(e -> confirmDialog.close());

			confirmDialog.setRejectable(true);
			confirmDialog.setRejectText("Cancel");
			confirmDialog.addRejectListener(e -> confirmDialog.close());

			confirmDialog.setConfirmText("Archive");
			confirmDialog.addConfirmListener(e -> archivingMessageTemplate(grid.getSelectedItems()));
			confirmDialog.open();
		}
	}
	
	void dearchiveMessageTemplate() {

		if (grid.getSelectedItems().size() == 0) {

			Notification notification = Notification.show("Please select a Message Template");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			notification.setPosition(Notification.Position.MIDDLE);
			notification.open();
		} else {
			confirmDialog.setHeader("Darchive Message Template");

			confirmDialog.setText("You are about to Dearchive " + grid.getSelectedItems().size() + " Message Template");
			confirmDialog.setCloseOnEsc(false);
			confirmDialog.setCancelable(true);
			confirmDialog.addCancelListener(e -> confirmDialog.close());

			confirmDialog.setRejectable(true);
			confirmDialog.setRejectText("Cancel");
			confirmDialog.addRejectListener(e -> confirmDialog.close());

			confirmDialog.setConfirmText("Dearchive");
			confirmDialog.addConfirmListener(e -> dearchivingMessageTemplate(grid.getSelectedItems()));
			confirmDialog.open();
		}
	}

	public void archivingMessageTemplate(Collection<MessageTemplateDto> selectedRows) {

		if (selectedRows.size() == 0) {

			Notification notification = Notification.show("Please select a Message Template");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			notification.setPosition(Notification.Position.MIDDLE);
			notification.open();
		} else {
			List<String> uuids = selectedRows.stream().map(MessageTemplateDto::getUuid).collect(Collectors.toList());
			FacadeProvider.getMessageFacade().archivingMessageTemplate(uuids);

			Notification notification = Notification.show("Selected Message Template have been Archived");
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			notification.setPosition(Notification.Position.MIDDLE);
			notification.open();
			
			UI.getCurrent().getPage().reload();
		}
	}
	
	public void dearchivingMessageTemplate(Collection<MessageTemplateDto> selectedRows) {

		if (selectedRows.size() == 0) {

			Notification notification = Notification.show("Please select a Message Template");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			notification.setPosition(Notification.Position.MIDDLE);
			notification.open();
		} else {
			List<String> uuids = selectedRows.stream().map(MessageTemplateDto::getUuid).collect(Collectors.toList());
			FacadeProvider.getMessageFacade().dearchivingMessageTemplate(uuids);

			Notification notification = Notification.show("Selected Message Template have been Dearchived");
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			notification.setPosition(Notification.Position.MIDDLE);
			notification.open();
			
			UI.getCurrent().getPage().reload();
		}
	}

	public void configureGrid() {

		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);

		TextRenderer<MessageTemplateDto> creationDateRenderer = new TextRenderer<>(dto -> {
			Date timestamp = dto.getCreationDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return dateFormat.format(timestamp);
		});

		TextRenderer<MessageTemplateDto> changeDateRenderer = new TextRenderer<>(dto -> {
			Date timestamp = dto.getChangeDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return dateFormat.format(timestamp);
		});
		
		ComponentRenderer<Checkbox, MessageTemplateDto> archiveRenderer = new ComponentRenderer<>(input -> {
			boolean value = input.isArchived();
			Checkbox checkbox = new Checkbox();

			if (value == true)
				checkbox.setValue(true);
			return checkbox;
		});

		grid.addColumn(archiveRenderer).setHeader("Archive Status").setResizable(true)
		.setClassNameGenerator(item -> "archiveColumn-style");
		grid.addColumn(MessageTemplateDto.MESSAGE_CONTENT).setHeader("Message Content").setSortable(true)
				.setResizable(true);
		grid.addColumn(MessageTemplateDto.MESSAGE_CATEGORY).setHeader("Message category").setSortable(true)
				.setResizable(true);		
		grid.addColumn(creationDateRenderer).setHeader("Creatipon Date").setSortable(true).setResizable(true);
		grid.addColumn(changeDateRenderer).setHeader("Change Date").setSortable(true).setResizable(true);
		grid.addColumn(MessageTemplateDto.CREATED_BY).setHeader("Created By").setSortable(true).setResizable(true);

		grid.setVisible(true);
		grid.setWidthFull();
		grid.setAllRowsVisible(true);

		grid.setDataProvider(filterDataProvider);
		if (userProvider.hasUserRight(UserRight.CAMPAIGN_EDIT)) {

			grid.asSingleSelect().addValueChangeListener(event -> editMessageTemplateLayout(event.getValue()));
		}
	}

	public void newMessageTemplateLayout(MessageTemplateDto messageTemplateDto) {

		MessageTemplateLayout messageTemplateLayout = new MessageTemplateLayout(messageTemplateDto, true);
		messageTemplateLayout.setMessageTemplate(messageTemplateDto);

		messageTemplateLayout.addSaveListener(event -> {
			try {
				saveMessageTemplate(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		Dialog dialog = new Dialog();
		dialog.add(messageTemplateLayout);
		dialog.setHeaderTitle("New Message Template");
//		dialog.setSizeFull();
		dialog.open();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
		dialog.setModal(true);
		dialog.setHeight("500px");
		dialog.setWidth("600px");
		dialog.setClassName("new-message-template");
	}

	public void editMessageTemplateLayout(MessageTemplateDto messageTemplateDto) {

		MessageTemplateLayout messageTemplateLayout = new MessageTemplateLayout(messageTemplateDto, true);
		messageTemplateLayout.setMessageTemplate(messageTemplateDto);

		messageTemplateLayout.addSaveListener(event -> {
			try {
				saveMessageTemplate(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		messageTemplateLayout.addDeleteListener(event -> {
			try {
				deleteMessageTemplate(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Dialog dialog = new Dialog();
		dialog.add(messageTemplateLayout);
		dialog.setHeaderTitle("Edit Message Template");
//		dialog.setSizeFull();
		dialog.open();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
		dialog.setModal(true);
		dialog.setHeight("500px");
		dialog.setWidth("600px");
		dialog.setClassName("edit-message-template");
	}

	private void deleteMessageTemplate(MessageTemplateLayout.DeleteEvent event) {
		FacadeProvider.getMessageFacade().deleteMessage(event.getMessage());
	}

	public void saveMessageTemplate(MessageTemplateLayout.SaveEvent event) throws Exception {
		FacadeProvider.getMessageFacade().saveMessage(event.getMessage());
	}

}
