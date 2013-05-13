/**
 * 
 * Project: product-agents
 * 
 * Package: newDataClasses
 * 
 * File: ParameterGroup.java
 * 
 * Author: Mike Schaap
 * 
 * Version: 1.0
 * 
 */
package rexos.mas.data;

import java.util.HashMap;

import com.mongodb.BasicDBObject;

/**
 * @author Peter
 * 
 */
public class ParameterGroup extends Parameter implements IMongoSaveable {
	private static final long serialVersionUID = -8737315910311192437L;
	
	/**
	 * 
	 */
	private HashMap<String, Parameter> _parameters;

	/**
	 * 
	 */
	public ParameterGroup() {
		_parameters = new HashMap<String, Parameter>();
	}

	/**
	 * 
	 */
	public ParameterGroup(BasicDBObject object) {
		_parameters = new HashMap<String, Parameter>();
		fromBasicDBObject(object);
	}

	/**
	 * @param name
	 * @param parameter
	 */
	public ParameterGroup addParameter(String name, Parameter parameter) {
		_parameters.put(name, parameter);
		return this;
	}

	/**
	 * @param name
	 * @return
	 */
	public Parameter getParameter(String name) {
		return _parameters.get(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public Parameter removeParameter(String name) {
		return _parameters.remove(name);
	}

	/**
	 * @return
	 */
	public HashMap<String, Parameter> getParameters() {
		return (HashMap<String, Parameter>) _parameters.clone();
	}

	/* (non-Javadoc)
	 * @see rexos.mas.data.IMongoSaveable#toBasicDBObject() */
	@Override
	public BasicDBObject toBasicDBObject() {
		BasicDBObject dbObject = new BasicDBObject();
		for(String name : _parameters.keySet()) {
			Object parameterValue = _parameters.get(name).getValue();
			if(parameterValue instanceof IMongoSaveable) {
				dbObject.append(name, ((IMongoSaveable) parameterValue).toBasicDBObject());
			} else {
				dbObject.append(name, parameterValue);
			}
		}
		return dbObject;
	}

	/* (non-Javadoc)
	 * @see
	 * rexos.mas.data.IMongoSaveable#fromBasicDBObject(com.mongodb.BasicDBObject
	 * ) */
	@Override
	public void fromBasicDBObject(BasicDBObject object) {
		for(String name : object.keySet()) {
			Object parameter = object.get(name);
			if(parameter instanceof BasicDBObject) {
				_parameters.put(name, new ParameterGroup((BasicDBObject) parameter));
			} else {
				_parameters.put(name, new Parameter(object.get(name)));
			}
		}
	}
}