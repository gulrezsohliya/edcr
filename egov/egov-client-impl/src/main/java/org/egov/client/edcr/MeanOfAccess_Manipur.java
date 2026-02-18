package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.MeanOfAccess;
import org.springframework.stereotype.Service;

@Service
public class MeanOfAccess_Manipur extends MeanOfAccess {

	private static final Logger LOG = Logger.getLogger(MeanOfAccess_Manipur.class);
	private static final String ACCESS_WIDTH = "Access Width";
	private static final String SUBRULE_54_C_A = "54C(a)";
	private static final String SUBRULE_54_C_B = "54C(b)";
	private static final String SUBRULE_54_C_C = "54C(c)";
	private static final String SUBRULE_54_C_D = "54C(d)";
	public static final BigDecimal MEANS_ACCESS_9 = BigDecimal.valueOf(9);

	@Override
	public Plan validate(Plan plan) {

		String rule = ACCESS_WIDTH;
		String subRule = null;
		BigDecimal meansofAccess = BigDecimal.ZERO;
		Boolean status = false;
		try {
			OccupancyTypeHelper mostRestrictiveOccupancyType = Util_Manipur.getMostRestrictive(plan);

			boolean isHighRise = false;
			for (Block block : plan.getBlocks()) {
				Occupancy occupancy = new Occupancy();
				BigDecimal requiredAccessWidth = BigDecimal.ZERO;
				occupancy.setTypeHelper(block.getBuilding().getMostRestrictiveFarHelper());
				if (plan.getPlanInformation().getAccessWidth() != null) {
					if (block.getBuilding().getIsHighRise() != null)
						isHighRise = block.getBuilding().getIsHighRise();
					if (isHighRise) {
						requiredAccessWidth = MEANS_ACCESS_9;
						subRule = SUBRULE_54_C_A;
						if (plan.getPlanInformation().getAccessWidth() != null)
							meansofAccess = plan.getPlanInformation().getAccessWidth();
						else
							plan.addError("Means Of Access Not Found", "Means Of Access Not defined");
						if (meansofAccess.compareTo(requiredAccessWidth) >= 0)
							status = true;
						buildResult(meansofAccess, requiredAccessWidth, mostRestrictiveOccupancyType, rule, subRule,
								plan, status);
					}else {
						if(block.getBuilding().getMaxFloor().compareTo(BigDecimal.valueOf(3))<=0) {
							requiredAccessWidth = BigDecimal.valueOf(3.6);
							subRule = "Section 30";
							if (plan.getPlanInformation().getAccessWidth() != null)
								meansofAccess = plan.getPlanInformation().getAccessWidth();
							else
								plan.addError("Means Of Access Not Found", "Means Of Access Not defined");
							if (meansofAccess.compareTo(requiredAccessWidth) >= 0)
								status = true;
							buildResult(meansofAccess, requiredAccessWidth, mostRestrictiveOccupancyType, rule, subRule,
									plan, status);
						}else {
							requiredAccessWidth = BigDecimal.valueOf(5);
							subRule = "Section 30";
							if (plan.getPlanInformation().getAccessWidth() != null)
								meansofAccess = plan.getPlanInformation().getAccessWidth();
							else
								plan.addError("Means Of Access Not Found", "Means Of Access Not defined");
							if (meansofAccess.compareTo(requiredAccessWidth) >= 0)
								status = true;
							buildResult(meansofAccess, requiredAccessWidth, mostRestrictiveOccupancyType, rule, subRule,
									plan, status);
						}
					}
				}
					
					
						

			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		return plan;
	}

	private void buildResult(BigDecimal meansofAccess, BigDecimal requiredAccessWidth,
			OccupancyTypeHelper mostRestrictiveOccupancyType, String rule, String subRule, Plan plan, Boolean status) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Access Width");
		scrutinyDetail.setHeading("Means Of Access");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, OCCUPANCY);
		scrutinyDetail.addColumnHeading(4, REQUIRED.concat(" in meters"));
		scrutinyDetail.addColumnHeading(5, PROVIDED.concat(" in meters"));
		scrutinyDetail.addColumnHeading(6, STATUS);

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, subRule);
		details.put(DESCRIPTION, rule);
		details.put(OCCUPANCY, mostRestrictiveOccupancyType.getType().getName());
		details.put(REQUIRED, requiredAccessWidth.toString());
		details.put(PROVIDED, meansofAccess.toString());
		if (status == true)
			details.put(STATUS, "Accepted");
		else
			details.put(STATUS, "Not Accepted");

		scrutinyDetail.getDetail().add(details);
		plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

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
