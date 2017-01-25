package ufrgs.maslab.abstractsimulator.core.simulators.disaster;

import java.util.HashMap;

import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Environment;
import ufrgs.maslab.abstractsimulator.core.Evaluator;
import ufrgs.maslab.abstractsimulator.core.Value;
import ufrgs.maslab.abstractsimulator.core.Variable;
import ufrgs.maslab.abstractsimulator.values.FireBuildingTask;
import ufrgs.maslab.abstractsimulator.variables.FireFighter;

public class UrbanDisasterEvaluator extends Evaluator {

	public HashMap<Integer, Double> quality = new HashMap<Integer, Double>();
	public HashMap<Integer, Double> allocatedTasks = new HashMap<Integer, Double>();
	public HashMap<Integer, Double> notAllocatedTasks = new HashMap<Integer, Double>();
	public HashMap<Integer, Double> idleAgents = new HashMap<Integer, Double>();
	public int time;
	
	@Override
	public Double qualitySolution(Environment<? extends Entity> env) {
		
		HashMap<Integer, Double> skill = new HashMap<Integer, Double>();
		double idle = 0d;
		double allocated = 0d;
		double notAllocated = 0d;
		
		for(Variable v : env.getVariables())
		{
			FireFighter f = null;
			if(v instanceof FireFighter){
				f = (FireFighter)v;
				if(f.getValue() != null)
				{
					double s = 0d;
					if(skill.containsKey(f.getValue()))
						s = skill.get(f.getValue());
					s += (f.getFireFighting() + f.getDexterity());
					
					skill.put(f.getValue(), s);
					
					
				}else{
					idle++;
				}
			}				
		}
		double q = 0d;
		for(Value t : env.getValues())
		{
			FireBuildingTask b = null;
			if(t instanceof FireBuildingTask)
			{
				b = (FireBuildingTask)t;
				if(skill.containsKey(b.getId()))
				{
					allocated++;
					q += (b.getSuccess()/skill.get(b.getId()));
				}else{
					notAllocated++;
					q += b.getSuccess();
				}
			}
		}
		this.quality.put(Environment.time, q);
		this.allocatedTasks.put(Environment.time, allocated);
		this.notAllocatedTasks.put(Environment.time, notAllocated);
		this.idleAgents.put(Environment.time, idle);
		this.time = Environment.time;
		return q;
	}

	@Override
	public HashMap<Integer, Double> quality() {
		return this.quality;
	}
	
	public HashMap<Integer, Double> allocatedValues()
	{
		return this.allocatedTasks;
	}
	
	public HashMap<Integer, Double> notAllocatedValues()
	{
		return this.notAllocatedTasks;
	}
	
	public HashMap<Integer, Double> idleVariables()
	{
		return this.idleAgents;
	}

	@Override
	public int getTime() {
		
		return this.time;
	}

}
