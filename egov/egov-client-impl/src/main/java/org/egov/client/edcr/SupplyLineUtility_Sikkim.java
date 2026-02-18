package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SupplyLine;
import org.egov.common.entity.edcr.Utility;
import org.egov.edcr.feature.SupplyLineUtility;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SupplyLineUtility_Sikkim extends SupplyLineUtility {

	private static final Logger LOG = Logger.getLogger(SupplyLineUtility_Sikkim.class);

//	private static final String RULE_BYELAWNO = "";
	private static final String RULE_ELECTRICAL_LINE_4 = "4";
	private static final String RULE_SEWERAGE_LINE_4_8_25 = "4 8 & 25";
	private static final String RULE_WATERDRAIN_LINE_4 = "4";
	private static final String RULE_WATERSUPPLY_LINE_4 = "4";

	private static final String RULE_DESC = "Rule Desc";
	private static final String UTILITY_SUPPLY_LINE = "Utility Supply Line";

	private static final String ELECTRICITY_SUPPLY_LINE_DESC = "Electricity Supply Line";
	private static final String SEWARAGE_LINE_DESC = "Sewerage Line";
	private static final String WATER_DRAIN_LINE_DESC = "Water Drain Line";
	private static final String WATER_SUPPLY_LINE_DESC = "Water Supply Line";

	private static final int ELECTRICITY_SUPPLY_LINE_COLOR_CODE_1 = 1;
	private static final int SEWERAGE_LINE_COLOR_CODE_48 = 48;
	private static final int WATER_DRAIN_LINE_COLOR_CODE_40 = 40;
	private static final int WATER_SUPPLY_LINE_COLOR_CODE_4 = 4;

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub

		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey(String.format("Common_%s", UTILITY_SUPPLY_LINE));
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);

		HashMap<String, String> errors = new HashMap<>();
		Utility utility = null;
		if(plan.getUtility()!=null)
		 utility = plan.getUtility();
		SupplyLine supplyLines = null;
		if(utility.getSupplyLine()!=null)
			supplyLines=utility.getSupplyLine();
		if (supplyLines != null) {
			if (supplyLines.getSupplyLines() != null && !supplyLines.getSupplyLines().isEmpty()) {
				String ruleNo = StringUtils.EMPTY, ruleDesc = StringUtils.EMPTY, actual = StringUtils.EMPTY;

				boolean electricityLineFlag = false, septicTankFlag = false, sewerageLineFlag = false,
						waterSupplyLineFlag = false, waterDrainLineFlag = false;

				if (plan.getSepticTanks() != null && !plan.getSepticTanks().isEmpty()
						&& plan.getSepticTanks().size() > 0) {
					septicTankFlag = true;
				}

				for (Measurement m : supplyLines.getSupplyLines()) {

					switch (m.getColorCode()) {

					case ELECTRICITY_SUPPLY_LINE_COLOR_CODE_1:
						ruleNo = RULE_ELECTRICAL_LINE_4;
						ruleDesc = String.format("%s", ELECTRICITY_SUPPLY_LINE_DESC);
						actual = DcrConstants.OBJECTDEFINED_DESC;
						setReportOutputDetails(plan, ruleNo, ruleDesc, actual, Result.Verify.getResultVal(),
								scrutinyDetail);
						electricityLineFlag = true;

						break;

					case SEWERAGE_LINE_COLOR_CODE_48:
						ruleNo = RULE_SEWERAGE_LINE_4_8_25;
						ruleDesc = String.format("%s", SEWARAGE_LINE_DESC);
						actual = DcrConstants.OBJECTDEFINED_DESC;
						setReportOutputDetails(plan, ruleNo, ruleDesc, actual, Result.Verify.getResultVal(),
								scrutinyDetail);
						sewerageLineFlag = true;
						break;

					case WATER_DRAIN_LINE_COLOR_CODE_40:
						ruleNo = RULE_WATERDRAIN_LINE_4;
						ruleDesc = String.format("%s", WATER_DRAIN_LINE_DESC);
						actual = DcrConstants.OBJECTDEFINED_DESC;
						setReportOutputDetails(plan, ruleNo, ruleDesc, actual, Result.Verify.getResultVal(),
								scrutinyDetail);
						waterDrainLineFlag = true;

						break;

					case WATER_SUPPLY_LINE_COLOR_CODE_4:
						ruleNo = RULE_WATERSUPPLY_LINE_4;
						ruleDesc = String.format("%s", WATER_SUPPLY_LINE_DESC);
						actual = DcrConstants.OBJECTDEFINED_DESC;
						setReportOutputDetails(plan, ruleNo, ruleDesc, actual, Result.Verify.getResultVal(),
								scrutinyDetail);
						waterSupplyLineFlag = true;
						break;

					default:
						break;
					}

				}

				actual = DcrConstants.OBJECTNOTDEFINED_DESC;
				ruleNo = "Bye Law No.";
				ruleDesc = String.format("%s", ELECTRICITY_SUPPLY_LINE_DESC);
				if (!electricityLineFlag) {
					errors.put(String.format("%s %s", ELECTRICITY_SUPPLY_LINE_DESC, DcrConstants.OBJECTNOTDEFINED),
							String.format("%s %s", ELECTRICITY_SUPPLY_LINE_DESC, DcrConstants.OBJECTNOTDEFINED_DESC));
					plan.addErrors(errors);
				}

				ruleDesc = String.format("%s", SEWARAGE_LINE_DESC);
				if (!sewerageLineFlag && !septicTankFlag) {
					errors.put(String.format("%s %s", SEWARAGE_LINE_DESC, DcrConstants.OBJECTNOTDEFINED),
							String.format("%s %s", SEWARAGE_LINE_DESC, DcrConstants.OBJECTNOTDEFINED_DESC));
					plan.addErrors(errors);
				}

				ruleDesc = String.format("%s", WATER_DRAIN_LINE_DESC);
				if (!waterDrainLineFlag) {
					errors.put(String.format("%s %s", WATER_DRAIN_LINE_DESC, DcrConstants.OBJECTNOTDEFINED),
							String.format("%s %s", WATER_DRAIN_LINE_DESC, DcrConstants.OBJECTNOTDEFINED_DESC));
					plan.addErrors(errors);
				}

				ruleDesc = String.format("%s", WATER_SUPPLY_LINE_DESC);
				if (!waterSupplyLineFlag) {
					errors.put(String.format("%s %s", WATER_SUPPLY_LINE_DESC, DcrConstants.OBJECTNOTDEFINED),
							String.format("%s %s", WATER_SUPPLY_LINE_DESC, DcrConstants.OBJECTNOTDEFINED_DESC));
					plan.addErrors(errors);
				}
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

	public void setReportOutputDetails(Plan plan, String ruleNo, String ruleDesc, String actual, String status,
			ScrutinyDetail scrutinyDetail) {
		HashMap<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, "Required");
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

}
