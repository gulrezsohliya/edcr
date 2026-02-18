package org.egov.client.edcr;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.feature.NorthDirection;
import org.springframework.stereotype.Service;

@Service
public class NorthDirection_Manipur extends NorthDirection{

	private static final Logger LOG = Logger.getLogger(NorthDirection_Manipur.class);
	
	@Override
	public Plan validate(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		// TODO Auto-generated method stub
		return plan;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// TODO Auto-generated method stub
		return super.getAmendments();
	}
}
