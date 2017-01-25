package ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import ufrgs.maslab.abstractsimulator.exception.FactorNotPresentException;


public class FactorNode implements Node {
	
	/**
     * The FunctionEvaluator that implements the function represented by this NodeFunction.
     */
    private FunctionEvaluator function;

    /**
     * who/what I am in the real scenario
     */
    public VariableNode leader;
    
    public int degree = 0;
    
    
    /**
     * Like Node or Edge. Look at their doc, please.
     */
    private static HashMap<Integer, FactorNode> table = new HashMap<Integer, FactorNode>();
    /**
     * id.. what else?
     */
    private int id;

    static int lastId = -1;

    
	public FactorNode(int id)
	{
		this.id = id;
		lastId = id;
	}
	
	public boolean equals(Object n) {
        return (n instanceof FactorNode)
                && (this.getId() == ((FactorNode) n).getId());
    }
	
	public int getId() {
		return this.id;
	}

	public int hashCode() {
        return ("Factor_" + this.id).hashCode();
    }

	@Override
	public void addNeighbour(Node n) {
		if(n instanceof VariableNode)// && n != (VariableNode)this.leader)
			this.function.addParameter((VariableNode)n);	
	}
	
	public void removeNeighbour(VariableNode v)
	{
		this.function.removeArg(v);
	}
	
	public void removeNeighbours(Collection<VariableNode> c)
	{
		this.function.removeArgs(c);
	}

	@Override
	public HashSet<VariableNode> getNeighbour() {
		return this.function.getNeighbour();
	}

	/**
	 * print string of all neighbours
	 */
	@Override
	public String stringOfNeighbour() {
		StringBuilder neighbours = new StringBuilder();
		Iterator<VariableNode> itnode = this.function.getNeighbour().iterator();
		while(itnode.hasNext())
		{
			VariableNode variableNode = itnode.next();
			neighbours.append(variableNode.getId()).append(" ");
		}
		return neighbours.toString();
	}
	
	public void setFunction(FunctionEvaluator function)
	{
		this.function = function;
	}
	
	public FunctionEvaluator getFunction()
	{
		return this.function;
	}
	
	/**
	 * save the factor node with id and function evaluator
	 * creates a new object
	 * @param id
	 * @param fe
	 * @return
	 */
	public static FactorNode putFactorNode(Integer id, FunctionEvaluator fe)
	{
		if(!(FactorNode.table.containsKey(id)))
		{
			FactorNode.table.put(id, new FactorNode(id));
			FactorNode.table.get(id).setFunction(fe);
		}		
		return FactorNode.table.get(id);
	}
	
	/**
	 * remove factor node
	 * @param id
	 */
	public static Boolean removeFactorNode(Integer id)
	{
		
		if(FactorNode.table.containsKey(id))
		{
			if(FactorNode.table.get(id).getNeighbour().size() <= 0)
			{
				FactorNode.table.remove(id);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * retrieve an existing factor node
	 * 
	 * @param id
	 * @return
	 * @throws FactorNotPresentException
	 */
	public static FactorNode getFactorNode(Integer id)
	{
		if(!(FactorNode.table.containsKey(id))){
			return null;
		}
		return FactorNode.table.get(id);
	}
	
	public static FactorNode getNewNextFactorNode(FunctionEvaluator fe)
	{
		int idn = lastId + 1;
		while(FactorNode.table.containsKey(idn)){
			idn++;
		}
		return FactorNode.putFactorNode(idn,fe);
	}


}
