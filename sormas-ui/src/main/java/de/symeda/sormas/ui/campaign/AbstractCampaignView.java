/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.campaign;

import static de.symeda.sormas.ui.UiUtil.permitted;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignDataView;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignFormDataView;
import de.symeda.sormas.ui.campaign.campaigns.CampaignView;
import de.symeda.sormas.ui.campaign.campaigns.CampaignsView;
import de.symeda.sormas.ui.campaign.campaignstatistics.CampaignStatisticsView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public abstract class AbstractCampaignView extends AbstractSubNavigationView<Component> {

	public static final String ROOT_VIEW_NAME = "campaign";

	protected AbstractCampaignView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) { 
		menu.removeAllViews();
//
//		menu.addView(
//				CampaignStatisticsView.VIEW_NAME,
//				I18nProperties.getPrefixCaption("View", CampaignStatisticsView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
//				params);
		menu.addView(
			CampaignDataView.VIEW_NAME,
			I18nProperties.getPrefixCaption("View", CampaignDataView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
			params);
		if (permitted(FeatureType.CAMPAIGNS, UserRight.CAMPAIGN_EDIT)) {
		menu.addView(
			CampaignsView.VIEW_NAME,
			I18nProperties.getPrefixCaption("View", CampaignsView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
			params);
		}
	}

	public static void registerViews(Navigator navigator) {
//		navigator.addView(CampaignStatisticsView.VIEW_NAME, CampaignStatisticsView.class);
		navigator.addView(CampaignDataView.VIEW_NAME, CampaignDataView.class);
		navigator.addView(CampaignFormDataView.VIEW_NAME, CampaignFormDataView.class);
		if (permitted(FeatureType.CAMPAIGNS, UserRight.CAMPAIGN_EDIT)) {
			navigator.addView(CampaignsView.VIEW_NAME, CampaignsView.class);
		}
		navigator.addView(CampaignView.VIEW_NAME, CampaignView.class);
	}

}
