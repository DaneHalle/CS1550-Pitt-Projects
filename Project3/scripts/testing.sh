#!/bin/bash
# This script tests all of the given test on Gradescope with the expected result printed after

echo  
echo simple - SECOND
java vmsim -n 2 -a second simple.trace
printf 'EXPECTED\nAlgorithm: SECOND\nNumber of frames: 2\nTotal memory accesses: 13\nTotal page faults: 11\nTotal writes to disk: 1\n'
echo  
echo gcc - SECOND
java vmsim -n 8 -a second gcc.trace
printf 'EXPECTED\nAlgorithm: SECOND\nNumber of frames: 8\nTotal memory accesses: 515683\nTotal page faults: 23680\nTotal writes to disk: 7621\n'
echo  
echo gzip - SECOND
java vmsim -n 8 -a second gzip.trace
printf 'EXPECTED\nAlgorithm: SECOND\nNumber of frames: 8\nTotal memory accesses: 481044\nTotal page faults: 39912\nTotal writes to disk: 39866\n'
echo  
echo swim - SECOND
java vmsim -n 8 -a second swim.trace
printf 'EXPECTED\nAlgorithm: SECOND\nNumber of frames: 8\nTotal memory accesses: 303193\nTotal page faults: 9916\nTotal writes to disk: 4892\n'

echo  
echo simple - OPTIMAL
java vmsim -n 2 -a opt simple.trace
printf 'EXPECTED\nAlgorithm: OPT\nNumber of frames: 2\nTotal memory accesses: 13\nTotal page faults: 9\nTotal writes to disk: 1\n'
echo  
echo gcc - OPTIMAL
java vmsim -n 2 -a opt gcc.trace
printf 'EXPECTED\nAlgorithm: OPT\nNumber of frames: 2\nTotal memory accesses: 515683\nTotal page faults: 97148\nTotal writes to disk: 31826\n'
echo  
echo gzip - OPTIMAL
java vmsim -n 2 -a opt gzip.trace
printf 'EXPECTED\nAlgorithm: OPT\nNumber of frames: 2\nTotal memory accesses: 481044\nTotal page faults: 40353\nTotal writes to disk: 40074\n'
echo  
echo swim - OPTIMAL
java vmsim -n 2 -a opt swim.trace
printf 'EXPECTED\nAlgorithm: OPT\nNumber of frames: 2\nTotal memory accesses: 303193\nTotal page faults: 57975\nTotal writes to disk: 23529\n'

echo  
echo simple - LRU
java vmsim -n 3 -a lru simple.trace
printf 'EXPECTED\nAlgorithm: LRU\nNumber of frames: 3\nTotal memory accesses: 13\nTotal page faults: 9\nTotal writes to disk: 1\n'
echo  
echo gcc - LRU
java vmsim -n 2 -a lru gcc.trace
printf 'EXPECTED\nAlgorithm: LRU\nNumber of frames: 2\nTotal memory accesses: 515683\nTotal page faults: 126391\nTotal writes to disk: 43137\n'
echo  
echo gzip - LRU
java vmsim -n 2 -a lru gzip.trace
printf 'EXPECTED\nAlgorithm: LRU\nNumber of frames: 2\nTotal memory accesses: 481044\nTotal page faults: 40496\nTotal writes to disk: 40146\n'
echo  
echo swim - LRU
java vmsim -n 2 -a lru swim.trace
printf 'EXPECTED\nAlgorithm: LRU\nNumber of frames: 2\nTotal memory accesses: 303193\nTotal page faults: 67789\nTotal writes to disk: 27499\n'