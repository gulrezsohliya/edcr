package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.BuildingHeight;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class BuildingHeight_GangtokTEST extends BuildingHeight{
	
	private static final Logger LOG = Logger.getLogger(BuildingHeight_GangtokTEST.class);
	
	public static final BigDecimal ZONE_SK_1 = BigDecimal.valueOf(1);
	public static final BigDecimal ZONE_SK_2 = BigDecimal.valueOf(2);
	public static final BigDecimal ZONE_SK_3 = BigDecimal.valueOf(3);
	public static final BigDecimal ZONE_SK_4 = BigDecimal.valueOf(4);
	public static final BigDecimal ZONE_SK_5 = BigDecimal.valueOf(5);
	
	public static final BigDecimal HEIGHT_SK_6 = BigDecimal.valueOf(6);
	public static final BigDecimal HEIGHT_SK_9 = BigDecimal.valueOf(9);
	public static final BigDecimal HEIGHT_SK_12 = BigDecimal.valueOf(12);
	public static final BigDecimal HEIGHT_SK_15 = BigDecimal.valueOf(15);
	public static final BigDecimal HEIGHT_SK_18 = BigDecimal.valueOf(18);
	
	private static final String HEIGHT_BUILDING = "Maximum height of building allowed...";
	private static final String ZONE= "Zone";
	private static final String RULE_TBD= "Zone";
	private static final String BUILDING_HEIGHT= "Building Height";
	@Override
    public Plan validate(Plan pl) {
		
		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, ZONE);
		scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(6, PROVIDED);
		scrutinyDetail.addColumnHeading(7, STATUS);
		
		HashMap<String, String> errors = new HashMap<>();
         
		BigDecimal ZONE_DC = BigDecimal.ZERO;
		String stabilityReport = StringUtils.EMPTY, temp [] = new String[2];
		
		try {
			stabilityReport = new String(pl.getPlanInfoProperties().entrySet().stream()
					.filter(e -> e.getKey().equals("STABILITY_REPORT")).map(Map.Entry::getValue).findFirst().orElse("0"));
			temp = stabilityReport.split("_", 2);
			ZONE_DC = BigDecimal.valueOf(Integer.valueOf(temp[1]));
		}catch(Exception e){
			ZONE_DC = BigDecimal.ZERO;
			errors.put("STABILITY_REPORT is not declared", "STABILITY_REPORT is not declared");
			pl.setErrors(errors);
			return pl;
		}
		
		if(ZONE_DC.compareTo(BigDecimal.ZERO) <= 0) {
			errors.put("STABILITY_REPORT is not declared", "STABILITY_REPORT is not declared");
			pl.setErrors(errors);
			return pl;
		}
		
		
		for (Block block : pl.getBlocks()) {

			boolean isAccepted = false;
			String ruleNo = RULE_TBD;

			scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Height of Building");
			
			if (block.getBuilding() != null && (block.getBuilding().getBuildingHeight() != null
					|| block.getBuilding().getBuildingHeight().compareTo(BigDecimal.ZERO) > 0)) {
				
				String requiredBuildingHeight = StringUtils.EMPTY;
				BigDecimal buildingHeight = block.getBuilding().getBuildingHeight();


				if (ZONE_DC.compareTo(ZONE_SK_1) == 0 && buildingHeight.compareTo(HEIGHT_SK_18) <= 0) {
					isAccepted = true;
					requiredBuildingHeight = "<= " + HEIGHT_SK_18;
				} else if (ZONE_DC.compareTo(ZONE_SK_2) == 0 && buildingHeight.compareTo(HEIGHT_SK_15) <= 0) {
					isAccepted = true;
					requiredBuildingHeight = "<= " + HEIGHT_SK_15;
				} else if (ZONE_DC.compareTo(ZONE_SK_3) == 0 && buildingHeight.compareTo(HEIGHT_SK_12) <= 0) {
					isAccepted = true;
					requiredBuildingHeight = "<= " + HEIGHT_SK_12;
				} else if (ZONE_DC.compareTo(ZONE_SK_4) == 0 && buildingHeight.compareTo(HEIGHT_SK_9) <= 0) {
					isAccepted = true;
					requiredBuildingHeight = "<= " + HEIGHT_SK_9;
				} else if (ZONE_DC.compareTo(ZONE_SK_5) == 0 && buildingHeight.compareTo(HEIGHT_SK_6) <= 0) {
					isAccepted = true;
					requiredBuildingHeight = "<= " + HEIGHT_SK_6;
				} else {
					isAccepted = false;
					if (ZONE_DC.compareTo(ZONE_SK_1) == 0) {
						requiredBuildingHeight = "<= " + HEIGHT_SK_18;
					} else if (ZONE_DC.compareTo(ZONE_SK_2) == 0) {
						requiredBuildingHeight = "<= " + HEIGHT_SK_15;
					} else if (ZONE_DC.compareTo(ZONE_SK_3) == 0) {
						requiredBuildingHeight = "<= " + HEIGHT_SK_12;
					} else if (ZONE_DC.compareTo(ZONE_SK_4) == 0) {
						requiredBuildingHeight = "<= " + HEIGHT_SK_9;
					} else if (ZONE_DC.compareTo(ZONE_SK_5) == 0) {
						requiredBuildingHeight = "<= " + HEIGHT_SK_6;
					}
				}

				if (errors.isEmpty() && StringUtils.isNotBlank(requiredBuildingHeight)) {

					Map<String, String> details = new HashMap<>();
					details.put(RULE_NO, ruleNo);
					details.put(DESCRIPTION, HEIGHT_BUILDING);
					details.put(ZONE, " Zone " + ZONE_DC);
					details.put(PERMISSIBLE, requiredBuildingHeight);
					details.put(PROVIDED, String.valueOf(buildingHeight));
					details.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

					scrutinyDetail.getDetail().add(details);
					pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
				}
				
			}else {
				errors.put(BUILDING_HEIGHT + block.getNumber(), getLocaleMessage(DcrConstants.OBJECTNOTDEFINED,
						BUILDING_HEIGHT + " for block " + block.getNumber()));
				pl.addErrors(errors);
			}
			
		}
		 
        return pl;
    }
	
	
	@Override
    public Plan process(Plan Plan) {

		validate(Plan);
         
        return Plan;
    }
	
}
