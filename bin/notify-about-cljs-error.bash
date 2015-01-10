#!/usr/bin/env bash

if [[ $1 == Successfully* ]]; then
    terminal-notifier -remove cljsbuild
else
    terminal-notifier -title 'cljsbuild failed' -group cljsbuild -message "$1"
fi
