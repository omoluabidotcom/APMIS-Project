package de.symeda.sormas.backend.task;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class TaskService extends AbstractAdoService<Task> {
	
	@EJB
	CaseService caseService;
	@EJB
	ContactService contactService;
	@EJB
	EventService eventService;

	public TaskService() {
		super(Task.class);
	}
	
	/**
	 * @return ordered by priority, suggested start
	 */
	public List<Task> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		Root<Task> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from, user);
		if (date != null) {
			filter = cb.and(filter, cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date));
		}
		cq.where(filter);
		cq.orderBy(cb.asc(from.get(Task.PRIORITY)), cb.asc(from.get(Task.SUGGESTED_START)), cb.asc(from.get(AbstractDomainObject.ID)));

		List<Task> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Task,Task> taskPath, User user) {
		// whoever created the task or is assigned to it is allowed to access it
		Predicate filter = cb.equal(taskPath.get(Task.CREATOR_USER), user);
		filter = cb.or(filter, cb.equal(taskPath.get(Task.ASSIGNEE_USER), user));
		
		filter = cb.or(filter, caseService.createUserFilter(cb, cq, taskPath.join(Task.CAZE, JoinType.LEFT), user));
		filter = cb.or(filter, contactService.createUserFilter(cb, cq, taskPath.join(Task.CONTACT, JoinType.LEFT), user));
		filter = cb.or(filter, eventService.createUserFilter(cb, cq, taskPath.join(Task.EVENT, JoinType.LEFT), user));
		
		return filter;
	}
	
	public long getCount(TaskCriteria taskCriteria) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> from = cq.from(getElementClass());
		
		Predicate filter = buildCriteraFilter(taskCriteria, cb, from);
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.select(cb.countDistinct(from));

		long count = em.createQuery(cq).getSingleResult();
		return count;
	}

	public List<Task> findBy(TaskCriteria taskCriteria) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		Root<Task> from = cq.from(getElementClass());

		Predicate filter = buildCriteraFilter(taskCriteria, cb, from);
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Task.CREATION_DATE)));

		List<Task> resultList = em.createQuery(cq).getResultList();
		return resultList;	
	}

	private Predicate buildCriteraFilter(TaskCriteria taskCriteria, CriteriaBuilder cb, Root<Task> from) {
		Predicate filter = null;
		if (taskCriteria.getTaskStatuses() != null && taskCriteria.getTaskStatuses().length > 0) {
			if (taskCriteria.getTaskStatuses().length == 1) {
				filter = and(cb, filter, cb.equal(from.get(Task.TASK_STATUS), taskCriteria.getTaskStatuses()[0]));
			} else {
				Predicate subFilter = null;
				for (TaskStatus taskStatus : taskCriteria.getTaskStatuses()) {
					subFilter = or(cb, subFilter, cb.equal(from.get(Task.TASK_STATUS), taskStatus));
				}
				filter = and(cb, filter, subFilter);
			}
		}
		if (taskCriteria.getTaskType() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.TASK_TYPE), taskCriteria.getTaskType()));
		}
		if (taskCriteria.getAssigneeUser() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.ASSIGNEE_USER), taskCriteria.getAssigneeUser()));
		}
		if (taskCriteria.getCaze() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.CAZE), taskCriteria.getCaze()));
		}
		if (taskCriteria.getContact() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.CONTACT), taskCriteria.getContact()));
		}
		if(taskCriteria.getEvent() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.EVENT), taskCriteria.getEvent()));
		}
		return filter;
	}
	
	/**
	 * TODO move to CriteriaBuilderHelper
	 * @param existing nullable
	 */
	public static Predicate and(CriteriaBuilder cb, Predicate existing, Predicate additional) {
		if (existing == null) {
			return additional;
		}
		return cb.and(existing, additional);
	}
	
	/**
	 * TODO move to CriteriaBuilderHelper
	 * @param existing nullable
	 */
	public static Predicate or(CriteriaBuilder cb, Predicate existing, Predicate additional) {
		if (existing == null) {
			return additional;
		}
		return cb.or(existing, additional);
	}
}