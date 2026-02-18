package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Door;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Ramp;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.RampService;
import org.springframework.stereotype.Service;

@Service
public class RampService_Manipur extends RampService{

	private static final Logger LOG = Logger.getLogger(RampService_Manipur.class);
	private static final String RAMP_SERVICE = "Ramps";
	private static final String SUBRULE_54_F_7_a = "54 F(7)(a)";
	private static final String SUBRULE_54_F_7_d = "54 F(7)(d)";
	private static final String SUBRULE_54_F_7_f = "54 F(7)(f)";
	public static final BigDecimal RAMP_WIDTH_0_72 = BigDecimal.valueOf(7.2);
	public static final BigDecimal RAMP_WIDTH_4 = BigDecimal.valueOf(4);
	public static final BigDecimal RAMP_GRADIENT_0_1= BigDecimal.valueOf(0.1);//1:10
	public static final BigDecimal RAMP_GRADIENT_0_067= BigDecimal.valueOf(0.067);//1:15
	@Override
	public Plan validate(Plan plan) {
		OccupancyTypeHelper mostRestrictiveOccupancyType = Util_Manipur.getMostRestrictive(plan);
		String subRule="";
		
		BigDecimal rampWidth=null;
		BigDecimal rampslope=null;
		BigDecimal requiredrampWidth=null;
		BigDecimal requiredrampSlope=null;
		for (Block block : plan.getBlocks()) {
			Boolean status=false;
			
			Occupancy occupancy = new Occupancy();
			occupancy.setTypeHelper(block.getBuilding().getMostRestrictiveFarHelper());
//			if ((occupancy.getTypeHelper().getType() != null
//					&& R.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode()))) { 
				for (Floor floor : block.getBuilding().getFloors()) {
						if(floor.getRamps()!=null && floor.getVehicleRamps()!=null) {
							for(Ramp ramp:floor.getRamps()) {
								if(ramp!=null)
									rampWidth=ramp.getWidth();
							}
							
							for(Ramp vehicleRamp:floor.getVehicleRamps()) {
								if(vehicleRamp!=null)
									rampslope=vehicleRamp.getSlope();
							}
							
						}

					}
				if(rampWidth==null)
					plan.addError("Ramp Width Error", "Ramp Width not defined ");
				if(rampslope==null)
					plan.addError("Ramp Slope Error", "Ramp Gradient not defined");
				
				subRule=SUBRULE_54_F_7_a;
				if(plan.getPlanInfoProperties().get("RAMP_TWO_WAY")!=null) {
					if(plan.getPlanInfoProperties().get("RAMP_TWO_WAY").equalsIgnoreCase("YES"))
						requiredrampWidth=RAMP_WIDTH_0_72;
				}
				if(plan.getPlanInfoProperties().get("RAMP_ONE_WAY")!=null) {
					if(plan.getPlanInfoProperties().get("RAMP_ONE_WAY").equalsIgnoreCase("YES"))
						requiredrampWidth=RAMP_WIDTH_4;
				}
				if(plan.getPlanInfoProperties().get("RAMP_CARS")!=null) {
					if(plan.getPlanInfoProperties().get("RAMP_CARS").equalsIgnoreCase("YES"))
						requiredrampSlope=RAMP_GRADIENT_0_1;
				}
				if(plan.getPlanInfoProperties().get("RAMP_HEAVY_VEHICLES")!=null) {
					if(plan.getPlanInfoProperties().get("RAMP_HEAVY_VEHICLES").equalsIgnoreCase("YES"))
						requiredrampSlope=RAMP_GRADIENT_0_067;
				}
					
				
				if(requiredrampWidth.compareTo(rampWidth)>=0  && requiredrampSlope.compareTo(rampslope)>=0) {
					status=true;
				}
				buidResult(rampWidth,requiredrampWidth,rampslope,requiredrampSlope,mostRestrictiveOccupancyType,subRule,plan,status,block);
//			}
		}
		return plan;
	}
	
	private void buidResult(BigDecimal rampWidth, BigDecimal requiredrampWidth, BigDecimal rampslope,
			BigDecimal requiredrampSlope, OccupancyTypeHelper mostRestrictiveOccupancyType, String subRule, Plan plan,
			Boolean status,Block block) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		 scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Ramp");
		scrutinyDetail.setHeading("Ramp");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, "Occupancy");
		scrutinyDetail.addColumnHeading(4, "Width Of Ramp".concat(" in meters"));
		scrutinyDetail.addColumnHeading(5, "Required Width Of Ramp".concat(" in meters"));
		scrutinyDetail.addColumnHeading(6, "Slope of Ramp".concat(" in meters"));
		scrutinyDetail.addColumnHeading(7, "Required Slope of Ramp".concat(" in meters"));
		scrutinyDetail.addColumnHeading(8, STATUS);
		
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, subRule);
		details.put(DESCRIPTION, "Ramp");
		details.put("Occupancy", mostRestrictiveOccupancyType.getType().getName());
		details.put("Width Of Door", rampWidth.toString());
		details.put("Required Width Of Door", requiredrampWidth.toString());
		details.put("Exit Width Of Door", rampslope.toString());
		details.put("Required Exit Width Of Door", requiredrampSlope.toString());
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
//		validate(plan);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return plan;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	
	}
}
