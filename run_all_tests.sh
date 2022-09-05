#!/usr/bin/env bash
sbt clean -mem 2048 scalafmtAll scalastyle compile coverage test coverageOff coverageReport dependencyUpdates
