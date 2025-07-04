/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.campaign.edit;

import static android.view.View.GONE;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.Campaign;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.Bundler;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;
import static de.symeda.sormas.app.util.DataUtils.toItems;

public class CampaignFormDataNewActivity extends BaseEditActivity<CampaignFormData> {

    private AsyncTask saveTask;
    private Campaign campaign;
    private CampaignFormMeta campaignFormMeta;

    private CampaignFormDataCriteria criteria = new CampaignFormDataCriteria();

    public static void startActivity(Context context, String campaignUUID, String campaignFormMetaUUID) {
        BaseEditActivity.startActivity(context, CampaignFormDataNewActivity.class,
                BaseEditActivity.buildBundle(null).setCampaignUuid(campaignUUID).setCampaignFormMetaUuid(campaignFormMetaUUID));
    }

    @Override
    public void onCreateInner(@Nullable Bundle savedInstanceState) {
        super.onCreateInner(savedInstanceState);
        campaign = DatabaseHelper.getCampaignDao().queryUuid(new Bundler(savedInstanceState).getCampaignUuid());
        campaignFormMeta = DatabaseHelper.getCampaignFormMetaDao().queryUuid(new Bundler(savedInstanceState).getCampaignFormMetaUuid());
    }

    @Override
    protected CampaignFormData queryRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected CampaignFormData buildRootEntity() {
        CampaignFormData campaignFormData = DatabaseHelper.getCampaignFormDataDao().build();
        return campaignFormData;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, CampaignFormData activityRootData) {
        activityRootData.setCampaign(campaign);
        activityRootData.setCampaignFormMeta(campaignFormMeta);



        BaseEditFragment campaignFormDataNewFragment = CampaignFormDataNewFragment.newInstance(activityRootData);
        campaignFormDataNewFragment.setLiveValidationDisabled(true);
        return campaignFormDataNewFragment;
    }

    @Override
    public void saveData() {
        if (saveTask != null) {
            NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
            return; // don't save multiple times
        }

        final CampaignFormData campaignFormDataToSave = getStoredRootEntity();
        boolean saveChecker = true;
        criteria.setCampaign(campaign);
        criteria.setCampaignFormMeta(campaignFormMeta);




        if(!ConfigProvider.getUser().getUserRoles().contains(UserRole.SURVEILLANCE_OFFICER)) { // District Officer
            criteria.setCommunity(campaignFormDataToSave.getCommunity());
//            criteria.setCommunity(null);
        }else{
            criteria.setCommunity(null);
        }

        List<CampaignFormData> lotchecker = DatabaseHelper.getCampaignFormDataDao().queryByCriteria(criteria, 0, 100);

//        campaignFormDataToSave.setRecordversion(1L);
        campaignFormDataToSave.setFormCategory(campaignFormDataToSave.getCampaignFormMeta().getFormCategory());


        try {
            FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }

        final List<CampaignFormDataEntry> formValues = campaignFormDataToSave.getFormValues();
        final List<CampaignFormDataEntry> filledFormValues = new ArrayList<>();

        CampaignFormDataEntry lotNo = new CampaignFormDataEntry();
        CampaignFormDataEntry lotClusterNo = new CampaignFormDataEntry();

//        formValues.forEach(campaignFormDataEntry ->
        for(CampaignFormDataEntry campaignFormDataEntry : formValues) {

            if (campaignFormDataEntry.getId() != null && campaignFormDataEntry.getValue() != null) {
                String value = campaignFormDataEntry.getValue().toString();
                if (value.endsWith(".0")) {
                    value = value.replaceAll(".0", "");// .replaceALl(".0", "");
                    campaignFormDataEntry.setValue(value);
                }
                filledFormValues.add(campaignFormDataEntry);
                if (campaignFormDataEntry.getId().equalsIgnoreCase("LotNo")) {
                    lotNo = campaignFormDataEntry;
                }
                if (campaignFormDataEntry.getId().equalsIgnoreCase("LotClusterNo")) {
                    lotClusterNo = campaignFormDataEntry;
                }
            }
        };

        for (CampaignFormElement campaignFormElement : campaignFormMeta.getCampaignFormElements()) {
                if (CampaignFormElementType.fromString(campaignFormElement.getType()) == CampaignFormElementType.DATE) {
                    String idtoBeUpdated = campaignFormElement.getId();
                    for(CampaignFormDataEntry campaignFormDataEntry : formValues) {
                        if (idtoBeUpdated.equalsIgnoreCase(campaignFormDataEntry.getId())) {
                            campaignFormDataEntry.setValue(dateFormatterLongAndMobile(campaignFormDataEntry.getValue()));;
                        }
                    }

                }
        }



        List<String> listLotNo = new ArrayList();
        List<String> listLotClusterNo = new ArrayList();

        if (lotchecker.size() > 0) {
            for (CampaignFormData campaignFormDataData : lotchecker) {
                List<CampaignFormDataEntry> lotOwnSec = campaignFormDataData.getFormValues();
                if (lotOwnSec.contains(lotNo)) {
                    listLotNo.add(lotOwnSec.get(lotOwnSec.indexOf(lotNo)).getValue().toString());
                }

                if (lotOwnSec.contains(lotClusterNo) && lotOwnSec.contains(lotNo)) {
                    listLotClusterNo.add(lotOwnSec.get(lotOwnSec.indexOf(lotClusterNo)).getValue().toString());
                }
            }
        }

        for (String string : listLotClusterNo) {
            if (listLotNo.size() > 0) {
            if ((Long.parseLong(string) - Long.parseLong(lotClusterNo.getValue().toString()) == 0)
                        && (Long.parseLong(listLotNo.get(0))
                        - Long.parseLong(lotNo.getValue().toString()) == 0)
            ) {
                saveChecker = false;
                break;
            }
            }
        }
        campaignFormDataToSave.setFormValues(filledFormValues);


        CampaignFormDataNewFragment activeFragment = (CampaignFormDataNewFragment) getActiveFragment();
        activeFragment.setLiveValidationDisabled(false);

        if (saveChecker) {
            saveTask = new SavingAsyncTask(getRootView(), campaignFormDataToSave) {

                @Override
                public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                	campaignFormDataToSave.setRecordversion(campaignFormDataToSave.getRecordversion() == null ? 1L : campaignFormDataToSave.getRecordversion());
//
                    DatabaseHelper.getCampaignFormDataDao().saveAndSnapshot(campaignFormDataToSave);
                }

                @Override
                protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                    hidePreloader();
                    super.onPostExecute(taskResult);
                    if (taskResult.getResultStatus().isSuccess()) {
                        finish();
                        CampaignFormDataEditActivity.startActivity(getContext(), campaignFormDataToSave.getUuid());
                    }
                    saveTask = null;
                }
            }.executeOnThreadPool();
        } else {
            NotificationHelper.showNotification(this, WARNING, "Lot Cluster Number Already Exist for this Lot Number");
            return;
        }
    }

//    public String dateFormatterLongAndMobile(Object value) {
//        String dateStr = String.valueOf(value);
//        System.out.println("Date in question: " + dateStr);
//
//        // List of possible input date formats (ordered by likelihood)
//        String[] inputFormats = {
//                "yyyy-MM-dd",                   // e.g., 2025-06-25
//                "MMM dd, yyyy HH:mm:ss a",      // e.g., Jun 25, 2025 10:30:00 AM
//                "MMM d, yyyy HH:mm:ss",         // e.g., Jun 5, 2025 10:30:00
//                "MMM d, yyyy HH:mm:ss a",       // e.g., Jun 5, 2025 10:30:00 AM
//                "dd/MM/yyyy",                   // e.g., 25/06/2025
//                "EEE MMM dd HH:mm:ss z yyyy"    // e.g., Wed Jun 25 10:30:00 GMT 2025
//        };
//
//        // The desired output format (date only)
//        DateFormat outputFormatter = new SimpleDateFormat("dd-MM-yyyy");
//
//        for (String formatString : inputFormats) {
//            try {
//                DateFormat inputFormatter = new SimpleDateFormat(formatString);
//                Date parsedDate = inputFormatter.parse(dateStr);
//
//                // Format the parsed date into the desired output format
//                String formattedDate = outputFormatter.format(parsedDate);
//                System.out.println("Successfully parsed. Formatted date: " + formattedDate);
//
//                return formattedDate; // Return date in yyyy-MM-dd format
//            } catch (ParseException e) {
//                System.out.println("Failed to parse with format '" + formatString + "': " + e.getMessage());
//            }
//        }
//
//        System.out.println("Could not parse date----: " + dateStr);
//        return null; // or throw an exception
//    }

    public String dateFormatterLongAndMobile(Object value) {
        if (value == null) return null;

        String dateStr = String.valueOf(value);
        System.out.println("Date in question: " + dateStr);

        // Standard output format
        DateFormat outputFormatter = new SimpleDateFormat("dd-MM-yyyy");

        try {
            // First try parsing with the standard format
            Date parsedDate = outputFormatter.parse(dateStr);
            return outputFormatter.format(parsedDate);
        } catch (ParseException e) {
            // If standard format fails, try other formats
            String[] inputFormats = {
                    "yyyy-MM-dd",                   // e.g., 2025-06-25
                    "MMM dd, yyyy HH:mm:ss a",      // e.g., Jun 25, 2025 10:30:00 AM
                    "MMM d, yyyy HH:mm:ss",         // e.g., Jun 5, 2025 10:30:00
                    "MMM d, yyyy HH:mm:ss a",       // e.g., Jun 5, 2025 10:30:00 AM
                    "dd/MM/yyyy",                   // e.g., 25/06/2025
                    "EEE MMM dd HH:mm:ss z yyyy"    // e.g., Wed Jun 25 10:30:00 GMT 2025
            };

            for (String formatString : inputFormats) {
                try {
                    DateFormat inputFormatter = new SimpleDateFormat(formatString);
                    Date parsedDate = inputFormatter.parse(dateStr);
                    return outputFormatter.format(parsedDate);
                } catch (ParseException e2) {
                    // Continue to next format
                }
            }
        }

        System.out.println("Could not parse date: " + dateStr);
        return dateStr; // Return original if we can't parse it
    }


    @Override
    public Enum getPageStatus() {
        return null;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_campaign_form_data_new;
    }
}
