package com.xwg.mlyq.core.datatrans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xwg.mlyq.core.constant.HttpConstant;
import com.xwg.mlyq.core.context.Context;
import com.xwg.mlyq.core.enumdata.ReqValueTypeEnum;

/**
 * 资源整合接口实现
 * 处理请求数据的封装
 * @author Andy_Liu 2015-6-1
 *
 */
public class HttpBaseResource extends Context{

	private static ObjectMapper jsonObjMapper = new ObjectMapper();
	
	public ObjectMapper getJsonObjMapper() {
        return jsonObjMapper;
    }

    public void setJsonObjMapper(ObjectMapper jsonObjMapper) {
        HttpBaseResource.jsonObjMapper = jsonObjMapper;
    }

    public Logger LOGGER = Logger.getLogger(HttpBaseResource.class
			.getName());

	private String reqJson;
	
	public String getReqJson(){
		return reqJson;
	}
	
	/**
     * 取得保存图片的子路径
     * 
     * @return
     */
    public String getSavePath() {
        return "";
    }
    
	/**
	 * 创建JSON工厂
	 * 
	 * @return
	 */
	public ObjectNode getJsonObject() {
		JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
		return jsonFactory.objectNode();
	}
	
	/**
	 * 得到 request中的属性或参数 对应值
	 * 先取参数中的值，没有的话，再取属性中的
	 * @param key
	 * @return 对应值
	 */
	public String getReqValue(String key) {
		String paraValue = getRequst().getParameter(key);
		if(!StringUtils.isEmpty(paraValue)){
			return paraValue;
		}
		Object obj = getRequst().getAttribute(key);
		if (!StringUtils.isEmpty(obj)) {
			return obj.toString();
		}
		return "";
	}
	
	/**
	 *  通过type去判断，是取请求中哪个数据
	 * @param key
	 * @param type 取的是参数还是属性中的值
	 * @return
	 */
	public String getReqValue(String key,ReqValueTypeEnum type) {
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		
		if(ReqValueTypeEnum.PARAMETER.equals(type)){
			String paraValue = req.getParameter(key);
			return paraValue;
		}else if(ReqValueTypeEnum.ATTRIBUTE.equals(type)){
			Object obj = req.getAttribute(key);
			if (!StringUtils.isEmpty(obj)) {
				return obj.toString();
			}
		}
		return "";
	}
	
	/**
	 * 处理请求的数据
	 */
	public boolean executeRequestData(HttpServletRequest request) {
		String type = request.getContentType();
		LOGGER.info("请求TYPE为："+type);
		//类型为NULL，说明是程序请求的
		if (StringUtils.isEmpty(type)){
            exeJson(request);
            return true;
		}else if(type.indexOf(HttpConstant.HTTP_CONTENT_TYPE_DATA)!=-1){
			exeData(request,getSavePath());
			return true;
		}else if(type.indexOf(HttpConstant.HTTP_CONTENT_TYPE_JSON)!=-1){
			exeJson(request);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void exeData(HttpServletRequest request,String subPath) {
		// 获得磁盘文件条目工厂。
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 获取文件上传需要保存的路径，upload文件夹需存在。
		String path = null;
		Object root = System.getProperty("appWeb.root");
		if(StringUtils.isEmpty(root)){
		    path = request.getSession().getServletContext()
		    .getRealPath("" + subPath);
		}else{
		    path = root.toString()+subPath;
		}
		// 设置暂时存放文件的存储室，这个存储室可以和最终存储文件的文件夹不同。因为当文件很大的话会占用过多内存所以设置存储室。
		factory.setRepository(new File(path));
		// 设置缓存的大小，当上传文件的容量超过缓存时，就放到暂时存储室。
		factory.setSizeThreshold(1024 * 1024 * 10);
		// 上传处理工具类（高水平API上传处理？）
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		OutputStream out = null;
		InputStream in = null;
		try {
			// 调用 parseRequest（request）方法 获得上传文件 FileItem 的集合list 可实现多文件上传。
			List<FileItem> list = (List<FileItem>) upload.parseRequest(request);
			//对应数据库字段的名字
			String filedID = getFiledID(list);
			//对应保存目录的文件夹名 以RECID为区分，一个专家  或医院的相片单独建立文件夹
            String titleID = getValueID(list,"recid");
			for (FileItem item : list) {
				// 获取表单属性名字。
				String name = item.getFieldName().toLowerCase();
				// 如果获取的表单信息是普通的文本信息。即通过页面表单形式传递来的字符串。
				if (item.isFormField()) {
					// 获取用户具体输入的字符串，
					byte[] valueByte = item.get();
					String valueStr = new String(valueByte, "utf-8");
					Object oldValue = request.getAttribute(name);
					if (oldValue == null) {
						request.setAttribute(name, valueStr);
					} else {
						oldValue += ("," + valueStr);
						request.setAttribute(name, oldValue);
					}
				}
				// 如果传入的是非简单字符串，而是图片，音频，视频等二进制文件。
				else {
					
					// 获取路径名
					String value = item.getName();
					// 取到最后一个反斜杠。
					int start = value.lastIndexOf("\\");
					// 截取上传文件的 字符串名字。+1是去掉反斜杠。
					String filename = value.substring(start + 1);

					/*
					 * 第三方提供的方法直接写到文件中。 item.write(new File(path,filename));
					 */
					// 收到写到接收的文件中。
					path = path+"\\"+titleID;
					File fileDir = new File(path);
					File files = new File(path, filename);
					
					if(files.exists()){
						files.delete();
					}
					String pathTemp = files.getPath();
					System.out.println(name+"的保存路径为："+files.getPath());
//					pathTemp = pathTemp.replace("\\", "\\\\");
//					System.out.println(name+"的保存路径（替换后）为："+pathTemp);
					request.setAttribute(filedID, pathTemp);
					
					if (!fileDir.exists()) {
						fileDir.mkdirs();
						files.createNewFile();
					} else {
						files.createNewFile();
					}
					out = new FileOutputStream(files);
					in = item.getInputStream();

					int length = 0;
					byte[] buf = new byte[1024];
					System.out.println("获取文件总量的容量:" + item.getSize());

					while ((length = in.read(buf)) != -1) {
						out.write(buf, 0, length);
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(in!=null){
					in.close();
				}
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getFiledID(List<FileItem> list) throws UnsupportedEncodingException {
		for (int i = 0; i < list.size(); i++) {
			FileItem obj = list.get(i);
			if(!obj.isFormField()){
//				if(name.equalsIgnoreCase(obj.getFieldName())){
//					byte[] valueByte = obj.get();
//					String valueStr = new String(valueByte,"utf-8");
//					return valueStr;
//				}
			    return obj.getFieldName();
			}
		}
		return UUID.randomUUID().toString().replace("-", "");
	}
	
    private String getValueID(List<FileItem> list, String name) throws UnsupportedEncodingException {
        for (int i = 0; i < list.size(); i++) {
            FileItem obj = list.get(i);
            if (obj.isFormField()) {
                if (name.equalsIgnoreCase(obj.getFieldName())) {
                    byte[] valueByte = obj.get();
                    String valueStr = new String(valueByte, "utf-8");
                    return valueStr;
                }
            }
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

	private Map<String, String> exeJson(HttpServletRequest request) {
		/* 读取数据 */
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer("");
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			reqJson = sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		LOGGER.info("通过字符流接收请求的JSON为: " + sb.toString());

		if (!StringUtils.isEmpty(sb)) {
			//判断是否是IOS端传过来的
			boolean isIOS = judgePhone(sb);
			if(isIOS){
				String[] sbSpl = sb.toString().split("&");
				for (int i = 0; i < sbSpl.length; i++) {
					String[] param = sbSpl[i].split("=");
					// dataMap.put(param[0], param[1]);
					request.setAttribute(param[0], param[1]);
				}
			}else{
				try {
					JsonNode json = jsonObjMapper.readTree(sb.toString());
					Iterator<Entry<String, JsonNode>> it = json.fields();
					while(it.hasNext()){
						Entry<String, JsonNode> data = it.next();
						request.setAttribute(data.getKey(), data.getValue().asText());
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 如果是IOS传过来的，则返回TRUE
	 * @param sb
	 * @return
	 */
	private boolean judgePhone(StringBuffer sb) {
		if(sb.indexOf("{")==-1 && sb.indexOf("}")==-1){
			return true;
		}
		return false;
	}

}
