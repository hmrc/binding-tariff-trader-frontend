#!/usr/bin/env bash
sbt clean scalafmtAll scalastyleAll compile coverage test coverageOff coverageReport dependencyUpdates A11y/test
