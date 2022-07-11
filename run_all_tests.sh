#!/usr/bin/env bash
sbt clean -mem 2048 scalastyle compile coverage test coverageOff coverageReport
