package Communication.Benchmark;
import java.util.Random;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.google.gson.Gson;
import java.io.IOException;
 
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.util.ArrayList; 
import nl.hu.client.BlackboardClient;
import java.util.HashMap; 
import java.util.Scanner;
import nl.hu.client.ISubscriber;
import java.util.Date;
import java.io.*;
import java.util.concurrent.*; 

public class BlackboardSendAgent extends Agent implements ISubscriber
{
  	private BlackboardClient client;
	ArrayList<Long> times = new ArrayList<Long>();
	private String database = "REXOS";
	private String writetopic = "send"; 
	private String readtopic = "receive";
	private Long sendingTime; 

	private String collection = "blackboard";
	int messageCount =0;
	int messages =0;
	public void setup()
	{
		
		client = new BlackboardClient("localhost");		
		try
		{
		client.setDatabase(database);	
		client.setCollection(collection);
		client.subscribe(readtopic);
		client.setCallback(this);
		}catch(Exception e){e.printStackTrace();}	


		
		this.addBehaviour(new CyclicBehaviour()
		{
			@Override
			public void action() 
			{
				try
				{
				ACLMessage temp = blockingReceive();   
				messages = Integer.parseInt(temp.getContent());
				sendingTime = System.nanoTime();				
				client.insertJson("{ topic:\""+writetopic+"\", data:\"................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................\" }");  }catch(Exception e){e.printStackTrace();}
				
				}
		});



	}

	public void onMessage(String json)
	{	
		client.removeFirst();						
		long receivingTime = System.nanoTime();
		times.add((receivingTime - sendingTime));
		messageCount++;
		if(messageCount < messages)
		{
			try{
			sendingTime = System.nanoTime();				
			client.insertJson("{ topic:\""+writetopic+"\", data:\"................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................\" }");  
}catch(Exception e){e.printStackTrace();}
			// send new message
		}	
		else
		{			
				try
				{	
					File yourFile = new File("out.txt");
					if(!yourFile.exists()) {
			    			yourFile.createNewFile();
					} 
					FileWriter fstream = new FileWriter("out.txt");
			  		BufferedWriter out = new BufferedWriter(fstream);
					System.out.println(times.size());
					for(Long l:times)
					{  		
					out.write(""+l+"\n");
					}	
		
			  		//Close the output stream
			  		out.close();
					System.out.println("done!");
					times.clear();
					messageCount = 0;
		  		}
				catch (Exception e) 	
					{//Catch exception if any
		  			System.err.println("Error: " + e.getMessage());
		  		}

		}
		
		
	}	
}