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
 
import android.os.DeadSystemException;
 
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.Campaign;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMetaRegion;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.PopulationData;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.dialog.FormDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogSelectCampaignFormMetaLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class CampaignFormMetaDialog extends FormDialog {

    private DialogSelectCampaignFormMetaLayoutBinding contentBinding;
    private Campaign campaign;

    public CampaignFormMetaDialog(final FragmentActivity activity, Campaign campaign) {
        super(
                activity,
                R.layout.dialog_root_layout,
                R.layout.dialog_select_campaign_form_meta_layout,
                R.layout.dialog_root_two_button_panel_layout,
                R.string.heading_campaign_form_meta_select,
                -1,
                UiFieldAccessCheckers.forSensitiveData(campaign.isPseudonymized()));

        this.campaign = campaign;
    }

    @Override
    protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
        this.contentBinding = (DialogSelectCampaignFormMetaLayoutBinding) binding;
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {

        List<CampaignFormMeta> allFormsForCampaign =new ArrayList<CampaignFormMeta>();
        allFormsForCampaign =  campaign.getCampaignFormMetas();

        System.out.println("allFormsForCampaign----" + allFormsForCampaign);

        List<CampaignFormMeta> allUnexpiredFormsForCampaign = new ArrayList<>();



        for (CampaignFormMeta campaignFormMeta : allFormsForCampaign) {
            Date expiryDate = DatabaseHelper.getCampaignFormMetaWithExpDao().getCampaignFormExpiryDateByCampaignIdAndFormId(campaign.getUuid(), campaignFormMeta.getUuid());
            LocalDate currentDate = LocalDate.now();

            if (expiryDate != null) {
//                System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuyyyyyytt");
            LocalDate expiryLocalDate = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (currentDate.isBefore(expiryLocalDate) || expiryLocalDate.isEqual(currentDate)) {
                    User user = ConfigProvider.getUser();

//System.out.println("xxxuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuyyyyyytt");
// After Checking if the form meets the expiry criteria the we want to check if the form is added fro this region before we add
//the form to the list to be presented in the forms dialog ;
                    List<CampaignFormMetaRegion> formsSelectedForCampaign = DatabaseHelper.getCampaignFormMetaRegionDao().getSelectedFormsByRegion(campaignFormMeta.getUuid(), user.getRegion().getArea().getUuid());
                    System.out.println(formsSelectedForCampaign + "formsSelectedForCampaignformsSelectedForCampaignformsSelectedForCampaign" +campaignFormMeta.getUuid() + "campaignFormMeta.getUuid()," +  user.getRegion().getArea().getUuid());
                    if(formsSelectedForCampaign.size() > 0){

                        allUnexpiredFormsForCampaign.add(campaignFormMeta);
                    }
// expiryDate is before currentDate or equals tob the current date itshold be added to my new list
            }  else {
//expiryDate is after currentDate
//System.out.println("This form has Expired Dte is " + expiryLocalDate + " current date is " + currentDate);
//System.out.println("This form has Expired For Data Entry " + campaignFormMeta.getFormName());
            }
            } else {

                System.out.println("This form does not have an expiry date set  " + campaignFormMeta.getFormName());

            }
}
        Collections.sort(allUnexpiredFormsForCampaign, Comparator.comparing(CampaignFormMeta::getFormName));

            contentBinding.campaignFormMeta.initializeSpinner(DataUtils.toItems(allUnexpiredFormsForCampaign));
    }

    public CampaignFormMeta getCampaignFormMeta() {
        return (CampaignFormMeta) contentBinding.campaignFormMeta.getValue();
    }

    @Override
    protected void onPositiveClick() {

 

        try {
            System.out.println("Positvite ccallback clicked -------------------------");
            setLiveValidationDisabled(false);

            FragmentValidator.validate(getContext(), contentBinding);
        } catch (ValidationException  e) {
             NotificationHelper.showDialogNotification(CampaignFormMetaDialog.this, ERROR, e.getMessage());

            System.out.println("META DIALOG  Fragment Error Logged--------------------");

            ErrorReportingHelper.logAndStoreDeviceError( "New Form : " + e.getMessage(), e); // replaced sendCaughtException

            return;
 
        }catch (RuntimeException e) {
            NotificationHelper.showDialogNotification(CampaignFormMetaDialog.this, ERROR, e.getMessage());

            System.out.println("META DIALOG  Fragment Error Logged--------------------");

            ErrorReportingHelper.logAndStoreDeviceError( "New Form : " + e.getMessage(), e); // replaced sendCaughtException

            return;        }
 
        super.setCloseOnPositiveButtonClick(true);
        super.onPositiveClick();
    }
}
