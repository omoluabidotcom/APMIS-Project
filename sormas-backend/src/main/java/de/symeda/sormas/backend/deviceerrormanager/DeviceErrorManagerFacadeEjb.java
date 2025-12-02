/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.deviceerrormanager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.vladmihalcea.hibernate.type.util.SQLExtractor;

import de.symeda.sormas.api.deviceerrormanager.DeviceErrorManagerDto;
import de.symeda.sormas.api.deviceerrormanager.DeviceErrorManagerFacade;
import de.symeda.sormas.api.deviceerrormanager.DeviceErrorManagerReferenceDto;
import de.symeda.sormas.api.deviceerrormanager.DeviceErrorMangerCriteria;
import de.symeda.sormas.api.devicemanager.DeviceManagerDto;
import de.symeda.sormas.api.devicemanager.DeviceManagerFacade;
import de.symeda.sormas.api.devicemanager.DeviceManagerReferenceDto;
import de.symeda.sormas.api.devicemanager.DeviceMangerCriteria;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.devicemanager.DeviceManagerService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "DeviceErrorManagerFacade")
public class DeviceErrorManagerFacadeEjb implements DeviceErrorManagerFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private DeviceErrorManagerService deviceErrorManagerService;

	@EJB
	private DistrictService districtService;

	@EJB
	private CommunityService communityService;

	@EJB
	private UserService userService;

	@EJB
	private UserFacadeEjb.UserFacadeEjbLocal userServiceEBJ;

	@EJB
	private DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacadeEjb;

	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;

	public DeviceErrorManager fromDto(@NotNull DeviceErrorManagerDto source, boolean checkChangeDate) {
		DeviceErrorManager target = DtoHelper.fillOrBuildEntity(source, deviceErrorManagerService.getByUuid(source.getUuid()),
				DeviceErrorManager::new, checkChangeDate);

//target.setChangeDate(source.getChangeDate());
//target.setCreationDate(source.getCreationDate());
target.setDeviceId(source.getDeviceId());
target.setErrorAction(source.getErrorAction());
target.setErrorMessage(source.getErrorMessage());
target.setLastUpdated(source.getLastUpdated());
target.setStackTrace(source.getStackTrace());
target.setUserName(source.getUserName());
target.setUuid(source.getUuid());

		return target;
	}

	public DeviceErrorManagerDto toDto(DeviceErrorManager source) {
		if (source == null) {
			return null;
		}

		DeviceErrorManagerDto target = new DeviceErrorManagerDto();
		DtoHelper.fillDto(target, source);
		
		target.setDeviceId(source.getDeviceId());
		target.setErrorAction(source.getErrorAction());
		target.setErrorMessage(source.getErrorMessage());
		target.setLastUpdated(source.getLastUpdated());
		target.setStackTrace(source.getStackTrace());
		target.setUserName(source.getUserName());
		target.setUuid(source.getUuid());

		return target;
	}

	private void validate(DeviceErrorManagerDto deviceManagerDto) {
		
	}

	@Override
	public DeviceErrorManagerDto saveDeviceErrorFromMobile(@Valid DeviceErrorManagerDto deviceErroeManagerDto)
			throws ValidationRuntimeException {

		System.out.println(" MObile version of save chittt ");
		UserReferenceDto currtUsr = userServiceEBJ.getCurrentUserAsReference();

		DeviceErrorManager deviceInfoData = fromDto(deviceErroeManagerDto, true);

		validate(deviceErroeManagerDto);

		deviceErrorManagerService.ensurePersisted(deviceInfoData);
		return toDto(deviceInfoData);
	}

	@Override
	public List<DeviceErrorManagerDto> getByUuids(List<String> uuids) {
		return deviceErrorManagerService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	
	@Override
	public DeviceErrorManagerDto getDeviceDetailsByUuid(String uuid) {
		return toDto(deviceErrorManagerService.getByUuid(uuid));
	}
	
	@Override
	public DeviceErrorManagerDto getDeviceErrorByUsernameAndDeviceId(String username, String deviceId) {
	    if (username == null || deviceId == null) {
	        return null;
	    }
	    
	    DeviceErrorManager deviceErrorManager = deviceErrorManagerService.getByUsernameAndDeviceId(username, deviceId);
	    return toDto(deviceErrorManager);
	}

	
	@Override
	public List<DeviceErrorManagerDto> getIndexList(DeviceErrorMangerCriteria criteria, Integer first, Integer max,
	                                           List<SortProperty> sortProperties) {

	    CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<DeviceErrorManager> cq = cb.createQuery(DeviceErrorManager.class);
	    Root<DeviceErrorManager> root = cq.from(DeviceErrorManager.class);

	    Predicate filter = null;

	    if (criteria != null) {
	        // Example: if criteria has filters, add them here
	        List<Predicate> predicates = new ArrayList<>();

//	        if (criteria.getDeviceModel() != null && !criteria.getDeviceModel().isEmpty()) {
//	            predicates.add(cb.equal(root.get("deviceModel"), criteria.getDeviceModel()));
//	        }
//
//	        if (criteria.getUserName() != null && !criteria.getUserName().isEmpty()) {
//	            predicates.add(cb.equal(root.get("userName"), criteria.getUserName()));
//	        }
//
//	        if (criteria.getUserLocation() != null && !criteria.getUserLocation().isEmpty()) {
//	            predicates.add(cb.equal(root.get("userLocation"), criteria.getUserLocation()));
//	        }
//
//	        if (criteria.getApkVersion() != null && !criteria.getApkVersion().isEmpty()) {
//	            predicates.add(cb.equal(root.get("apkVersion"), criteria.getApkVersion()));
//	        }

	        if (!predicates.isEmpty()) {
	            filter = cb.and(predicates.toArray(new Predicate[0]));
	        }
	    }

	    if (filter != null) {
	        cq.where(filter);
	    }

	    // Sorting
	    if (sortProperties != null && !sortProperties.isEmpty()) {
	        List<Order> orderList = new ArrayList<>();
	        for (SortProperty sortProperty : sortProperties) {
	            Expression<?> expression = root.get(sortProperty.propertyName);
	            orderList.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
	        }
	        cq.orderBy(orderList);
	    } else {
	        cq.orderBy(cb.asc(root.get("user_name"))); // default sort
	    }

	    cq.select(root);

	    // Debug SQL (like your Community example)
	    System.out.println("DEBUGGER DeviceManager Query: " + SQLExtractor.from(em.createQuery(cq)));

	    // Convert to DTO
	    return QueryHelper.getResultList(em, cq, first, max, this::toDto);
	}
	
	
	@Override
	public List<DeviceErrorManagerDto> getLatestLogs(String username, String deviceSerial, int max) {
	    List<DeviceErrorManager> logs = deviceErrorManagerService
	        .getLatestByUsernameAndDeviceId(username, deviceSerial, max);
	    return logs.stream().map(this::toDto).collect(Collectors.toList());
	}


//	private DeviceErrorManagerReferenceDto toReferenceDto(DeviceErrorManager source) {
//		if (source == null) {
//			return null;
//		}
//		return source.toReference();
//	}

	@LocalBean
	@Stateless
	public static class DeviceErrorManagerFacadeEjbLocal extends DeviceErrorManagerFacadeEjb {

		public DeviceErrorManagerFacadeEjbLocal() {

		}

	}




}