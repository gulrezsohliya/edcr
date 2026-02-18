package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.client.edcr.util.Utility;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.MeasurementWithHeight;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.BathRoomWaterClosets;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class BathRoomWaterClosets_Sikkim extends BathRoomWaterClosets {

	private static final Logger LOG = Logger.getLogger(BathRoomWaterClosets_Sikkim.class);
	private static final String RULE_16 = "16";
	public static final String BATHROOMWATERCLOSETS_DESCRIPTION = "Bathroom Water Closets";
	public static final String COMMON_BATHROOMWATERCLOSETS_DESCRIPTION = "Common Bathroom Water Closets";
	public static final String FEMALE_BATHROOMWATERCLOSETS_DESCRIPTION = "Female Bathroom Water Closets";
	public static final String MALE_BATHROOMWATERCLOSETS_DESCRIPTION = "Male Bathroom Water Closets";
	private static final BigDecimal MIN_HEIGHT_SK_2_75 = BigDecimal.valueOf(2.75);// metres
	private static final BigDecimal MIN_WIDTH_SK_1_21 = BigDecimal.valueOf(1.21);// metres
	private static final BigDecimal MIN_AREA_SK_2_32 = BigDecimal.valueOf(2.32);// metres
	private static final BigDecimal MIN_LIGHTANDVENTILATIONAREA_SK_25 = BigDecimal.valueOf(25);// percentage

	private static final String FLOOR = "Floor";
	private static final String LAYER_ROOM_HEIGHT = "Block %s Floor %s %s height";
	private static final String LAYER_ROOM_WIDTH = "Block %s Floor %s %s width";
	private static final String LAYER_ROOM_AREA = "Block %s %s%s%s area";

	@Override
	public Plan validate(Plan pl) {
		HashMap<String, String> errors = new HashMap<>();

		Boolean isAnyBathroomWaterClosetsDefined = false, isBathroomWaterClosetsDefined = false,
				isCommonRoomWaterClosetsDefined = false, isFemaleRoomWaterClosetsDefined = false,
				isMaleRoomWaterClosetsDefined = false, isResidential = false;

		BigDecimal minHeight = BigDecimal.ZERO, minWidth = BigDecimal.ZERO, totalArea = BigDecimal.ZERO;
		List<BigDecimal> roomFloorAreas = new ArrayList<BigDecimal>();
		String errorKey = StringUtils.EMPTY, errorMsg = StringUtils.EMPTY;

		for (Block b : pl.getBlocks()) {
			if(b.getResidentialBuilding()!=null)
				isResidential = b.getResidentialBuilding();

			isAnyBathroomWaterClosetsDefined = Utility.checkIfAnyBatroomDefined(b);

			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, REQUIRED);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);
			List<Measurement> commonRoomWaterClosets = null;
			List<Measurement> femaleRoomWaterClosets = null;
			List<Measurement> maleRoomWaterClosets = null;
			if(b.getSanityDetails()!=null) {
				if(b.getSanityDetails().getCommonRoomsWithWaterCloset()!=null)
					commonRoomWaterClosets = b.getSanityDetails().getCommonRoomsWithWaterCloset();
				if(b.getSanityDetails().getFemaleRoomsWithWaterCloset()!=null)
					femaleRoomWaterClosets = b.getSanityDetails().getFemaleRoomsWithWaterCloset();
				if(b.getSanityDetails().getMaleRoomsWithWaterCloset()!=null)
					maleRoomWaterClosets = b.getSanityDetails().getMaleRoomsWithWaterCloset();
			}

			if (commonRoomWaterClosets != null && !commonRoomWaterClosets.isEmpty()) {
				isCommonRoomWaterClosetsDefined = true;
				scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + COMMON_BATHROOMWATERCLOSETS_DESCRIPTION);

				totalArea = commonRoomWaterClosets.stream()
						.filter(action -> action.getArea() != null & action.getArea().compareTo(BigDecimal.ZERO) > 0)
						.map(Measurement::getArea).reduce(BigDecimal::add).get();

				if (totalArea != null && totalArea.compareTo(BigDecimal.ZERO) > 0) {
					if (totalArea.compareTo(MIN_AREA_SK_2_32) >= 0) {
						setReportOutputDetails(pl, RULE_16, " minimum area",
								"Area >=" + MIN_AREA_SK_2_32 + DcrConstants.IN_METER_SQR,
								totalArea
										.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
												DcrConstants.ROUNDMODE_MEASUREMENTS)
										.toString() + DcrConstants.IN_METER_SQR,
								Result.Accepted.getResultVal());
					} else {
						setReportOutputDetails(pl, RULE_16, " minimum area",
								"Area >=" + MIN_AREA_SK_2_32 + DcrConstants.IN_METER_SQR,
								totalArea
										.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
												DcrConstants.ROUNDMODE_MEASUREMENTS)
										.toString() + DcrConstants.IN_METER_SQR,
								Result.Not_Accepted.getResultVal());
					}
				} else {
					errorKey = String.format(LAYER_ROOM_AREA, b.getNumber(), "", "",
							COMMON_BATHROOMWATERCLOSETS_DESCRIPTION);
					errorMsg = String.format(errorKey + DcrConstants.OBJECTDEFINED_DESC);
					errors.put(errorKey, errorMsg);
				}
			}

			totalArea = BigDecimal.ZERO;
			if (femaleRoomWaterClosets != null && !femaleRoomWaterClosets.isEmpty()) {
				if (isResidential)
					scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + COMMON_BATHROOMWATERCLOSETS_DESCRIPTION);
				else
					scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + FEMALE_BATHROOMWATERCLOSETS_DESCRIPTION);

				isFemaleRoomWaterClosetsDefined = true;
				totalArea = femaleRoomWaterClosets.stream()
						.filter(action -> action.getArea() != null & action.getArea().compareTo(BigDecimal.ZERO) > 0)
						.map(Measurement::getArea).reduce(BigDecimal::add).get();

				if (totalArea != null && totalArea.compareTo(BigDecimal.ZERO) > 0) {
					if (totalArea.compareTo(MIN_AREA_SK_2_32) >= 0) {
						setReportOutputDetails(pl, RULE_16, " minimum area",
								"Area >=" + MIN_AREA_SK_2_32 + DcrConstants.IN_METER_SQR,
								totalArea
										.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
												DcrConstants.ROUNDMODE_MEASUREMENTS)
										.toString() + DcrConstants.IN_METER_SQR,
								Result.Accepted.getResultVal());
					} else {
						setReportOutputDetails(pl, RULE_16, " minimum area",
								"Area >=" + MIN_AREA_SK_2_32 + DcrConstants.IN_METER_SQR,
								totalArea
										.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
												DcrConstants.ROUNDMODE_MEASUREMENTS)
										.toString() + DcrConstants.IN_METER_SQR,
								Result.Not_Accepted.getResultVal());
					}
				} else {
					errorKey = String.format(LAYER_ROOM_AREA, b.getNumber(), "", "",
							FEMALE_BATHROOMWATERCLOSETS_DESCRIPTION);
					errorMsg = String.format(errorKey + DcrConstants.OBJECTDEFINED_DESC);
					errors.put(errorKey, errorMsg);
				}
			}

			totalArea = BigDecimal.ZERO;
			if (maleRoomWaterClosets != null && !maleRoomWaterClosets.isEmpty()) {
				LOG.info("isMaleRoomWaterClosetsDefined");
				if (isResidential)
					scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + COMMON_BATHROOMWATERCLOSETS_DESCRIPTION);
				else
					scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + MALE_BATHROOMWATERCLOSETS_DESCRIPTION);
				isMaleRoomWaterClosetsDefined = true;
				totalArea = maleRoomWaterClosets.stream()
						.filter(action -> action.getArea() != null & action.getArea().compareTo(BigDecimal.ZERO) > 0)
						.map(Measurement::getArea).reduce(BigDecimal::add).get();

				if (totalArea != null && totalArea.compareTo(BigDecimal.ZERO) > 0) {
					if (totalArea.compareTo(MIN_AREA_SK_2_32) >= 0) {
						setReportOutputDetails(pl, RULE_16,  " minimum area",
								"Area >=" + MIN_AREA_SK_2_32 + DcrConstants.IN_METER_SQR,
								totalArea
										.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
												DcrConstants.ROUNDMODE_MEASUREMENTS)
										.toString() + DcrConstants.IN_METER_SQR,
								Result.Accepted.getResultVal());
					} else {
						setReportOutputDetails(pl, RULE_16, " minimum area",
								"Area >=" + MIN_AREA_SK_2_32 + DcrConstants.IN_METER_SQR,
								totalArea
										.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
												DcrConstants.ROUNDMODE_MEASUREMENTS)
										.toString() + DcrConstants.IN_METER_SQR,
								Result.Not_Accepted.getResultVal());
					}
				} else {
					errorKey = String.format(LAYER_ROOM_AREA, b.getNumber(), "", "",
							MALE_BATHROOMWATERCLOSETS_DESCRIPTION);
					errorMsg = String.format(errorKey + DcrConstants.OBJECTDEFINED_DESC);
					errors.put(errorKey, errorMsg);
				}
			}

			if (b.getBuilding() != null && b.getBuilding().getFloors() != null
					&& !b.getBuilding().getFloors().isEmpty()) {

				scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, FLOOR);
				scrutinyDetail.addColumnHeading(4, REQUIRED);
				scrutinyDetail.addColumnHeading(5, PROVIDED);
				scrutinyDetail.addColumnHeading(6, STATUS);
				scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + BATHROOMWATERCLOSETS_DESCRIPTION);

				for (Floor f : b.getBuilding().getFloors()) {
					if (f.getBathRoomWaterClosets() != null && f.getBathRoomWaterClosets().getRooms() != null
							&& !f.getBathRoomWaterClosets().getRooms().isEmpty()
							&& f.getBathRoomWaterClosets().getRooms().size() > 0) {
						LOG.info("isBathroomWaterClosetsDefined");
						isBathroomWaterClosetsDefined = true;

						if (f.getBathRoomWaterClosets().getHeights() != null
								&& !f.getBathRoomWaterClosets().getHeights().isEmpty()) {
							minHeight = f.getBathRoomWaterClosets().getHeights().get(0).getHeight();
							for (RoomHeight rh : f.getBathRoomWaterClosets().getHeights()) {
								if (rh.getHeight().compareTo(minHeight) < 0) {
									minHeight = rh.getHeight();
								}
							}
						} else {
							/*
							 * errorKey = String.format(LAYER_ROOM_HEIGHT, b.getNumber(), f.getNumber(),
							 * BathroomWaterClosets_DESCRIPTION); errorMsg = String.format(errorKey +
							 * DcrConstants.OBJECTDEFINED_DESC); errors.put(errorKey, errorMsg);
							 */
						}

						if (f.getBathRoomWaterClosets().getRooms() != null
								&& !f.getBathRoomWaterClosets().getRooms().isEmpty()) {
							minWidth = f.getBathRoomWaterClosets().getRooms().get(0).getWidth();
							for (Measurement m : f.getBathRoomWaterClosets().getRooms()) {
								roomFloorAreas.add(m.getArea());
								if (m.getWidth().compareTo(minWidth) < 0) {
									minWidth = m.getWidth();
								}
								if (m.getArea() != null)
									totalArea.add(m.getArea());
								/*
								 * if (m.getArea().compareTo(minArea) < 0 &&
								 * m.getArea().compareTo(BigDecimal.ZERO) > 0) { minArea = m.getArea(); }
								 */
							}
						} else {
							errorKey = String.format(LAYER_ROOM_WIDTH, b.getNumber(), f.getNumber(),
									BATHROOMWATERCLOSETS_DESCRIPTION);
							errorMsg = String.format(errorKey + DcrConstants.OBJECTDEFINED_DESC);
							errors.put(errorKey, errorMsg);

							errorKey = String.format(LAYER_ROOM_AREA, b.getNumber(), f.getNumber(),
									BATHROOMWATERCLOSETS_DESCRIPTION);
							errorMsg = String.format(errorKey + DcrConstants.OBJECTDEFINED_DESC);
							errors.put(errorKey, errorMsg);
						}

						/*
						 * if (minHeight.compareTo(MIN_HEIGHT_SK_2_75) >= 0) {
						 * setReportOutputDetails(pl, RULE_16, BathroomWaterClosets_DESCRIPTION +
						 * " minimum height", FLOOR + " " + f.getNumber(), "Height >=" +
						 * MIN_HEIGHT_SK_2_75 + DcrConstants.IN_METER, minHeight.toString(),
						 * Result.Accepted.getResultVal());
						 * 
						 * } else { setReportOutputDetails(pl, RULE_16, BathroomWaterClosets_DESCRIPTION
						 * + " minimum height", FLOOR + " " + f.getNumber(), "Height >=" +
						 * MIN_HEIGHT_SK_2_75 + DcrConstants.IN_METER, minHeight.toString(),
						 * Result.Not_Accepted.getResultVal()); }
						 */

						/*
						 * if (minWidth.compareTo(MIN_WIDTH_SK_1_21) >= 0) { setReportOutputDetails(pl,
						 * RULE_16, BathroomWaterClosets_DESCRIPTION + " minimum width", FLOOR + " " +
						 * f.getNumber(), "Width >=" + MIN_WIDTH_SK_1_21 + DcrConstants.IN_METER,
						 * minWidth.toString(), Result.Accepted.getResultVal()); } else {
						 * setReportOutputDetails(pl, RULE_16, BathroomWaterClosets_DESCRIPTION +
						 * " minimum width", FLOOR + " " + f.getNumber(), "Width >=" + MIN_WIDTH_SK_1_21
						 * + DcrConstants.IN_METER, minWidth.toString(),
						 * Result.Not_Accepted.getResultVal()); }
						 */

						/*
						 * if (totalArea != null) { if (totalArea.compareTo(MIN_AREA_SK_2_32) >= 0) {
						 * setReportOutputDetails(pl, RULE_16, BATHROOMWATERCLOSETS_DESCRIPTION +
						 * " minimum area", FLOOR + " " + f.getNumber(), "Area >=" + MIN_AREA_SK_2_32 +
						 * DcrConstants.IN_METER_SQR, totalArea.toString() + DcrConstants.IN_METER_SQR,
						 * Result.Accepted.getResultVal()); } else { setReportOutputDetails(pl, RULE_16,
						 * BATHROOMWATERCLOSETS_DESCRIPTION + " minimum area", FLOOR + " " +
						 * f.getNumber(), "Area >=" + MIN_AREA_SK_2_32 + DcrConstants.IN_METER_SQR,
						 * totalArea.toString() + DcrConstants.IN_METER_SQR,
						 * Result.Not_Accepted.getResultVal()); } } else { errorKey =
						 * String.format(LAYER_ROOM_AREA, b.getNumber(), f.getNumber(),
						 * BATHROOMWATERCLOSETS_DESCRIPTION); errorMsg = String.format(errorKey +
						 * DcrConstants.OBJECTDEFINED_DESC); errors.put(errorKey, errorMsg); }
						 */

						/* totalArea = roomFloorAreas.stream().reduce(BigDecimal::add).get(); */
					}
				}

				if (!isAnyBathroomWaterClosetsDefined && !isBathroomWaterClosetsDefined
						&& !isCommonRoomWaterClosetsDefined && !isFemaleRoomWaterClosetsDefined
						&& !isMaleRoomWaterClosetsDefined) {
					errorKey = String.format("Block %s %s %s", b.getNumber(), BATHROOMWATERCLOSETS_DESCRIPTION,
							DcrConstants.OBJECTNOTDEFINED);
					errorMsg = String.format("Block %s %s %s", b.getNumber(), BATHROOMWATERCLOSETS_DESCRIPTION,
							DcrConstants.OBJECTNOTDEFINED_DESC);
					errors.put(errorKey, errorMsg);
				}
			}

		}

		if (errors != null && !errors.isEmpty())
			pl.addErrors(errors);
		return pl;
	}

	@Override
	public Plan process(Plan pl) {

		LOG.info("DECENT: " + BATHROOMWATERCLOSETS_DESCRIPTION);
		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));

			return pl;
		}

		validate(pl);

		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
			String status) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String floor, String expected,
			String actual, String status) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(FLOOR, floor);
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}
}
