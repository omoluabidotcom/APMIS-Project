/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class CampaignDtoHelper extends AdoDtoHelper<Campaign, CampaignDto> {

    public static CampaignReferenceDto toReferenceDto(Campaign ado) {
        if (ado == null) {
            return null;
        }
        CampaignReferenceDto dto = new CampaignReferenceDto(ado.getUuid());
        return dto;
    }

    @Override
    protected Class<Campaign> getAdoClass() {
        return Campaign.class;
    }

    @Override
    protected Class<CampaignDto> getDtoClass() {
        return CampaignDto.class;
    }

    @Override
    protected Call<List<CampaignDto>> pullAllSince(long since) throws NoConnectionException {
        System.out.println("=======================99999999999999999000000000" + RetroProvider.getCampaignFacade().pullAllSince(since));
        return RetroProvider.getCampaignFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<CampaignDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        System.out.println("=======================9999999999999999900000000043" + RetroProvider.getCampaignFacade().pullByUuids(uuids));

        return RetroProvider.getCampaignFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<CampaignDto> campaignDtos) throws NoConnectionException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void fillInnerFromDto(Campaign target, CampaignDto source) {
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
        target.setCreatingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getCreatingUser()));
        System.out.println(source.getName() + "=======================9999999999999999900000000043" + source.campaignStatus);

        target.setClosed(source.campaignStatus != null ? source.campaignStatus.equalsIgnoreCase("True") ? true :  false : false );
        final Set<CampaignFormMetaReferenceDto> campaignFormMetaReferenceDtos = source.getCampaignFormMetas();
        if (campaignFormMetaReferenceDtos != null) {
            target.setCampaignFormMetas(new ArrayList<>(campaignFormMetaReferenceDtos));
        }
    }

    @Override
    protected void fillInnerFromAdo(CampaignDto dto, Campaign campaign) {
        throw new UnsupportedOperationException();
    }
}
