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

package de.symeda.sormas.app.backend.region;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.PopulationDataReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class PopulationDataDtoHelper extends AdoDtoHelper<PopulationData, PopulationDataDto> {
    public static PopulationDataReferenceDto toReferenceDto(PopulationData ado) {
        if (ado == null) {
            return null;
        }
        PopulationDataReferenceDto dto = new PopulationDataReferenceDto(ado.getUuid());
        return dto;
    }
    @Override
    protected Class<PopulationData> getAdoClass() {
        return PopulationData.class;
    }
    @Override
    protected Class<PopulationDataDto> getDtoClass() {
        return PopulationDataDto.class;
    }
    @Override
    protected Call<List<PopulationDataDto>> pullAllSince(long since) throws NoConnectionException {
//        return RetroProvider.getPopulationDataFacade().pullAllSince(since);

        return RetroProvider.getPopulationDataFacade().fetchPopulationDataSelectionByUserDistricts();
    }
    @Override
    protected Call<List<PopulationDataDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getPopulationDataFacade().fetchPopulationDataSelectionByUserDistricts();
    }
    @Override
    protected Call<List<PushResult>> pushAll(List<PopulationDataDto> populationDataDtos) throws NoConnectionException {
       return null;
    }
    @Override
    protected void fillInnerFromDto(PopulationData target, PopulationDataDto source) {
        if (source.getUuid() == null) {
            throw new IllegalArgumentException("UUID cannot be null in PopulationDataDto");
        }

        // Set the UUID first
        target.setUuid(source.getUuid());
        target.setCampaign_id(source.getCampaign_id());
        target.setDistrict_id(source.getDistrict_id());
        target.setSelected(source.getSelected());
    }

    @Override
    protected void fillInnerFromAdo(PopulationDataDto target, PopulationData source) {
        target.setCampaign_id(source.getCampaign_id());
        target.setDistrict_id(source.getDistrict_id());
        target.setSelected(source.isSelected());
    }

}

