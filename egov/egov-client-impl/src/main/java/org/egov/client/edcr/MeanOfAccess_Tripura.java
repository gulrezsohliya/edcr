package org.egov.client.edcr;
import static org.egov.client.constants.DxfFileConstants_AR.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.NotifiedRoad;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.MeanOfAccess;
import org.springframework.stereotype.Service;

@Service
public class MeanOfAccess_Tripura extends MeanOfAccess {

	private static final String ACCESS_WIDTH = "Access Width";
	public static final BigDecimal MEANS_ACCESS_9 = BigDecimal.valueOf(9);
	private static final BigDecimal MEANS_ACCESS_8 = BigDecimal.valueOf(8);
	private static final BigDecimal MEANS_ACCESS_10 = BigDecimal.valueOf(10);
	private static final BigDecimal MEANS_ACCESS_1_8 = BigDecimal.valueOf(1.8);

	@Override
	public Plan validate(Plan pl) {
		HashMap<String, String> errors = new HashMap<String, String>();
		
		String rule = "TBD 2017";
		String subRule = "TBD 2017";
		Boolean status = false;
		BigDecimal internalWidth=BigDecimal.ZERO;
		if (pl.getNotifiedRoads() != null) {
			for (NotifiedRoad n : pl.getNotifiedRoads()) {
				if (n.getWidth() != null ) {
					if (n.getColorCode() == 5) {
						internalWidth=n.getWidth();	
					}
					
				}
			}
		}
		int blocks=0;
		for(Block block : pl.getBlocks()) {
			blocks=blocks+1;
		}
		
		if(blocks>1) {
			if(internalWidth.compareTo(BigDecimal.ZERO)>0) {
				if(internalWidth.compareTo(BigDecimal.valueOf(1.2))>=0) {
					status = true;
				}
				buildResult(internalWidth, BigDecimal.valueOf(1.2), null, rule, subRule, pl, status);
			}else {
				pl.addError("InyternalError", "PLease provide internal road for multi blocks. Please provide notified road with color code =5");
			}
		}

		return pl;
	}

	private void buildResult(BigDecimal roadWidth, BigDecimal required,
			OccupancyTypeHelper mostRestrictiveOccupancyType, String rule, String subRule, Plan pl, Boolean status) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Internal Road Width");
		scrutinyDetail.setHeading("Internal Road Width");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//		scrutinyDetail.addColumnHeading(3, OCCUPANCY);
		scrutinyDetail.addColumnHeading(4, REQUIRED);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, subRule);
		details.put(DESCRIPTION, "Internal Road Width");
//		details.put(OCCUPANCY, mostRestrictiveOccupancyType.getType().getName());
		details.put(REQUIRED, required.toString().concat(" in meters"));
		details.put(PROVIDED, roadWidth.toString().concat(" in meters"));
		if (status == true)
			details.put(STATUS, "Accepted");
		else
			details.put(STATUS, "Not Accepted");

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

	}

	@Override
	public Plan process(Plan plan) {
		validate(plan);
		return plan;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	}
}
