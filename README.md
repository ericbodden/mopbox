MOPBox is [Monitor-Oriented Programming](http://fsl.cs.uiuc.edu/index.php/Monitoring-oriented_programming) in a box. It is a library for defining and constructing runtime monitors. Runtime monitors can be useful both as oracles in testing or as security monitors, also known as inline reference monitors. In the first case, the monitor notifies the user when an internal error state was reached, thereby flagging the current test run as erroneous. In the case of security monitors, the monitor will detect an imminent attack and can then take evasive actions.

For the particular case of the MOPBox library, we envision the following use cases:

Efficient offline trace processing of (possibly parameterized) runtime traces that were collected online.
Online security monitoring (inline reference monitors) through a combination with [AspectJ](http://www.eclipse.org/aspectj/).
Integration into other tools and IDEs. For an example, see [Stateful Breakpoints](http://www.bodden.de/research/current/sbp/).
As tool for teaching runtime verification. Compared to other approaches based on code generation, the algorithms implemented in MOPBox are easy to follow, extend and customize.
As a platform for comparing existing runtime verification algorithms. Currently, such comparisons are hard to impossible because different algorithms are implemented using different tools.
As an enabler for novel, more flexible runtime verification algorithms. For instance, imagine a runtime verification adjusting its indexing strategy over time.
MOPBox is open source. Feel free to use it and extend it however you wish. Of course, we are also always interested in collaborations on this topic.

Examples
See [here](https://github.com/ericbodden/mopbox/wiki) for a list of toy examples.

Download and testing
Although already quite stable, MOPBox has not yet been released. To download the current code, please access the source code from our repository. The repository also includes test cases that show how to define and use runtime monitors. In case of any questions, do not hesitate to [contact me](http://www.bodden.de/about-me/).

Who are the developers of MOPBox?
MOPBOX was designed, implemented and is maintained by [Eric Bodden](http://bodden.de/). This work is supported by [CASED](http://www.cased.de/).
