/**
 * @file rexos/mas/service_agent/behaviours/OtherAgentDied.java
 * @brief
 * @date Created: 31 mei 2013
 * 
 * @author Peter Bonnema
 * 
 * @section LICENSE
 *          License: newBSD
 * 
 *          Copyright © 2013, HU University of Applied Sciences Utrecht.
 *          All rights reserved.
 * 
 *          Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 *          the following conditions are met:
 *          - Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *          following disclaimer.
 *          - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *          following disclaimer in the documentation and/or other materials provided with the distribution.
 *          - Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be
 *          used to endorse or promote products derived from this software without specific prior written permission.
 * 
 *          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *          THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *          ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 *          BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *          CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *          GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *          HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *          LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *          OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 **/
package agents.service_agent.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import libraries.utillities.log.LogLevel;
import libraries.utillities.log.Logger;
import agents.service_agent.ServiceAgent;
import agents.shared_behaviours.ReceiveBehaviour;

/**
 * Behaviour that receives messages when the equipletAgent or hardwareAgent dies.
 */
public class OtherAgentDied extends ReceiveBehaviour {
	/**
	 * @var long serialVersionUID
	 * 		The serial version UID for this class.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @var ServicAgent serviceAgent
	 * 		The serviceAgent linked to this behaviour.
	 */
	private ServiceAgent serviceAgent;

	/**
	 * Constructor for this behaviour.
	 * 
	 * @param serviceAgent
	 * 		The serviceAgent linked to this behaviour
	 */
	public OtherAgentDied(ServiceAgent serviceAgent) {
		super(serviceAgent, MessageTemplate.or(MessageTemplate.MatchOntology("EquipletAgentDied"),
				MessageTemplate.MatchOntology("HardwareAgentDied")));
		this.serviceAgent = serviceAgent;
		Logger.log(LogLevel.DEBUG, "OtherAgentDied behaviour created.");
	}

	/* (non-Javadoc)
	 * @see rexos.mas.behaviours.ReceiveBehaviour#handle(jade.lang.acl.ACLMessage) */
	@Override
	public void handle(ACLMessage message) {
		if(message.getOntology().equals("EquipletAgentDied")) {
			Logger.log(LogLevel.DEBUG, "Received message.");
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(serviceAgent.getHardwareAgentAID());
			msg.setOntology("ServiceAgentDied");
			serviceAgent.send(msg);
		} else {
			Logger.log(LogLevel.WARNING, "Unknown/Unexpected message received.");
			serviceAgent.doDelete();
		}
	}
}
