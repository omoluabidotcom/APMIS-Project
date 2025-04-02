package com.cinoteck.application.views.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.district.DistrictCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.api.infrastructure.region.RegionIndexDto;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.SortProperty;

public class UsersDataProvider extends AbstractBackEndDataProvider<UserDto, UserCriteria>{
	
	private static final long serialVersionUID = 7345965237429493032L;

	@Override
	protected Stream<UserDto> fetchFromBackEnd(Query<UserDto, UserCriteria> query) {
//
//		Stream<UserDto> stream = DATABASE.stream();
//
//		if (query.getFilter().isPresent()) {
//			stream = stream.filter(person -> query.getFilter().get().equals(person));
//		}
//
//		if (query.getSortOrders().size() > 0) {
//			stream = stream.sorted(sortComparator(query.getSortOrders()));
//		}
		
	    List<SortProperty> sortProperties = null;
	    if (!query.getSortOrders().isEmpty()) {
	        sortProperties = query.getSortOrders().stream()
	                .map(order -> new SortProperty(
	                        order.getSorted(),
	                        order.getDirection() == SortDirection.ASCENDING
	                ))
	                .collect(Collectors.toList());
	    }
	    
	    return FacadeProvider.getUserFacade()
	            .getIndexList(
	                    query.getFilter().orElse(null),  
	                    query.getOffset(),               
	                    query.getLimit(),                
	                    sortProperties)                  
	            .stream();

	}

	@Override
	protected int sizeInBackEnd(Query<UserDto, UserCriteria> query) {

		 return (int) FacadeProvider.getUserFacade().count(query.getFilter().orElse(null));
//		return (int) fetchFromBackEnd(query).count();
	}
	
	private static Comparator<UserDto> sortComparator(List<QuerySortOrder> sortOrders) {
		return sortOrders.stream().map(sortOrder -> {
			Comparator<UserDto> comparator = personFieldComparator(sortOrder.getSorted());

			if (sortOrder.getDirection() == SortDirection.ASCENDING) {
				comparator = comparator.reversed();
			}

			return comparator;
		}).reduce(Comparator::thenComparing).orElse((p1, p2) -> 0);
	}

	private static Comparator<UserDto> personFieldComparator(String sorted) {
	        if (sorted.equals("user")) {
	            return Comparator.comparing(person -> person.toString());
	        } 
	        return (p1, p2) -> 0;
	 }
}