#!/usr/bin/env bash
sbt clean -mem 2048 scalafmtAll scalastyleAll compile coverage test coverageOff coverageReport dependencyUpdates
