package ufrgs.maslab.abstractsimulator.core.simulators.basic;

import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Environment;
import ufrgs.maslab.abstractsimulator.core.Value;
import ufrgs.maslab.abstractsimulator.core.Variable;
import ufrgs.maslab.abstractsimulator.core.simulators.DefaultSimulation;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;

public class ValueRemover extends DefaultSimulation {

	private String exceptionFile = "exception.properties";

	@SuppressWarnings("unchecked")
	@Override
	public void simulate(Entity entity) throws SimulatorException {
		
		if(!(entity instanceof Environment))
			throw new SimulatorException(Transmitter.getProperty(this.exceptionFile, "exception.not.environment"));
		
		Environment<Entity> env = (Environment<Entity>)entity;
		
		for(Variable var : env.getVariables())
		{
			if(var.getValue() != null){
				Value t = env.findValueByID(var.getValue(), env.getValClass().get(var.getValue()));
				if(t != null){
					if(var.getDomain().contains(t))
					{
						int idx = var.getDomain().indexOf(t);
						var.getDomain().remove(idx);
						if(!var.getDomainValues().isEmpty())
							var.getDomainValues().remove(idx);
					}
					if(env.getValues().contains(t))
					{
						env.getSolvedValues().add(t);
						env.getValues().remove(t);
					}
				}
				var.assign(null);
			}
		}
		
		
	}

	@Override
	public void simulate(Entity... entity) throws SimulatorException {
		// TODO Auto-generated method stub
		
	}

}
