package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.SepticTank;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class SepticTank_Tripura extends SepticTank {

	private static final Logger LOG = Logger.getLogger(SepticTank_Tripura.class);

	private static final String DECLARED = "Declared";
	private static final String RULE_4_6_i_2_e_8_25 = "4, 6(i) 2(e), 8 & 25";

	@Override
	public Plan validate(Plan plan) {

		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Septic Tank ");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, PERMITTED);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);
			List<org.egov.common.entity.edcr.SepticTank> septicTanks = null;
			Boolean septicExist = false;
			OccupancyTypeHelper mostRestrictive = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
			if (!pl.getSepticTanks().isEmpty() && pl.getSepticTanks() != null) {
				septicExist = true;
				septicTanks = pl.getSepticTanks();

				
				for (org.egov.common.entity.edcr.SepticTank septicTank : septicTanks) {
					boolean validWaterSrcDistance = false;
					boolean validBuildingDistance = false;

					if (septicTank != null) {
						if (septicTank.getDistanceFromWaterSource() != null
								&& !septicTank.getDistanceFromWaterSource().isEmpty()) {
							for (BigDecimal db : septicTank.getDistanceFromWaterSource()) {
								BigDecimal minDistWaterSrc = septicTank.getDistanceFromWaterSource().stream()
										.reduce(BigDecimal::min).get();
								if (minDistWaterSrc != null
										&& minDistWaterSrc.compareTo(BigDecimal.valueOf(0.25)) >= 0) {
									validWaterSrcDistance = true;
								}
								buildResult(pl, scrutinyDetail, validWaterSrcDistance, "Distance from plot boundary",
										">= 0.25", minDistWaterSrc.toString());
							}
						} else {
							pl.addError("Distance from plot boundary Error",
									"Distance from plot boundary for Septic Tank not defined in the plan");
						}

						if (septicTank.getDistanceFromBuilding() != null
								&& !septicTank.getDistanceFromBuilding().isEmpty()) {
							for (BigDecimal db : septicTank.getDistanceFromBuilding()) {
								BigDecimal minDistBuilding = septicTank.getDistanceFromBuilding().stream()
										.reduce(BigDecimal::min).get();
								if (minDistBuilding != null
										&& minDistBuilding.compareTo(BigDecimal.valueOf(0.75)) <= 0) {
									validBuildingDistance = true;
								}
								buildResult(pl, scrutinyDetail, validBuildingDistance,
										"Projection from building footprint", "<= 0.75", minDistBuilding.toString());
							}
						} else {
							pl.addError("Projection from building footprint Error",
									"Projection from building footprint for Septic Tank not defined in the plan");
						}

					}
				}

			}
			if (!septicExist) {
				if (mostRestrictive.getConvertedType().getCode().equalsIgnoreCase(R)
						|| mostRestrictive.getConvertedType().getCode().equalsIgnoreCase(B)) {
					pl.addError("Septic Tank Error", "Septic Tank not defined in the plan");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return pl;
	}

	private void buildResult(Plan pl, ScrutinyDetail scrutinyDetail, boolean valid, String description, String permited,
			String provided) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, "-");
		details.put(DESCRIPTION, description);
		details.put(PERMITTED, permited);
		details.put(PROVIDED, provided);
		details.put(STATUS, valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

}
