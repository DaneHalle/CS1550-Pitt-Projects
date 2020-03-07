#!/bin/bash

rm -r test

./museumsim -m 10 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > test1.txt
./museumsim -m 20 -k 2 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > test2.txt
./museumsim -m 7 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > test3.txt
./museumsim -m 10 -k 2 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > test4.txt
./museumsim -m 30 -k 3 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > test5.txt

mkdir test

mv test*.txt test