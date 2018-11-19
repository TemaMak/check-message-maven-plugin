package naviguide.checkmessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MessageProblemHolder {

	Map<String,List<String>> messageProblems = new HashMap<String,List<String>>();
	Set<String> uniqProblems = new HashSet<String>();
	
	
	public void addProblem(File f, String message) throws IOException{
		addProblem(f.getCanonicalPath(), message);
	}
	
	public void addProblem(String filename, String message){
		if(messageProblems.get(message) == null){
			messageProblems.put(message, new ArrayList<String>());
		}
		
		String ploblenmHash = filename + ";" + message;
		if(uniqProblems.contains(ploblenmHash) == false ){
			List<String> messageList = messageProblems.get(message);
			messageList.add(filename);
			messageProblems.put(message, messageList);
			uniqProblems.add(ploblenmHash);
		}		
	}
	
	public Set<Entry<String, List<String>>> entrySet(){
		return messageProblems.entrySet();
	}
}
