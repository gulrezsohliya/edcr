package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.B;
import static org.egov.client.constants.DxfFileConstants_AR.IN;
import static org.egov.client.constants.DxfFileConstants_AR.R;
import static org.egov.client.constants.DxfFileConstants_AR.R1a;
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
import org.egov.edcr.feature.WaterTreatmentPlant;
import org.springframework.stereotype.Service;

@Service
public class WaterTreatmentPlant_Tripura extends WaterTreatmentPlant {

	private static final Logger LOG = Logger.getLogger(WaterTreatmentPlant_Tripura.class);

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Waste Water Treatment");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, PERMITTED);
			scrutinyDetail.addColumnHeading(3, PROVIDED);
			scrutinyDetail.addColumnHeading(4, STATUS);
			BigDecimal coveredArea = null;
			BigDecimal wasteDisposal = BigDecimal.ZERO;
			for (Block b : pl.getBlocks()) {
				coveredArea = b.getBuilding().getCoverageArea();
			}
			if (pl.getUtility().getWasteDisposalUnits() != null && !pl.getUtility().getWasteDisposalUnits().isEmpty()) {
				for (WasteDisposal wd : pl.getUtility().getWasteDisposalUnits()) {
					wasteDisposal = wd.getArea();
				}
			}
			Boolean req=false;

			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;

			if (mostRestrictiveOccupancy != null
					&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				if (R1b.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())) {
					req=true;
				} else if (R1c.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())) {
					req=true;
				}

			} else if (mostRestrictiveOccupancy != null
					&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				req=true;
			} else if (mostRestrictiveOccupancy != null
					&& (IN.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				req=true;
			}
			if(req) {
				if (coveredArea.compareTo(BigDecimal.valueOf(10000)) > 0
						|| wasteDisposal.compareTo(BigDecimal.valueOf(10000)) > 0) {
					if (pl.getUtility() != null && !pl.getUtility().getWasteWaterRecyclePlant().isEmpty()
							&& pl.getUtility().getWasteWaterRecyclePlant() != null) {

						setReportOutputDetails(pl, "110 (a) (3)", "Waste Water Management", "Defined in the plan",
								Result.Verify.getResultVal(), scrutinyDetail);
					} else {
						setReportOutputDetails(pl, "110 (a) (3)", "Waste Water Management", "Not Defined in the plan",
								Result.Not_Accepted.getResultVal(), scrutinyDetail);
					}
				}
			}else {
				if (pl.getUtility() != null && !pl.getUtility().getWasteWaterRecyclePlant().isEmpty()
						&& pl.getUtility().getWasteWaterRecyclePlant() != null) {

					setReportOutputDetails(pl, "110 (a) (3)", "Waste Water Management", "Defined in the plan",
							Result.Verify.getResultVal(), scrutinyDetail);
				} 
			}
			

		} catch (Exception e) {
			// TODO: handle exception
		}
		return pl;
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
