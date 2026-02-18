package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.egov.client.edcr.util.Utility;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RoofArea;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.RainWaterHarvesting;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class RainWaterHarvesting_Sikkim extends RainWaterHarvesting {

	private static final Logger LOG = Logger.getLogger(RainWaterHarvesting_Sikkim.class);

	private static final String RULE_21A = "21a";
	private static final String RULE_21A_DESCRIPTION = "Rain Water Harvesting";
	/*
	 * private static final String RWH_DECLARATION_ERROR =
	 * DxfFileConstants.RWH_DECLARED +
	 * " in PLAN_INFO layer must be declared as YES for total roof area greater than 184 sqm."
	 * ;
	 */
	private static final String RWH_DECLARATION_ERROR = DxfFileConstants.RWH_DECLARED
			+ " in PLAN_INFO layer must be declared as YES. ";

	private static final BigDecimal TOTAL_ROOFAREA_SK_185 = BigDecimal.valueOf(185);/* SQMETRE */

	@Override
	public Plan validate(Plan pl) {

		HashMap<String, String> errors = new HashMap<>();
		BigDecimal roofArea = BigDecimal.ZERO;
		String expected = DcrConstants.OBJECTDEFINED_DESC;

		roofArea = Utility.getTotalRoofArea(pl);
		if (roofArea.compareTo(TOTAL_ROOFAREA_SK_185) >= 0) {
			expected = String.format(
					"Required for all buildings having roof area of 185sqm or more",
					"");
			/* addOutput(pl, errors, RULE_21A, RULE_21A_DESCRIPTION, expected); */
			addReportOutput(pl, RULE_21A, RULE_21A_DESCRIPTION, expected);
		}

		return pl;
	}

	@Override
	public Plan process(Plan pl) {

		LOG.info("DECENT_SIKKIM RainWaterHarvesting: process");

		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().compareTo(BigDecimal.ZERO) <= 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
			return pl;
		}

		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		scrutinyDetail.setKey("Common_Rain Water Harvesting");

		validate(pl);
		return pl;
	}

	private void addReportOutput(Plan pl, String subRule, String subRuleDesc, String expected) {
		if (pl.getUtility() != null) {
			if (pl.getUtility().getRainWaterHarvest() != null && !pl.getUtility().getRainWaterHarvest().isEmpty()) {
				setReportOutputDetails(pl, subRule, subRuleDesc, expected, "Defined in the plan",
						Result.Accepted.getResultVal());
			} else {
				pl.addError("RWH LAYER "+OBJECTNOTDEFINED, String.format("Rainwater Harvesting is %s %s",DcrConstants.OBJECTNOTDEFINED_DESC, expected));
				setReportOutputDetails(pl, subRule, subRuleDesc, expected, "Not Defined in the plan",
						Result.Not_Accepted.getResultVal());
			}
		}
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
			String status) {

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	/*
	 * private void addOutput(Plan pl, HashMap<String, String> errors, String
	 * subRule, String subRuleDesc, String expected) { if (pl.getPlanInformation()
	 * != null && pl.getPlanInformation().getRwhDeclared() != null) { if
	 * (pl.getPlanInformation().getRwhDeclared().equalsIgnoreCase(DcrConstants.NO)
	 * ||
	 * pl.getPlanInformation().getRwhDeclared().equalsIgnoreCase(DcrConstants.NA)) {
	 * errors.put(DxfFileConstants.RWH_DECLARED, RWH_DECLARATION_ERROR);
	 * pl.addErrors(errors); addReportOutput(pl, subRule, subRuleDesc, expected); }
	 * else { addReportOutput(pl, subRule, subRuleDesc, expected); } } }
	 */

	/*
	 * private String getOccupancyTypeDetails() {
	 * 
	 * return OccupancyType.OCCUPANCY_A2.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_A3.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_A4.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_A5.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_B1.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_B2.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_B3.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_C.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_C1.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_C2.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_C3.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_D.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_D1.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_D2.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_F.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_F1.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_F2.getOccupancyType() + "," +
	 * OccupancyType.OCCUPANCY_F3.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_F4.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_G1.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_G2.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_H.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_I1.getOccupancyType() + ", " +
	 * OccupancyType.OCCUPANCY_I2.getOccupancyType(); }
	 */

}
