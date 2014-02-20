#!/bin/bash

START=$(date +%s)

httperf --server localhost --port 1234 --num-conns 2000 --http-version 1.0

END=$(date +%s)
DIFF=$(( $END - $START ))

echo ""
echo "Total running time: $DIFF seconds"

