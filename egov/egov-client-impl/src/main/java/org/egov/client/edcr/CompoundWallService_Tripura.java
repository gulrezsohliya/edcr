package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.CompoundWall;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.CompoundWallService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class CompoundWallService_Tripura extends CompoundWallService{

	private static final Logger LOG = Logger.getLogger(CompoundWallService_Tripura.class);
	
	@Override
	public Plan validate(Plan plan) {
		process(plan);
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_COMPOUND WALL");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);
			scrutinyDetail.setHeading("Compound Wall");
			Map<String, String> planInfoProperties = pl.getPlanInfoProperties();
			List<String> valuesList = new ArrayList<>();
			Boolean compound=Boolean.FALSE;
			for (Object value : planInfoProperties.values()) {
			    valuesList.add(value.toString()); // or cast if you know the type
			    System.out.println(value.toString());
			    if(value.toString().equalsIgnoreCase("COMPOUNDWALL")) {
			    	compound=Boolean.TRUE;
				}
			}
			if(!compound) {
					if (pl.getBlocks().isEmpty())
						pl.addError("BLDG_FOOT_PRINT",
								getEdcrMessageSource().getMessage(DcrConstants.OBJECTNOTDEFINED,
										new String[] { "BLDG_FOOT_PRINT" }, null));
					if (pl.getVirtualBuilding().getMostRestrictiveFarHelper() == null) {
						pl.addError("OccupancyError", "Occupancy Not Defined");
					}
			}
			
			
				BigDecimal frontRailheight=BigDecimal.ZERO;
				BigDecimal rearRailheight=BigDecimal.ZERO;
				BigDecimal railheight=BigDecimal.ZERO;
				Boolean isAccepted=Boolean.FALSE;
				if (pl.getCompoundWall()!=null && pl.getCompoundWall().getWallHeights()!=null && !pl.getCompoundWall().getWallHeights().isEmpty()) {
					for(Measurement cp : pl.getCompoundWall().getRailingHeights()) { //upper part of wall
						railheight=cp.getHeight();
					}
					for(Measurement cp : pl.getCompoundWall().getWallHeights()) { // front / rear height of wall
						if(cp.getColorCode()==1) {
							frontRailheight=cp.getHeight();
						}
						if(cp.getColorCode()==2) {
							rearRailheight=cp.getHeight();
						}
					}
					if(frontRailheight.compareTo(BigDecimal.ZERO)>0) {
						if(frontRailheight.compareTo(BigDecimal.valueOf(1.5))<=0)
							isAccepted=Boolean.TRUE;
						Map<String, String> details = new HashMap<>();
						details.put(RULE_NO, "69");
						details.put(DESCRIPTION, "Front Height");
						details.put(PERMISSIBLE,BigDecimal.valueOf(1.5)+"");
						details.put(PROVIDED, frontRailheight +"");
						details.put(STATUS,isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}else {
						pl.addError("FrontRailError", "Front Rail Height Not defined");
					}
					if(rearRailheight.compareTo(BigDecimal.ZERO)>0) {
						if(rearRailheight.compareTo(BigDecimal.valueOf(1.5))<=0)
							isAccepted=Boolean.TRUE;
						Map<String, String> details = new HashMap<>();
						details.put(RULE_NO, "69");
						details.put(DESCRIPTION, "Rear Height");
						details.put(PERMISSIBLE,BigDecimal.valueOf(1.5)+"");
						details.put(PROVIDED, rearRailheight +"");
						details.put(STATUS,isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}
					if(railheight.compareTo(BigDecimal.ZERO)>0) {
						Map<String, String> details = new HashMap<>();
						details.put(RULE_NO, "69");
						details.put(DESCRIPTION, "Railing Height");
						details.put(PERMISSIBLE, "");
						details.put(PROVIDED, String.valueOf(railheight.doubleValue()));
						details.put(STATUS,"");

						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}
					Map<String, String> details = new HashMap<>();
					details.put(RULE_NO, "69");
					details.put(DESCRIPTION, "Total");
					details.put(PERMISSIBLE, "Front Height("+frontRailheight +") + Railing Height("+railheight+") should be less than or equal to 2m");
					details.put(PROVIDED, frontRailheight.add(railheight) + "");
					details.put(STATUS,frontRailheight.add(railheight).compareTo(BigDecimal.valueOf(2))<=0 ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
					scrutinyDetail.getDetail().add(details);
					pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					
					if(rearRailheight.compareTo(BigDecimal.valueOf(0))>0) {
						details = new HashMap<>();
						details.put(RULE_NO, "69");
						details.put(DESCRIPTION, "");
						details.put(PERMISSIBLE, "Rear Height("+rearRailheight +") + Railing Height("+railheight+") should be less than or equal to 2m");
						details.put(PROVIDED, rearRailheight.add(railheight) + "");
						details.put(STATUS,rearRailheight.add(railheight).compareTo(BigDecimal.valueOf(2))<=0 ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}
				}
				
				
				
				
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	}
	
	private void buildResult(Plan pl,String desc,Boolean isaccepted,BigDecimal provided,BigDecimal permissible) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "69");
		details.put(DESCRIPTION, desc);
		details.put(PERMISSIBLE, permissible+"");
		details.put(PROVIDED, provided+"");
		details.put(STATUS,
				isaccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}
}
