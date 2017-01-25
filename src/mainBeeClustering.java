import ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.BeeClusterEvaluator;
import ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.BeeClusteringEnvironment;
import ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.algorithm.BeeClustering;
import ufrgs.maslab.abstractsimulator.algorithms.model.factorgraph.FGNetworkEvaluator;
import ufrgs.maslab.abstractsimulator.core.BlackBox;
import ufrgs.maslab.abstractsimulator.core.simulators.basic.CommunicationSimulation;
import ufrgs.maslab.abstractsimulator.core.simulators.basic.PerceptionSimulation;
import ufrgs.maslab.abstractsimulator.core.simulators.basic.ValueRemover;
import ufrgs.maslab.abstractsimulator.core.simulators.disaster.UrbanDisasterEvaluator;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.values.FireBuildingTask;
import ufrgs.maslab.abstractsimulator.variables.FireFighter;


public class mainBeeClustering {

	public static void main(String[] args) {
		long t0 = System.currentTimeMillis();
		System.out.println("start time "+t0);
		simulation(1, "beeclustering_ultra_high_values_w_remover");
		System.out.println("finish time "+System.currentTimeMillis());
		System.out.println("total time "+(System.currentTimeMillis() - t0));
		
	}
	
	public static void simulation(int k, String algorithm)
	{
		
		BlackBox core = new BlackBox();
		
		core.setAlgorithmRun(k);
		core.setAlgorithmName(algorithm);
		
		core.newEnvironment(BeeClusteringEnvironment.class);
		core.addSimulation(PerceptionSimulation.class);
		core.addSimulation(CommunicationSimulation.class);
		core.addSimulation(BeeClustering.class);
		core.addEvaluator(new UrbanDisasterEvaluator());
		core.addSimulation(BeeClusterEvaluator.class);
		core.addSimulation(ValueRemover.class);
		try {
			/**
			 * insert fire building tasks
			 * insert fire fighter agents
			 * insert fire station agents
			 */

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
