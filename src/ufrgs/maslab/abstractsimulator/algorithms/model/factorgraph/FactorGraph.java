package ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph;

import java.util.ArrayList;
import java.util.HashMap;

import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.function.NCAFunction;
import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Environment;
import ufrgs.maslab.abstractsimulator.core.Variable;
import ufrgs.maslab.abstractsimulator.variables.Human;

public class FactorGraph<E extends Entity> extends Environment<E> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3159996778642755184L;

	/**
	 * 
	 */
	
	
	public ArrayList<VariableNode> variable = new ArrayList<VariableNode>();
	
	public HashMap<Integer, ArrayList<FactorNode>> factor = new HashMap<Integer, ArrayList<FactorNode>>();

	/**
	 * this method initializes the variables and add the humans into the variablenode list
	 * and not humans variables as factor
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void registerVariable(Class<? extends Variable> varClass, Integer ammount) {
		
		this.getVarClass().put((Class<Variable>)varClass, ammount);
		for(Object v : this.getVariables())
		{
			//insert all variables as variable node in the factor graph
			//even fire stations are variable node
			if(v instanceof Variable && v.getClass() == varClass)
			{
				Variable var = (Variable)v;
				//creates instance of the variable node
				this.variable.add(VariableNode.getVariableNode(var.getId()));
				//real variable wears the variable node
				int idx = this.variable.indexOf(VariableNode.getVariableNode(var.getId()));
				this.variable.get(idx).agent = var;
				
			}
			
			//creates initial number of factor nodes
			//all not human agents are initial factor nodes
			if(v instanceof Variable && !(v instanceof Human) && v.getClass() == varClass){
				Variable var = (Variable)v;
				//creates instance of the factor node
				int degree = 0;
				if(this.factor.isEmpty())
				{
					this.factor.put(degree, new ArrayList<FactorNode>());
				}
				this.factor.get(degree).add(FactorNode.putFactorNode(var.getId(), new NCAFunction()));
				
				//real variable wears the factor node
				
				int idx = -1;
				if(FactorNode.getFactorNode(var.getId()) != null)
					idx = this.factor.get(0).indexOf(FactorNode.getFactorNode(var.getId()));
								
				this.factor.get(0).get(idx).degree = degree;
				this.factor.get(0).get(idx).leader = VariableNode.getVariableNode(var.getId());
			}
		
		}
		
		//initialization
		//all variable nodes are connected to the factor nodes
		for(ArrayList<FactorNode> arrFactor : this.factor.values())
		{
			for(FactorNode f : arrFactor)
			{
				for(VariableNode v : this.variable)
				{
					v.addNeighbour(f);
					f.addNeighbour(v);
				}
			}
		}
		
		
			
	}
	

	

}
