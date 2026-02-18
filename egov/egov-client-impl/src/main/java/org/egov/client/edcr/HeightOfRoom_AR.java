package org.egov.client.edcr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
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
public class HeightOfRoom_AR extends HeightOfRoom {
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
	private static final Logger LOG = Logger.getLogger(HeightOfRoom_AR.class);

	@Override
	public Plan validate(Plan pl) {
		
		
		  scrutinyDetail = new ScrutinyDetail(); 
		  scrutinyDetail.addColumnHeading(1,RULE_NO); scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		  scrutinyDetail.addColumnHeading(3, FLOOR); 
		  scrutinyDetail.addColumnHeading(4,ROOM); 
		  scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
		  scrutinyDetail.addColumnHeading(6, PROVIDED);
		  scrutinyDetail.addColumnHeading(7, STATUS);
		  
		  HashMap<String, String> errors = new HashMap<>();
		  
		  Map<String, Integer> heightOfRoomFeaturesColor =pl.getSubFeatureColorCodesMaster().get("HeightOfRoom"); 
		  OccupancyTypeHelper occupancyType = new OccupancyTypeHelper(); 
		  if (pl.getBlocks() != null && !pl.getBlocks().isEmpty()) { 
			  OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null ?pl.getVirtualBuilding().getMostRestrictiveFarHelper() : null; 
			  for (Block blk: pl.getBlocks()) { 
				  if (blk.getBuilding() != null) { 
						  			occupancyType = blk.getBuilding().getMostRestrictiveFarHelper();
						  					validateResidentialRoomHeight(pl, blk, occupancyType, heightOfRoomFeaturesColor,errors); 
				  }
			}
		  }
		  								if (!errors.isEmpty()) pl.addErrors(errors);
		  										return pl;
	}

	@Override
	public Plan process(Plan pl) {
		validate(pl);
		return pl;
	}

	
	private Boolean validateRegularRoomsHeight(Plan pl, Map<String, Integer> heightOfRoomFeaturesColor, String blockno,
			Floor floor, String color, String rule, String ruleDesc, BigDecimal permissbleMinHeight,
			BigDecimal permissbleMaxHeight, Map<String, String> errors) {
		Boolean stiltExist=false;
		if(floor.getParking().getStilts()!=null && !floor.getParking().getStilts().isEmpty()) {
			stiltExist=true;
		}
		
		
		String actual = "", expected = "";
		String floorNo = floor.getNumber().toString();

		List<BigDecimal> roomAreas = new ArrayList<>();
		List<BigDecimal> roomWidths = new ArrayList<>();

		boolean skipValidation = false;

		if (floor.getRegularRooms() != null && floor.getRegularRooms().size() > 0) {
			for (Room room : floor.getRegularRooms()) {
				String roomNo = room.getNumber();
				List<BigDecimal> floorRoomHeights = new ArrayList<>();
				List<RoomHeight> heights = new ArrayList<>();
				List<Measurement> rooms = new ArrayList<>();

				if (room.getHeights() != null)
					heights.addAll(room.getHeights());
				if (room.getRooms() != null)
					rooms.addAll(room.getRooms());

				if (heights != null && !heights.isEmpty()) {
					for (RoomHeight roomHeight : heights) {
						floorRoomHeights.add(roomHeight.getHeight());
					}
				}
				
				if (!skipValidation) {
					if (floorRoomHeights != null && !floorRoomHeights.isEmpty()) {
						BigDecimal minHeight = floorRoomHeights.stream().reduce(BigDecimal::min).get();
						boolean valid = false, valid1 = false;
							
						if (permissbleMinHeight != null && permissbleMinHeight.compareTo(BigDecimal.ZERO) > 0) {
							ruleDesc = RULE_DESC_MINIMUM_ROOMHEIGHT_DESC;
							expected = String.format("height >= %s %s", permissbleMinHeight, " m");
							actual = String.format("%s %s", minHeight.setScale(2, RoundingMode.DOWN).toString(), " m");
							if (minHeight.compareTo(permissbleMinHeight) >= 0 && minHeight.compareTo(BigDecimal.valueOf(4))<=0) {
								valid = true;
							}
							addScrutinyDetails(scrutinyDetail, rule, ruleDesc, expected, actual, floorNo, roomNo,
									valid);
						}
						

					} else {
						String layerName = String.format(LAYER_ROOM_HEIGHT, blockno, floor.getNumber(),
								"REGULAR_ROOM_" + room.getNumber());
						errors.put(layerName, ROOM_HEIGHT_NOTDEFINED + layerName);
					}
				}
			}

		} else {
			if(!stiltExist) {
				LOG.info("No Regular Rooms Present");
				String layerName = String.format(LAYER_ROOM_HEIGHT, blockno, floor.getNumber(), "REGULAR_ROOM_");
				errors.put(layerName, ROOM_HEIGHT_NOTDEFINED + layerName);
			}
			
		}
		return skipValidation;
	}



	private void validateResidentialRoomHeight(Plan pl, Block blk, OccupancyTypeHelper mostRestrictiveOccupancy,
			Map<String, Integer> heightOfRoomFeaturesColor, HashMap<String, String> errors) {
		LOG.info("validateResidentialRoomHeight");

		String blockNo = blk.getNumber();

		boolean isRuleDefined = false, isOccupancyMatched = false;

		String color = "Residential";
		String rule = RULE_NO_ROOMHEIGHT, ruleDesc = ROOMHEIGHT_DESC;

		if (blk.getBuilding().getFloors() != null && !blk.getBuilding().getFloors().isEmpty()) {
			List<Floor> floors = blk.getBuilding().getFloors().stream().filter(f -> f != null && f.getNumber() >= 0)
					.collect(Collectors.toList());

			BigDecimal minHeight = BigDecimal.ZERO, maxHeight = BigDecimal.ZERO;

			/* 1. Plotted Housing ◦ Min 3m */
			rule = "Section 4.3";

			isRuleDefined = true;
			/* 2. Group Housing ◦ Min 2.75m */
			minHeight = MINIMUM_HEIGHT_2_75;
			Boolean res = false;
			if (isRuleDefined) {
				for (Floor floor : floors) {
//						validateAcRoomsHeight(pl, heightOfRoomFeaturesColor, blockNo, floor, color, rule, ruleDesc,
//								minHeight, maxHeight, errors);
					res = validateRegularRoomsHeight(pl, heightOfRoomFeaturesColor, blockNo, floor, color, rule,
							ruleDesc, minHeight, maxHeight, errors);

				}
				if (!res)
					scrutinyDetail.setKey("Block_" + blk.getNumber() + "_" + "Room");

				if (scrutinyDetail != null && scrutinyDetail.getDetail() != null
						&& !scrutinyDetail.getDetail().isEmpty()) {
					pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
				}
			}
		} else {
			LOG.info("BLK_" + blk.getNumber() + "FLRS NOT DEFINED");
		}
	}



	private void addScrutinyDetails(ScrutinyDetail scrutinyDetail, String ruleNo, String ruleDesc, String expected,
			String actual, String floor, String roomNo, boolean valid) {
		HashMap<String, String> details = new HashMap<String, String>();
//		floor = (floor != null && floor != "") ? FloorNames.getFloorNames(Integer.valueOf(floor)) : "-";

		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(FLOOR, floor);
		details.put(ROOM, roomNo);
		details.put(PERMISSIBLE, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
		scrutinyDetail.addDetail(details);
	}

}
