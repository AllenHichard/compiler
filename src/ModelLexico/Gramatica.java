/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelLexico;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author allen
 */
public class Gramatica {

    private final Pattern pattern;
    private final String nomeToken;

    public Gramatica(Pattern pattern, String nomeToken) {
        this.pattern = pattern;
        this.nomeToken = nomeToken;
    }

    public boolean pertence(String expressao) {
        Matcher m = pattern.matcher(expressao);
        if (m.matches()) {
            return m.matches();
        }
        return false;
    }
    
    public Pattern getpattern(){
        return this.pattern;
    }

    @Override
    public String toString() {
        return this.nomeToken;
    }

}
