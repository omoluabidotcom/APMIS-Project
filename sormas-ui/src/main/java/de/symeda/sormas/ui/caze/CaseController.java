package de.symeda.sormas.ui.caze;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataFacade;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationFacade;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.epidata.EpiDataForm;
import de.symeda.sormas.ui.epidata.EpiDataView;
import de.symeda.sormas.ui.hospitalization.CaseHospitalizationForm;
import de.symeda.sormas.ui.hospitalization.CaseHospitalizationView;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CaseController {

	private CaseFacade cf = FacadeProvider.getCaseFacade();
	private SymptomsFacade sf = FacadeProvider.getSymptomsFacade();
	private HospitalizationFacade hf = FacadeProvider.getHospitalizationFacade();
	private EpiDataFacade edf = FacadeProvider.getEpiDataFacade();
	private ContactFacade conf = FacadeProvider.getContactFacade();
	
    public CaseController() {
    	
    }
    
    public void registerViews(Navigator navigator) {
    	navigator.addView(CasesView.VIEW_NAME, CasesView.class);
    	navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
    	navigator.addView(CasePersonView.VIEW_NAME, CasePersonView.class);
    	navigator.addView(CaseSymptomsView.VIEW_NAME, CaseSymptomsView.class);
    	navigator.addView(CaseContactsView.VIEW_NAME, CaseContactsView.class);
    	navigator.addView(CaseHospitalizationView.VIEW_NAME, CaseHospitalizationView.class);
    	navigator.addView(EpiDataView.VIEW_NAME, EpiDataView.class);
    }
    
    public void create() {
    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(null, null, null);
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");    	
    }
    
    public void create(PersonDto person, Disease disease) {
    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(person, disease, null);
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case"); 
    }
    
    public void create(PersonDto person, Disease disease, ContactDto contact) {
    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(person, disease, contact);
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");
    }
    
    public void navigateToData(String caseUuid) {
   		String navigationState = CaseDataView.VIEW_NAME + "/" + caseUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }

    public void navigateToSymptoms(String caseUuid) {
   		String navigationState = CaseSymptomsView.VIEW_NAME + "/" + caseUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }

    public void navigateToPerson(String caseUuid) {
   		String navigationState = CasePersonView.VIEW_NAME + "/" + caseUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }

    public void navigateToHospitalization(String caseUuid) {
    	String navigationState = CaseHospitalizationView.VIEW_NAME + "/" + caseUuid;
    	SormasUI.get().getNavigator().navigateTo(navigationState);
    }
    
    public void navigateToEpiData(String caseUuid) {
    	String navigationState = EpiDataView.VIEW_NAME + "/" + caseUuid;
    	SormasUI.get().getNavigator().navigateTo(navigationState);
    }
    
    public void navigateToIndex() {
    	String navigationState = CasesView.VIEW_NAME;
    	SormasUI.get().getNavigator().navigateTo(navigationState);
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    public void setUriFragmentParameter(String caseUuid) {
        String fragmentParameter;
        if (caseUuid == null || caseUuid.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = caseUuid;
        }

        Page page = SormasUI.get().getPage();
        page.setUriFragment("!" + CasesView.VIEW_NAME + "/"
                + fragmentParameter, false);
    }

    public List<CaseDataDto> getCaseIndexList() {
    	UserDto user = LoginHelper.getCurrentUser();
    	return FacadeProvider.getCaseFacade().getAllCasesAfter(null, user.getUuid());
    }
    
    private CaseDataDto findCase(String uuid) {
        return cf.getCaseDataByUuid(uuid);
    }

    private CaseDataDto createNewCase(PersonDto person, Disease disease) {
    	CaseDataDto caze = new CaseDataDto();
    	caze.setUuid(DataHelper.createUuid());
    	
    	if(person != null) {
    		caze.setPerson(person);
    	}
    	if(disease == null) {
    		caze.setDisease(Disease.EVD);
    	} else {
    		caze.setDisease(disease);
    	}
    	caze.setInvestigationStatus(InvestigationStatus.PENDING);
    	caze.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
    	
    	caze.setReportDate(new Date());
    	UserDto user = LoginHelper.getCurrentUser();
    	UserReferenceDto userReference = LoginHelper.getCurrentUserAsReference();
    	caze.setReportingUser(userReference);
    	caze.setRegion(user.getRegion());
    	caze.setDistrict(user.getDistrict());
    	
    	return caze;
    }
    
    public CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent(PersonDto person, Disease disease, ContactDto contact) {
    	
    	CaseCreateForm createForm = new CaseCreateForm();
    	CaseDataDto caze = createNewCase(person, disease);
        createForm.setValue(caze);
        
        if (person != null) {
        	createForm.setPerson(person);
        	createForm.setNameReadOnly(true);
        }
        if (contact != null) {
        	createForm.setDiseaseReadOnly(true);
        }
        final CommitDiscardWrapperComponent<CaseCreateForm> editView = new CommitDiscardWrapperComponent<CaseCreateForm>(createForm, createForm.getFieldGroup());
       
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (createForm.getFieldGroup().isValid()) {
        			final CaseDataDto dto = createForm.getValue();
        			
        			if (contact != null) {
        				// automatically change the contact classification to "converted"
						contact.setContactClassification(ContactClassification.CONVERTED);
						conf.saveContact(contact);

						// use the person of the contact we are creating a case for
        				dto.setPerson(person);
        				cf.saveCase(dto);        				
	        			Notification.show("New case created", Type.ASSISTIVE_NOTIFICATION);
	        			navigateToPerson(dto.getUuid());
        			} else {
	        			ControllerProvider.getPersonController().selectOrCreatePerson(
	        					createForm.getPersonFirstName(), createForm.getPersonLastName(), 
	        					person -> {
	        						if (person != null) {
		        						dto.setPerson(person);
		        						cf.saveCase(dto);
		        	        			Notification.show("New case created", Type.ASSISTIVE_NOTIFICATION);
		        	        			navigateToPerson(dto.getUuid());
	        						}
	        					});
					}
        		}
        	}
        });
        
        return editView;
    }

    public CommitDiscardWrapperComponent<CaseDataForm> getCaseDataEditComponent(final String caseUuid) {
    	CaseDataDto caze = findCase(caseUuid);
    	CaseDataForm caseEditForm = new CaseDataForm(FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid()), caze.getDisease());
        caseEditForm.setValue(caze);
        final CommitDiscardWrapperComponent<CaseDataForm> editView = new CommitDiscardWrapperComponent<CaseDataForm>(caseEditForm, caseEditForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (caseEditForm.getFieldGroup().isValid()) {
        			CaseDataDto cazeDto = caseEditForm.getValue();
        			cazeDto = cf.saveCase(cazeDto);
        			Notification.show("Case data saved", Type.WARNING_MESSAGE);
        			navigateToData(cazeDto.getUuid());
        		}
        	}
        });

        return editView;
    }

	public CommitDiscardWrapperComponent<SymptomsForm> getCaseSymptomsEditComponent(final String caseUuid) {
    	
        CaseDataDto caseDataDto = findCase(caseUuid);

    	SymptomsForm symptomsForm = new SymptomsForm(caseDataDto.getDisease(), true);
        symptomsForm.setValue(caseDataDto.getSymptoms());
    	symptomsForm.initializeSymptomRequirementsForCase();
        final CommitDiscardWrapperComponent<SymptomsForm> editView = new CommitDiscardWrapperComponent<SymptomsForm>(symptomsForm, symptomsForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (symptomsForm.getFieldGroup().isValid()) {
        			SymptomsDto dto = symptomsForm.getValue();
        			sf.saveSymptoms(dto);
        			Notification.show("Case symptoms saved", Type.WARNING_MESSAGE);
        			navigateToSymptoms(caseUuid);
        		}
        	}
        });
        
        return editView;
    }    
	
	public CommitDiscardWrapperComponent<CaseHospitalizationForm> getCaseHospitalizationComponent(final String caseUuid) {
		CaseDataDto caze = findCase(caseUuid);
		CaseHospitalizationForm hospitalizationForm = new CaseHospitalizationForm(caze);
		hospitalizationForm.setValue(caze.getHospitalization());
	
		final CommitDiscardWrapperComponent<CaseHospitalizationForm> editView = new CommitDiscardWrapperComponent<CaseHospitalizationForm>(hospitalizationForm, hospitalizationForm.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (hospitalizationForm.getFieldGroup().isValid()) {
					HospitalizationDto dto = hospitalizationForm.getValue();
					hf.saveHospitalization(dto);
					Notification.show("Case hospitalization saved", Type.WARNING_MESSAGE);
					navigateToHospitalization(caseUuid);
				}
			}
		});
		
		return editView;
	}
	
	public CommitDiscardWrapperComponent<EpiDataForm> getEpiDataComponent(final String caseUuid) {
		CaseDataDto caze = findCase(caseUuid);
		EpiDataForm epiDataForm = new EpiDataForm(caze.getDisease());
		epiDataForm.setValue(caze.getEpiData());
		
		final CommitDiscardWrapperComponent<EpiDataForm> editView = new CommitDiscardWrapperComponent<EpiDataForm>(epiDataForm, epiDataForm.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (epiDataForm.getFieldGroup().isValid()) {
					EpiDataDto dto = epiDataForm.getValue();
					edf.saveEpiData(dto);
					Notification.show("Case epidemiological data saved", Type.WARNING_MESSAGE);
					navigateToEpiData(caseUuid);
				}
			}
		});
		
		return editView;
	}
	
}