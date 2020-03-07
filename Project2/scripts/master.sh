#!/bin/bash

scp dmh148@thoth.cs.pitt.edu:/afs/pitt.edu/home/d/m/dmh148/private/cs1550/Projects/Project2/museumsim .

./passed.sh
./test.sh
./failed.sh