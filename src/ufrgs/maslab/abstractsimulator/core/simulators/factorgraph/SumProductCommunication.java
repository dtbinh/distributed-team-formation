package ufrgs.maslab.abstractsimulator.core.simulators.factorgraph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;

import ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.NectarSource;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FGNetworkEvaluator;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FactorGraph;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FactorNode;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.VariableNode;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.function.NCAFunction;
import ufrgs.maslab.abstractsimulator.core.BlackBox;
import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Value;
import ufrgs.maslab.abstractsimulator.core.simulators.basic.CommunicationSimulation;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.exception.VariableNotSetException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.util.WriteFile;
import ufrgs.maslab.abstractsimulator.variables.Human;

public class SumProductCommunication extends CommunicationSimulation {
	
	private String logFileName = Transmitter.getProperty("files.properties", "algorithm.log")+"_"+BlackBox.getAlgorithmName()+"_"+BlackBox.getAlgorithmRun();
	
	private Boolean debug = Transmitter.getBooleanConfigParameter("config.properties", "config.debug");
	
	private String exceptionFile = "exception.properties";
	
	@SuppressWarnings("unchecked")
	@Override
	public void simulate(Entity entity) throws SimulatorException {
		
		if(!(entity instanceof FactorGraph))
			throw new SimulatorException(Transmitter.getProperty(this.exceptionFile, "exception.not.factorgraph"));
		
		FactorGraph<Entity> env = (FactorGraph<Entity>)entity;
		
		//erase messages
		/*
		 * 
		for(Variable v: env.getVariables())
		{
			if(v instanceof Human)
				((Human)v).getEar().clear();
			if(v instanceof Agent)
				((Agent)v).getRadioMessage().clear();
		}
		*/
		this.deleteValues(env);
		//actions of agents
		this.action(env);
		
		this.upwardMessages(env);
		this.downwardMessages(env);
		
		/*for(int k = 0; k < env.factor.keySet().size(); k++)
		{
			System.out.println("Level "+k);
			for(FactorNode f : env.factor.get(k))
			{
				System.out.println("Factor "+f.getId());
				System.out.println("Neighbours");
				for(VariableNode v : f.getNeighbour())
				{
					if(v.getId() == f.leader.getId())
					{
						System.out.println("Leader "+v.getId());
						for(FactorNode g : v.getNeighbour())
						{
							System.out.print(" Factors ");
							System.out.println(g.getId()+"; ");
						}
					}else{
						System.out.println("Worker "+v.getId());
						for(FactorNode g : v.getNeighbour())
						{
							System.out.print("  Factors ");
							System.out.println(g.getId()+"; ");
						}
					}
				}
			}
		}*/
				
	}
	
	/**
	 * Removes all agents of the factor node and send them to upper factor node
	 * @param f Factor Node
	 */
	@SuppressWarnings("unchecked")
	private void moveAgentsUp(FactorNode f)
	{
		VariableNode leader = f.leader;
		FactorNode upperFactor = null;
		
		for(FactorNode g : leader.getNeighbour())
		{
			if(!g.equals(f) && g.degree < f.degree)
				upperFactor = g;
		}
		
		if(f.getNeighbour().size() > 0)
		{
			
			HashSet<VariableNode> tmpVars = (HashSet<VariableNode>) f.getNeighbour().clone();
			for(VariableNode v : tmpVars)
			{
				this.linkRemoval(f, v);
				this.linkInclusion(upperFactor, v);
				v.agent.assign(null);
				
				if(this.debug)
				{
					String step1 = "action:;move;agent:;"+v.getId()+";message:;Moving agent "+v.getId()+" to factor Node "+upperFactor.getId();
					
					//String step1 = "worker agent "+x.getId()+" send message with the value "+qTmp+" to factor "+f.getId();

					WriteFile.getInstance().openFile(this.logFileName);
					WriteFile.getInstance().write(step1, this.logFileName);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deleteValues(FactorGraph<Entity> env)
	{
		ArrayList<Value> tmp = (ArrayList<Value>) env.getSolvedValues().clone();
		ArrayList<Value> tmpUnsolved = (ArrayList<Value>) env.getUnsolvedValue().clone();
		
		for(Value v : tmp)
		{
			if(FactorNode.getFactorNode(v.getId()) != null){
				
				FactorNode oldF = FactorNode.getFactorNode(v.getId());
				
				this.moveAgentsUp(oldF);
				if(FactorNode.removeFactorNode(v.getId()))
				{
					this.removeFromTree(env, oldF);
				}
				//NectarSource.removeNectarSource(v.getId());
				if(this.debug)
				{
					String step1 = "action:;remove;factor:;"+v.getId()+";message:;Searching values in solved list to remove. Factor Node "+v.getId()+" removed.";
					
					//String step1 = "worker agent "+x.getId()+" send message with the value "+qTmp+" to factor "+f.getId();

					WriteFile.getInstance().openFile(this.logFileName);
					WriteFile.getInstance().write(step1, this.logFileName);
				}
			}
		}
		
		for(Value v : tmpUnsolved)
		{
			if(FactorNode.getFactorNode(v.getId()) != null){
				
				FactorNode oldF = FactorNode.getFactorNode(v.getId());
				this.moveAgentsUp(oldF);
				if(FactorNode.removeFactorNode(v.getId()))
				{
					this.removeFromTree(env, oldF);
				}
				
				if(this.debug)
				{
					String step1 = "action:;remove;factor:;"+v.getId()+";message:;Searching values in unsolved list to remove. Factor Node "+v.getId()+" was unsolved and it was removed.";
					//String step1 = "worker agent "+x.getId()+" send message with the value "+qTmp+" to factor "+f.getId();

					WriteFile.getInstance().openFile(this.logFileName);
					WriteFile.getInstance().write(step1, this.logFileName);
				}
			}
		}
		env.getRemovedValues().addAll(env.getSolvedValues());
		env.getRemovedValues().addAll(env.getUnsolvedValue());
		env.getValues().removeAll(env.getSolvedValues());
		env.getValues().removeAll(env.getUnsolvedValue());
		env.getSolvedValues().clear();
		env.getUnsolvedValue().clear();
	}
	
	@SuppressWarnings("static-access")
	private void upwardMessages(FactorGraph<Entity> env)
	{
		for(int k = (env.factor.keySet().size()-1); k > -1; k--)
		{
			//variables to factor, factor to leader 
			for(FactorNode f : env.factor.get(k))
			{
				
				BigDecimal q = BigDecimal.ZERO;
				for(VariableNode x : f.getNeighbour())
				{
					if(x.getId() != f.leader.getId()){
						BigDecimal qTmp = BigDecimal.ZERO;
						qTmp = this.computeMessageToFunction(x, f, env.time);
						q = q.add(qTmp);
						f.getFunction().addParametersCost(x.getId(), qTmp);
						
						if(this.debug)
						{
							String step1 = "message from;worker agent;"+x.getId()+";to factor;"+f.getId()+";led by;"+f.leader.getId()+";value;"+qTmp;
							//String step1 = "worker agent "+x.getId()+" send message with the value "+qTmp+" to factor "+f.getId();

							WriteFile.getInstance().openFile(this.logFileName);
							WriteFile.getInstance().write(step1, this.logFileName);
						}
						
					}
				}
				if(!q.equals(BigDecimal.ZERO))
				{
					f.getFunction().addParametersCost(f.leader.getId(), q);
					
					if(this.debug)
					{
						String step1 = "message from;factor;"+f.getId()+";to leader agent;"+f.leader.getId()+";value;"+q;
						//String step1 = "factor "+f.getId()+" send message with the value "+q+" to the leader agent "+f.leader.getId();

						WriteFile.getInstance().openFile(this.logFileName);
						WriteFile.getInstance().write(step1, this.logFileName);
					}
				}
				
				//factor to variable, leader to its leader
				for(FactorNode g : f.leader.getNeighbour())
				{
					if(g != f)
					{
						//System.out.println("evaluating leader of the leaders");
						g.getFunction().addParametersCost(f.leader.getId(), f.getFunction().evaluate(f.leader.getId()));
						
						if(this.debug)
						{
							String step1 = "message from;leader agent;"+f.leader.getId()+";to factor;"+g.getId()+";led by;"+g.leader.getId()+";value;"+f.getFunction().evaluate(f.leader.getId());
							//String step1 = "leader agent "+f.leader.getId()+" send message with the value "+f.getFunction().evaluate(f.leader.getId())+" to the factor "+g.getId();

							WriteFile.getInstance().openFile(this.logFileName);
							WriteFile.getInstance().write(step1, this.logFileName);
						}
						
					}
				}
			}
			
			
		}
	}
	
	private void downwardMessages(FactorGraph<Entity> env)
	{
		for(int k = 0; k < env.factor.keySet().size(); k++)
		{
			for(FactorNode f : env.factor.get(k))
			{
				BigDecimal alphaTmp = BigDecimal.ZERO;
				BigDecimal alpha = BigDecimal.ONE;
				
				BigDecimal o_u = f.getFunction().evaluate(f.leader.getId());
				//get normalization factor
				if(k != 0)
				{
					for(FactorNode g : f.leader.getNeighbour())
					{
						if(g != f){
							alpha = new BigDecimal(g.getFunction().getAlpha().doubleValue());
						}
					}
				}
				//send message to each variable
				for(VariableNode x : f.getNeighbour())
				{
					
					if(x.getId() != f.leader.getId())
					{
						BigDecimal p_u = new BigDecimal(x.agent.getUtilityPerception());
						
						//BigDecimal p_u = f.getFunction().evaluate(x.getId());
						//System.out.println("Overall Utility "+o_u);
						//System.out.println("Partial Utility "+p_u);
						//System.out.println("o_u / p_u "+o_u.divide(p_u, 5, BigDecimal.ROUND_CEILING));
						//System.out.println("p_u / o_u "+p_u.divide(o_u, 3, BigDecimal.ROUND_CEILING));
						if(!o_u.equals(BigDecimal.ZERO)){
							BigDecimal r = (o_u.multiply(alpha));
							r = (o_u.divide(p_u, 10, BigDecimal.ROUND_CEILING));
							x.agent.setValueProbability(r.doubleValue());
							alphaTmp = alphaTmp.add(r);
							f.getFunction().addParametersCost(x.getId(), r);
							if(this.debug)
							{
								String step1 = "message from;factor;"+f.getId()+";led by;"+f.leader.getId()+";to worker agent;"+x.getId()+";value;"+r;
								//String step1 = "factor "+f.getId()+" send down a message with the value "+r+" to the worker agent "+x.getId();

								WriteFile.getInstance().openFile(this.logFileName);
								WriteFile.getInstance().write(step1, this.logFileName);
							}
							
						}/*else{
							x.agent.setValueProbability((p_u.multiply(alpha).doubleValue()));
							alphaTmp.add(alphaTmp).add(p_u.multiply(alpha));
							f.getFunction().addParametersCost(x.getId(), (p_u.multiply(alpha)));
						}*/
					}
					
				}
				f.getFunction().setAlpha(alphaTmp.doubleValue());
				
				if(this.debug)
				{
					String step1 = "factor "+f.getId();

					WriteFile.getInstance().openFile(this.logFileName);
					WriteFile.getInstance().write(step1, this.logFileName);
				}
				
			}
		}
		
	}
	
	/**
	 * verifies if the factor node exists or not
	 *  
	 * @param id ID of the factoor node
	 * @return true or false
	 */
	public boolean existsFactor(int id)
	{
		if(FactorNode.getFactorNode(id) != null)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * creates a new factor node in the factor graph
	 * 
	 * 
	 * @param var Active Variable Node
	 * @param currentFactor Active Factor Node
	 * @param env Factor Graph Environment
	 */
	protected void createNewFactor(VariableNode var, FactorNode currentFactor, FactorGraph<Entity> env)
	{
		VariableNode v = VariableNode.getVariableNode(var.getId());
		FactorNode f = FactorNode.getFactorNode(currentFactor.getId());
				
		FactorNode newFactor = FactorNode.putFactorNode(v.agent.getValue(), new NCAFunction());
		
		int degree = f.degree;

		//try to remove and define new position in the tree 
		if(this.tryToRemove(f, v, env))
		{
			if(this.debug)
			{
				String step1 = "agent;"+var.getId()+";creates;new factor;"+newFactor.getId()+";level;"+degree+";factor removed;"+f.getId();
				//String step1 = "agent "+var.getId()+" creates a new factor node "+newFactor.getId()+" in level "+degree+" while factor "+var.agent.getOldValue()+" was removed";

				WriteFile.getInstance().openFile(this.logFileName);
				WriteFile.getInstance().write(step1, this.logFileName);
			}
			newFactor.degree = degree;
		}else{
			newFactor.degree = degree + 1;
			if(this.debug)
			{
				String step1 = "agent;"+var.getId()+";creates;new factor;"+newFactor.getId()+";level;"+(degree+1)+";below factor;"+f.getId()+";led by;"+f.leader.getId();
				//String step1 = "agent "+var.getId()+" creates a new factor node "+newFactor.getId()+" in level "+(degree+1)+" below factor "+var.agent.getOldValue();

				WriteFile.getInstance().openFile(this.logFileName);
				WriteFile.getInstance().write(step1, this.logFileName);
			}
		}
		
		newFactor.addNeighbour(v);
		
		newFactor.getFunction().addParametersCost(v.getId(), new BigDecimal(v.agent.getUtilityPerception()));
		
		newFactor.leader = v;
		
		//measure communication
		//if(this.debug)
			//FGNetworkEvaluator.measureCommunication(f, newFactor, v);
		
		this.insertFactorInTheTree(env, newFactor);
	}
	
	/**
	 * 
	 * @param var Variable Node that is trying to lead the factor
	 * @param currentFactor Current Factor Node
	 * @param env Environment Factor Graph
	 * @return Integer it returns the id of the leader of the factor in the join action
	 */
	protected Integer joinFactorNode(VariableNode var, FactorNode currentFactor, FactorGraph<Entity> env)
	{
		//variable
		VariableNode v = VariableNode.getVariableNode(var.getId());
		//current factor
		FactorNode f = FactorNode.getFactorNode(currentFactor.getId());
		
		//try to remove old factor of the agent
		//if it is not possible to remove factor then remove link between variable and old factor
		if(!this.tryToRemove(f, v, env))
		{
			if(this.debug)
			{
				String step1 = "removed link between;factor;"+f.getId()+";variable;"+v.getId();
				//String step1 = "link removed between factor "+f.getId()+" and variable "+v.getId();
				String step2 = "new action;agent;"+v.getId()+";is;"+v.agent.getValue()+";old action was;"+f.getId();
				//String step2 = "new action of agent "+v.getId()+" is "+v.agent.getValue()+" old was "+v.agent.getOldValue();
				
				WriteFile.getInstance().openFile(this.logFileName);
				WriteFile.getInstance().write(step1, this.logFileName);
				WriteFile.getInstance().write(step2, this.logFileName);
			}
			this.linkRemoval(f, v);
		}else{
			if(this.debug)
			{
				String step1 = "removed;"+f.getId();
				String step2 = "new action;agent;"+v.getId()+";is;"+v.agent.getValue()+";old action was;"+f.getId();
				
				WriteFile.getInstance().openFile(this.logFileName);
				WriteFile.getInstance().write(step1, this.logFileName);
				WriteFile.getInstance().write(step2, this.logFileName);
			}
		}
		
		//enter new factor
		FactorNode existingFactor = FactorNode.getFactorNode(v.agent.getValue());
		existingFactor.addNeighbour(v);
		existingFactor.getFunction().addParametersCost(v.getId(), new BigDecimal(v.agent.getUtilityPerception()));

		//measure type of communication
		//if(this.debug)
			//FGNetworkEvaluator.measureCommunication(f, existingFactor, v);
			
		return this.defineLeadership(v, existingFactor);
	}

	
	/**
	 * get actions of variable nodes 
	 * @param env
	 */
	@SuppressWarnings("unchecked")
	private void action(FactorGraph<Entity> env)
	{
		if(this.debug)
		{
			Integer time = FactorGraph.time;
			
			WriteFile.getInstance().openFile(this.logFileName);
			WriteFile.getInstance().write(time.toString(), this.logFileName);
		}
		HashSet<Integer> leaders = new HashSet<Integer>();
		HashSet<Integer> readyAgents = new HashSet<Integer>();
		
		//for each level (bottom-up)
		for(int k = (env.factor.keySet().size() - 1); k > -1; k--)
		{
			
			//clone factor in the level
			ArrayList<FactorNode> fTmp = (ArrayList<FactorNode>) env.factor.get(k).clone();
			
			//for each factor (in each level of the three)
			for(FactorNode f : fTmp)
			{
				//clone variables of the factor
				HashSet<VariableNode> tmpVars = (HashSet<VariableNode>) f.getNeighbour().clone();
				
				//for each variable in each factor
				for(VariableNode vTmp : tmpVars)
				{
					if(vTmp.agent instanceof Human){
						//if var is not ready
						//if var already did  not make its action in this timestep
						if(!readyAgents.contains(vTmp.getId())){
							
							//if var is not leader of this factor
							//and var is not leader of any other factor
							//or var is alone in the factor
							if( (f.leader.getId() != vTmp.getId() && !leaders.contains(vTmp.getId())) || FactorNode.getFactorNode(f.getId()).getNeighbour().size() <= 1)
							{
								
								VariableNode var = VariableNode.getVariableNode(vTmp.getId());
								FactorNode factor = FactorNode.getFactorNode(f.getId());
						
								if(var.agent.getValue() != null){
									if(this.existsFactor(var.agent.getValue()))
									{
										if(!var.agent.getValue().equals(f.getId()))
										{
											leaders.add(this.joinFactorNode(var, factor, env));
										}
									}else{
										if(!var.agent.getValue().equals(f.getId())){
											this.createNewFactor(var, factor, env);
											leaders.add(var.getId());
										}
									}
								}
								if(this.debug){
									FGNetworkEvaluator.measureLeading(FactorNode.getFactorNode(f.getId()), VariableNode.getVariableNode(vTmp.getId()), FactorNode.getFactorNode(vTmp.agent.getValue()));
									FGNetworkEvaluator.measureCommunication(FactorNode.getFactorNode(f.getId()), FactorNode.getFactorNode(vTmp.agent.getValue()), VariableNode.getVariableNode(vTmp.getId()));
								}
								//FGNetworkEvaluator.measureLeading(f, vTmp);
							}else{
								if(this.debug){
									FGNetworkEvaluator.measureLeading(FactorNode.getFactorNode(f.getId()), VariableNode.getVariableNode(vTmp.getId()), FactorNode.getFactorNode(vTmp.agent.getValue()));
									FGNetworkEvaluator.measureCommunication(FactorNode.getFactorNode(f.getId()), FactorNode.getFactorNode(vTmp.agent.getValue()), VariableNode.getVariableNode(vTmp.getId()));
								}
							}
							/*else if((f.leader.getId() == vTmp.getId() || leaders.contains(vTmp.getId())) && !readyAgents.contains(vTmp.getId()) ){
								FGNetworkEvaluator.measureLeading(f, vTmp);
							}else
							if(!readyAgents.contains(vTmp.getId())){
								FGNetworkEvaluator.measureLeading(f, vTmp);
							}*/
							readyAgents.add(vTmp.getId());
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Inserts new factor into the tree structured factor graph
	 * @param env Factor Graph Environment
	 * @param newFactor Created New Factor
	 */
	private void insertFactorInTheTree(FactorGraph<Entity> env, FactorNode newFactor)
	{
		//insert factor into the tree
		if(env.factor.containsKey(newFactor.degree))
		{
			env.factor.get(newFactor.degree).add(newFactor);
		}else{
			env.factor.put(newFactor.degree, new ArrayList<FactorNode>());
			env.factor.get(newFactor.degree).add(newFactor);
		}
	}
	
	/**
	 * link removal from factor and variable nodes
	 * 
	 * @param f Factor Node 
	 * @param v Variable Node
	 */
	private void linkRemoval(FactorNode f, VariableNode v)
	{
		FactorNode oldFactor = FactorNode.getFactorNode(f.getId());
		VariableNode var = VariableNode.getVariableNode(v.getId());
		if(oldFactor.getNeighbour().contains(var))
		{
			oldFactor.removeNeighbour(var);
		}
		if(var.getNeighbour().contains(oldFactor))
		{
			var.removeNeighbour(oldFactor);
		}
	}
	
	/**
	 * creates link between a factor and variable node
	 * 
	 * @param f Factor Node
	 * @param v Variable Node
	 */
	private void linkInclusion(FactorNode f, VariableNode v)
	{
		FactorNode factor = FactorNode.getFactorNode(f.getId());
		VariableNode var = VariableNode.getVariableNode(v.getId());
		
		if(!factor.getNeighbour().contains(var)){
			factor.addNeighbour(var);
			factor.getFunction().addParametersCost(var.getId(), new BigDecimal(var.agent.getUtilityPerception()));
		}
		if(!var.getNeighbour().contains(factor)){
			var.addNeighbour(factor);
		}
	}
	
	/**
	 * Remove an old factor node from the tree structured factor graph
	 * 
	 * @param env Factor Graph Environment
	 * @param oldFactor Old Factor Node
	 */
	private void removeFromTree(FactorGraph<Entity> env, FactorNode oldFactor)
	{
		if(env.factor.containsKey(oldFactor.degree))
		{
			if(env.factor.get(oldFactor.degree).contains(oldFactor))
			{
				env.factor.get(oldFactor.degree).remove(oldFactor);
			}
			if(env.factor.get(oldFactor.degree).size() < 1)
				env.factor.remove(oldFactor.degree);
		}
	}
	
	/**
	 * - remove variable from the old factor
	 * - remove old factor from the variable
	 * - if old factor is empty and it is not root factor
	 * -- remove old factor from the tree
	 * -- remove old factor from factor list
	 * 
	 * @param oldFactor Old task chosen by the agent
	 * @param var The agent
	 * @param env The environment
	 */
	protected boolean tryToRemove(FactorNode oldFactor, VariableNode var, FactorGraph<Entity> env)
	{			
		if(oldFactor.degree != 0){
			if(oldFactor.getNeighbour().size() <= 1)
			{
				if(FactorNode.removeFactorNode(oldFactor.getId()))
				{
					//remvove link between factor and variable
					this.linkRemoval(oldFactor, var);
					
					//remove from tree
					this.removeFromTree(env, oldFactor);
					
					return true;
				}
			}
		}
		return false;
		
	}
	
	/**
	 * compares the variable with the current leader of the factor node
	 * makes changes between the leaders according to their perceptions
	 * 
	 * @param var Variable Node
	 */
	private Integer defineLeadership(VariableNode var, FactorNode chosenFactor){
		
		VariableNode currentLeader = VariableNode.getVariableNode(chosenFactor.leader.getId());
		
		if(var.agent.getUtilityPerception() < currentLeader.agent.getUtilityPerception())
		{
			//list all factor of the current leader
			for(FactorNode g : currentLeader.getNeighbour())
			{
				//previous connections of the current leader agent (except low factor)
				if(g.getId() != chosenFactor.getId())
				{
					if(this.debug)
					{
						String changeLeader = "change leader;from variable;"+currentLeader.getId()+";to;"+var.getId()+";in factor;"+chosenFactor.getId()+";and variable;"+var.getId()+";is now connected to factor;"+g.getId()+";and;"+chosenFactor.getId();
						WriteFile.getInstance().openFile(this.logFileName);
						WriteFile.getInstance().write(changeLeader, this.logFileName);
					}
					
					FactorNode factorG = FactorNode.getFactorNode(g.getId());
					this.linkRemoval(factorG, currentLeader);

					//add new leader to prior factor
					this.linkInclusion(factorG, var);
	
				}
			}
			chosenFactor.leader = var;
			return var.getId();
		}else{
			if(this.debug)
			{
				String changeLeader = "agent;"+var.getId()+";entered the factor;"+chosenFactor.getId()+";but it is not the leader; and it is now connected to;"+chosenFactor.getId();
				WriteFile.getInstance().openFile(this.logFileName);
				WriteFile.getInstance().write(changeLeader, this.logFileName);
			}
		}
		return currentLeader.getId();
	}
	

	/**
	 * message from variable node to factor node
	 * sends 
	 * @param x
	 * @param f
	 * @param time
	 * @return
	 * @throws VariableNotSetException
	 */
	private BigDecimal computeMessageToFunction(VariableNode x, FactorNode f, int time){
		BigDecimal q = BigDecimal.ONE;
		
		for(VariableNode y : f.getNeighbour())
		{
			if(y != x){
				if(y != f.leader)
				{
					BigDecimal u = new BigDecimal(y.agent.getUtilityPerception());
					q = q.multiply(u);
					//q.multiply(f.getFunction().evaluate(y.getId()));
				}else if(y == f.leader){
					if(f.degree == 0 && time <= 1)
					{
						BigDecimal exp = new BigDecimal(Math.exp(- Math.sqrt(Math.pow((1d - x.agent.getUtilityPerception().doubleValue()), 2))));
						//BigDecimal exp = new BigDecimal(Math.exp(- Math.sqrt(Math.pow((1d - f.getFunction().evaluate(y.getId()).doubleValue()), 2))));
						q = q.multiply(exp);
					}
					else if(f.degree == 0)
					{
						BigDecimal utility = f.getFunction().evaluate(f.getId());
						BigDecimal exp = new BigDecimal(Math.exp(- Math.sqrt(Math.pow((utility.doubleValue() - x.agent.getUtilityPerception().doubleValue()), 2))));
						//BigDecimal exp = new BigDecimal(Math.exp(- Math.sqrt(Math.pow((1d - f.getFunction().evaluate(y.getId()).doubleValue()), 2))));
						q = q.multiply(exp);
					}
					else
					{
						BigDecimal exp = new BigDecimal(Math.exp(- Math.sqrt(Math.pow((x.agent.getUtilityPerception() - y.agent.getUtilityPerception()), 2))));
						//BigDecimal exp = new BigDecimal(Math.exp(- Math.sqrt(Math.pow((f.getFunction().evaluate(x.getId()).doubleValue() - f.getFunction().evaluate(y.getId()).doubleValue()), 2))));
						q = q.multiply(exp);
					}
				}
			}
			
		}
		return q;
	}
	
	
	
}
