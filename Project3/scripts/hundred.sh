#!/bin/bash
# This script runs each trace with the Second Chance algorithm with frames increasing from 2-100

i=2
printf '\n=======gcc.trace=======\n'
until [ $i -gt 100 ]
do
  java vmsim -n $i -a second gcc.trace

  ((i=i+1))
done


j=2
printf '\n=======gzip.trace=======\n'
until [ $j -gt 100 ]
do
  java vmsim -n $j -a second gzip.trace

  ((j=j+1))
done


k=2
printf '\n=======swim.trace=======\n'
until [ $k -gt 100 ]
do
  java vmsim -n $k -a second swim.trace

  ((k=k+1))
done