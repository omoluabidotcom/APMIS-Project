package com.cinoteck.application.messaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cinoteck.application.UserProvider;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.messaging.MessageScheduleCriteria;
import de.symeda.sormas.api.messaging.MessageScheduleDto;

public class ScheduledMessageLayout extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7370357967398473680L;

	UserProvider userProvider = new UserProvider();

	private Grid<MessageScheduleDto> grid = new Grid<>(MessageScheduleDto.class, false);

	private GridListDataView<MessageScheduleDto> dataView;

	private MessageScheduleCriteria messageScheduleCriteria;

	public ScheduledMessageLayout() {
		setSizeFull();
		setHeightFull();
		setWidthFull();
		gridConfig();
	}
	
	private String formAccessConfig(MessageScheduleDto messageScheduleDto) {
		String value = messageScheduleDto.getFormAccess().toString();
		return value.replace("[", "").replace("]", "").replace("null,", "").replace("null", "");
	}

	private String rolesConfig(MessageScheduleDto messageScheduleDto) {
		I18nProperties.setUserLanguage(userProvider.getUser().getLanguage());
		String value = messageScheduleDto.getUserRoles().toString();
		return value.replace("[", "").replace("]", "").replace("null,", "").replace("null", "");
	}

	private String areaConfig(MessageScheduleDto messageScheduleDto) {
		String value = messageScheduleDto.getArea().toString();
		return value.replace("[", "").replace("]", "").replace("null,", "").replace("null", "");
	}

	private String regionConfig(MessageScheduleDto messageScheduleDto) {
		String value = messageScheduleDto.getRegion().toString();
		return value.replace("[", "").replace("]", "").replace("null,", "").replace("null", "");
	}

	private String districtConfig(MessageScheduleDto messageScheduleDto) {
		String value = messageScheduleDto.getDistrict().toString();
		return value.replace("[", "").replace("]", "").replace("null,", "").replace("null", "");
	}
	
	private String clusterConfig(MessageScheduleDto messageScheduleDto) {
		String value = messageScheduleDto.getCommunity().toString();
		return value.replace("[", "").replace("]", "").replace("null,", "").replace("null", "");
	}

	private void gridConfig() {

		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);
		
		TextRenderer<MessageScheduleDto> createdDateRenderer = new TextRenderer<>(dto -> {
			Date timestamp = dto.getCreationDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return dateFormat.format(timestamp);
		});

		TextRenderer<MessageScheduleDto> changeDateRenderer = new TextRenderer<>(dto -> {
			Date timestamp = dto.getChangeDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return dateFormat.format(timestamp);
		});		

		grid.addColumn(MessageScheduleDto.MESSAGE_CONTENT).setHeader("Message Content").setResizable(true);
		grid.addColumn(this::rolesConfig).setHeader("Useroles").setResizable(true);
		grid.addColumn(this::formAccessConfig).setHeader("Form Accesses").setResizable(true);
		grid.addColumn(this::areaConfig).setHeader("Regions").setResizable(true);
		grid.addColumn(this::regionConfig).setHeader("Province").setResizable(true);
		grid.addColumn(this::districtConfig).setHeader("District").setResizable(true);
		grid.addColumn(this::clusterConfig).setHeader("Cluster").setResizable(true);		
		grid.addColumn(createdDateRenderer).setHeader("Creation Date").setResizable(true);
//		grid.addColumn(changeDateRenderer).setHeader("Change date").setResizable(true);
		grid.addColumn(MessageScheduleDto.SCHEDULE_TIME).setHeader("Schedule Time").setResizable(true);
		grid.addColumn(MessageScheduleDto.SCHEDULE_DATE).setHeader("Schedule Date").setResizable(true);

		List<MessageScheduleDto> scheduledMessageList = FacadeProvider.getMessageFacade()
				.getIndexListMessageSchedule(messageScheduleCriteria, 0, 100, null);

		ListDataProvider<MessageScheduleDto> dataProvider = DataProvider.fromStream(scheduledMessageList.stream());
		dataView = grid.setItems(dataProvider);	
		add(grid);
	}
	
}
