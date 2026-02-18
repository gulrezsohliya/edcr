package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Door;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.ExitWidth;
import org.springframework.stereotype.Service;

@Service
public class ExitWidth_Tripura extends ExitWidth{
	private static final Logger LOG = Logger.getLogger(ExitWidth_Tripura.class);
	private static final String EXIT_WIDTH = "Exit Width";
	private static final String SUBRULE_54_F_4_1_b = "54 F(4)1(b)";
	public static final BigDecimal EXIT_WIDTH_0_75 = BigDecimal.valueOf(0.75);
	public static final BigDecimal EXIT_WIDTH_1= BigDecimal.valueOf(1);
	@Override
	public Plan validate(Plan plan) {
		try {
			Double widthofdoor=Double.valueOf(0);
			Double exitwidthDoor = Double.valueOf(0);
			Double requiredWidthofdoor=Double.valueOf(0);
			Double requiredExitwidthDoor = Double.valueOf(0);
			String subRule = null;
			Boolean status=false;
			OccupancyTypeHelper mostRestrictiveOccupancyType = Util_Tripura.getMostRestrictive(plan);
			for (Block block : plan.getBlocks()) {
				Occupancy occupancy = new Occupancy();
				occupancy.setTypeHelper(block.getBuilding().getMostRestrictiveFarHelper());
				if ((occupancy.getTypeHelper().getType() != null
						&& A.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode()))) { 
					for (Floor floor : block.getBuilding().getFloors()) {
						for(Door door:floor.getDoors()) {
							if(door.getWidths()!=null)
								widthofdoor=door.getWidths().stream().mapToDouble(BigDecimal::doubleValue).sum();
						}
						if(floor.getExitWidthDoor()!=null)
							exitwidthDoor=floor.getExitWidthDoor().stream().mapToDouble(BigDecimal::doubleValue).sum();
					}
					if(widthofdoor>0 && exitwidthDoor>0) {
						subRule=SUBRULE_54_F_4_1_b;
						requiredWidthofdoor=EXIT_WIDTH_0_75.doubleValue();
						requiredExitwidthDoor=EXIT_WIDTH_1.doubleValue();
						if(requiredWidthofdoor<=widthofdoor && requiredExitwidthDoor<=exitwidthDoor) {
							status=true;
						}
						buidResult(widthofdoor,requiredWidthofdoor,exitwidthDoor,requiredExitwidthDoor,mostRestrictiveOccupancyType,subRule,plan,status);
					}
					
				}
				
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	
		return plan;
	}

	private void buidResult(Double widthofdoor, Double requiredWidthofdoor, Double exitwidthDoor,
			Double requiredExitwidthDoor, OccupancyTypeHelper mostRestrictiveOccupancyType, String subRule, Plan plan,
			Boolean status) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Access Width");
		scrutinyDetail.setHeading("Exit Width");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, "Occupancy");
		scrutinyDetail.addColumnHeading(4, "Width Of Door".concat(" in meters"));
		scrutinyDetail.addColumnHeading(5, "Required Width Of Door".concat(" in meters"));
		scrutinyDetail.addColumnHeading(6, "Exit Width Of Door".concat(" in meters"));
		scrutinyDetail.addColumnHeading(7, "Required Exit Width Of Door".concat(" in meters"));
		scrutinyDetail.addColumnHeading(8, STATUS);
		
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, subRule);
		details.put(DESCRIPTION, "Exit Width");
		details.put("Occupancy", mostRestrictiveOccupancyType.getType().getName());
		details.put("Width Of Door", widthofdoor.toString());
		details.put("Required Width Of Door", requiredWidthofdoor.toString());
		details.put("Exit Width Of Door", exitwidthDoor.toString());
		details.put("Required Exit Width Of Door", requiredExitwidthDoor.toString());
		if(status==true)
			details.put(STATUS, "Accepted");
		else
			details.put(STATUS, "Not Accepted");
		scrutinyDetail.getDetail().add(details);
	    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	@Override
	public Plan process(Plan plan) {
		try {
			validate(plan);
		}catch (Exception e) {
			// TODO: handle exception
		}
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	
	}
}
