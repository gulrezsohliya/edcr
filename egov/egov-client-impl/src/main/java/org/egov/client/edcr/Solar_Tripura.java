package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.B;
import static org.egov.client.constants.DxfFileConstants_AR.R;
import static org.egov.client.constants.DxfFileConstants_AR.R1a;
import static org.egov.client.constants.DxfFileConstants_AR.R1b;
import static org.egov.client.constants.DxfFileConstants_AR.R1c;
import static org.egov.client.constants.DxfFileConstants_AR.IN;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Solar;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Solar_Tripura extends Solar {

	private static final Logger LOG = Logger.getLogger(Solar_Tripura.class);

	private static final String RULE_110_a_3 = "110 (a) (3)";

	private static final String RULE_51_DESCRIPTION = "Solar Panel";

	@Override
	public Plan validate(Plan plan) {

		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Solar Panel");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, PERMITTED);
			scrutinyDetail.addColumnHeading(3, PROVIDED);
			scrutinyDetail.addColumnHeading(4, STATUS);

			BigDecimal BUA = BigDecimal.ZERO;

			for (Block b : pl.getBlocks()) {
				for (Floor f : b.getBuilding().getFloors()) {
					if (f.getNumber() == 0) {
						for (Occupancy occ : f.getOccupancies()) {
							BUA = BUA.add(occ.getBuiltUpArea());
						}
					}
				}
			}
			System.out.println("Ground Floor BUA=" + BUA);
			String expected = DcrConstants.OBJECTDEFINED_DESC;
			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;
			if (mostRestrictiveOccupancy != null
					&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				if (R1a.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())) {
					if (BUA.compareTo(BigDecimal.valueOf(500)) >= 0) {
						addReportOutput(pl, RULE_110_a_3, RULE_51_DESCRIPTION, expected);
					}
				} else if (R1b.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())) {
					addReportOutput(pl, RULE_110_a_3, RULE_51_DESCRIPTION, expected);
				} else if (R1c.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())) {
					if (BUA.compareTo(BigDecimal.valueOf(500)) >= 0) {
						addReportOutput(pl, RULE_110_a_3, RULE_51_DESCRIPTION, expected);
					}

				}

			} else if (mostRestrictiveOccupancy != null
					&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				if (BUA.compareTo(BigDecimal.valueOf(500)) >= 0) {
					addReportOutput(pl, RULE_110_a_3, RULE_51_DESCRIPTION, expected);
				}
			} else if (mostRestrictiveOccupancy != null
					&& (IN.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				if (BUA.compareTo(BigDecimal.valueOf(500)) >= 0) {
					addReportOutput(pl, RULE_110_a_3, RULE_51_DESCRIPTION, expected);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;
	}

	private void addReportOutput(Plan pl, String subRule, String subRuleDesc, String expectedTankCapacity) {
		if (pl.getUtility() != null && !pl.getUtility().getSolar().isEmpty() && pl.getUtility().getSolar() != null) {
			setReportOutputDetails(pl, "110 (a) (3)", "Solar Panel", "Defined in the plan",
					Result.Verify.getResultVal(), scrutinyDetail);
		} else {
			setReportOutputDetails(pl, "110 (a) (3)", "Solar Panel", "Not Defined in the plan",
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

}
