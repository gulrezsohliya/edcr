package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.NotifiedRoad;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Coverage;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class Coverage_Tripura extends Coverage {
	// private static final String OCCUPANCY2 = "OCCUPANCY";

	private static final Logger LOG = Logger.getLogger(Coverage_Tripura.class);

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

	private static final BigDecimal PLOT_AREA_200 = BigDecimal.valueOf(200);
	private static final BigDecimal PLOT_AREA_502 = BigDecimal.valueOf(502);
	private static final BigDecimal PLOT_AREA_929 = BigDecimal.valueOf(929);

	private static final BigDecimal COVERAGE_AREA_70 = BigDecimal.valueOf(70);
	private static final BigDecimal COVERAGE_AREA_100 = BigDecimal.valueOf(100);
	private static final BigDecimal COVERAGE_AREA_60 = BigDecimal.valueOf(60);
	private static final BigDecimal COVERAGE_AREA_50 = BigDecimal.valueOf(50);

	private static final BigDecimal PLOT_AREA_100 = BigDecimal.valueOf(100);

	private static final BigDecimal PLOT_AREA_400 = BigDecimal.valueOf(400);

	private static final BigDecimal PLOT_AREA_1200 = BigDecimal.valueOf(1200);

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
		try {
			HashMap<String, String> errors = new HashMap<String, String>();
			String ruleNo = "";
			if (pl.getPlot() == null || (pl.getPlot() != null
					&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
				pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));

				return pl;
			}

			String requiredPlotArea = "";
			validate(pl);

			BigDecimal totalCoverage = BigDecimal.ZERO;
			BigDecimal totalCoverageArea = BigDecimal.ZERO;
			BigDecimal plotArea = pl.getPlot().getArea();
			Integer noOfbuildings = 0;
			Integer buildingsallowed = 0;

			for (Block block : pl.getBlocks()) {
				BigDecimal coverage = BigDecimal.ZERO;
				BigDecimal coverageAreaWithoutDeduction = BigDecimal.ZERO;
				BigDecimal coverageDeductionArea = BigDecimal.ZERO;

				for (Measurement coveragearea : block.getCoverage()) {
					coverageAreaWithoutDeduction = coverageAreaWithoutDeduction.add(coveragearea.getArea());
				}
				for (Measurement deduct : block.getCoverageDeductions()) {
					coverageDeductionArea = coverageDeductionArea.add(deduct.getArea());
				}
				if (block.getBuilding() != null) {
					block.getBuilding().setCoverageArea(coverageAreaWithoutDeduction);

					if (pl.getPlot().getArea().doubleValue() > 0)
						coverage = coverageAreaWithoutDeduction.subtract(coverageDeductionArea)
								.multiply(BigDecimal.valueOf(100)).divide(pl.getPlanInformation().getPlotArea(),
										DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
					block.getBuilding().setCoverage(coverage);
					if (block.getBuilding().getCoverage() == null) {
						pl.addError("CoverageError", "Coverage Area Undefined");
					}

					totalCoverageArea = totalCoverageArea.add(block.getBuilding().getCoverageArea());
					noOfbuildings++;

				}
				if (pl.getPlot() != null && pl.getPlot().getArea().doubleValue() > 0)
					totalCoverage = totalCoverageArea.multiply(BigDecimal.valueOf(100)).divide(
							pl.getPlanInformation().getPlotArea(), DcrConstants.DECIMALDIGITS_MEASUREMENTS,
							DcrConstants.ROUNDMODE_MEASUREMENTS);
				pl.setCoverage(totalCoverage);

				if (pl.getVirtualBuilding() != null) {
					pl.getVirtualBuilding().setTotalCoverageArea(totalCoverageArea);
				}
				OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
						? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
						: null;
				String abuttingRoad = "";
				int no = 0;
				List<Pair<Integer, BigDecimal>> roadWidth = new ArrayList<>();
				if (pl.getNotifiedRoads() != null) {
					for (NotifiedRoad n : pl.getNotifiedRoads()) {
						if (n.getWidth() != null ) {
							no = no + 1;
							roadWidth.add(Pair.of(no, n.getWidth()));
						}
					}
				}

				if (pl.getPlanInfoProperties().containsKey("ABUTTING_ROAD")) {
					abuttingRoad = pl.getPlanInfoProperties().get("ABUTTING_ROAD");
				}
				int roadabut=Integer.valueOf(abuttingRoad);
				if (abuttingRoad.equalsIgnoreCase("3")
						&& mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase("IN")
						|| abuttingRoad.equalsIgnoreCase("3")
						&& mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase("B")) {
					if (no < 3) {
						errors.put("RoadWidthNoErrorCoverage", "There should be three abutting notified roads. Only "
								+ no + " notified roads are present in the plan");
						pl.addErrors(errors);
						return pl;
					}
					for (Map.Entry<Integer, BigDecimal> entry : roadWidth) {
						if (entry.getValue().compareTo(BigDecimal.valueOf(5)) >= 0) {
							requiredPlotArea = "Any Area";
							ruleNo = "46(1)";
							processCoverage(pl, requiredPlotArea, noOfbuildings, "1+", StringUtils.EMPTY, coverage,
									COVERAGE_AREA_100, ruleNo);
						}else {
							requiredPlotArea = "Any Area";
							ruleNo = "46(1)";
							processCoverage(pl, requiredPlotArea, noOfbuildings, "1+", StringUtils.EMPTY, coverage,
									COVERAGE_AREA_70, ruleNo);
						}
					}

				} else {
					if (pl.getPlanInfoProperties().get("PHYSIOGRAPHY").equalsIgnoreCase("PLAINS")) {
						if (mostRestrictiveOccupancy != null
								&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
							if (noOfbuildings.compareTo(1) == 0) {// noofbuilding=1
								requiredPlotArea = "Any Area";
								ruleNo = "46(1)";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_70, ruleNo);
							} else if (noOfbuildings.compareTo(1) > 0) {// noofbuilding=1+
								requiredPlotArea = "Any Area";
								ruleNo = "51(2)";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1+", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_60, ruleNo);
							}
						} else if (mostRestrictiveOccupancy != null
								&& (E.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
							if (noOfbuildings.compareTo(1) == 0) {// noofbuilding=1
								requiredPlotArea = "Any Area";
								ruleNo = "46(1)";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_70, ruleNo);
							} else if (noOfbuildings.compareTo(1) > 0) {// noofbuilding=1+
								requiredPlotArea = "Any Area";
								ruleNo = "51(2)";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1+", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_60, ruleNo);
							}
						} else {
							if (noOfbuildings.compareTo(1) == 0) {// noofbuilding=1
								requiredPlotArea = "Any Area";
								ruleNo = "46(1)";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_70, ruleNo);
							} else if (noOfbuildings.compareTo(1) > 0) {// noofbuilding=1+
								requiredPlotArea = "Any Area";
								ruleNo = "51(2)";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1+", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_60, ruleNo);
							}
						}

					} else if (pl.getPlanInfoProperties().get("PHYSIOGRAPHY").equalsIgnoreCase("HILLS")) {
						ruleNo = "96";
						if (mostRestrictiveOccupancy != null
								&& (R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
							if (plotArea.compareTo(PLOT_AREA_100) >= 0 && plotArea.compareTo(PLOT_AREA_400) < 0) {// 100-400
								requiredPlotArea = "100 - 400";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_70, ruleNo);
							} else if (plotArea.compareTo(PLOT_AREA_400) >= 0
									&& plotArea.compareTo(PLOT_AREA_1200) < 0) {// 400-1200
								requiredPlotArea = "400 - 1200";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_60, ruleNo);
							} else if (plotArea.compareTo(PLOT_AREA_1200) >= 0) {
								requiredPlotArea = "over 1200";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_50, ruleNo);
							}
						} else if (mostRestrictiveOccupancy != null
								&& (E.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
							if (plotArea.compareTo(PLOT_AREA_100) >= 0 && plotArea.compareTo(PLOT_AREA_400) < 0) {// 100-400
								requiredPlotArea = "100 - 400";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_70, ruleNo);
							} else if (plotArea.compareTo(PLOT_AREA_400) >= 0
									&& plotArea.compareTo(PLOT_AREA_1200) < 0) {// 400-1200
								requiredPlotArea = "400 - 1200";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_60, ruleNo);
							} else if (plotArea.compareTo(PLOT_AREA_1200) >= 0) {
								requiredPlotArea = "over 1200";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_50, ruleNo);
							}
						} else {
							if (plotArea.compareTo(PLOT_AREA_100) >= 0 && plotArea.compareTo(PLOT_AREA_400) < 0) {// 100-400
								requiredPlotArea = "100 - 400";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_60, ruleNo);
							} else if (plotArea.compareTo(PLOT_AREA_400) >= 0
									&& plotArea.compareTo(PLOT_AREA_1200) < 0) {// 400-1200
								requiredPlotArea = "400 - 1200";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_50, ruleNo);
							} else if (plotArea.compareTo(PLOT_AREA_1200) >= 0) {
								requiredPlotArea = "over 1200";
								processCoverage(pl, requiredPlotArea, noOfbuildings, "1", StringUtils.EMPTY, coverage,
										COVERAGE_AREA_40, ruleNo);
							}
						}
					} else {
						errors.put("CoverageError", "HILLS/PLAINS not define in planinfoproperties");
						pl.addErrors(errors);
						return pl;
					}
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;
	}

	private void processCoverage(Plan pl, String requiredPlot, Integer noOfBuilding, String buildingallowed,
			String occupancy, BigDecimal coverage, BigDecimal upperLimit, String ruleNo) {

		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Overall Summary_Coverage");
		scrutinyDetail.setHeading("Coverage in Percentage");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, PLOT_AREA);
		scrutinyDetail.addColumnHeading(4, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, "No of Buildings Allowed");
		scrutinyDetail.addColumnHeading(7, "No of Buildings Provided");
		scrutinyDetail.addColumnHeading(8, STATUS);

		String desc = "Coverage";
		String actualResult = coverage.toString() + "% of Plot area";
		String expectedResult = "Area <= " + upperLimit + "% of Plot area";
		Map<String, String> details = new HashMap<>();

		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, desc);
		details.put(PLOT_AREA, requiredPlot);
		details.put(PERMISSIBLE, expectedResult);
		details.put(PROVIDED, actualResult);
		details.put("No of Buildings Allowed", buildingallowed);
		details.put("No of Buildings Provided", noOfBuilding.toString());
		if (coverage.compareTo(upperLimit) <= 0) {
			details.put(STATUS, Result.Accepted.getResultVal());
		} else {
			details.put(STATUS, Result.Not_Accepted.getResultVal());
		}

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
