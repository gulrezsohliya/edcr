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

@Service
public class StairCover_AR extends StairCover{

	private static final Logger LOG = Logger.getLogger(StairCover_AR.class);
	
	private static final String RULE = "TBD";
//	private static final String RULE_13_6_1_2_C = "TBD";
//	private static final String RULE_13_6_2_4 = "13.6.2.4";
	public static final String STAIRCOVER_DESCRIPTION = "Mumty";

	@Override
	public Plan validate(Plan pl) {
		if (pl.getBlocks() == null)
			return pl;
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			if (pl.getPlot() == null || (pl.getPlot() != null
					&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
				pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
				return pl;
			}

			
			BigDecimal minHeight = BigDecimal.ZERO;
			Boolean status = Boolean.FALSE;
			for (Block b : pl.getBlocks()) {
				Map<String, String> details = new HashMap<>();
				
				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
				scrutinyDetail.addColumnHeading(4, PROVIDED);
//				scrutinyDetail.addColumnHeading(5, ACTION);
				scrutinyDetail.addColumnHeading(6, STATUS);
				
					
						if (b.getStairCovers() != null && !b.getStairCovers().isEmpty()) {
							scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + "Mumty");
							minHeight = b.getStairCovers().stream().reduce(BigDecimal::min).get();
							}
//						else {
//								pl.addError("MumtyError", "Mumty Not Defined");
//							}
						
							details.put(RULE_NO, "4.18.4.2");
							details.put(DESCRIPTION, STAIRCOVER_DESCRIPTION);
							details.put(PERMISSIBLE, ">=2.2m & <=3m");
							details.put(PROVIDED, minHeight.toString());
							
//							details.put(ACTION,minHeight.toString());
							if(minHeight.compareTo(BigDecimal.ZERO)>0 && minHeight.compareTo(BigDecimal.valueOf(2.2))>=0 && minHeight.compareTo(BigDecimal.valueOf(3))<=0) {
								status=Boolean.TRUE;
							}
							details.put(STATUS, status?Result.Accepted.getResultVal():Result.Not_Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

//					} 
				

			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
