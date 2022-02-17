package cora.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.Eval;


import java.util.Map;

public class GroovyShellService {

    public static boolean execute(Map<String,Object> triggerItems, String scriptText){
        Binding bind = new Binding();
        triggerItems.keySet().forEach(key->{
            bind.setVariable(key,triggerItems.get(key));
        });
        GroovyShell shell = new GroovyShell(bind);
        Boolean evaluate = (Boolean) shell.evaluate(scriptText);
        return evaluate;
    }

    public static void main(String[] args) {
        Binding bind = new Binding();
        bind.setVariable("name", "iamzhongyong");
        bind.setVariable("age", "25");
        GroovyShell shell = new GroovyShell(bind);
        Object obj = shell.evaluate("if(age>'25'){'a'}else if(age == '25'){'bb'}else{'c'}");
        System.out.println(obj);
        Object res = Eval.me("temp","22","if(temp < '22'){'a'}else{'b'}");
        String s = String.valueOf(res);
        System.out.println(s);
    }
}
