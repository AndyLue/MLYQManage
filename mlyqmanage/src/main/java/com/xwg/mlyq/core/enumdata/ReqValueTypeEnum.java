package com.xwg.mlyq.core.enumdata;


/**
 * 请求 取值 枚举
 * @author Andy_Liu
 * @date 2015年7月1日
 */
public enum ReqValueTypeEnum {
	PARAMETER(0, "parameter"), 
	ATTRIBUTE(1, "attribute");

    private int code;
    private String name;

    public final int getCode() {
        return code;
    }

    public final void setCode(int code) {
        this.code = code;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    ReqValueTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static ReqValueTypeEnum valueOfByCode(int messageType) {
        switch (messageType) {
        case 0:
            return PARAMETER;
        case 1:
            return ATTRIBUTE;
        default:
            break;
        }
        return null;
    }
}
