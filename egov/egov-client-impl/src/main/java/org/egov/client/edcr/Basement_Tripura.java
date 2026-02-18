package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Basement;
import org.springframework.stereotype.Service;

@Service
public class Basement_Tripura extends Basement {
	private static final String RULE_46_6A = "66";
	private static final String RULE_46_6C = "66";
	public static final String BASEMENT_DESCRIPTION_ONE = "Height from the floor to the soffit of the roof slab or ceiling";
	public static final String BASEMENT_DESCRIPTION_TWO = "Minimum height of the ceiling of upper basement above ground level";

	private static final Logger LOG = Logger.getLogger(Basement_Tripura.class);

	@Override
	public Plan validate(Plan pl) {
		try {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Basement");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, REQUIRED);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);

			Map<String, String> details = new HashMap<>();

			BigDecimal minLength = BigDecimal.ZERO;

			if (pl.getBlocks() != null) {
				for (Block b : pl.getBlocks()) {
					if (b.getBuilding() != null && b.getBuilding().getFloors() != null
							&& !b.getBuilding().getFloors().isEmpty()) {

						for (Floor f : b.getBuilding().getFloors()) {

							if (f.getNumber() <= -1) {

								if (f.getHeightFromTheFloorToCeiling() != null
										&& !f.getHeightFromTheFloorToCeiling().isEmpty()) {

									minLength = f.getHeightFromTheFloorToCeiling().stream().reduce(BigDecimal::min)
											.get();

									if (minLength.compareTo(BigDecimal.valueOf(2.4)) >= 0) {
										details.put(RULE_NO, RULE_46_6A);
										details.put(DESCRIPTION, BASEMENT_DESCRIPTION_ONE);
										details.put(REQUIRED, ">= 2.4");
										details.put(PROVIDED, minLength.toString());
										details.put(STATUS, Result.Accepted.getResultVal());
										scrutinyDetail.getDetail().add(details);

									} else {
										details = new HashMap<>();
										details.put(RULE_NO, RULE_46_6A);
										details.put(DESCRIPTION, BASEMENT_DESCRIPTION_ONE);
										details.put(REQUIRED, ">= 2.4");
										details.put(PROVIDED, minLength.toString());
										details.put(STATUS, Result.Not_Accepted.getResultVal());
										scrutinyDetail.getDetail().add(details);
									}
								}else {
									pl.addError("BasementFloortoCeilingHeightError"+f.getNumber(), "Please define basement height from floor to ceiling");
								}
								minLength = BigDecimal.ZERO;
								if (f.getHeightOfTheCeilingOfUpperBasement() != null
										&& !f.getHeightOfTheCeilingOfUpperBasement().isEmpty()) {

									minLength = f.getHeightOfTheCeilingOfUpperBasement().stream()
											.reduce(BigDecimal::min).get();

									if (minLength.compareTo(BigDecimal.valueOf(0.9)) >= 0
											&& minLength.compareTo(BigDecimal.valueOf(1.2)) <= 0) {
										details = new HashMap<>();
										details.put(RULE_NO, RULE_46_6C);
										details.put(DESCRIPTION, BASEMENT_DESCRIPTION_TWO);
										details.put(REQUIRED, "Between 0.9 to 1.2");
										details.put(PROVIDED, minLength.toString());
										details.put(STATUS, Result.Accepted.getResultVal());
										scrutinyDetail.getDetail().add(details);

									} else {
										details = new HashMap<>();
										details.put(RULE_NO, RULE_46_6C);
										details.put(DESCRIPTION, BASEMENT_DESCRIPTION_TWO);
										details.put(REQUIRED, "Between 0.9 to 1.2");
										details.put(PROVIDED, minLength.toString());
										details.put(STATUS, Result.Not_Accepted.getResultVal());
										scrutinyDetail.getDetail().add(details);
									}
								}else {
									pl.addError("BasementCeilingUpperError"+f.getNumber(), "Please define basement height of ceiling of upper basement");
								}

								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							}
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
	public Plan process(Plan plan) {
		validate(plan);
		return plan;
	}
}
