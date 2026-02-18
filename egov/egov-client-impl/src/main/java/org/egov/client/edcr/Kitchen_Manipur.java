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
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.Kitchen;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Kitchen_Manipur extends Kitchen {
	private static final Logger LOG = Logger.getLogger(Kitchen_Manipur.class);

	private static final String RULE_25_iii = "25(iii)";

	private static final String RULE_16_DESC = "Minimum height of kitchen";
	private static final String RULE_16_AREA_DESC = "Minimum area of %s";
	private static final String RULE_16_WIDTH_DESC = "Minimum Width of %s";
	private static final String SQMTRS = " m²";

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

	public static final BigDecimal MINIMUM_AREA_5_6 = BigDecimal.valueOf(5.6);// metres
	public static final BigDecimal MINIMUM_HEIGHT_SK_2_75 = BigDecimal.valueOf(2.75);// metres
	public static final BigDecimal MINIMUM_WIDTH_SK_1_8 = BigDecimal.valueOf(1.8);// metres
	private static final String LAYER_ROOM_AREA = "BLK_%s_FLR_%s_%s";
	private static final String LAYER_ROOM_WIDTH = "BLK_%s_FLR_%s_%s";
	private static final String LAYER_ROOM_LIGHTANDVENTILATION = "BLK_%s_FLR_%s_%s";
	private static final String LAYER_ROOM = "BLK_%s_%s";
	private static final String ROOM_AREA_NOTDEFINED = "Kitchen area is not defined in layer ";
	private static final String ROOM_WIDTH_NOTDEFINED = "Kitchen width is not defined in layer ";
	private static final String ROOM_LIGHTANDVENTILATION_NOTDEFINED = "Kitchen light and ventilation is not defined in layer ";
	private static final String ROOM_NOTDEFINED = "Kitchen is not defined in layer ";

	public static final BigDecimal MINIMUM_LIGHTANDVENTILATIONAREA_SK_25_00 = BigDecimal.valueOf(25.00);// percent

	private static final BigDecimal MINIMUM_AREA_4_8 = BigDecimal.valueOf(4.8);

	private static final BigDecimal MINIMUM_AREA_9_5 = BigDecimal.valueOf(9.5);;

	private static final BigDecimal MINIMUM_WIDTH_2_4 = BigDecimal.valueOf(2.4);;

	@Override
	public Plan validate(Plan pl) {
		try {
			Map<String, Integer> heightOfRoomFeaturesColor = pl.getSubFeatureColorCodesMaster().get("HeightOfRoom");
	        HashMap<String, String> errors = new HashMap<>();
	        if (pl != null && pl.getBlocks() != null) {
	                blk: for (Block block : pl.getBlocks()) {
	                    if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
	                    	
	                        scrutinyDetail = new ScrutinyDetail();
	                        scrutinyDetail.addColumnHeading(1, RULE_NO);
	                        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	                        scrutinyDetail.addColumnHeading(3, FLOOR);
	                        scrutinyDetail.addColumnHeading(4, REQUIRED);
	                        scrutinyDetail.addColumnHeading(5, PROVIDED);
	                        scrutinyDetail.addColumnHeading(6, STATUS);
	                        scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Kitchen");

	                        int flag =0;
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

	                                kitchenRoomColor = DxfFileConstants.RESIDENTIAL_KITCHEN_ROOM_COLOR;
	                                kitchenStoreRoomColor = DxfFileConstants.RESIDENTIAL_KITCHEN_STORE_ROOM_COLOR;
	                                kitchenDiningRoomColor = DxfFileConstants.RESIDENTIAL_KITCHEN_DINING_ROOM_COLOR;

	                            if (floor.getKitchen() != null) {
	                            	List<RoomHeight> heights = new ArrayList<>();
	                                List<Measurement> kitchenRooms = floor.getKitchen().getRooms();
	                                for (Measurement kitchen : kitchenRooms) {
	                                	System.out.println("Kitchen Color Code="+kitchen.getColorCode());
	                                    if (heightOfRoomFeaturesColor.get(kitchenRoomColor) == kitchen.getColorCode()) {
	            	                        
	                                        kitchenAreas.add(kitchen.getArea());
	                                        kitchenWidths.add(kitchen.getWidth());
	                                        if(floor.getKitchen().getHeights()!=null) {
	                                        	heights.addAll(floor.getKitchen().getHeights());
	                                        }
	                                        if (heights != null && !heights.isEmpty()) {
	                        					for (RoomHeight roomHeight : heights) {
	                        						kitchenHeights.add(roomHeight.getHeight());
	                        					}
	                        				}
	                                    }
	                                    else if (heightOfRoomFeaturesColor.get(kitchenStoreRoomColor) == kitchen.getColorCode()) {
	                                        kitchenStoreAreas.add(kitchen.getArea());
	                                        kitchenStoreWidths.add(kitchen.getWidth());
	                                        if(floor.getKitchen().getHeights()!=null) {
	                                        	heights.addAll(floor.getKitchen().getHeights());
	                                        }
	                                        if (heights != null && !heights.isEmpty()) {
	                        					for (RoomHeight roomHeight : heights) {
	                        						kitchenStoreHeights.add(roomHeight.getHeight());
	                        					}
	                        				}
	                                    }
	                                    else if (heightOfRoomFeaturesColor.get(kitchenDiningRoomColor) == kitchen.getColorCode()) {
	                                        kitchenDiningAreas.add(kitchen.getArea());
	                                        kitchenDiningWidths.add(kitchen.getWidth());
	                                        if(floor.getKitchen().getHeights()!=null) {
	                                        	heights.addAll(floor.getKitchen().getHeights());
	                                        }
	                                        if (heights != null && !heights.isEmpty()) {
	                        					for (RoomHeight roomHeight : heights) {
	                        						kitchenDiningHeights.add(roomHeight.getHeight());
	                        					}
	                        				}
	                                    }
	                                    else 
	                                    	return pl;
	                                }

	                             if (!kitchenHeights.isEmpty()) {
	                                    BigDecimal minHeight = kitchenHeights.stream().reduce(BigDecimal::min).get();

	                                    minimumHeight = MINIMUM_HEIGHT_2_75;
	                                    subRule = RULE_25_iii;
	                                    subRuleDesc = "Kitchen Height Without Dining";

	                                    boolean valid = false;
	                                    boolean isTypicalRepititiveFloor = false;
	                                    Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
	                                            isTypicalRepititiveFloor);
	                                    buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, minHeight, valid,
	                                            typicalFloorValues,flag);
	                                } 
	                             
	                             if (!kitchenStoreHeights.isEmpty()) {
	                                    BigDecimal minHeight = kitchenStoreHeights.stream().reduce(BigDecimal::min).get();

	                                    minimumHeight = MINIMUM_HEIGHT_2_75;
	                                    subRule = RULE_25_iii;
	                                    subRuleDesc = "Kitchen Height With Store";

	                                    boolean valid = false;
	                                    boolean isTypicalRepititiveFloor = false;
	                                    Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
	                                            isTypicalRepititiveFloor);
	                                    buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, minHeight, valid,
	                                            typicalFloorValues,flag);
	                                } 
	                             
	                             if (!kitchenDiningHeights.isEmpty()) {
	                                    BigDecimal minHeight = kitchenDiningHeights.stream().reduce(BigDecimal::min).get();

	                                    minimumHeight = MINIMUM_HEIGHT_2_75;
	                                    subRule = RULE_25_iii;
	                                    subRuleDesc = "Kitchen Height With Dining";

	                                    boolean valid = false;
	                                    boolean isTypicalRepititiveFloor = false;
	                                    Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
	                                            isTypicalRepititiveFloor);
	                                    buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, minHeight, valid,
	                                            typicalFloorValues,flag);
	                                } 

	                            
	                            subRule = RULE_25_iii;

	                            if (!kitchenAreas.isEmpty()) {
	                            	flag = 1;
	                                totalArea = kitchenAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	                                minimumHeight = MINIMUM_AREA_5_6;
	                                subRuleDesc = String.format("Kitchen Floor Area Without Dining", KITCHEN);

	                                boolean valid = false;
	                                boolean isTypicalRepititiveFloor = false;
	                                Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
	                                        isTypicalRepititiveFloor);
	                                buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, totalArea, valid, typicalFloorValues,flag);
	                                flag=0;

	                            }

	                            if (!kitchenWidths.isEmpty()) {
	                                boolean valid = false;
	                                boolean isTypicalRepititiveFloor = false;
	                                Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
	                                        isTypicalRepititiveFloor);
	                                BigDecimal minRoomWidth = kitchenWidths.stream().reduce(BigDecimal::min).get();
	                                minWidth = MINIMUM_WIDTH_1_8;
	                                subRuleDesc = String.format("Kitchen Width Without Dining", KITCHEN);
	                                buildResult(pl, floor, minWidth, subRule, subRuleDesc, minRoomWidth, valid, typicalFloorValues,flag);
	                            }

	                            if (!kitchenStoreAreas.isEmpty()) {
	                            	flag = 1;
	                                totalArea = kitchenStoreAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	                                minimumHeight = MINIMUM_AREA_4_8;
	                                subRuleDesc = String.format("Kitchen Floor Area With Store", KITCHEN_STORE);

	                                boolean valid = false;
	                                boolean isTypicalRepititiveFloor = false;
	                                Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
	                                        isTypicalRepititiveFloor);
	                                buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, totalArea, valid, typicalFloorValues,flag);
	                                flag=0;

	                            }

	                            if (!kitchenStoreWidths.isEmpty()) {
	                                boolean valid = false;
	                                boolean isTypicalRepititiveFloor = false;
	                                Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
	                                        isTypicalRepititiveFloor);
	                                BigDecimal minRoomWidth = kitchenStoreWidths.stream().reduce(BigDecimal::min).get();
	                                minWidth = MINIMUM_WIDTH_1_8;
	                                subRuleDesc = String.format("Kitchen Width With Store", KITCHEN_STORE);
	                                buildResult(pl, floor, minWidth, subRule, subRuleDesc, minRoomWidth, valid, typicalFloorValues,flag);
	                            }

	                            if (!kitchenDiningAreas.isEmpty()) {
	                            	flag = 1;
	                                totalArea = kitchenDiningAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	                                minimumHeight = MINIMUM_AREA_9_5;
	                                subRuleDesc = String.format("Kitchen Floor Area With Dining", KITCHEN_DINING);

	                                boolean valid = false;
	                                boolean isTypicalRepititiveFloor = false;
	                                Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
	                                        isTypicalRepititiveFloor);
	                                buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, totalArea, valid, typicalFloorValues,flag);
	                                flag=0;

	                            }

	                            if (!kitchenDiningWidths.isEmpty()) {
	                                boolean valid = false;
	                                boolean isTypicalRepititiveFloor = false;
	                                Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
	                                        isTypicalRepititiveFloor);
	                                BigDecimal minRoomWidth = kitchenDiningWidths.stream().reduce(BigDecimal::min).get();
	                                minWidth = MINIMUM_WIDTH_2_4;
	                                subRuleDesc = String.format("Kitchen Width With Dining", KITCHEN_DINING);
	                                buildResult(pl, floor, minWidth, subRule, subRuleDesc, minRoomWidth, valid, typicalFloorValues,flag);
	                            }
	                            }else{
	                            	pl.addError("KitchenError", "Kitchen Not Defined in floor "+floor.getNumber());	                        }
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
		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
			return pl;
		}
		validate(pl);

		return pl;

	}

	 private void buildResult(Plan pl, Floor floor, BigDecimal expected, String subRule, String subRuleDesc,
	            BigDecimal actual, boolean valid, Map<String, Object> typicalFloorValues,int flag) {
	    	String newactual="";
	    	 actual = actual.setScale(2, BigDecimal.ROUND_HALF_EVEN); 
	        if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")
	                && expected.compareTo(BigDecimal.valueOf(0)) > 0 &&
	                subRule != null && subRuleDesc != null) {
	            if (actual.compareTo(expected) >= 0) {
	                valid = true;
	            }
	            if(flag==1)
	            	newactual=" m²";
	            else 
	            	newactual = " m";
	            String value = typicalFloorValues.get("typicalFloors") != null
	                    ? (String) typicalFloorValues.get("typicalFloors")
	                    : " floor " + floor.getNumber();
	            if (valid) {
	                setReportOutputDetails(pl, subRule, subRuleDesc, value,
	                        expected.toString(),
	                        actual.toString() + newactual , Result.Accepted.getResultVal());
	            } else {
	                setReportOutputDetails(pl, subRule, subRuleDesc, value,
	                        expected.toString(),
	                        actual.toString() + newactual, Result.Not_Accepted.getResultVal());
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
