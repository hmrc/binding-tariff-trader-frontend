#!/usr/bin/env bash
sbt compile coverage test coverageOff coverageReport
