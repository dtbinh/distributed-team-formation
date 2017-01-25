package ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


/**
 * adapted by abelcorrea
 * @author Michele Roncalli
 *
 */
public abstract class FunctionEvaluator {
	
	protected ArrayList<VariableNode> parameters = new ArrayList<VariableNode>();
	
	//neighbours?
	protected HashSet<VariableNode> parametersSet = new HashSet<VariableNode>();
	
	
	//protected HashMap<VariableNode>
	protected Double alpha = 1d;
	protected BigDecimal minCost = null;
	protected BigDecimal maxCost = null;
	
	/**
	 * the factor node's neighbours are stored in its function evaluator.
	 * 
	 * @return the neighbours of factor node that owns this function evaluator
	 */
	public HashSet<VariableNode> getNeighbour()
	{
		return this.parametersSet;
	}
	
	public void setAlpha(Double alpha)
	{
		this.alpha = alpha;
	}
	
	public Double getAlpha()
	{
		return this.alpha;
	}
	
	
	/**
	 * set the parameters of the function
	 * add the neighbours of this factor
	 * @param parameters
	 */
	public void setParameters(ArrayList<VariableNode> parameters)
	{
		this.parameters = parameters;
		for(VariableNode x : parameters)
		{
			this.parametersSet.add(x);
		}
	}
	
	/**
	 * add the parameter as the last
	 * @param x
	 */
	public void addParameter(VariableNode x)
	{
		if(this.parametersSet.add(x))
			this.parameters.add(x);
	}
	
	public ArrayList<VariableNode> getParameters()
	{
		return this.parameters;
	}
	
	/**
	 * evaluate f over the passed values of its arguments
	 * @param params the values of arguments
	 * @return the value of f
	 */
	public abstract BigDecimal evaluate(Integer params);
	
	public void removeArg(VariableNode arg)
	{
		this.parametersSet.remove(arg);
	}
	
	public void removeArgs(Collection<VariableNode> args) {
		
		for(VariableNode v : args)
		{
			this.getNeighbour().remove(v);
		}
		/*
		ArrayList<VariableNode> to_remove = new ArrayList<VariableNode>();
		
		for(VariableNode nv : args)
		{
			if((this.hasParameter(nv)) && (!to_remove.contains(nv))){
				to_remove.add(nv);
			}
		}
		
		ArrayList<VariableNode> still_present = new ArrayList<VariableNode>();
		for(VariableNode nv : this.getParameters())
		{
			if(!to_remove.contains(nv))
			{
				still_present.add(nv);
			}
		}
		
		int[] to_remove_number_of_values = new int[to_remove.size()];
		int[] to_remove_values = new int[to_remove.size()];
		
		for(int index = 0; index < to_remove_number_of_values.length; index++)
		{
			//ammount of values of the variable node related with the index
			to_remove_number_of_values[index] = to_remove.get(index).size();
		}
		
		int[] still_present_number_of_values = new int[still_present.size()];
		int[] still_present_values = new int[still_present.size()];
		for(int index = 0; index < still_present_number_of_values.length; index++)
		{
			//amount of values of the variable node which still is present
			still_present_number_of_values[index] = still_present.get(index).size();
		}
		
		int[] args_param = new int[this.parametersNumber()];
		double fevaluate;
		
		int tableSize = 1;
		for(int size : still_present_number_of_values)
		{
			tableSize *= size;
		}
		
		HashMap<String, Double> newCostTable = new HashMap<String,Double>();
		
		int still_present_values_length_minus_one = still_present_values.length - 1;
		int i_present = still_present_values_length_minus_one;
		while(i_present>=0)
		{
			
		}
		*/
		
		
	}
	
	public VariableNode getParameter(int index)
	{
		return this.parameters.get(index);
	}
	
	public boolean hasParameter(VariableNode x)
	{
		return this.parameters.contains(x);
	}
	
	/**
	 * returns the number of parameters used by the function
	 * @return
	 */
	public int parametersNumber()
	{
		return this.parameters.size();
	}
	
	//public abstract String toString();

	public abstract ArrayList<BigDecimal> getCostValues();

	public abstract void addParametersCost(Integer params, BigDecimal cost);
	
	public abstract void clearNewParametersCost();
	
	public abstract void mergeCostTable(FactorNode f);
		
	public abstract void addParameterCardinality(Integer param, VariableNode var);
	
	public abstract HashMap<Integer, ArrayList<VariableNode>> getCardinalityPotentials();
	
}
