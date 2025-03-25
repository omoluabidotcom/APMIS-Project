package com.cinoteck.application.messaging;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import de.symeda.sormas.api.i18n.I18nProperties;


@PageTitle("APMIS-Notification")
@Route(value = "Notification", layout = MainLayout.class)
public class MessagingView extends VerticalLayout implements RouterLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1327683446669581511L;

	private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();

	public MessagingView() {
		

		setSpacing(false);
		 HorizontalLayout messageTabsheetLayout = new HorizontalLayout();
		 messageTabsheetLayout.setClassName("messageTabSheetLayout");
		 
		 Tabs tabs = createTabs();
			tabs.setSizeFull();
			tabs.getStyle().set("background", "#434343");
	        Div contentContainer = new Div();
	        contentContainer.setSizeFull();
	        setSizeFull();
	        tabs.addSelectedChangeListener(e -> {
	            contentContainer.removeAll();
	            contentContainer.add(tabComponentMap.get(e.getSelectedTab()));
	        });
	        
	        contentContainer.add(tabComponentMap.get(tabs.getSelectedTab()));
	        messageTabsheetLayout.add(tabs);
	        add(messageTabsheetLayout,contentContainer);
	
	}

	private Tabs createTabs() {
		tabComponentMap.put(new Tab(I18nProperties.getCaption("Pre-Written Messages")),new MessageTemplateManager());
		tabComponentMap.put(new Tab(I18nProperties.getCaption("Sent Messages")),new SentMessageView());
		
		return new Tabs(tabComponentMap.keySet().toArray(new Tab[] {}));
	}
	
}
