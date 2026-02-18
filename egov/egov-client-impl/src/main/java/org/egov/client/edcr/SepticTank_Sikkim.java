package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.SepticTank;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class SepticTank_Sikkim extends SepticTank{

	private static final Logger LOG = Logger.getLogger(SepticTank_Sikkim.class);
	
	private static final String DECLARED = "Declared";
	private static final String RULE_4_6_i_2_e_8_25 = "4, 6(i) 2(e), 8 & 25";
	
	@Override
	public Plan validate(Plan plan) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Septic Tank ");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(4, DECLARED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		if(plan.getSepticTanks() != null && !plan.getSepticTanks().isEmpty()) {
			buildResult(plan, scrutinyDetail, Result.Verify.getResultVal(), "Septic Tank", "", DcrConstants.OBJECTDEFINED_DESC);
		}
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		validate(plan);
		return plan;
	}

	private void buildResult(Plan pl, ScrutinyDetail scrutinyDetail, String status, String description, String permited,
			String provided) {
		Map<String, String> details = new HashMap<>();

		details.put(RULE_NO, RULE_4_6_i_2_e_8_25);
		details.put(DESCRIPTION, description);
		details.put(DECLARED, provided);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		 
	}
}
