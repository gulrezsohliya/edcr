package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.Kitchen;
import org.egov.edcr.service.ProcessHelper;
import org.springframework.stereotype.Service;

@Service
public class Kitchen_Tripura extends Kitchen {



	public static final BigDecimal MINIMUM_HEIGHT_2_75 = BigDecimal.valueOf(2.75);
	public static final BigDecimal MINIMUM_HEIGHT_2_4 = BigDecimal.valueOf(2.4);
	public static final BigDecimal MINIMUM_AREA_4_5 = BigDecimal.valueOf(4.5);
	public static final BigDecimal MINIMUM_AREA_7_5 = BigDecimal.valueOf(7.5);
	public static final BigDecimal MINIMUM_AREA_5 = BigDecimal.valueOf(5);

	public static final BigDecimal MINIMUM_WIDTH_1_8 = BigDecimal.valueOf(1.8);
	public static final BigDecimal MINIMUM_WIDTH_2_1 = BigDecimal.valueOf(2.1);
	private static final String FLOOR = "Floor";
	private static final String KITCHEN = "kitchen";
	private static final String KITCHEN_DINING = "kitchen with dining hall";

	private static final BigDecimal MINIMUM_AREA_9_5 = BigDecimal.valueOf(9.5);
	private static final BigDecimal MINIMUM_WIDTH_2_4 = BigDecimal.valueOf(2.4);

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			Map<String, Integer> heightOfRoomFeaturesColor = pl.getSubFeatureColorCodesMaster().get("HeightOfRoom");
			validate(pl);
			if (pl != null && pl.getBlocks() != null) {
				OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
						? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
						: null;
					for (Block block : pl.getBlocks()) {
					ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
					scrutinyDetail1.addColumnHeading(1, RULE_NO);
					scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
					scrutinyDetail1.addColumnHeading(3, FLOOR);
					scrutinyDetail1.addColumnHeading(4, REQUIRED);
					scrutinyDetail1.addColumnHeading(5, PROVIDED);
					scrutinyDetail1.addColumnHeading(6, STATUS);
					scrutinyDetail1.setKey("Block_" + block.getNumber() + "_" + "Kitchen Ventilations");
					if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {

						scrutinyDetail = new ScrutinyDetail();
						scrutinyDetail.addColumnHeading(1, RULE_NO);
						scrutinyDetail.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail.addColumnHeading(3, FLOOR);
						scrutinyDetail.addColumnHeading(4, REQUIRED);
						scrutinyDetail.addColumnHeading(5, PROVIDED);
						scrutinyDetail.addColumnHeading(6, STATUS);

						scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Kitchen");
						int flag = 0;
						int kitchenHeightFlag = 0;
						int kitchenAreaFlag = 0;
						int kitchenWidthFlag = 0;
						for (Floor floor : block.getBuilding().getFloors()) {
							List<BigDecimal> kitchenAreas = new ArrayList<>();
							List<BigDecimal> kitchenStoreAreas = new ArrayList<>();
							List<BigDecimal> kitchenDiningAreas = new ArrayList<>();
							List<BigDecimal> kitchenWidths = new ArrayList<>();
							List<BigDecimal> kitchenStoreWidths = new ArrayList<>();
							List<BigDecimal> kitchenDiningWidths = new ArrayList<>();
							List<BigDecimal> kitchenHeights = new ArrayList<>();
							List<BigDecimal> kitchenStoreHeights = new ArrayList<>();
							List<BigDecimal> kitchenDiningHeights = new ArrayList<>();
							BigDecimal minimumHeight = BigDecimal.ZERO;
							BigDecimal totalArea = BigDecimal.ZERO;
							BigDecimal minWidth = BigDecimal.ZERO;
							String subRule = null;
							String subRuleDesc = null;
							String kitchenRoomColor = "";
							String kitchenStoreRoomColor = "";
							String kitchenDiningRoomColor = "";
							String comercialKitchenRoomColor = "";
							String comercialKitchenDiningRoomColor = "";

							kitchenRoomColor = DxfFileConstants.RESIDENTIAL_KITCHEN_ROOM_COLOR;
							kitchenStoreRoomColor = DxfFileConstants.RESIDENTIAL_KITCHEN_STORE_ROOM_COLOR;
							kitchenDiningRoomColor = DxfFileConstants.RESIDENTIAL_KITCHEN_DINING_ROOM_COLOR;

							comercialKitchenRoomColor = DxfFileConstants.COMMERCIAL_KITCHEN_ROOM_COLOR;
							comercialKitchenDiningRoomColor = DxfFileConstants.COMMERCIAL_KITCHEN_DINING_ROOM_COLOR;

							if (floor.getKitchen() != null) {
								List<RoomHeight> heights = new ArrayList<>();
								List<Measurement> kitchenRooms = floor.getKitchen().getRooms();
								for (Measurement kitchen : kitchenRooms) {
									if (heightOfRoomFeaturesColor.get(kitchenRoomColor) == kitchen.getColorCode()) {

										kitchenAreas.add(kitchen.getArea());
										kitchenWidths.add(kitchen.getWidth());
										if (floor.getKitchen().getHeights() != null) {
											kitchenHeightFlag = 1;
											heights.addAll(floor.getKitchen().getHeights());
										}
										if (heights != null && !heights.isEmpty()) {
											for (RoomHeight roomHeight : heights) {
												kitchenHeights.add(roomHeight.getHeight());
											}
										}
									} else if (heightOfRoomFeaturesColor.get(kitchenStoreRoomColor) == kitchen
											.getColorCode()) {
										kitchenStoreAreas.add(kitchen.getArea());
										kitchenStoreWidths.add(kitchen.getWidth());
										if (floor.getKitchen().getHeights() != null) {
											kitchenHeightFlag = 1;
											heights.addAll(floor.getKitchen().getHeights());
										}
										if (heights != null && !heights.isEmpty()) {
											for (RoomHeight roomHeight : heights) {
												kitchenStoreHeights.add(roomHeight.getHeight());
											}
										}
									} else if (heightOfRoomFeaturesColor.get(kitchenDiningRoomColor) == kitchen
											.getColorCode()) {
										kitchenDiningAreas.add(kitchen.getArea());
										kitchenDiningWidths.add(kitchen.getWidth());
										if (floor.getKitchen().getHeights() != null) {
											kitchenHeightFlag = 1;
											heights.addAll(floor.getKitchen().getHeights());
										}
										if (heights != null && !heights.isEmpty()) {
											for (RoomHeight roomHeight : heights) {
												kitchenDiningHeights.add(roomHeight.getHeight());
											}
										}
									} else if (heightOfRoomFeaturesColor.get(comercialKitchenRoomColor) == kitchen
											.getColorCode()) {
										kitchenDiningAreas.add(kitchen.getArea());
										kitchenDiningWidths.add(kitchen.getWidth());
										if (floor.getKitchen().getHeights() != null) {
											kitchenHeightFlag = 1;
											heights.addAll(floor.getKitchen().getHeights());
										}
										if (heights != null && !heights.isEmpty()) {
											for (RoomHeight roomHeight : heights) {
												kitchenDiningHeights.add(roomHeight.getHeight());
											}
										}
									} else if (heightOfRoomFeaturesColor.get(comercialKitchenDiningRoomColor) == kitchen
											.getColorCode()) {
										kitchenDiningAreas.add(kitchen.getArea());
										kitchenDiningWidths.add(kitchen.getWidth());
										if (floor.getKitchen().getHeights() != null) {
											kitchenHeightFlag = 1;
											heights.addAll(floor.getKitchen().getHeights());
										}
										if (heights != null && !heights.isEmpty()) {
											for (RoomHeight roomHeight : heights) {
												kitchenDiningHeights.add(roomHeight.getHeight());
											}
										}
									}

								}

								subRule = "58(1)";
								
								if (!kitchenAreas.isEmpty()) {
									kitchenAreaFlag = 1;
									flag = 1;
									totalArea = kitchenAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
									minimumHeight = MINIMUM_AREA_4_5;
									subRuleDesc = String.format("Kitchen Floor Area Without Dining", KITCHEN);

									boolean valid = false;
									boolean isTypicalRepititiveFloor = false;
									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
											floor, isTypicalRepititiveFloor);
									buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, totalArea, valid,
											typicalFloorValues, flag);
									flag = 0;

								}

								if (!kitchenWidths.isEmpty()) {
									kitchenWidthFlag = 1;
									boolean valid = false;
									boolean isTypicalRepititiveFloor = false;
									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
											floor, isTypicalRepititiveFloor);
									BigDecimal minRoomWidth = kitchenWidths.stream().reduce(BigDecimal::min).get();
									minWidth = MINIMUM_WIDTH_1_8;
									subRuleDesc = String.format("Kitchen Width Without Dining", KITCHEN);
									buildResult(pl, floor, minWidth, subRule, subRuleDesc, minRoomWidth, valid,
											typicalFloorValues, flag);
								}

								if (!kitchenDiningAreas.isEmpty()) {
									kitchenAreaFlag = 1;
									flag = 1;
									totalArea = kitchenDiningAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
									minimumHeight = MINIMUM_AREA_9_5;
									subRuleDesc = String.format("Kitchen Floor Area With Dining", KITCHEN_DINING);

									boolean valid = false;
									boolean isTypicalRepititiveFloor = false;
									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
											floor, isTypicalRepititiveFloor);
									buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, totalArea, valid,
											typicalFloorValues, flag);
									flag = 0;

								}

								if (!kitchenDiningWidths.isEmpty()) {
									kitchenWidthFlag = 1;
									boolean valid = false;
									boolean isTypicalRepititiveFloor = false;
									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
											floor, isTypicalRepititiveFloor);
									BigDecimal minRoomWidth = kitchenDiningWidths.stream().reduce(BigDecimal::min)
											.get();
									minWidth = MINIMUM_WIDTH_2_4;
									subRuleDesc = String.format("Kitchen Width With Dining", KITCHEN_DINING);
									buildResult(pl, floor, minWidth, subRule, subRuleDesc, minRoomWidth, valid,
											typicalFloorValues, flag);
								}
								if (!kitchenHeights.isEmpty()) {
                                    BigDecimal minHeight = kitchenHeights.stream().reduce(BigDecimal::min).get();

                                    minimumHeight = BigDecimal.valueOf(2.5);

                                    boolean valid = false;
                                    boolean isTypicalRepititiveFloor = false;
                                    subRuleDesc = String.format("Kitchen Height Without Dining", KITCHEN);
                                    Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
                                            isTypicalRepititiveFloor);
                                    buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, minHeight, valid,
                                            typicalFloorValues,flag);
                                }
								
								if (!kitchenDiningHeights.isEmpty()) {
                                    BigDecimal minHeight = kitchenDiningHeights.stream().reduce(BigDecimal::min).get();

                                    minimumHeight = BigDecimal.valueOf(2.5);

                                    boolean valid = false;
                                    boolean isTypicalRepititiveFloor = false;
                                    subRuleDesc = String.format("Kitchen Height With Dining", KITCHEN);
                                    Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
                                            isTypicalRepititiveFloor);
                                    buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, minHeight, valid,
                                            typicalFloorValues,flag);
                                }

							}

							BigDecimal ventilationArea = BigDecimal.ZERO;
							Room kitchenvent = new Room();
							if (floor.getKitchen() != null) {
								kitchenvent = floor.getKitchen();
								if (kitchenvent.getLightAndVentilation() != null
										&& kitchenvent.getLightAndVentilation().getMeasurements() != null
										&& !kitchenvent.getLightAndVentilation().getMeasurements().isEmpty()) {

									ventilationArea = kitchenvent.getLightAndVentilation().getMeasurements().stream()
											.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
									;

									buildVentilation(pl, block, floor.getNumber(), ventilationArea, totalArea,
											scrutinyDetail1);
								} else {
									pl.addError("KitchenVent"+block.getNumber() + floor.getNumber(),"Block "+block.getNumber()+
											" Kitchen present but Kitchen Ventilations Undefined for floor no " + floor.getNumber());
								}
							}
							

						}
						BigDecimal coverage = block.getBuilding().getCoverageArea();
						if(block.getBuilding().getTotalExistingBuiltUpArea().compareTo(BigDecimal.ZERO)<=0) {
							if(coverage.compareTo(BigDecimal.valueOf(15))>0) {
								if (kitchenAreaFlag == 0 && mostRestrictiveOccupancy.getType().getCode().equalsIgnoreCase(R)) {
									pl.addError("KitchenAreaError"+block.getNumber(), "Block "+block.getNumber()+" Kitchen Area Undefined ");
								}
								if (kitchenWidthFlag == 0 && mostRestrictiveOccupancy.getType().getCode().equalsIgnoreCase(R)) {
									pl.addError("KitchenWidthError"+block.getNumber(),"Block "+block.getNumber()+ " Kitchen Width Undefined ");
								}
								if (kitchenHeightFlag == 0
										&& mostRestrictiveOccupancy.getType().getCode().equalsIgnoreCase(R)) {
									pl.addError("KitchenHeightError"+block.getNumber(), "Block "+block.getNumber()+" Kitchen Height Undefined ");
								}
							}
							
						}
						// errormessages
					
					}
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;

	}

	private void buildResult(Plan pl, Floor floor, BigDecimal expected, String subRule, String subRuleDesc,
			BigDecimal actual, boolean valid, Map<String, Object> typicalFloorValues, int flag) {
		String newactual = "";
		actual = actual.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")
				&& expected.compareTo(BigDecimal.valueOf(0)) > 0 && subRule != null && subRuleDesc != null) {
			if (actual.compareTo(expected) >= 0) {
				valid = true;
			}
			if (flag == 1)
				newactual = " m²";
			else
				newactual = " m";
			String value = typicalFloorValues.get("typicalFloors") != null
					? (String) typicalFloorValues.get("typicalFloors")
					: " floor " + floor.getNumber();
			if (valid) {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected.toString(),
						actual.toString() + newactual, Result.Accepted.getResultVal());
			} else {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected.toString(),
						actual.toString() + newactual, Result.Not_Accepted.getResultVal());
			}
		}
	}

	public void buildVentilation(Plan pl, Block b, Integer floorNo, BigDecimal ventsarea, BigDecimal roomArea,
			ScrutinyDetail scrutinyDetail1) {
		Boolean status = false;

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "60 & 70 of TBR");
		details.put(DESCRIPTION, "Kitchen Ventilation Area");
		details.put(FLOOR, floorNo.toString());
		details.put("Kitchen Area", roomArea.toString());
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
