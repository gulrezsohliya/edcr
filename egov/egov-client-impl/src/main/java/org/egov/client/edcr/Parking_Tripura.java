
package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.egov.client.constants.DxfFileConstants_AR;
import static org.egov.client.constants.DxfFileConstants_AR.*;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.EdcrPdfDetail;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.FloorUnit;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.NotifiedRoad;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.ParkingDetails;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.feature.Parking;
import org.springframework.stereotype.Service;

@Service
public class Parking_Tripura extends Parking {

	/*************************************************
	 * Check heightfromfloortobottomofbeam >=2.4 for stilt parking
	 */

	private static final Logger LOGGER = Logger.getLogger(Parking_Tripura.class);

	private static final String RULE = "13.8.5";
	private static final String RULE_DESCRIPTION = "Parking space";
	private static final String SQMTRS = " m²";

	// ECS Values
	double STD_OPEN_ECS = 0;
	double STD_STILT_ECS = 0;
	double STD_OPEN_ECS_HEAVY = 0;
	double STD_STILT_ECS_HEAVY = 0;
	double STD_BSMNT_ECS = 32;
	private static final BigDecimal TwoWheeler_ECS_SIX = BigDecimal.valueOf(6);

	@Override
	public Plan validate(Plan pl) {
		try {
			if (pl.getPlot() == null || (pl.getPlot() != null
					&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
				pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
				return pl;
			}
			if (pl.getBlocks() == null) {
				return pl;
			}
			if (pl.getNotifiedRoads() != null) {
				for (NotifiedRoad r : pl.getNotifiedRoads()) {
					if (r.getColorCode() == 4) {
						if (r.getWidth().compareTo(BigDecimal.ZERO) > 0) {
							pl.getPlanInformation().setRoadWidth(r.getWidth().setScale(2, RoundingMode.HALF_UP));
						} else {
							pl.addError("NotifiedWidth", "Error getting road width");
						}
					}
				}
			}
			BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();
			if (roadWidth.compareTo(BigDecimal.valueOf(2.4)) >= 0) {
				processParking(pl);
			}

		} catch (Exception e) {
			System.out.println("Parking :: error ::" + e);
		}
		return pl;
	}

	@Override
	public Plan process(Plan pl) {

		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Parking");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, "Occupancy");
		scrutinyDetail.addColumnHeading(3, "Type of Parking");
		scrutinyDetail.addColumnHeading(4, "Area Standard");
		scrutinyDetail.addColumnHeading(5, "Polygon Area in plan");
		scrutinyDetail.addColumnHeading(6, "Nos Provided in plan");
		scrutinyDetail.addColumnHeading(7, "Required Nos");
		scrutinyDetail.addColumnHeading(8, STATUS);
		validate(pl);
		return pl;
	}

	@SuppressWarnings("static-access")
	public void processParking(Plan pl) {
		try {
			BigDecimal totalCoverageArea = BigDecimal.ZERO;
			List<Pair<BigDecimal, Integer>> stiltsParkingList = new ArrayList<>();
			List<Pair<BigDecimal, Integer>> stiltsExistParkingList = new ArrayList<>();
			List<Pair<BigDecimal, Integer>> openParkingList = new ArrayList<>();
			List<Pair<BigDecimal, Integer>> basementParkingList = new ArrayList<>();
			List<Pair<BigDecimal, Integer>> garagrParkingList = new ArrayList<>();
			List<Pair<BigDecimal, Integer>> groundBUAList = new ArrayList<>();

			for (Block block : pl.getBlocks()) {
				totalCoverageArea = block.getBuilding().getCoverageArea();
				if (block.getBuilding() == null
						|| (block.getBuilding() != null && block.getBuilding().getFloors() == null)) {
					return;
				}
			}

			for (Measurement parking : pl.getParkingDetails().getOpenCars()) {
				openParkingList.add(Pair.of(parking.getArea(), parking.getColorCode()));
			}
			Boolean stiltParkingExist = Boolean.FALSE;
			Boolean stiltExist = Boolean.FALSE;
			BigDecimal stiltExistParkingArea = BigDecimal.ZERO;
			for (Block block : pl.getBlocks()) {
				for (Floor floor : block.getBuilding().getFloors()) {
					for (Measurement parking : floor.getStiltExist().getMeasurements()) {
						BigDecimal area = BigDecimal.ZERO;
						if (floor.getStiltExist() != null) {
							stiltParkingExist = Boolean.TRUE;
							for (Measurement existparking : floor.getStiltExist().getMeasurements()) {
								if (existparking.getColorCode() == parking.getColorCode()) {
									stiltExist = true;
									area = existparking.getArea();
									System.out.println(area);
									stiltExistParkingArea = stiltExistParkingArea.add(area);
								}
							}
						}
						stiltsParkingList.add(Pair.of(stiltExistParkingArea, parking.getColorCode()));
						System.out.println(stiltExistParkingArea);
						for (Occupancy occupancies : floor.getOccupancies()) {
							if (parking.getColorCode() == occupancies.getTypeHelper().getSubtype().getColor()) {
								groundBUAList.add(Pair.of(parking.getArea(),
										occupancies.getTypeHelper().getSubtype().getColor()));
							}
						}
					}
					for (Measurement parking : floor.getParking().getStilts()) {
						stiltExist = true;
						garagrParkingList.clear();
						if (stiltParkingExist) {
							stiltsParkingList.clear();
							stiltsParkingList
									.add(Pair.of(parking.getArea().add(stiltExistParkingArea), parking.getColorCode()));
							for (Occupancy occupancies : floor.getOccupancies()) {
								if (parking.getColorCode() == occupancies.getTypeHelper().getSubtype().getColor()) {
									groundBUAList.add(Pair.of(parking.getArea().add(stiltExistParkingArea),
											occupancies.getTypeHelper().getSubtype().getColor()));
								}
							}
						} else {
							stiltsParkingList.add(Pair.of(parking.getArea(), parking.getColorCode()));
							for (Occupancy occupancies : floor.getOccupancies()) {
								if (parking.getColorCode() == occupancies.getTypeHelper().getSubtype().getColor()) {
									groundBUAList.add(Pair.of(parking.getArea(),
											occupancies.getTypeHelper().getSubtype().getColor()));
								}
							}
						}

					}

					if (floor.getRegularRooms() != null && floor.getRegularRooms().size() > 0) {
						for (Room room : floor.getRegularRooms()) {
							for (Measurement r : room.getRooms()) {
								if (r.getColorCode() == 4 || r.getColorCode() == 9) {
									garagrParkingList.add(Pair.of(r.getArea(), 30));
									groundBUAList.add(Pair.of(r.getArea(), 30));
								}
							}

						}
					}

					for (Measurement parking : floor.getParking().getBasementCars()) {
						basementParkingList.add(Pair.of(parking.getArea(), parking.getColorCode()));
						for (Occupancy occupancies : floor.getOccupancies()) {
							if (parking.getColorCode() == occupancies.getTypeHelper().getSubtype().getColor()) {
								groundBUAList.add(Pair.of(parking.getArea(),
										occupancies.getTypeHelper().getSubtype().getColor()));
							}
						}
					}

				}

			}
			scrutinyDetail.setHeading("Parking");
			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;
			Boolean mixedOccupancy = Boolean.FALSE;
			int occCounter = 0;
			Boolean residentialExist = Boolean.FALSE;
			for (Block block : pl.getBlocks()) {
				for (Occupancy occup : block.getBuilding().getOccupancies()) {
					if (occup.getTypeHelper().getType().getCode().equalsIgnoreCase(R)) {
						residentialExist = Boolean.TRUE;
					}
					occCounter = occCounter + 1;
				}
			}

			if (occCounter > 1 && residentialExist) {
				mixedOccupancy = Boolean.TRUE;
			}

			Boolean existingBuilding = false;
			BigDecimal proposedBUA = BigDecimal.ZERO;
			for (Block b : pl.getBlocks()) {
				if (b.getBuilding().getTotalExistingBuiltUpArea().compareTo(BigDecimal.ZERO) > 0) {
					existingBuilding = true;
					proposedBUA = b.getBuilding().getTotalBuitUpArea()
							.subtract(b.getBuilding().getTotalExistingBuiltUpArea());
//					proposedBUA=b.getBuilding().getTotalBuitUpArea();
				}
				if (stiltExist) {
					for (Floor f : b.getBuilding().getFloors()) {
						if (f.getParking().getStilts() != null && !f.getParking().getStilts().isEmpty()) {
							if (f.getHeightFromFloorToBottomOfBeam() != null
									&& !f.getHeightFromFloorToBottomOfBeam().isEmpty()) {
								build(pl, f.getNumber(), f.getHeightFromFloorToBottomOfBeam().get(0));
							} else {
								pl.addError("ParkingHeightError",
										"Please mark the dimension of stilt parking in floor " + f.getNumber());
							}
						}

					}
				}
			}

			BigDecimal openECS = BigDecimal.ZERO, stiltECS = BigDecimal.ZERO, bsmtECS = BigDecimal.ZERO,
					openECSHeavy = BigDecimal.ZERO, stiltECSHeavy = BigDecimal.ZERO, totalEcs = BigDecimal.ZERO;
			double ecs = 0;
			double ecsHeavy = 0;
			int occflag = 0;
			for (Occupancy o : pl.getOccupancies()) {
				if (totalCoverageArea.compareTo(BigDecimal.valueOf(70)) < 0
						&& o.getTypeHelper().getType().getCode() == "R") {
					return;
				}
				if (totalCoverageArea.compareTo(BigDecimal.valueOf(50)) < 0
						&& o.getTypeHelper().getType().getCode() == "B") {
					return;
				}
				occflag = occflag + 1;
			}
			if (occflag == 1) {
				for (Occupancy occ : pl.getOccupancies()) {
					int code = occ.getTypeHelper().getSubtype().getColor();
					System.out.println(code);
					System.out.println(occ.getColorCode());
					int parkingcode = 0;
					BigDecimal bua = occ.getFloorArea();
					BigDecimal area = BigDecimal.ZERO;
					for (Map.Entry<BigDecimal, Integer> e : groundBUAList) {
						System.out.println(e.getValue());
						if (e.getValue() == code) {
							bua = bua.subtract(e.getKey());
						}
					}
					if (totalCoverageArea.compareTo(BigDecimal.ZERO) > 0) {
						ecs = getECS(code, totalCoverageArea, bua);
						if (code == 214) {
							ecsHeavy = getECSHeavy(code, totalCoverageArea, bua);
						}

						ecs = ecs + ecsHeavy;
					}
					if (bua.compareTo(BigDecimal.ZERO) > 0) {
						for (Map.Entry<BigDecimal, Integer> entry : stiltsParkingList) {
							if (occ.getTypeHelper().getSubtype().getCode().equalsIgnoreCase(R1a)) {
								STD_STILT_ECS = 10.8;
							} else {
								if (occ.getTypeHelper().getSubtype().getCode().equalsIgnoreCase(IN1a)) {
									if (pl.getPlanInfoProperties().get("DRIVEWAY") != null) {
										if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("SINGLY")) {
											STD_STILT_ECS_HEAVY = 58;
										} else if (pl.getPlanInfoProperties().get("DRIVEWAY")
												.equalsIgnoreCase("DOUBLY")) {
											STD_STILT_ECS_HEAVY = 56;
										}
									} else {
										pl.addError("stiltECS", "Please provide SINGLY/DOUBLY in DRIVEWAY of planinfo");
									}
								}
								if (pl.getPlanInfoProperties().get("DRIVEWAY") != null) {
									if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("SINGLY")) {
										STD_STILT_ECS = 21;
									} else if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("DOUBLY")) {
										STD_STILT_ECS = 16;
									}
								} else {
									pl.addError("stiltECS", "Please provide SINGLY/DOUBLY in DRIVEWAY of planinfo");
								}
							}

							parkingcode = entry.getValue();
							area = entry.getKey();
							if (parkingcode == code) {

								if (totalCoverageArea.compareTo(BigDecimal.ZERO) > 0) {
									stiltECS = area.divide(BigDecimal.valueOf(STD_STILT_ECS), 2, RoundingMode.HALF_UP);
									stiltECS = stiltECS.setScale(0, RoundingMode.DOWN);
									totalEcs = totalEcs.add(stiltECS);
									if (STD_STILT_ECS_HEAVY > 0) {
										stiltECSHeavy = area.divide(BigDecimal.valueOf(STD_STILT_ECS_HEAVY), 2,
												RoundingMode.HALF_UP);
										stiltECSHeavy = stiltECSHeavy.setScale(0, RoundingMode.DOWN);
										totalEcs = totalEcs.add(stiltECSHeavy);

										setReportOutputDetails(pl,
												occ.getTypeHelper().getSubtype().getName()
														+ " / BUA after deduction of concerned parking(" + bua + ")",
												RULE, "Under Stilts(Heavy)", area + SQMTRS,
												(new BigDecimal(STD_STILT_ECS)).doubleValue() + SQMTRS,
												stiltECSHeavy.setScale(0, RoundingMode.DOWN) + "", "-" + "",
												Result.Verify.getResultVal());
									}
									setReportOutputDetails(pl,
											occ.getTypeHelper().getSubtype().getName()
													+ " / BUA after deduction of concerned parking(" + bua + ")",
											RULE, "Under Stilts", area + SQMTRS,
											(new BigDecimal(STD_STILT_ECS)).doubleValue() + SQMTRS,
											stiltECS.setScale(0, RoundingMode.DOWN) + "", "-" + "",
											Result.Verify.getResultVal());
								}

							} else {
								pl.addError("ParkingcodeError",
										"Please specify the color code of parking as the color code of the occupancy defined");
							}
						}

						for (Map.Entry<BigDecimal, Integer> entry : garagrParkingList) {
							if (occ.getTypeHelper().getSubtype().getCode().equalsIgnoreCase(R1a)) {
								STD_STILT_ECS = 10.8;
							} else {
								if (pl.getPlanInfoProperties().get("DRIVEWAY") != null) {
									if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("SINGLY")) {
										STD_STILT_ECS = 21;
									} else if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("DOUBLY")) {
										STD_STILT_ECS = 16;
									}
								} else {
									pl.addError("stiltECS", "Please provide SINGLY/DOUBLY in DRIVEWAY of planinfo");
								}
							}

							parkingcode = entry.getValue();
							area = entry.getKey();
							if (parkingcode == code) {

								if (totalCoverageArea.compareTo(BigDecimal.ZERO) > 0) {
									stiltECS = area.divide(BigDecimal.valueOf(STD_STILT_ECS), 2, RoundingMode.HALF_UP);
									stiltECS = stiltECS.setScale(0, RoundingMode.DOWN);
									totalEcs = totalEcs.add(stiltECS);
									setReportOutputDetails(pl,
											occ.getTypeHelper().getSubtype().getName()
													+ " / BUA after deduction of concerned parking(" + bua + ")",
											RULE, "Garage", area + SQMTRS,
											(new BigDecimal(STD_STILT_ECS)).doubleValue() + SQMTRS,
											stiltECS.setScale(0, RoundingMode.DOWN) + "", "-" + "",
											Result.Verify.getResultVal());
								}

							}else {
								pl.addError("ParkingcodeError",
										"Please specify the color code of parking as the color code of the occupancy defined");
							}
						}

						for (Map.Entry<BigDecimal, Integer> entry : basementParkingList) {

							parkingcode = entry.getValue();
							area = entry.getKey();
							if (parkingcode == code) {
								if (totalCoverageArea.compareTo(BigDecimal.ZERO) > 0) {
									bsmtECS = area.divide(BigDecimal.valueOf(STD_BSMNT_ECS), 2, RoundingMode.HALF_UP);
									bsmtECS = bsmtECS.setScale(0, RoundingMode.DOWN);
									totalEcs = totalEcs.add(bsmtECS);
									setReportOutputDetails(pl,
											occ.getTypeHelper().getSubtype().getName()
													+ " / BUA after deduction of concerned parking(" + bua + ")",
											RULE, "Basement Parking", area + SQMTRS,
											(new BigDecimal(STD_BSMNT_ECS)).doubleValue() + SQMTRS,
											bsmtECS.setScale(0, RoundingMode.DOWN) + "", "-" + "",
											Result.Verify.getResultVal());

								}

							}else {
								pl.addError("ParkingcodeError",
										"Please specify the color code of parking as the color code of the occupancy defined");
							}
						}
						for (Map.Entry<BigDecimal, Integer> entry : openParkingList) {
							if (occ.getTypeHelper().getSubtype().getCode().equalsIgnoreCase(R1a)) {
								STD_OPEN_ECS = 10.8;
							} else {
								if (occ.getTypeHelper().getSubtype().getCode().equalsIgnoreCase(IN1a)) {
									if (pl.getPlanInfoProperties().get("DRIVEWAY") != null) {
										if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("SINGLY")) {
											STD_OPEN_ECS_HEAVY = 56.25;
										} else if (pl.getPlanInfoProperties().get("DRIVEWAY")
												.equalsIgnoreCase("DOUBLY")) {
											STD_OPEN_ECS_HEAVY = 54.37;
										}
									} else {
										pl.addError("stiltECS", "Please provide SINGLY/DOUBLY in DRIVEWAY of planinfo");
									}
								}
								if (pl.getPlanInfoProperties().get("DRIVEWAY") != null) {
									if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("SINGLY")) {
										STD_OPEN_ECS = 19.2;
									} else if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("DOUBLY")) {
										STD_OPEN_ECS = 15;
									}
								} else {
									pl.addError("openECS", "Please provide SINGLY/DOUBLY in DRIVEWAY of planinfo");
								}
							}

							parkingcode = entry.getValue();
							area = entry.getKey();
//							bua=occ.getFloorArea();
							if (parkingcode == code) {
								if (totalCoverageArea.compareTo(BigDecimal.ZERO) > 0) {
									openECS = area.divide(BigDecimal.valueOf(STD_OPEN_ECS), 2, RoundingMode.HALF_UP);
									openECS = openECS.setScale(0, RoundingMode.DOWN);
									totalEcs = totalEcs.add(openECS);
									if (STD_OPEN_ECS_HEAVY > 0) {
										setReportOutputDetails(pl,
												occ.getTypeHelper().getSubtype().getName() + " / BUA(" + bua + ")",
												RULE, "Open Parking (Heavy)", area + SQMTRS,
												(new BigDecimal(STD_OPEN_ECS_HEAVY)).doubleValue() + SQMTRS,
												openECSHeavy.setScale(0, RoundingMode.DOWN) + "", "-" + "",
												Result.Verify.getResultVal());
									}
									setReportOutputDetails(pl,
											occ.getTypeHelper().getSubtype().getName() + " / BUA(" + bua + ")", RULE,
											"Open Parking", area + SQMTRS,
											(new BigDecimal(STD_OPEN_ECS)).doubleValue() + SQMTRS,
											openECS.setScale(0, RoundingMode.DOWN) + "", "-" + "",
											Result.Verify.getResultVal());
								}

							}else {
								pl.addError("ParkingcodeError",
										"Please specify the color code of parking as the color code of the occupancy defined");
							}
						}
						for (Block b : pl.getBlocks()) {
							if (b.getBuilding().getDateOfConstruction() == true) {
								BigDecimal difference = BigDecimal.ZERO;
								BigDecimal deficientParkingSpace = BigDecimal.ZERO;
								BigDecimal allowedBUA = BigDecimal.ZERO;
								BigDecimal occupancyDeduct = BigDecimal.ZERO;
								if (mixedOccupancy) {
									occupancyDeduct = BigDecimal.valueOf(100);
								} else if (mostRestrictiveOccupancy.getType() != null
										&& mostRestrictiveOccupancy.getType().getCode().equals(R)) {
									occupancyDeduct = BigDecimal.valueOf(200);
								} else if (mostRestrictiveOccupancy.getType() != null
										&& mostRestrictiveOccupancy.getType().getCode().equals(B)) {
									occupancyDeduct = BigDecimal.valueOf(100);
								}
								System.out.println(occupancyDeduct);
								Boolean showParking = true;
								if (BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)
										.compareTo(totalEcs.setScale(0, RoundingMode.DOWN)) > 0 && existingBuilding) {
									difference = BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)
											.subtract(totalEcs.setScale(0, RoundingMode.DOWN));
									System.out.println(ecs);
									System.out.println(totalEcs);
									System.out.println(difference);
									deficientParkingSpace = difference.multiply(BigDecimal.valueOf(20));
									System.out.println(deficientParkingSpace);
									allowedBUA = occupancyDeduct.subtract(deficientParkingSpace);
									System.out.println(allowedBUA);
									System.out.println(proposedBUA);
									if (proposedBUA.compareTo(allowedBUA) < 0) {
										showParking = false;
									} else {
										pl.addError("As per Rule 52(2) ParkingErrorExisting",
												"Due to lack of space for provision of car parking, the maximum allowed built up area that"
														+ " can be constructed is " + allowedBUA
														+ "sqmt. However due to deficiency of " + difference
														+ " parking space , " + deficientParkingSpace
														+ "sqmt has been deducted from " + occupancyDeduct
														+ ". So total proposed area of " + proposedBUA
														+ "sqmt is not allowed to be constructed.");
									}
								}

								if (showParking) {
									if (BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)
											.compareTo(totalEcs.setScale(0, RoundingMode.DOWN)) <= 0) {
										setReportOutputDetails(pl, "Total", "", "", "", "",
												totalEcs.setScale(0, RoundingMode.DOWN) + "",
												(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
												Result.Accepted.getResultVal());
									} else {
										setReportOutputDetails(pl, "Total", "", "", "", "",
												totalEcs.setScale(0, RoundingMode.DOWN) + "",
												(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
												Result.Not_Accepted.getResultVal());
									}
								}
							} else {
								if (BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)
										.compareTo(totalEcs.setScale(0, RoundingMode.DOWN)) <= 0) {
									setReportOutputDetails(pl, "Total", "", "", "", "",
											totalEcs.setScale(0, RoundingMode.DOWN) + "",
											(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
											Result.Accepted.getResultVal());
								} else {
									setReportOutputDetails(pl, "Total", "", "", "", "",
											totalEcs.setScale(0, RoundingMode.DOWN) + "",
											(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
											Result.Not_Accepted.getResultVal());
								}
							}
						}

					} else {
						pl.addError("BUAERROR", "Please Provide correct color code for Parking");
					}

				}
			} else {
				int counter=0;
				Boolean parkingExist=false;
				for (Occupancy occ : pl.getOccupancies()) {
					totalEcs = BigDecimal.ZERO;
					int code = occ.getTypeHelper().getSubtype().getColor();
					int parkingcode = 0;
					
					BigDecimal bua = occ.getFloorArea();
					BigDecimal area = BigDecimal.ZERO;
					
					System.out.println(bua);

					if (bua.compareTo(BigDecimal.ZERO) > 0) {
						for (Map.Entry<BigDecimal, Integer> entry : stiltsParkingList) {
							parkingExist=true;
							if (occ.getTypeHelper().getSubtype().getCode().equalsIgnoreCase(R1a)) {
								STD_STILT_ECS = 10.8;
							} else {
								if (pl.getPlanInfoProperties().get("DRIVEWAY") != null) {
									if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("SINGLY")) {
										STD_STILT_ECS = 21;
									} else if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("DOUBLY")) {
										STD_STILT_ECS = 16;
									}
								} else {
									pl.addError("stiltECS", "Please provide SINGLY/DOUBLY in DRIVEWAY of planinfo");
								}
							}

							parkingcode = entry.getValue();
							area = entry.getKey();
							if (parkingcode == code) {
								counter=counter+1;
								bua = bua.subtract(area);
								if (totalCoverageArea.compareTo(BigDecimal.ZERO) > 0) {
									ecs = getECS(parkingcode, totalCoverageArea, bua);
									stiltECS = area.divide(BigDecimal.valueOf(STD_STILT_ECS), 2, RoundingMode.HALF_UP);
									stiltECS = stiltECS.setScale(0, RoundingMode.DOWN);
									totalEcs = totalEcs.add(stiltECS);
//									if (BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)
//											.compareTo(stiltECS.setScale(0, RoundingMode.DOWN)) <= 0) {
									setReportOutputDetails(pl,
											occ.getTypeHelper().getSubtype().getName()
													+ " / BUA after deduction of concerned parking(" + bua + ")",
											RULE, "Under Stilts", area + SQMTRS,
											(new BigDecimal(STD_STILT_ECS)).doubleValue() + SQMTRS,
											stiltECS.setScale(0, RoundingMode.DOWN) + "", "-",
											Result.Verify.getResultVal());
//									} 
//									else {
//										setReportOutputDetails(pl,
//												occ.getTypeHelper().getSubtype().getName()
//														+ " / BUA after deduction of concerned parking(" + bua + ")",
//												RULE, "Under Stilts", area + SQMTRS,
//												(new BigDecimal(STD_STILT_ECS)).doubleValue() + SQMTRS,
//												stiltECS.setScale(0, RoundingMode.DOWN) + "",
//												(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
//												Result.Not_Accepted.getResultVal());
//									}
								}

							}
						}

						for (Map.Entry<BigDecimal, Integer> entry : garagrParkingList) {
							
							parkingExist=true;
							if (occ.getTypeHelper().getSubtype().getCode().equalsIgnoreCase(R1a)) {
								STD_STILT_ECS = 10.8;
							} else {
								if (pl.getPlanInfoProperties().get("DRIVEWAY") != null) {
									if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("SINGLY")) {
										STD_STILT_ECS = 21;
									} else if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("DOUBLY")) {
										STD_STILT_ECS = 16;
									}
								} else {
									pl.addError("stiltECS", "Please provide SINGLY/DOUBLY in DRIVEWAY of planinfo");
								}
							}

							parkingcode = entry.getValue();
							area = entry.getKey();
							if (parkingcode == code) {
								counter=counter+1;
								bua = bua.subtract(area);
								if (totalCoverageArea.compareTo(BigDecimal.ZERO) > 0) {
									ecs = getECS(parkingcode, totalCoverageArea, bua);
									stiltECS = area.divide(BigDecimal.valueOf(STD_STILT_ECS), 2, RoundingMode.HALF_UP);
									stiltECS = stiltECS.setScale(0, RoundingMode.DOWN);
									totalEcs = totalEcs.add(stiltECS);
//									if (BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)
//											.compareTo(stiltECS.setScale(0, RoundingMode.DOWN)) <= 0) {
									setReportOutputDetails(pl,
											occ.getTypeHelper().getSubtype().getName()
													+ " / BUA after deduction of concerned parking(" + bua + ")",
											RULE, "Under Stilts", area + SQMTRS,
											(new BigDecimal(STD_STILT_ECS)).doubleValue() + SQMTRS,
											stiltECS.setScale(0, RoundingMode.DOWN) + "", "-",
											Result.Verify.getResultVal());
//									} 
//									else {
//										setReportOutputDetails(pl,
//												occ.getTypeHelper().getSubtype().getName()
//														+ " / BUA after deduction of concerned parking(" + bua + ")",
//												RULE, "Under Stilts", area + SQMTRS,
//												(new BigDecimal(STD_STILT_ECS)).doubleValue() + SQMTRS,
//												stiltECS.setScale(0, RoundingMode.DOWN) + "",
//												(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
//												Result.Not_Accepted.getResultVal());
//									}
								}

							}
						}

						for (Map.Entry<BigDecimal, Integer> entry : basementParkingList) {
							parkingExist=true;
							parkingcode = entry.getValue();
							area = entry.getKey();
							if (parkingcode == code) {
								counter=counter+1;
								bua = bua.subtract(area);
								if (totalCoverageArea.compareTo(BigDecimal.ZERO) > 0) {
									ecs = getECS(parkingcode, totalCoverageArea, bua);
									bsmtECS = area.divide(BigDecimal.valueOf(STD_BSMNT_ECS), 2, RoundingMode.HALF_UP);
									bsmtECS = bsmtECS.setScale(0, RoundingMode.DOWN);
									totalEcs = totalEcs.add(bsmtECS);
//									if (BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)
//											.compareTo(bsmtECS.setScale(0, RoundingMode.DOWN)) <= 0) {
									setReportOutputDetails(pl,
											occ.getTypeHelper().getSubtype().getName()
													+ " / BUA after deduction of concerned parking(" + bua + ")",
											RULE, "Basement Parking", area + SQMTRS,
											(new BigDecimal(STD_BSMNT_ECS)).doubleValue() + SQMTRS,
											bsmtECS.setScale(0, RoundingMode.DOWN) + "", "-",
											Result.Verify.getResultVal());
//									}
//									else {
//										setReportOutputDetails(pl,
//												occ.getTypeHelper().getSubtype().getName()
//														+ " / BUA after deduction of concerned parking(" + bua + ")",
//												RULE, "Basement Parking", area + SQMTRS,
//												(new BigDecimal(STD_BSMNT_ECS)).doubleValue() + SQMTRS,
//												bsmtECS.setScale(0, RoundingMode.DOWN) + "",
//												(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
//												Result.Not_Accepted.getResultVal());
//									}
								}

							}
						}
						for (Map.Entry<BigDecimal, Integer> entry : openParkingList) {
							parkingExist=true;
							if (occ.getTypeHelper().getSubtype().getCode().equalsIgnoreCase(R1a)) {
								STD_OPEN_ECS = 10.8;
							} else {
								if (pl.getPlanInfoProperties().get("DRIVEWAY") != null) {
									if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("SINGLY")) {
										STD_OPEN_ECS = 19.2;
									} else if (pl.getPlanInfoProperties().get("DRIVEWAY").equalsIgnoreCase("DOUBLY")) {
										STD_OPEN_ECS = 15;
									}
								} else {
									pl.addError("openECS", "Please provide SINGLY/DOUBLY in DRIVEWAY of planinfo");
								}
							}

							parkingcode = entry.getValue();
							area = entry.getKey();
//							bua=occ.getFloorArea();

							if (parkingcode == code) {
								counter=counter+1;
								if (totalCoverageArea.compareTo(BigDecimal.ZERO) > 0) {
									ecs = getECS(parkingcode, totalCoverageArea, bua);
									openECS = area.divide(BigDecimal.valueOf(STD_OPEN_ECS), 2, RoundingMode.HALF_UP);
									openECS = openECS.setScale(0, RoundingMode.DOWN);
									totalEcs = totalEcs.add(openECS);
//									if (BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)
//											.compareTo(openECS.setScale(0, RoundingMode.DOWN)) <= 0) {
									setReportOutputDetails(pl,
											occ.getTypeHelper().getSubtype().getName() + " / BUA(" + bua + ")", RULE,
											"Open Parking", area + SQMTRS,
											(new BigDecimal(STD_OPEN_ECS)).doubleValue() + SQMTRS,
											openECS.setScale(0, RoundingMode.DOWN) + "", "-",
											Result.Verify.getResultVal());
//									} 
//									else {
//										setReportOutputDetails(pl,
//												occ.getTypeHelper().getSubtype().getName() + " / BUA(" + bua + ")",
//												RULE, "Open Parking", area + SQMTRS,
//												(new BigDecimal(STD_OPEN_ECS)).doubleValue() + SQMTRS,
//												openECS.setScale(0, RoundingMode.DOWN) + "",
//												(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
//												Result.Not_Accepted.getResultVal());
//									}
								}

							}
						}
					}

					
					if (BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)
							.compareTo(totalEcs.setScale(0, RoundingMode.DOWN)) <= 0) {
						setReportOutputDetails(pl, "Total", "", "", "", "",
								totalEcs.setScale(0, RoundingMode.DOWN) + "",
								(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
								Result.Accepted.getResultVal());
					} else {
						setReportOutputDetails(pl, "Total", "", "", "", "",
								totalEcs.setScale(0, RoundingMode.DOWN) + "",
								(BigDecimal.valueOf(ecs).setScale(0, RoundingMode.CEILING)) + "",
								Result.Not_Accepted.getResultVal());
					}

				}
				System.out.println(counter);
				if(parkingExist && counter<1) {
					pl.addError("DEFINEPARKINGERROR", "Please Define Parking with proper color code");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void build(Plan pl, Integer number, BigDecimal heightFromFloorToBottomOfBeam) {
		ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
		scrutinyDetail1.setKey("Common_Parking Height");
		scrutinyDetail1.addColumnHeading(1, RULE_NO);
		scrutinyDetail1.addColumnHeading(2, "Floor No");
		scrutinyDetail1.addColumnHeading(3, "Height Provided");
		scrutinyDetail1.addColumnHeading(4, "Required Height");
		scrutinyDetail1.addColumnHeading(5, STATUS);

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "13.8.5");
		details.put("Floor No", number.toString());
		details.put("Height Provided", heightFromFloorToBottomOfBeam + " m");
		details.put("Required Height", ">=2.4");
		if (heightFromFloorToBottomOfBeam.compareTo(BigDecimal.valueOf(2.4)) >= 0)
			details.put(STATUS, Result.Accepted.getResultVal());
		else
			details.put(STATUS, Result.Not_Accepted.getResultVal());
		scrutinyDetail1.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail1);

	}

	private void setReportOutputDetails(Plan pl, String occName, String ruleNo, String parkingType, String providedArea,
			String ecsSTD, String providedECS, String requiredECS, String status) {
		Map<String, String> details = new HashMap<>();

		details.put(RULE_NO, ruleNo);
		details.put("Occupancy", occName);
		details.put("Type of Parking", parkingType);
		details.put("Polygon Area in plan", providedArea);
		details.put("Area Standard", ecsSTD);
		details.put("Nos Provided in plan", providedECS);
		details.put("Required Nos", requiredECS);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private double roundUp(double value, double multiplier) {
		return Math.ceil(value / multiplier) * multiplier;
	}

	private double roundDown(double value, double multiplier) {
		return Math.floor(value / multiplier) * multiplier;
	}

	private double getECSHeavy(int code, BigDecimal totalCoverageArea, BigDecimal totalBuiltupArea) {
		double requiredHeavyEcs = 0;
		if (code == 214) {
			requiredHeavyEcs = totalBuiltupArea.divide(BigDecimal.valueOf(1000)).multiply(BigDecimal.ONE).doubleValue();
		}
		return requiredHeavyEcs;
	}

	private double getECS(int code, BigDecimal totalCoverageArea, BigDecimal totalBuiltupArea) {
		double requiredEcs = 0;
		if (code == 30 || code == 31) {
			if (totalBuiltupArea.compareTo(BigDecimal.valueOf(70)) > 0) {
				requiredEcs = totalBuiltupArea.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.ONE).doubleValue();
			} else {
				requiredEcs = 0;
			}
		}
		if (code == 12) {
			requiredEcs = totalBuiltupArea.divide(BigDecimal.valueOf(400)).multiply(BigDecimal.ONE).doubleValue();
		}
		if (code == 241) {
			requiredEcs = totalBuiltupArea.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.ONE).doubleValue();
		}
		if (code == 145) {
			requiredEcs = totalBuiltupArea.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.ONE).doubleValue();
		}
		if (code == 144) {
			requiredEcs = totalBuiltupArea.divide(BigDecimal.valueOf(200)).multiply(BigDecimal.ONE).doubleValue();
		}
		if (code == 143) {
			requiredEcs = totalBuiltupArea.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.ONE).doubleValue();
		}
		if (code == 5) {
			if (totalBuiltupArea.compareTo(BigDecimal.valueOf(2000)) > 0) {
				BigDecimal buaalter = totalBuiltupArea.subtract(BigDecimal.valueOf(2000));
				requiredEcs = buaalter.divide(BigDecimal.valueOf(100), 0).multiply(BigDecimal.ONE).doubleValue();
			} else {
				requiredEcs = 0;
			}
		}
		if (code == 214) {
			requiredEcs = totalBuiltupArea.divide(BigDecimal.valueOf(500)).multiply(BigDecimal.ONE).doubleValue();
		}
		return requiredEcs;
	}

}
