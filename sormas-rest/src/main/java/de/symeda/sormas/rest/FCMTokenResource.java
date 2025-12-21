package de.symeda.sormas.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.messaging.FCMTokenDto;
import de.symeda.sormas.api.user.UserDto;

@Path("/fcmtoken")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class FCMTokenResource extends EntityDtoResource {

	@POST
	@Path("/push")
	public List<PushResult> postFCMToken(@Valid List<FCMTokenDto> dtos) {
		System.out.println("ACTIVEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!!! ");
		UserDto userToUpdate = FacadeProvider.getUserFacade().getByUserName(dtos.get(0).getUserName());
		userToUpdate.setToken(dtos.get(0).getToken());
		return savePushedDto(List.of(userToUpdate), FacadeProvider.getUserFacade()::saveUserFcmMobile);
	}
}
