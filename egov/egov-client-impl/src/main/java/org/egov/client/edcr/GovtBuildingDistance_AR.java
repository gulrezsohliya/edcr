package org.egov.client.edcr;
import static org.egov.client.constants.DxfFileConstants_AR.R;
import static org.egov.client.constants.DxfFileConstants_AR.R1b;
import static org.egov.client.constants.DxfFileConstants_AR.R1c;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.StairCover;
import org.springframework.stereotype.Service;
import org.apache.log4j.Logger;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.feature.GovtBuildingDistance;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class GovtBuildingDistance_AR extends GovtBuildingDistance{

	private static final Logger LOG = Logger.getLogger(GovtBuildingDistance_AR.class);
	private static final String RULE = "TBD";
//	private static final String RULE_13_6_1_2_C = "TBD";
//	private static final String RULE_13_6_2_4 = "13.6.2.4";
	public static final String GOVTBUILDING_DESCRIPTION = "Distance From Govt Building";
	
	@Override
	public Plan validate(Plan pl) {
		if (pl.getBlocks() == null)
			return pl;
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Government Building Distance");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, DISTANCE);
//			scrutinyDetail.addColumnHeading(4, PERMITTED);
			scrutinyDetail.addColumnHeading(5, PROVIDED);
			scrutinyDetail.addColumnHeading(6, STATUS);

			HashMap<String, String> errors = new HashMap<>();
			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, RULE);
			details.put(DESCRIPTION, GOVTBUILDING_DESCRIPTION);

			BigDecimal minDistanceFromGovtBuilding = BigDecimal.ZERO;
			BigDecimal maxHeightOfBuilding = BigDecimal.ZERO;
			List<BigDecimal> distancesFromGovtBuilding = null;
			if(pl.getDistanceToExternalEntity().getGovtBuildings()!=null) {
				distancesFromGovtBuilding=pl.getDistanceToExternalEntity().getGovtBuildings();
			}
			List<Block> blocks = pl.getBlocks();

			if (StringUtils.isNotBlank(pl.getPlanInformation().getBuildingNearGovtBuilding())
					&& "YES".equalsIgnoreCase(pl.getPlanInformation().getBuildingNearGovtBuilding())) {
				if (!distancesFromGovtBuilding.isEmpty()) {

					minDistanceFromGovtBuilding = distancesFromGovtBuilding.stream().reduce(BigDecimal::min).get();
					details.put(PROVIDED, minDistanceFromGovtBuilding.toString());
					details.put(STATUS, Result.Verify.getResultVal());
					scrutinyDetail.getDetail().add(details);
					pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}
				} 
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	}
}
