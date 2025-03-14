package de.symeda.sormas.backend.messaging;

import java.util.Arrays;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.messaging.MessageCriteria;
import de.symeda.sormas.api.messaging.MessageTemplateCriteria;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class MessageServiceTemplate extends AdoServiceWithUserFilter<MessagesTemplate> {

	public MessageServiceTemplate() {
		super(MessagesTemplate.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, MessagesTemplate> from) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Predicate buildCriteriaFilter(MessageTemplateCriteria messageTemplateCriteria, CriteriaBuilder cb, Root<MessagesTemplate> from) {

		Predicate filter = null;
		
		if (messageTemplateCriteria.getMessageCategory() != null) {
		    filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(MessagesTemplate.MESSAGE_CATEGORY), 
		        messageTemplateCriteria.getMessageCategory()));
		}
		
		if (messageTemplateCriteria.getStartDate() != null) {
			 filter = cb.greaterThanOrEqualTo(from.get(MessagesTemplate.CREATION_DATE), messageTemplateCriteria.getStartDate());
	    }

	    if (messageTemplateCriteria.getEndDate() != null) {
	    	 filter = cb.lessThanOrEqualTo(from.get(MessagesTemplate.CHANGE_DATE), messageTemplateCriteria.getEndDate());
	    }
	    
	    if (messageTemplateCriteria.isArchived() != null) {
	    	filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(MessagesTemplate.ARCHIVED), true));
		}	    
		
		if (messageTemplateCriteria.getFreeText() != null) {
			String[] textFilters = (messageTemplateCriteria.getFreeText().split("\\s+"));
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
						CriteriaBuilderHelper.unaccentedIlikeCustom(cb, from.get(MessagesTemplate.MESSAGE_CONTENT), textFilter),
						CriteriaBuilderHelper.ilike(cb, from.get(MessagesTemplate.UUID), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		return filter;
	}


}
