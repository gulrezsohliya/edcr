package org.egov.client.edcr;

//from  DxfFileConstants
import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.NotifiedRoad;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.BuildingHeight;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class BuildingHeight_Tripura extends BuildingHeight {

	public static final BigDecimal HEIGHT_04_5 = BigDecimal.valueOf(4.5);
	public static final BigDecimal HEIGHT_06 = BigDecimal.valueOf(6);
	public static final BigDecimal HEIGHT_09 = BigDecimal.valueOf(9);
	public static final BigDecimal HEIGHT_12 = BigDecimal.valueOf(12);
	public static final BigDecimal HEIGHT_14 = BigDecimal.valueOf(14);
	public static final BigDecimal HEIGHT_15 = BigDecimal.valueOf(15);
	public static final BigDecimal HEIGHT_18 = BigDecimal.valueOf(18);
	public static final BigDecimal HEIGHT_20 = BigDecimal.valueOf(20);
	public static final BigDecimal HEIGHT_25 = BigDecimal.valueOf(25);
	public static final BigDecimal HEIGHT_26 = BigDecimal.valueOf(26);
	public static final BigDecimal HEIGHT_30 = BigDecimal.valueOf(30);
	public static final BigDecimal HEIGHT_37 = BigDecimal.valueOf(37);
	public static final BigDecimal HEIGHT_50 = BigDecimal.valueOf(50);
	public static final BigDecimal HEIGHT_11 = BigDecimal.valueOf(11);
	public static final BigDecimal HEIGHT_13 = BigDecimal.valueOf(13);
	public static final BigDecimal HEIGHT_16 = BigDecimal.valueOf(16);
	public static final BigDecimal HEIGHT_19 = BigDecimal.valueOf(19);
	public static final BigDecimal HEIGHT_22 = BigDecimal.valueOf(22);
	public static final BigDecimal HEIGHT_8POINT4 = BigDecimal.valueOf(8.4);
	public static final BigDecimal HEIGHT_14POINT4 = BigDecimal.valueOf(14.4);
	public static final BigDecimal HEIGHT_17POINT4 = BigDecimal.valueOf(17.4);

	private static final String HEIGHT_BUILDING = "Maximum height of building allowed...";
	private static final String RULE_47_3 = "47(3)";
	private static final String BUILDING_HEIGHT = "Building Height";

	private static final BigDecimal HEIGHT_8 = BigDecimal.valueOf(8);
	private static final BigDecimal HEIGHT_12_5 = BigDecimal.valueOf(12.5);
	private static final BigDecimal HEIGHT_14_5 = BigDecimal.valueOf(14.5);
	private static final BigDecimal HEIGHT_17_5 = BigDecimal.valueOf(17.5);
	private static final BigDecimal HEIGHT_20_5 = BigDecimal.valueOf(20.5);

	@Override
	public Plan validate(Plan pl) {
		try {
			Boolean eavesExist = false;
			Boolean ridgeExist = false;
			BigDecimal eavesHeight = BigDecimal.ZERO;
			BigDecimal ridgeHeight = BigDecimal.ZERO;
			BigDecimal bldgHt = null;
			for (Block b : pl.getBlocks()) {
				if (b.getBuilding().getEavesHeight() != null) {
					eavesHeight = b.getBuilding().getEavesHeight();
					eavesExist = true;

				}
				if (b.getBuilding().getRidgeHeight() != null) {
					ridgeHeight = b.getBuilding().getRidgeHeight();
					ridgeExist = true;
				}
			}
			
			
			

			HashMap<String, String> errors = new HashMap<>();
			BigDecimal accessWidth = null;
			Boolean mainRoadExist = false;

			if (pl.getNotifiedRoads() != null) {
				for (NotifiedRoad n : pl.getNotifiedRoads()) {
					if (n.getWidth() != null) {
						if (n.getColorCode() == 4) {
							mainRoadExist = true;
							accessWidth = n.getWidth();
						}
					}
				}
			}
			if (mainRoadExist == false) {
				pl.addError("MainRoadError", "Please provide color code = 4 for Notified Road for main road");
			}

			if (accessWidth == null) {
				errors.put("BuildingHeightError",
						"Access Width Not Defined. Please provide notified road with color code =4");
				pl.addErrors(errors);
			}
			int blocks=0;
			for(Block block : pl.getBlocks()) {
				blocks=blocks+1;
			}
			BigDecimal internalWidth=BigDecimal.ZERO;
			if (pl.getNotifiedRoads() != null) {
				for (NotifiedRoad n : pl.getNotifiedRoads()) {
					if (n.getWidth() != null ) {
						if (n.getColorCode() == 5) {
							internalWidth=n.getWidth();
						}
						
					}
				}
			}
			
			
			for (Block block : pl.getBlocks()) {
				BigDecimal minHeight = BigDecimal.ZERO;
//				if (block.getStairCovers() != null && !block.getStairCovers().isEmpty()) {
//					minHeight = block.getStairCovers().stream().reduce(BigDecimal::min).get();
//					block.getBuilding().setBuildingHeight(block.getBuilding().getBuildingHeight().subtract(minHeight));
//				}
				scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, "Access Width");
				if (eavesExist)
					scrutinyDetail.addColumnHeading(4, "Eaves Height");
				if (ridgeExist)
					scrutinyDetail.addColumnHeading(5, "Ridge Height");
				scrutinyDetail.addColumnHeading(6, PERMISSIBLE.concat(" in meters"));
				scrutinyDetail.addColumnHeading(7, PROVIDED.concat(" in meters"));
				scrutinyDetail.addColumnHeading(9, STATUS);
				Boolean mixedOccupancy = Boolean.FALSE;
				int occCounter = 0;
				Boolean residentialExist = Boolean.FALSE;
				for (Occupancy occup : block.getBuilding().getOccupancies()) {
					if (occup.getTypeHelper().getType().getCode().equalsIgnoreCase(R)) {
						residentialExist = Boolean.TRUE;
					}
					occCounter = occCounter + 1;
				}
				if (occCounter > 1 && residentialExist) {
					mixedOccupancy = Boolean.TRUE;
				}
				OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
						? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
						: null;
				boolean isAccepted = false;

				scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Height of Building");
				BigDecimal plotArea = pl.getPlot().getArea();
				if (block.getBuilding() != null && (block.getBuilding().getBuildingHeight() != null
						|| block.getBuilding().getBuildingHeight().compareTo(BigDecimal.ZERO) > 0)) {
					String requiredBuildingHeight = StringUtils.EMPTY;
					BigDecimal buildingHeight = block.getBuilding().getBuildingHeight();
					if(block.getBuilding().getTotalExistingBuiltUpArea().compareTo(BigDecimal.ZERO)>0) {
						if(buildingHeight.compareTo(BigDecimal.valueOf(14.5))>0) {
							pl.addError("EistingBuildingHeightError", "No Building abpve 14m in height shall be allowed for existing building");
						}
					}
					else if(blocks>1 && internalWidth.compareTo(BigDecimal.valueOf(1.2))>=0 && internalWidth.compareTo(BigDecimal.valueOf(3.5))<0) {
						isAccepted = buildingHeight.compareTo(HEIGHT_8) <= 0;
						requiredBuildingHeight = "<= 8";
					}
					else if (mixedOccupancy) {
						if (plotArea.compareTo(BigDecimal.valueOf(100)) < 0
								&& block.getBuilding().getMaxFloor().compareTo(BigDecimal.valueOf(1)) > 0) {
							errors.put("PlotAreaError",
									"For plot having area less than 100sqm only one storey is allowed in mixed used occupancy");
							pl.addErrors(errors);
						} else if (plotArea.compareTo(BigDecimal.valueOf(40)) < 0) {
							errors.put("PlotAreaError",
									"In mixed used occupancy building less than 40 sqm is not allowed");
							pl.addErrors(errors);
						}
						if (accessWidth.compareTo(BigDecimal.valueOf(8)) == 0) {
							isAccepted = buildingHeight.compareTo(HEIGHT_14_5) <= 0;
							requiredBuildingHeight = "<= 14.5";

						} else if (accessWidth.compareTo(BigDecimal.valueOf(1.8)) >= 0
								&& accessWidth.compareTo(BigDecimal.valueOf(2.4)) <= 0) {
							isAccepted = buildingHeight.compareTo(HEIGHT_8) <= 0;
							requiredBuildingHeight = "<= 8";

						} else if (accessWidth.compareTo(BigDecimal.valueOf(2.4)) > 0
								&& accessWidth.compareTo(BigDecimal.valueOf(6)) <= 0) {
							isAccepted = buildingHeight.compareTo(HEIGHT_12_5) <= 0;
							requiredBuildingHeight = "<= 12.5";

						} else if (accessWidth.compareTo(BigDecimal.valueOf(6)) > 0
								&& accessWidth.compareTo(BigDecimal.valueOf(7.5)) <= 0) {
							isAccepted = buildingHeight.compareTo(HEIGHT_17_5) <= 0;
							requiredBuildingHeight = "<= 17.5";

						} else if (accessWidth.compareTo(BigDecimal.valueOf(7.5)) > 0
								&& accessWidth.compareTo(BigDecimal.valueOf(10)) <= 0) {
							isAccepted = buildingHeight.compareTo(HEIGHT_20_5) <= 0;
							requiredBuildingHeight = "<= 20.5";

						} else if (accessWidth.compareTo(BigDecimal.valueOf(10)) > 0) {
							isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(999)) <= 0;
							requiredBuildingHeight = "Subject to the permission of the AAI";

						} else {
							pl.addError("AccessWidthError",
									"Access Width cannot be less than 1.8. Provided access width =" + accessWidth);
						}
					} else {
						if (mostRestrictiveOccupancy.getType() != null
								&& mostRestrictiveOccupancy.getType().getCode().equals(R)) {
							if (accessWidth.compareTo(BigDecimal.valueOf(1.8)) >= 0
									&& accessWidth.compareTo(BigDecimal.valueOf(2.4)) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_8) <= 0;
								requiredBuildingHeight = "<= 8";

							} else if (accessWidth.compareTo(BigDecimal.valueOf(2.4)) > 0
									&& accessWidth.compareTo(BigDecimal.valueOf(6)) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_12_5) <= 0;
								requiredBuildingHeight = "<= 12.5";

							} else if (accessWidth.compareTo(BigDecimal.valueOf(6)) > 0
									&& accessWidth.compareTo(BigDecimal.valueOf(7.5)) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_17_5) <= 0;
								requiredBuildingHeight = "<= 17.5";

							} else if (accessWidth.compareTo(BigDecimal.valueOf(7.5)) > 0
									&& accessWidth.compareTo(BigDecimal.valueOf(10)) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_20_5) <= 0;
								requiredBuildingHeight = "<= 20.5";

							} else if (accessWidth.compareTo(BigDecimal.valueOf(10)) > 0) {
								isAccepted = true;
								requiredBuildingHeight = "Subject to the permission of the AAI";

							} else {
								pl.addError("AccessWidthError",
										"Access Width cannot be less than 1.8. Provided access width =" + accessWidth);
							}
						} else if (mostRestrictiveOccupancy.getType().getCode().equals(IN)) {
							if (accessWidth.compareTo(BigDecimal.valueOf(3.5)) >= 0
									&& accessWidth.compareTo(BigDecimal.valueOf(5)) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_12_5) <= 0;
								requiredBuildingHeight = "<= 12.5";

							} else if (accessWidth.compareTo(BigDecimal.valueOf(5)) > 0
									&& accessWidth.compareTo(BigDecimal.valueOf(10)) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_14_5) <= 0;
								requiredBuildingHeight = "<= 14.5";

							} else if (accessWidth.compareTo(BigDecimal.valueOf(10)) > 0) {
								isAccepted = true;
								requiredBuildingHeight = "Subject to the permission of the AAI";

							} else {
								pl.addError("AccessWidthError",
										"Access Width cannot be less than 3.5. Provided access width =" + accessWidth);
							}
						} else {
							if (accessWidth.compareTo(BigDecimal.valueOf(1.8)) >= 0
									&& accessWidth.compareTo(BigDecimal.valueOf(2.4)) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_8) <= 0;
								requiredBuildingHeight = "<= 8";

							} else if (accessWidth.compareTo(BigDecimal.valueOf(2.4)) > 0
									&& accessWidth.compareTo(BigDecimal.valueOf(6)) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_12_5) <= 0;
								requiredBuildingHeight = "<= 12.5";

							} else if (accessWidth.compareTo(BigDecimal.valueOf(6)) > 0
									&& accessWidth.compareTo(BigDecimal.valueOf(10)) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_14_5) <= 0;
								requiredBuildingHeight = "<= 14.5";

							} else if (accessWidth.compareTo(BigDecimal.valueOf(10)) > 0) {
								isAccepted = true;
								requiredBuildingHeight = "Subject to the permission of the AAI";

							} else {
								pl.addError("AccessWidthError",
										"Access Width cannot be less than 1.8. Provided access width =" + accessWidth);
							}
						}
					}

					if (errors.isEmpty() && StringUtils.isNotBlank(requiredBuildingHeight)) {
						buildResult(pl, requiredBuildingHeight, buildingHeight, isAccepted, accessWidth);
					}

				} else {
					errors.put(BUILDING_HEIGHT + block.getNumber(), getLocaleMessage(DcrConstants.OBJECTNOTDEFINED,
							BUILDING_HEIGHT + " for block " + block.getNumber()));
					pl.addErrors(errors);
				}
//				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;
	}

	private void buildResult(Plan pl, String requiredBuildingHeight, BigDecimal buildingHeight, boolean isAccepted,
			BigDecimal accesswidth) {

		Boolean eavesExist = false;
		Boolean ridgeExist = false;
		BigDecimal eavesHeight = BigDecimal.ZERO;
		BigDecimal ridgeHeight = BigDecimal.ZERO;
		BigDecimal bldgHt = null;
		for (Block b : pl.getBlocks()) {
			if (b.getBuilding().getEavesHeight() != null) {
				eavesHeight = b.getBuilding().getEavesHeight();
				eavesExist = true;

			}
			if (b.getBuilding().getRidgeHeight() != null) {
				ridgeHeight = b.getBuilding().getRidgeHeight();
				ridgeExist = true;
			}
		}
		String ruleNo = RULE_47_3;
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, HEIGHT_BUILDING);
		details.put("Access Width", accesswidth.setScale(2, RoundingMode.DOWN).toString());
		if (eavesExist)
			details.put("Eaves Height", eavesHeight.setScale(2, RoundingMode.DOWN).toString());
		if (ridgeExist)
			details.put("Ridge Height", ridgeHeight.setScale(2, RoundingMode.DOWN).toString());
		details.put(PERMISSIBLE.concat(" in meters"), requiredBuildingHeight);
		details.put(PROVIDED.concat(" in meters"), String.valueOf(buildingHeight));
		details.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

	}

	@Override
	public Plan process(Plan Plan) {
		try {
			validate(Plan);
		} catch (Exception e) {
		}
		return Plan;
	}

}
