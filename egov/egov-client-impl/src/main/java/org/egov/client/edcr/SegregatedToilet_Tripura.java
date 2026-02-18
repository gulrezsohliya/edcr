package org.egov.client.edcr;
import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.feature.SegregatedToilet;
import org.springframework.stereotype.Service;

import static org.egov.client.constants.DxfFileConstants_AR.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.infra.utils.StringUtils;

@Service
public class SegregatedToilet_Tripura extends SegregatedToilet{
	private static final Logger LOG = Logger.getLogger(SegregatedToilet.class);
    private static final String RULE_59_10  = "106(7)";
    public static final String SEGREGATEDTOILET_DESCRIPTION = "Num. of segregated toilets";
    public static final String SEGREGATEDTOILET_DIMENSION_DESCRIPTION = "Segregated toilet distance from main entrance";
	@Override
	public Plan process(Plan pl) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_Segregated Toilet");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);

        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_59_10);

        BigDecimal minDimension = BigDecimal.ZERO;
        BigDecimal maxHeightOfBuilding = BigDecimal.ZERO;
        BigDecimal maxNumOfFloorsOfBuilding = BigDecimal.ZERO;

        if (pl.getSegregatedToilet() != null && !pl.getSegregatedToilet().getDistancesToMainEntrance().isEmpty())
            minDimension = pl.getSegregatedToilet().getDistancesToMainEntrance().stream().reduce(BigDecimal::min).get();

        for (Block b : pl.getBlocks()) {
            if (b.getBuilding().getBuildingHeight() != null) {
                if (b.getBuilding() != null && b.getBuilding().getBuildingHeight().compareTo(maxHeightOfBuilding) > 0) {
                    maxHeightOfBuilding = b.getBuilding().getBuildingHeight();
                }
                if (b.getBuilding().getFloorsAboveGround() != null
                        && b.getBuilding().getFloorsAboveGround().compareTo(maxNumOfFloorsOfBuilding) > 0) {
                    maxNumOfFloorsOfBuilding = b.getBuilding().getFloorsAboveGround();
                }
            }
        }

//        if (pl.getVirtualBuilding() != null && pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null
//                && pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType() != null &&
//                !R.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {

//            if (pl.getSegregatedToilet() != null && pl.getSegregatedToilet().getSegregatedToilets() != null
//                    && !pl.getSegregatedToilet().getSegregatedToilets().isEmpty()) {
//                details.put(DESCRIPTION, SEGREGATEDTOILET_DESCRIPTION);
//                details.put(REQUIRED, "1");
//                details.put(PROVIDED, String.valueOf(pl.getSegregatedToilet().getSegregatedToilets().size()));
//                details.put(STATUS, Result.Accepted.getResultVal());
//                scrutinyDetail.getDetail().add(details);
//                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//            } else {
//                details.put(DESCRIPTION, SEGREGATEDTOILET_DESCRIPTION);
//                details.put(REQUIRED, "1");
//                details.put(PROVIDED, "0");
//                details.put(STATUS, Result.Not_Accepted.getResultVal());
//                scrutinyDetail.getDetail().add(details);
//                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//            }

//            if (minDimension != null && minDimension.compareTo(new BigDecimal(200)) >= 0) {
//                details.put(DESCRIPTION, SEGREGATEDTOILET_DIMENSION_DESCRIPTION);
//                details.put(REQUIRED, ">= 200");
//                details.put(PROVIDED, minDimension.toString());
//                details.put(STATUS, Result.Accepted.getResultVal());
//                scrutinyDetail.getDetail().add(details);
//                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//            } else {
//                details.put(DESCRIPTION, SEGREGATEDTOILET_DIMENSION_DESCRIPTION);
//                details.put(REQUIRED, ">= 200");
//                details.put(PROVIDED, minDimension.toString());
//                details.put(STATUS, Result.Not_Accepted.getResultVal());
//                scrutinyDetail.getDetail().add(details);
//                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//            }

//        }

        return pl;
	}

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}
}
