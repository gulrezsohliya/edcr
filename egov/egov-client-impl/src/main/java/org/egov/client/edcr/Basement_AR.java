package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Basement;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;
import org.egov.common.entity.edcr.Result;
@Service
public class Basement_AR extends Basement {

	private static final Logger LOG = Logger.getLogger(Basement_AR.class);
	public static final String HEIGHT ="Height";
	public static final String AREA ="Area";

	@Override
	public Plan validate(Plan plan) {
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		validate(pl);
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	        scrutinyDetail.setKey("Common_Basement");
	        scrutinyDetail.addColumnHeading(1, RULE_NO);
	        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	        scrutinyDetail.addColumnHeading(3, HEIGHT);
	        scrutinyDetail.addColumnHeading(4, "Provided");
	        scrutinyDetail.addColumnHeading(5, STATUS);
	        Map<String, String> details = new HashMap<>();

	        BigDecimal heightOfBasement = BigDecimal.ZERO;
	        BigDecimal areaOfBasement = BigDecimal.ZERO;
	        String rule = "4.10.4";
	        Boolean status=Boolean.FALSE;
	        if (pl.getBlocks() != null) {
	            for (Block b : pl.getBlocks()) {
	                if (b.getBuilding() != null && b.getBuilding().getFloors() != null
	                        && !b.getBuilding().getFloors().isEmpty()) {

	                    for (Floor f : b.getBuilding().getFloors()) {
	                    	status=Boolean.FALSE;
	                        if (f.getNumber() <= -1) {
	                        	areaOfBasement=f.getArea();
	                        	if(f.getHeightFromTheFloorToCeiling()!=null && !f.getHeightFromTheFloorToCeiling().isEmpty()) {
	                        		heightOfBasement=f.getHeightFromTheFloorToCeiling().stream().reduce(BigDecimal::min).get();
	                        		if(heightOfBasement.compareTo(BigDecimal.valueOf(2.4))>0 && heightOfBasement.compareTo(BigDecimal.valueOf(4.5))<0)
		                        		status=Boolean.TRUE;
		                        	 details = new HashMap<>();
	                                 details.put(RULE_NO, rule);
	                                 details.put(DESCRIPTION, "Basement");
	                                 details.put(HEIGHT, heightOfBasement.toString());
	                                 details.put("Provided", areaOfBasement.toString());
	                                 details.put(STATUS, status ? Result.Accepted.toString() : Result.Not_Accepted.toString());
	                                 scrutinyDetail.getDetail().add(details);
	                        	}	                        		
	                        	else
	                        		pl.addError("Basement"+f.getNumber(), "Please specify the height of basement for floor "+f.getNumber());
	                        	
	                        }
	                    }
	                }
	            }
	        }
	        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		}catch (Exception e) {
			// TODO: handle exception
		}
		 
		return pl;
	}
}
