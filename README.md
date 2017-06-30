# Game of Life

My attempt on writing famous John Conway's [cellular automaton](https://en.wikipedia.org/wiki/Cellular_automaton).

## Overview

It's a simple implementation using ClojureScript, Reagent and Figwheel for
interactive development.

Some links:

[Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life)

[ClojureScript](https://clojurescript.org)

[Reagent](https://reagent-project.github.io)

[Figwheel](https://github.com/bhauman/lein-figwheel)

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## License

Copyright © 2017 Grigorii Kargin

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
