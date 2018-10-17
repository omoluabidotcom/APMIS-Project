package de.symeda.sormas.app.event.edit.eventparticipant;

import android.view.View;

import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.databinding.FragmentEventParticipantEditLayoutBinding;
import de.symeda.sormas.app.person.edit.PersonEditFragment;

import static android.view.View.GONE;

public class EventParticipantEditFragment extends BaseEditFragment<FragmentEventParticipantEditLayoutBinding, EventParticipant, EventParticipant> {

    public static final String TAG = EventParticipantEditFragment.class.getSimpleName();

    private EventParticipant record;

    public static EventParticipantEditFragment newInstance(EventParticipant activityRootData) {
        return newInstance(EventParticipantEditFragment.class, null, activityRootData);
    }

    // Instance methods

    private void setUpFieldVisibilities(FragmentEventParticipantEditLayoutBinding contentBinding) {
        if (record.getResultingCaseUuid() != null) {
            contentBinding.createCaseFromEventPerson.setVisibility(GONE);
            if (DatabaseHelper.getCaseDao().queryUuidBasic(record.getResultingCaseUuid()) == null) {
                contentBinding.eventParticipantButtonsPanel.setVisibility(GONE);
            }
        } else {
            contentBinding.openEventPersonCase.setVisibility(GONE);
        }
    }

    private void setUpControlListeners(FragmentEventParticipantEditLayoutBinding contentBinding) {
        contentBinding.openEventPersonCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseReadActivity.startActivity(getActivity(), record.getResultingCaseUuid(), true);
            }
        });

        contentBinding.createCaseFromEventPerson.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               CaseNewActivity.startActivityFromEventPerson(getContext(), record.getUuid());
           }
        });
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_person_involved);
    }

    @Override
    public EventParticipant getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentEventParticipantEditLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        contentBinding.setData(record);
        contentBinding.setPresentConditionClass(PresentCondition.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventParticipantEditLayoutBinding contentBinding) {
        setUpFieldVisibilities(contentBinding);

        if (contentBinding.eventParticipantPersonLayout != null) {
            PersonEditFragment.setUpLayoutBinding(this, record.getPerson(), contentBinding.eventParticipantPersonLayout);
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_participant_edit_layout;
    }
}