package cora.schema;

import cora.graph.CoraNode;
import cora.util.GQLTemplate;
import graphql.language.ListType;
import graphql.language.Type;
import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CoraRuntimeWiring {
    private RuntimeWiring runtimeWiring;

    DataFetcher nodeInstanceFetcher;

    DataFetcher nodeInstanceListFetcher;

    DataFetcher nodeInstanceConstructor;

    DataFetcher nodeInstanceUpdater;

    private static final String QUERY_IN_GRAPHQL = "Query";

    private static final String MUTATION_IN_GRAPHQL = "Mutation";

    public CoraRuntimeWiring(){
    }

    public CoraRuntimeWiring(DataFetcher nodeInstanceFetcher
            , DataFetcher nodeInstanceListFetcher
            , DataFetcher nodeInstanceConstructor
            , DataFetcher nodeInstanceUpdater) {
        runtimeWiring = RuntimeWiring.newRuntimeWiring().build();
        this.nodeInstanceFetcher = nodeInstanceFetcher;
        this.nodeInstanceListFetcher = nodeInstanceListFetcher;
        this.nodeInstanceConstructor = nodeInstanceConstructor;
        this.nodeInstanceUpdater = nodeInstanceUpdater;
    }


    public RuntimeWiring getRuntimeWiring() {
        return runtimeWiring;
    }

    public void initCoraRuntimeWiring(){
        Map<String, DataFetcher> map = new LinkedHashMap<>();
        runtimeWiring.getDataFetchers().put(QUERY_IN_GRAPHQL,map);
        runtimeWiring.getDataFetchers().put(MUTATION_IN_GRAPHQL,new LinkedHashMap<>());
    }

    public void addCoraDataFetcherInCoraIngress(String name, DataFetcher dataFetcher){
        runtimeWiring.getDataFetchers().get(QUERY_IN_GRAPHQL).put(name,dataFetcher);
    }

    public void addCoraDataFetchersInCoraNode(String name, Map<String, DataFetcher> dataFetcherMap){
        runtimeWiring.getDataFetchers().put(name,dataFetcherMap);
    }

    public void addNewSchemaDataFetcher(CoraNode coraNode) {

        //queryDocument ==>  documentDataFetcher
        // RelationalDataFetcher relationalDataFetcher = new RelationalDataFetcher(connection);
        // this.addNewEntryInQueryDataFetcher(graphNode.getName(), relationalDataFetcher);
        this.addCoraDataFetcherInCoraIngress(GQLTemplate.querySingleInstance(coraNode.getName()),nodeInstanceFetcher);

        this.addCoraDataFetcherInCoraIngress(GQLTemplate.queryInstanceList(coraNode.getName()),nodeInstanceListFetcher);

        this.addCoraDataFetcherInCoraIngress(GQLTemplate.createNodeInstance(coraNode.getName()),nodeInstanceConstructor);

        this.addCoraDataFetcherInCoraIngress(GQLTemplate.updateNodeInstance(coraNode.getName()),nodeInstanceUpdater);

        if(!coraNode.getLinkedTypeMap().isEmpty()){
            Map<String,DataFetcher> dataFetcherMap = new HashMap<>();
            coraNode.getLinkedTypeMap().keySet().forEach(field->{
                Type nodeInstanceType = coraNode.getLinkedTypeMap().get(field);
                if(nodeInstanceType instanceof ListType){
                    dataFetcherMap.put(field,nodeInstanceListFetcher);
                }else{
                    dataFetcherMap.put(field,nodeInstanceFetcher);
                }
            });
            this.addCoraDataFetchersInCoraNode(coraNode.getName(),dataFetcherMap);
        }
        //this.addNewEntryInMutation(GQLTemplate.createNodeInstance(graphNode.getName()),mongodbNodeInstanceConstructor);
    }




}
