#!/usr/bin/env bash

sbt clean scalafmtAll scalastyleAll compile coverage test A11y/test coverageOff coverageReport dependencyUpdates
