/**
 * @file rexos/mas/productAgent/ProductAgent.java
 * @brief Initial product agent creation for testing purposes
 * @date Created: 08-04-2013
 * 
 * @author Alexander Streng
 * @author Arno Derks
 * @author Mike Schaap
 * 
 * @section LICENSE License: newBSD
 * 
 *          Copyright � 2012, HU University of Applied Sciences Utrecht. All
 *          rights reserved.
 * 
 *          Redistribution and use in source and binary forms, with or without
 *          modification, are permitted provided that the following conditions
 *          are met: - Redistributions of source code must retain the above
 *          copyright notice, this list of conditions and the following
 *          disclaimer. - Redistributions in binary form must reproduce the
 *          above copyright notice, this list of conditions and the following
 *          disclaimer in the documentation and/or other materials provided with
 *          the distribution. - Neither the name of the HU University of Applied
 *          Sciences Utrecht nor the names of its contributors may be used to
 *          endorse or promote products derived from this software without
 *          specific prior written permission.
 * 
 *          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *          LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *          FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE HU
 *          UNIVERSITY OF APPLIED SCIENCES UTRECHT BE LIABLE FOR ANY DIRECT,
 *          INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *          (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *          SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *          HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 *          STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *          ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 *          OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 **/

package agents.product_agent;

import jade.core.Agent;

import java.util.ArrayList;

import libraries.utillities.log.LogLevel;
import libraries.utillities.log.Logger;
import agents.data_classes.AgentStatus;
import agents.data_classes.Callback;
import agents.data_classes.Product;
import agents.data_classes.ProductAgentProperties;
import agents.data_classes.Production;
import agents.data_classes.ProductionStep;
import agents.product_agent.behaviours.OverviewBehaviour;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class ProductAgent extends Agent {

	private static final long serialVersionUID = 1L;
	// Private fields

	private OverviewBehaviour _overviewBehaviour;

	// CID variables
	private static int _convIDCnt = 0;
	private String _convIDBase;
	public int prodStep = 0;

	private ProductAgentProperties _properties;
	private AgentStatus _status;

	/**
	 * Constructs the product agent
	 */
	public ProductAgent() {
		_status = AgentStatus.STARTING;
	}

	/**
	 * Sets up the product agent
	 */
	@Override
	protected void setup() {
		try {
			// Load all arguments
			this.loadArguments();
			// Create the Overview Behaviour and start it
			_overviewBehaviour = new OverviewBehaviour(this);
			addBehaviour(_overviewBehaviour);
			Logger.log(LogLevel.NOTIFICATION, "" + this.getAID().getLocalName() + " spawned as an product agent.");
		} catch (IllegalArgumentException e) {
		Logger.log(
			LogLevel.ERROR,
			"No arguments found. ProductAgent needs atleast one ProductAgentPropeties as argument",
			e );
		}
	}

	/**
	 * Loads the arguments passed to the product agent
	 */
	private void loadArguments() throws IllegalArgumentException {
		// Get the arguments passed to the ProductAgent
		System.out.println("LOADING ARGUMENTS");
		Object[] args = this.getArguments();
		// Check if there are any arguments. If there aren't any there is a
		// problem. We need atleast one arguments for the product
		if (args.length > 0) {
			// The first argument should be the ProductAgentProperties object,
			// whether it's in json or as object.
			// Check if the arguments is a string (so we can assume it's JSON)
			// or if it's a known object
			if (args[0].getClass() == String.class) {
				try {
					// Change the incoming JSON message to also implement the
					// host to connect to.
					JsonParser parser = new JsonParser();
					JsonObject obj = (JsonObject) parser
							.parse((String) args[0]).getAsJsonObject();
					JsonElement callbackElement = obj.get("callback");

					JsonObject productObject = obj.get("product")
							.getAsJsonObject();

					JsonObject productionObject = productObject.get(
							"production").getAsJsonObject();

					JsonArray productionStepsArray = productionObject.get(
							"productionSteps").getAsJsonArray();

					ArrayList<ProductionStep> stepList = new ArrayList<>();
					ProductionStep step = null;
					for (int i = 0; i < productionStepsArray.size(); i++) {

						JsonObject ele = productionStepsArray.get(i)
								.getAsJsonObject();
						int id = ele.get("id").getAsInt();
						int capability = ele.get("capability").getAsInt();
						JsonElement parameterObject = ele.get("parameters");
						String parameters = parameterObject.toString();

					//	System.out.println(parameters);
						BasicDBObject DBParameters = (BasicDBObject) JSON.parse(parameters);
						BasicDBObject positionParameters =(BasicDBObject)((BasicDBObject)((BasicDBObject)DBParameters.get("parameterGroups")).get("loc")).get("parameters");
						//make sure the x, y and z values are doubles
						//convert them when needed						
						if (positionParameters.containsField("x")){
							BasicDBObject xval = (BasicDBObject) positionParameters.get("x");
							Double newXval = convertInputParameterToDouble(xval.get("value"));
							xval.put("value", newXval);
						}
						
						if(positionParameters.containsField("y")){
							BasicDBObject yval = (BasicDBObject) positionParameters.get("y");
							Double newYval = convertInputParameterToDouble(yval.get("value"));
							yval.put("value", newYval);
						}
						
						if(positionParameters.containsField("z")){
							BasicDBObject zval = (BasicDBObject) positionParameters.get("z");
							Double newZval = convertInputParameterToDouble(zval.get("value"));
							zval.put("value", newZval);
						}
						
						step = new ProductionStep(id, capability,
								DBParameters);

						stepList.add(step);
					}

					Production production = new Production(stepList);
					Product product = new Product(production);

					Callback callback = new Gson().fromJson(
							callbackElement.toString(), Callback.class);

					ProductAgentProperties pap = new ProductAgentProperties();
					pap.setCallback(callback);
					pap.setProduct(product);

					this._properties = pap;
				}catch(NullPointerException | IllegalArgumentException e){
					Logger.log(LogLevel.ERROR, "Error when parsing the arguments");
					e.printStackTrace();
				}
			} else if (args[0].getClass() == ProductAgentProperties.class) {
				this._properties = (ProductAgentProperties) args[0];
			}
		} else {
			throw new IllegalArgumentException("No argument(s) found.");
		}
	}

	/**
	 * 
	 * @param input
	 * @return
	 * @throws IllegalArgumentException 
	 */
	private Double convertInputParameterToDouble(Object input) throws IllegalArgumentException{
		double result = 0.0;
		if ( input instanceof String){
			try{
				result = Double.parseDouble((String)input);
			}catch(NumberFormatException e){
				throw new IllegalArgumentException("Could not parse input position string to double");
			}
		}
		else if (input instanceof Integer){
			result = (int) input;
		}
		else if (input instanceof Double){
			result = (double) input;
		}
		else{
			throw new IllegalArgumentException("Could not parse input position object to double");
		}
		return result;
	}
	
	/*
	 * Generates a unique conversation id based on the agents localname, the
	 * objects hashcode and the current time.
	 */
	/**
	 * Generate an communication ID
	 * 
	 * @return
	 */
	public String generateCID() {
		if (_convIDBase == null) {
			_convIDBase = getLocalName() + hashCode()
					+ System.currentTimeMillis() % 10000 + "_";
		}
		return _convIDBase + (_convIDCnt++);
	}

	/**
	 * Reschedule productionstep
	 */
	/**
	 * Get properties of the product agent
	 * 
	 * @return
	 */
	public ProductAgentProperties getProperties() {
		return this._properties;
	}

	/**
	 * Sets the product agent properties
	 * 
	 * @param properties
	 */
	public void setProperties(ProductAgentProperties properties) {
		this._properties = properties;
	}

	/**
	 * Gets the callback response of a behavior
	 * 
	 * @return
	 */
	public Callback getCallback() {
		return this._properties.getCallback();
	}

	/**
	 * Sets the callback of a behavior
	 * 
	 * @param callback
	 */
	public void setCallback(Callback callback) {
		this._properties.setCallback(callback);
	}

	/**
	 * Get the product
	 * 
	 * @return
	 */
	public Product getProduct() {
		return this._properties.getProduct();
	}

	/**
	 * Set the product
	 * 
	 * @param product
	 */
	public void setProduct(Product product) {
		this._properties.setProduct(product);
	}

	/**
	 * Get agent status
	 * 
	 * @return
	 */
	public AgentStatus getStatus() {
		return this._status;
	}

	/**
	 * Set the staus of the agent
	 * 
	 * @param status
	 */
	public void setStatus(AgentStatus status) {
		this._status = status;
	}

}
