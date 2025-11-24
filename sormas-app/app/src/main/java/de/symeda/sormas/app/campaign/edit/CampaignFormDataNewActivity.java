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
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
 
import java.util.Calendar;
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

    private Locale currentLocale;

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
        criteria.setCommunity(null);
        List<CampaignFormData> lotchecker = DatabaseHelper.getCampaignFormDataDao().queryByCriteria(criteria, 0, 100);

        if(!ConfigProvider.getUser().getUserRoles().contains(UserRole.SURVEILLANCE_OFFICER)) { // District Officer
            criteria.setCommunity(campaignFormDataToSave.getCommunity());
//            criteria.setCommunity(null);
        }
        System.out.println(campaignFormDataToSave.getFormDate()  + "campaignFormDataToSave.getFormDate()campaignFormDataToSave.getFormDate()campaignFormDataToSave.getFormDate()campaignFormDataToSave.getFormDate()");


//        campaignFormDataToSave.setRecordversion(1L);
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
        }        final List<CampaignFormDataEntry> filledFormValues = new ArrayList<>();

        CampaignFormDataEntry lotNo = new CampaignFormDataEntry();
        CampaignFormDataEntry lotClusterNo = new CampaignFormDataEntry();

//        formValues.forEach(campaignFormDataEntry ->
        for(CampaignFormDataEntry campaignFormDataEntry : formValues) {
            if (campaignFormDataEntry.getId() != null && campaignFormDataEntry.getValue() != null) {
                String value = campaignFormDataEntry.getValue().toString();

                System.out.println(campaignFormDataEntry.getId() + "Village code Value detected -------" + value);

                if(campaignFormDataEntry.getId().toString().equalsIgnoreCase("villageCode")
               || campaignFormDataEntry.getId().toString().equalsIgnoreCase("tazkiraNo")
               || campaignFormDataEntry.getId().toString().equalsIgnoreCase("phone")){
                   System.out.println("Village code Value detected -------");
               }else{
                   try {
                       double num = Double.parseDouble(value);
                       if (num == Math.floor(num)) { // means it's a whole number
                           value = String.valueOf((int) num); // convert to int string
                       } else {
                           value = String.valueOf(num); // keep original decimal
                       }
                   } catch (NumberFormatException e) {
                       // not a number, leave value as is
                   }
               }

                System.out.println(campaignFormDataEntry.getId() + "before setting v Village code Value detected -------" + value);

                campaignFormDataEntry.setValue(value);
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
 
        if (campaignFormDataToSave.getFormDate() != null) {
            Date date = campaignFormDataToSave.getFormDate();

            Calendar cal = Calendar.getInstance();

            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            int milli = cal.get(Calendar.MILLISECOND);

// now apply that to your date
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, second);
            cal.set(Calendar.MILLISECOND, milli);

            campaignFormDataToSave.setFormDate(cal.getTime());
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

        if(campaignFormDataToSave.getFormDate() == null){
            saveChecker = false;
//            NotificationHelper.showNotification(this, WARNING, "Lot Cluster Number Already Exist for this Lot Number");

        }

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
            if(campaignFormDataToSave.getFormDate() == null){
                NotificationHelper.showNotification(this, ERROR, "Form Date cannot be left Empty.");

            }else{
                NotificationHelper.showNotification(this, WARNING, "Lot Cluster Number Already Exist for this Lot Number");

            }
            return;
        }
    }

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
                    "EEE MMM dd HH:mm:ss z yyyy" ,   // e.g., Wed Jun 25 10:30:00 GMT 2025
                    "EEE MMM dd HH:mm:ss zzz yyyy"

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
