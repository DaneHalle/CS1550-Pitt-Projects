#!/bin/bash
# This script runs every algoritm 4 times each for each trace with 8, 16, 32, and 64 frames

echo  
echo gzip - SECOND
java vmsim -n 8 -a second gzip.trace
echo  
java vmsim -n 16 -a second gzip.trace
echo  
java vmsim -n 32 -a second gzip.trace
echo  
java vmsim -n 64 -a second gzip.trace
echo  

echo  
echo gcc - SECOND
java vmsim -n 8 -a second gcc.trace
echo  
java vmsim -n 16 -a second gcc.trace
echo  
java vmsim -n 32 -a second gcc.trace
echo  
java vmsim -n 64 -a second gcc.trace
echo  

echo  
echo swim - SECOND
java vmsim -n 8 -a second swim.trace
echo  
java vmsim -n 16 -a second swim.trace
echo  
java vmsim -n 32 -a second swim.trace
echo  
java vmsim -n 64 -a second swim.trace
echo  


echo  
echo gzip - OPTIMAL
java vmsim -n 8 -a opt gzip.trace
echo  
java vmsim -n 16 -a opt gzip.trace
echo  
java vmsim -n 32 -a opt gzip.trace
echo  
java vmsim -n 64 -a opt gzip.trace
echo  

echo  
echo gcc - OPTIMAL
java vmsim -n 8 -a opt gcc.trace
echo  
java vmsim -n 16 -a opt gcc.trace
echo  
java vmsim -n 32 -a opt gcc.trace
echo  
java vmsim -n 64 -a opt gcc.trace
echo  

echo  
echo swim - OPTIMAL
java vmsim -n 8 -a opt swim.trace
echo  
java vmsim -n 16 -a opt swim.trace
echo  
java vmsim -n 32 -a opt swim.trace
echo  
java vmsim -n 64 -a opt swim.trace
echo  


echo  
echo gzip - LRU
java vmsim -n 8 -a lru gzip.trace
echo  
java vmsim -n 16 -a lru gzip.trace
echo  
java vmsim -n 32 -a lru gzip.trace
echo  
java vmsim -n 64 -a lru gzip.trace
echo  

echo  
echo gcc - LRU
java vmsim -n 8 -a lru gcc.trace
echo  
java vmsim -n 16 -a lru gcc.trace
echo  
java vmsim -n 32 -a lru gcc.trace
echo  
java vmsim -n 64 -a lru gcc.trace
echo  

echo  
echo swim - LRU
java vmsim -n 8 -a lru swim.trace
echo  
java vmsim -n 16 -a lru swim.trace
echo  
java vmsim -n 32 -a lru swim.trace
echo  
java vmsim -n 64 -a lru swim.trace
echo  
