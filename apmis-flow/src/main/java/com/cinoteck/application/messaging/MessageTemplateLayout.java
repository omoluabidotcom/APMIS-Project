package com.cinoteck.application.messaging;

import java.sql.Timestamp;
import java.time.Instant;

import com.cinoteck.application.UserProvider;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import de.symeda.sormas.api.messaging.MessageCategory;
import de.symeda.sormas.api.messaging.MessageTemplateDto;

public class MessageTemplateLayout extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7709701585682941692L;

	private MessageTemplateDto messageTemplateDto;

	private H3 messageTemplateHeader = new H3("Message Template Manager");
	private TextField titleField;
	private TextArea messageContent;
	private ComboBox<MessageCategory> messageCategory;
	private Button saved;
	private Button delete;

	private boolean isNew = false;

	Binder<MessageTemplateDto> binder = new BeanValidationBinder<>(MessageTemplateDto.class);
	FormLayout formLayout = new FormLayout();

	UserProvider userProvider = new UserProvider();

	public MessageTemplateLayout(MessageTemplateDto messageTemplateDto_, boolean isNew) {

		this.isNew = isNew;
		if (isNew) {
			MessageTemplateDto messageTemplateDtoNew = new MessageTemplateDto();

			this.messageTemplateDto = messageTemplateDtoNew.build();
		} else {
			this.messageTemplateDto = messageTemplateDto_;
		}

		configureFields();
	}

	public void discardChanges() {
		UI currentUI = UI.getCurrent();
		if (currentUI != null) {
			Dialog dialog = (Dialog) this.getParent().get();
			dialog.close();
		}
	}

	private void configureFields() {

		TextField titleField = new TextField("Title");
		TextArea messageContent = new TextArea("Message Template Content");

		ComboBox<MessageCategory> messageCategory = new ComboBox<MessageCategory>("Message Category");
		messageCategory.setItems(MessageCategory.values());

		binder.forField(messageContent).asRequired("Message Content is Required")
				.bind(MessageTemplateDto::getMessageContent, MessageTemplateDto::setMessageContent);

		binder.forField(messageCategory).bind(MessageTemplateDto::getMessageCategory,
				MessageTemplateDto::setMessageCategory);

		formLayout.add(messageContent, messageCategory);

		final HorizontalLayout hr = new HorizontalLayout();
		formLayout.setColspan(messageContent, 2);
		messageContent.setHeight("250px");

		Icon discardIcon = new Icon(VaadinIcon.CLOSE_CIRCLE_O);
		Button discardChanges = new Button("Discard Changes", discardIcon);

		Icon saveIcon = new Icon(VaadinIcon.CHECK_CIRCLE_O);
		saved = new Button("Save", saveIcon);
		
		Icon deleteIcon = new Icon(VaadinIcon.DEL);
		delete = new Button("Delete", deleteIcon);
		delete.getStyle().set("background-color", "red");
		
		hr.add(discardChanges, saved, delete);
		hr.setJustifyContentMode(JustifyContentMode.START);
		add(formLayout, hr);

		discardChanges.addClickListener(e -> discardChanges());

		deleteIcon.addClickListener(e -> {
			
			MessageTemplateDto messageTemplateDto = new MessageTemplateDto();
			messageTemplateDto = binder.getBean();
			
			fireEvent(new DeleteEvent(this, messageTemplateDto));

			Notification notification = new Notification("Message Template Deleted", 3000, Position.MIDDLE);
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			notification.open();
			UI.getCurrent().getPage().reload();
		});
		
		saved.addClickListener(e -> {
			if (messageContent.getValue() != null && !messageContent.isEmpty()) {
				validateAndSave();
			} else {
				Notification notification = new Notification();
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				notification.setPosition(Position.MIDDLE);
				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(event -> {
					notification.close();
				});

				Paragraph text = new Paragraph("Message cannot be left Empty");

				HorizontalLayout layout = new HorizontalLayout(text, closeButton);
				layout.setAlignItems(Alignment.CENTER);

				notification.add(layout);
				notification.open();
			}
		});
	}

	public void validateAndSave() {

		if (binder.validate().isOk()) {
			messageTemplateDto = binder.getBean();		

			messageTemplateDto.setChgDate(Timestamp.from(Instant.now()));
			messageTemplateDto.setCreatingUser(userProvider.getUser().getUserName());
			fireEvent(new SaveEvent(this, messageTemplateDto));

			Notification notification = new Notification("New Message Template Created", 3000, Position.MIDDLE);
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			notification.open();
			UI.getCurrent().getPage().reload();
		} else {
			Notification notification = new Notification();
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			notification.setPosition(Position.MIDDLE);
			Button closeButton = new Button(new Icon("lumo", "cross"));
			closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			closeButton.getElement().setAttribute("aria-label", "Close");
			closeButton.addClickListener(event -> {
				notification.close();
			});

			Paragraph text = new Paragraph("Unable to Create a Message Template at the Moment");

			HorizontalLayout layout = new HorizontalLayout(text, closeButton);
			layout.setAlignItems(Alignment.CENTER);

			notification.add(layout);
			notification.open();
		}
	}

	public void setMessageTemplate(MessageTemplateDto messageTemplateDto) {
		messageTemplateDto.setCreatingUser(userProvider.getUser().getUserName());
		binder.setBean(messageTemplateDto);
	}

	public static abstract class MessageTemplateEvent extends ComponentEvent<MessageTemplateLayout> {
		private MessageTemplateDto messageTemplateDto;

		protected MessageTemplateEvent(MessageTemplateLayout source, MessageTemplateDto messageTemplateDto) {
			super(source, false);
			this.messageTemplateDto = messageTemplateDto;
		}

		public MessageTemplateDto getMessage() {
			if (messageTemplateDto == null) {
				messageTemplateDto = new MessageTemplateDto();
				return messageTemplateDto;
			} else {
				return messageTemplateDto;
			}
		}
	}

	public static class SaveEvent extends MessageTemplateEvent {
		SaveEvent(MessageTemplateLayout source, MessageTemplateDto messageTemplateDto) {
			super(source, messageTemplateDto);
		}
	}

	public static class DeleteEvent extends MessageTemplateEvent {
		DeleteEvent(MessageTemplateLayout source, MessageTemplateDto messageTemplateDto) {
			super(source, messageTemplateDto);
		}
	}

	public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
		return addListener(SaveEvent.class, listener);
	}

	public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
		return addListener(DeleteEvent.class, listener);
	}

}
