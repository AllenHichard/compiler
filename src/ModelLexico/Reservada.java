/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelLexico;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author allen
 */
public class Reservada {

    public List<String> p;
    
    public Reservada(){
        p = new ArrayList<>();
        addPalavras();
        
    }
    private void addPalavras(){
        p.add("class");
        p.add("final");
        p.add("if");
        p.add("else");
        p.add("for");
        p.add("scan");
        p.add("print");
        p.add("int");
        p.add("float");
        p.add("bool");
        p.add("true");
        p.add("false");
        p.add("string");
        p.add("main");
    }
    public boolean pertence(String token){
        if(p.contains(token)){
            return true;
        }
        return false;
    }
    
    
    
    
    
}
