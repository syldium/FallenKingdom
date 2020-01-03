package fr.devsylone.fallenkingdom.commands;

public class BuiltFkCommand
{
	public enum SucessLevel
	{
		SUCCESS,
		FAILED,
		UNKNOWN_COMMAND;
	}
	
	private String cmdPath;
	private String args[];
	private SucessLevel level;
	
	public BuiltFkCommand(String commandPath, SucessLevel lvl, String... args)
	{
		cmdPath = commandPath;
		level = lvl;
		this.args = args;
	}
	
//	@Override
//	public String toString()
//	{
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("command", cmdPath);
//		map.put("args", String.join(" ", args));
//		map.put("level", level.name());
//		return new JSONObject(map).toJSONString();
//	}
}
