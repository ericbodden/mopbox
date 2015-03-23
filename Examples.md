# Initial Example #

We have prepared a set of examples for you, which should allow you to get started quickly with MOPBox. To try the examples, check out the repository into an Eclipse project.

## Hello World ##

In this example, we define a simple monitor template that can accept two kinds of events, "hello" and "world". The monitor template will notify us when the event trace is exactly "hello world", i.e., when both events happen in that order...

[HelloWorld.java](http://mopbox.googlecode.com/hg/MOPBox/examples/HelloWorld.java)

## Suffix Matching ##

Often it is useful to define monitors that ignore initial prefixes of the execution trace. For instance, no matter what system state we are in, when a user logs out of the system but can then still access his/her profile, this is a violation. In this example, we define a "suffix matching" monitor template for Hello World, which will match not only the single trace "hello world" but also all other traces that end with "hello world". This is achieved by modifying the state-machine transition. The appropriate state machine can be obtained, by...

  1. adding a Sigma-loop to the automaton's initial state
  1. determinizing this automaton. (MOPBox only accepts deterministic finite-state machines)

[HelloWorldSuffixMatching.java](http://mopbox.googlecode.com/hg/MOPBox/examples/HelloWorldSuffixMatching.java)

## Hello from Alice, Hello from Bob ##

It is also often useful to distinguish events that are parameterized by different values. Imagine, for example, that both Alice and Bob may say "hello world" and we want to be notified when either one of them said "hello world", and ideally would even like to know which one of them it was who said it. MOPBox allows this functionality out of the box through so-called parameterized traces. A trace `hello(Alice) hello(Bob) world(Alice) hello(Bob)` is automatically dispatched to two different monitor instances, one for Alice, one for Bob. The first instance will receive Alice's events: `hello world`, while the other one receives Bob's events: `hello hello`. The first trace matches, the second does not.

[HelloAliceHelloBob.java](http://mopbox.googlecode.com/hg/MOPBox/examples/HelloAliceHelloBob.java)

Note: In the example, we use variable bindings mapping Strings to Strings. Those types can be modified by providing other type parameters to the AbstractFSMMonitorTemplate.

# Using MOPBox for Offline and Online/Inline Monitoring #

MOPBox can be used to both monitor applications online/inline, i.e., while they are running, or offline, by processing trace files collected at runtime.

## Offline / trace-based monitoring ##

During trace-based monitoring, one would just read a previously collected trace file, recognize atomic events in this file and then dispatch them to the monitor template.

[HelloWorldFromTrace.java](http://mopbox.googlecode.com/hg/MOPBox/examples/HelloWorldFromTrace.java)
[trace1.txt](http://mopbox.googlecode.com/hg/MOPBox/examples/trace1.txt)
[trace2.txt](http://mopbox.googlecode.com/hg/MOPBox/examples/trace2.txt)

## Online/Inline (reference) monitoring ##

Online monitoring requires program instrumentation. This can be achieved through many ways, the most convenient one probably being through [AspectJ](http://www.eclipse.org/aspectJ). In the example, the main method calls methods "hello" and "world". The two "before-execution" advice intercept those calls and send appropriate events to the monitor template.

[HelloWorldAspectJ.aj](http://mopbox.googlecode.com/hg/MOPBox/examples/HelloWorldAspectJ.aj)

Note: To compile and run this example, proceed as follows.
  1. Install the [AspectJ Development Tools](http://www.eclipse.org/ajdt/).
  1. Right-click the eclipse project and choose Configure -> Convert to AspectJ project.

Another note: Using AspectJ, one can also expose certain context values (this/target/args), which are often useful to use in parameterized events (see the Alice and Bob example above).