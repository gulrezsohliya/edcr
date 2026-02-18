package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
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
import org.egov.edcr.utility.Util;
import org.springframework.stereotype.Service;

@Service
public class HeadRoom_Manipur extends HeadRoom{

	private static final Logger LOG = Logger.getLogger(HeadRoom_Manipur.class);
	private static final BigDecimal TWO_POINTONE = BigDecimal.valueOf(2.1);
	private static final String RULE25_iv = "25(iv)";
	private static final BigDecimal TWO_POINTFOUR = BigDecimal.valueOf(2.4);
	private static final String RULE70_ii = "70(2)";
	
	@Override
	public Plan validate(Plan plan) {
		for (Block block : plan.getBlocks()) {
            if (block.getBuilding() != null) {

                ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.addColumnHeading(1, RULE_NO);
                scrutinyDetail.addColumnHeading(2, DESCRIPTION);
                scrutinyDetail.addColumnHeading(3, REQUIRED);
                scrutinyDetail.addColumnHeading(4, PROVIDED);
                scrutinyDetail.addColumnHeading(5, STATUS);
                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Stair Headroom");
                try {
                	 org.egov.common.entity.edcr.HeadRoom headRoom = block.getBuilding().getHeadRoom();

                     if (headRoom != null) {

                         List<BigDecimal> headRoomDimensions = headRoom.getHeadRoomDimensions();

                         if (headRoomDimensions != null && headRoomDimensions.size() > 0) {

                             BigDecimal minHeadRoomDimension = headRoomDimensions.stream().reduce(BigDecimal::min).get();
                             BigDecimal coveredParkingArea = BigDecimal.ZERO;
                             BigDecimal minWidth = Util.roundOffTwoDecimal(minHeadRoomDimension);
                             for (Floor floor : block.getBuilding().getFloors()) {
                            	 coveredParkingArea = coveredParkingArea.add(floor.getParking().getBasementCars().stream()
             							.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));
                            	 
                             }
                             
                             if(coveredParkingArea.compareTo(BigDecimal.ZERO)>0) {
                            	 if (minWidth.compareTo(TWO_POINTFOUR) >= 0) {
                                     setReportOutputDetails(plan, RULE70_ii, DESCRIPTION,
                                             String.valueOf(TWO_POINTFOUR), String.valueOf(minWidth), Result.Accepted.getResultVal(),
                                             scrutinyDetail);
                                 } else {
                                     setReportOutputDetails(plan, RULE70_ii, DESCRIPTION,
                                             String.valueOf(TWO_POINTFOUR), String.valueOf(minWidth), Result.Not_Accepted.getResultVal(),
                                             scrutinyDetail);
                                 }
                             }
                             else if (minWidth.compareTo(TWO_POINTONE) >= 0) {
                                 setReportOutputDetails(plan, RULE25_iv, DESCRIPTION,
                                         String.valueOf(TWO_POINTONE), String.valueOf(minWidth), Result.Accepted.getResultVal(),
                                         scrutinyDetail);
                             } else {
                                 setReportOutputDetails(plan, RULE25_iv, DESCRIPTION,
                                         String.valueOf(TWO_POINTONE), String.valueOf(minWidth), Result.Not_Accepted.getResultVal(),
                                         scrutinyDetail);
                             }
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
		//validate(plan);
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
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
}
