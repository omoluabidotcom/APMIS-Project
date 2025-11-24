package de.symeda.sormas.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataHistoryExtractDto;
import de.symeda.sormas.api.devicemanager.DeviceManagerDto;
import de.symeda.sormas.api.infrastructure.area.AreaHistoryExtractDto;

@Path("/deviceInfo")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class DeviceInfoResource extends EntityDtoResource {


//	@GET
//	@Path("/all/{since}")
//	public List<DeviceManagerDto> getAllCampaignFormData(@PathParam("since") long since) {
//		return new ArrayList<DeviceManagerDto>();
//	}
//
//	@POST
//	@Path("/query")
//	public List<CampaignFormDataDto> getByUuids(List<String> uuids) {
//		return FacadeProvider.getCampaignFormDataFacade().getByUuids(uuids);
//	}

	@POST
	@Path("/push")
	public List<PushResult> postDeviceInformation(@Valid List<DeviceManagerDto> dtos) {
		System.out.println("Request recieved on device into pusdh ====================");

		return savePushedDto(dtos, FacadeProvider.getDeviceManagerFacade()::saveDeviceDetailsMobile);
	}

//	@GET
//	@Path("/uuids")
//	public List<String> getAllUuids() {
//		return FacadeProvider.getCampaignFormDataFacade().getAllActiveUuids();
//	}
	
}
