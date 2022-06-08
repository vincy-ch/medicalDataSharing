#!/bin/sh
mvn package
cp cpabe-api/lib/jpbc-*.jar jars
cp cpabe-api/target/cpabe-api-*.jar jars
cp cpabe-demo/target/cpabe-demo-*.jar jars
java -cp "jars/*" co.junwei.cpabe.Demo
