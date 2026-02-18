
package org.egov.client.edcr;

import static org.egov.edcr.utility.DcrConstants.HEIGHTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.WRONGHEIGHTDEFINED;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.feature.SetBackService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetBackService_Manipur extends SetBackService {
	private static final Logger LOG = Logger.getLogger(SetBackService_Manipur.class);
    @Autowired
    private FrontYardService_Manipur frontYardService;

    @Autowired
    private SideYardService_Manipur sideYardService;

    @Autowired
    private RearYardService_Manipur rearYardService;

    @Override
    public Plan validate(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();
        BigDecimal heightOfBuilding = BigDecimal.ZERO;
        for (Block block : pl.getBlocks()) {
            int i = 0;
            if (!block.getCompletelyExisting()) {
                for (SetBack setback : block.getSetBacks()) {
                    i++;
                    if (setback.getLevel() >= 0) {
                        // for level 0, all the yards are mandatory. Else throw error.
                        if (setback.getFrontYard() == null)
                            errors.put("frontyardNodeDefined",
                                    getLocaleMessage(OBJECTNOTDEFINED, " SetBack of block" + block.getName() + "  at level zero "));
                        if (setback.getRearYard() == null
                                && !pl.getPlanInformation().getNocToAbutRearDesc().equalsIgnoreCase(DcrConstants.YES))
                            errors.put("rearyardNodeDefined",
                                    getLocaleMessage(OBJECTNOTDEFINED, " Rear Setback of block" + block.getName() + "  at level zero "));
                        if (setback.getSideYard1() == null)
                            errors.put("side1yardNodeDefined", getLocaleMessage(OBJECTNOTDEFINED,
                                    " Side Setback 1 of block " + block.getName() + " at level zero"));
                        if (setback.getSideYard2() == null
                                && !pl.getPlanInformation().getNocToAbutSideDesc().equalsIgnoreCase(DcrConstants.YES))
                            errors.put("side2yardNodeDefined", getLocaleMessage(OBJECTNOTDEFINED,
                                    " Side Setback 2 of block " + block.getName() + " at level zero "));
                    } 


                }
            }
        }
        if (errors.size() > 0)
            pl.addErrors(errors);

        return pl;
    }

    @Override
    public Plan process(Plan pl) {
    	try {
            validate(pl);
            
    			frontYardService.processFrontYard(pl);
    			rearYardService.processRearYard(pl);
    			sideYardService.processSideYard(pl);
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
