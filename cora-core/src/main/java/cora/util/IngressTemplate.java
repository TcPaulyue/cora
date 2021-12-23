package cora.util;

import cora.graph.CoraGraph;

import java.util.HashMap;
import java.util.Map;

public class IngressTemplate {
    private static final String queryTemplate = "{query_${nodeType}(_id:\"${id}\"){${resp}}}";

    private static final String queryListTemplate = "{query_${nodeType}_list{${resp}}}";

    private static final String createTemplate = "{create_${nodeType}(data:{${data}}){${resp}}}";

    private static final String queryStateTemplate = "{query_${nodeType}(_id:\"${id}\"){state}}";

    private static final String updateStateTemplate = "{update_${nodeType}(_id:\"${id}\",data:{state:\"${state}\"}){state}}";

    public static String getUpdateStateTemplate(String nodeType,String id,String state){
        Map<String,String> map = new HashMap<>();
        map.put("nodeType",StringUtil.lowerCase(nodeType));
        map.put("id",id);
        map.put("state",state);
        return VelocityTemplate.build(updateStateTemplate,map);
    }

    public static String getQueryStateTemplate(String nodeType,String id){
        Map<String,String> map = new HashMap<>();
        map.put("nodeType",StringUtil.lowerCase(nodeType));
        map.put("id",id);
        return VelocityTemplate.build(queryStateTemplate,map);
    }

    public static String getQueryTemplate(String nodeType){
        Map<String,String> map = new HashMap<>();
        map.put("nodeType",StringUtil.lowerCase(nodeType));
        StringBuilder sb = new StringBuilder();
        CoraGraph.CoraNodeMap.get(nodeType).getTypeMap().keySet().forEach(key->{
            sb.append(key).append("\n");
        });
        map.put("resp",sb.toString());
        return VelocityTemplate.build(queryTemplate, map);
    }

    public static String getQueryListTemplate(String nodeType){
        Map<String,String> map = new HashMap<>();
        map.put("nodeType",StringUtil.lowerCase(nodeType));
        StringBuilder sb = new StringBuilder();
        CoraGraph.CoraNodeMap.get(nodeType).getTypeMap().keySet().forEach(key->{
            sb.append(key).append("\n");
        });
        map.put("resp",sb.toString());
        return VelocityTemplate.build(queryListTemplate, map);
    }

    public static String getCreateTemplate(String nodeType){
        Map<String,String> map = new HashMap<>();
        map.put("nodeType",StringUtil.lowerCase(nodeType));
        StringBuilder sb = new StringBuilder();
        CoraGraph.CoraNodeMap.get(nodeType).getTypeMap().keySet().forEach(key->{
            sb.append(key).append("\n");
        });
        map.put("resp",sb.toString());
        return VelocityTemplate.build(createTemplate, map);
    }
}
