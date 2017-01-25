package ufrgs.maslab.abstractsimulator.log;

import ufrgs.maslab.abstractsimulator.core.BlackBox;
import ufrgs.maslab.abstractsimulator.core.Evaluator;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.util.WriteFile;

public class ExperimentLogger {
	
	private static String logFile = null;
	
	public static void saveHeader(String name)
	{
		logFile = Transmitter.getProperty("files.properties", "experiment")+"_"+BlackBox.getAlgorithmName()+"_"+BlackBox.getAlgorithmRun();
		
		if(name != null)
			logFile += "_"+name;
		
		WriteFile.getInstance().openFile(logFile);
		String header = "timestep;qualitySolution;allocatedValue;notAllocatedValue;idleVariables;";
		WriteFile.getInstance().write(header, logFile);
	}
	
	public static void logExperiment(Evaluator v)
	{
		WriteFile.getInstance().openFile(logFile);
		String step = v.getTime()+";"
				+v.quality().get(v.getTime())+";"
				+v.allocatedValues().get(v.getTime())+";"
				+v.notAllocatedValues().get(v.getTime())+";"
				+v.idleVariables().get(v.getTime())+";";
		WriteFile.getInstance().write(step, logFile);
	}

}
