package cora.schema;

import cora.graph.CoraNode;
import cora.util.GQLTemplate;
import graphql.language.*;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoraTypeRegistry {

    private final TypeDefinitionRegistry typeDefinitionRegistry;

    public static Map<String, Map<String, Type>> typeDefinitionsMap = new HashMap<>();

    private final List<FieldDefinition> fieldDefinitionListInQuery = new ArrayList<>();

    public CoraTypeRegistry() {
        this.typeDefinitionRegistry = new TypeDefinitionRegistry();
    }

    public TypeDefinitionRegistry getTypeDefinitionRegistry() {
        return typeDefinitionRegistry;
    }

    public void initSchemaDefinition() {
        SchemaDefinition.Builder builder = SchemaDefinition.newSchemaDefinition();
        OperationTypeDefinition operationTypeDefinition = new OperationTypeDefinition("query", new TypeName("Query"));
        SchemaDefinition schemaDefinition = builder.operationTypeDefinition(operationTypeDefinition).build();
        typeDefinitionRegistry.add(schemaDefinition);

        /**
         * filter field input init
         * for example
         * isPaybackEnd:{
         *             _eq:"是"
         *             _lt:"asd"
         *             ...
         *          }
         */
        List<InputValueDefinition> inputValueDefinitions = new ArrayList<>();
        GQLTemplate.getFilter_items_in_query_list_for_string().forEach(item-> inputValueDefinitions.add(new InputValueDefinition(item,new TypeName("String"))));
        InputObjectTypeDefinition inputObjectTypeDefinition = InputObjectTypeDefinition.newInputObjectDefinition()
                .name(GQLTemplate.getFilterItemForNodeInstanceForString())
                .inputValueDefinitions(inputValueDefinitions).build();
        typeDefinitionRegistry.add(inputObjectTypeDefinition);

        List<InputValueDefinition> intInputValueDefinitions = new ArrayList<>();
        GQLTemplate.getFilter_items_in_query_list_for_int().forEach(item->{
            intInputValueDefinitions.add(new InputValueDefinition(item,new TypeName("Int")));
        });
        InputObjectTypeDefinition intInputObjectTypeDefinition = InputObjectTypeDefinition.newInputObjectDefinition()
                .name(GQLTemplate.getFilterItemForNodeInstanceForInt())
                .inputValueDefinitions(intInputValueDefinitions).build();
        typeDefinitionRegistry.add(intInputObjectTypeDefinition);

    }

    public void buildTypeRegistry() {
        typeDefinitionRegistry.getType("Query").ifPresent(typeDefinition -> {
            if (typeDefinition instanceof ObjectTypeDefinition)
                typeDefinitionRegistry.remove(typeDefinition);
        });
        ObjectTypeDefinition query = ObjectTypeDefinition.newObjectTypeDefinition().name("Query").fieldDefinitions(fieldDefinitionListInQuery).build();
        typeDefinitionRegistry.add(query);
    }

    public void addGraphNode(CoraNode coraNode) {
        if (!typeDefinitionsMap.keySet().contains(coraNode.getName())) {

            typeDefinitionsMap.put(coraNode.getName(), coraNode.getTypeMap());

            this.addTypeDefinition(coraNode.getName(), coraNode.getTypeMap());

            this.addDocumentTypeInQuery(coraNode.getName());

            this.addDocumentListTypeInQuery(coraNode.getName(),coraNode.getInputTypeMap());

            this.addCreateNodeInstanceInQuery(coraNode.getName(), coraNode.getInputTypeMap());

            this.addUpdateNodeInstanceInQuery(coraNode.getName(),coraNode.getInputTypeMap());
        }
    }

    public void addCustomAPIInQuery(String nodeType,String apiName){
        this.addFieldDefinitionsInQueryType(apiName
                ,new TypeName(nodeType)
                ,new ArrayList<>());
    }

    //在GraphQL的Schema中的Query类中增加一个访问定义的对象的字段
    private void addDocumentTypeInQuery(String name) {
        List<InputValueDefinition> inputValueDefinitions = new ArrayList<>();
        inputValueDefinitions.add(new InputValueDefinition("_id", new TypeName("String")));
        //orderDocument(id:String):OrderDocument
        this.addFieldDefinitionsInQueryType(GQLTemplate.querySingleInstance(name)
                , new TypeName(name)
                , inputValueDefinitions);
    }

    private void addDocumentListTypeInQuery(String name, Map<String, Type> typeMap) {

        InputObjectTypeDefinition filterDefinition = FilterDefinitionBuilder.build(name, typeMap);
        typeDefinitionRegistry.add(filterDefinition);
        List<InputValueDefinition> filterValueDefinition = new ArrayList<>();
        filterValueDefinition.add(new InputValueDefinition("where", new TypeName(GQLTemplate.filtersOfNodeInstance(name))));
        this.addFieldDefinitionsInQueryType(GQLTemplate.queryInstanceList(name), new ListType(new TypeName(name)),
                filterValueDefinition);
    }

    private void addCreateNodeInstanceInQuery(String name, Map<String, Type> typeMap) {
        List<InputValueDefinition> inputValueDefinitions = new ArrayList<>();
        typeMap.forEach((key, value) -> inputValueDefinitions.add(new InputValueDefinition(key, value)));
        InputObjectTypeDefinition inputObjectTypeDefinition = InputObjectTypeDefinition.newInputObjectDefinition()
                .name(GQLTemplate.inputTypeForNodeInstance(name))
                .inputValueDefinitions(inputValueDefinitions).build();
        typeDefinitionRegistry.add(inputObjectTypeDefinition);

        List<InputValueDefinition> inputValueDefinition = new ArrayList<>();
        inputValueDefinition.add(new InputValueDefinition("data", new TypeName(GQLTemplate.inputTypeForNodeInstance(name))));

        this.addFieldDefinitionsInQueryType(GQLTemplate.createNodeInstance(name)
                , new TypeName(name)
                , inputValueDefinition);
    }

    private void addUpdateNodeInstanceInQuery(String name,Map<String,Type> typeMap){
        List<InputValueDefinition> inputValueDefinitions = new ArrayList<>();
        inputValueDefinitions.add(new InputValueDefinition("_id", new TypeName("String")));
        inputValueDefinitions.add(new InputValueDefinition("data", new TypeName(GQLTemplate.inputTypeForNodeInstance(name))));

        this.addFieldDefinitionsInQueryType(GQLTemplate.updateNodeInstance(name)
                , new TypeName(name)
                , inputValueDefinitions);
    }

    void addInputObjectTypeDefinition(String name, Map<String, Type> typeMap) {

        List<InputValueDefinition> inputValueDefinitions = new ArrayList<>();
        typeMap.forEach((key, value) -> inputValueDefinitions.add(new InputValueDefinition(key, value)));
        InputObjectTypeDefinition inputObjectTypeDefinition = InputObjectTypeDefinition.newInputObjectDefinition().name(name)
                .inputValueDefinitions(inputValueDefinitions).build();
        typeDefinitionRegistry.add(inputObjectTypeDefinition);
    }

    void addFieldDefinitionsInQueryType(String name, Type type, List<InputValueDefinition> inputValueDefinitions) {
        FieldDefinition definition = FieldDefinition.newFieldDefinition().inputValueDefinitions(inputValueDefinitions)
                .name(name).type(type).build();
        fieldDefinitionListInQuery.add(definition);
    }


    void addTypeDefinition(String name, Map<String, Type> typeMap) {
        typeDefinitionRegistry.add(newObjectTypeDefinition(name, newFieldDefinitions(typeMap)));
    }

    private List<FieldDefinition> newFieldDefinitions(Map<String, Type> typeMap) {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        typeMap.forEach((name, Type) -> fieldDefinitions.add(new FieldDefinition(name, Type)));
        return fieldDefinitions;
    }

    private ObjectTypeDefinition newObjectTypeDefinition(String name, List<FieldDefinition> fieldDefinitions) {
        ObjectTypeDefinition.Builder builder = ObjectTypeDefinition.newObjectTypeDefinition();
        return builder.name(name).fieldDefinitions(fieldDefinitions).build();
    }

}
