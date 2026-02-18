package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.StairCover;
import org.springframework.stereotype.Service;

@Service
public class StairCover_Tripura extends StairCover{

	private static final Logger LOG = Logger.getLogger(StairCover_Tripura.class);
	
	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		 ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	        scrutinyDetail.setKey("Common_Mumty");
	        scrutinyDetail.addColumnHeading(1, RULE_NO);
	        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	        scrutinyDetail.addColumnHeading(3, REQUIRED);
	        scrutinyDetail.addColumnHeading(4, PROVIDED);
	        scrutinyDetail.addColumnHeading(5, STATUS);

	        Map<String, String> details = new HashMap<>();
	        details.put(RULE_NO, "47(2)");

	        BigDecimal minHeight = BigDecimal.ZERO;

	        for (Block b : plan.getBlocks()) {
	            minHeight = BigDecimal.ZERO;
	            if (b.getStairCovers() != null && !b.getStairCovers().isEmpty()) {
	                minHeight = b.getStairCovers().stream().reduce(BigDecimal::min).get();

	                if (minHeight.compareTo(BigDecimal.valueOf(2.4)) <= 0) {
	                    details.put(DESCRIPTION, STAIRCOVER_DESCRIPTION);
	                    details.put(REQUIRED, "Height <= 2.4 meters");
	                    details.put(PROVIDED, minHeight +" m");
	                    details.put(STATUS, Result.Accepted.getResultVal());
	                    scrutinyDetail.getDetail().add(details);
	                    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	                } else {
	                    details.put(DESCRIPTION, STAIRCOVER_DESCRIPTION);
	                    details.put(REQUIRED, "Height <= 2.4 meters");
	                    details.put(PROVIDED, minHeight +" m");
	                    details.put(STATUS, Result.Not_Accepted.getResultVal());
	                    scrutinyDetail.getDetail().add(details);
	                    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	                }
	            }

	        }
		return plan;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	
	}
}
