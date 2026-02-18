
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
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.feature.Parking;
import org.springframework.stereotype.Service;

@Service
public class Parking_Manipur extends Parking {

	private static final Logger LOGGER = Logger.getLogger(Parking_Manipur.class);


	private static final String RULE = "70(7)";
	private static final String RULE_DESCRIPTION = "Parking space";
	private static final String SQMTRS = " m²";

	// ECS Values
	private static final double STD_OPEN_ECS = 23;
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
		scrutinyDetail.addColumnHeading(3, "Parking Area Provided");
		scrutinyDetail.addColumnHeading(4, "Area Standard ECS");
		scrutinyDetail.addColumnHeading(5, "Required Parking Area as per builtup area");
		scrutinyDetail.addColumnHeading(6, "Available Builtup Area");
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
	
		
//		pl.getParkingDetails().getC
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
		BigDecimal openParkingArea = pl.getParkingDetails().getOpenCars().stream().map(Measurement::getArea)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal twoWheelerParkingArea = pl.getParkingDetails().getTwoWheelers().stream().map(Measurement::getArea)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalProvidedCarParkArea = openParkingArea.add(stiltParkingArea).add(basementParkingArea).add(twoWheelerParkingArea);
//		HashMap<String, String> errors = new HashMap<>();
		BigDecimal requiredAreaCAR = BigDecimal.ZERO;
		BigDecimal requiredArea2WHEELER = BigDecimal.ZERO;
		String requiredDetailsForReport = "";
		String providedDetailsForReport = "";
		String status = null;
		Boolean isSetReportDetails = Boolean.FALSE;

		OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
				? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
				: null;
		Double roundingMultiple = null;
		if (mostRestrictiveOccupancy != null) {
			if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.R)) {

				requiredAreaCAR = BigDecimal.valueOf(60);
				requiredArea2WHEELER = BigDecimal.valueOf(40);

			}else if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.C)) {

				requiredAreaCAR = BigDecimal.valueOf(50);
				requiredArea2WHEELER = BigDecimal.valueOf(30);

			}else if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.IN)) {

				requiredAreaCAR = BigDecimal.valueOf(100);
				requiredArea2WHEELER = BigDecimal.valueOf(50);

			}
//			else if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.RS)) {
//
//				requiredAreaCAR = BigDecimal.valueOf(20);
//				requiredArea2WHEELER = BigDecimal.valueOf(10);
//
//			}
			else if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.OF)) {

				requiredAreaCAR = BigDecimal.valueOf(50);
				requiredArea2WHEELER = BigDecimal.valueOf(30);

			}
//			else if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.H)) {
//
//				requiredAreaCAR = BigDecimal.valueOf(50);
//				requiredArea2WHEELER = BigDecimal.valueOf(30);
//
//			}
			else if (mostRestrictiveOccupancy.getType().getCode().contentEquals(DxfFileConstants_AR.I)) {

				requiredAreaCAR = BigDecimal.valueOf(150);
				requiredArea2WHEELER = BigDecimal.valueOf(50);

			}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			BigDecimal openECS = BigDecimal.ZERO, stiltECS = BigDecimal.ZERO, bsmtECS = BigDecimal.ZERO,
					cvrdECS = BigDecimal.ZERO, twoWhlrECS = BigDecimal.ZERO, requiredArea = BigDecimal.ZERO, totalRequiredArea = BigDecimal.ZERO;

			if (openParkingArea.doubleValue() > 0) {
				openECS = totalBuiltupArea.divide(requiredAreaCAR, 0, RoundingMode.FLOOR);
				requiredArea = openECS.multiply(BigDecimal.valueOf(STD_OPEN_ECS));
				totalRequiredArea=totalRequiredArea.add(requiredArea).setScale(2, BigDecimal.ROUND_HALF_EVEN);
				setReportOutputDetails(pl, RULE, "On Site(Open)", openParkingArea + SQMTRS,
						(new Double(STD_OPEN_ECS)).intValue() + SQMTRS, requiredArea + SQMTRS, "-", "");
			}
			if (stiltParkingArea.doubleValue() > 0) {
				stiltECS = totalBuiltupArea.divide(requiredAreaCAR, 0, RoundingMode.FLOOR);
				requiredArea = stiltECS.multiply(BigDecimal.valueOf(STD_STILT_CVRD_ECS));
				totalRequiredArea=totalRequiredArea.add(requiredArea).setScale(2, BigDecimal.ROUND_HALF_EVEN);
				setReportOutputDetails(pl, RULE, "Under Stilts", stiltParkingArea + SQMTRS,
						(new Double(STD_STILT_CVRD_ECS)).intValue() + SQMTRS, requiredArea + SQMTRS, "-", "");
			}
			if (coveredParkingArea.doubleValue() > 0) {
				cvrdECS = totalBuiltupArea.divide(requiredAreaCAR, 0, RoundingMode.FLOOR);
				requiredArea = cvrdECS.multiply(BigDecimal.valueOf(STD_STILT_CVRD_ECS));
				totalRequiredArea=totalRequiredArea.add(requiredArea).setScale(2, BigDecimal.ROUND_HALF_EVEN);
				setReportOutputDetails(pl, RULE, "Covered", basementParkingArea + SQMTRS,
						(new Double(STD_STILT_CVRD_ECS)).intValue() + SQMTRS, requiredArea + SQMTRS, "-", "");
			}
			if (basementParkingArea.doubleValue() > 0) {
				bsmtECS = totalBuiltupArea.divide(requiredAreaCAR,0, RoundingMode.FLOOR);
				requiredArea = bsmtECS.multiply(BigDecimal.valueOf(STD_BSMNT_ECS));
				totalRequiredArea=totalRequiredArea.add(requiredArea).setScale(2, BigDecimal.ROUND_HALF_EVEN);
				setReportOutputDetails(pl, RULE, "Basement", coveredParkingArea + SQMTRS,
						(new Double(STD_BSMNT_ECS)).intValue() + SQMTRS, requiredArea + SQMTRS, "-", "");
			}
			// if (twoWheelerParkingArea.doubleValue() > 0) {
			if (totalProvidedCarParkArea.doubleValue() > 0) {
				twoWhlrECS = totalBuiltupArea.divide(requiredArea2WHEELER, 0, RoundingMode.FLOOR);
				requiredArea = twoWhlrECS.multiply(BigDecimal.valueOf(1.25));
				totalRequiredArea=totalRequiredArea.add(requiredArea);
				setReportOutputDetails(pl, RULE, "Two Wheelers", twoWheelerParkingArea.doubleValue()>0?(twoWheelerParkingArea + SQMTRS):"-",
						BigDecimal.valueOf(1.25) + SQMTRS, requiredArea + SQMTRS, "-", "");
			}

			if (totalProvidedCarParkArea.doubleValue() == 0) {
				pl.addError(RULE_DESCRIPTION, getLocaleMessage("msg.error.not.defined", RULE_DESCRIPTION));
			} else if (totalProvidedCarParkArea.compareTo(totalRequiredArea)<0) {
				setReportOutputDetails(pl, "", "Total", totalProvidedCarParkArea + SQMTRS, "", totalRequiredArea + SQMTRS,
						totalBuiltupArea + SQMTRS,
						Result.Not_Accepted.getResultVal());
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			} else {
				setReportOutputDetails(pl, "", "Total", totalProvidedCarParkArea + SQMTRS, "", totalRequiredArea + SQMTRS,
						totalBuiltupArea + SQMTRS,
						Result.Accepted.getResultVal());
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}

		}
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String parkingType, String providedArea, String ecsSTD,
			String providedECS, String requiredECS, String status) {
		Map<String, String> details = new HashMap<>();

		details.put(RULE_NO, ruleNo);
		details.put("Parking Type", parkingType);
		details.put("Parking Area Provided", providedArea);
		details.put("Area Standard ECS", ecsSTD);
		details.put("Required Parking Area as per builtup area", providedECS);
		details.put("Available Builtup Area", requiredECS);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
	}

	private double roundUp(double value, double multiplier) {
		return Math.ceil(value / multiplier) * multiplier;
	}

	private double roundDown(double value, double multiplier) {
		return Math.floor(value / multiplier) * multiplier;
	}

}
