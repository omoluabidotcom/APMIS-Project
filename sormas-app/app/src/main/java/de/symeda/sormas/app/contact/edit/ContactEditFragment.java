package de.symeda.sormas.app.contact.edit;

import android.view.View;

import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

import static android.view.View.GONE;

public class ContactEditFragment extends BaseEditFragment<FragmentContactEditLayoutBinding, Contact, Contact> {

    private Contact record;
    private Case sourceCase;

    // Enum lists

    private List<Item> relationshipList;
    private List<Item> contactClassificationList;

    // Instance methods

    public static ContactEditFragment newInstance(Contact activityRootData) {
        return newInstance(ContactEditFragment.class, null, activityRootData);
    }

    private void setUpControlListeners(FragmentContactEditLayoutBinding contentBinding) {
        contentBinding.createCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseNewActivity.startActivityFromContact(getContext(), record.getUuid());
            }
        });

        contentBinding.openSourceCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseReadActivity.startActivity(getActivity(), sourceCase.getUuid(), true);
            }
        });

        contentBinding.openResultingCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseReadActivity.startActivity(getActivity(), record.getResultingCaseUuid(), true);
            }
        });
    }

    private void setUpFieldVisibilities(FragmentContactEditLayoutBinding contentBinding) {
        setVisibilityByDisease(ContactDto.class, sourceCase.getDisease(), contentBinding.mainContent);

        if (record.getResultingCaseUuid() != null) {
            contentBinding.createCase.setVisibility(GONE);
            if (DatabaseHelper.getCaseDao().queryUuidBasic(record.getResultingCaseUuid()) == null) {
                contentBinding.openResultingCase.setVisibility(GONE);
            }
        } else {
            contentBinding.openResultingCase.setVisibility(GONE);
        }

        if (record.getContactClassification() != ContactClassification.CONFIRMED) {
            contentBinding.createCase.setVisibility(GONE);
        }
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_contact_information);
    }

    @Override
    public Contact getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();
        sourceCase = DatabaseHelper.getCaseDao().queryUuidBasic(record.getCaseUuid());

        relationshipList = DataUtils.getEnumItems(ContactRelation.class, true);
        contactClassificationList = DataUtils.getEnumItems(ContactClassification.class, true);
    }

    @Override
    public void onLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        contentBinding.setData(record);
        contentBinding.setCaze(sourceCase);
        contentBinding.setContactProximityClass(ContactProximity.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {
        setUpFieldVisibilities(contentBinding);

        // Initialize ControlSpinnerFields
        contentBinding.contactRelationToCase.initializeSpinner(relationshipList);
        contentBinding.contactContactClassification.initializeSpinner(contactClassificationList);

        // Initialize ControlDateFields
        contentBinding.contactLastContactDate.initializeDateField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_edit_layout;
    }
}