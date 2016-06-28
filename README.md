Language Detection
-------------

This is a simple ngram based language detector, based of the simple approach by [Cavnar et al](http://www.let.rug.nl/vannoord/TextCat/textcat.pdf)
, it requires proper tokenization and hence may not be the best fit for east asian languages.


#### Building

```
mvn clean package -Pshade
```

This builds a shaded jar

##### Generate Profiles

export env variable to point to target dir.

```
$ export LANGDETECT_JAR=$(pwd)/target
$ ./bin/build-profiles --samples-dir src/main/resources/langsamples --profiles-dir src/main/resources/profiles

```

##### Running language detection

```
$ export LANGDETECT_JAR=$(pwd)/target
$ cat article | ./bin/langdetect --profiles-dir src/main/resources/profiles 

```

