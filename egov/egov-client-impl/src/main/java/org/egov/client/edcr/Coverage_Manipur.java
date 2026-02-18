package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.G;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.C;

import static org.egov.edcr.constants.DxfFileConstants.A2;

import static org.egov.edcr.constants.DxfFileConstants.A_FH;
import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.constants.DxfFileConstants.A_SA;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_HE;

import static org.egov.edcr.constants.DxfFileConstants.B2;
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
import static org.egov.edcr.constants.DxfFileConstants.F_H;
import static org.egov.edcr.constants.DxfFileConstants.F_CB;

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

//from  DxfFileConstants_AR
import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.feature.Coverage;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class Coverage_Manipur extends Coverage {
	// private static final String OCCUPANCY2 = "OCCUPANCY";

	private static final Logger LOG = Logger.getLogger(Coverage_Manipur.class);
	private static final String RULE_DESCRIPTION_KEY = "coverage.description";
	private static final String RULE_EXPECTED_KEY = "coverage.expected";
	private static final String RULE_ACTUAL_KEY = "coverage.actual";
	private static final BigDecimal Forty = BigDecimal.valueOf(40);
	public static final String RULE_54_D = "54-D";
	private static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);
	private static final BigDecimal ROAD_WIDTH_THIRTY_POINTFIVE = BigDecimal.valueOf(30.5);

	private static final BigDecimal PLOT_AREA_48 = BigDecimal.valueOf(48);
	private static final BigDecimal PLOT_AREA_60 = BigDecimal.valueOf(60);
	private static final BigDecimal PLOT_AREA_100 = BigDecimal.valueOf(100);
	private static final BigDecimal PLOT_AREA_250 = BigDecimal.valueOf(250);
	private static final BigDecimal PLOT_AREA_500 = BigDecimal.valueOf(500);
	private static final BigDecimal PLOT_AREA_1000 = BigDecimal.valueOf(1000);
	private static final BigDecimal PLOT_AREA_1500 = BigDecimal.valueOf(1500);

	private static final BigDecimal PLOT_AREA_90 = BigDecimal.valueOf(90);
	private static final BigDecimal PLOT_AREA_150 = BigDecimal.valueOf(150);
	private static final BigDecimal PLOT_AREA_300 = BigDecimal.valueOf(300);
	private static final BigDecimal PLOT_AREA_750 = BigDecimal.valueOf(750);
	private static final BigDecimal PLOT_AREA_2500 = BigDecimal.valueOf(2500);

	private static final BigDecimal PLOT_AREA_3000 = BigDecimal.valueOf(3000);
	private static final BigDecimal PLOT_AREA_10000 = BigDecimal.valueOf(10000);
	private static final BigDecimal PLOT_AREA_20000 = BigDecimal.valueOf(20000);
	private static final BigDecimal PLOT_AREA_1080 = BigDecimal.valueOf(20000);
	private static final BigDecimal PLOT_AREA_510 = BigDecimal.valueOf(20000);
	private static final BigDecimal PLOT_AREA_2000 = BigDecimal.valueOf(2000);
	private static final BigDecimal PLOT_AREA_400 = BigDecimal.valueOf(400);
	private static final BigDecimal PLOT_AREA_4000 = BigDecimal.valueOf(4000);
	private static final BigDecimal PLOT_AREA_12000 = BigDecimal.valueOf(12000);
	private static final BigDecimal PLOT_AREA_450 = BigDecimal.valueOf(450);

	private static final BigDecimal COVERAGE_AREA_75 = BigDecimal.valueOf(0.75);
	private static final BigDecimal COVERAGE_AREA_70 = BigDecimal.valueOf(0.7);
	private static final BigDecimal COVERAGE_AREA_66 = BigDecimal.valueOf(0.66);
	private static final BigDecimal COVERAGE_AREA_65 = BigDecimal.valueOf(0.65);
	private static final BigDecimal COVERAGE_AREA_60 = BigDecimal.valueOf(0.6);
	private static final BigDecimal COVERAGE_AREA_55 = BigDecimal.valueOf(0.55);
	private static final BigDecimal COVERAGE_AREA_50 = BigDecimal.valueOf(0.5);
	private static final BigDecimal COVERAGE_AREA_45 = BigDecimal.valueOf(0.45);
	private static final BigDecimal COVERAGE_AREA_40 = BigDecimal.valueOf(0.4);
	private static final BigDecimal COVERAGE_AREA_35 = BigDecimal.valueOf(0.35);
	private static final BigDecimal COVERAGE_AREA_33 = BigDecimal.valueOf(0.33);
	private static final BigDecimal COVERAGE_AREA_33_33 = BigDecimal.valueOf(0.3333);
	private static final BigDecimal COVERAGE_AREA_30 = BigDecimal.valueOf(0.3);
	private static final BigDecimal COVERAGE_AREA_25 = BigDecimal.valueOf(0.25);
	private static final BigDecimal COVERAGE_AREA_20 = BigDecimal.valueOf(0.2);
	private static final BigDecimal COVERAGE_AREA_10 = BigDecimal.valueOf(0.1);

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

			validate(pl);
			String requiredPlotArea = "";
			BigDecimal totalCoverage = BigDecimal.ZERO;
			BigDecimal totalCoverageArea = BigDecimal.ZERO;
			BigDecimal coveragePercentage = BigDecimal.ZERO;
			BigDecimal coverage = BigDecimal.ZERO;
			BigDecimal PlotArea = pl.getPlanInformation().getPlotArea();
			BigDecimal frontsetback = null;
			BigDecimal rearsetback = null;
			BigDecimal side1setback = null;
			BigDecimal side2setback = null;
			BigDecimal s = null;
			OccupancyTypeHelper mostRestrictiveOccupancyType = Util_Manipur.getMostRestrictive(pl);

			for (Block block : pl.getBlocks()) {
				for (SetBack setback : block.getSetBacks()) {
					if (setback.getFrontYard() != null) {
						frontsetback = setback.getFrontYard().getArea();
					}
					if (setback.getRearYard() != null) {
						rearsetback = setback.getRearYard().getArea();
					}
					if (setback.getSideYard1() != null) {
						side1setback = setback.getSideYard1().getArea();
					}
					if (setback.getSideYard2() != null) {
						side2setback = setback.getSideYard2().getArea();
					}
					if (frontsetback != null && rearsetback != null && side1setback != null && side2setback != null)
						calculateCoverage(frontsetback, rearsetback, side1setback, side2setback, PlotArea,
								mostRestrictiveOccupancyType, pl);

				}
				s = frontsetback.add(rearsetback).add(side1setback).add(side2setback);
				coverage = pl.getPlot().getArea().subtract(s);
				block.getBuilding().setCoverage(coverage);

				totalCoverageArea = totalCoverageArea.add(block.getBuilding().getCoverage());
				pl.setCoverage(totalCoverageArea);
				block.getBuilding().setCoverageArea(totalCoverageArea);

				if (pl.getVirtualBuilding() != null) {
					pl.getVirtualBuilding().setTotalCoverageArea(totalCoverageArea);
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;
	}

	private void calculateCoverage(BigDecimal frontsetback, BigDecimal rearsetback, BigDecimal side1setback,
			BigDecimal side2setback, BigDecimal PlotArea, OccupancyTypeHelper mostRestrictiveOccupancyType, Plan pl) {
		BigDecimal s = null;
		String requiredPlotArea = "";
		String status = "Verify";
		BigDecimal requiredCoverage = BigDecimal.ZERO;
		BigDecimal coveragePercentage = null;
		s = frontsetback.add(rearsetback).add(side1setback).add(side2setback);
		coveragePercentage = PlotArea.subtract(s);
		if (mostRestrictiveOccupancyType.getType().getCode().equals(R)) {
			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(R1)) {
				if (PlotArea.compareTo(BigDecimal.valueOf(50)) > 0 && PlotArea.compareTo(BigDecimal.valueOf(90)) <= 0) {
					requiredPlotArea = " 50 to 90";
				} 
				else if (PlotArea.compareTo(PLOT_AREA_90) > 0 && PlotArea.compareTo(PLOT_AREA_150) <= 0) {
					requiredPlotArea = " 90 to 150";
				} else if (PlotArea.compareTo(PLOT_AREA_150) > 0 && PlotArea.compareTo(PLOT_AREA_300) <= 0) {
					requiredPlotArea = " 150 to 400";
				} else if (PlotArea.compareTo(PLOT_AREA_300) > 0 && PlotArea.compareTo(PLOT_AREA_500) <= 0) {
					requiredPlotArea = "Above 300 to 500";
				} else if (PlotArea.compareTo(PLOT_AREA_500) > 0 && PlotArea.compareTo(PLOT_AREA_750) <= 0) {
					requiredPlotArea = "Above 500 to 750";
				} else if (PlotArea.compareTo(PLOT_AREA_750) > 0 && PlotArea.compareTo(PLOT_AREA_1000) <= 0) {
					requiredPlotArea = "Above 750 to 1000";
				} else if (PlotArea.compareTo(PLOT_AREA_1000) > 0 && PlotArea.compareTo(PLOT_AREA_1500) <= 0) {
					requiredPlotArea = "Above 1000 to 1500";
				} else if (PlotArea.compareTo(PLOT_AREA_1500) > 0 && PlotArea.compareTo(PLOT_AREA_2000) <= 0) {
					requiredPlotArea = "Above 1500 to 2000";
				} else if (PlotArea.compareTo(PLOT_AREA_2000) > 0 && PlotArea.compareTo(PLOT_AREA_2500) <= 0) {
					requiredPlotArea = "Above 2000 to 2500";
				} else {
					pl.addError("Coverage Plot Area", "Coverage: Plot Area Less Than 50 m or greater than 2500 m");
				}
				
			}
			processCoverage(pl, requiredPlotArea, coveragePercentage, requiredCoverage, status);
		} else if (mostRestrictiveOccupancyType.getType().getCode().equals(I)) {
			if(mostRestrictiveOccupancyType.getSubtype().getCode().equals(I1)) {
				if (PlotArea.compareTo(PLOT_AREA_300) <= 0 ) {
					requiredPlotArea = " upto 300";
					requiredCoverage=BigDecimal.valueOf(70);
				} else if (PlotArea.compareTo(PLOT_AREA_300) > 0 && PlotArea.compareTo(PLOT_AREA_500) <= 0) {
					requiredPlotArea = "Above 300 to 500";
					requiredCoverage=BigDecimal.valueOf(70);
				} else if (PlotArea.compareTo(PLOT_AREA_500) > 0 && PlotArea.compareTo(PLOT_AREA_750) <= 0) {
					requiredPlotArea = "Above 500 to 750";
					requiredCoverage=BigDecimal.valueOf(70);
				} else if (PlotArea.compareTo(PLOT_AREA_750) > 0 && PlotArea.compareTo(PLOT_AREA_1000) <= 0) {
					requiredPlotArea = "Above 750 to 1000";
					requiredCoverage=BigDecimal.valueOf(70);
				} else if (PlotArea.compareTo(PLOT_AREA_1000) > 0 && PlotArea.compareTo(PLOT_AREA_1500) <= 0) {
					requiredPlotArea = "Above 1000 to 1500";
					requiredCoverage=BigDecimal.valueOf(70);
				} else if (PlotArea.compareTo(PLOT_AREA_1500) > 0 ) {
					requiredPlotArea = "Above 1500 ";
					requiredCoverage=BigDecimal.valueOf(70);
				}
			}else if(mostRestrictiveOccupancyType.getSubtype().getCode().equals(I2)) {
				if (PlotArea.compareTo(PLOT_AREA_1000) > 0 && PlotArea.compareTo(PLOT_AREA_2000) <= 0) {
					requiredPlotArea = "Above 1000 to 2000";
					requiredCoverage=BigDecimal.valueOf(74);
				}else if (PlotArea.compareTo(PLOT_AREA_2000) > 0){
					requiredPlotArea = "Above 1000 to 2000";
					requiredCoverage=BigDecimal.valueOf(65);
				}
			}
			if(requiredCoverage.compareTo(coveragePercentage)<=0) {
				status="Accepted";
			}else {
				status="Not Accepted";
			}
			processCoverage(pl, requiredPlotArea, coveragePercentage, requiredCoverage, status);
		}
		
		

	}

	private BigDecimal derivedCoverage(BigDecimal frontsetback, BigDecimal rearsetback, BigDecimal side1setback,
			BigDecimal side2setback, BigDecimal s) {
		BigDecimal coverage = null;
		BigDecimal A = null;
		double sqA = 0, c = 0;
		A = ((s.subtract(frontsetback)).multiply(s.subtract(rearsetback)).multiply(s.subtract(side1setback))
				.multiply(s.subtract(side2setback)));
		c = A.doubleValue();
		sqA = Math.sqrt(c);
		coverage = BigDecimal.valueOf(sqA);
		return coverage;
	}

	private void processCoverage(Plan pl, String plotArea, BigDecimal coverage, BigDecimal requiredCoverage,
			String status) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Coverage");
		scrutinyDetail.setHeading("Coverage in Percentage");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(4, "Plot Area in m²");
		scrutinyDetail.addColumnHeading(5, "Calcucated Coverage in m²");
		scrutinyDetail.addColumnHeading(6, "Required Coverage in m²");
		scrutinyDetail.addColumnHeading(7, STATUS);

		String desc = "Coverage";

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, RULE_54_D);
		details.put(DESCRIPTION, desc);
		details.put("Plot Area in m²", plotArea);
		details.put("Calcucated Coverage in m²", coverage.toString());
		if (requiredCoverage.compareTo(BigDecimal.ZERO) > 0)
			details.put("Required Coverage in m²", requiredCoverage.toString());
		else
			details.put("Required Coverage in m²", "-");
		details.put(STATUS, status);

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

}
