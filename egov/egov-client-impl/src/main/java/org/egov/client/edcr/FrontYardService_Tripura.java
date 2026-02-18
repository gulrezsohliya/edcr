package org.egov.client.edcr;

// arunachal constants add
import static org.egov.client.constants.DxfFileConstants_AR.*;
import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.I;
import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.NotifiedRoad;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Plot;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.feature.FrontYardService;
import org.springframework.stereotype.Service;

@Service
public class FrontYardService_Tripura extends FrontYardService {
	private static final String RULE_54_D = "54-D";

	private static final String MINIMUMLABEL = "Minimum distance ";

	public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);


	public static final String BSMT_FRONT_YARD_DESC = "Basement Front Yard";


	private static final Logger LOG = Logger.getLogger(FrontYardService_Tripura.class);

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
	}

	public void processFrontYard(Plan pl) {
		try {
			
			
			Plot plot = null;
			if (pl.getPlot() != null)
				plot = pl.getPlot();
			else
				return;
			HashMap<String, String> errors = new HashMap<>();

			Boolean OCTYP_FOUND = false;
			Boolean OCTYP_NOTFOUND = false;
			validateFrontYard(pl);
			int flag=0;
			if (plot != null && !pl.getBlocks().isEmpty()) {
				for (Block block : pl.getBlocks()) { // for each block
					ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
					scrutinyDetail.addColumnHeading(1, RULE_NO);
					scrutinyDetail.addColumnHeading(2, LEVEL);
					scrutinyDetail.addColumnHeading(3, OCCUPANCY);
					scrutinyDetail.addColumnHeading(4, FIELDVERIFIED);
					scrutinyDetail.addColumnHeading(5, PERMISSIBLE.concat(" in meters"));
					scrutinyDetail.addColumnHeading(6, PROVIDED.concat(" in meters"));
					scrutinyDetail.addColumnHeading(7, STATUS);
					scrutinyDetail.setHeading(FRONT_YARD_DESC);
					scrutinyDetail.setKey("Block_" + block.getName() + "_" + FRONT_YARD_DESC);

					BigDecimal buildingHeight = block.getBuilding().getBuildingHeight();
					BigDecimal builtupArea = block.getBuilding().getTotalBuitUpArea();
					
					
					
					
					for (SetBack setback : block.getSetBacks()) {
						
						FrontYardResult frontYardResult = new FrontYardResult();
						Boolean mixedOccupancy = Boolean.FALSE;
						int occCounter = 0;
						Boolean commercialExist=Boolean.FALSE;
						BigDecimal commercialBUA=BigDecimal.ZERO;
						BigDecimal min;
						BigDecimal mean;
						
						for (Occupancy occup : block.getBuilding().getOccupancies()) {
							if(occup.getTypeHelper().getType().getCode().equalsIgnoreCase(B)) {
								commercialExist=Boolean.TRUE;
								commercialBUA=occup.getFloorArea();
							}
							occCounter = occCounter + 1;
						}
						if (occCounter > 1) {
							mixedOccupancy = Boolean.TRUE;
						}
						
						OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
								? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
								: null;
						System.out.println(mostRestrictiveOccupancy.toString());
						
						
						if (setback.getFrontYard() != null) {
							min = setback.getFrontYard().getMinimumDistance();
							mean = setback.getFrontYard().getMean();
							//here floorwise check occupancy and set the highest occ and then validate according to that occ
							int no=0;
							List<Pair<Integer, BigDecimal>> roadWidth = new ArrayList<>();
							if(pl.getNotifiedRoads()!=null) {
								for(NotifiedRoad n : pl.getNotifiedRoads()) {
									if(n.getWidth()!=null) {
										no=no+1;
										roadWidth.add(Pair.of(no, n.getWidth()));
									}
								}
							}
							String abuttingRoad="";
							if(pl.getPlanInfoProperties().containsKey("ABUTTING_ROAD")) {
								abuttingRoad=pl.getPlanInfoProperties().get("ABUTTING_ROAD");
							}
							Integer abutting=0;
							if (no > 1) {
								abutting = Integer.valueOf(abuttingRoad);
							}
							
							if (abutting >= 3 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)
									|| abutting >= 3 &&  mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(B) ) {
								if (no < 3) {
									pl.addError("SideRoadWidthNoError", "There should be atleast three abutting notified roads. Only " + no
											+ " notified roads are present in the plan");
								}
								for (Map.Entry<Integer, BigDecimal> entry : roadWidth) {
									if (entry.getValue().compareTo(BigDecimal.valueOf(5)) > 0) {
											flag=flag+1;
									} else {
										pl.addError("SideRoadWidthLess_5" + entry.getKey(), "Road width (" + entry.getValue()
												+ ") of notified road with color code" + entry.getKey() + " is less than 5m");
									}
								}

							}

							OCTYP_FOUND = false;
							OCTYP_NOTFOUND = false;

//							for (final Occupancy occupancy : block.getBuilding().getTotalArea()) {
							System.out.println(commercialBUA);

								if (mostRestrictiveOccupancy.getType() == null) {

									OCTYP_NOTFOUND = true; // occ typ not found

								} else {
									
									
									OCTYP_FOUND = true; // search for occ typ
									if(flag==3) {
										if ((mostRestrictiveOccupancy.getConvertedType() != null
												&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode()))
												|| (IN.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode())))) { // Coverage 100 percent
											frontYardResult = check100Coverage(pl, buildingHeight, block.getName(),
													setback.getLevel(), plot, FRONT_YARD_DESC, min, mean, mostRestrictiveOccupancy,
													frontYardResult);

										}
									}
									
									else if(mixedOccupancy&&commercialExist) {
										if ((mostRestrictiveOccupancy.getType() != null
												&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())))) { // Business
											frontYardResult = checkFrontYardForBusiness(pl, buildingHeight, commercialBUA,
													block.getName(), setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
													mostRestrictiveOccupancy, frontYardResult);
										}
									}
									else{
										if ((mostRestrictiveOccupancy.getType() != null
												&& R.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))) { // Residential

											frontYardResult = checkFrontYardForResidential(pl, buildingHeight, block.getName(),
													setback.getLevel(), plot, FRONT_YARD_DESC, min, mean, mostRestrictiveOccupancy,
													frontYardResult);

										} 
										if ((mostRestrictiveOccupancy.getType() != null
												&& (E.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())
														|| I.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode())
														|| A.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode())
														|| IN.equalsIgnoreCase(mostRestrictiveOccupancy.getConvertedType().getCode())))) { // Non
																																	// Residential

											frontYardResult = checkFrontYardForEduInAssem(pl, buildingHeight, block.getName(),
													setback.getLevel(), plot, FRONT_YARD_DESC, min, mean, mostRestrictiveOccupancy,
													frontYardResult);

										} 
										if ((mostRestrictiveOccupancy.getType() != null
												&& (B.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())))) { // Business
											frontYardResult = checkFrontYardForBusiness(pl, buildingHeight, builtupArea,
													block.getName(), setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
													mostRestrictiveOccupancy, frontYardResult);

										}
									}
									
								}
//							}
							

							if (OCTYP_NOTFOUND == true && OCTYP_FOUND == false) {
								errors.put("Block_" + block.getName() + "_" + FRONT_YARD_DESC, FRONT_YARD_DESC
										+ " for Block " + block.getName() + " : Occupancy Type not Found!!");
								pl.addErrors(errors);
							}

							if (errors.isEmpty()) {
								Map<String, String> details = new HashMap<>();
								details.put(RULE_NO, frontYardResult.subRule);
								details.put(LEVEL, frontYardResult.level != null ? frontYardResult.level.toString() : "");
								details.put(OCCUPANCY, frontYardResult.occupancy);
								details.put(FIELDVERIFIED, MINIMUMLABEL);
								details.put(PERMISSIBLE.concat(" in meters"),
										frontYardResult.expectedminimumDistance.toString());
								details.put(PROVIDED.concat(" in meters"), frontYardResult.actualMinDistance.toString());

								if (frontYardResult.status) {
									details.put(STATUS, Result.Accepted.getResultVal());
								} else {
									details.put(STATUS, Result.Not_Accepted.getResultVal());
								}
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							}

						} else {
							int no=0;
							List<Pair<Integer, BigDecimal>> roadWidth = new ArrayList<>();
							if(pl.getNotifiedRoads()!=null) {
								for(NotifiedRoad n : pl.getNotifiedRoads()) {
									if(n.getWidth()!=null && n.getWidth().compareTo(BigDecimal.valueOf(0))>0) {
										no=no+1;
										roadWidth.add(Pair.of(no, n.getWidth()));
									}
								}
							}
							String abuttingRoad="";
							if(pl.getPlanInfoProperties().containsKey("ABUTTING_ROAD")) {
								abuttingRoad=pl.getPlanInfoProperties().get("ABUTTING_ROAD");
							}
							Integer abutting=0;
							if (no > 1) {
								abutting = Integer.valueOf(abuttingRoad);
							}
							
							if (abutting >= 3 && mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(IN)
									&& mostRestrictiveOccupancy.getConvertedType().getCode().equalsIgnoreCase(B) ) {
								if (no < 3) {
									pl.addError("SideRoadWidthNoError", "There should be atleast three abutting notified roads. Only " + no
											+ " notified roads are present in the plan");
								}
								for (Map.Entry<Integer, BigDecimal> entry : roadWidth) {
									if (entry.getValue().compareTo(BigDecimal.valueOf(5)) > 0) {
											flag=flag+1;
									} else {
										pl.addError("SideRoadWidthLess_5" + entry.getKey(), "Road width (" + entry.getValue()
												+ ") of notified road with color code" + entry.getKey() + " is less than 5m");
									}
								}
								if(flag==3) {
									return;
								}else {
									errors.put("FrontSetBack"+block.getNumber()+setback.getLevel(), "Block "+block.getNumber()+"Front Setback for level "+setback.getLevel()+" Is Not Defined");
									pl.addErrors(errors);
								}

							}
							
						}
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	

	private void validateFrontYard(Plan pl) {

		for (Block block : pl.getBlocks()) {
			if (!block.getCompletelyExisting()) {
				Boolean frontYardDefined = false;
				for (SetBack setback : block.getSetBacks()) {
					if (setback.getFrontYard() != null) {
						frontYardDefined = true;
					}
				}
				if (!frontYardDefined) {
					HashMap<String, String> errors = new HashMap<>();
					errors.put(FRONT_YARD_DESC,
							prepareMessage(OBJECTNOTDEFINED, FRONT_YARD_DESC + " for Block " + block.getName()));
					pl.addErrors(errors);
				}
			}

		}

	}
	
	
	
	private FrontYardResult check100Coverage(Plan pl, BigDecimal buildingHeight, String blockName,
			Integer level, Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_54_D;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
			minVal = BigDecimal.valueOf(0);


		valid = validateMinimumValue(min, minVal);

		return compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule,
				rule, minVal, meanVal, level);
	}

	private FrontYardResult checkFrontYardForResidential(Plan pl, BigDecimal buildingHeight, String blockName,
			Integer level, Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_54_D;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal plotArea = pl.getPlot().getArea();
		if (plotArea.compareTo(BigDecimal.valueOf(165)) <= 0) {
			if (buildingHeight.compareTo(BigDecimal.valueOf(8)) <= 0) {
				minVal = BigDecimal.valueOf(1.2);
			} else if (buildingHeight.compareTo(BigDecimal.valueOf(8)) > 0
					&& buildingHeight.compareTo(BigDecimal.valueOf(14.5)) <= 0) {
				minVal = BigDecimal.valueOf(1.8);
			}

		} else if (buildingHeight.compareTo(BigDecimal.valueOf(14.5)) <= 0) {
			minVal = BigDecimal.valueOf(1.8);
		} else if (buildingHeight.compareTo(BigDecimal.valueOf(14.5)) > 0
				&& buildingHeight.compareTo(BigDecimal.valueOf(25)) <= 0) {
			minVal = BigDecimal.valueOf(3);
		} else if (buildingHeight.compareTo(BigDecimal.valueOf(25)) > 0) {
			minVal = BigDecimal.valueOf(5);
		}

		else {
			pl.addError("Front Setback Residential",
					"Cannot Determined the required conditions for Front Setback");
		}

		valid = validateMinimumValue(min, minVal);

		return compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule,
				rule, minVal, meanVal, level);
	}

	private FrontYardResult checkFrontYardForEduInAssem(Plan pl, BigDecimal buildingHeight, String blockName,
			Integer level, Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_54_D;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;

		if (buildingHeight.compareTo(BigDecimal.valueOf(14.5)) <= 0) {
			minVal = BigDecimal.valueOf(3);
		} else if (buildingHeight.compareTo(BigDecimal.valueOf(14.5)) > 0
				&& buildingHeight.compareTo(BigDecimal.valueOf(25)) <= 0) {
			minVal = BigDecimal.valueOf(3);
		} else if (buildingHeight.compareTo(BigDecimal.valueOf(25)) > 0) {
			minVal = BigDecimal.valueOf(5);
		} else {
			pl.addError("Front Setback Residential",
					"Cannot Determined the required conditions for Front Setback");
		}
		valid = validateMinimumValue(min, minVal);

		return compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule,
				rule, minVal, meanVal, level);
	}

	private FrontYardResult checkFrontYardForBusiness(Plan pl, BigDecimal buildingHeight, BigDecimal builtupArea,
			String blockName, Integer level, Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_54_D;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal plotArea = pl.getPlot().getArea();
		System.out.println(buildingHeight);

		if (buildingHeight.compareTo(BigDecimal.valueOf(14.5)) <= 0) {
			if (builtupArea.compareTo(BigDecimal.valueOf(100)) <= 0) {
				minVal = BigDecimal.valueOf(1.8);
			} else if (builtupArea.compareTo(BigDecimal.valueOf(100)) > 0) {
				minVal = BigDecimal.valueOf(3);
			}

		} else if (buildingHeight.compareTo(BigDecimal.valueOf(14.5)) > 0
				&& buildingHeight.compareTo(BigDecimal.valueOf(25)) <= 0) {
			minVal = BigDecimal.valueOf(3);
		} else if (buildingHeight.compareTo(BigDecimal.valueOf(25)) > 0) {
			minVal = BigDecimal.valueOf(5);
		} else {
			pl.addError("Front Setback Residential",
					"Cannot Determined the required conditions for Front Setback");
		}

		valid = validateMinimumValue(min, minVal);

		return compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule,
				rule, minVal, meanVal, level);
	}

	private FrontYardResult compareFrontYardResult(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		if (mostRestrictiveOccupancy.getSubtype() != null)
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
		else
			occupancyName = mostRestrictiveOccupancy.getType().getName();
		if (minVal.compareTo(frontYardResult.expectedminimumDistance) >= 0) {
			if (minVal.compareTo(frontYardResult.expectedminimumDistance) == 0) {
				frontYardResult.rule = frontYardResult.rule != null ? frontYardResult.rule + "," + rule : rule;
				frontYardResult.occupancy = frontYardResult.occupancy != null
						? frontYardResult.occupancy + "," + occupancyName
						: occupancyName;
			} else {
				frontYardResult.rule = rule;
				frontYardResult.occupancy = occupancyName;
			}

			frontYardResult.subRule = subRule;
			frontYardResult.blockName = blockName;
			frontYardResult.level = level;
			frontYardResult.expectedminimumDistance = minVal;
			frontYardResult.expectedmeanDistance = meanVal;
			frontYardResult.actualMinDistance = min;
			frontYardResult.actualMeanDistance = mean;
			frontYardResult.status = valid;

		}
		return frontYardResult;
	}

	private Boolean validateMinimumAndMeanValue(BigDecimal min, BigDecimal mean, BigDecimal minval,
			BigDecimal meanval) {
		Boolean valid = false;
		if (min.compareTo(minval) >= 0 && mean.compareTo(meanval) >= 0) {
			valid = true;
		}
		return valid;
	}

	private Boolean validateMinimumValue(BigDecimal min, BigDecimal minval) {
		Boolean valid = false;
		if (min.compareTo(minval) >= 0) {
			valid = true;
		}
		return valid;
	}
}
