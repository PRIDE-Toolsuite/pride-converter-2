# Introduction #

It is possible to configure memory and proxy settings for the PRIDE Converter 2 applications.

# Details #

When the distribution archive is extracted, a file called converter.properties is created. This file contains the following information:

```java

###############################################################
# Pride Converter GUI Bootstrap configuration
###############################################################

# any JVM argument listed here will be passed verbatim to the JVM
jvm.args=-Xms128M -Xmx1024M

# uncomment and set accordingly to configure PROXY settings
#http.proxyHost=webcache.mydomain.com
#http.proxyPort=8080
#http.proxyUser=
#http.proxyPassword=
#http.proxySet=true
```

By default, PRIDE Converter applications will start with 128MB of allocated memory, with a max of 1GB. This should be sufficient to handle arbitrarily large tasks. Should you however run into java.lang.OutOfMemoryError problems, you can always increase the default settings by updating the **jvm.args** property. You can also set any other property supported by your JVM on this line, as anything set there will be passed directly to the JVM command-line.

PRIDE Converter requires an active internet connection to work. If you are not connected to the internet or are behind a proxy, PRIDE Converter may behave erratically (or not work at all). If you need to configure a proxy for your internet connection, uncomment the properties that begin with **http.** and set them to the appropriate values for your environment.