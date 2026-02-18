package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import static org.egov.edcr.utility.DcrConstants.OBJECTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.VirtualBuilding;
import org.egov.common.entity.edcr.WasteWaterRecyclePlant;
import org.egov.edcr.feature.RecycleWasteWater;
import org.springframework.stereotype.Service;

@Service
public class RecycleWasteWater_Manipur extends RecycleWasteWater {
	public static final BigDecimal COVERED_AREA_500 = BigDecimal.valueOf(500);
	public static final BigDecimal DISCHARGE_10000 = BigDecimal.valueOf(10000);
	public static final BigDecimal RULE_NO_62 = BigDecimal.valueOf(62);
	

	@Override
	public Plan validate(Plan plan) {
		 HashMap<String, String> errors = new HashMap<>(); 
		 BigDecimal wasteWaterDischarge=null;
		 String rule="";
		try {
			OccupancyTypeHelper mostRestrictiveOccupancyType = Util_Manipur.getMostRestrictive(plan);
			for(Block block :plan.getBlocks()) {
				Boolean status=false;
				Occupancy occupancy = new Occupancy();
				occupancy.setTypeHelper(block.getBuilding().getMostRestrictiveFarHelper());
				if ((occupancy.getTypeHelper().getType() != null
						&& R.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode()))) { 
//					if(plan.getCoverage().compareTo(COVERED_AREA_500)>=0) {
//						if(plan.getUtility().getWasteWaterRecyclePlant()!=null) {
//							for(WasteWaterRecyclePlant wasteWater:plan.getUtility().getWasteWaterRecyclePlant()) {
//								wasteWaterDischarge=wasteWater.getArea();
//								if(wasteWaterDischarge.compareTo(DISCHARGE_10000)>=0)
//									status=true;
//								rule=RULE_NO_62.toString();
//								buildResult(plan,wasteWaterDischarge,DISCHARGE_10000,status,rule);
//							}
//							
//						}else {
//							errors.put("WasteWater", "Recycling of waste water not defined in the plan");
//							
//						}
//					}
				}
					
				
			}
			
		}catch (Exception e) {
		}	
            
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		
		return pl;
	}
	private void buildResult(Plan pl, BigDecimal wasteWaterDischarge,BigDecimal expected, Boolean status,String rule) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Waste Water Recycle Plant");
		scrutinyDetail.setHeading("Coverage in Percentage");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(4, "Discharge in m²");
		scrutinyDetail.addColumnHeading(5, "Expected in m²");
		scrutinyDetail.addColumnHeading(6, STATUS);

		String desc = "Recycle Waste Water";
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, rule);
		details.put(DESCRIPTION, "Waste Water Recycle Plant");
		details.put("Discharge in m²", wasteWaterDischarge.toString());
		details.put("Expected in m²", expected.toString());
		if(status)
			details.put(STATUS, "Accepted");
		else
			details.put(STATUS, "Not Accepted");

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}



}
