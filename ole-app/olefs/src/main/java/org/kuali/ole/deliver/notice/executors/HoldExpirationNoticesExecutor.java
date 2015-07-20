package org.kuali.ole.deliver.notice.executors;

import org.kuali.ole.OLEConstants;
import org.kuali.ole.OLEParameterConstants;
import org.kuali.ole.deliver.bo.OLEDeliverNotice;
import org.kuali.ole.deliver.bo.OleDeliverRequestBo;
import org.kuali.ole.deliver.notice.bo.OleNoticeContentConfigurationBo;
import org.kuali.ole.deliver.notice.bo.OleNoticeFieldLabelMapping;
import org.kuali.ole.deliver.notice.noticeFormatters.RequestEmailContentFormatter;
import org.kuali.ole.deliver.notice.noticeFormatters.RequestExpirationEmailContentFormatter;
import org.kuali.ole.deliver.service.OleDeliverRequestDocumentHelperServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maheswarang on 6/24/15.
 */
public class HoldExpirationNoticesExecutor extends RequestNoticesExecutor {

    public HoldExpirationNoticesExecutor(List<OLEDeliverNotice> deliverNotices) {
        super(deliverNotices);
    }

    public OleDeliverRequestDocumentHelperServiceImpl getOleDeliverRequestHelperService(){
        return new OleDeliverRequestDocumentHelperServiceImpl();
    }

    @Override
    public RequestEmailContentFormatter getRequestEmailContentFormatter() {
        if(requestEmailContentFormatter == null){
        requestEmailContentFormatter =  new RequestExpirationEmailContentFormatter();
        }return requestEmailContentFormatter;
        }

    @Override
    public boolean isValidRequestToSendNotice(OleDeliverRequestBo oleDeliverRequestBo) {
        return true;
    }

    @Override
    protected void postProcess() {
        getOleDeliverRequestHelperService().deleteExpiredOnHoldNotices(deliverRequestBos);
        //To do create the request history record and delete the record from the request table
    }

    @Override
    public void populateFieldLabelMapping() {
        List<OleNoticeContentConfigurationBo> oleNoticeContentConfigurationBoList = null;
        Map<String,String> noticeConfigurationMap = new HashMap<String,String>();
        noticeConfigurationMap.put("noticeType",OLEConstants.ONHOLD_EXPIRATION_NOTICE);
        oleNoticeContentConfigurationBoList= (List<OleNoticeContentConfigurationBo>)getBusinessObjectService().findMatching(OleNoticeContentConfigurationBo.class,noticeConfigurationMap);
        if(oleNoticeContentConfigurationBoList!=null && oleNoticeContentConfigurationBoList.size()>0){
            if(oleNoticeContentConfigurationBoList.get(0)!=null){
                fieldLabelMap.put("noticeTitle",oleNoticeContentConfigurationBoList.get(0).getNoticeTitle());
                fieldLabelMap.put("noticeBody",oleNoticeContentConfigurationBoList.get(0).getNoticeBody());
                fieldLabelMap.put("noticeSubjectLine",oleNoticeContentConfigurationBoList.get(0).getNoticeSubjectLine());
                if(oleNoticeContentConfigurationBoList.get(0).getOleNoticeFieldLabelMappings()!=null && oleNoticeContentConfigurationBoList.get(0).getOleNoticeFieldLabelMappings().size()>0){
                    for(OleNoticeFieldLabelMapping oleNoticeFieldLabelMapping : oleNoticeContentConfigurationBoList.get(0).getOleNoticeFieldLabelMappings()){
                        fieldLabelMap.put(oleNoticeFieldLabelMapping.getFieldLabel(),oleNoticeFieldLabelMapping.getFieldName());
                    }
                }
            }
        }else{
            fieldLabelMap.put("noticeTitle",getTitle());
            fieldLabelMap.put("noticeBody",getBody());
        }
    }

    @Override
    public String getTitle() {
        String title = getParameterResolverInstance().getParameter(OLEConstants.APPL_ID, OLEConstants
                .DLVR_NMSPC, OLEConstants.DLVR_CMPNT,
                OLEParameterConstants
                        .EXPIRED_TITLE);
        return title;
    }

    @Override
    public String getBody() {
        String body = getParameterResolverInstance().getParameter(OLEConstants.APPL_ID_OLE, OLEConstants
                .DLVR_NMSPC, OLEConstants.DLVR_CMPNT, OLEConstants.OleDeliverRequest.EXP_HOLD_NOTICE_CONTENT);
        return body;
    }


}