/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.campaign.form;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaRegionDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaRegionReferenceDto;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class CampaignFormMetaRegionDtoHelper extends AdoDtoHelper<CampaignFormMetaRegion, CampaignFormMetaRegionDto> {

    public static CampaignFormMetaRegionReferenceDto toReferenceDto(CampaignFormMetaRegion ado) {
        if (ado == null) {
            return null;
        }
        CampaignFormMetaRegionReferenceDto dto = new CampaignFormMetaRegionReferenceDto(ado.getUuid());
        return dto;
    }

    @Override
    protected Class<CampaignFormMetaRegion> getAdoClass() {
        return CampaignFormMetaRegion.class;
    }

    @Override
    protected Class<CampaignFormMetaRegionDto> getDtoClass() {
        return CampaignFormMetaRegionDto.class;
    }

    @Override
    protected Call<List<CampaignFormMetaRegionDto>> pullAllSince(long since) throws NoConnectionException {
        return RetroProvider.getCampaignFormMetaRegionFacade().fetchSelectionnByUserArea();
    }

    @Override
    protected Call<List<CampaignFormMetaRegionDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getCampaignFormMetaRegionFacade().fetchSelectionnByUserArea();
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<CampaignFormMetaRegionDto> campaignFormMetaRegionDtos) throws NoConnectionException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void fillInnerFromDto(CampaignFormMetaRegion target, CampaignFormMetaRegionDto source) {
        target.setUuid(source.getUuid());
        target.setUuid(source.getUuid());
        target.setCampaignformmeta_id(source.getCampaignformmeta_id());
        target.setArea_id(source.getArea_id());

    }

    @Override
    protected void fillInnerFromAdo(CampaignFormMetaRegionDto dto, CampaignFormMetaRegion campaignFormMeta) {
        dto.setUuid(campaignFormMeta.getUuid());
        dto.setCampaignformmeta_id(campaignFormMeta.getCampaignformmeta_id());
        dto.setArea_id(campaignFormMeta.getArea_id());    }
}
