package org.egov.client.edcr;


import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.feature.AccessoryBuildingService;
import org.springframework.stereotype.Service;

@Service
public class AccessoryBuildingService_Tripura extends AccessoryBuildingService {


	@Override
	public Plan validate(Plan plan) {


		return plan;
	}

	@Override
	public Plan process(Plan plan) {

		if (plan != null)
			validate(plan);

		return plan;
	}

	
}
