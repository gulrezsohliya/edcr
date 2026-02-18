package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import static org.egov.edcr.constants.DxfFileConstants.F_RT;
import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_HE;
import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.D;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.F_CB;
import static org.egov.edcr.constants.DxfFileConstants.G;
import static org.egov.edcr.constants.DxfFileConstants.I;
import static org.egov.edcr.constants.DxfFileConstants.A_PO;
import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.SIDE_YARD1_DESC;
import static org.egov.edcr.utility.DcrConstants.SIDE_YARD2_DESC;
import static org.egov.edcr.utility.DcrConstants.SIDE_YARD_DESC;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.egov.common.entity.edcr.Yard;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.SideYardService;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SideYardService_Tripura extends SideYardService {

	private static final BigDecimal SIDEVALUE_ONEPOINTFIVE = BigDecimal.valueOf(1.5);
	private static final BigDecimal SIDEVALUE_ONEPOINTEIGHT = BigDecimal.valueOf(1.8);
	private static final BigDecimal SIDEVALUE_TWO = BigDecimal.valueOf(2);
	private static final BigDecimal SIDEVALUE_TWOPOINTFIVE = BigDecimal.valueOf(2.5);
	private static final BigDecimal SIDEVALUE_THREE = BigDecimal.valueOf(3);
	private static final BigDecimal SIDEVALUE_THREEPOINTSIX = BigDecimal.valueOf(3.66);
	private static final BigDecimal SIDEVALUE_FOUR = BigDecimal.valueOf(4);
	private static final BigDecimal SIDEVALUE_FOURPOINTFIVE = BigDecimal.valueOf(4.5);
	private static final BigDecimal SIDEVALUE_FIVE = BigDecimal.valueOf(5);
	private static final BigDecimal SIDEVALUE_SIX = BigDecimal.valueOf(6);
	private static final BigDecimal SIDEVALUE_SEVEN = BigDecimal.valueOf(7);
	private static final BigDecimal SIDEVALUE_SEVENTYFIVE = BigDecimal.valueOf(0.75);
	private static final BigDecimal SIDEVALUE_EIGHT = BigDecimal.valueOf(8);
	private static final BigDecimal SIDEVALUE_NINE = BigDecimal.valueOf(9);
	private static final BigDecimal SIDEVALUE_TEN = BigDecimal.valueOf(10);
	private static final BigDecimal SIDEVALUE_ZERO_POINT_9 = BigDecimal.valueOf(0.9);
	private static final BigDecimal SIDEVALUE_TWOPOINTFOUR = BigDecimal.valueOf(2.4);
	private static final BigDecimal SIDEVALUE_THREEPOINTFIVE = BigDecimal.valueOf(3.5);
	private static final BigDecimal SIDEVALUE_FOURPOINTTWO = BigDecimal.valueOf(4.2);

	private static final BigDecimal SIDEVALUE_ONE = BigDecimal.valueOf(1);
	private static final BigDecimal SIDEVALUE_ONE_TWO = BigDecimal.valueOf(1.2);

	private static final String SIDENUMBER = "Side Number";
	private static final String MINIMUMLABEL = "Minimum distance ";

	private static final String RULE_54_D = "54-D";
	private static final String SIDE_YARD_2_NOTDEFINED = "side2yardNodeDefined";
	private static final String SIDE_YARD_1_NOTDEFINED = "side1yardNodeDefined";

	public static final String BSMT_SIDE_YARD_DESC = "Basement Side Yard";
	public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);

	private static final Logger LOG = Logger.getLogger(SideYardService_Tripura.class);

	private class SideYardResult {
		String rule;
		String subRule;
		String blockName;
		Integer level;
		BigDecimal actualMeanDistance = BigDecimal.ZERO;
		BigDecimal actualDistance = BigDecimal.ZERO;
		String occupancy;
		BigDecimal expectedDistance = BigDecimal.ZERO;
		BigDecimal expectedmeanDistance = BigDecimal.ZERO;
		boolean status = false;
	}

	public void processSideYard(final Plan pl) {
		try {
			HashMap<String, String> errors = new HashMap<>();
			Plot plot = pl.getPlot();
			if (plot == null)
				return;

			Boolean OCTYP_FOUND = false;
			Boolean OCTYP_NOTFOUND = false;

			validateSideYardRule(pl);

			Boolean valid = false;
			if (plot != null && !pl.getBlocks().isEmpty()) {
				for (Block block : pl.getBlocks()) { // for each block
					scrutinyDetail = new ScrutinyDetail();
					scrutinyDetail.addColumnHeading(1, RULE_NO);
					scrutinyDetail.addColumnHeading(2, LEVEL);
					scrutinyDetail.addColumnHeading(3, OCCUPANCY);
					scrutinyDetail.addColumnHeading(4, SIDENUMBER);
					scrutinyDetail.addColumnHeading(5, FIELDVERIFIED);
					scrutinyDetail.addColumnHeading(6, PERMISSIBLE.concat(" in meters"));
					scrutinyDetail.addColumnHeading(7, PROVIDED.concat(" in meters"));
					scrutinyDetail.addColumnHeading(8, STATUS);
					scrutinyDetail.setHeading(SIDE_YARD_DESC);
					SideYardResult sideYard1Result = new SideYardResult();
					SideYardResult sideYard2Result = new SideYardResult();
					OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
							? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
							: null;
					double minlength = 0;
					double max = 0;
					int no = 0;

					if (pl.getNotifiedRoads() != null) {
						for (NotifiedRoad n : pl.getNotifiedRoads()) {
							if (n.getWidth() != null ) {
								no = no + 1;
							}
						}
					}
					int flag1 = 0;
					int flag2 = 0;
					for (SetBack setback : block.getSetBacks()) {
						Yard sideYard1 = null;
						Yard sideYard2 = null;

						if (setback.getSideYard1() != null) {
							sideYard1 = setback.getSideYard1();
						} else {
							if (no >=3 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)
									|| no >=3 &&  mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(B)) {
								
								
							}
							else if (no >=2 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)) {
								if (pl.getPlot().getArea().compareTo(BigDecimal.valueOf(1000)) > 0 && no >= 2) {
									pl.addError("PlotAreaError",
											"Side Setback 1 cannot be null for plotarea >1000 with two abutting road ");
									flag1 = 1;
								} else if (pl.getPlot().getArea().compareTo(BigDecimal.valueOf(500)) > 0 && no == 1) {
									flag1 = 1;
									pl.addError("PlotAreaError",
											"Side Setback 1 cannot be null for plotarea >500 with one abutting road ");
								}
							}

						}
						if (setback.getSideYard2() != null ) {
							sideYard2 = setback.getSideYard2();
						}else { 
							if (no >=3 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)
									||  no >=3 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(B)) {
								
								
							}
							 else {
								if (no >=2 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)) {
									if (pl.getPlot().getArea().compareTo(BigDecimal.valueOf(1000)) > 0 && no >= 2) {
										pl.addError("PlotAreaError",
												"Side Setback 1 cannot be null for plotarea >1000 with two abutting road ");
										flag2 = 1;
									} else if (pl.getPlot().getArea().compareTo(BigDecimal.valueOf(500)) > 0 && no == 1) {
										flag2 = 1;
										pl.addError("PlotAreaError",
												"Side Setback 1 cannot be null for plotarea >500 with one abutting road ");
									}
								}
							}
						}

						minlength = 0;
						max = 0;
						double minMeanlength = 0;
						double maxMeanLength = 0;
						if(sideYard2!=null) {
							if (sideYard2.getMinimumDistance().doubleValue() > 0) {
								max = sideYard2.getMinimumDistance().doubleValue();
								minlength = 0;

							}
						}
						if(sideYard1!=null) {
							max = 0;
							minlength = sideYard1.getMinimumDistance().doubleValue();
						}
						if(sideYard1!=null && sideYard2!=null) {
							max =sideYard1.getMinimumDistance().doubleValue();
							minlength = sideYard1.getMinimumDistance().doubleValue();
						}

						OCTYP_FOUND = false;
						OCTYP_NOTFOUND = false;

//								for (final Occupancy occupancy : block.getBuilding().getTotalArea()) {
						scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Side Setback");
						if (mostRestrictiveOccupancy.getType() == null) {

							OCTYP_NOTFOUND = true; // occ typ not found

						} else {
							OCTYP_FOUND = true; // search for occ typ

//							if (sideYard1 != null) {
							checkSideYardFor_Side1(pl, block.getBuilding(), block.getName(), setback.getLevel(), plot,
									minlength, max, minMeanlength, maxMeanLength, mostRestrictiveOccupancy, sideYard1Result,
									sideYard2Result);
//							}

						}

//								}

						if (OCTYP_NOTFOUND == true && OCTYP_FOUND == false) {
							errors.put("Block_" + block.getName() + "_" + SIDE_YARD_DESC,
									SIDE_YARD_DESC + " for Block " + block.getName() + " : Occupancy Type not Found!!");
							pl.addErrors(errors);
						}

						addSideYardResult(pl, errors, sideYard1Result, sideYard2Result, sideYard1, sideYard2);

					}

				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private void addSideYardResult(final Plan pl, HashMap<String, String> errors, SideYardResult sideYard1Result,
			SideYardResult sideYard2Result, Yard sideYard1, Yard sideYard2) {

		if (errors.isEmpty()) {

			if (sideYard1Result != null && sideYard1 != null) {
				Map<String, String> details = new HashMap<>();
				details.put(RULE_NO, sideYard1Result.subRule);
				details.put(LEVEL, sideYard1Result.level != null ? sideYard1Result.level.toString() : "");
				details.put(OCCUPANCY, sideYard1Result.occupancy);

				details.put(FIELDVERIFIED, MINIMUMLABEL);
				details.put(PERMISSIBLE.concat(" in meters"), sideYard1Result.expectedDistance.toString());
				details.put(PROVIDED.concat(" in meters"), sideYard1Result.actualDistance.toString());

				details.put(SIDENUMBER, SIDE_YARD1_DESC);

				if (sideYard1Result.status) {
					details.put(STATUS, Result.Accepted.getResultVal());
				} else {
					details.put(STATUS, Result.Not_Accepted.getResultVal());
				}

				scrutinyDetail.getDetail().add(details);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}

			if (sideYard2Result != null && sideYard2 != null) {
				Map<String, String> detailsSideYard2 = new HashMap<>();
				detailsSideYard2.put(RULE_NO, sideYard2Result.subRule);
				detailsSideYard2.put(LEVEL, sideYard2Result.level != null ? sideYard2Result.level.toString() : "");
				detailsSideYard2.put(OCCUPANCY, sideYard2Result.occupancy);
				detailsSideYard2.put(SIDENUMBER, SIDE_YARD2_DESC);

				detailsSideYard2.put(FIELDVERIFIED, MINIMUMLABEL);
				detailsSideYard2.put(PERMISSIBLE.concat(" in meters"), sideYard2Result.expectedDistance.toString());
				detailsSideYard2.put(PROVIDED.concat(" in meters"), sideYard2Result.actualDistance.toString());
				// }
				if (sideYard2Result.status) {
					detailsSideYard2.put(STATUS, Result.Accepted.getResultVal());
				} else {
					detailsSideYard2.put(STATUS, Result.Not_Accepted.getResultVal());
				}

				scrutinyDetail.getDetail().add(detailsSideYard2);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}
		}
	}

	private void checkSideYardFor_Side1(final Plan pl, Building building, String blockName, Integer level,
			final Plot plot, double min, double max, double minMeanlength, double maxMeanLength,
			final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
			SideYardResult sideYard2Result) {

		String abuttingRoad = "";
		if (pl.getPlanInfoProperties().containsKey("ABUTTING_ROAD")) {
			abuttingRoad = pl.getPlanInfoProperties().get("ABUTTING_ROAD");
		}
		String rule = SIDE_YARD_DESC;
		String subRule = RULE_54_D;
		Boolean valid1 = false;
		Boolean valid2 = false;

		BigDecimal side1val = BigDecimal.ZERO;
		BigDecimal side2val = BigDecimal.ZERO;

		BigDecimal plotArea = pl.getPlot().getArea();

		int no = 0;
		int color = 0;
		Integer abutting = 0;
		List<Pair<Integer, BigDecimal>> roadWidth = new ArrayList<>();
		Set<Integer> colorsPresent = new HashSet<>();

		if (pl.getNotifiedRoads() != null) {
			for (NotifiedRoad n : pl.getNotifiedRoads()) {
				System.out.println(n.getWidth());
				if (n.getWidth() != null ) {
					no = no + 1;
					color = n.getColorCode();
					colorsPresent.add(color);
					roadWidth.add(Pair.of(color, n.getWidth()));
				}
			}
		}

		if (colorsPresent.contains(1) && colorsPresent.contains(2)) {
			pl.addError("RoadColorNOtallowed",
					" Cannnot allow 2 notified roas with one being color coded as '1' and the other as '2' ");
			return;
		}

		if (no > 1) {
			abutting = Integer.valueOf(abuttingRoad);
		}
		
		
		int flag=0;
		if (abutting >= 3 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)
				|| abutting >= 3 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(B)) {
			if (no < 3) {
				pl.addError("SideRoadWidthNoError", "There should be atleast three abutting notified roads. Only " + no
						+ " notified roads are present in the plan");
				return;
			}
			for (Map.Entry<Integer, BigDecimal> entry : roadWidth) {
				if (entry.getValue().compareTo(BigDecimal.valueOf(5)) > 0) {
						flag=flag+1;
				} else {
					pl.addError("SideRoadWidthLess_5" + entry.getKey(), "Road width (" + entry.getValue()
							+ ") of notified road with color code" + entry.getKey() + " is less than 5m");
					return;
				}
			}
			if(flag==3) {
				side1val = BigDecimal.ZERO;
				side2val = BigDecimal.ZERO;
			}

		}else  if (abutting >= 2 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)) {
			if (no < 2) {
				pl.addError("SideRoadWidthNoError", "There should be atleast two abutting notified roads. Only " + no
						+ " notified roads are present in the plan");
				return;
			}
			for (Map.Entry<Integer, BigDecimal> entry : roadWidth) {
				if (entry.getValue().compareTo(BigDecimal.valueOf(3.5)) >= 0) {
					if (plotArea.compareTo(BigDecimal.valueOf(1000)) <= 0) {
						if (entry.getKey() == 1) {
							side1val = BigDecimal.ZERO;
							side2val = SIDEVALUE_THREEPOINTFIVE;
						}
						if (entry.getKey() == 2) {
							side1val = SIDEVALUE_THREEPOINTFIVE;
							side2val = BigDecimal.ZERO;
						}

					} else {
						side1val = SIDEVALUE_THREEPOINTFIVE;
						side2val = SIDEVALUE_THREEPOINTFIVE;
					}
				} else {
					pl.addError("SideRoadWidthLess3_5" + entry.getKey(), "Road width (" + entry.getValue()
							+ ") of notified road with color code" + entry.getKey() + " is less than 3.5m");
					return;
				}
			}

		} else if (abutting == 1 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)) {
			if (no < 1) {
				pl.addError("SideRoadWidthNoError", "There should be one abutting notified roads. Only " + no
						+ " notified roads are present in the plan");
				return;
			}
			for (Map.Entry<Integer, BigDecimal> entry : roadWidth) {
				if (entry.getValue().compareTo(BigDecimal.valueOf(3.5)) >= 0) {
					if (plotArea.compareTo(BigDecimal.valueOf(500)) <= 0) {
						if (entry.getKey() == 1) {
							side1val = BigDecimal.ZERO;
							side2val = SIDEVALUE_THREEPOINTFIVE;
						}
						if (entry.getKey() == 2) {
							side1val = SIDEVALUE_THREEPOINTFIVE;
							side2val = BigDecimal.ZERO;
						}

					} else {
						side1val = SIDEVALUE_THREEPOINTFIVE;
						side2val = SIDEVALUE_THREEPOINTFIVE;
					}
				} else {
					pl.addError("SideRoadWidthLess3_5" + entry.getKey(), "Road width (" + entry.getValue()
							+ ") of notified road with color code" + entry.getKey() + " is less than 3.5m");
					return;
				}
			}

		} else {
			if (building.getBuildingHeight().compareTo(BigDecimal.valueOf(8)) <= 0
					&& building.getMaxFloor().compareTo(BigDecimal.valueOf(2)) <= 0) {

				side1val = SIDEVALUE_ONE;
				side2val = SIDEVALUE_ONE;
			} else if (building.getBuildingHeight().compareTo(BigDecimal.valueOf(8)) > 0
					&& building.getBuildingHeight().compareTo(BigDecimal.valueOf(14.5)) <= 0) {

				side1val = SIDEVALUE_ONE_TWO;
				side2val = SIDEVALUE_ONE_TWO;
			} else if (building.getBuildingHeight().compareTo(BigDecimal.valueOf(14.5)) > 0
					&& building.getBuildingHeight().compareTo(BigDecimal.valueOf(30)) <= 0) {

				side1val = BigDecimal.valueOf(3.5);
				side2val = BigDecimal.valueOf(3.5);
			} else if (building.getBuildingHeight().compareTo(BigDecimal.valueOf(30)) > 0) {

				side1val = BigDecimal.valueOf(5);
				side2val = BigDecimal.valueOf(5);
			} else {
				pl.addError("Side Setback ", "Cannot Determined the required conditions for Side Setback");
				return;
			}
		}

//		if (max > min) {
//			if (max >= side1val.doubleValue()) {
//				valid1 = true;
//				if (min >= side2val.doubleValue()) {
//					valid2 = true;
//				}
//			}
//			compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
//					BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule, rule,
//					level);
//			compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(max), BigDecimal.ZERO,
//					BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule, rule,
//					level);
//
//		} else {
		Boolean only1 = false;
		Boolean only2 = false;

		if (side1val.compareTo(BigDecimal.ZERO) > 0 && side2val.compareTo(BigDecimal.ZERO) == 0) {
			only1 = true;
			if (max >= side1val.doubleValue()) {
				valid1 = true;
			}
		}

		else if (side2val.compareTo(BigDecimal.ZERO) > 0 && side1val.compareTo(BigDecimal.ZERO) == 0) {
			only2 = true;
			if (max >= side2val.doubleValue()) {
				valid2 = true;
			}
		} else {
			if (min >= side1val.doubleValue()) {
				valid1 = true;
				if (max >= side2val.doubleValue()) {
					valid2 = true;
				}
			}
		}

//		max=side2val.doubleValue();
		compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(max), BigDecimal.ZERO,
				BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule, rule,
				level);
		compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(min), BigDecimal.ZERO,
				BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule, rule,
				level);

//		}

	}

	private void compareSideYard1Result(String blockName, BigDecimal exptDistance, BigDecimal actualDistance,
			BigDecimal expectedMeanDistance, BigDecimal actualMeanDistance,
			OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result, Boolean valid, String subRule,
			String rule, Integer level) {
		String occupancyName;
		if (mostRestrictiveOccupancy.getSubtype() != null)
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
		else if (mostRestrictiveOccupancy.getType() != null)
			occupancyName = mostRestrictiveOccupancy.getType().getName();
		else
			occupancyName = "";

		if (exptDistance.compareTo(sideYard1Result.expectedDistance) >= 0) {
			sideYard1Result.rule = rule;
			sideYard1Result.occupancy = occupancyName;
			sideYard1Result.subRule = subRule;
			sideYard1Result.blockName = blockName;
			sideYard1Result.level = level;
			sideYard1Result.actualDistance = actualDistance;
			sideYard1Result.expectedDistance = exptDistance;
			sideYard1Result.status = valid;
		}

	}

	private void compareSideYard2Result(String blockName, BigDecimal exptDistance, BigDecimal actualDistance,
			BigDecimal expectedMeanDistance, BigDecimal actualMeanDistance,
			OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard2Result, Boolean valid, String subRule,
			String rule, Integer level) {
		String occupancyName;
		if (mostRestrictiveOccupancy.getSubtype() != null)
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
		else if (mostRestrictiveOccupancy.getType() != null)
			occupancyName = mostRestrictiveOccupancy.getType().getName();
		else
			occupancyName = "";

		if (exptDistance.compareTo(sideYard2Result.expectedDistance) >= 0) {
			sideYard2Result.rule = rule;
			sideYard2Result.occupancy = occupancyName;
			sideYard2Result.subRule = subRule;
			sideYard2Result.blockName = blockName;
			sideYard2Result.level = level;
			sideYard2Result.actualDistance = actualDistance;
			sideYard2Result.expectedDistance = exptDistance;
			sideYard2Result.status = valid;
		}

	}

	private void validateSideYardRule(final Plan pl) {

		for (Block block : pl.getBlocks()) {
			if (!block.getCompletelyExisting()) {
				Boolean sideYardDefined = false;
				for (SetBack setback : block.getSetBacks()) {
					if (setback.getSideYard1() != null
							&& setback.getSideYard1().getMinimumDistance().compareTo(BigDecimal.valueOf(0)) > 0) {
						sideYardDefined = true;
					} else if (setback.getSideYard2() != null
							&& setback.getSideYard2().getMinimumDistance().compareTo(BigDecimal.valueOf(0)) > 0) {
						sideYardDefined = true;
					}
				}
				if (!sideYardDefined) {
					HashMap<String, String> errors = new HashMap<>();
					errors.put(SIDE_YARD_DESC,
							prepareMessage(OBJECTNOTDEFINED, SIDE_YARD_DESC + " for Block " + block.getName()));
					pl.addErrors(errors);
				}
			}

		}

	}

}
