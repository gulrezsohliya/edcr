/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A;

import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;

//from  DxfFileConstants_AR
import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.egov.client.constants.DxfFileConstants_AR;
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.Balcony;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.FarDetails;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Lift;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.Stair;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.Far;
import org.egov.edcr.feature.StairCover;
import org.egov.edcr.service.ProcessPrintHelper;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class Far_Tripura extends Far {

	private static final Logger LOG = Logger.getLogger(Far_Tripura.class);

	private static final String VALIDATION_NEGATIVE_FLOOR_AREA = "msg.error.negative.floorarea.occupancy.floor";
	private static final String VALIDATION_NEGATIVE_EXISTING_FLOOR_AREA = "msg.error.negative.existing.floorarea.occupancy.floor";
	private static final String VALIDATION_NEGATIVE_BUILTUP_AREA = "msg.error.negative.builtuparea.occupancy.floor";
	private static final String VALIDATION_NEGATIVE_EXISTING_BUILTUP_AREA = "msg.error.negative.existing.builtuparea.occupancy.floor";
	public static final String RULE_31_1 = "31-1";
	public static final String RULE_54_D = "54-D";
	// old
	private static final BigDecimal POINTTWO = BigDecimal.valueOf(0.2);
	private static final BigDecimal POINTFOUR = BigDecimal.valueOf(0.4);
	private static final BigDecimal POINTFIVE = BigDecimal.valueOf(0.5);
	private static final BigDecimal POINTSIX = BigDecimal.valueOf(0.6);
	private static final BigDecimal POINTSEVEN = BigDecimal.valueOf(0.7);
	private static final BigDecimal POINTEIGHT = BigDecimal.valueOf(0.7);
	private static final BigDecimal ONE = BigDecimal.valueOf(1);
	private static final BigDecimal ONE_POINTTWO = BigDecimal.valueOf(1.2);
	private static final BigDecimal ONE_POINTFIVE = BigDecimal.valueOf(1.5);
	private static final BigDecimal ONE_POINTEIGHT = BigDecimal.valueOf(1.8);
	private static final BigDecimal ONE_POINTTWOFIVE = BigDecimal.valueOf(1.25);
	private static final BigDecimal TWO = BigDecimal.valueOf(2);
	private static final BigDecimal TWO_POINTFIVE = BigDecimal.valueOf(2.5);
	private static final BigDecimal THREE = BigDecimal.valueOf(3);
	private static final BigDecimal THREE_POINTTWOFIVE = BigDecimal.valueOf(3.25);
	private static final BigDecimal THREE_POINTFIVE = BigDecimal.valueOf(3.5);
	private static final BigDecimal FIFTEEN = BigDecimal.valueOf(15);
	// new for AP
//	private static final BigDecimal POINT_TWO = BigDecimal.valueOf(10).divide(BigDecimal.valueOf(100));
	private static final BigDecimal POINTONE = BigDecimal.valueOf(10).divide(BigDecimal.valueOf(100));
	private static final BigDecimal POINTONEFIVE = BigDecimal.valueOf(10).divide(BigDecimal.valueOf(100));
//	private static final BigDecimal TWENTY = BigDecimal.valueOf(20).divide(BigDecimal.valueOf(100));
//	private static final BigDecimal FORTY = BigDecimal.valueOf(40).divide(BigDecimal.valueOf(100));
	private static final BigDecimal POINT_FOUR = BigDecimal.valueOf(40).divide(BigDecimal.valueOf(100));
	private static final BigDecimal FORTY_FIVE = BigDecimal.valueOf(40).divide(BigDecimal.valueOf(100));
	private static final BigDecimal FOUR_POINTFIVE = BigDecimal.valueOf(40).divide(BigDecimal.valueOf(100));
	private static final BigDecimal FIFTY = BigDecimal.valueOf(50).divide(BigDecimal.valueOf(100));
	private static final BigDecimal FIVE = BigDecimal.valueOf(50).divide(BigDecimal.valueOf(100));
	private static final BigDecimal SIXTY = BigDecimal.valueOf(60).divide(BigDecimal.valueOf(100));
	private static final BigDecimal SIX = BigDecimal.valueOf(60).divide(BigDecimal.valueOf(100));
	private static final BigDecimal SEVENTY = BigDecimal.valueOf(70).divide(BigDecimal.valueOf(100));
	private static final BigDecimal SEVEN = BigDecimal.valueOf(70).divide(BigDecimal.valueOf(100));
	private static final BigDecimal EIGHTY = BigDecimal.valueOf(80).divide(BigDecimal.valueOf(100));
	private static final BigDecimal EIGHT = BigDecimal.valueOf(80).divide(BigDecimal.valueOf(100));
//	private static final BigDecimal TWO = BigDecimal.valueOf(100).divide(BigDecimal.valueOf(100));
//	private static final BigDecimal POINT_FOUR = BigDecimal.valueOf(120).divide(BigDecimal.valueOf(100));
//	private static final BigDecimal ONE_FIFTY = BigDecimal.valueOf(150).divide(BigDecimal.valueOf(100));
	private static final BigDecimal ONE_FIFTYFIVE = BigDecimal.valueOf(150).divide(BigDecimal.valueOf(100));
	private static final BigDecimal ONE_POINTFIVEFIVE = BigDecimal.valueOf(150).divide(BigDecimal.valueOf(100));

	private static final BigDecimal ONE_SIXTY = BigDecimal.valueOf(160).divide(BigDecimal.valueOf(100));
	private static final BigDecimal ONE_POINTSIX = BigDecimal.valueOf(160).divide(BigDecimal.valueOf(100));
	private static final BigDecimal ONE_SEVENTYFIVE = BigDecimal.valueOf(175).divide(BigDecimal.valueOf(100));
	private static final BigDecimal ONE_POINTSEVENFIVE = BigDecimal.valueOf(175).divide(BigDecimal.valueOf(100));
	private static final BigDecimal ONE_EIGHTY = BigDecimal.valueOf(180).divide(BigDecimal.valueOf(100));
//	private static final BigDecimal ONE_POINTEIGHT = BigDecimal.valueOf(180).divide(BigDecimal.valueOf(100));
//	private static final BigDecimal TWO_HUNDRED = BigDecimal.valueOf(200).divide(BigDecimal.valueOf(100));
	private static final BigDecimal TWO_TWENTYFIVE = BigDecimal.valueOf(225).divide(BigDecimal.valueOf(100));
	private static final BigDecimal TWO_POINTTWOFIVE = BigDecimal.valueOf(225).divide(BigDecimal.valueOf(100));
	private static final BigDecimal POINTSEVENFIVE = BigDecimal.valueOf(75).divide(BigDecimal.valueOf(100));
//	private static final BigDecimal THREE_HUNDRED = BigDecimal.valueOf(300).divide(BigDecimal.valueOf(100));

	// AREA for AP
	private static final BigDecimal PLOT_AREA_48 = BigDecimal.valueOf(48);
	private static final BigDecimal PLOT_AREA_60 = BigDecimal.valueOf(60);
	private static final BigDecimal PLOT_AREA_90 = BigDecimal.valueOf(90);
	private static final BigDecimal PLOT_AREA_100 = BigDecimal.valueOf(100);
	private static final BigDecimal PLOT_AREA_150 = BigDecimal.valueOf(150);
	private static final BigDecimal PLOT_AREA_250 = BigDecimal.valueOf(250);
	private static final BigDecimal PLOT_AREA_300 = BigDecimal.valueOf(300);
	private static final BigDecimal PLOT_AREA_500 = BigDecimal.valueOf(500);
	private static final BigDecimal PLOT_AREA_750 = BigDecimal.valueOf(750);
	private static final BigDecimal PLOT_AREA_1000 = BigDecimal.valueOf(1000);
	private static final BigDecimal PLOT_AREA_1500 = BigDecimal.valueOf(1500);
	private static final BigDecimal PLOT_AREA_2500 = BigDecimal.valueOf(2500);
	private static final BigDecimal PLOT_AREA_3000 = BigDecimal.valueOf(3000);
	private static final BigDecimal PLOT_AREA_5000 = BigDecimal.valueOf(5000);
	private static final BigDecimal PLOT_AREA_2000 = BigDecimal.valueOf(2000);
	private static final BigDecimal PLOT_AREA_4000 = BigDecimal.valueOf(4000);
	private static final BigDecimal PLOT_AREA_12000 = BigDecimal.valueOf(12000);
	private static final BigDecimal PLOT_AREA_28000 = BigDecimal.valueOf(28000);
	private static final BigDecimal PLOT_AREA_6000 = BigDecimal.valueOf(6000);
	private static final BigDecimal PLOT_AREA_20000 = BigDecimal.valueOf(20000);
	private static final BigDecimal PLOT_AREA_10000 = BigDecimal.valueOf(10000);
	private static final BigDecimal PLOT_AREA_1080 = BigDecimal.valueOf(1080);
	private static final BigDecimal PLOT_AREA_510 = BigDecimal.valueOf(510);
	private static final BigDecimal PLOT_AREA_400 = BigDecimal.valueOf(400);

	// Height for AP
	public static final BigDecimal HEIGHT_6 = BigDecimal.valueOf(6);
	public static final BigDecimal HEIGHT_9 = BigDecimal.valueOf(9);
	public static final BigDecimal HEIGHT_12 = BigDecimal.valueOf(12);
	public static final BigDecimal HEIGHT_15 = BigDecimal.valueOf(15);
	public static final BigDecimal HEIGHT_18 = BigDecimal.valueOf(18);

	public static final String OLD = "OLD";
	public static final String NEW = "NEW";
	public static final String PLAINS = "PLAINS";
	public static final String HILLS = "HILLS";
	public static final String OLD_AREA_ERROR = "road width old area";
	public static final String NEW_AREA_ERROR = "road width new area";
	public static final String OLD_AREA_ERROR_MSG = "No construction shall be permitted if the road width is less than 2.4m for old area.";
	public static final String NEW_AREA_ERROR_MSG = "No construction shall be permitted if the road width is less than 6.1m for new area.";

	private class AdditionalFarArea {
		String ruleNo;
		String ruleDesc;
		String remarks;
		BigDecimal area;
		BigDecimal additionalArea;
		boolean isAdded = false;
	}

	@Override
	public Plan validate(Plan pl) {
		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
		}

		return pl;
	}

	@Override
	public Plan process(Plan pl) {
//		decideNocIsRequired(pl); is it required?
		try {
			HashMap<String, String> errorMsgs = new HashMap<>();
			int errors = pl.getErrors().size();
			validate(pl);

			int validatedErrors = pl.getErrors().size();
			if (validatedErrors > errors) {
				return pl;
			}
			BigDecimal totalExistingBuiltUpArea = BigDecimal.ZERO;
			BigDecimal totalExistingFloorArea = BigDecimal.ZERO;
			BigDecimal totalBuiltUpArea = BigDecimal.ZERO;
			BigDecimal totalFloorArea = BigDecimal.ZERO;
			BigDecimal totalCarpetArea = BigDecimal.ZERO;
			BigDecimal totalExistingCarpetArea = BigDecimal.ZERO;
			Set<OccupancyTypeHelper> distinctOccupancyTypesHelper = new HashSet<>();
			for (Block blk : pl.getBlocks()) {
				BigDecimal flrArea = BigDecimal.ZERO;
				BigDecimal bltUpArea = BigDecimal.ZERO;
				BigDecimal existingFlrArea = BigDecimal.ZERO;
				BigDecimal existingBltUpArea = BigDecimal.ZERO;
				BigDecimal carpetArea = BigDecimal.ZERO;
				BigDecimal existingCarpetArea = BigDecimal.ZERO;
				Building building = blk.getBuilding();
				for (Floor flr : building.getFloors()) {

					for (Occupancy occupancy : flr.getOccupancies()) {

						validate2(pl, blk, flr, occupancy);
						/*
						 * occupancy.setCarpetArea(occupancy.getFloorArea().multiply
						 * (BigDecimal.valueOf(0.80))); occupancy
						 * .setExistingCarpetArea(occupancy.getExistingFloorArea().
						 * multiply(BigDecimal.valueOf(0.80)));
						 */

						bltUpArea = bltUpArea.add(occupancy.getBuiltUpArea() == null ? BigDecimal.valueOf(0)
								: occupancy.getBuiltUpArea());
						flrArea = flrArea.add(
								occupancy.getFloorArea() == null ? BigDecimal.valueOf(0) : occupancy.getFloorArea());
						existingBltUpArea = existingBltUpArea
								.add(occupancy.getExistingBuiltUpArea() == null ? BigDecimal.valueOf(0)
										: occupancy.getExistingBuiltUpArea());
//						if (!occupancy.getIsMezzanine()) {
//							flrArea = flrArea.add(occupancy.getFloorArea());
//						}

						existingFlrArea = existingFlrArea.add(occupancy.getExistingFloorArea());
						carpetArea = carpetArea.add(occupancy.getCarpetArea());
						existingCarpetArea = existingCarpetArea.add(occupancy.getExistingCarpetArea());
					}
				}
				/*
				 * This is hard coded for testing
				 */
				building.setTotalFloorArea(flrArea);
				building.setTotalBuitUpArea(bltUpArea);
				building.setTotalExistingBuiltUpArea(existingBltUpArea);
				building.setTotalExistingFloorArea(existingFlrArea);

				// check block is completely existing building or not.
				if (existingBltUpArea.compareTo(bltUpArea) == 0)
					blk.setCompletelyExisting(Boolean.TRUE);

				totalFloorArea = totalFloorArea.add(flrArea);
				totalBuiltUpArea = totalBuiltUpArea.add(bltUpArea);
				totalExistingBuiltUpArea = totalExistingBuiltUpArea.add(existingBltUpArea);
				totalExistingFloorArea = totalExistingFloorArea.add(existingFlrArea);
				totalCarpetArea = totalCarpetArea.add(carpetArea);
				totalExistingCarpetArea = totalExistingCarpetArea.add(existingCarpetArea);

				// Find Occupancies by block and add
				Set<OccupancyTypeHelper> occupancyByBlock = new HashSet<>();
				for (Floor flr : building.getFloors()) {
					for (Occupancy occupancy : flr.getOccupancies()) {
						if (occupancy.getTypeHelper().getType() != null)
							occupancyByBlock.add(occupancy.getTypeHelper());
					}
				}

				List<Map<String, Object>> listOfMapOfAllDtls = new ArrayList<>();
				List<OccupancyTypeHelper> listOfOccupancyTypes = new ArrayList<>();

				for (OccupancyTypeHelper occupancyType : occupancyByBlock) {

					Map<String, Object> allDtlsMap = new HashMap<>();
					BigDecimal blockWiseFloorArea = BigDecimal.ZERO;
					BigDecimal blockWiseBuiltupArea = BigDecimal.ZERO;
					BigDecimal blockWiseExistingFloorArea = BigDecimal.ZERO;
					BigDecimal blockWiseExistingBuiltupArea = BigDecimal.ZERO;
					for (Floor flr : blk.getBuilding().getFloors()) {
						for (Occupancy occupancy : flr.getOccupancies()) {
							if (occupancyType.getType() != null && occupancy.getTypeHelper().getType() != null
									&& occupancy.getTypeHelper().getType().getCode()
											.equals(occupancyType.getType().getCode())) {
								if (!occupancy.getIsMezzanine()) {
									blockWiseFloorArea = blockWiseFloorArea.add(occupancy.getFloorArea());
								}

								blockWiseBuiltupArea = blockWiseBuiltupArea
										.add(occupancy.getBuiltUpArea() == null ? BigDecimal.valueOf(0)
												: occupancy.getBuiltUpArea());
								blockWiseExistingFloorArea = blockWiseExistingFloorArea
										.add(occupancy.getExistingFloorArea());
								blockWiseExistingBuiltupArea = blockWiseExistingBuiltupArea
										.add(occupancy.getExistingBuiltUpArea() == null ? BigDecimal.valueOf(0)
												: occupancy.getExistingBuiltUpArea());

							}
						}
					}
					Occupancy occupancy = new Occupancy();
					occupancy.setBuiltUpArea(blockWiseBuiltupArea);
					occupancy.setFloorArea(blockWiseFloorArea);
					occupancy.setExistingFloorArea(blockWiseExistingFloorArea);
					occupancy.setExistingBuiltUpArea(blockWiseExistingBuiltupArea);
					occupancy.setCarpetArea(blockWiseFloorArea.multiply(BigDecimal.valueOf(.80)));
					occupancy.setTypeHelper(occupancyType);
					building.getTotalArea().add(occupancy);

					allDtlsMap.put("occupancy", occupancyType);
					allDtlsMap.put("totalFloorArea", blockWiseFloorArea);
					allDtlsMap.put("totalBuiltUpArea", blockWiseBuiltupArea);
					allDtlsMap.put("existingFloorArea", blockWiseExistingFloorArea);
					allDtlsMap.put("existingBuiltUpArea", blockWiseExistingBuiltupArea);

					listOfOccupancyTypes.add(occupancyType);

					listOfMapOfAllDtls.add(allDtlsMap);
				}
				Set<OccupancyTypeHelper> setOfOccupancyTypes = new HashSet<>(listOfOccupancyTypes);

				List<Occupancy> listOfOccupanciesOfAParticularblock = new ArrayList<>();
				// for each distinct converted occupancy types
				for (OccupancyTypeHelper occupancyType : setOfOccupancyTypes) {
					if (occupancyType != null) {
						Occupancy occupancy = new Occupancy();
						BigDecimal totalFlrArea = BigDecimal.ZERO;
						BigDecimal totalBltUpArea = BigDecimal.ZERO;
						BigDecimal totalExistingFlrArea = BigDecimal.ZERO;
						BigDecimal totalExistingBltUpArea = BigDecimal.ZERO;

						for (Map<String, Object> dtlsMap : listOfMapOfAllDtls) {
							if (occupancyType.equals(dtlsMap.get("occupancy"))) {
								totalFlrArea = totalFlrArea.add((BigDecimal) dtlsMap.get("totalFloorArea"));
								totalBltUpArea = totalBltUpArea.add((BigDecimal) dtlsMap.get("totalBuiltUpArea"));

								totalExistingBltUpArea = totalExistingBltUpArea
										.add((BigDecimal) dtlsMap.get("existingBuiltUpArea"));
								totalExistingFlrArea = totalExistingFlrArea
										.add((BigDecimal) dtlsMap.get("existingFloorArea"));

							}
						}
						occupancy.setTypeHelper(occupancyType);
						occupancy.setBuiltUpArea(totalBltUpArea);
						occupancy.setFloorArea(totalFlrArea);
						occupancy.setExistingBuiltUpArea(totalExistingBltUpArea);
						occupancy.setExistingFloorArea(totalExistingFlrArea);
						occupancy.setExistingCarpetArea(totalExistingFlrArea.multiply(BigDecimal.valueOf(0.80)));
						occupancy.setCarpetArea(totalFlrArea.multiply(BigDecimal.valueOf(0.80)));

						listOfOccupanciesOfAParticularblock.add(occupancy);
					}
				}
				blk.getBuilding().setOccupancies(listOfOccupanciesOfAParticularblock);

				if (!listOfOccupanciesOfAParticularblock.isEmpty()) {
					boolean singleFamilyBuildingTypeOccupancyPresent = false;
					boolean otherThanSingleFamilyOccupancyTypePresent = false;

					for (Occupancy occupancy : listOfOccupanciesOfAParticularblock) {
						if (occupancy.getTypeHelper().getSubtype() != null
								&& R1a.equals(occupancy.getTypeHelper().getSubtype().getCode()))
							singleFamilyBuildingTypeOccupancyPresent = true;
						else {
							otherThanSingleFamilyOccupancyTypePresent = true;
							break;
						}
					}
					blk.setSingleFamilyBuilding(
							!otherThanSingleFamilyOccupancyTypePresent && singleFamilyBuildingTypeOccupancyPresent);
					int allResidentialOccTypes = 0;
					int allResidentialOrCommercialOccTypes = 0;

					for (Occupancy occupancy : listOfOccupanciesOfAParticularblock) {
						if (occupancy.getTypeHelper().getType() != null) {
							// setting residentialBuilding
							int residentialOccupancyType = 0;
							if (R.equals(occupancy.getTypeHelper().getType().getCode())) {
								residentialOccupancyType = 1;
							}
							if (residentialOccupancyType == 0) {
								allResidentialOccTypes = 0;
								break;
							} else {
								allResidentialOccTypes = 1;
							}
						}
					}
					blk.setResidentialBuilding(allResidentialOccTypes == 1);
					for (Occupancy occupancy : listOfOccupanciesOfAParticularblock) {
						if (occupancy.getTypeHelper().getType() != null) {
							// setting residentialOrCommercial Occupancy Type
							int residentialOrCommercialOccupancyType = 0;
							if (A.equals(occupancy.getTypeHelper().getType().getCode())
									|| B.equals(occupancy.getTypeHelper().getType().getCode())) {
								residentialOrCommercialOccupancyType = 1;
							}
							if (residentialOrCommercialOccupancyType == 0) {
								allResidentialOrCommercialOccTypes = 0;
								break;
							} else {
								allResidentialOrCommercialOccTypes = 1;
							}
						}
					}
					blk.setResidentialOrCommercialBuilding(allResidentialOrCommercialOccTypes == 1);
				}

				if (blk.getBuilding().getFloors() != null && !blk.getBuilding().getFloors().isEmpty()) {
					BigDecimal noOfFloorsAboveGround = BigDecimal.ZERO;
					for (Floor floor : blk.getBuilding().getFloors()) {
						if (floor.getNumber() != null && floor.getNumber() >= 0) {
							noOfFloorsAboveGround = noOfFloorsAboveGround.add(BigDecimal.valueOf(1));
						}
					}

					boolean hasTerrace = blk.getBuilding().getFloors().stream()
							.anyMatch(floor -> floor.getTerrace().equals(Boolean.TRUE));

					noOfFloorsAboveGround = hasTerrace ? noOfFloorsAboveGround.subtract(BigDecimal.ONE)
							: noOfFloorsAboveGround;

					blk.getBuilding().setMaxFloor(noOfFloorsAboveGround);
					blk.getBuilding().setFloorsAboveGround(noOfFloorsAboveGround);
					blk.getBuilding().setTotalFloors(BigDecimal.valueOf(blk.getBuilding().getFloors().size()));
				}

			}
			// end of setting block and floor

			// begin get of block and floor
			for (Block blk : pl.getBlocks()) {
				Building building = blk.getBuilding();
				List<OccupancyTypeHelper> blockWiseOccupancyTypes = new ArrayList<>();
				for (Occupancy occupancy : blk.getBuilding().getOccupancies()) {
					if (occupancy.getTypeHelper().getType() != null)
						blockWiseOccupancyTypes.add(occupancy.getTypeHelper());
				}
				Set<OccupancyTypeHelper> setOfBlockDistinctOccupancyTypes = new HashSet<>(blockWiseOccupancyTypes);
				OccupancyTypeHelper mostRestrictiveFar = getMostRestrictiveFar(setOfBlockDistinctOccupancyTypes);
				blk.getBuilding().setMostRestrictiveFarHelper(mostRestrictiveFar);

				for (Floor flr : building.getFloors()) {
					BigDecimal flrArea = BigDecimal.ZERO;
					BigDecimal existingFlrArea = BigDecimal.ZERO;
					BigDecimal carpetArea = BigDecimal.ZERO;
					BigDecimal existingCarpetArea = BigDecimal.ZERO;
					BigDecimal existingBltUpArea = BigDecimal.ZERO;
					for (Occupancy occupancy : flr.getOccupancies()) {
						flrArea = flrArea.add(occupancy.getFloorArea());
						existingFlrArea = existingFlrArea.add(occupancy.getExistingFloorArea());
						carpetArea = carpetArea.add(occupancy.getCarpetArea());
						existingCarpetArea = existingCarpetArea.add(occupancy.getExistingCarpetArea());
					}

					List<Occupancy> occupancies = flr.getOccupancies();
					for (Occupancy occupancy : occupancies) {
						existingBltUpArea = existingBltUpArea
								.add(occupancy.getExistingBuiltUpArea() != null ? occupancy.getExistingBuiltUpArea()
										: BigDecimal.ZERO);
					}

					if (mostRestrictiveFar != null && mostRestrictiveFar.getConvertedSubtype() != null
							&& !R.equals(mostRestrictiveFar.getSubtype().getCode())) {
						if (carpetArea.compareTo(BigDecimal.ZERO) == 0) {
							pl.addError("Carpet area in block " + blk.getNumber() + "floor " + flr.getNumber(),
									"Carpet area is not defined in block " + blk.getNumber() + "floor "
											+ flr.getNumber());
						}

						if (existingBltUpArea.compareTo(BigDecimal.ZERO) > 0
								&& existingCarpetArea.compareTo(BigDecimal.ZERO) == 0) {
							pl.addError("Existing Carpet area in block " + blk.getNumber() + "floor " + flr.getNumber(),
									"Existing Carpet area is not defined in block " + blk.getNumber() + "floor "
											+ flr.getNumber());
						}
					}

					if (flrArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
							.compareTo(carpetArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
									DcrConstants.ROUNDMODE_MEASUREMENTS)) < 0) {
						pl.addError("Floor area in block " + blk.getNumber() + "floor " + flr.getNumber(),
								"Floor area is less than carpet area in block " + blk.getNumber() + "floor "
										+ flr.getNumber());
					}

					if (existingBltUpArea.compareTo(BigDecimal.ZERO) > 0 && existingFlrArea
							.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
							.compareTo(existingCarpetArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
									DcrConstants.ROUNDMODE_MEASUREMENTS)) < 0) {
						pl.addError("Existing floor area in block " + blk.getNumber() + "floor " + flr.getNumber(),
								"Existing Floor area is less than carpet area in block " + blk.getNumber() + "floor "
										+ flr.getNumber());
					}
				}
			}

			List<OccupancyTypeHelper> plotWiseOccupancyTypes = new ArrayList<>();
			for (Block block : pl.getBlocks()) {
				for (Occupancy occupancy : block.getBuilding().getOccupancies()) {
					if (occupancy.getTypeHelper().getType() != null)
						plotWiseOccupancyTypes.add(occupancy.getTypeHelper());
				}
			}

			Set<OccupancyTypeHelper> setOfDistinctOccupancyTypes = new HashSet<>(plotWiseOccupancyTypes);

			distinctOccupancyTypesHelper.addAll(setOfDistinctOccupancyTypes);

			List<Occupancy> occupanciesForPlan = new ArrayList<>();

			for (OccupancyTypeHelper occupancyType : setOfDistinctOccupancyTypes) {
				if (occupancyType != null) {
					BigDecimal totalFloorAreaForAllBlks = BigDecimal.ZERO;
					BigDecimal totalBuiltUpAreaForAllBlks = BigDecimal.ZERO;
					BigDecimal totalCarpetAreaForAllBlks = BigDecimal.ZERO;
					BigDecimal totalExistBuiltUpAreaForAllBlks = BigDecimal.ZERO;
					BigDecimal totalExistFloorAreaForAllBlks = BigDecimal.ZERO;
					BigDecimal totalExistCarpetAreaForAllBlks = BigDecimal.ZERO;
					Occupancy occupancy = new Occupancy();
					for (Block block : pl.getBlocks()) {
						for (Occupancy buildingOccupancy : block.getBuilding().getOccupancies()) {
							if (occupancyType.equals(buildingOccupancy.getTypeHelper())) {
								totalFloorAreaForAllBlks = totalFloorAreaForAllBlks
										.add(buildingOccupancy.getFloorArea());
								totalBuiltUpAreaForAllBlks = totalBuiltUpAreaForAllBlks
										.add(buildingOccupancy.getBuiltUpArea());
								totalCarpetAreaForAllBlks = totalCarpetAreaForAllBlks
										.add(buildingOccupancy.getCarpetArea());
								totalExistBuiltUpAreaForAllBlks = totalExistBuiltUpAreaForAllBlks
										.add(buildingOccupancy.getExistingBuiltUpArea());
								totalExistFloorAreaForAllBlks = totalExistFloorAreaForAllBlks
										.add(buildingOccupancy.getExistingFloorArea());
								totalExistCarpetAreaForAllBlks = totalExistCarpetAreaForAllBlks
										.add(buildingOccupancy.getExistingCarpetArea());
							}
						}
					}
					occupancy.setTypeHelper(occupancyType);
					occupancy.setBuiltUpArea(totalBuiltUpAreaForAllBlks);
					occupancy.setCarpetArea(totalCarpetAreaForAllBlks);
					occupancy.setFloorArea(totalFloorAreaForAllBlks);
					occupancy.setExistingBuiltUpArea(totalExistBuiltUpAreaForAllBlks);
					occupancy.setExistingFloorArea(totalExistFloorAreaForAllBlks);
					occupancy.setExistingCarpetArea(totalExistCarpetAreaForAllBlks);
					occupanciesForPlan.add(occupancy);
				}
			}

			pl.setOccupancies(occupanciesForPlan);
			pl.getVirtualBuilding().setTotalFloorArea(totalFloorArea);
			pl.getVirtualBuilding().setTotalCarpetArea(totalCarpetArea);
			// pl.getVirtualBuilding().setTotalExistingBuiltUpArea(totalExistingBuiltUpArea);
			pl.getVirtualBuilding().setTotalExistingFloorArea(totalExistingFloorArea);
			pl.getVirtualBuilding().setTotalExistingCarpetArea(totalExistingCarpetArea);
			pl.getVirtualBuilding().setOccupancyTypes(distinctOccupancyTypesHelper);
			pl.getVirtualBuilding().setTotalBuitUpArea(totalBuiltUpArea);
			pl.getVirtualBuilding().setMostRestrictiveFarHelper(getMostRestrictiveFar(setOfDistinctOccupancyTypes));

			if (!distinctOccupancyTypesHelper.isEmpty()) {
				int allResidentialOccTypesForPlan = 0;
				for (OccupancyTypeHelper occupancy : distinctOccupancyTypesHelper) {
					// setting residentialBuilding
					int residentialOccupancyType = 0;
					if (R.equals(occupancy.getType().getCode())) {
						residentialOccupancyType = 1;
					}
					if (residentialOccupancyType == 0) {
						allResidentialOccTypesForPlan = 0;
						break;
					} else {
						allResidentialOccTypesForPlan = 1;
					}
				}
				pl.getVirtualBuilding().setResidentialBuilding(allResidentialOccTypesForPlan == 1);
				int allResidentialOrCommercialOccTypesForPlan = 0;
				for (OccupancyTypeHelper occupancyType : distinctOccupancyTypesHelper) {
					int residentialOrCommercialOccupancyTypeForPlan = 0;
					if (R.equals(occupancyType.getType().getCode()) || B.equals(occupancyType.getType().getCode())) {
						residentialOrCommercialOccupancyTypeForPlan = 1;
					}
					if (residentialOrCommercialOccupancyTypeForPlan == 0) {
						allResidentialOrCommercialOccTypesForPlan = 0;
						break;
					} else {
						allResidentialOrCommercialOccTypesForPlan = 1;
					}
				}
				pl.getVirtualBuilding()
						.setResidentialOrCommercialBuilding(allResidentialOrCommercialOccTypesForPlan == 1);
			}
//			if (!pl.getVirtualBuilding().getResidentialOrCommercialBuilding()) {
//				pl.getErrors().put(DxfFileConstants.OCCUPANCY_ALLOWED_KEY, DxfFileConstants.OCCUPANCY_ALLOWED);
//				return pl;
//			}

			BigDecimal providedFar = BigDecimal.ZERO;
			BigDecimal surrenderRoadArea = BigDecimal.ZERO;

			if (!pl.getSurrenderRoads().isEmpty()) {
				for (Measurement measurement : pl.getSurrenderRoads()) {
					surrenderRoadArea = surrenderRoadArea.add(measurement.getArea());
				}
			}
			HashMap<String, String> errorsMsg = new HashMap<String, String>(); // for
			occupancyPercentage(pl, errorsMsg);

			pl.setTotalSurrenderRoadArea(surrenderRoadArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
					DcrConstants.ROUNDMODE_MEASUREMENTS));
			pl.getPlot().setArea(pl.getPlot().getPlotBndryArea());
			BigDecimal plotArea = pl.getPlot() != null ? pl.getPlot().getArea().add(surrenderRoadArea)
					: BigDecimal.ZERO;
			BigDecimal totalLiftArea = BigDecimal.ZERO;
			BigDecimal totalStairArea = BigDecimal.ZERO;
			BigDecimal totalBasementArea = BigDecimal.ZERO;
			BigDecimal totalBalconyArea = BigDecimal.ZERO;
			BigDecimal totalPorticosArea = BigDecimal.ZERO;
			BigDecimal totalMumtyArea = BigDecimal.ZERO;
			BigDecimal buildingHeight = BigDecimal.ZERO;
			BigDecimal totalFlrArea = pl.getVirtualBuilding().getTotalFloorArea();
			OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
			// Far Exemptions
			for (Block blk : pl.getBlocks()) {

				buildingHeight = blk.getBuilding().getBuildingHeight();
				for (Floor floor : blk.getBuilding().getFloors()) {
					try {
						if (floor.getLifts() != null) {
							for (Lift l : floor.getLifts()) {
								if (l.getArea() != null) {
									totalLiftArea = totalLiftArea.add(l.getArea());
									totalFlrArea = totalFlrArea.subtract(totalLiftArea);
								}
							}
						}
						if (floor.getGeneralStairs() != null) {
							for (Stair s : floor.getGeneralStairs()) {
								if (s.getArea() != null) {
									totalStairArea = totalStairArea.add(s.getArea());
									totalFlrArea = totalFlrArea.subtract(totalStairArea);
								}
							}
						}

						if (floor.getNumber() < 0) {
							if (floor.getArea() != null && floor.getArea().compareTo(BigDecimal.ZERO) > 0) {
								totalBasementArea = totalBasementArea.add(floor.getArea());
								totalFlrArea = totalFlrArea.subtract(totalBasementArea);
							}
						}
						if (floor.getBalconies() != null) {
							for (Balcony b : floor.getBalconies()) {
								if (b.getArea() == null || b.getArea().compareTo(BigDecimal.ZERO) <= 0
										&& (b.getMeasurements() != null && !b.getMeasurements().isEmpty())) {
									totalBalconyArea = totalBalconyArea.add(b.getMeasurements().stream()
											.map(Measurement::getArea).reduce(BigDecimal::add).get());
									totalFlrArea = totalFlrArea.subtract(totalBalconyArea);
								}
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
				if (blk.getPorticos() != null && !blk.getPorticos().isEmpty()) {
					try {
						totalPorticosArea = blk.getPorticos().stream()
								.filter(p -> p.getArea() != null && p.getArea().compareTo(BigDecimal.ZERO) > 0)
								.map(Measurement::getArea).reduce(BigDecimal::add).get();
						totalFlrArea = totalFlrArea.subtract(totalPorticosArea);
					} catch (Exception e) {
					}
				}

			}
//			System.out.println("After Exemption, Total Floor Area=" + totalFloorArea);
			if (plotArea.doubleValue() > 0)
				providedFar = totalFlrArea.divide(plotArea, DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS); // Calculation
			// FAR
			pl.setFarDetails(new FarDetails());
			pl.getFarDetails().setProvidedFar(providedFar.doubleValue());
			String typeOfArea = pl.getPlanInformation().getTypeOfArea();
			// get area from plan
			BigDecimal PlotArea = pl.getPlanInformation().getPlotArea();
			BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();

			String planInfoDate = "";
			Boolean existBuilding = false;
			for (Block b : pl.getBlocks()) {
				b.getBuilding().setDateOfConstruction(false);
				if (b.getBuilding().getTotalExistingBuiltUpArea().compareTo(BigDecimal.ZERO) > 0) {
					existBuilding = true;
				}

				if (existBuilding) {
					if (pl.getPlanInfoProperties().containsKey("DATE_OF_CONSTRUCTION")) {
						planInfoDate = pl.getPlanInfoProperties().get("DATE_OF_CONSTRUCTION");
					} else {
						pl.addError("DateOfConsError", "PLease mark DATE_OF_CONSTRUCTION = DD-MM-YYYY in plan info");
					}
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					LocalDate date = LocalDate.parse(planInfoDate, formatter);

					LocalDate cutoff = LocalDate.of(2017, 11, 10);

					if (date.isBefore(cutoff)) {
						b.getBuilding().setDateOfConstruction(true);
						System.out.println("Date is before 10 Nov 2017");
					} else {
						b.getBuilding().setDateOfConstruction(false);
						System.out.println("Date is NOT before 10 Nov 2017");
					}

				}
			}

			if (mostRestrictiveOccupancyType != null) {
				processFar(pl, mostRestrictiveOccupancyType, providedFar, PlotArea, errorMsgs, buildingHeight);
			}

			BigDecimal eavesHeight = BigDecimal.ZERO;
			BigDecimal ridgeHeight = BigDecimal.ZERO;
			Boolean eavesExist = false;
			Boolean ridgeExist = false;
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
				if (eavesExist && ridgeExist) {
					bldgHt = eavesHeight.add(ridgeHeight);
					bldgHt = bldgHt.divide(BigDecimal.valueOf(2));
				} else if (eavesExist && !ridgeExist) {
					pl.addError("RidgeError", "Ridge Height Required");
				} else if (!eavesExist && ridgeExist) {
					pl.addError("EavesError", "Eaves Height Required");
				}

				if (bldgHt != null) {
					b.getBuilding().setBuildingHeight(bldgHt);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;
	}

	private void occupancyPercentage(Plan pl, HashMap<String, String> errors) {
		try {

			// Please add principal occupancy at the bottom
			for (Block block : pl.getBlocks()) {
				ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
				scrutinyDetail1.setKey("Block_" + block.getName() + "_" + "APercentage");
//				scrutinyDetail1.setKey("Common_Percentage_Block_"+block.getNumber());
				scrutinyDetail1.setHeading("Occupancy Wise BUA Percentage ");
				scrutinyDetail1.addColumnHeading(1, "Occupancy");
				scrutinyDetail1.addColumnHeading(3, "BUA");
				scrutinyDetail1.addColumnHeading(4, "Percentage");
				List<Pair<String, BigDecimal>> occPer = new ArrayList<>();
				List<Pair<String, String>> occTypePair = new ArrayList<>();
				List<Pair<String, String>> occSubTypePair = new ArrayList<>();
				List<Pair<String, String>> subOccNamePair = new ArrayList<>();
				List<Pair<String, Integer>> subOccColorPair = new ArrayList<>();

				if (block.getBuilding() == null
						|| (block.getBuilding() != null && block.getBuilding().getFloors() == null)) {
					return;
				}

//				BigDecimal totalBuiltupArea = pl.getVirtualBuilding().getTotalFloorArea();
				BigDecimal totalBuiltupArea = BigDecimal.ZERO;
				for (Occupancy o : block.getBuilding().getTotalArea()) {
					if (o.getFloorArea().compareTo(BigDecimal.ZERO) > 0) {
						totalBuiltupArea = totalBuiltupArea.add(o.getFloorArea());
					}
				}
				BigDecimal perc = BigDecimal.ZERO;
				BigDecimal bua = BigDecimal.ZERO;
				BigDecimal maxArea = BigDecimal.ZERO;
				String occName = "";
				String subOccName = "";
				String ressubOccName = "";
				String mostResoccName = "";
				String occType = "";
				String occSubType = "";
				Integer occTypeColor = 0;
				Integer occSubColor = 0;
				for (Occupancy occ : block.getBuilding().getTotalArea()) {
					bua = occ.getFloorArea();
					perc = bua.multiply(BigDecimal.valueOf(100));
					perc = perc.divide(totalBuiltupArea, 0);
					occName = occ.getTypeHelper().getType().getName();
					subOccName = occ.getTypeHelper().getSubtype().getName();
					occPer.add(Pair.of(occName, perc));
					occTypePair.add(Pair.of(occName, occ.getTypeHelper().getType().getCode()));
					occSubTypePair.add(Pair.of(occName, occ.getTypeHelper().getSubtype().getCode()));
					subOccNamePair.add(Pair.of(occName, occ.getTypeHelper().getSubtype().getName()));
					subOccColorPair.add(Pair.of(occName, occ.getTypeHelper().getSubtype().getColor()));
					build(pl, occName, bua, perc, scrutinyDetail1);
				}
				for (Map.Entry<String, BigDecimal> entry : occPer) {
					if (entry.getValue().compareTo(maxArea) > 0) {
						maxArea = entry.getValue(); // Update max bua
						mostResoccName = entry.getKey();
					}
				}
//				System.out.println(maxArea);
//				System.out.println(mostResoccName);
				if (maxArea.compareTo(BigDecimal.valueOf(50)) < 0) {
					occType = block.getBuilding().getMostRestrictiveFarHelper().getType().getCode();
					occSubType = block.getBuilding().getMostRestrictiveFarHelper().getSubtype().getCode();
					occSubColor = block.getBuilding().getMostRestrictiveFarHelper().getSubtype().getColor();
					OccupancyHelperDetail occHD = new OccupancyHelperDetail();
					occHD.setCode(occType);
					occHD.setName("Mixed Occupancy");

//					occHD.setName(mostResoccName);
					block.getBuilding().getMostRestrictiveFarHelper().setConvertedType(occHD);
					OccupancyHelperDetail occHD1 = new OccupancyHelperDetail();
					occHD1.setName(ressubOccName);
					occHD1.setCode(occSubType);
					occHD1.setColor(occSubColor);
					block.getBuilding().getMostRestrictiveFarHelper().setConvertedSubtype(occHD1);
				} else {
					for (Map.Entry<String, String> entry : occTypePair) {
						if (entry.getKey().equalsIgnoreCase(mostResoccName)) {
							occType = entry.getValue();
						}
					}
					for (Map.Entry<String, String> entry : occSubTypePair) {
						if (entry.getKey().equalsIgnoreCase(mostResoccName)) {
							occSubType = entry.getValue();
						}
					}
					for (Map.Entry<String, String> entry : subOccNamePair) {
						if (entry.getKey().equalsIgnoreCase(mostResoccName)) {
							ressubOccName = entry.getValue();
						}
					}

					for (Map.Entry<String, Integer> entry : subOccColorPair) {
						if (entry.getKey().equalsIgnoreCase(mostResoccName)) {
							occSubColor = entry.getValue();
						}
					}

//					System.out.println(occType);
//					System.out.println(occSubType);
					OccupancyHelperDetail occHD = new OccupancyHelperDetail();
					occHD.setCode(occType);
					occHD.setName(mostResoccName);
					block.getBuilding().getMostRestrictiveFarHelper().setConvertedType(occHD);
					OccupancyHelperDetail occHD1 = new OccupancyHelperDetail();
					occHD1.setName(ressubOccName);
					occHD1.setCode(occSubType);
					occHD1.setColor(occSubColor);
					block.getBuilding().getMostRestrictiveFarHelper().setConvertedSubtype(occHD1);
					buildnew(pl, mostResoccName, ressubOccName, maxArea, scrutinyDetail1);
				}

			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

	private void build(Plan pl, String occName, BigDecimal BUA, BigDecimal perc, ScrutinyDetail scrutinyDetail) {

		Map<String, String> details = new HashMap<>();

//		details.put("Floor No", floorno.toString());
		details.put("Occupancy", occName);
		details.put("BUA", BUA.toString());
		details.put("Percentage", perc.toString());
		details.put(STATUS, Result.Verify.getResultVal());
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private void buildnew(Plan pl, String occName, String subocc, BigDecimal perc, ScrutinyDetail scrutinyDetail) {

		Map<String, String> details = new HashMap<>();

//		details.put("Floor No", floorno.toString());
		details.put("Occupancy", "Main Occupancy");
		details.put("BUA", occName + ", " + subocc);
		details.put("Percentage", perc.toString());
		details.put(STATUS, Result.Verify.getResultVal());
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private void validate2(Plan pl, Block blk, Floor flr, Occupancy occupancy) {
		String occupancyTypeHelper = StringUtils.EMPTY;
		if (occupancy.getTypeHelper() != null) {
			if (occupancy.getTypeHelper().getType() != null) {
				occupancyTypeHelper = occupancy.getTypeHelper().getType().getName();
			} else if (occupancy.getTypeHelper().getSubtype() != null) {
				occupancyTypeHelper = occupancy.getTypeHelper().getSubtype().getName();
			} else
				pl.addError("Occupancy null", "Occupancy Not defined");
		} else
			pl.addError("Occupancy null", "Occupancy Not defined");
		if (occupancy.getBuiltUpArea() != null && occupancy.getBuiltUpArea().compareTo(BigDecimal.valueOf(0)) < 0) {
			pl.addError(VALIDATION_NEGATIVE_BUILTUP_AREA, getLocaleMessage(VALIDATION_NEGATIVE_BUILTUP_AREA,
					blk.getNumber(), flr.getNumber().toString(), occupancyTypeHelper));
		}
		if (occupancy.getExistingBuiltUpArea() != null
				&& occupancy.getExistingBuiltUpArea().compareTo(BigDecimal.valueOf(0)) < 0) {
			pl.addError(VALIDATION_NEGATIVE_EXISTING_BUILTUP_AREA,
					getLocaleMessage(VALIDATION_NEGATIVE_EXISTING_BUILTUP_AREA, blk.getNumber(),
							flr.getNumber().toString(), occupancyTypeHelper));
		}
		occupancy.setFloorArea((occupancy.getBuiltUpArea() == null ? BigDecimal.ZERO : occupancy.getBuiltUpArea())
				.subtract(occupancy.getDeduction() == null ? BigDecimal.ZERO : occupancy.getDeduction()));
		if (occupancy.getFloorArea() != null && occupancy.getFloorArea().compareTo(BigDecimal.valueOf(0)) < 0) {
			pl.addError(VALIDATION_NEGATIVE_FLOOR_AREA, getLocaleMessage(VALIDATION_NEGATIVE_FLOOR_AREA,
					blk.getNumber(), flr.getNumber().toString(), occupancyTypeHelper));
		}
		occupancy.setExistingFloorArea(
				(occupancy.getExistingBuiltUpArea() == null ? BigDecimal.ZERO : occupancy.getExistingBuiltUpArea())
						.subtract(occupancy.getExistingDeduction() == null ? BigDecimal.ZERO
								: occupancy.getExistingDeduction()));
		if (occupancy.getExistingFloorArea() != null
				&& occupancy.getExistingFloorArea().compareTo(BigDecimal.valueOf(0)) < 0) {
			pl.addError(VALIDATION_NEGATIVE_EXISTING_FLOOR_AREA,
					getLocaleMessage(VALIDATION_NEGATIVE_EXISTING_FLOOR_AREA, blk.getNumber(),
							flr.getNumber().toString(), occupancyTypeHelper));
		}
	}

	protected OccupancyTypeHelper getMostRestrictiveFar(Set<OccupancyTypeHelper> distinctOccupancyTypes) {
		Set<String> codes = new HashSet<>();
		Map<String, OccupancyTypeHelper> codesMap = new HashMap<>();
		for (OccupancyTypeHelper typeHelper : distinctOccupancyTypes) {

			if (typeHelper.getType() != null)
				codesMap.put(typeHelper.getType().getCode(), typeHelper);
			if (typeHelper.getSubtype() != null)
				codesMap.put(typeHelper.getSubtype().getCode(), typeHelper);
		}
		codes = codesMap.keySet();
		if (codes.contains(IN1a))
			return codesMap.get(IN1a);
		else if (codes.contains(B1a))
			return codesMap.get(B1a);
		else if (codes.contains(A1c))
			return codesMap.get(A1c);
		else if (codes.contains(A1b))
			return codesMap.get(A1b);
		else if (codes.contains(A1a))
			return codesMap.get(A1a);
		else if (codes.contains(I1a))
			return codesMap.get(I1a);
		else if (codes.contains(E1a))
			return codesMap.get(E1a);
		else if (codes.contains(R1b))
			return codesMap.get(R1b);
		else if (codes.contains(R1a))
			return codesMap.get(R1a);
		else
			return null;

	}

	private void processFar(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far, BigDecimal PlotArea,
			HashMap<String, String> errors, BigDecimal buildingHeight) {

		String expectedResult = StringUtils.EMPTY;
		boolean isAccepted = false;
		String remarks = "";
		String status = "";
		if (buildingHeight.compareTo(BigDecimal.valueOf(14.5)) <= 0) {
			remarks = "";
			expectedResult = "No Restriction";
			status = "Accepted";
		} else if (buildingHeight.compareTo(BigDecimal.valueOf(14.5)) > 0) {
			if (occupancyType.getConvertedType().getCode().equalsIgnoreCase(R)) {
				if (far.compareTo(BigDecimal.valueOf(3)) <= 0) {
					remarks = "Permissible";
					status = "Accepted";
					pl.getFarDetails().setPermissableFar(3.00);
					expectedResult = "<= 3";
				} else if (far.compareTo(BigDecimal.valueOf(3)) > 0 && far.compareTo(BigDecimal.valueOf(5)) <= 0) {
					remarks = "additional fee + approval of ULB";
					status = "Verify";
					// pl.getFarDetails().setPermissableFar(3.00);
					expectedResult = ">3 and <=5";
				} else if (far.compareTo(BigDecimal.valueOf(5)) > 0) {
					if (pl.getPlanInfoProperties().containsKey("CBD_TOD")) {
						if (far.compareTo(BigDecimal.valueOf(7)) > 0) {
							remarks = "If present in Cnetral Business District AND Transit Oriented Development Corridor";
							status = "Verify";
							// pl.getFarDetails().setPermissableFar(3.00);
							expectedResult = ">7";
						}

					} else {
						remarks = "additional fee + approval of State Government";
						status = "Verify";
						// pl.getFarDetails().setPermissableFar(3.00);
						expectedResult = ">5";
					}

				}
			} else {
				remarks = "";
				expectedResult = "No Restriction";
				status = "Accepted";
			}

		}

		if (errors.isEmpty() && StringUtils.isNotBlank(expectedResult)) {
			buildResult(pl, occupancyType, far, expectedResult, isAccepted, remarks, status);
		} else {
			errors.put("FAR", "Error in Far");
		}

	}

	// BigDecimal far, String typeOfArea,
	private void buildResult(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far, String expectedResult,
			boolean isAccepted, String remarks, String status) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, OCCUPANCY);
		scrutinyDetail.addColumnHeading(5, PERMISSIBLE.concat(" in ratio"));
		scrutinyDetail.addColumnHeading(6, PROVIDED.concat(" in ratio"));
		scrutinyDetail.addColumnHeading(7, "Remarks");
		scrutinyDetail.addColumnHeading(8, STATUS);
		scrutinyDetail.setKey("Common_FAR");
//		scrutinyDetail.setKey("Block_" + blkNo + "_" + "AFar");
		scrutinyDetail.setHeading("FAR");

		String actualResult = far.toString();

		String occupancyName;
		if (occupancyType.getConvertedSubtype() != null)
			occupancyName = occupancyType.getConvertedSubtype().getName();
		else
			occupancyName = occupancyType.getConvertedType().getName();

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "50(6)(a)");
		details.put(OCCUPANCY, occupancyName);
		details.put(PERMISSIBLE.concat(" in ratio"), expectedResult);
		details.put(PROVIDED.concat(" in ratio"), actualResult);
		details.put("Remarks", remarks);
		details.put(STATUS, status);

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private ScrutinyDetail getFarScrutinyDetail(String key) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, "Area Type");
		scrutinyDetail.addColumnHeading(3, "Road Width");
		scrutinyDetail.addColumnHeading(4, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);
		scrutinyDetail.setKey(key);
		return scrutinyDetail;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
