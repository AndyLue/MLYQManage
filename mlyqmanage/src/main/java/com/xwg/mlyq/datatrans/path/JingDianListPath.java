package com.xwg.mlyq.datatrans.path;

import java.io.File;
import java.util.Random;

import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.xwg.mlyq.core.datatrans.BaseResource;

/**
 * 景点
 * 
 * @author Andy_Liu
 *
 */
@Path("jingdians")
@Component
public class JingDianListPath extends BaseResource {
    private static Logger LOGGER = LoggerFactory.getLogger(JingDianListPath.class);

//    ExpertService expertService = getContext().getBean(ExpertService.class);
//    
//    
//    @Override
//    public String getSavePath() {
//        return File.separator+"Pictures"+File.separator+"ExpertPictures";
//    }
    
    public String Select() throws Exception {
//        String recid = getReqValue(ExpertConst.FIELD_RECID);
//        JsonItem<YDYL_Expert> resultItem = new JsonItem<YDYL_Expert>();
//        if (!StringUtils.isEmpty(recid)) {
//            YDYL_Expert resultExpert = expertService.selectExpert(recid);
//            resultItem.setResultcode(ResultCode.OK.getCode());
//            resultItem.setValues(resultExpert);
//        } else {
//            resultItem.setResultcode(ResultCode.S_Internal_Server_Error.getCode());
//        }
//        String json = getJsonObjMapper().writeValueAsString(resultItem);
        return "[{\"name\":\"nihao1\"},{\"name\":\"nihao2\"}]";
    }



}
