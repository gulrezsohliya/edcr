package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import static org.egov.client.constants.DxfFileConstants_AR.R;
import static org.egov.client.constants.DxfFileConstants_AR.R1a;
import static org.egov.client.constants.DxfFileConstants_AR.R1c;
import static org.egov.client.constants.DxfFileConstants_AR.R1b;
import static org.egov.client.constants.DxfFileConstants_AR.I;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.WasteDisposal;
import org.egov.edcr.feature.SolidLiquidWasteTreatment;
import org.springframework.stereotype.Service;

@Service
public class SolidLiquidWasteTreatment_Tripura extends SolidLiquidWasteTreatment {

	private static final Logger LOG = Logger.getLogger(SolidLiquidWasteTreatment_Tripura.class);

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub

		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Waste Management");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, PERMITTED);
			scrutinyDetail.addColumnHeading(3, PROVIDED);
			scrutinyDetail.addColumnHeading(4, STATUS);
			BigDecimal coveredArea = null;
			Boolean req = false;
			BigDecimal plotArea = pl.getPlot().getArea();
			for (Block b : pl.getBlocks()) {
				coveredArea = b.getBuilding().getCoverageArea();
			}

			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;
			if (mostRestrictiveOccupancy != null
					&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				if (R1a.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())) {
					if (plotArea.compareTo(BigDecimal.valueOf(5000)) > 0) {
						req=true;
						addReportOutput(pl, "", "");
					}
				} else if (R1b.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())) {
					if (coveredArea.compareTo(BigDecimal.valueOf(10000)) >= 0) {
						req=true;
						addReportOutput(pl, "", "");
					}
				} else if (R1c.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())) {
					if (coveredArea.compareTo(BigDecimal.valueOf(10000)) >= 0) {
						req=true;
						addReportOutput(pl, "", "");
					}

				}

			} else if (mostRestrictiveOccupancy != null
					&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				if (coveredArea.compareTo(BigDecimal.valueOf(10000)) >= 0) {
					req=true;
					addReportOutput(pl, "", "");
				}
			} else if (mostRestrictiveOccupancy != null
					&& (IN.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				if (coveredArea.compareTo(BigDecimal.valueOf(10000)) >= 0
						|| plotArea.compareTo(BigDecimal.valueOf(5000)) >= 0) {
					req=true;
					addReportOutput(pl, "", "");
				}
			}
			if(!req) {
				if (pl.getUtility() != null && !pl.getUtility().getSolidLiqdWasteTrtmnt().isEmpty()
						&& pl.getUtility().getSolidLiqdWasteTrtmnt() != null) {

					setReportOutputDetails(pl, "110 (a) (3)", "Solid Waste Management", "Defined in the plan",
							Result.Verify.getResultVal(), scrutinyDetail);
				} 
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return pl;
	}

	private void addReportOutput(Plan pl, String subRule, String subRuleDesc) {
		if (pl.getUtility() != null && !pl.getUtility().getSolidLiqdWasteTrtmnt().isEmpty()
				&& pl.getUtility().getSolidLiqdWasteTrtmnt() != null) {

			setReportOutputDetails(pl, "110 (a) (3)", "Solid Waste Management", "Defined in the plan",
					Result.Verify.getResultVal(), scrutinyDetail);
		} else {
			setReportOutputDetails(pl, "110 (a) (3)", "Solid Waste Management", "Not Defined in the plan",
					Result.Not_Accepted.getResultVal(), scrutinyDetail);
		}
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String actual, String status,
			ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
//		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
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
