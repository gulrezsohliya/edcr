package org.egov.client.edcr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Well;
import org.springframework.stereotype.Service;

@Service
public class Well_AR extends Well{

	private static final Logger LOG = Logger.getLogger(Well_AR.class);
	
	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Well");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, PERMITTED);
			scrutinyDetail.addColumnHeading(3, PROVIDED);
			scrutinyDetail.addColumnHeading(4, STATUS);
			if (pl.getUtility() != null && !pl.getUtility().getWells().isEmpty()
					&& pl.getUtility().getWells() != null) {
					setReportOutputDetails(pl, "TBD",  "Well", "Defined in the plan",
							Result.Verify.getResultVal(),scrutinyDetail);
			}else {
				setReportOutputDetails(pl, "TBD",  "Well", "Not Defined in the plan",
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
		details.put(RULE_NO, "-");
		details.put(DESCRIPTION, ruleDesc);
//		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	
	}
}
