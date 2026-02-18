package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.egov.client.constants.DxfFileConstants_AR.*;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Plantation;
import org.springframework.stereotype.Service;
import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;

@Service
public class Plantation_Tripura extends Plantation {

	private static final Logger LOGGER = Logger.getLogger(Plantation_Tripura.class);

	private static final String RULE_113_1 = "113(1)";
	public static final String PLANTATION_TREECOVER_DESCRIPTION = "Plantation tree cover";
	public static final String MINTREE = "Minimum No of Trees To be planted";
	public static final String PLOTAREA = "Plot Area";

	@Override
	public Plan validate(Plan plan) {
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		validate(pl);
		try {
			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Plantation");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, PLOTAREA);
			scrutinyDetail.addColumnHeading(4, MINTREE);
			scrutinyDetail.addColumnHeading(5, REQUIRED);
			scrutinyDetail.addColumnHeading(6, PROVIDED);
			scrutinyDetail.addColumnHeading(7, STATUS);
			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, RULE_113_1);
			details.put(DESCRIPTION, PLANTATION_TREECOVER_DESCRIPTION);

			BigDecimal totalArea = BigDecimal.ZERO;
			BigDecimal plotArea = BigDecimal.ZERO;
			BigDecimal plantationPer = BigDecimal.ZERO;
			String type = "";
			String subType = "";
			if (pl.getPlantation() != null && pl.getPlantation().getPlantations() != null
					&& !pl.getPlantation().getPlantations().isEmpty()) {
				for (Measurement m : pl.getPlantation().getPlantations()) {
					totalArea = totalArea.add(m.getArea());
				}

				if (pl.getPlot() != null)
					plotArea = pl.getPlot().getArea();

				if (pl.getVirtualBuilding() != null && pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null
						&& pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype() != null) {
					type = pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode();
					subType = pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getCode();
				}
				details.put(PLOTAREA, plotArea.toString());
				if (type.equalsIgnoreCase(R) || type.equalsIgnoreCase(B) || type.equalsIgnoreCase(IN)) {
					if (totalArea.intValue() > 0 && plotArea != null && plotArea.intValue() > 0)
						plantationPer = totalArea.divide(plotArea, DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS);
					if (plotArea.compareTo(BigDecimal.valueOf(150)) > 0
							&& plotArea.compareTo(BigDecimal.valueOf(500)) <= 0) {
						details.put(MINTREE, "One tree for every 100 sqmt plot area or part thereof beyond 150 sqmt");
						if (plantationPer.compareTo(new BigDecimal("0.01")) < 0) {
							details.put(REQUIRED, ">= 1%");
							details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
							details.put(STATUS, Result.Not_Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
						} else {
							details.put(REQUIRED, ">= 1%");
							details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
							details.put(STATUS, Result.Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
						}
					} else if (plotArea.compareTo(BigDecimal.valueOf(500)) > 0
							&& plotArea.compareTo(BigDecimal.valueOf(20000)) <= 0) {
						details.put(MINTREE, "One tree for every 200 sqmt plot area or part thereof ");
						if (plantationPer.compareTo(new BigDecimal("0.025")) < 0) {
							details.put(REQUIRED, ">= 2.5%");
							details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
							details.put(STATUS, Result.Not_Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
						} else {
							details.put(REQUIRED, ">= 2.5%");
							details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
							details.put(STATUS, Result.Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
						}
					} else if (plotArea.compareTo(BigDecimal.valueOf(20000)) > 0
							&& plotArea.compareTo(BigDecimal.valueOf(50000)) <= 0) {
						details.put(MINTREE, "One tree for every 200 sqmt plot area or part thereof  ");
						if (plantationPer.compareTo(new BigDecimal("0.05")) < 0) {
							details.put(REQUIRED, ">= 5%");
							details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
							details.put(STATUS, Result.Not_Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
						} else {
							details.put(REQUIRED, ">= 5%");
							details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
							details.put(STATUS, Result.Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
						}
					} else if (plotArea.compareTo(BigDecimal.valueOf(50000)) > 0) {
						details.put(MINTREE, "One tree for every 200 sqmt plot area or part thereof  ");
						if (plantationPer.compareTo(new BigDecimal("0.75")) < 0) {
							details.put(REQUIRED, ">= 7.5%");
							details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
							details.put(STATUS, Result.Not_Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
						} else {
							details.put(REQUIRED, ">= 7.5%");
							details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
							details.put(STATUS, Result.Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
						}
					}
				}

			}
		} catch (Exception e) {
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
