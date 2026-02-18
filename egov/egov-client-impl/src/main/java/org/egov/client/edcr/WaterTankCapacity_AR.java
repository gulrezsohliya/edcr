package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.WaterTankCapacity;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;
import org.egov.common.entity.edcr.Result;

@Service
public class WaterTankCapacity_AR extends WaterTankCapacity{

	private static final Logger LOG = Logger.getLogger(WaterTankCapacity_AR.class);
	private static final String RULE = "TBD";
	private static final String RULE_DESCRIPTION = "Water tank capacity";
	private static final String WATER_TANK_CAPACITY = "Minimum capacity of Water tank";
	
	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			 scrutinyDetail = new ScrutinyDetail();
		        scrutinyDetail.addColumnHeading(1, RULE_NO);
		        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		        scrutinyDetail.addColumnHeading(3, REQUIRED);
		        scrutinyDetail.addColumnHeading(4, PROVIDED);
		        scrutinyDetail.addColumnHeading(5, STATUS);
		        scrutinyDetail.setKey("Common_Water tank capacity");
		        String subRule = RULE;
		        String subRuleDesc = RULE_DESCRIPTION;
		        BigDecimal expectedWaterTankCapacity = BigDecimal.ZERO;

		        if (pl.getUtility() != null && pl.getVirtualBuilding() != null
		                && pl.getUtility().getWaterTankCapacity() != null) {
		            Boolean valid = false;
		            BigDecimal totalBuitUpArea = pl.getVirtualBuilding().getTotalBuitUpArea();
		            BigDecimal providedWaterTankCapacity = pl.getUtility().getWaterTankCapacity();
		            providedWaterTankCapacity = providedWaterTankCapacity.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
		                    DcrConstants.ROUNDMODE_MEASUREMENTS);
		          
		            processWaterTankCapacity(pl, "TBD", subRule, subRuleDesc, providedWaterTankCapacity, valid);
		        }
		}catch (Exception e) {
			// TODO: handle exception
		}
		

	        return pl;
	}
	private void processWaterTankCapacity(Plan plan, String rule, String subRule, String subRuleDesc,
            BigDecimal providedWaterTankCapacity, Boolean valid) {
        if (providedWaterTankCapacity.compareTo(BigDecimal.valueOf(0)) > 0) {
                setReportOutputDetails(plan, subRule, WATER_TANK_CAPACITY,
                		providedWaterTankCapacity.toString(),
                        plan.getUtility().getWaterTankCapacity().toString(),
                        Result.Verify.getResultVal());
           
        }
    }

    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
            String status) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
//        details.put(REQUIRED, expected);
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
