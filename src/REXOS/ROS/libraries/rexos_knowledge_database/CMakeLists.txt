cmake_minimum_required(VERSION 2.8.3)
project(rexos_knowledge_database)

## Find catkin and any catkin packages
find_package(catkin REQUIRED COMPONENTS)
find_package(MYSQLCONNECTORCPP)

## Declare a catkin package
catkin_package(
INCLUDE_DIRS include
LIBRARIES rexos_knowledge_database
CATKIN_DEPENDS
DEPENDS MYSQLCONNECTORCPP)

file(GLOB_RECURSE sources "src" "*.cpp" "*.c")
include_directories(include ${catkin_INCLUDE_DIRS})
add_library(rexos_knowledge_database ${sources})
target_link_libraries(rexos_knowledge_database ${catkin_LIBRARIES})
