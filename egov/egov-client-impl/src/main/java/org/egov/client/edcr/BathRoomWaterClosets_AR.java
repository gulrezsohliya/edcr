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
public class BathRoomWaterClosets_AR extends BathRoomWaterClosets {

	
	@Override
	public Plan validate(Plan pl) {
		
		return pl;
	}

	@Override
	public Plan process(Plan pl) {

	
		validate(pl);
//		try {
//			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//			scrutinyDetail.setKey("Common_Bathroom Water Closets");
//			scrutinyDetail.addColumnHeading(1, RULE_NO);
//			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
////			scrutinyDetail.addColumnHeading(3, REQUIRED);
//			scrutinyDetail.addColumnHeading(4, PROVIDED);
//			scrutinyDetail.addColumnHeading(5, STATUS);
//
//			Map<String, String> details = new HashMap<>();
//			details.put(RULE_NO, "TBD");
//			details.put(DESCRIPTION, "Bathroom Water Closets");
//
//			BigDecimal minHeight = BigDecimal.ZERO, totalArea = BigDecimal.ZERO, minWidth = BigDecimal.ZERO;
//
//			for (Block b : pl.getBlocks()) {
//				if (b.getBuilding() != null && b.getBuilding().getFloors() != null
//						&& !b.getBuilding().getFloors().isEmpty()) {
//
//					for (Floor f : b.getBuilding().getFloors()) {
//
//						if (f.getBathRoomWaterClosets() != null && f.getBathRoomWaterClosets().getHeights() != null
//								&& !f.getBathRoomWaterClosets().getHeights().isEmpty()
//								&& f.getBathRoomWaterClosets().getRooms() != null
//								&& !f.getBathRoomWaterClosets().getRooms().isEmpty()) {
//
//							if (f.getBathRoomWaterClosets().getHeights() != null
//									&& !f.getBathRoomWaterClosets().getHeights().isEmpty()) {
//								minHeight = f.getBathRoomWaterClosets().getHeights().get(0).getHeight();
//								for (RoomHeight rh : f.getBathRoomWaterClosets().getHeights()) {
//									if (rh.getHeight().compareTo(minHeight) < 0) {
//										minHeight = rh.getHeight();
//									}
//								}
//							}
//
//							if (f.getBathRoomWaterClosets().getRooms() != null
//									&& !f.getBathRoomWaterClosets().getRooms().isEmpty()) {
////								minWidth = f.getBathRoomWaterClosets().getRooms().get(0).getWidth();
//								for (Measurement m : f.getBathRoomWaterClosets().getRooms()) {
//									totalArea = totalArea.add(m.getArea());
////									if (m.getWidth().compareTo(minWidth) < 0) {
////										minWidth = m.getWidth();
////									}
//								}
//							}
//
////								details.put(REQUIRED, "Height >= 2.4, Total Area >= 2.8");
//								details.put(PROVIDED, "Height >= " + minHeight + ", Total Area >= " + totalArea);
//								details.put(STATUS, Result.Verify.getResultVal());
//								scrutinyDetail.getDetail().add(details);
//								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//
//						}
//
//					}
//				}
//
//			}
//		}catch (Exception e) {
//			// TODO: handle exception
//		}

		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

	
}
