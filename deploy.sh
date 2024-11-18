#!/bin/bash

#/home/quacksort/.jdks/openjdk-17.0.2/bin/java -Dmaven.multiModuleProjectDirectory=/home/quacksort/Documents/uni/qesm/perfect_sampling/qesm_perfect_sampling -Djansi.passthrough=true -Dmaven.home=/snap/intellij-idea-ultimate/548/plugins/maven/lib/maven3 -Dclassworlds.conf=/snap/intellij-idea-ultimate/548/plugins/maven/lib/maven3/bin/m2.conf -Dmaven.ext.class.path=/snap/intellij-idea-ultimate/548/plugins/maven/lib/maven-event-listener.jar -javaagent:/snap/intellij-idea-ultimate/548/lib/idea_rt.jar=35903:/snap/intellij-idea-ultimate/548/bin -Dfile.encoding=UTF-8 -classpath /snap/intellij-idea-ultimate/548/plugins/maven/lib/maven3/boot/plexus-classworlds-2.8.0.jar:/snap/intellij-idea-ultimate/548/plugins/maven/lib/maven3/boot/plexus-classworlds.license org.codehaus.classworlds.Launcher -Didea.version=2024.3 clean compile assembly:single
#scp target/QESM_perfect_sampling-1.0-SNAPSHOT-jar-with-dependencies.jar giorgi@192.168.3.47:QESM_perfect_sampling.jar
#scp script.sh giorgi@192.168.3.47:
scp -r files/input giorgi@192.168.3.47:project2/files
#scp postprocess/*.py giorgi@192.168.3.47:project1/postprocess
