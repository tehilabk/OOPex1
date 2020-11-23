package ex1.src;

import java.io.*;
import java.util.*;
import java.util.PriorityQueue;

public class WGraph_Algo implements weighted_graph_algorithms {
    private weighted_graph graph = new WGraph_DS();

    //////Contractor//////
    public WGraph_Algo() {

        this.graph = new WGraph_DS();
    }

    @Override
    public void init(weighted_graph g) {

        this.graph = g;                            //return shallow copy
    }

    @Override
    public weighted_graph getGraph() {

        return this.graph;                           //return shallow copy
    }

    @Override
    public weighted_graph copy() {

        weighted_graph NewGraph = new WGraph_DS();
        NewGraph = new WGraph_DS(this.graph);

        return NewGraph;                           //return deep copy using WGraph_DS copy contractor

    }

    @Override
    public boolean isConnected() {

        if (this.graph == null || this.graph.nodeSize() <= 1) {        //if the graph have 0 || 1 nodes
            return true;
        }

        Queue<node_info> q = new LinkedList<>();                       //use q to go through all the connect nodes (BFS)
        int counter = 0;                                               //use to count the nodes added to the q

        resetTagAndInfo(this.graph);


        Collection<node_info> G = this.graph.getV();                   //go to to the first node at G collection
        Iterator<node_info> itr = G.iterator();
        if (itr.hasNext()) {
            node_info node = (node_info) itr.next();
            q.add(node);                                               //add the node to q
            counter++;                                                 //update the counter of nodes added to the q
            node.setInfo("1");                                         //mark node as visited

            while (!q.isEmpty()) {
                Collection<node_info> N = graph.getV(node.getKey());  //go through all the node neighbors
                if (N != null) {
                    for (node_info n : N) {
                        if (n.getInfo() == " ") {                      //if not visited add to the q,mark as visited,update the counter of nodes added to the q
                            q.add(n);
                            n.setInfo("1");
                            counter++;
                        }
                    }
                    q.poll();                                          //if finish the neighbors remove the node from the q
                }
                node = q.peek();                                       //update to the next node in the q, and go through his neighbors
            }
        }

        if (this.graph.nodeSize() != counter) {                        //when q is empty if counter != nodeSize -> g not connected
            return false;
        }
        return true;
    }

    /**
     * help function for short path between two nodes
     * based on Dijkstra algorithm, implements with priority queue
     * @param src
     * @param dest
     */
    private void Short(int src, int dest) {

        node_info start = this.graph.getNode(src);
        node_info end = this.graph.getNode(dest);
        resetTagAndInfo(this.graph);                                            //help function- reset all tag and info

        if ((start != null) && (end != null)) {

            PriorityQueue<node_info> pQueue = new PriorityQueue<node_info>();
            node_info nodeNa;

            pQueue.add(start);                                                 //add the first element
            start.setInfo(String.valueOf(src));                                //set info as node father -the father of src is src itself, the tag (dist) is 0
            nodeNa = start;

            Collection<node_info> DestNa = graph.getV(end.getKey());           //the na of dest,if there is no na -there is no path

            while ((!pQueue.isEmpty() && (!DestNa.isEmpty()))) {               //if the PQ not empty and the na of dest not visited- (we can stop check the path when we visited all the nodes na of dist because we use PQ)

                if (DestNa.contains(nodeNa)) {
                    DestNa.remove(nodeNa);                                     //if the curr node is na of dest, remove him from the collection
                }

                nodeNa = pQueue.poll();

                Collection<node_info> Na = this.graph.getV(nodeNa.getKey());    // go through all curr na
                if (Na != null) {
                    for (node_info na : Na) {
                        if (Integer.valueOf(nodeNa.getInfo()) != na.getKey()) {  //if the node na is not the father of curr

                            if (na.getInfo() == " ") {                            //if node not visited
                                na.setInfo(String.valueOf(nodeNa.getKey()));      //set father of node to node curr
                                na.setTag(nodeNa.getTag() + graph.getEdge(nodeNa.getKey(), na.getKey())); //set tag as sum of father dist from src + this edge
                                pQueue.add(na);                                   //add the node to PQ
                            } else if (na.getInfo() != " ") {                     //if node already visited
                                if (nodeNa.getTag() + graph.getEdge(nodeNa.getKey(), na.getKey()) < na.getTag()) { //if the older dist from src > the path from curr
                                    na.setTag(nodeNa.getTag() + graph.getEdge(nodeNa.getKey(), na.getKey()));    //update the dist path
                                    na.setInfo(String.valueOf(nodeNa.getKey()));                                 //update father to curr
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    @Override
    public double shortestPathDist(int src, int dest) {

        node_info start = this.graph.getNode(src);
        node_info end = this.graph.getNode(dest);

        if (end == null || start == null){       //if the nodes not in graph
            return -1;
        }
        if (src == dest ){                       //the path from node to itself
            return 0;
        }
            Short(src, dest);                    //help function

        if (end.getTag() != 0) {                 //if there is a path- return the dist
            return end.getTag();
        }
        return -1;

    }

    @Override
    public List<node_info> shortestPath(int src, int dest) {

        node_info start;
        start = this.graph.getNode(src);
        node_info end = this.graph.getNode(dest);

        if (end == null || start == null){      //if the nodes not in graph
            return null;
        }
        LinkedList <node_info> list = new LinkedList<>();

        if (src == dest){                       //the path from node to itself
            list.add(start);
            return list;
        }


        Short(src, dest);



        if (end.getTag() != 0) {                //if there is a path

            while (end.getKey() != src) {       //go through all fathers till src
               list.addFirst(end);              //add the nodes in the path to the list
                end = this.graph.getNode(Integer.valueOf(end.getInfo()));
            }
                list.addFirst(start);           //after added all the nodes till src- add src


            return list;
        }
        return null;                            //if there is no path return null
    }

    @Override
    public boolean save(String file) {          //use Serializable

        try {
            FileOutputStream myFile = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(myFile);

            out.writeObject(this.graph);
            out.close();
            myFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean load(String file) {

        WGraph_DS g;

        try {
            FileInputStream myFile = new FileInputStream(file);
            ObjectInputStream out = new ObjectInputStream(myFile);

            g = (WGraph_DS) out.readObject();
            out.close();
            myFile.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        this.graph = g;
        return true;

    }


    /////help function that reset all tags and info/////
    private void resetTagAndInfo(weighted_graph g) {
        Collection<node_info> G = g.getV();
        for (node_info n : G) {
            n.setTag(0);
            n.setInfo(" ");
        }
    }
}
