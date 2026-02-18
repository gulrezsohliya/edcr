package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.SepticTank;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class SepticTank_AR extends SepticTank{

	private static final Logger LOG = Logger.getLogger(SepticTank_AR.class);
	
	private static final String RULE = "45-e";
	public static final String DISTANCE_FROM_WATERSOURCE = "Distance from watersource";
	public static final String DISTANCE_FROM_BUILDING = "Distance from Building";
	public static final String MIN_DISTANCE_FROM_GOVTBUILDING_DESC = "Minimum distance fcrom government building";
//	public static final BigDecimal MIN_DIS_WATERSRC = BigDecimal.valueOf(18);
//	public static final BigDecimal MIN_DIS_BUILDING = BigDecimal.valueOf(6);
	
	@Override
	public Plan validate(Plan plan) {
	
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Septic Tank ");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, PERMITTED);
			scrutinyDetail.addColumnHeading(3, PROVIDED);
			scrutinyDetail.addColumnHeading(4, STATUS);
			List<org.egov.common.entity.edcr.SepticTank> septicTanks = pl.getSepticTanks();

			for (org.egov.common.entity.edcr.SepticTank septicTank : septicTanks) {
				boolean validWaterSrcDistance = false;
				boolean validBuildingDistance = false;
				if (septicTank != null) {
					setReportOutputDetails(pl, "4.16",  "Septic tank", "Defined in the plan",
							Result.Verify.getResultVal(),scrutinyDetail);
				} else {
					setReportOutputDetails(pl, "4.16",  "Septic Tank", "Not Defined in the plan",
							Result.Not_Accepted.getResultVal(),scrutinyDetail);
				}

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
