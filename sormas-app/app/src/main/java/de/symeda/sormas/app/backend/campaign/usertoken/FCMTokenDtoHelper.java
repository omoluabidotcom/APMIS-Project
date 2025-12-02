package de.symeda.sormas.app.backend.campaign.usertoken;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.messaging.FCMTokenDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
 
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class FCMTokenDtoHelper extends AdoDtoHelper<FCMToken, FCMTokenDto> {

    @Override
    protected Class<FCMToken> getAdoClass() {
        return FCMToken.class;
    }

    @Override
    protected Class<FCMTokenDto> getDtoClass() {
        return FCMTokenDto.class;
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
        return RetroProvider.getFCMTokenFacade().pushAll(fcmTokenDtos);
    }

    @Override
    protected void fillInnerFromDto(FCMToken target, FCMTokenDto source) {}

    @Override
    protected void fillInnerFromAdo(FCMTokenDto target, FCMToken source) {
        target.setToken(source.getToken());
        target.setUserName(source.getUserName());
    }
}
