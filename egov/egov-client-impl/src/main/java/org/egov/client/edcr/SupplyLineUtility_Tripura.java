package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SupplyLine;
import org.egov.common.entity.edcr.Utility;
import org.egov.edcr.feature.SupplyLineUtility;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SupplyLineUtility_Tripura extends SupplyLineUtility {
	

	

	@Override
	public Plan validate(Plan pl) {
		

		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Utility Supply Line ");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, PERMITTED);
			scrutinyDetail.addColumnHeading(3, PROVIDED);
			scrutinyDetail.addColumnHeading(4, STATUS);
			if(pl.getUtility().getSupplyLine()!=null) {
				for(Measurement supplyLine : pl.getUtility().getSupplyLine().getSupplyLines()) {
					if(supplyLine.getColorCode()==40) {
						buildResult(pl, scrutinyDetail, true, "Water Drain Line", "Defined in the plan");
					}
					if(supplyLine.getColorCode()==48) {
						buildResult(pl, scrutinyDetail, true, "Sewarage Line", "Defined in the plan");
					}
					if(supplyLine.getColorCode()==1) {
						buildResult(pl, scrutinyDetail, true, "Electricity Supply Line", "Defined in the plan");
					}
					if(supplyLine.getColorCode()==2) {
						buildResult(pl, scrutinyDetail, true, "Water Supply Line", "Defined in the plan");
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return pl;
	}
	
	private void buildResult(Plan pl, ScrutinyDetail scrutinyDetail, boolean valid, String description,String provided) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "TBD");
		details.put(DESCRIPTION, description);
		details.put(PROVIDED, provided);
		details.put(STATUS, Result.Verify.getResultVal());
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	

}
