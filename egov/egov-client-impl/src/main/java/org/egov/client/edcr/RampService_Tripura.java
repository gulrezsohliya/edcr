package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Door;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Ramp;
import org.egov.edcr.feature.RampService;
import org.springframework.stereotype.Service;

@Service
public class RampService_Tripura extends RampService{

	private static final Logger LOG = Logger.getLogger(RampService_Tripura.class);
	private static final String RAMP_SERVICE = "Ramps";
	private static final String SUBRULE_54_F_7_a = "54 F(7)(a)";
	private static final String SUBRULE_54_F_7_d = "54 F(7)(d)";
	private static final String SUBRULE_54_F_7_f = "54 F(7)(f)";
	public static final BigDecimal RAMP_WIDTH_0_75 = BigDecimal.valueOf(7.2);
	public static final BigDecimal RAMP_WIDTH_4 = BigDecimal.valueOf(4);
	public static final BigDecimal RAMP_GRADIENT_0_1= BigDecimal.valueOf(0.1);//1:10
	public static final BigDecimal RAMP_GRADIENT_0_067= BigDecimal.valueOf(0.067);//1:15
	@Override
	public Plan validate(Plan plan) {
		OccupancyTypeHelper mostRestrictiveOccupancyType = Util_Tripura.getMostRestrictive(plan);
		String subRule="";
		for (Block block : plan.getBlocks()) {
			Occupancy occupancy = new Occupancy();
			occupancy.setTypeHelper(block.getBuilding().getMostRestrictiveFarHelper());
			if ((occupancy.getTypeHelper().getType() != null
					&& A.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode()))) { 
				for (Floor floor : block.getBuilding().getFloors()) {
					if(floor.getNumber()<0) {
						for(Ramp ramp:floor.getRamps()) {
						System.out.println(ramp.getWidth());
						System.out.println(ramp.getSlope());//Gradient
					}
					}

				}
				subRule=SUBRULE_54_F_7_a;
//				requiredWidthofdoor=EXIT_WIDTH_0_75.doubleValue();
//				requiredExitwidthDoor=EXIT_WIDTH_1.doubleValue();
//				if(requiredWidthofdoor<=widthofdoor && requiredExitwidthDoor<=exitwidthDoor) {
//					status=true;
//				}
//				buidResult(widthofdoor,requiredWidthofdoor,exitwidthDoor,requiredExitwidthDoor,mostRestrictiveOccupancyType,subRule,plan,status);
			}
		}
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		try {
		validate(plan);
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
