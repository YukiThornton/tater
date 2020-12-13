#!/bin/bash

TEMPLATE=$1
CREDENTIALS=$2
TARGET=$3

exp=""
for line in $(cat $CREDENTIALS); do
    exp="-e s/${line}/g ${exp}"
done

cat $TEMPLATE \
    | sed $(echo "$exp") \
    > $TARGET