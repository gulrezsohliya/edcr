package org.egov.client.edcr;


import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.EdcrPdfDetail;
import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.feature.DxfToPdfConverter;
import org.springframework.stereotype.Service;

@Service
public class DxfToPdfConverter_Tripura extends DxfToPdfConverter {

	private static final Logger LOG = Logger.getLogger(DxfToPdfConverter_Tripura.class);



	@Override
	public Plan validate(Plan pl) {
		try {
			for(EdcrPdfDetail pdfDetail : pl.getEdcrPdfDetails()) {
				
				System.out.println("Here"+pdfDetail.getConvertedPdf());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		

		return pl;
	}

	

	@Override
	public Plan process(Plan Plan) {
		try {
			validate(Plan);
		} catch (Exception e) {
		}
		return Plan;
	}

}
