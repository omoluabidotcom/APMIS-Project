package de.symeda.sormas.backend.messaging;

import java.util.Arrays;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.messaging.MessageScheduleCriteria;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.area.AreaService;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class ScheduleMessageService extends AdoServiceWithUserFilter<MessageCron> {

	@EJB
	private AreaService areaService;

	@EJB
	private RegionService regionService;

	@EJB
	private DistrictService districtService;

	@EJB
	private CommunityService communityService;
	
	public ScheduleMessageService() {
		super(MessageCron.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, MessageCron> from) {
		// TODO Auto-generated method stub
		return null;
	}

	public Predicate buildCriteriaFilter(MessageScheduleCriteria messageScheduleCriteria, CriteriaBuilder cb, Root<MessageCron> from) {

		Predicate filter = null;

		if (messageScheduleCriteria.getUserRole() != null) {
			Join<User, UserRole> joinRoles = from.join(Message.USER_ROLES, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, joinRoles.in(Arrays.asList(messageScheduleCriteria.getUserRole())));
		}

		if (messageScheduleCriteria.getFormAccess() != null) {
			Join<User, FormAccess> joinFormAccess = from.join(Message.MESSAGE_FORM_ACCESS, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, joinFormAccess.in(Arrays.asList(messageScheduleCriteria.getFormAccess())));
		}
		
		if (messageScheduleCriteria.getArea() != null) {
			Join<Message, Area> joinAreas = from.join(Message.AREA, JoinType.LEFT);
			Predicate areaFilter = joinAreas.in(areaService.getByUuid(messageScheduleCriteria.getArea().getUuid()));
			filter = CriteriaBuilderHelper.and(cb, filter, areaFilter);
		}
		
		if (messageScheduleCriteria.getRegion() != null) {
			Join<Message, Region> joinRegion = from.join(Message.REGION, JoinType.LEFT);
			Predicate regionFilter = joinRegion.in(regionService.getByUuid(messageScheduleCriteria.getRegion().getUuid()));
			filter = CriteriaBuilderHelper.and(cb, filter, regionFilter);
		}
		
		if (messageScheduleCriteria.getDistrict() != null) {
			Join<Message, District> joinDistrict = from.join(Message.DISTRICT, JoinType.LEFT);
			Predicate districtFilter = joinDistrict.in(districtService.getByUuid(messageScheduleCriteria.getDistrict().getUuid()));
			filter = CriteriaBuilderHelper.and(cb, filter, districtFilter);
		}

		if (messageScheduleCriteria.getFreeText() != null) {
			String[] textFilters = (messageScheduleCriteria.getFreeText().split("\\s+"));
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
						CriteriaBuilderHelper.unaccentedIlikeCustom(cb, from.get(Message.MESSAGE_CONTENT), textFilter),
						CriteriaBuilderHelper.ilike(cb, from.get(Message.UUID), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		return filter;
	}

}
