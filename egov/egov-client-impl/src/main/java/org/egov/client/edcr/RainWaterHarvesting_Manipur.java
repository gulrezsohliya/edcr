package org.egov.client.edcr;

/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.IN_LITRE;

import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.feature.RainWaterHarvesting;
import org.egov.edcr.utility.DcrConstants;
import org.python.antlr.ast.Str;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RainWaterHarvesting_Manipur extends RainWaterHarvesting {
	private static final String RULE_61 = "61(a)";
	/*
	 * private static final String RULE_51_DESCRIPTION =
	 * "RainWater Storage Arrangement "; private static final String
	 * RAINWATER_HARVESTING_TANK_CAPACITY =
	 * "Minimum capacity of Rain Water Harvesting Tank";
	 */
	private static final String RULE_51_DESCRIPTION = "Rain Water Harvesting";
	private static final String RAINWATER_HARVESTING_TANK_CAPACITY = "Minimum capacity of Rain Water Harvesting Tank";
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final String RWH_DECLARATION_ERROR = DxfFileConstants.RWH_DECLARED
			+ " in PLAN_INFO layer must be declared as YES for plot area greater than 100 sqm.";

	private static final String RAINWATER_HARVESTING = "Rainwater Harvesting";
	private static final String RAINWATER_HARVES_TANKCAPACITY = "Rainwater Harvest Tank Capacity";

	private static final Logger LOG = Logger.getLogger(RainWaterHarvesting_Manipur.class);

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		try {

			HashMap<String, String> errors = new HashMap<>();

			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, REQUIRED);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);
			scrutinyDetail.setKey("Common_Rain Water Harvesting");
			String subRule = RULE_61;
			String subRuleDesc = RULE_51_DESCRIPTION;
			String expected="";
			BigDecimal plotArea = pl.getPlot() != null ? pl.getPlot().getArea() : BigDecimal.ZERO;
			OccupancyTypeHelper mostRestrictiveFarHelper = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;

				if (mostRestrictiveFarHelper != null && mostRestrictiveFarHelper.getType() != null
						&& plotArea.compareTo(HUNDRED) >= 0 ) {
					expected = String.format(
							"Required for all buildings with plot area 100sqm or more",
							"");
						
					if (pl.getUtility() != null) {
						if (pl.getUtility().getRainWaterHarvest() != null
								&& !pl.getUtility().getRainWaterHarvest().isEmpty()) {
							setReportOutputDetails(pl, subRule, subRuleDesc, expected, "Defined in the plan",
									Result.Accepted.getResultVal());
						} else {
							pl.addError("RWH LAYER " + OBJECTNOTDEFINED, String.format("Rainwater Harvesting is %s %s",
									DcrConstants.OBJECTNOTDEFINED_DESC, expected));
							setReportOutputDetails(pl, subRule, subRuleDesc, expected, "Not Defined in the plan",
									Result.Not_Accepted.getResultVal());
						}
					}
				
					
				}

				

		} catch (Exception e) {
			// TODO: handle exception
		}
		return pl;
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
			String status) {

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	

	

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
