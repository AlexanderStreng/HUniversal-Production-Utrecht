/**
* @file OutputDevice.h
* @brief Output device interface
* @date Created: 2012-10-16
*
* @author Koen Braham
*
* @section LICENSE
* Copyright © 2012, HU University of Applied Sciences Utrecht.
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
**/

#include <rexos_gripper/InputOutputController.h>

#include <libjson/libjson.h>

namespace rexos_gripper
{
	/**
	 * OutputDevice interface.
	 * This device provides a general way of communication to the input output controller.
	 **/
	class OutputDevice {
	
	public:
		OutputDevice(JSONNode node);
		void readJSONNode(const JSONNode node);
		//OutputDevice(InputOutputController* ioController, uint32_t address, uint8_t pin);
		virtual ~OutputDevice();

		virtual void enable();
		virtual void disable();

	private:
		/**
		 * @var InputOutputController& ioController
		 * The InputOutput interface
		 **/
		InputOutputController* ioController;

		/**
		 * @var uint32_t address;
		 * Register that holds the on / off state of the device pin
		 **/
		uint32_t address;

		/**
		 * @var uint8_t pin;
		 * The pin connected to the output device
		 **/
		uint8_t pin;
	};
}
