package org.egov.client.edcr;


import static org.egov.client.constants.DxfFileConstants_AR.R;
import static org.egov.client.constants.DxfFileConstants_AR.R1b;
import static org.egov.client.constants.DxfFileConstants_AR.R1c;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Balcony;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Balcony_AR extends Balcony{
	
	private static final Logger LOG = Logger.getLogger(Balcony_AR.class);
	
	private static final String RULE_NO_TBD = "TBD";
	private static final String FLOOR = "Floor";

	@Override
	public Plan validate(Plan plan) {
		try {
			for (Block block : plan.getBlocks()) {
				OccupancyTypeHelper mostRestrictiveOccupancyType = Util_AR.getMostRestrictive(plan);
				if (block.getBuilding() != null) {
					scrutinyDetail = new ScrutinyDetail();
					scrutinyDetail.addColumnHeading(1, RULE_NO);
					scrutinyDetail.addColumnHeading(2, FLOOR);
					scrutinyDetail.addColumnHeading(3, DESCRIPTION);
					scrutinyDetail.addColumnHeading(4, PERMISSIBLE);
					scrutinyDetail.addColumnHeading(5, PROVIDED);
					scrutinyDetail.addColumnHeading(6, STATUS);
					scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Balcony");
					List<Floor> floors = block.getBuilding().getFloors();
					String ruleDesc = RULE_NO_TBD;
					OccupancyTypeHelper occupancyType = new OccupancyTypeHelper();
					if (block.getBuilding().getMostRestrictiveFarHelper() != null) {
						occupancyType = block.getBuilding().getMostRestrictiveFarHelper();
					}
					for (Floor floor : floors) {
						List<org.egov.common.entity.edcr.Balcony> balconies = floor.getBalconies();
						if (!balconies.isEmpty()) {
							for (org.egov.common.entity.edcr.Balcony balcony : balconies) {
								BigDecimal area = BigDecimal.ZERO;
								if (( balcony.getWidths() != null && !balcony.getWidths().isEmpty())) {
									for(BigDecimal w : balcony.getWidths()) {
										area=area.add(w);
									}
								}
								area = area.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
										DcrConstants.ROUNDMODE_MEASUREMENTS);

//								if ((mostRestrictiveOccupancyType.getType() != null
//										&& R.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode()))||mostRestrictiveOccupancyType.getSubtype().getCode().equals(R1b)
//										|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(R1c)) {
											ruleDesc = "4.30.1.c";
//										}
									
								
								setReportOutputDetailsFloorBalconyWise(plan, ruleDesc,
										FloorNames.getFloorNames(floor.getNumber()), "Balcony Depth", BigDecimal.valueOf(1.2),
										area, Result.Verify.getResultVal(), scrutinyDetail);
							}
							}
						}
					}
				}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		if (plan != null)
			try {
				validate(plan);
			} catch (Exception e) {
				e.printStackTrace();
			}

		return plan;
	}

	private void setReportOutputDetailsFloorBalconyWise(Plan pl, String ruleNo, String floor, String description,
			BigDecimal expected, BigDecimal actual, String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(FLOOR, floor);
		details.put(DESCRIPTION, description);
        details.put(PERMISSIBLE, expected.toString());
		details.put(PROVIDED, actual.toString());
		if(actual.compareTo(expected)<=0 && actual.compareTo(BigDecimal.ZERO)>0)
			details.put(STATUS, Result.Accepted.getResultVal());
		else
			details.put(STATUS, Result.Not_Accepted.getResultVal());
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

}
