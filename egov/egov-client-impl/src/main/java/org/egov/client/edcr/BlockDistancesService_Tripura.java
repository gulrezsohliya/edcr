package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.BlockDistances;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.BlockDistancesService;
import org.springframework.stereotype.Service;

@Service
public class BlockDistancesService_Tripura extends BlockDistancesService {

	private static final Logger LOG = Logger.getLogger(BlockDistancesService_Tripura.class);

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		scrutinyDetail = new ScrutinyDetail();
//		scrutinyDetail.addColumnHeading(1, "Block");
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		scrutinyDetail.setKey("Common_Distances Between Block");
		BigDecimal BUA = BigDecimal.ZERO;
		int noOfBlocks = 0;
		Boolean exist=false;
		BigDecimal distBetwBlocks = null;
		for (Block b : plan.getBlocks()) {

			noOfBlocks = noOfBlocks + 1;
			if (b.getDisBetweenBlocks() != null && !b.getDisBetweenBlocks().isEmpty()) {
					if (b.getDisBetweenBlocks().get(0).getDistances() != null && !b.getDisBetweenBlocks().get(0).getDistances().isEmpty()) {
						exist=true;
						distBetwBlocks = b.getDisBetweenBlocks().get(0).getDistances().get(0);
						Map<String, String> details = new HashMap<>();
//						details.put("Block", b.getNumber());
						details.put(DESCRIPTION, "Distance Between Block "+b.getNumber()+" and Block "+b.getDisBetweenBlocks().get(0).getBlockNumber());
						details.put(REQUIRED,">="+ BigDecimal.valueOf(1.2).toString());
						details.put(PROVIDED, distBetwBlocks.toString());
						if (distBetwBlocks.compareTo(BigDecimal.valueOf(1.2)) >= 0)
							details.put(STATUS, Result.Accepted.getResultVal());
						else
							details.put(STATUS, Result.Not_Accepted.getResultVal());
						scrutinyDetail.getDetail().add(details);
						plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}
			}
			
		}
		if(noOfBlocks > 1 && !exist) {
			plan.addError("BlockDistanceError", "Distance between blocks is required for multi block plans. Use layer [DIST_BETWEEN_BLK_n_BLK_m]");
		}

		return plan;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	}

}
