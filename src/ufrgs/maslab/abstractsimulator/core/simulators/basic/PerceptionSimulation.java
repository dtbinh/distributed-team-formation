package ufrgs.maslab.abstractsimulator.core.simulators.basic;

import java.util.ArrayList;
import java.util.Arrays;

import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Environment;
import ufrgs.maslab.abstractsimulator.core.Value;
import ufrgs.maslab.abstractsimulator.core.Variable;
import ufrgs.maslab.abstractsimulator.core.simulators.DefaultSimulation;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.util.Utilities;

public class PerceptionSimulation extends DefaultSimulation {

	private String configFile = "config.properties";
	private String exceptionFile = "exception.properties";
	
	@SuppressWarnings("unchecked")
	@Override
	public void simulate(Entity env) throws SimulatorException {
		
		if(!(env instanceof Environment))
			throw new SimulatorException(Transmitter.getProperty(this.exceptionFile, "exception.not.environment"));
		
		Environment<Entity> environment = (Environment<Entity>)env;
		
		for(Variable var : environment.getVariables())
		{
			if(var != null && environment != null)
				this.sense(var, environment);
			

			
				//updates age of the agent
				var.setTime(environment.time);
				//runs the act command for each agent
				var.act(environment.time);
			
		}
		
	}
	
	/**
	 * function to analyse the variable and to define the domain of each agent
	 * based on the radius parameter.
	 * @param env
	 * @throws SimulatorException
	 */
	private void sense(Variable var, Environment<Entity> env) throws SimulatorException{
		
			ArrayList<Double> e1 = new ArrayList<Double>(
				    Arrays.asList(var.getX().doubleValue(), var.getY().doubleValue()));
			
			ArrayList<Entity> domain = new ArrayList<Entity>();
			//HashMap<Integer, Class<? extends Entity>> domain = new HashMap<Integer, Class<? extends Entity>>();
			Integer radius = Transmitter.getIntConfigParameter(this.configFile, "agent.radius");
			for(Value val : env.getValues())
			{
				ArrayList<Double> e2 = new ArrayList<Double>(
					    Arrays.asList(val.getX().doubleValue(), val.getY().doubleValue()));
				
				if(Utilities.euclideanDistance(e1, e2) <= radius.doubleValue())
					domain.add(val);
					//domain.put(val.getId(),val.getClass());
				
			}
			env.findVariableByID(var.getId(), var.getClass()).getDomain().clear();
			env.findVariableByID(var.getId(), var.getClass()).setDomain(domain);
		
	}

	@Override
	public void simulate(Entity... entity) throws SimulatorException {
		// TODO Auto-generated method stub
		
	}
	

}
