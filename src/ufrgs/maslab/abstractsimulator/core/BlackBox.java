package ufrgs.maslab.abstractsimulator.core;

import java.util.ArrayList;

import ufrgs.maslab.abstractsimulator.core.simulators.DefaultSimulation;
import ufrgs.maslab.abstractsimulator.core.simulators.basic.CommunicationSimulation;
import ufrgs.maslab.abstractsimulator.core.simulators.basic.PerceptionSimulation;
import ufrgs.maslab.abstractsimulator.core.simulators.basic.ValueRemover;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.log.ExperimentLogger;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.util.WriteFile;

public class BlackBox {
	
	/**
	 *  <ul>
	 *  <li>configuration file</li>
	 *  <li>contains all configuration parameters of the simulation</li>
	 *  </ul>
	 *  
	 */
	private String configFileName = "config.properties";
	
	private String messageFileName = "message.properties";
	
	private String exceptionFileName = "exception.properties";
	/**
	 * environment variable
	 */
	private static Environment<? extends Entity> env;
	
	private static String algorithmName = null;
	
	
	private static Integer algorithmRun = null;
	
	/**
	 *  list of simulations
	 */
	private ArrayList<DefaultSimulation> simulation = new ArrayList<DefaultSimulation>();
	
	private ArrayList<ValueRemover> removerSimulator = new ArrayList<ValueRemover>();
	
	private Evaluator eval;

	/**
	 *  initial variables (agents)
	 */
	//private int initialAgents = Transmitter.getIntConfigParameter(this.configFileName, "config.variables");
	
	/**
	 * initial values (tasks)
	 */
	//private int initialTasks = Transmitter.getIntConfigParameter(this.configFileName, "config.values");
	
	/**
	 *  total timesteps
	 */
	private int timesteps = Transmitter.getIntConfigParameter(this.configFileName, "config.timesteps");
	
	/**
	 * current time
	 */
	private int time = 0; 
	
	
	/**
	 * specific constructor
	 */
	public BlackBox()
	{
		/**
		 * insert the perception simulation (1st simulator)
		 */
		//this.addSimulation(PerceptionSimulation.class);
		/**
		 * insert the communication simulation (2nd simulator)
		 */
		//this.addSimulation(CommunicationSimulation.class);
		
	}
	
	/**
	 * new specific environment
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void newEnvironment(Class<? extends Environment> envClass){
		if(BlackBox.env == null){
			try {
				BlackBox.env = envClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}else
			Transmitter.message(this.messageFileName, "message.environment");
	}
	
	/**
	 * <ul>
	 *  <li>function to define the ammount of variables of the environment</li>
	 * </ul>
	 * 
	 * @param agentClass Class of the variable
	 * @param ammount Ammount of the variable
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void addAgent(Class<? extends Variable> agentClass, Integer ammount) throws InstantiationException, IllegalAccessException{
		for(int i = 0; i < ammount; i++){
			Variable var = agentClass.newInstance();
			if(i == 0)
				var.header();
			var.logger();
			//HumanLogger.logHuman((Human)var);
			BlackBox.env.getVariables().add(var);
		}
		this.getEnvironment().registerVariable(agentClass, ammount);
	}
	
	public void addTask(Class<? extends Value> taskClass, Integer ammount) throws InstantiationException, IllegalAccessException{
		for(int i = 0; i < ammount; i++)
		{
			Value val = taskClass.newInstance();
			if(i == 0)
				val.header();
			val.logger();
			//FireBuildingTaskLogger.logFireBuildingTask((FireBuildingTask)val);
			BlackBox.env.getValues().add(val);
			this.getEnvironment().registerValue(val.getId(), taskClass);
		}
		//this.getEnvironment().registerValue(taskClass, ammount);
	}

	/**
	 * private function to create new variables and values to the simulation
	 * 
	 * @param t
	 * @param a
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	/*
	private void configure(Class<? extends Variable> var, Class<? extends Value> val) throws InstantiationException, IllegalAccessException{
		
		this.newEnvironment();
		for(int x = 0; x < this.initialAgents; x++)
		{
			
			ArrayList<Variable> arrVar = new ArrayList<Variable>();
			arrVar.add(var.newInstance());
			this.env.getVariables().add(arrVar.get(0));
		}
		
		for(int y = 0; y < this.initialTasks; y++)
		{
			this.env.getValues().add(val.newInstance());
		}		
	
	}*/
	
	/**
	 * starts the simulation until the total timesteps
	 * 
	 * @throws SimulatorException
	 */
	public void simulationStart() throws SimulatorException{
		if(this.getSimulation().isEmpty())
			throw new SimulatorException(Transmitter.getProperty(this.exceptionFileName, "exception.no.simulator"));
		while(this.time <= this.timesteps){
			System.out.println("Timestep "+this.time+"/"+this.timesteps);
			this.time++;
			this.simulationStep();
		}

		WriteFile.getInstance().closeFile();
		Transmitter.message(this.configFileName, "final.message");
	}
	
	/**
	 * perform one simulation step
	 * @throws SimulatorException 
	 */
	private void simulationStep() throws SimulatorException{
		
		/**
		 * updates the agent view and runs commands for all agents
		 */
		//runs the perception simulator to update the current agent view
		//perform the act method in each agent
		//this method is responsible for perception and action of the agents (reactive module)
		if(this.getSimulation().get(0) instanceof PerceptionSimulation){
			this.getSimulation().get(0).simulate(this.getEnvironment());
		}
		//one agents already perform their actions,
		//the communication simulator verifies if there are messages to be sent
		if(this.getSimulation().get(1) instanceof CommunicationSimulation)
			this.getSimulation().get(1).simulate(this.getEnvironment());
		
		//run other simulators
		for(int k = 2; k < this.getSimulation().size(); k++)
		{
			if(!(this.getSimulation().get(k) instanceof PerceptionSimulation)
					|| !(this.getSimulation().get(k) instanceof CommunicationSimulation)){
				this.getSimulation().get(k).simulate(this.getEnvironment());
			}
		}
		
		if(this.getEvaluator() != null){
			//System.out.println("Current Timestep "+Environment.time);
			this.getEvaluator().qualitySolution(BlackBox.env);
			if(Environment.time == 0){
				if(BlackBox.getAlgorithmName() != null && BlackBox.getAlgorithmRun() != null)
				{
					ExperimentLogger.saveHeader(BlackBox.algorithmName+"_"+BlackBox.algorithmRun);
				}else
					ExperimentLogger.saveHeader(null);
			}
			ExperimentLogger.logExperiment(this.getEvaluator());
			//System.out.println("Time "+Environment.time+" - "+this.getEvaluator().qualitySolution(this.env));
			//System.out.println("Allocated "+this.getEvaluator().allocatedValues().get(Environment.time));
			//System.out.println("Not Allocated "+this.getEvaluator().notAllocatedValues().get(Environment.time));
			//System.out.println("Idle "+this.getEvaluator().idleVariables().get(Environment.time));
		}
		/**
		 * perform the simulations except perception simulator
		 */
		
		if(!this.removerSimulator.isEmpty())
		{
			for(ValueRemover s : this.removerSimulator)
			{
				s.simulate(this.getEnvironment());
			}
		}
		
		Environment.time = this.time;
	}

	/**
	 * return list of simulators
	 * @return
	 */
	public ArrayList<DefaultSimulation> getSimulation() {
		return simulation;
	}

	/**
	 * add simulation to the list
	 * @param simulation
	 */
	public void addSimulation(Class<? extends DefaultSimulation> simulation) {
		DefaultSimulation s = null;
		try {
			s = simulation.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(s instanceof ValueRemover)
		{
			this.removerSimulator.add((ValueRemover)s);
		}else{
			this.getSimulation().add(s);
		}
	}
	
	/**
	 * add communication simulation
	 * @param simulation
	 */
	public void addCommunicationSimulation(Class<? extends CommunicationSimulation> simulation) {
		try {
			this.getSimulation().add(1, simulation.newInstance());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * add perception simulation
	 * 
	 * @param simulation
	 */
	public void addPerceptionSimulation(Class<? extends PerceptionSimulation> simulation)
	{
		try{
			this.getSimulation().add(0, simulation.newInstance());
		}catch(InstantiationException e)
		{
			e.printStackTrace();
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * return the environment
	 * @return
	 */
	public Environment<? extends Entity> getEnvironment() {
		return BlackBox.env;
	}



	public void addEvaluator(Evaluator evaluator) {
		this.eval = evaluator;
	}

	public Evaluator getEvaluator() {
		return eval;
	}

	public static String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		BlackBox.algorithmName = algorithmName;
	}

	public static Integer getAlgorithmRun() {
		return algorithmRun;
	}

	public void setAlgorithmRun(int algorithmRun) {
		BlackBox.algorithmRun = algorithmRun;
	}

	

}
