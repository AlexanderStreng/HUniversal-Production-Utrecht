/**
 * @file rexos/mas/logistics_agent/behaviours/ArePartsAvailableInTime.java
 * @brief Responds to ArePartsAvailableInTime messages, returning whether or not the parts are available in time.
 * @date Created: 25 apr. 2013
 *
 * @author Peter Bonnema
 *
 * @section LICENSE
 * License: newBSD
 *
 * Copyright © 2013, HU University of Applied Sciences Utrecht.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 **/
package agents.logistics_agent.behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import libraries.utillities.log.LogLevel;
import libraries.utillities.log.Logger;
import agents.data_classes.Part;
import agents.logistics_agent.LogisticsAgent;
import agents.shared_behaviours.ReceiveOnceBehaviour;

/**
 * Responds to ArePartsAvailableInTime messages, returning whether or not the
 * parts are available in time.
 */
public class ArePartsAvailableInTime extends ReceiveOnceBehaviour {
	/**
	 * @var long serialVersionUID
	 *      The serialVersionUID for this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @var MessageTemplate MESSAGE_TEMPLATE
	 *      The messageTemplate to match the messages.
	 */
	private static final MessageTemplate MESSAGE_TEMPLATE = MessageTemplate.MatchOntology("ArePartsAvailableInTime");
	
	/**
	 * @var LogisticsAgent logisticsAgent
	 *      The logisticsAgent of this behaviour.
	 */
	private LogisticsAgent logisticsAgent;
	
	/**
	 * Constructs the behaviour for the given agent.
	 * 
	 * @param a
	 *            The agent associated with this behaviour.
	 * @param conversationId
	 *            The conversationId that should be used for this behaviour.
	 */
	public ArePartsAvailableInTime(LogisticsAgent logisticsAgent, String conversationId) {
		this(logisticsAgent, 2000, conversationId);
	}

	/**
	 * Constructs the behaviour for the given agent.
	 * 
	 * @param a
	 *            The agent associated with this behaviour.
	 * @param millis
	 *            Timeout in milliseconds.
	 * @param conversationId
	 *            The conversationId that should be used for this behaviour.
	 */
	public ArePartsAvailableInTime(LogisticsAgent logisticsAgent, int millis, String conversationId) {
		super(logisticsAgent, millis, MessageTemplate.and(
				MESSAGE_TEMPLATE, MessageTemplate.MatchConversationId(conversationId)));
		this.logisticsAgent = logisticsAgent;
	}

	/**
	 * Handles ArePartsAvailableInTime messages and responds with an
	 * ArePartsAvailableInTimeResponse indicating whether or not the parts will
	 * be available in time.
	 * 
	 * @see rexos.mas.behaviours.ReceiveBehaviour#handle(jade.lang.acl.ACLMessage)
	 */
	@Override
	public void handle(ACLMessage message) {
		if (message != null) {
			try {
				Part[] parts = (Part[]) message.getContentObject();

				ACLMessage reply = message.createReply();
				reply.setOntology("ArePartsAvailableInTime");
				reply.setPerformative(ACLMessage.CONFIRM);
				logisticsAgent.send(reply);

				logisticsAgent.addBehaviour(new PartsInfo(logisticsAgent, message
						.getConversationId()));
			} catch (UnreadableException e) {
				
				logisticsAgent.doDelete();
			}
		} else {
			logisticsAgent.removeBehaviour(this);
		}
	}
}
