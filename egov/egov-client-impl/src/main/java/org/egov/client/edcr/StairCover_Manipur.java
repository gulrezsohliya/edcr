package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.HeadRoom;
import org.egov.edcr.feature.StairCover;
import org.egov.edcr.utility.Util;
import org.springframework.stereotype.Service;

@Service
public class StairCover_Manipur extends StairCover{

	private static final BigDecimal TWO_POINTONE = BigDecimal.valueOf(2.1);
	private static final String RULE25_iv = "25(iv)";
	private static final BigDecimal TWO_POINTFOUR = BigDecimal.valueOf(2.4);
	private static final String RULE70_ii = "70(2)";
	
	@Override
	public Plan validate(Plan plan) {
		BigDecimal minHeight = BigDecimal.ZERO;
		for (Block block : plan.getBlocks()) {
            if (block.getBuilding() != null) {

                ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.addColumnHeading(1, RULE_NO);
                scrutinyDetail.addColumnHeading(2, DESCRIPTION);
                scrutinyDetail.addColumnHeading(3, REQUIRED);
                scrutinyDetail.addColumnHeading(4, PROVIDED);
                scrutinyDetail.addColumnHeading(5, STATUS);
                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Mumty");
                try {
                	if(block.getStairCovers()!=null)
                		minHeight = block.getStairCovers().stream().reduce(BigDecimal::min).get();
                	else
                   	 plan.addError("StairCover Error", "Stair Cover / Mumty Not defined");
                	
                     if (minHeight != null && minHeight.compareTo(BigDecimal.ZERO)>0) {

                             BigDecimal coveredParkingArea = BigDecimal.ZERO;
                             for (Floor floor : block.getBuilding().getFloors()) {
                            	 coveredParkingArea = coveredParkingArea.add(floor.getParking().getBasementCars().stream()
             							.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));
                            	 
                             }
                             
                             if(coveredParkingArea.compareTo(BigDecimal.ZERO)>0) {
                            	 if (minHeight.compareTo(TWO_POINTFOUR) >= 0) {
                                     setReportOutputDetails(plan, RULE70_ii, DESCRIPTION,
                                             String.valueOf(TWO_POINTFOUR), String.valueOf(minHeight), Result.Accepted.getResultVal(),
                                             scrutinyDetail);
                                 } else {
                                     setReportOutputDetails(plan, RULE70_ii, DESCRIPTION,
                                             String.valueOf(TWO_POINTFOUR), String.valueOf(minHeight), Result.Not_Accepted.getResultVal(),
                                             scrutinyDetail);
                                 }
                             }
                             else if (minHeight.compareTo(TWO_POINTONE) >= 0) {
                                 setReportOutputDetails(plan, RULE25_iv, DESCRIPTION,
                                         String.valueOf(TWO_POINTONE), String.valueOf(minHeight), Result.Accepted.getResultVal(),
                                         scrutinyDetail);
                             } else {
                                 setReportOutputDetails(plan, RULE25_iv, DESCRIPTION,
                                         String.valueOf(TWO_POINTONE), String.valueOf(minHeight), Result.Not_Accepted.getResultVal(),
                                         scrutinyDetail);
                             }
                         

                     }
                     
                }catch (Exception e) {
					// TODO: handle exception
				}
               
            }
        }
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		validate(plan);
		// TODO Auto-generated method stub
		return plan;
	}

	 private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
	            String status, ScrutinyDetail scrutinyDetail) {
	        Map<String, String> details = new HashMap<>();
	        details.put(RULE_NO, ruleNo);
	        details.put(DESCRIPTION, ruleDesc);
	        details.put(REQUIRED, expected);
	        details.put(PROVIDED, actual);
	        details.put(STATUS, status);
	        scrutinyDetail.getDetail().add(details);
	        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	    }

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
