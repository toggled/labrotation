#include <iostream>
#include <map>
#include <set>
#include <string>
#include <fstream>
#include <cstdlib>
#include <algorithm>
#include <iterator>
#include <vector>

class compare { // simple comparison function
   public:
      bool operator()(const int& x,const int& y) { return x<y; } // returns x>y
};
class Graph{
    private:
        std::map< int ,std::set<int,compare> > adj_list;
    public:
        void add_vertex(const int& v){

        }
        void add_edge(const int& v, const int& u){

        }
        std::set<int,compare> get_vertex_list(){
             std::set<int,compare> vertex_set;
             std::map< int ,std::set<int,compare> > ::iterator it;
             for(it=adj_list.begin();it!=adj_list.end();it++){
                vertex_set.insert(it->first);
             }
             return vertex_set;
        }
        std::set <int,compare> get_adjacent_vertices(const int& v){
            std::set<int,compare> list = adj_list[v];
            /*std::set<int>::iterator it;
            for(it=list.begin();it!=list.end();it++)
                std::cout<<*it<<" ";*/
            return list;
        }
        void build_graph_from_edgelist(std::string filename){
            std::ifstream infile(filename.c_str()); // stl string to char array
            int from,to;
            int count = 0;
			
			
            while(infile>> from>> to){
                //if(count++<3 )    continue;
				 //std::cout<<from<<to<<std::endl;
                std::map<int, std::set<int,compare> >::iterator fromit = adj_list.find(from);
                std::map<int, std::set<int,compare> >::iterator toit = adj_list.find(to);
               if ( fromit!= adj_list.end()){
                    //std::cout<<"key exist"<<std::endl;
                    std::set<int,compare> theset = fromit->second;
                    std::set<int,compare>::iterator iter;
                    /*
                    for(iter = theset.begin(); iter!=theset.end() ; iter++)
                        std::cout<<(*iter)<<std::endl;
                    */
                    fromit->second.insert(to);
               }
               else{ // no key exists. insert first time
                    //std::cout<<"hmm"<<from<<to<<std::endl;
                    std::set<int,compare> st;
                    st.insert(to);
                    //std::cout<<"size "<<st.size()<<std::endl;
                    //std::set<int> s;
                    //s.insert(1);
                    //printing set
                    std::set<int>::iterator iter;
                    /*
                    for(iter = st.begin(); iter!=st.end() ; iter++)
                        std::cout<<(*iter)<<std::endl;
                     */
                    adj_list.insert(std::make_pair(from,st));
                    //exit(0);
               }
               // construct the adjacency list corresponding to 'to' vertex
               if ( toit!= adj_list.end()){
                    //std::cout<<"key exist"<<std::endl;
                    std::set<int,compare> theset = toit->second;
                    std::set<int,compare>::iterator iter;
                    /*
                    for(iter = theset.begin(); iter!=theset.end() ; iter++)
                        std::cout<<(*iter)<<std::endl;
                    */
                    toit->second.insert(from); // insert from vertex to the adjacency list of from
               }
               else{
                    std::set<int,compare> st;
                    st.insert(from);
                    std::set<int>::iterator iter;
                     /*
                    for(iter = st.begin(); iter!=st.end() ; iter++)
                        std::cout<<(*iter)<<std::endl;
                     */
                    adj_list.insert(std::make_pair(to,st));
               }
            }
            //print_adjlist();
        }
        void print_adjlist(){
            std::map<int, std::set<int,compare> >::iterator it = adj_list.begin();
            while(it!=adj_list.end()){
                //printing key
                std::cout<<it->first<<": ";
                std::set<int,compare>sett = it->second;

                //printing value: set sett
                std::set<int,compare>::iterator setit;
                for(setit = sett.begin();setit!=sett.end();setit++){
                   std::cout<<*setit<<" ";
                }
                std::cout<<std::endl;
                it++;
            }
        }

};

class Tomita{
    private:
    Graph g;
    std::vector <std::set<int> > cliques;
	std::string fname;
	std::ofstream output_file;
    public:
    Tomita(Graph g,std::string fname):g(g),output_file(fname,std::ios::trunc){}
    void run(){
        std::set<int,compare> cand = g.get_vertex_list();
        std::set<int,compare> fini;
        std::set<int> initial_K;
		//std::ofstream output_file(filename,std::ios::trunc);
        expand(initial_K,cand,fini);
    }
    std::set<int,compare> get_intersection(std::set<int,compare> u, std::set<int,compare> v){
        std::vector <int> inters;
      /*  std::set<int,compare> ::iterator seti;
        for(seti = u.begin();seti!=u.end();seti++){
            std::cout<<*seti <<" ";
        }
        std::cout<<std::endl;

        for(seti = v.begin();seti!=v.end();seti++){
            std::cout<<*seti <<" ";
        }
        std::cout<<std::endl;
      */
        set_intersection(u.begin(),u.end(),v.begin(),v.end(), std::back_inserter(inters));
        std::set<int,compare> s(inters.begin(),inters.end());
         /*
         std::vector<int> ::iterator it;


        for(it = inters.begin();it!=inters.end();it++){
            std::cout<<*it<<" ";
        }

        std::cout<<std::endl;
        */
        return s;
    }
    std::set<int,compare> get_union(std::set<int,compare> u, std::set<int,compare> v){
        std::vector <int> inters;
        set_union(u.begin(),u.end(),v.begin(),v.end(), std::back_inserter(inters));
        std::set<int,compare> s(inters.begin(),inters.end());
        return s;
    }
    std::set<int,compare> get_difference(std::set<int,compare> u, std::set<int,compare> v){
        std::vector <int> inters;
        set_difference(u.begin(),u.end(),v.begin(),v.end(), std::back_inserter(inters));
        std::set<int,compare> s(inters.begin(),inters.end());
        return s;
    }
    void expand(std::set<int>  K, std::set<int,compare> Cand, std::set<int,compare> Fini){
        if(Cand.empty() && Fini.empty()){
            /* printing clique */
            /*
            std::set<int> ::iterator cliqit;
            for(cliqit = K.begin();cliqit!=K.end();cliqit++)
                std::cout<<*cliqit<<" ";
            std::cout<<std::endl;
            */
			std::set<int> ::iterator it;
			for(it=K.begin();it!=K.end();it++){
		    		output_file<<*it<<" ";	
			}
			output_file<<std::endl;
            cliques.push_back(K);
            return;
        }

        /*
        Find u in SUBG which maximize intersectin(Cand,gamma(u))
        */
        std::set<int,compare>::iterator it = Cand.begin();
        int max_size = -1;
        int pivot = -1;
        //Find the pivot in Cand
        while(it!=Cand.end()){
            int u = *it;
            std::set<int,compare> gamma_u = g.get_adjacent_vertices(u);

            std::set<int,compare> intersection = get_intersection(gamma_u,Cand);
            int size  = intersection.size();
            //test

            std::set<int,compare>::iterator dummy = intersection.begin();

            if(size>max_size){
                max_size = size;
                pivot = u;
                //std::cout<<"d"<<pivot<<" hi";
            }

            it++;
        }
        //std::cout<<std::endl;
        //Find the pivot in Fini
        it = Fini.begin();
        while(it!=Fini.end()){
            int u = *it;
            std::set<int,compare> gamma_u = g.get_adjacent_vertices(u);
            int size  = get_intersection(gamma_u,Cand).size();
            if(size>max_size){
                max_size = size;
                pivot = u;

            }
            //std::cout<<*it<<" ";
            it++;
        }
        //std::cout<<std::endl;
        //std::cout<<pivot<<std::endl;
        // Form Ext set
        std::set<int,compare> pivotadj = g.get_adjacent_vertices(pivot);
        std::set<int,compare> Ext = get_difference(Cand,pivotadj);

       /* for(it=Ext.begin();it!=Ext.end();it++){
            std::cout<<*it<<" ";
        }*/
        for(it=Ext.begin();it!=Ext.end();it++){
            int q = *it;
            K.insert(q);
            std::set<int,compare> gamma_q = g.get_adjacent_vertices(q);
            std::set<int,compare> Candq,Finiq;
            Candq = get_intersection(Cand,gamma_q);
            Finiq = get_intersection(Fini,gamma_q);
            expand(K,Candq,Finiq);
            Cand.erase(q);
            Fini.insert(q);
            K.erase(q);
        }
    }
    void printcliqes(){

        std::set <int> v ;
        std::vector <std::set<int> > ::iterator it;
        std::cout<<"cliqes:";
        for(it=cliques.begin();it!=cliques.end();it++){
            v = *it;
            //std::copy(v.begin(),v.end(),std::ostream_iterator<int>(std::cout<< "\n" ));
            std::set<int> ::iterator cliqit;
            for(cliqit = v.begin();cliqit!=v.end();cliqit++)
                std::cout<<*cliqit<<" ";
            std::cout<<std::endl;
        }
        std::cout<<"Total: "<<cliques.size()<<" maximal Cliques"<<std::endl;
    }
	void write_cliques_tofile(std::string filename){
		
		if(output_file.is_open()){
        	std::set <int> v ;
			std::vector <std::set<int> > ::iterator it;
        
			for(it=cliques.begin();it!=cliques.end();it++){
            	v = *it;
				std::set<int> ::iterator cliqit;
				for(cliqit = v.begin();cliqit!=v.end();cliqit++)
		    		output_file<<*cliqit<<" ";
				output_file<<std::endl;
			}
		}
		else
			std::cout<<"file writing failed!!"<<std::endl;
	}
};

int main(){
	std::string s;
	std::string delimiter = "=";

	size_t pos = 0;
	std::string token;
	std::string filename = "cliquecon.cfg";
    std::ifstream infile(filename.c_str()); // stl string to char array
    int max_closure = 0;
    while(infile>> s){
	 	while ((pos = s.find(delimiter)) != std::string::npos) {
	    	token = s.substr(0, pos);
	    	//std::cout << token << std::endl;
			if(token == "maxclosure"){
				s.erase(0, pos + delimiter.length());
				pos = s.find(delimiter);
				token = s.substr(0, pos);
				max_closure = stoi(s);
			}
	    	else
				s.erase(0, pos + delimiter.length());
		}
		//std::cout<<s<<std::endl;
	}
	for(int i = 1;i<=max_closure; i++){
    	Graph g;
		//g.build_graph_from_edgelist("1684.edges");
		 g.build_graph_from_edgelist("graph"+std::to_string(i)+".edges");
		 //g.get_adjacent_vertices(3);
		 /*std::set<int,compare > set = g.get_vertex_list();
		 std::set<int,compare> ::iterator it;
		 for(it=set.begin();it!=set.end();it++)
        std::cout<<*it<<std::endl;
        */
		 Tomita algo(g,"clique_"+std::to_string(i)+".out");
		 algo.run();
		 //algo.printcliqes();
		 std::cout<<"clique_"+std::to_string(i)+".out"<<std::endl;
		 //algo.write_cliques_tofile("clique_"+std::to_string(i)+".out");
	 }
    return 0;
}
