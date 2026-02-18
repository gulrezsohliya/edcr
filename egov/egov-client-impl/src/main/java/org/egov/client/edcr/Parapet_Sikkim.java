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
public class Parapet_Sikkim extends Parapet {

	private static final Logger LOG = Logger.getLogger(Parapet_Sikkim.class);
	private static final String RULE_6I_2A = "6i 2a";
	public static final String PARAPET_DESCRIPTION = "Parapet";

	private static final BigDecimal MIN_HEIGHT = BigDecimal.valueOf(0.9);// metres
	private static final BigDecimal MIN_HEIGHT_1_06 = BigDecimal.valueOf(1.06);// metres

	@Override
	public Plan validate(Plan pl) {

		return pl;
	}

	@Override
	public Plan process(Plan pl) {

		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
			return pl;
		}

		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Parapet");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, RULE_6I_2A);
		details.put(DESCRIPTION, PARAPET_DESCRIPTION);

		BigDecimal minHeight = BigDecimal.ZERO;

		for (Block b : pl.getBlocks()) {
			if (b.getParapets() != null && !b.getParapets().isEmpty()) {
				minHeight = b.getParapets().stream().reduce(BigDecimal::min).get();
				minHeight = minHeight.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
						DcrConstants.ROUNDMODE_MEASUREMENTS);

				details.put(REQUIRED, "Height >= " + MIN_HEIGHT.toString() + DcrConstants.IN_METER);
				details.put(PROVIDED, "" + minHeight.toString() + DcrConstants.IN_METER);

				if (minHeight.compareTo(MIN_HEIGHT) >= 0) {
					details.put(STATUS, Result.Accepted.getResultVal());
				} else {
					details.put(STATUS, Result.Not_Accepted.getResultVal());
				}
				scrutinyDetail.getDetail().add(details);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}
		}

		return pl;
	}

}
