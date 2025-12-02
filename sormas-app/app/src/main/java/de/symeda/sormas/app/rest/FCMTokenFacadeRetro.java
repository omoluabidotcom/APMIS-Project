package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.messaging.FCMTokenDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FCMTokenFacadeRetro {
    @POST("fcmtoken/push")
    Call<List<PushResult>> pushAll(@Body List<FCMTokenDto> dtos);
}
