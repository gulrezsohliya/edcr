package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Solar;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Solar_AR extends Solar{

	private static final Logger LOG = Logger.getLogger(Solar_AR.class);
	
	private static final String RULE = "-";
	
	@Override
	public Plan validate(Plan plan) {
	
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Solar ");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, PERMITTED);
			scrutinyDetail.addColumnHeading(3, PROVIDED);
			scrutinyDetail.addColumnHeading(4, STATUS);

			if (pl.getUtility() != null && !pl.getUtility().getSolar().isEmpty()
					&& pl.getUtility().getSolar() != null) {
				setReportOutputDetails(pl, "10.2.3",  "Solar", "Defined in the plan",
						Result.Verify.getResultVal(),scrutinyDetail);
			}else {
				setReportOutputDetails(pl, "10.2.3",  "Solar", "Not Defined in the plan",
						Result.Not_Accepted.getResultVal(),scrutinyDetail);
			}
				

		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return pl;
	}
	
	
	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String actual,
			String status,ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
//		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	
}
