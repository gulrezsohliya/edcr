package org.egov.client.edcr.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.DistanceToExternalEntity;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Footpath;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RoofArea;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.FeatureProcess;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;

import antlr.collections.List;

public class Utility extends FeatureProcess {

	private static final Logger LOG = Logger.getLogger(Utility.class);

	private static final String DECLARED = "Declared";
	private static final String RULE_17 = "17";
	private static final String STABILITY_ZONE = "Building Stability Zone ";
	
	private static final String ERROR_STABILITY_ZONE_NOT_DECLARED = "STABILITY_REPORT is not declared";
	private static final String ERROR_STABILITY_ZONE_NOT_EXIST = "Invalid Building Stability Zone. Stability Zone %s does not exist";

	public static BigDecimal getBuildingStabilityZone(Plan pl) {

		HashMap<String, String> errors = new HashMap<>();
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Stability Zone");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, DECLARED);
		scrutinyDetail.addColumnHeading(4, STATUS);

		BigDecimal ZONE_DC = BigDecimal.ZERO;

		String stabilityReport = StringUtils.EMPTY, temp[] = new String[2];

		try {
			stabilityReport = new String(
					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals("STABILITY_REPORT"))
							.map(Map.Entry::getValue).findFirst().orElse("0"));
			temp = stabilityReport.split("_", 2);
			ZONE_DC = BigDecimal.valueOf(Integer.valueOf(temp[1]));
		} catch (Exception e) {
			ZONE_DC = BigDecimal.ZERO;
		}

		if (ZONE_DC.compareTo(BigDecimal.ZERO) <= 0) {
			errors.put(ERROR_STABILITY_ZONE_NOT_DECLARED, ERROR_STABILITY_ZONE_NOT_DECLARED);
			pl.addErrors(errors);
		}else {
			if (ZONE_DC.compareTo(new BigDecimal(6)) >= 0) {
				errors.put(String.format(ERROR_STABILITY_ZONE_NOT_EXIST, ZONE_DC), String.format(ERROR_STABILITY_ZONE_NOT_EXIST, ZONE_DC));
				pl.addErrors(errors);
			}
		}

		if (errors.isEmpty()) {
			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, RULE_17);
			details.put(DESCRIPTION, STABILITY_ZONE);
			details.put(DECLARED, "Zone " + ZONE_DC);
			details.put(STATUS, Result.Verify.getResultVal());
			scrutinyDetail.getDetail().add(details);
			pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		}

		return ZONE_DC;
	}

	public static BigDecimal getRoadReserve(Plan pl) {

		BigDecimal ROAD_RESERVE = BigDecimal.ZERO;

		String temp[] = new String[2], roadReserveTemp = pl.getPlanInfoProperties().entrySet().stream()
				.filter(e -> e.getKey().equals("ROAD_RESERVE")).map(Map.Entry::getValue).findFirst().orElse(null);

		if (roadReserveTemp != null && !roadReserveTemp.isEmpty()) {
			temp = roadReserveTemp.split("_", 2);
			try {
				ROAD_RESERVE = new BigDecimal(temp[0]);/* MIN_ROAD_RESERVE from planInfo */
			} catch (NumberFormatException nfe) {
				ROAD_RESERVE = BigDecimal.ZERO;
			}
		}

		return ROAD_RESERVE;
	}

	public static BigDecimal getTotalRoofArea(Plan pl) {
		HashMap<String, String> errors = new HashMap<>();

		BigDecimal totalRoofArea = BigDecimal.ZERO;

		if (pl.getBlocks() != null && !pl.getBlocks().isEmpty()) {
			for (Block block : pl.getBlocks()) {
				if (block.getBuilding().getFloors() != null && !block.getBuilding().getFloors().isEmpty()) {
					for (Floor floor : block.getBuilding().getFloors()) {
						try {
							if (floor.getRoofAreas() != null && !floor.getRoofAreas().isEmpty()) {
								totalRoofArea = totalRoofArea.add(floor.getRoofAreas().stream().map(RoofArea::getArea)
										.reduce(BigDecimal::add).get());
							}
						} catch (Exception e) {
							LOG.error("Exception :" + e);
						}
					}

					if (totalRoofArea == null || totalRoofArea.compareTo(BigDecimal.ZERO) == 0) {
						errors.put(
								String.format("Block %s roof area %s", block.getNumber(),
										DcrConstants.OBJECTNOTDEFINED),
								String.format("Block %s roof area %s", block.getNumber(),
										DcrConstants.OBJECTNOTDEFINED_DESC));
						pl.addErrors(errors);
					}
				}
			}

		}

		return totalRoofArea;
	}

	public static Boolean checkIfRoadPresent(Plan pl) {

		Boolean hasRoad = false;

		if (pl.getNonNotifiedRoads() != null && !pl.getNonNotifiedRoads().isEmpty())
			hasRoad = true;
		else if (pl.getNotifiedRoads() != null && !pl.getNotifiedRoads().isEmpty())
			hasRoad = true;
		else if (pl.getCuldeSacRoads() != null && !pl.getCuldeSacRoads().isEmpty())
			hasRoad = true;
		else if (pl.getLaneRoads() != null && !pl.getLaneRoads().isEmpty())
			hasRoad = true;
		else
			hasRoad = false;

		return hasRoad;
	}

	public static Boolean checkIfFootpathPresent(Plan pl) {

		Boolean hasFootpath = false;

		DistanceToExternalEntity distanceToExternalEntity = pl.getDistanceToExternalEntity();
		Footpath footpaths = distanceToExternalEntity.getFootpath();

		if (footpaths != null)
			hasFootpath = true;

		return hasFootpath;
	}

	public static Boolean checkIfAdjoiningBuildingsPresent(Plan pl, String adjoiningType) {
		/* To be determined from declaration */

		Boolean hasAdjoiningBuildings = false;
		try {
			String ADJOINING = new String(
					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals(adjoiningType))
							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));

			if (ADJOINING != null && !ADJOINING.isEmpty())
				hasAdjoiningBuildings = true;
		} catch (Exception e) {
			LOG.info("Error: " + e);
		}

		return hasAdjoiningBuildings;
	}

	public static BigDecimal getNoOfAdjoiningBuildings(Plan pl) {
		/* To be calculated once adjoining buildings are declared */
		BigDecimal noOfAdjoiningBuildindgs = BigDecimal.ZERO;
		try {

			String FRONT_ADJOINING = new String(
					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals("FRONT_ADJOINING"))
							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));
			String REAR_ADJOINING = new String(
					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals("REAR_ADJOINING"))
							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));
			String SIDE1_ADJOINING = new String(
					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals("SIDE1_ADJOINING"))
							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));
			String SIDE2_ADJOINING = new String(
					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals("SIDE2_ADJOINING"))
							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));

			if (FRONT_ADJOINING != null && !FRONT_ADJOINING.isEmpty())
				noOfAdjoiningBuildindgs.add(BigDecimal.ONE);
			if (REAR_ADJOINING != null && !REAR_ADJOINING.isEmpty())
				noOfAdjoiningBuildindgs.add(BigDecimal.ONE);
			if (SIDE1_ADJOINING != null && !SIDE1_ADJOINING.isEmpty())
				noOfAdjoiningBuildindgs.add(BigDecimal.ONE);
			if (SIDE2_ADJOINING != null && !SIDE2_ADJOINING.isEmpty())
				noOfAdjoiningBuildindgs.add(BigDecimal.ONE);

		} catch (Exception e) {
			LOG.info("ERROR: " + e);
		}

		return noOfAdjoiningBuildindgs;
	}

	public static Boolean checkIfAnyBatroomDefined(Block block) {
		Boolean isBathroomDefined = false;

		if (block.getSanityDetails().getCommonBathRooms() != null
				&& !block.getSanityDetails().getCommonBathRooms().isEmpty()) {
			isBathroomDefined = true;
		} else if (block.getSanityDetails().getFemaleBathRooms() != null
				&& !block.getSanityDetails().getFemaleBathRooms().isEmpty()) {
			isBathroomDefined = true;
		} else if (block.getSanityDetails().getMaleBathRooms() != null
				&& !block.getSanityDetails().getMaleBathRooms().isEmpty()) {
			isBathroomDefined = true;
		} else if (block.getSanityDetails().getCommonRoomsWithWaterCloset() != null
				&& !block.getSanityDetails().getCommonRoomsWithWaterCloset().isEmpty()) {
			isBathroomDefined = true;
		} else if (block.getSanityDetails().getFemaleRoomsWithWaterCloset() != null
				&& !block.getSanityDetails().getFemaleRoomsWithWaterCloset().isEmpty()) {
			isBathroomDefined = true;
		} else if (block.getSanityDetails().getMaleRoomsWithWaterCloset() != null
				&& !block.getSanityDetails().getMaleRoomsWithWaterCloset().isEmpty()) {
			isBathroomDefined = true;
		} else {
			/*
			 * if (block.getBuilding().getFloors() != null &&
			 * !block.getBuilding().getFloors().isEmpty()) { Floor f = new Floor(); Room
			 * bRoom = new Room(); f.setBathRoom(bRoom); isBathroomDefined =
			 * block.getBuilding().getFloors().stream().anyMatch(flr -> {
			 * 
			 * if (flr.getBathRoom() != null && flr.getBathRoom().getRooms() != null &&
			 * !flr.getBathRoom().getRooms().isEmpty() &&
			 * flr.getBathRoom().getRooms().size() > 0) { return true; }else if
			 * (flr.getBathRoomWaterClosets() != null &&
			 * flr.getBathRoomWaterClosets().getRooms() != null &&
			 * !flr.getBathRoomWaterClosets().getRooms().isEmpty() &&
			 * flr.getBathRoomWaterClosets().getRooms().size() > 0) {
			 * 
			 * LOG.info(flr.getBathRoomWaterClosets().getRooms().stream().map(Measurement::
			 * getArea).reduce(BigDecimal::add).get());
			 * 
			 * return true; } else return false; }); }else
			 */
			isBathroomDefined = false;
		}

		return isBathroomDefined;
	}

	@Override
	public Plan validate(Plan pl) {
		// TODO Auto-generated method stub
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		// TODO Auto-generated method stub
		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return null;
	}

}

/*
 * public static void Test(Plan pl) { LOG.info("TEST");
 * 
 * for (Block b : pl.getBlocks()) { LOG.info("Block " + b.getNumber() +
 * " height " + b.getHeight() + " building Height " +
 * b.getBuilding().getHeight());
 * 
 * for (Floor f : b.getBuilding().getFloors()) { f.getOccupancies().forEach(o ->
 * { LOG.info("OccupancyType - " + o.getType().getOccupancyType());
 * LOG.info("OccupancyTypeHelper ->type " +
 * o.getTypeHelper().getType().getName());
 * LOG.info("OccupancyTypeHelper ->subtype " +
 * o.getTypeHelper().getSubtype().getName()); }); } }
 * 
 * if (pl.getVirtualBuilding() != null) {
 * LOG.info("pl.getVirtualBuilding().getBuildingHeight() = " +
 * pl.getVirtualBuilding().getBuildingHeight());
 * LOG.info("pl.getVirtualBuilding().getFloorsAboveGround() = " +
 * pl.getVirtualBuilding().getFloorsAboveGround());
 * 
 * if (pl.getVirtualBuilding().getMostRestrictiveCoverage() != null) { LOG.
 * info("pl.getVirtualBuilding().getMostRestrictiveCoverage().getOccupancyType = "
 * + pl.getVirtualBuilding().getMostRestrictiveCoverage().getOccupancyType()); }
 * if (pl.getVirtualBuilding().getMostRestrictiveCoverageHelper() != null) {
 * LOG.
 * info("pl.getVirtualBuilding().getMostRestrictiveCoverageHelper().getOccupancyType = "
 * +
 * pl.getVirtualBuilding().getMostRestrictiveCoverageHelper().getType().getName(
 * )); LOG.
 * info("pl.getVirtualBuilding().getMostRestrictiveCoverageHelper().getOccupancyType = "
 * + pl.getVirtualBuilding().getMostRestrictiveCoverageHelper().getSubtype().
 * getName()); } if (pl.getVirtualBuilding().getMostRestrictiveFar() != null) {
 * LOG.
 * info("pl.getVirtualBuilding().getMostRestrictiveFar().getOccupancyType = " +
 * pl.getVirtualBuilding().getMostRestrictiveFar().getOccupancyType()); } if
 * (pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null) { LOG.
 * info("pl.getVirtualBuilding().getMostRestrictiveFarHelper().getOccupancyType = "
 * + pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getName());
 * LOG.
 * info("pl.getVirtualBuilding().getMostRestrictiveFarHelper().getOccupancyType = "
 * +
 * pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getName())
 * ; }
 * 
 * } else { LOG.info("Virtual isNUll"); } LOG.info("END TEST"); }
 */
