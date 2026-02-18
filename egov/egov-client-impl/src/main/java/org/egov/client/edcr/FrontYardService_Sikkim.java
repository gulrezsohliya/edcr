package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.client.edcr.util.Utility;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.DistanceToExternalEntity;
import org.egov.common.entity.edcr.Footpath;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Plot;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Road;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.feature.FrontYardService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class FrontYardService_Sikkim extends FrontYardService {

	private static final Logger LOG = Logger.getLogger(FrontYardService_Sikkim.class);

	private static final String RULE_35 = "35 Table-8";
	private static final String RULE_36 = "36";
	private static final String RULE_37_TWO_A = "37-2-A";
	private static final String RULE_37_TWO_B = "37-2-B";
	private static final String RULE_37_TWO_C = "37-2-C";
	private static final String RULE_37_TWO_D = "37-2-D";
	private static final String RULE_37_TWO_G = "37-2-G";
	private static final String RULE_37_TWO_H = "37-2-H";
	private static final String RULE_37_TWO_I = "37-2-I";
	private static final String RULE_47 = "47";

	private static final String MINIMUMLABEL = "Minimum distance ";

	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_1_5 = BigDecimal.valueOf(1.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_1_8 = BigDecimal.valueOf(1.8);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_2_5 = BigDecimal.valueOf(2.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_3 = BigDecimal.valueOf(3);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_3_6 = BigDecimal.valueOf(3.6);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_4 = BigDecimal.valueOf(4);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_4_5 = BigDecimal.valueOf(4.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_5 = BigDecimal.valueOf(5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_5_5 = BigDecimal.valueOf(5.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_6 = BigDecimal.valueOf(6);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_6_5 = BigDecimal.valueOf(6.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_7 = BigDecimal.valueOf(7);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_7_5 = BigDecimal.valueOf(7.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_8 = BigDecimal.valueOf(8);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_9 = BigDecimal.valueOf(9);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_10 = BigDecimal.valueOf(10);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_11 = BigDecimal.valueOf(11);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_12 = BigDecimal.valueOf(12);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_13 = BigDecimal.valueOf(13);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_14 = BigDecimal.valueOf(14);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_15 = BigDecimal.valueOf(15);
	public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);

	public static final String BSMT_FRONT_YARD_DESC = "Basement Front Yard";
	private static final int PLOTAREA_300 = 300;

	// PLOT AREA SIKKIM
	private static final BigDecimal PLOT_AREA_UPTO251 = BigDecimal.valueOf(251);
	private static final BigDecimal PLOT_AREA_ABOVE251TO502 = BigDecimal.valueOf(502);
	private static final BigDecimal PLOT_AREA_ABOVE502TO929 = BigDecimal.valueOf(929);
	private static final BigDecimal PLOT_AREA_ABOVE929 = BigDecimal.valueOf(929);

	private static final BigDecimal WITH_FOOTPATH_SK = BigDecimal.valueOf(1);
	private static final BigDecimal WITH_ROADONANYSIDE_SK = BigDecimal.valueOf(2);
	// private BigDecimal WITH_NONE_SK = BigDecimal.valueOf(3);

	// FRONT SETBACK OR YARD SIKKIM
	// Front Yard Independent Structure Minimum Distance
	private static final BigDecimal FRONTYARD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9 = BigDecimal.valueOf(0.9);
	private static final BigDecimal FRONTYARD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	private static final BigDecimal FRONTYARD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	private static final BigDecimal FRONTYARD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	// Front Yard With Attached Buildings In Adjoining Plots Minimum Distance
	private static final BigDecimal FRONTYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9 = BigDecimal.valueOf(0.9);
	private static final BigDecimal FRONTYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8 = BigDecimal.valueOf(1.8);
	private static final BigDecimal FRONTYARD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal FRONTYARD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal FRONTYARD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8 = BigDecimal.valueOf(1.8);
	// Front Yard With Footpath & Independent Structure Minimum Distance
	private static final BigDecimal FRONTYARD_FOOTPATH_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2 = BigDecimal
			.valueOf(1.2);
	private static final BigDecimal FRONTYARD_FOOTPATH_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	private static final BigDecimal FRONTYARD_FOOTPATH_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	private static final BigDecimal FRONTYARD_FOOTPATH_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5 = BigDecimal
			.valueOf(1.5);
	// Front Yard With Footpath & Attached Buildings In Adjoining Plots Minimum
	// Distance
	private static final BigDecimal FRONTYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2 = BigDecimal
			.valueOf(1.2);
	private static final BigDecimal FRONTYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal FRONTYARD_FOOTPATH_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal FRONTYARD_FOOTPATH_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	private static final BigDecimal FRONTYARD_FOOTPATH_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8 = BigDecimal
			.valueOf(1.8);
	// Front Yard With Road On Any Side & Independent Structure Minimum Distance
	private static final BigDecimal FRONTYARD_ROAD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal FRONTYARD_ROAD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal FRONTYARD_ROAD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal FRONTYARD_ROAD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	// Front Yard With Road On Any Side & Attached Buildings In Adjoining Plots
	// Minimum Distance
	private static final BigDecimal FRONTYARD_ROAD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal FRONTYARD_ROAD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal FRONTYARD_ROAD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);
	private static final BigDecimal FRONTYARD_ROAD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0 = BigDecimal
			.valueOf(3.0);

	private static final String FRONTYARD_STRUCTURE_IND_DESC = "Independent structure";
	private static final String FRONTYARD_STRUCTURE_DEP_DESC = "Buildings attached in adjoining plots";

	private static final String FRONTYARD_FOOTHPATH_DESC = "with footpath";
	private static final String FRONTYARD_ROAD_DESC = "with road on any side";
	private static final String FRONTYARD_NONE_DESC = "-";

	private static final String FRONTYARD_PLOTUPTO251_DESC = "plot area up to 251 sq. M";
	private static final String FRONTYARD_PLOTABOVE251TO502_DESC = "plot area above 251 to 502 sq. M";
	private static final String FRONTYARD_PLOTABOVE502TO929_DESC = "plot area above 502 to 929 sq. M";
	private static final String FRONTYARD_PLOTABOVE929_DESC = "plot area above 929 sq. M";
	private static final String PLOTTYPE = "Plot type";
	private static final String STRUCTURE = "Adjoining Buildings";
	private static final String SETBACK_SIDETYPE = "Setback Sidetype";
	private static final String OWNERSHIPTYPE = "Ownershiptype";

	private static final String ROAD_RESERVE_FRONT = "ROAD_RESERVE_FRONT";
	private static final String FRONT_ADJOINING = "FRONT_ADJOINING";

	private static final String BYELAW = " 4 & 18 2(i) & (ii) ";
	private static BigDecimal ROAD_RESERVE = BigDecimal.ZERO;

	private class FrontYardResult {
		String rule;
		String subRule;
		String blockName;
		Integer level;
		BigDecimal actualMeanDistance = BigDecimal.ZERO;
		BigDecimal actualMinDistance = BigDecimal.ZERO;
		String occupancy;
		BigDecimal expectedminimumDistance = BigDecimal.ZERO;
		BigDecimal expectedmeanDistance = BigDecimal.ZERO;
		String additionalCondition;
		boolean status = false;
//		______________________
		String structureIndOrDep;
		String setbackWithFootpathOrRoadOrNone;
		String plotType;
		String ownershipType;
		BigDecimal noAdjoiningBuildings;
//		______________________
	}

	public void processFrontYard(Plan pl) {

		Plot plot = null;
		HashMap<String, String> errors = new HashMap<>();
		if (pl.getPlot() == null)
			return;
		else
			plot=pl.getPlot();

		Boolean isAttached = false, isPrivateSite = true, isRoadReserveDeclared = false;

		/* Note: roadSetbackValue to be defined since value may vary */

		BigDecimal noOfAdjoiningBuildings = BigDecimal.ZERO, plotArea = BigDecimal.ZERO,
				roadSetbackValue = BigDecimal.ZERO, withRoadorFootpathorNone = BigDecimal.ZERO;
		try {
			isRoadReserveDeclared = checkIfRoadReservePresent(pl, ROAD_RESERVE_FRONT);
		}catch (Exception e) {
			// TODO: handle exception
		}
		

		if (!isRoadReserveDeclared || ROAD_RESERVE.compareTo(BigDecimal.ZERO) <= 0) {

			validateFrontYard(pl, errors);

			if (plot != null && !pl.getBlocks().isEmpty()) {
				if(plot.getArea()!=null)
					plotArea = plot.getArea();
				isAttached = Utility.checkIfAdjoiningBuildingsPresent(pl, FRONT_ADJOINING);
				/*
				 * withRoadorFootpathorNone = Utility.checkIfRoadPresent(pl) ? new BigDecimal(2)
				 * : (Utility.checkIfFootpathPresent(pl) ? BigDecimal.ONE : BigDecimal.ZERO);
				 */
				withRoadorFootpathorNone = Utility.checkIfRoadPresent(pl) ? new BigDecimal(2) : BigDecimal.ZERO;

				if (isAttached)
					noOfAdjoiningBuildings = Utility.getNoOfAdjoiningBuildings(pl);

				for (Block block : pl.getBlocks()) { // for each block

					ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
					scrutinyDetail.addColumnHeading(1, RULE_NO);
					scrutinyDetail.addColumnHeading(3, OWNERSHIPTYPE);
					scrutinyDetail.addColumnHeading(5, PLOTTYPE);
					scrutinyDetail.addColumnHeading(6, SETBACK_SIDETYPE);
					scrutinyDetail.addColumnHeading(7, STRUCTURE);
					scrutinyDetail.addColumnHeading(8, FIELDVERIFIED);
					scrutinyDetail.addColumnHeading(9, PERMISSIBLE);
					scrutinyDetail.addColumnHeading(10, PROVIDED);
					scrutinyDetail.addColumnHeading(11, STATUS);
					scrutinyDetail.setHeading(FRONT_YARD_DESC);

					FrontYardResult frontYardResult = new FrontYardResult();
					frontYardResult.subRule = BYELAW;
					frontYardResult.blockName = block.getName();

					for (SetBack setback : block.getSetBacks()) {
						BigDecimal min = BigDecimal.ZERO, mean=BigDecimal.ZERO;

						if (setback.getFrontYard() != null) {
							frontYardResult.structureIndOrDep = isAttached ? "Attached to adjoining buildings"
									: "No Buildings attached";
							if (isAttached)
								frontYardResult.noAdjoiningBuildings = noOfAdjoiningBuildings;

							frontYardResult.ownershipType = isPrivateSite ? "Private Site" : "Alloted Site";
							
							if(setback.getFrontYard().getMinimumDistance()!=null)
								min = setback.getFrontYard().getMinimumDistance();
							if(setback.getFrontYard().getMean()!=null)
								mean = setback.getFrontYard().getMean();

							frontYardResult.actualMinDistance = min;
							frontYardResult.actualMeanDistance = mean;
							BigDecimal buildingHeight=null;
							// if height defined at frontyard level, then use elase use buidling height.
							buildingHeight = setback.getFrontYard().getHeight() != null
									&& setback.getFrontYard().getHeight().compareTo(BigDecimal.ZERO) > 0
											? setback.getFrontYard().getHeight()
											: block.getBuilding().getBuildingHeight();

							if (buildingHeight != null && (min.doubleValue() > 0 || mean.doubleValue() > 0)) {
								scrutinyDetail.setKey("Block_" + block.getName() + "_" + FRONT_YARD_DESC);
								if (plotArea.compareTo(PLOT_AREA_UPTO251) <= 0) {
									frontYardResult.plotType = FRONTYARD_PLOTUPTO251_DESC;
									processFrontYardUptoPlotArea251(pl, scrutinyDetail, frontYardResult, block, errors,
											min, noOfAdjoiningBuildings, roadSetbackValue, withRoadorFootpathorNone,
											isAttached, isPrivateSite, isRoadReserveDeclared);
								} else if (plotArea.compareTo(PLOT_AREA_ABOVE251TO502) <= 0) {
									frontYardResult.plotType = FRONTYARD_PLOTABOVE251TO502_DESC;
									processFrontYardUptoPlotAreaAbove251To502(pl, scrutinyDetail, frontYardResult,
											block, errors, min, noOfAdjoiningBuildings, roadSetbackValue,
											withRoadorFootpathorNone, isAttached, isPrivateSite, isRoadReserveDeclared);
								} else if (plotArea.compareTo(PLOT_AREA_ABOVE502TO929) <= 0) {
									frontYardResult.plotType = FRONTYARD_PLOTABOVE502TO929_DESC;
									processFrontYardUptoPlotAreaAbove502To929(pl, scrutinyDetail, frontYardResult,
											block, errors, min, noOfAdjoiningBuildings, roadSetbackValue,
											withRoadorFootpathorNone, isAttached, isPrivateSite, isRoadReserveDeclared);
								} else {
									frontYardResult.plotType = FRONTYARD_PLOTABOVE929_DESC;
									processFrontYardUptoPlotAreaAbove929(pl, scrutinyDetail, frontYardResult, block,
											errors, min, noOfAdjoiningBuildings, roadSetbackValue,
											withRoadorFootpathorNone, isAttached, isPrivateSite, isRoadReserveDeclared);
								}

								setReportOutputDetails(pl, scrutinyDetail, frontYardResult, errors);
							}
						}
					}
				}

			}
		}

	}

	private void validateFrontYard(Plan pl, HashMap<String, String> errors) {

		// Front yard may not be mandatory at each level. We can check whether in any
		// level front yard defined or not ?

		for (Block block : pl.getBlocks()) {
			if (!block.getCompletelyExisting()) {
				Boolean frontYardDefined = false;
				for (SetBack setback : block.getSetBacks()) {
					if (setback.getFrontYard() != null
							&& setback.getFrontYard().getMean().compareTo(BigDecimal.valueOf(0)) > 0) {
						frontYardDefined = true;
					}
				}
				if (!frontYardDefined) {
					errors.put(FRONT_YARD_DESC,
							prepareMessage(OBJECTNOTDEFINED, FRONT_YARD_DESC + " for Block " + block.getName()));
					pl.addErrors(errors);
				}
			}

		}

	}

	private void processFrontYardUptoPlotArea251(Plan pl, ScrutinyDetail scrutinyDetail,
			FrontYardResult frontYardResult, Block block, HashMap<String, String> errors, BigDecimal minFrontYard,
			BigDecimal noOfAdjoiningBuildings, BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone,
			Boolean isAttached, Boolean isPrivateSite, Boolean isRoadReserveDeclared) {

		if (isPrivateSite) {
			switch (withRoadorFootpathorNone.intValue()) {
			case 1:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_FOOTHPATH_DESC;
				if (isAttached) {
					if (noOfAdjoiningBuildings.compareTo(BigDecimal.ONE) == 0)
						minFrontYard = FRONTYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2;
					else
						minFrontYard = FRONTYARD_FOOTPATH_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8;
				} else {
					minFrontYard = FRONTYARD_FOOTPATH_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_2;
				}
				break;
			case 2:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_ROAD_DESC;
				if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) {
					minFrontYard = roadSetbackValue;
				} else {
					if (isAttached)
						minFrontYard = FRONTYARD_ROAD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0;
					else
						minFrontYard = FRONTYARD_ROAD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_3_0;
				}
				break;
			default:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_NONE_DESC;
				if (isAttached)
					if (noOfAdjoiningBuildings.compareTo(BigDecimal.ONE) == 0)
						minFrontYard = FRONTYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9;
					else
						minFrontYard = FRONTYARD_ADJOINING_PLOTUPTO251_MINIMUM_DISTANCE_SK_1_8;
				else
					minFrontYard = FRONTYARD_INDEPENDENT_PLOTUPTO251_MINIMUM_DISTANCE_SK_0_9;
			}

			frontYardResult.expectedminimumDistance = minFrontYard;

			if (frontYardResult.actualMinDistance.compareTo(frontYardResult.expectedminimumDistance) >= 0)
				frontYardResult.status = true;
			else
				frontYardResult.status = false;

		} else {
			/* No setback validation required */
			/* minFrontYard = BigDecimal.ZERO; */
		}

	}

	private void processFrontYardUptoPlotAreaAbove251To502(Plan pl, ScrutinyDetail scrutinyDetail,
			FrontYardResult frontYardResult, Block block, HashMap<String, String> errors, BigDecimal minFrontYard,
			BigDecimal noOfAdjoiningBuildings, BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone,
			Boolean isAttached, Boolean isPrivateSite, Boolean isRoadReserveDeclared) {
		if (isPrivateSite) {
			switch (withRoadorFootpathorNone.intValue()) {
			case 1:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_FOOTHPATH_DESC;
				if (isAttached) {
					minFrontYard = FRONTYARD_FOOTPATH_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8;
				} else {
					minFrontYard = FRONTYARD_FOOTPATH_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5;
				}
				break;
			case 2:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_ROAD_DESC;
				if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) {
					minFrontYard = roadSetbackValue;
				} else {
					if (isAttached)
						minFrontYard = FRONTYARD_ROAD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0;
					else
						minFrontYard = FRONTYARD_ROAD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_3_0;
				}
				break;
			default:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_NONE_DESC;
				if (isAttached)
					minFrontYard = FRONTYARD_ADJOINING_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_8;
				else
					minFrontYard = FRONTYARD_INDEPENDENT_PLOTABOVE251TO502_MINIMUM_DISTANCE_SK_1_5;
			}

			frontYardResult.expectedminimumDistance = minFrontYard;

			if (frontYardResult.actualMinDistance.compareTo(frontYardResult.expectedminimumDistance) >= 0)
				frontYardResult.status = true;
			else
				frontYardResult.status = false;

		} else {
			/* No setback validation required */
			/* minFrontYard = BigDecimal.ZERO; */
		}

	}

	private void processFrontYardUptoPlotAreaAbove502To929(Plan pl, ScrutinyDetail scrutinyDetail,
			FrontYardResult frontYardResult, Block block, HashMap<String, String> errors, BigDecimal minFrontYard,
			BigDecimal noOfAdjoiningBuildings, BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone,
			Boolean isAttached, Boolean isPrivateSite, Boolean isRoadReserveDeclared) {

		if (isPrivateSite) {
			frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_FOOTHPATH_DESC;
			switch (withRoadorFootpathorNone.intValue()) {
			case 1:
				if (isAttached) {
					minFrontYard = FRONTYARD_FOOTPATH_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8;
				} else {
					minFrontYard = FRONTYARD_FOOTPATH_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5;
				}
				break;
			case 2:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_ROAD_DESC;
				if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) {
					minFrontYard = roadSetbackValue;
				} else {
					if (isAttached)
						minFrontYard = FRONTYARD_ROAD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0;
					else
						minFrontYard = FRONTYARD_ROAD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_3_0;
				}
				break;
			default:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_NONE_DESC;
				if (isAttached)
					minFrontYard = FRONTYARD_ADJOINING_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_8;
				else
					minFrontYard = FRONTYARD_INDEPENDENT_PLOTABOVE502TO929_MINIMUM_DISTANCE_SK_1_5;
			}

			frontYardResult.expectedminimumDistance = minFrontYard;

			if (frontYardResult.actualMinDistance.compareTo(frontYardResult.expectedminimumDistance) >= 0)
				frontYardResult.status = true;
			else
				frontYardResult.status = false;

		} else {
			/* No setback validation required */
			/* minFrontYard = BigDecimal.ZERO; */
		}

	}

	private void processFrontYardUptoPlotAreaAbove929(Plan pl, ScrutinyDetail scrutinyDetail,
			FrontYardResult frontYardResult, Block block, HashMap<String, String> errors, BigDecimal minFrontYard,
			BigDecimal noOfAdjoiningBuildings, BigDecimal roadSetbackValue, BigDecimal withRoadorFootpathorNone,
			Boolean isAttached, Boolean isPrivateSite, Boolean isRoadReserveDeclared) {

		if (isPrivateSite) {
			switch (withRoadorFootpathorNone.intValue()) {
			case 1:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_FOOTHPATH_DESC;
				if (isAttached) {
					minFrontYard = FRONTYARD_FOOTPATH_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8;
				} else {
					minFrontYard = FRONTYARD_FOOTPATH_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5;
				}
				break;
			case 2:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_ROAD_DESC;
				if (roadSetbackValue.compareTo(BigDecimal.ZERO) > 0) {
					minFrontYard = roadSetbackValue;
				} else {
					if (isAttached)
						minFrontYard = FRONTYARD_ROAD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0;
					else
						minFrontYard = FRONTYARD_ROAD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_3_0;
				}
				break;
			default:
				frontYardResult.setbackWithFootpathOrRoadOrNone = FRONTYARD_NONE_DESC;
				if (isAttached)
					minFrontYard = FRONTYARD_ADJOINING_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_8;
				else
					minFrontYard = FRONTYARD_INDEPENDENT_PLOTABOVE929_MINIMUM_DISTANCE_SK_1_5;
			}

			frontYardResult.expectedminimumDistance = minFrontYard;

			if (frontYardResult.actualMinDistance.compareTo(frontYardResult.expectedminimumDistance) >= 0)
				frontYardResult.status = true;
			else
				frontYardResult.status = false;

		} else {
			/* No setback validation required */
			/* minFrontYard = BigDecimal.ZERO; */
		}

	}

	private void setReportOutputDetails(Plan pl, ScrutinyDetail scrutinyDetail, FrontYardResult frontYardResult,
			HashMap<String, String> errors) {

		if (errors.isEmpty()) {
			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, frontYardResult.subRule);
			details.put(OWNERSHIPTYPE, frontYardResult.ownershipType);
			details.put(PLOTTYPE, frontYardResult.plotType);
			details.put(SETBACK_SIDETYPE, frontYardResult.setbackWithFootpathOrRoadOrNone);
			details.put(STRUCTURE, frontYardResult.structureIndOrDep);
			details.put(FIELDVERIFIED, MINIMUMLABEL);
			details.put(PERMISSIBLE, frontYardResult.expectedminimumDistance.toString() + DcrConstants.IN_METER);
			details.put(PROVIDED,
					frontYardResult.actualMinDistance
							.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
							.toString() + DcrConstants.IN_METER);

			if (frontYardResult.status) {
				details.put(STATUS, Result.Accepted.getResultVal());
			} else {
				details.put(STATUS, Result.Not_Accepted.getResultVal());
			}
			scrutinyDetail.getDetail().add(details);
			pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		}
	}	

	private Boolean checkIfRoadReservePresent(Plan pl, String roadReserve) {
		/* If RoadReserve Present No Validation of Setback required */
		Boolean hasRoadReserve = false;

		if (pl.getRoadReserves() != null && !pl.getRoadReserves().isEmpty()) {

			hasRoadReserve = pl.getRoadReserves().stream().anyMatch(road -> {
				final Boolean hRR = road.getName().equalsIgnoreCase(roadReserve);

				if (hRR && !road.getShortestDistanceToRoad().isEmpty()&&road.getShortestDistanceToRoad()!=null)
					ROAD_RESERVE = road.getShortestDistanceToRoad().stream().reduce(BigDecimal::min).get();

				return hRR;
			});

			if (hasRoadReserve && ROAD_RESERVE.compareTo(BigDecimal.ZERO) == 0)
				pl.addError(String.format("%s shortest distance to road cannot be 0", roadReserve),
						String.format("%s shortest distance to road cannot be 0", roadReserve));

		}

		return hasRoadReserve;
	}
}