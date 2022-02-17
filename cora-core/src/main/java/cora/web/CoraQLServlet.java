package cora.web;

import com.alibaba.fastjson.JSONObject;
import cora.CoraBuilder;
import cora.context.ContextHandler;
import cora.graph.fsm.impl.StateImpl;
import cora.stateengine.StateEngine;
import cora.util.ServletUtil;
import graphql.GraphQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//graphql api impl
public class CoraQLServlet extends HttpServlet {

    private static Logger logger = LogManager.getLogger(CoraQLServlet.class);

    private final StateEngine stateEngine;

    private GraphQL graphQL;

    private final CoraBuilder coraBuilder;

    private final ContextHandler contextHandler;

    public CoraQLServlet(StateEngine stateEngine, CoraBuilder coraBuilder, GraphQL graphQL, ContextHandler contextHandler) {
        this.stateEngine = stateEngine;
        this.coraBuilder = coraBuilder;
        this.graphQL = graphQL;
        this.contextHandler = contextHandler;
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
            logger.info("query registered schemas.");
            JSONObject schemas = coraBuilder.getSchemas();
            response.getWriter().write(schemas.toJSONString());
        }else if(schema.contains("create_api")){
            logger.info("create custom api for coraNode");
            this.graphQL = coraBuilder.addCustomIngress(schema);
            response.getWriter().write("add new ingress.");
        }else if(schema.contains("query_flowDefinitions")){
            JSONObject flows = coraBuilder.getFlows();
            response.getWriter().write(flows.toJSONString());
        }else if(schema.contains("create_context")){
            logger.info("create new context");
            boolean b = contextHandler.addContext(schema);
            response.getWriter().write("add new Context "+b);
        }else if(schema.contains("input_event")){
            //todo
            logger.info("send event to context");
            contextHandler.deliverEvent(schema);
            response.getWriter().write("input_event");
        }else{
            logger.info("send event to cora");
            StateImpl state = (StateImpl) stateEngine.execute(schema);
            String result = state.getExecutionResult();
            if (result.isEmpty())
                response.getWriter().write(result);
            else response.getWriter().write(result);
        }
    }
}