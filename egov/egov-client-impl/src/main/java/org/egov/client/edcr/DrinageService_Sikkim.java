package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.DistanceToExternalEntity;
import org.egov.common.entity.edcr.Drinage;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.DrinageService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class DrinageService_Sikkim extends DrinageService {

	private static final Logger LOG = Logger.getLogger(DrinageService_Sikkim.class);

	private static final String JHORA = "JHORA";
	private static final String RULE_4 = "4";
	private static final String RULE_DESC = "Minimum distance of jhora from building";
	private static final BigDecimal MIN_DISTANCE_FROM_DRAIN_3_0 = BigDecimal.valueOf(3.0);

	@Override
	public Plan validate(Plan plan) {

		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Jhora");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(6, PROVIDED);
		scrutinyDetail.addColumnHeading(7, STATUS);

		HashMap<String, String> errors = new HashMap<>();

		BigDecimal minDistanceFromDrainageToBuilding = BigDecimal.valueOf(9999);

		DistanceToExternalEntity distanceToExternalEntity = null;
		if(plan.getDistanceToExternalEntity()!=null)
			distanceToExternalEntity = plan.getDistanceToExternalEntity();
		
		Drinage drinages = null;
		if(distanceToExternalEntity.getDrinage()!=null)
			drinages=distanceToExternalEntity.getDrinage();
		
		if (distanceToExternalEntity.getDrinage() != null) {
			if (!drinages.getDrinages().isEmpty() && drinages.getDrinages() != null) {
				BigDecimal minDist = BigDecimal.ZERO;

				if (!drinages.getDistancesFromBuilding().isEmpty() && drinages.getDistancesFromBuilding() != null
						&& drinages.getDistancesFromBuilding().size() > 0) {

					for (Entry<Integer, List<BigDecimal>> m : drinages.getDistancesFromBuilding().entrySet()) {
						minDist = m.getValue().stream().reduce(BigDecimal::min).get();
						if (minDistanceFromDrainageToBuilding.compareTo(minDist) > 0) {
							minDistanceFromDrainageToBuilding = minDist;
						}
					}

					if (minDistanceFromDrainageToBuilding.compareTo(MIN_DISTANCE_FROM_DRAIN_3_0) >= 0) {
						setReportOutputDetails(plan,
								minDistanceFromDrainageToBuilding.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
										DcrConstants.ROUNDMODE_MEASUREMENTS).toString() + DcrConstants.IN_METER,
								Result.Accepted.getResultVal(), scrutinyDetail);
					} else {
						setReportOutputDetails(plan,
								minDistanceFromDrainageToBuilding.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
										DcrConstants.ROUNDMODE_MEASUREMENTS).toString() + DcrConstants.IN_METER,
								Result.Not_Accepted.getResultVal(), scrutinyDetail);
					}

				} else {
					errors.put(JHORA,
							getLocaleMessage(DcrConstants.OBJECTNOTDEFINED, JHORA + " distance from building"));
					plan.addErrors(errors);
				}
			}
		}

		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		if (plan.getPlot() == null || (plan.getPlot() != null
				&& (plan.getPlot().getArea() == null || plan.getPlot().getArea().doubleValue() == 0))) {
			plan.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));

			return plan;
		}
		validate(plan);

		return plan;
	}

	private void setReportOutputDetails(Plan plan, String actual, String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, RULE_4);
		details.put(DESCRIPTION, RULE_DESC);
		details.put(PERMISSIBLE, String.format(">= %s%s", MIN_DISTANCE_FROM_DRAIN_3_0, DcrConstants.IN_METER));
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

	}
}
