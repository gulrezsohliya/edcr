package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import static org.egov.client.constants.DxfFileConstants_AR.I;
import static org.egov.client.constants.DxfFileConstants_AR.R;
import static org.egov.client.constants.DxfFileConstants_AR.R1b;
import static org.egov.client.constants.DxfFileConstants_AR.R1c;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Passage;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Parapet;
import org.egov.edcr.feature.PassageService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class PassageService_ML extends PassageService {

	@Override
	public Plan validate(Plan pl) {

		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		try {

			for (Block block : pl.getBlocks()) {
				scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Passage");
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, PERMITTED);
				scrutinyDetail.addColumnHeading(4, PROVIDED);
				scrutinyDetail.addColumnHeading(5, STATUS);

				Boolean req = false;
				BigDecimal permissible = null;
				BigDecimal passageWidth = null;

				OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
						? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
						: null;

				if (mostRestrictiveOccupancy != null
						&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
					if (block.getBuilding().getPassage().getColorCode() == 1)
						permissible = BigDecimal.valueOf(0.9);
					else if (block.getBuilding().getPassage().getColorCode() == 2)
						permissible = BigDecimal.valueOf(1);

				} else if (mostRestrictiveOccupancy != null
						&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
					permissible = BigDecimal.valueOf(2.5);
				} else if (mostRestrictiveOccupancy != null
						&& (IN.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
					permissible = BigDecimal.valueOf(1.5);
				}
				if (block.getBuilding().getPassage() != null) {
					passageWidth = block.getBuilding().getPassage().getWidth();
					if (passageWidth.compareTo(permissible) >= 0) {
						setReportOutputDetails(pl, "110 (a) (3)", "Distance To Exit", permissible, passageWidth,
								Result.Accepted.getResultVal(), scrutinyDetail);
					} else {
						setReportOutputDetails(pl, "110 (a) (3)", "Distance To Exit", permissible, passageWidth,
								Result.Not_Accepted.getResultVal(), scrutinyDetail);
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, BigDecimal permissible,
			BigDecimal passageWidth, String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, permissible.toString());
		details.put(PROVIDED, passageWidth.toString());
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

}
