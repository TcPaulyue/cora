package cora.web;

import com.alibaba.fastjson.JSONObject;
import cora.CoraBuilder;
import cora.graph.fsm.impl.StateImpl;
import cora.stateengine.StateEngine;
import cora.util.ServletUtil;
import graphql.GraphQL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//graphql api impl
public class CoraQLServlet extends HttpServlet {

    private StateEngine stateEngine;

    private GraphQL graphQL;

    private final CoraBuilder coraBuilder;

    public CoraQLServlet(StateEngine stateEngine, CoraBuilder coraBuilder) {
        this.stateEngine = stateEngine;
        this.coraBuilder = coraBuilder;
        this.graphQL = coraBuilder.createGraphQL();
    }
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Methods","POST");

        String schema = ServletUtil.getRequestBody(req);
        if(schema.contains("query_schemas")){
            JSONObject schemas = coraBuilder.getSchemas();
            response.getWriter().write(schemas.toJSONString());
        }else if(schema.contains("create_api")){
            graphQL = coraBuilder.addCustomIngress(schema);
            response.getWriter().write("add new ingress.");
        }else if(schema.contains("query_flowDefinitions")){
            JSONObject flows = coraBuilder.getFlows();
            response.getWriter().write(flows.toJSONString());
        }else{
            StateImpl state = (StateImpl) stateEngine.execute(schema);
            String result = state.getExecutionResult();
            if (result.isEmpty())
                response.getWriter().write(result);
            else response.getWriter().write(result);
        }


    }
}