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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.data.PlatformEnum;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.Campaign;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

public class CampaignFormDataEditActivity extends BaseEditActivity<CampaignFormData> {

    private AsyncTask saveTask;
    private Campaign campaign;
    private CampaignFormMeta campaignFormMeta;
    private CampaignFormDataCriteria criteria = new CampaignFormDataCriteria();

    private Locale currentLocale;

    public static void startActivity(Context context, String rootUuid) {
        BaseActivity.startActivity(context, CampaignFormDataEditActivity.class, buildBundle(rootUuid));
    }

    @Override
    protected CampaignFormData queryRootEntity(String recordUuid) {
        return DatabaseHelper.getCampaignFormDataDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    protected CampaignFormData buildRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, CampaignFormData activityRootData) {
        return CampaignFormDataEditFragment.newInstance(activityRootData);
    }

    @Override
    public void saveData() {

        if (saveTask != null) {
            NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
            return; // don't save multiple times
        }

        final CampaignFormData campaignFormDataToSave = getStoredRootEntity();

        campaign = DatabaseHelper.getCampaignDao().queryUuid(campaignFormDataToSave.getCampaign().getUuid());
        campaignFormMeta = DatabaseHelper.getCampaignFormMetaDao().queryUuid(campaignFormDataToSave.getCampaignFormMeta().getUuid());

        System.out.println(campaignFormDataToSave.getCampaignFormMeta().getFormCategory()+">>>>>edit>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>__");
        //true is returned when the form is yet to be synchronized with the server, so we only increment teh record version when
        //this form has been subimmted and synchronized with server
        //in return none synced changes wouldn't increment record version
//        if(!campaignFormDataToSave.isModifiedOrChildModified()){
//            campaignFormDataToSave.setRecordversion(campaignFormDataToSave.getRecordversion() + 1L);
//        }
        campaignFormDataToSave.setFormCategory(campaignFormDataToSave.getCampaignFormMeta().getFormCategory());

        try {
            FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentLocale = getResources().getConfiguration().getLocales().get(0);
        } else {
            currentLocale = getResources().getConfiguration().locale;
        }
        String language = currentLocale.getLanguage();

        List<CampaignFormDataEntry> cleanedFormValues = new ArrayList<>(campaignFormDataToSave.getFormValues().size());
        List<CampaignFormDataEntry> formValues = new ArrayList<>();
        if (!language.equalsIgnoreCase("en")) {

            for (CampaignFormDataEntry entry : campaignFormDataToSave.getFormValues()) {
                if ("time".equalsIgnoreCase(entry.getId())) {
                    String convertedTime = convertToEnglishNumbers(String.valueOf(entry.getValue()));
                    if (!convertedTime.equals(entry.getValue())) {
                        CampaignFormDataEntry timeEntry = new CampaignFormDataEntry();
                        timeEntry.setId(entry.getId());
                        timeEntry.setValue(convertedTime);
                        cleanedFormValues.add(timeEntry);
                    } else {
                        cleanedFormValues.add(entry);
                    }
                } else {
                    cleanedFormValues.add(entry);
                }
            }
            formValues = cleanedFormValues;
        } else {
            formValues = campaignFormDataToSave.getFormValues();
        }
        final List<CampaignFormDataEntry> filledFormValues = new ArrayList<>();


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
        for(CampaignFormDataEntry campaignFormDataEntry : formValues) {
            if (campaignFormDataEntry.getId() != null && campaignFormDataEntry.getValue() != null) {
                filledFormValues.add(campaignFormDataEntry);
            }
        }

        campaignFormDataToSave.setFormValues(filledFormValues);
        campaignFormDataToSave.setSoruce(PlatformEnum.MOBILE);

        saveTask = new SavingAsyncTask(getRootView(), campaignFormDataToSave) {

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException {

                if(!campaignFormDataToSave.isModifiedOrChildModified()){
                    campaignFormDataToSave.setRecordversion(campaignFormDataToSave.getRecordversion()== null ? 1l  : campaignFormDataToSave.getRecordversion() + 1L);
                }

//                campaignFormDataToSave.setRecordversion(campaignFormDataToSave.getRecordversion() == null ? 1L : campaignFormDataToSave.getRecordversion());

                DatabaseHelper.getCampaignFormDataDao().saveAndSnapshot(campaignFormDataToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                super.onPostExecute(taskResult);

                if (taskResult.getResultStatus().isSuccess()) {
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_ROW_COUNT");
                    sendBroadcast(intent);

                    finish();
                } else {
                 //   onResume(); // reload data
                }
                saveTask = null;
            }
        }.executeOnThreadPool();
    }
    void setSetSubHeadingRowCountForCampaign(){

    };

    public String dateFormatterLongAndMobile(Object value) {
        String dateStr = String.valueOf(value);
        System.out.println("Date in question: " + dateStr);

        String[] inputFormats = {
                "yyyy-MM-dd",                   // e.g., 2025-06-25
                "MMM dd, yyyy HH:mm:ss a",      // e.g., Jun 25, 2025 10:30:00 AM
                "MMM d, yyyy HH:mm:ss",         // e.g., Jun 5, 2025 10:30:00
                "MMM d, yyyy HH:mm:ss a",       // e.g., Jun 5, 2025 10:30:00 AM
                "dd/MM/yyyy",                   // e.g., 25/06/2025
                "EEE MMM dd HH:mm:ss z yyyy"    // e.g., Wed Jun 25 10:30:00 GMT 2025
        };

        // The desired output format (date only)
        DateFormat outputFormatter = new SimpleDateFormat("dd-MM-yyyy");

        for (String formatString : inputFormats) {
            try {
                DateFormat inputFormatter = new SimpleDateFormat(formatString);
                Date parsedDate = inputFormatter.parse(dateStr);
                String formattedDate = outputFormatter.format(parsedDate);

                return formattedDate; // Return date in yyyy-MM-dd format
            } catch (ParseException e) {
                System.out.println("Failed to parse with format '" + formatString + "': " + e.getMessage());
            }
        }
        System.out.println("Could not parse date----: " + dateStr);
        return value.toString();
    }

    @Override
    public Enum getPageStatus() {
        return null;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_campaign_form_data_edit;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

    public static String convertToEnglishNumbers(String input) {
        if (input == null) return null;
        final char[] persianDigits = {'\u06F0','\u06F1','\u06F2','\u06F3','\u06F4','\u06F5','\u06F6','\u06F7','\u06F8','\u06F9'};
        final char[] arabicDigits = {'\u0660','\u0661','\u0662','\u0663','\u0664','\u0665','\u0666','\u0667','\u0668','\u0669'};
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            boolean found = false;
            for (int j = 0; j < 10; j++) {
                if (ch == persianDigits[j] || ch == arabicDigits[j]) {
                    output.append((char) ('0' + j));
                    found = true;
                    break;
                }
            }
            if (!found) {
                output.append(ch);
            }
        }
        return output.toString();
    }
}
