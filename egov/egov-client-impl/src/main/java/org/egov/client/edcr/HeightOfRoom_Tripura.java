package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.Balcony;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Lift;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.Stair;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.HeightOfRoom;
import org.egov.edcr.feature.Verandah;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;
import org.egov.edcr.service.ProcessHelper;

import com.fasterxml.jackson.databind.BeanProperty.Bogus;

@Service
public class HeightOfRoom_Tripura extends HeightOfRoom {

	private static final Logger LOG = Logger.getLogger(HeightOfRoom_Tripura.class);
	private static final String RULE_NO_ROOMHEIGHT = "13";
	private static final String RULE_NO_RESIDENTIAL = "13.6.1";
	private static final String RULE_NO_25_I = "25(i)";
	private static final String RULE_NO_25_II = "25(ii)";
	private static final String ROOMHEIGHT_DESC = "Room Height";
	private static final String RULE_DESC_MINIMUM_ROOMHEIGHT_DESC = "Minimum Room Height";
	private static final String RULE_DESC_MAXIMUM_ROOMHEIGHT_DESC = "Minimum Room Height";
	private static final String FLOOR = "Floor";
	private static final String ROOM = "Room";
	private static final String ROOM_HEIGHT_NOTDEFINED = "Room height is not defined in layer ";
	private static final String LAYER_ROOM_HEIGHT = "BLK_%s_FLR_%s_%s";
	private static final BigDecimal MINIMUM_HEIGHT_3 = new BigDecimal(3);
	private static final Integer CONVENIENTSHOP_COLORCODE = 41;

	@Override
	public Plan validate(Plan pl) {
		try {
			Map<String, Integer> heightOfRoomFeaturesColor = pl.getSubFeatureColorCodesMaster().get("HeightOfRoom");
			HashMap<String, String> errors = new HashMap<>();
			if (pl != null && pl.getBlocks() != null) {
				OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
						? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
						: null;
				if (mostRestrictiveOccupancy != null) {
					for (Block block : pl.getBlocks()) {
						if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
							scrutinyDetail = new ScrutinyDetail();
							scrutinyDetail.addColumnHeading(1, RULE_NO);
							scrutinyDetail.addColumnHeading(2, DESCRIPTION);
							scrutinyDetail.addColumnHeading(3, FLOOR);
							scrutinyDetail.addColumnHeading(4, REQUIRED);
							scrutinyDetail.addColumnHeading(5, PROVIDED);
							scrutinyDetail.addColumnHeading(6, STATUS);
							scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Room");

							ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
							scrutinyDetail1.addColumnHeading(1, RULE_NO);
							scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
							scrutinyDetail1.addColumnHeading(3, FLOOR);
							scrutinyDetail1.addColumnHeading(4, "Room No");
							scrutinyDetail1.addColumnHeading(5, "Room Area");
							scrutinyDetail1.addColumnHeading(6, REQUIRED);
							scrutinyDetail1.addColumnHeading(7, PROVIDED);
							scrutinyDetail1.addColumnHeading(8, STATUS);
							scrutinyDetail1.setKey("Block_" + block.getNumber() + "_" + "Room Ventilations");

							for (Floor floor : block.getBuilding().getFloors()) {
								List<BigDecimal> roomAreas = new ArrayList<>();
								List<BigDecimal> roomWidths = new ArrayList<>();
//								List<Pair<BigDecimal, String>> roomAreasColorCode = new ArrayList<>();
								List<Triple<BigDecimal, String, String>> roomAreasColorCode = new ArrayList<>();
//								List<Pair<BigDecimal, String>> roomWidthsColorcode = new ArrayList<>();
								List<Triple<BigDecimal, String, String>> roomWidthsColorcode = new ArrayList<>();
								BigDecimal minimumHeight = BigDecimal.ZERO;
								BigDecimal totalArea = BigDecimal.ZERO;
								BigDecimal minWidth = BigDecimal.ZERO;
								String subRule = null;
								String subRuleDesc = null;
								String color = DxfFileConstants.COLOR_RESIDENTIAL_ROOM;
								if (floor.getRegularRooms() != null && floor.getRegularRooms().size() > 0) {
									List<BigDecimal> residentialRoomHeights = new ArrayList<>();
//									List<RoomHeight> heights = new ArrayList<>();
									List<Pair<List<RoomHeight>, String>> heights = new ArrayList<>();
									List<Measurement> rooms = new ArrayList<>();
									List<Measurement> acrooms = new ArrayList<>();
									String roomNo = "";
									for (Room room : floor.getRegularRooms()) {
										BigDecimal roomAreaVents = BigDecimal.ZERO;
										BigDecimal ventilationArea = BigDecimal.ZERO;
										int noOfRooms = 0;
										roomNo = room.getNumber();
										if (room.getHeights() != null && !room.getHeights().isEmpty()) {
//											heights.addAll(room.getHeights());
											heights.add(Pair.of(room.getHeights(), roomNo));
										} else {
											errors.put(
													"Floor No :" + floor.getNumber() + ". Room Height For Room No "
															+ room.getNumber(),
													"Floor No :" + floor.getNumber() + " Room Height For Room No "
															+ room.getNumber() + " is not defined");
											pl.addErrors(errors);
										}
										if (room.getRooms() != null && !room.getRooms().isEmpty()) {
											rooms.addAll(room.getRooms());
											for (Measurement r : room.getRooms()) {
												noOfRooms = noOfRooms + 1;
											}
											if (noOfRooms > 1) {
												errors.put("NoOfRoomsError " + room.getNumber(),
														"Floor " + floor.getNumber()
																+ ". More than one rooms assigned for regular room "
																+ room.getNumber());
												pl.addErrors(errors);
											}
//											if(errors.isEmpty()) {
											if (room.getLightAndVentilation() != null
													&& room.getLightAndVentilation().getMeasurements() != null
													&& !room.getLightAndVentilation().getMeasurements().isEmpty()
													&& room.getHeights() != null && !room.getHeights().isEmpty()
													&& room.getRooms() != null && !room.getRooms().isEmpty()) {
												for (Measurement r : room.getRooms()) {
													roomAreaVents = r.getArea();
													if (roomAreaVents.compareTo(BigDecimal.ZERO) <= 0) {
														errors.put("RoomAreaVent" + room.getNumber(),
																"Room Area for Room No " + room.getNumber()
																		+ "is 0 or not defined");
														pl.addErrors(errors);
													}
												}
												ventilationArea = room.getLightAndVentilation().getMeasurements()
														.stream().map(Measurement::getArea)
														.reduce(BigDecimal.ZERO, BigDecimal::add);
												;

												if (errors.isEmpty()) {
													buildVentilation(pl, block, floor.getNumber(), room.getNumber(),
															ventilationArea, roomAreaVents, scrutinyDetail1);
												}
											} else {
												if(room.getHeights() != null && !room.getHeights().isEmpty()
													&& room.getRooms() != null && !room.getRooms().isEmpty()) {
													for (Measurement r : room.getRooms()) {
														if(r.getColorCode()==6 || r.getColorCode()==5) {
															
														}else {
															pl.addError("RoomVentError" + room.getNumber(),
																	"Ventilations for room no " + room.getNumber()
																			+ " is not defined");
														}
															
													}
												}
												
											}
//											}
										} else {
											errors.put(
													"Floor No:" + floor.getNumber() + "Rooms For Room No "
															+ room.getNumber(),
													"Floor No :" + floor.getNumber() + ". Rooms For Room No  "
															+ room.getNumber() + " is not defined");
											pl.addErrors(errors);
										}
									}

//									for (Room room : floor.getAcRooms()) {
//										int noOfRooms = 0;
//										roomNo = room.getNumber();
//										if (room.getHeights() != null && !room.getHeights().isEmpty()) {
////											heights.addAll(room.getHeights());
//											heights.add(Pair.of(room.getHeights(), roomNo));
//										} else {
//											errors.put(
//													"Floor No :" + floor.getNumber() + ". AC Room Height For Room No "
//															+ room.getNumber(),
//													"Floor No :" + floor.getNumber() + " AC Room Height For Room No "
//															+ room.getNumber() + " is not defined");
//											pl.addErrors(errors);
//										}
//										if (room.getRooms() != null && !room.getRooms().isEmpty()) {
//											acrooms.addAll(room.getRooms());
//											for (Measurement r : room.getRooms()) {
//												noOfRooms = noOfRooms + 1;
//											}
//											if (noOfRooms > 1) {
//												errors.put("NoOfACRoomsError " + room.getNumber(),
//														"Floor " + floor.getNumber()
//																+ ". More than one rooms assigned for ac room "
//																+ room.getNumber());
//												pl.addErrors(errors);
//											}
//
//										} else {
//											System.out.println("Rooms For AC Room No " + room.getNumber());
//											errors.put(
//													"Floor No:" + floor.getNumber() + "Rooms For AC Room No "
//															+ room.getNumber(),
//													"Floor No :" + floor.getNumber() + ". Rooms For AC Room No  "
//															+ room.getNumber() + " is not defined");
//											pl.addErrors(errors);
//										}
//									}
									if (errors.isEmpty()) {
										int heightCounter = 0;
//										for (RoomHeight roomHeight : heights) {
										for (Pair<List<RoomHeight>, String> roomHeight : heights) {
											heightCounter = heightCounter + 1;
											subRule = "56(2)";
											subRuleDesc = "56(2)";
											boolean valid = false;
											boolean isTypicalRepititiveFloor = false;
											Map<String, Object> typicalFloorValues = ProcessHelper
													.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);
											residentialRoomHeights.add(roomHeight.getKey().get(0).getHeight());
											BigDecimal minHeight = roomHeight.getKey().get(0).getHeight();
											minimumHeight = MINIMUM_HEIGHT_2_75;
											String heightcolor = "";
											if (roomHeight.getKey().get(0).getColorCode() == 1) {
												heightcolor = "BEDROOM";
												buildResult(pl, floor, minimumHeight, subRule,
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											} else if (roomHeight.getKey().get(0).getColorCode() == 3) {
												heightcolor = "DRAWING ROOM/ LIVING ROOM ";
												buildResult(pl, floor, minimumHeight, subRule,
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											} else if (roomHeight.getKey().get(0).getColorCode() == 6) {
												minimumHeight = BigDecimal.valueOf(2.4);
												heightcolor = "AC ROOM ";
												buildResult(pl, floor, minimumHeight, subRule,
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											} else if (roomHeight.getKey().get(0).getColorCode() == 7) {
												heightcolor = "STORE ROOM ";
												minimumHeight = BigDecimal.valueOf(2.1);
												buildResult(pl, floor, minimumHeight, "64",
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											}else if (roomHeight.getKey().get(0).getColorCode() == 41) {
												heightcolor = "MERCANTILE SHOP ";
												minimumHeight = BigDecimal.valueOf(2.3);
												buildResult(pl, floor, minimumHeight, "64",
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											} else if (roomHeight.getKey().get(0).getColorCode() == 5) {
												heightcolor = "PUJA ROOM ";
												minimumHeight = BigDecimal.valueOf(2.4);
												buildResult(pl, floor, minimumHeight, "64",
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											}
											else if (roomHeight.getKey().get(0).getColorCode() == 8) {
												heightcolor = "OTHER UTILITIES ROOM ";
												buildResult(pl, floor, minimumHeight, subRule,
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											} else if (roomHeight.getKey().get(0).getColorCode() == 10) {
												heightcolor = "DINING";
												buildResult(pl, floor, minimumHeight, subRule,
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											} else if (roomHeight.getKey().get(0).getColorCode() == 2) {
												heightcolor = "Habitable ROOMS";
												buildResult(pl, floor, minimumHeight, subRule,
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											} else if (roomHeight.getKey().get(0).getColorCode() == 4) { //garage cars
												heightcolor = "GARAGE CARS";
												minimumHeight = BigDecimal.valueOf(2.1);
												buildResult(pl, floor, minimumHeight, subRule,
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											} else if (roomHeight.getKey().get(0).getColorCode() == 9) { // garage trucks
												heightcolor = "GARAGE TRUCKS";
												minimumHeight = BigDecimal.valueOf(2.3);
												buildResult(pl, floor, minimumHeight, subRule,
														heightcolor + " " + roomHeight.getValue() + " Height",
														minHeight, valid, typicalFloorValues);
											} else {
												pl.addError("RoomColorCodeError" +block.getNumber()+ heightCounter, "Block "+block.getNumber()+ "Floor No :"
														+ floor.getNumber() + ". Room Height Error:ColorCode"
														+ roomHeight.getKey().get(0).getColorCode() + " Not Defined ");
											}

										}
										int flag = 0;
										int roomColorCounter = 0;
										for (Measurement acroom : acrooms) {
//											if (room.getColorCode() == 1) {
											acroom.setName("AC ROOM");
											roomAreas.add(acroom.getArea());
											roomWidths.add(acroom.getWidth());
											roomAreasColorCode.add(Triple.of(acroom.getArea(), acroom.getName(),
													floor.getAcRooms().get(flag).getNumber()));
											roomWidthsColorcode.add(Triple.of(acroom.getWidth(), acroom.getName(),
													floor.getAcRooms().get(flag).getNumber()));
//											} 
											flag = flag + 1;
										}
										flag=0;
										for (Measurement room : rooms) {
											
											roomColorCounter = roomColorCounter + 1;

											if (room.getArea() == null)
												pl.addError(
														"RoomAreaNull" + floor.getRegularRooms().get(flag).getNumber(),
														"Room Area Not Defined for room no "
																+ floor.getRegularRooms().get(flag).getNumber());
											if (room.getWidth() == null)
												pl.addError(
														"RoomWidthNull" + floor.getRegularRooms().get(flag).getNumber(),
														"Room Width Not Defined for room no "
																+ floor.getRegularRooms().get(flag).getNumber());
											if (room.getColorCode() == 1) {
												room.setName("BEDROOM");
												roomAreas.add(room.getArea());
												roomWidths.add(room.getWidth());
												roomAreasColorCode.add(Triple.of(room.getArea(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
												roomWidthsColorcode.add(Triple.of(room.getWidth(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
											} else if (room.getColorCode() == 3) {
												room.setName("DRAWING ROOM/ LIVING ROOM ");
												roomAreas.add(room.getArea());
												roomWidths.add(room.getWidth());
												roomAreasColorCode.add(Triple.of(room.getArea(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
												roomWidthsColorcode.add(Triple.of(room.getWidth(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
											}else if (room.getColorCode() == 5) {
												
											}else if (room.getColorCode() == 41) {
												
											} else if (room.getColorCode() == 7) {

												room.setName("STORE ROOM");
												roomAreas.add(room.getArea());
												roomWidths.add(room.getWidth());
												boolean valid = false;
												boolean isTypicalRepititiveFloor = false;
//												roomAreasColorCode.add(Pair.of(room.getArea(), room.getName()));
//												roomWidthsColorcode.add(Pair.of(room.getWidth(), room.getName()));
												Map<String, Object> typicalFloorValues = ProcessHelper
														.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);
												minimumHeight = BigDecimal.valueOf(1.5);
												totalArea = room.getArea();
												buildResultArea(pl, floor, minimumHeight, "64",
														"Store Room Area "
																+ floor.getRegularRooms().get(flag).getNumber(),
														totalArea, valid, typicalFloorValues);
											} else if (room.getColorCode() == 4) {

												room.setName("GARAGE CARS");
												roomAreas.add(room.getArea());
												roomWidths.add(room.getWidth());
												boolean valid = false;
												boolean isTypicalRepititiveFloor = false;
//												roomAreasColorCode.add(Pair.of(room.getArea(), room.getName()));
//												roomWidthsColorcode.add(Pair.of(room.getWidth(), room.getName()));
												Map<String, Object> typicalFloorValues = ProcessHelper
														.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);
												minimumHeight = BigDecimal.valueOf(12.5);
												totalArea = room.getArea();
												buildResultArea(pl, floor, minimumHeight, "65",
														"Garage Car Area "
																+ floor.getRegularRooms().get(flag).getNumber(),
														totalArea, valid, typicalFloorValues);
											}else if (room.getColorCode() == 9) {

												room.setName("GARAGE TRUCKS");
												roomAreas.add(room.getArea());
												roomWidths.add(room.getWidth());
												boolean valid = false;
												boolean isTypicalRepititiveFloor = false;
//												roomAreasColorCode.add(Pair.of(room.getArea(), room.getName()));
//												roomWidthsColorcode.add(Pair.of(room.getWidth(), room.getName()));
												Map<String, Object> typicalFloorValues = ProcessHelper
														.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);
												minimumHeight = BigDecimal.valueOf(37.5);
												totalArea = room.getArea();
												buildResultArea(pl, floor, minimumHeight, "65",
														"Garage Truck Area "
																+ floor.getRegularRooms().get(flag).getNumber(),
														totalArea, valid, typicalFloorValues);
											}else if (room.getColorCode() == 8) {
												room.setName("OTHER UTILITIES ROOM ");
												roomAreas.add(room.getArea());
												roomWidths.add(room.getWidth());
												roomAreasColorCode.add(Triple.of(room.getArea(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
												roomWidthsColorcode.add(Triple.of(room.getWidth(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
											} else if (room.getColorCode() == 10) {
												room.setName("DINING");
												roomAreas.add(room.getArea());
												roomWidths.add(room.getWidth());
												roomAreasColorCode.add(Triple.of(room.getArea(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
												roomWidthsColorcode.add(Triple.of(room.getWidth(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
											}else if (room.getColorCode() == 6) {
												room.setName("AC ROOM");
												roomAreas.add(room.getArea());
												roomWidths.add(room.getWidth());
												roomAreasColorCode.add(Triple.of(room.getArea(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
												roomWidthsColorcode.add(Triple.of(room.getWidth(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
											} else if (room.getColorCode() == 2) {
												room.setName("Habitable ROOMS");
												roomAreas.add(room.getArea());
												roomWidths.add(room.getWidth());
												roomAreasColorCode.add(Triple.of(room.getArea(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
												roomWidthsColorcode.add(Triple.of(room.getWidth(), room.getName(),
														floor.getRegularRooms().get(flag).getNumber()));
											} else {
												pl.addError("RoomColorCodeError" + roomColorCounter,
														"Floor No :" + floor.getNumber()
																+ ". Please provide the correct colorcode room no "
																+ floor.getRegularRooms().get(flag).getNumber());
											}
											flag = flag + 1;
										}

										if (flag == 0) {
											pl.addError("NoRoomPresent ",
													"No Rooms Present in floor no " + floor.getNumber());
										}
									}
								}

								BigDecimal maxArea = BigDecimal.valueOf(0);
								String colorCodeOfMaxArea = "";
								String colorCodeOfMaxwidth = "";
								BigDecimal maxWidth = BigDecimal.valueOf(0);
								BigDecimal minRoomWidth = BigDecimal.ZERO;
								if (!roomAreas.isEmpty() && !roomAreasColorCode.isEmpty()) {
									subRule = "56(1)";
									subRuleDesc = "56(1)";
									boolean valid = false;
									boolean isTypicalRepititiveFloor = false;
									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
											floor, isTypicalRepititiveFloor);
									if (roomAreas.size() == 1) {
										for (Triple<BigDecimal, String, String> entry : roomAreasColorCode) {
											totalArea = roomAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
											minRoomWidth = roomWidths.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
											minimumHeight = MINIMUM_AREA_9_5;
											minWidth = MINIMUM_WIDTH_2_4;
											buildResultArea(pl, floor, minimumHeight, subRule,
													entry.getMiddle() + entry.getRight() + " Area", totalArea, valid,
													typicalFloorValues);
											buildResult(pl, floor, minWidth, subRule, entry.getMiddle() + " Width",
													minRoomWidth, valid, typicalFloorValues);
										}

									} else {
//										for (Map.Entry<BigDecimal, String> entry : roomAreasColorCode) {
//											if (entry.getKey().compareTo(maxArea) > 0) {
//												maxArea = entry.getKey(); // Update max area
//												colorCodeOfMaxArea = entry.getValue(); // Update the corresponding color
//											}
//										}
										for (Triple<BigDecimal, String, String> entry : roomAreasColorCode) {
											if (entry.getLeft().compareTo(maxArea) > 0) {
												maxArea = entry.getLeft(); // Update max area
												colorCodeOfMaxArea = entry.getMiddle() + " " + entry.getRight(); 
											}
										}

										for (Triple<BigDecimal, String, String> entry : roomWidthsColorcode) {
											if (entry.getLeft().compareTo(maxWidth) > 0) {
												maxWidth = entry.getLeft(); // Update max area
												colorCodeOfMaxwidth = entry.getMiddle() + " " + entry.getRight(); 
											}
										}
//										for (Map.Entry<BigDecimal, String> entry : roomWidthsColorcode) {
//											if (entry.getKey().compareTo(maxWidth) > 0) {
//												maxWidth = entry.getKey(); // Update max area
//												colorCodeOfMaxwidth =entry.getValue(); // Update the corresponding
//																						// color
//											}
//										}
										// Step 2: If the max area is greater than 9.5, set it as totalArea and update
										// minimumHeight
										if (maxArea.compareTo(BigDecimal.valueOf(9.5)) >= 0) {
											totalArea = maxArea;
											minimumHeight = MINIMUM_AREA_9_5;
											buildResultArea(pl, floor, minimumHeight, subRule,
													colorCodeOfMaxArea + " Area", totalArea, valid, typicalFloorValues);
										}
										if (maxWidth.compareTo(BigDecimal.valueOf(2.4)) >= 0) {
											minRoomWidth = maxWidth;
											minWidth = BigDecimal.valueOf(2.4);
											buildResult(pl, floor, minWidth, subRule, colorCodeOfMaxwidth + " Width",
													minRoomWidth, valid, typicalFloorValues);
										}

										// Step 3: Remove the first occurrence of maxArea
										Iterator<BigDecimal> iterator = roomAreas.iterator();
										boolean removed = false;
										for (int i = 0; i < roomWidthsColorcode.size(); i++) {
											if (roomWidthsColorcode.get(i).getLeft().compareTo(maxWidth) == 0
													&& !removed) {
												roomWidthsColorcode.remove(i); // Remove the first occurrence
												removed = true; // Ensure only the first occurrence is removed
												break; // Exit the loop once the item is removed
											}
										}

										removed = false;
										for (int i = 0; i < roomAreasColorCode.size(); i++) {
											if (roomAreasColorCode.get(i).getLeft().compareTo(maxArea) == 0
													&& !removed) {
												roomAreasColorCode.remove(i); // Remove the first occurrence
												removed = true; // Ensure only the first occurrence is removed
												break; // Exit the loop once the item is removed
											}
										}
										for (Triple<BigDecimal, String, String> entry : roomAreasColorCode) {
											totalArea = entry.getLeft(); // Store total area
											minimumHeight = BigDecimal.valueOf(7.5);
											buildResultArea(pl, floor, minimumHeight, subRule,
													entry.getMiddle() + " " + entry.getRight() + " Area", totalArea,
													valid, typicalFloorValues);
										}
										for (Triple<BigDecimal, String, String> entry : roomWidthsColorcode) {
											minRoomWidth = entry.getLeft(); // Store total area
											minWidth = BigDecimal.valueOf(2.1);
											buildResult(pl, floor, minWidth, subRule,
													entry.getMiddle() + " " + entry.getRight() + " Width", minRoomWidth,
													valid, typicalFloorValues);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;
	}

	private void buildResultArea(Plan pl, Floor floor, BigDecimal expected, String subRule, String subRuleDesc,
			BigDecimal actual, boolean valid, Map<String, Object> typicalFloorValues) {
		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")
				&& expected.compareTo(BigDecimal.valueOf(0)) > 0 && subRule != null && subRuleDesc != null) {
			if (actual.compareTo(expected) >= 0) {
				valid = true;
			}
			String value = typicalFloorValues.get("typicalFloors") != null
					? (String) typicalFloorValues.get("typicalFloors")
					: " floor " + floor.getNumber();
			if (valid) {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected + " m²",
						actual.setScale(2, RoundingMode.HALF_UP) + " m²", Result.Accepted.getResultVal());
			} else {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected + " m²",
						actual.setScale(2, RoundingMode.HALF_UP) + " m²", Result.Not_Accepted.getResultVal());
			}
		}
	}

	private void buildResult(Plan pl, Floor floor, BigDecimal expected, String subRule, String subRuleDesc,
			BigDecimal actual, boolean valid, Map<String, Object> typicalFloorValues) {
		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")
				&& expected.compareTo(BigDecimal.valueOf(0)) > 0 && subRule != null && subRuleDesc != null) {
			if (actual.compareTo(expected) >= 0) {
				valid = true;
			}
			String value = typicalFloorValues.get("typicalFloors") != null
					? (String) typicalFloorValues.get("typicalFloors")
					: " floor " + floor.getNumber();
			if (valid) {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected + " m",
						actual.setScale(2, RoundingMode.HALF_UP) + " m", Result.Accepted.getResultVal());
			} else {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected + " m",
						actual.setScale(2, RoundingMode.HALF_UP) + " m", Result.Not_Accepted.getResultVal());
			}
		}
	}

	public void checkfloorarea(Plan pl) {
		Map<String, Integer> heightOfRoomFeaturesColor = pl.getSubFeatureColorCodesMaster().get("HeightOfRoom");
		for (Block blk : pl.getBlocks()) {
			for (Floor floor : blk.getBuilding().getFloors()) {
				BigDecimal totalLiftArea = BigDecimal.ZERO;
				BigDecimal totalStairArea = BigDecimal.ZERO;
				BigDecimal totalmiscellaneousArea = BigDecimal.ZERO;
				BigDecimal totalpantryArea = BigDecimal.ZERO;
				BigDecimal totalBalconyArea = BigDecimal.ZERO;
				BigDecimal totalVerandahArea = BigDecimal.ZERO;
				BigDecimal totalAreaAfterAdding = BigDecimal.ZERO;
				BigDecimal totalFloorArea = BigDecimal.ZERO;
				BigDecimal totalBathroomArea = BigDecimal.ZERO;
				BigDecimal totalBathroomWCArea = BigDecimal.ZERO;
				BigDecimal totalKitchenArea = BigDecimal.ZERO;
				BigDecimal totalroomArea = BigDecimal.ZERO;
				BigDecimal totalWCArea = BigDecimal.ZERO;
				BigDecimal basementParkingArea = BigDecimal.ZERO;
				BigDecimal stiltParkingArea = BigDecimal.ZERO;
				BigDecimal openParkingArea = BigDecimal.ZERO;
				BigDecimal twoWheelerParkingArea = BigDecimal.ZERO;
				BigDecimal totalProvidedCarParkArea = BigDecimal.ZERO;

				if (floor.getNumber() < 0)
					basementParkingArea = basementParkingArea.add(floor.getParking().getBasementCars().stream()
							.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));
				if (floor.getNumber() == 0) {
					stiltParkingArea = stiltParkingArea.add(floor.getParking().getStilts().stream()
							.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));
					twoWheelerParkingArea = pl.getParkingDetails().getTwoWheelers().stream().map(Measurement::getArea)
							.reduce(BigDecimal.ZERO, BigDecimal::add);

				}
				
				totalProvidedCarParkArea = stiltParkingArea.add(basementParkingArea).add(twoWheelerParkingArea);
				for (Occupancy occupancy : floor.getOccupancies()) {
					if(!occupancy.getIsMezzanine())
						totalFloorArea = totalFloorArea.add(occupancy.getFloorArea().subtract(occupancy.getExistingBuiltUpArea()));
				}
				try {
					if (floor.getLifts() != null) {
						for (Lift l : floor.getLifts()) {
							if (l.getArea() != null) {
								totalLiftArea = totalLiftArea.add(l.getArea());

							}
						}

					}

					if (floor.getGeneralStairs() != null) {
						totalStairArea = BigDecimal.ZERO;
						for (Stair s : floor.getGeneralStairs()) {
							if (s.getArea() != null) {
								totalStairArea = totalStairArea.add(s.getStairMeasurements().stream()
										.map(Measurement::getArea).reduce(BigDecimal::add).get());

							}
						}

					}

					if (floor.getBalconies() != null) {
						totalBalconyArea = BigDecimal.ZERO;
						for (Balcony b : floor.getBalconies()) {
							if (b.getArea() == null || b.getArea().compareTo(BigDecimal.ZERO) <= 0
									&& (b.getMeasurements() != null && !b.getMeasurements().isEmpty())) {
								totalBalconyArea = totalBalconyArea.add(b.getMeasurements().stream()
										.map(Measurement::getArea).reduce(BigDecimal::add).get());

							}
						}
					}

					if (floor.getVerandah() != null) {
						totalVerandahArea = BigDecimal.ZERO;
//						for (Verandah b : floor.getVerandah()) {
						if (floor.getVerandah().getMeasurements() != null
								&& !floor.getVerandah().getMeasurements().isEmpty()) {
							totalVerandahArea = totalVerandahArea.add(floor.getVerandah().getMeasurements().stream()
									.map(Measurement::getArea).reduce(BigDecimal::add).get());

						}
//						}
					}
					
					if (floor.getMiscellaneous() != null) {
						totalmiscellaneousArea = BigDecimal.ZERO;
//						for (Verandah b : floor.getVerandah()) {
						if (floor.getMiscellaneous().getMeasurements() != null
								&& !floor.getMiscellaneous().getMeasurements().isEmpty()) {
							totalmiscellaneousArea = totalmiscellaneousArea
									.add(floor.getMiscellaneous().getMeasurements().stream().map(Measurement::getArea)
											.reduce(BigDecimal::add).get());

						}
//						}
					}
					
					if (floor.getMiscellaneous() != null) {
						totalpantryArea = BigDecimal.ZERO;
//						for (Verandah b : floor.getVerandah()) {
						if (floor.getPantry().getMeasurements() != null
								&& !floor.getPantry().getMeasurements().isEmpty()) {
							totalpantryArea = totalpantryArea
									.add(floor.getPantry().getMeasurements().stream().map(Measurement::getArea)
											.reduce(BigDecimal::add).get());

						}
//						}
					}

					if (floor.getBathRoom() != null && floor.getBathRoom().getHeights() != null
							&& !floor.getBathRoom().getHeights().isEmpty() && floor.getBathRoom().getRooms() != null
							&& !floor.getBathRoom().getRooms().isEmpty()) {
						totalBathroomArea = BigDecimal.ZERO;

						if (floor.getBathRoom().getRooms() != null && !floor.getBathRoom().getRooms().isEmpty()) {

							for (Measurement m : floor.getBathRoom().getRooms()) {
								totalBathroomArea = totalBathroomArea.add(m.getArea());
							}
						}

					}
					if (floor.getBathRoomWaterClosets() != null && floor.getBathRoomWaterClosets().getHeights() != null
							&& !floor.getBathRoomWaterClosets().getHeights().isEmpty()
							&& floor.getBathRoomWaterClosets().getRooms() != null
							&& !floor.getBathRoomWaterClosets().getRooms().isEmpty()) {
						if (floor.getBathRoomWaterClosets().getRooms() != null
								&& !floor.getBathRoomWaterClosets().getRooms().isEmpty()) {
							for (Measurement m : floor.getBathRoomWaterClosets().getRooms()) {
								totalBathroomWCArea = totalBathroomWCArea.add(m.getArea());
							}
						}

					}

					if (floor.getWaterClosets().getRooms() != null && !floor.getWaterClosets().getRooms().isEmpty()) {
						for (Measurement m : floor.getWaterClosets().getRooms()) {
							totalWCArea = totalWCArea.add(m.getArea());
						}
					}

					List<BigDecimal> kitchenAreas = new ArrayList<>();
					List<BigDecimal> kitchenStoreAreas = new ArrayList<>();
					List<BigDecimal> kitchenDiningAreas = new ArrayList<>();
					String kitchenRoomColor = "";
					String kitchenStoreRoomColor = "";
					String kitchenDiningRoomColor = "";
					String comercialKitchenRoomColor = "";
					String comercialKitchenDiningRoomColor = "";
					BigDecimal kitchenArea = BigDecimal.ZERO;
					BigDecimal kitchenDiningArea = BigDecimal.ZERO;
					BigDecimal kitchenStoreArea = BigDecimal.ZERO;
					kitchenRoomColor = DxfFileConstants.RESIDENTIAL_KITCHEN_ROOM_COLOR;
					kitchenStoreRoomColor = DxfFileConstants.RESIDENTIAL_KITCHEN_STORE_ROOM_COLOR;
					kitchenDiningRoomColor = DxfFileConstants.RESIDENTIAL_KITCHEN_DINING_ROOM_COLOR;
					comercialKitchenRoomColor = DxfFileConstants.COMMERCIAL_KITCHEN_ROOM_COLOR;
					comercialKitchenDiningRoomColor = DxfFileConstants.COMMERCIAL_KITCHEN_DINING_ROOM_COLOR;

					if (floor.getKitchen() != null) {
						kitchenArea = BigDecimal.ZERO;
						kitchenDiningArea = BigDecimal.ZERO;
						kitchenStoreArea = BigDecimal.ZERO;
						List<Measurement> kitchenRooms = floor.getKitchen().getRooms();
						for (Measurement kitchen : kitchenRooms) {
							if (heightOfRoomFeaturesColor.get(kitchenRoomColor) == kitchen.getColorCode()) {
								kitchenAreas.add(kitchen.getArea());
							} else if (heightOfRoomFeaturesColor.get(kitchenStoreRoomColor) == kitchen.getColorCode()) {
								kitchenStoreAreas.add(kitchen.getArea());
							} else if (heightOfRoomFeaturesColor.get(kitchenDiningRoomColor) == kitchen
									.getColorCode()) {
								kitchenDiningAreas.add(kitchen.getArea());
							} else if (heightOfRoomFeaturesColor.get(comercialKitchenRoomColor) == kitchen
									.getColorCode()) {
								kitchenDiningAreas.add(kitchen.getArea());
							} else if (heightOfRoomFeaturesColor.get(comercialKitchenDiningRoomColor) == kitchen
									.getColorCode()) {
								kitchenDiningAreas.add(kitchen.getArea());
							}
							if (!kitchenAreas.isEmpty()) {
								kitchenArea = kitchenAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

							}

							if (!kitchenDiningAreas.isEmpty()) {
								kitchenDiningArea = kitchenDiningAreas.stream().reduce(BigDecimal.ZERO,
										BigDecimal::add);
							}

							if (!kitchenStoreAreas.isEmpty()) {
								kitchenStoreArea = kitchenStoreAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
							}
						}

					}

					if (floor.getRegularRooms() != null && floor.getRegularRooms().size() > 0) {

						List<RoomHeight> heights = new ArrayList<>();
						List<Measurement> rooms = new ArrayList<>();
						for (Room room : floor.getRegularRooms()) {
							if (room.getHeights() != null)
								heights.addAll(room.getHeights());
							if (room.getRooms() != null)
								rooms.addAll(room.getRooms());
						}
						for (Measurement room : rooms) {
							totalroomArea = totalroomArea.add(room.getArea());

						}
					}
					if (floor.getAcRooms() != null && floor.getAcRooms().size() > 0) {

						List<RoomHeight> heights = new ArrayList<>();
						List<Measurement> rooms = new ArrayList<>();
						for (Room room : floor.getAcRooms()) {
							if (room.getHeights() != null)
								heights.addAll(room.getHeights());
							if (room.getRooms() != null)
								rooms.addAll(room.getRooms());
						}
						for (Measurement room : rooms) {
							totalroomArea = totalroomArea.add(room.getArea());

						}
					}
					System.out.println("Floor Area =" + totalFloorArea);
					System.out.println("Total Balcony Area=" + totalBalconyArea);
					System.out.println("Total Verandah Area=" + totalVerandahArea);
					System.out.println("Total Miscellaneous Area=" + totalmiscellaneousArea);
					System.out.println("Total Pantry Area=" + totalpantryArea);
					System.out.println("Total Stairs Area=" + totalStairArea);
					System.out.println("Total Room Area = " + totalroomArea);
					System.out.println("Total Parking Area = " + totalProvidedCarParkArea);
					System.out.println("Total BathRoom  = " + totalBathroomArea);
					System.out.println("Total BathRoom WC   = " + totalBathroomWCArea);
					System.out.println("Total Kitchen Area=" + totalKitchenArea);
					System.out.println("Total WC Area=" + totalWCArea);
					totalKitchenArea = kitchenArea.add(kitchenDiningArea).add(kitchenStoreArea);
					totalAreaAfterAdding = totalroomArea.add(totalBathroomArea).add(totalKitchenArea)
							.add(totalBathroomWCArea).add(totalWCArea).add(totalProvidedCarParkArea)
							.add(totalBalconyArea).add(totalVerandahArea).add(totalmiscellaneousArea).add(totalpantryArea)
							.add(totalStairArea);
					System.out.println("Total calculated Area=" + totalAreaAfterAdding);
					System.out.println("Total Area 80 % = " + totalFloorArea.multiply(BigDecimal.valueOf(0.80)));
					if (totalAreaAfterAdding.compareTo(totalFloorArea.multiply(BigDecimal.valueOf(0.80))) >= 0
							&& totalAreaAfterAdding.compareTo(totalFloorArea) <= 0) {

					} else {
						pl.addError("FloorAreaError" + floor.getNumber(),
								"The total Calculated Area of floor " + floor.getNumber()
										+ " does not match with the total floor area of that floor. Required"
										+ " total calculated Area of all features of floor(" + totalAreaAfterAdding
										+ ") should be >=80% of the total floor area(builtup area) (" + totalFloorArea
										+ ") and <=" + totalFloorArea);
					}

				} catch (Exception e) {

				}

			}
		}
	}

	public void buildVentilation(Plan pl, Block b, Integer floorNo, String roomNo, BigDecimal ventsarea,
			BigDecimal roomArea, ScrutinyDetail scrutinyDetail1) {
		Boolean status = false;

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "60 & 70 of TBR");
		details.put(DESCRIPTION, "Aggregate Room Ventilation Area");
		details.put(FLOOR, floorNo.toString());
		details.put("Room No", roomNo);
		details.put("Room Area", roomArea.toString());
		details.put(REQUIRED, ">0.1 times of roomArea");
		details.put(PROVIDED, ventsarea.toString());
		if (ventsarea.compareTo(roomArea.multiply(BigDecimal.valueOf(0.1))) > 0) {
			status = true;
		}
		if (status)
			details.put(STATUS, "Accepted");
		else
			details.put(STATUS, "Not Accepted");
		scrutinyDetail1.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail1);
	}

	@Override
	public Plan process(Plan pl) {
		if (pl != null)
			try {
				validate(pl);
				checkfloorarea(pl);
			} catch (Exception e) {
				e.printStackTrace();
			}

		return pl;
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String floor, String expected,
			String actual, String status) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(FLOOR, floor);
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

}
