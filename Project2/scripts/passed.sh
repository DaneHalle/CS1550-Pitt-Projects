#!/bin/bash

scp dmh148@thoth.cs.pitt.edu:/afs/pitt.edu/home/d/m/dmh148/private/cs1550/Projects/Project2/museumsim .

rm -r passed

./museumsim -m 1 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed1.txt
./museumsim -m 10 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed2.txt
./museumsim -m 10 -k 1 -pv 50 -dv 1 -sv 1 -pg 50 -dg 1 -sg 2 > passed3.txt
./museumsim -m 50 -k 5 -pv 100 -dv 1 -sv 10 -pg 0 -dg 3 -sg 20 > passed4.txt
./museumsim -m 50 -k 5 -pv 0 -dv 1 -sv 10 -pg 0 -dg 12 -sg 20 > passed5.txt
./museumsim -m 10 -k 1 -pv 0 -dv 1 -sv 1 -pg 0 -dg 1 -sg 2 > passed6.txt
./museumsim -m 2 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed7.txt
./museumsim -m 3 -k 1 -pv 0 -dv 1 -sv 1 -pg 0 -dg 1 -sg 2 > passed8.txt
./museumsim -m 4 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed9.txt
./museumsim -m 5 -k 1 -pv 50 -dv 1 -sv 1 -pg 50 -dg 1 -sg 2 > passed10.txt
./museumsim -m 6 -k 1 -pv 0 -dv 1 -sv 1 -pg 0 -dg 1 -sg 2 > passed11.txt
./museumsim -m 7 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed12.txt
./museumsim -m 8 -k 1 -pv 0 -dv 1 -sv 1 -pg 0 -dg 1 -sg 2 > passed13.txt
./museumsim -m 9 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed14.txt
./museumsim -m 11 -k 2 -pv 100 -dv 1 -sv 1 -pg 0 -dg 3 -sg 2 &> passed15.txt
./museumsim -m 22 -k 3 -pv 100 -dv 1 -sv 1 -pg 0 -dg 3 -sg 2 > passed16.txt
./museumsim -m 32 -k 4 -pv 100 -dv 0 -sv 1 -pg 0 -dg 3 -sg 2 > passed17.txt

mkdir passed

mv passed*.txt passed