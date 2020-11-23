package ex1.src;

import java.io.Serializable;
import java.util.*;

public class WGraph_DS  implements weighted_graph, Serializable{

    private int Getkey = 0;
    private	HashMap <Integer, node_info> graph  = new HashMap<>();
    private  int numOfEdge;
    private  int numOfMC;


    //////Contractor///////
    public WGraph_DS (){
        this.graph = new HashMap<>();
        this.numOfEdge = 0;
        this.numOfMC = 0;
    }

    //////Copy Contractor - using deep copy///////
    public WGraph_DS (weighted_graph other){

        Collection <node_info> otherG = other.getV();

        if (otherG != null) {
            for (node_info n : otherG) {                                 //add the nodes to this graph
                addNode(n.getKey());
            }
            Collection<node_info> thisG = getV();
            for (node_info node : thisG) {
                Collection<node_info> N = other.getV(node.getKey());    //get the neighbors of each node in graph other
                if (N != null) {
                    for (node_info na : N) {
                        connect(node.getKey(), na.getKey(), other.getEdge(node.getKey(), na.getKey()));
                    }
                }
            }
        }
    }


    @Override
    public node_info getNode(int key) {
        return this.graph.get(key);                                    //returns the node_info if the key contains in the graph,else return null
    }

    @Override
    public boolean hasEdge(int node1, int node2) {

        NodeInfo n1 = (NodeInfo) getNode(node1);
        NodeInfo n2 = (NodeInfo) getNode(node2);

        if (n1 == null || n2 == null){                                //if nodes not in graph
            return false;
        }

        return n1.hasNi(node2) && n2.hasNi(node1);                    //has edge if the nodes are neighbors
    }

    @Override
    public double getEdge(int node1, int node2) {
        NodeInfo n1 = (NodeInfo) getNode(node1);
        NodeInfo n2 = (NodeInfo) getNode(node2);

        if ((n1 != null) && (node1 == node2)){                       //edge from node to itself
            return 0;
        }

        if (n1 != null && n2 != null && n1.hasNi(node2)){
            return n1.getW(node2);                                    //return the w
        }
        return -1;
    }

    @Override
    public void addNode(int key) {

        int size = nodeSize();
        this.Getkey = key;
        node_info node = new NodeInfo();
        graph.put(key, node);

        if (size != nodeSize()){
            this.numOfMC++;                                         //if the size change MC++
        }


    }

    @Override
    public void connect(int node1, int node2, double w) {

        NodeInfo n1 = (NodeInfo) getNode(node1);
        NodeInfo n2 = (NodeInfo) getNode(node2);

        if ((w >= 0) && (node1 != node2) && (n1 != null) && (n2 != null) ) {

            if (! n1.hasNi(node2)){
                this.numOfEdge++;
                this.numOfMC++;
            }
             if (n1.hasNi(node2) && w != getEdge(node1,node2) ){
                 this.numOfMC++;                                    //if the nodes connected but the w changed
             }


            n1.addNi(node2,w);
            n2.addNi(node1,w);



        }


    }

    @Override
    public Collection<node_info> getV() {
        return graph.values();
    }                                                             //return a collection of nodes in the HashMap with complexity O(1)

    @Override
    public Collection<node_info> getV(int node_id) {
        NodeInfo node = (NodeInfo) getNode(node_id);
        if (node != null) {
            return node.getNi();                                  //return the neighbors collection of node_id in the neighbors HashMap with complexity O(k), k - being the degree of node_id.
        }

        return null;
    }

    @Override
    public node_info removeNode(int key) {
        node_info node = getNode(key);

        if (node != null ) {							    	//if node == null return null

            Collection <node_info> na = getV(key);

            if (na != null) {

                for (node_info n : na) {
                    NodeInfo newN = (NodeInfo) n;
                    newN.removeNi(key);                        //remove the edge from all the neighbors by remove the node from the neighbors HashMap

                  this.numOfEdge--;
                    this.numOfMC++;
                }
            }
            graph.remove(key);							    	//remove the node from the graph HashMap

            this.numOfMC++;
        }
        return node;
    }

    @Override
    public void removeEdge(int node1, int node2) {

        NodeInfo n1 = (NodeInfo) getNode(node1);
        NodeInfo n2 = (NodeInfo) getNode(node2);

        if ((n1 != null) && (n2 != null) && n1.hasNi(node2)) {

            n1.removeNi(node2);                                     //remove n2 from the HashMap of n1 neighbors with complexity O(1)
            n2.removeNi(node1);                                     //remove n1 from the HashMap of n2 neighbors with complexity O(1)

            this.numOfEdge--;
            this.numOfMC++;
        }
    }

    @Override
    public int nodeSize() {
        return graph.size();                                        //return the number of nodes in the HashMap with complexity O(1)
    }

    @Override
    public int edgeSize() {
        return this.numOfEdge;                                     //return the edge counter - O(1)
    }

    @Override
    public int getMC() {

        return this.numOfMC;                                        //return the MC counter - O(1)
    }

/////equals for tests//////
    @Override
    public boolean equals(Object o) {
       if (!( o instanceof weighted_graph)){
           return false;
       }
        weighted_graph g = (weighted_graph) o;

       if (nodeSize() != g.nodeSize() || edgeSize() != g.edgeSize()){
           return false;
       }
        Collection <node_info> MY = getV();
        Collection<node_info> OTHER = g.getV();

        if (! (OTHER.containsAll(MY))){
            return false;
        }

        return true;

    }

    private class NodeInfo implements node_info, Comparable , Serializable {


        private int key;
        private String info;
        private double tag;
        private HashMap<Integer, Double> na = new HashMap<>();              //hash of na, the value is the w of edges


        public NodeInfo() {

            this.key = Getkey;
            this.info = " ";
            this.tag = 0;
            this.na = new HashMap<>();

        }

        @Override
        public int getKey() {

            return this.key;
        }

        @Override
        public String getInfo() {

            return this.info;
        }

        @Override
        public void setInfo(String s) {

            this.info = s;
        }

        @Override
        public double getTag() {

            return this.tag;
        }

        @Override
        public void setTag(double t) {

            this.tag = t;
        }

        /**
         *This method adds node to na with w
         * @param k - the key of node
         * @param w - the weight of edge
         */
        //help function//
        private void addNi(int k,double w) {

            na.put(k, w);
        }

        /**
         *This method remove node from na
         * @param k - the key of node
         */
        //help function//
        private void removeNi(int k) {
            na.remove(k);

        }
        /**
         *This method return true iff this<==>k are adjacent, as an edge between them.
         * @param k - the key of node
         */
        //help function//
        private boolean hasNi(int k) {
            return na.containsKey(k);
        }
        /**
         *This method return the w of edge between nodes
         * @param k - the key of node
         */
        //help function//
        private double getW(int k) {
            return na.get(k);
        }
        /**
         * This method returns a collection with all the Neighbor nodes of this node_info
         */
        //help function//
        private Collection <node_info> getNi() {

            Collection <node_info> val = new HashSet<>();

                for (Map.Entry<Integer, Double> entery : na.entrySet()) {
                    int k = entery.getKey();
                    node_info node = graph.get(k);
                    val.add(node);
                }

            return val;
        }
        //compare nodes by tag, use for the PQ in Algo//
        @Override
        public int compareTo(Object o) {

            node_info node = (node_info) o;

            if (getTag() > node.getTag()) {
                return 1;
            }
            if (getTag() < node.getTag()) {
                return -1;
            }

                return 0;

        }


        // help function fo tests//
        @Override
        public boolean equals(Object o) {

            if (!( o instanceof node_info)){
                return false;
            }

            NodeInfo other = (NodeInfo) o;

            if (getKey() != other.getKey()) {
                return false;
            }
            Collection<node_info> OTHERNA = other.getNi();

            for (node_info oth : OTHERNA) {
                if ((na.get(oth.getKey()) == null || (na.get(oth.getKey()) != other.getW(oth.getKey())))) {
                    return false;
                }

            }
            return true;

        }

    }
}
