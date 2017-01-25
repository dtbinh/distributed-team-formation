package ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph;

import java.util.HashMap;
import java.util.HashSet;

import ufrgs.maslab.abstractsimulator.core.BlackBox;
import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Environment;
import ufrgs.maslab.abstractsimulator.core.simulators.DefaultSimulation;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.util.WriteFile;
import ufrgs.maslab.abstractsimulator.variables.Human;

public class FGNetworkEvaluator extends DefaultSimulation {
	

	private String logFile;
	
	private String logComm;
	
	private String algorithmName;
	
	private Integer algorithmRun;
	
	protected static HashMap<Double, Integer> cooperation = new HashMap<Double, Integer>();
	
	protected static HashMap<Double, Integer> insubordination = new HashMap<Double, Integer>();
	
	protected static HashMap<Double, Integer> executing = new HashMap<Double, Integer>();
	
	protected static HashMap<Double, Integer> leading = new HashMap<Double, Integer>();
	
	private int time = 0;
	
	@SuppressWarnings("unchecked")
	@Override
	public void simulate(Entity entity) throws SimulatorException {
		
		if(!(entity instanceof FactorGraph))
			throw new SimulatorException(Transmitter.getProperty("exception.properties", "exception.not.factorgraph"));
		
		FactorGraph<Entity> env = (FactorGraph<Entity>)entity;
		
		if(FactorGraph.time == 0)
		{
			this.algorithmName = BlackBox.getAlgorithmName();
			this.algorithmRun = BlackBox.getAlgorithmRun();
			this.saveHeader();
			this.saveHeaderComm();
		}else if(this.time != FactorGraph.time)
		{
			this.saveHeader();
			this.time = FactorGraph.time;
		}
		this.logExperiment(env);
		this.logCommunication();
		
		cooperation.clear();
		insubordination.clear();
		executing.clear();
		leading.clear();
	}
	
	public void saveHeaderComm()
	{
		logComm = Transmitter.getProperty("files.properties", "network.communication")+"_"+this.algorithmName+"_"+this.algorithmRun;
		
		WriteFile.getInstance().openFile(logComm);
		String header = "time;omega;insubordination;cooperation;executing;leading";
		WriteFile.getInstance().write(header, logComm);
	}
	
	public void logCommunication()
	{		
		String step = "";
		for(Double d : executing.keySet())
		{
			step = this.time+";"
					+d+";"
					+insubordination.get(d)+";"
					+cooperation.get(d)+";"
					+executing.get(d)+";"
					+leading.get(d)+";";
			
			WriteFile.getInstance().openFile(logComm);
			WriteFile.getInstance().write(step, logComm);
			
		}
		
	}
	
	
	
	public void saveHeader()
	{
		logFile = Transmitter.getProperty("files.properties", "network.evaluator")+"_"+this.algorithmName+"_"+this.algorithmRun;
		
		WriteFile.getInstance().openFile(logFile);
		String header = "time;degree;value;leader;acuracy;utility;neighbours;";
		WriteFile.getInstance().write(header, logFile);
	}
	
	public void logExperiment(FactorGraph<Entity> env)
	{
		
		HashSet<Integer> assigned = new HashSet<Integer>();
		
		for(Integer k : env.factor.keySet())
		{
			for(FactorNode f : env.factor.get(k))
			{
				WriteFile.getInstance().openFile(logFile);
				
				String step = 
							Environment.time+";"
							+k+";"
							+f.getId()+";"
							+f.leader.getId()+";"
							+f.getFunction().evaluate(f.leader.getId())+";"
							+f.getFunction().getAlpha()+";";
				for(VariableNode n : f.getNeighbour())
				{
					step += n.getId()+";";
					assigned.add(n.getId());
				}
				
				
				WriteFile.getInstance().write(step, logFile);
				
			}
		}
		
		
	}
	
	public static void measureLeading(FactorNode f, VariableNode v, FactorNode newFactor)
	{
		int oldExecuting = 0;
		int oldLeading = 0;
		Double idx = ((Human)v.agent).getOmega();
		
		if(leading.containsKey(idx))
			oldLeading = leading.get(idx);
		if(executing.containsKey(idx))
			oldExecuting = executing.get(idx);
		if(newFactor != null){
			if((f.leader.getId() == v.getId()) || newFactor.leader.getId() == v.getId())
			{
				oldLeading++;
			}else{
				oldExecuting++;
			}
		}else
			if(f.leader.getId() == v.getId())
			{
				oldLeading++;
			}else{
				oldExecuting++;
			}
		leading.put(idx, oldLeading);
		executing.put(idx, oldExecuting);
		
	}

	/**
	 * get information about the old and the new action of the agent
	 * counts the level of insubordination between leader and worker agent
	 * and cooperation between leader and worker agent
	 * 
	 * @param oldF Old factor node of the agent
	 * @param newF New factor node of the agent
	 * @param v Variable Node
	 */
	public static void measureCommunication(FactorNode oldF, FactorNode newF, VariableNode v)
	{
		int oldValueCooperation = 0;
		int oldValueInsubordination = 0;
		Double idx = ((Human)v.agent).getOmega();
		
		
		if(cooperation.containsKey(idx))
			oldValueCooperation = cooperation.get(idx);
		
		if(insubordination.containsKey(idx))
			oldValueInsubordination = insubordination.get(idx);
		
		if(oldF != null && newF != null)
		{
			if((newF.degree < oldF.degree) || (newF.degree == oldF.degree && !newF.equals(oldF)))
			{
				oldValueInsubordination++;
			}else{
				oldValueCooperation++;
			}
		}else{
			oldValueCooperation++;
		}
		cooperation.put(idx, oldValueCooperation);
		insubordination.put(idx, oldValueInsubordination);
		
	}
	
	@Override
	public void simulate(Entity... entity) throws SimulatorException {
		// TODO Auto-generated method stub
		
	}

	
}
