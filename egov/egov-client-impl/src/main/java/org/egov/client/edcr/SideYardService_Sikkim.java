package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.SIDE_YARD1_DESC;
import static org.egov.edcr.utility.DcrConstants.SIDE_YARD2_DESC;
import static org.egov.edcr.utility.DcrConstants.SIDE_YARD_DESC;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.egov.client.edcr.util.Utility;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.DistanceToExternalEntity;
import org.egov.common.entity.edcr.Footpath;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Plot;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.common.entity.edcr.Yard;
import org.egov.edcr.feature.SideYardService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class SideYardService_Sikkim extends SideYardService {

	private static final Logger LOG = Logger.getLogger(SideYardService_Sikkim.class);

	private static final BigDecimal SIDEVALUE_ONE = BigDecimal.valueOf(1);
	private static final BigDecimal SIDEVALUE_ONE_TWO = BigDecimal.valueOf(1.2);
	private static final BigDecimal SIDEVALUE_ONEPOINTFIVE = BigDecimal.valueOf(1.5);
	private static final BigDecimal SIDEVALUE_ONEPOINTEIGHT = BigDecimal.valueOf(1.8);
	private static final BigDecimal SIDEVALUE_TWO = BigDecimal.valueOf(2);
	private static final BigDecimal SIDEVALUE_TWOPOINTFIVE = BigDecimal.valueOf(2.5);
	private static final BigDecimal SIDEVALUE_THREE = BigDecimal.valueOf(3);
	private static final BigDecimal SIDEVALUE_THREEPOINTSIX = BigDecimal.valueOf(3.66);
	private static final BigDecimal SIDEVALUE_FOUR = BigDecimal.valueOf(4);
	private static final BigDecimal SIDEVALUE_FOURPOINTFIVE = BigDecimal.valueOf(4.5);
	private static final BigDecimal SIDEVALUE_FIVE = BigDecimal.valueOf(5);
	private static final BigDecimal SIDEVALUE_SIX = BigDecimal.valueOf(6);
	private static final BigDecimal SIDEVALUE_SEVEN = BigDecimal.valueOf(7);
	private static final BigDecimal SIDEVALUE_SEVENTYFIVE = BigDecimal.valueOf(0.75);
	private static final BigDecimal SIDEVALUE_EIGHT = BigDecimal.valueOf(8);
	private static final BigDecimal SIDEVALUE_NINE = BigDecimal.valueOf(9);
	private static final BigDecimal SIDEVALUE_TEN = BigDecimal.valueOf(10);

	private static final String SIDENUMBER = "Side Number";
	private static final String MINIMUMLABEL = "Minimum distance ";

	private static final String RULE_35 = "35 Table-9";
	private static final String RULE_36 = "36";
	private static final String RULE_37_TWO_A = "37-2-A";
	private static final String RULE_37_TWO_B = "37-2-B";
	private static final String RULE_37_TWO_C = "37-2-C";
	private static final String RULE_37_TWO_D = "37-2-D";
	private static final String RULE_37_TWO_G = "37-2-G";
	private static final String RULE_37_TWO_H = "37-2-H";
	private static final String RULE_37_TWO_I = "37-2-I";
	private static final String RULE_47 = "47";
	private static final String SIDE_YARD_2_NOTDEFINED = "side2yardNodeDefined";
	private static final String SIDE_YARD_1_NOTDEFINED = "side1yardNodeDefined";

	public static final String BSMT_SIDE_YARD_DESC = "Basement Side Yard";
	private static final int PLOTAREA_300 = 300;
	public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);

	// PLOT AREA SIKKIM
	private static final BigDecimal PLOT_AREA_UPTO251 = BigDecimal.valueOf(251);
	private static final BigDecimal PLOT_AREA_ABOVE251TO502 = BigDecimal.valueOf(502);
	private static final BigDecimal PLOT_AREA_ABOVE502TO929 = BigDecimal.valueOf(929);
	private static final BigDecimal PLOT_AREA_ABOVE929 = BigDecimal.valueOf(929);

	// SIDE SETBACK OR YARD SIKKIM
	// Side Yard With Footpath & Independent Structure Minimum Distance
	private static final BigDecimal SIDEYARD_FOOTPATH_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2 = BigDecimal
			.valueOf(1.2);
	private static final BigDecimal SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	private static final BigDecimal SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	private static final BigDecimal SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);

	// SideYard With Road On Any Side & Independent Structure Minimum Distance
	private static final BigDecimal SIDEYARD_ROAD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);

	// Side Yard With Footpath & Adjoining buildings Minimum Distance
	private static final BigDecimal SIDEYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2 = BigDecimal
			.valueOf(1.2);
	private static final BigDecimal SIDEYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);

	// SideYard With Road On Any Side & Adjoining buildings Minimum Distance
	private static final BigDecimal SIDEYARD_ROAD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal SIDEYARD_ROAD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal SIDEYARD_ROAD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal SIDEYARD_ROAD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);

	// SideYard With Adjoining buildings Minimum Distance
	private static final BigDecimal SIDEYARD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9 = BigDecimal.valueOf(0.9);
	private static final BigDecimal SIDEYARD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	private static final BigDecimal SIDEYARD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	private static final BigDecimal SIDEYARD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5 = BigDecimal.valueOf(1.5);

	// Side Independent Stricture Adjoining Plots Minimum Distance
	private static final BigDecimal SIDEYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9 = BigDecimal.valueOf(0.9);
	private static final BigDecimal SIDEYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8 = BigDecimal.valueOf(1.8);
	private static final BigDecimal SIDEYARD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal SIDEYARD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal SIDEYARD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8 = BigDecimal.valueOf(1.8);

	private static final BigDecimal WITH_FOOTPATH_SK = BigDecimal.valueOf(1);
	private static final BigDecimal WITH_ROADONANYSIDE_SK = BigDecimal.valueOf(2);

	private static final String SIDEYARD_STRUCTURE_IND_DESC = "Independent structure";
	private static final String SIDEYARD_STRUCTURE_DEP_DESC = "Buildings attached in adjoining plots";

	private static final String SIDEYARD_FOOTHPATH_DESC = "with footpath";
	private static final String SIDEYARD_ROAD_DESC = "with road on any side";
	private static final String SIDEYARD_NONE_DESC = "-";

	private static final String SIDEYARD_PLOTUPTO251_DESC = "plot area up to 251 sq. M";
	private static final String SIDEYARD_PLOTABOVE251TO502_DESC = "plot area above 251 to 502 sq. M";
	private static final String SIDEYARD_PLOTABOVE502TO929_DESC = "plot area above 502 to 929 sq. M";
	private static final String SIDEYARD_PLOTABOVE929_DESC = "plot area above 929 sq. M";

	private static final String PLOTTYPE = "Plot type";
	private static final String STRUCTURE = "Adjoining Buildings";;
	private static final String SETBACK_SIDETYPE = "Setback Sidetype";

	private static final String ROAD_RESERVE_SIDE1 = "ROAD_RESERVE_SIDE1";
	private static final String ROAD_RESERVE_SIDE2 = "ROAD_RESERVE_SIDE2";
	private static final String SIDE1_ADJOINING = "SIDE1_ADJOINING";
	private static final String SIDE2_ADJOINING = "SIDE2_ADJOINING";

	private static BigDecimal ROAD_RESERVE1 = BigDecimal.ZERO;
	private static BigDecimal ROAD_RESERVE2 = BigDecimal.ZERO;
	private static final String OWNERSHIPTYPE = "Ownershiptype";
	private static final String BYELAW = " 4 & 18 2(i) & (ii) ";
	private Boolean sideYardDefined1 = false;
	private Boolean sideYardDefined2 = false;

	private class SideYardResult {
		String rule;
		String subRule;
		String blockName;
		Integer level;
		BigDecimal actualMeanDistance = BigDecimal.ZERO;
		BigDecimal actualDistance = BigDecimal.ZERO;
		String occupancy;
		BigDecimal expectedDistance = BigDecimal.ZERO;
		BigDecimal expectedmeanDistance = BigDecimal.ZERO;
		boolean status = false;

		String structureIndOrDep;
		String setbackWithFootpathOrRoadOrNone;
		String plotType;
		String ownershipType;
		BigDecimal noAdjoiningBuildings;

		@Override
		public String toString() {
			return "SideYardResult [rule=" + rule + ", subRule=" + subRule + ", blockName=" + blockName + ", level="
					+ level + ", actualMeanDistance=" + actualMeanDistance + ", actualDistance=" + actualDistance
					+ ", occupancy=" + occupancy + ", expectedDistance=" + expectedDistance + ", expectedmeanDistance="
					+ expectedmeanDistance + ", status=" + status + ", structureIndOrDep=" + structureIndOrDep
					+ ", setbackWithFootpathOrRoadOrNone=" + setbackWithFootpathOrRoadOrNone + ", plotType=" + plotType
					+ ", ownershipType=" + ownershipType + ", noAdjoiningBuildings=" + noAdjoiningBuildings + "]";
		}

	}

	public void processSideYard(final Plan pl) {

		HashMap<String, String> errors = new HashMap<>();
		Plot plot = null;
		if (pl.getPlot() == null)
			return;
		else
			plot=pl.getPlot();

		// Side yard 1 and side yard 2 both may not mandatory in same levels. Get
		// previous level side yards in this case.
		// In case of side yard 1 defined and other side not required, then consider
		// other side as zero distance ( in case of noc
		// provided cases).

		Boolean isAttachedSide1 = false, isAttachedSide2 = false, isPrivateSite = true,
				isRoadReserveSide1Declared = false, isRoadReserveSide2Declared = false;

		/* Note: roadSetbackValue to be defined since value may vary */

		BigDecimal noOfAdjoiningBuildings = BigDecimal.ZERO, plotArea = BigDecimal.ZERO,
				roadSetbackValue = BigDecimal.ZERO, withRoadorFootpathorNone = BigDecimal.ZERO;

		isRoadReserveSide1Declared = checkIfRoadReservePresent(pl, ROAD_RESERVE_SIDE1);
		isRoadReserveSide2Declared = checkIfRoadReservePresent(pl, ROAD_RESERVE_SIDE2);

		if (!isRoadReserveSide1Declared || !isRoadReserveSide2Declared || ROAD_RESERVE1.compareTo(BigDecimal.ZERO) <= 0
				|| ROAD_RESERVE2.compareTo(BigDecimal.ZERO) <= 0) {

			sideYardDefined1 = false;
			sideYardDefined2 = false;
			validateSideYardRule(pl, errors, isRoadReserveSide1Declared, isRoadReserveSide2Declared);

			Boolean valid = false;
			if (plot != null && !pl.getBlocks().isEmpty()) {
				if(plot.getArea()!=null)
					plotArea = plot.getArea();
				isAttachedSide1 = Utility.checkIfAdjoiningBuildingsPresent(pl, SIDE1_ADJOINING);
				isAttachedSide2 = Utility.checkIfAdjoiningBuildingsPresent(pl, SIDE2_ADJOINING);
				/*
				 * withRoadorFootpathorNone = Utility.checkIfRoadPresent(pl) ? new BigDecimal(2)
				 * : (Utility.checkIfFootpathPresent(pl) ? BigDecimal.ONE : BigDecimal.ZERO);
				 */
				withRoadorFootpathorNone = Utility.checkIfRoadPresent(pl) ? new BigDecimal(2) : BigDecimal.ZERO;

				if (isAttachedSide1 || isAttachedSide2)
					noOfAdjoiningBuildings = Utility.getNoOfAdjoiningBuildings(pl);

				for (Block block : pl.getBlocks()) { // for each block

					scrutinyDetail = new ScrutinyDetail();
					scrutinyDetail.addColumnHeading(1, RULE_NO);
					scrutinyDetail.addColumnHeading(3, OWNERSHIPTYPE);
					scrutinyDetail.addColumnHeading(5, PLOTTYPE);
					scrutinyDetail.addColumnHeading(6, SETBACK_SIDETYPE);
					scrutinyDetail.addColumnHeading(7, STRUCTURE);
					scrutinyDetail.addColumnHeading(8, FIELDVERIFIED);
					scrutinyDetail.addColumnHeading(9, PERMISSIBLE);
					scrutinyDetail.addColumnHeading(10, PROVIDED);
					scrutinyDetail.addColumnHeading(11, STATUS);
					scrutinyDetail.setHeading(SIDE_YARD_DESC);

					SideYardResult sideYard1Result = new SideYardResult();
					SideYardResult sideYard2Result = new SideYardResult();

					for (SetBack setback : block.getSetBacks()) {

						sideYard1Result.structureIndOrDep = isAttachedSide1 ? "Attached to adjoining buildings"
								: "No Buildings attached";
						sideYard2Result.structureIndOrDep = isAttachedSide2 ? "Attached to adjoining buildings"
								: "No Buildings attached";

						Yard sideYard1 = null;
						Yard sideYard2 = null;

						if (setback.getSideYard1() != null
								&& setback.getSideYard1().getMean().compareTo(BigDecimal.ZERO) > 0) {
							sideYard1 = setback.getSideYard1();
						}
						if (setback.getSideYard2() != null
								&& setback.getSideYard2().getMean().compareTo(BigDecimal.ZERO) > 0) {
							sideYard2 = setback.getSideYard2();
						}

						BigDecimal buildingHeight=null;
						double minlength = 0;
						double max = 0;
						double minMeanlength = 0;
						double maxMeanLength = 0;

						if (!isRoadReserveSide1Declared) {
							sideYard1Result.subRule = BYELAW;
							if (sideYard1 != null && sideYard1.getMinimumDistance()!=null) {
								sideYard1Result.actualDistance = sideYard1.getMinimumDistance();
							} else {
								sideYard1Result.actualDistance = BigDecimal.ZERO;
							}
						}

						if (!isRoadReserveSide2Declared) {
							sideYard2Result.subRule = BYELAW;
							if (sideYard2 != null && sideYard2.getMinimumDistance()!=null) {
								sideYard2Result.actualDistance = sideYard2.getMinimumDistance();
							} else {
								sideYard2Result.actualDistance = BigDecimal.ZERO;
							}
						}

						sideYard1Result.ownershipType = isPrivateSite ? "Private Site" : "Alloted Site";
						sideYard2Result.ownershipType = isPrivateSite ? "Private Site" : "Alloted Site";
						sideYard1Result.noAdjoiningBuildings = isAttachedSide1 ? noOfAdjoiningBuildings
								: BigDecimal.ZERO;
						sideYard2Result.noAdjoiningBuildings = isAttachedSide2 ? noOfAdjoiningBuildings
								: BigDecimal.ZERO;
						// sideYard2Result.setbackWithFootpathOrRoadOrNone =
						// withRoadorFootpathorNone.compareTo(new BigDecimal(2))==0?
						if (sideYard1 != null && sideYard1.getHeight() != null
								&& sideYard1.getHeight().compareTo(BigDecimal.ZERO) > 0 && sideYard2 != null
								&& sideYard2.getHeight() != null
								&& sideYard2.getHeight().compareTo(BigDecimal.ZERO) > 0) {
							buildingHeight = sideYard1.getHeight().compareTo(sideYard2.getHeight()) >= 0
									? sideYard1.getHeight()
									: sideYard2.getHeight();
						} else {
							buildingHeight = sideYard1 != null && sideYard1.getHeight() != null
									&& sideYard1.getHeight().compareTo(BigDecimal.ZERO) > 0
											? sideYard1.getHeight()
											: sideYard2 != null && sideYard2.getHeight() != null
													&& sideYard2.getHeight().compareTo(BigDecimal.ZERO) > 0
															? sideYard2.getHeight()
															: block.getBuilding().getBuildingHeight();
						}

						if (sideYard2 != null && sideYard1 != null) {
							if (sideYard2.getMinimumDistance().doubleValue() > sideYard1.getMinimumDistance()
									.doubleValue()) {
								minlength = sideYard1.getMinimumDistance().doubleValue();
								max = sideYard2.getMinimumDistance().doubleValue();
							} else {
								minlength = sideYard2.getMinimumDistance().doubleValue();
								max = sideYard1.getMinimumDistance().doubleValue();
							}
						} else {
							if (sideYard1 != null) {
								max = sideYard1.getMinimumDistance().doubleValue();

							} else if (sideYard2 != null) {
								minlength = sideYard2.getMinimumDistance().doubleValue();
							}
						}

						if (buildingHeight != null && (minlength > 0 || max > 0)) {
							scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Side Setback");
							if (sideYardDefined1) {
								if (plotArea.compareTo(PLOT_AREA_UPTO251) <= 0) {
									processSideYardUptoPlotArea251(pl, scrutinyDetail, sideYard1Result, block, errors,
											sideYard1.getMinimumDistance(), noOfAdjoiningBuildings, roadSetbackValue,
											withRoadorFootpathorNone, isAttachedSide1, isPrivateSite,
											isRoadReserveSide1Declared);
								} else if (plotArea.compareTo(PLOT_AREA_ABOVE251TO502) <= 0) {
									processFrontYardUptoPlotAreaAbove251To502(pl, scrutinyDetail, sideYard1Result,
											block, errors, sideYard1.getMinimumDistance(), noOfAdjoiningBuildings,
											roadSetbackValue, withRoadorFootpathorNone, isAttachedSide1, isPrivateSite,
											isRoadReserveSide1Declared);
								} else if (plotArea.compareTo(PLOT_AREA_ABOVE502TO929) <= 0) {
									processFrontYardUptoPlotAreaAbove502To929(pl, scrutinyDetail, sideYard1Result,
											block, errors, sideYard1.getMinimumDistance(), noOfAdjoiningBuildings,
											roadSetbackValue, withRoadorFootpathorNone, isAttachedSide1, isPrivateSite,
											isRoadReserveSide1Declared);
								} else {
									processFrontYardUptoPlotAreaAbove929(pl, scrutinyDetail, sideYard1Result, block,
											errors, sideYard1.getMinimumDistance(), noOfAdjoiningBuildings,
											roadSetbackValue, withRoadorFootpathorNone, isAttachedSide1, isPrivateSite,
											isRoadReserveSide1Declared);
								}
							}
							if (sideYardDefined2) {
								if (plotArea.compareTo(PLOT_AREA_UPTO251) <= 0) {
									processSideYardUptoPlotArea251(pl, scrutinyDetail, sideYard2Result, block, errors,
											sideYard2.getMinimumDistance(), noOfAdjoiningBuildings, roadSetbackValue,
											withRoadorFootpathorNone, isAttachedSide2, isPrivateSite,
											isRoadReserveSide2Declared);
								} else if (plotArea.compareTo(PLOT_AREA_ABOVE251TO502) <= 0) {
									processFrontYardUptoPlotAreaAbove251To502(pl, scrutinyDetail, sideYard2Result,
											block, errors, sideYard2.getMinimumDistance(), noOfAdjoiningBuildings,
											roadSetbackValue, withRoadorFootpathorNone, isAttachedSide2, isPrivateSite,
											isRoadReserveSide2Declared);
								} else if (plotArea.compareTo(PLOT_AREA_ABOVE502TO929) <= 0) {

									processFrontYardUptoPlotAreaAbove502To929(pl, scrutinyDetail, sideYard2Result,
											block, errors, sideYard2.getMinimumDistance(), noOfAdjoiningBuildings,
											roadSetbackValue, withRoadorFootpathorNone, isAttachedSide2, isPrivateSite,
											isRoadReserveSide2Declared);
								} else {

									processFrontYardUptoPlotAreaAbove929(pl, scrutinyDetail, sideYard2Result, block,
											errors, sideYard2.getMinimumDistance(), noOfAdjoiningBuildings,
											roadSetbackValue, withRoadorFootpathorNone, isAttachedSide2, isPrivateSite,
											isRoadReserveSide2Declared);
								}
							}

							/*
							 * if (plotArea.compareTo(PLOT_AREA_UPTO251) <= 0) {
							 * processSideYardUptoPlotArea251(pl, scrutinyDetail, sideYard1Result,
							 * sideYard2Result, block, errors, sideYard1.getMinimumDistance(),
							 * sideYard2.getMinimumDistance(), noOfAdjoiningBuildings, roadSetbackValue,
							 * withRoadorFootpathorNone, isAttachedSide1, isAttachedSide2, isPrivateSite,
							 * isRoadReserveSide1Declared, isRoadReserveSide2Declared);
							 * 
							 * } else if (plotArea.compareTo(PLOT_AREA_ABOVE251TO502) <= 0) {
							 * processFrontYardUptoPlotAreaAbove251To502(pl, scrutinyDetail,
							 * sideYard1Result, sideYard2Result, block, errors,
							 * sideYard1.getMinimumDistance(), sideYard2.getMinimumDistance(),
							 * noOfAdjoiningBuildings, roadSetbackValue, withRoadorFootpathorNone,
							 * isAttachedSide1, isAttachedSide2, isPrivateSite, isRoadReserveSide1Declared,
							 * isRoadReserveSide2Declared); } else if
							 * (plotArea.compareTo(PLOT_AREA_ABOVE502TO929) <= 0) {
							 * processFrontYardUptoPlotAreaAbove502To929(pl, scrutinyDetail,
							 * sideYard1Result, sideYard2Result, block, errors,
							 * sideYard1.getMinimumDistance(), sideYard2.getMinimumDistance(),
							 * noOfAdjoiningBuildings, roadSetbackValue, withRoadorFootpathorNone,
							 * isAttachedSide1, isAttachedSide2, isPrivateSite, isRoadReserveSide1Declared,
							 * isRoadReserveSide2Declared); } else {
							 * processFrontYardUptoPlotAreaAbove929(pl, scrutinyDetail, sideYard1Result,
							 * sideYard2Result, block, errors, sideYard1.getMinimumDistance(),
							 * sideYard2.getMinimumDistance(), noOfAdjoiningBuildings, roadSetbackValue,
							 * withRoadorFootpathorNone, isAttachedSide1, isAttachedSide2, isPrivateSite,
							 * isRoadReserveSide1Declared, isRoadReserveSide2Declared); }
							 */

							setReportOutputDetails(pl, scrutinyDetail, sideYard1Result, sideYard2Result,
									isRoadReserveSide1Declared, isRoadReserveSide2Declared, errors);
						}
					}
				}
			}

		}

	}

	private void validateSideYardRule(final Plan pl, HashMap<String, String> errors, Boolean isRoadReserveSide1Declared,
			Boolean isRoadReserveSide2Declared) {
		LOG.info("validateSideYardRule");
		for (Block block : pl.getBlocks()) {
			if (!block.getCompletelyExisting()) {
				/*
				 * Boolean sideYardDefined1 = false; Boolean sideYardDefined2 = false;
				 */
				for (SetBack setback : block.getSetBacks()) {
					if (setback.getSideYard1() != null
							&& setback.getSideYard1().getMinimumDistance().compareTo(BigDecimal.valueOf(0)) > 0) {
						sideYardDefined1 = true;
					}
					if (setback.getSideYard2() != null
							&& setback.getSideYard2().getMinimumDistance().compareTo(BigDecimal.valueOf(0)) > 0) {
						sideYardDefined2 = true;
					}
				}
				if (!sideYardDefined1 && !isRoadReserveSide1Declared) {
					errors.put(SIDE_YARD_1_NOTDEFINED,
							prepareMessage(OBJECTNOTDEFINED, SIDE_YARD1_DESC + " for Block " + block.getName()));
					pl.addErrors(errors);
				}
				if (!sideYardDefined2 && !isRoadReserveSide2Declared) {
					errors.put(SIDE_YARD_2_NOTDEFINED,
							prepareMessage(OBJECTNOTDEFINED, SIDE_YARD2_DESC + " for Block " + block.getName()));
					pl.addErrors(errors);
				}
			}

		}
		/*
		 * errors.entrySet().forEach(entry -> LOG.info("KEY: " + entry.getKey() +
		 * "  VALLUE: " + entry.getValue()));
		 */

	}

	private void processSideYardUptoPlotArea251(Plan pl, ScrutinyDetail scrutinyDetail, SideYardResult sideYardResult,
			Block block, HashMap<String, String> errors, BigDecimal minSideYard, BigDecimal noOfAdjoiningBuildings,
			BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone, Boolean isAttachedSide,
			Boolean isPrivateSite, Boolean isRoadReserveDeclared) {

		if (isPrivateSite) {
			sideYardResult.plotType = SIDEYARD_PLOTUPTO251_DESC;
			switch (withRoadorFootpathorNone.intValue()) {
			case 1:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_FOOTHPATH_DESC;
				if (isAttachedSide) {
					if (noOfAdjoiningBuildings.compareTo(BigDecimal.ONE) == 0)
						minSideYard = SIDEYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2;
					else
						minSideYard = SIDEYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8;
				} else {
					minSideYard = SIDEYARD_FOOTPATH_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2;
				}

				break;
			case 2:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_ROAD_DESC;

				if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) {
					minSideYard = roadSetbackValue;
				} else {
					if (isAttachedSide) {
						minSideYard = SIDEYARD_ROAD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0;
					} else {
						minSideYard = SIDEYARD_ROAD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0;
					}
				}
				break;
			default:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_NONE_DESC;
				if (isAttachedSide)
					if (noOfAdjoiningBuildings.compareTo(BigDecimal.ONE) == 0)
						minSideYard = SIDEYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9;
					else
						minSideYard = SIDEYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8;
				else
					minSideYard = SIDEYARD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9;
			}

			sideYardResult.expectedDistance = minSideYard;

			if (sideYardResult.actualDistance.compareTo(sideYardResult.expectedDistance) >= 0)
				sideYardResult.status = true;
			else
				sideYardResult.status = false;

		} else {
			/* No setback validation required */
			/* minFrontYard = BigDecimal.ZERO; */
		}

	}

	/*
	 * private void processSideYardUptoPlotArea251(Plan pl, ScrutinyDetail
	 * scrutinyDetail, SideYardResult sideYard1Result, SideYardResult
	 * sideYard2Result, Block block, HashMap<String, String> errors, BigDecimal
	 * minSideYard1, BigDecimal minSideYard2, BigDecimal noOfAdjoiningBuildings,
	 * BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone, Boolean
	 * isAttachedSide1, Boolean isAttachedSide2, Boolean isPrivateSite, Boolean
	 * isRoadReserveSide1Declared, Boolean isRoadReserveSide2Declared) {
	 * 
	 * if (isPrivateSite) { sideYard1Result.plotType = SIDEYARD_PLOTUPTO251_DESC;
	 * sideYard2Result.plotType = SIDEYARD_PLOTUPTO251_DESC; switch
	 * (withRoadorFootpathorNone.intValue()) { case 1:
	 * sideYard1Result.setbackWithFootpathOrRoadOrNone = SIDEYARD_FOOTHPATH_DESC;
	 * sideYard2Result.setbackWithFootpathOrRoadOrNone = SIDEYARD_FOOTHPATH_DESC; if
	 * (isAttachedSide1) { if (noOfAdjoiningBuildings.compareTo(BigDecimal.ONE) ==
	 * 0) minSideYard1 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2; else
	 * minSideYard1 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8; } else {
	 * minSideYard1 =
	 * SIDEYARD_FOOTPATH_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2; }
	 * 
	 * if (isAttachedSide2) { if (noOfAdjoiningBuildings.compareTo(BigDecimal.ONE)
	 * == 0) minSideYard2 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2; else
	 * minSideYard2 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8; } else {
	 * minSideYard2 =
	 * SIDEYARD_FOOTPATH_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2; } break;
	 * case 2: sideYard1Result.setbackWithFootpathOrRoadOrNone = SIDEYARD_ROAD_DESC;
	 * sideYard2Result.setbackWithFootpathOrRoadOrNone = SIDEYARD_ROAD_DESC;
	 * 
	 * if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) { minSideYard1 =
	 * roadSetbackValue; minSideYard2 = roadSetbackValue; } else { if
	 * (isAttachedSide1) { minSideYard1 =
	 * SIDEYARD_ROAD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0; } else {
	 * minSideYard1 = SIDEYARD_ROAD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0;
	 * } if (isAttachedSide2) { minSideYard2 =
	 * SIDEYARD_ROAD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0; } else {
	 * minSideYard2 = SIDEYARD_ROAD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0;
	 * } } break; default: sideYard1Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_NONE_DESC; sideYard2Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_NONE_DESC; if (isAttachedSide1) if
	 * (noOfAdjoiningBuildings.compareTo(BigDecimal.ONE) == 0) minSideYard1 =
	 * SIDEYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9; else minSideYard1 =
	 * SIDEYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8; else minSideYard1 =
	 * SIDEYARD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9;
	 * 
	 * if (isAttachedSide2) if (noOfAdjoiningBuildings.compareTo(BigDecimal.ONE) ==
	 * 0) minSideYard2 = SIDEYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9;
	 * else minSideYard2 = SIDEYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8;
	 * else minSideYard2 = SIDEYARD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9;
	 * }
	 * 
	 * sideYard1Result.expectedDistance = minSideYard1;
	 * sideYard2Result.expectedDistance = minSideYard2;
	 * 
	 * if
	 * (sideYard1Result.actualDistance.compareTo(sideYard1Result.expectedDistance)
	 * >= 0) sideYard1Result.status = true; else sideYard1Result.status = false; if
	 * (sideYard2Result.actualDistance.compareTo(sideYard2Result.expectedDistance)
	 * >= 0) sideYard2Result.status = true; else sideYard2Result.status = false;
	 * 
	 * 
	 * setReportOutputDetails(pl, scrutinyDetail, sideYard1Result, sideYard2Result,
	 * isRoadReserveSide1Declared, isRoadReserveSide2Declared, errors);
	 * 
	 * } else { No setback validation required minFrontYard = BigDecimal.ZERO; }
	 * 
	 * }
	 */
	private void processFrontYardUptoPlotAreaAbove251To502(Plan pl, ScrutinyDetail scrutinyDetail,
			SideYardResult sideYardResult, Block block, HashMap<String, String> errors, BigDecimal minSideYard,
			BigDecimal noOfAdjoiningBuildings, BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone,
			Boolean isAttachedSide, Boolean isPrivateSite, Boolean isRoadReserveDeclared) {

		if (isPrivateSite) {
			sideYardResult.plotType = SIDEYARD_PLOTABOVE251TO502_DESC;
			switch (withRoadorFootpathorNone.intValue()) {
			case 1:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_FOOTHPATH_DESC;
				if (isAttachedSide) {
					minSideYard = SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8;
				} else {
					minSideYard = SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5;
				}
				break;
			case 2:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_ROAD_DESC;

				if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) {
					minSideYard = roadSetbackValue;
				} else {
					if (isAttachedSide) {
						minSideYard = SIDEYARD_ROAD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0;
					} else {
						minSideYard = SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0;
					}
				}
				break;
			default:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_NONE_DESC;
				if (isAttachedSide)
					minSideYard = SIDEYARD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8;
				else
					minSideYard = SIDEYARD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5;

			}

			sideYardResult.expectedDistance = minSideYard;

			if (sideYardResult.actualDistance.compareTo(sideYardResult.expectedDistance) >= 0)
				sideYardResult.status = true;
			else
				sideYardResult.status = false;

		} else {
			/* No setback validation required */
			/* minFrontYard = BigDecimal.ZERO; */
		}

	}

	/*
	 * private void processFrontYardUptoPlotAreaAbove251To502(Plan pl,
	 * ScrutinyDetail scrutinyDetail, SideYardResult sideYard1Result, SideYardResult
	 * sideYard2Result, Block block, HashMap<String, String> errors, BigDecimal
	 * minSideYard1, BigDecimal minSideYard2, BigDecimal noOfAdjoiningBuildings,
	 * BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone, Boolean
	 * isAttachedSide1, Boolean isAttachedSide2, Boolean isPrivateSite, Boolean
	 * isRoadReserveSide1Declared, Boolean isRoadReserveSide2Declared) {
	 * 
	 * if (isPrivateSite) { sideYard1Result.plotType =
	 * SIDEYARD_PLOTABOVE251TO502_DESC; sideYard2Result.plotType =
	 * SIDEYARD_PLOTABOVE251TO502_DESC; switch (withRoadorFootpathorNone.intValue())
	 * { case 1: sideYard1Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_FOOTHPATH_DESC; sideYard2Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_FOOTHPATH_DESC; if (isAttachedSide1) { minSideYard1 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8; } else
	 * { minSideYard1 =
	 * SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5; }
	 * 
	 * if (isAttachedSide2) { minSideYard2 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8; } else
	 * { minSideYard2 =
	 * SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5; }
	 * break; case 2: sideYard1Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_ROAD_DESC; sideYard2Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_ROAD_DESC;
	 * 
	 * if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) { minSideYard1 =
	 * roadSetbackValue; minSideYard2 = roadSetbackValue; } else { if
	 * (isAttachedSide1) { minSideYard1 =
	 * SIDEYARD_ROAD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0; } else {
	 * minSideYard1 =
	 * SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0; } if
	 * (isAttachedSide2) { minSideYard2 =
	 * SIDEYARD_ROAD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0; } else {
	 * minSideYard2 =
	 * SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0; } }
	 * break; default: sideYard1Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_NONE_DESC; sideYard2Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_NONE_DESC; if (isAttachedSide1) minSideYard1 =
	 * SIDEYARD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8; else
	 * minSideYard1 =
	 * SIDEYARD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5;
	 * 
	 * if (isAttachedSide2) minSideYard2 =
	 * SIDEYARD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8; else
	 * minSideYard2 =
	 * SIDEYARD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5; }
	 * 
	 * sideYard1Result.expectedDistance = minSideYard1;
	 * sideYard2Result.expectedDistance = minSideYard2;
	 * 
	 * if
	 * (sideYard1Result.actualDistance.compareTo(sideYard1Result.expectedDistance)
	 * >= 0) sideYard1Result.status = true; else sideYard1Result.status = false; if
	 * (sideYard2Result.actualDistance.compareTo(sideYard2Result.expectedDistance)
	 * >= 0) sideYard2Result.status = true; else sideYard2Result.status = false;
	 * 
	 * } else { No setback validation required minFrontYard = BigDecimal.ZERO; }
	 * 
	 * }
	 */
	private void processFrontYardUptoPlotAreaAbove502To929(Plan pl, ScrutinyDetail scrutinyDetail,
			SideYardResult sideYardResult, Block block, HashMap<String, String> errors, BigDecimal minSideYard,
			BigDecimal noOfAdjoiningBuildings, BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone,
			Boolean isAttachedSide, Boolean isPrivateSite, Boolean isRoadReserveDeclared) {

		if (isPrivateSite) {
			sideYardResult.plotType = SIDEYARD_PLOTABOVE502TO929_DESC;
			switch (withRoadorFootpathorNone.intValue()) {
			case 1:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_FOOTHPATH_DESC;
				if (isAttachedSide) {
					minSideYard = SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8;
				} else {
					minSideYard = SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5;
				}

				break;
			case 2:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_ROAD_DESC;

				if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) {
					minSideYard = roadSetbackValue;
				} else {
					if (isAttachedSide) {
						minSideYard = SIDEYARD_ROAD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0;
					} else {
						minSideYard = SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0;
					}
				}
				break;
			default:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_NONE_DESC;
				if (isAttachedSide)
					minSideYard = SIDEYARD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8;
				else
					minSideYard = SIDEYARD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5;

			}

			sideYardResult.expectedDistance = minSideYard;

			if (sideYardResult.actualDistance.compareTo(sideYardResult.expectedDistance) >= 0)
				sideYardResult.status = true;
			else
				sideYardResult.status = false;

		} else {
			/* No setback validation required */
			/* minFrontYard = BigDecimal.ZERO; */
		}

	}

	/*
	 * private void processFrontYardUptoPlotAreaAbove502To929(Plan pl,
	 * ScrutinyDetail scrutinyDetail, SideYardResult sideYard1Result, SideYardResult
	 * sideYard2Result, Block block, HashMap<String, String> errors, BigDecimal
	 * minSideYard1, BigDecimal minSideYard2, BigDecimal noOfAdjoiningBuildings,
	 * BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone, Boolean
	 * isAttachedSide1, Boolean isAttachedSide2, Boolean isPrivateSite, Boolean
	 * isRoadReserveSide1Declared, Boolean isRoadReserveSide2Declared) {
	 * 
	 * if (isPrivateSite) { sideYard1Result.plotType =
	 * SIDEYARD_PLOTABOVE502TO929_DESC; sideYard2Result.plotType =
	 * SIDEYARD_PLOTABOVE502TO929_DESC; switch (withRoadorFootpathorNone.intValue())
	 * { case 1: sideYard1Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_FOOTHPATH_DESC; sideYard2Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_FOOTHPATH_DESC; if (isAttachedSide1) { minSideYard1 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8; } else
	 * { minSideYard1 =
	 * SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5; }
	 * 
	 * if (isAttachedSide2) { minSideYard2 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8; } else
	 * { minSideYard2 =
	 * SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5; }
	 * break; case 2: sideYard1Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_ROAD_DESC; sideYard2Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_ROAD_DESC;
	 * 
	 * if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) { minSideYard1 =
	 * roadSetbackValue; minSideYard2 = roadSetbackValue; } else { if
	 * (isAttachedSide1) { minSideYard1 =
	 * SIDEYARD_ROAD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0; } else {
	 * minSideYard1 =
	 * SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0; } if
	 * (isAttachedSide2) { minSideYard2 =
	 * SIDEYARD_ROAD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0; } else {
	 * minSideYard2 =
	 * SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0; } }
	 * break; default: sideYard1Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_NONE_DESC; sideYard2Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_NONE_DESC; if (isAttachedSide1) minSideYard1 =
	 * SIDEYARD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8; else
	 * minSideYard1 =
	 * SIDEYARD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5;
	 * 
	 * if (isAttachedSide2) minSideYard2 =
	 * SIDEYARD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8; else
	 * minSideYard2 =
	 * SIDEYARD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5; }
	 * 
	 * sideYard1Result.expectedDistance = minSideYard1;
	 * sideYard2Result.expectedDistance = minSideYard2;
	 * 
	 * if
	 * (sideYard1Result.actualDistance.compareTo(sideYard1Result.expectedDistance)
	 * >= 0) sideYard1Result.status = true; else sideYard1Result.status = false; if
	 * (sideYard2Result.actualDistance.compareTo(sideYard2Result.expectedDistance)
	 * >= 0) sideYard2Result.status = true; else sideYard2Result.status = false;
	 * 
	 * 
	 * setReportOutputDetails(pl, scrutinyDetail, sideYard1Result, sideYard2Result,
	 * isRoadReserveSide1Declared, isRoadReserveSide2Declared, errors);
	 * 
	 * 
	 * } else { No setback validation required minFrontYard = BigDecimal.ZERO; }
	 * 
	 * }
	 */
	private void processFrontYardUptoPlotAreaAbove929(Plan pl, ScrutinyDetail scrutinyDetail,
			SideYardResult sideYardResult, Block block, HashMap<String, String> errors, BigDecimal minSideYard,
			BigDecimal noOfAdjoiningBuildings, BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone,
			Boolean isAttachedSide, Boolean isPrivateSite, Boolean isRoadReserveSideDeclared) {

		if (isPrivateSite) {
			sideYardResult.plotType = SIDEYARD_PLOTABOVE929_DESC;
			switch (withRoadorFootpathorNone.intValue()) {
			case 1:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_FOOTHPATH_DESC;
				if (isAttachedSide) {
					minSideYard = SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8;
				} else {
					minSideYard = SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5;
				}

				break;
			case 2:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_ROAD_DESC;

				if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) {
					minSideYard = roadSetbackValue;
				} else {
					if (isAttachedSide) {
						minSideYard = SIDEYARD_ROAD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0;
					} else {
						minSideYard = SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0;
					}
				}
				break;
			default:
				sideYardResult.setbackWithFootpathOrRoadOrNone = SIDEYARD_NONE_DESC;
				if (isAttachedSide)
					minSideYard = SIDEYARD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8;
				else
					minSideYard = SIDEYARD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5;

			}

			sideYardResult.expectedDistance = minSideYard;

			if (sideYardResult.actualDistance.compareTo(sideYardResult.expectedDistance) >= 0)
				sideYardResult.status = true;
			else
				sideYardResult.status = false;

		} else {
			/* No setback validation required */
			/* minFrontYard = BigDecimal.ZERO; */
		}

	}

	/*
	 * private void processFrontYardUptoPlotAreaAbove929(Plan pl, ScrutinyDetail
	 * scrutinyDetail, SideYardResult sideYard1Result, SideYardResult
	 * sideYard2Result, Block block, HashMap<String, String> errors, BigDecimal
	 * minSideYard1, BigDecimal minSideYard2, BigDecimal noOfAdjoiningBuildings,
	 * BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone, Boolean
	 * isAttachedSide1, Boolean isAttachedSide2, Boolean isPrivateSite, Boolean
	 * isRoadReserveSide1Declared, Boolean isRoadReserveSide2Declared) {
	 * 
	 * if (isPrivateSite) { sideYard1Result.plotType = SIDEYARD_PLOTABOVE929_DESC;
	 * sideYard2Result.plotType = SIDEYARD_PLOTABOVE929_DESC; switch
	 * (withRoadorFootpathorNone.intValue()) { case 1:
	 * sideYard1Result.setbackWithFootpathOrRoadOrNone = SIDEYARD_FOOTHPATH_DESC;
	 * sideYard2Result.setbackWithFootpathOrRoadOrNone = SIDEYARD_FOOTHPATH_DESC; if
	 * (isAttachedSide1) { minSideYard1 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8; } else {
	 * minSideYard1 =
	 * SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5; }
	 * 
	 * if (isAttachedSide2) { minSideYard2 =
	 * SIDEYARD_FOOTPATH_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8; } else {
	 * minSideYard2 =
	 * SIDEYARD_FOOTPATH_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5; } break;
	 * case 2: sideYard1Result.setbackWithFootpathOrRoadOrNone = SIDEYARD_ROAD_DESC;
	 * sideYard2Result.setbackWithFootpathOrRoadOrNone = SIDEYARD_ROAD_DESC;
	 * 
	 * if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) { minSideYard1 =
	 * roadSetbackValue; minSideYard2 = roadSetbackValue; } else { if
	 * (isAttachedSide1) { minSideYard1 =
	 * SIDEYARD_ROAD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0; } else {
	 * minSideYard1 =
	 * SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0; } if
	 * (isAttachedSide2) { minSideYard2 =
	 * SIDEYARD_ROAD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0; } else {
	 * minSideYard2 =
	 * SIDEYARD_ROAD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0; } } break;
	 * default: sideYard1Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_NONE_DESC; sideYard2Result.setbackWithFootpathOrRoadOrNone =
	 * SIDEYARD_NONE_DESC; if (isAttachedSide1) minSideYard1 =
	 * SIDEYARD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8; else minSideYard1 =
	 * SIDEYARD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5;
	 * 
	 * if (isAttachedSide2) minSideYard2 =
	 * SIDEYARD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8; else minSideYard2 =
	 * SIDEYARD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5; }
	 * 
	 * sideYard1Result.expectedDistance = minSideYard1;
	 * sideYard2Result.expectedDistance = minSideYard2;
	 * 
	 * if
	 * (sideYard1Result.actualDistance.compareTo(sideYard1Result.expectedDistance)
	 * >= 0) sideYard1Result.status = true; else sideYard1Result.status = false; if
	 * (sideYard2Result.actualDistance.compareTo(sideYard2Result.expectedDistance)
	 * >= 0) sideYard2Result.status = true; else sideYard2Result.status = false;
	 * 
	 * 
	 * setReportOutputDetails(pl, scrutinyDetail, sideYard1Result, sideYard2Result,
	 * isRoadReserveSide1Declared, isRoadReserveSide2Declared, errors);
	 * 
	 * 
	 * } else { No setback validation required minFrontYard = BigDecimal.ZERO; }
	 * 
	 * }
	 */
	private void setReportOutputDetails(Plan pl, ScrutinyDetail scrutinyDetail, SideYardResult sideYard1Result,
			SideYardResult sideYard2Result, Boolean isRoadReserveSide1Declared, Boolean isRoadReserveSide2Declared,
			HashMap<String, String> errors) {

		if (errors.isEmpty() || !errors.containsKey(SIDE_YARD_1_NOTDEFINED)) {
			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, sideYard1Result.subRule);
			details.put(OWNERSHIPTYPE, sideYard1Result.ownershipType);
			details.put(PLOTTYPE, sideYard1Result.plotType);
			details.put(SETBACK_SIDETYPE, sideYard1Result.setbackWithFootpathOrRoadOrNone);
			details.put(STRUCTURE, sideYard1Result.structureIndOrDep);
			details.put(FIELDVERIFIED, MINIMUMLABEL);
			details.put(PERMISSIBLE, sideYard1Result.expectedDistance.toString() + DcrConstants.IN_METER);
			details.put(PROVIDED,
					sideYard1Result.actualDistance
							.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
							.toString() + DcrConstants.IN_METER);
			details.put(SIDENUMBER, SIDE_YARD1_DESC);
			if (sideYard1Result.status) {
				details.put(STATUS, Result.Accepted.getResultVal());
			} else {
				details.put(STATUS, Result.Not_Accepted.getResultVal());
			}
			scrutinyDetail.getDetail().add(details);
			pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		}

		if (errors.isEmpty() || !errors.containsKey(SIDE_YARD_2_NOTDEFINED)) {
			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, sideYard2Result.subRule);
			details.put(OWNERSHIPTYPE, sideYard2Result.ownershipType);
			details.put(PLOTTYPE, sideYard2Result.plotType);
			details.put(SETBACK_SIDETYPE, sideYard2Result.setbackWithFootpathOrRoadOrNone);
			details.put(STRUCTURE, sideYard2Result.structureIndOrDep);
			details.put(FIELDVERIFIED, MINIMUMLABEL);
			details.put(PERMISSIBLE, sideYard2Result.expectedDistance.toString() + DcrConstants.IN_METER);
			details.put(PROVIDED,
					sideYard2Result.actualDistance
							.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
							.toString() + DcrConstants.IN_METER);
			details.put(SIDENUMBER, SIDE_YARD2_DESC);
			if (sideYard2Result.status) {
				details.put(STATUS, Result.Accepted.getResultVal());
			} else {
				details.put(STATUS, Result.Not_Accepted.getResultVal());
			}
			scrutinyDetail.getDetail().add(details);
			pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		}

	}

//	private Boolean checkIfRoadPresent(Plan pl) {
//
//		Boolean hasRoad = false;
//
//		if (pl.getNonNotifiedRoads() != null && !pl.getNonNotifiedRoads().isEmpty())
//			hasRoad = true;
//		else if (pl.getNotifiedRoads() != null && !pl.getNotifiedRoads().isEmpty())
//			hasRoad = true;
//		else if (pl.getCuldeSacRoads() != null && !pl.getCuldeSacRoads().isEmpty())
//			hasRoad = true;
//		else if (pl.getLaneRoads() != null && !pl.getLaneRoads().isEmpty())
//			hasRoad = true;
//		else
//			hasRoad = false;
//
//		return hasRoad;
//	}
//
//	private Boolean checkIfFootpathPresent(Plan pl) {
//
//		Boolean hasFootpath = false;
//
//		DistanceToExternalEntity distanceToExternalEntity = pl.getDistanceToExternalEntity();
//		Footpath footpaths = distanceToExternalEntity.getFootpath();
//
//		if (footpaths != null)
//			hasFootpath = true;
//
//		return hasFootpath;
//	}
//
//	private Boolean checkIfAdjoiningBuildingsPresent(Plan pl, String adjoiningType) {
//		/* To be determined from declaration */
//
//		Boolean hasAdjoiningBuildings = false;
//		try {
//			String attached = new String(
//					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals(adjoiningType))
//							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));
//			if (attached != null && !attached.isEmpty())
//				hasAdjoiningBuildings = true;
//		} catch (Exception e) {
//			LOG.info("Error: " + e);
//		}
//
//		return hasAdjoiningBuildings;
//	}

	private Boolean checkIfRoadReservePresent(Plan pl, String roadReserve) {
		/* If RoadReserve Present No Validation of Setback required */
		Boolean hasRoadReserve = false;

		if (pl.getRoadReserves() != null && !pl.getRoadReserves().isEmpty()) {

			hasRoadReserve = pl.getRoadReserves().stream().anyMatch(road -> {
				final Boolean hRR = road.getName().equalsIgnoreCase(roadReserve);

				if (hRR && roadReserve.equalsIgnoreCase("ROAD_RESERVE_SIDE1")&&!road.getShortestDistanceToRoad().isEmpty()&&road.getShortestDistanceToRoad()!=null) {
					ROAD_RESERVE1 = road.getShortestDistanceToRoad().stream().reduce(BigDecimal::min).get();

					if (hRR && ROAD_RESERVE1.compareTo(BigDecimal.ZERO) == 0)
						pl.addError(String.format("%s shortest distance to road cannot be 0", roadReserve),
								String.format("%s shortest distance to road cannot be 0", roadReserve));
				}
				if (hRR && roadReserve.equalsIgnoreCase("ROAD_RESERVE_SIDE2")&&!road.getShortestDistanceToRoad().isEmpty()&&road.getShortestDistanceToRoad()!=null) {
					ROAD_RESERVE2 = road.getShortestDistanceToRoad().stream().reduce(BigDecimal::min).get();

					if (hRR && ROAD_RESERVE2.compareTo(BigDecimal.ZERO) == 0)
						pl.addError(String.format("%s shortest distance to road cannot be 0", roadReserve),
								String.format("%s shortest distance to road cannot be 0", roadReserve));
				}

				return hRR;
			});

		}

		return hasRoadReserve;
	}

//	private BigDecimal getNoOfAdjoiningBuildings(Plan pl) {
//		/* To be calculated once adjoining buildings are declared */
//		BigDecimal noOfAdjoiningBuildindgs = BigDecimal.ZERO;
//		try {
//
//			String FRONT_ADJOINING = new String(
//					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals("FRONT_ADJOINING"))
//							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));
//			String REAR_ADJOINING = new String(
//					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals("REAR_ADJOINING"))
//							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));
//			String SIDE1_ADJOINING = new String(
//					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals("SIDE1_ADJOINING"))
//							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));
//			String SIDE2_ADJOINING = new String(
//					pl.getPlanInfoProperties().entrySet().stream().filter(e -> e.getKey().equals("SIDE2_ADJOINING"))
//							.map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY));
//
//			if (FRONT_ADJOINING != null && !FRONT_ADJOINING.isEmpty())
//				noOfAdjoiningBuildindgs.add(BigDecimal.ONE);
//			if (REAR_ADJOINING != null && !REAR_ADJOINING.isEmpty())
//				noOfAdjoiningBuildindgs.add(BigDecimal.ONE);
//			if (SIDE1_ADJOINING != null && !SIDE1_ADJOINING.isEmpty())
//				noOfAdjoiningBuildindgs.add(BigDecimal.ONE);
//			if (SIDE2_ADJOINING != null && !SIDE2_ADJOINING.isEmpty())
//				noOfAdjoiningBuildindgs.add(BigDecimal.ONE);
//
//		} catch (Exception e) {
//			LOG.info("ERROR: " + e);
//		}
//
//		return noOfAdjoiningBuildindgs;
//	}
}