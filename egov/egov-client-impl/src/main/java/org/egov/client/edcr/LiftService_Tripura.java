package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.R;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Lift;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.LiftService;
import org.springframework.stereotype.Service;
import org.egov.common.entity.edcr.Result;

@Service
public class LiftService_Tripura extends LiftService{

	private static final Logger LOG = Logger.getLogger(LiftService_Tripura.class);
	
	@Override
	public Plan validate(Plan plan) {
		HashMap<String, String> errors = new HashMap<String, String>();
		try {
			if (plan != null && !plan.getBlocks().isEmpty()) {
	            blk: for (Block block : plan.getBlocks()) {
	                scrutinyDetail = new ScrutinyDetail();
	                scrutinyDetail.addColumnHeading(1, RULE_NO);
	                scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	                scrutinyDetail.addColumnHeading(3, REQUIRED);
	                scrutinyDetail.addColumnHeading(4, PROVIDED);
	                scrutinyDetail.addColumnHeading(5, STATUS);
	                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Lift - Minimum Required");

	               

	                if (block.getBuilding() != null && !block.getBuilding().getOccupancies().isEmpty()) {
	                    
	                    BigDecimal noOfLiftsRqrd = BigDecimal.ZERO;
	                    Boolean liftReq = Boolean.FALSE;
	                 
	                    if (plan.getVirtualBuilding().getMostRestrictiveFarHelper().getType() != null
								&& plan.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode().equals(R) && block.getBuilding().getMaxFloor()!=null) {
	                    	if(block.getBuilding().getMaxFloor().compareTo(BigDecimal.valueOf(5))>=0 || block.getBuilding().getMaxFloor().compareTo(BigDecimal.valueOf(6))>=0) {
	                    		noOfLiftsRqrd = BigDecimal.valueOf(1);
	                    		liftReq=Boolean.TRUE;
	                    	}
	                    		
	                    	else if(block.getBuilding().getMaxFloor().compareTo(BigDecimal.valueOf(7))>=0 ) {
	                    		noOfLiftsRqrd = BigDecimal.valueOf(2);
	                    		liftReq=Boolean.TRUE;
	                    	}
	                    		
	                    }
	                    else {
	                    	if(block.getBuilding().getMaxFloor().compareTo(BigDecimal.valueOf(4))>=0 || block.getBuilding().getMaxFloor().compareTo(BigDecimal.valueOf(5))>=0) {
	                    		noOfLiftsRqrd = BigDecimal.valueOf(1);
	                    		liftReq=Boolean.TRUE;
	                    	}
	                    		
	                    	else if(block.getBuilding().getMaxFloor().compareTo(BigDecimal.valueOf(6))>=0 ) {
	                    		noOfLiftsRqrd = BigDecimal.valueOf(2);
	                    		liftReq=Boolean.TRUE;
	                    	}
	                    		
	                    	
	                    }
	                        
	                        boolean valid = false;
	                        Boolean liftExist=Boolean.FALSE;
	                        for(Floor floor : block.getBuilding().getFloors()) {
	                        	if(floor.getLifts()!=null && !floor.getLifts().isEmpty()) {
	                        		liftExist=Boolean.TRUE;
	                        	}
	                        }
//	                        if(liftExist) {
	                        	if(liftReq && !liftExist) {
	                        		plan.addError("LiftError", "No Lifts Defined");
	                        	}else {
	                        		if (BigDecimal.valueOf(Double.valueOf(block.getNumberOfLifts()))
		                                    .compareTo(noOfLiftsRqrd) >= 0) {
		                                valid = true;
		                            }
		                            if (valid) {
		                                setReportOutputDetails(plan, "Rule 75", "Rule 75",
		                                        noOfLiftsRqrd.toString(), block.getNumberOfLifts(), Result.Accepted.getResultVal(),
		                                        "", scrutinyDetail);
		                            } else {
		                                setReportOutputDetails(plan, "Rule 75", "Rule 75", noOfLiftsRqrd.toString(),
		                                        block.getNumberOfLifts(), Result.Not_Accepted.getResultVal(), "", scrutinyDetail);
		                            }
	                        	}
	                        	
//	                        }
	                        	
	                        

	                    }
	                }

	                

	            }
		}catch (Exception e) {
			// TODO: handle exception
		}
		
        

        return plan;
	}

	

	@Override
	public Plan process(Plan plan) {
		try {
			
			validate(plan);
			 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return plan;
	}
	
	private void setReportOutputDetails(Plan plan, String ruleNo, String ruleDesc, String expected, String actual,
            String status, String remarks, ScrutinyDetail scrutinyDetail) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }
	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	}
}
