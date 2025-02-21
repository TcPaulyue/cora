package cora.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cora.CoraBuilder;
import cora.util.ServletUtil;
import cora.graph.CoraGraph;
import cora.graph.IngressType;
import cora.util.VelocityTemplate;
import graphql.ExecutionResult;
import graphql.GraphQL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


// restful api impl
public class RestApiServlet extends HttpServlet {

    private GraphQL graphQL;

    private final CoraBuilder coraBuilder;

    public RestApiServlet(CoraBuilder coraBuilder,GraphQL graphQL) {
        this.coraBuilder = coraBuilder;
        this.graphQL = graphQL;
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String coraQL;
        String type = request.getParameter("type");
        String pathInfo = request.getPathInfo();
        if(!request.getParameterMap().keySet().contains("id")){
            coraQL = CoraGraph.CoraIngressMap.get(type).getIngressData(IngressType.QUERY_LIST);
        }else{
            String a = CoraGraph.CoraIngressMap.get(type).getIngressData(IngressType.QUERY);
            String id = request.getParameter("id");
            Map<String,String> map = new HashMap<>();
            map.put("id",id);
            map.put("resp","_id");
            coraQL = VelocityTemplate.build(a, map);
        }
        ExecutionResult result = graphQL.execute(coraQL);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin","*");
        response.getWriter().write(JSON.toJSONString(result.getData()));
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin","*");
        if(req.getPathInfo().equals("/schemas")){
            String schema = ServletUtil.getRequestBody(req);
            JSONObject jsonObject = JSON.parseObject(schema).getJSONObject("fieldschema");
            this.graphQL = coraBuilder.addTypeInGraphQL(jsonObject.toJSONString());
            JSONObject coraNode = coraBuilder.addTypeInDB(schema);
            response.getWriter().write(JSON.toJSONString(coraNode));
        }else if(req.getPathInfo().equals("/flowDefinitions")){
            String schema = ServletUtil.getRequestBody(req);
            JSONObject jsonObject = coraBuilder.addFlowDefinitionInDB(schema);
            response.getWriter().write(JSON.toJSONString(jsonObject));
        }else{
            String type = req.getParameter("type");
            String schema = ServletUtil.getRequestBody(req);
            String a = CoraGraph.CoraIngressMap.get(type).getIngressData(IngressType.MUTATION);
            Map<String,String> map = new HashMap<>();
            map.put("data",schema);
            String coraQL = VelocityTemplate.build(a, map);
            ExecutionResult result = graphQL.execute(coraQL);
            response.getWriter().write(JSON.toJSONString(result.getData()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
