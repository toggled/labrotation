/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraph;

/**
 *
 * @author naheed
 */
public interface RandomGraph {
    
    public Graph generate();  
    public String getGraphParam(String param_key);
}
