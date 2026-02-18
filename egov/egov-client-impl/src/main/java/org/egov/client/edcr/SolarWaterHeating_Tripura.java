package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.B;
import static org.egov.client.constants.DxfFileConstants_AR.I;
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
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.SolarWaterHeating;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class SolarWaterHeating_Tripura extends SolarWaterHeating{

	private static final Logger LOG = Logger.getLogger(SolarWaterHeating_Tripura.class);
	private static final String RULE_110_a_3 = "110 (a) (3)";

	private static final String RULE_51_DESCRIPTION = "Solar Water Heating";
	private static final String RAINWATER_HARVESTING_TANK_CAPACITY = "Minimum capacity of Rain Water Harvesting Tank";
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final String RWH_DECLARATION_ERROR = DxfFileConstants.RWH_DECLARED
			+ " in PLAN_INFO layer must be declared as YES for plot area greater than 100 sqm.";

	private static final String RAINWATER_HARVESTING = "Rainwater Harvesting";
	private static final String RAINWATER_HARVES_TANKCAPACITY = "Rainwater Harvest Tank Capacity";
	
	@Override
	public Plan validate(Plan plan) {
		process(plan);
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			HashMap<String, String> errors = new HashMap<>();

			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, REQUIRED);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);
			scrutinyDetail.setKey("Common_Rain Water Harvesting");
			BigDecimal BUA=BigDecimal.ZERO;
			
			for(Block b : pl.getBlocks() ) {
				for(Floor f : b.getBuilding().getFloors()) {
					if(f.getNumber()==0) {
						for(Occupancy occ : f.getOccupancies()) {
							BUA=BUA.add(occ.getBuiltUpArea());
						}
					}
				}
			}
			System.out.println("Ground Floor BUA="+BUA);
			String expected = DcrConstants.OBJECTDEFINED_DESC;
			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;
//			if (mostRestrictiveOccupancy != null
//					&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
//				if(R1a.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())){
//					if(BUA.compareTo(BigDecimal.valueOf(150))>=0) {
//						addReportOutput(pl, RULE_110_a_3, RULE_51_DESCRIPTION, expected);
//					}
//				}else if(R1c.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedSubtype().getCode())){
//					addReportOutput(pl, RULE_110_a_3, RULE_51_DESCRIPTION, expected);
//				}
//				
//			}else if (mostRestrictiveOccupancy != null
//					&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
//				if(BUA.compareTo(BigDecimal.valueOf(50))>=0) {
//					addReportOutput(pl, RULE_110_a_3, RULE_51_DESCRIPTION, expected);
//				}
//			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return pl;
	}
	private void addReportOutput(Plan pl, String subRule, String subRuleDesc, String expectedTankCapacity) {
		if (pl.getUtility() != null) {
			if (pl.getUtility().getSolarWaterHeatingSystems() != null
					&& !pl.getUtility().getSolarWaterHeatingSystems().isEmpty()) {
				setReportOutputDetails(pl, subRule, subRuleDesc, "", "Defined in the plan",
						Result.Verify.getResultVal());
			} else {
				setReportOutputDetails(pl, subRule, subRuleDesc, "", "Not Defined in the plan",
						Result.Not_Accepted.getResultVal());
			}
		}
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
			String status) {
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
