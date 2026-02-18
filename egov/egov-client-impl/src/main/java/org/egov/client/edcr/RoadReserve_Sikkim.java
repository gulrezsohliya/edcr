package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Road;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.RoadReserve;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class RoadReserve_Sikkim extends RoadReserve {

	private static final Logger LOG = Logger.getLogger(RoadReserve_Sikkim.class);

	private static final String ROAD_RESERVE_DESC = "Road Reserve";
	private static final String RULE_4_18_2I_II = "4 18 2i && ii";
	private static final String RULE_DESC = "Rule Description";

	private static final String ROAD_RESERVE_FRONT = "ROAD_RESERVE_FRONT";
	private static final String ROAD_RESERVE_REAR = "ROAD_RESERVE_REAR";
	private static final String ROAD_RESERVE_SIDE1 = "ROAD_RESERVE_SIDE1";
	private static final String ROAD_RESERVE_SIDE2 = "ROAD_RESERVE_SIDE2";

	private static final String ROAD_RESERVE_FRONT_DESC = "Road Reserve Front";
	private static final String ROAD_RESERVE_REAR_DESC = "Road Reserve Rear";
	private static final String ROAD_RESERVE_SIDE1_DESC = "Road Reserve Side 1";
	private static final String ROAD_RESERVE_SIDE2_DESC = "Road Reserve Side 2";

	private static final String DECLARED = "Declared";

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
	
		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Road Reserve");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, DECLARED);
//		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);

		String ruleNo = StringUtils.EMPTY, ruleDesc = StringUtils.EMPTY;

		BigDecimal ROAD_RESERVE = BigDecimal.ZERO, roadReserve = BigDecimal.ZERO, minDistance = BigDecimal.ZERO;

		/*
		 * String temp[] = new String[2], roadReserveTemp =
		 * plan.getPlanInfoProperties().entrySet().stream() .filter(e ->
		 * e.getKey().equals("ROAD_RESERVE")).map(Map.Entry::getValue).findFirst().
		 * orElse(null);
		 * 
		 * if (roadReserveTemp != null && !roadReserveTemp.isEmpty()) { temp =
		 * roadReserveTemp.split("_", 2); try { ROAD_RESERVE = new BigDecimal(temp[0]);
		 * MIN_ROAD_RESERVE from planInfo } catch (NumberFormatException nfe) {
		 * ROAD_RESERVE = BigDecimal.ZERO; } }
		 */
		List<Road> roadReserves = null;
		if (plan.getRoadReserves() != null)
			roadReserves = plan.getRoadReserves();
		if (roadReserves != null && !roadReserves.isEmpty()) {

			for (Road r : roadReserves) {
				if (r.getShortestDistanceToRoad() != null && !r.getShortestDistanceToRoad().isEmpty()) {
					minDistance = r.getShortestDistanceToRoad().stream().reduce(BigDecimal::min).get();
//					minDistance=minDistance.divide(BigDecimal.valueOf(10));
					
					if(r.getName()!=null&&!r.getName().isEmpty()) {
						switch (r.getName()) {
						case ROAD_RESERVE_FRONT:
							ruleDesc = ROAD_RESERVE_FRONT_DESC;
							break;

						case ROAD_RESERVE_REAR:
							ruleDesc = ROAD_RESERVE_REAR_DESC;
							break;

						case ROAD_RESERVE_SIDE1:
							ruleDesc = ROAD_RESERVE_SIDE1_DESC;
							break;

						case ROAD_RESERVE_SIDE2:
							ruleDesc = ROAD_RESERVE_SIDE2_DESC;
							break;

						default:
							ruleDesc = "";
						}
					}
					System.out.println("Minimum Distance="+minDistance);
					minDistance = minDistance.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
							DcrConstants.ROUNDMODE_MEASUREMENTS);
					setReportOutputDetails(plan, scrutinyDetail, RULE_4_18_2I_II, ruleDesc, ROAD_RESERVE.toString(),
							minDistance.toString() + DcrConstants.IN_METER, Result.Verify.getResultVal());
				}
				
				/*
				 * if (ROAD_RESERVE.compareTo(minDistance) <= 0) setReportOutputDetails(plan,
				 * scrutinyDetail, ruleNo, ruleDesc, ROAD_RESERVE.toString(),
				 * minDistance.setScale(2, RoundingMode.FLOOR).toString(),
				 * Result.Accepted.getResultVal()); else setReportOutputDetails(plan,
				 * scrutinyDetail, ruleNo, ruleDesc, ROAD_RESERVE.toString(),
				 * minDistance.setScale(2, RoundingMode.FLOOR).toString(),
				 * Result.Not_Accepted.getResultVal());
				 */
			}
		}

		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		if (plan.getPlot() == null || (plan.getPlot() != null
				&& (plan.getPlot().getArea() == null || plan.getPlot().getArea().doubleValue() == 0))) {
			plan.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
			return plan;
		}
		validate(plan);

		return plan;
	}

	public void setReportOutputDetails(Plan plan, ScrutinyDetail scrutinyDetail, String ruleNo, String ruleDesc,
			String expected, String actual, String status) {
		HashMap<String, String> details = new HashMap<String, String>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(DECLARED, actual);
		details.put(STATUS, status);
		scrutinyDetail.addDetail(details);
		plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}
}
