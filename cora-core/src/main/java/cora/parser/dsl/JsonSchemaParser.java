package cora.parser.dsl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import cora.context.Context;
import cora.graph.fsm.Event;
import cora.graph.fsm.FSM;
import cora.graph.fsm.State;
import cora.graph.fsm.impl.FSMImpl;
import cora.graph.fsm.impl.InputEvent;
import cora.graph.fsm.impl.StateImpl;
import cora.parser.JsonAST;
import cora.parser.JsonArray;
import cora.util.StringUtil;
import graphql.language.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import static com.google.gson.stream.JsonToken.END_DOCUMENT;

public class JsonSchemaParser implements CoraParser {

    public enum JSONSchemaType {
        string, date, number, object, array
    }

    @Override
    public boolean isValid(String schema) {
        try {
            return isJsonValid(new StringReader(schema));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isJsonValid(final Reader reader) throws IOException {
        return isJsonValid(new JsonReader(reader));
    }

    private static boolean isJsonValid(final JsonReader jsonReader) throws IOException {
        try {
            JsonToken token;
            loop:
            while ((token = jsonReader.peek()) != END_DOCUMENT && token != null) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        break;
                    case END_ARRAY:
                        jsonReader.endArray();
                        break;
                    case BEGIN_OBJECT:
                        jsonReader.beginObject();
                        break;
                    case END_OBJECT:
                        jsonReader.endObject();
                        break;
                    case NAME:
                        jsonReader.nextName();
                        break;
                    case STRING:
                    case NUMBER:
                    case BOOLEAN:
                    case NULL:
                        jsonReader.skipValue();
                        break;
                    case END_DOCUMENT:
                        break loop;
                    default:
                        throw new AssertionError(token);
                }
            }
            return true;
        } catch (final MalformedJsonException ignored) {
            return false;
        }
    }

    public List<Definition> parseSchema(String schema) {
        JsonAST parsedAST = this.parse(schema);
        return parseAST(parsedAST);
    }

    private JsonAST parse(String schema) {
        return JsonAST.parseJSON(schema);
    }

    private List<Definition> parseAST(JsonAST jsonast) {
        List<Definition> definitions = new ArrayList<>();
        JsonAST properties = jsonast.getJSONAST("properties");
        String name = jsonast.getString("nodeType");
        ObjectTypeDefinition.Builder builder = ObjectTypeDefinition.newObjectTypeDefinition();

        //properties
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        if (properties != null) {
            properties.getMap().keySet().forEach(key -> {
                JsonAST propertiesJsonAST = properties.getJSONAST(key);
                if (propertiesJsonAST.getString("type") == null) {
                    String s = propertiesJsonAST.getString("$ref");
                    String substring = s.substring(s.lastIndexOf('/') + 1);
                    fieldDefinitions.add(new FieldDefinition(key, new TypeName(StringUtil.upperCase(substring))));
                } else if (propertiesJsonAST.getString("type").equals("array")) {
                    String s = propertiesJsonAST.getJSONAST("items").getString("$ref");
                    if (s != null) {
                        String substring = s.substring(s.lastIndexOf('/') + 1);
                        fieldDefinitions.add(new FieldDefinition(key, new ListType(new TypeName(StringUtil.upperCase(substring)))));
                    } else {
                        fieldDefinitions.add(new FieldDefinition(key, new ListType(new TypeName("String"))));
                    }
                } else {
                    JSONSchemaType type = JSONSchemaType.valueOf(propertiesJsonAST.getString("type"));
                    switch (type) {
                        case string:
                            fieldDefinitions.add(new FieldDefinition(key, new TypeName("String")));
                            break;
                        case number:
                            fieldDefinitions.add(new FieldDefinition(key, new TypeName("Int")));
                            break;
                        case date:
                            fieldDefinitions.add(new FieldDefinition(key, new TypeName("Date")));
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        ObjectTypeDefinition objectTypeDefinition = builder.name(name).fieldDefinitions(fieldDefinitions).build();
        definitions.add(objectTypeDefinition);
        return definitions;
    }

    @Override
    public FSM parseFSM(String fsmSchema){
        if(!isValid(fsmSchema)){
            return null;
        }
        JsonAST jsonAST = JsonAST.parseJSON(fsmSchema);
        String startWith = jsonAST.getString("startWith");
        State initState = new StateImpl(startWith);
        Map<State, Map<Event,State>> fsmMap = new HashMap<>();
        String[] objects = jsonAST.getStringList("states");
        for(String object:objects){
            State state = new StateImpl(object);
            fsmMap.put(state,new HashMap<>());
        }
        JsonArray transitions = jsonAST.getJsonArray("transitions");
        for(int i = 0;i<transitions.getSize();i++){
            JsonAST ast = transitions.getJsonAST(i);
            Event event = new InputEvent(ast.getString("event"));
            State nextState = new StateImpl(ast.getString("to"));
            String[] froms = ast.getStringList("from");
            Arrays.stream(froms).forEach(item->{
                StateImpl state = new StateImpl(item);
                fsmMap.get(state).put(event,nextState);
            });
        }
        return new FSMImpl(fsmMap,initState);
    }

    @Override
    public Context parseContext(String context) {
        //todo: parse Context
        return null;
    }

    @Override
    public Event parseEvent(String eventInput){
        if(!isValid(eventInput)){
            return null;
        }
        JSONObject jsonObject =  JSON.parseObject(eventInput);

        String eventName = jsonObject.getString("eventName");
        InputEvent event = new InputEvent(eventName);
        String nodeType = jsonObject.getString("nodeType");
        event.setNodeType(nodeType);
        String id = jsonObject.getString("id");
        event.setId(id);
        Map<String, Object> data = jsonObject.getJSONObject("data").getInnerMap();
        event.setData(data);
        Boolean isDuration = jsonObject.getBoolean("isDuration");
        return event;
    }
    public static void main(String[] args) {
        String s = "{\n" +
                "    \"type\": \"OBJECT\",\n" +
                "    \"nodeType\": \"GrossProfit\",\n" +
                "    \"properties\": {\n" +
                "      \"date\": {\n" +
                "        \"title\": \"日期\",\n" +
                "        \"type\": \"STRING\"\n" +
                "      },\n" +
                "      \"amount\": {\n" +
                "        \"title\": \"金额\",\n" +
                "        \"type\": \"STRING\"\n" +
                "      },\n" +
                "      \"salerExpenditures\":{\n" +
                "        \"title\":\"销售支出集合\",\n" +
                "        \"type\":\"ARRAY\",\n" +
                "        \"items\":{\n" +
                "          \"$ref\":\"#/$defs/SalerExpenditure\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"saler\":{\n" +
                "        \"$ref\":\"#/$defs/Saler\"\n" +
                "      }\n" +
                "    }\n" +
                "  }";
        String s1 = "{\n" +
                "      \"states\": [\"off\",\"on\",\"low_speed\",\"high_speed\"],\n" +
                "      \"startWith\": \"off\",\n" +
                "      \"transitions\": [\n" +
                "        {\n" +
                "          \"event\": \"turn_on\",\n" +
                "          \"from\": [\"off\"],\n" +
                "          \"to\": \"on\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"event\": \"speed_up\",\n" +
                "          \"from\": [\"on\",\"low_speed\",\"high_speed\"],\n" +
                "          \"to\": \"high_speed\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"event\": \"low_speed\",\n" +
                "          \"from\": [\"on\",\"low_speed\",\"high_speed\"],\n" +
                "          \"to\": \"low_speed\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"event\": \"turn_off\",\n" +
                "          \"from\": [\"on\",\"low_speed\",\"high_speed\"],\n" +
                "          \"to\": \"off\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }";
        JsonSchemaParser jsonSchemaParser = new JsonSchemaParser();
        //List<Definition> definitions = jsonSchemaParser.parseSchema(s);
        FSM fsm = jsonSchemaParser.parseFSM(s1);
        System.out.println("definitions");
    }
}
