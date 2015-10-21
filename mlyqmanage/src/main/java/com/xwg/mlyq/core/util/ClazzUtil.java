package com.xwg.mlyq.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.xwg.mlyq.core.constant.SQLConstant;

/**
 * 反射处理工具类
 * @author Andy_Liu
 *
 */
public class ClazzUtil {

	/**
	 * 自动 拼写 插入语句
	 * @param obj 需要持久话的对象
	 * @param tableName 表名
	 * @param befName 
	 * @return 拼写好的SQL
	 */
	public static String getInsertSql(Object obj, String tableName, String befName) {
		String fileds = getSqlFileds(obj,befName);
		String title = " insert into " + tableName;
		String values = " values (";
		String wenHao = getValueWH(fileds);
		return title + "(" + fileds + ")" + values + wenHao + ")";
	}

	/**
	 * 自动 拼写 更新语句
	 * @param obj 要持久话的对象
	 * @param tableName 表名
	 * @param where 条件
	 * @param beforeCol 
	 * @return 拼写好的SQL
	 */
	public static String getUpdateSql(Object obj, String tableName, String where, String beforeCol) {
		String title = " update " + tableName;
		String values = " set ";
		String updateVal = getValueUpdate(obj,beforeCol);
		if (updateVal == null)
			return null;
		return title + values + updateVal + " where " + where;
	}
	
	
	
	private static Field[] getFileds(Object obj) {
		if (obj == null) {
			return null;
		}
		return obj.getClass().getDeclaredFields();
	}

	public static String getSqlFileds(Object obj, String befName) {
		Field[] field = getFileds(obj);

		if (field == null || field.length == 0) {
			return null;
		}
		String res = "";
		for (int i = 0; i < field.length; i++) {
			if (i == 0) {
				res += (field[i].getName());
			} else {
				res += ("," +befName+ field[i].getName());
			}
		}
		return res;
	}

	private static String getValueUpdate(Object obj, String beforeCol) {
		String res = "";
		Field[] fileds = getFileds(obj);
		for (int i = 0; i < fileds.length; i++) {
			String name = toFirstUpper(fileds[i].getName());
			if (name.equalsIgnoreCase("id") || name.equalsIgnoreCase("recid")) {
				continue;
			}
			try {
				Method method = obj.getClass().getMethod("get" + name);
				Object result = method.invoke(obj);
				Class<?> retType = method.getReturnType();
				if (!StringUtils.isEmpty(result)) {
					if(!StringUtils.isEmpty(beforeCol)){
						name = beforeCol+name;
					}
					if(retType.getClassLoader()!=null){
					    String keyCol = getKeyCol(result.getClass());
					    if(keyCol==null){
					        continue;
					    }
					    Method m1 = result.getClass().getMethod(keyCol);
					    Object r1 = m1.invoke(result);
					    res += (name + "='" + r1 + "',");
					}else if (result instanceof String) {
						res += (name + "='" + result + "',");
					}else if (result instanceof Date) {
						res += (name + "='" + result + "',");
					}else if (result instanceof Boolean) {
						res += (name + "='" + ((Boolean)result==true?1:0) + "',");
					}else if (result instanceof Long) {
						res += (name + "=" + result + ",");
					}else if (result instanceof Collection) {
					    continue;
	                } else {
						res += (name + "=" + result + ",");
					}
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		if (StringUtils.isEmpty(res)) {
			return null;
		}
		return res.substring(0, res.length() - 1);
	}

	private static String getKeyCol(Class<? extends Object> class1) {
	    Field[] fileds = null;
        try {
            fileds = getFileds(class1.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if(fileds==null){
            return null;
        }
	    for (int i = 0; i < fileds.length; i++) {
	        Field filed = fileds[i];
	        if(filed.getName().equals("id")){
	            return "getId";
	        }else if(filed.getName().equals("ID")){
                return "getID";
            }else if(filed.getName().equals("recid")){
                return "getRecid";
            }else if(filed.getName().equals("RECID")){
                return "getRECID";
            }
        }
        return null;
    }

    private static boolean isNull(Object obj, Class<?> retType) {
		if (StringUtils.isEmpty(obj)) {
			return true;
		} else {
			if (obj instanceof Integer) {
				if (Integer.parseInt(obj.toString()) == 0) {
					return true;
				}
			} else if (obj instanceof Double) {
				if (Double.parseDouble(obj.toString()) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	private static String getValueWH(String fileds) {
		String[] str = fileds.split(",");
		String res = "";
		for (int i = 0; i < str.length; i++) {
			if (i == 0) {
				res += "?";
			} else {
				res += ",?";
			}
		}
		return res;
	}

	/**
	 * 创建
	 * 
	 * @param ps
	 */
	public static void setPreparedSta(PreparedStatement ps, Object obj) {
		Field[] fileds = getFileds(obj);
		for (int i = 0; i < fileds.length; i++) {
			int num = i + 1;
			String name = toFirstUpper(fileds[i].getName());
			try {
				Method method = obj.getClass().getMethod("get" + name);
				Object result = method.invoke(obj);
				Class<?> retType = method.getReturnType();
				if(result==null){
                    ps.setString(num, null);
                }else if(retType.getClassLoader()!=null){
                    String keyCol = getKeyCol(result.getClass());
                    if(keyCol==null){
                        continue;
                    }
                    Method m1 = result.getClass().getMethod(keyCol);
                    Object r1 = m1.invoke(result);
                    ps.setString(num, r1 == null ? null : r1.toString());
                }else if (retType.getName().indexOf("int") != -1
						|| retType.getName().indexOf("Integer") != -1) {
					ps.setInt(
							num,
							result == null ? 0 : Integer.parseInt(result
									.toString()));
				} else if (retType.getName().indexOf("double") != -1
						|| retType.getName().indexOf("Double") != -1) {
					ps.setDouble(
							num,
							result == null ? 0.0 : Double.parseDouble(result
									.toString()));
				} else if (retType.getName().indexOf("date") != -1
						|| retType.getName().indexOf("Date") != -1) {
					ps.setDate(num, result == null ? null : (Date) result);
				} else if (retType.getName().indexOf("boolean") != -1
						|| retType.getName().indexOf("Boolean") != -1) {
					ps.setBoolean(num, result == null ? false : true);
				} else if (retType.getName().indexOf("Set") != -1 || retType.getName().indexOf("List") != -1) {
				    ps.setString(num,null);
				}else {
					ps.setString(num, result == null ? null : result.toString());
				}
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static String toFirstUpper(String name) {
		String first = name.substring(0, 1).toUpperCase();
		return first + name.substring(1, name.length());
	}

	/**
	 * 取得where条件
	 * @param parMap
	 * @param filterFiled 
	 * @param gUANL_COLMAP TODO 暂时简单处理
	 * @return
	 */
    public static String getWhereSql(Map<String, String[]> parMap, String beforeStr, String[] gUANL_COL, String... filterFiled) {
        Iterator<Entry<String, String[]>> it = parMap.entrySet().iterator();
        String sql = "";
        if (StringUtils.isEmpty(beforeStr)) {
            beforeStr = "";
        }
        String[] sqlWhereTypeS = parMap.get(SQLConstant.SQL_WHERE_TYPE);
        String sqlWhereType = " or ";
        if(!StringUtils.isEmpty(sqlWhereTypeS) && sqlWhereType.length()>0){
            String temp = sqlWhereTypeS[0].trim();
            //以防注入攻击
            if("and".equalsIgnoreCase(temp)||"or".equalsIgnoreCase(temp)){
                sqlWhereType = (" "+sqlWhereTypeS[0]+" ");
            }
        }
        while (it.hasNext()) {
            Entry<String, String[]> entry = it.next();
            String tempSql = "";
            boolean flag = false;
            String trueValue = isContainVal(gUANL_COL, entry.getKey());
            //过滤PAGESIZE和STARTINDEX
            if(isSelectMainCol(entry.getKey()) || !StringUtils.isEmpty(isContainVal(filterFiled, entry.getKey()))){
                continue;
            }
            
            if (!StringUtils.isEmpty(trueValue)) {
                for (int i = 0; i < entry.getValue().length; i++) {
                    if(StringUtils.isEmpty(entry.getValue()[i])){
                        continue;
                    }
                    if(trueValue.endsWith("ID")||trueValue.endsWith("Id")){
                        if(flag){
                            tempSql += (" or "+trueValue+"Temp."+trueValue+" like  '%" + entry.getValue()[i] + "%' ");
                        }else{
                            tempSql += (" "+trueValue+"Temp."+trueValue+" like  '%" + entry.getValue()[i] + "%' ");
                        }
                    }else {
                        if(flag){
                            tempSql += (" or "+trueValue+"Temp.name like  '%" + entry.getValue()[i] + "%' ");
                        }else{
                            tempSql += (" "+trueValue+"Temp.name like  '%" + entry.getValue()[i] + "%' ");
                        }
                    }
                    flag = true;
                }
            }else{
                for (int i = 0; i < entry.getValue().length; i++) {
                    if (flag) {
                        tempSql += (" or " + beforeStr + entry.getKey() + " like  '%" + entry.getValue()[i] + "%' ");
                    } else {
                        tempSql += (" " + beforeStr + entry.getKey() + " like  '%" + entry.getValue()[i] + "%' ");
                    }
                    flag = true;
                }
            }
            if(StringUtils.isEmpty(sql)){
                sql += ("("+tempSql+")");
            }else{
                sql += ( " "+sqlWhereType+" ("+tempSql+")");
            }
        }
        if(StringUtils.isEmpty(sql)){
            return sql;
        }
        return " where ( " + sql+" )";
    }
	
	
    /**
     * 过滤PAGESIZE和STARTINDEX
     * 
     * @param value
     * @return boolean
     */
    private static boolean isSelectMainCol(String value) {
        if ("pageSize".equalsIgnoreCase(value) || "startIndex".equalsIgnoreCase(value)
                || "whereType".equalsIgnoreCase(value)) {
            return true;
        }
        return false;
    }

    private static String isContainVal(String[] gUANL_COL, String key) {
        if(StringUtils.isEmpty(gUANL_COL)){
            return null;
        }
        for (int i = 0; i < gUANL_COL.length; i++) {
            if(gUANL_COL[i].equalsIgnoreCase(key)){
                return gUANL_COL[i];
            }
        }
        return null;
    }

}
