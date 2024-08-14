#!/usr/bin/env bash

sbt clean scalafmtAll compile coverage test A11y/test coverageOff coverageReport dependencyUpdates
