#!/usr/bin/env bash
sbt compile coverage test it:test coverageOff coverageReport
