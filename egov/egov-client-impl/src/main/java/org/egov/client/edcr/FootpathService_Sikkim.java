package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.util.CharArrayMap.EntrySet;
import org.egov.common.entity.edcr.DistanceToExternalEntity;
import org.egov.common.entity.edcr.Footpath;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.ScrutinyDetail.ColumnHeadingDetail;
import org.egov.edcr.feature.FootpathService;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class FootpathService_Sikkim extends FootpathService {

	private static final Logger LOG = Logger.getLogger(FootpathService_Sikkim.class);
	private static final BigDecimal MINIMUM_DISTANCE_FOOTPATH_FROM_BUILDING_1_2 = BigDecimal.valueOf(1.2);
	private static final String RULE_DESC = "Minimum distance of footpath from building";
	private static final String FOOTPATH = "Footpath";
	private static final String RULE_FOOTPATH = "4";

	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		HashMap<String, String> errors = new HashMap<>();

		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Footpath");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, FOOTPATH);
		scrutinyDetail.addColumnHeading(3, DESCRIPTION);
		scrutinyDetail.addColumnHeading(4, REQUIRED);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);

		String ruleNo = StringUtils.EMPTY, ruleDesc = StringUtils.EMPTY;
		DistanceToExternalEntity distanceToExternalEntity = null;
		if(plan.getDistanceToExternalEntity()!=null)
			distanceToExternalEntity = plan.getDistanceToExternalEntity();
		
		BigDecimal minDistance = BigDecimal.ZERO;
		HashMap<Integer, String> fpath = new HashMap<>();
		HashMap<Integer, BigDecimal> minDistances = new HashMap<>();

		if (distanceToExternalEntity.getFootpath() != null) {
			Footpath footpaths = distanceToExternalEntity.getFootpath();
			if (!footpaths.getFootpaths().isEmpty() && footpaths.getFootpaths() != null) {
				for (Measurement f : footpaths.getFootpaths()) {
					fpath.put(f.getColorCode(), f.getName());
					System.out.println(fpath.toString());

				}
				if (!footpaths.getDistancesFromBuilding().isEmpty() && footpaths.getDistancesFromBuilding() != null) {
					for (Entry<Integer, List<BigDecimal>> k : footpaths.getDistancesFromBuilding().entrySet()) {
						minDistance = k.getValue().stream().reduce(BigDecimal::min).get();
						minDistances.put(k.getKey(), minDistance);
					}
					for (Entry<Integer, String> entry : fpath.entrySet()) {
						if(minDistances.get(entry.getKey())!=null) {
							if (minDistance.compareTo(minDistances.get(entry.getKey())) < 0)
								minDistance = minDistances.get(entry.getKey());
						}
						
					}

					if (minDistance.compareTo(MINIMUM_DISTANCE_FOOTPATH_FROM_BUILDING_1_2) >= 0)
						setReportOutputDetails(plan, scrutinyDetail, ruleNo, RULE_DESC, FOOTPATH,
								">= " + MINIMUM_DISTANCE_FOOTPATH_FROM_BUILDING_1_2.toString() + DcrConstants.IN_METER,
								minDistance.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
										DcrConstants.ROUNDMODE_MEASUREMENTS).toString() + DcrConstants.IN_METER,
								Result.Accepted.getResultVal());
					else
						setReportOutputDetails(plan, scrutinyDetail, ruleNo, RULE_DESC, FOOTPATH,
								">= " + MINIMUM_DISTANCE_FOOTPATH_FROM_BUILDING_1_2.toString() + DcrConstants.IN_METER,
								minDistance.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
										DcrConstants.ROUNDMODE_MEASUREMENTS).toString() + DcrConstants.IN_METER,
								Result.Accepted.getResultVal());

				} else {
					errors.put(
							String.format("%s distances from building %s", FOOTPATH,
									DcrConstants.OBJECTNOTDEFINED_DESC),
							String.format("%s distances from building %s", FOOTPATH,
									DcrConstants.OBJECTNOTDEFINED_DESC));
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
		try {
		validate(plan);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return plan;
	}

	public void setReportOutputDetails(Plan plan, ScrutinyDetail scrutinyDetail, String ruleNo, String ruleDesc,
			String foothpath, String expected, String actual, String status) {
		HashMap<String, String> details = new HashMap<String, String>();
		details.put(RULE_NO, RULE_FOOTPATH);
		details.put(FOOTPATH, foothpath);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

	}
}
