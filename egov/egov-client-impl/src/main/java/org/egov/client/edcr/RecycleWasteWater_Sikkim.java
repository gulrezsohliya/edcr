package org.egov.client.edcr;

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
import org.egov.client.edcr.util.Utility;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.VirtualBuilding;
import org.egov.edcr.feature.RecycleWasteWater;
import org.springframework.stereotype.Service;

@Service
public class RecycleWasteWater_Sikkim extends RecycleWasteWater {

	private static final Logger LOG = Logger.getLogger(RecycleWasteWater_Sikkim.class);

	private static final BigDecimal ONETHOUSANDFIVEHUNDER = BigDecimal.valueOf(1500);
	private static final String SUB_RULE_53_6_DESCRIPTION = "Recycling and reuse of waste water generated facility ";
	private static final String SUB_RULE_53_6 = "53-6";
	private static final BigDecimal TWOTHOUSANDFIVEHUNDER = BigDecimal.valueOf(2500);
	private static final BigDecimal TOTAL_ROOFAREA_SK_185 = BigDecimal.valueOf(185);

	@Override
	public Plan validate(Plan pl) {

		HashMap<String, String> errors = new HashMap<>();

		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		scrutinyDetail.setKey("Common_Water Reuse and Recycling");

		BigDecimal roofArea = BigDecimal.ZERO;
		String expected = StringUtils.EMPTY;

		/* if (pl.getVirtualBuilding().getResidentialBuilding()) { */
		roofArea = Utility.getTotalRoofArea(pl);
		if (roofArea.compareTo(TOTAL_ROOFAREA_SK_185) >= 0) {
			expected = String.format("Required for all buildings having roof area of 185sqm or more",
					"");
			processWasteWaterRecyclePlant(pl, errors, expected);
		}
		/*
		 * } else { expected =
		 * String.format("Required for all buildings of occupancy type %s",
		 * getOccupancyTypeDetails()); processWasteWaterRecyclePlant(pl, errors,
		 * expected);
		 * 
		 * }
		 */
		if (errors != null && !errors.isEmpty())
			pl.addErrors(errors);

		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
			return pl;
		}

		validate(pl);
		return pl;
	}

	private void processWasteWaterRecyclePlant(Plan pl, HashMap<String, String> errors, String expected) {
		if (!pl.getUtility().getWasteWaterRecyclePlant().isEmpty()) {
			setReportOutputDetails(pl, SUB_RULE_53_6, SUB_RULE_53_6_DESCRIPTION, expected, OBJECTDEFINED_DESC,
					Result.Accepted.getResultVal());
			return;
		} else {
			errors.put("Recycling and reuse of waste water facilty",String.format("Water Reuse & Recyling is %s %s",OBJECTNOTDEFINED_DESC, expected));
			setReportOutputDetails(pl, SUB_RULE_53_6, SUB_RULE_53_6_DESCRIPTION, expected, OBJECTNOTDEFINED_DESC,
					Result.Not_Accepted.getResultVal());
			return;
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
