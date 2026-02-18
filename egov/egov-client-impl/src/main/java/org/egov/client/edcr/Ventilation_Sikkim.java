package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.MeasurementWithHeight;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.feature.Ventilation;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Ventilation_Sikkim extends Ventilation {

	private static final Logger LOG = Logger.getLogger(Ventilation_Sikkim.class);
	private static final String RULE_19 = "19";
	public static final String LIGHT_VENTILATION_DESCRIPTION = "Light and Ventilation";
	public static final BigDecimal MINIMUM_LIGHTANDVENTILATIONAREA_SK_25_00 = BigDecimal.valueOf(25.00);// percentage
	private static final String FLOOR = "Floor";

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

		HashMap<String, String> errors = new HashMap<String, String>();

		for (Block b : pl.getBlocks()) {

			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, FLOOR);
			scrutinyDetail.addColumnHeading(4, REQUIRED);
			scrutinyDetail.addColumnHeading(5, PROVIDED);
			scrutinyDetail.addColumnHeading(6, STATUS);

			if (b.getBuilding() != null && b.getBuilding().getFloors() != null
					&& !b.getBuilding().getFloors().isEmpty()) {

				scrutinyDetail.setKey("Block_" + b.getName() + "_Light And Ventilation");

				for (Floor f : b.getBuilding().getFloors()) {
					Map<String, String> details = new HashMap<>();
					details.put(RULE_NO, RULE_19);
					details.put(DESCRIPTION, LIGHT_VENTILATION_DESCRIPTION);
					details.put(FLOOR, String.format("%s %s", FLOOR, f.getNumber().toString()));
					details.put(REQUIRED,
							String.format("Minimum %s%% of floor area", MINIMUM_LIGHTANDVENTILATIONAREA_SK_25_00));

					if (f.getLightAndVentilation() != null && f.getLightAndVentilation().getMeasurements() != null
							&& !f.getLightAndVentilation().getMeasurements().isEmpty()) {

						BigDecimal totalVentilationArea = f.getLightAndVentilation().getMeasurements().stream()
								.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
						BigDecimal totalCarpetArea = f.getOccupancies().stream().map(Occupancy::getCarpetArea)
								.reduce(BigDecimal.ZERO, BigDecimal::add);
						BigDecimal totalFloorArea = f.getOccupancies().stream().map(Occupancy::getFloorArea)
								.reduce(BigDecimal.ZERO, BigDecimal::add);

						if (totalVentilationArea.compareTo(BigDecimal.ZERO) > 0) {

							/*
							 * BigDecimal totalVentilationAreaByTotalCarpetArea =
							 * totalVentilationArea.multiply(BigDecimal.valueOf(100))
							 * .divide(totalCarpetArea) .setScale(2, BigDecimal.ROUND_HALF_UP);
							 */
							BigDecimal totalVentilationAreaByTotalFloorArea = totalVentilationArea
									.multiply(BigDecimal.valueOf(100)).divide(totalFloorArea, BigDecimal.ROUND_HALF_UP);

							details.put(PROVIDED, String.format("%s%%", totalVentilationAreaByTotalFloorArea));

							if (MINIMUM_LIGHTANDVENTILATIONAREA_SK_25_00
									.compareTo(totalVentilationAreaByTotalFloorArea) <= 0) {

								details.put(STATUS, Result.Accepted.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							} else {
								details.put(STATUS, Result.Not_Accepted.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							}

						} else {
							details.put(PROVIDED, "Ventilation area not defined in floor  " + f.getNumber());
							details.put(STATUS, Result.Not_Accepted.getResultVal());
							scrutinyDetail.getDetail().add(details);
							pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							errors.put(
									String.format("BLK_%s_FLR_%s_%s area", b.getNumber(), f.getNumber(),
											LIGHT_VENTILATION_DESCRIPTION),
									String.format("BLK_%s_FLR_%s_%s area %s", b.getNumber(), f.getNumber(),
											LIGHT_VENTILATION_DESCRIPTION, DcrConstants.OBJECTNOTDEFINED_DESC));
							pl.addErrors(errors);
						}
					} else {
						/*
						 * LOG.info(LIGHT_VENTILATION_DESCRIPTION + " 2"); details.put(PROVIDED,
						 * "Ventilation area not defined in floor  " + f.getNumber());
						 * details.put(STATUS, Result.Not_Accepted.getResultVal());
						 * scrutinyDetail.getDetail().add(details);
						 * pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail); errors.put(
						 * String.format("BLK_%s_FLR_%s_%s area", b.getNumber(), f.getNumber(),
						 * LIGHT_VENTILATION_DESCRIPTION), String.format("BLK_%s_FLR_%s_%s area %s",
						 * b.getNumber(), f.getNumber(), LIGHT_VENTILATION_DESCRIPTION,
						 * DcrConstants.OBJECTNOTDEFINED_DESC)); pl.addErrors(errors);
						 */
					}

				}
			}

		}

		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
