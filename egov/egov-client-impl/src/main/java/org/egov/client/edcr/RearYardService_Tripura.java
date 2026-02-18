package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_FH;
import static org.egov.edcr.constants.DxfFileConstants.F_RT;
import static org.egov.edcr.constants.DxfFileConstants.A_HE;
import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.D;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.F_CB;
import static org.egov.edcr.constants.DxfFileConstants.I;
import static org.egov.edcr.constants.DxfFileConstants.A_PO;
import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.REAR_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.YES;

//arunachal constants add
import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.NotifiedRoad;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Plot;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.RearYardService;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class RearYardService_Tripura extends RearYardService {
	private static final String RULE_54_D = "54-D";

	private static final String MINIMUMLABEL = "Minimum distance ";

	private static final BigDecimal REARYARDMINIMUM_DISTANCE_0_9 = BigDecimal.valueOf(0.9);

	private static final BigDecimal REARYARDMINIMUM_DISTANCE_1_5 = BigDecimal.valueOf(1.5);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_1_8 = BigDecimal.valueOf(1.8);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_2 = BigDecimal.valueOf(2);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_2_5 = BigDecimal.valueOf(2.5);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_3 = BigDecimal.valueOf(3);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_3_6 = BigDecimal.valueOf(3.6);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_4 = BigDecimal.valueOf(4);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_4_5 = BigDecimal.valueOf(4.5);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_5 = BigDecimal.valueOf(5);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_6 = BigDecimal.valueOf(6);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_7 = BigDecimal.valueOf(7);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_8 = BigDecimal.valueOf(8);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_9 = BigDecimal.valueOf(9);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_12 = BigDecimal.valueOf(12);

	// Arunachal front yard minimum
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_1 = BigDecimal.valueOf(1);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_1_2 = BigDecimal.valueOf(1.2);

	// Arunachal plot area
	private static final BigDecimal PLOT_AREA_100 = BigDecimal.valueOf(100);
	private static final BigDecimal PLOT_AREA_250 = BigDecimal.valueOf(250);
	private static final BigDecimal PLOT_AREA_500 = BigDecimal.valueOf(500);

	public static final String BSMT_REAR_YARD_DESC = "Basement Rear Setback";
	public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);

	private static final Logger LOG = Logger.getLogger(RearYardService_Tripura.class);

	private class RearYardResult {
		String rule;
		String subRule;
		String blockName;
		Integer level;
		BigDecimal actualMeanDistance = BigDecimal.ZERO;
		BigDecimal actualMinDistance = BigDecimal.ZERO;
		String occupancy;
		BigDecimal expectedminimumDistance = BigDecimal.ZERO;
		BigDecimal expectedmeanDistance = BigDecimal.ZERO;
		boolean status = false;
	}

	public void processRearYard(final Plan pl) {
		try {
			HashMap<String, String> errors = new HashMap<>();
			final Plot plot = pl.getPlot();

			Boolean OCTYP_FOUND = false;
			Boolean OCTYP_NOTFOUND = false;

			if (plot == null)
				return;

//			validateRearYard(pl);

			if (plot != null && !pl.getBlocks().isEmpty()) {
				OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
						? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
						: null;
				for (Block block : pl.getBlocks()) { // for each block

					scrutinyDetail = new ScrutinyDetail();
					scrutinyDetail.addColumnHeading(1, RULE_NO);
					scrutinyDetail.addColumnHeading(2, LEVEL);
					scrutinyDetail.addColumnHeading(3, OCCUPANCY);
					scrutinyDetail.addColumnHeading(4, FIELDVERIFIED);
					scrutinyDetail.addColumnHeading(5, PERMISSIBLE.concat(" in meters"));
					scrutinyDetail.addColumnHeading(6, PROVIDED.concat(" in meters"));
					scrutinyDetail.addColumnHeading(7, STATUS);
					scrutinyDetail.setHeading(REAR_YARD_DESC);
					RearYardResult rearYardResult = new RearYardResult();
					
					int no=0;
					List<Pair<Integer, BigDecimal>> roadWidth = new ArrayList<>();
					if(pl.getNotifiedRoads()!=null) {
						for(NotifiedRoad n : pl.getNotifiedRoads()) {
							if(n.getWidth()!=null ) {
								no=no+1;
								roadWidth.add(Pair.of(no, n.getWidth()));
							}
						}
					}
					

					for (SetBack setback : block.getSetBacks()) {
						BigDecimal min;
						BigDecimal mean;
						
						if(setback.getRearYard()==null && no>=3) {
							String abuttingRoad="";
							if(pl.getPlanInfoProperties().containsKey("ABUTTING_ROAD")) {
								abuttingRoad=pl.getPlanInfoProperties().get("ABUTTING_ROAD");
							}
							Integer abutting=0;
							if (no > 1) {
								abutting = Integer.valueOf(abuttingRoad);
							}
							if(abutting>2 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)
									|| abutting>2 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(B)) {
								if(no<3) {
									pl.addError("SideRoadWidthNoError", "There should be atleast three abutting notified roads. Only " + no
											+ " notified roads are present in the plan");
//									return rearYardResult;
								}
								for (Map.Entry<Integer, BigDecimal> entry : roadWidth) {
									if(entry.getValue().compareTo(BigDecimal.valueOf(5))>=0) {
									}else {
										pl.addError("SideRoadWidthLess_5" + entry.getKey(), "Road width (" + entry.getValue()
										+ ") of notified road with color code" + entry.getKey() + " is less than 5m");
//										return rearYardResult;
									}
								}
								
							}
						}else if (setback.getRearYard() != null) {
							min = setback.getRearYard().getMinimumDistance();
							mean = setback.getRearYard().getMean();

							OCTYP_FOUND = false;
							OCTYP_NOTFOUND = false;

//								for (final Occupancy occupancy : block.getBuilding().getTotalArea()) {
							scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Rear Setback");

							if (mostRestrictiveOccupancy.getType() == null) {

								OCTYP_NOTFOUND = true; // occ typ not found
							} else {
								OCTYP_FOUND = true; // search for occ typ

								rearYardResult = checkRearYard(setback, block.getBuilding(), pl, block, setback.getLevel(),
										plot, REAR_YARD_DESC, min, mean, mostRestrictiveOccupancy, rearYardResult);

							}

//								}
							if (OCTYP_NOTFOUND == true && OCTYP_FOUND == false) {
								errors.put("Block_" + block.getName() + "_" + REAR_YARD_DESC,
										REAR_YARD_DESC + " for Block " + block.getName() + " : Occupancy Type not Found!!");
								pl.addErrors(errors);
							}

							if (errors.isEmpty()) {
								Map<String, String> details = new HashMap<>();
								details.put(RULE_NO, rearYardResult.subRule);
								details.put(LEVEL, rearYardResult.level != null ? rearYardResult.level.toString() : "");
								details.put(OCCUPANCY, rearYardResult.occupancy);
								details.put(FIELDVERIFIED, MINIMUMLABEL);
								details.put(PERMISSIBLE.concat(" in meters"),
										rearYardResult.expectedminimumDistance.toString());
								details.put(PROVIDED.concat(" in meters"), rearYardResult.actualMinDistance.toString());
								if (rearYardResult.status) {
									details.put(STATUS, Result.Accepted.getResultVal());

								} else {
									details.put(STATUS, Result.Not_Accepted.getResultVal());
								}
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							}

						} else {
							
//							errors.put("Rear Setback", "Rear Setback for level is Not Defined");
//							pl.addErrors(errors);
						}
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}

		

	}

	private RearYardResult checkRearYard(SetBack setback, Building building, final Plan pl, Block block, Integer level,
			final Plot plot, final String rearYardFieldName, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult) {
		String subRule = RULE_54_D;
		String rule = REAR_YARD_DESC;
		Boolean valid = false;
		BigDecimal minVal = BigDecimal.valueOf(0);
		BigDecimal meanVal = BigDecimal.valueOf(0);
		BigDecimal plotArea = pl.getPlot().getArea();
		int no=0;
		List<Pair<Integer, BigDecimal>> roadWidth = new ArrayList<>();
		if(pl.getNotifiedRoads()!=null) {
			for(NotifiedRoad n : pl.getNotifiedRoads()) {
				if(n.getWidth()!=null) {
					no=no+1;
					roadWidth.add(Pair.of(no, n.getWidth()));
				}
			}
		}
		
		
		
		String abuttingRoad="";
		if(pl.getPlanInfoProperties().containsKey("ABUTTING_ROAD")) {
			abuttingRoad=pl.getPlanInfoProperties().get("ABUTTING_ROAD");
		}
		Integer abutting=0;
		if (no > 1) {
			abutting = Integer.valueOf(abuttingRoad);
		}
		int flag=0;
		if (abutting >= 3 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)
				|| abutting >= 3 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(B) ) {
			if (no < 3) {
				pl.addError("SideRoadWidthNoError", "There should be atleast three abutting notified roads. Only " + no
						+ " notified roads are present in the plan");
			}
			for (Map.Entry<Integer, BigDecimal> entry : roadWidth) {
				if (entry.getValue().compareTo(BigDecimal.valueOf(5)) > 0) {
						flag=flag+1;
				} else {
					pl.addError("SideRoadWidthLess_5" + entry.getKey(), "Road width (" + entry.getValue()
							+ ") of notified road with color code" + entry.getKey() + " is less than 5m");
				}
			}
			if(flag==3) {
				minVal = BigDecimal.ZERO;
			}

		}else if(abutting>2 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)) {
			if(no<3) {
				pl.addError("RoadWidthNoError", "There should be three abutting notified roads. Only "+no+" notified roads are present in the plan");
//				return rearYardResult;
			}
			for (Map.Entry<Integer, BigDecimal> entry : roadWidth) {
				if(entry.getValue().compareTo(BigDecimal.valueOf(3.5))>=0) {
					minVal = BigDecimal.ZERO;
				}else {
					pl.addError("RoadWidthLess3_5"+entry.getKey(), "Road width ("+entry.getValue()+") of notified road "+no+" is less than 3.5m");
//					return rearYardResult;
				}
			}
			
		}else {
			if (building.getBuildingHeight().compareTo(BigDecimal.valueOf(10)) <= 0) {
				minVal = REARYARDMINIMUM_DISTANCE_1;

			} else if (building.getBuildingHeight().compareTo(BigDecimal.valueOf(10)) > 0
					&& building.getBuildingHeight().compareTo(BigDecimal.valueOf(14.5)) <= 0) {
				minVal = REARYARDMINIMUM_DISTANCE_1_2;

			} else if (building.getBuildingHeight().compareTo(BigDecimal.valueOf(14.5)) > 0
					&& building.getBuildingHeight().compareTo(BigDecimal.valueOf(18)) <= 0) {
				minVal = BigDecimal.valueOf(3.5);

			} else if (building.getBuildingHeight().compareTo(BigDecimal.valueOf(18)) > 0
					&& building.getBuildingHeight().compareTo(BigDecimal.valueOf(30)) <= 0) {
				minVal = REARYARDMINIMUM_DISTANCE_5;

			} else if (building.getBuildingHeight().compareTo(BigDecimal.valueOf(30)) > 0) {
				minVal = BigDecimal.valueOf(10);

			} else {
				pl.addError("Rear Setback Residential", "Cannot Determined the required conditions for Rear Setback");
//				return rearYardResult;
			}
		}
		
		valid = validateMinimumValue(min, minVal);

		return compareRearYardResult(block.getName(), min, mean, mostRestrictiveOccupancy, rearYardResult, valid,
				subRule, rule, minVal, meanVal, level);

	}

	private Boolean validateMinimumValue(BigDecimal min, BigDecimal minval) {
		Boolean valid = false;
		if (min.compareTo(minval) >= 0) {
			valid = true;
		}
		return valid;
	}

	private void validateRearYard(final Plan pl) {
		for (Block block : pl.getBlocks()) {
			if (!block.getCompletelyExisting()) {
				Boolean rearYardDefined = false;
				for (SetBack setback : block.getSetBacks()) {
					if (setback.getRearYard() != null
							&& setback.getRearYard().getMinimumDistance().compareTo(BigDecimal.valueOf(0)) > 0) {
						rearYardDefined = true;
					}
				}
				if (!rearYardDefined && !pl.getPlanInformation().getNocToAbutRearDesc().equalsIgnoreCase(YES)) {
					HashMap<String, String> errors = new HashMap<>();
					errors.put(REAR_YARD_DESC,
							prepareMessage(OBJECTNOTDEFINED, REAR_YARD_DESC + " for Block " + block.getName()));
					pl.addErrors(errors);
				}
			}

		}

	}

	private RearYardResult compareRearYardResult(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, Boolean valid, String subRule,
			String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		if (mostRestrictiveOccupancy.getSubtype() != null)
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
		else
			occupancyName = mostRestrictiveOccupancy.getType().getName();
		if (minVal.compareTo(rearYardResult.expectedminimumDistance) >= 0) {
			rearYardResult.rule = rule;
			rearYardResult.occupancy = occupancyName;
			rearYardResult.expectedmeanDistance = meanVal;
			rearYardResult.actualMeanDistance = mean;
			rearYardResult.subRule = subRule;
			rearYardResult.blockName = blockName;
			rearYardResult.level = level;
			rearYardResult.expectedminimumDistance = minVal;
			rearYardResult.actualMinDistance = min;
			rearYardResult.status = valid;

		}
		return rearYardResult;
	}
}
