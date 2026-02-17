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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.devicemanager.DeviceManagerDto;
import de.symeda.sormas.api.devicemanager.DeviceManagerFacade;
import de.symeda.sormas.api.devicemanager.DeviceManagerReferenceDto;
import de.symeda.sormas.api.devicemanager.DeviceMangerCriteria;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.area.AreaFacadeEjb;
import de.symeda.sormas.backend.infrastructure.area.AreaService;
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
	private CommunityService communityService;

	@EJB
	private UserService userService;

	@EJB
	private UserFacadeEjb.UserFacadeEjbLocal userServiceEBJ;

	@EJB
	private DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacadeEjb;

	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;
	
	@EJB
	private AreaService areaService;
	
	@EJB
	private RegionService regionService;
	
	@EJB
	private DistrictService districtService;

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

        target.setNetworkProvider(source.getNetworkProvider());
        target.setActiveCampaigns(source.getActiveCampaigns());
        target.setActiveFormCount(source.getActiveFormCount());        
		target.setArea(areaService.getByReferenceDto(source.getArea()));
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		if (source.getDistricts() != null) {
			target.setDistricts(districtService.getByReferenceDto(source.getDistricts()));
		}

//        target.setTotal_int_storage_gb(source.getInternalStorageTotalGb());
//        target.setFree_int_storage_gb(source.getInternalStorageFreeGb());
//        target.setTotal_ext_storage_gb(source.getExternalStorageTotalGb());
//        target.setFree_ext_storage_gb(source.getExternalStorageFreeGb());
//        target.setRam_storage_gb(source.getRamTotalGb());
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
        
        target.setInternalStorageTotalGb(source.getTotal_int_storage_gb());
        target.setInternalStorageFreeGb(source.getFree_int_storage_gb());
        target.setExternalStorageTotalGb(source.getTotal_ext_storage_gb());
        target.setExternalStorageFreeGb(source.getFree_ext_storage_gb());
        target.setRamTotalGb(source.getRam_storage_gb());
 
        
        target.setNetworkProvider(source.getNetworkProvider());
        target.setActiveCampaigns(source.getActiveCampaigns());
        target.setActiveFormCount(source.getActiveFormCount());
        target.setArea(AreaFacadeEjb.toReferenceDto(source.getArea()));
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		if (source.getDistricts() != null) {
			target.setDistricts(DistrictFacadeEjb.toReferenceDto(new HashSet<District>(source.getDistricts())));
		}
		
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

	    List<Predicate> predicates = new ArrayList<>();

	    System.out.println("DEBUG BACKEND - Criteria is null? " + (criteria == null));
	    if (criteria != null) {
	        System.out.println("DEBUG: Criteria received - Area: " + (criteria.getArea() != null ? criteria.getArea().size() : 0) + 
	                          ", Region: " + (criteria.getRegion() != null ? criteria.getRegion().size() : 0) + 
	                          ", District: " + (criteria.getDistrict() != null ? criteria.getDistrict().size() : 0));
	        
	        // Check if ANY filters are applied
	        boolean hasFilters = (criteria.getArea() != null && !criteria.getArea().isEmpty()) ||
	                            (criteria.getRegion() != null && !criteria.getRegion().isEmpty()) ||
	                            (criteria.getDistrict() != null && !criteria.getDistrict().isEmpty());
	        
	        if (hasFilters) {
	            // Create a subquery to handle filtering through User
	            Subquery<Long> subquery = cq.subquery(Long.class);
	            Root<DeviceManager> subRoot = subquery.from(DeviceManager.class);
	            Join<DeviceManager, User> subUserJoin = subRoot.join("user_id", JoinType.LEFT);
	            
	            List<Predicate> subPredicates = new ArrayList<>();
	            
	            // Area filter
	            if (criteria.getArea() != null && !criteria.getArea().isEmpty()) {
	                System.out.println("AREAFILTERING - Areas selected: " + criteria.getArea().size());
	                
	                List<String> areaUuids = criteria.getArea().stream()																													
	                    .map(AreaReferenceDto::getUuid)
	                    .collect(Collectors.toList());
	                
	                Join<User, Area> areaJoin = subUserJoin.join("area", JoinType.LEFT);
	                subPredicates.add(areaJoin.get("uuid").in(areaUuids));
	            }
	            
	            // Region filter
	            if (criteria.getRegion() != null && !criteria.getRegion().isEmpty()) {
	                System.out.println("REGIONFILTERING - Regions selected: " + criteria.getRegion().size());
	                
	                List<String> regionUuids = criteria.getRegion().stream()
	                    .map(RegionReferenceDto::getUuid)
	                    .collect(Collectors.toList());
	                
	                Join<User, Region> regionJoin = subUserJoin.join("region", JoinType.LEFT);
	                subPredicates.add(regionJoin.get("uuid").in(regionUuids));
	            }
	            
	            // District filter  
	            if (criteria.getDistrict() != null && !criteria.getDistrict().isEmpty()) {
	                System.out.println("DISTRICTFILTERING - Districts selected: " + criteria.getDistrict().size());
	                
	                List<String> districtUuids = criteria.getDistrict().stream()
	                    .map(DistrictReferenceDto::getUuid)
	                    .collect(Collectors.toList());
	                
	                Join<User, District> districtJoin = subUserJoin.join("district", JoinType.LEFT);
	                subPredicates.add(districtJoin.get("uuid").in(districtUuids));
	            }
	            
	            // The subquery selects DeviceManager IDs that match the filters
	            subquery.select(subRoot.get("id"));
	            
	            // Link subquery to main query
	            if (!subPredicates.isEmpty()) {
	                subquery.where(cb.and(subPredicates.toArray(new Predicate[0])));
	                // Include records that match the filter OR have no user (if you want to show them)
	                predicates.add(cb.or(
	                    cb.in(root.get("id")).value(subquery),  // Matches filter
	                    cb.isNull(root.get("user_id"))          // OR has no user
	                ));
	            }
	        }
	    }

	    // Apply predicates if any
	    if (!predicates.isEmpty()) {
	        cq.where(cb.and(predicates.toArray(new Predicate[0])));
	    }

	    cq.orderBy(cb.asc(root.get("user_name")));
	    cq.select(root);

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