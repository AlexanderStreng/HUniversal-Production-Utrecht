/**
 * @file RosComBenchmarkClient.cpp
 * @brief Communication test client.
 * @date Created: 2012-10-05
 *
 * @author Arjen van Zanten
 * @author Dennis Koole
 *
 * @section LICENSE
 * License: newBSD
 * 
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

#include "ros/ros.h"
#include "rosCommunicationBenchmark/TestServiceEmpty.h"
#include "rosCommunicationBenchmark/TestServiceFilled.h"
#include "rosCommunicationBenchmark/SignalTestEnd.h"
#include <iostream>
#include <cstdlib>
#include <time.h>

// @cond HIDE_NODE_NAME_FROM_DOXYGEN
#define NODE_NAME "RosComBenchmarkClient"
// @endcond

int main(int argc, char **argv)
{
	ros::init(argc, argv, NODE_NAME);

	ros::NodeHandle n;
	ros::ServiceClient testEmptyClient = n.serviceClient<rosCommunicationBenchmark::TestServiceEmpty>("testServiceEmpty");

	rosCommunicationBenchmark::TestServiceEmpty emptySrv;

	uint64_t send, returned;

	// for(int i = 0; i < 1000; i++){
	// 	send = ros::Time::now().toNSec();
	// 	testEmptyClient.call(emptySrv);
	// 	returned = ros::Time::now().toNSec();

	// 	std::cout << "SND;" << send << std::endl
	// 		<< "RTN;" << returned << std::endl;
	// }

	// Test filled service
	ros::ServiceClient testFilledClient = n.serviceClient<rosCommunicationBenchmark::TestServiceFilled>("testServiceFilled");

	rosCommunicationBenchmark::TestServiceFilled filledSrv;

	srand(time(NULL));

	for(int i = 1000; i < 2000 && ros::ok(); i++){
		for(int i2 = 0; i2 < 10 && ros::ok(); i2++){
			rosCommunicationBenchmark::TestMsg msg;
			msg.testFloat = (double)((rand() % 1000) / (rand() % 1000 + 1));
			filledSrv.request.msgs.push_back(msg);
		}

		filledSrv.request.id.id = i;
		send = ros::Time::now().toNSec();
		testFilledClient.call(filledSrv);
		returned = ros::Time::now().toNSec();

		std::cout << "SND;" << send << std::endl
			<< "RTN;" << returned << std::endl;	
	}

	ros::ServiceClient testEndClient = n.serviceClient<rosCommunicationBenchmark::SignalTestEnd>("signalTestEnd");

	rosCommunicationBenchmark::SignalTestEnd testEnd;

	testEndClient.call(testEnd);

	return 0;
}