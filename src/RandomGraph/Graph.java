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
public class Graph {
    public int [][] adjmat;
    public int [] deg;
    public Graph() {
    }
    public void initialize_graph(int N){
        adjmat = new int[N][N];
        deg = new int[N];
    }
    public void add_edges(int u,int v){
        adjmat[u][v] = adjmat[v][u] = 1;
        deg[u]++;
        deg[v]++;
    }
    public void remove_edges(int u,int v){
        adjmat[u][v] = adjmat[v][u] = 0;
        deg[u]--;
        deg[v]--;
    }
    public int get_degree(int u){
        return deg[u];
    }
    public void print_matrix(){
        for (int i = 0; i < adjmat.length; i++) {
            for (int j = 0; j < adjmat[i].length; j++) {
                System.out.print(adjmat[i][j]+" ");
            }
            System.out.println();
        }
    }
    public void write_graph(String filename){
        Graph_writer.write_graph(adjmat, filename, "edgelist");
    }
}
