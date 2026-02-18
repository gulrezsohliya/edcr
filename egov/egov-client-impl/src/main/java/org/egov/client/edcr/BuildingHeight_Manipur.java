package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.A2;

import static org.egov.edcr.constants.DxfFileConstants.A_FH;
import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.constants.DxfFileConstants.A_SA;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_HE;

import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.B2;
import static org.egov.edcr.constants.DxfFileConstants.C;
import static org.egov.edcr.constants.DxfFileConstants.B_PS;
import static org.egov.edcr.constants.DxfFileConstants.D_A;
import static org.egov.edcr.constants.DxfFileConstants.D_B;
import static org.egov.edcr.constants.DxfFileConstants.D_C;
import static org.egov.edcr.constants.DxfFileConstants.E_CLG;
import static org.egov.edcr.constants.DxfFileConstants.E_EARC;
import static org.egov.edcr.constants.DxfFileConstants.E_NS;
import static org.egov.edcr.constants.DxfFileConstants.E_PS;
import static org.egov.edcr.constants.DxfFileConstants.E_SACA;
import static org.egov.edcr.constants.DxfFileConstants.E_SFDAP;
import static org.egov.edcr.constants.DxfFileConstants.E_SFMC;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.F_CB;
import static org.egov.edcr.constants.DxfFileConstants.F_H;
import static org.egov.edcr.constants.DxfFileConstants.G;

import static org.egov.edcr.constants.DxfFileConstants.H_PP;
import static org.egov.edcr.constants.DxfFileConstants.M_DFPAB;
import static org.egov.edcr.constants.DxfFileConstants.M_HOTHC;
import static org.egov.edcr.constants.DxfFileConstants.M_NAPI;
import static org.egov.edcr.constants.DxfFileConstants.M_OHF;
import static org.egov.edcr.constants.DxfFileConstants.M_VH;
import static org.egov.edcr.constants.DxfFileConstants.S_BH;
import static org.egov.edcr.constants.DxfFileConstants.S_CA;
import static org.egov.edcr.constants.DxfFileConstants.S_CRC;
import static org.egov.edcr.constants.DxfFileConstants.S_ECFG;
import static org.egov.edcr.constants.DxfFileConstants.S_ICC;
import static org.egov.edcr.constants.DxfFileConstants.S_MCH;
import static org.egov.edcr.constants.DxfFileConstants.S_SAS;
import static org.egov.edcr.constants.DxfFileConstants.S_SC;
import static org.egov.edcr.constants.DxfFileConstants.F_RT;
//from  DxfFileConstants
import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.BuildingHeight;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.jfree.util.Log;
import org.springframework.stereotype.Service;

@Service
public class BuildingHeight_Manipur extends BuildingHeight {

	private static final Logger LOG = Logger.getLogger(BuildingHeight_Manipur.class);

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

	// AREA for AP
	private static final BigDecimal PLOTEA_48 = BigDecimal.valueOf(48);
	private static final BigDecimal PLOTEA_90 = BigDecimal.valueOf(90);
	private static final BigDecimal PLOTEA_150 = BigDecimal.valueOf(150);
	private static final BigDecimal PLOTEA_300 = BigDecimal.valueOf(300);
	private static final BigDecimal PLOTEA_500 = BigDecimal.valueOf(500);
	private static final BigDecimal PLOTEA_1000 = BigDecimal.valueOf(1000);
	private static final BigDecimal PLOTEA_1500 = BigDecimal.valueOf(1500);
	private static final BigDecimal PLOTEA_750 = BigDecimal.valueOf(750);
	private static final BigDecimal PLOTEA_2500 = BigDecimal.valueOf(2500);
	private static final BigDecimal PLOTEA_2000 = BigDecimal.valueOf(2000);
	private static final BigDecimal PLOTEA_4000 = BigDecimal.valueOf(4000);
	private static final BigDecimal PLOTEA_12000 = BigDecimal.valueOf(12000);
	private static final BigDecimal PLOTEA_28000 = BigDecimal.valueOf(28000);
	private static final BigDecimal PLOTEA_6000 = BigDecimal.valueOf(6000);
	private static final BigDecimal PLOTEA_20000 = BigDecimal.valueOf(20000);
	private static final BigDecimal PLOTEA_10000 = BigDecimal.valueOf(10000);
	private static final BigDecimal PLOTEA_1080 = BigDecimal.valueOf(1080);
	private static final BigDecimal PLOTEA_510 = BigDecimal.valueOf(510);
	private static final BigDecimal PLOTEA_400 = BigDecimal.valueOf(400);
	private static final BigDecimal PLOT_WIDTH_8 = BigDecimal.valueOf(8);

	private static final String HEIGHT_BUILDING = "Maximum height of building allowed...";
	private static final String ZONE = "Zone";
	private static final String RULE_54_D = "54-D";
	private static final String BUILDING_HEIGHT = "Building Height";

	@Override
	public Plan validate(Plan pl) {
		String OccupancyType = pl.getPlanInformation().getLandUseZone().toUpperCase();
		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(5, PERMISSIBLE.concat(" in meters"));
		scrutinyDetail.addColumnHeading(6, PROVIDED.concat(" in meters"));
		scrutinyDetail.addColumnHeading(7, STATUS);

		HashMap<String, String> errors = new HashMap<>();

		BigDecimal PlotArea = pl.getPlanInformation().getPlotArea();
		BigDecimal PlotWidth = pl.getPlanInformation().getWidthOfPlot();
		for (Block block : pl.getBlocks()) {
			List<OccupancyTypeHelper> plotWiseOccupancyTypes = new ArrayList<>();

			for (Occupancy occupancy : block.getBuilding().getOccupancies()) {
				if (occupancy.getTypeHelper().getType() != null) {
					if (occupancy.getTypeHelper().getType() != null
							&& occupancy.getTypeHelper().getType().getCode() != null
							&& !occupancy.getTypeHelper().getType().getCode().isEmpty()
							&& !occupancy.getTypeHelper().getType().getName().isEmpty()
							&& occupancy.getTypeHelper().getType().getName() != null) {

					}
				}
				boolean isAccepted = false;
				Boolean isTDR = false;
				if (pl.getPlanInfoProperties().containsKey("TDR")) {
					if (pl.getPlanInfoProperties().get("TDR").equalsIgnoreCase("YES")) {
						isTDR = true;
					}
				}
				Boolean isCbdTod = false;
				BigDecimal roadWidth = BigDecimal.ZERO;
				if (pl.getPlanInfoProperties().containsKey("CBD_TOD")) {
					if (pl.getPlanInfoProperties().get("CBD_TOD").equalsIgnoreCase("YES")) {
						isCbdTod = true;
					}
				}

				if(pl.getPlanInformation().getRoadWidth()!=null) {
					roadWidth = pl.getPlanInformation().getRoadWidth();
				}

				scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Height of Building");

				if (block.getBuilding() != null && block.getBuilding().getBuildingHeight() != null
						&& block.getBuilding().getBuildingHeight().compareTo(BigDecimal.ZERO) > 0) {
					String requiredBuildingHeight = StringUtils.EMPTY;
					BigDecimal buildingHeight = block.getBuilding().getBuildingHeight();

					if (occupancy.getTypeHelper().getType() != null
							&& occupancy.getTypeHelper().getType().getCode().equals(R)) {

						if (occupancy.getTypeHelper().getSubtype().getCode().equals(R1)
								|| occupancy.getTypeHelper().getSubtype().getCode().equals(R2)) {
							if (PlotArea.compareTo(BigDecimal.valueOf(50)) >= 0 && PlotArea.compareTo(PLOTEA_90) <= 0) {
								isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(7)) <= 0;
								requiredBuildingHeight = "<= 7";

							}
							else if (PlotArea.compareTo(PLOTEA_90) > 0 && PlotArea.compareTo(PLOTEA_150) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_11) <= 0;
								requiredBuildingHeight = "<= 11";

							} else if (PlotArea.compareTo(PLOTEA_150) > 0 && PlotArea.compareTo(PLOTEA_300) <= 0) {

								isAccepted = buildingHeight.compareTo(HEIGHT_11) <= 0;
								requiredBuildingHeight = "<= 11";

							} else if (PlotArea.compareTo(PLOTEA_300) >= 0 && PlotArea.compareTo(PLOTEA_500) < 0) {

								isAccepted = buildingHeight.compareTo(HEIGHT_13) <= 0;
								requiredBuildingHeight = "<= 13";

							} else if (PlotArea.compareTo(PLOTEA_500) > 0 && PlotArea.compareTo(PLOTEA_750) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_14) <= 0;
								requiredBuildingHeight = "<= 14";

							} else if (PlotArea.compareTo(PLOTEA_750) > 0 && PlotArea.compareTo(PLOTEA_1000) <= 0) {

								isAccepted = buildingHeight.compareTo(HEIGHT_16) <= 0;
								requiredBuildingHeight = "<= 16";
							} else if (PlotArea.compareTo(PLOTEA_1000) > 0 && PlotArea.compareTo(PLOTEA_1500) <= 0) {

								isAccepted = buildingHeight.compareTo(HEIGHT_19) <= 0;
								requiredBuildingHeight = "<= 19";

							} else if (PlotArea.compareTo(PLOTEA_1500) > 0 && PlotArea.compareTo(PLOTEA_2000) <= 0) {

								isAccepted = buildingHeight.compareTo(HEIGHT_22) <= 0;
								requiredBuildingHeight = "<= 22";

							} else if (PlotArea.compareTo(PLOTEA_2000) > 0 && PlotArea.compareTo(PLOTEA_2500) <= 0) {

								isAccepted = buildingHeight.compareTo(HEIGHT_25) <= 0;
								requiredBuildingHeight = "<= 25";

							} else {
								pl.addError("Building Height Area",
										"Building Height: Plot Area less than 90 m or greater than 2500 m");
							}

						}

					} else if (occupancy.getTypeHelper().getType() != null
							&& occupancy.getTypeHelper().getType().getCode().equals(C)) {
						if (isCbdTod) {
							if (isTDR) {
								if (roadWidth.compareTo(BigDecimal.valueOf(30)) > 0) {
									if (PlotArea.compareTo(PLOTEA_1000) > 0) {
										isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(40)) <= 0;
										requiredBuildingHeight = "<= 40";
									}
								} else {
									if (PlotArea.compareTo(PLOTEA_1000) > 0) {
										isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(19)) <= 0;
										requiredBuildingHeight = "<= 19";
									}
								}
							} else {
								if (roadWidth.compareTo(BigDecimal.valueOf(30)) > 0) {
									if (PlotArea.compareTo(PLOTEA_1000) > 0) {
										isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(27)) <= 0;
										requiredBuildingHeight = "<= 27";
									}
								} else {
									if (PlotArea.compareTo(PLOTEA_1000) > 0) {
										isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(19)) <= 0;
										requiredBuildingHeight = "<= 19";
									}
								}
							}

						} else {
							if (PlotArea.compareTo(PLOTEA_90) >= 0 && PlotArea.compareTo(PLOTEA_150) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_11) <= 0;
								requiredBuildingHeight = "<= 11";

							} else if (PlotArea.compareTo(PLOTEA_150) > 0 && PlotArea.compareTo(PLOTEA_300) <= 0) {

								isAccepted = buildingHeight.compareTo(HEIGHT_11) <= 0;
								requiredBuildingHeight = "<= 11";

							} else if (PlotArea.compareTo(PLOTEA_300) >= 0 && PlotArea.compareTo(PLOTEA_500) < 0) {

								isAccepted = buildingHeight.compareTo(HEIGHT_13) <= 0;
								requiredBuildingHeight = "<= 13";

							} else if (PlotArea.compareTo(PLOTEA_500) > 0 && PlotArea.compareTo(PLOTEA_750) <= 0) {
								isAccepted = buildingHeight.compareTo(HEIGHT_13) <= 0;
								requiredBuildingHeight = "<= 13";

							} else if (PlotArea.compareTo(PLOTEA_750) > 0 && PlotArea.compareTo(PLOTEA_1000) <= 0) {

								isAccepted = buildingHeight.compareTo(HEIGHT_16) <= 0;
								requiredBuildingHeight = "<= 16";
							} else {
								if (roadWidth.compareTo(BigDecimal.valueOf(30)) < 0) {
									if (PlotArea.compareTo(PLOTEA_1000) > 0) {
										isAccepted = buildingHeight.compareTo(HEIGHT_19) <= 0;
										requiredBuildingHeight = "<= 19";
									}
								} else {
									if (PlotArea.compareTo(PLOTEA_1000) > 0) {
										isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(27)) <= 0;
										requiredBuildingHeight = "<= 27";
									}
								}

							}
						}
					} else if (occupancy.getTypeHelper().getType() != null
							&& occupancy.getTypeHelper().getType().getCode().equals(IN)) {
						if (PlotArea.compareTo(PLOTEA_750) >= 0 && PlotArea.compareTo(PLOTEA_1000) <= 0) {
							isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(16)) <= 0;
							requiredBuildingHeight = "<= 16";
						} else if (PlotArea.compareTo(PLOTEA_1000) > 0 && PlotArea.compareTo(PLOTEA_1500) <= 0) {
							isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(19)) <= 0;
							requiredBuildingHeight = "<= 19";
						} else if (PlotArea.compareTo(PLOTEA_1500) > 0) {
							isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(22)) <= 0;
							requiredBuildingHeight = "<= 22";
						}
					} else if (occupancy.getTypeHelper().getType() != null
							&& occupancy.getTypeHelper().getType().getCode().equals(I)) {
						if (occupancy.getTypeHelper().getSubtype().getCode().equals(I1)) {
							if (PlotArea.compareTo(PLOTEA_300) >= 0) {
								isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(10)) <= 0;
								requiredBuildingHeight = "<= 10";
							} else if (PlotArea.compareTo(PLOTEA_1500) > 0) {
								isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(13)) <= 0;
								requiredBuildingHeight = "<= 13";

							}

						} else if (occupancy.getTypeHelper().getSubtype().getCode().equals(I2)) {
							if (PlotArea.compareTo(PLOTEA_1000) > 0 && PlotArea.compareTo(PLOTEA_2000) <= 0) {
								isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(13)) <= 0;
								requiredBuildingHeight = "<= 13";
							} else if (PlotArea.compareTo(PLOTEA_2000) > 0) {
								isAccepted = buildingHeight.compareTo(BigDecimal.valueOf(13)) <= 0;
								requiredBuildingHeight = "<= 13";

							}

						}

					}

					if (errors.isEmpty() && StringUtils.isNotBlank(requiredBuildingHeight)) {
						buildResult(pl, requiredBuildingHeight, buildingHeight, isAccepted);
					}

				} else {
					errors.put(BUILDING_HEIGHT + block.getNumber(), getLocaleMessage(DcrConstants.OBJECTNOTDEFINED,
							BUILDING_HEIGHT + " for block " + block.getNumber()));
					pl.addErrors(errors);
				}
			}
		}

		return pl;
	}

	private void buildResult(Plan pl, String requiredBuildingHeight, BigDecimal buildingHeight, boolean isAccepted) {
		String ruleNo = RULE_54_D;
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, HEIGHT_BUILDING);
		// details.put(ZONE, " Zone " + ZONE_DC);
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
			// TODO: handle exception
		}

		/*
		 * scrutinyDetail = new ScrutinyDetail(); //
		 * scrutinyDetail.setKey("Common_Height of Building");
		 * scrutinyDetail.addColumnHeading(1, RULE_NO);
		 * scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		 * scrutinyDetail.addColumnHeading(3, ZONE); scrutinyDetail.addColumnHeading(4,
		 * PERMISSIBLE); scrutinyDetail.addColumnHeading(4, PROVIDED);
		 * scrutinyDetail.addColumnHeading(5, STATUS);
		 */
//		 if (!ProcessHelper.isSmallPlot(Plan)) {
//		 checkBuildingHeight(Plan);
//		 }
		// checkBuildingInSecurityZoneArea(Plan);

		return Plan;
	}

}
