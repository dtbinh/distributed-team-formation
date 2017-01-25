package ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering;

import java.util.HashMap;

import ufrgs.maslab.abstractsimulator.core.BlackBox;
import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.simulators.DefaultSimulation;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.util.WriteFile;
import ufrgs.maslab.abstractsimulator.variables.Human;

public class BeeClusterEvaluator extends DefaultSimulation {
	
	private String logFile;
	
	private String algorithmName;
	
	private Integer algorithmRun;
	
	//similar to cooperation
	protected static HashMap<Double, Integer> watching = new HashMap<Double, Integer>();
	
	//similar to insubordination
	protected static HashMap<Double, Integer> visiting = new HashMap<Double, Integer>();
	
	//similar to leading
	protected static HashMap<Double, Integer> dancing = new HashMap<Double, Integer>();
	
	private int time = 0;

	@SuppressWarnings("unchecked")
	@Override
	public void simulate(Entity entity) throws SimulatorException {
		
		if(!(entity instanceof BeeClusteringEnvironment))
			throw new SimulatorException(Transmitter.getProperty("exception.properties", "exception.not.beeclustering"));
		
		BeeClusteringEnvironment<Entity> env = (BeeClusteringEnvironment<Entity>)entity;
		
		if(BeeClusteringEnvironment.time == 0)
		{
			this.algorithmName = BlackBox.getAlgorithmName();
			this.algorithmRun = BlackBox.getAlgorithmRun();
			this.saveHeader();
		}else if(this.time != BeeClusteringEnvironment.time)
		{
			this.saveHeader();
			this.time = BeeClusteringEnvironment.time;
		}
		
		
		this.logBeeClustering();
		
		watching.clear();
		visiting.clear();
		dancing.clear();
		// TODO Auto-generated method stub
		
	}
	
	public static void measureBehaviour(Bee i)
	{
		int oldWatching = 0;
		int oldVisiting = 0;
		int oldDancing = 0;
		
		Double idx = ((Human)i.agent).getOmega();
		
		if(watching.containsKey(idx))
			oldWatching = watching.get(idx);
		if(visiting.containsKey(idx))
			oldVisiting = visiting.get(idx);
		if(dancing.containsKey(idx))
			oldDancing = dancing.get(idx);
		
			
		if(i.getState() == "d")
		{
			oldDancing++;
		}else if(i.getState() == "w")
		{
			oldWatching++;
		}else if(i.getState() == "v")
		{
			oldVisiting++;
		}
		
		watching.put(idx, oldWatching);
		visiting.put(idx, oldVisiting);
		dancing.put(idx, oldDancing);
	}
	
	public void saveHeader()
	{
		logFile = Transmitter.getProperty("files.properties", "beeclustering.behaviour")+"_"+this.algorithmName+"_"+this.algorithmRun;
		
		WriteFile.getInstance().openFile(logFile);
		String header = "time;omega;watching;visiting;dancing";
		WriteFile.getInstance().write(header, logFile);
	}
	
	public void logBeeClustering()
	{		
		String step = "";
		for(Double d : watching.keySet())
		{
			step = this.time+";"
					+d+";"
					+watching.get(d)+";"
					+visiting.get(d)+";"
					+dancing.get(d)+";";
			
			WriteFile.getInstance().openFile(logFile);
			WriteFile.getInstance().write(step, logFile);
			
		}
		
	}

	@Override
	public void simulate(Entity... entity) throws SimulatorException {
		// TODO Auto-generated method stub
		
	}

}
