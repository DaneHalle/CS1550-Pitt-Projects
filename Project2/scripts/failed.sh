#!/bin/bash

scp dmh148@thoth.cs.pitt.edu:/afs/pitt.edu/home/d/m/dmh148/private/cs1550/Projects/Project2/museumsim .

rm -r failed

./museumsim -m 50 -k 5 -pv 0 -dv 1 -sv 10 -pg 0 -dg 12 -sg 20 > failed5.txt

mkdir failed

mv failed*.txt failed