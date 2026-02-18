package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.egov.common.entity.edcr.Floor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Kitchen;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Kitchen_AR extends Kitchen {
	private static final String SQMTRS = " m²";
	private static final String FLOOR = "Floor";
	private static final String KITCHEN = "kitchen";
	private static final String ROOM_HEIGHT_NOTDEFINED = "Kitchen height is not defined in layer ";
	private static final String LAYER_ROOM_HEIGHT = "BLK_%s_FLR_%s_%s";
	private static final String LAYER_ROOM_AREA = "BLK_%s_FLR_%s_%s";
	private static final String ROOM_AREA_NOTDEFINED = "Kitchen area is not defined in layer ";
	@Override
	public Plan validate(Plan pl) {
		
		
		return pl;
	}

	@Override
	public Plan process(Plan pl) {

//		try {
//			OccupancyTypeHelper mostRestrictiveOccupancyType = Util_AR.getMostRestrictive(pl);
//			HashMap<String, String> errors = new HashMap<>();
//
//			String layerName = StringUtils.EMPTY;
//			Boolean isKitchenDefined = false;
//			
//
//			if (pl != null && pl.getBlocks() != null) {
//				for (Block block : pl.getBlocks()) {
//					if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
//
//						scrutinyDetail = new ScrutinyDetail();
//						scrutinyDetail.addColumnHeading(1, RULE_NO);
//						scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//						scrutinyDetail.addColumnHeading(3, FLOOR);
//						scrutinyDetail.addColumnHeading(4, REQUIRED);
//						scrutinyDetail.addColumnHeading(5, PROVIDED);
//						scrutinyDetail.addColumnHeading(6, STATUS);
//
//						scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Kitchen");
//
//						boolean isResidential = block.getResidentialBuilding();
//
//						for (Floor floor : block.getBuilding().getFloors()) {
//							Boolean flag =false;
//							List<BigDecimal> kitchenAreas = new ArrayList<>();
//							List<BigDecimal> kitchenWidths = new ArrayList<>();
//							String subRule = null;
//							String subRuleDesc = null;
//							if(floor.getKitchen()==null && mostRestrictiveOccupancyType.getType().getCode().equalsIgnoreCase("R")) {
//								pl.addError("KitchenOccupancyError", "Kitchen Mandtory for Residential Building");
//							}
//
//							else if (floor.getKitchen() != null) {
//
//								isKitchenDefined = true;
//
//								subRule = "TBD";
//
//								List<BigDecimal> kitchenHeights = new ArrayList<>();
//								List<RoomHeight> heights = floor.getKitchen().getHeights();
//								List<Measurement> kitchenRooms = floor.getKitchen().getRooms();
//
//								for (RoomHeight roomHeight : heights) {
//									kitchenHeights.add(roomHeight.getHeight());
//								}
//
//								for (Measurement kitchen : kitchenRooms) {
//									kitchenAreas.add(kitchen.getArea());
//									kitchenWidths.add(kitchen.getWidth());
//								}
//
//								if (kitchenHeights != null && !kitchenHeights.isEmpty()) {
//									BigDecimal minHeight = kitchenHeights.stream().reduce(BigDecimal::min).get();
//									System.out.println(minHeight);
//									subRuleDesc = "TBD";
//									boolean valid = false;
//									boolean isTypicalRepititiveFloor = false;
//									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
//											floor, isTypicalRepititiveFloor);
//									
//									  buildResult(pl, floor, BigDecimal.ZERO, subRule, subRuleDesc,
//									  minHeight, valid, typicalFloorValues,flag);
//									 
//								} else {
//									
//									  layerName = String.format(LAYER_ROOM_HEIGHT, block.getNumber(),
//									  floor.getNumber(), "KITCHEN"); 
//									  addOutputErrorDetails(pl, layerName,ROOM_HEIGHT_NOTDEFINED + layerName);
//									 
//								}
//
//								
//								if (!kitchenAreas.isEmpty()) {
//									flag=true;
//									BigDecimal totalArea = kitchenAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
//
//									boolean valid = false;
//									subRuleDesc = String.format("TBD", KITCHEN);
//									boolean isTypicalRepititiveFloor = false;
//									Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block,
//											floor, isTypicalRepititiveFloor);
//									buildResult(pl, floor, BigDecimal.ZERO, subRule, subRuleDesc, totalArea, valid,
//											typicalFloorValues,flag);
//								} else {
//									layerName = String.format(LAYER_ROOM_AREA, block.getNumber(), floor.getNumber(),
//											"KITCHEN");
//									addOutputErrorDetails(pl, layerName, ROOM_AREA_NOTDEFINED + layerName);
//								}
//							}
//						}
//
//						
//					}
//				}
//			}
//		}catch (Exception e) {
//			// TODO: handle exception
//		}
		
		return pl;
	}
	private void buildResult(Plan pl, Floor floor, BigDecimal expected, String subRule, String subRuleDesc,
			BigDecimal actual, boolean valid, Map<String, Object> typicalFloorValues,Boolean flag) {
		String unit="m"; 
		if(flag)
			unit=SQMTRS;

		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")
				&& expected.compareTo(BigDecimal.valueOf(0)) > 0 && subRule != null && subRuleDesc != null) {
			if (actual.compareTo(expected) >= 0) {
				valid = true;
			}
			actual = actual.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

			String value = typicalFloorValues.get("typicalFloors") != null
					? (String) typicalFloorValues.get("typicalFloors")
					: " floor " + floor.getNumber();
//			if (valid) {
//				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected + unit,
//						actual + unit, Result.Accepted.getResultVal());
//			} else {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, expected + unit,
						actual + unit, Result.Verify.getResultVal());
//			}
		}
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String floor, String expected,
			String actual, String status) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(FLOOR, floor);
//		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	public void addOutputErrorDetails(Plan pl, String errorKey, String errorMsg) {
		pl.addError(errorKey, errorMsg);
	}
	

}
