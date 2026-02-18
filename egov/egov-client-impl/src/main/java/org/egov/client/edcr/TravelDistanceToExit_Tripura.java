package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import static org.egov.client.constants.DxfFileConstants_AR.I;
import static org.egov.client.constants.DxfFileConstants_AR.R;
import static org.egov.client.constants.DxfFileConstants_AR.R1b;
import static org.egov.client.constants.DxfFileConstants_AR.R1c;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.WasteDisposal;
import org.egov.edcr.feature.TravelDistanceToExit;
import org.springframework.stereotype.Service;

@Service
public class TravelDistanceToExit_Tripura extends TravelDistanceToExit {

	private static final Logger LOG = Logger.getLogger(TravelDistanceToExit_Tripura.class);

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {

			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Distance Between Exit");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, PERMITTED);
			scrutinyDetail.addColumnHeading(3, PROVIDED);
			scrutinyDetail.addColumnHeading(4, STATUS);

			Boolean req = false;
			BigDecimal permissibleExit = null;
			BigDecimal distanceToExit = null;

			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;

			if (mostRestrictiveOccupancy != null
					&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
					permissibleExit = BigDecimal.valueOf(22.5);

			} else if (mostRestrictiveOccupancy != null
					&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				permissibleExit = BigDecimal.valueOf(22.5);
			} else if (mostRestrictiveOccupancy != null
					&& (IN.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				permissibleExit = BigDecimal.valueOf(22.5);
			}
			if (pl.getDistanceToExit() != null && !pl.getDistanceToExit().isEmpty()) {
				for (BigDecimal d : pl.getDistanceToExit()) {
					distanceToExit = d;
					if (d.compareTo(permissibleExit) <= 0) {
						setReportOutputDetails(pl, "110 (a) (3)", "Distance To Exit", permissibleExit, distanceToExit,
								Result.Accepted.getResultVal(), scrutinyDetail);
					} else {
						setReportOutputDetails(pl, "110 (a) (3)", "Distance To Exit", permissibleExit, distanceToExit,
								Result.Not_Accepted.getResultVal(), scrutinyDetail);
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return pl;
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, BigDecimal permissibleExit,
			BigDecimal distanceToExit, String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, permissibleExit.toString());
		details.put(PROVIDED, distanceToExit.toString());
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();

	}
}
