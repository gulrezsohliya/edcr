
package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.client.constants.DxfFileConstants_AR;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Parking;
import org.springframework.stereotype.Service;

@Service
public class Parking_AR extends Parking {

	private static final Logger LOGGER = Logger.getLogger(Parking_AR.class);

	private static final String RULE = "70(7)";
	private static final String RULE_DESCRIPTION = "Parking space";
	private static final String SQMTRS = " m²";

	// ECS Values
	private static final double STD_OPEN_ECS = 18;
	private static final double STD_STILT_CVRD_ECS = 28;
	private static final double STD_BSMNT_ECS = 32;

	@Override
	public Plan validate(Plan pl) {
		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
			return pl;
		}
		if (pl.getBlocks() == null) {
			return pl;
		}
		BigDecimal stiltParkingArea = BigDecimal.ZERO;
		for (Block block : pl.getBlocks()) {
			for (Floor floor : block.getBuilding().getFloors()) {
				stiltParkingArea = stiltParkingArea.add(floor.getParking().getStilts().stream()
						.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));
			}
		}
		try {
			processParking(pl);
		} catch (Exception e) {
			System.out.println("Parking :: error ::" + e);
		}
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Parking");
		scrutinyDetail.setHeading("Parking Provisions");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, "Parking Type");
		scrutinyDetail.addColumnHeading(3, "Plot Area");
		scrutinyDetail.addColumnHeading(4, "Area Standard ECS");
		scrutinyDetail.addColumnHeading(5, "Permissible Area");
		scrutinyDetail.addColumnHeading(6, "Provided Area");
		scrutinyDetail.addColumnHeading(7, STATUS);
		// processMechanicalParking(pl);
		validate(pl);
		return pl;
	}

	public void processParking(Plan pl) {

		BigDecimal totalBuiltupArea = BigDecimal.ZERO;
		BigDecimal totalCoverageArea = (pl.getCoverage() != null) ? pl.getCoverage() : BigDecimal.ZERO;
		BigDecimal totalFloorArea = BigDecimal.ZERO;
		BigDecimal totalPlotArea = (pl.getPlot().getArea() != null) ? pl.getPlot().getArea() : BigDecimal.ZERO;

		for (Block block : pl.getBlocks()) {
			if (block.getBuilding() == null
					|| (block.getBuilding() != null && block.getBuilding().getFloors() == null)) {
				return;
			}
			for (Floor floor : block.getBuilding().getFloors()) {
				if (floor.getArea() != null && floor.getArea().compareTo(BigDecimal.valueOf(0.0)) > 0) {
					totalFloorArea = totalFloorArea.add(floor.getArea());
				} else {
					for (Occupancy occupancy : floor.getOccupancies()) {
						totalFloorArea = totalFloorArea.add(occupancy.getFloorArea());
						totalBuiltupArea = totalBuiltupArea.add(occupancy.getBuiltUpArea())
								.subtract(occupancy.getDeduction());
					}
				}
			}
		}
		BigDecimal coveredParkingArea = BigDecimal.ZERO;
		BigDecimal basementParkingArea = BigDecimal.ZERO;
		BigDecimal stiltParkingArea = BigDecimal.ZERO;
		for (Block block : pl.getBlocks()) {
			for (Floor floor : block.getBuilding().getFloors()) {
				stiltParkingArea = stiltParkingArea.add(floor.getParking().getStilts().stream()
						.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));
				if (floor.getNumber() < 0)
					basementParkingArea = basementParkingArea.add(floor.getParking().getBasementCars().stream()
							.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));// Cover Parking
																									// considered as
																									// basement parking
				if (floor.getNumber() >= 0)
					coveredParkingArea = coveredParkingArea.add(floor.getParking().getBasementCars().stream()
							.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));
			}
		}
		
		BigDecimal floor0bua=BigDecimal.ZERO;
		//Setting the totalBuiltupArea 
		if(stiltParkingArea.compareTo(BigDecimal.ZERO)>0) {
			totalBuiltupArea=totalBuiltupArea.subtract(stiltParkingArea);
			for(Block block: pl.getBlocks()) {
				for (Floor floor : block.getBuilding().getFloors()) {
					if(floor.getNumber()==0) {
						pl.getVirtualBuilding().setTotalBuitUpArea(totalBuiltupArea);
						for(Occupancy occ : floor.getOccupancies()) {
							floor0bua=floor0bua.add(occ.getBuiltUpArea());
							occ.setBuiltUpArea(occ.getBuiltUpArea().subtract(stiltParkingArea));
						}
						
					}
				}
			}
			
			
		}
		BigDecimal openParkingArea = pl.getParkingDetails().getOpenCars().stream().map(Measurement::getArea)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		System.out.println(openParkingArea);
		BigDecimal twoWheelerParkingArea = pl.getParkingDetails().getTwoWheelers().stream().map(Measurement::getArea)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalProvidedCarParkArea = openParkingArea.add(stiltParkingArea).add(basementParkingArea);
//		HashMap<String, String> errors = new HashMap<>();
		BigDecimal requiredAreaCAR = BigDecimal.ZERO;
		BigDecimal requiredArea2WHEELER = BigDecimal.ZERO;
		String requiredDetailsForReport = "";
		String providedDetailsForReport = "";
		Boolean isSetReportDetails = Boolean.FALSE;
		BigDecimal ecs = BigDecimal.ZERO;
		BigDecimal plotArea = pl.getPlot().getArea();
		OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
				? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
				: null;
		Double roundingMultiple = null;
		if (mostRestrictiveOccupancy != null) {
			if (mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.R1b)
					|| mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.R1c)) {
				if (plotArea.compareTo(BigDecimal.valueOf(100)) < 0) {
					ecs = BigDecimal.valueOf(0);
				} else if (plotArea.compareTo(BigDecimal.valueOf(100)) >= 0
						&& plotArea.compareTo(BigDecimal.valueOf(300)) <= 0) {
					ecs = BigDecimal.valueOf(1.5);
				} else {
					ecs = totalBuiltupArea.divide(BigDecimal.valueOf(100));
				}

			} else if (mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.P3c)) {

				ecs = totalBuiltupArea.divide(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(1.5)));

			} else if (mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.C1a)) {
				if(plotArea.compareTo(BigDecimal.valueOf(100))<0) {
					ecs = totalFloorArea.divide(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(0.5)));
				}else {
					ecs = totalFloorArea.divide(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(1.5)));
				}
				

			}else if (mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.C1b)
					|| mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.C1c)
					|| mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.C2a)) {

				ecs = totalFloorArea.divide(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(3)));

			} else if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.I)) {
				if (plotArea.compareTo(BigDecimal.valueOf(250)) < 0) {
					ecs = BigDecimal.valueOf(1);
				} else {
					ecs = totalFloorArea.divide(BigDecimal.valueOf(250));
				}

			} else if (mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.G1a)
					|| mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.G2a)
					|| mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.P3a)) {

				ecs = totalFloorArea.divide(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(2)));

			} else if (mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.P2a)
					|| mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.P2b)
					|| mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.P2d)) {

				ecs = totalFloorArea.divide(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(2)));

			} else if (mostRestrictiveOccupancy.getSubtype().getCode().contentEquals(DxfFileConstants_AR.P2c)) {
				ecs = totalFloorArea.divide(BigDecimal.valueOf(100));
			}
			System.out.println(totalBuiltupArea);
			System.out.println(ecs);
//			else if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.C)) {
//
//				requiredAreaCAR = BigDecimal.valueOf(20);
//				requiredArea2WHEELER = BigDecimal.valueOf(20);
//
//			}else if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.OF)) {
//
//				requiredAreaCAR = BigDecimal.valueOf(20);
//				requiredArea2WHEELER = BigDecimal.valueOf(20);
//
//			}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			BigDecimal openECS = BigDecimal.valueOf(18), stiltECS = BigDecimal.valueOf(28),
					bsmtECS = BigDecimal.valueOf(32), cvrdECS = BigDecimal.ZERO, twoWhlrECS = BigDecimal.ZERO,
					requiredArea = BigDecimal.ZERO, totalRequiredArea = BigDecimal.ZERO;
			Boolean status = Boolean.FALSE;

			if (openParkingArea.doubleValue() > 0) {
				totalRequiredArea = ecs.multiply(openECS);
				System.out.println(totalRequiredArea);
				if (openParkingArea.compareTo(totalRequiredArea) >= 0) {
					status = Boolean.TRUE;
				}
				setReportOutputDetails(pl, "Table 3.1 APBBL", "Open Parking", plotArea, ecs, totalRequiredArea,
						openParkingArea, status);
			}
			if (stiltParkingArea.doubleValue() > 0) {
				totalRequiredArea = ecs.multiply(stiltECS);
				System.out.println(totalRequiredArea);
				if (stiltParkingArea.compareTo(totalRequiredArea) >= 0) {
					status = Boolean.TRUE;
				}
				setReportOutputDetails(pl, "Table 3.1 APBBL", "Stilt Parking", plotArea, ecs, totalRequiredArea,
						stiltParkingArea, status);
			}
//			if (stiltParkingArea.doubleValue() > 0) {
//				stiltECS = stiltParkingArea.divide(BigDecimal.valueOf(STD_STILT_CVRD_ECS), 0, RoundingMode.FLOOR);
//				requiredArea = stiltECS.multiply(requiredAreaCAR);
//				totalRequiredArea=totalRequiredArea.add(requiredArea);
//				setReportOutputDetails(pl, "Table 3.1 APBBL", "Under Stilts",plotArea,ecs, stiltParkingArea + SQMTRS,
//						(new Double(STD_STILT_CVRD_ECS)).intValue() + SQMTRS, requiredArea + SQMTRS, "-", "");
//				setReportOutputDetails(pl, RULE, "Under Stilts", stiltParkingArea + SQMTRS,
//						(new Double(STD_STILT_CVRD_ECS)).intValue() + SQMTRS, "", "-", "");
//			}
//			if (coveredParkingArea.doubleValue() > 0) {
//				cvrdECS = coveredParkingArea.divide(BigDecimal.valueOf(STD_STILT_CVRD_ECS), 0, RoundingMode.FLOOR);
//				requiredArea = cvrdECS.multiply(requiredAreaCAR);
//				totalRequiredArea=totalRequiredArea.add(requiredArea);
//				setReportOutputDetails(pl, RULE, "Covered", basementParkingArea + SQMTRS,
//						(new Double(STD_STILT_CVRD_ECS)).intValue() + SQMTRS, requiredArea + SQMTRS, "-", "");
//				setReportOutputDetails(pl, RULE, "Covered", basementParkingArea + SQMTRS,
//						(new Double(STD_STILT_CVRD_ECS)).intValue() + SQMTRS, "", "-", "");
//			}
//			if (basementParkingArea.doubleValue() > 0) {
//				bsmtECS = basementParkingArea.divide(BigDecimal.valueOf(STD_BSMNT_ECS), RoundingMode.FLOOR);
//				requiredArea = bsmtECS.multiply(requiredAreaCAR);
//				totalRequiredArea=totalRequiredArea.add(requiredArea);
//				setReportOutputDetails(pl, RULE, "Basement", coveredParkingArea + SQMTRS,
//						(new Double(STD_BSMNT_ECS)).intValue() + SQMTRS, requiredArea + SQMTRS, "-", "");
//				setReportOutputDetails(pl, RULE, "Basement", coveredParkingArea + SQMTRS,
//						(new Double(STD_BSMNT_ECS)).intValue() + SQMTRS, "", "-", "");
//			}
//			if (twoWheelerParkingArea.doubleValue() > 0) {
//				twoWhlrECS = twoWheelerParkingArea.divide(BigDecimal.valueOf(1.25), RoundingMode.FLOOR);
//				requiredArea = twoWhlrECS.multiply(requiredAreaCAR);
//				totalRequiredArea=totalRequiredArea.add(requiredArea);
//				setReportOutputDetails(pl, RULE, "2 Wheelers", twoWheelerParkingArea + SQMTRS,
//						BigDecimal.valueOf(1.25) + SQMTRS, requiredArea + SQMTRS, "-", "");
//			}

//			if (totalProvidedCarParkArea.doubleValue() == 0) {
//				pl.addError(RULE_DESCRIPTION, getLocaleMessage("msg.error.not.defined", RULE_DESCRIPTION));
//			} else if (totalBuiltupArea.compareTo(totalRequiredArea)>0) {
//				setReportOutputDetails(pl, "", "", "", "Total", totalRequiredArea + SQMTRS,
//						totalBuiltupArea + SQMTRS,
//						Result.Not_Accepted.getResultVal());
//				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//			} else {
//				setReportOutputDetails(pl, "", "", "", "Total", totalRequiredArea + SQMTRS,
//						totalBuiltupArea + SQMTRS,
//						Result.Accepted.getResultVal());
//				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//			}

		}
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String parkingType, BigDecimal plotArea, BigDecimal ecs,
			BigDecimal requiredParkingArea, BigDecimal providedParkingArea, Boolean status) {
		Map<String, String> details = new HashMap<>();

		details.put(RULE_NO, ruleNo);
		details.put("Parking Type", parkingType);
		details.put("Plot Area", plotArea.toString());
		details.put("Area Standard ECS", ecs.setScale(2, RoundingMode.HALF_UP).toString());
		details.put("Permissible Area", requiredParkingArea.setScale(2, RoundingMode.HALF_UP).toString());
		details.put("Provided Area", providedParkingArea.setScale(2, RoundingMode.HALF_UP).toString());
		details.put(STATUS, status ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private double roundUp(double value, double multiplier) {
		return Math.ceil(value / multiplier) * multiplier;
	}

	private double roundDown(double value, double multiplier) {
		return Math.floor(value / multiplier) * multiplier;
	}

}
