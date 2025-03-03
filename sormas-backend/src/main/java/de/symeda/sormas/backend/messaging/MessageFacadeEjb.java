package de.symeda.sormas.backend.messaging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Value;
import com.google.gson.Gson;
import com.vladmihalcea.hibernate.type.util.SQLExtractor;

import de.symeda.sormas.api.messaging.FCMDto;
import de.symeda.sormas.api.messaging.FCMResponseDto;
import de.symeda.sormas.api.messaging.MessageCriteria;
import de.symeda.sormas.api.messaging.MessageDto;
import de.symeda.sormas.api.messaging.MessageFacade;
import de.symeda.sormas.api.messaging.MessageScheduleDto;
import de.symeda.sormas.api.messaging.MessageTemplateCriteria;
import de.symeda.sormas.api.messaging.MessageTemplateDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserActivitySummaryDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.area.AreaFacadeEjb;
import de.symeda.sormas.backend.infrastructure.area.AreaService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.user.event.UserUpdateEvent;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "MessageFacade")
public class MessageFacadeEjb implements MessageFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private MessageService messageService;

	@EJB
	private MessageServiceTemplate messageServiceTemplate;
	
	@EJB
	private ScheduleMessageService scheduleMessageService;

	@EJB
	private AreaService areaService;

	@EJB
	private DistrictService districtService;

	@EJB
	private CommunityService communityService;

	@EJB
	private RegionService regionService;

	@EJB
	private UserService userService;

	@Value("${fcm.secret.key}")
	private String fcmSecretKey;

	public static MessageDto toDto(Message source) {
		if (source == null) {
			return null;
		}

		MessageDto target = new MessageDto();
		DtoHelper.fillDto(target, source);

		target.setMessageContent(source.getMessageContent());
		target.setUserRoles(new HashSet<UserRole>(source.getUserRoles()));
		target.setFormAccess(new HashSet<FormAccess>(source.getFormAccess()));
		target.setArea(AreaFacadeEjb.toReferenceDto(source.getArea()));
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setChgDate(source.getChgDate());	

		if (source.getCommunity() != null) {
			target.setCommunity(CommunityFacadeEjb.toReferenceDto(new HashSet<Community>(source.getCommunity())));
		}
		target.setCreatingUser(source.getCreatingUser().getUserName());

		return target;
	}

	public static MessageTemplateDto toDtoTemplate(MessagesTemplate source) {
		if (source == null) {
			return null;
		}

		MessageTemplateDto target = new MessageTemplateDto();
		DtoHelper.fillDto(target, source);

		target.setMessageContent(source.getMessageContent());
		target.setMessageCategory(source.getMessageCategory());
		target.setArchived(source.isArchived());
		target.setChgDate(source.getChgDate());
		target.setCreatingUser(source.getCreatingUser().getUserName());

		return target;
	}
	
	public static MessageScheduleDto toDtoScheduleMessage(MessageCron source) {
		if (source == null) {
			return null;
		}

		MessageScheduleDto target = new MessageScheduleDto();
		DtoHelper.fillDto(target, source);

		target.setMessageContent(source.getMessageContent());
		target.setUserRoles(new HashSet<UserRole>(source.getUserRoles()));
		target.setFormAccess(new HashSet<FormAccess>(source.getFormAccess()));
		target.setArea(AreaFacadeEjb.toReferenceDto(source.getArea()));
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setChgDate(source.getChgDate());	

		if (source.getCommunity() != null) {
			target.setCommunity(CommunityFacadeEjb.toReferenceDto(new HashSet<Community>(source.getCommunity())));
		}
		target.setCreatingUser(source.getCreatingUser().getUserName());

		return target;
	}

	public Message fromDto(@NotNull MessageDto source, boolean checkChangeDate) {
		Message target = (Message) DtoHelper.fillOrBuildEntity(source, messageService.getByUuid(source.getUuid()),
				Message::new, checkChangeDate);

		target.setMessageContent(source.getMessageContent());
		target.setUserRoles(new HashSet<UserRole>(source.getUserRoles()));
//		target.setUsertype(source.getUserTypes());
		target.setFormAccess(new HashSet<FormAccess>(source.getFormAccess()));
		target.setArea(areaService.getByReferenceDto(source.getArea()));
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setCreatingUser(userService.getByUserName(source.getCreatingUser()));
		target.setChgDate(source.getChgDate());

		return target;
	}

	public MessagesTemplate fromDtoTemplate(@NotNull MessageTemplateDto source, boolean checkChangeDate) {
		MessagesTemplate target = (MessagesTemplate) DtoHelper.fillOrBuildEntity(source,
				messageServiceTemplate.getByUuid(source.getUuid()), MessagesTemplate::new, checkChangeDate);

		target.setMessageContent(source.getMessageContent());
		target.setCreatingUser(userService.getByUserName(source.getCreatingUser()));
		target.setMessageCategory(source.getMessageCategory());
		target.setArchived(source.isArchived());
		target.setChgDate(source.getChgDate());

		return target;
	}
	
	public MessageCron fromDtoMessageSchedule(@NotNull MessageScheduleDto source, boolean checkChangeDate) {
		MessageCron target = (MessageCron) DtoHelper.fillOrBuildEntity(source, messageService.getByUuid(source.getUuid()),
				MessageCron::new, checkChangeDate);

		target.setMessageContent(source.getMessageContent());
		target.setUserRoles(new HashSet<UserRole>(source.getUserRoles()));
		target.setFormAccess(new HashSet<FormAccess>(source.getFormAccess()));
		target.setArea(areaService.getByReferenceDto(source.getArea()));
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setCreatingUser(userService.getByUserName(source.getCreatingUser()));
		target.setChgDate(source.getChgDate());

		return target;
	}

	@Override
	public List<MessageDto> getIndexList(MessageCriteria messageCriteria, Integer first, Integer max,
			List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Message> cq = cb.createQuery(Message.class);
		Root<Message> messages = cq.from(Message.class);

		Join<Message, Area> area = messages.join(Message.AREA, JoinType.LEFT);
		Join<Message, Region> regionJoin = messages.join(Message.REGION, JoinType.LEFT);
		Join<Message, District> districtJoin = messages.join(Message.DISTRICT, JoinType.LEFT);
		Join<Message, Community> communityJoin = messages.join(Message.COMMUNITY, JoinType.LEFT);
		Join<Message, User> userJoin = messages.join(Message.CREATED_BY, JoinType.LEFT);

		Predicate filter = null;

		if (messageCriteria != null) {
			filter = messageService.buildCriteriaFilter(messageCriteria, cb, messages);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Message.UUID:
				case Message.MESSAGE_CONTENT:
					expression = messages.get(Message.MESSAGE_CONTENT);
					break;
				case Message.USER_ROLES:
					expression = messages.get(Message.USER_ROLES);
					break;
				case Message.MESSAGE_FORM_ACCESS:
					expression = messages.get(Message.MESSAGE_FORM_ACCESS);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(messages.get(Message.CHANGE_DATE)));
		}
		cq.select(messages);

		return QueryHelper.getResultList(em, cq, first, max, MessageFacadeEjb::toDto);
	}

	@Override
	public List<MessageTemplateDto> getIndexListForMessageTemplate(MessageTemplateCriteria messageTemplateCriteria,
			Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MessagesTemplate> cq = cb.createQuery(MessagesTemplate.class);
		Root<MessagesTemplate> messageTemplate = cq.from(MessagesTemplate.class);

		Join<MessagesTemplate, User> userJoin = messageTemplate.join(MessagesTemplate.CREATED_BY, JoinType.LEFT);

		cq.multiselect(messageTemplate.get(MessagesTemplate.UUID),
				messageTemplate.get(MessagesTemplate.MESSAGE_CONTENT), userJoin.get(User.USER_NAME));

		Predicate filter = null;

		if (messageTemplateCriteria != null) {
			filter = messageServiceTemplate.buildCriteriaFilter(messageTemplateCriteria, cb, messageTemplate);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case MessagesTemplate.UUID:
				case MessagesTemplate.MESSAGE_CONTENT:
					expression = messageTemplate.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(messageTemplate.get(MessagesTemplate.CHANGE_DATE)));
		}
		cq.select(messageTemplate);
		return QueryHelper.getResultList(em, cq, first, max, MessageFacadeEjb::toDtoTemplate);
	}

	@Override
	public MessageDto saveMessage(@Valid MessageDto messageDto) {
		Message message = fromDto(messageDto, true);
		messageService.ensurePersisted(message);
		return toDto(message);
	}

	@Override
	public MessageTemplateDto saveMessage(@Valid MessageTemplateDto messageTemplateDto) {
		MessagesTemplate messageTemplate = fromDtoTemplate(messageTemplateDto, true);
		messageServiceTemplate.ensurePersisted(messageTemplate);
		return toDtoTemplate(messageTemplate);
	}
	
	@Override
	public MessageScheduleDto saveMessage(@Valid MessageScheduleDto messageScheduleDto) {
		MessageCron messageCron = fromDtoMessageSchedule(messageScheduleDto, false);
		scheduleMessageService.ensurePersisted(messageCron);
		return toDtoScheduleMessage(messageCron);
	}

	@Override
	public boolean deleteMessage(MessageTemplateDto messageTemplateDto) {
		MessagesTemplate messageTemplate = fromDtoTemplate(messageTemplateDto, true);
		messageServiceTemplate.delete(messageTemplate);
		return true;
	}

	@Override
	public long count(MessageCriteria messageCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Message> from = cq.from(Message.class);

		Predicate filter = null;

		if (messageCriteria != null) {
			filter = messageService.buildCriteriaFilter(messageCriteria, cb, from);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public long count(MessageTemplateCriteria messageTemplateCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MessagesTemplate> from = cq.from(MessagesTemplate.class);

		Predicate filter = null;

		if (messageTemplateCriteria != null) {
			filter = messageServiceTemplate.buildCriteriaFilter(messageTemplateCriteria, cb, from);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<MessageDto> getMessageByUserRoles(MessageCriteria messageCriteria, UserType userType, Integer first,
			Integer max, Set<UserRole> userRoles, Set<FormAccess> formAccess) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Message> cq = cb.createQuery(Message.class);
		Root<Message> from = cq.from(Message.class);

		Predicate filter = null;
		Predicate filterMain = null;
		Predicate filterArea = null;
		Predicate filterRegion = null;
		Predicate filterDistrict = null;

		if (messageCriteria != null) {
			filterMain = messageService.buildCriteriaFilterCustom(messageCriteria, cb, from, userRoles, formAccess);
			filter = filterMain;
		}

		if (messageCriteria.getArea() != null) {
			filterArea = messageService.buildCriteriaFilterArea(messageCriteria, cb, from);
			if (filter != null) {
				filter = cb.and(filter, filterArea);
			} else {
				filter = filterArea;
			}
		}

		if (messageCriteria.getRegion() != null) {
			filterRegion = messageService.buildCriteriaFilterRegion(messageCriteria, cb, from);

			if (filter != null) {
				filter = cb.and(filter, filterRegion);
			} else {
				filter = filterRegion;
			}
		}

		if (messageCriteria.getDistrict() != null) {
			filterDistrict = messageService.buildCriteriaFilterDistrict(messageCriteria, cb, from);

			if (filter != null) {
				filter = cb.and(filter, filterDistrict);
			} else {
				filter = filterDistrict;
			}
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true).orderBy(cb.desc(from.get(Message.CHANGE_DATE)));
		return QueryHelper.getResultList(em, cq, first, max, MessageFacadeEjb::toDto);
	}

	@Override
	public List<MessageDto> getMessageByDate(MessageCriteria messageCriteria, UserType userType, Integer first,
			Integer max, Set<UserRole> userRoles, Set<FormAccess> formAccess, Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Message> cq = cb.createQuery(Message.class);
		Root<Message> from = cq.from(Message.class);

		Predicate filter = null;
		Predicate filterMain = null;
		Predicate filterArea = null;
		Predicate filterRegion = null;
		Predicate filterDistrict = null;
		Predicate dateFilter = null;

		if (messageCriteria != null) {
			filterMain = messageService.buildCriteriaFilterCustom(messageCriteria, cb, from, userRoles, formAccess);
			filter = filterMain;
		}

		if (messageCriteria.getArea() != null) {
			filterArea = messageService.buildCriteriaFilterArea(messageCriteria, cb, from);
			if (filter != null) {
				filter = cb.and(filter, filterArea);
			} else {
				filter = filterArea;
			}
		}

		if (messageCriteria.getRegion() != null) {
			filterRegion = messageService.buildCriteriaFilterRegion(messageCriteria, cb, from);

			if (filter != null) {
				filter = cb.and(filter, filterRegion);
			} else {
				filter = filterRegion;
			}
		}

		if (messageCriteria.getDistrict() != null) {
			filterDistrict = messageService.buildCriteriaFilterDistrict(messageCriteria, cb, from);

			if (filter != null) {
				filter = cb.and(filter, filterDistrict);
			} else {
				filter = filterDistrict;
			}
		}

		if (date != null) {
			dateFilter = cb.greaterThan(from.get("changeDate"), date);
			filter = filter != null ? cb.and(filter, dateFilter) : dateFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true).orderBy(cb.desc(from.get(Message.CHANGE_DATE)));
		return QueryHelper.getResultList(em, cq, first, max, MessageFacadeEjb::toDto);
	}

	@Override
	public long getNewMessage(MessageCriteria messageCriteria, UserType userType, Integer first, Integer max,
			Set<UserRole> userRoles, Set<FormAccess> formAccess) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Message> from = cq.from(Message.class);

		Predicate filter = null;
		Predicate filterMain = null;
		Predicate filterArea = null;
		Predicate filterRegion = null;
		Predicate filterDistrict = null;

		if (messageCriteria != null) {
			filterMain = messageService.buildCriteriaFilterCustom(messageCriteria, cb, from, userRoles, formAccess);
			filter = filterMain;
		}

		if (messageCriteria.getArea() != null) {
			filterArea = messageService.buildCriteriaFilterArea(messageCriteria, cb, from);
			if (filter != null) {
				filter = cb.and(filter, filterArea);
			} else {
				filter = filterArea;
			}
		}

		if (messageCriteria.getRegion() != null) {
			filterRegion = messageService.buildCriteriaFilterRegion(messageCriteria, cb, from);
			if (filter != null) {
				filter = cb.and(filter, filterRegion);
			} else {
				filter = filterRegion;
			}
		}

		if (messageCriteria.getDistrict() != null) {
			filterDistrict = messageService.buildCriteriaFilterDistrict(messageCriteria, cb, from);
			if (filter != null) {
				filter = cb.and(filter, filterDistrict);
			} else {
				filter = filterDistrict;
			}
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(from));

		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public void archivingMessageTemplate(List<String> uuids) {

		List<MessagesTemplate> messagesTemplates = messageServiceTemplate.getByUuids(uuids);
		for (MessagesTemplate messageTemplate : messagesTemplates) {

			messageTemplate.setArchived(true);
			messageServiceTemplate.ensurePersisted(messageTemplate);
		}
	}

	@Override
	public void dearchivingMessageTemplate(List<String> uuids) {

		List<MessagesTemplate> messagesTemplates = messageServiceTemplate.getByUuids(uuids);
		for (MessagesTemplate messageTemplate : messagesTemplates) {

			messageTemplate.setArchived(false);
			messageServiceTemplate.ensurePersisted(messageTemplate);
		}
	}

}
