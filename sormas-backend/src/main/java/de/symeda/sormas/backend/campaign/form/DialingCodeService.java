package de.symeda.sormas.backend.campaign.form;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;

@Stateless
@LocalBean
public class DialingCodeService extends AdoServiceWithUserFilter<DialingCode> {

	public DialingCodeService() {
		super(DialingCode.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, DialingCode> from) {
		// TODO Auto-generated method stub
		return null;
	}

}
