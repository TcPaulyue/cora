package cora.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.Eval;

public class GroovyShellService {
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
