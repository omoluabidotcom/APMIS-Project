package de.symeda.sormas.app.backend.campaign.usertoken;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.messaging.FCMTokenDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class FCMTokenDtoHelper extends AdoDtoHelper<FCMToken, FCMTokenDto> {

    @Override
    protected Class<FCMToken> getAdoClass() {
        return null;
    }

    @Override
    protected Class<FCMTokenDto> getDtoClass() {
        return null;
    }

    @Override
    protected Call<List<FCMTokenDto>> pullAllSince(long since) throws NoConnectionException {
        return null;
    }

    @Override
    protected Call<List<FCMTokenDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return null;
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<FCMTokenDto> fcmTokenDtos) throws NoConnectionException {
        return null;
    }

    @Override
    protected void fillInnerFromDto(FCMToken fcmToken, FCMTokenDto dto) {

    }

    @Override
    protected void fillInnerFromAdo(FCMTokenDto dto, FCMToken fcmToken) {

    }
}
