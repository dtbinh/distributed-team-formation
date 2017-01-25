package ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.function;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FactorNode;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FunctionEvaluator;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.VariableNode;

public class NCAFunction extends FunctionEvaluator {

	/**
     * Correspondence between parameters and function values.<br/>
     * The parameters are represented in a String.
     */
    public HashMap<Integer, BigDecimal> costTable;
    
    /**
     * correspondence between parameters and function values in the next state.<br/>
     * 
     */
    public HashMap<Integer, BigDecimal> newCostTable;
    
    protected HashMap<Integer, ArrayList<VariableNode>> cardinality;
	
    public NCAFunction()
    {
    	this.costTable = new HashMap<Integer, BigDecimal>();
    	this.newCostTable = new HashMap<Integer, BigDecimal>();
    	this.cardinality = new HashMap<Integer, ArrayList<VariableNode>>();
    }
    
    public void clearTemporaryValues()
    {
    	this.newCostTable.clear();
    }
    
	@Override
	public BigDecimal evaluate(Integer params) {
		BigDecimal c = BigDecimal.ZERO;
		if(this.costTable.containsKey(params)){
			c = this.costTable.get(params);
		}else{
			//this.addParametersCost(params, 0d);
		}
		return c;
	}
	
	
	public void addParameterCardinality(Integer param, VariableNode var)
	{
		if(this.cardinality.containsKey(param))
		{
			if(!this.cardinality.get(param).contains(var))
				this.cardinality.get(param).add(var);
		}else{
			ArrayList<VariableNode> variableList = new ArrayList<VariableNode>();
			variableList.add(var);
			this.cardinality.put(param, variableList);
		}
	}
	
	public void addParametersCost(Integer params, BigDecimal cost)
	{			
		
		this.costTable.put(params, cost);
		
		//set min and max
		if(cost.doubleValue() != Double.NEGATIVE_INFINITY && cost.doubleValue() != Double.POSITIVE_INFINITY){
			if(this.minCost == null || cost.doubleValue() < this.minCost.doubleValue())
			{
				this.minCost = cost;
			}
			if(this.maxCost == null || cost.doubleValue() > this.maxCost.doubleValue())
			{
				this.maxCost = cost;
			}
		}
	}
	
	/**
	 * how much values does this function have?
	 * 
	 * @return
	 */
	public int entryNumber()
	{
		return this.costTable.size();
	}
	
	public String toString()
	{
		String s = "\n";
		for(Integer n : this.costTable.keySet())
		{
			s += n+" "+this.costTable.get(n)+"\n";
			//s += n.agent.getClass()+" "+n.agent.getValue()+": "+this.costTable.get(n)+"\n";
		}
		return s;
	}
	
	/**
	 * list of the cost values
	 * @return
	 */
	@Override
	public ArrayList<BigDecimal> getCostValues()
	{
		return (ArrayList<BigDecimal>) this.costTable.values();
	}

	@Override
	public HashMap<Integer, ArrayList<VariableNode>> getCardinalityPotentials() {
		return this.cardinality;
	}

	@Override
	public void clearNewParametersCost() {
		this.newCostTable.clear();
		
	}
	
	/**
	 * during the computation of the utility the new values are stored in a
	 * temporary new cost table, at the end of each iteration the new cost table must
	 * be merged to the real cost table
	 */
	public void mergeCostTable(FactorNode f)
	{
	
		BigDecimal total = this.newCostTable.get(f.leader);
		for(Integer n : this.newCostTable.keySet())
		{
			this.addParametersCost(n, (this.newCostTable.get(n).divide(total, BigDecimal.ROUND_CEILING)));
		}
		this.clearNewParametersCost();
	}
	

}
