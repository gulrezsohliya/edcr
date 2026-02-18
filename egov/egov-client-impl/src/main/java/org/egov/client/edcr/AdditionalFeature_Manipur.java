package org.egov.client.edcr;

import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.constants.DxfFileConstants.C;
import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;
import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
//import org.egov.common.entity.bpa.Occupancy;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Passage;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.AdditionalFeature;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AdditionalFeature_Manipur extends AdditionalFeature {
	private static final Logger LOG = Logger.getLogger(AdditionalFeature_Manipur.class);
	private static final BigDecimal PLOTEA_90 = BigDecimal.valueOf(90);
	private static final BigDecimal PLOTEA_150 = BigDecimal.valueOf(150);
	private static final BigDecimal PLOTEA_300 = BigDecimal.valueOf(300);
	private static final BigDecimal PLOTEA_500 = BigDecimal.valueOf(500);
	private static final BigDecimal PLOTEA_1000 = BigDecimal.valueOf(1000);
	private static final BigDecimal PLOTEA_1500 = BigDecimal.valueOf(1500);
	private static final BigDecimal PLOTEA_750 = BigDecimal.valueOf(750);
	private static final BigDecimal PLOTEA_2000 = BigDecimal.valueOf(2000);
	private static final BigDecimal PLOTEA_2500 = BigDecimal.valueOf(2500);

	public static final String N0_OF_FLOORS = "No of Floors";
	private static final BigDecimal CORRIDOR_WIDTH_1 = BigDecimal.valueOf(1);
	private static final BigDecimal CORRIDOR_WIDTH_1_8 = BigDecimal.valueOf(1.8);
	private static final BigDecimal CORRIDOR_WIDTH_1_5 = BigDecimal.valueOf(1.5);

	@Override
	public Plan validate(Plan pl) {
		HashMap<String, String> errors = new HashMap<String, String>();
		try {
			noOfFloors(pl, errors);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
//		corridors(pl, errors);

		return pl;
	}

	private void corridors(Plan pl, HashMap<String, String> errors) {
		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Corridor");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		Map<String, String> details = new HashMap<>();
		BigDecimal minWidth = BigDecimal.ZERO;
		BigDecimal requiredCorridorWidth = BigDecimal.ZERO;
		String rule = "";
		for (Block block : pl.getBlocks()) {
			if (block.getBuilding() != null) {
				if (block.getBuilding().getPassage() != null) {
					Occupancy occupancy = new Occupancy();
					occupancy.setTypeHelper(block.getBuilding().getMostRestrictiveFarHelper());
					if ((occupancy.getTypeHelper().getType() != null
							&& R.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode()))) {
						rule = "54 F(8)(b)";
						System.out.println(block.getBuilding().getPassage().getWidth());
						minWidth = block.getBuilding().getPassage().getWidth();
						if (pl.getPlanInfoProperties().get("SINGLE_LOADED") != null) {
							if (pl.getPlanInfoProperties().get("SINGLE_LOADED").equalsIgnoreCase("YES"))
								requiredCorridorWidth = CORRIDOR_WIDTH_1;
						}else if (pl.getPlanInfoProperties().get("DOUBLE_LOADED") != null) {
							if (pl.getPlanInfoProperties().get("DOUBLE_LOADED").equalsIgnoreCase("YES"))
								requiredCorridorWidth = CORRIDOR_WIDTH_1_8;
						} 

					}else
						requiredCorridorWidth = CORRIDOR_WIDTH_1_5;
					details.put(RULE_NO, rule);
					details.put(DESCRIPTION, "Corridor");
					details.put(REQUIRED, requiredCorridorWidth.toString());
					details.put(PROVIDED, minWidth.toString());
					if (minWidth.compareTo(requiredCorridorWidth) >= 0)
						details.put(STATUS, Result.Accepted.getResultVal());
					else
						details.put(STATUS, Result.Not_Accepted.getResultVal());
					scrutinyDetail.getDetail().add(details);

					pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

				}else
					pl.addError("Passage", "Corridors Not Defined");

			}
		}

	}

	private void noOfFloors(Plan pl, HashMap<String, String> errors) {
		try {
			Boolean isAccepted = false;
			Boolean isCbdTod = false;
			BigDecimal roadWidth = BigDecimal.ZERO;
			if (pl.getPlanInfoProperties().containsKey("CBD_TOD")) {
				if (pl.getPlanInfoProperties().get("CBD_TOD").equalsIgnoreCase("YES")) {
					isCbdTod = true;
				}
			}
			Boolean isTDR = false;
			if (pl.getPlanInfoProperties().containsKey("TDR")) {
				if (pl.getPlanInfoProperties().get("TDR").equalsIgnoreCase("YES")) {
					isTDR = true;
				}
			}

			if(pl.getPlanInformation().getRoadWidth()!=null) {
				roadWidth = pl.getPlanInformation().getRoadWidth();
			}
			String noOfFloorsAllowed = StringUtils.EMPTY;
			BigDecimal totalFloorArea = BigDecimal.valueOf(0);
			for (Block block : pl.getBlocks()) {
				BigDecimal maxFloorArea = BigDecimal.valueOf(0);
				BigDecimal topFloorArea = BigDecimal.valueOf(0);
				BigDecimal maxFloor = BigDecimal.valueOf(0), minFloor = BigDecimal.ZERO;

				if (block.getBuilding().getFloors() != null && !block.getBuilding().getFloors().isEmpty()) {
					minFloor = new BigDecimal(block.getBuilding().getFloors().stream()
							.min(Comparator.comparing(Floor::getNumber)).get().getNumber());
//					maxFloor = new BigDecimal(block.getBuilding().getFloors().stream().max(Comparator.comparing(Floor::getNumber)).get().getNumber());
					System.out.println(block.getBuilding().getMaxFloor());

					maxFloor = block.getBuilding().getMaxFloor();
				}
				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.setKey("Block_" + block.getName() + "_" + N0_OF_FLOORS);
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
				scrutinyDetail.addColumnHeading(6, PROVIDED);
				scrutinyDetail.addColumnHeading(7, STATUS);
				scrutinyDetail.setHeading(N0_OF_FLOORS);
				for (Occupancy occupancy : block.getBuilding().getOccupancies()) {
					BigDecimal PlotArea = pl.getPlanInformation().getPlotArea();
					BigDecimal noOfFloors = block.getBuilding().getMaxFloor();
					
					if (occupancy.getTypeHelper().getType() != null
							&& occupancy.getTypeHelper().getType().getCode().equals(R)) {
						if (occupancy.getTypeHelper().getSubtype().getCode().equals(R1)
								|| occupancy.getTypeHelper().getSubtype().getCode().equals(R2)) {
							if (PlotArea.compareTo(BigDecimal.valueOf(50)) >= 0 && PlotArea.compareTo(PLOTEA_90) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(2)) <= 0;
								noOfFloorsAllowed = "<= G+1";
							}
							else if (PlotArea.compareTo(PLOTEA_90) > 0 && PlotArea.compareTo(PLOTEA_150) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(3)) <= 0;
								noOfFloorsAllowed = "<= G+2";
							} else if (PlotArea.compareTo(PLOTEA_150) > 0 && PlotArea.compareTo(PLOTEA_300) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(3)) <= 0;
								noOfFloorsAllowed = "<= G+2";

							} else if (PlotArea.compareTo(PLOTEA_300) > 0 && PlotArea.compareTo(PLOTEA_500) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(4)) <= 0;
								noOfFloorsAllowed = "<= G+3";

							} else if (PlotArea.compareTo(PLOTEA_500) > 0 && PlotArea.compareTo(PLOTEA_750) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(4)) <= 0;
								noOfFloorsAllowed = "<= G+3";

							} else if (PlotArea.compareTo(PLOTEA_750) > 0 && PlotArea.compareTo(PLOTEA_1000) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(5)) <= 0;
								noOfFloorsAllowed = "<= G+4";
							} else if (PlotArea.compareTo(PLOTEA_1000) > 0 && PlotArea.compareTo(PLOTEA_1500) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(6)) <= 0;
								noOfFloorsAllowed = "<= G+5";

							} else if (PlotArea.compareTo(PLOTEA_1500) > 0 && PlotArea.compareTo(PLOTEA_2000) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(7)) <= 0;
								noOfFloorsAllowed = "<= G+6";
							} else if (PlotArea.compareTo(PLOTEA_2000) > 0 && PlotArea.compareTo(PLOTEA_2500) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(8)) <= 0;
								noOfFloorsAllowed = "<= G+7";
							}else {
								pl.addError("Floor Height Area", "No Of Floors: Plot Area less than 90 m or greater than 2500 m");
							}
						} else if (occupancy.getTypeHelper().getType() != null
								&& occupancy.getTypeHelper().getType().getCode().equals(C)) {
							if (isCbdTod) {
								if(isTDR) {
									if (roadWidth.compareTo(BigDecimal.valueOf(30)) > 0) {
										if (PlotArea.compareTo(PLOTEA_1000) > 0) {
											isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(13)) <= 0;
											noOfFloorsAllowed = "<= G+12";
										}
									} else {
										isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(6)) <= 0;
										noOfFloorsAllowed = "<= G+5";
									}
								}else {
									if (roadWidth.compareTo(BigDecimal.valueOf(30)) > 0) {
										if (PlotArea.compareTo(PLOTEA_1000) > 0) {
											isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(9)) <= 0;
											noOfFloorsAllowed = "<= G+8";
										}
									} else {
										isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(6)) <= 0;
										noOfFloorsAllowed = "<= G+5";
									}
								}
								

							}else {
								if (PlotArea.compareTo(PLOTEA_90) >= 0 && PlotArea.compareTo(PLOTEA_150) <= 0) {
									isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(3)) <= 0;
									noOfFloorsAllowed = "<= G+2";
								
							} else if (PlotArea.compareTo(PLOTEA_150) > 0 && PlotArea.compareTo(PLOTEA_300) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(3)) <= 0;
								noOfFloorsAllowed = "<= G+2";
								
							} else if (PlotArea.compareTo(PLOTEA_300) >= 0
									&& PlotArea.compareTo(PLOTEA_500) < 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(4)) <= 0;
								noOfFloorsAllowed = "<= G+3";

								
							} else if (PlotArea.compareTo(PLOTEA_500) > 0
									&& PlotArea.compareTo(PLOTEA_750) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(4)) <= 0;
								noOfFloorsAllowed = "<= G+3";

								
							} else if (PlotArea.compareTo(PLOTEA_750) > 0
									&& PlotArea.compareTo(PLOTEA_1000) <= 0) {
							
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(5)) <= 0;
								noOfFloorsAllowed = "<= G+4";
							} else {
								if (roadWidth.compareTo(BigDecimal.valueOf(30)) < 0) {
									if (PlotArea.compareTo(PLOTEA_1000) > 0) {
										isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(6)) <= 0;
										noOfFloorsAllowed = "<= G+5";
									}
								}else {
									 if (PlotArea.compareTo(PLOTEA_1000) > 0) {
										 isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(9)) <= 0;
											noOfFloorsAllowed = "<= G+8";
										}
								}
								
							}
							}
						} else if (occupancy.getTypeHelper().getType() != null
								&& occupancy.getTypeHelper().getType().getCode().equals(IN)) {
							if (PlotArea.compareTo(PLOTEA_750) >= 0 && PlotArea.compareTo(PLOTEA_1000) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(5)) <= 0;
								noOfFloorsAllowed = "<= G+4";
							} else if (PlotArea.compareTo(PLOTEA_1000) > 0 && PlotArea.compareTo(PLOTEA_1500) <= 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(6)) <= 0;
								noOfFloorsAllowed = "<= G+5";
							} else if (PlotArea.compareTo(PLOTEA_1500) > 0) {
								isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(7)) <= 0;
								noOfFloorsAllowed = "<= G+6";
							}  
						}else if (occupancy.getTypeHelper().getType() != null
								&& occupancy.getTypeHelper().getType().getCode().equals(I)) {
							if (occupancy.getTypeHelper().getSubtype().getCode().equals(I1)) {
								if (PlotArea.compareTo(PLOTEA_300) >=0 ) {
									isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(3)) <= 0;
									noOfFloorsAllowed = "<= G+3";
								} else if (PlotArea.compareTo(PLOTEA_1500) > 0 ) {
									isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(4)) <= 0;
									noOfFloorsAllowed = "<= G+3";

								}

							} else if (occupancy.getTypeHelper().getSubtype().getCode().equals(I2)) {
								 if (PlotArea.compareTo(PLOTEA_1000) > 0 && PlotArea.compareTo(PLOTEA_2000) <= 0) {
									 isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(7)) <= 0;
										noOfFloorsAllowed = "<= G+6";
								} else if (PlotArea.compareTo(PLOTEA_2000) > 0 ) {
									isAccepted = noOfFloors.compareTo(BigDecimal.valueOf(7)) <= 0;
									noOfFloorsAllowed = "<= G+6";

								} 

							}
							
						}
					}

					
					int i = 0;
					int count = 0;
					int[] floorNos = new int[100];
					int[] counter = new int[100];
					for (Floor floor : block.getBuilding().getFloors()) {

						totalFloorArea = BigDecimal.valueOf(0);

						if (floor.getArea() != null && floor.getArea().compareTo(BigDecimal.valueOf(0.0)) > 0) {
							totalFloorArea = floor.getArea();
						} else {
							if (floor.getOccupancies() != null) {
								for (Occupancy occ : floor.getOccupancies()) {
									// checking for floor type occupancy
									if (occ.getTypeHelper() == null || occ.getTypeHelper().getType() == null
											|| occ.getTypeHelper().getSubtype() == null) {
										errors.put("Floor No " + floor.getNumber() + " Occupancy error",
												"Floor No " + floor.getNumber() + " occupancy is not present");
										pl.addErrors(errors);
									}
									if (occ.getFloorArea() != null)
										totalFloorArea = totalFloorArea.add(occ.getFloorArea());
								}
							}

							floor.setArea(totalFloorArea);
						}
						String floorocc = "";

						counter[count] = floor.getNumber();
						count++;

						floorNos[i] = floor.getNumber();
						i++;

					}
					
				
					// Find Repeating Floors
					for (int k = 0; k < i; k++) {
						for (int j = k + 1; j < i; j++) {
							if (floorNos[k] == floorNos[j]) {
								errors.put("Floor No " + floorNos[k] + " Repeating error",
										"Floor No " + floorNos[k] + " is repeating");
								pl.addErrors(errors);
							}
						}
					}

					// Find Missing Floor Number

					int c = 0;
					for (int k = minFloor.intValue(); k <= maxFloor.intValue()-1; k++) {
						if (counter[c] != k) {
							errors.put("Floor No " + k + " Missing ", "Floor No " + k + " Missing");
							pl.addErrors(errors);
						} else
							c++;

					}
					if (errors.isEmpty() && StringUtils.isNotBlank(noOfFloorsAllowed)) {
						String ruleNo = "54-D";
						Map<String, String> details = new HashMap<>();
						details.put(RULE_NO, ruleNo);
						details.put(DESCRIPTION, "No Of Floors");
						details.put(PERMISSIBLE, noOfFloorsAllowed);
						details.put(PROVIDED, String.valueOf(noOfFloors));
						details.put(STATUS,
								isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@Override
	public Plan process(Plan pl) {
		validate(pl);

		return pl;
	}

}
