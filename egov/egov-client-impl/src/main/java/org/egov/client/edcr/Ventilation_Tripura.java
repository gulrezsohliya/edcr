package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class Ventilation_Tripura extends Ventilation {


	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		try {
//			HashMap<String, String> errors = new HashMap<String, String>();
//			for (Block b : pl.getBlocks()) {
//				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//				scrutinyDetail.setKey("Common_Ventilation");
//				scrutinyDetail.addColumnHeading(1, RULE_NO);
//				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//				scrutinyDetail.addColumnHeading(3, "Ventilation Area Required");
//				scrutinyDetail.addColumnHeading(4, "Ventilation Area Provided");
//				scrutinyDetail.addColumnHeading(5, "Ventilation Width Required");
//				scrutinyDetail.addColumnHeading(6, "Ventilation Width Provided");
//				scrutinyDetail.addColumnHeading(7, STATUS);
//				BigDecimal reqVenArea=null;
//				BigDecimal reqVenWidth=null;
//				Boolean status=false;
//				
//				if (b.getBuilding() != null && b.getBuilding().getFloors() != null
//						&& !b.getBuilding().getFloors().isEmpty()) {
//
//					for (Floor f : b.getBuilding().getFloors()) {
//						Map<String, String> details = new HashMap<>();
//						details.put(RULE_NO, "TBD");
//						details.put(DESCRIPTION, LIGHT_VENTILATION_DESCRIPTION);
//
//						if (f.getLightAndVentilation() != null && f.getLightAndVentilation().getMeasurements() != null
//								&& !f.getLightAndVentilation().getMeasurements().isEmpty()) {
//
//							BigDecimal totalVentilationArea = f.getLightAndVentilation().getMeasurements().stream()
//									.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
//							BigDecimal totalVentilationWidth = f.getLightAndVentilation().getMeasurements().stream()
//									.map(Measurement::getWidth).reduce(BigDecimal.ZERO, BigDecimal::add);
//							
//							if(b.getBuilding().getHeight().compareTo(BigDecimal.valueOf(11))<=0) {
//								reqVenArea=BigDecimal.valueOf(1.5);
//								reqVenWidth=BigDecimal.valueOf(1);
//								if(totalVentilationArea.compareTo(reqVenArea)>=0 
//								   && totalVentilationWidth.compareTo(reqVenWidth)>=0)
//									status=true;
//							}else if(b.getBuilding().getHeight().compareTo(BigDecimal.valueOf(11))>0
//									&& b.getBuilding().getHeight().compareTo(BigDecimal.valueOf(14.5))>0) {
//								reqVenArea=BigDecimal.valueOf(3);
//								reqVenWidth=BigDecimal.valueOf(1.2);
//								if(totalVentilationArea.compareTo(reqVenArea)>=0 
//								   && totalVentilationWidth.compareTo(reqVenWidth)>=0)
//									status=true;
//							}
//							if (totalVentilationArea.compareTo(BigDecimal.ZERO) > 0) {
//								
//									details.put("Ventilation Area Required", reqVenArea.toString());
//									details.put("Ventilation Area Provided", totalVentilationArea.setScale(2, RoundingMode.HALF_UP).toString());
//									details.put("Ventilation Width Required", reqVenWidth.toString());
//									details.put("Ventilation Width Provided", totalVentilationWidth.setScale(2, RoundingMode.HALF_UP).toString());
//									if(status)
//										details.put(STATUS, Result.Accepted.getResultVal());
//									else
//										details.put(STATUS, Result.Not_Accepted.getResultVal());
//									scrutinyDetail.getDetail().add(details);
//									pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//							} 
//							
//							
//						}else {
//							errors.put("VentilationError", "Ventilation Not Defined");
//							pl.addErrors(errors);
//							
//						}
//						
//
//					}
//					
//				}
//				
//
//			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		

		return pl;
	}
	
	

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
