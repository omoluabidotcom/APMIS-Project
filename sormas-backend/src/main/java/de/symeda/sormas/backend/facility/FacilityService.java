package de.symeda.sormas.backend.facility;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class FacilityService extends AbstractAdoService<Facility> {
	
	public FacilityService() {
		super(Facility.class);
	}
	
	public List<Facility> getAllByCommunity(Community community) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Facility.COMMUNITY), community));
		
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		return em.createQuery(cq).getResultList();
	}
	
	public List<Facility> getAllByDistrict(District district) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Facility.DISTRICT), district));
		
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		return em.createQuery(cq).getResultList();
	}
	
	public List<Facility> getAllByFacilityType(FacilityType type) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Facility.TYPE), type));
		
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		return em.createQuery(cq).getResultList();
	}

	@Override
	protected Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Facility, Facility> from, User user) {
		throw new UnsupportedOperationException();
	}
}