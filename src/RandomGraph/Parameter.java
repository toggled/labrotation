/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraph;

import java.util.HashMap;

/**
 *
 * @author naheed
 */
public class Parameter {
    HashMap<String, Object> param;
    public Parameter() {
        param = new HashMap<>();
    }
    void put(String key, Object val){
        param.put(key, val);
    }
    Object get(String key){
        return param.get(key);
    }
}
