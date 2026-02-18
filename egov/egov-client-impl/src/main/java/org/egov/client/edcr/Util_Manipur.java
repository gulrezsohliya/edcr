package org.egov.client.edcr;

import static org.egov.client.constants.DxfFileConstants_AR.B;
import static org.egov.client.constants.DxfFileConstants_AR.B1;
import static org.egov.client.constants.DxfFileConstants_AR.C;
import static org.egov.client.constants.DxfFileConstants_AR.C1;
import static org.egov.client.constants.DxfFileConstants_AR.E;
import static org.egov.client.constants.DxfFileConstants_AR.E1;
import static org.egov.client.constants.DxfFileConstants_AR.H;
import static org.egov.client.constants.DxfFileConstants_AR.H1;
import static org.egov.client.constants.DxfFileConstants_AR.HZ;
import static org.egov.client.constants.DxfFileConstants_AR.HZ1;
import static org.egov.client.constants.DxfFileConstants_AR.I;
import static org.egov.client.constants.DxfFileConstants_AR.I1;
import static org.egov.client.constants.DxfFileConstants_AR.IN;
import static org.egov.client.constants.DxfFileConstants_AR.IN1;
import static org.egov.client.constants.DxfFileConstants_AR.OF;
import static org.egov.client.constants.DxfFileConstants_AR.OF1;
import static org.egov.client.constants.DxfFileConstants_AR.PS;
import static org.egov.client.constants.DxfFileConstants_AR.PS1;
import static org.egov.client.constants.DxfFileConstants_AR.PS2;
import static org.egov.client.constants.DxfFileConstants_AR.PS3;
import static org.egov.client.constants.DxfFileConstants_AR.R;
import static org.egov.client.constants.DxfFileConstants_AR.R1;
import static org.egov.client.constants.DxfFileConstants_AR.R2;
import static org.egov.client.constants.DxfFileConstants_AR.RS;
import static org.egov.client.constants.DxfFileConstants_AR.RS1;
import static org.egov.client.constants.DxfFileConstants_AR.S;
import static org.egov.client.constants.DxfFileConstants_AR.S1;
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

public class Util_Manipur {

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
		 if (codes.contains(HZ1))
				return codesMap.get(HZ1);
			else if (codes.contains(HZ))
				return codesMap.get(HZ);
			else if (codes.contains(S1))
				return codesMap.get(S1);
			else if (codes.contains(S))
				return codesMap.get(S);
			else if (codes.contains(I1))
				return codesMap.get(I1);
			else if (codes.contains(I))
				return codesMap.get(I);
			else if (codes.contains(IN1))
				return codesMap.get(IN1);
			else if (codes.contains(IN))
				return codesMap.get(IN);
			else if (codes.contains(E1))
				return codesMap.get(E1);
			else if (codes.contains(E))
				return codesMap.get(E);
			else if (codes.contains(RS1))
				return codesMap.get(RS1);
			else if (codes.contains(RS))
				return codesMap.get(RS);
			else if (codes.contains(H1))
				return codesMap.get(H1);
			else if (codes.contains(H))
				return codesMap.get(H);
			else if (codes.contains(OF1))
				return codesMap.get(OF1);
			else if (codes.contains(OF))
				return codesMap.get(OF);
			else if (codes.contains(B1))
				return codesMap.get(B1);
			else if (codes.contains(B))
				return codesMap.get(B);
			else if (codes.contains(PS3))
				return codesMap.get(PS3);
			else if (codes.contains(PS2))
				return codesMap.get(PS2);
			else if (codes.contains(PS1))
				return codesMap.get(PS1);
			else if (codes.contains(PS))
				return codesMap.get(PS);
			else if (codes.contains(C1))
				return codesMap.get(C1);
			else if (codes.contains(C))
				return codesMap.get(C);
			else if (codes.contains(R2))
				return codesMap.get(R2);
			else if (codes.contains(R1))
				return codesMap.get(R1);
			else if (codes.contains(R))
				return codesMap.get(R);
			else
				return null;
	}

}
