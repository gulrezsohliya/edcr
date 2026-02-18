package org.egov.client.edcr;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.feature.Basement;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Basement_Sikkim extends Basement {

	private static final Logger LOG = Logger.getLogger(Basement_Sikkim.class);

	@Override
	public Plan validate(Plan plan) {

		if (plan.getPlot() == null || plan.getPlot().getArea().compareTo(BigDecimal.ZERO) <= 0)
			return plan;

		HashMap<String, String> errors = new HashMap<>();

		Boolean isBasementPresent = false;
		if (plan.getBlocks() != null) {
			for (Block b : plan.getBlocks()) {
				if (b.getBuilding() != null && b.getBuilding().getFloors() != null
						&& !b.getBuilding().getFloors().isEmpty()) {

					for (Floor f : b.getBuilding().getFloors()) {

						if (f.getNumber() < 0) {
							isBasementPresent = true;
							break;
						}
					}
				}
				if (isBasementPresent) {
					
				}
			}
		}

		if (!errors.isEmpty())
			plan.addErrors(errors);

		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		validate(plan);
		return plan;
	}
}
