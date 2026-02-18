package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Coverage;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class Coverage_Sikkim extends Coverage {
	// private static final String OCCUPANCY2 = "OCCUPANCY";

	private static final Logger LOG = Logger.getLogger(Coverage_Sikkim.class);

	/*
	 * private static final String RULE_NAME_KEY = "coverage.rulename"; private
	 * static final String RULE_DESCRIPTION_KEY = "coverage.description"; private
	 * static final String RULE_EXPECTED_KEY = "coverage.expected"; private static
	 * final String RULE_ACTUAL_KEY = "coverage.actual"; private static final
	 * BigDecimal Forty = BigDecimal.valueOf(40);
	 */

	public static final String RULE_38 = "38";
	
	/*
	 * private static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO =
	 * BigDecimal.valueOf(12.2); private static final BigDecimal
	 * ROAD_WIDTH_THIRTY_POINTFIVE = BigDecimal.valueOf(30.5);
	 */
	private static final String PLOT_AREA = "Plot Area in m²";
	
	private static final BigDecimal PLOT_AREA_251 = BigDecimal.valueOf(251);
	private static final BigDecimal PLOT_AREA_502 = BigDecimal.valueOf(502);
	private static final BigDecimal PLOT_AREA_929 = BigDecimal.valueOf(929);

	private static final BigDecimal COVERAGE_AREA_70 = BigDecimal.valueOf(70);
	private static final BigDecimal COVERAGE_AREA_50 = BigDecimal.valueOf(50);
	private static final BigDecimal COVERAGE_AREA_40 = BigDecimal.valueOf(40);

	@Override
	public Plan validate(Plan pl) {
		for (Block block : pl.getBlocks()) {
			if (block.getCoverage().isEmpty()) {
				pl.addError("coverageArea" + block.getNumber(),
						"Coverage Area for block " + block.getNumber() + " not Provided");
			}
		}
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		
		LOG.info("DECENT_GANGTOK Coverage_Gangtok: Process");
		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));

			return pl;
		}
		
		String requiredPlotArea = "";
		validate(pl);

		BigDecimal totalCoverage = BigDecimal.ZERO;
		BigDecimal totalCoverageArea = BigDecimal.ZERO;

		for (Block block : pl.getBlocks()) {
			if (block.getBuilding().getOccupancies() == null || block.getBuilding().getOccupancies().isEmpty()) {
				pl.addError("OccupancyError", "Occupancy for block " + block.getNumber() + " not defined");
			}
			for (Occupancy occup : block.getBuilding().getOccupancies()) {

				if (occup.getTypeHelper().getType() == null) {
					pl.addError("OccupancyTypeError", "Occupancy Type for block " + block.getNumber() + " not defined");
				}
				if (occup.getTypeHelper().getSubtype() == null) {
					pl.addError("OccupancySubTypeError",
							"Sub Occupancy Type for block " + block.getNumber() + " not defined");
				}
			}

			BigDecimal coverageAreaWithoutDeduction = BigDecimal.ZERO;
			BigDecimal coverageDeductionArea = BigDecimal.ZERO;

			for (Measurement coverage : block.getCoverage()) {
				coverageAreaWithoutDeduction = coverageAreaWithoutDeduction.add(coverage.getArea());
			}
			for (Measurement deduct : block.getCoverageDeductions()) {
				coverageDeductionArea = coverageDeductionArea.add(deduct.getArea());
			}
			if (block.getBuilding() != null) {
//				block.getBuilding().setCoverageArea(coverageAreaWithoutDeduction.subtract(coverageDeductionArea));
				block.getBuilding().setCoverageArea(coverageAreaWithoutDeduction);
				BigDecimal coverage = BigDecimal.ZERO;
				if (pl.getPlot().getArea().doubleValue() > 0)
					coverage = block.getBuilding().getCoverageArea().multiply(BigDecimal.valueOf(100)).divide(
							pl.getPlanInformation().getPlotArea(), DcrConstants.DECIMALDIGITS_MEASUREMENTS,
							DcrConstants.ROUNDMODE_MEASUREMENTS);

				block.getBuilding().setCoverage(coverage);

				totalCoverageArea = totalCoverageArea.add(block.getBuilding().getCoverageArea());
			}

		}
		
		if (pl.getPlot() != null && pl.getPlot().getArea().doubleValue() > 0)
			totalCoverage = totalCoverageArea.multiply(BigDecimal.valueOf(100)).divide(
					pl.getPlanInformation().getPlotArea(), DcrConstants.DECIMALDIGITS_MEASUREMENTS,
					DcrConstants.ROUNDMODE_MEASUREMENTS);
		pl.setCoverage(totalCoverage);
		
		if (pl.getVirtualBuilding() != null) {
			pl.getVirtualBuilding().setTotalCoverageArea(totalCoverageArea);
		}

		/*
		 * BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth(); if (roadWidth
		 * != null && roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_THIRTY_POINTFIVE) <= 0) { processCoverage(pl,
		 * StringUtils.EMPTY, totalCoverage, Forty); }
		 */


		if (pl.getPlot().getArea().compareTo(PLOT_AREA_251) >= 0) {
			requiredPlotArea = "Above 2700 sq ft";
			processCoverage(pl, requiredPlotArea, StringUtils.EMPTY, totalCoverage, COVERAGE_AREA_70);
		} 


		return pl;
	}

	private void processCoverage(Plan pl, String requiredPlot, String occupancy, BigDecimal coverage, BigDecimal upperLimit) {
		
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Coverage");
		scrutinyDetail.setHeading("Coverage in Percentage");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, PLOT_AREA);
		scrutinyDetail.addColumnHeading(4, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);
		
		String desc = "Coverage";
		String actualResult = coverage.toString()+"% of Plot area";
		String expectedResult = "Area <= " + upperLimit+"% of Plot area";
		Map<String, String> details = new HashMap<>();
		
		details.put(RULE_NO, RULE_38);
		details.put(DESCRIPTION, desc);
		details.put(PLOT_AREA, requiredPlot);
		details.put(PERMISSIBLE, expectedResult);
		details.put(PROVIDED, actualResult);
		
		if (coverage.compareTo(upperLimit) <= 0) {
			details.put(STATUS, Result.Accepted.getResultVal());
		} else {
			details.put(STATUS, Result.Not_Accepted.getResultVal());
		}

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	protected OccupancyType getMostRestrictiveCoverage(EnumSet<OccupancyType> distinctOccupancyTypes) {

		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_B1))
			return OccupancyType.OCCUPANCY_B1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_B2))
			return OccupancyType.OCCUPANCY_B2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_B3))
			return OccupancyType.OCCUPANCY_B3;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_D))
			return OccupancyType.OCCUPANCY_D;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_D1))
			return OccupancyType.OCCUPANCY_D1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_I2))
			return OccupancyType.OCCUPANCY_I2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_I1))
			return OccupancyType.OCCUPANCY_I1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_C))
			return OccupancyType.OCCUPANCY_C;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_A1))
			return OccupancyType.OCCUPANCY_A1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_A4))
			return OccupancyType.OCCUPANCY_A4;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_A2))
			return OccupancyType.OCCUPANCY_A2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_G1))
			return OccupancyType.OCCUPANCY_G1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_E))
			return OccupancyType.OCCUPANCY_E;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_F))
			return OccupancyType.OCCUPANCY_F;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_F4))
			return OccupancyType.OCCUPANCY_F4;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_G2))
			return OccupancyType.OCCUPANCY_G2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_H))
			return OccupancyType.OCCUPANCY_H;

		else
			return null;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
