package de.symeda.sormas.backend.infrastructure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;

import com.vladmihalcea.hibernate.type.util.SQLExtractor;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignLogDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.CampaignTreeGridDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramCriteria;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaExpiryDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.PopulationDataFacade;
import de.symeda.sormas.api.infrastructure.PopulationDataFauxDto;
import de.symeda.sormas.api.infrastructure.PopulationDataReferenceDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.CampaignFacadeEjb;
import de.symeda.sormas.backend.campaign.CampaignLog;
import de.symeda.sormas.backend.campaign.CampaignService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import org.apache.poi.util.SystemOutLogger;

@Stateless(name = "PopulationDataFacade")
public class PopulationDataFacadeEjb implements PopulationDataFacade {
	

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private PopulationDataService service;

//	@EJB
//	private PopulationDataFauxService fauxService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private CampaignService campaignService;

	@Override
	public Integer getRegionPopulation(String regionUuid, PopulationDataCriteria critariax) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);

		PopulationDataCriteria criteria = new PopulationDataCriteria().ageGroupIsNull(true).sexIsNull(true)
				.districtIsNull(true).communityIsNull(true).region(new RegionReferenceDto(regionUuid, null, null));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);

		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));

		cq.where(filter, campaignFilter);

		cq.where(filter);
		cq.select(root.get(PopulationData.POPULATION));

		return QueryHelper.getSingleResult(em, cq);
	}

	@Override
	public Integer getProjectedRegionPopulation(String regionUuid, PopulationDataCriteria critariax) {

		Float growthRate = regionService.getByUuid(regionUuid).getGrowthRate();

		if (growthRate == null || growthRate == 0) {
			return getRegionPopulation(regionUuid, critariax);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);

		PopulationDataCriteria criteria = new PopulationDataCriteria().ageGroupIsNull(true).sexIsNull(true)
				.districtIsNull(true).communityIsNull(true).region(new RegionReferenceDto(regionUuid, null, null));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));

		cq.where(filter, campaignFilter);

		try {
			PopulationData populationData = em.createQuery(cq).getSingleResult();
			return InfrastructureHelper.getProjectedPopulation(populationData.getPopulation(),
					populationData.getCollectionDate(), growthRate);
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	@Override
	public Integer getDistrictPopulation(String districtUuid, PopulationDataCriteria critariax) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);

		PopulationDataCriteria criteria = new PopulationDataCriteria().ageGroupIsNull(true).sexIsNull(true)
				.communityIsNull(true).district(new DistrictReferenceDto(districtUuid, null, null));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));

		cq.where(filter, campaignFilter);
		cq.select(root.get(PopulationData.POPULATION));

		return QueryHelper.getSingleResult(em, cq);
	}
	
		@Override
		public Long getDistrictPopulationCountByType(String districtUuid,
		                                             String campaignUuid,
		                                             AgeGroup ageGroup) {

		    CriteriaBuilder cb = em.getCriteriaBuilder();
		    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		    Root<PopulationData> root = cq.from(PopulationData.class);

		    Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);
		    Join<PopulationData, District> districtJoin = root.join(PopulationData.DISTRICT);

		    Predicate campaignFilter =
		            cb.equal(campaignJoin.get(Campaign.UUID), campaignUuid);

		    Predicate districtFilter =
		            cb.equal(districtJoin.get(District.UUID), districtUuid);

		    Predicate ageFilter =
		            cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup);

		    cq.where(campaignFilter, districtFilter, ageFilter);

		    cq.select(cb.count(root));

		    System.out.println("Generated SQL: " + SQLExtractor.from(em.createQuery(cq)));

		    return em.createQuery(cq).getSingleResult();
		}

	@Override
	public Integer getDistrictPopulationByType(String districtUuid, String campaignUuid, AgeGroup ageGroup) {

		System.out.println("------------!!!!!!!!!!!!!!!!!!!!!!: ");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);
		Join<PopulationData, District> districtJoin = root.join(PopulationData.DISTRICT);

		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), campaignUuid));
		Predicate districtFilter = cb.and(cb.equal(districtJoin.get(District.UUID), districtUuid));
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));

		cq.where(campaignFilter, districtFilter, ageFilter);
		cq.select(root.get(PopulationData.POPULATION));

		System.out.println("------------!!!!!!!!!!!!!!!!!!!!!!: " + SQLExtractor.from(em.createQuery(cq)));

		return QueryHelper.getSingleResult(em, cq);
	}

	@Override
	public Integer getProjectedDistrictPopulation(String districtUuid, PopulationDataCriteria critariax) {

		Float growthRate = districtService.getByUuid(districtUuid).getGrowthRate();

		if (growthRate == null || growthRate == 0) {
			return getDistrictPopulation(districtUuid, critariax);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);
		PopulationDataCriteria criteria = new PopulationDataCriteria().ageGroupIsNull(true).sexIsNull(true)
				.district(new DistrictReferenceDto(districtUuid, null, null));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);

		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));

		cq.where(filter, campaignFilter);

		try {
			PopulationData populationData = em.createQuery(cq).getSingleResult();
			return InfrastructureHelper.getProjectedPopulation(populationData.getPopulation(),
					populationData.getCollectionDate(), growthRate);
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	@Override
	public void savePopulationDatax(@Valid List<PopulationDataDto> populationDataList,
			@Valid List<PopulationDataFauxDto> PopulationDataFauxDto, boolean isFauxData)
			throws ValidationRuntimeException {

//		if(isFauxData){
//			for (PopulationDataFauxDto populationData : PopulationDataFauxDto) {
//				validate(populationData);
////				PopulationDataFaux entity = fromDtox(populationData, true);
////				fauxService.ensurePersisted(entity);
//			}
//		}else {
//			for (PopulationDataDto populationData : populationDataList) {
//				validate(populationData);
//				PopulationData entity = fromDto(populationData, true);
//				service.ensurePersisted(entity);
//			}
//		}

	}

	@Override
	public void savePopulationData(@Valid List<PopulationDataDto> populationDataList)
			throws ValidationRuntimeException {

		for (PopulationDataDto populationData : populationDataList) {
			System.out.println(populationData.getAgeGroup() + "11111111133333" + populationData.getModality()
					+ "11111111133333" + populationData.getPopulation() + populationData.getDistrictStatus());

			validate(populationData);
			PopulationData entity = fromDto(populationData, false);
			service.ensurePersisted(entity);
		}
	}

	@Override
	public List<PopulationDataDto> getDistrictPopulationByTypeUsingUUIDs(String districtUuid, String campaignUuid,
			AgeGroup ageGroup) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);
		Join<PopulationData, District> districtJoin = root.join(PopulationData.DISTRICT);

		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), campaignUuid));
		Predicate districtFilter = cb.and(cb.equal(districtJoin.get(District.UUID), districtUuid));
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));

		cq.where(campaignFilter, districtFilter, ageFilter);

		System.out.println("zzzzzzDEBUGGER 5678ijhyuioYYYYYY" + SQLExtractor.from(em.createQuery(cq)) + ageFilter);

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<PopulationDataDto> getClusterPopulationByTypeUsingUUIDs(String clusterUuid, String campaignUuid,
			AgeGroup ageGroup) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);
		Join<PopulationData, Community> clusterJoin = root.join(PopulationData.COMMUNITY);

		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), campaignUuid));
		Predicate clusterFilter = cb.and(cb.equal(clusterJoin.get(Community.UUID), clusterUuid));
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));

		cq.where(campaignFilter, clusterFilter, ageFilter);

		System.out.println("zzzzzzDEBUGGER 5678ijhyuioYYYYYY" + SQLExtractor.from(em.createQuery(cq)) + ageFilter);

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
	}

	@Override
	public List<PopulationDataDto> getAllPopulationData() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		// System.out.println("DEBUGGER ----- "+ criteria.getCampaign()!= null);

//		Predicate filter = service.buildCriteriaFilter( cb, root);
//		if (criteria.getCampaign() != null) {
//			Predicate filter_ = CriteriaBuilderHelper.and(cb, filter,
//					cb.equal(root.join(PopulationData.CAMPAIGN, JoinType.LEFT).get(Campaign.UUID),
//							criteria.getCampaign().getUuid()));
//			Predicate filterx = CriteriaBuilderHelper.and(cb, filter_);
//
//			cq.where(filterx);
//		} else {
//			cq.where(filter);
//		}

		System.out.println("zzzzzzDEBUGGER 5678ijhyuio" + SQLExtractor.from(em.createQuery(cq)));

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
//		return null;
	}

	@Override
	public List<PopulationDataDto> getPopulationData(PopulationDataCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		// System.out.println("DEBUGGER ----- "+ criteria.getCampaign()!= null);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		if (criteria.getCampaign() != null) {
			Predicate filter_ = CriteriaBuilderHelper.and(cb, filter,
					cb.equal(root.join(PopulationData.CAMPAIGN, JoinType.LEFT).get(Campaign.UUID),
							criteria.getCampaign().getUuid()));
			Predicate filterx = CriteriaBuilderHelper.and(cb, filter_, cb.equal(root.get("selected"), true));

			cq.where(filterx);
		} else {
			cq.where(filter);
		}

		System.out.println("DEBUGGER 5678ijhyuio" + SQLExtractor.from(em.createQuery(cq)));

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
	}

	public List<PopulationDataDto> getPopulationDataSelected(PopulationDataCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		// System.out.println("DEBUGGER ----- "+ criteria.getCampaign()!= null);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		if (criteria.getCampaign() != null) {
			Predicate filter_ = CriteriaBuilderHelper.and(cb, filter,
					cb.equal(root.join(PopulationData.CAMPAIGN, JoinType.LEFT).get(Campaign.UUID),
							criteria.getCampaign().getUuid()));
			Predicate filterx = CriteriaBuilderHelper.and(cb, filter_, cb.equal(root.get("selected"), true));
			Predicate selectedFilter = cb.and(cb.equal(root.get(PopulationData.SELECTED), true));

			cq.where(filterx, selectedFilter);
		} else {
			cq.where(filter);
		}

		System.out.println("DEBUGGER 5678ijhyuio" + SQLExtractor.from(em.createQuery(cq)));

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
	}

	@Override
	public List<PopulationDataDto> getPopulationDataImportChecker(PopulationDataCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		// System.out.println("DEBUGGER ----- "+ criteria.getCampaign()!= null);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		if (criteria.getCampaign() != null) {
			Predicate filter_ = CriteriaBuilderHelper.and(cb, filter,
					cb.equal(root.join(PopulationData.CAMPAIGN, JoinType.LEFT).get(Campaign.UUID),
							criteria.getCampaign().getUuid()));
			// Predicate filterx = CriteriaBuilderHelper.and(cb, filter_,
			// cb.equal(root.get("selected"), null));

			cq.where(filter_);
		} else {
			cq.where(filter);
		}

		System.out.println("DEBUGGER 5678ijhyuio" + SQLExtractor.from(em.createQuery(cq)));

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<PopulationDataDto> getPopulationDataWithCriteria(CampaignFormDataCriteria criteria) {
		

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN, JoinType.LEFT);
		Join<PopulationData, Region> regionJoin = root.join(PopulationData.REGION, JoinType.LEFT);
		Join<PopulationData, District> districtJoin = root.join(PopulationData.DISTRICT, JoinType.LEFT);
		Join<PopulationData, Community> communityJoin = root.join(PopulationData.COMMUNITY, JoinType.LEFT);
		
	    List<Predicate> predicates = new ArrayList<>();

	    if (criteria.getCampaign() != null) {
	        predicates.add(cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));
	    }
	    
	    if (criteria.getRegion() != null) {
	        predicates.add(cb.equal(regionJoin.get(Region.UUID), criteria.getRegion().getUuid()));
	    }
	    
	    if (criteria.getDistrict() != null) {
	        predicates.add(cb.equal(districtJoin.get(District.UUID), criteria.getDistrict().getUuid()));
	    }
	    
	    if (criteria.getCommunity() != null) {
	        predicates.add(cb.equal(communityJoin.get(Community.UUID), criteria.getCommunity().getUuid()));
	    }

	    predicates.add(cb.isTrue(root.get(PopulationData.SELECTED)));

	    cq.where(cb.and(predicates.toArray(new Predicate[0])));
		

		System.out.println(criteria.getCampaign().getUuid() + "DEBUGGER 5678ijhyuio  getPopulationDataWithCriteria  "
				+ SQLExtractor.from(em.createQuery(cq)));

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
	}
	

	@Override
	public List<PopulationDataDto> getPopulationDataWithCriteria(String campUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);

		Predicate filter_ = cb
				.and(cb.equal(root.join(PopulationData.CAMPAIGN, JoinType.LEFT).get(Campaign.UUID), campUuid));
		Predicate filterx = CriteriaBuilderHelper.and(cb, filter_, cb.equal(root.get(PopulationData.SELECTED), true));
//		Predicate filterxx = CriteriaBuilderHelper.and(cb, filterx,
//				cb.equal(root.get(PopulationData.AGE_GROUP), AgeGroup.AGE_0_4));
//		Predicate filterxxx = CriteriaBuilderHelper.and(cb, filterxx,
//				cb.equal(root.get(PopulationData.AGE_GROUP), AgeGroup.AGE_5_10));

		cq.where(filterx);

//
		System.out.println(campUuid + "DEBUGGER 5678ijhyuio  getPopulationDataWithCriteria  "
				+ SQLExtractor.from(em.createQuery(cq)));

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getPopulationDataForExport(String campaignUuid) {
//TODO addd campaign to the selection
		//@formatter:off
		String qry = "SELECT "
				+ Area.TABLE_NAME + "." + Area.NAME + " AS areaname, "
				+ Area.TABLE_NAME + "." + Area.EXTERNAL_ID + " AS rcode, "
				+ Region.TABLE_NAME + "." + Region.NAME + " AS regionname, "
				+ Region.TABLE_NAME + "." + Region.EXTERNAL_ID + " AS pcode, "
				+ District.TABLE_NAME + "." + District.NAME + " AS districtname, "
				+ District.TABLE_NAME + "." + District.EXTERNAL_ID + " AS dcode, "
				+ Community.TABLE_NAME + "." + Community.NAME + " AS communityname," 
				+ Campaign.TABLE_NAME + "." + Campaign.UUID + " AS campaignname, " 
				
				+ PopulationData.AGE_GROUP + ", "
				+ PopulationData.SEX + ", " 
				+ PopulationData.MODALITY  + ", "
				+ PopulationData.DISTRICT_STATUS  + ", "
				+ PopulationData.POPULATION 
				+ " FROM " + PopulationData.TABLE_NAME
				+ " LEFT JOIN " + Campaign.TABLE_NAME + " ON " + PopulationData.CAMPAIGN + "_id = "
				+ Campaign.TABLE_NAME + "." + Campaign.ID
				
				+ " LEFT JOIN " + Region.TABLE_NAME + " ON " + PopulationData.REGION + "_id = "
					+ Region.TABLE_NAME + "." + Region.ID
					
				+ " LEFT JOIN " + Area.TABLE_NAME + " ON " + Region.TABLE_NAME + ".area_id = "
				+ Area.TABLE_NAME + "." + Area.ID
					
				+ " LEFT JOIN " + District.TABLE_NAME + " ON "
					+ PopulationData.DISTRICT + "_id = " + District.TABLE_NAME + "." + District.ID
					
				+ " LEFT JOIN " + Community.TABLE_NAME + " ON "
					+ PopulationData.COMMUNITY + "_id = " + Community.TABLE_NAME + "." + Community.ID
					+" where "+ Campaign.TABLE_NAME + "." + Campaign.UUID +" = '"+campaignUuid+"' " 
				+ " ORDER BY campaignname, regionname, districtname, communityname, pcode, dcode asc NULLS FIRST";
		System.out.println("__________________: "+qry);
		//@formatter:on
		return em.createNativeQuery(qry).getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getMissingPopulationDataForStatistics(StatisticsCaseCriteria criteria, boolean groupByRegion,
			boolean groupByDistrict, boolean groupBySex, boolean groupByAgeGroup) {

		StringBuilder regionsIn = new StringBuilder();
		StringBuilder districtsIn = new StringBuilder();
		StringBuilder sexesIn = new StringBuilder();
		StringBuilder ageGroupsIn = new StringBuilder();
		List<Object> parameters = new ArrayList<>();

		if (!CollectionUtils.isEmpty(criteria.getRegions()) && CollectionUtils.isEmpty(criteria.getDistricts())) {
			List<Long> regionIds = regionService.getIdsByReferenceDtos(criteria.getRegions());
			QueryHelper.appendInFilterValues(regionsIn, parameters, regionIds, entry -> entry);
		}
		if (!CollectionUtils.isEmpty(criteria.getDistricts())) {
			List<Long> districtIds = districtService.getIdsByReferenceDtos(criteria.getDistricts());
			QueryHelper.appendInFilterValues(districtsIn, parameters, districtIds, entry -> entry);
		}
		if (!CollectionUtils.isEmpty(criteria.getSexes())) {
			QueryHelper.appendInFilterValues(sexesIn, parameters, criteria.getSexes(), entry -> entry.name());
		}
		if (!CollectionUtils.isEmpty(criteria.getAgeGroups())) {
			QueryHelper.appendInFilterValues(ageGroupsIn, parameters, criteria.getAgeGroups(), entry -> entry.name());
		}

		StringBuilder queryBuilder = new StringBuilder();
		if (!groupByDistrict && CollectionUtils.isEmpty(criteria.getDistricts())) {
			queryBuilder.append("SELECT ").append(Region.ID).append(" FROM ").append(Region.TABLE_NAME);

			if (regionsIn.length() > 0) {
				queryBuilder.append(" WHERE ").append(Region.ID).append(" IN ").append(regionsIn);
			}

			//@formatter:off
			queryBuilder.append(" EXCEPT SELECT ").append(PopulationData.REGION).append("_id FROM ").append(PopulationData.TABLE_NAME)
			.append(" WHERE ").append(PopulationData.TABLE_NAME).append(".").append(PopulationData.DISTRICT).append("_id IS NULL").append(" AND ");
			//@formatter:on
		} else {
			queryBuilder.append("SELECT ").append(District.ID).append(" FROM ").append(District.TABLE_NAME);

			if (districtsIn.length() > 0) {
				queryBuilder.append(" WHERE ").append(District.ID).append(" IN ").append(districtsIn);
			}

			//@formatter:off
			queryBuilder.append(" EXCEPT SELECT ").append(PopulationData.DISTRICT).append("_id FROM ").append(PopulationData.TABLE_NAME).append(" WHERE ");
			//@formatter:on
		}

		queryBuilder.append(PopulationData.TABLE_NAME).append(".").append(PopulationData.SEX);
		if (sexesIn.length() > 0) {
			queryBuilder.append(" IN ").append(sexesIn);
		} else {
			queryBuilder.append(groupBySex ? " IS NOT NULL " : " IS NULL ");
		}

		queryBuilder.append(" AND ").append(PopulationData.TABLE_NAME).append(".").append(PopulationData.AGE_GROUP);
		if (ageGroupsIn.length() > 0) {
			queryBuilder.append(" IN ").append(ageGroupsIn);
		} else {
			queryBuilder.append(groupByAgeGroup ? " IS NOT NULL " : " IS NULL ");
		}

		Query query = em.createNativeQuery(queryBuilder.toString());
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}

		return query.getResultList();
	}

	public Integer getAreaPopulation(String areaUuid, AgeGroup ageGroup, CampaignDiagramCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Region> regionJoin = root.join(PopulationData.REGION);
		Join<Region, Area> areaJoin = regionJoin.join(Region.AREA);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);

		Predicate areaFilter = cb.equal(areaJoin.get(Area.UUID), areaUuid);
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));
		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));

		cq.where(areaFilter, ageFilter, campaignFilter);
		cq.select(root.get(PopulationData.POPULATION));

		TypedQuery query = em.createQuery(cq);
		try {
			Integer totalPopulation = 0;
			for (Object i : query.getResultList()) {
				if (Objects.nonNull(i)) {
					totalPopulation = totalPopulation + (Integer) i;
				}
			}
			return totalPopulation;
		} catch (NoResultException e) {
			return null;
		}
	}

	public Integer getAreaPopulationSelected(String areaUuid, AgeGroup ageGroup, CampaignDiagramCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Region> regionJoin = root.join(PopulationData.REGION);
		Join<Region, Area> areaJoin = regionJoin.join(Region.AREA);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);

		Predicate areaFilter = cb.equal(areaJoin.get(Area.UUID), areaUuid);
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));
		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));
		Predicate selectedFilter = cb.and(cb.equal(root.get(PopulationData.SELECTED), true));

		cq.where(areaFilter, ageFilter, campaignFilter, selectedFilter);
		cq.select(root.get(PopulationData.POPULATION));

		TypedQuery query = em.createQuery(cq);
		try {
			Integer totalPopulation = 0;
			for (Object i : query.getResultList()) {
				if (Objects.nonNull(i)) {
					totalPopulation = totalPopulation + (Integer) i;
				}
			}
			return totalPopulation;
		} catch (NoResultException e) {
			return null;
		}
	}

	public Integer getAreaPopulationByUuid(String areaUuid, AgeGroup ageGroup, CampaignDiagramCriteria criteria) {
		// System.out.println("DEBUGGER 5678ijhyuio ___xxxxxccccc
		// "+criteria.getCampaign().getUuid());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Region> regionJoin = root.join(PopulationData.REGION);
		Join<Region, Area> areaJoin = regionJoin.join(Region.AREA);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);

		Predicate areaFilter = cb.equal(areaJoin.get(Area.UUID), areaUuid);
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));
		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));
		Predicate filter_ = CriteriaBuilderHelper.and(cb, campaignFilter, cb.equal(root.get("selected"), true));

		cq.where(areaFilter, ageFilter, filter_);
		cq.select(root.get(PopulationData.POPULATION));
		// System.out.println("DEBUGGER 5678ijhyuio
		// ___xxxxxcccccc____TOtalpopulation____________________________
		// "+SQLExtractor.from(em.createQuery(cq)));

		TypedQuery query = em.createQuery(cq);
		try {
			Integer totalPopulation = 0;
			for (Object i : query.getResultList()) {
				if (Objects.nonNull(i)) {
					totalPopulation = totalPopulation + (Integer) i;
				}
			}
			return totalPopulation;
		} catch (NoResultException e) {
			return null;
		}
	}

	public Integer getAreaPopulationByUuidSelect(String areaUuid, AgeGroup ageGroup, CampaignDiagramCriteria criteria) {
		// System.out.println("DEBUGGER 5678ijhyuio ___xxxxxccccc
		// "+criteria.getCampaign().getUuid());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Region> regionJoin = root.join(PopulationData.REGION);
		Join<Region, Area> areaJoin = regionJoin.join(Region.AREA);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);

		Predicate areaFilter = cb.equal(areaJoin.get(Area.UUID), areaUuid);
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));
		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));
		Predicate filter_ = CriteriaBuilderHelper.and(cb, campaignFilter, cb.equal(root.get("selected"), true));
		Predicate selectedFilter = cb.and(cb.equal(root.get(PopulationData.SELECTED), true));

		cq.where(areaFilter, ageFilter, filter_, selectedFilter);
		cq.select(root.get(PopulationData.POPULATION));
		// System.out.println("DEBUGGER 5678ijhyuio
		// ___xxxxxcccccc____TOtalpopulation____________________________
		// "+SQLExtractor.from(em.createQuery(cq)));

		TypedQuery query = em.createQuery(cq);
		try {
			Integer totalPopulation = 0;
			for (Object i : query.getResultList()) {
				if (Objects.nonNull(i)) {
					totalPopulation = totalPopulation + (Integer) i;
				}
			}
			return totalPopulation;
		} catch (NoResultException e) {
			return null;
		}
	}

	public Integer getAreaPopulationParent(String notneeded, AgeGroup ageGroup, CampaignDiagramCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);

		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));
		System.out.println("DEBUGGERcccccccccooooo: -----============ " + criteria.getCampaign());
		if (criteria.getCampaign() != null) {

			System.out.println("DEBUGGERcccccccccbbbb: -----============ " + criteria.getCampaign().getUuid());
			// campaignService
			Predicate filterx = CriteriaBuilderHelper.and(cb, ageFilter,
					cb.equal(root.join(PopulationData.CAMPAIGN, JoinType.LEFT).get(Campaign.UUID),
							criteria.getCampaign().getUuid()),
					cb.equal(root.get("selected"), true));

			cq.where(filterx);
		} else {
			cq.where(ageFilter);
		}

		cq.select(root.get(PopulationData.POPULATION));
		System.out.println("DEBUGGER 56 TOtalpopulation______: " + SQLExtractor.from(em.createQuery(cq)));

		TypedQuery query = em.createQuery(cq);
		try {
			Integer totalPopulation = 0;
			for (Object i : query.getResultList()) {
				if (Objects.nonNull(i)) {
					totalPopulation = totalPopulation + (Integer) i;
				}
			}
			return totalPopulation;
		} catch (NoResultException e) {
			return null;
		}
	}

	public Integer getAreaPopulationParentSelect(String notneeded, AgeGroup ageGroup,
			CampaignDiagramCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);

		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));
		System.out.println("DEBUGGERcccccccccooooo: -----============ " + criteria.getCampaign());
		if (criteria.getCampaign() != null) {

			System.out.println("DEBUGGERcccccccccbbbb: -----============ " + criteria.getCampaign().getUuid());
			// campaignService
			Predicate filterx = CriteriaBuilderHelper.and(cb, ageFilter,
					cb.equal(root.join(PopulationData.CAMPAIGN, JoinType.LEFT).get(Campaign.UUID),
							criteria.getCampaign().getUuid()),
					cb.equal(root.get("selected"), true));
			Predicate selectedFilter = cb.and(cb.equal(root.get(PopulationData.SELECTED), true));

			cq.where(filterx, selectedFilter);
		} else {
			cq.where(ageFilter);
		}

		cq.select(root.get(PopulationData.POPULATION));
		System.out.println("DEBUGGER 56 TOtalpopulation______: " + SQLExtractor.from(em.createQuery(cq)));

		TypedQuery query = em.createQuery(cq);
		try {
			Integer totalPopulation = 0;
			for (Object i : query.getResultList()) {
				if (Objects.nonNull(i)) {
					totalPopulation = totalPopulation + (Integer) i;
				}
			}
			return totalPopulation;
		} catch (NoResultException e) {
			return null;
		}
	}

	private void validate(PopulationDataDto populationData) throws ValidationRuntimeException {

		if (populationData.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
	}

	private void validate(PopulationDataFauxDto populationData) throws ValidationRuntimeException {

		if (populationData.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
	}

	public PopulationData fromDto(@NotNull PopulationDataDto source, boolean checkChangeDate) {

		PopulationData target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()),
				PopulationData::new, checkChangeDate);

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setCampaign(campaignService.getByReferenceDto(source.getCampaign()));
		target.setAgeGroup(source.getAgeGroup());
		target.setSex(source.getSex());
		target.setPopulation(source.getPopulation());
		target.setCollectionDate(source.getCollectionDate());
		target.setModality(source.getModality());
		target.setDistrictStatus(source.getDistrictStatus());
		target.setSelected(source.getSelected());

		return target;
	}


	public static PopulationDataDto toDto(PopulationData source) {

		if (source == null) {
			return null;
		}
		PopulationDataDto target = new PopulationDataDto();
		DtoHelper.fillDto(target, source);

		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setCampaign(CampaignFacadeEjb.toReferenceDto(source.getCampaign()));
		target.setAgeGroup(source.getAgeGroup());
		target.setSex(source.getSex());
		target.setPopulation(source.getPopulation());
		target.setCollectionDate(source.getCollectionDate());
		target.setModality(source.getModality());
		target.setDistrictStatus(source.getDistrictStatus());
		System.out.println(source.getCommunity() + "source.getCommunity()source.getCommunity()source.getCommunity()source.getCommunity()");
		target.setCluster_id(source.getCommunity() == null ? "" : CommunityFacadeEjb.toReferenceDto(source.getCommunity()).getUuid());
		target.setSelected(source.isSelected());


		return target;
	}
	
	public static PopulationDataDto toDtoPopulationByDistrict(PopulationData source) {

		if (source == null) {
			return null;
		}
		PopulationDataDto target = new PopulationDataDto();
		DtoHelper.fillDto(target, source);

		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setCampaign(CampaignFacadeEjb.toReferenceDto(source.getCampaign()));
		target.setSelected(source.isSelected());

		return target;
	}
	
	
	private void selectDtoFields(CriteriaQuery<PopulationDataDto> cq, Root<PopulationData> root) {


		cq.multiselect(root.get(PopulationData.CAMPAIGN), root.get(PopulationData.DISTRICT), root.get(PopulationData.SELECTED));
	}
	
	public List<PopulationDataDto> getAllAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationDataDto> cq = cb.createQuery(PopulationDataDto.class);
		Root<PopulationData> district = cq.from(PopulationData.class);

		selectDtoFields(cq, district);

		Predicate filter = service.createChangeDateFilter(cb, district, date);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	@LocalBean
	@Stateless
	public static class PopulationDataFacadeEjbLocal extends PopulationDataFacadeEjb {

	}

	@Override
	public void savePopulationList(Set<PopulationDataDto> savePopulationList) {
		if (savePopulationList.size() > 0) {
			String buildsql = "";
			String buildsql_ = "";
			for (PopulationDataDto dfc : savePopulationList) {
				
				System.out.println(dfc + "========XXXXXXX");
				System.out.println(dfc.getCommunity() + "======XXXXXXXdfc");

				System.out.println(dfc.getCommunity().getUuid() + "===uuid===XXXXXXXdfc9999");

				
				System.out.println(communityService.getByUuid(dfc.getCommunity().getUuid()).getId() + "=getid=====XXXXXXXdfc9999");

				buildsql = buildsql +  communityService.getByUuid(dfc.getCommunity().getUuid()).getId() + ", ";

				// System.out.println("=============== ");
//				buildsql = buildsql + districtService.getByUuid(dfc.getDistrict().getUuid()).getId() + ", ";
				buildsql_ = campaignService.getByUuid(dfc.getCampaign().getUuid()).getId() + "";
				// dfc.getCampaign().getUuid()
			}
			buildsql = buildsql + "#";

			{

				String sqlstatemtnt = "update populationdata set selected = 'false' where campaign_id = " + buildsql_
						+ ";";
				System.out.println(sqlstatemtnt);
				em.createNativeQuery(sqlstatemtnt).executeUpdate();
			}

			{
				String sqlstatemtnt_ = "update populationdata set selected = 'true' where campaign_id = " + buildsql_
						+ "" + " and community_id in (" + buildsql.replace(", #", "") + ");";

				System.out.println(sqlstatemtnt_);
				em.createNativeQuery(sqlstatemtnt_).executeUpdate();
			}

			// TODO Auto-generated method stub
		}

	}

	@Override
	public Integer getDistrictPopulationByUuidAndAgeGroup(String districtUuid, String campaignUuid, String ageGroup) {
		// TODO Auto-generated method stub

		final String joinBuilder = "select population \n" + "from PopulationData population \n"
				+ "inner join campaigns campaign on population.campaign_id=campaign.id \n"
				+ "inner join community cluster on population.community_id=cluster.id \n" 
				+ "where campaign.uuid= '"
				+ campaignUuid + "' and cluster.uuid='" + districtUuid + "' and population.ageGroup='" + ageGroup
				+ "';";

//		System.out.println(districtUuid  +  campaignUuid +  ageGroup + "Credentials from backend OOOPPPPPP" +joinBuilder);

		try {
			return (Integer) em.createNativeQuery(joinBuilder).getSingleResult();

		} catch (NoResultException e) {
			return null;
		}

	}
	
	

	@Override
	public String getDistrictStatusByCampaign(String districtUuid, String campaignUuid, String ageGroup) {
		// TODO Auto-generated method stub
		final String joinBuilder = "select districtstatus \n" + "from PopulationData population \n"
				+ "inner join campaigns campaign on population.campaign_id=campaign.id \n"
				+ "inner join community district on population.community_id=district.id \n" + "where campaign.uuid= '"
				+ campaignUuid + "' and district.uuid='" + districtUuid + "' and population.ageGroup='" + ageGroup
				+ "';";
		try {
			return (String) em.createNativeQuery(joinBuilder).getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public String getDistrictModalityByUuidAndCampaignAndAgeGroup(String districtUuid, String campaignUuid,
			String ageGroup) {
		// TODO Auto-generated method stub
		final String joinBuilder = "select population.modality \n" + "from PopulationData population \n"
				+ "inner join campaigns campaign on population.campaign_id=campaign.id \n"
				+ "inner join community district on population.community_id=district.id \n" + "where campaign.uuid= '"
				+ campaignUuid + "' and district.uuid='" + districtUuid + "' and population.ageGroup='" + ageGroup
				+ "';";

		try {
			return (String) em.createNativeQuery(joinBuilder).getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<PopulationDataDto> getDistrictModalityByUUIDsandCampaignUUIdAndAgeGroup(String districtUuid,
			String campaignUuid, AgeGroup agegroup) {
		// TODO Auto-generated method stub
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);
		Join<PopulationData, District> districtJoin = root.join(PopulationData.DISTRICT);

//		System.out.println(districtUuid + "1111zzzzzzDEBUGGER 5678ijhyuioYYYYYY" + campaignUuid
//				+ "1111zzzzzzDEBUGGER 5678ijhyuioYYYYYY" + agegroup);

		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), campaignUuid));
		Predicate districtFilter = cb.and(cb.equal(districtJoin.get(District.UUID), districtUuid));
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), agegroup));

		cq.where(campaignFilter, districtFilter, ageFilter);

//		 System.out.println(//"resultData - "+ resultData.toString());
//		 "DUMBGFyyresultData - "+SQLExtractor.from(seriesDataQuery));

		System.out.println("1111zzzzzzDEBUGGER 5678ijhyuioYYYYYY" + SQLExtractor.from(em.createQuery(cq)));

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
	}

	
	@Override
	public List<PopulationDataDto> getDistrictModalityByclusterUUIDsandCampaignUUIdAndAgeGroup(String clusterUuid,
			String campaignUuid, AgeGroup agegroup) {
		// TODO Auto-generated method stub
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Campaign> campaignJoin = root.join(PopulationData.CAMPAIGN);
		Join<PopulationData, Community> clusterJoin = root.join(PopulationData.COMMUNITY);

//		System.out.println(districtUuid + "1111zzzzzzDEBUGGER 5678ijhyuioYYYYYY" + campaignUuid
//				+ "1111zzzzzzDEBUGGER 5678ijhyuioYYYYYY" + agegroup);

		Predicate campaignFilter = cb.and(cb.equal(campaignJoin.get(Campaign.UUID), campaignUuid));
		Predicate clusterFilter = cb.and(cb.equal(clusterJoin.get(District.UUID), clusterUuid));
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), agegroup));

		cq.where(campaignFilter, clusterFilter, ageFilter);

//		 System.out.println(//"resultData - "+ resultData.toString());
//		 "DUMBGFyyresultData - "+SQLExtractor.from(seriesDataQuery));

		System.out.println("1111zzzzzzDEBUGGER 5678ijhyuioYYYYYY" + SQLExtractor.from(em.createQuery(cq)));

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData))
				.collect(Collectors.toList());
	}

	
	@Override
	public void deletePopulationDataByUUId(String populationDataList,String ageGroup, String campaignUUID) {
		// TODO Auto-generated method stub

//		for (Long populationDataListItems : populationDataList) {

			String executeQuery = "DELETE FROM populationdata p \n" 
								+ "USING district d, campaigns c \n"
								+ "WHERE p.district_id = d.id \n" 
								+ "AND p.campaign_id = c.id \n"
								+ "AND p.uuid = '" + populationDataList + "'" 
//								+ " AND agegroup = " + "AGE_" + ageGroup
								+ " AND c.\"uuid\" = '" + campaignUUID + "';";

			
			System.out.println(executeQuery + "========Debuggerr ");
			// Create a native query
			Query query = em.createNativeQuery(executeQuery);

			// Execute the query
			query.executeUpdate();

//		}

	}

	@Override
	public void deletePopulationDataByDistrict(List<Long> populationDataList, String campaignUUID) {
		// TODO Auto-generated method stub

		for (Long populationDataListItems : populationDataList) {

			String executeQuery = "DELETE FROM populationdata p \n" 
								+ "USING district d, campaigns c \n"
								+ "WHERE p.district_id = d.id \n" 
								+ "AND p.campaign_id = c.id \n"
								+ "AND d.id = " + populationDataListItems
								+ " AND c.\"uuid\" = '" + campaignUUID + "';";

			
			System.out.println(executeQuery + "========Debuggerr ");
			// Create a native query
			Query query = em.createNativeQuery(executeQuery);

			// Execute the query
			query.executeUpdate();

		}

	}
	
	@Override
	public void deletePopulationDataByClusters(List<Long> populationDataList, String campaignUUID) {
		// TODO Auto-generated method stub

		for (Long populationDataListItems : populationDataList) {

			String executeQuery = "DELETE FROM populationdata p \n" 
								+ "USING community d, campaigns c \n"
								+ "WHERE p.community_id = d.id \n" 
								+ "AND p.campaign_id = c.id \n"
								+ "AND d.id = " + populationDataListItems
								+ " AND c.\"uuid\" = '" + campaignUUID + "';";

			
			System.out.println(executeQuery + "========Debuggerr ");
			// Create a native query
			Query query = em.createNativeQuery(executeQuery);

			// Execute the query
			query.executeUpdate();

		}

	}
	
	@Override
	public void deletePopulationDataByDistrictAndAgeGroup(List<Long> populationDataList, String campaignUUID, String ageGroup) {
		// TODO Auto-generated method stub

		for (Long populationDataListItems : populationDataList) {

			String executeQuery = "DELETE FROM populationdata p \n" 
								+ "USING district d, campaigns c \n"
								+ "WHERE p.district_id = d.id \n" 
								+ "AND p.campaign_id = c.id \n"
								+ "AND d.id = " + populationDataListItems 
								+ " AND p.agegroup = '" + ageGroup
								+ "' AND c.\"uuid\" = '" + campaignUUID + "';";

			
			System.out.println(executeQuery + "========Debuggerr ");
			// Create a native query
			Query query = em.createNativeQuery(executeQuery);

			// Execute the query
			query.executeUpdate();

		}

	}
	
	@Override
	public void deletePopulationDataByClusterAndAgeGroup(List<Long> populationDataList, String campaignUUID, String ageGroup) {
		// TODO Auto-generated method stub

		for (Long populationDataListItems : populationDataList) {

			String executeQuery = "DELETE FROM populationdata p \n" 
								+ "USING community d, campaigns c \n"
								+ "WHERE p.community_id = d.id \n" 
								+ "AND p.campaign_id = c.id \n"
								+ "AND d.id = " + populationDataListItems 
								+ " AND p.agegroup = '" + ageGroup
								+ "' AND c.\"uuid\" = '" + campaignUUID + "';";

			
			System.out.println(executeQuery + "========Debuggerr ");
			// Create a native query
			Query query = em.createNativeQuery(executeQuery);

			// Execute the query
			query.executeUpdate();

		}

	}
	
	@Override
	public void updateClusterSelectionByClusterIds(List<String> clusterUuids, String campaignUUID, boolean selected) {
		for (String clusterUuid : clusterUuids) {
			String executeQuery = "UPDATE populationdata p \n" 
								+ "SET selected = " + selected + " \n"
								+ "FROM community d, campaigns c \n"
								+ "WHERE p.community_id = d.id \n" 
								+ "AND p.campaign_id = c.id \n"
								+ "AND d.uuid = '" + clusterUuid + "'"
								+ " AND c.\"uuid\" = '" + campaignUUID + "';";

			System.out.println(executeQuery + "========Debuggerr Update Selection ");
			Query query = em.createNativeQuery(executeQuery);
			query.executeUpdate();
		}
	}
	
	
	@Override
	public List<PopulationDataDto> fetchPopulationDataSelectionByUserDistricts(List<String> uuids) {
	    // Validate input
	    if (uuids == null || uuids.isEmpty()) {
	        return Collections.emptyList();
	    }

	    // Base query using IN clause for multiple UUIDs
	    String executeQuery = "SELECT DISTINCT ON (p.campaign_id, p.community_id) c.uuid as campaign_id, d.uuid as district_id, com.uuid AS cluster_id, p.selected, p.uuid , p.changedate " +
	                          "FROM public.populationdata p " +
	                          "JOIN public.district d ON p.district_id = d.id  " +
	                          "LEFT JOIN public.community com ON p.community_id = com.id "+
	                          "left join public.campaigns c ON p.campaign_id = c.id " +
	                          "WHERE d.uuid IN :uuids AND p.selected = TRUE " +
	                          "ORDER BY p.campaign_id, p.community_id," +
	                          "CASE WHEN p.agegroup = '0_4' THEN 1 ELSE 2 END";

	    // Create the query
	    Query getFormExpressionsQuery = em.createNativeQuery(executeQuery);
	    getFormExpressionsQuery.setParameter("uuids", uuids);

	    // Fetch and map the results
	    @SuppressWarnings("unchecked")
		List<PopulationDataDto> resultData = new ArrayList<>();

	    List<Object[]> resultList = getFormExpressionsQuery.getResultList();
	    
		resultData.addAll(resultList.stream()
				.map((result) -> new PopulationDataDto(
						result[0] != null ? (String) result[0].toString() : "",
						result[1] != null ? (String) result[1].toString() : "",
						result[2] != null ? (String) result[2].toString() : "",
						result[3] != null ? (boolean) result[3].toString().equalsIgnoreCase("true") ? true : false : false, 
						result[4] != null ? (String) result[4].toString() : "",
						result[5] != null ? (Date) result[5] : new Date()
								)).collect(Collectors.toList()));
		

	    return resultData;
	}
	
//	public void generatePopulationDataForCamapign(String campaignUuid) {
//		
//	}

	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean generatePopulationDataForCamapign(String campaignUuid) {
	    try {
		String sql = "INSERT INTO public.populationdata (\n" + 
				"    uuid,\n" + 
				"    changedate,\n" + 
				"    creationdate,\n" + 
				"    region_id,\n" + 
				"    district_id,\n" + 
				"    community_id,\n" + 
				"    agegroup,\n" + 
				"    population,\n" + 
				"    campaign_id,\n" + 
				"    districtstatus,\n" + 
				"    modality\n" + 
				")\n" + 
				"SELECT\n" + 
				"    gen_random_uuid(),\n" + 
				"    now(),\n" + 
				"    now(),\n" + 
				"    r.id,\n" + 
				"    c.district_id,\n" + 
				"    c.id,\n" + 
				"    ag.agegroup,\n" + 
				"    ag.population,\n" + 
				"    c2.id,\n" + 
				"    'Full Cluster',\n" + 
				"    'H2H'\n" + 
				"FROM community c\n" + 
				"JOIN district d ON d.id = c.district_id\n" + 
				"JOIN region r ON r.id = d.region_id\n" + 
				"JOIN campaigns c2 \n" + 
				"    ON c2.uuid = :campaignUuid\n" + 
				"CROSS JOIN LATERAL (\n" + 
				"    VALUES\n" + 
				"        ('AGE_0_4',  c.populationdata_0_4),\n" + 
				"        ('AGE_5_10', c.populationdata_5_10),\n" +
				"        ('AGE_4_23M', c.populationdata_4_23M)\n" + 
				") AS ag(agegroup, population)\n" + 
				"WHERE c.archived = false\n" + 
				"  AND ag.population IS NOT NULL\n" + 
				"  AND ag.population >= 0;";

		Query query = em.createNativeQuery(sql);
		query.setParameter("campaignUuid", campaignUuid);
		query.executeUpdate();
		
		return true;
	    }catch(Exception e) {
	    	return false;
	    }
	}
	

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean generatePopulationDataForCamapign(CampaignDto campaignDto, List<AgeGroup> selectedAgeGroups) {
		try {
			// First, delete existing population data for this campaign to avoid duplicates
			String deleteSql = "DELETE FROM public.populationdata WHERE campaign_id = (SELECT id FROM campaigns WHERE uuid = :campaignUuid)";
			Query deleteQuery = em.createNativeQuery(deleteSql);
			deleteQuery.setParameter("campaignUuid", campaignDto.getUuid());
			deleteQuery.executeUpdate();

			List<String> selectedGroupNames = selectedAgeGroups.stream().map(AgeGroup::name).collect(Collectors.toList());

			String sql = "INSERT INTO public.populationdata (\n" + 
					"    uuid,\n" + 
					"    changedate,\n" + 
					"    creationdate,\n" + 
					"    region_id,\n" + 
					"    district_id,\n" + 
					"    community_id,\n" + 
					"    agegroup,\n" + 
					"    population,\n" + 
					"    campaign_id,\n" + 
					"    districtstatus,\n" + 
					"    modality,\n" + 
					"    selected\n" + 
					")\n" + 
					"SELECT\n" + 
					"    gen_random_uuid(),\n" + 
					"    now(),\n" + 
					"    now(),\n" + 
					"    r.id,\n" + 
					"    c.district_id,\n" + 
					"    c.id,\n" + 
					"    ag.agegroup,\n" + 
					"    CASE WHEN ag.agegroup IN (:selectedGroups) THEN COALESCE(ag.population, 0) ELSE 0 END,\n" + 
					"    c2.id,\n" + 
					"    c.status,\n" + 
					"    c.modality,\n" + 
					"     true \n" + 
					"FROM community c\n" + 
					"JOIN district d ON d.id = c.district_id\n" + 
					"JOIN region r ON r.id = d.region_id\n" + 
					"JOIN campaigns c2 \n" + 
					"    ON c2.uuid = :campaignUuid\n" + 
					"CROSS JOIN LATERAL (\n" + 
					"    VALUES\n" + 
					"        ('AGE_0_4',  c.populationdata_0_4),\n" + 
					"        ('AGE_5_10', c.populationdata_5_10),\n" + 
					"        ('AGE_4_23M', c.populationdata_4_23M)\n" + 
					") AS ag(agegroup, population)\n" + 
					"WHERE c.archived = false;";

			Query query = em.createNativeQuery(sql);
			query.setParameter("campaignUuid", campaignDto.getUuid());
			query.setParameter("selectedGroups", selectedGroupNames);
			query.executeUpdate();
			
			
	

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	

	

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean generatePopulationDataForCamapignByRegionAndPopulationType(CampaignDto campaignDto, List<AgeGroup> selectedAgeGroups, List<AreaReferenceDto> selectedRegions){
		try {
			// First, delete existing population data for this campaign to avoid duplicates
			String deleteSql = "DELETE FROM public.populationdata WHERE campaign_id = (SELECT id FROM campaigns WHERE uuid = :campaignUuid)";
			Query deleteQuery = em.createNativeQuery(deleteSql);
			deleteQuery.setParameter("campaignUuid", campaignDto.getUuid());
			deleteQuery.executeUpdate();

		    List<String> selectedRegionUuids = selectedRegions.stream() .map(AreaReferenceDto::getUuid).collect(Collectors.toList());
			List<String> selectedGroupNames = selectedAgeGroups.stream().map(AgeGroup::name).collect(Collectors.toList());

			String sql = "INSERT INTO public.populationdata (\n" + 
					"    uuid,\n" + 
					"    changedate,\n" + 
					"    creationdate,\n" + 
					"    region_id,\n" + 
					"    district_id,\n" + 
					"    community_id,\n" + 
					"    agegroup,\n" + 
					"    population,\n" + 
					"    campaign_id,\n" + 
					"    districtstatus,\n" + 
					"    modality,\n" + 
					"    selected\n" + 
					")\n" + 
					"SELECT\n" + 
					"    gen_random_uuid(),\n" + 
					"    now(),\n" + 
					"    now(),\n" + 
					"    r.id,\n" + 
					"    c.district_id,\n" + 
					"    c.id,\n" + 
					"    ag.agegroup,\n" + 
					"    CASE WHEN ag.agegroup IN (:selectedGroups) THEN COALESCE(ag.population, 0) ELSE 0 END,\n" + 
					"    c2.id,\n" + 
					// Temporaryr fix : ensure clusters saves modality captions same for status 
                "    CASE CAST(c.status AS TEXT)\n" +
                "        WHEN 'Additional' THEN 'Additional'\n" +
                "        WHEN 'AdditionalCold' THEN 'Additional & Cold'\n" +
                "        WHEN 'Cold' THEN 'Cold'\n" +
                "        WHEN 'FullCluster' THEN 'Full Cluster'\n" +
                "        WHEN 'HRMPOnly' THEN 'HRMP Only'\n" +
                "        WHEN 'Partial' THEN 'Partial'\n" +
                "        WHEN 'NotTargeted' THEN 'Not Targeted'\n" +
                "        WHEN 'OnHold' THEN 'On Hold'\n" +
                "        ELSE CAST(c.status AS TEXT)\n" +
                "    END,\n" +
                
                // MODALITY DISPLAY VALUE
                "    CASE CAST(c.modality AS TEXT)\n" +
                "        WHEN 'H2H' THEN 'H2H'\n" +
                "        WHEN 'M2M' THEN 'M2M'\n" +
                "        WHEN 'S2S' THEN 'S2S'\n" +
                "        WHEN 'HF2HF' THEN 'HF2HF'\n" +
                "        WHEN 'Mixed' THEN 'Mixed'\n" +
                "        ELSE CAST(c.modality AS TEXT)\n" +
                "    END,\n" +
                
                
//					"    c.status,\n" + 
//					"    c.modality,\n" + 
					"     true \n" + 
					"FROM community c\n" + 
					"JOIN district d ON d.id = c.district_id\n" + 
					"JOIN region r ON r.id = d.region_id\n" + 
					"JOIN areas a ON a.id = r.area_id\n" + 
					"JOIN campaigns c2 \n" + 
					"    ON c2.uuid = :campaignUuid\n" + 
					"CROSS JOIN LATERAL (\n" + 
					"    VALUES\n" + 
					"        ('AGE_0_4',  c.populationdata_0_4),\n" + 
					"        ('AGE_5_10', c.populationdata_5_10),\n" + 
					"        ('AGE_4_23M', c.populationdata_4_23M)\n" + 
					") AS ag(agegroup, population)\n" + 
					"WHERE c.archived = false AND a.uuid IN (:selectedRegionUuids);";

			Query query = em.createNativeQuery(sql);
			query.setParameter("campaignUuid", campaignDto.getUuid());
			query.setParameter("selectedGroups", selectedGroupNames);
			query.setParameter("selectedRegionUuids", selectedRegionUuids);

			query.executeUpdate();
			
			
	

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	

}