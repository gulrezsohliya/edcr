package org.egov.client.edcr;
import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Flight;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.StairLanding;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.GeneralStair;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GeneralStair_Tripura extends GeneralStair {

	private static final Logger LOG = Logger.getLogger(GeneralStair_Tripura.class);
	private static final String FLOOR = "Floor";
	private static final String RULE42_5_II = "42-5-ii";
	private static final String EXPECTED_NO_OF_RISER = "12";
	private static final String NO_OF_RISER_DESCRIPTION = "Maximum no of risers required per flight for general stair %s flight %s";
	private static final String WIDTH_DESCRIPTION = "Minimum width for general stair %s flight %s";
	private static final String TREAD_DESCRIPTION = "Minimum tread for general stair %s flight %s";
	private static final String NO_OF_RISERS = "Number of risers ";
	private static final String FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION = "Flight polyline is not defined in layer ";
	private static final String FLIGHT_LENGTH_DEFINED_DESCRIPTION = "Flight polyline length is not defined in layer ";
	private static final String FLIGHT_WIDTH_DEFINED_DESCRIPTION = "Flight polyline width is not defined in layer ";
	private static final String WIDTH_LANDING_DESCRIPTION = "Minimum width for general stair %s mid landing %s";
	private static final String FLIGHT_NOT_DEFINED_DESCRIPTION = "General stair flight is not defined in block %s floor %s";

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		try {
			HashMap<String, String> errors = new HashMap<>();
			String ruleNo="";
			blk: for (Block block : plan.getBlocks()) {
				int generalStairCount = 0;

				if (block.getBuilding() != null) {
					ScrutinyDetail scrutinyDetail2 = new ScrutinyDetail();
					scrutinyDetail2.addColumnHeading(1, RULE_NO);
					scrutinyDetail2.addColumnHeading(2, FLOOR);
					scrutinyDetail2.addColumnHeading(3, DESCRIPTION);
					scrutinyDetail2.addColumnHeading(4, PERMISSIBLE);
					scrutinyDetail2.addColumnHeading(5, PROVIDED);
					scrutinyDetail2.addColumnHeading(6, STATUS);
					scrutinyDetail2.setKey("Block_" + block.getNumber() + "_" + "General Stair - Width");

					ScrutinyDetail scrutinyDetail3 = new ScrutinyDetail();
					scrutinyDetail3.addColumnHeading(1, RULE_NO);
					scrutinyDetail3.addColumnHeading(2, FLOOR);
					scrutinyDetail3.addColumnHeading(3, DESCRIPTION);
					scrutinyDetail3.addColumnHeading(4, PERMISSIBLE);
					scrutinyDetail3.addColumnHeading(5, PROVIDED);
					scrutinyDetail3.addColumnHeading(6, STATUS);
					scrutinyDetail3.setKey("Block_" + block.getNumber() + "_" + "No of Stairs");
					
					ScrutinyDetail scrutinyDetailLanding = new ScrutinyDetail();
	                scrutinyDetailLanding.addColumnHeading(1, RULE_NO);
	                scrutinyDetailLanding.addColumnHeading(2, FLOOR);
	                scrutinyDetailLanding.addColumnHeading(3, DESCRIPTION);
	                scrutinyDetailLanding.addColumnHeading(4, PERMISSIBLE);
	                scrutinyDetailLanding.addColumnHeading(5, PROVIDED);
	                scrutinyDetailLanding.addColumnHeading(6, STATUS);
	                scrutinyDetailLanding.setKey("Block_" + block.getNumber() + "_" + "General Stair - Mid landing");


					BigDecimal buildingHeight = block.getBuilding().getHeight();


					OccupancyTypeHelper mostRestrictiveOccupancyType = block.getBuilding() != null
							? block.getBuilding().getMostRestrictiveFarHelper()
							: null;

					/*
					 * String occupancyType = mostRestrictiveOccupancy != null ?
					 * mostRestrictiveOccupancy.getOccupancyType() : null;
					 */

					List<Floor> floors = block.getBuilding().getFloors();
					List<String> stairAbsent = new ArrayList<>();
					// BigDecimal floorSize = block.getBuilding().getFloorsAboveGround();
					for (Floor floor : floors) {
						if (!floor.getTerrace()) {

							boolean isTypicalRepititiveFloor = false;
							Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor,
									isTypicalRepititiveFloor);

							List<org.egov.common.entity.edcr.GeneralStair> generalStairs = floor.getGeneralStairs();

							int size = generalStairs.size();
							generalStairCount = generalStairCount + size;

							if (!generalStairs.isEmpty()) {
								validateNoOfStairs(plan, scrutinyDetail3,floor,
			                            mostRestrictiveOccupancyType,buildingHeight,size);
								for (org.egov.common.entity.edcr.GeneralStair generalStair : generalStairs) {
									{
										
										validateFlight(plan, errors, block, scrutinyDetail2, scrutinyDetail3,
												mostRestrictiveOccupancyType, floor, typicalFloorValues,
												generalStair,buildingHeight);
										
										List<StairLanding> landings = generalStair.getLandings();
	                                    if (!landings.isEmpty()) {
	                                        validateLanding(plan, block, scrutinyDetailLanding, mostRestrictiveOccupancyType,
	                                                floor,
	                                                typicalFloorValues, generalStair, landings, errors);
	                                    } else {
	                                        errors.put(
	                                                "General Stair landing not defined in block " + block.getNumber() + " floor "
	                                                        + floor.getNumber()
	                                                        + " stair " + generalStair.getNumber(),
	                                                "General Stair landing not defined in block " + block.getNumber() + " floor "
	                                                        + floor.getNumber()
	                                                        + " stair " + generalStair.getNumber());
	                                        plan.addErrors(errors);
	                                    }

									}
								}
							} else {
								stairAbsent.add("Block " + block.getNumber() + " floor " + floor.getNumber());
							}

						}
					}

					

					if (block.getBuilding().getFloors().size() > 1 && generalStairCount == 0) {
						errors.put("General Stair not defined in blk " + block.getNumber(),
								"General Stair not defined in block " + block.getNumber()
										+ ", it is mandatory for building with floors more than one.");
						plan.addErrors(errors);
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		

		return plan;
	}

    private void validateFlight(Plan plan, HashMap<String, String> errors, Block block, ScrutinyDetail scrutinyDetail2,
            ScrutinyDetail scrutinyDetail3, OccupancyTypeHelper mostRestrictiveOccupancyType,
            Floor floor, Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,BigDecimal buildingHeight) {
        if (!generalStair.getFlights().isEmpty()) {
            for (Flight flight : generalStair.getFlights()) {
                List<Measurement> flightPolyLines = flight.getFlights();
                List<BigDecimal> flightWidths = flight.getWidthOfFlights();
                Boolean flightPolyLineClosed = flight.getFlightClosed();
                System.out.println(flightPolyLineClosed);

                BigDecimal minFlightWidth = BigDecimal.ZERO;
                String flightLayerName = String.format(DxfFileConstants.LAYER_STAIR_FLIGHT,
                        block.getNumber(), floor.getNumber(), generalStair.getNumber(),
                        flight.getNumber());

                if (flightPolyLines != null && flightPolyLines.size() > 0) {
                    if (flightPolyLineClosed) {
                        if (flightWidths != null && flightWidths.size() > 0) {
                            minFlightWidth = validateWidth(plan, scrutinyDetail2, floor, block,
                                    typicalFloorValues, generalStair, flight, flightWidths,
                                    minFlightWidth,
                                    mostRestrictiveOccupancyType);
                            

                        } else {
                            errors.put("Flight PolyLine width" + flightLayerName,
                                    FLIGHT_WIDTH_DEFINED_DESCRIPTION + flightLayerName);
                            plan.addErrors(errors);
                        }

                      

                    }
                } else {
                    errors.put("Flight PolyLine " + flightLayerName,
                            FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION + flightLayerName);
                    plan.addErrors(errors);
                }

            }
        } else {
            String error = String.format(FLIGHT_NOT_DEFINED_DESCRIPTION, block.getNumber(), floor.getNumber());
            errors.put(error, error);
            plan.addErrors(errors);
        }
    }

    private void validateNoOfStairs(Plan plan, ScrutinyDetail scrutinyDetail3,Floor floor2,
			OccupancyTypeHelper mostRestrictiveOccupancyType,BigDecimal buildingheight,int noofstairs) {
			Boolean valid =false;
			int reqnoofStairs=0;
			
			if(floor2.getArea().compareTo(BigDecimal.valueOf(400))<=0) {
				if( buildingheight.compareTo(BigDecimal.valueOf(14.5))<0) {
					reqnoofStairs=0;
				}else if(buildingheight.compareTo(BigDecimal.valueOf(14.5))>0) {
					reqnoofStairs=2;
				}
			}else if(floor2.getArea().compareTo(BigDecimal.valueOf(400))>0) {
				if( buildingheight.compareTo(BigDecimal.valueOf(14.5))<0) {
					reqnoofStairs=2;
				}else if(buildingheight.compareTo(BigDecimal.valueOf(14.5))>0) {
					reqnoofStairs=2;
				}
			}
					
				
			
			if(noofstairs>=reqnoofStairs) {
				valid=true;
			}
			 if (valid) {
	                setReportOutputDetailsFloorWise(plan, "75", reqnoofStairs,
	                        noofstairs, Result.Accepted.getResultVal(), scrutinyDetail3,floor2.getNumber());
	                setReportOutputDetailsFloorStairWise(plan, "75",floor2.getNumber()+"","No of stairs", reqnoofStairs+"",noofstairs+"",Result.Accepted.getResultVal(), scrutinyDetail3);
	                
	            } else {
	            	setReportOutputDetailsFloorStairWise(plan, "75",floor2.getNumber()+"","No of stairs", reqnoofStairs+"",noofstairs+"",Result.Accepted.getResultVal(), scrutinyDetail3);
	            }
		
	}
    
    private void validateLanding(Plan plan, Block block, ScrutinyDetail scrutinyDetailLanding,
            OccupancyTypeHelper mostRestrictiveOccupancyType, Floor floor, Map<String, Object> typicalFloorValues,
            org.egov.common.entity.edcr.GeneralStair generalStair, List<StairLanding> landings, HashMap<String, String> errors) {
        for (StairLanding landing : landings) {
            List<BigDecimal> widths = landing.getWidths();
            if(!widths.isEmpty()) {
            BigDecimal landingWidth = widths.stream().reduce(BigDecimal::min).get();
            BigDecimal minWidth = BigDecimal.ZERO;
            boolean valid = false;
            String stairno = generalStair.getNumber();
            if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
                minWidth = Util.roundOffTwoDecimal(landingWidth);
                BigDecimal minimumWidth = getRequiredWidth(block, mostRestrictiveOccupancyType,stairno);

                if (minWidth.compareTo(minimumWidth) >= 0) {
                    valid = true;
                }
                String value = typicalFloorValues.get("typicalFloors") != null
                        ? (String) typicalFloorValues.get("typicalFloors")
                        : " floor " + floor.getNumber();

                if (valid) {
                    setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
                            String.format(WIDTH_LANDING_DESCRIPTION, generalStair.getNumber(),
                                    landing.getNumber()),
                            minimumWidth.toString(),
                            String.valueOf(minWidth), Result.Accepted.getResultVal(),
                            scrutinyDetailLanding);
                } else {
                    setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
                            String.format(WIDTH_LANDING_DESCRIPTION, generalStair.getNumber(),
                                    landing.getNumber()),
                            minimumWidth.toString(),
                            String.valueOf(minWidth), Result.Not_Accepted.getResultVal(),
                            scrutinyDetailLanding);
                }
            }
            }else {
                errors.put(
                        "General Stair landing width not defined in block " + block.getNumber() + " floor "
                                + floor.getNumber()
                                + " stair " + generalStair.getNumber(),
                        "General Stair landing width not defined in block " + block.getNumber() + " floor "
                                + floor.getNumber()
                                + " stair " + generalStair.getNumber());
                plan.addErrors(errors);
            }
        }
    }

	

	private BigDecimal validateWidth(Plan plan, ScrutinyDetail scrutinyDetail2, Floor floor, Block block,
            Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight,
            List<BigDecimal> flightWidths, BigDecimal minFlightWidth,
            OccupancyTypeHelper mostRestrictiveOccupancyType) {
        BigDecimal flightPolyLine = flightWidths.stream().reduce(BigDecimal::min).get();

        boolean valid = false;
        String stairno = generalStair.getNumber();
        if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
            minFlightWidth = Util.roundOffTwoDecimal(flightPolyLine);
            BigDecimal minimumWidth = getRequiredWidth(block, mostRestrictiveOccupancyType,stairno);

            if (minFlightWidth.compareTo(minimumWidth) >= 0) {
                valid = true;
            }
            String value = typicalFloorValues.get("typicalFloors") != null
                    ? (String) typicalFloorValues.get("typicalFloors")
                    : " floor " + floor.getNumber();

            if (valid) {
                setReportOutputDetailsFloorStairWise(plan, "76", value,
                        String.format(WIDTH_DESCRIPTION, generalStair.getNumber(), flight.getNumber()), minimumWidth.toString(),
                        String.valueOf(minFlightWidth), Result.Accepted.getResultVal(), scrutinyDetail2);
            } else {
                setReportOutputDetailsFloorStairWise(plan, "76", value,
                        String.format(WIDTH_DESCRIPTION, generalStair.getNumber(), flight.getNumber()), minimumWidth.toString(),
                        String.valueOf(minFlightWidth), Result.Not_Accepted.getResultVal(), scrutinyDetail2);
            }
        }
        return minFlightWidth;
    }

    private BigDecimal getRequiredWidth(Block block, OccupancyTypeHelper mostRestrictiveOccupancyType,String stairno) {
    	Boolean mixedOccupancy = Boolean.FALSE;
		int occCounter = 0;
		for (Occupancy occup : block.getBuilding().getOccupancies()) {
			occCounter = occCounter + 1;
		}
		if (occCounter > 1) {
			mixedOccupancy = Boolean.TRUE;
		}
		if(mixedOccupancy && stairno.equalsIgnoreCase("1")) {
			if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getConvertedType() != null
	                && R.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	        	if(R1a.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())) {
	        		return BigDecimal.valueOf(1);
	        	}else if(R1b.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode()) ) {
	        		return BigDecimal.valueOf(1.25);
	        	}
	            
	        }else if(A.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	    	}else if(E.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	        		return BigDecimal.valueOf(1.5);
	    	}else if(E.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	    		return BigDecimal.valueOf(2);
	    	}else if(I.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	    		return BigDecimal.valueOf(2);
	    	}
	        else {
	            return BigDecimal.valueOf(1.5);
	        }
		}else if(mixedOccupancy && !stairno.equalsIgnoreCase("1")) {
			if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getConvertedType() != null
	                && R.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	        	if(R1a.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedSubtype().getCode())) {
	        		return BigDecimal.valueOf(1);
	        	}else if(R1b.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedSubtype().getCode()) ) {
	        		return BigDecimal.valueOf(1.25);
	        	}
	            
	        }else if(A.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	    	}else if(E.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	        		return BigDecimal.valueOf(1.5);
	    	}else if(E.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	    		return BigDecimal.valueOf(2);
	    	}else if(I.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	    		return BigDecimal.valueOf(2);
	    	}
	        else {
	            return BigDecimal.valueOf(1.5);
	        }
		}else {
			if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getConvertedType() != null
	                && R.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	        	if(R1a.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())) {
	        		return BigDecimal.valueOf(1);
	        	}else if(R1b.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode()) ) {
	        		return BigDecimal.valueOf(1.25);
	        	}
	            
	        }else if(A.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	    	}else if(E.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	        		return BigDecimal.valueOf(1.5);
	    	}else if(E.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	    		return BigDecimal.valueOf(2);
	    	}else if(I.equalsIgnoreCase(mostRestrictiveOccupancyType.getConvertedType().getCode())) {
	    		return BigDecimal.valueOf(2);
	    	}
	        else {
	            return BigDecimal.valueOf(1.5);
	        }
		}
        
		return null;
    }

   
   
    private void setReportOutputDetailsFloorStairWise(Plan pl, String ruleNo, String floor, String description,
            String expected, String actual, String status, ScrutinyDetail scrutinyDetail) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(FLOOR, floor);
        details.put(DESCRIPTION, description);
        details.put(PERMISSIBLE, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }
    
    private void setReportOutputDetailsFloorWise(Plan plan, String rule425Ii, int reqnoofStairs, int noofstairs,
			String resultVal, ScrutinyDetail scrutinyDetail3, Integer number) {
    	 Map<String, String> details = new HashMap<>();
         details.put(RULE_NO, rule425Ii);
         details.put(FLOOR, number+"");
         details.put(DESCRIPTION, "No of Stairs");
         details.put(PERMISSIBLE, reqnoofStairs+"");
         details.put(PROVIDED, noofstairs+"");
         details.put(STATUS, resultVal);
         scrutinyDetail.getDetail().add(details);
         plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    	
	}
    
   
}
