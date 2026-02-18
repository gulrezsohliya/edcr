package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Kitchen;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Kitchen_Sikkim extends Kitchen {
	private static final Logger LOG = Logger.getLogger(Kitchen_Sikkim.class);

	private static final String RULE_16 = "16";

	private static final String RULE_16_DESC = "Minimum height of kitchen";
	private static final String RULE_16_AREA_DESC = "Minimum area of %s";
	private static final String RULE_16_WIDTH_DESC = "Minimum Width of %s";

	public static final BigDecimal MINIMUM_HEIGHT_2_75 = BigDecimal.valueOf(2.75);
	public static final BigDecimal MINIMUM_HEIGHT_2_4 = BigDecimal.valueOf(2.4);
	public static final BigDecimal MINIMUM_AREA_4_5 = BigDecimal.valueOf(4.5);
	public static final BigDecimal MINIMUM_AREA_7_5 = BigDecimal.valueOf(7.5);
	public static final BigDecimal MINIMUM_AREA_5 = BigDecimal.valueOf(5);

	public static final BigDecimal MINIMUM_WIDTH_1_8 = BigDecimal.valueOf(1.8);
	public static final BigDecimal MINIMUM_WIDTH_2_1 = BigDecimal.valueOf(2.1);
	private static final String FLOOR = "Floor";
	private static final String ROOM_HEIGHT_NOTDEFINED = "Kitchen height is not defined in layer ";
	private static final String LAYER_ROOM_HEIGHT = "BLK_%s_FLR_%s_%s";
	private static final String KITCHEN = "kitchen";
	private static final String KITCHEN_STORE = "kitchen with store room";
	private static final String KITCHEN_DINING = "kitchen with dining hall";

	public static final BigDecimal MINIMUM_AREA_SK_5_01 = BigDecimal.valueOf(5.01);// metres
	public static final BigDecimal MINIMUM_HEIGHT_SK_2_75 = BigDecimal.valueOf(2.75);// metres
	public static final BigDecimal MINIMUM_WIDTH_SK_1_82 = BigDecimal.valueOf(1.82);// metres
	private static final String LAYER_ROOM_AREA = "BLK_%s_FLR_%s_%s";
	private static final String LAYER_ROOM_WIDTH = "BLK_%s_FLR_%s_%s";
	private static final String LAYER_ROOM_LIGHTANDVENTILATION = "BLK_%s_FLR_%s_%s";
	private static final String LAYER_ROOM = "BLK_%s_%s";
	private static final String ROOM_AREA_NOTDEFINED = "Kitchen area is not defined in layer ";
	private static final String ROOM_WIDTH_NOTDEFINED = "Kitchen width is not defined in layer ";
	private static final String ROOM_LIGHTANDVENTILATION_NOTDEFINED = "Kitchen light and ventilation is not defined in layer ";
	private static final String ROOM_NOTDEFINED = "Kitchen is not defined in layer ";

	public static final BigDecimal MINIMUM_LIGHTANDVENTILATIONAREA_SK_25_00 = BigDecimal.valueOf(25.00);// percent

	@Override
	public Plan validate(Plan pl) {
		try {
			HashMap<String, String> errors = new HashMap<>();

			/* LOG.info("Decent : validateKitchenofBuildingSikkim"); */
			String layerName = StringUtils.EMPTY;
			Boolean isKitchenDefined = false;

			if (pl != null && pl.getBlocks() != null) {
				for (Block block : pl.getBlocks()) {
					/*
					 * LOG.info("Decent : block["+block.getNumber()+"]");
					 */
					if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {

						scrutinyDetail = new ScrutinyDetail();
						scrutinyDetail.addColumnHeading(1, RULE_NO);
						scrutinyDetail.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail.addColumnHeading(3, FLOOR);
						scrutinyDetail.addColumnHeading(4, REQUIRED);
						scrutinyDetail.addColumnHeading(5, PROVIDED);
						scrutinyDetail.addColumnHeading(6, STATUS);

						scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Kitchen");

						boolean isResidential = block.getResidentialBuilding();

						for (Floor floor : block.getBuilding().getFloors()) {

							List<BigDecimal> kitchenAreas = new ArrayList<>();
							List<BigDecimal> kitchenStoreAreas = new ArrayList<>();
							List<BigDecimal> kitchenDiningAreas = new ArrayList<>();
							List<BigDecimal> kitchenWidths = new ArrayList<>();
							List<BigDecimal> kitchenStoreWidths = new ArrayList<>();
							List<BigDecimal> kitchenDiningWidths = new ArrayList<>();
							List<BigDecimal> kitchenLigthAndVentilationAreas = new ArrayList<>();

							BigDecimal minimumHeight = BigDecimal.ZERO;
							BigDecimal totalArea = BigDecimal.ZERO;
							BigDecimal minArea = BigDecimal.ZERO;
							BigDecimal totalLightAndVentilationArea = BigDecimal.ZERO;
							BigDecimal lightAndVentilationToFloorAreaCoverage = BigDecimal.ZERO;
							BigDecimal minWidth = BigDecimal.ZERO;
							String subRule = null;
							String subRuleDesc = null;

							if (floor.getKitchen() != null) {

								isKitchenDefined = true;

								subRule = RULE_16;

								List<BigDecimal> kitchenHeights = new ArrayList<>();
								List<RoomHeight> heights = floor.getKitchen().getHeights();
								List<Measurement> kitchenRooms = floor.getKitchen().getRooms();

								for (RoomHeight roomHeight : heights) {
									kitchenHeights.add(roomHeight.getHeight());
								}

								for (Measurement kitchen : kitchenRooms) {
									kitchenAreas.add(kitchen.getArea());
									kitchenWidths.add(kitchen.getWidth());
								}

								if (kitchenHeights != null && !kitchenHeights.isEmpty()) {
									BigDecimal minHeight = kitchenHeights.stream().reduce(BigDecimal::min).get();
									subRuleDesc = RULE_16_DESC;
									boolean valid = false;
									boolean isTypicalRepititiveFloor = false;
									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
											floor, isTypicalRepititiveFloor);
									/*
									 * buildResult(pl, floor, MINIMUM_HEIGHT_SK_2_75, subRule, subRuleDesc,
									 * minHeight, valid, typicalFloorValues);
									 */
								} else {
									/*
									 * layerName = String.format(LAYER_ROOM_HEIGHT, block.getNumber(),
									 * floor.getNumber(), "KITCHEN"); addOutputErrorDetails(pl, layerName,
									 * ROOM_HEIGHT_NOTDEFINED + layerName);
									 */
								}

								if (!kitchenWidths.isEmpty()) {
									BigDecimal minRoomWidth = kitchenWidths.stream().reduce(BigDecimal::min).get();

									boolean valid = false;
									subRuleDesc = String.format(RULE_16_WIDTH_DESC, KITCHEN);
									boolean isTypicalRepititiveFloor = false;
									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
											floor, isTypicalRepititiveFloor);
									/*
									 * buildResult(pl, floor, MINIMUM_WIDTH_SK_1_82, subRule, subRuleDesc,
									 * minRoomWidth, valid, typicalFloorValues);
									 */
								} else {
									/*
									 * layerName = String.format(LAYER_ROOM_WIDTH, block.getNumber(),
									 * floor.getNumber(), "KITCHEN"); addOutputErrorDetails(pl, layerName,
									 * ROOM_WIDTH_NOTDEFINED + layerName);
									 */
								}
								if (!kitchenAreas.isEmpty()) {
									BigDecimal totalRoomArea = kitchenAreas.stream().reduce(BigDecimal::add).get();

									boolean valid = false;
									subRuleDesc = String.format(RULE_16_AREA_DESC, KITCHEN);
									boolean isTypicalRepititiveFloor = false;
									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
											floor, isTypicalRepititiveFloor);
									buildResult(pl, floor, MINIMUM_AREA_SK_5_01, subRule, subRuleDesc, totalRoomArea, valid,
											typicalFloorValues);
								} else {
									layerName = String.format(LAYER_ROOM_AREA, block.getNumber(), floor.getNumber(),
											"KITCHEN");
									addOutputErrorDetails(pl, layerName, ROOM_AREA_NOTDEFINED + layerName);
								}
							}
						}

						if (!isKitchenDefined && isResidential) {
							addOutputErrorDetails(pl,
									String.format("Block %s %s %s", block.getNumber(), KITCHEN,
											DcrConstants.OBJECTNOTDEFINED),
									String.format("Block %s %s %s", block.getNumber(), KITCHEN,
											DcrConstants.OBJECTNOTDEFINED_DESC));
						}
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		LOG.info("DECENT_SIKKIM Kitchen Service: Process");
		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
			return pl;
		}
		validate(pl);

		return pl;

	}

	private void buildResult(Plan pl, Floor floor, BigDecimal expected, String subRule, String subRuleDesc,
			BigDecimal actual, boolean valid, Map<String, Object> typicalFloorValues) { 

		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")
				&& expected.compareTo(BigDecimal.valueOf(0)) > 0 && subRule != null && subRuleDesc != null) {
			if (actual.compareTo(expected) >= 0) {
				valid = true;
			}
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

	public void addOutputErrorDetails(Plan pl, String errorKey, String errorMsg) {
		pl.addError(errorKey, errorMsg);
	}

}
