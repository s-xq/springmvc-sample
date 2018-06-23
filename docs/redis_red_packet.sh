#!/usr/bin/env bash
./redis-cli flushall
./redis-cli hset red_packet_1 stock 2000
./redis-cli hset red_packet_1 unit_amount 100
