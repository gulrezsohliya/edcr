package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_FH;
import static org.egov.edcr.constants.DxfFileConstants.F_RT;
import static org.egov.edcr.constants.DxfFileConstants.F_CB;

import static org.egov.edcr.constants.DxfFileConstants.A_HE;
import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.constants.DxfFileConstants.F_H;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.D;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.I;
import static org.egov.edcr.constants.DxfFileConstants.A_PO;

import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;

// arunachal constants add
import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Plot;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.feature.FrontYardService;
import org.springframework.stereotype.Service;

@Service
public class FrontYardService_Manipur extends FrontYardService {
	private static final String RULE_54_D = "54-D";
	private static final String RULE_36 = "36";
	private static final String RULE_37_TWO_A = "37-2-A";
	private static final String RULE_37_TWO_B = "37-2-B";
	private static final String RULE_37_TWO_C = "37-2-C";
	private static final String RULE_37_TWO_D = "37-2-D";
	private static final String RULE_37_TWO_G = "37-2-G";
	private static final String RULE_37_TWO_H = "37-2-H";
	private static final String RULE_37_TWO_I = "37-2-I";
	private static final String RULE_47 = "47";

	private static final String MINIMUMLABEL = "Minimum distance ";

	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_1_5 = BigDecimal.valueOf(1.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_1_8 = BigDecimal.valueOf(1.8);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_2_5 = BigDecimal.valueOf(2.5);

	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_3_6 = BigDecimal.valueOf(3.6);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_4 = BigDecimal.valueOf(4);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_4_5 = BigDecimal.valueOf(4.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_5 = BigDecimal.valueOf(5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_5_5 = BigDecimal.valueOf(5.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_6 = BigDecimal.valueOf(6);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_6_5 = BigDecimal.valueOf(6.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_7 = BigDecimal.valueOf(7);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_7_5 = BigDecimal.valueOf(7.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_8 = BigDecimal.valueOf(8);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_9 = BigDecimal.valueOf(9);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_10 = BigDecimal.valueOf(10);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_11 = BigDecimal.valueOf(11);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_12 = BigDecimal.valueOf(12);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_13 = BigDecimal.valueOf(13);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_14 = BigDecimal.valueOf(14);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_15 = BigDecimal.valueOf(15);
	public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);

	// Arunachal front yard minimum
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_2 = BigDecimal.valueOf(2);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_3 = BigDecimal.valueOf(3);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_30 = BigDecimal.valueOf(30);

	public static final String BSMT_FRONT_YARD_DESC = "Basement Front Yard";
	private static final int PLOTAREA_300 = 300;

	// Arunachal plot area
	private static final BigDecimal PLOT_AREA_100 = BigDecimal.valueOf(100);
	private static final BigDecimal PLOT_AREA_250 = BigDecimal.valueOf(250);
	private static final BigDecimal PLOT_AREA_500 = BigDecimal.valueOf(500);

	private static final Logger LOG = Logger.getLogger(FrontYardService_Manipur.class);

	private class FrontYardResult {
		String rule;
		String subRule;
		String blockName;
		Integer level;
		BigDecimal actualMeanDistance = BigDecimal.ZERO;
		BigDecimal actualMinDistance = BigDecimal.ZERO;
		String occupancy;
		BigDecimal expectedminimumDistance = BigDecimal.ZERO;
		BigDecimal expectedmeanDistance = BigDecimal.ZERO;
		String additionalCondition;
		boolean status = false;
	}

	public void processFrontYard(Plan pl) {

		Plot plot = null;
		if (pl.getPlot() != null)
			plot = pl.getPlot();
		else
			return;
		HashMap<String, String> errors = new HashMap<>();

		Boolean OCTYP_FOUND = false;
		Boolean OCTYP_NOTFOUND = false;
		validateFrontYard(pl);

		if (plot != null && !pl.getBlocks().isEmpty()) {
			for (Block block : pl.getBlocks()) { // for each block

				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, LEVEL);
				scrutinyDetail.addColumnHeading(3, OCCUPANCY);
				scrutinyDetail.addColumnHeading(4, FIELDVERIFIED);
				scrutinyDetail.addColumnHeading(5, PERMISSIBLE.concat(" in meters"));
				scrutinyDetail.addColumnHeading(6, PROVIDED.concat(" in meters"));
				scrutinyDetail.addColumnHeading(7, STATUS);
				scrutinyDetail.setHeading(FRONT_YARD_DESC);

				FrontYardResult frontYardResult = new FrontYardResult();

				for (SetBack setback : block.getSetBacks()) {
					BigDecimal min;
					BigDecimal mean;

					if (setback.getFrontYard() != null) {
						min = setback.getFrontYard().getMinimumDistance();
						mean = setback.getFrontYard().getMean();

						OCTYP_FOUND = false;
						OCTYP_NOTFOUND = false;

						for (final Occupancy occupancy : block.getBuilding().getTotalArea()) {
							scrutinyDetail.setKey("Block_" + block.getName() + "_" + FRONT_YARD_DESC);

							if (occupancy.getTypeHelper().getType() == null) {

								OCTYP_NOTFOUND = true; // occ typ not found

							} else {

								OCTYP_FOUND = true; // search for occ typ

								if ((occupancy.getTypeHelper().getType() != null
										&& ((R1.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())))
										|| R2.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode()))) { // Residential

									frontYardResult = checkFrontYardForResidential(pl, block.getBuilding(),
											block.getName(), setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
											occupancy.getTypeHelper(), frontYardResult);
								}
//									else if (occupancy.getTypeHelper().getType() != null
//											&& PS.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())){ // Public Semi Public
//										if(roadWidth.compareTo(BigDecimal.valueOf(6))<=0) {
//											frontYardResult = checkPublicSemiPublic(pl, block.getBuilding(),
//													block.getName(), setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
//													occupancy.getTypeHelper(), frontYardResult);
//										}
//									
//									}
								else if (occupancy.getTypeHelper().getType() != null
										&& C.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())) { // Commercial

									frontYardResult = checkCommercial(pl, block.getBuilding(), block.getName(),
											setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
											occupancy.getTypeHelper(), frontYardResult);

								}else if (occupancy.getTypeHelper().getType() != null
										&& I.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())) { // Commercial

									frontYardResult = checkIndustrial(pl, block.getBuilding(), block.getName(),
											setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
											occupancy.getTypeHelper(), frontYardResult);

								}else if (occupancy.getTypeHelper().getType() != null
										&& IN.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())) { // Commercial

									frontYardResult = checkInstitutional(pl, block.getBuilding(), block.getName(),
											setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
											occupancy.getTypeHelper(), frontYardResult);

								}

								if (errors.isEmpty()) {
									Map<String, String> details = new HashMap<>();
									details.put(RULE_NO, frontYardResult.subRule);
									details.put(LEVEL,
											frontYardResult.level != null ? frontYardResult.level.toString() : "");
									details.put(OCCUPANCY, frontYardResult.occupancy);
									details.put(FIELDVERIFIED, MINIMUMLABEL);
									details.put(PERMISSIBLE.concat(" in meters"),
											frontYardResult.expectedminimumDistance.toString());
									details.put(PROVIDED.concat(" in meters"),
											frontYardResult.actualMinDistance.toString());

									if (frontYardResult.status) {
										details.put(STATUS, Result.Accepted.getResultVal());
									} else {
										details.put(STATUS, Result.Not_Accepted.getResultVal());
									}
									scrutinyDetail.getDetail().add(details);
									pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
								}
							}
						}

						if (OCTYP_NOTFOUND == true && OCTYP_FOUND == false) {
							errors.put("Block_" + block.getName() + "_" + FRONT_YARD_DESC, FRONT_YARD_DESC
									+ " for Block " + block.getName() + " : Occupancy Type not Found!!");
							pl.addErrors(errors);
						}

					}
				}
			}
		}
	}

	private void validateFrontYard(Plan pl) {

		for (Block block : pl.getBlocks()) {
			if (!block.getCompletelyExisting()) {
				Boolean frontYardDefined = false;
				for (SetBack setback : block.getSetBacks()) {
					if (setback.getFrontYard() != null) {
						frontYardDefined = true;
					}
				}
				if (!frontYardDefined) {
					HashMap<String, String> errors = new HashMap<>();
					errors.put(FRONT_YARD_DESC,
							prepareMessage(OBJECTNOTDEFINED, FRONT_YARD_DESC + " for Block " + block.getName()));
					pl.addErrors(errors);
				}
			}

		}

	}

	private FrontYardResult checkCommercial(Plan pl, Building building, String blockName, Integer level, Plot plot,
			String frontYardFieldName, BigDecimal min, BigDecimal mean, OccupancyTypeHelper mostRestrictiveOccupancy,
			FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_54_D;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal plotArea = pl.getPlot().getArea();
		Boolean isCbdTod = false;
		if (pl.getPlanInfoProperties().containsKey("CBD_TOD")) {
			if (pl.getPlanInfoProperties().get("CBD_TOD").equalsIgnoreCase("YES")) {
				isCbdTod = true;
			}
		}

		BigDecimal roadWidth = BigDecimal.ZERO;
		if(pl.getPlanInformation().getRoadWidth()!=null) {
			roadWidth = pl.getPlanInformation().getRoadWidth();
		}

//		if (mostRestrictiveOccupancy.getSubtype() != null
//				&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
//				|| (A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())))) { // Residential
		if (isCbdTod) {
			if (roadWidth.compareTo(BigDecimal.valueOf(30)) > 0) {
				if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0
						&& plotArea.compareTo(BigDecimal.valueOf(1500)) <= 0) {
					minVal = FRONTYARDMINIMUM_DISTANCE_6;

				} else if (plotArea.compareTo(BigDecimal.valueOf(1500)) > 0) {
					minVal = FRONTYARDMINIMUM_DISTANCE_7;

				}
			} else {
				if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0) {
					minVal = FRONTYARDMINIMUM_DISTANCE_6;

				}
			}
		} else {
			if (plotArea.compareTo(BigDecimal.valueOf(90)) >= 0 && plotArea.compareTo(BigDecimal.valueOf(150)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_1_5;

			} else if (plotArea.compareTo(BigDecimal.valueOf(150)) > 0
					&& plotArea.compareTo(BigDecimal.valueOf(300)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_2;

			} else if (plotArea.compareTo(BigDecimal.valueOf(300)) > 0
					&& plotArea.compareTo(BigDecimal.valueOf(500)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_3;

			} else if (plotArea.compareTo(BigDecimal.valueOf(500)) > 0
					&& plotArea.compareTo(BigDecimal.valueOf(750)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_4;

			} else if (plotArea.compareTo(BigDecimal.valueOf(750)) > 0
					&& plotArea.compareTo(BigDecimal.valueOf(1000)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_5;

			} else if (roadWidth.compareTo(BigDecimal.valueOf(30)) < 0) {
				if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0) {
					minVal = FRONTYARDMINIMUM_DISTANCE_6;
				}
			} else if (roadWidth.compareTo(BigDecimal.valueOf(30)) >= 0) {
				if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0
						&& plotArea.compareTo(BigDecimal.valueOf(1500)) <= 0) {
					minVal = FRONTYARDMINIMUM_DISTANCE_6;

				} else if (plotArea.compareTo(BigDecimal.valueOf(1500)) > 0) {
					minVal = FRONTYARDMINIMUM_DISTANCE_7;
				}
			}

		}

//		} 

		valid = validateMinimumValue(min, minVal);

		return compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule,
				rule, minVal, meanVal, level);
	}

	private FrontYardResult checkIndustrial(Plan pl, Building building, String blockName, Integer level, Plot plot,
			String frontYardFieldName, BigDecimal min, BigDecimal mean, OccupancyTypeHelper mostRestrictiveOccupancy,
			FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_54_D;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal plotArea = pl.getPlot().getArea();

		if (mostRestrictiveOccupancy.getSubtype() != null
				&& I1.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())) {

			if (plotArea.compareTo(BigDecimal.valueOf(300))<= 0 ) {
				minVal = FRONTYARDMINIMUM_DISTANCE_1_5;

			} else if (plotArea.compareTo(BigDecimal.valueOf(300)) > 0
					&& plotArea.compareTo(BigDecimal.valueOf(500)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_2_5;

			} else if (plotArea.compareTo(BigDecimal.valueOf(500)) > 0
					&& plotArea.compareTo(BigDecimal.valueOf(750)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_4;

			} else if (plotArea.compareTo(BigDecimal.valueOf(750)) > 0
					&& plotArea.compareTo(BigDecimal.valueOf(1000)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_4_5;

			} else if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0
					&& plotArea.compareTo(BigDecimal.valueOf(1500)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_5;

			} else if (plotArea.compareTo(BigDecimal.valueOf(1500)) > 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_6;

			} 
		}else if(I2.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())) {
			if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0
					&& plotArea.compareTo(BigDecimal.valueOf(2000)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_3;

			}else if (plotArea.compareTo(BigDecimal.valueOf(2000)) > 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_3;

			}
		}

		valid = validateMinimumValue(min, minVal);

		return compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule,
				rule, minVal, meanVal, level);
	}
	
	private FrontYardResult checkInstitutional(Plan pl, Building building, String blockName, Integer level,
			Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_54_D;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal plotArea = pl.getPlot().getArea();

//		if (mostRestrictiveOccupancy.getSubtype() != null
//				&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
//				|| (A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())))) { // Residential

		if (plotArea.compareTo(BigDecimal.valueOf(750)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(1000)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_6;

		} else if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(1500)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_7_5;

		} else if (plotArea.compareTo(BigDecimal.valueOf(1500)) > 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_10;

		} 
//		} 

		valid = validateMinimumValue(min, minVal);

		return compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule,
				rule, minVal, meanVal, level);
	}

	private FrontYardResult checkFrontYardForResidential(Plan pl, Building building, String blockName, Integer level,
			Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_54_D;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal plotArea = pl.getPlot().getArea();

//		if (mostRestrictiveOccupancy.getSubtype() != null
//				&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
//				|| (A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())))) { // Residential
		if (plotArea.compareTo(BigDecimal.valueOf(50)) >= 0 && plotArea.compareTo(BigDecimal.valueOf(90)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_1_5;
		}
		else if (plotArea.compareTo(BigDecimal.valueOf(90)) > 0 && plotArea.compareTo(BigDecimal.valueOf(150)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_1_5;

		} else if (plotArea.compareTo(BigDecimal.valueOf(150)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(300)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_2;

		} else if (plotArea.compareTo(BigDecimal.valueOf(300)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(500)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_3;

		} else if (plotArea.compareTo(BigDecimal.valueOf(500)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(750)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4;

		} else if (plotArea.compareTo(BigDecimal.valueOf(750)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(1000)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_5;

		} else if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(1500)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_6;

		} else if (plotArea.compareTo(BigDecimal.valueOf(1500)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_7;

		} else if (plotArea.compareTo(BigDecimal.valueOf(2000)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(2500)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_8;
		}
//		} 

		valid = validateMinimumValue(min, minVal);

		return compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule,
				rule, minVal, meanVal, level);
	}

	private FrontYardResult compareFrontYardResult(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		if (mostRestrictiveOccupancy.getSubtype() != null)
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
		else
			occupancyName = mostRestrictiveOccupancy.getType().getName();
		if (minVal.compareTo(frontYardResult.expectedminimumDistance) >= 0) {
			if (minVal.compareTo(frontYardResult.expectedminimumDistance) == 0) {
				frontYardResult.rule = frontYardResult.rule != null ? frontYardResult.rule + "," + rule : rule;
				frontYardResult.occupancy = frontYardResult.occupancy != null
						? frontYardResult.occupancy + "," + occupancyName
						: occupancyName;
			} else {
				frontYardResult.rule = rule;
				frontYardResult.occupancy = occupancyName;
			}

			frontYardResult.subRule = subRule;
			frontYardResult.blockName = blockName;
			frontYardResult.level = level;
			frontYardResult.expectedminimumDistance = minVal;
			frontYardResult.expectedmeanDistance = meanVal;
			frontYardResult.actualMinDistance = min;
			frontYardResult.actualMeanDistance = mean;
			frontYardResult.status = valid;

		}
		return frontYardResult;
	}

	private Boolean validateMinimumAndMeanValue(BigDecimal min, BigDecimal mean, BigDecimal minval,
			BigDecimal meanval) {
		Boolean valid = false;
		if (min.compareTo(minval) >= 0 && mean.compareTo(meanval) >= 0) {
			valid = true;
		}
		return valid;
	}

	private Boolean validateMinimumValue(BigDecimal min, BigDecimal minval) {
		Boolean valid = false;
		if (min.compareTo(minval) >= 0) {
			valid = true;
		}
		return valid;
	}
}
