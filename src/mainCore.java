import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FGNetworkEvaluator;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FactorGraph;
import ufrgs.maslab.abstractsimulator.core.BlackBox;
import ufrgs.maslab.abstractsimulator.core.simulators.basic.ValueRemover;
import ufrgs.maslab.abstractsimulator.core.simulators.disaster.UrbanDisasterEvaluator;
import ufrgs.maslab.abstractsimulator.core.simulators.factorgraph.SumProductCommunication;
import ufrgs.maslab.abstractsimulator.core.simulators.factorgraph.SumProductPerception;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.values.FireBuildingTask;
import ufrgs.maslab.abstractsimulator.variables.FireFighter;
import ufrgs.maslab.abstractsimulator.variables.FireStation;

public class mainCore {

	public static void main(String[] args) {
		//runs
		long t0 = System.currentTimeMillis();
		System.out.println("start time "+t0);
		simulation(1, "factoring__ultra_high_values_w_remover");
		System.out.println("finish time "+System.currentTimeMillis());
		System.out.println("total time "+(System.currentTimeMillis() - t0));
		
	}
	
	public static void simulation(int k, String algorithm)
	{
		
		// TODO Auto-generated method stub

				//BlackBox<Task, Human> core = new BlackBox<Task,Human>(Human.class, Task.class);
				
				BlackBox core = new BlackBox();
				
				core.setAlgorithmRun(k);
				core.setAlgorithmName(algorithm);
				//core.newEnvironment();
				
				//set the problem model
				core.newEnvironment(FactorGraph.class);
				//set the communication framework
				core.addPerceptionSimulation(SumProductPerception.class);
				core.addCommunicationSimulation(SumProductCommunication.class);
				core.addEvaluator(new UrbanDisasterEvaluator());
				core.addSimulation(FGNetworkEvaluator.class);
				core.addSimulation(ValueRemover.class);
				try {
					/**
					 * insert fire building tasks
					 * insert fire fighter agents
					 * insert fire station agents
					 */

					core.addAgent(FireStation.class, Transmitter.getIntConfigParameter("config.properties", "config.central"));
					core.addAgent(FireFighter.class, Transmitter.getIntConfigParameter("config.properties", "config.variables"));
					core.addTask(FireBuildingTask.class, Transmitter.getIntConfigParameter("config.properties", "config.values"));
					
					
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					core.simulationStart();
				} catch (SimulatorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.gc();

		
	}
	

}
