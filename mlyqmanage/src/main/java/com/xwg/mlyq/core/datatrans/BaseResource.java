package com.xwg.mlyq.core.datatrans;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 资源整合接口实现
 * 
 * @author Andy_Liu 2015-6-1
 *
 */
public abstract class BaseResource extends HttpBaseResource implements IBaseResource {

    protected WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    @Context
    protected HttpServletRequest req;

    @Context
    protected HttpServletResponse resp;


    /**
     * 为子类提供 创建 接口
     * 
     * @return
     */
    public String Create() throws Exception {
        return null;
    }

    /**
     * 为子类提供 更新 接口
     * 
     * @return
     */
    public String Update() throws Exception {
        return null;
    }

    /**
     * 为子类提供 查询 接口
     * 
     * @return
     */
    public String Select() throws Exception {
        return null;
    }

    /**
     * 为子类提供 删除 接口
     * 
     * @return
     */
    public String Delete() throws Exception {
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String SelectExe() throws Exception {
        String res = "";
        res = Select();
        return res;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String CreateExe() throws Exception {
        boolean isSuccess = executeRequestData(req);
        String res = "";
        if (isSuccess) {
            res = Create();
        }
        return res;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public String UpdateExe() throws Exception {
        boolean isSuccess = executeRequestData(req);
        String res = "";
        if (isSuccess) {
            res = Update();
        }
        return res;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String DeleteExe() throws Exception {
        String res = "";
        res = Delete();
        return res;
    }

    /**
     * 返回客户端错误信息
     */
    public String returnErrorClient(ObjectNode resJson) {
        return resJson.toString();
    }

    /**
     * 返回成功信息
     */
    public String returnSuccess(ObjectNode resJson) {
        return resJson.toString();
    }
}
