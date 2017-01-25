package ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Variable;
import ufrgs.maslab.abstractsimulator.exception.NoMoreValuesException;
import ufrgs.maslab.abstractsimulator.values.Task;

/**
 * adapted from michele roncalli's work
 * @author abel correa
 *
 */
public class VariableNode implements Node {

	/**
	 * Static map of Variables created. Like FactorNode, NodeArgument, Edge.
	 */
	private static HashMap<Integer, VariableNode> table = new HashMap<Integer, VariableNode>();
	
	/**
	 * object which wears the variable node implementation
	 */
	public Variable agent = null;
	
	/**
     * represent M(i), that is the set of function nodes connected to the variable i
     */
    HashSet<FactorNode> neighbours;
    
    /**
     * id, old story..
     */
    private int id;
    static int lastId = -1;
    /**
     * The index of the actual value of this NodeVariable.
     */
    //protected int index_actual_argument = -1;
    
    /**
     * arraylist of the possible values of the variable represented by this node
     */
    //protected ArrayList<NodeArgument> values;
    
    private VariableNode(int id) {
        this.id = id;
        lastId = id;
        //this.values = new ArrayList<NodeArgument>();
        this.neighbours = new HashSet<FactorNode>();
    }
    
    /**
     * Add a new possible value
     * @param v new NodeArgument for this
     */
    //public void addValue(NodeArgument v) {
    //	if(!this.values.contains(v))
    //		this.values.add(v);
    //}
    
    
	
	public boolean equals(Object n) {
        return (n instanceof VariableNode)
                && (this.getId() == ((VariableNode) n).getId());
    }

    public int getId() {
        return this.id;
    }
	
	public int hashCode() {
        return ("VariableNode_" + this.id).hashCode();
    }
	
	//public ArrayList<NodeArgument> getValues()
	//{
	//	return this.values;
	//}
	/**
	 * 
	 * @return domain of the agent represented by this variable node
	 */
	public ArrayList<Entity> getValues()
	{
		return this.agent.getDomain();
	}

	@Override
	public void addNeighbour(Node n) {
		if(n instanceof FactorNode)
		{
			this.neighbours.add((FactorNode)n);
		}
		
	}
	
	public void removeNeighbour(FactorNode n)
	{
		this.neighbours.remove(n);
	}
	
	public void removeNeighbour(ArrayList<FactorNode> n)
	{
		for(FactorNode c : n)
		{
			this.neighbours.remove(c);
		}
	}
	
	
    //public int size() {
    //    return this.values.size();
    //}
	/**
     * @return the number of possible values of this variable
     */
	public int size()
	{
		return this.agent.getDomain().size();
	}
    
    /**
     * return index of the argument 
     * @param node
     * @return
     */
    //public int numberOfArgument(NodeArgument node) {
    //    return values.indexOf(node);
    //}
    
    /**
     * return the argument
     * @param index
     * @return
     */
    //public NodeArgument getArgument(int index)
    //{
    //	return this.values.get(index);
    //}
    
    /**
     * set the index of actual parameter
     * @param index
     */
    //public void setStateIndex(int index)
    //{
    //	this.index_actual_argument = index;
    //}
    
    /**
     * set the actual node argument
     * @param n
     */
    //public void setStateArgument(NodeArgument n)
    //{
    //	this.index_actual_argument = this.numberOfArgument(n);
    //}
    
    /**
     * get tha index of the current state
     * @return
     */
    //public int getStateIndex()
    //{
    //	return this.index_actual_argument;
    //}
    
    /**
     * get the current node argument
     * returns exception if there is no actual argument
     * @return
     * @throws VariableNotSetException
     */
    //public NodeArgument getStateArgument() throws VariableNotSetException
    //{
    //	if(this.index_actual_argument == -1)
    //		throw new VariableNotSetException();
    //	
    //	return this.getArgument(this.index_actual_argument);
    //}
    
    /**
     * choose a random valid value different of the current assigned value
     * @throws NoMoreValuesException
     */
    public void setAnotherRandomValidValue() throws NoMoreValuesException{
    	if(this.size() == 1){
    		if(this.agent.getValue() == null){
    			this.agent.assign((Task)this.agent.getDomain().get(0));
    		}else{
    			throw new NoMoreValuesException();
    		}
    	}else if(this.size() > 1){
    		Random rnd = new Random();
    		int oldValue = this.agent.getValue();
    		int pos;
    		Task newValue = null;
    		do{
    			pos = rnd.nextInt(this.size());
    			newValue = (Task)this.agent.getDomain().get(pos);
    		}while(oldValue == newValue.getId());
    		this.agent.assign(newValue);
    	}
    }
    
    /**
     * choose a random valid value to the variable
     * @throws NoMoreValuesException
     */
    public void setRandomValidValue() throws NoMoreValuesException
    {
    	if(this.size() == 1)
    	{
    		if(this.agent.getValue() == null){
    			this.agent.assign((Task)this.agent.getDomain().get(0));
    		}else{
    			throw new NoMoreValuesException();
    		}
    	}else if(this.size() > 1)
    	{
    		Random rnd = new Random();
    		
    		int pos = rnd.nextInt(this.size());
    		Task newValue = (Task)this.agent.getDomain().get(pos);
    		this.agent.assign(newValue);
    	}
    }
    
    //public void clearArguments()
    //{
    //	this.values = new ArrayList<NodeArgument>();
    //}

	@Override
	public Set<FactorNode> getNeighbour() {
		return this.neighbours;
	}

	@Override
	public String stringOfNeighbour() {
		StringBuilder neighbours = new StringBuilder();
		Iterator<FactorNode> itnode = this.neighbours.iterator();
		while(itnode.hasNext())
		{
			FactorNode variableNode = itnode.next();
			neighbours.append(variableNode).append(" ");
		}
		return neighbours.toString();
	}
	
	//creates a new table
	public static void resetIds()
	{
		VariableNode.table = new HashMap<Integer, VariableNode>();
		lastId = -1;
	}
	
	//inserts one variable node in the table  if it does not exists
	//returns the variable node specified by the id parameter
	public static VariableNode getVariableNode(Integer id)
	{
		if(!(VariableNode.table.containsKey(id)))
		{
			VariableNode.table.put(id, new VariableNode(id));
		}
		return VariableNode.table.get(id);
	}

}
