package ufrgs.maslab.abstractsimulator.log;

import ufrgs.maslab.abstractsimulator.core.BlackBox;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.util.WriteFile;
import ufrgs.maslab.abstractsimulator.variables.FireFighter;

public class FireFighterLogger {
	
	/**
	 * <ul>
	 * <li>environment log filename</li>
	 * </ul>
	 * 
	 */
	private static String logFile = null;
	
	/**
	 * creates header for environment log file
	 */
	public static void saveHeader()
	{
		logFile = Transmitter.getProperty("files.properties", "firefighter")+"_"+BlackBox.getAlgorithmName()+"_"+BlackBox.getAlgorithmRun();
		WriteFile.getInstance().openFile(logFile);
		String header = "id;omega;firefghting;strength;dexterity;stamina;charisma;appearance;leadership;intelligence;reasoning;perception;will;hp;x;y";
		WriteFile.getInstance().write(header,logFile);
	}
	
	/**
	 * saves the current step of the environment in the log file
	 * @param time
	 */
	public static void logFireFighter(FireFighter f)
	{
		WriteFile.getInstance().openFile(logFile);
		String step = f.getId()+";"
				+f.getOmega()+";"
				+f.getFireFighting()+";"
				+f.getStrength()+";"
				+f.getDexterity()+";"
				+f.getStamina()+";"
				+f.getCharisma()+";"
				+f.getAppearance()+";"
				+f.getLeadership()+";"
				+f.getIntelligence()+";"
				+f.getReasoning()+";"
				+f.getPerception()+";"
				+f.getWill()+";"
				+f.getHp()+";"
				+f.getX()+";"
				+f.getY()
				;
		WriteFile.getInstance().write(step, logFile);

	}

}
