package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.feature.WaterClosets;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.springframework.stereotype.Service;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;

@Service
public class WaterClosets_Tripura extends WaterClosets{

	private static final Logger LOG = Logger.getLogger(WaterClosets_Tripura.class);
	private static final String RULE_41_IV = "60(2)";
	
	@Override
	public Plan validate(Plan plan) {
		
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//		scrutinyDetail.setKey("Common_Water Closets");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, "Floor No");
		scrutinyDetail.addColumnHeading(4, REQUIRED);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);

		

		BigDecimal minHeight = BigDecimal.ZERO, totalArea = BigDecimal.ZERO, minWidth = BigDecimal.ZERO;

		for (Block b : pl.getBlocks()) {
			scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + "Water Closets");
			ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
			scrutinyDetail1.addColumnHeading(1, RULE_NO);
			scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail1.addColumnHeading(3, "Floor No");
			scrutinyDetail1.addColumnHeading(4, REQUIRED);
			scrutinyDetail1.addColumnHeading(5, PROVIDED);
			scrutinyDetail1.addColumnHeading(6, STATUS);
			scrutinyDetail1.setKey("Block_" + b.getNumber() + "_" + "Water Closets Ventilations");
			if (b.getBuilding() != null && b.getBuilding().getFloors() != null
					&& !b.getBuilding().getFloors().isEmpty()) {

				for (Floor f : b.getBuilding().getFloors()) {
					Map<String, String> details = new HashMap<>();
					details.put(RULE_NO, RULE_41_IV);
					details.put(DESCRIPTION, WATERCLOSETS_DESCRIPTION);
					int colorcode=0;
					if (f.getWaterClosets() != null 
							&& f.getWaterClosets().getRooms() != null
							&& !f.getWaterClosets().getRooms().isEmpty()) {
						if (f.getWaterClosets().getRooms() != null && !f.getWaterClosets().getRooms().isEmpty()) {
							minWidth = f.getWaterClosets().getRooms().get(0).getWidth();
							for (Measurement m : f.getWaterClosets().getRooms()) {
								colorcode=m.getColorCode();
								if(colorcode!=9) {
									pl.addError("WCColorCodeError", "Please provide Water Closets with color code 9. Provided colorcode is "+colorcode);
									return pl;
								}
								totalArea = totalArea.add(m.getArea());
								if (m.getWidth().compareTo(minWidth) < 0) {
									minWidth = m.getWidth();
								}
							}
							if (totalArea.compareTo(new BigDecimal(1.1)) >= 0
									&& minWidth.compareTo(new BigDecimal(0.9)) >= 0) {
								details.put("Floor No", f.getNumber()+"");
								details.put(REQUIRED, "Total Area >= 1.1, Width >= 0.9");
								details.put(PROVIDED, " Total Area >= " + totalArea
										+ ", Width >= " + minWidth);
								details.put(STATUS, Result.Accepted.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

							} else {
								details.put("Floor No", f.getNumber()+"");
								details.put(REQUIRED, "Total Area >= 1.1, Width >= 0.9");
								details.put(PROVIDED, " Total Area >= " + totalArea
										+ ", Width >= " + minWidth);
								details.put(STATUS, Result.Not_Accepted.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							}
						}
						

						BigDecimal ventilationArea = BigDecimal.ZERO;
						Room WaterClosets = f.getWaterClosets();
						if (WaterClosets.getLightAndVentilation() != null
								&& WaterClosets.getLightAndVentilation().getMeasurements() != null
								&& !WaterClosets.getLightAndVentilation().getMeasurements().isEmpty()) {
							
							ventilationArea = WaterClosets.getLightAndVentilation().getMeasurements().stream()
									.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
							;
							System.out.println("Floor No=" + f.getNumber());
							System.out.println("Ventilation Area =" + ventilationArea);

							buildVentilation(pl, b, f.getNumber(), 
									ventilationArea, scrutinyDetail1);
						}else {
							pl.addError("WCVentError", "Ventilations for Water Closets is not defined in floor no "+f.getNumber());
						}

					}

				}
			}

		}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	
	}
	public void buildVentilation(Plan pl, Block b, Integer floorNo, BigDecimal ventsarea,
			ScrutinyDetail scrutinyDetail1) {
			Boolean status = false;

			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, "60 & 70 of TBR");
			details.put(DESCRIPTION, "Water Closets Ventilation Area");
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
