package org.egov.client.edcr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.feature.GeneralStair;
import org.springframework.stereotype.Service;

@Service
public class GeneralStair_Sikkim extends GeneralStair{

	private static final Logger LOG = Logger.getLogger(GeneralStair_Sikkim.class);
	
	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		
		if(plan.getErrors() != null && !plan.getErrors().isEmpty()) {
			
			String stairErrorKey = "BLK_%s_FLR_%s_STAIR";
			Set<String> stairErrorKeys = new HashSet<String>();
			
			if(plan.getBlocks() != null && !plan.getBlocks().isEmpty()) {
				for(Block bl : plan.getBlocks()) {
					if(bl.getBuilding() != null && bl.getBuilding().getFloors() != null && !bl.getBuilding().getFloors().isEmpty()) {
						for(Floor fl : bl.getBuilding().getFloors()) {
							stairErrorKeys.add(String.format(stairErrorKey, bl.getNumber(), fl.getNumber()));
						}
					}
				}
			}
			
			for(String t: stairErrorKeys) {
				/* LOG.info(plan.getErrors().get("BLK_1_LVL_0_FRONT_SETBACK")); */
				plan.getErrors().entrySet().removeIf(entry -> entry.getKey().contains(t));
			}
		}
		
		LOG.info("DECENT_SIKKIM GeneralStair: PRCOCESS");
		// TODO Auto-generated method stub
		return plan;
	}
}
