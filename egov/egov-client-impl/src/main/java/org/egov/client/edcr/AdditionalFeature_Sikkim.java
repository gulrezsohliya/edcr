package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.client.edcr.util.Utility;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
//import org.egov.common.entity.bpa.Occupancy;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.AdditionalFeature;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AdditionalFeature_Sikkim extends AdditionalFeature {
	private static final Logger LOG = Logger.getLogger(AdditionalFeature_Sikkim.class);

	private static final String RULE_38 = "38";
	private static final String RULE_39 = "39";
	private static final String RULE_41_I_A = "41-i-a";
	private static final String RULE_41_I_B = "41-i-b";
	private static final String RULE_47 = "47";
	private static final String RULE_50 = "50";
	private static final String RULE_56 = "56";
	private static final BigDecimal TWO = BigDecimal.valueOf(2);
	private static final BigDecimal ONE_POINTFIVE = BigDecimal.valueOf(1.5);
	private static final BigDecimal THREE = BigDecimal.valueOf(3);
	private static final BigDecimal FOUR = BigDecimal.valueOf(4);
	private static final BigDecimal SIX = BigDecimal.valueOf(6);
	private static final BigDecimal SEVEN = BigDecimal.valueOf(7);
	private static final BigDecimal TEN = BigDecimal.valueOf(10);
	private static final BigDecimal TWELVE = BigDecimal.valueOf(12);
	private static final BigDecimal NINETEEN = BigDecimal.valueOf(19);

	private static final BigDecimal ROAD_WIDTH_TWO_POINTFOUR = BigDecimal.valueOf(2.4);
	private static final BigDecimal ROAD_WIDTH_TWO_POINTFOURFOUR = BigDecimal.valueOf(2.44);
	private static final BigDecimal ROAD_WIDTH_THREE_POINTSIX = BigDecimal.valueOf(3.6);
	private static final BigDecimal ROAD_WIDTH_FOUR_POINTEIGHT = BigDecimal.valueOf(4.8);
	private static final BigDecimal ROAD_WIDTH_SIX_POINTONE = BigDecimal.valueOf(6.1);
	private static final BigDecimal ROAD_WIDTH_NINE_POINTONE = BigDecimal.valueOf(9.1);
	private static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);

	private static final int PLOTAREA_100 = 100;
	private static final int PLOTAREA_300 = 300;
	private static final int PLOTAREA_500 = 500;
	private static final int PLOTAREA_1000 = 1000;
	private static final int PLOTAREA_3000 = 3000;
	/*
	 * private static final BigDecimal ROAD_WIDTH_EIGHTEEN_POINTTHREE =
	 * BigDecimal.valueOf(18.3); private static final BigDecimal
	 * ROAD_WIDTH_TWENTYFOUR_POINTFOUR = BigDecimal.valueOf(24.4); private static
	 * final BigDecimal ROAD_WIDTH_TWENTYSEVEN_POINTFOUR = BigDecimal.valueOf(27.4);
	 * private static final BigDecimal ROAD_WIDTH_THIRTY_POINTFIVE =
	 * BigDecimal.valueOf(30.5);
	 */

	public static final String OLD = "OLD";
	public static final String NEW = "NEW";
	public static final String OLD_AREA_ERROR = "road width old area";
	public static final String NEW_AREA_ERROR = "road width new area";
	public static final String OLD_AREA_ERROR_MSG = "No construction shall be permitted if the road width is less than 2.4m for old area.";
	public static final String NEW_AREA_ERROR_MSG = "No construction shall be permitted if the road width is less than 6.1m for new area.";
	public static final String NO_OF_FLOORS = "Top half floor must be 50% of previous floor";
	public static final String HEIGHT_BUILDING = "Maximum height of building allowed...";
	public static final String MIN_PLINTH_HEIGHT = " >= 0.45";
	public static final String MIN_PLINTH_HEIGHT_DESC = "Minimum plinth height";
	public static final String MAX_BSMNT_CELLAR = "Number of basement/cellar allowed";
	public static final String MIN_INT_COURT_YARD = "0.15";
	public static final String MIN_INT_COURT_YARD_DESC = "Minimum interior courtyard";
	public static final String BARRIER_FREE_ACCESS_FOR_PHYSICALLY_CHALLENGED_PEOPLE_DESC = "Barrier free access for physically challenged people";
	public static final String GREEN_BUILDINGS_AND_SUSTAINABILITY_PROVISIONS_ERROR_CODE = "Green buildings and sustainability provisions";
	public static final String GREEN_BUILDINGS_AND_SUSTAINABILITY_PROVISIONS_ERROR_MSG = "Green buildings and sustainability provision should be YES";
	public static final String GREEN_BUILDINGS_AND_SUSTAINABILITY = "Green buildings and sustainability provisions";
	public static final String FIRE_PROTECTION_AND_FIRE_SAFETY_REQUIREMENTS_DESC = "Fire Protection And Fire Safety Requirements";

	public static final BigDecimal HEIGHT_SK_6 = BigDecimal.valueOf(6);
	public static final BigDecimal HEIGHT_SK_9 = BigDecimal.valueOf(9);
	public static final BigDecimal HEIGHT_SK_12 = BigDecimal.valueOf(12);
	public static final BigDecimal HEIGHT_SK_15 = BigDecimal.valueOf(15);
	public static final BigDecimal HEIGHT_SK_18 = BigDecimal.valueOf(18);

	public static final BigDecimal ZONE_SK_1 = BigDecimal.valueOf(1);
	public static final BigDecimal ZONE_SK_2 = BigDecimal.valueOf(2);
	public static final BigDecimal ZONE_SK_3 = BigDecimal.valueOf(3);
	public static final BigDecimal ZONE_SK_4 = BigDecimal.valueOf(4);
	public static final BigDecimal ZONE_SK_5 = BigDecimal.valueOf(5);

	public static final BigDecimal STOREY_SK_2 = BigDecimal.valueOf(2);
	public static final BigDecimal STOREY_SK_3 = BigDecimal.valueOf(3);
	public static final BigDecimal STOREY_SK_4 = BigDecimal.valueOf(4);
	public static final BigDecimal STOREY_SK_5 = BigDecimal.valueOf(5);
	public static final BigDecimal STOREY_SK_6 = BigDecimal.valueOf(6);

	public static final BigDecimal STOREY_ZONE1_SK_5_5 = BigDecimal.valueOf(5.5);
	public static final BigDecimal STOREY_ZONE2_SK_4_5 = BigDecimal.valueOf(4.5);
	public static final BigDecimal STOREY_ZONE3_SK_3_5 = BigDecimal.valueOf(3.5);
	public static final BigDecimal STOREY_ZONE4_SK_2_5 = BigDecimal.valueOf(2.5);
	public static final BigDecimal STOREY_ZONE5_SK_1_5 = BigDecimal.valueOf(1.5);

	private static final String ZONE_TYPE = "Zone";
	private static final String TOTAL_FLOOR = "Total Floor";

	@Override
	public Plan validate(Plan pl) {

		HashMap<String, String> errors = new HashMap<>();

		List<Block> blocks = pl.getBlocks();

		for (Block block : blocks) {
			if (block.getBuilding() != null) {
				if (block.getBuilding().getBuildingHeight().compareTo(BigDecimal.ZERO) == 0) {
					errors.put(String.format(DcrConstants.BLOCK_BUILDING_HEIGHT, block.getNumber()),
							edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
									new String[] {
											String.format(DcrConstants.BLOCK_BUILDING_HEIGHT, block.getNumber()) },
									LocaleContextHolder.getLocale()));
					pl.addErrors(errors);
				}
			}
		}

		
		return pl;
	}

	@Override
	public Plan process(Plan pl) {

		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));

			return pl;
		}

		pl.getErrors().forEach((k, v) -> System.out.println(("ERRORS: " + k + ":" + v)));

		HashMap<String, String> errors = new HashMap<>();
		validate(pl);

		String typeOfArea = null;
		if (pl.getPlanInformation().getTypeOfArea() != null)
			typeOfArea = pl.getPlanInformation().getTypeOfArea();

		BigDecimal roadWidth = null;
		if (pl.getPlanInformation().getRoadWidth() != null)
			roadWidth = pl.getPlanInformation().getRoadWidth();

		/*
		 * if (StringUtils.isNotBlank(typeOfArea) && roadWidth != null) { }
		 */
		try {
			validateNumberOfFloorsSikkim(pl, errors, typeOfArea, roadWidth);
		} catch (Exception e) {
			// TODO: handle exception
		}

		/* validateHabitableRoomOfBuildingSikkim(pl, errors); */
		return pl;
	}

	private Plan validateNumberOfFloorsSikkim(Plan pl, HashMap<String, String> errors, String typeOfArea,
			BigDecimal roadWidth) {

		BigDecimal ZONE_DC = BigDecimal.ZERO;
		ZONE_DC = Utility.getBuildingStabilityZone(pl);
		if (ZONE_DC.compareTo(BigDecimal.ZERO) <= 0 || ZONE_DC.compareTo(BigDecimal.valueOf(6)) >= 0)
			return pl;

		for (Block block : pl.getBlocks()) {

			BigDecimal totalFloorArea = BigDecimal.valueOf(0);
			BigDecimal maxFloorArea = BigDecimal.valueOf(0);
			BigDecimal topFloorArea = BigDecimal.valueOf(0);
			BigDecimal maxFloor = BigDecimal.valueOf(0), minFloor = BigDecimal.ZERO;

			if (block.getBuilding().getFloors() != null && !block.getBuilding().getFloors().isEmpty()) {
				minFloor = new BigDecimal(block.getBuilding().getFloors().stream()
						.min(Comparator.comparing(Floor::getNumber)).get().getNumber());
//				maxFloor = new BigDecimal(block.getBuilding().getFloors().stream().max(Comparator.comparing(Floor::getNumber)).get().getNumber());
				System.out.println(block.getBuilding().getMaxFloor());

				maxFloor = block.getBuilding().getMaxFloor();
			}
			System.out.println("Min Floor " + minFloor);
			System.out.println("Max Floor " + maxFloor);

			int i = 0;
			int count = 0;
			int[] floorNos = new int[100];
			
			
			int[] counter = new int[100];
			for (Floor floor : block.getBuilding().getFloors()) {
				String floorocc = "";

				totalFloorArea = BigDecimal.valueOf(0);

				if (floor.getArea() != null && floor.getArea().compareTo(BigDecimal.valueOf(0.0)) > 0) {
					totalFloorArea = floor.getArea();
				} else {
					if (floor.getOccupancies() != null) {

						for (Occupancy occupancy : floor.getOccupancies()) {
							// checking for floor type occupancy
							if (occupancy.getTypeHelper() == null || occupancy.getTypeHelper().getType() == null
									|| occupancy.getTypeHelper().getSubtype() == null) {
								errors.put("Floor No " + floor.getNumber() + " Occupancy error",
										"Floor No " + floor.getNumber() + " occupancy is not present");
								pl.addErrors(errors);
							} else {
								floorocc = occupancy.getTypeHelper().getSubtype().getCode();

								
							}
							if (occupancy.getFloorArea() != null)
								totalFloorArea = totalFloorArea.add(occupancy.getFloorArea());

						}
//						occupancy.setFloorArea(flrDimensionMap.get(flrOccMap).flrArea.add(occupancy.getFloorArea()));
					}

					floor.setArea(totalFloorArea);
					
				}

				if (totalFloorArea.compareTo(maxFloorArea) > 0) {
					maxFloorArea = totalFloorArea;
				}
				counter[count] = floor.getNumber();
				count++;
				
				floorNos[i]=floor.getNumber();
				i++;
			}
			
			

			// Find Repeating Floors
//			for(int k=0;k<i;k++) {
//				for(int j=k+1;j<i;j++) {
//					if(floorNos[k]==floorNos[j]) {
//						errors.put("Floor No "+ floorNos[k] +" Repeating error","Floor No "+ floorNos[k] + " is repeating");
//						pl.addErrors(errors);
//					}
//				}
//			}
			
			// Find Missing Floor Number

			int c = 0;
			for (int k = minFloor.intValue(); k <= maxFloor.intValue()-1; k++) {
				if (counter[c] != k) {
					errors.put("Floor No " + k + " Missing ", "Floor No " + k + " Missing");
					pl.addErrors(errors);
				} else
					c++;

			}
			boolean isAccepted = false;
			ScrutinyDetail scrutinyDetail = getNewScrutinyDetailRoadArea(
					"Block_" + block.getNumber() + "_" + "Number of Floors");

			if (block.getBuilding().getTotalFloors() == null
					|| block.getBuilding().getTotalFloors().compareTo(BigDecimal.ZERO) == 0)
				block.getBuilding().setTotalFloors(new BigDecimal(block.getBuilding().getFloors().size()));

			BigDecimal totalFloors = block.getBuilding().getTotalFloors();
			String requiredFloorCount = StringUtils.EMPTY;
			topFloorArea = block.getBuilding().getFloors().get(maxFloor.intValue()).getArea();

			if (ZONE_DC.compareTo(ZONE_SK_1) == 0 && totalFloors.compareTo(STOREY_SK_6) <= 0) {
				requiredFloorCount = "<= " + STOREY_ZONE1_SK_5_5;

				if (totalFloors.compareTo(STOREY_ZONE1_SK_5_5) > 0) {
					if ((topFloorArea.multiply(BigDecimal.valueOf(100.0)).divide(maxFloorArea, BigDecimal.ROUND_UP))
							.compareTo(BigDecimal.valueOf(50.0)) <= 0) {
						isAccepted = true;
					} else {
						isAccepted = false;
					}
				} else {
					isAccepted = true;
				}

			} else if (ZONE_DC.compareTo(ZONE_SK_2) == 0 && totalFloors.compareTo(STOREY_SK_5) <= 0) {

				requiredFloorCount = "<= " + STOREY_ZONE2_SK_4_5;

				if (totalFloors.compareTo(STOREY_ZONE2_SK_4_5) > 0) {
					if ((topFloorArea.multiply(BigDecimal.valueOf(100.0)).divide(maxFloorArea, BigDecimal.ROUND_UP))
							.compareTo(BigDecimal.valueOf(50.0)) <= 0) {
						isAccepted = true;
					} else {
						isAccepted = false;
					}
				} else {
					isAccepted = true;
				}

			} else if (ZONE_DC.compareTo(ZONE_SK_3) == 0 && totalFloors.compareTo(STOREY_SK_4) <= 0) {

				requiredFloorCount = "<= " + STOREY_ZONE3_SK_3_5;

				if (totalFloors.compareTo(STOREY_ZONE3_SK_3_5) > 0) {
					if ((topFloorArea.multiply(BigDecimal.valueOf(100.0)).divide(maxFloorArea, BigDecimal.ROUND_UP))
							.compareTo(BigDecimal.valueOf(50.0)) <= 0) {
						isAccepted = true;
					} else {
						isAccepted = false;
					}
				} else {
					isAccepted = true;
				}
			} else if (ZONE_DC.compareTo(ZONE_SK_4) == 0 && totalFloors.compareTo(STOREY_SK_3) <= 0) {

				requiredFloorCount = "<= " + STOREY_ZONE4_SK_2_5;

				if (totalFloors.compareTo(STOREY_ZONE4_SK_2_5) > 0) {

					if ((topFloorArea.multiply(BigDecimal.valueOf(100.0)).divide(maxFloorArea, 2, BigDecimal.ROUND_UP))
							.compareTo(BigDecimal.valueOf(50.0)) <= 0) {
						isAccepted = true;
					} else {
						isAccepted = false;
					}
				} else {
					isAccepted = true;
				}
			} else if (ZONE_DC.compareTo(ZONE_SK_5) == 0 && totalFloors.compareTo(STOREY_SK_2) <= 0) {

				requiredFloorCount = "<= " + STOREY_ZONE5_SK_1_5;

				if (totalFloors.compareTo(STOREY_ZONE5_SK_1_5) > 0) {

					if ((topFloorArea.multiply(BigDecimal.valueOf(100.0)).divide(maxFloorArea, BigDecimal.ROUND_UP))
							.compareTo(BigDecimal.valueOf(50.0)) <= 0) {
						isAccepted = true;
					} else {
						isAccepted = false;
					}
				} else {
					isAccepted = true;
				}
			} else {
				isAccepted = false;
				if (ZONE_DC.compareTo(ZONE_SK_1) == 0) {
					requiredFloorCount = "<= " + STOREY_ZONE1_SK_5_5;
				} else if (ZONE_DC.compareTo(ZONE_SK_2) == 0) {
					requiredFloorCount = "<= " + STOREY_ZONE2_SK_4_5;
				} else if (ZONE_DC.compareTo(ZONE_SK_3) == 0) {
					requiredFloorCount = "<= " + STOREY_ZONE3_SK_3_5;
				} else if (ZONE_DC.compareTo(ZONE_SK_4) == 0) {
					requiredFloorCount = "<= " + STOREY_ZONE2_SK_4_5;
				} else if (ZONE_DC.compareTo(ZONE_SK_5) == 0) {
					requiredFloorCount = "<= " + STOREY_ZONE5_SK_1_5;
				}
			}

			if (errors.isEmpty() && StringUtils.isNotBlank(requiredFloorCount)) {
				Map<String, String> details = new HashMap<>();
				details.put(RULE_NO, RULE_38);
				details.put(DESCRIPTION, NO_OF_FLOORS);
				details.put(ZONE_TYPE, "Zone " + ZONE_DC);
				details.put(TOTAL_FLOOR, totalFloors.toString());
				details.put(PERMISSIBLE, requiredFloorCount);
				details.put(PROVIDED, String.valueOf(block.getBuilding().getFloorsAboveGround()));
				details.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
				scrutinyDetail.getDetail().add(details);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}
		}

		return pl;
	}

	private static final String FLOOR = "Floor";

	private static final String HABITABLE_ROOM_DESC = "Habitable Room";

	private static final BigDecimal MINIMUM_WIDTH_HABITABLE_ROOM_SK_2_43 = BigDecimal.valueOf(2.43);
	private static final BigDecimal MINIMUM_AREA_HABITABLE_ROOM_SK_7_43 = BigDecimal.valueOf(7.43);
	private static final BigDecimal MINIMUM_HEIGHT_HABITABLE_ROOM_SK_2_75 = BigDecimal.valueOf(2.75);

	private Plan validateHabitableRoomOfBuildingSikkim(Plan plan, HashMap<String, String> errors) {

		Boolean isHabitableRoomDefined = false;

		if (plan != null && plan.getBlocks() != null && !plan.getBlocks().isEmpty()) {

			for (Block blk : plan.getBlocks()) {

				scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, FLOOR);
				scrutinyDetail.addColumnHeading(4, REQUIRED);
				scrutinyDetail.addColumnHeading(5, PROVIDED);
				scrutinyDetail.addColumnHeading(6, STATUS);

				scrutinyDetail.setKey("Block_" + blk.getNumber() + "_" + "Habitable Room");

				if (blk.getBuilding() != null && blk.getBuilding().getFloors() != null
						&& !blk.getBuilding().getFloors().isEmpty()) {
					for (Floor flr : blk.getBuilding().getFloors()) {

						BigDecimal minWidth = BigDecimal.ZERO, minArea = BigDecimal.ZERO,
								totalRoomFloorArea = BigDecimal.ZERO, minHeight = BigDecimal.ZERO,
								totalLightAndVentilationArea = BigDecimal.ZERO,
								lightAndVentilationToFloorAreaCoverage = BigDecimal.ZERO;

						Boolean widthNotDefined = false, areaNotDefined = false, heightNotDefined = false,
								lightAndVentilationNotDefined = false, valid = false;

						List<BigDecimal> roomFloorAreas = new ArrayList<BigDecimal>(),
								lightAndVentilationAreas = new ArrayList<BigDecimal>();

						if (flr.getRegularRooms() != null && !flr.getRegularRooms().isEmpty()) {

							isHabitableRoomDefined = true;

							for (Room room : flr.getRegularRooms()) {

								if (room.getHeights() != null && !room.getHeights().isEmpty()) {

									heightNotDefined = false;
									minHeight = room.getHeights().get(0).getHeight();

									for (RoomHeight roomHeight : room.getHeights()) {

										if (roomHeight.getHeight() != null
												&& roomHeight.getHeight().compareTo(BigDecimal.ZERO) > 0) {
											if (minHeight.compareTo(roomHeight.getHeight()) >= 0) {
												minHeight = roomHeight.getHeight();
											}
										} else {
											heightNotDefined = true;
										}
									}

								} else {
									heightNotDefined = true;
								}

								if (!heightNotDefined) {
									if (minHeight.compareTo(MINIMUM_HEIGHT_HABITABLE_ROOM_SK_2_75) >= 0) {
										valid = true;
										setHabitableRoomSikkinReportOutputDetails(plan, flr.getNumber().toString(),
												"RULE TBD", String.format("Minimum %s height", HABITABLE_ROOM_DESC),
												MINIMUM_HEIGHT_HABITABLE_ROOM_SK_2_75.toString(), minHeight.toString(),
												Result.Accepted.getResultVal(), scrutinyDetail);
									} else {
										valid = false;
										setHabitableRoomSikkinReportOutputDetails(plan, flr.getNumber().toString(),
												"RULE TBD", String.format("Minimum %s height", HABITABLE_ROOM_DESC),
												MINIMUM_HEIGHT_HABITABLE_ROOM_SK_2_75.toString(), minHeight.toString(),
												Result.Not_Accepted.getResultVal(), scrutinyDetail);
									}
								} else {
									errors.put(
											String.format("% Block %s Floor %s height not defined ",
													HABITABLE_ROOM_DESC, blk.getNumber(), flr.getNumber()),
											edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
													new String[] { String.format(
															"%s Block %s Floor %s height not defined ",
															HABITABLE_ROOM_DESC, blk.getNumber(), flr.getNumber()) },
													LocaleContextHolder.getLocale()));
									plan.addErrors(errors);
								}

								if (room.getRooms() != null && !room.getRooms().isEmpty()) {

									widthNotDefined = false;
									areaNotDefined = false;

									minWidth = room.getRooms().get(0).getWidth();
									minArea = room.getRooms().get(0).getArea();

									for (Measurement measurement : room.getRooms()) {
										if (measurement.getWidth() != null
												&& measurement.getWidth().compareTo(BigDecimal.ZERO) > 0) {
											if (minWidth.compareTo(measurement.getWidth()) >= 0)
												minWidth = measurement.getWidth();

										} else {
											widthNotDefined = true;
										}

										if (measurement.getArea() != null
												&& measurement.getArea().compareTo(BigDecimal.ZERO) > 0) {

											roomFloorAreas.add(measurement.getArea());
											if (minArea.compareTo(measurement.getArea()) >= 0
													&& measurement.getArea().compareTo(BigDecimal.ZERO) > 0)
												minArea = measurement.getArea();

										} else {
											areaNotDefined = true;
										}

									}

								} else {
									widthNotDefined = true;
									areaNotDefined = true;
								}

								if (!widthNotDefined) {
									if (minWidth.compareTo(MINIMUM_WIDTH_HABITABLE_ROOM_SK_2_43) >= 0) {
										valid = true;
										setHabitableRoomSikkinReportOutputDetails(plan, flr.getNumber().toString(),
												"RULE TBD", String.format("Minimum %s width", HABITABLE_ROOM_DESC),
												MINIMUM_WIDTH_HABITABLE_ROOM_SK_2_43.toString(), minWidth.toString(),
												Result.Accepted.getResultVal(), scrutinyDetail);
									} else {
										valid = false;
										setHabitableRoomSikkinReportOutputDetails(plan, flr.getNumber().toString(),
												"RULE TBD", String.format("Minimum %s width", HABITABLE_ROOM_DESC),
												MINIMUM_WIDTH_HABITABLE_ROOM_SK_2_43.toString(), minWidth.toString(),
												Result.Not_Accepted.getResultVal(), scrutinyDetail);
									}
								} else {
									errors.put(
											String.format("%s Block %s Floor %s width not defined ",
													HABITABLE_ROOM_DESC, blk.getNumber(), flr.getNumber()),
											edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
													new String[] { String.format(
															"%s Block %s Floor %s width not defined ",
															HABITABLE_ROOM_DESC, blk.getNumber(), flr.getNumber()) },
													LocaleContextHolder.getLocale()));
									plan.addErrors(errors);
								}

								if (minArea.compareTo(MINIMUM_AREA_HABITABLE_ROOM_SK_7_43) >= 0) {
									valid = true;
									setHabitableRoomSikkinReportOutputDetails(plan, flr.getNumber().toString(),
											"RULE TBD", String.format("Minimum %s area", HABITABLE_ROOM_DESC),
											MINIMUM_AREA_HABITABLE_ROOM_SK_7_43.toString(), minArea.toString(),
											Result.Accepted.getResultVal(), scrutinyDetail);
								} else {
									valid = false;
									setHabitableRoomSikkinReportOutputDetails(plan, flr.getNumber().toString(),
											"RULE TBD", String.format("Minimum %s area ", HABITABLE_ROOM_DESC),
											MINIMUM_AREA_HABITABLE_ROOM_SK_7_43.toString(), minArea.toString(),
											Result.Not_Accepted.getResultVal(), scrutinyDetail);
								}

							}

						}

					}

					if (!isHabitableRoomDefined) {
						errors.put(
								String.format("Block %s %s %s", blk.getNumber(), HABITABLE_ROOM_DESC,
										DcrConstants.OBJECTNOTDEFINED),
								String.format("Block %s %s %s", blk.getNumber(), HABITABLE_ROOM_DESC,
										DcrConstants.OBJECTNOTDEFINED_DESC));
					}
				}
			}
		}

		if (!errors.isEmpty())
			plan.addErrors(errors);

		return plan;
	}

	private void setHabitableRoomSikkinReportOutputDetails(Plan plan, String floor, String ruleNo, String ruleDesc,
			String expected, String actual, String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(FLOOR, String.format("%s %s", FLOOR, floor));
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private ScrutinyDetail getNewScrutinyDetailRoadArea(String key) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, ZONE_TYPE);
		scrutinyDetail.addColumnHeading(4, TOTAL_FLOOR);
		scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(6, PROVIDED);
		scrutinyDetail.addColumnHeading(7, STATUS);
		scrutinyDetail.setKey(key);
		return scrutinyDetail;
	}

	private ScrutinyDetail getNewScrutinyDetailBuildingHeight(String key) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, ZONE_TYPE);
		scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(6, PROVIDED);
		scrutinyDetail.addColumnHeading(7, STATUS);
		scrutinyDetail.setKey(key);
		return scrutinyDetail;
	}

}

