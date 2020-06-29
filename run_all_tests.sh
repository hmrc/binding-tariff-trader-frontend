#!/usr/bin/env bash
sbt -mem 2048 scalastyle compile coverage test coverageOff coverageReport
