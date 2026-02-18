package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.AccessoryBlock;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.AccessoryBuildingService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccessoryBuildingService_Sikkim extends AccessoryBuildingService {

	private static final Logger LOG = Logger.getLogger(AccessoryBuildingService_Sikkim.class);

	/*
	 * private static final String SUBRULE_88_1_DESC =
	 * "Maximum area of accessory block %s"; private static final String
	 * SUBRULE_88_3_DESC = "Maximum height of accessory block %s";
	 */
	/* SIKKIM */
	private static final String SUBRULE_88_1_DESC = "Minimum area of accessory block %s";
	private static final String SUBRULE_88_3_DESC = "Minimum height of accessory block %s";
	/* SIKKIM */

	private static final String SUBRULE_88_1 = "88-1";
	private static final String SUBULE_88_3 = "88-3";
	private static final String SUBRULE_88_4 = "88-4";
	private static final String SUBRULE_88_5 = "88-5";

	private static final String MIN_DIS_NOTIFIED_ROAD_FROM_ACC_BLDG = "Minimum distance from accessory block to notified road";
	private static final String MIN_DIS_NON_NOTIFIED_ROAD_FROM_ACC_BLDG = "Minimum distance from accessory building to non notified road";
	private static final String MIN_DIS_CULDESAC_ROAD_FROM_ACC_BLDG = "Minimum distance from accessory building to culdesac road";
	private static final String MIN_DIS_LANE_ROAD_FROM_ACC_BLDG = "Minimum distance from accessory building to lane road";
	private static final String SUBRULE_88_5_DESC = "Minimum distance from accessory block %s to plot boundary";

	// TESTING FOR SIKKIM COLORCODE
	private static final int GARAGE_PRIVATE_COLOR = 100;
	private static final int PRIVY_COLOR = 101;
//    private static final String LAYER_ROOM_HEIGHT = "ACCBLK_%s_%s";
//    private static final String LAYER_ROOM_AREA = "ACCBLK_%s_%s";
//    private static final String LAYER_ROOM_WIDTH = "ACCBLK_%s_%s";
	private static final String LAYER_ROOM = "ACCBLK_%s_%s";
	private static final String ROOM_HEIGHT_NOTDEFINED = "Garage height is not defined in layer ";
	private static final String ROOM_AREA_NOTDEFINED = "Garage area is not defined in layer ";
	private static final String ROOM_WIDTH_NOTDEFINED = "Garage width is not defined in layer ";
	private static final String ROOM_MEASUREMENTS_NOTDEFINED = "Garage measurements is not defined in layer ";

	public static final BigDecimal MINIMUM_GARAGE_WIDTH_SK_2_75 = BigDecimal.valueOf(2.75);// metres
	public static final BigDecimal MINIMUM_GARAGE_AREA_SK_16_72 = BigDecimal.valueOf(16.72);// metres
	public static final BigDecimal MINIMUM_GARAGE_HEIGHT_SK_2_43 = BigDecimal.valueOf(2.43);// metres

	public static final BigDecimal MINIMUM_PRIVY_WIDTH_SK_1_21 = BigDecimal.valueOf(1.21);// metres
	public static final BigDecimal MINIMUM_PRIVY_AREA_SK_1_12 = BigDecimal.valueOf(1.12);// metres
	public static final BigDecimal MINIMUM_PRIVY_HEIGHT_SK_2_43 = BigDecimal.valueOf(2.43);// metres

	@Override
	public Plan validate(Plan plan) {

		/* LOG.info("Decent AccessoryBuildingService: validateAccessoryBlockSikim"); */
		if (plan.getPlot() == null || (plan.getPlot() != null
				&& (plan.getPlot().getArea() == null || plan.getPlot().getArea().doubleValue() == 0))) {
			plan.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));

			return plan;
		}

		HashMap<String, String> errors = new HashMap<>();

		if (plan != null && !plan.getAccessoryBlocks().isEmpty()) {
			for (AccessoryBlock accessoryBlock : plan.getAccessoryBlocks()) {
				try {
					switch (accessoryBlock.getColorCode()) {
					case GARAGE_PRIVATE_COLOR:
						ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
						scrutinyDetail.addColumnHeading(1, RULE_NO);
						scrutinyDetail.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail.addColumnHeading(3, REQUIRED);
						scrutinyDetail.addColumnHeading(4, PROVIDED);
						scrutinyDetail.addColumnHeading(5, STATUS);
						scrutinyDetail.setHeading("Accessory Block Garage");
						processWidthOfGarageOfAccessoryBlockSikkim(plan, errors, accessoryBlock, scrutinyDetail);
						processAreaOfGarageOfAccessoryBlockSikkim(plan, errors, accessoryBlock, scrutinyDetail);
						processHeightOfGarageOfAccessoryBlockSikkim(plan, errors, accessoryBlock, scrutinyDetail);
						break;
					case PRIVY_COLOR:
						scrutinyDetail = new ScrutinyDetail();
						scrutinyDetail.addColumnHeading(1, RULE_NO);
						scrutinyDetail.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail.addColumnHeading(3, REQUIRED);
						scrutinyDetail.addColumnHeading(4, PROVIDED);
						scrutinyDetail.addColumnHeading(5, STATUS);
						scrutinyDetail.setHeading("Accessory Block Privy");
						processWidthOfPrivyOfAccessoryBlockSikkim(plan, errors, accessoryBlock, scrutinyDetail);
						processAreaOfPrivyOfAccessoryBlockSikkim(plan, errors, accessoryBlock, scrutinyDetail);
						processHeightOfPrivyOfAccessoryBlockSikkim(plan, errors, accessoryBlock, scrutinyDetail);
						break;
					}
				} catch (NullPointerException e) {
					LOG.error("NullPointerException: " + e);
					errors.put("Accesscory Block color code " + DcrConstants.OBJECTNOTDEFINED_DESC,
							"Accesscory Block color code " + DcrConstants.OBJECTNOTDEFINED_DESC);
					plan.addErrors(errors);
				}

			}
		}

		return plan;
	}

	@Override
	public Plan process(Plan plan) {

		if (plan != null)
			validate(plan);

		return plan;
	}

	private void setReportOutputDetails(Plan plan, String ruleNo, String ruleDesc, String expected, String actual,
			String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

	private void processHeightOfGarageOfAccessoryBlockSikkim(Plan plan, HashMap<String, String> errors,
			AccessoryBlock accessoryBlock, ScrutinyDetail scrutinyDetail1) {

		scrutinyDetail1.setKey("Block_Accessory Block Garage Height");

		String subRuleDesc = SUBRULE_88_3_DESC;
		String subRule = SUBULE_88_3;

		Boolean valid = false;
		if (accessoryBlock.getAccessoryBuilding() != null && accessoryBlock.getAccessoryBuilding().getHeight() != null
				&& accessoryBlock.getAccessoryBuilding().getHeight().compareTo(BigDecimal.valueOf(0)) > 0) {
			if (accessoryBlock.getAccessoryBuilding().getHeight().compareTo(MINIMUM_GARAGE_HEIGHT_SK_2_43) >= 0) {
				valid = true;
			}

			if (valid) {

				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Garage"),
						MINIMUM_GARAGE_HEIGHT_SK_2_43 + DcrConstants.IN_METER,
						accessoryBlock.getAccessoryBuilding().getHeight().setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER,
						Result.Accepted.getResultVal(), scrutinyDetail1);
			} else {

				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Garage"),
						MINIMUM_GARAGE_HEIGHT_SK_2_43 + DcrConstants.IN_METER,
						accessoryBlock.getAccessoryBuilding().getHeight().setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER,
						Result.Not_Accepted.getResultVal(), scrutinyDetail1);
			}
		} else {
			errors.put(String.format(DcrConstants.ACCESSORRY_BLK_HGHT, "GARAGE"),
					edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
							new String[] {
									String.format(DcrConstants.ACCESSORRY_BLK_HGHT, accessoryBlock.getNumber()) },
							LocaleContextHolder.getLocale()));
			plan.addErrors(errors);
		}
	}

	private void processWidthOfGarageOfAccessoryBlockSikkim(Plan plan, HashMap<String, String> errors,
			AccessoryBlock accessoryBlock, ScrutinyDetail scrutinyDetail1) {

		scrutinyDetail1.setKey("Block_Accessory Block Garage Width");
		String subRuleDesc = "Minimum width of accessory block %s ";// SUBRULE_88_3_DESC;
		String subRule = SUBULE_88_3;

		Boolean valid = false, notdefined = false;

		BigDecimal minWidth = BigDecimal.ZERO;

		if (accessoryBlock.getAccessoryBuilding() != null && accessoryBlock.getMeasurements() != null) {

			minWidth = accessoryBlock.getMeasurements().get(0).getWidth();

			for (Measurement m : accessoryBlock.getMeasurements()) {
				if (minWidth.compareTo(m.getWidth()) >= 0 && m.getWidth().compareTo(BigDecimal.ZERO) > 0
						&& m.getWidth() != null) {
					minWidth = m.getWidth();
				}
			}

			if (minWidth.compareTo(BigDecimal.ZERO) > 0) {
				if (minWidth.compareTo(MINIMUM_GARAGE_WIDTH_SK_2_75) >= 0) {
					valid = true;
				} else {
					valid = false;
				}

			} else {
				notdefined = true;
			}
		} else {
			notdefined = true;
		}

		if (!notdefined) {

			if (valid) {

				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Garage"),
						MINIMUM_GARAGE_WIDTH_SK_2_75 + DcrConstants.IN_METER, minWidth.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER,
						Result.Accepted.getResultVal(), scrutinyDetail1);
			} else {

				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Garage"),
						MINIMUM_GARAGE_WIDTH_SK_2_75 + DcrConstants.IN_METER, minWidth.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER,
						Result.Not_Accepted.getResultVal(), scrutinyDetail1);
			}
		}

		if (notdefined) {
			errors.put(String.format("ACCBLK %s WIDTH", "GARAGE"),
					edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
							new String[] { String.format("ACCBLK %s width", accessoryBlock.getNumber()) },
							LocaleContextHolder.getLocale()));
			plan.addErrors(errors);
		}
	}

	private void processAreaOfGarageOfAccessoryBlockSikkim(Plan plan, HashMap<String, String> errors,
			AccessoryBlock accessoryBlock, ScrutinyDetail scrutinyDetail) {

		scrutinyDetail.setKey("Block_Accessory Block Garage Area");
		String subRuleDesc = SUBRULE_88_1_DESC;
		String subRule = SUBRULE_88_1;

		BigDecimal accessoryBlockArea = BigDecimal.ZERO;
		Boolean valid = false;

		if (accessoryBlock.getAccessoryBuilding() != null && accessoryBlock.getAccessoryBuilding().getArea() != null
				&& accessoryBlock.getAccessoryBuilding().getArea().compareTo(BigDecimal.valueOf(0)) > 0) {
			accessoryBlockArea = accessoryBlock.getAccessoryBuilding().getArea();
			if (accessoryBlockArea.compareTo(MINIMUM_GARAGE_AREA_SK_16_72) >= 0) {
				valid = true;
			} else {
				valid = false;
			}

			if (valid) {
				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Garage"),
						MINIMUM_GARAGE_AREA_SK_16_72 + DcrConstants.IN_METER_SQR,
						accessoryBlockArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER_SQR, Result.Accepted.getResultVal(), scrutinyDetail);
			} else {
				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Garage"),
						MINIMUM_GARAGE_AREA_SK_16_72 + DcrConstants.IN_METER_SQR,
						accessoryBlockArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER_SQR, Result.Not_Accepted.getResultVal(),
						scrutinyDetail);
			}
		} else {
			errors.put(String.format("ACCBLK %s AREA", "GARAGE"),
					edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
							new String[] { String.format("ACCBLK %s area", accessoryBlock.getNumber()) },
							LocaleContextHolder.getLocale()));
			plan.addErrors(errors);
		}

	}

	private void processHeightOfPrivyOfAccessoryBlockSikkim(Plan plan, HashMap<String, String> errors,
			AccessoryBlock accessoryBlock, ScrutinyDetail scrutinyDetail1) {

		scrutinyDetail1.setKey("Block_Accessory Block Privy Height");

		String subRuleDesc = SUBRULE_88_3_DESC;
		String subRule = SUBULE_88_3;

		Boolean valid = false;
		if (accessoryBlock.getAccessoryBuilding() != null && accessoryBlock.getAccessoryBuilding().getHeight() != null
				&& accessoryBlock.getAccessoryBuilding().getHeight().compareTo(BigDecimal.valueOf(0)) > 0) {
			if (accessoryBlock.getAccessoryBuilding().getHeight().compareTo(MINIMUM_PRIVY_HEIGHT_SK_2_43) >= 0) {
				valid = true;
			}

			if (valid) {

				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Privy"),
						MINIMUM_PRIVY_HEIGHT_SK_2_43 + DcrConstants.IN_METER,
						accessoryBlock.getAccessoryBuilding().getHeight().setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER,
						Result.Accepted.getResultVal(), scrutinyDetail1);
			} else {

				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Privy"),
						MINIMUM_PRIVY_HEIGHT_SK_2_43 + DcrConstants.IN_METER,
						accessoryBlock.getAccessoryBuilding().getHeight().setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER,
						Result.Not_Accepted.getResultVal(), scrutinyDetail1);

			}
		} else {
			errors.put(String.format(DcrConstants.ACCESSORRY_BLK_HGHT, "PRIVY"),
					edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
							new String[] {
									String.format(DcrConstants.ACCESSORRY_BLK_HGHT, accessoryBlock.getNumber()) },
							LocaleContextHolder.getLocale()));
			plan.addErrors(errors);
		}
	}

	private void processWidthOfPrivyOfAccessoryBlockSikkim(Plan plan, HashMap<String, String> errors,
			AccessoryBlock accessoryBlock, ScrutinyDetail scrutinyDetail1) {

		scrutinyDetail1.setKey("Block_Accessory Block Privy Width");

		String subRuleDesc = "Minimum width of accessory block %s";// SUBRULE_88_3_DESC;
		String subRule = SUBULE_88_3;

		Boolean valid = false, notdefined = false;
		BigDecimal minWidth = BigDecimal.ZERO;

		if (accessoryBlock.getAccessoryBuilding() != null && accessoryBlock.getMeasurements() != null) {
			minWidth = accessoryBlock.getMeasurements().get(0).getWidth();

			for (Measurement m : accessoryBlock.getMeasurements()) {
				if (minWidth.compareTo(m.getWidth()) >= 0 && m.getWidth().compareTo(BigDecimal.ZERO) > 0
						&& m.getWidth() != null) {
					minWidth = m.getWidth();
				}
			}	

			if (minWidth.compareTo(BigDecimal.ZERO) > 0) {
				if (minWidth.compareTo(MINIMUM_PRIVY_WIDTH_SK_1_21) >= 0) {
					valid = true;
				} else {
					valid = false;
				}

			} else {
				notdefined = true;
			}

		} else {
			notdefined = true;
		}

		if (!notdefined) {

			if (valid) {
				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Privy"),
						MINIMUM_PRIVY_WIDTH_SK_1_21 + DcrConstants.IN_METER, minWidth.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER,
						Result.Accepted.getResultVal(), scrutinyDetail1);
			} else {
				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Privy"),
						MINIMUM_PRIVY_WIDTH_SK_1_21 + DcrConstants.IN_METER, minWidth.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER,
						Result.Not_Accepted.getResultVal(), scrutinyDetail1);
			}
		}

		if (notdefined) {
			errors.put(String.format("ACCBLK %s WIDTH", "PRIVY"),
					edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
							new String[] { String.format("ACCBLK %s width", accessoryBlock.getNumber()) },
							LocaleContextHolder.getLocale()));
			plan.addErrors(errors);
		}

	}

	private void processAreaOfPrivyOfAccessoryBlockSikkim(Plan plan, HashMap<String, String> errors,
			AccessoryBlock accessoryBlock, ScrutinyDetail scrutinyDetail) {

		scrutinyDetail.setKey("Block_Accessory Block Privy Area");

		String subRuleDesc = SUBRULE_88_1_DESC;
		String subRule = SUBRULE_88_1;

		BigDecimal accessoryBlockArea = BigDecimal.ZERO;
		Boolean valid = false;

		if (accessoryBlock.getAccessoryBuilding() != null && accessoryBlock.getAccessoryBuilding().getArea() != null
				&& accessoryBlock.getAccessoryBuilding().getArea().compareTo(BigDecimal.valueOf(0)) > 0) {

			accessoryBlockArea = accessoryBlock.getAccessoryBuilding().getArea();

			if (accessoryBlockArea.compareTo(MINIMUM_PRIVY_AREA_SK_1_12) >= 0) {
				valid = true;
			} else {
				valid = false;
			}

			if (valid) {
				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Privy"),
						MINIMUM_PRIVY_AREA_SK_1_12 + DcrConstants.IN_METER_SQR,
						accessoryBlockArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER_SQR, Result.Accepted.getResultVal(), scrutinyDetail);
			} else {
				setReportOutputDetails(plan, subRule, String.format(subRuleDesc, "Privy"),
						MINIMUM_PRIVY_AREA_SK_1_12 + DcrConstants.IN_METER_SQR,
						accessoryBlockArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS) + DcrConstants.IN_METER_SQR, Result.Not_Accepted.getResultVal(),
						scrutinyDetail);
			}
		} else {
			errors.put(String.format("ACCBLK %s AREA", "PRIVY"),
					edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
							new String[] { String.format("ACCBLK %s area", accessoryBlock.getNumber()) },
							LocaleContextHolder.getLocale()));
			plan.addErrors(errors);
		}

	}

}
