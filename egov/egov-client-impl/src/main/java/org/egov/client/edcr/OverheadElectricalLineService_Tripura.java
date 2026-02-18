package org.egov.client.edcr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.ElectricLine;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.feature.OverheadElectricalLineService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class OverheadElectricalLineService_Tripura extends OverheadElectricalLineService {
	private static final String REMARKS = "Remarks";
	private static final String VOLTAGE = "Voltage";
	private static final BigDecimal VERTICAL_DISTANCE_11000 = BigDecimal.valueOf(2.5);
	private static final BigDecimal VERTICAL_DISTANCE_33000 = BigDecimal.valueOf(3.7);
	private static final BigDecimal HORIZONTAL_DISTANCE_33000 = BigDecimal.valueOf(2);
	private static final BigDecimal HORIZONTAL_DISTANCE_11000 = BigDecimal.valueOf(1.2);
	private static final int VOLTAGE_11000 = 11;
	private static final int VOLTAGE_33000 = 33;

	@Override
	public Plan validate(Plan pl) {
		try {
			HashMap<String, String> errors = new HashMap<>();
			for (ElectricLine electricalLine : pl.getElectricLine()) {
				if (electricalLine.getPresentInDxf()) {
					if (electricalLine.getVoltage() == null) {
						errors.put("VoltageError1", "Voltage NOt Defined");
						pl.addErrors(errors);
					}
					if (electricalLine.getVoltage() != null && (electricalLine.getHorizontalDistance() == null
							&& electricalLine.getVerticalDistance() == null)) {
						errors.put("VoltageError2",
								"Horizontal Distance or Vertical Distance Not defined for Electrical Line");
						pl.addErrors(errors);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		validate(pl);
		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_OverHead Electric Line");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(6, VOLTAGE);
		scrutinyDetail.addColumnHeading(7, REMARKS);
		scrutinyDetail.addColumnHeading(8, STATUS);

		for (ElectricLine electricalLine : pl.getElectricLine()) {
			if (electricalLine.getPresentInDxf())
				if (electricalLine.getVoltage() != null && electricalLine.getVoltage().compareTo(BigDecimal.ZERO) > 0
						&& (electricalLine.getHorizontalDistance() != null
								|| electricalLine.getVerticalDistance() != null)) {
					boolean horizontalDistancePassed = false;
					if (electricalLine.getHorizontalDistance() != null) {
						String expectedResult = "";
						String actualResult = electricalLine.getHorizontalDistance().toString() + DcrConstants.IN_METER;
						if (electricalLine.getVoltage().compareTo(BigDecimal.valueOf(VOLTAGE_11000)) < 0) {
							expectedResult = HORIZONTAL_DISTANCE_11000.toString() + DcrConstants.IN_METER;
							if (electricalLine.getHorizontalDistance().compareTo(HORIZONTAL_DISTANCE_11000) >= 0)
								horizontalDistancePassed = true;

						} else if (electricalLine.getVoltage().compareTo(BigDecimal.valueOf(VOLTAGE_11000)) >= 0
								&& electricalLine.getVoltage().compareTo(BigDecimal.valueOf(VOLTAGE_33000)) <= 0) {
							expectedResult = HORIZONTAL_DISTANCE_33000.toString() + DcrConstants.IN_METER;
							if (electricalLine.getHorizontalDistance().compareTo(HORIZONTAL_DISTANCE_33000) >= 0)
								horizontalDistancePassed = true;
						} else if (electricalLine.getVoltage().compareTo(BigDecimal.valueOf(VOLTAGE_33000)) > 0) {
							Double totalHorizontalOHE = HORIZONTAL_DISTANCE_33000.doubleValue() + 0.3
									* Math.ceil(electricalLine.getVoltage().subtract(BigDecimal.valueOf(VOLTAGE_33000))
											.divide(BigDecimal.valueOf(VOLTAGE_33000), 2, RoundingMode.HALF_UP)
											.doubleValue());
							expectedResult = totalHorizontalOHE + DcrConstants.IN_METER;
							if (electricalLine.getHorizontalDistance()
									.compareTo(BigDecimal.valueOf(totalHorizontalOHE)) >= 0) {
								horizontalDistancePassed = true;
							}
						}
						if (horizontalDistancePassed) {
							setReportOutputDetails(pl, "42",
									DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(),
									expectedResult, actualResult, Result.Accepted.getResultVal(), "",
									electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
						} else {
							setReportOutputDetails(pl, "42",
									DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(),
									expectedResult, actualResult, Result.Not_Accepted.getResultVal(), "",
									electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
						}

						boolean verticalDistancePassed = processVerticalDistance(electricalLine, pl, "", "");
						if (verticalDistancePassed) {
							setReportOutputDetails(pl, "42",
									DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(),
									expectedResult, actualResult, Result.Verify.getResultVal(),
									String.format(DcrConstants.HORIZONTAL_ELINE_DISTANCE_NOC,
											electricalLine.getNumber()),
									electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
						} else {
							setReportOutputDetails(pl, "42",
									DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(),
									expectedResult, actualResult, Result.Not_Accepted.getResultVal(), "",
									electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
						}

					}
				}
		}
		return pl;
	}

	private boolean processVerticalDistance(ElectricLine electricalLine, Plan plan, String remarks1, String remarks2) {

		boolean verticalDistancePassed = false;

		if (electricalLine.getVerticalDistance() != null) {
			String actualResult = electricalLine.getVerticalDistance().toString() + DcrConstants.IN_METER;
			String expectedResult = "";

			if (electricalLine.getVoltage().compareTo(BigDecimal.valueOf(VOLTAGE_11000)) < 0) {

				expectedResult = VERTICAL_DISTANCE_11000.toString() + DcrConstants.IN_METER;
				if (electricalLine.getVerticalDistance().compareTo(VERTICAL_DISTANCE_11000) >= 0)
					verticalDistancePassed = true;

			} else if (electricalLine.getVoltage().compareTo(BigDecimal.valueOf(VOLTAGE_11000)) >= 0
					&& electricalLine.getVoltage().compareTo(BigDecimal.valueOf(VOLTAGE_33000)) <= 0) {

				expectedResult = VERTICAL_DISTANCE_33000.toString() + DcrConstants.IN_METER;
				if (electricalLine.getVerticalDistance().compareTo(VERTICAL_DISTANCE_33000) >= 0)
					verticalDistancePassed = true;

			} else if (electricalLine.getVoltage().compareTo(BigDecimal.valueOf(VOLTAGE_33000)) > 0) {

				Double totalVertficalOHE = VERTICAL_DISTANCE_33000.doubleValue()
						+ 0.3 * Math.ceil(electricalLine.getVoltage().subtract(BigDecimal.valueOf(VOLTAGE_33000))
								.divide(BigDecimal.valueOf(VOLTAGE_33000), 2, RoundingMode.HALF_UP).doubleValue());
				expectedResult = totalVertficalOHE + DcrConstants.IN_METER;
				if (electricalLine.getVerticalDistance().compareTo(BigDecimal.valueOf(totalVertficalOHE)) >= 0) {
					verticalDistancePassed = true;
				}
			}
			if (verticalDistancePassed) {
				setReportOutputDetails(plan, "42",
						DcrConstants.VERTICAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(), expectedResult,
						actualResult, Result.Accepted.getResultVal(), remarks1,
						electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
			} else {
				setReportOutputDetails(plan, "42",
						DcrConstants.VERTICAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(), expectedResult,
						actualResult, Result.Not_Accepted.getResultVal(), remarks2,
						electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
			}

		}
		return verticalDistancePassed;
	}



	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
			String status, String remarks, String voltage) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(REMARKS, remarks);
		details.put(VOLTAGE, voltage);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

}
