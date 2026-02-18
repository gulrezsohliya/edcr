package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.GroundWaterRecharge;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.MeasurementWithHeight;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.WasteDisposal;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.AdditionalFeature;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AdditionalFeature_Tripura extends AdditionalFeature {
	private static final BigDecimal PLOTEA_50 = BigDecimal.valueOf(50);
	private static final BigDecimal PLOTEA_100 = BigDecimal.valueOf(100);
	private static final BigDecimal PLOTEA_40 = BigDecimal.valueOf(40);

	public static final String N0_OF_FLOORS = "No of Stories";

	@Override
	public Plan validate(Plan pl) {

		HashMap<String, String> errors = new HashMap<String, String>();

		validateExtension(pl, errors);
		noOfFloors(pl, errors);
		validatePlinthHeight(pl, errors);
		validateCorridor(pl, errors);
		pantryValidations(pl, errors);
		groundWaterRecharge(pl, errors);

		return pl;
	}

	private void validateExtension(Plan pl, HashMap<String, String> errors) {
		Boolean extExist=false;
		for(Block b : pl.getBlocks()) {
			if(b.getBuilding().getTotalExistingBuiltUpArea().compareTo(BigDecimal.valueOf(0))>0) {
				extExist=true;
			}
		}
//		String extension = "";
//		if (pl.getPlanInfoProperties().containsKey("EXTENSION")) {
//			extension = pl.getPlanInfoProperties().get("EXTENSION");
//		}
		
//		if(extExist) {
//			pl.addError("PlanInfoExtension", "Please provide EXTENSION=XXX in PLAN_INFO");
//		}

		Boolean mixedOccupancy = Boolean.FALSE;
		int occCounter = 0;
		for (Block b : pl.getBlocks()) {
			for (Occupancy occup : b.getBuilding().getOccupancies()) {
				occCounter = occCounter + 1;
			}
		}
		if (occCounter > 1) {
			mixedOccupancy = Boolean.TRUE;
		}
		
		BigDecimal totalBUA=BigDecimal.ZERO;
		for (Block b : pl.getBlocks()) {
				totalBUA=b.getBuilding().getTotalBuitUpArea().subtract(b.getBuilding().getTotalExistingBuiltUpArea());
		}

		if (extExist) {
			for (Block b : pl.getBlocks()) {
				for (Occupancy o : b.getBuilding().getOccupancies()) {
					if (mixedOccupancy) {
						if (totalBUA.compareTo(BigDecimal.valueOf(100)) > 0) {
							pl.addError("HorizontalError",
									"Limit upto 100 sqm is fixed for Building Extension for Mixed Occupancy ");
						}
					}
					else if (o.getTypeHelper().getType().getCode().equalsIgnoreCase("R")) {
						if (totalBUA.compareTo(BigDecimal.valueOf(200)) > 0) {
							pl.addError("HorizontalError",
									"Limit upto 200 sqm is fixed for Building Extension for Residential ");
						}
					} else if (o.getTypeHelper().getType().getCode().equalsIgnoreCase("B")) {
						if (totalBUA.compareTo(BigDecimal.valueOf(100)) > 0) {
							pl.addError("HorizontalError",
									"Limit upto 100 sqm is fixed for Building Extension for Commercial ");
						}
					}
				}
			}
		}
	}

	private void validateCorridor(Plan pl, HashMap<String, String> errors) {
		try {
			for (Block block : pl.getBlocks()) {
				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Corridor");
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, "Floor");
				scrutinyDetail.addColumnHeading(4, PERMITTED);
				scrutinyDetail.addColumnHeading(5, PROVIDED);
				scrutinyDetail.addColumnHeading(6, STATUS);

				Boolean req = false;
				BigDecimal permissible = null;
				BigDecimal corridorWidth = null;

				OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
						? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
						: null;

				if (mostRestrictiveOccupancy != null
						&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
					permissible = BigDecimal.valueOf(0.9);

				} else if (mostRestrictiveOccupancy != null
						&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
					permissible = BigDecimal.valueOf(2);
				} else if (mostRestrictiveOccupancy != null
						&& (I.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
					permissible = BigDecimal.valueOf(1.5);
				}
				for (Floor f : block.getBuilding().getFloors()) {
					if (f.getCorridor() != null) {
						for (Measurement c : f.getCorridor().getMeasurements()) {
							if (c.getWidth().compareTo(BigDecimal.ZERO) > 0
									&& c.getWidth().compareTo(permissible) >= 0) {
								setReportOutputDetails(pl, "110 (a) (3)", "Corridor", f.getNumber(), permissible,
										c.getWidth(), Result.Accepted.getResultVal(), scrutinyDetail);
							} else {
								setReportOutputDetails(pl, "110 (a) (3)", "Corridor", f.getNumber(), permissible,
										c.getWidth(), Result.Not_Accepted.getResultVal(), scrutinyDetail);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void validatePlinthHeight(Plan pl, HashMap<String, String> errors) {
		try {
			Boolean parking = Boolean.FALSE;
			Boolean basement = Boolean.FALSE;
			for (Block block : pl.getBlocks()) {
				for (Floor floor : block.getBuilding().getFloors()) {
					if (floor.getNumber() <= -1) {
						basement = true;
					}
					if (floor.getParking().getStilts() != null || !floor.getParking().getStilts().isEmpty()) {
						parking = Boolean.TRUE;
					}
					if (floor.getParking().getOpenCars() != null || !floor.getParking().getOpenCars().isEmpty()) {
						parking = Boolean.TRUE;
					}
					if (floor.getParking().getBasementCars() != null
							|| !floor.getParking().getBasementCars().isEmpty()) {
						parking = Boolean.TRUE;
					}

				}

				boolean isAccepted = false;
				BigDecimal minPlinthHeight = BigDecimal.ZERO;
				String blkNo = block.getNumber();
				ScrutinyDetail scrutinyDetail = getNewScrutinyDetail("Block_" + blkNo + "_" + "Plinth");
				List<BigDecimal> plinthHeights = block.getPlinthHeight();
				String permissible = "";

				if (!plinthHeights.isEmpty()) {
					minPlinthHeight = plinthHeights.stream().reduce(BigDecimal::min).get();
					if (parking) {
						permissible = ">=0.15";
						if (minPlinthHeight.compareTo(BigDecimal.valueOf(0.15)) >= 0) {
							isAccepted = true;
						} else
							isAccepted = false;
					} else {
						permissible = ">=0.6";
						if (minPlinthHeight.compareTo(BigDecimal.valueOf(0.6)) >= 0) {
							isAccepted = true;
						} else
							isAccepted = false;
					}
					Map<String, String> details = new HashMap<>();
					details.put(RULE_NO, "55(1)");
					details.put(DESCRIPTION, MIN_PLINTH_HEIGHT_DESC);
					details.put(PERMISSIBLE, permissible);
					details.put(PROVIDED, String.valueOf(minPlinthHeight));
					details.put(STATUS,
							isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
					scrutinyDetail.getDetail().add(details);
					pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

				} else {
					if (!basement) {
						String plinthHeightLayer = String.format(DxfFileConstants.LAYER_PLINTH_HEIGHT,
								block.getNumber());
						errors.put(plinthHeightLayer, "Plinth height is not defined in layer " + plinthHeightLayer);
						pl.addErrors(errors);
					}

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void noOfFloors(Plan pl, HashMap<String, String> errors) {
		try {
			Boolean isAccepted = false;
			String noOfFloorsAllowed = StringUtils.EMPTY;
			String ruleNo = "";
			for (Block block : pl.getBlocks()) {
				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.setKey("Block_" + block.getName() + "_" + N0_OF_FLOORS);
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
				scrutinyDetail.addColumnHeading(6, PROVIDED);
				scrutinyDetail.addColumnHeading(7, STATUS);
				scrutinyDetail.setHeading(N0_OF_FLOORS);
				for (Occupancy occupancy : block.getBuilding().getOccupancies()) {
					BigDecimal PlotArea = pl.getPlanInformation().getPlotArea();
					BigDecimal noOfFloors = block.getBuilding().getMaxFloor();
					if (pl.getPlanInfoProperties().get("PHYSIOGRAPHY").equalsIgnoreCase("PLAINS")) {
						if (occupancy.getTypeHelper().getType().getCode().equals(R)) {

							if (PlotArea.compareTo(PLOTEA_50) >= 0 && PlotArea.compareTo(PLOTEA_100) < 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(1)) <= 0;
								noOfFloorsAllowed = "1";
							} else if (PlotArea.compareTo(PLOTEA_100) >= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(1)) >= 0;
								noOfFloorsAllowed = "1+";
							}
						} else if (occupancy.getTypeHelper().getType().getCode().equals(IN)) {
							isAccepted = true;
							noOfFloorsAllowed = "No Restriction";
						} else {
							if (PlotArea.compareTo(PLOTEA_40) < 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(1)) <= 0;
								noOfFloorsAllowed = "1";
							} else if (PlotArea.compareTo(PLOTEA_40) >= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(1)) >= 0;
								noOfFloorsAllowed = "1+";
							}
						}
						ruleNo = "4(1)(a)";
					} else if (pl.getPlanInfoProperties().get("PHYSIOGRAPHY").equalsIgnoreCase("HILLS")) {
						if (occupancy.getTypeHelper().getSubtype().getCode().equals(R)) {

							if (PlotArea.compareTo(PLOTEA_100) >= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(1)) >= 0;
								noOfFloorsAllowed = "1 or 1+";
							}
						} else if (occupancy.getTypeHelper().getType().getCode().equals(IN)) {
							isAccepted = true;
							noOfFloorsAllowed = "No Restriction";
						} else {
							if (PlotArea.compareTo(PLOTEA_40) >= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(1)) >= 0;
								noOfFloorsAllowed = "1+";
							}
						}
						ruleNo = "90(1)(c)";
					}

					if (errors.isEmpty() && StringUtils.isNotBlank(noOfFloorsAllowed)) {
						Map<String, String> details = new HashMap<>();
						details.put(RULE_NO, ruleNo);
						details.put(DESCRIPTION, "No Of Stories");
						details.put(PERMISSIBLE, noOfFloorsAllowed);
						details.put(PROVIDED, String.valueOf(noOfFloors));
						details.put(STATUS,
								isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					} else {
						errors.put("No Of Stories", "Block No " + block.getNumber() + "No of Stories Is Not Defined");
						pl.addErrors(errors);
					}

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void pantryValidations(Plan pl, HashMap<String, String> errors) {

		String widthPermissible = "";
		String areaPermissible = "";

		try {
			for (Block block : pl.getBlocks()) {
				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Pantry");
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, "Area");
				scrutinyDetail.addColumnHeading(4, "Area Provided");
				scrutinyDetail.addColumnHeading(5, "Width");
				scrutinyDetail.addColumnHeading(6, "Width Provided");
				scrutinyDetail.addColumnHeading(7, STATUS);
				scrutinyDetail.setHeading("Pantry");
				for (Floor floor : block.getBuilding().getFloors()) {
					if (floor.getPantry() != null) {
						for (Measurement p : floor.getPantry().getMeasurements()) {
							if (p.getArea() != null || p.getArea().compareTo(BigDecimal.ZERO) > 0) {
								Boolean isAccepted = Boolean.FALSE;
								widthPermissible = ">1.4";
								areaPermissible = ">3";
								Map<String, String> details = new HashMap<>();
								details.put(RULE_NO, "59");
								details.put(DESCRIPTION, "Pantry");
								details.put("Area", areaPermissible + " m²");
								details.put("Area Provided",
										String.valueOf(p.getArea().setScale(2, RoundingMode.HALF_UP)) + " m²");
								details.put("Width", widthPermissible + "m");
								details.put("Width Provided",
										String.valueOf(p.getWidth().setScale(2, RoundingMode.HALF_UP)) + "m");
								if (p.getArea().compareTo(BigDecimal.valueOf(3)) >= 0
										&& p.getWidth().compareTo(BigDecimal.valueOf(1.4)) >= 0) {
									isAccepted = Boolean.TRUE;
								}
								details.put(STATUS, isAccepted ? Result.Accepted.getResultVal()
										: Result.Not_Accepted.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							}
						}
					}

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void groundWaterRecharge(Plan pl, HashMap<String, String> errors) {
		try {
			ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
			scrutinyDetail1.addColumnHeading(1, RULE_NO);
			scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, REQUIRED);
			scrutinyDetail1.addColumnHeading(4, PROVIDED);
			scrutinyDetail1.addColumnHeading(5, STATUS);
			scrutinyDetail1.setKey("Common_Ground Water Recharge");
			BigDecimal plotArea = pl.getPlot().getArea();
			BigDecimal groundWaterRecharge = BigDecimal.ZERO;

			if (pl.getUtility().getGroundWaterRechargeUnits() != null
					&& !pl.getUtility().getGroundWaterRechargeUnits().isEmpty()) {
				for (GroundWaterRecharge wd : pl.getUtility().getGroundWaterRechargeUnits()) {
					groundWaterRecharge = wd.getArea();
				}
			}
			Boolean req = false;

			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;

			if (mostRestrictiveOccupancy != null
					&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				req = true;

			} else if (mostRestrictiveOccupancy != null
					&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				req = true;
			} else if (mostRestrictiveOccupancy != null
					&& (IN.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))) {
				req = true;
			}
			if (req) {
				if (plotArea.compareTo(BigDecimal.valueOf(400)) > 0) {
					if (pl.getUtility() != null && !pl.getUtility().getGroundWaterRechargeUnits().isEmpty()
							&& pl.getUtility().getGroundWaterRechargeUnits() != null) {

						setReportOutputDetails(pl, "110 (a) (3)", "Ground Water Recharge", "Defined in the plan",
								Result.Verify.getResultVal(), scrutinyDetail1);
					} else {
						setReportOutputDetails(pl, "110 (a) (3)", "Ground Water Recharge", "Not Defined in the plan",
								Result.Not_Accepted.getResultVal(), scrutinyDetail1);
					}
				}
			} else {
				if (pl.getUtility() != null && !pl.getUtility().getGroundWaterRechargeUnits().isEmpty()
						&& pl.getUtility().getGroundWaterRechargeUnits() != null) {

					setReportOutputDetails(pl, "110 (a) (3)", "Ground Water Recharge", "Defined in the plan",
							Result.Verify.getResultVal(), scrutinyDetail1);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String actual, String status,
			ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
//		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, Integer floorNo,
			BigDecimal permissible, BigDecimal c, String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, permissible.toString());
		details.put(PROVIDED, c.toString());
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private ScrutinyDetail getNewScrutinyDetail(String key) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		scrutinyDetail.setKey(key);
		return scrutinyDetail;
	}

	@Override
	public Plan process(Plan pl) {
		validate(pl);

		return pl;
	}

}
