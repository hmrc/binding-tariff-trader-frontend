#!/usr/bin/env bash
sbt clean compile coverage test it:test coverageOff coverageReport
