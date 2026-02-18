package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Parapet;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Parapet_AR extends Parapet {

	private static final Logger LOG = Logger.getLogger(Parapet.class);
	private static final String RULE = "TBD";
	public static final String PARAPET_DESCRIPTION = "Parapet";

	@Override
	public Plan validate(Plan pl) {
		
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Parapet");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);

			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, "Section 4.12");
			details.put(DESCRIPTION, PARAPET_DESCRIPTION);

			BigDecimal minHeight = BigDecimal.ZERO;

			for (Block b : pl.getBlocks()) {
				if (b.getParapets() != null && !b.getParapets().isEmpty()) {
					minHeight = b.getParapets().stream().reduce(BigDecimal::min).get();

					if (minHeight.compareTo(new BigDecimal(1.0)) >= 0 && minHeight.compareTo(new BigDecimal(1.5)) <= 0) {

						details.put(PERMISSIBLE, "Height >= 1.0 and height <= 1.5");
						details.put(PROVIDED, "Height = " + minHeight );
						details.put(STATUS, Result.Accepted.getResultVal());
						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

					} else {
						details.put(PERMISSIBLE, "Height >= 1.0 and height <= 1.5");
						details.put(PROVIDED, "Height = " + minHeight );
						details.put(STATUS, Result.Not_Accepted.getResultVal());
						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}
				}
//				else {
//					pl.addError("ParapetError", "Parapet Not Defined");
//				}
					
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		

		return pl;
	}

}
