package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.egov.common.entity.edcr.Floor;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.WaterClosets;
import org.springframework.stereotype.Service;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.MeasurementWithHeight;
import org.egov.common.entity.edcr.Result;

@Service
public class WaterClosets_AR extends WaterClosets{

	private static final Logger LOG = Logger.getLogger(WaterClosets_AR.class);
	
	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		// TODO Auto-generated method stub
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Water Closets");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, REQUIRED);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);

			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, "TBD");
			details.put(DESCRIPTION, "Water Closets");

			BigDecimal minHeight = BigDecimal.ZERO, totalArea = BigDecimal.ZERO, minWidth = BigDecimal.ZERO;

			for (Block b : pl.getBlocks()) {
				if (b.getBuilding() != null && b.getBuilding().getFloors() != null
						&& !b.getBuilding().getFloors().isEmpty()) {

					for (Floor f : b.getBuilding().getFloors()) {

						if (f.getWaterClosets() != null 
								&&  f.getWaterClosets().getRooms() != null
								&& !f.getWaterClosets().getRooms().isEmpty()) {

//							if (f.getWaterClosets().getHeights() != null && !f.getWaterClosets().getHeights().isEmpty()) {
//								minHeight = f.getWaterClosets().getHeights().get(0).getHeight();
//								for (RoomHeight rh : f.getWaterClosets().getRooms()) {
//									if (rh.getHeight().compareTo(minHeight) < 0) {
//										minHeight = rh.getHeight();
//									}
//								}
//							}

							if (f.getWaterClosets().getRooms() != null && !f.getWaterClosets().getRooms().isEmpty()) {
								minWidth = f.getWaterClosets().getRooms().get(0).getWidth();
								for (Measurement m : f.getWaterClosets().getRooms()) {
									totalArea = totalArea.add(m.getArea());
									minHeight=m.getHeight();
//									if (m.getWidth().compareTo(minWidth) < 0) {
//										minWidth = m.getWidth();
//									}
								}
							}

//								details.put(REQUIRED, "Height >= 2.4, Total Area >= 1.2, Width >= 1");
								details.put(PROVIDED, "Height = " + minHeight.setScale(2, BigDecimal.ROUND_DOWN) + ", Total Area = " + totalArea);
								details.put(STATUS, Result.Verify.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

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
}
