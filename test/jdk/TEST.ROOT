# This file identifies the root of the test-suite hierarchy.
# It also contains test-suite configuration information.

# The list of keywords supported in the entire test suite.  The
# "intermittent" keyword marks tests known to fail intermittently.
# The "randomness" keyword marks tests using randomness with test
# cases differing from run to run. (A test using a fixed random seed
# would not count as "randomness" by this definition.) Extra care
# should be taken to handle test failures of intermittent or
# randomness tests.
#
# A "headful" test requires a graphical environment to meaningfully
# run. Tests that are not headful are "headless".
# A test flagged with key "printer" requires a printer to succeed, else
# throws a PrinterException or the like.

keys=2d dnd headful i18n intermittent printer randomness jfr

# Group definitions
groups=TEST.groups

# Allow querying of various System properties in @requires clauses
#
# Source files for classes that will be used at the beginning of each test suite run,
# to determine additional characteristics of the system for use with the @requires tag.
# Note: compiled bootlibs code will be located in the folder 'bootClasses'
requires.extraPropDefns.bootlibs = ../lib/sun \
    ../lib/jdk/test/lib/Platform.java \
    ../lib/jdk/test/lib/Container.java
requires.extraPropDefns.vmOpts = -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI -Xbootclasspath/a:bootClasses
requires.properties= \
    sun.arch.data.model \
    java.runtime.name \
    vm.gc.Z \
    vm.gc.Shenandoah \
    vm.graal.enabled \
    vm.compiler1.enabled \
    vm.compiler2.enabled \
    vm.cds \
    vm.debug \
    vm.hasSA \
    vm.hasSAandCanAttach \
    vm.hasJFR \
    docker.support \
    release.implementor

# Minimum jtreg version
requiredVersion=4.2 b14

# Path to libraries in the topmost test directory. This is needed so @library
# does not need ../../ notation to reach them
external.lib.roots = ../../

# Use new module options
useNewOptions=true

# Use --patch-module instead of -Xmodule:
useNewPatchModule=true

# disabled till JDK-8219408 is fixed
allowSmartActionArgs=false
