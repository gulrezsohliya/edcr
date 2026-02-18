package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
//import org.egov.common.entity.bpa.Occupancy;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.AdditionalFeature;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AdditionalFeature_AR extends AdditionalFeature {
	private static final Logger LOG = Logger.getLogger(AdditionalFeature_AR.class);

	

	@Override
	public Plan validate(Plan pl) {
		try {
//			validateFloorHeight(pl);
			validatePlinth(pl);
			validateNoFloors(pl);
			validateNoBasements(pl);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	
		return pl;
	}

	private void validateNoBasements(Plan pl) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Level Of Basement");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//		scrutinyDetail.addColumnHeading(3, DISTANCE);
//		scrutinyDetail.addColumnHeading(4, PERMITTED);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);

		HashMap<String, String> errors = new HashMap<>();
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "TBD");
		details.put(DESCRIPTION, "Levels Of Basement");

		BigDecimal noOfFloors = BigDecimal.ZERO;
		
		List<Block> blocks = pl.getBlocks();
		for(Block b:pl.getBlocks()) {
			BigDecimal levels=BigDecimal.ZERO;
			levels=b.getBuilding().getTotalFloors().subtract(b.getBuilding().getFloorsAboveGround());
//			for(Floor f:b.getBuilding().getFloors()) {
				details.put(PROVIDED, levels.toString());
				details.put(STATUS, Result.Verify.getResultVal());
				scrutinyDetail.getDetail().add(details);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
				return;
//			}
			
		}
		
	}

	private void validateNoFloors(Plan pl) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_No Of Floors");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//		scrutinyDetail.addColumnHeading(3, DISTANCE);
//		scrutinyDetail.addColumnHeading(4, PERMITTED);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);

		HashMap<String, String> errors = new HashMap<>();
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "TBD");
		details.put(DESCRIPTION, "No Of Floors");

		BigDecimal noOfFloors = BigDecimal.ZERO;
		
		List<Block> blocks = pl.getBlocks();
		for(Block b:pl.getBlocks()) {
			if(b.getBuilding().getMaxFloor()!=null) {
				details.put(PROVIDED, b.getBuilding().getMaxFloor().toString());
				details.put(STATUS, Result.Verify.getResultVal());
				scrutinyDetail.getDetail().add(details);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}
		}
		
	}
	
	private void validatePlinth(Plan pl) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Plinth Height");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//		scrutinyDetail.addColumnHeading(3, DISTANCE);
		scrutinyDetail.addColumnHeading(4, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);

		HashMap<String, String> errors = new HashMap<>();
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "4.2.1.1");
		details.put(DESCRIPTION, "Plinth Height");

		BigDecimal noOfFloors = BigDecimal.ZERO;
		Boolean status=Boolean.FALSE;
		List<Block> blocks = pl.getBlocks();
		for(Block b:pl.getBlocks()) {
			if(b.getPlinthHeight()!=null && !b.getPlinthHeight().isEmpty()) {
				for(BigDecimal p : b.getPlinthHeight()) {
					if(p.compareTo(BigDecimal.valueOf(0.3))>=0) {
						status=Boolean.TRUE;
						details.put(PROVIDED,p.toString());
					}
				}
				details.put(PERMISSIBLE, BigDecimal.valueOf(0.3).toString());
				details.put(STATUS, status?Result.Accepted.getResultVal():Result.Not_Accepted.getResultVal());
				scrutinyDetail.getDetail().add(details);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}
		}
		
	}

	private void validateFloorHeight(Plan pl) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Floor_Height");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//		scrutinyDetail.addColumnHeading(3, DISTANCE);
//		scrutinyDetail.addColumnHeading(4, PERMITTED);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);

		HashMap<String, String> errors = new HashMap<>();
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "TBD");
		details.put(DESCRIPTION, "Floor Height");

		BigDecimal floorHeight = BigDecimal.ZERO;
		
		List<Block> blocks = pl.getBlocks();
		for(Block b:pl.getBlocks()) {
			for(Floor f:b.getBuilding().getFloors()) {
				if(f.getHeight()!=null) {
					floorHeight=f.getHeight();
					details.put(PROVIDED, floorHeight.toString());
					details.put(STATUS, Result.Verify.getResultVal());
					scrutinyDetail.getDetail().add(details);
					pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
				}else {
					pl.addError("FloorHeightError", "Floor Height for floor "+f.getNumber()+ " is missing");
				}
			}
		}
		
	}

	@Override
	public Plan process(Plan pl) {
    	validate(pl);
		
		return pl;
	}

	

}
