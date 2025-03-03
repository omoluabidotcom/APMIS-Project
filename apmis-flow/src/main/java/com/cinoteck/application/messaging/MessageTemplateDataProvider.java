package com.cinoteck.application.messaging;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.messaging.MessageCriteria;
import de.symeda.sormas.api.messaging.MessageDto;
import de.symeda.sormas.api.messaging.MessageTemplateCriteria;
import de.symeda.sormas.api.messaging.MessageTemplateDto;

public class MessageTemplateDataProvider extends AbstractBackEndDataProvider<MessageTemplateDto, MessageTemplateCriteria> {
	
	@SuppressWarnings("unchecked")
	@Override
	protected Stream<MessageTemplateDto> fetchFromBackEnd(Query<MessageTemplateDto, MessageTemplateCriteria> query) {	
		return FacadeProvider.getMessageFacade().getIndexListForMessageTemplate(
				 query.getFilter().orElse(null),
                 query.getOffset(),
                 query.getLimit(),
                 null).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<MessageTemplateDto, MessageTemplateCriteria> query) {		
		return (int) FacadeProvider.getMessageFacade().count(query.getFilter().orElse(null));
	}

}
