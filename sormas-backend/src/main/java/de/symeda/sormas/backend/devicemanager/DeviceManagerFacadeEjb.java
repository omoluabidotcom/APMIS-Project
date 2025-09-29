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

package de.symeda.sormas.backend.devicemanager;

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

import de.symeda.sormas.api.devicemanager.DeviceManagerDto;
import de.symeda.sormas.api.devicemanager.DeviceManagerFacade;
import de.symeda.sormas.api.devicemanager.DeviceManagerReferenceDto;
import de.symeda.sormas.api.devicemanager.DeviceMangerCriteria;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
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

@Stateless(name = "DeviceManagerFacade")
public class DeviceManagerFacadeEjb implements DeviceManagerFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private DeviceManagerService deviceManagerService;

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

	public DeviceManager fromDto(@NotNull DeviceManagerDto source, boolean checkChangeDate) {
		DeviceManager target = DtoHelper.fillOrBuildEntity(source, deviceManagerService.getByUuid(source.getUuid()),
				DeviceManager::new, checkChangeDate);

		target.setDevice_model(source.getDeviceModel());
		target.setDevice_brand(source.getDeviceBrand());
		target.setUser_name(source.getUserName());
		target.setUser_location(source.getUserLocation());
		target.setApk_version(source.getApkVersion());		
        target.setDevice_serial(source.getDeviceSerial());
        target.setAndroid_version(source.getAndroidVersion());
        target.setTotal_int_storage(source.getInternalStorageTotal());
        target.setFree_int_storage(source.getInternalStorageFree());
        target.setTotal_ext_storage(source.getExternalStorageTotal());
        target.setFree_ext_storage(source.getExternalStorageFree());
        target.setRam_storage(source.getRamTotal());
        target.setBattery_level(source.getBatteryLevel());
        target.setWifi_connected(source.getWifiConnected());	        
        target.setNetwork_strength(source.getNetworkStrength());
        target.setDevice_id(source.getDeviceId());
//        
//        UserReferenceDto userRef = source.getUser();
//        if (userRef != null) {
//            // Prefer by UUID if available, else by ID if your UserService supports it
//            User userEntity = userService.getByUuid(userRef.getUuid());
//            target.setUser_id(userEntity);
//        } else {
//            target.setUser_id(null);
//        }
        
        target.setUser_name(source.getUserName());
        target.setUser_location(source.getUserLocation());
	        
		return target;
	}

	public DeviceManagerDto toDto(DeviceManager source) {
		if (source == null) {
			return null;
		}

		DeviceManagerDto target = new DeviceManagerDto();
		DtoHelper.fillDto(target, source);
		
		target.setDeviceModel(source.getDevice_model());
		target.setUserName(source.getUser_name());
		target.setUserLocation(source.getUser_location());
		target.setApkVersion(source.getApk_version());		
		target.setDeviceBrand(source.getDevice_brand());
        target.setDeviceSerial(source.getDevice_serial());
        target.setAndroidVersion(source.getAndroid_version());
        target.setInternalStorageTotal(source.getTotal_int_storage());
        target.setInternalStorageFree(source.getFree_int_storage());
        target.setExternalStorageTotal(source.getTotal_ext_storage());
        target.setExternalStorageFree(source.getTotal_int_storage());
        target.setRamTotal(source.getRam_storage());
        target.setBatteryLevel(source.getBattery_level());
        target.setWifiConnected(source.getWifi_connected());	        
        target.setNetworkStrength(source.getNetwork_strength());
        target.setDeviceId(source.getDevice_id());

//        if (source.getUser_id() != null) {
//            target.setUser(new UserReferenceDto(source.getUser_id().getUuid(), source.getUser_id().getFirstName(), source.getUser_id().getLastName()));
//        } else {
//            target.setUser(null);
//        }        target.setUserName(source.getUser_name());
        target.setUserLocation(source.getUser_location());


        //APPVERSION AND USER LOCATION
        

		return target;
	}

	private void validate(DeviceManagerDto deviceManagerDto) {
		
	}

	@Override
	public DeviceManagerDto saveDeviceDetailsMobile(@Valid DeviceManagerDto deviceManagerDto)
			throws ValidationRuntimeException {

		System.out.println(" MObile version of save chittt ");
		UserReferenceDto currtUsr = userServiceEBJ.getCurrentUserAsReference();

		DeviceManager deviceInfoData = fromDto(deviceManagerDto, true);

		validate(deviceManagerDto);

		deviceManagerService.ensurePersisted(deviceInfoData);
		return toDto(deviceInfoData);
	}

	@Override
	public List<DeviceManagerDto> getByUuids(List<String> uuids) {
		return deviceManagerService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	
	@Override
	public DeviceManagerDto getDeviceDetailsByUuid(String uuid) {
		return toDto(deviceManagerService.getByUuid(uuid));
	}
	
	
	@Override
	public List<DeviceManagerDto> getIndexList(DeviceMangerCriteria criteria, Integer first, Integer max,
	                                           List<SortProperty> sortProperties) {

	    CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<DeviceManager> cq = cb.createQuery(DeviceManager.class);
	    Root<DeviceManager> root = cq.from(DeviceManager.class);

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


	private DeviceManagerReferenceDto toReferenceDto(DeviceManager source) {
		if (source == null) {
			return null;
		}
		return source.toReference();
	}

	@LocalBean
	@Stateless
	public static class DeviceManagerFacadeEjbLocal extends DeviceManagerFacadeEjb {

		public DeviceManagerFacadeEjbLocal() {

		}

	}



}