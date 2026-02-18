package org.egov.client.edcr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.ElectricLine;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.feature.OverheadElectricalLineService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class OverheadElectricalLineService_Sikkim extends OverheadElectricalLineService{

	private static final Logger LOG = Logger.getLogger(OverheadElectricalLineService_Sikkim.class);

    private static final String SUB_RULE_31 = "31";

    private static final BigDecimal VERTICAL_DISTANCE_11000 = BigDecimal.valueOf(2.5);
    private static final BigDecimal VERTICAL_DISTANCE_33000 = BigDecimal.valueOf(3.7);
    private static final BigDecimal HORIZONTAL_DISTANCE_33000 = BigDecimal.valueOf(2);
    private static final BigDecimal HORIZONTAL_DISTANCE_11000 = BigDecimal.valueOf(1.2);

    private static final int VOLTAGE_11000 = 11;
    private static final int VOLTAGE_33000 = 33;
    private static final String REMARKS = "Remarks";
    private static final String VOLTAGE = "Voltage";

	@Override
	public Plan validate(Plan pl) {
		// TODO Auto-generated method stub
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		// TODO Auto-generated method stub
		return pl;
	}

}
