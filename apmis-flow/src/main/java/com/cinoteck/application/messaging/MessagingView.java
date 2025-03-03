package com.cinoteck.application.messaging;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.cinoteck.application.UserProvider;
import com.cinoteck.application.views.MainLayout;
import com.cinoteck.application.views.useractivitysummary.CampaignDataEditActivityView;
import com.cinoteck.application.views.useractivitysummary.FormManagerActivityLog;
import com.cinoteck.application.views.useractivitysummary.ImportActivitySummary;
import com.cinoteck.application.views.useractivitysummary.LoginReportView;
import com.cinoteck.application.views.useractivitysummary.UserModuleActionSummaryView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.messaging.FCMDto;
import de.symeda.sormas.api.messaging.FCMResponseDto;
import de.symeda.sormas.api.messaging.MessageCriteria;
import de.symeda.sormas.api.messaging.MessageDto;
import de.symeda.sormas.api.messaging.MessageTemplateDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;

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
