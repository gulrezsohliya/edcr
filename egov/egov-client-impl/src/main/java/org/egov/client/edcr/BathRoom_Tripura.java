package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
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
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.feature.BathRoom;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class BathRoom_Tripura extends BathRoom {

	

	private static final String RULE_41_IV = "TBD";

	@Override
	public Plan validate(Plan pl) {
		try {
			HashMap<String, String> errors = new HashMap<String, String>();
			

			
			
			

			BigDecimal minHeight = BigDecimal.ZERO, totalArea = BigDecimal.ZERO, minWidth = BigDecimal.ZERO,minArea = BigDecimal.ZERO;

			for (Block b : pl.getBlocks()) {
				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, "Floor No");
				scrutinyDetail.addColumnHeading(4, REQUIRED);
				scrutinyDetail.addColumnHeading(5, PROVIDED);
				scrutinyDetail.addColumnHeading(6, STATUS);
				scrutinyDetail.setKey("Block_" + b.getName() + "_" + "Bathroom");
				ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
				scrutinyDetail1.addColumnHeading(1, RULE_NO);
				scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail1.addColumnHeading(3, "Floor No");
				scrutinyDetail1.addColumnHeading(4, REQUIRED);
				scrutinyDetail1.addColumnHeading(5, PROVIDED);
				scrutinyDetail1.addColumnHeading(6, STATUS);
				scrutinyDetail1.setKey("Block_" + b.getNumber() + "_" + "Bathroom Ventilations");
				if (b.getBuilding() != null && b.getBuilding().getFloors() != null
						&& !b.getBuilding().getFloors().isEmpty()) {

					for (Floor f : b.getBuilding().getFloors()) {
						Map<String, String> details = new HashMap<>();
						details.put(RULE_NO, "60(1)");
						details.put(DESCRIPTION, BATHROOM_DESCRIPTION);
						int colorcode=0;
						if (f.getBathRoom() != null && f.getBathRoom().getHeights() != null
								&& !f.getBathRoom().getHeights().isEmpty() && f.getBathRoom().getRooms() != null
								&& !f.getBathRoom().getRooms().isEmpty()) {

							if (f.getBathRoom().getHeights() != null && !f.getBathRoom().getHeights().isEmpty()) {
								minHeight = f.getBathRoom().getHeights().get(0).getHeight();
								if (f.getBathRoom().getRooms() != null && !f.getBathRoom().getRooms().isEmpty()) {
									minWidth = f.getBathRoom().getRooms().get(0).getWidth();
									
									for (Measurement m : f.getBathRoom().getRooms()) {
										colorcode=m.getColorCode();
										if(colorcode!=4) {
											pl.addError("BathroomColorCodeError"+b.getNumber()+f.getBathRoom().getNumber(), "Block No "+b.getNumber()+"Please provide Bathroom with color code 4. Provided colorcode is "+colorcode);
											return pl;
										}
										totalArea = totalArea.add(m.getArea());
										if (m.getWidth().compareTo(minWidth) < 0) {
											minWidth = m.getWidth();
										}
									}
								}
								for (RoomHeight rh : f.getBathRoom().getHeights()) {
									if (rh.getHeight().compareTo(minHeight) < 0) {
										minHeight = rh.getHeight();
									}
								}
								if (minHeight.compareTo(new BigDecimal(2.2)) >= 0
										&& totalArea.compareTo(new BigDecimal(1.8)) >= 0
										&& minWidth.compareTo(new BigDecimal(1.2)) >= 0) {
									details.put("Floor No", f.getNumber()+"");
									details.put(REQUIRED, "Height >= 2.2, Total Area >= 1.8, Width >= 1.2");
									details.put(PROVIDED, "Height >= " + minHeight.setScale(2, RoundingMode.HALF_UP) + ", Total Area >= " + totalArea.setScale(2, RoundingMode.HALF_UP)
											+ ", Width >= " + minWidth.setScale(2, RoundingMode.HALF_UP));
									details.put(STATUS, Result.Accepted.getResultVal());
									scrutinyDetail.getDetail().add(details);
									pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

								} else {
									details.put("Floor No", f.getNumber()+"");
									details.put(REQUIRED, "Height >= 2.2, Total Area >= 1.8, Width >= 1.2");
									details.put(PROVIDED, "Height >= " + minHeight.setScale(2, RoundingMode.HALF_UP) + ", Total Area >= " + totalArea.setScale(2, RoundingMode.HALF_UP)
											+ ", Width >= " + minWidth.setScale(2, RoundingMode.HALF_UP));
									details.put(STATUS, Result.Not_Accepted.getResultVal());
									scrutinyDetail.getDetail().add(details);
									pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
								}
							}

							
							
							BigDecimal ventilationArea = BigDecimal.ZERO;
							Room bathroom = f.getBathRoom();
							if (bathroom.getLightAndVentilation() != null
									&& bathroom.getLightAndVentilation().getMeasurements() != null
									&& !bathroom.getLightAndVentilation().getMeasurements().isEmpty()) {
								
								ventilationArea = bathroom.getLightAndVentilation().getMeasurements().stream()
										.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
								;

								buildVentilation(pl, b, f.getNumber(), 
										ventilationArea, scrutinyDetail1);
							}else {
								pl.addError("BathRoomVentError"+b.getNumber()+bathroom.getNumber(),"Block No "+b.getNumber()+ "Ventilations for Bathroom is not defined in floor no "+f.getNumber());
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
	public Plan process(Plan pl) {
		try {
			HashMap<String, String> errors = new HashMap<String, String>();
			boolean bathroomexist=false;
			boolean waterclosetsexist=false;
			boolean bathroomwcexist=false;
			BigDecimal bldgFtPrnt=BigDecimal.ZERO;
			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;
			 
			//Checking for atleast one water closets or bathroom with waterclosets exist in a building
			for (Block b : pl.getBlocks()) {
				BigDecimal coverage = b.getBuilding().getCoverageArea();
				 
				for(SetBack bfp : b.getSetBacks()) {
					if(bfp.getBuildingFootPrint().getArea().compareTo(bldgFtPrnt)>0) {
						bldgFtPrnt=bfp.getBuildingFootPrint().getArea();
					}
				}
				BigDecimal thirtyPercentBldgFtprnt = bldgFtPrnt.multiply(BigDecimal.valueOf(0.30));
				if(coverage.compareTo(BigDecimal.valueOf(15))>0) {
					if ((mostRestrictiveOccupancy.getType() != null
							&& R.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))) {
							if(R1a.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())){
								for (Block block : pl.getBlocks()) {
									if(block.getBuilding().getTotalExistingBuiltUpArea().compareTo(BigDecimal.ZERO)<=0) {
										for (Floor f : block.getBuilding().getFloors()) {
											bathroomexist=Boolean.FALSE;
											bathroomwcexist=Boolean.FALSE;
											waterclosetsexist=Boolean.FALSE;
//											if (f.getBathRoom() != null && f.getBathRoom().getHeights() != null
//													&& !f.getBathRoom().getHeights().isEmpty() && f.getBathRoom().getRooms() != null
//													&& !f.getBathRoom().getRooms().isEmpty()) {
//												bathroomexist=true;
//											}
											if (f.getBathRoomWaterClosets() != null && f.getBathRoomWaterClosets().getHeights() != null
													&& !f.getBathRoomWaterClosets().getHeights().isEmpty()
													&& f.getBathRoomWaterClosets().getRooms() != null
													&& !f.getBathRoomWaterClosets().getRooms().isEmpty()) {
												bathroomwcexist=true;
											}
											if (f.getWaterClosets() != null 
													&& f.getWaterClosets().getRooms() != null
													&& !f.getWaterClosets().getRooms().isEmpty()) {
												waterclosetsexist=true;
											}
											for(Occupancy occ : f.getOccupancies()) {
												if(occ.getBuiltUpArea().compareTo(thirtyPercentBldgFtprnt)>0) {
													if( !bathroomwcexist && !waterclosetsexist) {
														errors.put("BathroomandWCError"+block.getNumber()+f.getNumber(), "Block No "+block.getNumber()+"Atleast One Water Closet or Bathroom with Water Closet shoud be defined for floor "+f.getNumber());
														pl.addErrors(errors);
													}
												}
											}
										}
									}
									
								}
								
							}else if(R1b.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())){
								for (Block block : pl.getBlocks()) {
									if(block.getBuilding().getTotalExistingBuiltUpArea().compareTo(BigDecimal.ZERO)<=0) {
										for (Floor f : block.getBuilding().getFloors()) {
											if (f.getBathRoomWaterClosets() != null && f.getBathRoomWaterClosets().getHeights() != null
													&& !f.getBathRoomWaterClosets().getHeights().isEmpty()
													&& f.getBathRoomWaterClosets().getRooms() != null
													&& !f.getBathRoomWaterClosets().getRooms().isEmpty()) {
												bathroomwcexist=true;
											}
											if (f.getWaterClosets() != null 
													&& f.getWaterClosets().getRooms() != null
													&& !f.getWaterClosets().getRooms().isEmpty()) {
												waterclosetsexist=true;
											}
											for (Measurement parking : f.getParking().getStilts()) {
												if(parking.getArea().compareTo(BigDecimal.ZERO)>0) {
													if(!bathroomwcexist && !waterclosetsexist) {
														errors.put("BathroomandWCError"+block.getNumber(),"Block No "+block.getNumber()+ "Atleast One Water Closet or Bathroom with Water Closet shoud be defined since stilt parking is used in ground floor");
														pl.addErrors(errors);
													}
												}
											}
										}
									}
									
								}
							}
					}
				}
				
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		validate(pl);

		return pl;
	}
	
	public void buildVentilation(Plan pl, Block b, Integer floorNo, BigDecimal ventsarea,
		ScrutinyDetail scrutinyDetail1) {
		Boolean status = false;

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "60 & 70 of TBR");
		details.put(DESCRIPTION, "Bathroom Ventilation Area");
		details.put("Floor No", floorNo.toString());
		details.put(REQUIRED, ">0.37 sqmt");
		details.put(PROVIDED, ventsarea.toString());
		if (ventsarea.compareTo(BigDecimal.valueOf(0.37)) > 0) {
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
