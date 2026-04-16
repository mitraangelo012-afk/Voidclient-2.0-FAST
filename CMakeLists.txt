cmake_minimum_required(VERSION 3.18.1)

project("voidclient")

# Enable C++17
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add native source files
file(GLOB_RECURSE NATIVE_SOURCES
    "app/src/main/cpp/*.cpp"
    "app/src/main/cpp/*.h"
)

# Create the library
add_library(
    voidclient
    SHARED
    ${NATIVE_SOURCES}
)

# Find log library
find_library(
    log-lib
    log
)

# Link libraries
target_link_libraries(
    voidclient
    ${log-lib}
)