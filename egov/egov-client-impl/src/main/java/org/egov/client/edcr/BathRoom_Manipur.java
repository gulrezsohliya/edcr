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
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.BathRoom;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class BathRoom_Manipur extends BathRoom {

	

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		validate(pl);
		
		
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Bathroom");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "25(ii)");
		details.put(DESCRIPTION, BATHROOM_DESCRIPTION);

		BigDecimal minHeight = BigDecimal.ZERO, totalArea = BigDecimal.ZERO, minWidth = BigDecimal.ZERO;

		for (Block b : pl.getBlocks()) {
			if (b.getBuilding() != null && b.getBuilding().getFloors() != null
					&& !b.getBuilding().getFloors().isEmpty()) {

				for (Floor f : b.getBuilding().getFloors()) {

					if (f.getBathRoom() != null && f.getBathRoom().getHeights() != null
							&& !f.getBathRoom().getHeights().isEmpty() && f.getBathRoom().getRooms() != null
							&& !f.getBathRoom().getRooms().isEmpty()) {

						if (f.getBathRoom().getHeights() != null && !f.getBathRoom().getHeights().isEmpty()) {
							minHeight = f.getBathRoom().getHeights().get(0).getHeight();
							for (RoomHeight rh : f.getBathRoom().getHeights()) {
								if (rh.getHeight().compareTo(minHeight) < 0) {
									minHeight = rh.getHeight();
								}
							}
						}

						if (minHeight.compareTo(new BigDecimal(2.4)) >= 0) {

							details.put(REQUIRED, "Minimum Height >= 2.4");
							details.put(PROVIDED, minHeight.toString() );
							details.put(STATUS, Result.Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

						} else {
							details.put(REQUIRED, "Minimum Height >= 2.4");
							details.put(PROVIDED,  minHeight.toString());
							details.put(STATUS, Result.Not_Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
						}

					}

				}
			}

		}

		return pl;
	}

	

}
