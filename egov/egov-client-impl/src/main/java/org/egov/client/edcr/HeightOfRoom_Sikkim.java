package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.HeightOfRoom;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class HeightOfRoom_Sikkim extends HeightOfRoom {

	private static final Logger LOG = Logger.getLogger(HeightOfRoom_Sikkim.class);

	private static final BigDecimal MINIMUM_HEIGHT_2_75 = new BigDecimal(2.75);
	private static final BigDecimal MINIMUM_WIDTH_2_43 = new BigDecimal(2.43);
	private static final BigDecimal MINIMUM_AREA_7_43 = new BigDecimal(7.43);
	private static final String RULE_16 = "16";
	private static final String FLOOR = "Floor";
	private static final String ROOM_HEIGHT_NOTDEFINED = "Room height is not defined in layer ";
	private static final String ROOM_AREA_NOTDEFINED = "Room area is not defined in layer ";
	private static final String ROOM_WIDTH_NOTDEFINED = "Room width is not defined in layer ";
	private static final String LAYER_ROOM_HEIGHT = "BLK_%s_FLR_%s_%s";

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub

		HashMap<String, String> errors = new HashMap<>();

		Map<String, Integer> heightOfRoomFeaturesColor = null;
		if(plan.getSubFeatureColorCodesMaster()!=null)
			heightOfRoomFeaturesColor=plan.getSubFeatureColorCodesMaster().get("HeightOfRoom");

		if (plan.getBlocks() != null && !plan.getBlocks().isEmpty()) {

			Boolean isHabitableRoomDefined = false;

			for (Block block : plan.getBlocks()) {
				if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
					scrutinyDetail = new ScrutinyDetail();
					scrutinyDetail.addColumnHeading(1, RULE_NO);
					scrutinyDetail.addColumnHeading(2, DESCRIPTION);
					scrutinyDetail.addColumnHeading(3, FLOOR);
					scrutinyDetail.addColumnHeading(4, REQUIRED);
					scrutinyDetail.addColumnHeading(5, PROVIDED);
					scrutinyDetail.addColumnHeading(6, STATUS);

					scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Room");

					for (Floor floor : block.getBuilding().getFloors()) {

						List<BigDecimal> roomHeights = new ArrayList<>();
						List<BigDecimal> roomAreas = new ArrayList<>();
						List<BigDecimal> roomWidths = new ArrayList<>();
						BigDecimal minimumHeight = BigDecimal.ZERO;
						BigDecimal totalArea = BigDecimal.ZERO;
						BigDecimal minArea = BigDecimal.ZERO;
						BigDecimal minWidth = BigDecimal.ZERO;
						String subRule = null;
						String subRuleDesc = null;
						String color = "Residential room"; /* residential */

						if (floor.getAcRooms() != null && floor.getAcRooms().size() > 0) {
							List<BigDecimal> residentialAcRoomHeights = new ArrayList<>();

							List<RoomHeight> acHeights = new ArrayList<>();
							List<Measurement> acRooms = new ArrayList<>();

							for (Room room : floor.getAcRooms()) {
								if (room.getHeights() != null)
									acHeights.addAll(room.getHeights());
								if (room.getRooms() != null)
									acRooms.addAll(room.getRooms());
							}

							for (RoomHeight roomHeight : acHeights) {
								if (heightOfRoomFeaturesColor.get(color) == roomHeight.getColorCode()) {
									residentialAcRoomHeights.add(roomHeight.getHeight());
								}
							}

							for (Measurement acRoom : acRooms) {
								if (heightOfRoomFeaturesColor.get(color) == acRoom.getColorCode()) {
									roomAreas.add(acRoom.getArea());
									roomWidths.add(acRoom.getWidth());
								}
							}

							if (!residentialAcRoomHeights.isEmpty()) {
								BigDecimal minHeight = residentialAcRoomHeights.stream().reduce(BigDecimal::min).get();

								minimumHeight = MINIMUM_HEIGHT_2_75;

								subRule = RULE_16;
								subRuleDesc = String.format("Minimum %s height", "Habitable Room");

								boolean valid = false;
								boolean isTypicalRepititiveFloor = false;
								Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
										floor, isTypicalRepititiveFloor);
								buildResult(plan, floor, minimumHeight, subRule, subRuleDesc, minHeight, valid,
										typicalFloorValues);
							} else {
								String layerName = String.format(LAYER_ROOM_HEIGHT, block.getNumber(),
										floor.getNumber(), "AC_ROOM");
								errors.put(layerName, ROOM_HEIGHT_NOTDEFINED + layerName);
								/* plan.addErrors(errors); */
							}

						}

						if (floor.getRegularRooms() != null && !floor.getRegularRooms().isEmpty()
								&& floor.getRegularRooms().size() > 0) {

							/*
							 * floor.getRegularRooms().forEach(action -> { LOG.info("Number: " +
							 * action.getNumber()); action.getRooms().forEach(action1 -> {
							 * LOG.info("Rooms"); LOG.info("Name: " + action1.getName());
							 * LOG.info("colorCode: " + action1.getColorCode()); LOG.info("height: " +
							 * action1.getHeight()); LOG.info("area: " + action1.getArea());
							 * LOG.info("width: " + action1.getWidth()); });
							 * 
							 * LOG.info("Height size: " + action.getHeights().size());
							 * 
							 * action.getHeights().forEach(action1 -> { LOG.info("HEIGHT: ");
							 * LOG.info("height: " + action1.getHeight()); LOG.info("colorCode: " +
							 * action1.getColorCode()); });
							 * 
							 * });
							 * 
							 * LOG.info("heightOfRoomFeaturesColor size: " +
							 * heightOfRoomFeaturesColor.size()); if (heightOfRoomFeaturesColor != null &&
							 * !heightOfRoomFeaturesColor.isEmpty()) {
							 * LOG.info("heightOfRoomFeaturesColor");
							 * heightOfRoomFeaturesColor.entrySet().forEach(entry -> { LOG.info("KEY: " +
							 * entry.getKey() + " VALUE: " + entry.getValue()); }); }
							 */
							 

							isHabitableRoomDefined = true;

							List<BigDecimal> residentialRoomHeights = new ArrayList<>();

							List<RoomHeight> heights = new ArrayList<>();
							List<Measurement> rooms = new ArrayList<>();

							for (Room room : floor.getRegularRooms()) {
								if (room.getHeights() != null)
									heights.addAll(room.getHeights());
								if (room.getRooms() != null)
									rooms.addAll(room.getRooms());
							}

							if (!heights.isEmpty()) {
								for (RoomHeight roomHeight : heights) {
									/*
									 * if (heightOfRoomFeaturesColor.get(color) == roomHeight.getColorCode()) {
									 * residentialRoomHeights.add(roomHeight.getHeight()); }
									 */
									roomHeights.add(roomHeight.getHeight());
								}
							}

							for (Measurement room : rooms) {
								/*
								 * if (heightOfRoomFeaturesColor.get(color) == room.getColorCode()) {
								 * roomAreas.add(room.getArea()); roomWidths.add(room.getWidth()); }
								 */
								roomAreas.add(room.getArea());
								roomWidths.add(room.getWidth());
							}

							/* if (!residentialRoomHeights.isEmpty()) { */
							if (!roomHeights.isEmpty()) {
							
								BigDecimal minHeight = roomHeights.stream().reduce(BigDecimal::min).get();

								minimumHeight = MINIMUM_HEIGHT_2_75;

								subRule = RULE_16;
								subRuleDesc = String.format("Minimum %s height", "Habitable Room");

								boolean valid = false;
								boolean isTypicalRepititiveFloor = false;
								Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
										floor, isTypicalRepititiveFloor);
								buildResult(plan, floor, minimumHeight, subRule, subRuleDesc, minHeight, valid,
										typicalFloorValues);
							} else {
								String layerName = String.format(LAYER_ROOM_HEIGHT, block.getNumber(),
										floor.getNumber(), "REGULAR ROOM");
								errors.put(layerName, ROOM_HEIGHT_NOTDEFINED + layerName);
								plan.addErrors(errors);
							}
							/*
							 * }
							 * 
							 * if (floor.getRegularRooms() != null && !floor.getRegularRooms().isEmpty()) {
							 */

							if (!roomAreas.isEmpty()) {
								totalArea = roomAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
								minArea = MINIMUM_AREA_7_43;

								subRule = RULE_16;
								subRuleDesc = String.format("Minimum %s area", "Habitable Room");

								boolean valid = false;
								boolean isTypicalRepititiveFloor = false;
								Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
										floor, isTypicalRepititiveFloor);
								buildResult(plan, floor, minArea, subRule, subRuleDesc, totalArea, valid,
										typicalFloorValues);

							} else {
								String layerName = String.format("Block %s Floor %s %s", block.getNumber(),
										floor.getNumber(), "HABITABLE_ROOM");
								errors.put(layerName, ROOM_AREA_NOTDEFINED + layerName);
								plan.addErrors(errors);
							}

							/*
							 * if (!roomWidths.isEmpty()) { BigDecimal minRoomWidth =
							 * roomWidths.stream().reduce(BigDecimal::min).get(); minWidth =
							 * MINIMUM_WIDTH_2_43;
							 * 
							 * subRule = RULE_16; subRuleDesc = String.format("Minimum %s width",
							 * "Habitable Room");
							 * 
							 * boolean valid = false; boolean isTypicalRepititiveFloor = false; Map<String,
							 * Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
							 * floor, isTypicalRepititiveFloor); buildResult(plan, floor, minWidth, subRule,
							 * subRuleDesc, minRoomWidth, valid, typicalFloorValues); } else { String
							 * layerName = String.format("Block %s Floor %s %s", block.getNumber(),
							 * floor.getNumber(), "REGULAR_ROOM"); errors.put(layerName,
							 * ROOM_WIDTH_NOTDEFINED + layerName); plan.addErrors(errors); }
							 */
						}

					}
				}

				if (!isHabitableRoomDefined) {
					errors.put(
							String.format("Block %s %s %s", block.getNumber(), "REGULAR_ROOM",
									DcrConstants.OBJECTNOTDEFINED),
							String.format("Block %s %s %s", block.getNumber(), "REGULAR_ROOM",
									DcrConstants.OBJECTNOTDEFINED_DESC));
					plan.addErrors(errors);
				}
			}
		}

		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		LOG.info("DECENT_SIKKIM HeightOfRoom_: PRCOCESS");

		if (plan != null && plan.getPlot() != null && plan.getPlot().getArea().compareTo(BigDecimal.ZERO) >= 0)
			validate(plan);

		return plan;
	}

	private void buildResult(Plan pl, Floor floor, BigDecimal expected, String subRule, String subRuleDesc,
			BigDecimal actual, boolean valid, Map<String, Object> typicalFloorValues) {

		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")
				&& expected.compareTo(BigDecimal.valueOf(0)) > 0 && subRule != null && subRuleDesc != null) {
			if (actual.compareTo(expected) >= 0) {
				valid = true;
			}
			expected = expected.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
			actual = actual.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

			String value = typicalFloorValues.get("typicalFloors") != null
					? (String) typicalFloorValues.get("typicalFloors")
					: " floor " + floor.getNumber();
			if (valid) {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected + DcrConstants.IN_METER,
						actual + DcrConstants.IN_METER, Result.Accepted.getResultVal());
			} else {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected + DcrConstants.IN_METER,
						actual + DcrConstants.IN_METER, Result.Not_Accepted.getResultVal());
			}
		}
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
