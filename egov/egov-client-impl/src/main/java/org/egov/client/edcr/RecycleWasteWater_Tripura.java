package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A;
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
import org.egov.edcr.feature.RecycleWasteWater;
import org.springframework.stereotype.Service;

@Service
public class RecycleWasteWater_Tripura extends RecycleWasteWater {
	public static final BigDecimal COVERED_AREA_500 = BigDecimal.valueOf(500);
	

	@Override
	public Plan validate(Plan plan) {
		 HashMap<String, String> errors = new HashMap<>(); 
//		try {
//			OccupancyTypeHelper mostRestrictiveOccupancyType = Util_Tripura.getMostRestrictive(plan);
//			for(Block block :plan.getBlocks()) {
//				Boolean isPresent=false;
//				Occupancy occupancy = new Occupancy();
//				occupancy.setTypeHelper(block.getBuilding().getMostRestrictiveFarHelper());
//				if ((occupancy.getTypeHelper().getType() != null
//						&& A.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode()))) { 
//					if(plan.getPlot().getArea().compareTo(COVERED_AREA_500)>=0) {//Checking plot area in place of covered area
//						if(plan.getUtility().getWasteWaterRecyclePlant()==null) {
//							errors.put("WasteWater", "Recycling of waste water not defined in the plan");
//						}else {
//							isPresent=true;
//							buildResult(plan, plan.getPlot().getArea(),isPresent);
//						}
//					}
//				}else {
//					plan.addError("Occupancy Not Found","Occupancy Not defined");
//				}
//					
//				
//			}
//			
//		}catch (Exception e) {
//		}	
//            
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		
		return pl;
	}
	private void buildResult(Plan pl, BigDecimal coveredArea,Boolean isPresent) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Waste Water Recycle Plant");
		scrutinyDetail.setHeading("Coverage in Percentage");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(4, "Covered Area in m²");
		scrutinyDetail.addColumnHeading(6, STATUS);

		String desc = "Recycle Waste Water";
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "Section 62");
		details.put(DESCRIPTION, desc);
		details.put("Covered Area in m²", coveredArea.toString());
		if(isPresent)
			details.put(STATUS, "Accepted");
		else
			details.put(STATUS, "Not Accepted");

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}



}
