package org.egov.client.edcr;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.feature.MezzanineFloorService;
import org.springframework.stereotype.Service;

import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.HEIGHTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.IN_METER;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.SQMTRS;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.entity.edcr.Balcony;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Hall;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.springframework.stereotype.Service;

@Service
public class MezzanineFloorService_Tripura extends MezzanineFloorService {

	private static final Logger LOG = Logger.getLogger(MezzanineFloorService_Tripura.class);
	private static final String SUBRULE_46 = "46";
	private static final String RULE46_MAXAREA_DESC = "Maximum allowed area of mezzanine floor";
	private static final String RULE46_MINAREA_DESC = "Minimum area of mezzanine floor";
	private static final String RULE46_DIM_DESC = "Minimum height of mezzanine floor";
	public static final String SUB_RULE_55_7_DESC = "Maximum allowed area of balcony";
	public static final String SUB_RULE_55_7 = "55-7";
	private static final String FLOOR = "Floor";
	public static final String HALL_NUMBER = "Hall Number";
	private static final BigDecimal AREA_9_POINT_5 = BigDecimal.valueOf(9.5);
	private static final BigDecimal HEIGHT_2_POINT_1 = BigDecimal.valueOf(2.1);

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		// validate(pl);
		try {
			String subRule = SUBRULE_46;
			if (pl != null && !pl.getBlocks().isEmpty()) {
				for (Block block : pl.getBlocks()) {
					scrutinyDetail = new ScrutinyDetail();
					scrutinyDetail.addColumnHeading(1, RULE_NO);
					scrutinyDetail.addColumnHeading(2, DESCRIPTION);
					scrutinyDetail.addColumnHeading(3, FLOOR);
					scrutinyDetail.addColumnHeading(4, REQUIRED);
					scrutinyDetail.addColumnHeading(5, PROVIDED);
					scrutinyDetail.addColumnHeading(6, STATUS);
					scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Mezzanine Floor");

					ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
					scrutinyDetail1.addColumnHeading(1, RULE_NO);
					scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
					scrutinyDetail1.addColumnHeading(3, "Floor No");
					scrutinyDetail1.addColumnHeading(4, "Mezzanine Room Area");
					scrutinyDetail1.addColumnHeading(5, REQUIRED);
					scrutinyDetail1.addColumnHeading(6, PROVIDED);
					scrutinyDetail1.addColumnHeading(7, STATUS);
					scrutinyDetail1.setKey("Block_" + block.getNumber() + "_" + "Mezzanine Ventilations");
					if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
						BigDecimal totalBuiltupArea = BigDecimal.ZERO;
						for (Floor floor : block.getBuilding().getFloors()) {
							BigDecimal builtupArea = BigDecimal.ZERO;
							for (Occupancy occ : floor.getOccupancies()) {
								if (!occ.getIsMezzanine() && occ.getBuiltUpArea() != null)
									builtupArea = builtupArea.add(occ.getBuiltUpArea().subtract(occ.getDeduction()));
							}
							totalBuiltupArea = totalBuiltupArea.add(builtupArea);
							for (Occupancy mezzanineFloor : floor.getOccupancies()) {
								if (mezzanineFloor.getIsMezzanine()) {
									if (mezzanineFloor.getBuiltUpArea() != null
											&& mezzanineFloor.getBuiltUpArea().doubleValue() > 0
											&& mezzanineFloor.getTypeHelper() == null) {
										pl.addError(OBJECTNOTDEFINED_DESC,
												getLocaleMessage("msg.error.mezz.occupancy.not.defined",
														block.getNumber(), String.valueOf(floor.getNumber()),
														mezzanineFloor.getMezzanineNumber()));
									}
									BigDecimal twentyfivepercent = BigDecimal.ZERO;
									if (floor.getNumber() < 1) {
										BigDecimal mezzanineFloorArea = BigDecimal.ZERO;
										if (mezzanineFloor.getBuiltUpArea() != null)
											mezzanineFloorArea = mezzanineFloor.getBuiltUpArea()
													.subtract(mezzanineFloor.getDeduction());

										boolean valid = false;

//	                                    BigDecimal oneThirdOfBuiltup = builtupArea.divide(BigDecimal.valueOf(3),
//	                                            DECIMALDIGITS_MEASUREMENTS,
//	                                            ROUNDMODE_MEASUREMENTS);

										if (floor.getNumber() == 0) {
											twentyfivepercent = (block.getCoverage().get(floor.getNumber()).getArea())
													.multiply(BigDecimal.valueOf(0.25));
										}

										if (mezzanineFloorArea.doubleValue() > 0
												&& mezzanineFloorArea.compareTo(twentyfivepercent) <= 0) {
											valid = true;
										}
										String floorNo = " floor " + floor.getNumber();

										if (valid) {
											setReportOutputDetails(pl, subRule,
													RULE46_MAXAREA_DESC + "( < 25% of covered area) ", floorNo,
													twentyfivepercent + SQMTRS, mezzanineFloorArea + SQMTRS,
													Result.Accepted.getResultVal());
										} else {
											setReportOutputDetails(pl, subRule,
													RULE46_MAXAREA_DESC + "( < 25% of covered area) " + floorNo,
													floorNo, twentyfivepercent + SQMTRS, mezzanineFloorArea + SQMTRS,
													Result.Not_Accepted.getResultVal());
										}
									}

								}
							}
							BigDecimal mezArea = BigDecimal.ZERO;
							for (Room r : floor.getMezzanineRooms()) {
								mezArea = r.getMezzanineAreas().get(0).getBuiltUpArea();
								String floorNo = " floor " + floor.getNumber();
								BigDecimal height = r.getMezzanineAreas().get(0).getHeight();
								if (height.compareTo(BigDecimal.ZERO) == 0) {
									pl.addError(RULE46_DIM_DESC,
											getLocaleMessage(HEIGHTNOTDEFINED, "Mezzanine floor " + floorNo,
													block.getName(), String.valueOf(floor.getNumber())));
								} else if (height.compareTo(HEIGHT_2_POINT_1) >= 0) {
									setReportOutputDetails(pl, subRule, RULE46_DIM_DESC + " ", floorNo,
											HEIGHT_2_POINT_1 + IN_METER, height + IN_METER,
											Result.Accepted.getResultVal());
								} else {
									setReportOutputDetails(pl, subRule, RULE46_DIM_DESC + " ", floorNo,
											HEIGHT_2_POINT_1 + IN_METER, height + IN_METER,
											Result.Not_Accepted.getResultVal());
								}

								BigDecimal ventarea = BigDecimal.ZERO;
								if (r.getLightAndVentilation() != null) {
									for (Measurement m : r.getLightAndVentilation().getMeasurements()) {
										ventarea = ventarea.add(m.getArea());
									}

								} else {
									pl.addError("LightVentMEzError"+block.getNumber()+floorNo,
											"Block "+block.getNumber()+" Lights and ventilations not defined for mezannine floor " + floorNo);
								}
								buildVentilation(pl, block, floor.getNumber(), ventarea, mezArea, scrutinyDetail1);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
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

	public void buildVentilation(Plan pl, Block b, Integer floorNo, BigDecimal ventsarea, BigDecimal mezArea,
			ScrutinyDetail scrutinyDetail1) {
		Boolean status = false;

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "60 & 70 of TBR");
		details.put(DESCRIPTION, "Mezzanine Ventilation Area");
		details.put("Floor No", floorNo.toString());
		details.put("Mezzanine Room Area", mezArea.toString());
		details.put(REQUIRED, ">0.1 times of mezArea");
		details.put(PROVIDED, ventsarea.toString());
		if (ventsarea.compareTo(mezArea.multiply(BigDecimal.valueOf(0.1))) > 0) {
			status = true;
		}
		if (status)
			details.put(STATUS, "Accepted");
		else
			details.put(STATUS, "Not Accepted");
		scrutinyDetail1.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail1);
	}

}
