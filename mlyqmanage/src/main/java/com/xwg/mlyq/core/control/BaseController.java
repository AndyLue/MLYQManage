package com.xwg.mlyq.core.control;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xwg.mlyq.core.constant.JsonConstant;
import com.xwg.mlyq.core.datatrans.HttpBaseResource;

/**
 * WEB 端底层
 * @author Andy_Liu
 *
 */
public class BaseController extends HttpBaseResource implements IBaseController{
	
	/**
	 * 返回错误JSON
	 * @return
	 */
	public String returnErrorJson(){
		ObjectNode jsonObj = getJsonObject();
		jsonObj.put(JsonConstant.JSON_RETURN_RESULT,JsonConstant.JSON_RESULT_ERROR);
		return jsonObj.toString();
	}
	
	/**
	 * 返回成功JSON
	 * @return
	 */
	public String returnSuccessJson(){
		ObjectNode jsonObj = getJsonObject();
		jsonObj.put(JsonConstant.JSON_RETURN_RESULT,JsonConstant.JSON_RESULT_OK);
		return jsonObj.toString();
	}
}
