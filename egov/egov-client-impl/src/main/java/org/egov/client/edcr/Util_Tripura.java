package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.*;
import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.A2;
import static org.egov.edcr.constants.DxfFileConstants.A_FH;
import static org.egov.edcr.constants.DxfFileConstants.A_SA;
import static org.egov.edcr.constants.DxfFileConstants.D_A;
import static org.egov.edcr.constants.DxfFileConstants.D_B;
import static org.egov.edcr.constants.DxfFileConstants.D_C;
import static org.egov.edcr.constants.DxfFileConstants.E_CLG;
import static org.egov.edcr.constants.DxfFileConstants.E_EARC;
import static org.egov.edcr.constants.DxfFileConstants.E_NS;
import static org.egov.edcr.constants.DxfFileConstants.E_PS;
import static org.egov.edcr.constants.DxfFileConstants.E_SACA;
import static org.egov.edcr.constants.DxfFileConstants.E_SFDAP;
import static org.egov.edcr.constants.DxfFileConstants.E_SFMC;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.H_PP;
import static org.egov.edcr.constants.DxfFileConstants.M_DFPAB;
import static org.egov.edcr.constants.DxfFileConstants.M_HOTHC;
import static org.egov.edcr.constants.DxfFileConstants.M_NAPI;
import static org.egov.edcr.constants.DxfFileConstants.M_OHF;
import static org.egov.edcr.constants.DxfFileConstants.M_VH;
import static org.egov.edcr.constants.DxfFileConstants.S_BH;
import static org.egov.edcr.constants.DxfFileConstants.S_CA;
import static org.egov.edcr.constants.DxfFileConstants.S_CRC;
import static org.egov.edcr.constants.DxfFileConstants.S_ECFG;
import static org.egov.edcr.constants.DxfFileConstants.S_ICC;
import static org.egov.edcr.constants.DxfFileConstants.S_MCH;
import static org.egov.edcr.constants.DxfFileConstants.S_SAS;
import static org.egov.edcr.constants.DxfFileConstants.S_SC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;

public class Util_Tripura {

	protected static OccupancyTypeHelper getMostRestrictive(Plan pl) {
		
		Set<OccupancyTypeHelper> distinctOccupancyTypes=pl.getVirtualBuilding().getOccupancyTypes();
		
		Set<String> codes = new HashSet<>();
		Map<String, OccupancyTypeHelper> codesMap = new HashMap<>();
		for (OccupancyTypeHelper typeHelper : distinctOccupancyTypes) {

			if (typeHelper.getType() != null)
				codesMap.put(typeHelper.getType().getCode(), typeHelper);
			if (typeHelper.getSubtype() != null)
				codesMap.put(typeHelper.getSubtype().getCode(), typeHelper);
		}
		codes = codesMap.keySet();
		if (codes.contains(IN1a))
			return codesMap.get(IN1a);
		else if (codes.contains(B1a))
			return codesMap.get(B1a);
		else if (codes.contains(A1c))
			return codesMap.get(A1c);
		else if (codes.contains(A1b))
			return codesMap.get(A1b);
		else if (codes.contains(A1a))
			return codesMap.get(A1a);
		else if (codes.contains(I1a))
			return codesMap.get(I1a);
		else if (codes.contains(E1a))
			return codesMap.get(E1a);
		else if (codes.contains(R1b))
			return codesMap.get(R1b);
		else if (codes.contains(R1a))
			return codesMap.get(R1a);
		else
			return null;

	}

}
