package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.egov.client.constants.DxfFileConstants_AR.B;
import static org.egov.client.constants.DxfFileConstants_AR.IN;
import static org.egov.client.constants.DxfFileConstants_AR.R;
import static org.egov.client.constants.DxfFileConstants_AR.R1a;
import static org.egov.client.constants.DxfFileConstants_AR.R1b;
import static org.egov.client.constants.DxfFileConstants_AR.R1c;
import static org.egov.edcr.utility.DcrConstants.IN_LITRE;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.WaterTankCapacity;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;


@Service
public class WaterTankCapacity_Tripura extends WaterTankCapacity {
	private static final String RULE_59_10_vii = "TBD";
	private static final String RULE_59_10_vii_DESCRIPTION = "Water tank capacity";
	private static final String WATER_TANK_CAPACITY = "Water tank Capacity";
	private static final BigDecimal TWELVE_POINTFIVE = BigDecimal.valueOf(12.5);
	private static final BigDecimal ONEHUNDRED_THIRTYFIVE = BigDecimal.valueOf(135);

	private static final Logger LOG = Logger.getLogger(WaterTankCapacity_Tripura.class);

	@Override
	public Plan validate(Plan pl) {

		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, REQUIRED);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);
			scrutinyDetail.setKey("Common_Water tank capacity");
			String subRule = RULE_59_10_vii;
			String subRuleDesc = RULE_59_10_vii_DESCRIPTION;
			BigDecimal expectedWaterTankCapacity = BigDecimal.ZERO;
			BigDecimal bua=pl.getVirtualBuilding().getTotalBuitUpArea();
			BigDecimal buildingHeiht=null;
			Boolean valid=false;
			for(Block b : pl.getBlocks()) {
				buildingHeiht= b.getBuilding().getBuildingHeight();
			}
			Boolean req=false;
			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;
			if (mostRestrictiveOccupancy != null
					&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
					if(bua.compareTo(BigDecimal.valueOf(1000))>0 || buildingHeiht.compareTo(BigDecimal.valueOf(15))>0) {
						req=true;
					}
					
				
			}else if (mostRestrictiveOccupancy != null
					&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				if(bua.compareTo(BigDecimal.valueOf(500))>0 || buildingHeiht.compareTo(BigDecimal.valueOf(15))>0) {
					req=true;
				}
			}else if (mostRestrictiveOccupancy != null
					&& (IN.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				if(bua.compareTo(BigDecimal.valueOf(500))>0 || buildingHeiht.compareTo(BigDecimal.valueOf(15))>0) {
					req=true;
				}
			}
			
			if(req) {
				if (pl.getUtility() != null && pl.getVirtualBuilding() != null
						&& pl.getUtility().getWaterTankCapacity() != null) {
					valid=true;
					
				}else {
					valid=false;
				}
				processWaterTankCapacity(pl, "", subRule, subRuleDesc, expectedWaterTankCapacity, valid);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return pl;
	}

	private void processWaterTankCapacity(Plan plan, String rule, String subRule, String subRuleDesc,
			BigDecimal expectedWaterTankCapacity, Boolean valid) {
			if (valid) {
				setReportOutputDetails(plan, subRule, WATER_TANK_CAPACITY, expectedWaterTankCapacity.toString(),
						"Defined in the plan", Result.Accepted.getResultVal());
			} else {
				setReportOutputDetails(plan, subRule, WATER_TANK_CAPACITY,
						expectedWaterTankCapacity.toString() + IN_LITRE,
						"Not Defined in the plan",
						Result.Not_Accepted.getResultVal());
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
		return super.getAmendments();

	}
}
