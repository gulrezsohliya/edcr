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
public class BathRoomWaterClosets_Tripura extends BathRoomWaterClosets {

	
	private static final String RULE_41_IV = "TBD";

	@Override
	public Plan validate(Plan pl) {
		try {
			HashMap<String, String> errors = new HashMap<String, String>();
			

			

			BigDecimal minHeight = BigDecimal.ZERO, totalArea = BigDecimal.ZERO, minWidth = BigDecimal.ZERO;

			for (Block b : pl.getBlocks()) {
				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//				scrutinyDetail.setKey("Common_Bathroom Water Closets");
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, "Floor No");
				scrutinyDetail.addColumnHeading(4, REQUIRED);
				scrutinyDetail.addColumnHeading(5, PROVIDED);
				scrutinyDetail.addColumnHeading(6, STATUS);
				scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + "Bathroom Water Closets");
				ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
				scrutinyDetail1.addColumnHeading(1, RULE_NO);
				scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail1.addColumnHeading(3, "Floor No");
				scrutinyDetail1.addColumnHeading(4, REQUIRED);
				scrutinyDetail1.addColumnHeading(5, PROVIDED);
				scrutinyDetail1.addColumnHeading(6, STATUS);
				scrutinyDetail1.setKey("Block_" + b.getNumber() + "_" + "Bathroom Water Closets Ventilations");
				if (b.getBuilding() != null && b.getBuilding().getFloors() != null
						&& !b.getBuilding().getFloors().isEmpty()) {

					for (Floor f : b.getBuilding().getFloors()) {
						Map<String, String> details = new HashMap<>();
						details.put(RULE_NO, "60(1)");
						details.put(DESCRIPTION, BathroomWaterClosets_DESCRIPTION);
						int colorcode=0;
						if (f.getBathRoomWaterClosets() != null && f.getBathRoomWaterClosets().getHeights() != null
								&& !f.getBathRoomWaterClosets().getHeights().isEmpty()
								&& f.getBathRoomWaterClosets().getRooms() != null
								&& !f.getBathRoomWaterClosets().getRooms().isEmpty()) {

							if (f.getBathRoomWaterClosets().getHeights() != null
									&& !f.getBathRoomWaterClosets().getHeights().isEmpty()) {
								minHeight = f.getBathRoomWaterClosets().getHeights().get(0).getHeight();
								if (f.getBathRoomWaterClosets().getRooms() != null
										&& !f.getBathRoomWaterClosets().getRooms().isEmpty()) {
									minWidth = f.getBathRoomWaterClosets().getRooms().get(0).getWidth();
									for (Measurement m : f.getBathRoomWaterClosets().getRooms()) {
										colorcode=m.getColorCode();
										if(colorcode!=20) {
											pl.addError("BathroomWCColorCodeError"+b.getNumber()+f.getNumber(), "Block No "+b.getNumber()+"Please provide Bathroom With WaterClosets with color code 20. Provided colorcode is "+colorcode);
											return pl;
										}
										totalArea = totalArea.add(m.getArea());
										if (m.getWidth().compareTo(minWidth) < 0) {
											minWidth = m.getWidth();
										}
									}
								}
								for (RoomHeight rh : f.getBathRoomWaterClosets().getHeights()) {
									if (rh.getHeight().compareTo(minHeight) < 0) {
										minHeight = rh.getHeight();
									}
								}
								System.out.println(f.getNumber());
								
								if (totalArea.compareTo(new BigDecimal(2.6)) >= 0) {
									details.put("Floor No", f.getNumber()+"");
									details.put(REQUIRED, "Floor Area >= 2.6");
									details.put(PROVIDED, "" + totalArea.setScale(2, RoundingMode.HALF_UP));
									details.put(STATUS, Result.Accepted.getResultVal());
									scrutinyDetail.getDetail().add(details);
									pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

								} else {
									details.put("Floor No", f.getNumber()+"");
									details.put(REQUIRED, "Floor Area >= 2.6");
									details.put(PROVIDED, "" + totalArea.setScale(2, RoundingMode.HALF_UP));
									details.put(STATUS, Result.Not_Accepted.getResultVal());
									scrutinyDetail.getDetail().add(details);
									pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
								}
								
							}

							
							

							
							BigDecimal ventilationArea = BigDecimal.ZERO;
							Room bathroomWC = f.getBathRoomWaterClosets();
							if (bathroomWC.getLightAndVentilation() != null
									&& bathroomWC.getLightAndVentilation().getMeasurements() != null
									&& !bathroomWC.getLightAndVentilation().getMeasurements().isEmpty()) {
								
								ventilationArea = bathroomWC.getLightAndVentilation().getMeasurements().stream()
										.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
								;

								buildVentilation(pl, b, f.getNumber(), 
										ventilationArea, scrutinyDetail1);
							}else {
								pl.addError("BathRoomWCVentError"+b.getNumber()+f.getNumber(),  "Block No "+b.getNumber()+"Ventilations for Bathroom With Water Closets is not defined in floor no "+f.getNumber());
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
			validate(pl);
		} catch (Exception e) {
			// TODO: handle exception
		}
	
		

		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
	public void buildVentilation(Plan pl, Block b, Integer floorNo, BigDecimal ventsarea,
			ScrutinyDetail scrutinyDetail1) {
			Boolean status = false;

			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, "60 & 70 of TBR");
			details.put(DESCRIPTION, "Bathroom with Water Closets Ventilation Area");
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
