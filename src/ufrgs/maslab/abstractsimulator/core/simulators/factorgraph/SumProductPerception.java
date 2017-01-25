package ufrgs.maslab.abstractsimulator.core.simulators.factorgraph;

import java.util.ArrayList;
import java.util.Arrays;

import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FactorGraph;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FactorNode;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.VariableNode;
import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Value;
import ufrgs.maslab.abstractsimulator.core.Variable;
import ufrgs.maslab.abstractsimulator.core.simulators.basic.PerceptionSimulation;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.util.Utilities;
import ufrgs.maslab.abstractsimulator.variables.FireStation;

public class SumProductPerception extends PerceptionSimulation {
	
	private String configFile = "config.properties";
	private String exceptionFile = "exception.properties";
	
	@SuppressWarnings("unchecked")
	@Override
	public void simulate(Entity env) throws SimulatorException {
		
		if(!(env instanceof FactorGraph))
			throw new SimulatorException(Transmitter.getProperty(this.exceptionFile, "exception.not.environment"));
	
		FactorGraph<Entity> environment = (FactorGraph<Entity>)env;
		
		//each variable updates their local view
		for(int k = 0; k < environment.factor.keySet().size(); k++){
		//for(ArrayList<FactorNode> arrFactor : environment.factor.values()){
			for(FactorNode f : environment.factor.get(k)){
				//perception of the leaders which are subordinated to factor zero
				if(f.degree == 0){
				//if(f.leader.agent instanceof FireStation){
					for(VariableNode var : f.getNeighbour())
					{
						
						if(var != null && environment != null){
							this.sense(var, environment);
						}
						
					}
				}else{
					VariableNode leader = f.leader;
					for(VariableNode var : f.getNeighbour())
					{
						
						if(var != leader)
							this.shareDomain(leader, var, environment.time);
					}
				}
			}
		}
		
	}
	
	/**
	 * leader agents share the domain values with worker agents
	 * D_w = D_a
	 * 
	 * @param leader
	 * @param worker
	 * @param time
	 */
	private void shareDomain(VariableNode leader, VariableNode worker, int time)
	{
		
		Variable l = ((Variable)leader.agent);
		
		Variable a = ((Variable)worker.agent);
		
		//System.out.println("l "+l.getDomain().size());
		//System.out.println("a "+a.getDomain().size());
		ArrayList<Double> e1 = new ArrayList<Double>(Arrays.asList(a.getX().doubleValue(), a.getY().doubleValue()));
		ArrayList<Entity> domain = new ArrayList<Entity>();
		
		//worker.clearArguments();
		Double maxValue = a.getMostDistanceTask();
		Double minValue = a.getMostClosestTask();
		
		Class<? extends Value> vClass = null;
		for(Entity valEnt : l.getDomain())
		{
			Value val = (Value)valEnt;
			ArrayList<Double> e2 = new ArrayList<Double>(
					Arrays.asList(val.getX().doubleValue(), val.getY().doubleValue()));
			
			if(vClass == null)
				vClass = val.getClass();
			
			Double dist = Utilities.euclideanDistance(e1, e2);
			
			domain.add(val);
			//worker.addValue(NodeArgument.getNodeArgument(val));
			
			if(dist > maxValue)
			{
				maxValue = dist;
			}
			if(dist < minValue)
			{
				minValue = dist;
			}
			
			if(val.getX() < a.getClosestX())
				a.setClosestX(val.getX());
			if(val.getX() > a.getLongestX())
				a.setLongestX(val.getX());
			if(val.getY() < a.getClosestY())
				a.setClosestY(val.getY());
			if(val.getY() > a.getLongestY())
				a.setLongestY(val.getY());
			
			
			
		}
		
		a.getDomain().clear();
		a.setDomain(l.getDomain());
		
		a.setMostDistancetTask(maxValue);
		a.setMostClosestTask(minValue);
		if(a.getTime() <= time){
			
			a.setTime(time);
			a.act(time);
		}
		
		
	}
	
	
	/**
	 * function to analyse the variable and to define the domain of each agent
	 * based on the radius parameter.
	 * @param env
	 * @throws SimulatorException
	 */
	private void sense(VariableNode var, FactorGraph<Entity> env) throws SimulatorException{
		
			Variable agent = (Variable)var.agent;
			ArrayList<Double> e1 = new ArrayList<Double>(
				    Arrays.asList(agent.getX().doubleValue(), agent.getY().doubleValue()));
			
			ArrayList<Entity> domain = new ArrayList<Entity>();
			//HashMap<Integer, Class<? extends Entity>> domain = new HashMap<Integer, Class<? extends Entity>>();
			Integer radius = Transmitter.getIntConfigParameter(this.configFile, "agent.radius");
			//var.clearArguments();
			
			//use to internal normalization of the agents
			Double maxValue = agent.getMostDistanceTask();
			Double minValue = agent.getMostClosestTask();
			
			Class<? extends Value> vClass = null;			
			
			for(Value val : env.getValues())
			{
				ArrayList<Double> e2 = new ArrayList<Double>(
					    Arrays.asList(val.getX().doubleValue(), val.getY().doubleValue()));
				
				Double dist = Utilities.euclideanDistance(e1, e2);
				
				if(dist <= radius.doubleValue()){
					//adds value to domain of the variable
					//perception of the agent
					domain.add(val);
					if(vClass == null)
						vClass = val.getClass();
					//print value, x and y of the task
					//System.out.println("val "+val.getId()+" --> "+val.getValue()+" x and y <"+val.getX()+", "+val.getY()+">");
					
					//add the value at the node argument list of the variable node
					//var.addValue(NodeArgument.getNodeArgument(val));
					
					if(dist > maxValue)
					{
						maxValue = dist;
					}
					if(dist < minValue)
					{
						minValue = dist;
					}
					
					if(val.getX() < agent.getClosestX())
						agent.setClosestX(val.getX());
					if(val.getX() > agent.getLongestX())
						agent.setLongestX(val.getX());
					if(val.getY() < agent.getClosestY())
						agent.setClosestY(val.getY());
					if(val.getY() > agent.getLongestY())
						agent.setLongestY(val.getY());
					
					
				}
					//domain.put(val.getId(),val.getClass());
			}
			env.findVariableByID(agent.getId(), agent.getClass()).getDomain().clear();
			env.findVariableByID(agent.getId(), agent.getClass()).setDomain(domain);
			
			
			agent.setMostDistancetTask(maxValue);
			agent.setMostClosestTask(minValue);
			if(agent.getTime() <= env.time){
				
				
				//updates age of the agent
				agent.setTime(env.time);
				
				//agent action
				agent.act(env.time);
			}
			
			//set the state argument of the agent
			//if(vClass != null && agent.getValue() != null){
			//	Value v = env.findValueByID(agent.getValue(), vClass);
			//	var.setStateArgument(NodeArgument.getNodeArgument(v));
			//}
		
	}
	

}
